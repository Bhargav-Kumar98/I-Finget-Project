/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject;

public class Wallet {
    private String name;
    private double balance;

    public Wallet(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }
}