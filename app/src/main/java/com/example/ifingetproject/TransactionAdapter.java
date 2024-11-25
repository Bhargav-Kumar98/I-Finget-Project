package com.example.ifingetproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.R;
import com.example.ifingetproject.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public TransactionAdapter() {
        this.transactions = new ArrayList<>();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView typeTextView;
        private TextView categoryOrSourceTitle;
        private TextView categoryOrSourceTextView;
        private TextView amountTextView;
        private TextView walletTextView;
        private TextView dateTextView;
        private TextView descriptionTextView;
        private TextView descriptionTitle;
        private TextView interestTextView;
        private TextView interestTitle;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            categoryOrSourceTitle = itemView.findViewById(R.id.categoryOrSourceTitle);
            categoryOrSourceTextView = itemView.findViewById(R.id.categoryOrSourceTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            walletTextView = itemView.findViewById(R.id.walletTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTitle = itemView.findViewById(R.id.descriptionTitle);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);

            interestTextView = itemView.findViewById(R.id.interestTextView);
            interestTitle = itemView.findViewById(R.id.interestTitle);
        }

        public void bind(Transaction transaction) {
            typeTextView.setText(transaction.getType().substring(0, 1).toUpperCase() + transaction.getType().substring(1));

            switch (transaction.getType()) {
                case "expense":
                    categoryOrSourceTitle.setText("Category");
                    categoryOrSourceTextView.setText(transaction.getCategory());
                    interestTitle.setVisibility(View.GONE);
                    interestTextView.setVisibility(View.GONE);
                    break;
                case "income":
                    categoryOrSourceTitle.setText("Source");
                    categoryOrSourceTextView.setText(transaction.getIncomeSource());
                    interestTitle.setVisibility(View.GONE);
                    interestTextView.setVisibility(View.GONE);
                    break;
                case "lend":
                    categoryOrSourceTitle.setText("Lend To");
                    categoryOrSourceTextView.setText(transaction.getLendToName());
                    interestTitle.setVisibility(View.VISIBLE);
                    interestTextView.setVisibility(View.VISIBLE);
                    interestTextView.setText(String.format("%.2f%%", transaction.getInterest()));
                    break;
                case "borrow":
                    categoryOrSourceTitle.setText("Borrowed From");
                    categoryOrSourceTextView.setText(transaction.getBorrowedFromName());
                    interestTitle.setVisibility(View.VISIBLE);
                    interestTextView.setVisibility(View.VISIBLE);
                    interestTextView.setText(String.format("%.2f%%", transaction.getBorrowedInterest()));
                    break;
            }

            amountTextView.setText(String.format("$%.2f", transaction.getAmount()));
            walletTextView.setText(transaction.getWallet());
            dateTextView.setText(transaction.getDate().format(DATE_FORMATTER));

            if (transaction.getDescription() != null && !transaction.getDescription().isEmpty()) {
                descriptionTitle.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(transaction.getDescription());
            } else {
                descriptionTitle.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
            }

            int color;
            switch (transaction.getType()) {
                case "expense":
                    color = Color.RED;
                    break;
                case "lend":
                    color = Color.BLUE;
                    break;
                case "income":
                    color = Color.GREEN;
                    break;
                case "borrow":
                    color = Color.MAGENTA;
                    break;
                default:
                    color = Color.BLACK;
            }
            amountTextView.setTextColor(color);
            typeTextView.setTextColor(color);
        }
    }
}