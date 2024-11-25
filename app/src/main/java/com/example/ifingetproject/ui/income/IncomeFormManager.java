package com.example.ifingetproject.ui.income;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class IncomeFormManager {
    private Context context;
    private IFingetDatabaseHelper iFingetDatabaseHelper;

    public IncomeFormManager(Context context) {
        this.context = context;
        iFingetDatabaseHelper = new IFingetDatabaseHelper(context);
    }

    public void showIncomeForm(Dialog parentDialog) {
        View formView = LayoutInflater.from(context).inflate(R.layout.income_form, null);
        parentDialog.setContentView(formView);

        // Populating Income source drop down value start
        Spinner incomeSource_spinner = formView.findViewById(R.id.incomeSource);
        List<String> incomeSourceNames = getIncomeSources();
        ArrayAdapter<String> is_adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                incomeSourceNames
        );

        is_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeSource_spinner.setAdapter(is_adapter);
        // Populating Income source drop down value End

        // Populating Wallets drops down value Start

        Spinner newWallet_spinner = formView.findViewById(R.id.wallet_name);
        List<String> walletsNames = getWallets();
        ArrayAdapter<String> wallets_adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                walletsNames
        );

        wallets_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newWallet_spinner.setAdapter(wallets_adapter);
        // Populating Wallets drops down value End

        EditText initialBalance = formView.findViewById(R.id.amount);
        Button btnAddWalletAmount = formView.findViewById(R.id.btnAddWalletAmount);
        ImageButton addWalletButton = formView.findViewById(R.id.addWalletButton);
        ImageButton addIncomeSrcButton = formView.findViewById(R.id.addIncomeSrcButton);
        EditText dateField = formView.findViewById(R.id.dateField);
        EditText walletAmountDesc = formView.findViewById(R.id.walletAmountDesc);

        // Setting up date picker with today's date as default
        Calendar calendar = Calendar.getInstance();
        setDateField(dateField, calendar);
        dateField.setOnClickListener(v -> showDatePickerDialog(dateField, calendar));

        btnAddWalletAmount.setOnClickListener(v -> {
            String selectedWallet = newWallet_spinner.getSelectedItem().toString();
            String selectedIncomeSrc = incomeSource_spinner.getSelectedItem().toString();
            String balance = initialBalance.getText().toString();
            String date = dateField.getText().toString();
            String description = walletAmountDesc.getText().toString();

            if (newWallet_spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION
                    && !balance.isEmpty()
                    && incomeSource_spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION
                    && !date.isEmpty()) {

                double amount = Double.parseDouble(balance);

                // Check if wallet exists, if not, create it
                if (!iFingetDatabaseHelper.walletExists(selectedWallet)) {
                    long newWalletId = iFingetDatabaseHelper.addWallet(selectedWallet, 0);
                    if (newWalletId == -1) {
                        Toast.makeText(context, "Error creating new wallet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Insert data into the database
                long newRowId = iFingetDatabaseHelper.addIncomeTransaction(selectedWallet, selectedIncomeSrc,
                        amount, date, description);

                if (newRowId != -1) {
                    // Get updated wallet balance
                    double updatedBalance = iFingetDatabaseHelper.getWalletBalance(selectedWallet);

                    Toast.makeText(context, "Income added successfully. New balance: $" + String.format("%.2f", updatedBalance), Toast.LENGTH_SHORT).show();

                    // Log all transactions
                    Cursor cursor = iFingetDatabaseHelper.getAllIncomeTransactions();
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                sb.append(cursor.getColumnName(i)).append(": ");
                                sb.append(cursor.getString(i)).append(", ");
                            }
                            Log.d("Database_Income_Transactions", sb.toString());
                        } while (cursor.moveToNext());
                        cursor.close();
                    } else {
                        Log.d("Database_Income_Transactions", "No transactions found");
                    }
                    // Log all transactions
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
                    //updateBalanceDisplay(selectedWallet);
                    parentDialog.dismiss();
                } else {
                    Toast.makeText(context, "Error adding income", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });

        addWalletButton.setOnClickListener(v -> {
            showAddWalletForm(parentDialog);
            Toast.makeText(context, "Add new wallet type functionality triggered!", Toast.LENGTH_SHORT).show();
        });

        addIncomeSrcButton.setOnClickListener(v -> {
            showAddIncomeSourceForm(parentDialog);
            Toast.makeText(context, "Add new Income Source type functionality triggered!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAddWalletForm(Dialog parentDialog) {
        View formView = LayoutInflater.from(context).inflate(R.layout.add_new_wallet, null);
        parentDialog.setContentView(formView);

        EditText wallet_name = formView.findViewById(R.id.wallet_name);
        EditText walletDesc = formView.findViewById(R.id.walletDesc);
        Button btnAddNewWallet = formView.findViewById(R.id.btnAddNewWallet);

        btnAddNewWallet.setOnClickListener(v -> {
            String name = wallet_name.getText().toString();
            String desc = walletDesc.getText().toString();

            if (!name.isEmpty()) {
                saveNewWallet(name, desc);
                Toast.makeText(context, "Income Source added: " + name, Toast.LENGTH_SHORT).show();
                showIncomeForm(parentDialog);
            } else {
                Toast.makeText(context, "Please enter an income source name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewWallet(String name, String description) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WalletNames", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> newWallets = new HashSet<>(sharedPreferences.getStringSet("wallets", new HashSet<>()));
        newWallets.add(name + "|" + description);
        editor.putStringSet("wallets", newWallets);
        editor.apply();
    }

    public List<String> getWallets() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WalletNames", Context.MODE_PRIVATE);
        Set<String> walletNamesSet = sharedPreferences.getStringSet("wallets", new HashSet<>());

        List<String> walletsList = new ArrayList<>();

        for (String wallet : walletNamesSet) {
            String[] parts = wallet.split("\\|", 2);
            walletsList.add(parts[0]); // Add only the name part
        }

        // If the list is empty, add default sources
        if (walletsList.isEmpty()) {
            walletsList.addAll(Arrays.asList(context.getResources().getStringArray(R.array.wallet_names)));
        }

        return walletsList;
    }

    private void showAddIncomeSourceForm(Dialog parentDialog) {
        View formView = LayoutInflater.from(context).inflate(R.layout.add_income_src, null);
        parentDialog.setContentView(formView);

        EditText incomeSourceName = formView.findViewById(R.id.income_source_name);
        EditText description = formView.findViewById(R.id.incomeSourceDesc);
        Button btnAddIncomeSrc = formView.findViewById(R.id.btnAddIncomeSrc);

        btnAddIncomeSrc.setOnClickListener(v -> {
            String name = incomeSourceName.getText().toString();
            String desc = description.getText().toString();

            if (!name.isEmpty()) {
                saveIncomeSource(name, desc);
                Toast.makeText(context, "Income Source added: " + name, Toast.LENGTH_SHORT).show();
                showIncomeForm(parentDialog);
            } else {
                Toast.makeText(context, "Please enter an income source name", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveIncomeSource(String name, String description) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("IncomeSources", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the current set of income sources
        Set<String> incomeSources = new HashSet<>(sharedPreferences.getStringSet("sources", new HashSet<>()));

        // Add the new income source (name and description) as a single string
        incomeSources.add(name + "|" + description);

        // Save the updated set back to SharedPreferences
        editor.putStringSet("sources", incomeSources);
        editor.apply();
    }

    private List<String> getIncomeSources() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("IncomeSources", Context.MODE_PRIVATE);
        Set<String> sourcesSet = sharedPreferences.getStringSet("sources", new HashSet<>());

        List<String> sourcesList = new ArrayList<>();

        for (String source : sourcesSet) {
            String[] parts = source.split("\\|", 2);
            sourcesList.add(parts[0]); // Add only the name part
        }

        // If the list is empty, add default sources
        if (sourcesList.isEmpty()) {
            sourcesList.addAll(Arrays.asList(context.getResources().getStringArray(R.array.income_sources)));
        }

        return sourcesList;
    }

    public void showDatePickerDialog(final EditText dateField, final Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    setDateField(dateField, calendar);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    public void setDateField(EditText dateField, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateField.setText(dateFormat.format(calendar.getTime()));
    }

    public void closeDatabase() {
        if (iFingetDatabaseHelper != null) {
            iFingetDatabaseHelper.close();
        }
    }

}
