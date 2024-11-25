package com.example.ifingetproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class TransactionsViewModel extends ViewModel {

    private final MutableLiveData<LocalDate> currentDate;
    private final MutableLiveData<String> currentPeriod;
    private final MutableLiveData<Double> periodIncome;
    private final MutableLiveData<Double> periodExpenses;
    private final MutableLiveData<Double> periodBalance;
    private final MutableLiveData<List<Transaction>> transactions;
    private IFingetDatabaseHelper dbHelper;

    public TransactionsViewModel(IFingetDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        currentDate = new MutableLiveData<>(LocalDate.now());
        currentPeriod = new MutableLiveData<>("Monthly");
        periodIncome = new MutableLiveData<>(0.0);
        periodExpenses = new MutableLiveData<>(0.0);
        periodBalance = new MutableLiveData<>(0.0);
        transactions = new MutableLiveData<>();
        updatePeriodData();
    }

    public LiveData<LocalDate> getCurrentDate() {
        return currentDate;
    }

    public LiveData<String> getCurrentPeriod() {
        return currentPeriod;
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

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public void setPeriod(String period) {
        currentPeriod.setValue(period);
        updatePeriodData();
    }

    public void previousPeriod() {
        LocalDate date = currentDate.getValue();
        String period = currentPeriod.getValue();
        if (date != null && period != null) {
            switch (period) {
                case "Daily":
                    currentDate.setValue(date.minusDays(1));
                    break;
                case "Weekly":
                    currentDate.setValue(date.minusWeeks(1));
                    break;
                case "Monthly":
                    currentDate.setValue(date.minusMonths(1));
                    break;
                case "3 Months":
                    currentDate.setValue(date.minusMonths(3));
                    break;
                case "6 Months":
                    currentDate.setValue(date.minusMonths(6));
                    break;
                case "Yearly":
                    currentDate.setValue(date.minusYears(1));
                    break;
            }
            updatePeriodData();
        }
    }

    public void nextPeriod() {
        LocalDate date = currentDate.getValue();
        String period = currentPeriod.getValue();
        if (date != null && period != null) {
            switch (period) {
                case "Daily":
                    currentDate.setValue(date.plusDays(1));
                    break;
                case "Weekly":
                    currentDate.setValue(date.plusWeeks(1));
                    break;
                case "Monthly":
                    currentDate.setValue(date.plusMonths(1));
                    break;
                case "3 Months":
                    currentDate.setValue(date.plusMonths(3));
                    break;
                case "6 Months":
                    currentDate.setValue(date.plusMonths(6));
                    break;
                case "Yearly":
                    currentDate.setValue(date.plusYears(1));
                    break;
            }
            updatePeriodData();
        }
    }

    private void updatePeriodData() {
        LocalDate date = currentDate.getValue();
        String period = currentPeriod.getValue();
        if (date != null && period != null) {
            LocalDate startDate = getStartDate(date, period);
            LocalDate endDate = getEndDate(date, period);

            double income = calculatePeriodIncome(startDate, endDate);
            double expenses = calculatePeriodExpenses(startDate, endDate);
            double balance = income - expenses;

            periodIncome.setValue(income);
            periodExpenses.setValue(expenses);
            periodBalance.setValue(balance);

            List<Transaction> periodTransactions = getPeriodTransactions(startDate, endDate);
            transactions.setValue(periodTransactions);
        }
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
                return date.withDayOfMonth(date.lengthOfMonth());
            case "6 Months":
                return date.withDayOfMonth(date.lengthOfMonth());
            case "Yearly":
                return date.withDayOfYear(date.lengthOfYear());
            default:
                return date;
        }
    }

    private double calculatePeriodIncome(LocalDate startDate, LocalDate endDate) {
        return dbHelper.getPeriodIncome(startDate, endDate);
    }

    private double calculatePeriodExpenses(LocalDate startDate, LocalDate endDate) {
        return dbHelper.getPeriodExpenses(startDate, endDate);
    }

    private List<Transaction> getPeriodTransactions(LocalDate startDate, LocalDate endDate) {
        return dbHelper.getPeriodTransactions(startDate, endDate);
    }

    public void setCurrentDate(LocalDate date) {
        currentDate.setValue(date);
        updatePeriodData();
    }

    public void setCurrentPeriod(String period) {
        currentPeriod.setValue(period);
        updatePeriodData();
    }
}