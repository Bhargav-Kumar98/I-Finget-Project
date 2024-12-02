/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.income;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.IncomeSourceRecyclerAdapter;
import com.example.ifingetproject.R;
import com.example.ifingetproject.SourceTransactionsAdapter;
import com.example.ifingetproject.Transaction;
import com.example.ifingetproject.Wallet;
import com.example.ifingetproject.WalletAdapter;
import com.example.ifingetproject.WalletRecyclerAdapter;
import com.example.ifingetproject.databinding.IncomeAnalysisBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeAnalysisActivity extends AppCompatActivity {
    private IncomeAnalysisBinding binding;
    private IncomeAnalysisViewModel incomeAnalysisViewModel;
    private IncomeSourceRecyclerAdapter incomeSourceAdapter;
    private WalletRecyclerAdapter walletAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = IncomeAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IFingetDatabaseHelper dbHelper = new IFingetDatabaseHelper(this);
        incomeAnalysisViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new IncomeAnalysisViewModel(dbHelper);
            }
        }).get(IncomeAnalysisViewModel.class);

        setupActionBar();
        setupSummaryCard();
        setupIncomeSourcesList();
        observeViewModel();
        //setupWalletList();
    }
/*
    private void setupWalletList() {
        RecyclerView walletRecyclerView = findViewById(R.id.walletRecyclerView);
        walletRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        walletAdapter = new WalletRecyclerAdapter(new ArrayList<>());
        walletRecyclerView.setAdapter(walletAdapter);

        incomeAnalysisViewModel.getWallets().observe(this, wallets -> {
            walletAdapter.updateData(wallets);
        });
    }
*/

    private void setupActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.centered_title_with_back_btn);

        TextView titleTextView = findViewById(R.id.action_bar_title);
        titleTextView.setText(R.string.income_analysis_title);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupSummaryCard() {
        binding.summaryCard.setOnPeriodChangeListener((date, period) -> {
            incomeAnalysisViewModel.setCurrentDate(date);
            incomeAnalysisViewModel.setCurrentPeriod(period);
        });
    }


    private void observeViewModel() {
        incomeAnalysisViewModel.getCurrentDate().observe(this, this::updateDateDisplay);
        incomeAnalysisViewModel.getCurrentPeriod().observe(this, this::updatePeriodDisplay);
        incomeAnalysisViewModel.getCurrentPeriod().observe(this, this::updatePeriodTitle);
        incomeAnalysisViewModel.getPeriodIncome().observe(this, this::updateIncomeDisplay);
        incomeAnalysisViewModel.getPeriodExpenses().observe(this, this::updateExpensesDisplay);
        incomeAnalysisViewModel.getPeriodBalance().observe(this, this::updateBalanceDisplay);
    }

    private void updatePeriodTitle(String period) {
        TextView currentPeriodTitle = findViewById(R.id.currentPeriodTitle);
        currentPeriodTitle.setText("Income Breakdown: " + period);
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

    private void setupIncomeSourcesList() {
        RecyclerView incomeSourceRecyclerView = findViewById(R.id.incomeSourceRecyclerView);
        incomeSourceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        int income_source_amount_color = ContextCompat.getColor(this, R.color.green_color);
        incomeSourceAdapter = new IncomeSourceRecyclerAdapter(new ArrayList<>(), this::showIncomeSourceDialog, income_source_amount_color);
        incomeSourceRecyclerView.setAdapter(incomeSourceAdapter);

        TextView emptyRecyclerViewMessage = findViewById(R.id.emptyRecyclerViewMessage);

        incomeAnalysisViewModel.getIncomeSources().observe(this, incomeSources -> {
            if (incomeSources.isEmpty()) {
                incomeSourceRecyclerView.setVisibility(View.GONE);
                emptyRecyclerViewMessage.setVisibility(View.VISIBLE);
            } else {
                incomeSourceRecyclerView.setVisibility(View.VISIBLE);
                emptyRecyclerViewMessage.setVisibility(View.GONE);
                incomeSourceAdapter.updateData(incomeSources);
            }
        });
    }

    private void showIncomeSourceDialog(IncomeSource incomeSource) {
        final Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.income_source_transactions_dialog, null);

        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitle);
        titleTextView.setText(incomeSource.getName() + " Transactions");

        RecyclerView transactionsRecyclerView = dialogView.findViewById(R.id.transactionsRecyclerView);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        LocalDate startDate = incomeAnalysisViewModel.getStartDate();
        LocalDate endDate = incomeAnalysisViewModel.getEndDate();

        List<Transaction> transactions = incomeAnalysisViewModel.getTransactionsForSource(incomeSource.getName(), startDate, endDate);

        int income_source_amount_color = ContextCompat.getColor(this, R.color.green_color);
        SourceTransactionsAdapter adapter = new SourceTransactionsAdapter(transactions, income_source_amount_color);
        transactionsRecyclerView.setAdapter(adapter);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}