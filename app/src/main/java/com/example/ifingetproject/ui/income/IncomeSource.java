package com.example.ifingetproject.ui.income;

public class IncomeSource {
    private String name;
    private double amount;

    public IncomeSource(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }
}