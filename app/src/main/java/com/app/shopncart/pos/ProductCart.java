package com.app.shopncart.pos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmedelsayed.sunmiprinterutill.PrintMe;
import com.app.shopncart.Constant;
import com.app.shopncart.R;
import com.app.shopncart.adapter.CartAdapter;
import com.app.shopncart.database.DatabaseAccess;
import com.app.shopncart.model.Customer;
import com.app.shopncart.model.OrderDetails;
import com.app.shopncart.networking.ApiClient;
import com.app.shopncart.networking.ApiInterface;
import com.app.shopncart.utils.BaseActivity;
import com.app.shopncart.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCart extends BaseActivity {

    CartAdapter productCartAdapter;
    ImageView imgNoProduct;
    Button btnSubmitOrder;
    TextView txtNoProduct, txtTotalPrice, txtTotalCgst,txtTotalSgst, txtTotalCess, txtFinalTotal,txtTotalDiscount;
    LinearLayout linearLayout;
    DatabaseAccess databaseAccess;
    ProgressDialog loading;
    double getTax = 0;

    double getSgst = 0;
    double getCgst = 0;
    double getCess = 0;

    String discount1 = "";
    String dateTime = "";
    double orderPrice = 0;
    String customerName = "";
    double calculatedTotalCostPrint = 0;
    List<OrderDetails> orderDetails = new ArrayList<>();
    DecimalFormat decimn = new DecimalFormat("#,###,#0.00");
    private PrintMe printMe;


    List<String> customerNames, orderTypeNames, paymentMethodNames;
    List<Customer> customerData;
    ArrayAdapter<String> customerAdapter, orderTypeAdapter, paymentMethodAdapter;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userType;
    String servedBy, staffId, shopTax, currency, shopID, ownerId;
    String shopName = "";
    String address = "";
    String email = "";
    String contact = "";
    String country = "";

    DecimalFormat f;
    String invoiceNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);
        printMe = new PrintMe(this);
        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_cart);
        f = new DecimalFormat("#0.00");
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userType = sp.getString(Constant.SP_USER_TYPE, "");
        servedBy = sp.getString(Constant.SP_STAFF_NAME, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");
        shopTax = sp.getString(Constant.SP_TAX, "");
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");

        shopName = sp.getString(Constant.SP_SHOP_NAME, "");
        address = sp.getString(Constant.SP_SHOP_ADDRESS, "");
        email = sp.getString(Constant.SP_EMAIL, "");
        contact = sp.getString(Constant.SP_SHOP_CONTACT, "");
        country = sp.getString(Constant.SP_SHOP_COUNTRY, "");

        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");


        getCustomers(shopID, ownerId);

        RecyclerView recyclerView = findViewById(R.id.cart_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);
        txtNoProduct = findViewById(R.id.txt_no_product);
        linearLayout = findViewById(R.id.linear_layout);

        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTotalDiscount = findViewById(R.id.txt_total_discount);
        txtTotalCgst = findViewById(R.id.txt_total_cgst);
        txtTotalSgst = findViewById(R.id.txt_total_sgst);
        txtTotalCess = findViewById(R.id.txt_total_cess);
        txtFinalTotal = findViewById(R.id.txt_price_with_tax);

        txtNoProduct.setVisibility(View.GONE);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);


        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> cartProductList;
        cartProductList = databaseAccess.getCartProduct();

        Log.d("CartSize", "" + cartProductList.size());

        if (cartProductList.isEmpty()) {

            imgNoProduct.setImageResource(R.drawable.empty_cart);
            imgNoProduct.setVisibility(View.VISIBLE);
            txtNoProduct.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
            txtTotalSgst.setVisibility(View.GONE);
            txtTotalCgst.setVisibility(View.GONE);
            txtTotalCess.setVisibility(View.GONE);
            txtFinalTotal.setVisibility(View.GONE);
            txtTotalDiscount.setVisibility(View.GONE);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            productCartAdapter = new CartAdapter(ProductCart.this, cartProductList, txtTotalPrice, btnSubmitOrder, imgNoProduct, txtNoProduct,txtTotalCgst,txtTotalSgst, txtTotalCess, txtFinalTotal,txtTotalDiscount);

            recyclerView.setAdapter(productCartAdapter);


        }


        btnSubmitOrder.setOnClickListener(v -> dialog());

    }

    public void proceedOrder(String type, String paymentMethod, String customerName, double tax, String discount, double price) {

        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();

        int itemCount = databaseAccess.getCartItemCount();

        databaseAccess.open();
        orderPrice = databaseAccess.getTotalPrice();


        if (itemCount > 0) {

            databaseAccess.open();
            //get data from local database
            final List<HashMap<String, String>> lines;
            lines = databaseAccess.getCartProduct();

            if (lines.isEmpty()) {
                Toasty.error(ProductCart.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            } else {

                //get current timestamp
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                //H denote 24 hours and h denote 12 hour hour format
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a
                dateTime = currentDate + " " + currentTime;

                //timestamp use for invoice id for unique
                Long tsLong = System.currentTimeMillis() / 1000;
                String timeStamp = tsLong.toString();
                Log.d("Time", timeStamp);
                //Invoice number=INV+StaffID+CurrentYear+timestamp
                invoiceNumber = "INV" + staffId + currentYear + timeStamp;

                final JSONObject obj = new JSONObject();
                try {


                    obj.put("invoice_id", invoiceNumber);
                    obj.put("order_date", currentDate);
                    obj.put("order_time", currentTime);
                    obj.put("order_type", type);
                    obj.put("order_payment_method", paymentMethod);
                    obj.put("customer_name", customerName);

                    obj.put("order_price", String.valueOf(orderPrice));
                    obj.put("tax", String.valueOf(tax));
                    obj.put("discount", discount);
                    obj.put("served_by", servedBy);
                    obj.put("shop_id", shopID);
                    obj.put("owner_id", ownerId);

                    JSONArray array = new JSONArray();


                    for (int i = 0; i < lines.size(); i++) {

                        databaseAccess.open();
                        String invoiceId = lines.get(i).get("invoice_id");
                        String productId = lines.get(i).get("product_id");
                        String productName = lines.get(i).get("product_name");
                        String productImage = lines.get(i).get("product_image");
                        String productDiscount = lines.get(i).get("product_discount");


                        String productWeightUnit = lines.get(i).get("product_weight_unit");


                        JSONObject objp = new JSONObject();
                        objp.put("invoice_id", invoiceId);
                        objp.put("product_id", productId);
                        objp.put("product_name", productName);
                        objp.put("product_image", productImage);
                        objp.put("product_weight", lines.get(i).get("product_weight") + " " + productWeightUnit);
                        objp.put("product_qty", lines.get(i).get("product_qty"));
                        objp.put("product_discount", productDiscount);
                        objp.put("product_price", lines.get(i).get("product_price"));
                        objp.put("product_order_date", currentDate);
                        objp.put("cgst", lines.get(i).get("cgst"));
                        objp.put("sgst", lines.get(i).get("sgst"));
                        objp.put("cess", lines.get(i).get("cess"));

                        array.put(objp);

                    }
                    obj.put("lines", array);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Utils utils = new Utils();

                if (utils.isNetworkAvailable(ProductCart.this)) {
                    orderSubmit(obj);
                } else {
                    Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                }


            }

        } else {
            Toasty.error(ProductCart.this, R.string.no_product_in_cart, Toast.LENGTH_SHORT).show();
        }
    }


    private void orderSubmit(final JSONObject obj) {

        Log.d("Json", obj.toString());


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        RequestBody body2 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(obj));


        Call<String> call = apiInterface.submitOrders(body2);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful()) {

                    progressDialog.dismiss();
                    Toasty.success(ProductCart.this, R.string.order_successfully_done, Toast.LENGTH_SHORT).show();

                    databaseAccess.open();
                    databaseAccess.emptyCart();
                    dialogSuccess();

                } else {

                    Toasty.error(ProductCart.this, R.string.error, Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                    Log.d("error", response.toString());

                }


            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                Log.d("onFailure", t.toString());

            }
        });


    }


    public void dialogSuccess() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close_dialog);
        Button dialogBtnPrint = dialogView.findViewById(R.id.btn_print1);

        TextView txtShopName = dialogView.findViewById(R.id.shop_name);
        TextView txtShopAddress = dialogView.findViewById(R.id.address);
        TextView txtShopEmail = dialogView.findViewById(R.id.email);
        TextView txtShopContact = dialogView.findViewById(R.id.contact);
        TextView txtInvoiceId = dialogView.findViewById(R.id.invoice_id);
        TextView txtOrderDate = dialogView.findViewById(R.id.order_date);
        TextView txtServedBy = dialogView.findViewById(R.id.served_by);
        TextView txtCustomerName = dialogView.findViewById(R.id.customer_name);
        TextView txtSubTotal = dialogView.findViewById(R.id.sub_total);
        TextView txtSTotalTax = dialogView.findViewById(R.id.total_tax);
        TextView txtSSgst = dialogView.findViewById(R.id.txt_sgst);
        TextView txtSCgst = dialogView.findViewById(R.id.txt_cgst);
        TextView txtSCess = dialogView.findViewById(R.id.txt_cess);

        TextView txtHintSgst = dialogView.findViewById(R.id.hint_sgst);
        TextView txtHintCgst = dialogView.findViewById(R.id.hint_cgst);
        TextView txtHintCess = dialogView.findViewById(R.id.hint_cess);

        LinearLayout layoutSgst = dialogView.findViewById(R.id.sgst_layout);
        LinearLayout layoutCgst = dialogView.findViewById(R.id.cgst_layout);
        LinearLayout layoutCess = dialogView.findViewById(R.id.cess_layout);
        LinearLayout layoutDisc = dialogView.findViewById(R.id.layout_discount);
        TextView discount = dialogView.findViewById(R.id.discount);
        TextView totalPrice = dialogView.findViewById(R.id.total_price);
        RecyclerView recyclerViewDialog = dialogView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewDialog.setLayoutManager(layoutManager);
        recyclerViewDialog.setHasFixedSize(true);

        databaseAccess.open();
        final List<HashMap<String, String>> lines;
        lines = databaseAccess.getCartProduct();


        userType = sp.getString(Constant.SP_USER_TYPE, "");
        shopName = sp.getString(Constant.SP_SHOP_NAME, "");
        address = sp.getString(Constant.SP_SHOP_ADDRESS, "");
        email = sp.getString(Constant.SP_EMAIL, "");
        contact = sp.getString(Constant.SP_SHOP_CONTACT, "");
        calculatedTotalCostPrint = (Double.valueOf(orderPrice)- Double.valueOf(discount1)) + Double.valueOf(getTax) ;

        txtShopName.setText(shopName);
        txtShopAddress.setText(address);
        txtShopEmail.setText("Email: " + email);
        txtShopContact.setText("Contact: " + contact);
        txtInvoiceId.setText("Invoice iD: " + invoiceNumber);
        txtOrderDate.setText("Order Date: " + dateTime);
        txtServedBy.setText("Served By: " + servedBy);
        txtCustomerName.setText("Customer Name: " + customerName);
        txtSubTotal.setText(String.valueOf(decimn.format(orderPrice)));

        if(country.equals("UAE")){
            layoutCess.setVisibility(View.GONE);
            layoutSgst.setVisibility(View.GONE);

            if (getCgst != 0) {
                txtHintCgst.setText("VAT");
                layoutCgst.setVisibility(View.VISIBLE);
                txtSCgst.setText(decimn.format(Double.valueOf(getCgst)));
            } else {
                layoutCgst.setVisibility(View.GONE); }

            /*if (getSgst != 0) {
                txtHintSgst.setText("VAT 2");
                layoutSgst.setVisibility(View.VISIBLE);
                txtSSgst.setText(decimn.format(Double.valueOf(getSgst)));
            } else {
                layoutSgst.setVisibility(View.GONE);
            }*/


        }else {

            if (getSgst != 0) {
                layoutCgst.setVisibility(View.VISIBLE);
                txtSSgst.setText(decimn.format(Double.valueOf(getSgst)));
            } else {
                layoutSgst.setVisibility(View.GONE);
            }
            if (getCgst != 0) {
                layoutCgst.setVisibility(View.VISIBLE);
                txtSCgst.setText(decimn.format(Double.valueOf(getCgst)));
            } else {
                layoutCgst.setVisibility(View.GONE);

            }
            if (getCess != 0) {
                layoutCess.setVisibility(View.VISIBLE);
                txtSCess.setText(decimn.format(Double.valueOf(getCess)));
            } else {
                layoutCess.setVisibility(View.GONE);

            }

        }

        if(Double.valueOf(discount1)!=0){
            layoutDisc.setVisibility(View.VISIBLE);
            discount.setText(decimn.format(Double.valueOf(discount1)));}else{
            layoutDisc.setVisibility(View.GONE);
        }

        txtSTotalTax.setText(String.valueOf(decimn.format(getTax)));
        totalPrice.setText(getResources().getString(R.string.rupee) + " " + String.valueOf(decimn.format(calculatedTotalCostPrint)));

        getProductsData(invoiceNumber, recyclerViewDialog);

        CartPrintItemListAdapter mAdapter = new CartPrintItemListAdapter(orderDetails);
        recyclerViewDialog.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        AlertDialog alertDialogSuccess = dialog.create();
        dialogBtnCloseDialog.setOnClickListener(v -> {

            alertDialogSuccess.dismiss();
            Intent intent = new Intent(ProductCart.this, PosActivity.class);
            startActivity(intent);
            finish();

        });


        dialogBtnPrint.setOnClickListener(v -> {

//            alertDialogSuccess.dismiss();
            printLayout(orderDetails);

        });

        alertDialogSuccess.show();


    }

    //print operation text setup
    public void printLayout(List<OrderDetails> orderDetails) {

        View dialogView = ((View) findViewById(R.id.print_me_layout));

        TextView txtShopName = dialogView.findViewById(R.id.shop_name);
        TextView txtShopAddress = dialogView.findViewById(R.id.address);
        TextView txtShopEmail = dialogView.findViewById(R.id.email);
        TextView txtShopContact = dialogView.findViewById(R.id.contact);
        TextView txtInvoiceId = dialogView.findViewById(R.id.invoice_id);
        TextView txtOrderDate = dialogView.findViewById(R.id.order_date);
        TextView txtServedBy = dialogView.findViewById(R.id.served_by);
        TextView txtCustomerName = dialogView.findViewById(R.id.customer_name);
        TextView txtSubTotal = dialogView.findViewById(R.id.sub_total);
        TextView txtSTotalTax = dialogView.findViewById(R.id.total_tax);
        TextView txtSSgst = dialogView.findViewById(R.id.txt_sgst);
        TextView txtSCgst = dialogView.findViewById(R.id.txt_cgst);
        TextView txtSCess = dialogView.findViewById(R.id.txt_cess);

        TextView txtHintSgst = dialogView.findViewById(R.id.hint_sgst);
        TextView txtHintCgst = dialogView.findViewById(R.id.hint_cgst);
        TextView txtHintCess = dialogView.findViewById(R.id.hint_cess);

        TextView discount = dialogView.findViewById(R.id.discount);
        TextView totalPrice = dialogView.findViewById(R.id.total_price);
        LinearLayout layoutSgst = dialogView.findViewById(R.id.sgst_layout);
        LinearLayout layoutCgst = dialogView.findViewById(R.id.cgst_layout);
        LinearLayout layoutCess = dialogView.findViewById(R.id.cess_layout);
        LinearLayout layoutDisc = dialogView.findViewById(R.id.layout_discount);
        RecyclerView recyclerViewDialog = dialogView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewDialog.setLayoutManager(layoutManager);
        recyclerViewDialog.setHasFixedSize(true);

        txtShopName.setText(shopName);
        txtShopAddress.setText(address);
        txtShopEmail.setText("Email: " + email);
        txtShopContact.setText("Contact: " + contact);
        txtInvoiceId.setText("Invoice iD: " + invoiceNumber);
        txtOrderDate.setText("Order Date: " + dateTime);
        txtServedBy.setText("Served By: " + servedBy);
        txtCustomerName.setText("Customer Name: " + customerName);

        txtSubTotal.setText(String.valueOf(decimn.format(orderPrice)));
//        txtSTotalTax.setText(String.valueOf(decimn.format(getTax)));

        if(country.equals("UAE")){
            layoutCess.setVisibility(View.GONE);
            layoutSgst.setVisibility(View.GONE);

            if (getCgst != 0) {
                txtHintCgst.setText("VAT");
                layoutCgst.setVisibility(View.VISIBLE);
                txtSCgst.setText(decimn.format(Double.valueOf(getCgst)));
            } else {
                layoutCgst.setVisibility(View.GONE);

            }

          /*  if (getSgst != 0) {
                txtHintSgst.setText("VAT 2");
                layoutSgst.setVisibility(View.VISIBLE);
                txtSSgst.setText(decimn.format(Double.valueOf(getSgst)));
            } else {
                layoutSgst.setVisibility(View.GONE);
            }*/

        }else {
            if (getSgst != 0) {
                layoutCgst.setVisibility(View.VISIBLE);
                txtSSgst.setText(decimn.format(Double.valueOf(getSgst)));
            } else {
                layoutSgst.setVisibility(View.GONE);
            }
            if (getCgst != 0) {
                layoutCgst.setVisibility(View.VISIBLE);
                txtSCgst.setText(decimn.format(Double.valueOf(getCgst)));
            } else {
                layoutCgst.setVisibility(View.GONE);

            }
            if (getCess != 0) {
                layoutCess.setVisibility(View.VISIBLE);
                txtSCess.setText(decimn.format(Double.valueOf(getCess)));
            } else {
                layoutCess.setVisibility(View.GONE);

            }

        }

        if(Double.valueOf(discount1)!=0){
            layoutDisc.setVisibility(View.VISIBLE);
        discount.setText(decimn.format(Double.valueOf(discount1)));}else{
            layoutDisc.setVisibility(View.GONE);
        }

        totalPrice.setText(getResources().getString(R.string.rupee) + " " + String.valueOf(decimn.format(calculatedTotalCostPrint)));


        CartPrintItemListAdapter mAdapter = new CartPrintItemListAdapter(orderDetails);
        recyclerViewDialog.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        printMe.sendViewToPrinter(dialogView);
    }


    class CartPrintItemListAdapter extends RecyclerView.Adapter<CartPrintItemListAdapter.MyViewHolder> {

        List<OrderDetails> reportViewArrayList;

        CartPrintItemListAdapter(List<OrderDetails> reportViewArrayList) {
            this.reportViewArrayList = reportViewArrayList;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cart_print_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {


            holder.txtItem.setText(reportViewArrayList.get(position).getProductName() + (" ") + reportViewArrayList.get(position).getProductWeight() + ("-") + "( " + reportViewArrayList.get(position).getProductQuantity() + " X " + reportViewArrayList.get(position).getProductPrice() + " )");

            holder.txtPrice.setText(String.valueOf(decimn.format(Integer.parseInt(reportViewArrayList.get(position).getProductQuantity()) * Double.parseDouble(reportViewArrayList.get(position).getProductPrice()))));

        }

        @Override
        public int getItemCount() {
            return reportViewArrayList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtItem, txtPrice;


            MyViewHolder(View view) {
                super(view);
                txtItem = itemView.findViewById(R.id.txt_order_item);
                txtPrice = itemView.findViewById(R.id.order_item_price);

            }
        }


    }

    //dialog for taking otp code
    public void dialog() {

        databaseAccess.open();
        double totalCgst = databaseAccess.getTotalCGST();

        databaseAccess.open();
        double totalSgst = databaseAccess.getTotalSGST();

        databaseAccess.open();
        double totalCESS = databaseAccess.getTotalCESS();

        databaseAccess.open();
        double totalDiscount = databaseAccess.getTotalProductDiscount();


        String shopCurrency = currency;
        // String tax = shopTax;

        getTax = totalCgst + totalSgst + totalCESS;

        getSgst = totalSgst;
        getCgst = totalCgst;
        getCess = totalCESS;

        //Toast.makeText(this, ""+getTax, Toast.LENGTH_SHORT).show();


        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final Button dialogBtnSubmit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialogBtnClose = dialogView.findViewById(R.id.btn_close);
        final TextView dialogOrderPaymentMethod = dialogView.findViewById(R.id.dialog_order_status);
        final TextView dialogOrderType = dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialogCustomer = dialogView.findViewById(R.id.dialog_customer);
        final TextView dialogTxtTotal = dialogView.findViewById(R.id.dialog_txt_total);
        final TextView dialogTxtTotalTax = dialogView.findViewById(R.id.dialog_txt_total_tax);
        final TextView dialogTxtLevelTax = dialogView.findViewById(R.id.dialog_level_tax);
        final TextView dialogTxtTotalCost = dialogView.findViewById(R.id.dialog_txt_total_cost);
        final TextView dialogtxtDiscount = dialogView.findViewById(R.id.dialog_txt_total_discount);


        final ImageButton dialogImgCustomer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialogImgOrderPaymentMethod = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialogImgOrderType = dialogView.findViewById(R.id.img_order_type);


        dialogTxtLevelTax.setText(getString(R.string.total_tax));
        double totalCost = CartAdapter.totalPrice;
        dialogTxtTotal.setText(shopCurrency + totalCost);
        dialogtxtDiscount.setText(shopCurrency + totalDiscount);


        dialogTxtTotalTax.setText(shopCurrency + f.format(getTax));

        double calculatedTotalCost = totalCost- totalDiscount + getTax ;
        dialogTxtTotalCost.setText(shopCurrency + calculatedTotalCost);


        orderTypeNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> orderType;
        orderType = databaseAccess.getOrderType();

        for (int i = 0; i < orderType.size(); i++) {

            // Get the ID of selected Country
            orderTypeNames.add(orderType.get(i).get("order_type_name"));

        }


        //payment methods
        paymentMethodNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> paymentMethod;
        paymentMethod = databaseAccess.getPaymentMethod();

        for (int i = 0; i < paymentMethod.size(); i++) {

            // Get the ID of selected Country
            paymentMethodNames.add(paymentMethod.get(i).get("payment_method_name"));

        }


        dialogImgOrderPaymentMethod.setOnClickListener(v -> {

            paymentMethodAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
            paymentMethodAdapter.addAll(paymentMethodNames);

            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ProductCart.this);
            View dialogView1 = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog1.setView(dialogView1);
            dialog1.setCancelable(false);

            Button dialogButton = (Button) dialogView1.findViewById(R.id.dialog_button);
            EditText dialogInput = (EditText) dialogView1.findViewById(R.id.dialog_input);
            TextView dialogTitle = (TextView) dialogView1.findViewById(R.id.dialog_title);
            ListView dialogList = (ListView) dialogView1.findViewById(R.id.dialog_list);


            dialogTitle.setText(R.string.select_payment_method);
            dialogList.setVerticalScrollBarEnabled(true);
            dialogList.setAdapter(paymentMethodAdapter);

            dialogInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("data", s.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    paymentMethodAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("data", s.toString());
                }
            });


            final AlertDialog alertDialog = dialog1.create();

            dialogButton.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();


            dialogList.setOnItemClickListener((parent, view, position, id) -> {

                alertDialog.dismiss();
                String selectedItem = paymentMethodAdapter.getItem(position);
                dialogOrderPaymentMethod.setText(selectedItem);


            });
        });


        dialogImgOrderType.setOnClickListener(v -> {


            orderTypeAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
            orderTypeAdapter.addAll(orderTypeNames);

            AlertDialog.Builder dialog12 = new AlertDialog.Builder(ProductCart.this);
            View dialogView12 = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog12.setView(dialogView12);
            dialog12.setCancelable(false);

            Button dialogButton = (Button) dialogView12.findViewById(R.id.dialog_button);
            EditText dialogInput = (EditText) dialogView12.findViewById(R.id.dialog_input);
            TextView dialogTitle = (TextView) dialogView12.findViewById(R.id.dialog_title);
            ListView dialogList = (ListView) dialogView12.findViewById(R.id.dialog_list);


            dialogTitle.setText(R.string.select_order_type);
            dialogList.setVerticalScrollBarEnabled(true);
            dialogList.setAdapter(orderTypeAdapter);

            dialogInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("data", s.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    orderTypeAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("data", s.toString());
                }
            });


            final AlertDialog alertDialog = dialog12.create();

            dialogButton.setOnClickListener(v13 -> alertDialog.dismiss());

            alertDialog.show();


            dialogList.setOnItemClickListener((parent, view, position, id) -> {

                alertDialog.dismiss();
                String selectedItem = orderTypeAdapter.getItem(position);


                dialogOrderType.setText(selectedItem);


            });
        });


        dialogImgCustomer.setOnClickListener(v -> {
            customerAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
            customerAdapter.addAll(customerNames);

            AlertDialog.Builder dialog13 = new AlertDialog.Builder(ProductCart.this);
            View dialogView13 = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog13.setView(dialogView13);
            dialog13.setCancelable(false);

            Button dialogButton = (Button) dialogView13.findViewById(R.id.dialog_button);
            EditText dialogInput = (EditText) dialogView13.findViewById(R.id.dialog_input);
            TextView dialogTitle = (TextView) dialogView13.findViewById(R.id.dialog_title);
            ListView dialogList = (ListView) dialogView13.findViewById(R.id.dialog_list);

            dialogTitle.setText(R.string.select_customer);
            dialogList.setVerticalScrollBarEnabled(true);
            dialogList.setAdapter(customerAdapter);

            dialogInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("data", s.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    customerAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("data", s.toString());
                }
            });


            final AlertDialog alertDialog = dialog13.create();

            dialogButton.setOnClickListener(v12 -> alertDialog.dismiss());

            alertDialog.show();


            dialogList.setOnItemClickListener((parent, view, position, id) -> {

                alertDialog.dismiss();
                String selectedItem = customerAdapter.getItem(position);


                dialogCustomer.setText(selectedItem);


            });
        });


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();


        dialogBtnSubmit.setOnClickListener(v -> {

            String orderType1 = dialogOrderType.getText().toString().trim();
            String orderPaymentMethod = dialogOrderPaymentMethod.getText().toString().trim();
            customerName = dialogCustomer.getText().toString().trim();
            discount1 =String.valueOf(totalDiscount);
            if (discount1.isEmpty()) {
                discount1 = "0.00";
            }

            proceedOrder(orderType1, orderPaymentMethod, customerName, getTax, discount1, calculatedTotalCost);


            alertDialog.dismiss();
        });


        dialogBtnClose.setOnClickListener(v -> alertDialog.dismiss());


    }

    public void getCustomers(String shopId, String ownerId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Customer>> call;


        call = apiInterface.getCustomers("", shopId, ownerId);

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    customerData = response.body();

                    customerNames = new ArrayList<>();

                    for (int i = 0; i < customerData.size(); i++) {

                        customerNames.add(customerData.get(i).getCustomerName());

                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }

    public void getProductsData(String invoiceId, RecyclerView recyclerViewDialog) {


        loading = new ProgressDialog(ProductCart.this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<OrderDetails>> call;
        call = apiInterface.OrderDetailsByInvoice(invoiceId);

        call.enqueue(new Callback<List<OrderDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDetails>> call, @NonNull Response<List<OrderDetails>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    orderDetails = response.body();
                    loading.dismiss();


                    if (orderDetails.isEmpty()) {


                        Toasty.warning(ProductCart.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();


                    } else {

                        CartPrintItemListAdapter mAdapter = new CartPrintItemListAdapter(orderDetails);
                        recyclerViewDialog.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();


                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDetails>> call, @NonNull Throwable t) {

                loading.dismiss();
                Toast.makeText(ProductCart.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

