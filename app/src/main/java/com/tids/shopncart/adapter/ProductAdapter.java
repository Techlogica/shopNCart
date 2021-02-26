package com.tids.shopncart.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.model.Product;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.product.EditProductActivity;
import com.tids.shopncart.utils.Utils;
import com.bumptech.glide.Glide;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {


    private ArrayList<HashMap<String, String>> productData;
    private Context context;
    Utils utils;
    SharedPreferences sp;
    String currency;
    DecimalFormat decimn = new DecimalFormat("#,###,##0.00");



    public ProductAdapter(Context context, ArrayList<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;
        utils = new Utils();
        sp = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");


    }


    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductAdapter.MyViewHolder holder, int position) {

        holder.fullCoverProduct.startAnimation(AnimationUtils.loadAnimation(context,R.anim.recycler_view_animation));


        final String product_id = productData.get(position).get("product_id");
        String productName = productData.get(position).get("product_name");
        String productStock = productData.get(position).get("product_stock");
        String productPrice = productData.get(position).get("product_sell_price");
        String productImage = productData.get(position).get("product_image");
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

                                if (utils.isNetworkAvailable(context)) {
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
        LinearLayout fullCoverProduct;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtSupplierName = itemView.findViewById(R.id.txt_product_supplier);
            txtSellPrice = itemView.findViewById(R.id.txt_product_sell_price);

            imgDelete = itemView.findViewById(R.id.img_delete);
            productImage = itemView.findViewById(R.id.product_image);
            fullCoverProduct = itemView.findViewById(R.id.full_cover_prodect);

            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, EditProductActivity.class);
            i.putExtra(Constant.PRODUCT_ID, productData.get(getAdapterPosition()).get("product_id"));
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
