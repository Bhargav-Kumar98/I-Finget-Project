/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.goals;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.R;
import com.example.ifingetproject.ui.expenses.ExpenseFormManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GoalsFormManager {

    private Context context;
    private IFingetDatabaseHelper databaseHelper;
    private ExpenseFormManager expenseFormManager;

    public GoalsFormManager(Context context) {
        this.context = context;
        databaseHelper = new IFingetDatabaseHelper(context);
        expenseFormManager = new ExpenseFormManager(context);
    }

    public void showGoalsForm(Dialog parentDialog) {
        //final Dialog dialog = new Dialog(context);
        //dialog.setContentView(R.layout.add_goals_form);

        View formView = LayoutInflater.from(context).inflate(R.layout.add_goals_form, null);
        parentDialog.setContentView(formView);

        Spinner categorySpinner = formView.findViewById(R.id.expenseCategory);
        EditText budgetAmountEditText = formView.findViewById(R.id.budgetAmount);
        TextView selectedMonthTextView = formView.findViewById(R.id.selectedMonth);
        Button addButton = formView.findViewById(R.id.btnAddBudgetAmount);

        // Set up the category spinner
        List<String> expenseCategoryNames = expenseFormManager.getExpenseCategories();
        ArrayAdapter<String> cat_adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                expenseCategoryNames
        );
        cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(cat_adapter);

        // Set the current month and year
        YearMonth currentMonth = YearMonth.now();
        selectedMonthTextView.setText("Budget for " + currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        addButton.setOnClickListener(v -> {
            String category = categorySpinner.getSelectedItem().toString();
            String amountStr = budgetAmountEditText.getText().toString();

            if (category.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            long result = databaseHelper.addBudgetGoal(category, amount, currentMonth.getMonth().toString(), currentMonth.getYear());

            if (result != -1) {
                Toast.makeText(context, "Budget goal added successfully", Toast.LENGTH_SHORT).show();

                Cursor cursor = databaseHelper.getBudgetGoals(currentMonth.getMonth().toString(), currentMonth.getYear());
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            sb.append(cursor.getColumnName(i)).append(": ");
                            sb.append(cursor.getString(i)).append(", ");
                        }
                        Log.d("All budget goals for Transactions", sb.toString());
                    } while (cursor.moveToNext());
                    cursor.close();
                } else {
                    Log.d("All budget goals for Transactions", "No transactions found");
                }

                parentDialog.dismiss();
            } else {
                Toast.makeText(context, "Failed to add budget goal", Toast.LENGTH_SHORT).show();
            }
        });

        parentDialog.show();
    }
}