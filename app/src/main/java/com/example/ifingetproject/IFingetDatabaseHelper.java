package com.example.ifingetproject;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ifingetproject.ui.borrow.BorrowDetail;
import com.example.ifingetproject.ui.expenses.ExpenseCategory;
import com.example.ifingetproject.ui.income.IncomeSource;
import com.example.ifingetproject.ui.lend.LendDetail;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class IFingetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "IFingetDB";
    private static final int DATABASE_VERSION = 1;

    public IFingetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS expense_transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category TEXT," +
                "wallet TEXT," +
                "amount REAL," +
                "transaction_date TEXT," +
                "description TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS lend_transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lend_to_name TEXT," +
                "wallet TEXT," +
                "amount REAL," +
                "interest REAL," +
                "transaction_date TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS borrowed_transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "borrowed_from_name TEXT," +
                "into_wallet TEXT," +
                "borrowed_amount REAL," +
                "borrowed_interest REAL," +
                "transaction_date TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS income_transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "wallet_name TEXT," +
                "income_source TEXT," +
                "amount REAL," +
                "transaction_date TEXT," +
                "description TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS wallets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "balance REAL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS budget_goals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category TEXT," +
                "amount REAL," +
                "month TEXT," +
                "year INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS stocks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "symbol TEXT UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Need to implement
    }

    public boolean walletExists(String walletName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("wallets", new String[]{"id"}, "name = ?",
                new String[]{walletName}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long addWallet(String name, double initialBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("balance", initialBalance);
        return db.insert("wallets", null, values);
    }


    public void updateWalletBalance(String walletName, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        int rowsAffected = db.update("wallets", values, "name = ?", new String[]{walletName});
        if (rowsAffected == 0) {
            Log.e("Database", "Failed to update balance for wallet: " + walletName);
        }
    }

    public double getWalletBalance(String walletName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("wallets", new String[]{"balance"}, "name = ?",
                new String[]{walletName}, null, null, null);
        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            cursor.close();
            return balance;
        }
        cursor.close();
        return 0;
    }

    public long addExpenseTransaction(String category, String wallet, double amount, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Start a transaction
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("category", category);
            values.put("wallet", wallet);
            values.put("amount", amount);
            values.put("transaction_date", date);
            values.put("description", description);
            long newRowId = db.insert("expense_transactions", null, values);

            // Update wallet balance
            double currentBalance = getWalletBalance(wallet);
            updateWalletBalance(wallet, currentBalance - amount);

            db.setTransactionSuccessful();
            return newRowId;
        } finally {
            db.endTransaction();
        }
    }

    public long addIncomeTransaction(String walletName, String incomeSource, double amount, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Start a transaction
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("wallet_name", walletName);
            values.put("income_source", incomeSource);
            values.put("amount", amount);
            values.put("transaction_date", date);
            values.put("description", description);
            long newRowId = db.insert("income_transactions", null, values);

            // Update wallet balance
            double currentBalance = getWalletBalance(walletName);
            updateWalletBalance(walletName, currentBalance + amount);

            db.setTransactionSuccessful();
            return newRowId;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllExpenseTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM expense_transactions", null);
    }

    public Cursor getAllIncomeTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM income_transactions", null);
    }

    public Cursor getAllWallets() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM wallets", null);
    }

    public List<Wallet> getAllWallets_Balance() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Wallet> wallets = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT name, balance FROM wallets", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                double balance = cursor.getDouble(1);
                wallets.add(new Wallet(name, balance));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return wallets;
    }


    public Cursor getAllTransactionsSortedByDate() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id, amount, transaction_date, description, 'expense' AS type, wallet, category, NULL AS income_source FROM expense_transactions " +
                "UNION ALL " +
                "SELECT id, amount, transaction_date, description, 'income' AS type, wallet_name AS wallet, NULL AS category, income_source FROM income_transactions " +
                "ORDER BY transaction_date DESC";
        return db.rawQuery(query, null);
    }

    public Cursor getFilteredTransactions(String wallet, String source, String category, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> whereClause = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        String baseQuery = "SELECT id, amount, transaction_date, description, 'expense' AS type, wallet, category, NULL AS income_source FROM expense_transactions " +
                "UNION ALL " +
                "SELECT id, amount, transaction_date, description, 'income' AS type, wallet_name AS wallet, NULL AS category, income_source FROM income_transactions";

        if (!wallet.isEmpty()) {
            whereClause.add("wallet = ?");
            whereArgs.add(wallet);
        }
        if (!source.isEmpty()) {
            whereClause.add("income_source = ?");
            whereArgs.add(source);
        }
        if (!category.isEmpty()) {
            whereClause.add("category = ?");
            whereArgs.add(category);
        }
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            whereClause.add("transaction_date BETWEEN ? AND ?");
            whereArgs.add(startDate);
            whereArgs.add(endDate);
        }

        String whereString = whereClause.isEmpty() ? "" : " WHERE " + String.join(" AND ", whereClause);
        String finalQuery = baseQuery + whereString + " ORDER BY transaction_date DESC";

        return db.rawQuery(finalQuery, whereArgs.toArray(new String[0]));
    }

    public List<Transaction> getMonthTransactions(LocalDate date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String startDate = date.withDayOfMonth(1).toString();
        String endDate = date.withDayOfMonth(date.lengthOfMonth()).toString();

        String query = "SELECT id, amount, transaction_date, description, 'expense' AS type, wallet, category, NULL AS income_source FROM expense_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT id, amount, transaction_date, description, 'income' AS type, wallet_name AS wallet, NULL AS category, income_source FROM income_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "ORDER BY transaction_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate, startDate, endDate});

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            Transaction transaction = new Transaction(
                    cursor.getLong(0),
                    cursor.getDouble(1),
                    LocalDate.parse(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)
            );
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    public double getPeriodIncome(LocalDate startDate, LocalDate endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(amount) FROM income_transactions WHERE transaction_date BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{startDate.toString(), endDate.toString()});
        double income = 0;
        if (cursor.moveToFirst()) {
            income = cursor.getDouble(0);
        }
        cursor.close();
        return income;
    }

    public double getPeriodExpenses(LocalDate startDate, LocalDate endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(amount) FROM expense_transactions WHERE transaction_date BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{startDate.toString(), endDate.toString()});
        double expenses = 0;
        if (cursor.moveToFirst()) {
            expenses = cursor.getDouble(0);
        }
        cursor.close();
        return expenses;
    }

    public List<Transaction> getPeriodTransactions(LocalDate startDate, LocalDate endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id, amount, transaction_date, description, 'expense' AS type, wallet, category, NULL AS income_source, NULL AS lend_to_name, NULL AS interest, NULL AS borrowed_from_name, NULL AS borrowed_interest FROM expense_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT id, amount, transaction_date, description, 'income' AS type, wallet_name AS wallet, NULL AS category, income_source, NULL AS lend_to_name, NULL AS interest, NULL AS borrowed_from_name, NULL AS borrowed_interest FROM income_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT id, amount, transaction_date, NULL AS description, 'lend' AS type, wallet, NULL AS category, NULL AS income_source, lend_to_name, interest, NULL AS borrowed_from_name, NULL AS borrowed_interest FROM lend_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT id, borrowed_amount AS amount, transaction_date, NULL AS description, 'borrow' AS type, into_wallet AS wallet, NULL AS category, NULL AS income_source, NULL AS lend_to_name, NULL AS interest, borrowed_from_name, borrowed_interest FROM borrowed_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "ORDER BY transaction_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{
                startDate.toString(), endDate.toString(),
                startDate.toString(), endDate.toString(),
                startDate.toString(), endDate.toString(),
                startDate.toString(), endDate.toString()
        });

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            Transaction transaction = new Transaction(
                    cursor.getLong(0),
                    cursor.getDouble(1),
                    LocalDate.parse(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.isNull(9) ? null : cursor.getDouble(9),
                    cursor.getString(10),
                    cursor.isNull(11) ? null : cursor.getDouble(11)
            );
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    public List<IncomeSource> getIncomeSourcesForPeriod(LocalDate startDate, LocalDate endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT income_source, SUM(amount) as total_amount FROM income_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "GROUP BY income_source " +
                "ORDER BY total_amount DESC";

        Cursor cursor = db.rawQuery(query, new String[]{startDate.toString(), endDate.toString()});
        List<IncomeSource> incomeSources = new ArrayList<>();

        while (cursor.moveToNext()) {
            String sourceName = cursor.getString(0);
            double amount = cursor.getDouble(1);
            incomeSources.add(new IncomeSource(sourceName, amount));
        }

        cursor.close();
        return incomeSources;
    }

    public List<Transaction> getTransactionsForSource(String sourceName, LocalDate startDate, LocalDate endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id, amount, transaction_date, description, 'income' AS type, wallet_name AS wallet, NULL AS category, income_source FROM income_transactions " +
                "WHERE income_source = ? AND transaction_date BETWEEN ? AND ? " +
                "ORDER BY transaction_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{sourceName, startDate.toString(), endDate.toString()});

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            Transaction transaction = new Transaction(
                    cursor.getLong(0),
                    cursor.getDouble(1),
                    LocalDate.parse(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)
            );
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    public List<ExpenseCategory> getExpenseCategoriesForPeriod(LocalDate startDate, LocalDate endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT category, SUM(amount) as total_amount FROM expense_transactions " +
                "WHERE transaction_date BETWEEN ? AND ? " +
                "GROUP BY category " +
                "ORDER BY total_amount DESC";

        Cursor cursor = db.rawQuery(query, new String[]{startDate.toString(), endDate.toString()});
        List<ExpenseCategory> expenseCategories = new ArrayList<>();

        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(0);
            double amount = cursor.getDouble(1);
            expenseCategories.add(new ExpenseCategory(categoryName, amount));
        }

        cursor.close();
        return expenseCategories;
    }

    public List<Transaction> getTransactionsForExpenseCategory(String categoryName, LocalDate startDate, LocalDate endDate) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, amount, transaction_date, description, 'expense' AS type, wallet, category, NULL AS income_source FROM expense_transactions " +
                "WHERE category = ? AND transaction_date BETWEEN ? AND ? " +
                "ORDER BY transaction_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{categoryName, startDate.toString(), endDate.toString()});

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            Transaction transaction = new Transaction(
                    cursor.getLong(0),
                    cursor.getDouble(1),
                    LocalDate.parse(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)
            );
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    public long addBudgetGoal(String category, double amount, String month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if a budget goal already exists for this category, month, and year
        String[] whereArgs = {category, month, String.valueOf(year)};
        Cursor cursor = db.query("budget_goals", new String[]{"id", "amount"},
                "category = ? AND month = ? AND year = ?",
                whereArgs, null, null, null);

        long result;
        if (cursor.moveToFirst()) {
            // Update existing record
            int id = cursor.getInt(0);
            double existingAmount = cursor.getDouble(1);
            double newAmount = existingAmount + amount;

            ContentValues values = new ContentValues();
            values.put("amount", newAmount);

            result = db.update("budget_goals", values, "id = ?", new String[]{String.valueOf(id)});
            Log.d("New Budget", "Updated budget goal for " + category + " in " + month + " " + year +
                    ". New total: " + newAmount);
        } else {
            // Insert new record
            ContentValues values = new ContentValues();
            values.put("category", category);
            values.put("amount", amount);
            values.put("month", month);
            values.put("year", year);

            result = db.insert("budget_goals", null, values);
            Log.d("New Budget", "Added new budget goal for " + category + " in " + month + " " + year +
                    ". Amount: " + amount);
        }

        cursor.close();
        return result;
    }

    public Cursor getBudgetGoals(String month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("budget_goals", null, "month = ? AND year = ?",
                new String[]{month, String.valueOf(year)}, null, null, null);
    }

    public double[] getMonthlyBudgetAndExpenses(String month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        double[] result = new double[2]; // [0] for budget, [1] for expenses

        // Get budget total
        String budgetQuery = "SELECT SUM(amount) FROM budget_goals WHERE UPPER(month) = UPPER(?) AND year = ?";
        Cursor budgetCursor = db.rawQuery(budgetQuery, new String[]{month, String.valueOf(year)});
        if (budgetCursor.moveToFirst()) {
            result[0] = budgetCursor.getDouble(0);
        }
        budgetCursor.close();

        // Get expenses total
        YearMonth yearMonth = YearMonth.of(year, Month.valueOf(month.toUpperCase()));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        String expensesQuery = "SELECT SUM(amount) FROM expense_transactions WHERE transaction_date BETWEEN ? AND ?";
        Cursor expensesCursor = db.rawQuery(expensesQuery, new String[]{startDate.toString(), endDate.toString()});
        if (expensesCursor.moveToFirst()) {
            result[1] = expensesCursor.getDouble(0);
        }
        expensesCursor.close();

        Log.d("IFingetDatabaseHelper", "Monthly Budget Total for " + month + " " + year + ": " + result[0]);
        Log.d("IFingetDatabaseHelper", "Monthly Expenses Total for " + month + " " + year + ": " + result[1]);

        return result;
    }

    public List<CategoryBudgetStatus> getCategoryBudgetStatuses(String month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CategoryBudgetStatus> statuses = new ArrayList<>();

        String query = "SELECT bg.category, bg.amount AS budget, " +
                "COALESCE(SUM(et.amount), 0) AS spent, " +
                "bg.amount - COALESCE(SUM(et.amount), 0) AS balance " +
                "FROM budget_goals bg " +
                "LEFT JOIN expense_transactions et ON bg.category = et.category " +
                "AND strftime('%m', et.transaction_date) = ? " +
                "AND strftime('%Y', et.transaction_date) = ? " +
                "WHERE UPPER(bg.month) = UPPER(?) AND bg.year = ? " +
                "GROUP BY bg.category";

        String monthNumber = String.format("%02d", Month.valueOf(month.toUpperCase()).getValue());
        String[] selectionArgs = {monthNumber, String.valueOf(year), month, String.valueOf(year)};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        int categoryIndex = cursor.getColumnIndex("category");
        int budgetIndex = cursor.getColumnIndex("budget");
        int spentIndex = cursor.getColumnIndex("spent");
        int balanceIndex = cursor.getColumnIndex("balance");

        while (cursor.moveToNext()) {
            if (categoryIndex != -1 && budgetIndex != -1 && spentIndex != -1 && balanceIndex != -1) {
                String category = cursor.getString(categoryIndex);
                double budget = cursor.getDouble(budgetIndex);
                double spent = cursor.getDouble(spentIndex);
                double balance = cursor.getDouble(balanceIndex);

                Log.d("IFingetDatabaseHelper", "Row data: category=" + category +
                        ", budget=" + budget + ", spent=" + spent + ", balance=" + balance);

                statuses.add(new CategoryBudgetStatus(category, budget, spent, balance));
            } else {
                Log.e("IFingetDatabaseHelper", "One or more columns not found in the result set");
            }
        }

        cursor.close();
        return statuses;
    }

    public long addStock(String symbol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("symbol", symbol);
        return db.insert("stocks", null, values);
    }
/*
    public List<String> getAllStockSymbols() {
        List<String> symbols = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("stocks", new String[]{"symbol"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                symbols.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return symbols;
    }*/

    public List<String> getAllStockSymbols() {
        List<String> symbols = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "Fetching all stock symbols from database");

        Cursor cursor = db.query("stocks", new String[]{"symbol"}, null, null, null, null, null);

        Log.d(TAG, "Number of symbols found: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                String symbol = cursor.getString(0);
                symbols.add(symbol);
                Log.d(TAG, "Retrieved symbol: " + symbol);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No symbols found in the database");
        }

        cursor.close();

        Log.d(TAG, "Total symbols retrieved: " + symbols.size());

        return symbols;
    }

    public void deleteStock(String symbol) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("stocks", "symbol = ?", new String[]{symbol});
    }

    public long addLendTransaction(String lend_to_name, String wallet, double amount, double interest, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("lend_to_name", lend_to_name);
            values.put("wallet", wallet);
            values.put("amount", amount);
            values.put("interest", interest);
            values.put("transaction_date", date);
            long newRowId = db.insert("lend_transactions", null, values);

            // Update wallet balance
            double currentBalance = getWalletBalance(wallet);
            updateWalletBalance(wallet, currentBalance - amount);

            db.setTransactionSuccessful();
            return newRowId;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllLentTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM lend_transactions", null);
    }

    public List<LendDetail> getLendTransactionDetails() {
        List<LendDetail> lendDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT lend_to_name, amount, interest, amount * (interest / 100) AS interest_amount " +
                "FROM lend_transactions " +
                "ORDER BY transaction_date DESC ";

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor != null) {
                int lendToNameIndex = cursor.getColumnIndex("lend_to_name");
                int amountIndex = cursor.getColumnIndex("amount");
                int interestIndex = cursor.getColumnIndex("interest");
                int interestAmountIndex = cursor.getColumnIndex("interest_amount");

                while (cursor.moveToNext()) {
                    String lendToName = cursor.getString(lendToNameIndex);
                    double amount = cursor.getDouble(amountIndex);
                    double interest = cursor.getDouble(interestIndex);
                    double interestAmount = cursor.getDouble(interestAmountIndex);
                    lendDetails.add(new LendDetail(lendToName, amount, interest, interestAmount));
                }
            }
        } catch (Exception e) {
            Log.e("Database", "Error retrieving lend transaction details", e);
        }

        return lendDetails;
    }


    public long addBorrowerTransaction(String bname, String selectedWallet, double amount, double interest, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("borrowed_from_name", bname);
            values.put("into_wallet", selectedWallet);
            values.put("borrowed_amount", amount);
            values.put("borrowed_interest", interest);
            values.put("transaction_date", date);

            long newRowId = db.insert("borrowed_transactions", null, values);

            // Update wallet balance
            double currentBalance = getWalletBalance(selectedWallet);
            double newBalance = currentBalance + amount;
            updateWalletBalance(selectedWallet, newBalance);

            db.setTransactionSuccessful();
            return newRowId;
        } finally {
            db.endTransaction();
        }
    }

    public List<BorrowDetail> getBorrowedTransactionDetails() {
        List<BorrowDetail> borrowDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT borrowed_from_name, into_wallet, borrowed_amount, borrowed_interest, borrowed_amount * (borrowed_interest / 100) AS interest_amount " +
                "FROM borrowed_transactions " +
                "ORDER BY transaction_date DESC ";

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor != null) {
                int borrowed_from_nameIndex = cursor.getColumnIndex("borrowed_from_name");
                int into_wallet_Index = cursor.getColumnIndex("into_wallet");
                int borrowed_amount_Index = cursor.getColumnIndex("borrowed_amount");
                int borrowed_interest_Index = cursor.getColumnIndex("borrowed_interest");
                int interestAmountIndex = cursor.getColumnIndex("interest_amount");

                while (cursor.moveToNext()) {
                    String borrowedFromName = cursor.getString(borrowed_from_nameIndex);
                    String into_wallet = cursor.getString(into_wallet_Index);
                    double amount = cursor.getDouble(borrowed_amount_Index);
                    double interest = cursor.getDouble(borrowed_interest_Index);
                    double interestAmount = cursor.getDouble(interestAmountIndex);
                    borrowDetails.add(new BorrowDetail(borrowedFromName, into_wallet, amount, interest, interestAmount));
                }
            }
        } catch (Exception e) {
            Log.e("Database", "Error retrieving lend transaction details", e);
        }

        return borrowDetails;
    }

    public Cursor getAllBorrowedTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM borrowed_transactions", null);
    }
}