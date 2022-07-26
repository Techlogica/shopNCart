package com.tids.shopncart.product;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.model.Category;
import com.tids.shopncart.model.Product;
import com.tids.shopncart.model.Suppliers;
import com.tids.shopncart.model.WeightUnit;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;
import com.tids.shopncart.utils.InputFilterMinMax;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends BaseActivity {


    ProgressDialog loading;
    DatabaseAccess db;

    public static EditText etxtProductCode;
    EditText etxtProductName, etxtProductStock, etxtCGST, etxtSGST, etxtCESS, etxtProductCategory, etxtProductDescription, etxtProductSellPrice, etxtProductSupplier, etxtProdcutWeightUnit, etxtProductWeight, etxtProductCostPrice;
    TextView txtAddProdcut, txtChooseImage, textViewcgst, textViewsgst, textViewcess;
    ImageView imgProduct, imgScanCode, backBtn;
    List<Category> productCategory;
    List<Suppliers> productSuppliers;
    List<WeightUnit> weightUnits;
    ArrayAdapter<String> categoryAdapter, supplierAdapter, weightUnitAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;
    String country = "";
    CheckBox checkBox;
    String isEditable = "";
    String supplierName, weightUnitName, categoryName;
    Toolbar toolbar;
    String selectedCategoryID, selectedSupplierID = "0", selectedWeightUnitID, mediaPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        db = DatabaseAccess.getInstance(this);

//        getSupportActionBar().setHomeButtonEnabled(true); //for back button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
//        getSupportActionBar().setTitle(R.string.add_product);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.menu_back);
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        etxtProductName = findViewById(R.id.etxt_product_name);
        etxtProductCode = findViewById(R.id.etxt_product_code);
        etxtProductCategory = findViewById(R.id.etxt_product_category);
        etxtProductDescription = findViewById(R.id.etxt_product_description);
        etxtProductSellPrice = findViewById(R.id.etxt_product_sell_price);
        etxtProductCostPrice = findViewById(R.id.etxt_product_cost_price);
        etxtProductSupplier = findViewById(R.id.etxt_supplier);
        etxtProdcutWeightUnit = findViewById(R.id.etxt_product_weight_unit);
        etxtProductWeight = findViewById(R.id.etxt_product_weight);
        etxtProductStock = findViewById(R.id.etxt_product_stock);
        textViewcgst = findViewById(R.id.textView);
        textViewsgst = findViewById(R.id.textView2);
        textViewcess = findViewById(R.id.textView3);
        checkBox = findViewById(R.id.checkBox);
        etxtCGST = findViewById(R.id.etxt_cgst);
        etxtSGST = findViewById(R.id.etxt_sgst);
        etxtCESS = findViewById(R.id.etxt_cess);

        txtAddProdcut = findViewById(R.id.txt_add_product);
        imgProduct = findViewById(R.id.image_product);
        imgScanCode = findViewById(R.id.img_scan_code);
        txtChooseImage = findViewById(R.id.txt_choose_image);
        etxtProductWeight.setFilters(new InputFilter[]{new InputFilterMinMax("0.1","1000000000"), new InputFilter.LengthFilter(30)});


        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        String staffId = sp.getString(Constant.SP_STAFF_ID, "");
        country = sp.getString(Constant.SP_SHOP_COUNTRY, "");

        if (country.equals("UAE")) {
            textViewcgst.setText("VAT");
            etxtCGST.setHint("VAT");
            textViewsgst.setVisibility(View.GONE);
            etxtSGST.setVisibility(View.GONE);
            textViewcess.setVisibility(View.GONE);
            etxtCESS.setVisibility(View.GONE);
        }


        getProductCategory(shopID, ownerId,staffId);
        getProductSuppliers(shopID, ownerId,staffId);
        getWeightUnits();

        imgScanCode.setOnClickListener(v -> {
            Intent intent = new Intent(AddProductActivity.this, ScannerViewActivity.class);
            startActivity(intent);
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEditable = "yes";
                } else {
                    isEditable = "no";
                }
            }
        });

        txtChooseImage.setOnClickListener(v -> {

            Intent intent = new Intent(AddProductActivity.this, ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
            startActivityForResult(intent, 1213);
        });

        imgProduct.setOnClickListener(v -> {

            Intent intent = new Intent(AddProductActivity.this, ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
            startActivityForResult(intent, 1213);
        });


        etxtProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_list_item_1);
                categoryAdapter.addAll(categoryNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText(R.string.product_category);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(categoryAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        categoryAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = categoryAdapter.getItem(position);

                        String categoryId = "0";
                        etxtProductCategory.setText(selectedItem);


                        for (int i = 0; i < categoryNames.size(); i++) {
                            if (categoryNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                categoryId = productCategory.get(i).getProductCategoryId();
                                categoryName = productCategory.get(i).getProductCategoryName();

                            }
                        }

                        selectedCategoryID = categoryId;

                    }
                });
            }
        });


        etxtProductSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supplierAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_list_item_1);
                supplierAdapter.addAll(supplierNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);
                dialogTitle.setText(R.string.suppliers);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(supplierAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        supplierAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = supplierAdapter.getItem(position);

                        String supplierId = "0";
                        etxtProductSupplier.setText(selectedItem);


                        for (int i = 0; i < supplierNames.size(); i++) {
                            if (supplierNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                supplierId = productSuppliers.get(i).getSuppliersId();
                                supplierName = productSuppliers.get(i).getSuppliersName();

                            }
                        }


                        selectedSupplierID = supplierId;

                    }
                });
            }
        });


        etxtProdcutWeightUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightUnitAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_list_item_1);
                weightUnitAdapter.addAll(weightUnitNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText(R.string.product_weight_unit);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(weightUnitAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        weightUnitAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = weightUnitAdapter.getItem(position);

                        String weightUnitId = "0";
                        etxtProdcutWeightUnit.setText(selectedItem);


                        for (int i = 0; i < weightUnitNames.size(); i++) {
                            if (weightUnitNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                weightUnitId = weightUnits.get(i).getWeightUnitId();
                                weightUnitName = weightUnits.get(i).getWeightUnitName();

                            }
                        }
                        selectedWeightUnitID = weightUnitId;
                    }
                });
            }
        });


        txtAddProdcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String productName = etxtProductName.getText().toString();
                String productCode = etxtProductCode.getText().toString();
                String productCategoryName = etxtProductCategory.getText().toString();
                String productCategoryId = selectedCategoryID;
                String productDescription = etxtProductDescription.getText().toString();

                String productStock = etxtProductStock.getText().toString();

                String productSellPrice = etxtProductSellPrice.getText().toString();
                String productCostPrice = etxtProductCostPrice.getText().toString();

                String productSupplierName = etxtProductSupplier.getText().toString();
                String productSupplierId = selectedSupplierID;
                String productWeightUnitName = etxtProdcutWeightUnit.getText().toString();
                String productWeightUnitId = selectedWeightUnitID;
                String productWeight = etxtProductWeight.getText().toString();
                String cgst = etxtCGST.getText().toString().trim();
                String sgst = etxtSGST.getText().toString().trim();
                String cess = etxtCESS.getText().toString().trim();
                if(cgst.equals("")){
                    cgst="0";
                }
                if(sgst.equals("")){
                    sgst="0";
                }
                if(cess.equals("")){
                    cess="0";
                }


                if (productName.isEmpty()) {
                    etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
                    etxtProductName.requestFocus();
                } else if (productCode.isEmpty()) {
                    etxtProductCode.setError(getString(R.string.product_code_cannot_be_empty));
                    etxtProductCode.requestFocus();
                } else if (productCategoryName.isEmpty() || productCategoryId.isEmpty()) {
                    etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
                    etxtProductCategory.requestFocus();
                } else if (productSellPrice.isEmpty()) {
                    etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
                    etxtProductSellPrice.requestFocus();
                } else if (productCostPrice.isEmpty()) {
                    etxtProductCostPrice.setError(getString(R.string.product_cost_price_cannot_be_empty));
                    etxtProductCostPrice.requestFocus();
                } else if (productWeightUnitName.isEmpty() || productWeight.isEmpty()) {
                    etxtProductWeight.setError(getString(R.string.product_weight_cannot_be_empty));
                    etxtProductWeight.requestFocus();
                } else {

                    addProduct(productName, productCode, productCategoryId, productDescription, productCostPrice,productSellPrice, productWeight, productWeightUnitId, productSupplierId, productStock, cgst, sgst, cess, shopID, ownerId,staffId);


                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (requestCode == 1213 && resultCode == RESULT_OK && null != data) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);
                imgProduct.setImageBitmap(selectedImage);


            }


        } catch (Exception e) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }


