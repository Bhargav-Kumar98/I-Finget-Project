/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject;

import java.time.LocalDate;

public class Transaction {
    private long id;
    private double amount;
    private LocalDate date;
    private String description;
    private String type;
    private String wallet;
    private String category;
    private String incomeSource;
    private String lendToName;
    private Double interest;
    private String borrowedFromName;
    private Double borrowedInterest;

    public Transaction(long id, double amount, LocalDate date, String description, String type, String wallet, String category, String incomeSource) {
        this(id, amount, date, description, type, wallet, category, incomeSource, null, null, null, null);
    }

    public Transaction(long id, double amount, LocalDate date, String description, String type, String wallet, String category, String incomeSource, String lendToName, Double interest, String borrowedFromName, Double borrowedInterest) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.type = type;
        this.wallet = wallet;
        this.category = category;
        this.incomeSource = incomeSource;
        this.lendToName = lendToName;
        this.interest = interest;
        this.borrowedFromName = borrowedFromName;
        this.borrowedInterest = borrowedInterest;
    }

    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getWallet() {
        return wallet;
    }

    public String getCategory() {
        return category;
    }

    public String getIncomeSource() {
        return incomeSource;
    }

    public String getLendToName() {
        return lendToName;
    }

    public Double getInterest() {
        return interest;
    }

    public String getBorrowedFromName() {
        return borrowedFromName;
    }

    public Double getBorrowedInterest() {
        return borrowedInterest;
    }
}