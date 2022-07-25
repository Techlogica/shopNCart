package com.tids.shopncart.settings.categories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.model.Category;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class CategoriesActivity extends BaseActivity {


    private RecyclerView recyclerView;

    ImageView imgNoProduct, backBtn;
    private ShimmerFrameLayout mShimmerViewContainer;
    FloatingActionButton fabAdd;
    String shopID = "", ownerId = "", staffId = "";
    CategoryAdapter categoryAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


//        getSupportActionBar().setHomeButtonEnabled(true); //for back button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
//        getSupportActionBar().setTitle(R.string.categories);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.menu_back);
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        imgNoProduct = findViewById(R.id.image_no_product);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        fabAdd = findViewById(R.id.fab_add);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        getProductCategory(shopID, ownerId,staffId);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CatagoryAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });


    }


    public void getProductCategory(String shopId, String ownerId, String staffId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Category>> call;


        call = apiInterface.getCategory(shopId, ownerId,staffId);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {


                if (response.isSuccessful() && response.body() != null) {


                    List<Category> productCategory;
                    productCategory = response.body();

                    if (productCategory.isEmpty()) {
//                        Toasty.info(CategoriesActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                        imgNoProduct.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        imgNoProduct.setImageResource(R.drawable.no_data);

                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    } else {


                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        imgNoProduct.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        categoryAdapter = new CategoryAdapter(CategoriesActivity.this, productCategory);
                        recyclerView.setAdapter(categoryAdapter);

                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }

    //delete from server
    private void deleteCategory(String categoryId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Category> call = apiInterface.deleteCategory(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {


                if (response.isSuccessful() && response.body() != null) {

                    String value = response.body().getValue();

                    if (value.equals(Constant.KEY_SUCCESS)) {
                        Toasty.success(CategoriesActivity.this, R.string.category_deleted, Toast.LENGTH_SHORT).show();
                        getProductCategory(shopID, ownerId,staffId);

                    } else if (value.equals(Constant.KEY_FAILURE)) {
                        Toasty.error(CategoriesActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CategoriesActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, Throwable t) {
                Toast.makeText(CategoriesActivity.this, "Error! " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    getProductCategory(shopID, ownerId,staffId);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(CategoriesActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }


    //for back button
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            this.finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {


        private List<Category> categoryData;
        private Context context;


        public CategoryAdapter(Context context, List<Category> categoryData) {
            this.context = context;
            this.categoryData = categoryData;

        }


        @NonNull
        @Override
        public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.MyViewHolder holder, int position) {


            String categoryName = categoryData.get(position).getProductCategoryName();
            String categoryId = categoryData.get(position).getProductCategoryId();
            holder.txtCategoryName.setText(categoryName);

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                    dialogBuilder
                            .withTitle(context.getString(R.string.delete))
                            .withMessage(context.getString(R.string.want_to_delete))
                            .withEffect(Slidetop)
                            .withDialogColor("#2979ff") //use color code for dialog
                            .withButton1Text(context.getString(R.string.yes))
                            .withButton2Text(context.getString(R.string.cancel))
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    deleteCategory(categoryId);
                                    dialogBuilder.dismiss();
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
            return categoryData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView txtCategoryName;
            ImageView imgDelete;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                txtCategoryName = itemView.findViewById(R.id.txt_category_name);
                imgDelete = itemView.findViewById(R.id.img_delete);


                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

                Toast.makeText(context, categoryData.get(getAdapterPosition()).getProductCategoryName(), Toast.LENGTH_SHORT).show();

            }
        }


    }

}
