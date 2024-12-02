/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SourceTransactionsAdapter extends RecyclerView.Adapter<SourceTransactionsAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private int amountTextColor;

    public SourceTransactionsAdapter(List<Transaction> transactions, int amountTextColor) {
        this.transactions = transactions;
        this.amountTextColor = amountTextColor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_transactions_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.walletTextView.setText(transaction.getWallet());
        holder.amountTextView.setText(String.format("$%.2f", transaction.getAmount()));
        holder.dateTextView.setText(transaction.getDate().toString());
        holder.descriptionTextView.setText(transaction.getDescription());
        holder.amountTextView.setTextColor(amountTextColor);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView walletTextView;
        TextView amountTextView;
        TextView dateTextView;
        TextView descriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            walletTextView = itemView.findViewById(R.id.walletTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}