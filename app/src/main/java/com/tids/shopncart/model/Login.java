package com.tids.shopncart.model;

import com.google.gson.annotations.SerializedName;

public class Login {


    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;

    @SerializedName("cell")
    private String cell;

    @SerializedName("store_id")
    private String storeId;

    @SerializedName("user_type")
    private String userType;

    @SerializedName("password")
    private String password;

    @SerializedName("value")
    private String value;
    @SerializedName("message")
    private String massage;

    @SerializedName("clock_time")
    private String clockTime;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("shop_address")
    private String shopAddress;

    @SerializedName("shop_Country")
    private String shopCountry;

    @SerializedName("shop_email")
    private String shopEmail;


    @SerializedName("shop_contact")
    private String shopContact;

    @SerializedName("tax")
    private String tax;
    @SerializedName("currency_symbol")
    private String currencySymbol;
    @SerializedName("shop_status")
    private String shopStatus;

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("owner_id")
    private String ownerID;
    @SerializedName("Taxid")
    private String tax_id;

    @SerializedName("header_flag")
    private String headerFlag;

    @SerializedName("header_dis")
    private String header_dis;

    @SerializedName("total_order_price")
    private String total_order_price;
    @SerializedName("total_discount")
    private String total_today_discount;
    @SerializedName("total_tax")
    private String total_today_tax;
    @SerializedName("price_edit_flag")
    private Boolean price_edit_flag;
    @SerializedName("edit_value")
    private String edit_value;






    public String getStaffId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCell() {
        return cell;
    }

    public String getPassword() {
        return password;
    }


    public String getValue() {
        return value;
    }


    public String getStoreID() {
        return storeId;
    }




    public String getUserType() {
        return userType;
    }

    public String getMassage() {
        return massage;
    }


    public String getShopName() {
        return shopName;
    }

    public String getClockTime() {
        return clockTime;
    }

    public void setClockTime(String clockTime) {
        this.clockTime = clockTime;
    }

    public String getShopAddress() {
        return shopAddress;
    }


    public String getShopEmail() {
        return shopEmail;
    }

    public String getShopContact() {
        return shopContact;
    }


    public String getShopStatus() {
        return shopStatus;
    }


    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getTax() {
        return tax;
    }


    public String getShopId() {
        return shopId;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getShopCountry() {
        return shopCountry;
    }

    public String getTax_id() {
        return tax_id;
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

    public String getHeaderFlag() {
        return headerFlag;
    }

    public String getHeader_dis() {
        return header_dis;
    }

    public Boolean getPrice_edit_flag() {
        return price_edit_flag;
    }

    public String getEdit_value() {
        return edit_value;
    }
}