package com.clikader.stockwatch;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable, Comparable<Stock> {
    private String code;
    private String companyName;
    private double lastPrice;
    private double change;
    private double percentage;

    public Stock(String sCode, String sCompanyName) {
        this.code = sCode;
        this.companyName = sCompanyName;
        this.lastPrice = 0.00;
        this.change = 0.00;
        this.percentage = 0.00;
    }

    public Stock(String sCode, String sCompanyName, double sLastPrice, double sChange, double sPercentage) {
        this.code = sCode;
        this.companyName = sCompanyName;
        this.lastPrice = sLastPrice;
        this.change = sChange;
        this.percentage = sPercentage;
    }

    public String getCode() {return code;}
    public String getCompanyName() {return companyName;}
    public double getLastPrice() {return lastPrice;}
    public double getChange() {return change;}
    public double getPercentage() {return percentage;}

    public void setCode(String code) {this.code = code;}
    public void setCompanyName(String companyName) {this.companyName = companyName;}
    public void setLastPrice(double lastPrice) {this.lastPrice = lastPrice;}
    public void setChange(double change) {this.change = change;}
    public void setPercentage(double percentage) {this.percentage = percentage;}

    @Override
    public String toString() {
        return code + " " + companyName + " " + lastPrice + " " + change + " " + percentage;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Stock)) return false;
        Stock stock = (Stock) obj;
        return getCode() != null ? getCode().equals(stock.getCode()) : stock.getCode() == null;
    }

    @Override
    public int hashCode() {
        return getCode() != null ? getCode().hashCode() : 0;
    }

    public int compareTo(@NonNull Stock s) {
        return getCode().compareTo(s.getCode());
    }
}
