package com.example.ifingetproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.R;
import com.example.ifingetproject.CategoryBudgetStatus;

import java.util.ArrayList;
import java.util.List;

public class CategoryBudgetStatusAdapter extends RecyclerView.Adapter<CategoryBudgetStatusAdapter.ViewHolder> {

    private final Context context;
    private List<CategoryBudgetStatus> categoryBudgetStatuses;
    private String currentPeriod;

    public CategoryBudgetStatusAdapter(Context context) {
        this.context = context;
        this.categoryBudgetStatuses = new ArrayList<>();
    }

    public void setCurrentPeriod(String period) {
        this.currentPeriod = period;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.budget_status_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryBudgetStatus status = categoryBudgetStatuses.get(position);
        holder.categoryTextView.setText(status.getCategory());
        holder.monthTextView.setText(currentPeriod);
        holder.budgetTextView.setText(String.format("$%.2f", status.getBudget()));
        holder.spentTextView.setText(String.format("$%.2f", status.getSpent()));
        holder.balanceTextView.setText(String.format("$%.2f", status.getBalance()));
    }

    @Override
    public int getItemCount() {
        return categoryBudgetStatuses.size();
    }

    public void updateData(List<CategoryBudgetStatus> newStatuses) {
        categoryBudgetStatuses = newStatuses;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        TextView monthTextView;
        TextView budgetTextView;
        TextView spentTextView;
        TextView balanceTextView;

        ViewHolder(View view) {
            super(view);
            categoryTextView = view.findViewById(R.id.CategoryTextView);
            monthTextView = view.findViewById(R.id.monthTextView);
            budgetTextView = view.findViewById(R.id.budgetTextView);
            spentTextView = view.findViewById(R.id.spentTextView);
            balanceTextView = view.findViewById(R.id.balanceTextView);
        }
    }
}