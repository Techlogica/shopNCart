package com.app.shopncart.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopncart.Constant;
import com.app.shopncart.R;
import com.app.shopncart.database.DatabaseAccess;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {


    private List<HashMap<String, String>> cartProduct;
    private Context context;
    MediaPlayer player;
    TextView txtNoProduct;

    TextView txtTotalPrice, txtTotalCgst, txtTotalSgst, txtTotalCess, txtTotalDisc;

    public static Double totalPrice, allTax, allCgst, allSgst, allCess;
    Button btnSubmitOrder;
    ImageView imgNoProduct;

    SharedPreferences sp;
    String currency;
    String country = "";
    DecimalFormat f;

    public CartAdapter(Context context, List<HashMap<String, String>> cartProduct, TextView txtTotalPrice, Button btnSubmitOrder, ImageView imgNoProduct, TextView txtNoProduct, TextView txtTotalCgst, TextView txtTotalSgst, TextView txtTotalCess, TextView txtTotalDisc) {
        this.context = context;
        this.cartProduct = cartProduct;
        player = MediaPlayer.create(context, R.raw.delete_sound);
        this.txtTotalPrice = txtTotalPrice;
        this.txtTotalCgst = txtTotalCgst;
        this.txtTotalSgst = txtTotalSgst;
        this.txtTotalCess = txtTotalCess;
        this.txtTotalDisc = txtTotalDisc;
        this.btnSubmitOrder = btnSubmitOrder;
        this.imgNoProduct = imgNoProduct;
        this.txtNoProduct = txtNoProduct;

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

        final String cgst = cartProduct.get(position).get("cgst");
        final String sgst = cartProduct.get(position).get("sgst");
        final String cess = cartProduct.get(position).get("cess");

        String imageUrl = Constant.PRODUCT_IMAGE_URL + productImage;

        int getStock = Integer.parseInt(productStock);


        if (country.equals("UAE")) {
            holder.txtCgst.setText("VAT" + " " + currency + cgst);
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
            holder.txtSgst.setVisibility(View.GONE);
            holder.txtCess.setVisibility(View.GONE);
        } else {

            if (Double.valueOf(cgst) != 0) {
                holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + cgst);
            } else {
                holder.txtCgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(sgst) != 0) {
                holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + sgst);
            } else {
                holder.txtSgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(cess) != 0) {
                holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + cess);
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

        double priceWithTax = totalPrice + allTax;
        txtTotalPrice.setText(context.getString(R.string.total_price) + currency +" "+ f.format(totalPrice));

        if (country.equals("UAE")) {
            txtTotalCgst.setText("Total Vat :" + " " + currency +" "+ f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
            txtTotalSgst.setVisibility(View.GONE);
            txtTotalCess.setVisibility(View.GONE);
        } else {
            if (Double.valueOf(allCgst) != 0) {
                txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency +" "+ f.format(allCgst));
            } else {
                txtTotalCgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(allSgst) != 0) {
                txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency +" "+ f.format(allSgst));
            } else {
                txtTotalSgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(cess) != 0) {
                txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency +" "+ f.format(allCess));
            } else {
                txtTotalCess.setVisibility(View.GONE);
            }

        }
        if (Double.valueOf(priceWithTax) != 0){
            txtTotalDisc.setText(context.getString(R.string.price_with_tax) + currency +" "+ f.format(priceWithTax));}else{
            txtTotalDisc.setVisibility(View.GONE);
        }



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


        final double getPrice = Double.parseDouble(price) * Integer.parseInt(qty);


        holder.txtItemName.setText(productName);
        holder.txtPrice.setText(currency + f.format(getPrice));
        holder.txtWeight.setText(weight + " " + productWeightUnit);
        holder.txtQtyNumber.setText(qty);

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

                    //for delete cart item dynamically
                    cartProduct.remove(holder.getAdapterPosition());

                    // Notify that item at position has been removed
                    notifyItemRemoved(holder.getAdapterPosition());


                    databaseAccess.open();
                    totalPrice = databaseAccess.getTotalPrice();
                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency + totalPrice);

                    databaseAccess.open();
                    allTax = databaseAccess.getTotalTax();


                    double priceWithTax = totalPrice + allTax;
                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency +" "+ f.format(totalPrice));

                    if (country.equals("UAE")) {
                        txtTotalCgst.setText("Total Vat :" + " " + currency +" "+ f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        txtTotalSgst.setVisibility(View.GONE);
                        txtTotalCess.setVisibility(View.GONE);
                    } else {
                        if (Double.valueOf(allCgst) != 0) {
                            txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency +" "+ f.format(allCgst));
                        } else {
                            txtTotalCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(allSgst) != 0) {
                            txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency +" "+ f.format(allSgst));
                        } else {
                            txtTotalSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(cess) != 0) {
                            txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency +" "+ f.format(allCess));
                        } else {
                            txtTotalCess.setVisibility(View.GONE);
                        }

                    }
                    if (Double.valueOf(priceWithTax) != 0){
                        txtTotalDisc.setText(context.getString(R.string.price_with_tax) + currency +" "+ f.format(priceWithTax));}else{
                        txtTotalDisc.setVisibility(View.GONE);
                    }

                } else {
                    Toasty.error(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }


                databaseAccess.open();
                int itemCount = databaseAccess.getCartItemCount();
                Log.d("itemCount", "" + itemCount);
                if (itemCount <= 0) {
                    txtTotalPrice.setVisibility(View.GONE);
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
                int getQty = Integer.parseInt(qty1);

                if (getQty >= getStock) {
                    Toasty.warning(context, R.string.stock_not_available_please_update_stock, Toast.LENGTH_SHORT).show();
                } else {

                    getQty++;

                    double cost = Double.parseDouble(price) * getQty;


                    holder.txtPrice.setText(currency + f.format(cost));
                    holder.txtQtyNumber.setText(String.valueOf(getQty));


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + getQty);

                    totalPrice = totalPrice + Double.valueOf(price);
                    // txtTotalPrice.setText(context.getString(R.string.total_price) + currency + f.format(totalPrice));


                    databaseAccess.open();
                    allTax = databaseAccess.getTotalTax();


                    double priceWithTax = totalPrice + allTax;
                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency +" "+ f.format(totalPrice));

                    if (country.equals("UAE")) {
                        txtTotalCgst.setText("Total Vat :" + " " + currency +" "+ f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        txtTotalSgst.setVisibility(View.GONE);
                        txtTotalCess.setVisibility(View.GONE);
                    } else {
                        if (Double.valueOf(allCgst) != 0) {
                            txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency +" "+ f.format(allCgst));
                        } else {
                            txtTotalCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(allSgst) != 0) {
                            txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency +" "+ f.format(allSgst));
                        } else {
                            txtTotalSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(cess) != 0) {
                            txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency +" "+ f.format(allCess));
                        } else {
                            txtTotalCess.setVisibility(View.GONE);
                        }

                    }
                    if (Double.valueOf(priceWithTax) != 0){
                        txtTotalDisc.setText(context.getString(R.string.price_with_tax) + currency +" "+ f.format(priceWithTax));}else{
                        txtTotalDisc.setVisibility(View.GONE);
                    }
                }
            }
        });


        holder.txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String qty = holder.txtQtyNumber.getText().toString();
                int getQty = Integer.parseInt(qty);


                if (getQty >= 2) {
                    getQty--;

                    double cost = Double.parseDouble(price) * getQty;

                    holder.txtPrice.setText(currency + f.format(cost));
                    holder.txtQtyNumber.setText(String.valueOf(getQty));


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + getQty);

                    totalPrice = totalPrice - Double.valueOf(price);
                    // txtTotalPrice.setText(context.getString(R.string.total_price) + currency + f.format(totalPrice));

                    databaseAccess.open();
                    allTax = databaseAccess.getTotalTax();


                    double priceWithTax = totalPrice + allTax;
                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency + f.format(totalPrice) + "\nTotal Tax: " + currency + f.format(allTax) + "\nPrice with Tax: " + currency + f.format(priceWithTax));


                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return cartProduct.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtPrice, txtWeight, txtQtyNumber, txtPlus, txtMinus, txtCgst, txtSgst, txtCess;
        ImageView imgProduct, imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.txt_item_name);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtWeight = itemView.findViewById(R.id.txt_weight);
            txtQtyNumber = itemView.findViewById(R.id.txt_number);
            imgProduct = itemView.findViewById(R.id.cart_product_image);
            imgDelete = itemView.findViewById(R.id.img_delete);
            txtMinus = itemView.findViewById(R.id.txt_minus);
            txtPlus = itemView.findViewById(R.id.txt_plus);

            txtCgst = itemView.findViewById(R.id.txt_cgst);
            txtSgst = itemView.findViewById(R.id.txt_sgst);
            txtCess = itemView.findViewById(R.id.txt_cess);

        }


    }


}
