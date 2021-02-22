package com.app.shopncart.pos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmedelsayed.sunmiprinterutill.PrintMe;
import com.app.shopncart.Constant;
import com.app.shopncart.R;
import com.app.shopncart.database.DatabaseAccess;
import com.app.shopncart.model.Customer;
import com.app.shopncart.model.OrderDetails;
import com.app.shopncart.model.Product;
import com.app.shopncart.networking.ApiClient;
import com.app.shopncart.networking.ApiInterface;
import com.app.shopncart.utils.BaseActivity;
import com.app.shopncart.utils.InputFilterMinMax;
import com.app.shopncart.utils.Utils;
import com.bumptech.glide.Glide;

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

import static com.app.shopncart.utils.Utils.roundOff2Decimal;
import static java.lang.Math.round;

public class ProductCart extends BaseActivity {

    CartAdapter productCartAdapter;
    ImageView imgNoProduct, imgScanner;
    CardView cardView;
    Button btnSubmitOrder;
    TextView txtNoProduct, txtTotalPrice, txtTotalCgst, txtTotalSgst, txtTotalCess, txtFinalTotal, txtTotalDiscount;
    LinearLayout linearLayout;
    DatabaseAccess databaseAccess;
    ProgressDialog loading;
    RecyclerView recyclerView;
    MenuItem holdItem;
    MediaPlayer player;
    List<HashMap<String, String>> lines;
    public static Resources mResources;
    double getTax = 0;
    public static final int RESULT_CART = -2;
    List<HashMap<String, String>> cartProductList;
    List<HashMap<String, String>> cartProductListHold;

    public Double totalPrice, allTax, allCgst, allSgst, allCess, allDiscount = 0.0, finalTotal = 0.0;
    public Double allDiscountedCgst = 0.0, allDiscountedSgst = 0.0, allDiscountedCess = 0.0, allDiscountedTax = 0.0;

    double getSgst = 0;
    double getCgst = 0;
    double getCess = 0;
    Boolean isZero = false;

    String discount1 = "";
    String dateTime = "";
    double orderPrice = 0;
    String customerName = "";
    String custId = "";
    String custTaxId = "";
    double calculatedTotalCostPrint = 0;
    List<OrderDetails> orderDetails = new ArrayList<>();
    DecimalFormat decimn = new DecimalFormat("#,###,##0.00");
    private PrintMe printMe;
    String cash = "";
    String credit = "";
    String paypal = "";

    List<String> customerNames, customerIds, customerTaxtId, orderTypeNames, paymentMethodNames;
    List<String> paymentMethodNamesMulti, paymentMethodNamesMultiValues;
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
    String taxId = "";
    DecimalFormat f;
    String invoiceNumber = "";
    ArrayList<HashMap<String, String>> productsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);
        printMe = new PrintMe(this);
        mResources = getResources();
        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();
        productsList = databaseAccess.getProducts();
        player = MediaPlayer.create(this, R.raw.delete_sound);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_cart);
        f = new DecimalFormat("#,###,##0.00");
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
        taxId = sp.getString(Constant.SP_SHOP_TAX_ID, "");

        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");


        getCustomers(shopID, ownerId);

        recyclerView = findViewById(R.id.cart_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);
        txtNoProduct = findViewById(R.id.txt_no_product);
        linearLayout = findViewById(R.id.linear_layout);
        imgScanner = findViewById(R.id.img_scanner);
        cardView = findViewById(R.id.cardview);


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


        //get data from local database
        databaseAccess.open();
        cartProductList = databaseAccess.getCartProduct();

        populateCartList(cartProductList);

        imgScanner.setOnClickListener(v -> {
            Intent intent = new Intent(ProductCart.this, ScannerActivity.class);
            startActivityForResult(intent, 2);
        });

