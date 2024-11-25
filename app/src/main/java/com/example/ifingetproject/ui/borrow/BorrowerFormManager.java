package com.example.ifingetproject.ui.borrow;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.R;
import com.example.ifingetproject.ui.income.IncomeFormManager;

import java.util.Calendar;
import java.util.List;

public class BorrowerFormManager {
    private Context context;
    private IFingetDatabaseHelper databaseHelper;
    private IncomeFormManager incomeFormManager;

    public BorrowerFormManager(Context context) {
        this.context = context;
        databaseHelper = new IFingetDatabaseHelper(context);
        incomeFormManager = new IncomeFormManager(context);
    }

    public void showBorrowerForm(Dialog parentDialog) {
        View formView = LayoutInflater.from(context).inflate(R.layout.add_borrower_form, null);
        parentDialog.setContentView(formView);

        Spinner newWallet_spinner = formView.findViewById(R.id.into_wallet_name);
        List<String> walletsNames = incomeFormManager.getWallets();
        ArrayAdapter<String> wallets_adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                walletsNames
        );
        wallets_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newWallet_spinner.setAdapter(wallets_adapter);

        EditText borrowedFromField = formView.findViewById(R.id.borrowedFromField);
        EditText borrowedAmount = formView.findViewById(R.id.borrowedAmount);
        EditText borrowedInterest = formView.findViewById(R.id.borrowedInterest);
        EditText dateField = formView.findViewById(R.id.dateField);
        Button btnAddBorrowedTransaction = formView.findViewById(R.id.btnAddBorrowedTransaction);

        Calendar calendar = Calendar.getInstance();
        incomeFormManager.setDateField(dateField, calendar);
        dateField.setOnClickListener(v -> incomeFormManager.showDatePickerDialog(dateField, calendar));

        btnAddBorrowedTransaction.setOnClickListener(v -> {
            String bname = borrowedFromField.getText().toString();
            String selectedWallet = newWallet_spinner.getSelectedItem().toString();
            String bAmount = borrowedAmount.getText().toString();
            String bInterest = borrowedInterest.getText().toString();
            String date = dateField.getText().toString();

            if (!bname.isEmpty() && newWallet_spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION && !bAmount.isEmpty() && !date.isEmpty()) {
                double amount = Double.parseDouble(bAmount);
                double interest = bInterest.isEmpty() ? 0 : Double.parseDouble(bInterest);

                // Check if wallet exists, if not, create it
                if (!databaseHelper.walletExists(selectedWallet)) {
                    long newWalletId = databaseHelper.addWallet(selectedWallet, 0);
                    if (newWalletId == -1) {
                        Toast.makeText(context, "Error creating new wallet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Insert data into the database
                long newRowId = databaseHelper.addBorrowerTransaction(bname, selectedWallet, amount, interest, date);

                if (newRowId != -1) {
                    // Get updated wallet balance
                    double updatedBalance = databaseHelper.getWalletBalance(selectedWallet);

                    Toast.makeText(context, "Transaction successfully. New balance: $" + String.format("%.2f", updatedBalance), Toast.LENGTH_SHORT).show();

                    // Log all transactions
                    Cursor cursor = databaseHelper.getAllBorrowedTransactions();
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                sb.append(cursor.getColumnName(i)).append(": ");
                                sb.append(cursor.getString(i)).append(", ");
                            }
                            Log.d("Database_Borrower_Transactions", sb.toString());
                        } while (cursor.moveToNext());
                        cursor.close();
                    } else {
                        Log.d("Database_Borrower_Transactions", "No transactions found");
                    }
                    Cursor cursor2 = databaseHelper.getAllWallets();
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
                    Toast.makeText(context, "Error adding Borrowed Transaction!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
