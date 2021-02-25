package com.tids.shopncart.settings.categories;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.model.Category;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatagoryAddActivity extends BaseActivity {

    ProgressDialog loading;
    EditText etxtCategory;
    TextView txtAddCategory;
    DatabaseAccess db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_catagory);
        db = DatabaseAccess.getInstance(this);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_catagory);

        etxtCategory = findViewById(R.id.etxt_payment_method_name);
        txtAddCategory = findViewById(R.id.txt_add_payment_method);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");

        txtAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String categoryName = etxtCategory.getText().toString().trim();

                if (categoryName.isEmpty()) {
                    etxtCategory.setError(getString(R.string.enter_catagory_name));
                    etxtCategory.requestFocus();
                } else {

                    addCategory(categoryName, shopID, ownerId);

                }


            }
        });


    }

    private void addCategory(String categoryName, String shopId, String ownerId) {

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Category> call = apiInterface.addCategory(categoryName, shopId, ownerId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {


                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();
                    String catId = "";
                    if (response.body().getProductCategoryId() != null && !response.body().getProductCategoryId().equals("")) {
                        catId = response.body().getProductCategoryId();
                    }

                    if (value.equals(Constant.KEY_SUCCESS)) {

                        loading.dismiss();

                        Toasty.success(CatagoryAddActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();

                        db.open();
                        db.addCategory(catId,categoryName);
                        setResult(RESULT_OK);
                        finish();

                    } else if (value.equals(Constant.KEY_FAILURE)) {

                        loading.dismiss();
                        Toasty.error(CatagoryAddActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
//                        finish();

                    } else {
                        loading.dismiss();
                        Toasty.error(CatagoryAddActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loading.dismiss();
                    Log.d("Error", "Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.d("Error! ", t.toString());
                Toasty.error(CatagoryAddActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
