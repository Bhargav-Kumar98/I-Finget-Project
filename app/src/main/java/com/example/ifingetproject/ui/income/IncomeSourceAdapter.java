package com.example.ifingetproject.ui.income;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ifingetproject.R;

import java.util.List;

public class IncomeSourceAdapter extends ArrayAdapter<IncomeSource> {
    private Context context;
    private List<IncomeSource> incomeSources;

    public IncomeSourceAdapter(Context context, List<IncomeSource> incomeSources) {
        super(context, 0, incomeSources);
        this.context = context;
        this.incomeSources = incomeSources;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.income_source_item, parent, false);
        }

        IncomeSource currentIncomeSource = incomeSources.get(position);

        TextView sourceName = listItem.findViewById(R.id.sourceNameTextView);
        TextView sourceAmount = listItem.findViewById(R.id.sourceAmountTextView);

        sourceName.setText(currentIncomeSource.getName());
        sourceAmount.setText(String.format("$%.2f", currentIncomeSource.getAmount()));

        return listItem;
    }
}