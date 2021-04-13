package com.tids.shopncart.pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.adapter.ProductCategoryAdapter;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.Category;
import com.tids.shopncart.model.Product;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.tids.shopncart.utils.InputFilterMinMax;
import com.tids.shopncart.utils.MinMaxTextWatcher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tids.shopncart.pos.ProductCart.RESULT_CART;
import static com.tids.shopncart.utils.Utils.isNetworkAvailable;

public class PosActivity extends BaseActivity {


    private RecyclerView recyclerView, categoryRecyclerView;
    PosProductAdapter productAdapter;
    ProductApiAdapter productApiAdapter;
    TextView txtNoProducts;
    static ImageView RlCartHold;
    ImageView txtReset;
    PrefManager pref;
    static TextView txtCounterText, txtCounterHoldText;
    ProductCategoryAdapter categoryAdapter;
    static List<HashMap<String, String>> cartProductListCounter;
    SaveProducts saveProductsTask = null;
    ImageView imgNoProduct, imgScanner, imgBack;
    public static EditText etxtSearch;
    DatabaseAccess databaseAccess;
    List<HashMap<String, String>> cartProductList;
    static TextView cartBadge;
    static int cartCount = 0;
    String shopID = "";
    String deviceID = "";
    String ownerId = "";
    String staffId = "";
    String headerFlag = "";
    String headerDiscount = "";
    static Boolean editPriceFlag = false;
    static String editPriceValue = "";
    private FirebaseAnalytics mFirebaseAnalytics;
    public static Resources mResources;
    ArrayList<HashMap<String, String>> productsList = new ArrayList<>();
    List<Product> productsApiList = new ArrayList<>();
    ArrayList<HashMap<String, String>> categoryList = new ArrayList<>();


    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mResources = getResources();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        databaseAccess = DatabaseAccess.getInstance(this);
        pref = new PrefManager(this);
        etxtSearch = findViewById(R.id.etxt_search);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtNoProducts = findViewById(R.id.txt_no_products);
        imgScanner = findViewById(R.id.img_scanner);
        txtCounterText = findViewById(R.id.home_cart_counter);
        txtCounterHoldText = findViewById(R.id.home_cart_hold_counter);
        imgBack = findViewById(R.id.menu_back);
        categoryRecyclerView = findViewById(R.id.category_recyclerview);
        txtReset = findViewById(R.id.txt_reset);
        RlCartHold = findViewById(R.id.home_cart_hold);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");
        headerFlag = sp.getString(Constant.SP_HEADER_FLAG, "");
        headerDiscount = sp.getString(Constant.SP_HEADER_DISCOUNT, "");
        deviceID = pref.getKeyDeviceId();
        editPriceFlag = pref.getKeyEditFlag();
        editPriceValue = pref.getKeyEditValue();
        Log.e("device_id", "-----" + deviceID);


        counterSetiings();
        counterSetiingsHold();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        findViewById(R.id.home_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivityForResult(intent, 1);
            }
        });


        imgScanner.setOnClickListener(v -> {
            Intent intent = new Intent(PosActivity.this, ScannerActivity.class);
            startActivityForResult(intent, 2);
        });

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

//        getProductCategory(shopID, ownerId);

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);


        //Load data from server
