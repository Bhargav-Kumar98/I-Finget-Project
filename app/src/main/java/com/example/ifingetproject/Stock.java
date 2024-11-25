package com.example.ifingetproject;
public class Stock {
    private String symbol;
    private String companyName;
    private String price;
    private String change;
    private String changePercent;
    private String volume;
    private String marketCap;

    public Stock(String symbol, String companyName, String price, String change, String changePercent, String volume, String marketCap) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
        this.volume = volume;
        this.marketCap = marketCap;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPrice() {
        return price;
    }

    public String getChange() {
        return change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public String getVolume() {
        return volume;
    }

    public String getMarketCap() {
        return marketCap;
    }
}