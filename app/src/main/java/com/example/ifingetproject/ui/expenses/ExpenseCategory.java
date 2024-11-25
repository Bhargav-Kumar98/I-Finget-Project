package com.example.ifingetproject.ui.expenses;

public class ExpenseCategory {
    private String categoryName;
    private double amount;

    public ExpenseCategory(String categoryName, double amount) {
        this.categoryName = categoryName;
        this.amount = amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getAmount() {
        return amount;
    }
}
