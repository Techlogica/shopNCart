package com.app.shopncart.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopncart.Constant;
import com.app.shopncart.R;
import com.app.shopncart.database.DatabaseAccess;
import com.app.shopncart.utils.InputFilterMinMax;
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
    double discount = 0;
    double priceWithDisc = 0;

    TextView txtTotalPrice, txtTotalCgst, txtTotalSgst, txtTotalCess, txtFinalTotal,txtTotalDiscount;

    public static Double totalPrice, allTax, allCgst, allSgst, allCess,allDiscount=0.0,finalTotal=0.0;
    public static Double  allDiscountedCgst=0.0, allDiscountedSgst=0.0, allDiscountedCess=0.0, allDiscountedTax=0.0;
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

        String imageUrl = Constant.PRODUCT_IMAGE_URL + productImage;

        int getStock = Integer.parseInt(productStock);

        if (country.equals("UAE")) {
            holder.txtCgst.setText("VAT" + " " + currency + f.format(Double.parseDouble(cgst)));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
            holder.txtSgst.setVisibility(View.GONE);
            holder.txtCess.setVisibility(View.GONE);
        } else {

            if (Double.valueOf(cgst) != 0) {
                holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + f.format(Double.parseDouble(cgst)));
            } else {
                holder.txtCgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(sgst) != 0) {
                holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + f.format(Double.parseDouble(sgst)));
            } else {
                holder.txtSgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(cess) != 0) {
                holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + f.format(Double.parseDouble(cess)));
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

        allDiscount=0.0;
        //getting total discount from db
        databaseAccess.open();
        allDiscount = databaseAccess.getTotalProductDiscount();

       /* databaseAccess.open();
        finalTotal= databaseAccess.getFinalTotalPrice();*/

        finalTotal=(totalPrice-allDiscount)+allTax;
        Log.e("Price-dscnt+tax=ft","---"+totalPrice+"-"+allDiscount+"+"+allTax+"="+finalTotal);





        txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

        //setting total discount
        txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + " " + currency + " " + f.format(allDiscount));

        txtFinalTotal.setText(context.getString(R.string.price_with_tax) + currency + " " + f.format(finalTotal));


        if (country.equals("UAE")) {
            txtTotalCgst.setText("Total Vat :" + " " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
            txtTotalSgst.setVisibility(View.GONE);
            txtTotalCess.setVisibility(View.GONE);
        } else {
            if (Double.valueOf(allCgst) != 0) {
                txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency + " " + f.format(allCgst));
            } else {
                txtTotalCgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(allSgst) != 0) {
                txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency + " " + f.format(allSgst));
            } else {
                txtTotalSgst.setVisibility(View.GONE);
            }

            if (Double.valueOf(cess) != 0) {
                txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency + " " + f.format(allCess));
            } else {
                txtTotalCess.setVisibility(View.GONE);
            }

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
        if(!productDiscount.equals("0")){
        holder.edtDisc.setText(productDiscount);}

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

                    databaseAccess.open();
                    allTax = databaseAccess.getTotalTax();
                    databaseAccess.open();
                    allCgst = databaseAccess.getTotalCGST();
                    databaseAccess.open();
                    allSgst = databaseAccess.getTotalSGST();
                    databaseAccess.open();
                    allCess = databaseAccess.getTotalCESS();

                    allDiscount=0.0;
                    //getting total discount from db
                    databaseAccess.open();
                    allDiscount = databaseAccess.getTotalProductDiscount();

                    double priceWithTax =(totalPrice-allDiscount) + allTax;
                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

                    //setting total discount
                    txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + " " + currency + " " + f.format(allDiscount));

                    if (country.equals("UAE")) {
                        txtTotalCgst.setText("Total Vat :" + " " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        txtTotalSgst.setVisibility(View.GONE);
                        txtTotalCess.setVisibility(View.GONE);
                    } else {
                        if (Double.valueOf(allCgst) != 0) {
                            txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency + " " + f.format(allCgst));
                        } else {
                            txtTotalCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(allSgst) != 0) {
                            txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency + " " + f.format(allSgst));
                        } else {
                            txtTotalSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(cess) != 0) {
                            txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency + " " + f.format(allCess));
                        } else {
                            txtTotalCess.setVisibility(View.GONE);
                        }

                    }
                    if (Double.valueOf(priceWithTax) != 0) {
                        txtFinalTotal.setText(context.getString(R.string.price_with_tax) + currency + " " + f.format(priceWithTax));
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
                int getQty = Integer.parseInt(qty1);

                if (getQty >= getStock) {
                    Toasty.warning(context, R.string.stock_not_available_please_update_stock, Toast.LENGTH_SHORT).show();
                } else {

                    getQty++;

                    double cost = Double.parseDouble(price) * getQty;


                    double itemCgst=cost*Double.valueOf(cgstPercent)/100;
                    double itemSgst=cost*Double.valueOf(sgstPercent)/100;
                    double itemCess=cost*Double.valueOf(cessPercent)/100;


                    holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(cost)), new InputFilter.LengthFilter(5)});


                    holder.txtPrice.setText(currency + f.format(cost));
                    holder.txtQtyNumber.setText(String.valueOf(getQty));


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + getQty);

                    totalPrice = totalPrice + Double.valueOf(price);

                    double lineTotalTax = itemCgst + itemSgst+itemCess;
                    double lineTotal = Double.valueOf(cost) + lineTotalTax;

                    databaseAccess.open();
                    databaseAccess.updateLineTotal(cart_id, "" + lineTotal);


                    holder.edtDisc.getText().clear();
                    discount=0;
                    databaseAccess.open();
                    databaseAccess.updateProductDiscount(cart_id, "" + discount);

                    holder.edtDisc.requestFocus();

                    allDiscount=0.0;
                    databaseAccess.open();
                    allDiscount = databaseAccess.getTotalProductDiscount();
                    //setting total discount
                    txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + " " + currency + " " + f.format(allDiscount));

                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

                    if (country.equals("UAE")) {
                        holder.txtCgst.setText("VAT" + " " + currency + f.format(itemCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        holder.txtSgst.setVisibility(View.GONE);
                        holder.txtCess.setVisibility(View.GONE);
                    } else {

                        if (Double.valueOf(itemCgst) != 0) {
                            holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + f.format(itemCgst));
                        } else {
                            holder.txtCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(itemSgst) != 0) {
                            holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + f.format(itemSgst));
                        } else {
                            holder.txtSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(itemCess) != 0) {
                            holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + f.format(itemCess));
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
                        txtTotalCgst.setText("Total Vat :" + " " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        txtTotalSgst.setVisibility(View.GONE);
                        txtTotalCess.setVisibility(View.GONE);
                    } else {
                        if (Double.valueOf(allCgst) != 0) {
                            txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency + " " + f.format(allCgst));
                        } else {
                            txtTotalCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(allSgst) != 0) {
                            txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency + " " + f.format(allSgst));
                        } else {
                            txtTotalSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(cess) != 0) {
                            txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency + " " + f.format(allCess));
                        } else {
                            txtTotalCess.setVisibility(View.GONE);
                        }

                    }

                    databaseAccess.open();
                    allDiscountedTax = databaseAccess.getTotalTax();
                    finalTotal= (totalPrice-allDiscount)+allDiscountedTax;

                    if (Double.valueOf(finalTotal) != 0) {
                        txtFinalTotal.setText(context.getString(R.string.price_with_tax) + currency + " " + f.format(finalTotal));
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
                int getQty = Integer.parseInt(qty);


                if (getQty >= 2) {
                    getQty--;

                    double cost = Double.parseDouble(price) * getQty;

                    double itemCgst=cost*Double.valueOf(cgstPercent)/100;
                    double itemSgst=cost*Double.valueOf(sgstPercent)/100;
                    double itemCess=cost*Double.valueOf(cessPercent)/100;

                    holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(cost)), new InputFilter.LengthFilter(5)});

                    holder.txtPrice.setText(currency + f.format(cost));
                    holder.txtQtyNumber.setText(String.valueOf(getQty));


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + getQty);

                    totalPrice = totalPrice - Double.valueOf(price);


                    holder.edtDisc.getText().clear();
                    discount=0;
                    databaseAccess.open();
                    databaseAccess.updateProductDiscount(cart_id, "" + discount);

                    holder.edtDisc.requestFocus();

                    allDiscount=0.0;
                    databaseAccess.open();
                    allDiscount = databaseAccess.getTotalProductDiscount();
                    //setting total discount
                    txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + " " + currency + " " + f.format(allDiscount));

                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency + " " + f.format(totalPrice));

                    if (country.equals("UAE")) {
                        holder.txtCgst.setText("VAT" + " " + currency + f.format(itemCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        holder.txtSgst.setVisibility(View.GONE);
                        holder.txtCess.setVisibility(View.GONE);
                    } else {

                        if (Double.valueOf(itemCgst) != 0) {
                            holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + f.format(itemCgst));
                        } else {
                            holder.txtCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(itemSgst) != 0) {
                            holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + f.format(itemSgst));
                        } else {
                            holder.txtSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(itemCess) != 0) {
                            holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + f.format(itemCess));
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
                        txtTotalCgst.setText("Total Vat :" + " " + currency + " " + f.format(allCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                        txtTotalSgst.setVisibility(View.GONE);
                        txtTotalCess.setVisibility(View.GONE);
                    } else {
                        if (Double.valueOf(allCgst) != 0) {
                            txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency + " " + f.format(allCgst));
                        } else {
                            txtTotalCgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(allSgst) != 0) {
                            txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency + " " + f.format(allSgst));
                        } else {
                            txtTotalSgst.setVisibility(View.GONE);
                        }

                        if (Double.valueOf(cess) != 0) {
                            txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency + " " + f.format(allCess));
                        } else {
                            txtTotalCess.setVisibility(View.GONE);
                        }

                    }

                    double lineTotalTax = itemCgst + itemSgst+itemCess;
                    double lineTotal = Double.valueOf(cost) + lineTotalTax;
                    databaseAccess.open();
                    databaseAccess.updateLineTotal(cart_id, "" + lineTotal);
                    databaseAccess.open();
                    allDiscountedTax = databaseAccess.getTotalTax();
                    finalTotal= (totalPrice-allDiscount)+allDiscountedTax;

                    if (finalTotal != 0) {
                        txtFinalTotal.setText(context.getString(R.string.price_with_tax) + currency + " " + f.format(finalTotal));
                    } else {
                        txtFinalTotal.setVisibility(View.GONE);
                    }


                }



            }

        });



        holder.edtDisc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                final String dis = holder.edtDisc.getText().toString().trim();
                final String qty = holder.txtQtyNumber.getText().toString().trim();

                double cost = Double.parseDouble(price) * Integer.parseInt(qty);

                holder.edtDisc.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(cost)), new InputFilter.LengthFilter(5)});


