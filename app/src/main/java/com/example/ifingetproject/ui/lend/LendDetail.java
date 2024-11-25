package com.example.ifingetproject.ui.lend;

public class LendDetail {
    private String lend_To_Name;
    private double amount;
    private double interest;
    private double montlyInterestAmount;

    public LendDetail(String lend_To_Name, double amount, double interest, double montlyInterestAmount){
        this.lend_To_Name = lend_To_Name;
        this.amount = amount;
        this.interest = interest;
        this.montlyInterestAmount = montlyInterestAmount;
    }

    public double getMontlyInterestAmount() {
        return montlyInterestAmount;
    }

    public void setMontlyInterestAmount(double montlyInterestAmount) {
        this.montlyInterestAmount = montlyInterestAmount;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getLend_To_Name() {
        return lend_To_Name;
    }

    public void setLend_To_Name(String lend_To_Name) {
        this.lend_To_Name = lend_To_Name;
    }
}
