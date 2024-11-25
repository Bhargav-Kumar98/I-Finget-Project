package com.example.ifingetproject.ui.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.Transaction;
import com.example.ifingetproject.ui.income.IncomeSource;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAnalysisViewModel extends ViewModel {

    private final MutableLiveData<LocalDate> currentDate;
    private final MutableLiveData<String> currentPeriod;
    private final MutableLiveData<Double> periodIncome;
    private final MutableLiveData<Double> periodExpenses;
    private final MutableLiveData<Double> periodBalance;
    private final IFingetDatabaseHelper dbHelper;

    private final MutableLiveData<List<ExpenseCategory>> expenseCatgeories = new MutableLiveData<>();

    public ExpenseAnalysisViewModel(IFingetDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;

        // Initialize MutableLiveData objects with initial values
        this.currentDate = new MutableLiveData<>(LocalDate.now());
        this.currentPeriod = new MutableLiveData<>("Monthly");
        this.periodIncome = new MutableLiveData<>(0.0);
        this.periodExpenses = new MutableLiveData<>(0.0);
        this.periodBalance = new MutableLiveData<>(0.0);

        updatePeriodData();
    }

    public LiveData<LocalDate> getCurrentDate() {
        return currentDate;
    }

    public LiveData<String> getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentDate(LocalDate date) {
        currentDate.setValue(date);
        updatePeriodData();
    }

    public void setCurrentPeriod(String period) {
        currentPeriod.setValue(period);
        updatePeriodData();
    }

    public LiveData<Double> getPeriodIncome() {
        return periodIncome;
    }

    public LiveData<Double> getPeriodExpenses() {
        return periodExpenses;
    }

    public LiveData<Double> getPeriodBalance() {
        return periodBalance;
    }

    private void updatePeriodData() {
        LocalDate date = currentDate.getValue();
        String period = currentPeriod.getValue();
        if (date != null && period != null) {
            LocalDate startDate = getStartDate(date, period);
            LocalDate endDate = getEndDate(date, period);

            double income = dbHelper.getPeriodIncome(startDate, endDate);
            double expenses = dbHelper.getPeriodExpenses(startDate, endDate);
            double balance = income - expenses;

            periodIncome.setValue(income);
            periodExpenses.setValue(expenses);
            periodBalance.setValue(balance);
        }
        updateExpenseCategories();
    }

    private LocalDate getStartDate(LocalDate date, String period) {
        switch (period) {
            case "Daily":
                return date;
            case "Weekly":
                return date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            case "Monthly":
                return date.withDayOfMonth(1);
            case "3 Months":
                return date.withDayOfMonth(1).minusMonths(2);
            case "6 Months":
                return date.withDayOfMonth(1).minusMonths(5);
            case "Yearly":
                return date.withDayOfYear(1);
            default:
                return date;
        }
    }

    private LocalDate getEndDate(LocalDate date, String period) {
        switch (period) {
            case "Daily":
                return date;
            case "Weekly":
                return date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
            case "Monthly":
                return date.withDayOfMonth(date.lengthOfMonth());
            case "3 Months":
                return date.plusMonths(2).withDayOfMonth(date.plusMonths(2).lengthOfMonth());
            case "6 Months":
                return date.plusMonths(5).withDayOfMonth(date.plusMonths(5).lengthOfMonth());
            case "Yearly":
                return date.withDayOfYear(date.lengthOfYear());
            default:
                return date;
        }
    }

    public LocalDate getStartDate() {
        return getStartDate(currentDate.getValue(), currentPeriod.getValue());
    }

    public LocalDate getEndDate() {
        return getEndDate(currentDate.getValue(), currentPeriod.getValue());
    }

    public LiveData<List<ExpenseCategory>> getExpenseCategories() {
        updateExpenseCategories();
        return expenseCatgeories;
    }

    private void updateExpenseCategories() {
        LocalDate startDate = getStartDate(currentDate.getValue(), currentPeriod.getValue());
        LocalDate endDate = getEndDate(currentDate.getValue(), currentPeriod.getValue());
        List<ExpenseCategory> expCats = dbHelper.getExpenseCategoriesForPeriod(startDate, endDate);
        expenseCatgeories.setValue(expCats != null ? expCats : new ArrayList<>());
    }

    public List<Transaction> getTransactionsForExpenseCategory(String categoryName, LocalDate startDate, LocalDate endDate) {
        return dbHelper.getTransactionsForExpenseCategory(categoryName, startDate, endDate);
    }
}