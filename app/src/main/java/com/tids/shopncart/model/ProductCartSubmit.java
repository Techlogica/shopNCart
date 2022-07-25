package com.tids.shopncart.model;

import com.google.gson.annotations.SerializedName;

public class ProductCartSubmit {
    @SerializedName("value")
    private String value;
    @SerializedName("message")
    private String message;

    public ProductCartSubmit(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
