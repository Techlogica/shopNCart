package com.tids.shopncart.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.adapter.OrderDetailsAdapter;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.OrderDetails;
import com.tids.shopncart.model.SalesReport;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.DecimalFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesReportActivity extends BaseActivity {


    private RecyclerView recyclerView;
    ImageView imgNoProduct;
    LinearLayout layoutSummary;
    TextView txtNoProducts, txtTotalPrice, txtTotalTax, txtTotalDiscount, txtNetSales;
    private ShimmerFrameLayout mShimmerViewContainer;
    SharedPreferences sp;
    String currency, shopID, ownerId, staffId,deviceId="";
    DecimalFormat f;
    PrefManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);
        pref=new PrefManager(this);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        f = new DecimalFormat("#,###,##0.00");
        txtNoProducts = findViewById(R.id.txt_no_products);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTotalTax = findViewById(R.id.txt_total_tax);
        txtTotalDiscount = findViewById(R.id.txt_total_discount);
        txtNetSales = findViewById(R.id.txt_net_sales);
        layoutSummary = findViewById(R.id.layout_summary);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "N/A");

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");
        deviceId = pref.getKeyDeviceId();


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.daily);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SalesReportActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        //sum of all transaction
        getSalesReport("Today", shopID, ownerId, staffId,deviceId);
        //to view all sales data
        getReport("Today", shopID, ownerId, staffId,deviceId);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_sales_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_all_sales:
                getReport("all", shopID, ownerId, staffId,deviceId);
                return true;

            case R.id.menu_daily:
                getReport("Today", shopID, ownerId, staffId,deviceId);
                getSupportActionBar().setTitle(R.string.daily);

                return true;

            case R.id.menu_weekly:
                getReport("last_week", shopID, ownerId, staffId,deviceId);
                getSupportActionBar().setTitle(R.string.weekly);

                return true;


            case R.id.menu_monthly:
                getReport("monthly", shopID, ownerId, staffId,deviceId);
                getSupportActionBar().setTitle(R.string.monthly);


                return true;

            case R.id.menu_yearly:
                getReport("yearly", shopID, ownerId, staffId,deviceId);
                getSupportActionBar().setTitle(R.string.yearly);


                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getReport(String type, String shopId, String ownerId, String staffId, String deviceId) {

        getSalesReport(type, shopId, ownerId, staffId,deviceId);
        getReportList(type, shopId, ownerId, staffId,deviceId);
        //Stopping Shimmer Effects
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);


    }


    public void getSalesReport(String type, String shopId, String ownerId, String staffId, String deviceId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<SalesReport>> call;
        call = apiInterface.getSalesReport(type, shopId, ownerId, staffId,deviceId);

        call.enqueue(new Callback<List<SalesReport>>() {
            @Override
            public void onResponse(@NonNull Call<List<SalesReport>> call, @NonNull Response<List<SalesReport>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<SalesReport> salesReport;
                    salesReport = response.body();


                    if (salesReport.isEmpty()) {


                        Log.d("Data", "Empty");


                    } else {


                        String totalOrderPrice = salesReport.get(0).getTotalOrderPrice();
                        String totalTax = salesReport.get(0).getTotalTax();
                        String totalDiscount = salesReport.get(0).getTotalDiscount();

                        Double orderPrice = 0.0;
                        Double getTax = 0.0;
                        Double getDiscount = 0.0;
                        if (totalOrderPrice != null) {
                            orderPrice = Double.parseDouble(totalOrderPrice);
                            txtTotalPrice.setVisibility(View.VISIBLE);
                            txtTotalPrice.setText(getString(R.string.total_price) + currency + " " + f.format(Double.parseDouble(totalOrderPrice)));
                        } else {
                            txtTotalPrice.setVisibility(View.INVISIBLE);
                        }
                        if (totalTax != null) {
                            txtTotalTax.setVisibility(View.VISIBLE);
                            getTax = Double.parseDouble(totalTax);
                            txtTotalTax.setText(getString(R.string.total_tax) + ":" + currency + " " + f.format(Double.parseDouble(totalTax)));
                        } else {
                            txtTotalTax.setVisibility(View.INVISIBLE);
                        }
                        if (totalDiscount != null) {
                            txtTotalDiscount.setVisibility(View.VISIBLE);
                            getDiscount = Double.parseDouble(totalDiscount);
                            txtTotalDiscount.setText(getString(R.string.total_discount) + ":" + currency + " " + f.format(Double.parseDouble(totalDiscount)));
                        } else {
                            txtTotalDiscount.setVisibility(View.INVISIBLE);
                        }
                        Double netSales = (orderPrice - getDiscount) + getTax;
                        if (netSales != null) {
                            txtNetSales.setVisibility(View.VISIBLE);
                            txtNetSales.setText(getString(R.string.net_sales) + ":" + currency + " " + f.format(netSales));
                        } else {
                            txtNetSales.setVisibility(View.INVISIBLE);
                        }

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SalesReport>> call, @NonNull Throwable t) {

                Toast.makeText(SalesReportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


    public void getReportList(String type, String shopId, String ownerId, String staffId, String deviceId) {


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<OrderDetails>> call;
        call = apiInterface.getReportList(type, shopId, ownerId, staffId,deviceId);

        call.enqueue(new Callback<List<OrderDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDetails>> call, @NonNull Response<List<OrderDetails>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDetails> orderDetails;
                    orderDetails = response.body();
                    Log.d("reports", "" + orderDetails.toString());


                    if (orderDetails.isEmpty()) {

                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.GONE);
                        imgNoProduct.setVisibility(View.VISIBLE);
                        imgNoProduct.setImageResource(R.drawable.not_found);
                        txtNoProducts.setVisibility(View.VISIBLE);
                        layoutSummary.setVisibility(View.GONE);
//                        Toasty.warning(SalesReportActivity.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();

                    } else {

                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(SalesReportActivity.this, orderDetails);

                        recyclerView.setAdapter(orderDetailsAdapter);

                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoProduct.setVisibility(View.GONE);
                        txtNoProducts.setVisibility(View.GONE);
                        layoutSummary.setVisibility(View.VISIBLE);


                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDetails>> call, @NonNull Throwable t) {


                Toast.makeText(SalesReportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


}

