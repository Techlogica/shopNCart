package com.tids.shopncart;

public class Constant {

    Constant()
    {
        //write your action here if need
    }

    //For retrofit base url must end with /
//    public static final String BASE_URL = "https://demo.onlinesoftsell.com/shopncart";
//    public static final String BASE_URL = "http://techorbit.dyndns.org:7777/shopncart";
//    public static final String BASE_URL = "https://techorbit.dyndns.org/shopncart";

//    public static final String BASE_URL = "https://shopncart.co/app";  // live url
    public static final String BASE_URL = "https://shopncart.co/shopncart/app";  // url given by soniya

    //For retrofit base url must end with /
    public static final String PRODUCT_IMAGE_URL = BASE_URL+"/product_images/";


    //We will use this to store the user token number into shared preference
    public static final String SHARED_PREF_NAME = "com.app.shopncart"; //pcakage name+ id

    public static final String API_URL =BASE_URL+"/api/";

    public static final String SP_PHONE = "phone";
    public static final String SP_PASSWORD = "password";
    public static final String SP_USER_NAME = "user_name";
    public static final String SP_USER_TYPE = "user_type";
    public static final String PAYMENT_METHOD_ID = "payment_method_id";
    public static final String PAYMENT_METHOD_NAME="payment_method_name";


    public static final String ORDER_ID="order_id";

    public static final String CUSTOMER_ID="customer_id";
    public static final String CUSTOMER_NAME="customer_name";
    public static final String CUSTOMER_CELL="customer_cell";
    public static final String CUSTOMER_EMAIL="customer_email";
    public static final String CUSTOMER_ADDRESS="customer_address";


    public static final String SHOP_NUMBER="1";
    public static final String SHOP_ID="shop_id";

    public static final String SP_HEADER_DISCOUNT="header_dis";
    public static final String SP_HEADER_FLAG="header_flag";

    public static final String SP_SHOP_NAME="shop_name";
    public static final String SP_SHOP_ADDRESS="shop_address";
    public static final String SP_SHOP_EMAIL="shop_email";
    public static final String SP_SHOP_CONTACT="shop_contact";
    public static final String SP_SHOP_COUNTRY="shop_country";
    public static final String SP_SHOP_TAX_ID="Taxid";
    public static final String SP_TODAY_SALES="total_order_price";

    public static final String SP_SHOP_STATUS="shop_status";
    public static final String SP_CLOCK_TIME="clock_time";
    public static final String SP_CURRENCY_SYMBOL="currency_symbol";
    public static final String SP_TAX="tax";

    public static final String SP_SHOP_ID="shop_id";
    public static final String SP_OWNER_ID="owner_id";

    public static final String SP_F_DATE="f_date";
    public static final String SP_T_DATE="t_date";
    public static final String TIME1="time1";
    public static final String TIME2="time2";

    public static final String KEY_CGST="cgst";
    public static final String KEY_SGST="sgst";
    public static final String KEY_CESS="cess";

    public static final String INVOICE_ID="invoice_id";
    public static final String ORDER_DATE="order_date";
    public static final String ORDER_TIME="order_time";
    public static final String ORDER_PAYMENT_METHOD="order_payment_method";
    public static final String ORDER_TYPE="order_type";
    public static final String KEY_TYPE="type";
    public static final String KEY_CATEGORY_ID="category_id";
    public static final String TABLE_NO="table_no";
    public static final String TOTAL_TAX="total_tax";
    public static final String TOTAL_DISCOUNT="total_discount";

    public static final String STATUS_OPEN="OPEN";

    public static final String STATUS_CLOSED="CLOSED";

    public static final String SUCCESS="success";
    public static final String FAILURE="failure";
    public static final String TAX="tax";
    public static final String DISCOUNT="discount";
    public static final String ORDER_PRICE="order_price";

    public static final String ID="id";
    public static final String USER_NAME="user_name";
    public static final String USER_TYPE="user_type";
    public static final String USER_PHONE="user_phone";
    public static final String USER_PASSWORD="user_password";