//    //for back button
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; goto parent activity.
//            this.finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    // Uploading Image/Video
    private void addProduct(String productName, String productCode, String productCategoryId, String productDescription, String productCostPrice, String productSellPrice, String productWeight, String productWeightUnitId, String productSupplierId, String productStock, String cgst, String sgst, String cess, String shopId, String ownerId, String staffId) {

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();

        // Map is used to multipart the file using okhttp3.RequestBody
        if (mediaPath.isEmpty()) {
            Toasty.warning(this, R.string.choose_product_image, Toast.LENGTH_SHORT).show();
            loading.dismiss();

        } else {

            File file = new File(mediaPath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), productName);
            RequestBody code = RequestBody.create(MediaType.parse("text/plain"), productCode);
            RequestBody category = RequestBody.create(MediaType.parse("text/plain"), productCategoryId);
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), productDescription);
            RequestBody costPrice = RequestBody.create(MediaType.parse("text/plain"), productCostPrice);
            RequestBody sellPrice = RequestBody.create(MediaType.parse("text/plain"), productSellPrice);
            RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), productWeight);
            RequestBody weightUnitId = RequestBody.create(MediaType.parse("text/plain"), productWeightUnitId);
            RequestBody supplierId = RequestBody.create(MediaType.parse("text/plain"), productSupplierId);
            RequestBody stock = RequestBody.create(MediaType.parse("text/plain"), productStock);
            RequestBody getShopId = RequestBody.create(MediaType.parse("text/plain"), shopId);
            RequestBody getOwnerId = RequestBody.create(MediaType.parse("text/plain"), ownerId);
            RequestBody getStaffId = RequestBody.create(MediaType.parse("text/plain"), staffId);

            RequestBody getCGST = RequestBody.create(MediaType.parse("text/plain"), cgst);
            RequestBody getSGST = RequestBody.create(MediaType.parse("text/plain"), sgst);
            RequestBody getCESS = RequestBody.create(MediaType.parse("text/plain"), cess);
            RequestBody getEditable = RequestBody.create(MediaType.parse("text/plain"), isEditable);


            ApiInterface getResponse = ApiClient.getApiClient().create(ApiInterface.class);
            Call<Product> call = getResponse.addProduct(fileToUpload, filename, name, code, category, description,costPrice, sellPrice, weight, weightUnitId, supplierId, stock, getCGST, getSGST, getCESS, getShopId, getOwnerId,getStaffId,getEditable);
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        loading.dismiss();
                        String value = response.body().getValue();
                        String productId = "";
                        if (response.body().getProductId() != null && !response.body().getProductId().equals("")) {
                            productId = response.body().getProductId();
                        }
                        String productImage = "";
                        if (response.body().getProductImage() != null && !response.body().getProductImage().equals("")) {
                            productImage = response.body().getProductImage();
                        }
                        if (value.equals(Constant.KEY_SUCCESS)) {
                            Toasty.success(getApplicationContext(), R.string.product_successfully_added, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            db.open();
                            db.addProducts(productId, productName, productCode, productCategoryId, productSellPrice, productCostPrice,
                                    productWeight, weightUnitName, supplierName, productImage, productStock, cgst, sgst, cess, "", "", "", isEditable, categoryName);
                        } else if (value.equals(Constant.KEY_FAILURE)) {

                            loading.dismiss();
                            String message = response.body().getMessage();
                            Toasty.error(AddProductActivity.this,message, Toast.LENGTH_SHORT).show();


                        } else {
                            loading.dismiss();
                            Toasty.error(AddProductActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        loading.dismiss();
                        Log.d("Error", response.errorBody().toString());
                    }

                }


                @Override
                public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                    loading.dismiss();
                    Log.d("Error! ", t.toString());
                    Toasty.error(AddProductActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void getProductCategory(String shopId, String ownerId, String staffId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Category>> call;


        call = apiInterface.getCategory(shopId, ownerId,staffId);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    productCategory = response.body();

                    categoryNames = new ArrayList<>();

                    for (int i = 0; i < productCategory.size(); i++) {

                        categoryNames.add(productCategory.get(i).getProductCategoryName());

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }


    public void getProductSuppliers(String shopId, String ownerId, String staffId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Suppliers>> call;


        call = apiInterface.getSuppliers("", shopId, ownerId,staffId);

        call.enqueue(new Callback<List<Suppliers>>() {
            @Override
            public void onResponse(@NonNull Call<List<Suppliers>> call, @NonNull Response<List<Suppliers>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    productSuppliers = response.body();

                    supplierNames = new ArrayList<>();

                    for (int i = 0; i < productSuppliers.size(); i++) {

                        supplierNames.add(productSuppliers.get(i).getSuppliersName());

                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Suppliers>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }


    public void getWeightUnits() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<WeightUnit>> call;


        call = apiInterface.getWeightUnits("");

        call.enqueue(new Callback<List<WeightUnit>>() {
            @Override
            public void onResponse(@NonNull Call<List<WeightUnit>> call, @NonNull Response<List<WeightUnit>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    weightUnits = response.body();

                    weightUnitNames = new ArrayList<>();

                    for (int i = 0; i < weightUnits.size(); i++) {

                        weightUnitNames.add(weightUnits.get(i).getWeightUnitName());

                    }


                }


            }

            @Override
            public void onFailure(@NonNull Call<List<WeightUnit>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }


}



