/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.expenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.IncomeSourceRecyclerAdapter;
import com.example.ifingetproject.R;
import com.example.ifingetproject.ui.expenses.ExpenseCategory;
import com.example.ifingetproject.ui.income.IncomeSource;

import java.util.List;

public class ExpenseCategoryRecyclerAdapter extends RecyclerView.Adapter<ExpenseCategoryRecyclerAdapter.ViewHolder> {
    private List<ExpenseCategory> expenseCategories;
    private ExpenseCategoryRecyclerAdapter.OnItemClickListener listener;
    private int expenseCategoryAmountTextColor;

    public interface OnItemClickListener {
        void onItemClick(ExpenseCategory expenseCategory);
    }

    public ExpenseCategoryRecyclerAdapter(List<ExpenseCategory> expenseCategories, ExpenseCategoryRecyclerAdapter.OnItemClickListener listener, int expenseCategoryAmountTextColor) {
        this.expenseCategories = expenseCategories;
        this.listener = listener;
        this.expenseCategoryAmountTextColor = expenseCategoryAmountTextColor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_source_item, parent, false);
        return new ExpenseCategoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseCategoryRecyclerAdapter.ViewHolder holder, int position) {
        ExpenseCategory expenseCategory = expenseCategories.get(position);
        holder.categoryName.setText(expenseCategory.getCategoryName());
        holder.amount.setText(String.format("- $%.2f", expenseCategory.getAmount()));
        holder.amount.setTextColor(expenseCategoryAmountTextColor);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(expenseCategory));
    }

    @Override
    public int getItemCount() {
        return expenseCategories.size();
    }

    public void updateData(List<ExpenseCategory> newExpenseCategories) {
        this.expenseCategories = newExpenseCategories;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView amount;

        ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.sourceNameTextView);
            amount = itemView.findViewById(R.id.sourceAmountTextView);
        }
    }
}
