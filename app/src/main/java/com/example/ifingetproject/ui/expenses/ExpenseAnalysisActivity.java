package com.example.ifingetproject.ui.expenses;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.IncomeSourceRecyclerAdapter;
import com.example.ifingetproject.R;
import com.example.ifingetproject.SourceTransactionsAdapter;
import com.example.ifingetproject.Transaction;
import com.example.ifingetproject.databinding.ExpenseAnalysisBinding;
import com.example.ifingetproject.ui.expenses.ExpenseAnalysisViewModel;
import com.example.ifingetproject.ui.income.IncomeSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAnalysisActivity extends AppCompatActivity {
    private ExpenseAnalysisBinding binding;
    private ExpenseAnalysisViewModel expenseAnalysisViewModel;
    private ExpenseCategoryRecyclerAdapter expenseCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ExpenseAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IFingetDatabaseHelper dbHelper = new IFingetDatabaseHelper(this);
        expenseAnalysisViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new ExpenseAnalysisViewModel(dbHelper);
            }
        }).get(ExpenseAnalysisViewModel.class);

        setupActionBar();
        setupSummaryCard();
        setupExpenseCategoryList();
        observeViewModel();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.centered_title_with_back_btn);

        TextView titleTextView = findViewById(R.id.action_bar_title);
        titleTextView.setText(R.string.expense_analysis_title);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupExpenseCategoryList() {
        RecyclerView expenseCategoryRecyclerView = findViewById(R.id.expenseCategoryRecyclerView);
        expenseCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        int expense_category_amount_color = ContextCompat.getColor(this, R.color.red_color);
        expenseCategoryAdapter = new ExpenseCategoryRecyclerAdapter(new ArrayList<>(), this::showExpenseCategoryDialog, expense_category_amount_color);
        expenseCategoryRecyclerView.setAdapter(expenseCategoryAdapter);

        TextView emptyRecyclerViewMessage = findViewById(R.id.emptyRecyclerViewMessage);

        expenseAnalysisViewModel.getExpenseCategories().observe(this, expenseCategories -> {
            if (expenseCategories.isEmpty()) {
                expenseCategoryRecyclerView.setVisibility(View.GONE);
                emptyRecyclerViewMessage.setVisibility(View.VISIBLE);
            }else{
                expenseCategoryRecyclerView.setVisibility(View.VISIBLE);
                emptyRecyclerViewMessage.setVisibility(View.GONE);
                expenseCategoryAdapter.updateData(expenseCategories);
            }
        });
    }

    private void showExpenseCategoryDialog(ExpenseCategory expenseCategory) {
        final Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.income_source_transactions_dialog, null);

        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitle);
        titleTextView.setText(expenseCategory.getCategoryName() + " Transactions");

        RecyclerView transactionsRecyclerView = dialogView.findViewById(R.id.transactionsRecyclerView);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        LocalDate startDate = expenseAnalysisViewModel.getStartDate();
        LocalDate endDate = expenseAnalysisViewModel.getEndDate();

        List<Transaction> transactions = expenseAnalysisViewModel.getTransactionsForExpenseCategory(expenseCategory.getCategoryName(), startDate, endDate);

        int expense_category_amount_color = ContextCompat.getColor(this, R.color.red_color);
        SourceTransactionsAdapter adapter = new SourceTransactionsAdapter(transactions, expense_category_amount_color);
        transactionsRecyclerView.setAdapter(adapter);

        dialog.show();
    }

    private void setupSummaryCard() {
        binding.summaryCard.setOnPeriodChangeListener((date, period) -> {
            expenseAnalysisViewModel.setCurrentDate(date);
            expenseAnalysisViewModel.setCurrentPeriod(period);
        });
    }

    private void observeViewModel() {
        expenseAnalysisViewModel.getCurrentDate().observe(this, this::updateDateDisplay);
        expenseAnalysisViewModel.getCurrentPeriod().observe(this, this::updatePeriodDisplay);
        expenseAnalysisViewModel.getCurrentPeriod().observe(this, this::updatePeriodTitle);
        expenseAnalysisViewModel.getPeriodIncome().observe(this, this::updateIncomeDisplay);
        expenseAnalysisViewModel.getPeriodExpenses().observe(this, this::updateExpensesDisplay);
        expenseAnalysisViewModel.getPeriodBalance().observe(this, this::updateBalanceDisplay);
    }

    private void updatePeriodTitle(String period) {
        TextView currentPeriodTitle = findViewById(R.id.currentPeriodTitle);
        currentPeriodTitle.setText("Expenses Breakdown: " + period);
    }

    private void updateDateDisplay(LocalDate date) {
        binding.summaryCard.setCurrentDate(date);
    }

    private void updatePeriodDisplay(String period) {
        binding.summaryCard.setCurrentPeriod(period);
    }

    private void updateIncomeDisplay(Double income) {
        binding.summaryCard.setIncome(income);
    }

    private void updateExpensesDisplay(Double expenses) {
        binding.summaryCard.setExpenses(expenses);
    }

    private void updateBalanceDisplay(Double balance) {
        binding.summaryCard.setBalance(balance);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}