//        btnSubmitOrder.setOnClickListener(v -> dialog());
        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isZero) {
                    Toasty.error(ProductCart.this, R.string.quantity_empty, Toast.LENGTH_SHORT).show();
                } else {
                    dialog();
                }
            }
        });


    }

    public void populateCartList(List<HashMap<String, String>> cartProductList) {
        Log.d("CartSize", "" + cartProductList.size());

        if (cartProductList.isEmpty()) {

            imgNoProduct.setImageResource(R.drawable.empty_cart);
            imgNoProduct.setVisibility(View.VISIBLE);
            txtNoProduct.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);

            txtTotalPrice.setVisibility(View.GONE);
            txtTotalDiscount.setVisibility(View.GONE);
            txtTotalCgst.setVisibility(View.GONE);
            txtTotalSgst.setVisibility(View.GONE);
            txtTotalCess.setVisibility(View.GONE);
            txtFinalTotal.setVisibility(View.GONE);

        } else {

            txtNoProduct.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);

            txtTotalPrice.setVisibility(View.VISIBLE);
            txtTotalDiscount.setVisibility(View.VISIBLE);
            txtTotalCgst.setVisibility(View.VISIBLE);
            txtTotalSgst.setVisibility(View.VISIBLE);
            txtTotalCess.setVisibility(View.VISIBLE);
            txtFinalTotal.setVisibility(View.VISIBLE);


            productCartAdapter = new CartAdapter(ProductCart.this, cartProductList, txtTotalPrice, btnSubmitOrder, imgNoProduct, txtNoProduct, txtTotalCgst, txtTotalSgst, txtTotalCess, txtFinalTotal, txtTotalDiscount);
            recyclerView.setAdapter(productCartAdapter);


        }
    }


    public void proceedOrder(String type, String paymentMethod, String customerName, String custId, double tax, String discount, double price, String cash, String credit, String paypal) {

        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();

        int itemCount = databaseAccess.getCartItemCount();

        databaseAccess.open();
        orderPrice = databaseAccess.getTotalPrice();


        if (itemCount > 0) {

            databaseAccess.open();
            //get data from local database
            lines = databaseAccess.getCartProduct();
            Double total = 0.0;

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
                JSONArray payMethods = new JSONArray();
                final JSONObject obj = new JSONObject();
                try {
                    obj.put("invoice_id", invoiceNumber);
                    obj.put("order_date", currentDate);
                    obj.put("order_time", currentTime);
                    obj.put("order_type", type);
                    obj.put("order_payment_method", paymentMethod);
                    if (paymentMethod.toLowerCase().equals("multi")) {
                        if (paymentMethodNamesMulti.size() == paymentMethodNamesMultiValues.size()) {
                            for (int i = 0; i < paymentMethodNamesMulti.size(); i++) {
                                JSONObject payMethodObject = new JSONObject();
                                payMethodObject.put("name", paymentMethodNamesMulti.get(i));
                                payMethodObject.put("value", paymentMethodNamesMultiValues.get(i));
                                payMethods.put(payMethodObject);
                            }
                            obj.put("pay_methods", payMethods);
                        }
                    }

                    obj.put("cash", cash);
                    obj.put("credit", credit);
                    obj.put("paypal", paypal);
                    obj.put("customer_name", customerName);
                    obj.put("customer_id", custId);
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

                calculatedTotalCostPrint = (Double.valueOf(orderPrice) - Double.valueOf(discount1)) + Double.valueOf(getTax);

                for (int i = 0; i < paymentMethodNamesMultiValues.size(); i++) {
                    if (!paymentMethodNamesMultiValues.get(i).equals("")) {
                        Double value = Double.parseDouble(paymentMethodNamesMultiValues.get(i));
                        total = total + value;
                    }
                }

                Utils utils = new Utils();

                if (paymentMethod.toLowerCase().equals("multi")) {
                    if (Utils.round(total, 1) == Utils.round(calculatedTotalCostPrint, 1)) {

                        if (utils.isNetworkAvailable(ProductCart.this)) {
                            orderSubmit(obj);
                        } else {
                            Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(ProductCart.this, R.string.amount_mismatch, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    if (utils.isNetworkAvailable(ProductCart.this)) {
                        orderSubmit(obj);
                    } else {
                        Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    }
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
                    for (int i = 0; i < lines.size(); i++) {
                        String productId = lines.get(i).get("product_id");
                        String productStock = lines.get(i).get("product_stock");
                        String qty = lines.get(i).get("product_qty");
                        Double finalStock = Double.valueOf(productStock) - Double.valueOf(qty);
                        databaseAccess.open();
                        databaseAccess.updateStock(productId, String.valueOf(finalStock));


                    }

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
        TextView txtTaxId = dialogView.findViewById(R.id.tax_id);
        TextView txtOrderDate = dialogView.findViewById(R.id.order_date);
        TextView txtServedBy = dialogView.findViewById(R.id.served_by);
        TextView txtCustomerName = dialogView.findViewById(R.id.customer_name);
        TextView txtCustomerTaxId = dialogView.findViewById(R.id.customer_tax_id);

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
        calculatedTotalCostPrint = (Double.valueOf(orderPrice) - Double.valueOf(discount1)) + Double.valueOf(getTax);

        txtShopName.setText(shopName);
        txtShopAddress.setText(address);
        txtShopEmail.setText("Email: " + email);
        txtShopContact.setText("Contact: " + contact);
        txtInvoiceId.setText("Invoice iD: " + invoiceNumber);
        if (taxId != null && !taxId.equals("")) {
            txtTaxId.setVisibility(View.VISIBLE);
            txtTaxId.setText("Tax iD: " + taxId);
        } else {
            txtTaxId.setVisibility(View.GONE);
        }
        txtOrderDate.setText("Order Date: " + dateTime);
        txtServedBy.setText("Served By: " + servedBy);
        txtCustomerName.setText("Customer Name: " + customerName);
        if (custTaxId != null && !taxId.equals("")) {
            txtCustomerTaxId.setVisibility(View.VISIBLE);
            txtCustomerTaxId.setText("Customer Tax iD: " + custTaxId);
        } else {
            txtCustomerTaxId.setVisibility(View.GONE);
        }

        txtSubTotal.setText(String.valueOf(decimn.format(orderPrice)));

        if (country.equals("UAE")) {
            layoutCess.setVisibility(View.GONE);
            layoutSgst.setVisibility(View.GONE);

            if (getCgst != 0) {
                txtHintCgst.setText("VAT");
                layoutCgst.setVisibility(View.VISIBLE);
                txtSCgst.setText(decimn.format(Double.valueOf(getCgst)));
            } else {
                layoutCgst.setVisibility(View.GONE);
            }

            /*if (getSgst != 0) {
                txtHintSgst.setText("VAT 2");
                layoutSgst.setVisibility(View.VISIBLE);
                txtSSgst.setText(decimn.format(Double.valueOf(getSgst)));
            } else {
                layoutSgst.setVisibility(View.GONE);
            }*/


        } else {

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

        if (Double.valueOf(discount1) != 0) {
            layoutDisc.setVisibility(View.VISIBLE);
            discount.setText(decimn.format(Double.valueOf(discount1)));
        } else {
            layoutDisc.setVisibility(View.GONE);
        }

        txtSTotalTax.setText(String.valueOf(decimn.format(getTax)));
        totalPrice.setText(currency + " " + String.valueOf(decimn.format(Utils.round(calculatedTotalCostPrint, 1))));

        getProductsData(invoiceNumber, recyclerViewDialog);

        CartPrintItemListAdapter mAdapter = new CartPrintItemListAdapter(orderDetails);
        recyclerViewDialog.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        AlertDialog alertDialogSuccess = dialog.create();
        dialogBtnCloseDialog.setOnClickListener(v -> {

            alertDialogSuccess.dismiss();
            setResult(RESULT_OK);
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

        View dialogView = LayoutInflater.from(this).inflate(R.layout.print_reciept, null);

        TextView txtShopName = dialogView.findViewById(R.id.shop_name);
        TextView txtShopAddress = dialogView.findViewById(R.id.address);
        TextView txtShopEmail = dialogView.findViewById(R.id.email);
        TextView txtShopContact = dialogView.findViewById(R.id.contact);
        TextView txtInvoiceId = dialogView.findViewById(R.id.invoice_id);
        TextView txtOrderDate = dialogView.findViewById(R.id.order_date);
        TextView txtTaxId = dialogView.findViewById(R.id.tax_id);
        TextView txtServedBy = dialogView.findViewById(R.id.served_by);
        TextView txtCustomerName = dialogView.findViewById(R.id.customer_name);
        TextView txtCustomerTaxId = dialogView.findViewById(R.id.customer_tax_id);
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
        if (taxId != null && !taxId.equals("")) {
            txtTaxId.setVisibility(View.VISIBLE);
            txtTaxId.setText("Tax iD: " + taxId);
        } else {
            txtTaxId.setVisibility(View.GONE);
        }
        txtOrderDate.setText("Order Date: " + dateTime);
        txtServedBy.setText("Served By: " + servedBy);
        txtCustomerName.setText("Customer Name: " + customerName);
        if (custTaxId != null && !taxId.equals("")) {
            txtCustomerTaxId.setVisibility(View.VISIBLE);
            txtCustomerTaxId.setText("Customer Tax iD: " + custTaxId);
        } else {
            txtCustomerTaxId.setVisibility(View.GONE);
        }

        txtSubTotal.setText(String.valueOf(decimn.format(orderPrice)));
//        txtSTotalTax.setText(String.valueOf(decimn.format(getTax)));

        if (country.equals("UAE")) {
            layoutCess.setVisibility(View.GONE);
            layoutSgst.setVisibility(View.GONE);

            if (getCgst != 0) {
                txtHintCgst.setText("VAT (+)");
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

        } else {
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

        if (Double.valueOf(discount1) != 0) {
            layoutDisc.setVisibility(View.VISIBLE);
            discount.setText(decimn.format(Double.valueOf(discount1)));
        } else {
            layoutDisc.setVisibility(View.GONE);
        }

        totalPrice.setText(currency + " " + String.valueOf(decimn.format(Utils.round(calculatedTotalCostPrint, 1))));


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

            holder.txtPrice.setText(String.valueOf(decimn.format(Double.parseDouble(reportViewArrayList.get(position).getProductQuantity()) * Double.parseDouble(reportViewArrayList.get(position).getProductPrice()))));

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

        final LinearLayout dialogLayoutPay = dialogView.findViewById(R.id.layout_pay);
        final RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);

        final EditText dialogtxtCash = dialogView.findViewById(R.id.dialog_txt_total_cash);
        final EditText dialogtxtCredit = dialogView.findViewById(R.id.dialog_txt_total_credit);
        final EditText dialogtxtPaypal = dialogView.findViewById(R.id.dialog_txt_total_paypal);


        final ImageButton dialogImgCustomer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialogImgOrderPaymentMethod = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialogImgOrderType = dialogView.findViewById(R.id.img_order_type);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        dialogTxtLevelTax.setText(getString(R.string.total_tax));
        double totalCost = totalPrice;
        dialogTxtTotal.setText(shopCurrency + f.format(totalCost));
        dialogtxtDiscount.setText(shopCurrency + f.format(Double.valueOf(totalDiscount)));


        dialogTxtTotalTax.setText(shopCurrency + f.format(getTax));

        double calculatedTotalCost = totalCost - totalDiscount + getTax;
        dialogTxtTotalCost.setText(shopCurrency + f.format(Utils.round(calculatedTotalCost, 1)));


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
        paymentMethodNamesMulti = new ArrayList<>();
        paymentMethodNamesMultiValues = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> paymentMethod;
        paymentMethod = databaseAccess.getPaymentMethod();

        for (int i = 0; i < paymentMethod.size(); i++) {

            // Get the ID of selected Country
            paymentMethodNames.add(paymentMethod.get(i).get("payment_method_name"));

            if (!(paymentMethod.get(i).get("payment_method_name")).toLowerCase().equals("multi")) {
                paymentMethodNamesMulti.add(paymentMethod.get(i).get("payment_method_name"));
                paymentMethodNamesMultiValues.add("0");

            }


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
                if (selectedItem.toLowerCase().equals("multi")) {
                    recyclerView.setVisibility(View.VISIBLE);
                    dialogLayoutPay.setVisibility(View.GONE);


                    PaymentMethodListAdapter mAdapter = new PaymentMethodListAdapter(paymentMethodNamesMulti);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();


                } else {
                    recyclerView.setAdapter(null);
                    recyclerView.setVisibility(View.GONE);
                    dialogLayoutPay.setVisibility(View.GONE);
                    dialogtxtCash.getText().clear();
                    dialogtxtCredit.getText().clear();
                    dialogtxtPaypal.getText().clear();
                    cash = "";
                    credit = "";
                    paypal = "";
                }


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
                custId = customerIds.get(position);
                custTaxId = customerTaxtId.get(position);
                Log.e("cust_id", "------" + custId);


                dialogCustomer.setText(selectedItem);


            });
        });


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();


        dialogBtnSubmit.setOnClickListener(v -> {

            String orderType1 = dialogOrderType.getText().toString().trim();
            String orderPaymentMethod = dialogOrderPaymentMethod.getText().toString().trim();
            customerName = dialogCustomer.getText().toString().trim();
            cash = dialogtxtCash.getText().toString().trim();
            credit = dialogtxtCredit.getText().toString().trim();
            paypal = dialogtxtPaypal.getText().toString().trim();
            discount1 = String.valueOf(totalDiscount);
            if (discount1.isEmpty()) {
                discount1 = "0.00";
            }

            proceedOrder(orderType1, orderPaymentMethod, customerName, custId, getTax, discount1, calculatedTotalCost, cash, credit, paypal);


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
                    customerIds = new ArrayList<>();
                    customerTaxtId = new ArrayList<>();

                    for (int i = 0; i < customerData.size(); i++) {

                        customerNames.add(customerData.get(i).getCustomerName());
                        customerIds.add(customerData.get(i).getCustomerId());
                        customerTaxtId.add(customerData.get(i).getTaxid());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    String returnString = data.getStringExtra("product_code");
                    filterList1(returnString);

                }
            }
        } catch (Exception ex) {
            Toast.makeText(ProductCart.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hold_menu, menu);
        holdItem = menu.findItem(R.id.id_hold);
        databaseAccess.open();
        cartProductListHold = databaseAccess.getCartProductTemp();
        if (cartProductListHold.size() > 0) {
            holdItem.setVisible(false);
        }
//        ImageView searchView = (ImageView) holdItem.getActionView();
        holdItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                databaseAccess.open();
                cartProductList = databaseAccess.getCartProduct();

                for (int i = 0; i < cartProductList.size(); i++) {
                    databaseAccess.open();
                    databaseAccess.addToHold(cartProductList.get(i).get("product_id"), cartProductList.get(i).get("product_name"), cartProductList.get(i).get("product_weight"), cartProductList.get(i).get("product_weight_unit"), cartProductList.get(i).get("product_price"), Double.valueOf(cartProductList.get(i).get("product_qty")), cartProductList.get(i).get("product_image"), cartProductList.get(i).get("product_stock"), Double.valueOf(cartProductList.get(i).get("cgst")), Double.valueOf(cartProductList.get(i).get("sgst")), Double.valueOf(cartProductList.get(i).get("cess")), Double.valueOf(cartProductList.get(i).get("product_discount")), cartProductList.get(i).get("product_cegst_percent"), cartProductList.get(i).get("product_sgst_percent"), cartProductList.get(i).get("product_cess_percent"), Double.valueOf(cartProductList.get(i).get("product_discounted_total")), Double.valueOf(cartProductList.get(i).get("product_line_total")), cartProductList.get(i).get("editable"));
                }
                databaseAccess.open();
                databaseAccess.emptyCart();
                imgNoProduct.setImageResource(R.drawable.empty_cart);
                imgNoProduct.setVisibility(View.VISIBLE);
                txtNoProduct.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                txtTotalPrice.setVisibility(View.GONE);
                txtTotalSgst.setVisibility(View.GONE);
                txtTotalCgst.setVisibility(View.GONE);
                txtTotalCess.setVisibility(View.GONE);
                txtFinalTotal.setVisibility(View.GONE);
                btnSubmitOrder.setVisibility(View.GONE);
                txtTotalDiscount.setVisibility(View.GONE);
                holdItem.setVisible(false);
                Toasty.info(ProductCart.this, "Items holded", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CART);
                finish();
                this.finish();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CART);
        super.onBackPressed();
    }

    private void filterList1(String query) {

        query = query.toLowerCase();
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();


        if (query.length() == 0) {
            arrayList = productsList;
        } else {
            for (int i = 0; i < productsList.size(); i++) {
                boolean filter = false;
                if (productsList.get(i).get("product_code") != null)
                    if (productsList.get(i).get("product_code").toLowerCase().contains(query)) {
                        filter = true;
                    }

                if (filter) {
//                    arrayList.add(obj);
                    String productId = productsList.get(i).get("product_id");
                    String productName = productsList.get(i).get("product_name");
                    String productWeight = productsList.get(i).get("product_weight");
                    String productPrice = productsList.get(i).get("product_sell_price");
                    String weightUnit = productsList.get(i).get("product_weight_unit_name");
                    String productImage = productsList.get(i).get("product_image");
                    String productStock = productsList.get(i).get("product_stock");
                    String cgst = productsList.get(i).get("cgst");
                    String sgst = productsList.get(i).get("sgst");
                    String cess = productsList.get(i).get("cess");
                    String editable = productsList.get(i).get("editable");
                    String imageUrl = Constant.PRODUCT_IMAGE_URL + productImage;

                    double itemPrice = Double.parseDouble(productPrice);

                    double getCgst = 0;
                    double getSgst = 0;
                    double getCess = 0;

                    if (cgst != null && !cgst.equals("")) {
                        getCgst = Double.parseDouble(cgst);
                    }
                    if (sgst != null && !sgst.equals("")) {
                        getSgst = Double.parseDouble(sgst);
                    }
                    if (cess != null && !cess.equals("")) {
                        getCess = Double.parseDouble(cess);
                    }


                    double cgstAmount = (itemPrice * getCgst) / 100;
                    double sgstAmount = (itemPrice * getSgst) / 100;
                    double cessAmount = (itemPrice * getCess) / 100;

                    //Low stock marked as RED color
                    int getStock = Integer.parseInt(productStock);

                    if (getStock <= 0) {

                        Toasty.warning(ProductCart.this, R.string.stock_not_available_please_update_stock, Toast.LENGTH_SHORT).show();
                    } else {


                        databaseAccess.open();
                        int check = databaseAccess.addToCart(productId, productName, productWeight, weightUnit, productPrice, 1.0, productImage, productStock, cgstAmount, sgstAmount, cessAmount, 0, cgst, sgst, cess, 0, 0, editable);


                        if (check == 1) {
                            Toasty.success(ProductCart.this, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                            player.start();
                        } else if (check == 2) {

                            Toasty.info(ProductCart.this, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();

                        } else {

                            Toasty.error(ProductCart.this, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }

        }

        databaseAccess.open();
        cartProductList = databaseAccess.getCartProduct();
        populateCartList(cartProductList);
//        setUpRecyclerView(arrayList);
    }


    class PaymentMethodListAdapter extends RecyclerView.Adapter<PaymentMethodListAdapter.MyViewHolder> {

        List<String> paymentViewArrayList;
        String amount = "0";

        PaymentMethodListAdapter(List<String> paymentViewArrayList) {
            this.paymentViewArrayList = paymentViewArrayList;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_paymethod_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.txtItem.setText(paymentViewArrayList.get(position));


            holder.txtPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    amount = holder.txtPrice.getText().toString().trim();
                    paymentMethodNamesMultiValues.set(position, amount);

                }


                @Override
                public void afterTextChanged(Editable editable) {


                }
            });


        }

        @Override
        public int getItemCount() {
            return paymentViewArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtItem;
            EditText txtPrice;


            MyViewHolder(View view) {
                super(view);
                txtItem = itemView.findViewById(R.id.method_label);
                txtPrice = itemView.findViewById(R.id.dialog_txt_total_amount);

            }
        }


    }

    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {


        private List<HashMap<String, String>> cartProduct;
        private Context context;
        MediaPlayer player;
        TextView txtNoProduct;
        double discount = 0;
        double priceWithDisc = 0;

        TextView txtTotalPrice, txtTotalCgst, txtTotalSgst, txtTotalCess, txtFinalTotal, txtTotalDiscount;


        Button btnSubmitOrder;
        ImageView imgNoProduct;

        SharedPreferences sp;
        String currency;
        String country = "";
        DecimalFormat f;

        public CartAdapter(Context context, List<HashMap<String, String>> cartProduct, TextView txtTotalPrice, Button btnSubmitOrder, ImageView imgNoProduct, TextView txtNoProduct, TextView txtTotalCgst, TextView txtTotalSgst, TextView txtTotalCess, TextView txtFinalTotal, TextView txtTotalDiscount) {
            this.context = context;
            this.cartProduct = cartProduct;
            player = MediaPlayer.create(context, R.raw.delete_sound);
            this.txtTotalPrice = txtTotalPrice;
            this.txtTotalCgst = txtTotalCgst;
            this.txtTotalSgst = txtTotalSgst;
            this.txtTotalCess = txtTotalCess;
            this.txtFinalTotal = txtFinalTotal;
            this.btnSubmitOrder = btnSubmitOrder;
            this.imgNoProduct = imgNoProduct;
            this.txtNoProduct = txtNoProduct;
            this.txtTotalDiscount = txtTotalDiscount;

            sp = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");
            country = sp.getString(Constant.SP_SHOP_COUNTRY, "");

            f = new DecimalFormat("#,###,##0.00");
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product_items, parent, false);
            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

            databaseAccess.open();
            final String cart_id = cartProduct.get(position).get("cart_id");

            String productName = cartProduct.get(position).get("product_name");
            final String price = cartProduct.get(position).get("product_price");
            final String productWeightUnit = cartProduct.get(position).get("product_weight_unit");
            final String weight = cartProduct.get(position).get("product_weight");
            final String qty = cartProduct.get(position).get("product_qty");
            final String productImage = cartProduct.get(position).get("product_image");
            final String productStock = cartProduct.get(position).get("product_stock");
            final String productDiscount = cartProduct.get(position).get("product_discount");

            final String cgstPercent = cartProduct.get(position).get("product_cegst_percent");
            final String sgstPercent = cartProduct.get(position).get("product_sgst_percent");
            final String cessPercent = cartProduct.get(position).get("product_cess_percent");

            final String cgst = cartProduct.get(position).get("cgst");
            final String sgst = cartProduct.get(position).get("sgst");
            final String cess = cartProduct.get(position).get("cess");
            final String editable = cartProduct.get(position).get("editable");
            Log.e("editable", "-----" + editable);

            String imageUrl = Constant.PRODUCT_IMAGE_URL + productImage;

            Double getStock = Double.parseDouble(productStock);
            if (productImage != null) {
                if (productImage.isEmpty()) {
                    holder.imgProduct.setImageResource(R.drawable.image_placeholder);
                    holder.imgProduct.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {


                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.image_placeholder)
                            .into(holder.imgProduct);

                }
            }

            final double getPrice = Double.parseDouble(price) * Double.parseDouble(qty);


            holder.txtItemName.setText(productName);
            holder.txtPrice.setText(currency + f.format(getPrice));
            holder.txtWeight.setText(weight + " " + productWeightUnit);
            holder.txtQtyNumber.setText(qty);
            holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("0", String.valueOf(getPrice)), new InputFilter.LengthFilter(15)});

            if (!productDiscount.equals("0")) {
                holder.edtDisc.setText(productDiscount);
            }


            if (country.equals("UAE")) {
                holder.txtCgst.setText("VAT" + " " + currency + " " + f.format(Double.parseDouble(cgst)));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                holder.txtSgst.setVisibility(View.GONE);
                holder.txtCess.setVisibility(View.GONE);
            } else {

                if (Double.valueOf(cgst) != 0) {
                    holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + " " + f.format(Double.parseDouble(cgst)));
                } else {
                    holder.txtCgst.setVisibility(View.GONE);
                }

                if (Double.valueOf(sgst) != 0) {
                    holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + " " + f.format(Double.parseDouble(sgst)));
                } else {
                    holder.txtSgst.setVisibility(View.GONE);
                }

                if (Double.valueOf(cess) != 0) {
                    holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + " " + f.format(Double.parseDouble(cess)));
                } else {
                    holder.txtCess.setVisibility(View.GONE);
                }

            }

            databaseAccess.open();
            totalPrice = databaseAccess.getTotalPrice();

            databaseAccess.open();
            allTax = databaseAccess.getTotalTax();
            databaseAccess.open();
            allCgst = databaseAccess.getTotalCGST();
            databaseAccess.open();
            allSgst = databaseAccess.getTotalSGST();
            databaseAccess.open();
            allCess = databaseAccess.getTotalCESS();

            allDiscount = 0.0;
            //getting total discount from db
            databaseAccess.open();
            allDiscount = databaseAccess.getTotalProductDiscount();

       /* databaseAccess.open();
        finalTotal= databaseAccess.getFinalTotalPrice();*/

            finalTotal = (totalPrice - allDiscount) + allTax;
            Log.e("Price-dscnt+tax=ft", "---" + totalPrice + "-" + allDiscount + "+" + allTax + "=" + finalTotal);


            txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

            //setting total discount
            txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + "(-)" + ": " + currency + " " + f.format(allDiscount));

            txtFinalTotal.setText(context.getString(R.string.grand_total) + currency + " " + f.format(Utils.round(finalTotal, 1)));

            if (country.equals("UAE")) {
                txtTotalCgst.setText("Total Vat " + "(+)" + ": " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                txtTotalSgst.setVisibility(View.GONE);
                txtTotalCess.setVisibility(View.GONE);
            } else {
                if (Double.valueOf(allCgst) != 0) {
                    txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + "(+)" + ": " + currency + " " + f.format(allCgst));
                } else {
                    txtTotalCgst.setVisibility(View.GONE);
                }

                if (Double.valueOf(allSgst) != 0) {
                    txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + "(+)" + ": " + currency + " " + f.format(allSgst));
                } else {
                    txtTotalSgst.setVisibility(View.GONE);
                }

                if (Double.valueOf(cess) != 0) {
                    txtTotalCess.setText(context.getString(R.string.total_cess_cart) + "(+)" + ": " + currency + " " + f.format(allCess));
                } else {
                    txtTotalCess.setVisibility(View.GONE);
                }

            }

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    boolean deleteProduct = databaseAccess.deleteProductFromCart(cart_id);
                    if (deleteProduct) {
                        Toasty.success(context, context.getString(R.string.product_removed_from_cart), Toast.LENGTH_SHORT).show();

                        // Calculate Cart's Total Price Again
                        player.start();

                        removeAt(cartProduct, holder.getAdapterPosition());
                        cartProduct.clear();
                        databaseAccess.open();
                        cartProduct = databaseAccess.getCartProduct();
                        populateCartList(cartProduct);

                        databaseAccess.open();
                        totalPrice = databaseAccess.getTotalPrice();

                        databaseAccess.open();
                        allTax = databaseAccess.getTotalTax();
                        databaseAccess.open();
                        allCgst = databaseAccess.getTotalCGST();
                        databaseAccess.open();
                        allSgst = databaseAccess.getTotalSGST();
                        databaseAccess.open();
                        allCess = databaseAccess.getTotalCESS();

                        allDiscount = 0.0;

                        //getting total discount from db
                        databaseAccess.open();
                        allDiscount = databaseAccess.getTotalProductDiscount();

                        double priceWithTax = (totalPrice - allDiscount) + allTax;
                        txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

                        //setting total discount
                        txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + "(-)" + " " + currency + " " + f.format(allDiscount));

                        if (country.equals("UAE")) {
                            txtTotalCgst.setText("Total Vat " + "(+)" + ": " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                            txtTotalSgst.setVisibility(View.GONE);
                            txtTotalCess.setVisibility(View.GONE);
                        } else {
                            if (Double.valueOf(allCgst) != 0) {
                                txtTotalCgst.setVisibility(View.VISIBLE);
                                txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + "(+)" + ": " + currency + " " + f.format(allCgst));
                            } else {
                                txtTotalCgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(allSgst) != 0) {
                                txtTotalSgst.setVisibility(View.VISIBLE);
                                txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + "(+)" + ": " + currency + " " + f.format(allSgst));
                            } else {
                                txtTotalSgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(cess) != 0) {
                                txtTotalCess.setVisibility(View.VISIBLE);
                                txtTotalCess.setText(context.getString(R.string.total_cess_cart) + "(+)" + ": " + currency + " " + f.format(allCess));
                            } else {
                                txtTotalCess.setVisibility(View.GONE);
                            }

                        }

                        if (Double.valueOf(priceWithTax) != 0) {
                            txtFinalTotal.setVisibility(View.VISIBLE);
                            txtFinalTotal.setText(context.getString(R.string.grand_total) + currency + " " + f.format(Utils.round(priceWithTax, 1)));
                        } else {
                            txtFinalTotal.setVisibility(View.GONE);
                        }

                    } else {
                        Toasty.error(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }


                    databaseAccess.open();
                    int itemCount = databaseAccess.getCartItemCount();
                    Log.d("itemCount", "" + itemCount);
                    if (itemCount <= 0) {
                        txtTotalPrice.setVisibility(View.GONE);
                        txtTotalDiscount.setVisibility(View.GONE);
                        txtTotalCgst.setVisibility(View.GONE);
                        txtTotalSgst.setVisibility(View.GONE);
                        txtTotalCess.setVisibility(View.GONE);
                        txtFinalTotal.setVisibility(View.GONE);

                        btnSubmitOrder.setVisibility(View.GONE);

                        imgNoProduct.setVisibility(View.VISIBLE);
                        txtNoProduct.setVisibility(View.VISIBLE);
                    }

                }
            });

            holder.txtPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String qty1 = holder.txtQtyNumber.getText().toString();
                    double getQty = Double.parseDouble("0" + qty1);

                    if (getQty >= getStock) {
                        Toasty.warning(context, R.string.stock_not_available_please_update_stock, Toast.LENGTH_SHORT).show();
                    } else {

                        getQty++;

                        double cost = Double.parseDouble(price) * getQty;
                        Log.e("cost", "----" + cost);


                        double itemCgst = cost * Double.valueOf(cgstPercent) / 100;
                        double itemSgst = cost * Double.valueOf(sgstPercent) / 100;
                        double itemCess = cost * Double.valueOf(cessPercent) / 100;
                        Log.e("itemCgst", "----" + itemCgst);

                        holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("0", String.valueOf(cost)), new InputFilter.LengthFilter(15)});


                        holder.txtPrice.setText(currency + f.format(cost));
                        holder.txtQtyNumber.setText(String.valueOf(getQty));


                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        databaseAccess.updateProductQty(cart_id, "" + getQty);

                        databaseAccess.open();
                        totalPrice = databaseAccess.getTotalPrice();


                        double lineTotalTax = itemCgst + itemSgst + itemCess;
                        double lineTotal = Double.valueOf(cost) + lineTotalTax;

                        databaseAccess.open();
                        databaseAccess.updateLineTotal(cart_id, "" + lineTotal);


                        holder.edtDisc.getText().clear();
                        discount = 0;
                        databaseAccess.open();
                        databaseAccess.updateProductDiscount(cart_id, "" + discount);

                        holder.edtDisc.requestFocus();

                        allDiscount = 0.0;
                        databaseAccess.open();
                        allDiscount = databaseAccess.getTotalProductDiscount();
                        //setting total discount
                        txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + "(-)" + " " + currency + ": " + f.format(allDiscount));

                        txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

                        if (country.equals("UAE")) {
                            holder.txtCgst.setText("VAT" + " " + currency + " " + f.format(itemCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                            holder.txtSgst.setVisibility(View.GONE);
                            holder.txtCess.setVisibility(View.GONE);
                        } else {

                            if (Double.valueOf(itemCgst) != 0) {
                                holder.txtCgst.setVisibility(View.VISIBLE);
                                holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + " " + f.format(itemCgst));
                            } else {
                                holder.txtCgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(itemSgst) != 0) {
                                holder.txtSgst.setVisibility(View.VISIBLE);
                                holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + " " + f.format(itemSgst));
                            } else {
                                holder.txtSgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(itemCess) != 0) {
                                holder.txtCess.setVisibility(View.VISIBLE);
                                holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + " " + f.format(itemCess));
                            } else {
                                holder.txtCess.setVisibility(View.GONE);
                            }

                        }

                        //updates discounted tax to db
                        databaseAccess.open();
                        databaseAccess.updatecgst(cart_id, "" + itemCgst);
                        databaseAccess.open();
                        databaseAccess.updatesgst(cart_id, "" + itemSgst);
                        databaseAccess.open();
                        databaseAccess.updatecess(cart_id, "" + itemCess);


                        databaseAccess.open();
                        allTax = databaseAccess.getTotalTax();
                        databaseAccess.open();
                        allCgst = databaseAccess.getTotalCGST();
                        databaseAccess.open();
                        allSgst = databaseAccess.getTotalSGST();
                        databaseAccess.open();
                        allCess = databaseAccess.getTotalCESS();

                        if (country.equals("UAE")) {
                            txtTotalCgst.setText("Total Vat " + "(+)" + ": " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                            txtTotalSgst.setVisibility(View.GONE);
                            txtTotalCess.setVisibility(View.GONE);
                        } else {
                            if (Double.valueOf(allCgst) != 0) {
                                txtTotalCgst.setVisibility(View.VISIBLE);
                                txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + "(+)" + ": " + currency + " " + f.format(allCgst));
                            } else {
                                txtTotalCgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(allSgst) != 0) {
                                txtTotalSgst.setVisibility(View.VISIBLE);
                                txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + "(+)" + ": " + currency + " " + f.format(allSgst));
                            } else {
                                txtTotalSgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(cess) != 0) {
                                txtTotalCess.setVisibility(View.VISIBLE);
                                txtTotalCess.setText(context.getString(R.string.total_cess_cart) + "(+)" + ": " + currency + " " + f.format(allCess));
                            } else {
                                txtTotalCess.setVisibility(View.GONE);
                            }

                        }

                        databaseAccess.open();
                        allDiscountedTax = databaseAccess.getTotalTax();
                        finalTotal = (totalPrice - allDiscount) + allDiscountedTax;

                        if (Double.valueOf(finalTotal) != 0) {
                            txtFinalTotal.setVisibility(View.VISIBLE);
                            txtFinalTotal.setText(context.getString(R.string.grand_total) + currency + " " + f.format(Utils.round(finalTotal, 1)));
                        } else {
                            txtFinalTotal.setVisibility(View.GONE);
                        }

                    }
                }
            });

            holder.txtMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String qty = holder.txtQtyNumber.getText().toString();
                    double getQty = Double.parseDouble("0" + qty);


                    if (getQty >= 2) {
                        getQty--;

                        double cost = Double.parseDouble(price) * getQty;

                        double itemCgst = cost * Double.valueOf(cgstPercent) / 100;
                        double itemSgst = cost * Double.valueOf(sgstPercent) / 100;
                        double itemCess = cost * Double.valueOf(cessPercent) / 100;

                        holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("0", String.valueOf(cost)), new InputFilter.LengthFilter(15)});

                        holder.txtPrice.setText(currency + f.format(cost));
                        holder.txtQtyNumber.setText(String.valueOf(getQty));


                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        databaseAccess.updateProductQty(cart_id, "" + getQty);

                        databaseAccess.open();
                        totalPrice = databaseAccess.getTotalPrice();

