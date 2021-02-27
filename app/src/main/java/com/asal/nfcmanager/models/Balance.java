package com.asal.nfcmanager.models;

import com.google.gson.Gson;

public class Balance {
    private Integer balanceNo;
    private String balanceName;
    private Double balanceAmount;

    public Balance(Integer balanceNo, String balanceName, Double balanceAmount) {
        this.balanceNo = balanceNo;
        this.balanceName = balanceName;
        this.balanceAmount = balanceAmount;
    }

    public Integer getBalanceNo() {
        return balanceNo;
    }

    public void setBalanceNo(Integer balanceNo) {
        this.balanceNo = balanceNo;
    }

    public String getBalanceName() {
        return balanceName;
    }

    public void setBalanceName(String balanceName) {
        this.balanceName = balanceName;
    }

    public Double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
