package com.tids.shopncart.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tids.shopncart.R;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.Product;
import com.tids.shopncart.pos.PosActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.MyViewHolder> {


    MediaPlayer player;
    private final ArrayList<HashMap<String, String>> categoryData;
    private final Context context;
    RecyclerView recyclerView;
    ImageView imgNoProduct;
    TextView txtNoProducts;
    DatabaseAccess databaseAccess;
    ArrayList<HashMap<String, String>> productsList;
    List<Product> productsApiList;
    PrefManager pref;

    public ProductCategoryAdapter(Context context, ArrayList<HashMap<String, String>> categoryData, ArrayList<HashMap<String, String>> productsList, List<Product> productsApiList, RecyclerView recyclerView, ImageView imgNoProduct, TextView txtNoProducts, ShimmerFrameLayout mShimmerViewContainer) {
        this.context = context;
        this.categoryData = categoryData;
        this.productsList = productsList;
        this.productsApiList = productsApiList;
        this.recyclerView = recyclerView;
        player = MediaPlayer.create(context, R.raw.delete_sound);

        this.imgNoProduct = imgNoProduct;
        this.txtNoProducts = txtNoProducts;

    }


    @NonNull
    @Override
    public ProductCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductCategoryAdapter.MyViewHolder holder, int position) {

        final String categoryId = categoryData.get(position).get("product_category_id");
        String categoryName = categoryData.get(position).get("product_category_name");
        pref = new PrefManager(context);

        holder.txtCategoryName.setText(categoryName);
        holder.cardCategory.setOnClickListener(v -> {

            player.start();
            if (!pref.getKeyDevice().equals("")) {
                if (Double.parseDouble(pref.getKeyDevice()) > 1) {
                    filterListApi(categoryName);

                } else {
                    filterList1(categoryName);
                }
            }


        });


    }

    @Override
    public int getItemCount() {
        return categoryData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtCategoryName;
        CardView cardCategory;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategoryName = itemView.findViewById(R.id.txt_category_name);
            cardCategory = itemView.findViewById(R.id.card_category);

        }


    }

    //filter by searchquery
    private void filterList1(String query) {
        databaseAccess = DatabaseAccess.getInstance(context);
        query = query.toLowerCase();

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();


        if (query.length() == 0) {
            arrayList = productsList;
        } else {
            for (int i = 0; i < productsList.size(); i++) {

                boolean filter = false;
                databaseAccess.open();
                if (productsList.get(i).get("product_category_name") != null)
                    if (Objects.requireNonNull(productsList.get(i).get("product_category_name")).toLowerCase().contains(query)) {
                        filter = true;
                    }
                if (filter) {
                    arrayList.add(productsList.get(i));
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
            imgNoProduct.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            PosActivity.PosProductAdapter productAdapter = new PosActivity.PosProductAdapter(context, arrayList);
            recyclerView.setAdapter(productAdapter);
        }

    }

    private void filterListApi(String query) {
        databaseAccess = DatabaseAccess.getInstance(context);
        query = query.toLowerCase();

        List<Product> arrayList = new ArrayList<>();


        if (query.length() == 0) {
            arrayList = productsApiList;
        } else {
            for (int i = 0; i < productsApiList.size(); i++) {
                Product obj = productsApiList.get(i);
                boolean filter = false;
                databaseAccess.open();
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
            PosActivity.ProductApiAdapter productAdapter = new PosActivity.ProductApiAdapter(context, arrayList);
            recyclerView.setAdapter(productAdapter);
        }
    }
}
