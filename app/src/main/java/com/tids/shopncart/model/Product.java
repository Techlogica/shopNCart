package com.tids.shopncart.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {


    @SerializedName("product_id")
    private String productId;

    @SerializedName("product_name")
    private String productName;
    @SerializedName("product_code")
    private String product_code;

    @SerializedName("product_category_id")
    private String productCategoryId;

    @SerializedName("product_category_name")
    private String productCategoryName;

    @SerializedName("product_description")
    private String productDescription;

    @SerializedName("product_cost_price")
    private String productCostPrice;

    @SerializedName("product_sell_price")
    private String productSellPrice;


    @SerializedName("product_stock")
    private String productStock;


    @SerializedName("product_weight")
    private String productWeight;


    @SerializedName("weight_unit_name")
    private String productWeightUnit;


    @SerializedName("weight_unit_id")
    private String productWeightUnitId;


    @SerializedName("product_supplier_id")
    private String productSupplierId;

    @SerializedName("suppliers_name")
    private String supplierName;


    @SerializedName("product_image")
    private String productImage;


    @SerializedName("cgst")
    private String cgst;

    @SerializedName("sgst")
    private String sgst;

    @SerializedName("cess")
    private String cess;

    @SerializedName("total_order_price")
    private String total_order_price;

    @SerializedName("total_discount")
    private String total_today_discount;

    @SerializedName("total_tax")
    private String total_today_tax;

    @SerializedName("value")
    private String value;

    @SerializedName("message")
    private String message;

    @SerializedName("editable")
    private String editable;


    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProduct_code() {
        return product_code;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductCostPrice() {
        return productCostPrice;
    }

    public String getProductSellPrice() {
        return productSellPrice;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public String getProductWeightUnit() {
        return productWeightUnit;
    }

    public String getProductSupplierID() {
        return productSupplierId;
    }

    public String getProductSupplierName() {
        return supplierName;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductStock() {
        return productStock;
    }

    public String getProductWeightUnitId() {
        return productWeightUnitId;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public String getCgst() {
        return cgst;
    }
    public String getSgst() {
        return sgst;
    }
    public String getCess() {
        return cess;
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

    public String getEditable() {
        return editable;
    }
}