//                        totalPrice = totalPrice - Double.valueOf(price);


                        holder.edtDisc.getText().clear();
                        discount = 0;
                        databaseAccess.open();
                        databaseAccess.updateProductDiscount(cart_id, "" + discount);

                        holder.edtDisc.requestFocus();

                        allDiscount = 0.0;
                        databaseAccess.open();
                        allDiscount = databaseAccess.getTotalProductDiscount();
                        //setting total discount
                        txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + "(-)" + " " + currency + " " + f.format(allDiscount));

                        txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

                        if (country.equals("UAE")) {
                            holder.txtCgst.setText("VAT" + " " + currency + " " + f.format(itemCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                            holder.txtSgst.setVisibility(View.GONE);
                            holder.txtCess.setVisibility(View.GONE);
                        } else {

                            if (Double.valueOf(itemCgst) != 0) {
                                holder.txtCgst.setVisibility(View.VISIBLE);
                                holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + " " + f.format(itemCgst));
                            } else {
                                holder.txtCgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(itemSgst) != 0) {
                                holder.txtSgst.setVisibility(View.VISIBLE);
                                holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + " " + f.format(itemSgst));
                            } else {
                                holder.txtSgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(itemCess) != 0) {
                                holder.txtCess.setVisibility(View.VISIBLE);
                                holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + " " + f.format(itemCess));
                            } else {
                                holder.txtCess.setVisibility(View.GONE);
                            }

                        }

                        //updates discounted tax to db
                        databaseAccess.open();
                        databaseAccess.updatecgst(cart_id, "" + itemCgst);
                        databaseAccess.open();
                        databaseAccess.updatesgst(cart_id, "" + itemSgst);
                        databaseAccess.open();
                        databaseAccess.updatecess(cart_id, "" + itemCess);


                        databaseAccess.open();
                        allTax = databaseAccess.getTotalTax();
                        databaseAccess.open();
                        allCgst = databaseAccess.getTotalCGST();
                        databaseAccess.open();
                        allSgst = databaseAccess.getTotalSGST();
                        databaseAccess.open();
                        allCess = databaseAccess.getTotalCESS();
                        if (country.equals("UAE")) {
                            txtTotalCgst.setText("Total Vat" + "(+)" + ": " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                            txtTotalSgst.setVisibility(View.GONE);
                            txtTotalCess.setVisibility(View.GONE);
                        } else {
                            if (Double.valueOf(allCgst) != 0) {
                                txtTotalCgst.setVisibility(View.VISIBLE);
                                txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + "(+)" + ": " + currency + " " + f.format(allCgst));
                            } else {
                                txtTotalCgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(allSgst) != 0) {
                                txtTotalSgst.setVisibility(View.VISIBLE);
                                txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + "(+)" + ": " + currency + " " + f.format(allSgst));
                            } else {
                                txtTotalSgst.setVisibility(View.GONE);
                            }

                            if (Double.valueOf(cess) != 0) {
                                txtTotalCess.setVisibility(View.VISIBLE);
                                txtTotalCess.setText(context.getString(R.string.total_cess_cart) + "(+)" + ": " + currency + " " + f.format(allCess));
                            } else {
                                txtTotalCess.setVisibility(View.GONE);
                            }

                        }

                        double lineTotalTax = itemCgst + itemSgst + itemCess;
                        double lineTotal = Double.valueOf(cost) + lineTotalTax;
                        databaseAccess.open();
                        databaseAccess.updateLineTotal(cart_id, "" + lineTotal);
                        databaseAccess.open();
                        allDiscountedTax = databaseAccess.getTotalTax();
                        finalTotal = (totalPrice - allDiscount) + allDiscountedTax;

                        if (finalTotal != 0) {
                            txtFinalTotal.setVisibility(View.VISIBLE);
                            txtFinalTotal.setText(context.getString(R.string.grand_total) + currency + " " + f.format(Utils.round(finalTotal, 1)));
                        } else {
                            txtFinalTotal.setVisibility(View.GONE);
                        }


                    }


                }

            });

            if (editable.equals("yes")) {
                holder.txtQtyNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        final String dis = holder.edtDisc.getText().toString().trim();
                        final String qty = holder.txtQtyNumber.getText().toString().trim();
                        isZero = false;

                        double cost = 0;
                        double qty1 = 0;

                        qty1 = Double.valueOf("0" + qty);

                        if (qty1 > getStock) {
                            Toasty.warning(context, R.string.stock_not_available_please_update_stock, Toast.LENGTH_SHORT).show();
                        } else {
                            if (qty1 == 0) {
                                isZero = true;
                            } else {

                                cost = Double.parseDouble(price) * qty1;
                                Log.e("cost", "------" + cost + "-");

                                holder.txtQtyNumber.setFilters(new InputFilter[]{new InputFilterMinMax("0.1", String.valueOf(getStock)), new InputFilter.LengthFilter(15)});
                                holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("0", String.valueOf(cost)), new InputFilter.LengthFilter(15)});


//              calculating tax to the discounted amount for each item
                                if (!dis.equals("")) {
                                    discount = Double.parseDouble(dis);
                                } else {
                                    discount = 0.0;
                                }
                                priceWithDisc = cost - discount;
                                Log.e("price", "------" + price + "-");
                                Log.e("qty", "------" + qty + "-");
                                Log.e("cost-discount=total", "------" + cost + "-" + dis + "=" + priceWithDisc);
                                double getCgst = 0, getSgst = 0, getCess = 0;
                                if (cgstPercent != null && !cgstPercent.equals("")) {
                                    getCgst = Double.parseDouble(cgstPercent);
                                }
                                if (sgstPercent != null && !sgstPercent.equals("")) {
                                    getSgst = Double.parseDouble(sgstPercent);
                                }
                                if (cessPercent != null && !cessPercent.equals("")) {
                                    getCess = Double.parseDouble(cessPercent);
                                }
                                double cgstAmount = (priceWithDisc * getCgst) / 100;
                                double sgstAmount = (priceWithDisc * getSgst) / 100;
                                double cessAmount = (priceWithDisc * getCess) / 100;
                                Log.e("price*vat=vatline", "---" + priceWithDisc + "*" + getCgst + "=" + cgstAmount);


                                // setting discounted price to the item
                                holder.txtPrice.setText(currency + f.format(priceWithDisc));

                                //update product discount to db
                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                databaseAccess.updateProductDiscount(cart_id, "" + discount);
                                //updates discounted tax to db
                                databaseAccess.open();
                                databaseAccess.updatecgst(cart_id, "" + cgstAmount);
                                databaseAccess.open();
                                databaseAccess.updatesgst(cart_id, "" + sgstAmount);
                                databaseAccess.open();
                                databaseAccess.updatecess(cart_id, "" + cessAmount);

                                // setting  tax amount to the item
                                if (country.equals("UAE")) {
                                    holder.txtCgst.setText("VAT" + " " + currency + " " + f.format(cgstAmount));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                                    holder.txtSgst.setVisibility(View.GONE);
                                    holder.txtCess.setVisibility(View.GONE);
                                } else {

                                    if (Double.valueOf(cgst) != 0) {
                                        holder.txtCgst.setVisibility(View.VISIBLE);
                                        holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + " " + f.format(cgstAmount));
                                    } else {
                                        holder.txtCgst.setVisibility(View.GONE);
                                    }

                                    if (Double.valueOf(sgst) != 0) {
                                        holder.txtSgst.setVisibility(View.VISIBLE);
                                        holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + " " + f.format(sgstAmount));
                                    } else {
                                        holder.txtSgst.setVisibility(View.GONE);
                                    }

                                    if (Double.valueOf(cess) != 0) {
                                        holder.txtCess.setVisibility(View.VISIBLE);
                                        holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + " " + f.format(cessAmount));
                                    } else {
                                        holder.txtCess.setVisibility(View.GONE);
                                    }

                                }

                                allDiscount = 0.0;
                                //getting total discount from db
                                databaseAccess.open();
                                allDiscount = databaseAccess.getTotalProductDiscount();

                                databaseAccess.open();
                                databaseAccess.updateProductQty(cart_id, "" + qty1);
                                databaseAccess.open();
                                totalPrice = databaseAccess.getTotalPrice();

