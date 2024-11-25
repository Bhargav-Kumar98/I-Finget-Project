package com.example.ifingetproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.databinding.TransactionsActivityBinding;

import java.time.LocalDate;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private TransactionsActivityBinding binding;
    private TransactionsViewModel viewModel;
    private TransactionAdapter adapter;
    private SummaryCardView summaryCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TransactionsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        summaryCard = binding.summaryCard;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.centered_title_with_back_btn);

        TextView titleTextView = findViewById(R.id.action_bar_title);
        titleTextView.setText(R.string.transaction_title);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupViewModel();
        setupRecyclerView();
        setupSummaryCard();
        observeViewModel();
    }

    private void setupViewModel() {
        IFingetDatabaseHelper dbHelper = new IFingetDatabaseHelper(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new TransactionsViewModel(dbHelper);
            }
        }).get(TransactionsViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        binding.transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionsRecyclerView.setAdapter(adapter);
    }

    private void setupSummaryCard() {
        summaryCard.setOnPeriodChangeListener((date, period) -> {
            viewModel.setCurrentDate(date);
            viewModel.setCurrentPeriod(period);
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentDate().observe(this, this::updateDateDisplay);
        viewModel.getCurrentPeriod().observe(this, this::updatePeriodDisplay);
        viewModel.getPeriodIncome().observe(this, this::updateIncomeDisplay);
        viewModel.getPeriodExpenses().observe(this, this::updateExpensesDisplay);
        viewModel.getPeriodBalance().observe(this, this::updateBalanceDisplay);
        viewModel.getTransactions().observe(this, this::updateTransactions);
    }

    private void updateDateDisplay(LocalDate date) {
        summaryCard.setCurrentDate(date);
    }

    private void updatePeriodDisplay(String period) {
        summaryCard.setCurrentPeriod(period);
    }

    private void updateIncomeDisplay(Double income) {
        summaryCard.setIncome(income);
    }

    private void updateExpensesDisplay(Double expenses) {
        summaryCard.setExpenses(expenses);
    }

    private void updateBalanceDisplay(Double balance) {
        summaryCard.setBalance(balance);
    }

    private void updateTransactions(List<Transaction> transactions) {
        adapter.setTransactions(transactions);
        binding.noTransactionsText.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}