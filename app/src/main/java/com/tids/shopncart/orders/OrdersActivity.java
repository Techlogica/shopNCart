package com.tids.shopncart.orders;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.adapter.OrderAdapter;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.OrderList;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;
import com.tids.shopncart.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends BaseActivity {


    private RecyclerView recyclerView;

    ImageView imgNoProduct;
    TextView txtNoProducts;
    private ShimmerFrameLayout mShimmerViewContainer;
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<OrderList> orderList;
    PrefManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        pref=new PrefManager(this);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);

        txtNoProducts = findViewById(R.id.txt_no_products);

        //set color of swipe refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_history);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        String staffId = sp.getString(Constant.SP_STAFF_ID, "");
        String deviceId =pref.getKeyDeviceId();


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrdersActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);

        Utils utils = new Utils();


        //swipe refresh listeners
        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            if (utils.isNetworkAvailable(OrdersActivity.this)) {
                getOrdersData("", shopID, ownerId,staffId,deviceId);
            } else {
                Toasty.error(OrdersActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }


            //after shuffle id done then swife refresh is off
            mSwipeRefreshLayout.setRefreshing(false);
        });


        if (utils.isNetworkAvailable(OrdersActivity.this)) {
            //Load data from server
            getOrdersData("", shopID, ownerId, staffId,deviceId);
        } else {
            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            //Stopping Shimmer Effects
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }


    }


    public void getOrdersData(String searchText, String shopId, String ownerId, String staffId, String deviceId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<OrderList>> call;
        call = apiInterface.getOrders(searchText, shopId, ownerId,staffId,deviceId);

        call.enqueue(new Callback<List<OrderList>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderList>> call, @NonNull Response<List<OrderList>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    orderList = response.body();


                    setUpRecyclerView(orderList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderList>> call, @NonNull Throwable t) {

                Toast.makeText(OrdersActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


    //filter by searchquery
    private void filterList(String query) {

        query = query.toLowerCase();
        List<OrderList> arrayList = new ArrayList<>();


        if (query.length() == 0) {
            arrayList = orderList;
        } else {
            for (int i = 0; i < orderList.size(); i++) {
                OrderList obj = orderList.get(i);
                boolean filter = false;
                if (obj.getCustomerName() != null)
                    if (obj.getCustomerName().toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (obj.getInvoiceId() != null)
                    if (obj.getInvoiceId().toLowerCase().contains(query)) {
                        filter = true;
                    }

                if (obj.getOrderPaymentMethod() != null)
                    if (obj.getOrderPaymentMethod().toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (filter) {
                    arrayList.add(obj);
                }
            }

        }
        setUpRecyclerView(arrayList);
    }

    private void setUpRecyclerView(List<OrderList> arrayList) {

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
            OrderAdapter orderAdapter = new OrderAdapter(OrdersActivity.this, arrayList);

            recyclerView.setAdapter(orderAdapter);

        }


    }


    // searchview on toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
//        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filterList(query);
                return true;
            }

        });
        return true;
    }

    // home button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
