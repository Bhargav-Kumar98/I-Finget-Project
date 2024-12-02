/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.borrow;

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
import com.example.ifingetproject.databinding.BorrowAnalysisBinding;

import java.util.ArrayList;

public class BorrowActivity extends AppCompatActivity {
    private BorrowAnalysisBinding binding;
    private BorrowActivityViewModel borrowActivityViewModel;
    private BorrowedTransactionsRecycleAdapter borrowedTransactionsRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BorrowAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IFingetDatabaseHelper dbHelper = new IFingetDatabaseHelper(this);
        borrowActivityViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new BorrowActivityViewModel(dbHelper);
            }
        }).get(BorrowActivityViewModel.class);

        setupActionBar();
        setupBorrowTransactionsList();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.centered_title_with_back_btn);

        TextView titleTextView = findViewById(R.id.action_bar_title);
        titleTextView.setText(R.string.borrowed_transactions_title);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupBorrowTransactionsList() {
        RecyclerView borrowedTransactionsRecyclerView = findViewById(R.id.borrowTransactionsRecyclerView);
        borrowedTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        borrowedTransactionsRecycleAdapter = new BorrowedTransactionsRecycleAdapter(new ArrayList<>());
        borrowedTransactionsRecyclerView.setAdapter(borrowedTransactionsRecycleAdapter);

        TextView emptyRecyclerViewMessage = findViewById(R.id.emptyRecyclerViewMessage);

        borrowActivityViewModel.getBorrowedTransactions().observe(this, borrowDetails -> {
            if (borrowDetails.isEmpty()) {
                borrowedTransactionsRecyclerView.setVisibility(View.GONE);
                emptyRecyclerViewMessage.setVisibility(View.VISIBLE);
            } else {
                borrowedTransactionsRecyclerView.setVisibility(View.VISIBLE);
                emptyRecyclerViewMessage.setVisibility(View.GONE);
                borrowedTransactionsRecycleAdapter.updateData(borrowDetails);
            }
        });

    }
}
