package com.example.ifingetproject.ui.lend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.R;
import com.example.ifingetproject.ui.expenses.ExpenseCategoryRecyclerAdapter;
import com.example.ifingetproject.ui.lend.LendTransactionRecyclerAdapter;

import java.util.List;
import java.util.Locale;

public class LendTransactionRecyclerAdapter extends RecyclerView.Adapter<LendTransactionRecyclerAdapter.ViewHolder> {
    private List<LendDetail> lendDetails;

    public LendTransactionRecyclerAdapter(List<LendDetail> lendDetails){
        this.lendDetails = lendDetails;
    }

    @NonNull
    @Override
    public LendTransactionRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lend_transaction_item, parent, false);
        return new LendTransactionRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LendTransactionRecyclerAdapter.ViewHolder holder, int position) {
        LendDetail lendDetail = lendDetails.get(position);
        holder.lend_To_Name.setText(lendDetail.getLend_To_Name());
        holder.amount.setText(String.format(Locale.US, "$%.2f", lendDetail.getAmount()));
        holder.interest.setText(String.format(Locale.US, "%.2f%%", lendDetail.getInterest()));
        holder.monthlyInterestAmount.setText(String.format(Locale.US, "$%.2f", lendDetail.getMontlyInterestAmount()));
    }

    @Override
    public int getItemCount() {
        return lendDetails.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lend_To_Name;
        TextView amount;
        TextView interest;
        TextView monthlyInterestAmount;
        ViewHolder(View itemView) {
            super(itemView);
            lend_To_Name = itemView.findViewById(R.id.lendToTextView);
            amount = itemView.findViewById(R.id.amountToRecieveTextView);
            interest = itemView.findViewById(R.id.acceptedInterestTextView);
            monthlyInterestAmount = itemView.findViewById(R.id.montlyInterestAmountTextView);
        }
    }

    public void updateData(List<LendDetail> newLendDetails) {
        this.lendDetails = newLendDetails;
        notifyDataSetChanged();
    }
}
