package com.example.ifingetproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private List<Stock> stocks;

    public interface OnItemClickListener {
        void onItemClick(Stock stock);
    }
    private OnItemClickListener listener;

    public StockAdapter(List<Stock> stocks, OnItemClickListener listener) {
        this.stocks = stocks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stocks.get(position);
        holder.bind(stock);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(stock));
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public void updateStocks(List<Stock> newStocks) {
        this.stocks = newStocks;
        notifyDataSetChanged();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSymbol;
        private TextView textViewCompanyName;
        private TextView textViewPrice;
        private TextView textViewChange;
        private TextView textViewVolume;
        private TextView textViewMarketCap;
        private ImageView imageViewTrend;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSymbol = itemView.findViewById(R.id.textViewSymbol);
            textViewCompanyName = itemView.findViewById(R.id.textViewCompanyName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewChange = itemView.findViewById(R.id.textViewChange);
            textViewVolume = itemView.findViewById(R.id.textViewVolume);
            textViewMarketCap = itemView.findViewById(R.id.textViewMarketCap);
            imageViewTrend = itemView.findViewById(R.id.imageViewTrend);
        }

        public void bind(Stock stock) {
            textViewSymbol.setText(stock.getSymbol());
            textViewCompanyName.setText(stock.getCompanyName());
            textViewPrice.setText("$" + stock.getPrice());

            double change = Double.parseDouble(stock.getChange());
            String changeText = String.format("$%.2f (%.2f%%)", change, Double.parseDouble(stock.getChangePercent()));
            textViewChange.setText(changeText);

            if (change >= 0) {
                textViewChange.setTextColor(Color.GREEN);
                imageViewTrend.setImageResource(R.drawable.uptrend);
            } else {
                textViewChange.setTextColor(Color.RED);
                imageViewTrend.setImageResource(R.drawable.downtrend);
            }

            textViewVolume.setText("Volume: " + formatLargeNumber(Double.parseDouble(stock.getVolume())));
            textViewMarketCap.setText("Market Cap: " + formatLargeNumber(Double.parseDouble(stock.getMarketCap())));
        }

        private String formatLargeNumber(double value) {
            if (value < 1000) {
                return String.format("%.2f", value);
            }

            String[] units = {"", "K", "M", "B", "T"};
            int unitIndex = (int) (Math.log10(value) / 3);
            double scaledValue = value / Math.pow(1000, unitIndex);

            return String.format("%.1f%s", scaledValue, units[unitIndex]);
        }
    }
}