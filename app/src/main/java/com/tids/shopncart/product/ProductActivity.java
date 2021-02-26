package com.tids.shopncart.product;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.tids.shopncart.adapter.ProductAdapter;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.Product;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;
import com.tids.shopncart.utils.Utils;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tids.shopncart.utils.Utils.isNetworkAvailable;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class ProductActivity extends BaseActivity {

    private RecyclerView recyclerView;
    ProductAdapter productAdapter;
    ProductApiAdapter productApiAdapter;
    DatabaseAccess databaseAccess;
    ImageView imgNoProduct;
    SaveProducts saveProductsTask = null;
    ArrayList<HashMap<String, String>> productsList = new ArrayList<>();
    List<Product> productsApiList = new ArrayList<>();
    PrefManager pref;

    FloatingActionButton fabAdd;
    private ShimmerFrameLayout mShimmerViewContainer;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String shopID = "";
    String ownerId = "";
    String staffId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        databaseAccess = DatabaseAccess.getInstance(this);
        pref = new PrefManager(this);
        fabAdd = findViewById(R.id.fab_add);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.products);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        //set color of swipe refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.product_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });


        //swipe refresh listeners
        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            getData();
            //after shuffle id done then swife refresh is off
            mSwipeRefreshLayout.setRefreshing(false);
        });

        getData();


    }

    private void getData() {

        if (!pref.getKeyDevice().equals("")) {
            if (Double.parseDouble(pref.getKeyDevice()) > 1) {
                if (isNetworkAvailable(ProductActivity.this)) {
                    getProductsData("", shopID, ownerId,staffId);

                } else {
                    Toasty.error(ProductActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                }
            } else {
                saveProductsTask = new SaveProducts(ProductActivity.this);
                saveProductsTask.execute();
            }
        }

    }


    //filter by searchquery
    private void filterList(String query) {

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


    public void getProductsData(String searchText, String shopId, String ownerId, String staffId) {
        productsApiList.clear();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Product>> call;
        call = apiInterface.getProducts(searchText, shopId, ownerId,staffId);

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
                        productApiAdapter = new ProductApiAdapter(ProductActivity.this, productsApiList);
                        recyclerView.setAdapter(productApiAdapter);

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {

                Toast.makeText(ProductActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


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

        }
    }

    private void setUpRecyclerView(ArrayList<HashMap<String, String>> arraylist) {

        if (arraylist.isEmpty()) {

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
            productAdapter = new ProductAdapter(ProductActivity.this, arraylist);

            recyclerView.setAdapter(productAdapter);

        }
    }

    public class ProductApiAdapter extends RecyclerView.Adapter<ProductApiAdapter.MyViewHolder> {


        private List<Product> productData;
        private Context context;
        Utils utils;
        SharedPreferences sp;
        String currency;
        DecimalFormat decimn = new DecimalFormat("#,###,##0.00");


        public ProductApiAdapter(Context context, List<Product> productData) {
            this.context = context;
            this.productData = productData;
            utils = new Utils();
            sp = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");


        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            Product obj = productData.get(position);

            final String product_id = obj.getProductId();
            String productName = obj.getProductName();
            String productStock = obj.getProductStock();
            String productPrice = obj.getProductSellPrice();
            String productImage = obj.getProductImage();
            String imageUrl = Constant.PRODUCT_IMAGE_URL + productImage;


            holder.txtProductName.setText(productName);
            holder.txtSupplierName.setText(context.getString(R.string.stock) + " :" + productStock);
            holder.txtSellPrice.setText(context.getString(R.string.sell_price) + currency + decimn.format(Double.parseDouble(productPrice)));


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


            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                    dialogBuilder
                            .withTitle(context.getString(R.string.delete))
                            .withMessage(context.getString(R.string.want_to_delete_product))
                            .withEffect(Slidetop)
                            .withDialogColor("#2979ff") //use color code for dialog
                            .withButton1Text(context.getString(R.string.yes))
                            .withButton2Text(context.getString(R.string.cancel))
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (isNetworkAvailable(context)) {
                                        deleteProduct(product_id);
                                        productData.remove(holder.getAdapterPosition());
                                        dialogBuilder.dismiss();
                                    } else {
                                        dialogBuilder.dismiss();
                                        Toasty.error(context, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                                    }


                                }
                            })
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialogBuilder.dismiss();
                                }
                            })
                            .show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return productData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView txtProductName, txtSupplierName, txtSellPrice;
            ImageView imgDelete, productImage;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                txtProductName = itemView.findViewById(R.id.txt_product_name);
                txtSupplierName = itemView.findViewById(R.id.txt_product_supplier);
                txtSellPrice = itemView.findViewById(R.id.txt_product_sell_price);

                imgDelete = itemView.findViewById(R.id.img_delete);
                productImage = itemView.findViewById(R.id.product_image);

                itemView.setOnClickListener(this);


            }


            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EditProductActivity.class);
                i.putExtra(Constant.PRODUCT_ID, productData.get(getAdapterPosition()).getProductId());
                context.startActivity(i);

            }
        }


        //delete from server
        private void deleteProduct(String productId) {

            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

            Call<Product> call = apiInterface.deleteProduct(productId);
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {


                    if (response.isSuccessful() && response.body() != null) {

                        String value = response.body().getValue();

                        if (value.equals(Constant.KEY_SUCCESS)) {
                            Toasty.error(context, R.string.product_deleted, Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();

                        } else if (value.equals(Constant.KEY_FAILURE)) {
                            Toasty.error(context, R.string.error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Product> call, Throwable t) {
                    Toast.makeText(context, "Error! " + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}
