package com.example.ifingetproject.ui.goals;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ifingetproject.CategoryBudgetStatusAdapter;
import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.R;
import com.example.ifingetproject.databinding.GoalsActivityBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GoalsActivity extends AppCompatActivity {

    private GoalsActivityViewModel viewModel;
    private GoalsActivityBinding binding;
    private CategoryBudgetStatusAdapter categoryBudgetStatusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = GoalsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IFingetDatabaseHelper dbHelper = new IFingetDatabaseHelper(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new GoalsActivityViewModel(dbHelper);
            }
        }).get(GoalsActivityViewModel.class);

        setupActionBar();
        setupNavigationButtons();
        setupRecyclerView();
        setupObservers();
    }

    private void setupRecyclerView() {
        categoryBudgetStatusAdapter = new CategoryBudgetStatusAdapter(this);
        binding.categoryBudgetStatusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.categoryBudgetStatusRecyclerView.setAdapter(categoryBudgetStatusAdapter);
    }

    private void setupObservers() {
        viewModel.getCurrentDate().observe(this, this::updatePeriodDisplay);
        viewModel.getMonthlyBudget().observe(this, budget ->
                binding.budgetTextView.setText(String.format("$%.2f", budget)));
        viewModel.getMonthlyExpenses().observe(this, spent ->
                binding.spentTextView.setText(String.format("$%.2f", spent)));
        viewModel.getPeriodBalance().observe(this, balance ->
                binding.balanceTextView.setText(String.format("$%.2f", balance)));
        viewModel.getCategoryBudgetStatuses().observe(this, statuses -> {
            if (statuses.isEmpty()) {
                binding.categoryBudgetStatusRecyclerView.setVisibility(View.GONE);
                binding.emptyRecyclerViewMessage.setVisibility(View.VISIBLE);
            } else {
                binding.categoryBudgetStatusRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyRecyclerViewMessage.setVisibility(View.GONE);
                categoryBudgetStatusAdapter.updateData(statuses);
                categoryBudgetStatusAdapter.setCurrentPeriod(binding.periodTextView.getText().toString());
                categoryBudgetStatusAdapter.notifyDataSetChanged();
            }
        });

    }

    private void updatePeriodDisplay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        String formattedDate = date.format(formatter);
        binding.periodTextView.setText(formattedDate);

        categoryBudgetStatusAdapter.setCurrentPeriod(formattedDate);
        categoryBudgetStatusAdapter.notifyDataSetChanged();
    }

    private void setupNavigationButtons() {
        binding.leftArrowButton.setOnClickListener(v -> viewModel.navigatePeriod(false));
        binding.rightArrowButton.setOnClickListener(v -> viewModel.navigatePeriod(true));
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.centered_title_with_back_btn);

        TextView titleTextView = findViewById(R.id.action_bar_title);
        titleTextView.setText(R.string.goals_title);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}