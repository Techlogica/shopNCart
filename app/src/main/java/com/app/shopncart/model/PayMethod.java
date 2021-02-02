package com.app.shopncart.model;

import com.google.gson.annotations.SerializedName;

public class PayMethod {


    @SerializedName("name")
    private String name;

    @SerializedName("value")
    private String valueAmount;

    @SerializedName("product_total")
    private String totalSales;

    @SerializedName("product_expense_amount")
    private String totalExpense;

    @SerializedName("return_value")
    private String totalReturn;

    @SerializedName("product_value_discount")
    private String totalDiscount;

    @SerializedName("product_value_tax")
    private String totalTax;

    @SerializedName("f_date")
    private String fromDate;

    @SerializedName("t_date")
    private String toDate;

    @SerializedName("time1")
    private String startTime;

    @SerializedName("time2")
    private String endTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return valueAmount;
    }

    public String getTotalSales() {
        return totalSales;
    }

    public String getTotalExpense() {
        return totalExpense;
    }

    public void setValue(String valueAmount) {
        this.valueAmount = valueAmount;
    }

    public String getTotalReturn() {
        return totalReturn;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public String getTotalTax() {
        return totalTax;
    }
}