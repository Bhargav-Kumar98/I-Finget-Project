/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class SummaryCardView extends CardView {

    private Spinner timePeriodSpinner;
    private ImageButton leftArrowButton;
    private ImageButton rightArrowButton;
    private TextView periodTextView;
    private TextView incomeTextView;
    private TextView expensesTextView;
    private TextView balanceTextView;

    private LocalDate currentDate;
    private String currentPeriod;

    public SummaryCardView(Context context) {
        super(context);
        init(context);
    }

    public SummaryCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SummaryCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.summary_card_view, this, true);

        timePeriodSpinner = findViewById(R.id.timePeriodSpinner);
        leftArrowButton = findViewById(R.id.leftArrowButton);
        rightArrowButton = findViewById(R.id.rightArrowButton);
        periodTextView = findViewById(R.id.periodTextView);
        incomeTextView = findViewById(R.id.incomeTextView);
        expensesTextView = findViewById(R.id.expensesTextView);
        balanceTextView = findViewById(R.id.balanceTextView);

        setupTimePeriodSpinner();
        setupNavigationButtons();

        currentDate = LocalDate.now();
        currentPeriod = "Monthly";
        updatePeriodDisplay();
    }

    private void setupTimePeriodSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.time_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timePeriodSpinner.setAdapter(adapter);

        int monthlyIndex = adapter.getPosition("Monthly");
        timePeriodSpinner.setSelection(monthlyIndex);

        timePeriodSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                currentPeriod = parent.getItemAtPosition(position).toString();
                updatePeriodDisplay();
                if (onPeriodChangeListener != null) {
                    onPeriodChangeListener.onPeriodChanged(currentDate, currentPeriod);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupNavigationButtons() {
        leftArrowButton.setOnClickListener(v -> {
            navigatePeriod(false);
            if (onPeriodChangeListener != null) {
                onPeriodChangeListener.onPeriodChanged(currentDate, currentPeriod);
            }
        });

        rightArrowButton.setOnClickListener(v -> {
            navigatePeriod(true);
            if (onPeriodChangeListener != null) {
                onPeriodChangeListener.onPeriodChanged(currentDate, currentPeriod);
            }
        });
    }

    private void navigatePeriod(boolean forward) {
        switch (currentPeriod) {
            case "Daily":
                currentDate = forward ? currentDate.plusDays(1) : currentDate.minusDays(1);
                break;
            case "Weekly":
                currentDate = forward ? currentDate.plusWeeks(1) : currentDate.minusWeeks(1);
                break;
            case "Monthly":
                currentDate = forward ? currentDate.plusMonths(1) : currentDate.minusMonths(1);
                break;
            case "3 Months":
                currentDate = forward ? currentDate.plusMonths(3) : currentDate.minusMonths(3);
                break;
            case "6 Months":
                currentDate = forward ? currentDate.plusMonths(6) : currentDate.minusMonths(6);
                break;
            case "Yearly":
                currentDate = forward ? currentDate.plusYears(1) : currentDate.minusYears(1);
                break;
        }
        updatePeriodDisplay();
    }

    private void updatePeriodDisplay() {
        DateTimeFormatter formatter;
        String displayText;

        switch (currentPeriod) {
            case "Daily":
                formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                displayText = currentDate.format(formatter);
                break;
            case "Weekly":
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                LocalDate weekStart = currentDate.with(weekFields.dayOfWeek(), 1);
                LocalDate weekEnd = weekStart.plusDays(6);
                formatter = DateTimeFormatter.ofPattern("dd MMM");
                displayText = weekStart.format(formatter) + " - " + weekEnd.format(formatter) + " " + weekStart.getYear();
                break;
            case "Monthly":
                formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
                displayText = currentDate.format(formatter);
                break;
            case "3 Months":
            case "6 Months":
                LocalDate periodStart = currentDate.withDayOfMonth(1).minusMonths(currentPeriod.equals("3 Months") ? 2 : 5);
                formatter = DateTimeFormatter.ofPattern("MMM");
                displayText = periodStart.format(formatter) + " - " + currentDate.format(formatter) + " " + currentDate.getYear();
                break;
            case "Yearly":
                formatter = DateTimeFormatter.ofPattern("yyyy");
                displayText = currentDate.format(formatter);
                break;
            default:
                displayText = currentDate.toString();
        }

        periodTextView.setText(displayText);
    }

    public void setIncome(double income) {
        incomeTextView.setText(String.format("$%.2f", income));
    }

    public void setExpenses(double expenses) {
        expensesTextView.setText(String.format("$%.2f", expenses));
    }

    public void setBalance(double balance) {
        balanceTextView.setText(String.format("$%.2f", balance));
    }

    public interface OnPeriodChangeListener {
        void onPeriodChanged(LocalDate date, String period);
    }

    private OnPeriodChangeListener onPeriodChangeListener;

    public void setOnPeriodChangeListener(OnPeriodChangeListener listener) {
        this.onPeriodChangeListener = listener;
    }

    public void setCurrentDate(LocalDate date) {
        this.currentDate = date;
        updatePeriodDisplay();
    }

    public void setCurrentPeriod(String period) {
        this.currentPeriod = period;
        updatePeriodDisplay();
    }
}