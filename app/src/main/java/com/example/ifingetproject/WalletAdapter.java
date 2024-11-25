package com.example.ifingetproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WalletAdapter extends ArrayAdapter<Wallet> {
    private Context context;
    private List<Wallet> wallets;

    public WalletAdapter(Context context, List<Wallet> wallets) {
        super(context, 0, wallets);
        this.context = context;
        this.wallets = wallets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.income_source_item, parent, false);
        }

        Wallet currentWallet = wallets.get(position);

        TextView walletName = listItem.findViewById(R.id.sourceNameTextView);
        TextView walletBalance = listItem.findViewById(R.id.sourceAmountTextView);

        walletName.setText(currentWallet.getName());
        walletBalance.setText(String.format("$%.2f", currentWallet.getBalance()));

        return listItem;
    }
}