//              calculating tax to the discounted amount for each item
                if (!dis.equals("")) {
                    discount = Double.parseDouble(dis);
                } else {
                    discount = 0.0;
                }
                priceWithDisc = cost - discount;
                Log.e("price","------"+price+"-");
                Log.e("qty","------"+qty+"-");
                Log.e("cost-discount=total","------"+cost+"-"+dis+"="+priceWithDisc);
                double getCgst=0,getSgst=0,getCess=0;
                if(cgstPercent!=null&&!cgstPercent.equals("")){
                    getCgst=Double.parseDouble(cgstPercent);}
                if(sgstPercent!=null&&!sgstPercent.equals("")){
                    getSgst=Double.parseDouble(sgstPercent);}
                if(cessPercent!=null&&!cessPercent.equals("")){
                    getCess=Double.parseDouble(cessPercent);}
                double cgstAmount=(priceWithDisc*getCgst)/100;
                double sgstAmount=(priceWithDisc*getSgst)/100;
                double cessAmount=(priceWithDisc*getCess)/100;
                Log.e("price*vat=vatline","---"+priceWithDisc+"*"+getCgst+"="+cgstAmount);


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
                    holder.txtCgst.setText("VAT" + " " + currency + cgstAmount);
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                    holder.txtSgst.setVisibility(View.GONE);
                    holder.txtCess.setVisibility(View.GONE);
                } else {

                    if (Double.valueOf(cgst) != 0) {
                        holder.txtCgst.setText(context.getString(R.string.cgst) + " " + currency + cgstAmount);
                    } else {
                        holder.txtCgst.setVisibility(View.GONE);
                    }

                    if (Double.valueOf(sgst) != 0) {
                        holder.txtSgst.setText(context.getString(R.string.sgst) + " " + currency + sgstAmount);
                    } else {
                        holder.txtSgst.setVisibility(View.GONE);
                    }

                    if (Double.valueOf(cess) != 0) {
                        holder.txtCess.setText(context.getString(R.string.cess) + " " + currency + cessAmount);
                    } else {
                        holder.txtCess.setVisibility(View.GONE);
                    }

                }

                allDiscount=0.0;
                //getting total discount from db
                databaseAccess.open();
                allDiscount = databaseAccess.getTotalProductDiscount();

                //setting total discount
                txtTotalDiscount.setText(context.getString(R.string.total_product_discount) + " " + currency + " " + f.format(allDiscount));

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
                    txtTotalCgst.setText("Total Vat :" + " " + currency + " " + f.format(allDiscountedCgst));
