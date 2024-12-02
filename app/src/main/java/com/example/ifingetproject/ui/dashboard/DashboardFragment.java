/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.R;
import com.example.ifingetproject.WalletRecyclerAdapter;
import com.example.ifingetproject.ui.borrow.BorrowActivity;
import com.example.ifingetproject.ui.goals.GoalsActivity;
import com.example.ifingetproject.ui.lend.LentActivity;
import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.TransactionsActivity;
import com.example.ifingetproject.databinding.FragmentDashboardBinding;
import com.example.ifingetproject.ui.expenses.ExpenseAnalysisActivity;
import com.example.ifingetproject.ui.income.IncomeAnalysisActivity;

import java.time.LocalDate;
import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private WalletRecyclerAdapter walletAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        IFingetDatabaseHelper dbHelper = new IFingetDatabaseHelper(requireContext());
        dashboardViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new DashboardViewModel(dbHelper);
            }
        }).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupSummaryCard();
        setupCardClickListeners();
        observeViewModel();
        setupWalletList();

        return root;
    }

    private void setupSummaryCard() {
        binding.summaryCard.setOnPeriodChangeListener((date, period) -> {
            dashboardViewModel.setCurrentDate(date);
            dashboardViewModel.setCurrentPeriod(period);
        });

        binding.showTransactionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TransactionsActivity.class);
            startActivity(intent);
        });
    }

    private void setupCardClickListeners() {
        binding.incomeCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), IncomeAnalysisActivity.class);
            startActivity(intent);
        });
        binding.expensesCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ExpenseAnalysisActivity.class);
            startActivity(intent);
        });
        binding.goalsCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GoalsActivity.class);
            startActivity(intent);
        });
        binding.lendCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LentActivity.class);
            startActivity(intent);
        });
        binding.borrowCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BorrowActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        dashboardViewModel.getCurrentDate().observe(getViewLifecycleOwner(), this::updateDateDisplay);
        dashboardViewModel.getCurrentPeriod().observe(getViewLifecycleOwner(), this::updatePeriodDisplay);
        dashboardViewModel.getPeriodIncome().observe(getViewLifecycleOwner(), this::updateIncomeDisplay);
        dashboardViewModel.getPeriodExpenses().observe(getViewLifecycleOwner(), this::updateExpensesDisplay);
        dashboardViewModel.getPeriodBalance().observe(getViewLifecycleOwner(), this::updateBalanceDisplay);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupWalletList() {
        RecyclerView walletRecyclerView = binding.walletRecyclerView;
        walletRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        walletAdapter = new WalletRecyclerAdapter(new ArrayList<>());
        walletRecyclerView.setAdapter(walletAdapter);

        dashboardViewModel.getWallets().observe(getViewLifecycleOwner(), wallets -> {
            walletAdapter.updateData(wallets);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        dashboardViewModel.updatePeriodData();
        dashboardViewModel.updateWallets();
        setupSummaryCard();
    }
}