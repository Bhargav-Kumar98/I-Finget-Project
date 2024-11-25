package com.example.ifingetproject.ui.borrow;

public class BorrowDetail {
    private String borrowedFromField;
    private String into_wallet;
    private double borrowedAmount;
    private double borrowedInterest;
    private double montlyInterestAmount;

    public BorrowDetail(String borrowedFromField, String into_wallet, double borrowedAmount, double borrowedInterest, double montlyInterestAmount){
        this.borrowedFromField = borrowedFromField;
        this.into_wallet = into_wallet;
        this.borrowedAmount = borrowedAmount;
        this.borrowedInterest = borrowedInterest;
        this.montlyInterestAmount = montlyInterestAmount;
    }

    public String getBorrowedFromField() {
        return borrowedFromField;
    }

    public void setBorrowedFromField(String borrowedFromField) {
        this.borrowedFromField = borrowedFromField;
    }

    public String getInto_wallet() {
        return into_wallet;
    }

    public void setInto_wallet(String into_wallet) {
        this.into_wallet = into_wallet;
    }

    public double getBorrowedAmount() {
        return borrowedAmount;
    }

    public void setBorrowedAmount(double borrowedAmount) {
        this.borrowedAmount = borrowedAmount;
    }

    public double getBorrowedInterest() {
        return borrowedInterest;
    }

    public void setBorrowedInterest(double borrowedInterest) {
        this.borrowedInterest = borrowedInterest;
    }

    public double getMontlyInterestAmount() {
        return montlyInterestAmount;
    }

    public void setMontlyInterestAmount(double montlyInterestAmount) {
        this.montlyInterestAmount = montlyInterestAmount;
    }
}