//                        totalPrice = totalPrice + (Double.valueOf(price) * qty1);


                                txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));
                                //setting total discount
                                txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + "(-)" + ": " + currency + " " + f.format(allDiscount));

                                //getting total tax from db
                                databaseAccess.open();
                                allDiscountedCgst = databaseAccess.getTotalCGST();
                                databaseAccess.open();
                                allDiscountedSgst = databaseAccess.getTotalSGST();
                                databaseAccess.open();
                                allDiscountedCess = databaseAccess.getTotalCESS();
                                databaseAccess.open();
                                allDiscountedTax = databaseAccess.getTotalTax();

                                //setting total tax amount
                                if (country.equals("UAE")) {
                                    txtTotalCgst.setText("Total Vat " + "(+)" + ": " + currency + " " + f.format(allDiscountedCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                                    txtTotalSgst.setVisibility(View.GONE);
                                    txtTotalCess.setVisibility(View.GONE);
                                } else {
                                    if (Double.valueOf(allDiscountedCgst) != 0) {
                                        txtTotalCgst.setVisibility(View.VISIBLE);
                                        txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + "(+)" + ": " + currency + " " + f.format(allDiscountedCgst));
                                    } else {
                                        txtTotalCgst.setVisibility(View.GONE);
                                    }

                                    if (Double.valueOf(allDiscountedSgst) != 0) {
                                        txtTotalSgst.setVisibility(View.VISIBLE);
                                        txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + "(+)" + ": " + currency + " " + f.format(allDiscountedSgst));
                                    } else {
                                        txtTotalSgst.setVisibility(View.GONE);
                                    }

                                    if (Double.valueOf(allDiscountedCess) != 0) {
                                        txtTotalCess.setVisibility(View.VISIBLE);
                                        txtTotalCess.setText(context.getString(R.string.total_cess_cart) + "(+)" + ": " + currency + " " + f.format(allDiscountedCess));
                                    } else {
                                        txtTotalCess.setVisibility(View.GONE);
                                    }

                                }


                                double lineTotalTax = cgstAmount + sgstAmount + cessAmount;
                                double lineTotal = priceWithDisc + lineTotalTax;
                                Log.e("Price*TotalTax=lineT", "---" + priceWithDisc + "+" + lineTotalTax + "=" + lineTotal);

                                databaseAccess.open();
                                databaseAccess.updateLineTotal(cart_id, "" + lineTotal);

