package com.app.shopncart.model;

import com.google.gson.annotations.SerializedName;

public class Device {


    @SerializedName("no_of_device")
    private String deviceCount;

    @SerializedName("value")
    private String value;
    @SerializedName("shop_id")
    private String shop_id;

    public String getDeviceCount() {
        return deviceCount;
    }

    public String getValue() {
        return value;
    }

    public String getShop_id() {
        return shop_id;
    }
}