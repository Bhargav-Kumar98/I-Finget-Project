package com.example.ifingetproject.ui.expenses;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.R;
import com.example.ifingetproject.ui.income.IncomeFormManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpenseFormManager {
    private Context context;
    private IncomeFormManager incomeFormManager;
    private IFingetDatabaseHelper iFingetDatabaseHelper;

    public ExpenseFormManager(Context context) {
        this.context = context;
        incomeFormManager = new IncomeFormManager(context);
        iFingetDatabaseHelper = new IFingetDatabaseHelper(context);
    }

    public void showExpenseForm(Dialog parentDialog){
        View formView = LayoutInflater.from(context).inflate(R.layout.expense_form, null);
        parentDialog.setContentView(formView);

        Spinner expenseCategory_spinner = formView.findViewById(R.id.expenseCategory);
        List<String> expenseCategoryNames = getExpenseCategories();
        ArrayAdapter<String> exp_adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                expenseCategoryNames
        );

        exp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategory_spinner.setAdapter(exp_adapter);

        Spinner newWallet_spinner = formView.findViewById(R.id.wallet_name);
        List<String> walletsNames = incomeFormManager.getWallets();
        ArrayAdapter<String> wallets_adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                walletsNames
        );
        wallets_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newWallet_spinner.setAdapter(wallets_adapter);

        EditText expenseAmount = formView.findViewById(R.id.expenseAmount);
        EditText expenseDesc = formView.findViewById(R.id.ExpenseDesc);
        EditText dateField = formView.findViewById(R.id.dateField);
        Button btnAddExpense = formView.findViewById(R.id.btnAddExpense);
        ImageButton addexpenseCategoryButton = formView.findViewById(R.id.addexpenseCategoryButton);

        Calendar calendar = Calendar.getInstance();
        incomeFormManager.setDateField(dateField, calendar);
        dateField.setOnClickListener(v -> incomeFormManager.showDatePickerDialog(dateField, calendar));

        addexpenseCategoryButton.setOnClickListener(v -> {
            showAddExpenseCategoryForm(parentDialog);
            Toast.makeText(context, "Add new Expense Category functionality triggered!", Toast.LENGTH_SHORT).show();
        });

        btnAddExpense.setOnClickListener(v -> {
            String selectedCategory = expenseCategory_spinner.getSelectedItem().toString();
            String selectedWallet = newWallet_spinner.getSelectedItem().toString();
            String expAmount = expenseAmount.getText().toString();
            String date = dateField.getText().toString();
            String description = expenseDesc.getText().toString();

            if (newWallet_spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION
                    && !expAmount.isEmpty()
                    && expenseCategory_spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION
                    && !date.isEmpty()) {

                double amount = Double.parseDouble(expAmount);
                double currentBalance = iFingetDatabaseHelper.getWalletBalance(selectedWallet);

                if (currentBalance >= amount) {
                    // Insert data into the database
                    long newRowId = iFingetDatabaseHelper.addExpenseTransaction(selectedCategory, selectedWallet,
                            amount, date, description);

                    if (newRowId != -1) {
                        // Get updated wallet balance
                        double updatedBalance = iFingetDatabaseHelper.getWalletBalance(selectedWallet);

                        Toast.makeText(context, "Expense added successfully. New balance: $" + String.format("%.2f", updatedBalance), Toast.LENGTH_SHORT).show();

                        // Log all transactions
                        Cursor cursor = iFingetDatabaseHelper.getAllExpenseTransactions();
                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < cursor.getColumnCount(); i++) {
                                    sb.append(cursor.getColumnName(i)).append(": ");
                                    sb.append(cursor.getString(i)).append(", ");
                                }
                                Log.d("Database_Expenses_Transactions", sb.toString());
                            } while (cursor.moveToNext());
                            cursor.close();
                        } else {
                            Log.d("Database_Expenses_Transactions", "No transactions found");
                        }
                        Cursor cursor2 = iFingetDatabaseHelper.getAllWallets();
                        if (cursor2 != null && cursor2.moveToFirst()) {
                            do {
                                StringBuilder sb1 = new StringBuilder();
                                for (int i = 0; i < cursor2.getColumnCount(); i++) {
                                    sb1.append(cursor2.getColumnName(i)).append(": ");
                                    sb1.append(cursor2.getString(i)).append(", ");
                                }
                                Log.d("Database_Wallet_Transactions", sb1.toString());
                            } while (cursor2.moveToNext());
                            cursor2.close();
                        } else {
                            Log.d("Database_Wallet_Transactions", "No transactions found");
                        }
                        parentDialog.dismiss();
                    } else {
                        Toast.makeText(context, "Error adding expense", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Insufficient funds in " + selectedWallet + ". \n Current balance: $" + String.format("%.2f", currentBalance), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddExpenseCategoryForm(Dialog parentDialog) {

        View formView = LayoutInflater.from(context).inflate(R.layout.add_expense_category, null);
        parentDialog.setContentView(formView);

        EditText expense_category_name = formView.findViewById(R.id.expense_category_name);
        EditText expCategoryDesc = formView.findViewById(R.id.expCategoryDesc);
        Button btnAddExpCategory = formView.findViewById(R.id.btnAddExpCategory);

        btnAddExpCategory.setOnClickListener(v -> {
            String name = expense_category_name.getText().toString();
            String desc = expCategoryDesc.getText().toString();

            if (!name.isEmpty()) {
                saveExpCategory(name, desc);
                Toast.makeText(context, "Expense Category added: " + name, Toast.LENGTH_SHORT).show();
                showExpenseForm(parentDialog);
            } else {
                Toast.makeText(context, "Please enter an expense category name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveExpCategory(String name, String desc) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ExpenseCategories", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the current set of income sources
        Set<String> expCategories = new HashSet<>(sharedPreferences.getStringSet("category", new HashSet<>()));

        // Add the new income source (name and description) as a single string
        expCategories.add(name + "|" + desc);

        // Save the updated set back to SharedPreferences
        editor.putStringSet("category", expCategories);
        editor.apply();
    }

    public List<String> getExpenseCategories() {
        SharedPreferences expese_sharedPreferences = context.getSharedPreferences("ExpenseCategories", Context.MODE_PRIVATE);
        Set<String> categorySet = expese_sharedPreferences.getStringSet("category", new HashSet<>());

        List<String> categoryList = new ArrayList<>();

        for (String source : categorySet) {
            String[] parts = source.split("\\|", 2);
            categoryList.add(parts[0]); // Add only the name part
        }

        if (categoryList.isEmpty()) {
            categoryList.addAll(Arrays.asList(context.getResources().getStringArray(R.array.expense_categories)));
        }

        return categoryList;
    }
}