//                databaseAccess.open();
//                finalTotal= databaseAccess.getFinalTotalPrice();
                                finalTotal = (totalPrice - allDiscount) + allDiscountedTax;

                                if (finalTotal != 0) {
                                    txtFinalTotal.setVisibility(View.VISIBLE);
                                    txtFinalTotal.setText(context.getString(R.string.grand_total) + currency + " " + f.format(Utils.round(finalTotal, 1)));
                                } else {
                                    txtFinalTotal.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            } else {
                holder.txtQtyNumber.setEnabled(false);
            }

            holder.edtDisc.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                    final String dis = holder.edtDisc.getText().toString().trim();

                    final String qty = holder.txtQtyNumber.getText().toString().trim();

                    double cost = Double.parseDouble(price) * Double.parseDouble(qty);
                    Log.e("cost", "------" + cost + "-");


//              calculating tax to the discounted amount for each item
                    if (!dis.equals("")) {
                        if (holder.checkBox.isChecked()) {
                            holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100"), new InputFilter.LengthFilter(15)});
                            discount = (cost * Double.parseDouble(dis)) / 100;
                        } else {
                            holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("0", String.valueOf(cost)), new InputFilter.LengthFilter(15)});
                            discount = Double.parseDouble(dis);
                        }
                    } else {
                        discount = 0.0;
                    }
                    priceWithDisc = cost - discount;
                    Log.e("price", "------" + price + "-");
                    Log.e("qty", "------" + qty + "-");
                    Log.e("cost-discount=total", "------" + cost + "-" + dis + "=" + priceWithDisc);
                    double getCgst = 0, getSgst = 0, getCess = 0;
                    if (cgstPercent != null && !cgstPercent.equals("")) {
                        getCgst = Double.parseDouble(cgstPercent);
                    }
                    if (sgstPercent != null && !sgstPercent.equals("")) {
                        getSgst = Double.parseDouble(sgstPercent);
                    }
                    if (cessPercent != null && !cessPercent.equals("")) {
                        getCess = Double.parseDouble(cessPercent);
                    }
                    double cgstAmount = (priceWithDisc * getCgst) / 100;
                    double sgstAmount = (priceWithDisc * getSgst) / 100;
                    double cessAmount = (priceWithDisc * getCess) / 100;
                    Log.e("price*vat=vatline", "---" + priceWithDisc + "*" + getCgst + "=" + cgstAmount);


                    // setting discounted price to the item
                    holder.txtPrice.setText(currency + f.format(priceWithDisc));

                    //update product discount to db
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductDiscount(cart_id, "" + discount);

                    //updates discounted tax to db
                    databaseAccess.open();
                    databaseAccess.updatecgst(cart_id, "" + cgstAmount);
                    databaseAccess.open();
                    databaseAccess.updatesgst(cart_id, "" + sgstAmount);
                    databaseAccess.open();
                    databaseAccess.updatecess(cart_id, "" + cessAmount);

                    // setting  tax amount to the item
                    if (country.equals("UAE")) {
                        holder.txtCgst.setText("VAT" + " " + currency + " " + f.format(cgstAmount));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        holder.txtSgst.setVisibility(View.GONE);
                        holder.txtCess.setVisibility(View.GONE);
                    } else {

                        if (Double.valueOf(cgst) != 0) {
                            holder.txtCgst.setVisibility(View.VISIBLE);
                            holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + " " + f.format(cgstAmount));
                        } else {
                            holder.txtCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(sgst) != 0) {
                            holder.txtSgst.setVisibility(View.VISIBLE);
                            holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + " " + f.format(sgstAmount));
                        } else {
                            holder.txtSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(cess) != 0) {
                            holder.txtCess.setVisibility(View.VISIBLE);
                            holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + " " + f.format(cessAmount));
                        } else {
                            holder.txtCess.setVisibility(View.GONE);
                        }

                    }

                    allDiscount = 0.0;
                    //getting total discount from db
                    databaseAccess.open();
                    allDiscount = databaseAccess.getTotalProductDiscount();

                    //setting total discount
                    txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + "(-)" + ": " + currency + " " + f.format(allDiscount));

                    //getting total tax from db
                    databaseAccess.open();
                    allDiscountedCgst = databaseAccess.getTotalCGST();
                    databaseAccess.open();
                    allDiscountedSgst = databaseAccess.getTotalSGST();
                    databaseAccess.open();
                    allDiscountedCess = databaseAccess.getTotalCESS();
                    databaseAccess.open();
                    allDiscountedTax = databaseAccess.getTotalTax();

                    //setting total tax amount
                    if (country.equals("UAE")) {
                        txtTotalCgst.setText("Total Vat " + "(+)" + ": " + currency + " " + f.format(allDiscountedCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        txtTotalSgst.setVisibility(View.GONE);
                        txtTotalCess.setVisibility(View.GONE);
                    } else {
                        if (Double.valueOf(allDiscountedCgst) != 0) {
                            txtTotalCgst.setVisibility(View.VISIBLE);
                            txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + "(+)" + ": " + currency + " " + f.format(allDiscountedCgst));
                        } else {
                            txtTotalCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(allDiscountedSgst) != 0) {
                            txtTotalSgst.setVisibility(View.VISIBLE);
                            txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + "(+)" + ": " + currency + " " + f.format(allDiscountedSgst));
                        } else {
                            txtTotalSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(allDiscountedCess) != 0) {
                            txtTotalCess.setVisibility(View.VISIBLE);
                            txtTotalCess.setText(context.getString(R.string.total_cess_cart) + "(+)" + ": " + currency + " " + f.format(allDiscountedCess));
                        } else {
                            txtTotalCess.setVisibility(View.GONE);
                        }

                    }


                    double lineTotalTax = cgstAmount + sgstAmount + cessAmount;
                    double lineTotal = priceWithDisc + lineTotalTax;
                    Log.e("Price*TotalTax=lineT", "---" + priceWithDisc + "+" + lineTotalTax + "=" + lineTotal);

                    databaseAccess.open();
                    databaseAccess.updateLineTotal(cart_id, "" + lineTotal);

