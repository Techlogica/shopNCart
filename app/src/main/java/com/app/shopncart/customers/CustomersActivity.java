package com.app.shopncart.customers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.shopncart.Constant;
import com.app.shopncart.R;
import com.app.shopncart.adapter.CustomerAdapter;
import com.app.shopncart.model.Customer;
import com.app.shopncart.networking.ApiClient;
import com.app.shopncart.networking.ApiInterface;
import com.app.shopncart.utils.BaseActivity;
import com.app.shopncart.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersActivity extends BaseActivity {



    private RecyclerView recyclerView;
    ImageView imgNoProduct;
    FloatingActionButton fabAdd;
    private ShimmerFrameLayout mShimmerViewContainer;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SharedPreferences sp;
    List<Customer> customerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_customer);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mSwipeRefreshLayout =findViewById(R.id.swipeToRefresh);
        //set color of swipe refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView = findViewById(R.id.recycler_view);
        imgNoProduct = findViewById(R.id.image_no_product);
        fabAdd = findViewById(R.id.fab_add);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        Utils utils=new Utils();


        //swipe refresh listeners
        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            if (utils.isNetworkAvailable(CustomersActivity.this))
            {
                getCustomerData("",shopID,ownerId);
            }
            else
            {
                Toasty.error(CustomersActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }


            //after shuffle id done then swife refresh is off
            mSwipeRefreshLayout.setRefreshing(false);
        });


        if (utils.isNetworkAvailable(CustomersActivity.this))
        {
            //Load data from server
            getCustomerData("",shopID,ownerId);
        }
        else
        {
            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            //Stopping Shimmer Effects
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersActivity.this, AddCustomersActivity.class);
                startActivity(intent);
            }
        });


    }


    public void getCustomerData(String searchText,String shopId,String ownerId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Customer>> call;
        call = apiInterface.getCustomers(searchText,shopId,ownerId);

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    customerList = response.body();

                    setUpRecyclerView(customerList);

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {

                Toast.makeText(CustomersActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


    //filter by searchquery
    private void filterList(String query) {

        query = query.toLowerCase();
        List<Customer> arrayList = new ArrayList<>();


        if (query.length() == 0) {
            arrayList = customerList;
        } else {
            for (int i = 0; i < customerList.size(); i++) {
                Customer obj=customerList.get(i);
                boolean filter = false;
                if (obj.getCustomerName() != null)
                    if (obj.getCustomerName().toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (obj.getCustomerId() != null)
                    if (obj.getCustomerId().toLowerCase().contains(query)) {
                        filter = true;
                    }

                if (obj.getCustomerCell() != null)
                    if (obj.getCustomerCell().toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (filter) {
                    arrayList.add(obj);
                }
            }

        }
        setUpRecyclerView(arrayList);
    }

    private void setUpRecyclerView(List<Customer> arrayList) {

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
            CustomerAdapter customerAdapter = new CustomerAdapter(CustomersActivity.this, arrayList);

            recyclerView.setAdapter(customerAdapter);

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