    public static final String SUPPLIERS_ID="suppliers_id";
    public static final String SUPPLIERS_NAME="suppliers_name";
    public static final String SUPPLIERS_CONTACT_PERSON="suppliers_contact_person";
    public static final String SUPPLIERS_CELL="suppliers_cell";
    public static final String SUPPLIERS_EMAIL="suppliers_email";
    public static final String SUPPLIERS_ADDRESS="suppliers_address";
    public static final String KEY_FILE="file";
    public static final String PRODUCT_ID="product_id";
    public static final String PRODUCT_NAME="product_name";
    public static final String PRODUCT_CODE="product_code";
    public static final String PRODUCT_CATEGORY="product_category";
    public static final String PRODUCT_DESCRIPTION="product_description";
    public static final String PRODUCT_WEIGHT_UNIT_ID="product_weight_unit_id";
    public static final String PRODUCT_WEIGHT_UNIT="product_weight_unit";
    public static final String PRODUCT_WEIGHT_UNIT_NAME="product_weight_unit_name";
    public static final String PRODUCT_WEIGHT="product_weight";
    public static final String PRODUCT_STOCK="product_stock";
    public static final String PRODUCT_PRICE="product_price";
    public static final String PRODUCT_QTY="product_qty";
    public static final String PRODUCT_DISC="product_discount";
    public static final String PRODUCT_CGST_PERCENT="product_cegst_percent";
    public static final String PRODUCT_SGST_PERCENT="product_sgst_percent";
    public static final String PRODUCT_CESS_PERCENT="product_cess_percent";
    public static final String PRODUCT_DISCOUNTED_TOTAL="product_discounted_total";
    public static final String PRODUCT_LINE_TOTAL="product_line_total";
    public static final String EDITABLE="editable";
    public static final String PRODUCT_SUPPLIER="product_supplier";
    public static final String PRODUCT_SELL_PRICE="product_sell_price";
    public static final String PRODUCT_COST_PRICE="product_cost_price";
    public static final String PRODUCT_IMAGE="product_image";
    public static final String PRODUCT_ORDER_DATE="product_order_date";

    public static final String CART_ID="cart_id";


    public static final String CATEGORY_ID="category_id";
    public static final String CATEGORY_NAME="category_name";

    public static final String ORDER_RECEIPT="order_receipt";
    public static final String YEARLY="yearly";
    public static final String DAILY="daily";
    public static final String MONTHLY="monthly";

    public static final String ADMIN="admin";
    public static final String MANAGER="manager";


    public static final String EXPENSE_ID="expense_id";
    public static final String EXPENSE_NAME="expense_name";
    public static final String EXPENSE_NOTE="expense_note";
    public static final String EXPENSE_AMOUNT="expense_amount";
    public static final String EXPENSE_DATE="expense_date";
    public static final String EXPENSE_TIME="expense_time";

    public static final String CLOCK_ID="clock_id";
    public static final String CLOCK_IN_DATE="c_in_date";
    public static final String CLOCK_OUT_DATE="c_o_date";
    public static final String CLOCK_IN_TIME="clock_time_in";
    public static final String CLOCK_OUT_TIME="clock_time_out";
    public static final String TOTAL_TIME="time";
    public static final String STATUS="status";


    //all table names
    public static String customers="customers";
    public static String users="users";
    public static String suppliers="suppliers";
    public static String productCategory="product_category";
    public static String products="products";
    public static String category="category";
    public static String paymentMethod="payment_method";
    public static String expense="expense";
    public static String productCart="product_cart";
    public static String productCartHold="product_cart_hold";
    public static String orderList="order_list";
    public static String orderDetails="order_details";
    public static String clockInOut="clock_in_out";
    public static String syncCart = "sync_cart";



    public static final String CART_JSON_OBJECT = "cart_json_object";
    public static final String SYNC_PRODUCT_ID = "product_id";
    public static final String SYNC_FINAL_STOCK = "final_stock";

    public static final String SP_USERNAME = "username";
    public static final String SP_ID = "id";
    public static final String SP_NAME = "name";
    public static final String SP_EMAIL = "email";


    public static final String SP_STAFF_ID = "staff_id";
    public static final String SP_DEVICE_ID = "device_id";
    public static final String SP_STAFF_NAME = "staff_name";
    public static final String SP_STORE_ID = "store_id";
    public static final String SP_USER_ID = "user_id";

    public static final String SEARCH_TEXT = "search_text";
    public static final String CATEGORY = "product_category_name";
    public static final String PRODUCT_CATEGORY_ID = "product_category_id";
    public static final String IS_CLOCK_IN = "is_checked_in";

    public static final String SP_IS_FIRST_TIME = "is_first_time";
    public static final String SP_COUNTRY_CODE = "country_code";

    public static final String SP_PHONE_NO = "phone_no";
    public static final String SP_AUTH_TOKEN = "auth_token";

    public static final String SP_LOCATION_LAT = "location_lat";
    public static final String SP_LOCATION_LON = "location_lon";

    public static final String CURRENCY_SYMBOL= "৳ ";

    public static final String KEY_EMAIL= "email";

    public static final String KEY_TABLE_NAME= "table_name";
    public static final String KEY_UNIQUE_VALUE= "unique_value";

    public static final String KEY_PASSWORD= "password";

    public static final String KEY_JSON= "json";

    public static final String KEY_SUCCESS= "success";
    public static final String KEY_FAILURE= "failure";

    public static final String SP_VERIFICATION_CODE = "verification_code";


    public static final String INVOICE_NUMBER = "invoice_number";


}