//            holder.txtSgst.setText("VAT 2" + " " + currency + sgst);
                    txtTotalSgst.setVisibility(View.GONE);
                    txtTotalCess.setVisibility(View.GONE);
                } else {
                    if (Double.valueOf(allDiscountedCgst) != 0) {
                        txtTotalCgst.setVisibility(View.VISIBLE);
                        txtTotalCgst.setText(context.getString(R.string.total_cgst_cart) + " " + currency + " " + f.format(allDiscountedCgst));
                    } else {
                        txtTotalCgst.setVisibility(View.GONE);
                    }

                    if (Double.valueOf(allDiscountedSgst) != 0) {
                        txtTotalSgst.setVisibility(View.VISIBLE);
                        txtTotalSgst.setText(context.getString(R.string.total_sgst_cart) + " " + currency + " " + f.format(allDiscountedSgst));
                    } else {
                        txtTotalSgst.setVisibility(View.GONE);
                    }

                    if (Double.valueOf(allDiscountedCess) != 0) {
                        txtTotalCess.setVisibility(View.VISIBLE);
                        txtTotalCess.setText(context.getString(R.string.total_cess_cart) + " " + currency + " " + f.format(allDiscountedCess));
                    } else {
                        txtTotalCess.setVisibility(View.GONE);
                    }

                }


                double lineTotalTax = cgstAmount + sgstAmount+cessAmount;
                double lineTotal = priceWithDisc + lineTotalTax;
                Log.e("Price*TotalTax=lineT","---"+priceWithDisc+"+"+lineTotalTax+"="+lineTotal);

                databaseAccess.open();
                databaseAccess.updateLineTotal(cart_id, "" + lineTotal);

//                databaseAccess.open();
//                finalTotal= databaseAccess.getFinalTotalPrice();
                finalTotal= (totalPrice-allDiscount)+allDiscountedTax;

                if (finalTotal != 0) {
                    txtFinalTotal.setVisibility(View.VISIBLE);
                    txtFinalTotal.setText(context.getString(R.string.price_with_tax) + currency + " " + f.format(finalTotal));
                } else {
                    txtFinalTotal.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        EditText edtDisc;

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
            edtDisc = itemView.findViewById(R.id.edt_disc);

            txtCgst = itemView.findViewById(R.id.txt_cgst);
            txtSgst = itemView.findViewById(R.id.txt_sgst);
            txtCess = itemView.findViewById(R.id.txt_cess);

        }


    }


}
