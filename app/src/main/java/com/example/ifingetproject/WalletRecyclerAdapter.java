package com.example.ifingetproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WalletRecyclerAdapter extends RecyclerView.Adapter<WalletRecyclerAdapter.ViewHolder> {
    private List<Wallet> wallets;

    public WalletRecyclerAdapter(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_source_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wallet wallet = wallets.get(position);
        holder.nameTextView.setText(wallet.getName());
        holder.balanceTextView.setText(String.format("$%.2f", wallet.getBalance()));
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public void updateData(List<Wallet> newWallets) {
        this.wallets = newWallets;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView balanceTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.sourceNameTextView);
            balanceTextView = itemView.findViewById(R.id.sourceAmountTextView);
        }
    }
}