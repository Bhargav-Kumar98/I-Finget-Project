/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.borrow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.R;
import com.example.ifingetproject.ui.lend.LendDetail;

import java.util.List;
import java.util.Locale;

public class BorrowedTransactionsRecycleAdapter extends RecyclerView.Adapter<BorrowedTransactionsRecycleAdapter.ViewHolder> {
    private List<BorrowDetail> borrowDetails;

    public BorrowedTransactionsRecycleAdapter(List<BorrowDetail> borrowDetails){
        this.borrowDetails = borrowDetails;
    }

    @NonNull
    @Override
    public BorrowedTransactionsRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrowed_transaction_item, parent, false);
        return new ViewHolder(view);
    }

    /*@Override
    public void onBindViewHolder(@NonNull BorrowedTransactionsRecycleAdapter.ViewHolder holder, int position) {
        BorrowDetail borrowDetail = borrowDetails.get(position);
        holder.borrowedFromField.setText(borrowDetail.getBorrowedFromField());
        holder.borrowedAmount.setText(String.format(Locale.US, "$%.2f", borrowDetail.getBorrowedAmount()));
        holder.borrowedInterest.setText(String.format(Locale.US, "%.2f%%", borrowDetail.getBorrowedInterest()));
        holder.monthlyInterestAmount.setText(String.format(Locale.US, "$%.2f", borrowDetail.getMontlyInterestAmount()));
    }*/

    @Override
    public void onBindViewHolder(@NonNull BorrowedTransactionsRecycleAdapter.ViewHolder holder, int position) {
        BorrowDetail borrowDetail = borrowDetails.get(position);
        if (holder.borrowedFromField != null) {
            holder.borrowedFromField.setText(borrowDetail.getBorrowedFromField());
        }
        if (holder.borrowedAmount != null) {
            holder.borrowedAmount.setText(String.format(Locale.US, "$%.2f", borrowDetail.getBorrowedAmount()));
        }
        if (holder.borrowedInterest != null) {
            holder.borrowedInterest.setText(String.format(Locale.US, "%.2f%%", borrowDetail.getBorrowedInterest()));
        }
        if (holder.monthlyInterestAmount != null) {
            holder.monthlyInterestAmount.setText(String.format(Locale.US, "$%.2f", borrowDetail.getMontlyInterestAmount()));
        }
    }

    @Override
    public int getItemCount() {
        return borrowDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView borrowedFromField;
        TextView borrowedAmount;
        TextView borrowedInterest;
        TextView monthlyInterestAmount;

        ViewHolder(View itemView) {
            super(itemView);
            borrowedFromField = itemView.findViewById(R.id.borrowedFromTextView);
            borrowedAmount = itemView.findViewById(R.id.amountBorrowedTextView);
            borrowedInterest = itemView.findViewById(R.id.atInterestTextView);
            monthlyInterestAmount = itemView.findViewById(R.id.montlyInterestAmountTextView);
        }
    }

    public void updateData(List<BorrowDetail> newBorrowDetails) {
        this.borrowDetails = newBorrowDetails;
        notifyDataSetChanged();
    }
}
