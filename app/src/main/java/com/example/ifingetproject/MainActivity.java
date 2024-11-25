package com.example.ifingetproject;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ifingetproject.ui.borrow.BorrowerFormManager;
import com.example.ifingetproject.ui.goals.GoalsFormManager;
import com.example.ifingetproject.ui.lend.LendFormManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ifingetproject.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.ifingetproject.ui.income.IncomeFormManager;
import com.example.ifingetproject.ui.expenses.ExpenseFormManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private IncomeFormManager incomeFormManager;
    private ExpenseFormManager expenseFormManager;
    private GoalsFormManager goalsFormManager;
    private LendFormManager lendFormManager;
    private BorrowerFormManager borrowerFormManager;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingOverlay = findViewById(R.id.loadingOverlay);
        //getSupportActionBar().setTitle(getString(R.string.app_name));
        setupCustomActionBar();

        // Simulate logged-in state - Remove Once development Complete To call login again- Start
        //FirebaseAuth.getInstance().signInAnonymously();
        // Simulate logged-in state - Remove Once development Complete To call login again- End

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_portfolio, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        incomeFormManager = new IncomeFormManager(this);
        expenseFormManager = new ExpenseFormManager(this);
        goalsFormManager = new GoalsFormManager(this);
        lendFormManager = new LendFormManager(this);
        borrowerFormManager = new BorrowerFormManager(this);

        initializeIncomeSources();
    }

    private void setupCustomActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        View customView = getSupportActionBar().getCustomView();
        TextView titleTextView = customView.findViewById(R.id.action_bar_title);
        titleTextView.setText(getString(R.string.app_name));

        ImageButton logoutButton = customView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        showLoading(true);
        FirebaseAuth.getInstance().signOut();
        // Simulate a delay (you can remove this in production)
        new Handler().postDelayed(() -> {
            showLoading(false);

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }

    private void showLoading(boolean isLoading) {
        loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showAddDialog() {
        final Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_dialog, null);

        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogView.setPadding(0, 0, 0, 0);

        CardView addIncomeCard = dialogView.findViewById(R.id.addIncomeCard);
        CardView addExpenseCard = dialogView.findViewById(R.id.addExpenseCard);
        CardView addGoalCard = dialogView.findViewById(R.id.addGoalCard);
        CardView addLendCard = dialogView.findViewById(R.id.addLendCard);
        CardView addBorrowerCard = dialogView.findViewById(R.id.addBorrowerCard);

        addIncomeCard.setOnClickListener(v -> incomeFormManager.showIncomeForm(dialog));
        addExpenseCard.setOnClickListener(v -> expenseFormManager.showExpenseForm(dialog));
        addGoalCard.setOnClickListener(v -> goalsFormManager.showGoalsForm(dialog));
        addLendCard.setOnClickListener(v -> lendFormManager.showLendsForm(dialog));
        addBorrowerCard.setOnClickListener(v -> borrowerFormManager.showBorrowerForm(dialog));

        dialog.setOnDismissListener(dialogInterface -> refreshScreen());
        dialog.show();
    }

    private void refreshScreen() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        int currentDestinationId = navController.getCurrentDestination().getId();
        navController.popBackStack(currentDestinationId, true);
        navController.navigate(currentDestinationId);
    }

    private void initializeIncomeSources() {
        // Income Sources Initialization
        SharedPreferences sharedPreferencesIs = getSharedPreferences("IncomeSources", MODE_PRIVATE);
        boolean isInitialized = sharedPreferencesIs.getBoolean("isInitialized", false);

        if (!isInitialized) {
            Set<String> defaultSourcesIS = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.income_sources)));

            SharedPreferences.Editor editorIs = sharedPreferencesIs.edit();
            editorIs.putStringSet("sources", defaultSourcesIS);
            editorIs.putBoolean("isInitialized", true);
            editorIs.apply();

            Log.d("IncomeSources", "Initialized default income sources");
        }
        // Wallet Names Initialization
        SharedPreferences sharedPreferencesWn = getSharedPreferences("WalletNames", MODE_PRIVATE);
        boolean wnInitialized = sharedPreferencesWn.getBoolean("wnInitialized", false);

        if (!wnInitialized) {
            Set<String> defaultSourcesWn = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.wallet_names)));

            SharedPreferences.Editor editorWn = sharedPreferencesWn.edit();
            editorWn.putStringSet("wallets", defaultSourcesWn);
            editorWn.putBoolean("wnInitialized", true);
            editorWn.apply();

            Log.d("Wallets", "Initialized default Wallets");
        }
        // Expense Category Initialization
        SharedPreferences sharedPreferencesEc = getSharedPreferences("ExpenseCategories", MODE_PRIVATE);
        boolean ecInitialized = sharedPreferencesEc.getBoolean("ecInitialized", false);

        if (!ecInitialized) {
            Set<String> defaultSourcesEc = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.expense_categories)));

            SharedPreferences.Editor editorEc = sharedPreferencesEc.edit();
            editorEc.putStringSet("category", defaultSourcesEc);
            editorEc.putBoolean("ecInitialized", true);
            editorEc.apply();

            Log.d("category", "Initialized default category");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (incomeFormManager != null) {
            incomeFormManager.closeDatabase();
        }
    }

}