//        getProductsData("", shopID, ownerId);
//        databaseAccess.open();
//        productsList = databaseAccess.getProducts();

        if (!pref.getKeyDevice().equals("")) {
            if (Double.parseDouble(pref.getKeyDevice()) > 1) {
                databaseAccess.open();
                categoryList = databaseAccess.getCategory();
                if (isNetworkAvailable(PosActivity.this)) {
                    getProductsData("", shopID, ownerId, staffId, deviceID);

                } else {
                    Toasty.error(PosActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                }
            } else {
                saveProductsTask = new SaveProducts(PosActivity.this);
                saveProductsTask.execute();
            }
        }

//        setUpRecyclerView(productsList);


        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getProductsData("", shopID, ownerId);
                if (!pref.getKeyDevice().equals("")) {
                    if (Double.parseDouble(pref.getKeyDevice()) > 1) {
                        if (isNetworkAvailable(PosActivity.this)) {
                            getProductsData("", shopID, ownerId, staffId, deviceID);

                        } else {
                            Toasty.error(PosActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        saveProductsTask = new SaveProducts(PosActivity.this);
                        saveProductsTask.execute();
                    }
                }
            }
        });

        RlCartHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseAccess.open();
                cartProductList = databaseAccess.getCartProduct();

                if (cartProductList != null) {

                    if (cartProductList.size() > 0) {
                        Toasty.info(PosActivity.this, "cart is not empty", Toast.LENGTH_SHORT).show();

                    }
                    if (cartProductList.size() == 0) {
                        databaseAccess.open();
                        //get data from local database
                        List<HashMap<String, String>> cartProductListHold;
                        cartProductListHold = databaseAccess.getCartProductTemp();
                        if (cartProductListHold != null && cartProductListHold.size() != 0) {

                            for (int i = 0; i < cartProductListHold.size(); i++) {
                                databaseAccess.open();
                                databaseAccess.addToCart(cartProductListHold.get(i).get("product_id"), cartProductListHold.get(i).get("product_name"), cartProductListHold.get(i).get("product_weight"), cartProductListHold.get(i).get("product_weight_unit"),
                                        cartProductListHold.get(i).get("product_price"), Double.valueOf(cartProductListHold.get(i).get("product_qty")), cartProductListHold.get(i).get("product_image"),
                                        cartProductListHold.get(i).get("product_stock"), Double.valueOf(cartProductListHold.get(i).get("cgst")), Double.valueOf(cartProductListHold.get(i).get("sgst")),
                                        Double.valueOf(cartProductListHold.get(i).get("cess")), Double.valueOf(cartProductListHold.get(i).get("product_discount")), cartProductListHold.get(i).get("product_cegst_percent"),
                                        cartProductListHold.get(i).get("product_sgst_percent"), cartProductListHold.get(i).get("product_cess_percent"), Double.valueOf(cartProductListHold.get(i).get("product_discounted_total")),
                                        Double.valueOf(cartProductListHold.get(i).get("product_line_total")), cartProductListHold.get(i).get("editable"));

                            }
                            databaseAccess.open();
                            databaseAccess.emptyCartHold();
                            counterSetiings();
                            counterSetiingsHold();

                        }

                    }


                }

            }
        });


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(PosActivity.this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(linerLayoutManager); // set LayoutManager to RecyclerView


        categoryRecyclerView.setHasFixedSize(true);

        //swipe refresh listeners
        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!pref.getKeyDevice().equals("")) {
                    if (Double.parseDouble(pref.getKeyDevice()) > 1) {
                        filterListApi(s.toString());

                    } else {
                        filterList1(s.toString());
                    }
                }