//                databaseAccess.open();
//                finalTotal= databaseAccess.getFinalTotalPrice();
                    finalTotal = (totalPrice - allDiscount) + allDiscountedTax;

                    if (finalTotal != 0) {
                        txtFinalTotal.setVisibility(View.VISIBLE);
                        txtFinalTotal.setText(context.getString(R.string.grand_total) + currency + " " + f.format(Utils.round(finalTotal, 1)));
                    } else {
                        txtFinalTotal.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


        }

        public void removeAt(List<HashMap<String, String>> cartProduct, int position) {
            cartProduct.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartProduct.size());
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return cartProduct.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtItemName, txtPrice, txtWeight, txtPlus, txtMinus, txtCgst, txtSgst, txtCess;
            ImageView imgProduct, imgDelete;
            EditText edtDisc, txtQtyNumber;
            CheckBox checkBox;

            public MyViewHolder(View itemView) {
                super(itemView);
                txtItemName = itemView.findViewById(R.id.txt_item_name);
                txtPrice = itemView.findViewById(R.id.txt_price);
                txtWeight = itemView.findViewById(R.id.txt_weight);
                checkBox = itemView.findViewById(R.id.checkBox);
                txtQtyNumber = itemView.findViewById(R.id.txt_number);
                imgProduct = itemView.findViewById(R.id.cart_product_image);
                imgDelete = itemView.findViewById(R.id.img_delete);
                txtMinus = itemView.findViewById(R.id.txt_minus);
                txtPlus = itemView.findViewById(R.id.txt_plus);
                edtDisc = itemView.findViewById(R.id.edt_disc);

                txtCgst = itemView.findViewById(R.id.txt_cgst);
                txtSgst = itemView.findViewById(R.id.txt_sgst);
                txtCess = itemView.findViewById(R.id.txt_cess);

            }


        }


    }


}

