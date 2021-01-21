package com.app.shopncart.model;

import com.google.gson.annotations.SerializedName;

public class OrderSubmit {



    @SerializedName("value")
    private String value;
    @SerializedName("message")
    private String massage;
    @SerializedName("total_order_price")
    private String total_order_price;
    @SerializedName("total_discount")
    private String total_today_discount;
    @SerializedName("total_tax")
    private String total_today_tax;

    public String getValue() {
        return value;
    }

    public String getMassage() {
        return massage;
    }

    public String getTotal_order_price() {
        return total_order_price;
    }

    public String getTotal_today_discount() {
        return total_today_discount;
    }

    public String getTotal_today_tax() {
        return total_today_tax;
    }
}