//                ArrayList<HashMap<String, String>> searchResultList;
//                databaseAccess.open();
//                searchResultList = databaseAccess.searchProducts(s.toString());
//                setUpRecyclerView(searchResultList);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data", s.toString());

            }


        });


    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public void getProductCategory(String shopId, String ownerId, String staffId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Category>> call;


        call = apiInterface.getCategory(shopId, ownerId, staffId);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {


                if (response.isSuccessful() && response.body() != null) {


                    List<Category> productCategory;
                    productCategory = response.body();

                    if (productCategory.isEmpty()) {
                        Toasty.info(PosActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                        imgNoProduct.setImageResource(R.drawable.no_data);


                    } else {

                        setUpRecyclerViewCategory(categoryList);


                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }

    private void setUpRecyclerViewCategory(ArrayList<HashMap<String, String>> categoryList) {

        categoryAdapter = new ProductCategoryAdapter(PosActivity.this, categoryList, productsList, productsApiList, recyclerView, imgNoProduct, txtNoProducts, mShimmerViewContainer);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    public void getProductsData(String searchText, String shopId, String ownerId, String staffId, String deviceID) {
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        productsApiList.clear();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Product>> call;
        call = apiInterface.getProducts(searchText, shopId, ownerId, staffId, deviceID);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    productsApiList = response.body();


                    if (productsApiList.isEmpty()) {

                        recyclerView.setVisibility(View.GONE);
                        imgNoProduct.setVisibility(View.VISIBLE);
                        imgNoProduct.setImageResource(R.drawable.not_found);
                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);


                    } else {

                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoProduct.setVisibility(View.GONE);
                        productApiAdapter = new ProductApiAdapter(PosActivity.this, productsApiList);
                        recyclerView.setAdapter(productApiAdapter);
                        setUpRecyclerViewCategory(categoryList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {

                Toast.makeText(PosActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }

    private void setUpRecyclerView(ArrayList<HashMap<String, String>> arrayList) {

        if (arrayList.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            //Stopping Shimmer Effects
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);


        } else {
            //Stopping Shimmer Effects
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);
            imgNoProduct.setVisibility(View.GONE);
            productAdapter = new PosProductAdapter(PosActivity.this, arrayList);
            recyclerView.setAdapter(productAdapter);

        }
    }

    private void counterSetiings() {
        databaseAccess.open();
        //get data from local database

        cartProductListCounter = databaseAccess.getCartProduct();
        if (cartProductListCounter != null && cartProductListCounter.size() != 0) {
            cartCount = cartProductListCounter.size();
        } else {
            cartCount = 0;
        }

        if (cartCount == 0) {
            txtCounterText.setVisibility(View.INVISIBLE);
            RlCartHold.setBackground(getResources().getDrawable(R.drawable.ic_pause_cart_order));
            txtCounterHoldText.setBackground(getResources().getDrawable(R.drawable.cart_counter));
        } else {
            txtCounterText.setVisibility(View.VISIBLE);
            RlCartHold.setBackground(getResources().getDrawable(R.drawable.ic_pause_cart_order_tint));
            txtCounterHoldText.setBackground(getResources().getDrawable(R.drawable.cart_counter_tint));
            txtCounterText.setText(String.valueOf(cartCount));
        }
    }


    private void counterSetiingsHold() {
        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> cartProductList;
        cartProductList = databaseAccess.getCartProductTemp();
        if (cartProductList != null && cartProductList.size() != 0) {
            cartCount = cartProductList.size();
        } else {
            cartCount = 0;
        }

        if (cartCount == 0) {
            txtCounterHoldText.setVisibility(View.INVISIBLE);
            RlCartHold.setVisibility(View.GONE);
        } else {
            txtCounterHoldText.setVisibility(View.VISIBLE);
            RlCartHold.setVisibility(View.VISIBLE);
            txtCounterHoldText.setText(String.valueOf(cartCount));
        }
    }

    //filter by searchquery
    private void filterList1(String query) {

        query = query.toLowerCase();
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();


        if (query.length() == 0) {
            arrayList = productsList;
        } else {
            for (int i = 0; i < productsList.size(); i++) {

                boolean filter = false;
                databaseAccess.open();
                if (productsList.get(i).get("product_code") != null)
                    if (productsList.get(i).get("product_code").toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (productsList.get(i).get("product_name") != null)
                    if (productsList.get(i).get("product_name").toLowerCase().contains(query)) {
                        filter = true;
                    }

                if (productsList.get(i).get("product_category_name") != null)
                    if (productsList.get(i).get("product_category_name").toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (filter) {
                    arrayList.add(productsList.get(i));
                }
            }

        }
        setUpRecyclerView(arrayList);
    }

    private void filterListApi(String query) {

        query = query.toLowerCase();

        List<Product> arrayList = new ArrayList<>();


        if (query.length() == 0) {
            arrayList = productsApiList;
        } else {
            for (int i = 0; i < productsApiList.size(); i++) {
                Product obj = productsApiList.get(i);
                boolean filter = false;
                if (obj.getProduct_code() != null)
                    if (obj.getProduct_code().toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (obj.getProductName() != null)
                    if (obj.getProductName().toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (obj.getProductCategoryName() != null)
                    if (obj.getProductCategoryName().toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (filter) {
                    arrayList.add(obj);
                }
            }

        }
        if (arrayList.size() == 0) {
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.no_data);
            txtNoProducts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            imgNoProduct.setVisibility(View.GONE);
            txtNoProducts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ProductApiAdapter productAdapter = new ProductApiAdapter(this, arrayList);
            recyclerView.setAdapter(productAdapter);
        }
    }


    class SaveProducts extends AsyncTask<Void, Integer, Void> {
        Context context;

        SaveProducts(Context context) {
            this.context = context;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            productsList.clear();
            mShimmerViewContainer.startShimmer();
        }

        @Override
        protected Void doInBackground(Void... params) {

            /* This is just a code that delays the thread execution */
            try {
                databaseAccess.open();
                productsList = databaseAccess.getProducts();
                databaseAccess.open();
                categoryList = databaseAccess.getCategory();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {

            if (!isFinishing()) {
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);

                Log.e("sizee", "-----" + productsList.size());
                if (productsList != null)
                    setUpRecyclerView(productsList);

            }
            if (productsList != null) {
                setUpRecyclerViewCategory(categoryList);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {

                    counterSetiings();
                    counterSetiingsHold();
                    if (!pref.getKeyDevice().equals("")) {
                        if (Double.parseDouble(pref.getKeyDevice()) > 1) {
                            if (isNetworkAvailable(PosActivity.this)) {
                                getProductsData("", shopID, ownerId, staffId, deviceID);

                            } else {
                                Toasty.error(PosActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            saveProductsTask = new SaveProducts(PosActivity.this);
                            saveProductsTask.execute();
                        }
                    }

                    setResult(RESULT_OK);


                }
                if (resultCode == RESULT_CART) {
                    counterSetiings();
                    counterSetiingsHold();
                    setResult(RESULT_OK);

                }
            }
            if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    String returnString = data.getStringExtra("product_code");
                    etxtSearch.setText(returnString);


                }
            }
        } catch (Exception ex) {
            Toast.makeText(PosActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    public static class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {

        private ArrayList<HashMap<String, String>> productData;
        private Context context;
        MediaPlayer player;
        DatabaseAccess databaseAccess;
        SharedPreferences sp;
        String currency;
        String country = "";
        DecimalFormat decimn = new DecimalFormat("#,###,##0.00");


        public PosProductAdapter(Context context, ArrayList<HashMap<String, String>> productData) {
            this.context = context;
            this.productData = productData;
            player = MediaPlayer.create(context, R.raw.delete_sound);
            databaseAccess = DatabaseAccess.getInstance(context);
            sp = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");
            country = sp.getString(Constant.SP_SHOP_COUNTRY, "");


        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_product_item, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            holder.cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation));

            databaseAccess.open();
            String productId = productData.get(position).get("product_id");
            String productName = productData.get(position).get("product_name");
            String productWeight = productData.get(position).get("product_weight");
            String productPrice = productData.get(position).get("product_sell_price");
            String weightUnit = productData.get(position).get("product_weight_unit_name");
            String productImage = productData.get(position).get("product_image");
            String productStock = productData.get(position).get("product_stock");
            String cgst = productData.get(position).get("cgst");
            String sgst = productData.get(position).get("sgst");
            String cess = productData.get(position).get("cess");
            String editable = productData.get(position).get("editable");
//            String todaySales = productData.get(position).getTotal_order_price();
//            String todayDiscount = productData.get(position).getTotal_today_discount();
//            String todayTax = productData.get(position).getTotal_today_tax();
            String imageUrl = Constant.PRODUCT_IMAGE_URL + productImage;
//            double amnt=Double.parseDouble(todaySales)-Double.parseDouble(todayDiscount)+Double.parseDouble(todayTax);
//            String todaySalesTotal = String.valueOf(amnt);

//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString(Constant.SP_TODAY_SALES, todaySalesTotal);
//            editor.apply();Lo
            Log.e("length", "-----" + productName.trim().length());

            if (productName.trim().length() < 13) {
                holder.txtProductName.setTextSize(16);
            } else if (productName.trim().length() > 14 && productName.trim().length() < 18) {
                holder.txtProductName.setTextSize(15);
            } else if (productName.trim().length() > 19 && productName.trim().length() < 25) {
                holder.txtProductName.setTextSize(14);
            } else if (productName.trim().length() > 26 && productName.trim().length() < 30) {
                holder.txtProductName.setTextSize(11);
            } else if (productName.trim().length() > 31) {
                holder.txtProductName.setTextSize(10);
            } else {
                holder.txtProductName.setTextSize(14);
            }
            holder.txtProductName.setText(productName);
            holder.txtWeight.setText(productWeight + " " + weightUnit);
            holder.txtCurrency.setText(currency + " ");
            holder.txtPrice.setText(decimn.format(Double.parseDouble(productPrice)));


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

            if (country.equals("UAE")) {
                holder.txtHintCGST.setText("VAT");
//            holder.txtHintSGST.setText("VAT 2");
                if (cgstAmount != 0) {
                    holder.txtCGST.setVisibility(View.VISIBLE);
                    holder.txtHintCGST.setVisibility(View.VISIBLE);
                    holder.txtHintCGST.setText("VAT");
                    holder.txtCGST.setText(currency + " " + decimn.format(cgstAmount) + " (" + cgst + "%)");
                } else {
                    holder.txtCGST.setVisibility(View.INVISIBLE);
                    holder.txtHintCGST.setVisibility(View.INVISIBLE);
                }
        /*    if (sgstAmount != 0) {
                holder.txtSGST.setVisibility(View.VISIBLE);
                holder.txtHintSGST.setVisibility(View.VISIBLE);
                holder.txtHintSGST.setText("VAT 2");
                holder.txtSGST.setText(currency +sgstAmount+" ("+sgst+"%)");
            } else {
                holder.txtSGST.setVisibility(View.GONE);
                holder.txtHintSGST.setVisibility(View.GONE);
            }*/
                holder.txtSGST.setVisibility(View.GONE);
                holder.txtHintSGST.setVisibility(View.GONE);
                holder.txtCESS.setVisibility(View.GONE);
                holder.txtLabelCESS.setVisibility(View.GONE);

            } else {
                if (cgstAmount != 0) {
                    holder.txtCGST.setVisibility(View.VISIBLE);
                    holder.txtHintCGST.setVisibility(View.VISIBLE);
                    holder.txtCGST.setText(currency + " " + decimn.format(cgstAmount) + " (" + cgst + "%)");
                } else {
                    holder.txtCGST.setVisibility(View.GONE);
                    holder.txtHintCGST.setVisibility(View.GONE);
                }

                if (sgstAmount != 0) {
                    holder.txtSGST.setVisibility(View.VISIBLE);
                    holder.txtHintSGST.setVisibility(View.VISIBLE);
                    holder.txtSGST.setText(currency + " " + decimn.format(sgstAmount) + " (" + sgst + "%)");
                } else {
                    holder.txtSGST.setVisibility(View.GONE);
                    holder.txtHintSGST.setVisibility(View.GONE);
                }


                if (cessAmount == 0) {
                    holder.txtCESS.setVisibility(View.GONE);
                    holder.txtLabelCESS.setVisibility(View.GONE);
                } else {
                    holder.txtLabelCESS.setVisibility(View.VISIBLE);
                    holder.txtCESS.setVisibility(View.VISIBLE);
                    holder.txtCESS.setText(currency + " " + decimn.format(cessAmount) + " (" + cess + "%)");
                }
                if (cgstAmount == 0 && sgstAmount == 0 && cessAmount == 0) {
                    holder.txtSGST.setVisibility(View.INVISIBLE);
                    holder.txtHintSGST.setVisibility(View.INVISIBLE);

                    holder.txtLabelCESS.setVisibility(View.INVISIBLE);
                    holder.txtCESS.setVisibility(View.INVISIBLE);

                    holder.txtHintCGST.setVisibility(View.INVISIBLE);
                    holder.txtCGST.setVisibility(View.INVISIBLE);
                }

            }
            //Low stock marked as RED color
            Double getStock = Double.parseDouble(productStock);
            if (getStock > 5) {
                holder.txtStock.setText(context.getString(R.string.stock) + " : " + productStock);
                holder.txtStockStatus.setVisibility(View.GONE);
                holder.txtStock.getResources().getDrawable(R.drawable.stock_tag_icon);

                holder.txtStockStatus.setBackgroundColor(Color.parseColor("#43a047"));
                holder.txtStockStatus.setText(context.getString(R.string.in_stock));
            } else if (getStock == 0) {
                holder.txtStock.setVisibility(View.GONE);
                holder.txtStockStatus.setVisibility(View.VISIBLE);
                holder.txtStockStatus.setText(context.getString(R.string.not_available));


            } else {
                holder.txtStock.setVisibility(View.GONE);
                holder.txtStockStatus.setVisibility(View.VISIBLE);
                holder.txtStockStatus.setText(context.getString(R.string.stock) + " : " + productStock);
                holder.txtStock.getResources().getDrawable(R.drawable.stock_tag_icon_orange);


            }
            if (editPriceFlag) {
                holder.txtPrice.setEnabled(true);
            }

            holder.imgCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etxtSearch.getText().clear();

                    player.start();

                    databaseAccess.open();
                    //get data from local database

                    cartProductListCounter = databaseAccess.getCartProduct();
                    if (cartProductListCounter != null && cartProductListCounter.size() != 0) {
                        cartCount = cartProductListCounter.size();
                    } else {
                        cartCount = 0;
                    }

                    if (cartCount == 0) {
                        txtCounterText.setVisibility(View.INVISIBLE);
                    } else {
                        txtCounterText.setVisibility(View.VISIBLE);
                        txtCounterText.setText(String.valueOf(cartCount));
                    }
//                Intent intent=new Intent(context, EditProductActivity.class);
//                intent.putExtra("product_id",productId);
//                context.startActivity(intent);

                    if (getStock <= 0) {

                        Toasty.warning(context, R.string.stock_not_available_please_update_stock, Toast.LENGTH_SHORT).show();
                    } else {
                        if (cartProductListCounter.size() == 0) {

                        }

                        String productPrice = holder.txtPrice.getText().toString();

                        if (!productPrice.equals("")) {
                            if (!editPriceValue.equals("0")) {
                                double value = itemPrice * Double.valueOf(editPriceValue) / 100;
                                double max = itemPrice + value;
                                double min = itemPrice - value;

                                double n = 0;
                                try {
                                    n = Double.parseDouble(productPrice);
                                    if (n < min) {
                                        holder.txtPrice.setText(String.valueOf(min));
                                        Toast.makeText(context, "Minimum allowed is " + min, Toast.LENGTH_SHORT).show();
                                    } else if (n > max) {
                                        holder.txtPrice.setText(String.valueOf(max));
                                        Toast.makeText(context, "Maximum allowed is " + max, Toast.LENGTH_SHORT).show();
                                    } else {
                                        databaseAccess.open();
                                        int check = databaseAccess.addToCart(productId, productName, productWeight, weightUnit, productPrice, 1.0, productImage, productStock, cgstAmount, sgstAmount, cessAmount, 0, cgst, sgst, cess, 0, 0, editable);
                                        if (check == 1) {
                                            Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                                            player.start();
                                            cartCount = cartCount + 1;
                                            if (cartCount == 0) {
                                                RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order));
                                                txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter));
                                                txtCounterText.setVisibility(View.INVISIBLE);
                                            } else {
                                                RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order_tint));
                                                txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter_tint));
                                                txtCounterText.setVisibility(View.VISIBLE);
                                                txtCounterText.setText(String.valueOf(cartCount));
                                            }
                                        } else if (check == 2) {
                                            Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();
                                        } else {

                                            Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                } catch (NumberFormatException nfe) {
                                    holder.txtPrice.setText("" + itemPrice);
                                    Toast.makeText(context, "Bad format for number!" + max, Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                databaseAccess.open();
                                int check = databaseAccess.addToCart(productId, productName, productWeight, weightUnit, productPrice, 1.0, productImage, productStock, cgstAmount, sgstAmount, cessAmount, 0, cgst, sgst, cess, 0, 0, editable);
                                if (check == 1) {
                                    Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                                    player.start();
                                    cartCount = cartCount + 1;
                                    if (cartCount == 0) {
                                        RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order));
                                        txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter));
                                        txtCounterText.setVisibility(View.INVISIBLE);
                                    } else {
                                        RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order_tint));
                                        txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter_tint));
                                        txtCounterText.setVisibility(View.VISIBLE);
                                        txtCounterText.setText(String.valueOf(cartCount));
                                    }
                                } else if (check == 2) {
                                    Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();
                                } else {

                                    Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                                }

                            }
                        } else {
                            Toast.makeText(context, "Product price should not empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });



            if (productImage != null) {
                if (productImage.length() < 3) {

                    holder.productImage.setImageResource(R.drawable.image_placeholder);
                } else {


                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.image_placeholder)
                            .into(holder.productImage);

                }
            }


        }

        @Override
        public int getItemCount() {
            return productData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CardView cardProduct;
            CardView cardView;
            LinearLayout layoutPrice,imgCart;
            EditText txtPrice;
            TextView txtProductName, txtCurrency, txtWeight, txtStock, txtStockStatus, txtCGST, txtSGST, txtCESS, txtLabelCESS, txtHintCGST, txtHintSGST;
            ImageView productImage;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                txtProductName = itemView.findViewById(R.id.txt_product_name);
                txtWeight = itemView.findViewById(R.id.txt_weight);
                txtStock = itemView.findViewById(R.id.txt_stock);
                layoutPrice = itemView.findViewById(R.id.layout_price);
                imgCart = itemView.findViewById(R.id.img_cart);
                txtCurrency = itemView.findViewById(R.id.txt_currency);
                txtPrice = itemView.findViewById(R.id.txt_price);
                productImage = itemView.findViewById(R.id.img_product);
                cardProduct = itemView.findViewById(R.id.card_product);
                cardView = itemView.findViewById(R.id.card_product);
                txtStockStatus = itemView.findViewById(R.id.txt_stock_status);
                txtCGST = itemView.findViewById(R.id.txt_cgst);
                txtSGST = itemView.findViewById(R.id.txt_sgst);
                txtCESS = itemView.findViewById(R.id.txt_cess);

                txtHintCGST = itemView.findViewById(R.id.hint_cgst);
                txtHintSGST = itemView.findViewById(R.id.hint_sgst);

                txtLabelCESS = itemView.findViewById(R.id.txt_label_cess);


            }
        }


    }

    public static class ProductApiAdapter extends RecyclerView.Adapter<ProductApiAdapter.MyViewHolder> {

        private List<Product> productData;
        private Context context;
        MediaPlayer player;
        DatabaseAccess databaseAccess;
        SharedPreferences sp;
        String currency;
        String country = "";
        DecimalFormat decimn = new DecimalFormat("#,###,##0.00");


        public ProductApiAdapter(Context context, List<Product> productData) {
            this.context = context;
            this.productData = productData;
            player = MediaPlayer.create(context, R.raw.delete_sound);
            databaseAccess = DatabaseAccess.getInstance(context);
            sp = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");
            country = sp.getString(Constant.SP_SHOP_COUNTRY, "");


        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_product_item, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            holder.cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation));


            Product obj = productData.get(position);

            final String productId = obj.getProductId();
            String productName = obj.getProductName();
            String productWeight = obj.getProductWeight();
            String weightUnit = obj.getProductWeightUnit();
            String productStock = obj.getProductStock();
            String productPrice = obj.getProductSellPrice();
            String productImage = obj.getProductImage();
            String cgst = obj.getCgst();
            String sgst = obj.getSgst();
            String cess = obj.getCess();
            String editable = obj.getEditable();
            String imageUrl = Constant.PRODUCT_IMAGE_URL + productImage;


            holder.txtProductName.setText(productName);

//            String todaySales = productData.get(position).getTotal_order_price();
//            String todayDiscount = productData.get(position).getTotal_today_discount();
//            String todayTax = productData.get(position).getTotal_today_tax();
//            double amnt=Double.parseDouble(todaySales)-Double.parseDouble(todayDiscount)+Double.parseDouble(todayTax);
//            String todaySalesTotal = String.valueOf(amnt);

//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString(Constant.SP_TODAY_SALES, todaySalesTotal);
//            editor.apply();


            if (productName.trim().length() < 13) {
                holder.txtProductName.setTextSize(16);
            } else if (productName.trim().length() > 14 && productName.trim().length() < 18) {
                holder.txtProductName.setTextSize(15);
            } else if (productName.trim().length() > 19 && productName.trim().length() < 25) {
                holder.txtProductName.setTextSize(14);
            } else if (productName.trim().length() > 26 && productName.trim().length() < 30) {
                holder.txtProductName.setTextSize(11);
            } else if (productName.trim().length() > 31) {
                holder.txtProductName.setTextSize(10);
            } else {
                holder.txtProductName.setTextSize(14);
            }

            holder.txtProductName.setText(productName);
            holder.txtWeight.setText(productWeight + " " + weightUnit);
            holder.txtPrice.setText(currency + " " + decimn.format(Double.parseDouble(productPrice)));


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

            if (country.equals("UAE")) {
                holder.txtHintCGST.setText("VAT");
//            holder.txtHintSGST.setText("VAT 2");
                if (cgstAmount != 0) {
                    holder.txtCGST.setVisibility(View.VISIBLE);
                    holder.txtHintCGST.setVisibility(View.VISIBLE);
                    holder.txtHintCGST.setText("VAT");
                    holder.txtCGST.setText(currency + " " + decimn.format(cgstAmount) + " (" + cgst + "%)");
                } else {
                    holder.txtCGST.setVisibility(View.INVISIBLE);
                    holder.txtHintCGST.setVisibility(View.INVISIBLE);
                }
        /*    if (sgstAmount != 0) {
                holder.txtSGST.setVisibility(View.VISIBLE);
                holder.txtHintSGST.setVisibility(View.VISIBLE);
                holder.txtHintSGST.setText("VAT 2");
                holder.txtSGST.setText(currency +sgstAmount+" ("+sgst+"%)");
            } else {
                holder.txtSGST.setVisibility(View.GONE);
                holder.txtHintSGST.setVisibility(View.GONE);
            }*/
                holder.txtSGST.setVisibility(View.GONE);
                holder.txtHintSGST.setVisibility(View.GONE);
                holder.txtCESS.setVisibility(View.GONE);
                holder.txtLabelCESS.setVisibility(View.GONE);

            } else {
                if (cgstAmount != 0) {
                    holder.txtCGST.setVisibility(View.VISIBLE);
                    holder.txtHintCGST.setVisibility(View.VISIBLE);
                    holder.txtCGST.setText(currency + " " + decimn.format(cgstAmount) + " (" + cgst + "%)");
                } else {
                    holder.txtCGST.setVisibility(View.GONE);
                    holder.txtHintCGST.setVisibility(View.GONE);
                }

                if (sgstAmount != 0) {
                    holder.txtSGST.setVisibility(View.VISIBLE);
                    holder.txtHintSGST.setVisibility(View.VISIBLE);
                    holder.txtSGST.setText(currency + " " + decimn.format(sgstAmount) + " (" + sgst + "%)");
                } else {
                    holder.txtSGST.setVisibility(View.GONE);
                    holder.txtHintSGST.setVisibility(View.GONE);
                }


                if (cessAmount == 0) {
                    holder.txtCESS.setVisibility(View.GONE);
                    holder.txtLabelCESS.setVisibility(View.GONE);
                } else {
                    holder.txtLabelCESS.setVisibility(View.VISIBLE);
                    holder.txtCESS.setVisibility(View.VISIBLE);
                    holder.txtCESS.setText(currency + " " + decimn.format(cessAmount) + " (" + cess + "%)");
                }
                if (cgstAmount == 0 && sgstAmount == 0 && cessAmount == 0) {
                    holder.txtSGST.setVisibility(View.INVISIBLE);
                    holder.txtHintSGST.setVisibility(View.INVISIBLE);

                    holder.txtLabelCESS.setVisibility(View.INVISIBLE);
                    holder.txtCESS.setVisibility(View.INVISIBLE);

                    holder.txtHintCGST.setVisibility(View.INVISIBLE);
                    holder.txtCGST.setVisibility(View.INVISIBLE);
                }

            }
            //Low stock marked as RED color
            Double getStock = Double.parseDouble(productStock);
            if (getStock > 5) {
                holder.txtStock.setText(context.getString(R.string.stock) + " : " + productStock);
                holder.txtStockStatus.setVisibility(View.GONE);
                holder.txtStock.getResources().getDrawable(R.drawable.stock_tag_icon);

                holder.txtStockStatus.setBackgroundColor(Color.parseColor("#43a047"));
                holder.txtStockStatus.setText(context.getString(R.string.in_stock));
            } else if (getStock == 0) {
                holder.txtStock.setVisibility(View.GONE);
                holder.txtStockStatus.setVisibility(View.VISIBLE);
                holder.txtStockStatus.setText(context.getString(R.string.not_available));


            } else {
                holder.txtStock.setVisibility(View.GONE);
                holder.txtStockStatus.setVisibility(View.VISIBLE);
                holder.txtStockStatus.setText(context.getString(R.string.stock) + " : " + productStock);
                holder.txtStock.getResources().getDrawable(R.drawable.stock_tag_icon_orange);


            }

            if (editPriceFlag) {
                holder.txtPrice.setEnabled(true);
            }

            holder.imgCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etxtSearch.getText().clear();

                    player.start();

                    databaseAccess.open();
                    //get data from local database

                    cartProductListCounter = databaseAccess.getCartProduct();
                    if (cartProductListCounter != null && cartProductListCounter.size() != 0) {
                        cartCount = cartProductListCounter.size();
                    } else {
                        cartCount = 0;
                    }

                    if (cartCount == 0) {
                        txtCounterText.setVisibility(View.INVISIBLE);
                    } else {
                        txtCounterText.setVisibility(View.VISIBLE);
                        txtCounterText.setText(String.valueOf(cartCount));
                    }
//                Intent intent=new Intent(context, EditProductActivity.class);
//                intent.putExtra("product_id",productId);
//                context.startActivity(intent);

                    if (getStock <= 0) {

                        Toasty.warning(context, R.string.stock_not_available_please_update_stock, Toast.LENGTH_SHORT).show();
                    } else {
                        if (cartProductListCounter.size() == 0) {

                        }

                        String productPrice = holder.txtPrice.getText().toString();

                        if (!productPrice.equals("")) {
                            if (!editPriceValue.equals("0")) {
                                double value = itemPrice * Double.valueOf(editPriceValue) / 100;
                                double max = itemPrice + value;
                                double min = itemPrice - value;

                                double n = 0;
                                try {
                                    n = Double.parseDouble(productPrice);
                                    if (n < min) {
                                        holder.txtPrice.setText(String.valueOf(min));
                                        Toast.makeText(context, "Minimum allowed is " + min, Toast.LENGTH_SHORT).show();
                                    } else if (n > max) {
                                        holder.txtPrice.setText(String.valueOf(max));
                                        Toast.makeText(context, "Maximum allowed is " + max, Toast.LENGTH_SHORT).show();
                                    } else {
                                        databaseAccess.open();
                                        int check = databaseAccess.addToCart(productId, productName, productWeight, weightUnit, productPrice, 1.0, productImage, productStock, cgstAmount, sgstAmount, cessAmount, 0, cgst, sgst, cess, 0, 0, editable);
                                        if (check == 1) {
                                            Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                                            player.start();
                                            cartCount = cartCount + 1;
                                            if (cartCount == 0) {
                                                RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order));
                                                txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter));
                                                txtCounterText.setVisibility(View.INVISIBLE);
                                            } else {
                                                RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order_tint));
                                                txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter_tint));
                                                txtCounterText.setVisibility(View.VISIBLE);
                                                txtCounterText.setText(String.valueOf(cartCount));
                                            }
                                        } else if (check == 2) {
                                            Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();
                                        } else {

                                            Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                } catch (NumberFormatException nfe) {
                                    holder.txtPrice.setText("" + itemPrice);
                                    Toast.makeText(context, "Bad format for number!" + max, Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                databaseAccess.open();
                                int check = databaseAccess.addToCart(productId, productName, productWeight, weightUnit, productPrice, 1.0, productImage, productStock, cgstAmount, sgstAmount, cessAmount, 0, cgst, sgst, cess, 0, 0, editable);
                                if (check == 1) {
                                    Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                                    player.start();
                                    cartCount = cartCount + 1;
                                    if (cartCount == 0) {
                                        RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order));
                                        txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter));
                                        txtCounterText.setVisibility(View.INVISIBLE);
                                    } else {
                                        RlCartHold.setBackground(mResources.getDrawable(R.drawable.ic_pause_cart_order_tint));
                                        txtCounterHoldText.setBackground(mResources.getDrawable(R.drawable.cart_counter_tint));
                                        txtCounterText.setVisibility(View.VISIBLE);
                                        txtCounterText.setText(String.valueOf(cartCount));
                                    }
                                } else if (check == 2) {
                                    Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();
                                } else {

                                    Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                                }

                            }
                        } else {
                            Toast.makeText(context, "Product price should not empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            if (productImage != null) {
                if (productImage.length() < 3) {

                    holder.productImage.setImageResource(R.drawable.image_placeholder);
                } else {


                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.image_placeholder)
                            .into(holder.productImage);

                }
            }


        }

        @Override
        public int getItemCount() {
            return productData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;;
            CardView cardProduct;
            LinearLayout imgCart;
            TextView txtProductName, txtWeight, txtPrice, txtStock, txtStockStatus, txtCGST, txtSGST, txtCESS, txtLabelCESS, txtHintCGST, txtHintSGST;
            ImageView productImage;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                txtProductName = itemView.findViewById(R.id.txt_product_name);
                txtWeight = itemView.findViewById(R.id.txt_weight);
                txtStock = itemView.findViewById(R.id.txt_stock);
                txtPrice = itemView.findViewById(R.id.txt_price);
                imgCart = itemView.findViewById(R.id.img_cart);
                productImage = itemView.findViewById(R.id.img_product);
                cardProduct = itemView.findViewById(R.id.card_product);
                cardView = itemView.findViewById(R.id.card_product);
                txtStockStatus = itemView.findViewById(R.id.txt_stock_status);
                txtCGST = itemView.findViewById(R.id.txt_cgst);
                txtSGST = itemView.findViewById(R.id.txt_sgst);
                txtCESS = itemView.findViewById(R.id.txt_cess);

                txtHintCGST = itemView.findViewById(R.id.hint_cgst);
                txtHintSGST = itemView.findViewById(R.id.hint_sgst);

                txtLabelCESS = itemView.findViewById(R.id.txt_label_cess);


            }
        }

    }


}
