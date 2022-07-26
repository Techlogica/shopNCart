package com.tids.shopncart.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tids.shopncart.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;


    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {


        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    //insert payment method
    public boolean addPaymentMethod(String paymentMethodName) {

        ContentValues values = new ContentValues();


        values.put(Constant.PAYMENT_METHOD_NAME, paymentMethodName);


        long check = database.insert(Constant.paymentMethod, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //update payment method
    public boolean updatePaymentMethod(String paymentMethodId, String paymentMethodName) {

        ContentValues values = new ContentValues();


        values.put(Constant.PAYMENT_METHOD_NAME, paymentMethodName);


        long check = database.update(Constant.paymentMethod, values, "payment_method_id=? ", new String[]{paymentMethodId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Add product into cart
    public int addProducts(String productId, String productName, String productCode, String productCatId, String productSellPrice,
                           String productCostPrice, String weight, String weightUnit,
                           String supplierName, String productImage, String productStock, String cgst, String sgst, String cess,
                           String totalOrderPrice, String totalTax, String totalDiscount, String editable, String categoryName) {


        Cursor result = database.rawQuery("SELECT * FROM products WHERE product_id='" + productId + "'", null);
        if (result.getCount() >= 1) {

            return 2;

        } else {
            ContentValues values = new ContentValues();
            values.put(Constant.PRODUCT_ID, productId);
            values.put(Constant.PRODUCT_NAME, productName);
            values.put(Constant.PRODUCT_CODE, productCode);
            values.put(Constant.PRODUCT_CATEGORY_ID, productCatId);
            values.put(Constant.PRODUCT_SELL_PRICE, productSellPrice);
            values.put(Constant.PRODUCT_COST_PRICE, productCostPrice);
            values.put(Constant.PRODUCT_WEIGHT, weight);
            values.put(Constant.PRODUCT_WEIGHT_UNIT_NAME, weightUnit);
            values.put(Constant.SUPPLIERS_NAME, supplierName);
            values.put(Constant.PRODUCT_IMAGE, productImage);
            values.put(Constant.PRODUCT_STOCK, productStock);
            values.put(Constant.KEY_CGST, cgst);
            values.put(Constant.KEY_SGST, sgst);
            values.put(Constant.KEY_CESS, cess);
            values.put(Constant.SP_TODAY_SALES, totalOrderPrice);
            values.put(Constant.TOTAL_TAX, totalTax);
            values.put(Constant.TOTAL_DISCOUNT, totalDiscount);
            values.put(Constant.EDITABLE, editable);
            values.put(Constant.CATEGORY, categoryName);

            long check = database.insert(Constant.products, null, values);


            result.close();
            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;

            }
        }

    }

    // get last invoice number
    public int getInvoice(){

        Cursor cursor = database.rawQuery("SELECT invoice_number FROM invoice ORDER BY id DESC LIMIT 1",null);
        cursor.moveToFirst();
        int rslt = cursor.getInt(0);

        cursor.close();
        database.close();

        return rslt;
    }

    //update invoice number
    public void updateInvoice(int number) {

        ContentValues values = new ContentValues();
        values.put(Constant.INVOICE_NUMBER, number);

        database.update("invoice", values, null,null);

    }

    // table row count
    public int invoiceNumberTableCount(){

        String countQuery = "SELECT  * FROM invoice";
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        database.close();

        return count;
    }


    //Add product into cart
    public int addCategory(String categoryId, String categoryName) {

        Cursor result = database.rawQuery("SELECT * FROM category WHERE product_category_id='" + categoryId + "'", null);
        if (result.getCount() >= 1) {

            return 2;

        } else {
            ContentValues values = new ContentValues();
            values.put(Constant.PRODUCT_CATEGORY_ID, categoryId);
            values.put(Constant.CATEGORY, categoryName);

            long check = database.insert(Constant.category, null, values);


            result.close();
            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    public int addClockData(String staffId, String clockInDate, String clockOutDate, String clockInTime, String clockOutTime,String totalTime, Boolean isClockIn,String clockId ) {


        Cursor result = database.rawQuery("SELECT * FROM clock_in_out WHERE staff_id='" + staffId + "'", null);

            ContentValues values = new ContentValues();
            values.put(Constant.SP_STAFF_ID, staffId);
            values.put(Constant.CLOCK_IN_DATE, clockInDate);
            values.put(Constant.CLOCK_OUT_DATE, clockOutDate);
            values.put(Constant.CLOCK_IN_TIME, clockInTime);
            values.put(Constant.CLOCK_OUT_TIME, clockOutTime);
            values.put(Constant.TOTAL_TIME, totalTime);
            values.put(Constant.STATUS, isClockIn);

            long check = database.insert(Constant.clockInOut, null, values);


            result.close();
            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }


    }

    public void updateClockData(String staffId, String clockInDate, String clockOutDate, String clockInTime, String clockOutTime,String totalTime, Boolean isClockIn,String clockId) {

        ContentValues values = new ContentValues();
        values.put(Constant.SP_STAFF_ID, staffId);
        values.put(Constant.CLOCK_IN_DATE, clockInDate);
        values.put(Constant.CLOCK_OUT_DATE, clockOutDate);
        values.put(Constant.CLOCK_IN_TIME, clockInTime);
        values.put(Constant.CLOCK_OUT_TIME, clockOutTime);
        values.put(Constant.TOTAL_TIME, totalTime);
        values.put(Constant.STATUS, isClockIn);

        database.update(Constant.clockInOut, values, "clock_id=?", new String[]{clockId});


    }

    //update stock
    public void updateStock(String productId, String productStock) {

        ContentValues values = new ContentValues();

        values.put("product_stock", productStock);

        database.update("products", values, "product_id=?", new String[]{productId});

    }


    public void updateProducts(String productId, String productName, String productCode, String productCatId, String productSellPrice,
                               String productCostPrice, String weight, String weightUnit,
                               String supplierName, String productImage, String productStock, String cgst, String sgst, String cess,
                               String totalOrderPrice, String totalTax, String totalDiscount, String editable, String categoryName) {

        ContentValues values = new ContentValues();
        values.put(Constant.PRODUCT_ID, productId);
        values.put(Constant.PRODUCT_NAME, productName);
        values.put(Constant.PRODUCT_CODE, productCode);
        values.put(Constant.PRODUCT_CATEGORY_ID, productCatId);
        values.put(Constant.PRODUCT_SELL_PRICE, productSellPrice);
        values.put(Constant.PRODUCT_COST_PRICE, productCostPrice);
        values.put(Constant.PRODUCT_WEIGHT, weight);
        values.put(Constant.PRODUCT_WEIGHT_UNIT_NAME, weightUnit);
        values.put(Constant.SUPPLIERS_NAME, supplierName);
        values.put(Constant.PRODUCT_IMAGE, productImage);
        values.put(Constant.PRODUCT_STOCK, productStock);
        values.put(Constant.KEY_CGST, cgst);
        values.put(Constant.KEY_SGST, sgst);
        values.put(Constant.KEY_CESS, cess);
        values.put(Constant.SP_TODAY_SALES, totalOrderPrice);
        values.put(Constant.TOTAL_TAX, totalTax);
        values.put(Constant.TOTAL_DISCOUNT, totalDiscount);
        values.put(Constant.EDITABLE, editable);
        values.put(Constant.CATEGORY, categoryName);

        database.update("products", values, "product_id=?", new String[]{productId});


    }

    //Add product into cart
    public int addToCart(String productId, String productName, String weight, String weightUnit, String price, Double qty, String productImage, String productStock, double cgst, double sgst, double cess, double discount, String cgstPercent, String sgstPercent, String cessPercent, double discountedTotal, double lineTotal, String editable) {


        Cursor result = database.rawQuery("SELECT * FROM product_cart WHERE product_id='" + productId + "'", null);
        if (result.getCount() >= 1) {

            return 2;

        } else {
            ContentValues values = new ContentValues();
            values.put(Constant.PRODUCT_ID, productId);
            values.put(Constant.PRODUCT_NAME, productName);
            values.put(Constant.PRODUCT_WEIGHT, weight);
            values.put(Constant.PRODUCT_WEIGHT_UNIT, weightUnit);
            values.put(Constant.PRODUCT_PRICE, price);
            values.put(Constant.PRODUCT_QTY, qty);
            values.put(Constant.PRODUCT_IMAGE, productImage);
            values.put(Constant.PRODUCT_STOCK, productStock);

            values.put(Constant.KEY_CGST, cgst);
            values.put(Constant.KEY_SGST, sgst);
            values.put(Constant.KEY_CESS, cess);
            values.put(Constant.PRODUCT_DISC, discount);
            values.put(Constant.PRODUCT_CGST_PERCENT, cgstPercent);
            values.put(Constant.PRODUCT_SGST_PERCENT, sgstPercent);
            values.put(Constant.PRODUCT_CESS_PERCENT, cessPercent);
            values.put(Constant.PRODUCT_DISCOUNTED_TOTAL, discountedTotal);
            values.put(Constant.PRODUCT_LINE_TOTAL, lineTotal);
            values.put(Constant.EDITABLE, editable);

            long check = database.insert(Constant.productCart, null, values);


            result.close();
            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    public int addScannedItem(String productId, String productName, String weight, String weightUnit, String price, Double qty, String productImage, String productStock, double cgst, double sgst, double cess, double discount, String cgstPercent, String sgstPercent, String cessPercent, double discountedTotal, double lineTotal, String editable) {

            ContentValues values = new ContentValues();
            values.put(Constant.PRODUCT_ID, productId);
            values.put(Constant.PRODUCT_NAME, productName);
            values.put(Constant.PRODUCT_WEIGHT, weight);
            values.put(Constant.PRODUCT_WEIGHT_UNIT, weightUnit);
            values.put(Constant.PRODUCT_PRICE, price);
            values.put(Constant.PRODUCT_QTY, qty);
            values.put(Constant.PRODUCT_IMAGE, productImage);
            values.put(Constant.PRODUCT_STOCK, productStock);

            values.put(Constant.KEY_CGST, cgst);
            values.put(Constant.KEY_SGST, sgst);
            values.put(Constant.KEY_CESS, cess);
            values.put(Constant.PRODUCT_DISC, discount);
            values.put(Constant.PRODUCT_CGST_PERCENT, cgstPercent);
            values.put(Constant.PRODUCT_SGST_PERCENT, sgstPercent);
            values.put(Constant.PRODUCT_CESS_PERCENT, cessPercent);
            values.put(Constant.PRODUCT_DISCOUNTED_TOTAL, discountedTotal);
            values.put(Constant.PRODUCT_LINE_TOTAL, lineTotal);
            values.put(Constant.EDITABLE, editable);

            long check = database.insert(Constant.productCart, null, values);
            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }



    //Add product into cart
    public int addToHold(String productId, String productName, String weight, String weightUnit, String price, double qty, String productImage, String productStock, double cgst, double sgst, double cess, double discount, String cgstPercent, String sgstPercent, String cessPercent, double discountedTotal, double lineTotal, String editable) {


        Cursor result = database.rawQuery("SELECT * FROM product_cart_hold WHERE product_id='" + productId + "'", null);
        if (result.getCount() >= 1) {

            return 2;

        } else {
            ContentValues values = new ContentValues();
            values.put(Constant.PRODUCT_ID, productId);
            values.put(Constant.PRODUCT_NAME, productName);
            values.put(Constant.PRODUCT_WEIGHT, weight);
            values.put(Constant.PRODUCT_WEIGHT_UNIT, weightUnit);
            values.put(Constant.PRODUCT_PRICE, price);
            values.put(Constant.PRODUCT_QTY, qty);
            values.put(Constant.PRODUCT_IMAGE, productImage);
            values.put(Constant.PRODUCT_STOCK, productStock);

            values.put(Constant.KEY_CGST, cgst);
            values.put(Constant.KEY_SGST, sgst);
            values.put(Constant.KEY_CESS, cess);
            values.put(Constant.PRODUCT_DISC, discount);
            values.put(Constant.PRODUCT_CGST_PERCENT, cgstPercent);
            values.put(Constant.PRODUCT_SGST_PERCENT, sgstPercent);
            values.put(Constant.PRODUCT_CESS_PERCENT, cessPercent);
            values.put(Constant.PRODUCT_DISCOUNTED_TOTAL, discountedTotal);
            values.put(Constant.PRODUCT_LINE_TOTAL, lineTotal);
            values.put(Constant.EDITABLE, editable);

            long check = database.insert(Constant.productCartHold, null, values);


            result.close();
            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    //get cart product
    public ArrayList<HashMap<String, String>> getCartProduct() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_cart ORDER BY cart_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.CART_ID, cursor.getString(cursor.getColumnIndex("cart_id")));
                map.put(Constant.PRODUCT_ID, cursor.getString(cursor.getColumnIndex("product_id")));
                map.put(Constant.PRODUCT_NAME, cursor.getString(cursor.getColumnIndex("product_name")));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(cursor.getColumnIndex("product_weight")));
                map.put(Constant.PRODUCT_WEIGHT_UNIT, cursor.getString(cursor.getColumnIndex("product_weight_unit")));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(cursor.getColumnIndex("product_price")));
                map.put(Constant.PRODUCT_QTY, cursor.getString(cursor.getColumnIndex("product_qty")));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(cursor.getColumnIndex("product_image")));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(cursor.getColumnIndex("product_stock")));
                map.put(Constant.KEY_CGST, cursor.getString(cursor.getColumnIndex("cgst")));
                map.put(Constant.KEY_SGST, cursor.getString(cursor.getColumnIndex("sgst")));
                map.put(Constant.KEY_CESS, cursor.getString(cursor.getColumnIndex("cess")));
                map.put(Constant.PRODUCT_DISC, cursor.getString(cursor.getColumnIndex("product_discount")));
                map.put(Constant.PRODUCT_CGST_PERCENT, cursor.getString(cursor.getColumnIndex("product_cegst_percent")));
                map.put(Constant.PRODUCT_SGST_PERCENT, cursor.getString(cursor.getColumnIndex("product_sgst_percent")));
                map.put(Constant.PRODUCT_CESS_PERCENT, cursor.getString(cursor.getColumnIndex("product_cess_percent")));
                map.put(Constant.PRODUCT_DISCOUNTED_TOTAL, cursor.getString(cursor.getColumnIndex("product_discounted_total")));
                map.put(Constant.PRODUCT_LINE_TOTAL, cursor.getString(cursor.getColumnIndex("product_line_total")));
                map.put(Constant.EDITABLE, cursor.getString(cursor.getColumnIndex("editable")));

                product.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return product;
    }

    //get product data
    public ArrayList<HashMap<String, String>> getProducts() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.PRODUCT_ID, cursor.getString(cursor.getColumnIndex("product_id")));
                map.put(Constant.PRODUCT_NAME, cursor.getString(cursor.getColumnIndex("product_name")));
                map.put(Constant.PRODUCT_CODE, cursor.getString(cursor.getColumnIndex("product_code")));
                map.put(Constant.PRODUCT_CATEGORY_ID, cursor.getString(cursor.getColumnIndex("product_category_id")));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(cursor.getColumnIndex("product_sell_price")));
                map.put(Constant.PRODUCT_COST_PRICE, cursor.getString(cursor.getColumnIndex("product_cost_price")));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(cursor.getColumnIndex("product_weight")));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_NAME, cursor.getString(cursor.getColumnIndex("product_weight_unit_name")));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(cursor.getColumnIndex("suppliers_name")));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(cursor.getColumnIndex("product_image")));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(cursor.getColumnIndex("product_stock")));
                map.put(Constant.KEY_CGST, cursor.getString(cursor.getColumnIndex("cgst")));
                map.put(Constant.KEY_SGST, cursor.getString(cursor.getColumnIndex("sgst")));
                map.put(Constant.KEY_CESS, cursor.getString(cursor.getColumnIndex("cess")));
                map.put(Constant.SP_TODAY_SALES, cursor.getString(cursor.getColumnIndex("total_order_price")));
                map.put(Constant.TOTAL_TAX, cursor.getString(cursor.getColumnIndex("total_tax")));
                map.put(Constant.TOTAL_DISCOUNT, cursor.getString(cursor.getColumnIndex("total_discount")));
                map.put(Constant.EDITABLE, cursor.getString(cursor.getColumnIndex("editable")));
                map.put(Constant.CATEGORY, cursor.getString(cursor.getColumnIndex("product_category_name")));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }

    public ArrayList<HashMap<String, String>> searchProducts(String searchTxt) {
        ArrayList<HashMap<String, String>> reportData = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM products  WHERE product_name='" + searchTxt + "'OR product_code='" + searchTxt + "'OR product_category_name='" + searchTxt + "'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.PRODUCT_ID, cursor.getString(cursor.getColumnIndex("product_id")));
                map.put(Constant.PRODUCT_NAME, cursor.getString(cursor.getColumnIndex("product_name")));
                map.put(Constant.PRODUCT_CODE, cursor.getString(cursor.getColumnIndex("product_code")));
                map.put(Constant.PRODUCT_CATEGORY_ID, cursor.getString(cursor.getColumnIndex("product_category_id")));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(cursor.getColumnIndex("product_sell_price")));
                map.put(Constant.PRODUCT_COST_PRICE, cursor.getString(cursor.getColumnIndex("product_cost_price")));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(cursor.getColumnIndex("product_weight")));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_NAME, cursor.getString(cursor.getColumnIndex("product_weight_unit_name")));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(cursor.getColumnIndex("suppliers_name")));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(cursor.getColumnIndex("product_image")));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(cursor.getColumnIndex("product_stock")));
                map.put(Constant.KEY_CGST, cursor.getString(cursor.getColumnIndex("cgst")));
                map.put(Constant.KEY_SGST, cursor.getString(cursor.getColumnIndex("sgst")));
                map.put(Constant.KEY_CESS, cursor.getString(cursor.getColumnIndex("cess")));
                map.put(Constant.SP_TODAY_SALES, cursor.getString(cursor.getColumnIndex("total_order_price")));
                map.put(Constant.TOTAL_TAX, cursor.getString(cursor.getColumnIndex("total_tax")));
                map.put(Constant.TOTAL_DISCOUNT, cursor.getString(cursor.getColumnIndex("total_discount")));
                map.put(Constant.EDITABLE, cursor.getString(cursor.getColumnIndex("editable")));
                map.put(Constant.CATEGORY, cursor.getString(cursor.getColumnIndex("product_category_name")));
                reportData.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return reportData;
    }


    public ArrayList<HashMap<String, String>> getCategory() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM category ORDER BY product_category_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.PRODUCT_CATEGORY_ID, cursor.getString(cursor.getColumnIndex("product_category_id")));
                map.put(Constant.CATEGORY, cursor.getString(cursor.getColumnIndex("product_category_name")));

                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //calculate total price of product
    public HashMap<String, String> getStaffClock() {

        HashMap<String, String> map = new HashMap<>();

        Cursor cursor = database.rawQuery("SELECT * FROM clock_in_out", null);
        if (cursor.moveToFirst()) {
            do {
                map.put(Constant.SP_STAFF_ID,cursor.getString(cursor.getColumnIndex("staff_id")));
                map.put(Constant.CLOCK_IN_DATE,cursor.getString(cursor.getColumnIndex("c_in_date")));
                map.put(Constant.CLOCK_OUT_DATE,cursor.getString(cursor.getColumnIndex("c_o_date")));
                map.put(Constant.CLOCK_IN_TIME,cursor.getString(cursor.getColumnIndex("clock_time_in")));
                map.put(Constant.CLOCK_OUT_TIME,cursor.getString(cursor.getColumnIndex("clock_time_out")));
                map.put(Constant.TOTAL_TIME,cursor.getString(cursor.getColumnIndex("time")));
                map.put(Constant.STATUS,cursor.getString(cursor.getColumnIndex("status")));
            } while (cursor.moveToNext());
        } else {
            map.clear();
        }
        cursor.close();
        database.close();
        return map;
    }

    //get sync cart table datas
    public ArrayList<HashMap<String, String>> getSyncCart() {

        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM sync_cart", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.CART_JSON_OBJECT, cursor.getString(cursor.getColumnIndex("cart_json_object")));
//                map.put(Constant.SYNC_PRODUCT_ID, cursor.getString(cursor.getColumnIndex("product_id")));
//                map.put(Constant.SYNC_FINAL_STOCK, cursor.getString(cursor.getColumnIndex("final_stock")));
                product.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return product;
    }

    // invoices table row count
    public int invoiceTableCount(){

        String countQuery = "SELECT  * FROM " + Constant.syncCart;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        database.close();

        return count;
    }


    //add sync cart table
    public boolean addSyncCart(String cart_json_object) {

        ContentValues values = new ContentValues();

        values.put(Constant.CART_JSON_OBJECT, cart_json_object);
//        values.put(Constant.SYNC_PRODUCT_ID, product_id);
//        values.put(Constant.SYNC_FINAL_STOCK, final_stock);

        long check = database.insert(Constant.syncCart, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //clear products
    public void clearCartInvoice() {

        database.delete(Constant.syncCart, null, null);
        database.close();
    }


    public ArrayList<HashMap<String, String>> getCartProductTemp() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_cart_hold ORDER BY cart_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.CART_ID, cursor.getString(cursor.getColumnIndex("cart_id")));
                map.put(Constant.PRODUCT_ID, cursor.getString(cursor.getColumnIndex("product_id")));
                map.put(Constant.PRODUCT_NAME, cursor.getString(cursor.getColumnIndex("product_name")));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(cursor.getColumnIndex("product_weight")));
                map.put(Constant.PRODUCT_WEIGHT_UNIT, cursor.getString(cursor.getColumnIndex("product_weight_unit")));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(cursor.getColumnIndex("product_price")));
                map.put(Constant.PRODUCT_QTY, cursor.getString(cursor.getColumnIndex("product_qty")));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(cursor.getColumnIndex("product_image")));
                map.put(Constant.PRODUCT_STOCK, cursor.getString(cursor.getColumnIndex("product_stock")));

                map.put(Constant.KEY_CGST, cursor.getString(cursor.getColumnIndex("cgst")));

                map.put(Constant.KEY_SGST, cursor.getString(cursor.getColumnIndex("sgst")));

                map.put(Constant.KEY_CESS, cursor.getString(cursor.getColumnIndex("cess")));

                map.put(Constant.PRODUCT_DISC, cursor.getString(cursor.getColumnIndex("product_discount")));
                map.put(Constant.PRODUCT_CGST_PERCENT, cursor.getString(cursor.getColumnIndex("product_cegst_percent")));
                map.put(Constant.PRODUCT_SGST_PERCENT, cursor.getString(cursor.getColumnIndex("product_sgst_percent")));
                map.put(Constant.PRODUCT_CESS_PERCENT, cursor.getString(cursor.getColumnIndex("product_cess_percent")));
                map.put(Constant.PRODUCT_DISCOUNTED_TOTAL, cursor.getString(cursor.getColumnIndex("product_discounted_total")));
                map.put(Constant.PRODUCT_LINE_TOTAL, cursor.getString(cursor.getColumnIndex("product_line_total")));
                map.put(Constant.EDITABLE, cursor.getString(cursor.getColumnIndex("editable")));

                product.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return product;
    }

    //clear products
    public void clearProducts() {

        database.delete(Constant.products, null, null);
        database.close();
    }

    //clear category
    public void clearCategory() {

        database.delete(Constant.category, null, null);
        database.close();
    }

    //clear clock
    public void clearClock() {

        database.delete(Constant.clockInOut, null, null);
        database.close();
    }

    //empty cart
    public void emptyCart() {

        database.delete(Constant.productCart, null, null);
        database.close();
    }

    //empty syncart
    public void emptySyncCart() {

        database.delete(Constant.syncCart, null, null);
        database.close();
    }

    //empty cart
    public void emptyCartHold() {

        database.delete(Constant.productCartHold, null, null);
        database.close();
    }


    //delete product from cart
    public boolean deleteProductFromCart(String id) {


        long check = database.delete(Constant.productCart, "cart_id=?", new String[]{id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    //get cart item count
    public int getCartItemCount() {

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        int itemCount = cursor.getCount();


        cursor.close();
        database.close();
        return itemCount;

    }

    //delete product from cart
    public void updateProductQty(String id, String qty) {

        ContentValues values = new ContentValues();

        values.put("product_qty", qty);

        database.update("product_cart", values, "cart_id=?", new String[]{id});

    }

    //get product qty from cart
    public String getProductQty(String id){

        String qty = "";

        String query = "SELECT product_qty FROM product_cart WHERE cart_id =" + id ;
        Cursor  cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            qty =  cursor.getString(cursor.getColumnIndex(Constant.PRODUCT_QTY));
        }

        return qty;

    }

    //get product cartId from cart
    public String getProductCartid(String id){

        String cartId = "";

        String query = "SELECT cart_id FROM product_cart WHERE product_id =" + id ;
        Cursor  cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            cartId =  cursor.getString(cursor.getColumnIndex(Constant.CART_ID));
        }

        return cartId;

    }

    //delete product from cart
    public void updateProductPrice(String id, String price) {

        ContentValues values = new ContentValues();

        values.put("product_price", price);

        database.update("product_cart", values, "cart_id=?", new String[]{id});


    }

    //update product discount cart
    public void updateProductDiscount(String id, String discount) {

        ContentValues values = new ContentValues();

        values.put("product_discount", discount);

        database.update("product_cart", values, "cart_id=?", new String[]{id});

    }



    public void updatecgst(String id, String cgst) {

        ContentValues values = new ContentValues();

        values.put("cgst", cgst);

        database.update("product_cart", values, "cart_id=?", new String[]{id});

    }

    public void updatesgst(String id, String sgst) {

        ContentValues values = new ContentValues();

        values.put("sgst", sgst);

        database.update("product_cart", values, "cart_id=?", new String[]{id});

    }

    public void updatecess(String id, String cess) {

        ContentValues values = new ContentValues();

        values.put("cess", cess);

        database.update("product_cart", values, "cart_id=?", new String[]{id});

    }

    //update product discount cart
    public void updateLineTotal(String id, String lineTotal) {

        ContentValues values = new ContentValues();

        values.put("product_line_total", lineTotal);

        database.update("product_cart", values, "cart_id=?", new String[]{id});

    }

    //get product name
    public String getCurrency() {

        String currency = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM shop", null);


        if (cursor.moveToFirst()) {
            do {


                currency = cursor.getString(5);


            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return currency;
    }

    //calculate total price of product
    public double getTotalPrice() {

        double totalPrice = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {
                double price;
                String prdPrice = cursor.getString(cursor.getColumnIndex("product_price"));
                if (prdPrice.contains(",")){
                    String convertedPrice = prdPrice.replace(",","");
                    price = Double.parseDouble(convertedPrice);
                }else {
                    price = Double.parseDouble(prdPrice);
                }
                double qty = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_qty")));
                double subTotal = price * qty;
                totalPrice = totalPrice + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();
        return totalPrice;
    }

    //calculate total price of product
    public double getFinalTotalPrice() {

        double totalPrice = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_line_total")));
                double qty = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_qty")));
                double subTotal = price * qty;
                totalPrice = totalPrice + price;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();
        return totalPrice;
    }

    //calculate total price of product
    public double getTotalTax() {


        double totalTax = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {

                double cgst = Double.parseDouble(cursor.getString(cursor.getColumnIndex("cgst")));
                double sgst = Double.parseDouble(cursor.getString(cursor.getColumnIndex("sgst")));
                double cess = Double.parseDouble(cursor.getString(cursor.getColumnIndex("cess")));
                double qty = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_qty")));
                double subTotal = cgst + sgst + cess;
                totalTax = totalTax + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalTax = 0;
        }
        cursor.close();
        database.close();
        return totalTax;
    }

    //calculate total CGST
    public double getTotalCGST() {

        double totalCGST = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {

                double cgst = Double.parseDouble(cursor.getString(cursor.getColumnIndex("cgst")));
                double qty = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_qty")));
                double subTotal = cgst * qty;
                totalCGST = totalCGST + cgst;


            } while (cursor.moveToNext());
        } else {
            totalCGST = 0;
        }
        cursor.close();
        database.close();
        return totalCGST;
    }

    //calculate total SGST
    public double getTotalSGST() {


        double totalSGST = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {

                double sgst = Double.parseDouble(cursor.getString(cursor.getColumnIndex("sgst")));
                double qty = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_qty")));
                double subTotal = sgst * qty;
                totalSGST = totalSGST + sgst;


            } while (cursor.moveToNext());
        } else {
            totalSGST = 0;
        }
        cursor.close();
        database.close();
        return totalSGST;
    }

    //calculate total SGST
    public double getTotalCESS() {


        double totalCess = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {

                double cess = Double.parseDouble(cursor.getString(cursor.getColumnIndex("cess")));
                double qty = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_qty")));
                double subTotal = cess * qty;
                totalCess = totalCess + cess;


            } while (cursor.moveToNext());
        } else {
            totalCess = 0;
        }
        cursor.close();
        database.close();
        return totalCess;
    }

    //calculate total discount
    public double getTotalProductDiscount() {


        double totalDiscount = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {
                double discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("product_discount")));
                totalDiscount = totalDiscount + discount;


            } while (cursor.moveToNext());
        } else {
            totalDiscount = 0;
        }
        cursor.close();
        database.close();
        return totalDiscount;
    }


    //calculate total discount of product
    public double getTotalDiscount(String type) {


        double totalDiscount = 0;
        Cursor cursor = null;


        if (type.equals(Constant.MONTHLY)) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM order_list WHERE strftime('%m', order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.YEARLY)) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_list WHERE strftime('%Y', order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.DAILY)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_list WHERE   order_date='" + currentDate + "' ORDER BY order_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM order_list", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("discount")));
                totalDiscount = totalDiscount + discount;


            } while (cursor.moveToNext());
        } else {
            totalDiscount = 0;
        }
        cursor.close();
        database.close();
        return totalDiscount;
    }


    //calculate total tax of product
    public double getTotalTax(String type) {


        double totalTax = 0;
        Cursor cursor = null;


        if (type.equals(Constant.MONTHLY)) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM order_list WHERE strftime('%m', order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.YEARLY)) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_list WHERE strftime('%Y', order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.DAILY)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_list WHERE   order_date='" + currentDate + "' ORDER BY order_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM order_list", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double tax = Double.parseDouble(cursor.getString(cursor.getColumnIndex("tax")));
                totalTax = totalTax + tax;


            } while (cursor.moveToNext());
        } else {
            totalTax = 0;
        }
        cursor.close();
        database.close();
        return totalTax;
    }


    //calculate total price of product
    public double getTotalOrderPrice(String type) {


        double totalPrice = 0;
        Cursor cursor = null;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM order_details WHERE strftime('%m', product_order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_details WHERE strftime('%Y', product_order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_details WHERE   product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM order_details", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(4));
                double qty = Double.parseDouble(cursor.getString(5));
                double subTotal = price * qty;
                totalPrice = totalPrice + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();
        return totalPrice;
    }


    //calculate total price of product
    public double totalOrderPrice(String invoiceId) {


        double totalPrice = 0;


        Cursor cursor = database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + invoiceId + "'", null);


        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(4));
                double qty = Double.parseDouble(cursor.getString(5));
                double subTotal = price * qty;
                totalPrice = totalPrice + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();
        return totalPrice;
    }


    //calculate total price of expense
    public double getTotalExpense(String type) {


        double totalCost = 0;
        Cursor cursor = null;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM expense", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double expense = Double.parseDouble(cursor.getString(3));

                totalCost = totalCost + expense;


            } while (cursor.moveToNext());
        } else {
            totalCost = 0;
        }
        cursor.close();
        database.close();
        return totalCost;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> getCustomers() {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM customers ORDER BY customer_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("customer_id", cursor.getString(0));
                map.put("customer_name", cursor.getString(1));
                map.put("customer_cell", cursor.getString(2));
                map.put("customer_email", cursor.getString(3));
                map.put("customer_address", cursor.getString(4));


                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }


    //get order type data
    public ArrayList<HashMap<String, String>> getOrderType() {
        ArrayList<HashMap<String, String>> orderType = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_type ORDER BY order_type_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("order_type_id", cursor.getString(0));
                map.put("order_type_name", cursor.getString(1));


                orderType.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderType;
    }


    //get order type data
    public ArrayList<HashMap<String, String>> getPaymentMethod() {
        ArrayList<HashMap<String, String>> paymentMethod = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method ORDER BY payment_method_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("payment_method_id", cursor.getString(0));
                map.put("payment_method_name", cursor.getString(1));


                paymentMethod.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return paymentMethod;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> searchCustomers(String s) {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM customers WHERE customer_name LIKE '%" + s + "%' ORDER BY customer_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("customer_id", cursor.getString(0));
                map.put("customer_name", cursor.getString(1));
                map.put("customer_cell", cursor.getString(2));
                map.put("customer_email", cursor.getString(3));
                map.put("customer_address", cursor.getString(4));


                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> searchSuppliers(String s) {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers WHERE suppliers_name LIKE '%" + s + "%' ORDER BY suppliers_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.SUPPLIERS_ID, cursor.getString(0));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(1));
                map.put(Constant.SUPPLIERS_CONTACT_PERSON, cursor.getString(2));
                map.put(Constant.SUPPLIERS_CELL, cursor.getString(3));
                map.put(Constant.SUPPLIERS_EMAIL, cursor.getString(4));
                map.put(Constant.SUPPLIERS_ADDRESS, cursor.getString(5));
                customer.add(map);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }

    //get shop information
    public ArrayList<HashMap<String, String>> getShopInformation() {
        ArrayList<HashMap<String, String>> shopInfo = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM shop", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("shop_name", cursor.getString(1));
                map.put("shop_contact", cursor.getString(2));
                map.put("shop_email", cursor.getString(3));
                map.put("shop_address", cursor.getString(4));
                map.put("shop_currency", cursor.getString(5));
                map.put("tax", cursor.getString(6));


                shopInfo.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shopInfo;
    }

    //get product data
    public ArrayList<HashMap<String, String>> getProductsInfo(String productId) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + productId + "'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.PRODUCT_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_NAME, cursor.getString(1));
                map.put(Constant.PRODUCT_CODE, cursor.getString(2));
                map.put(Constant.PRODUCT_CATEGORY, cursor.getString(3));
                map.put(Constant.PRODUCT_DESCRIPTION, cursor.getString(4));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_SUPPLIER, cursor.getString(6));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(7));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_ID, cursor.getString(8));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(9));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }

    //get product data
    public ArrayList<HashMap<String, String>> getAllExpense() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM expense ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.EXPENSE_ID, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_ID)));
                map.put(Constant.EXPENSE_NAME, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_NAME)));
                map.put(Constant.EXPENSE_NOTE, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_NOTE)));
                map.put(Constant.EXPENSE_AMOUNT, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_AMOUNT)));
                map.put(Constant.EXPENSE_DATE, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_DATE)));
                map.put(Constant.EXPENSE_TIME, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_TIME)));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }

    //get product category data
    public ArrayList<HashMap<String, String>> getProductCategory() {
        ArrayList<HashMap<String, String>> productCategory = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_category ORDER BY category_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("category_id", cursor.getString(0));
                map.put("category_name", cursor.getString(1));

                productCategory.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productCategory;
    }

    //get user data
    public ArrayList<HashMap<String, String>> getUsers() {
        ArrayList<HashMap<String, String>> users = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM users ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.ID, cursor.getString(cursor.getColumnIndex(Constant.ID)));
                map.put(Constant.USER_NAME, cursor.getString(cursor.getColumnIndex(Constant.USER_NAME)));
                map.put(Constant.USER_TYPE, cursor.getString(cursor.getColumnIndex(Constant.USER_TYPE)));
                map.put(Constant.USER_PHONE, cursor.getString(cursor.getColumnIndex(Constant.USER_PHONE)));
                map.put(Constant.USER_PASSWORD, cursor.getString(cursor.getColumnIndex(Constant.USER_PASSWORD)));

                users.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return users;
    }

    //get product category data
    public ArrayList<HashMap<String, String>> searchProductCategory(String s) {
        ArrayList<HashMap<String, String>> productCategory = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_category WHERE category_name LIKE '%" + s + "%' ORDER BY category_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.CATEGORY_ID, cursor.getString(0));
                map.put(Constant.CATEGORY_NAME, cursor.getString(1));

                productCategory.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productCategory;
    }


    //search user data
    public ArrayList<HashMap<String, String>> searchUser(String s) {
        ArrayList<HashMap<String, String>> userData = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE user_name LIKE '%" + s + "%' ORDER BY id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.ID, cursor.getString(cursor.getColumnIndex(Constant.ID)));
                map.put(Constant.USER_NAME, cursor.getString(cursor.getColumnIndex(Constant.USER_NAME)));
                map.put(Constant.USER_TYPE, cursor.getString(cursor.getColumnIndex(Constant.USER_TYPE)));
                map.put(Constant.USER_PHONE, cursor.getString(cursor.getColumnIndex(Constant.USER_PHONE)));
                map.put(Constant.USER_PASSWORD, cursor.getString(cursor.getColumnIndex(Constant.USER_PASSWORD)));
                userData.add(map);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return userData;
    }


    //get product payment method
    public ArrayList<HashMap<String, String>> searchPaymentMethod(String s) {
        ArrayList<HashMap<String, String>> paymentMethod = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method WHERE payment_method_name LIKE '%" + s + "%' ORDER BY payment_method_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.PAYMENT_METHOD_ID, cursor.getString(0));
                map.put(Constant.PAYMENT_METHOD_NAME, cursor.getString(1));

                paymentMethod.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paymentMethod;
    }


    //get product supplier data
    public ArrayList<HashMap<String, String>> getProductSupplier() {
        ArrayList<HashMap<String, String>> productSuppliers = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("suppliers_id", cursor.getString(0));
                map.put("suppliers_name", cursor.getString(1));

                productSuppliers.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productSuppliers;
    }


    //get product supplier data
    public ArrayList<HashMap<String, String>> getWeightUnit() {
        ArrayList<HashMap<String, String>> productWeightUnit = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_weight", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("weight_id", cursor.getString(0));
                map.put("weight_unit", cursor.getString(1));

                productWeightUnit.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productWeightUnit;
    }

    //get product data
    public ArrayList<HashMap<String, String>> searchExpense(String s) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM expense WHERE expense_name LIKE '%" + s + "%' ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put("expense_id", cursor.getString(cursor.getColumnIndex("expense_id")));
                map.put("expense_name", cursor.getString(cursor.getColumnIndex("expense_name")));
                map.put("expense_note", cursor.getString(cursor.getColumnIndex("expense_note")));
                map.put("expense_amount", cursor.getString(cursor.getColumnIndex("expense_amount")));
                map.put("expense_date", cursor.getString(cursor.getColumnIndex("expense_date")));
                map.put("expense_time", cursor.getString(cursor.getColumnIndex("expense_time")));


                product.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return product;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getSearchProducts(String s) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_name LIKE '%" + s + "%' OR product_code LIKE '%" + s + "%' ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put("product_id", cursor.getString(0));
                map.put("product_name", cursor.getString(1));
                map.put("product_code", cursor.getString(2));
                map.put("product_category", cursor.getString(3));
                map.put("product_description", cursor.getString(4));

                map.put("product_sell_price", cursor.getString(5));
                map.put("product_supplier", cursor.getString(6));
                map.put("product_image", cursor.getString(7));

                map.put("product_weight_unit_id", cursor.getString(8));
                map.put("product_weight", cursor.getString(9));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //get suppliers data
    public ArrayList<HashMap<String, String>> getSuppliers() {
        ArrayList<HashMap<String, String>> supplier = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers ORDER BY suppliers_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.SUPPLIERS_ID, cursor.getString(0));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(1));
                map.put(Constant.SUPPLIERS_CONTACT_PERSON, cursor.getString(2));
                map.put(Constant.SUPPLIERS_CELL, cursor.getString(3));
                map.put(Constant.SUPPLIERS_EMAIL, cursor.getString(4));
                map.put(Constant.SUPPLIERS_ADDRESS, cursor.getString(5));


                supplier.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return supplier;
    }


    //delete customer
    public boolean deleteCustomer(String customerId) {


        long check = database.delete("customers", "customer_id=?", new String[]{customerId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete category
    public boolean deleteUser(String id) {


        long check = database.delete("users", "id=?", new String[]{id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete category
    public boolean deleteCategory(String categoryId) {


        long check = database.delete("product_category", "category_id=?", new String[]{categoryId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }


    }


    //delete payment method
    public boolean deletePaymentMethod(String paymentMethodId) {


        long check = database.delete(Constant.paymentMethod, "payment_method_id=?", new String[]{paymentMethodId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete order
    public boolean deleteOrder(String invoiceId) {


        long check = database.delete(Constant.orderList, "invoice_id=?", new String[]{invoiceId});
        database.delete(Constant.orderDetails, "invoice_id=?", new String[]{invoiceId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete product
    public boolean deleteProduct(String productId) {


        long check = database.delete(Constant.products, "product_id=?", new String[]{productId});
        database.delete(Constant.productCart, "product_id=?", new String[]{productId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete product
    public boolean deleteExpense(String expenseId) {


        long check = database.delete(Constant.expense, "expense_id=?", new String[]{expenseId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete supplier
    public boolean deleteSupplier(String customerId) {


        long check = database.delete(Constant.suppliers, "suppliers_id=?", new String[]{customerId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }
}