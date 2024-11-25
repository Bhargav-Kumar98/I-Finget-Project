package com.example.ifingetproject;

public class CategoryBudgetStatus {
    private String category;
    private double budget;
    private double spent;
    private double balance;

    public CategoryBudgetStatus(String category, double budget, double spent, double balance) {
        this.category = category;
        this.budget = budget;
        this.spent = spent;
        this.balance = balance;
    }

    public String getCategory() { return category; }
    public double getBudget() { return budget; }
    public double getSpent() { return spent; }
    public double getBalance() { return balance; }
}