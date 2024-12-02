/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.goals;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ifingetproject.CategoryBudgetStatus;
import com.example.ifingetproject.IFingetDatabaseHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class GoalsActivityViewModel extends ViewModel {

    private final MutableLiveData<LocalDate> currentDate;
    private final MutableLiveData<Double> monthlyBudget;
    private final MutableLiveData<Double> periodBalance;
    private final MutableLiveData<Double> monthlyExpenses;
    private final MutableLiveData<List<CategoryBudgetStatus>> categoryBudgetStatuses;
    private final IFingetDatabaseHelper dbHelper;

    public GoalsActivityViewModel(IFingetDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        currentDate = new MutableLiveData<>(LocalDate.now());
        monthlyBudget = new MutableLiveData<>(0.0);
        periodBalance = new MutableLiveData<>(0.0);
        monthlyExpenses = new MutableLiveData<>(0.0);
        categoryBudgetStatuses = new MutableLiveData<>();
        updatePeriodData();
    }

    public LiveData<LocalDate> getCurrentDate() {
        return currentDate;
    }

    public LiveData<Double> getMonthlyBudget() {
        return monthlyBudget;
    }

    public LiveData<Double> getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public LiveData<Double> getPeriodBalance() {
        return periodBalance;
    }

    public LiveData<List<CategoryBudgetStatus>> getCategoryBudgetStatuses() {
        return categoryBudgetStatuses;
    }

    public void navigatePeriod(boolean forward) {
        LocalDate newDate = forward ?
                currentDate.getValue().plusMonths(1) :
                currentDate.getValue().minusMonths(1);
        currentDate.setValue(newDate);
        updatePeriodData();
    }

    private void updatePeriodData() {
        LocalDate date = currentDate.getValue();
        String month = date.format(DateTimeFormatter.ofPattern("MMMM"));
        int year = date.getYear();

        double[] budgetAndExpenses = dbHelper.getMonthlyBudgetAndExpenses(month, year);
        double budget = budgetAndExpenses[0];
        double expenses = budgetAndExpenses[1];

        monthlyBudget.setValue(budget);
        monthlyExpenses.setValue(expenses);
        periodBalance.setValue(budget - expenses);

        // Fetch category budget statuses
        List<CategoryBudgetStatus> statuses = dbHelper.getCategoryBudgetStatuses(month, year);
        categoryBudgetStatuses.setValue(statuses != null ? statuses : Collections.emptyList());
    }
}