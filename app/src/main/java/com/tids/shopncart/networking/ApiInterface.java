package com.tids.shopncart.networking;


import com.tids.shopncart.Constant;
import com.tids.shopncart.model.Category;
import com.tids.shopncart.model.Clock;
import com.tids.shopncart.model.Customer;
import com.tids.shopncart.model.Device;
import com.tids.shopncart.model.Expense;
import com.tids.shopncart.model.ExpenseReport;
import com.tids.shopncart.model.Login;
import com.tids.shopncart.model.MonthData;
import com.tids.shopncart.model.OrderDetails;
import com.tids.shopncart.model.OrderList;
import com.tids.shopncart.model.PayMethod;
import com.tids.shopncart.model.Product;
import com.tids.shopncart.model.SalesReport;
import com.tids.shopncart.model.ShopInformation;
import com.tids.shopncart.model.Suppliers;
import com.tids.shopncart.model.WeightUnit;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {


    //for login
    @FormUrlEncoded
    @POST("login.php")
    Call<Login> login(
            @Field(Constant.KEY_EMAIL) String email,
            @Field(Constant.KEY_PASSWORD) String password);


    //calling json array , need list
    @POST("orders_submit.php")
    Call<String> submitOrders(
            @Body RequestBody ordersData
    );


    //get customers data
    @GET("get_customer.php")
    Call<List<Customer>> getCustomers(
            @Query(Constant.SEARCH_TEXT) String searchText,
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId

    );

    //get customers data
    @GET("sales_summary.php")
    Call<List<PayMethod>> getPaymethod(
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_F_DATE) String fromDate,
            @Query(Constant.SP_T_DATE) String toDate,
            @Query(Constant.TIME1) String fromTime,
            @Query(Constant.TIME2) String toTime,
            @Query(Constant.SP_DEVICE_ID) String deviceId

    );


    //get customers data
    @GET("get_orders.php")
    Call<List<OrderList>> getOrders(
            @Query(Constant.SEARCH_TEXT) String searchText,
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String owner,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId


    );


    //get customers data
    @GET("get_products.php")
    Call<List<Product>> getProducts(
            @Query(Constant.SEARCH_TEXT) String searchText,
            @Query(Constant.SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId
    );

    //get customers data
    @GET("get_products.php")
    Call<List<Product>> getProductsStock(
            @Query(Constant.SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId

    );


    //get product data
    @GET("get_product_by_id.php")
    Call<List<Product>> getProductById(
            @Query(Constant.PRODUCT_ID) String productId,
            @Query(Constant.SHOP_ID) String shopId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId

    );


    //get order data
    @GET("order_details_by_invoice.php")
    Call<List<OrderDetails>> OrderDetailsByInvoice(
            @Query(Constant.INVOICE_ID) String invoiceId

    );


    //get order data
    @GET("sales_report_list.php")
    Call<List<OrderDetails>> getReportList(
            @Query(Constant.KEY_TYPE) String type,
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId

    );


    //get order data
    @GET("shop_information.php")
    Call<List<ShopInformation>> shopInformation(
            @Query(Constant.SHOP_ID) String shopId

    );


    //get order data
    @GET("get_sales_report.php")
    Call<List<SalesReport>> getSalesReport(

            @Query(Constant.KEY_TYPE) String type,
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId

    );


    //get expense data
    @GET("get_expense_report.php")
    Call<List<ExpenseReport>> getExpenseReport(

            @Query(Constant.KEY_TYPE) String type,
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId

    );


    //for monthly expense data
    @GET("get_monthly_expense.php")
    Call<List<MonthData>> getMonthlyExpense(
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId

    );


    //for monthly sales data
    @GET("get_monthly_sales.php")
    Call<List<MonthData>> getMonthlySales(
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId

    );


    //for category data
    @GET("get_category.php")
    Call<List<Category>> getCategory(
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId

    );

    //for category data
    @GET("get_shop_D_count.php")
    Call<Device> getDeviceCount(
            @Query(Constant.SP_SHOP_ID) String shopId
    );

    @FormUrlEncoded
    @POST("clock_time_in.php")
    Call<Clock> getClockData(
            @Field(Constant.SP_SHOP_ID) String shopId,
            @Field(Constant.SP_OWNER_ID) String ownerId,
            @Field(Constant.SP_STAFF_ID) String staffId,
            @Field(Constant.IS_CLOCK_IN) Boolean isClockIn
    );


    //for product data
    @GET("search_product.php")
    Call<List<Product>> searchProductByCategory(
            @Query(Constant.KEY_CATEGORY_ID) String categoryId
    );


    //add customer data to server
    @FormUrlEncoded
    @POST("add_customer.php")
    Call<Customer> addCustomers(
            @Field(Constant.CUSTOMER_NAME) String name,
            @Field(Constant.CUSTOMER_CELL) String cell,
            @Field(Constant.CUSTOMER_EMAIL) String email,
            @Field(Constant.CUSTOMER_ADDRESS) String address,
            @Field(Constant.SP_SHOP_ID) String shopId,
            @Field(Constant.SP_OWNER_ID) String ownerId,
            @Field(Constant.SP_STAFF_ID) String staffId);


    @FormUrlEncoded
    @POST("add_category.php")
    Call<Category> addCategory(
            @Field(Constant.CATEGORY) String name,
            @Field(Constant.SP_SHOP_ID) String shopId,
            @Field(Constant.SP_OWNER_ID) String ownerId,
            @Field(Constant.SP_STAFF_ID) String staffId);

    //delete customer
    @FormUrlEncoded
    @POST("delete_category.php")
    Call<Category> deleteCategory(
            @Field(Constant.PRODUCT_CATEGORY_ID) String categoryId
    );


    //add expense data to server
    @FormUrlEncoded
    @POST("add_expense.php")
    Call<Expense> addExpense(
            @Field(Constant.EXPENSE_NAME) String name,
            @Field(Constant.EXPENSE_AMOUNT) String amount,
            @Field(Constant.EXPENSE_NOTE) String note,
            @Field(Constant.EXPENSE_DATE) String date,
            @Field(Constant.EXPENSE_TIME) String time,
            @Field(Constant.SP_SHOP_ID) String shopId,
            @Field(Constant.SP_OWNER_ID) String ownerId,
            @Field(Constant.SP_STAFF_ID) String staffId,
            @Field(Constant.SP_DEVICE_ID) String deviceId

            );


    //update expense data to server
    @FormUrlEncoded
    @POST("update_expense.php")
    Call<Expense> updateExpense(
            @Field(Constant.EXPENSE_ID) String id,
            @Field(Constant.EXPENSE_NAME) String name,
            @Field(Constant.EXPENSE_AMOUNT) String amount,
            @Field(Constant.EXPENSE_NOTE) String note,
            @Field(Constant.EXPENSE_DATE) String date,
            @Field(Constant.EXPENSE_TIME) String time);


    //add suppliers data to server
    @FormUrlEncoded
    @POST("add_suppliers.php")
    Call<Suppliers> addSuppliers(
            @Field(Constant.SUPPLIERS_NAME) String name,
            @Field(Constant.SUPPLIERS_CONTACT_PERSON) String contactPerson,
            @Field(Constant.SUPPLIERS_CELL) String cell,
            @Field(Constant.SUPPLIERS_EMAIL) String email,
            @Field(Constant.SUPPLIERS_ADDRESS) String address,
            @Field(Constant.SP_SHOP_ID) String shopId,
            @Field(Constant.SP_OWNER_ID) String ownerId,
            @Field(Constant.SP_STAFF_ID) String staffId);


    //add suppliers data to server
    @FormUrlEncoded
    @POST("update_suppliers.php")
    Call<Suppliers> updateSuppliers(
            @Field(Constant.SUPPLIERS_ID) String suppliersId,
            @Field(Constant.SUPPLIERS_NAME) String name,
            @Field(Constant.SUPPLIERS_CONTACT_PERSON) String contactPerson,
            @Field(Constant.SUPPLIERS_CELL) String cell,
            @Field(Constant.SUPPLIERS_EMAIL) String email,
            @Field(Constant.SUPPLIERS_ADDRESS) String address);


    //update customer data to server
    @FormUrlEncoded
    @POST("update_customer.php")
    Call<Customer> updateCustomers(
            @Field(Constant.CUSTOMER_ID) String id,
            @Field(Constant.CUSTOMER_NAME) String name,
            @Field(Constant.CUSTOMER_CELL) String cell,
            @Field(Constant.CUSTOMER_EMAIL) String email,
            @Field(Constant.CUSTOMER_ADDRESS) String address);


    //delete customer
    @FormUrlEncoded
    @POST("delete_customer.php")
    Call<Customer> deleteCustomer(
            @Field(Constant.CUSTOMER_ID) String customerId
    );


    //delete customer
    @FormUrlEncoded
    @POST("delete_order.php")
    Call<OrderList> deleteOrder(
            @Field(Constant.INVOICE_ID) String invoiceId
    );


    //delete product
    @FormUrlEncoded
    @POST("delete_product.php")
    Call<Product> deleteProduct(
            @Field(Constant.PRODUCT_ID) String productId
    );


    //delete customer
    @FormUrlEncoded
    @POST("delete_expense.php")
    Call<Expense> deleteExpense(
            @Field(Constant.EXPENSE_ID) String expenseId
    );


    //delete supplier
    @FormUrlEncoded
    @POST("delete_supplier.php")
    Call<Suppliers> deleteSupplier(
            @Field(Constant.SUPPLIERS_ID) String suppliersId
    );


    //get suppliers data
    @GET("get_suppliers.php")
    Call<List<Suppliers>> getSuppliers(
            @Query(Constant.SEARCH_TEXT) String searchText,
            @Query(Constant.SP_SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId

    );


    //get weight unit
    @GET("get_weight_units.php")
    Call<List<WeightUnit>> getWeightUnits(
            @Query(Constant.SEARCH_TEXT) String searchText

    );


    //for upload image and info
    @Multipart
    @POST("add_product.php")
    Call<Product> addProduct(@Part MultipartBody.Part file,
                             @Part(Constant.KEY_FILE) RequestBody name,
                             @Part(Constant.PRODUCT_NAME) RequestBody productName,
                             @Part(Constant.PRODUCT_CODE) RequestBody productCode,
                             @Part(Constant.CATEGORY_ID) RequestBody categoryId,
                             @Part(Constant.PRODUCT_DESCRIPTION) RequestBody description,
                             @Part(Constant.PRODUCT_COST_PRICE) RequestBody costPrice,
                             @Part(Constant.PRODUCT_SELL_PRICE) RequestBody sellPrice,
                             @Part(Constant.PRODUCT_WEIGHT) RequestBody weight,
                             @Part(Constant.PRODUCT_WEIGHT_UNIT_ID) RequestBody weightUnitId,
                             @Part(Constant.SUPPLIERS_ID) RequestBody supplierId,
                             @Part(Constant.PRODUCT_STOCK) RequestBody stock,
                             @Part(Constant.KEY_CGST) RequestBody cgst,
                             @Part(Constant.KEY_SGST) RequestBody sgst,
                             @Part(Constant.KEY_CESS) RequestBody cess,
                             @Part(Constant.SP_SHOP_ID) RequestBody shopId,
                             @Part(Constant.SP_OWNER_ID) RequestBody ownerId,
                             @Part(Constant.SP_STAFF_ID) RequestBody staffId,
                             @Part(Constant.EDITABLE) RequestBody editable);


    //for upload image and info
    @Multipart
    @POST("update_product.php")
    Call<Product> updateProduct(@Part MultipartBody.Part file,
                                @Part(Constant.KEY_FILE) RequestBody name,
                                @Part(Constant.PRODUCT_NAME) RequestBody productName,
                                @Part(Constant.PRODUCT_CODE) RequestBody productCode,
                                @Part(Constant.CATEGORY_ID) RequestBody categoryId,
                                @Part(Constant.PRODUCT_DESCRIPTION) RequestBody description,
                                @Part(Constant.PRODUCT_COST_PRICE) RequestBody costPrice,
                                @Part(Constant.PRODUCT_SELL_PRICE) RequestBody sellPrice,
                                @Part(Constant.PRODUCT_WEIGHT) RequestBody weight,
                                @Part(Constant.PRODUCT_WEIGHT_UNIT_ID) RequestBody weightUnitId,
                                @Part(Constant.SUPPLIERS_ID) RequestBody supplierId,
                                @Part(Constant.PRODUCT_STOCK) RequestBody stock,
                                @Part(Constant.PRODUCT_ID) RequestBody product_id,
                                @Part(Constant.KEY_CGST) RequestBody cgst,
                                @Part(Constant.KEY_SGST) RequestBody sgst,
                                @Part(Constant.KEY_CESS) RequestBody cess,
                                @Part(Constant.EDITABLE) RequestBody editable
    );


    //for upload image and info
    @Multipart
    @POST("update_product_without_image.php")
    Call<Product> updateProductWithoutImage(

            @Part(Constant.PRODUCT_NAME) RequestBody productName,
            @Part(Constant.PRODUCT_CODE) RequestBody productCode,
            @Part(Constant.CATEGORY_ID) RequestBody categoryId,
            @Part(Constant.PRODUCT_DESCRIPTION) RequestBody description,
            @Part(Constant.PRODUCT_COST_PRICE) RequestBody costPrice,
            @Part(Constant.PRODUCT_SELL_PRICE) RequestBody sellPrice,
            @Part(Constant.PRODUCT_WEIGHT) RequestBody weight,
            @Part(Constant.PRODUCT_WEIGHT_UNIT_ID) RequestBody weightUnitId,
            @Part(Constant.SUPPLIERS_ID) RequestBody supplierId,
            @Part(Constant.PRODUCT_STOCK) RequestBody stock,
            @Part(Constant.PRODUCT_ID) RequestBody productId,
            @Part(Constant.KEY_CGST) RequestBody cgst,
            @Part(Constant.KEY_SGST) RequestBody sgst,
            @Part(Constant.KEY_CESS) RequestBody cess,
            @Part(Constant.EDITABLE) RequestBody editable

    );


    //get expense data
    @GET("get_expense.php")
    Call<List<Expense>> getExpense(
            @Query(Constant.SEARCH_TEXT) String searchText,
            @Query(Constant.SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId,
            @Query(Constant.SP_DEVICE_ID) String deviceId
    );


    //get expense data
    @GET("get_all_expense.php")
    Call<List<Expense>> getAllExpense(
            @Query(Constant.KEY_TYPE) String type,
            @Query(Constant.SHOP_ID) String shopId,
            @Query(Constant.SP_OWNER_ID) String ownerId,
            @Query(Constant.SP_STAFF_ID) String staffId

    );


}