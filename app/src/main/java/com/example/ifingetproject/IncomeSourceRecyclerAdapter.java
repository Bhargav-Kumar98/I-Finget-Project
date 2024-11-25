package com.example.ifingetproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.R;
import com.example.ifingetproject.ui.income.IncomeSource;

import java.util.List;

public class IncomeSourceRecyclerAdapter extends RecyclerView.Adapter<IncomeSourceRecyclerAdapter.ViewHolder> {
    private List<IncomeSource> incomeSources;
    private OnItemClickListener listener;
    private int incomeSourceAmountTextColor;

    public interface OnItemClickListener {
        void onItemClick(IncomeSource incomeSource);
    }

    public IncomeSourceRecyclerAdapter(List<IncomeSource> incomeSources, OnItemClickListener listener, int incomeSourceAmountTextColor) {
        this.incomeSources = incomeSources;
        this.listener = listener;
        this.incomeSourceAmountTextColor = incomeSourceAmountTextColor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_source_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IncomeSource incomeSource = incomeSources.get(position);
        holder.sourceName.setText(incomeSource.getName());
        holder.sourceAmount.setText(String.format("+ $%.2f", incomeSource.getAmount()));
        holder.sourceAmount.setTextColor(incomeSourceAmountTextColor);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(incomeSource));
    }

    @Override
    public int getItemCount() {
        return incomeSources.size();
    }

    public void updateData(List<IncomeSource> newIncomeSources) {
        this.incomeSources = newIncomeSources;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sourceName;
        TextView sourceAmount;

        ViewHolder(View itemView) {
            super(itemView);
            sourceName = itemView.findViewById(R.id.sourceNameTextView);
            sourceAmount = itemView.findViewById(R.id.sourceAmountTextView);
        }
    }
}