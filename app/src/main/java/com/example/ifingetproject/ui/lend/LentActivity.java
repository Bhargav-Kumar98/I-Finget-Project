/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.lend;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.R;
import com.example.ifingetproject.databinding.LendAnalysisBinding;
import com.example.ifingetproject.ui.expenses.ExpenseCategoryRecyclerAdapter;

import java.util.ArrayList;

public class LentActivity extends AppCompatActivity {
    private LendAnalysisBinding binding;
    private LentActivityViewModel lentActivityViewModel;
    private LendTransactionRecyclerAdapter lendTransactionRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LendAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IFingetDatabaseHelper dbHelper = new IFingetDatabaseHelper(this);
        lentActivityViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new LentActivityViewModel(dbHelper);
            }
        }).get(LentActivityViewModel.class);

        setupActionBar();
        setupLendTransactionsList();

    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.centered_title_with_back_btn);

        TextView titleTextView = findViewById(R.id.action_bar_title);
        titleTextView.setText(R.string.lend_transactions_title);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupLendTransactionsList() {
        RecyclerView lendTransactionsRecyclerView = findViewById(R.id.lendTransactionsRecyclerView);
        lendTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lendTransactionRecyclerAdapter = new LendTransactionRecyclerAdapter(new ArrayList<>());
        lendTransactionsRecyclerView.setAdapter(lendTransactionRecyclerAdapter);

        TextView emptyRecyclerViewMessage = findViewById(R.id.emptyRecyclerViewMessage);

        lentActivityViewModel.getLendTransactions().observe(this, lendDetails ->{
            if (lendDetails.isEmpty()) {
                lendTransactionsRecyclerView.setVisibility(View.GONE);
                emptyRecyclerViewMessage.setVisibility(View.VISIBLE);
            } else {
                lendTransactionsRecyclerView.setVisibility(View.VISIBLE);
                emptyRecyclerViewMessage.setVisibility(View.GONE);
                lendTransactionRecyclerAdapter.updateData(lendDetails);
            }
        });
    }

}
