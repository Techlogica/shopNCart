package com.tids.shopncart.settings.sync;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tids.shopncart.Constant;
import com.tids.shopncart.HomeActivity;
import com.tids.shopncart.R;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.Category;
import com.tids.shopncart.model.Device;
import com.tids.shopncart.model.Product;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tids.shopncart.utils.Utils.isNetworkAvailable;


public class SyncActivity extends AppCompatActivity {


    String TAG = getClass().getSimpleName();
    private PrefManager pref;

    DatabaseAccess db;
    Boolean isProductsSynced = false;
    Boolean isCategorySynced = false;
    Boolean isDeviceSynced = false;
    List<Product> productsList = new ArrayList<>();
    SaveProductsTask saveProductTask = null;
    SaveCategoryTask saveCategoryTask = null;
    ProgressBar progressBar1, progressBar;
    LinearLayout layoutAction;
    TextView txtProducts, txtCategory, textPercent, txtDevice;
    Button btnDone;
    String shopID = "";
    String ownerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        pref = new PrefManager(this);
        pref.setSynced(false);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");

        if (!pref.isSynced()) {
            setContentView(R.layout.activity_sync);
            db = DatabaseAccess.getInstance(this);

            initViews();
            if (isNetworkAvailable(this)) {
                getProductsData("", shopID, ownerId);
                getProductCategory(shopID, ownerId);
                getDeviceCount(shopID);

            } else {
                Toasty.error(this, getResources().getString(R.string.no_network_connection));
            }
        } else {
            gotoHome();
        }

        txtProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(SyncActivity.this))
                    getProductsData("", shopID, ownerId);
                else
                    Toasty.error(SyncActivity.this, getResources().getString(R.string.no_network_connection));
            }
        });

        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(SyncActivity.this))
                    getProductCategory(shopID, ownerId);
                else
                    Toasty.error(SyncActivity.this, getResources().getString(R.string.no_network_connection));
            }
        });
        txtDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(SyncActivity.this))
                    getDeviceCount(shopID);

                else
                    Toasty.error(SyncActivity.this, getResources().getString(R.string.no_network_connection));
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoHome();
            }
        });

    }

    private void initViews() {

        progressBar1 = findViewById(R.id.progressBar1);
        progressBar = findViewById(R.id.progressBar);
        layoutAction = findViewById(R.id.layout_action);

        txtProducts = findViewById(R.id.txt_products);
        txtCategory = findViewById(R.id.txt_categories);
        textPercent = findViewById(R.id.text_percent);
        txtDevice = findViewById(R.id.txt_device);
        btnDone = findViewById(R.id.btn_done);

        toggleTextView(txtProducts, isProductsSynced, false);
        toggleTextView(txtCategory, isCategorySynced, false);
        toggleTextView(txtDevice, isDeviceSynced, false);

        validateAll();
    }

    private void validateAll() {
        int progress = 0;
        int count = 3;//todo change appropraite
        if (isProductsSynced)
            progress++;
        if (isCategorySynced)
            progress++;
        if (isDeviceSynced)
            progress++;

        setProgressBar(count, progress);
        if (isProductsSynced && isCategorySynced && isDeviceSynced) {
            pref.setSynced(true);
            layoutAction.setVisibility(View.VISIBLE);
        } else {
            pref.setSynced(false);
            layoutAction.setVisibility(View.GONE);
        }
    }

    private void toggleTextView(TextView view, Boolean isSynced, Boolean isDoing) {
        if (isDoing) {
            view.setText(R.string.syncing);
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_button_blue_rounded));
            view.setTextColor(ContextCompat.getColor(this, R.color.white));
            view.setCompoundDrawables(null, null, null, null);
            view.setEnabled(false);
            progressBar1.setVisibility(View.VISIBLE);
        } else {
            if (isSynced) {
                view.setText(R.string.synced);
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                view.setTextColor(ContextCompat.getColor(this, R.color.greenAccent400));
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0);
                view.setEnabled(false);
            } else {
                view.setText(R.string.sync);
                view.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_button_blue_rounded));
                view.setTextColor(ContextCompat.getColor(this, R.color.white));
                view.setCompoundDrawables(null, null, null, null);
                view.setEnabled(true);
            }
            progressBar1.setVisibility(View.INVISIBLE);
        }
    }

    private void setProgressBar(int max, int progress) {
        progressBar.setMax(max);
        progressBar.setProgress(progress);
        int percent = (int) (((double) progress / max) * 100);
        textPercent.setText(String.format(Locale.ENGLISH, "%d%%", percent));
    }


    private void gotoHome() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);// go to home page
        finish();
    }


    public void getProductsData(String searchText, String shopId, String ownerId) {
        toggleTextView(txtProducts, isProductsSynced, true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Product>> call;
        call = apiInterface.getProducts(searchText, shopId, ownerId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {

                toggleTextView(txtProducts, isProductsSynced, false);
                if (response.isSuccessful() && response.body() != null) {
                    productsList = response.body();

                    saveProductTask = new SaveProductsTask(productsList, SyncActivity.this);
                    saveProductTask.execute();

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                toggleTextView(txtProducts, isProductsSynced, false);
                Toast.makeText(SyncActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }

    public void getProductCategory(String shopId, String ownerId) {
        toggleTextView(txtCategory, isCategorySynced, true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Category>> call;


        call = apiInterface.getCategory(shopId, ownerId);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {

                toggleTextView(txtCategory, isCategorySynced, false);

                if (response.isSuccessful() && response.body() != null) {


                    List<Category> productCategory;
                    productCategory = response.body();
                    saveCategoryTask = new SaveCategoryTask(productCategory, SyncActivity.this);
                    saveCategoryTask.execute();

                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                toggleTextView(txtCategory, isCategorySynced, false);
                Toast.makeText(SyncActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }

    public void getDeviceCount(String shopId) {
        toggleTextView(txtDevice, isDeviceSynced, true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Device> call;

        call = apiInterface.getDeviceCount(shopId);

        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(@NonNull Call<Device> call, @NonNull Response<Device> response) {
                isDeviceSynced=true;

                toggleTextView(txtDevice, isDeviceSynced, false);

                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();
                    String count = response.body().getDeviceCount();
                    if (count != null && !count.equals("")) {
                        pref.setKeyDevice(count);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Device> call, @NonNull Throwable t) {
                toggleTextView(txtDevice, isDeviceSynced, false);
                Toast.makeText(SyncActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }

    // show dialog to confirm exit
    private void showCancelConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setMessage("Data sync will be cancelled.")
//                .setCancelable(false)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!pref.isSynced()) {
//                            deleteFcmId();
                        }
                        finish();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    //back button press
    @Override
    public void onBackPressed() {
        showCancelConfirmDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        if (saveProductTask != null)
            saveProductTask.cancel(true);
        if (saveCategoryTask != null)
            saveCategoryTask.cancel(true);
        super.onDestroy();
    }


    class SaveProductsTask extends AsyncTask<Void, Integer, Void> {
        Context context;
        List<Product> dataArray;

        SaveProductsTask(List<Product> productArray, Context context) {
            this.context = context;
            this.dataArray = productArray;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            toggleTextView(txtProducts, isProductsSynced, true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /* This is just a code that delays the thread execution */
            try {
                // for fast insertion
                if (dataArray != null) {

                    db.open();
                    db.clearProducts();

                    for (int i = 0; i < dataArray.size(); i++) {
                        db.open();
                        db.addProducts(dataArray.get(i).getProductId(), dataArray.get(i).getProductName(), dataArray.get(i).getProduct_code(),
                                dataArray.get(i).getProductCategoryId(), dataArray.get(i).getProductSellPrice(), dataArray.get(i).getProductCostPrice(), dataArray.get(i).getProductWeight(),
                                dataArray.get(i).getProductWeightUnit(), dataArray.get(i).getProductSupplierName(), dataArray.get(i).getProductImage(), dataArray.get(i).getProductStock(),
                                dataArray.get(i).getCgst(), dataArray.get(i).getSgst(), dataArray.get(i).getCess(), dataArray.get(i).getTotal_order_price(), dataArray.get(i).getTotal_today_tax(),
                                dataArray.get(i).getTotal_today_discount(), dataArray.get(i).getEditable(), dataArray.get(i).getProductCategoryName());

                    }
                    isProductsSynced = true;
                }


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
                toggleTextView(txtProducts, isProductsSynced, false);
                validateAll();
            }
        }
    }

    class SaveCategoryTask extends AsyncTask<Void, Integer, Void> {
        Context context;
        List<Category> dataArray;

        SaveCategoryTask(List<Category> categoryList, Context context) {
            this.context = context;
            this.dataArray = categoryList;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            toggleTextView(txtCategory, isCategorySynced, true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /* This is just a code that delays the thread execution */
            try {
                // for fast insertion
                if (dataArray != null) {
                    db.open();
                    db.clearCategory();

                    for (int i = 0; i < dataArray.size(); i++) {
                        db.open();
                        db.addCategory(dataArray.get(i).getProductCategoryId(), dataArray.get(i).getProductCategoryName());

                    }
                    isCategorySynced = true;
                }


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
                toggleTextView(txtCategory, isCategorySynced, false);
                validateAll();
            }
        }
    }


}
