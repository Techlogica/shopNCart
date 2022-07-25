package com.tids.shopncart.settings.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.model.ShopInformation;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopInformationActivity extends BaseActivity {


    EditText etxtShopName, etxtTax, etxtShopContact, etxtShopEmail, etxtShopAddress;
    ProgressDialog loading;
    ImageView backBtn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_information);

//        getSupportActionBar().setHomeButtonEnabled(true); //for back button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
//        getSupportActionBar().setTitle(R.string.shop_information);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.menu_back);
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        etxtShopName = findViewById(R.id.etxt_shop_name);
        etxtShopContact = findViewById(R.id.etxt_shop_contact);
        etxtShopEmail = findViewById(R.id.etxt_shop_email);
        etxtShopAddress = findViewById(R.id.etxt_shop_address);
        etxtTax = findViewById(R.id.etxt_tax);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");



        getShopInfo(shopID);

        etxtShopName.setEnabled(false);
        etxtShopContact.setEnabled(false);
        etxtShopEmail.setEnabled(false);
        etxtShopAddress.setEnabled(false);
        etxtTax.setEnabled(false);



    }



    public void getShopInfo(String shopId) {

        loading=new ProgressDialog(this);
        loading.setMessage(getString(R.string.please_wait));
        loading.setCancelable(false);
        loading.show();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<ShopInformation>> call;
        call = apiInterface.shopInformation(shopId);

        call.enqueue(new Callback<List<ShopInformation>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopInformation>> call, @NonNull Response<List<ShopInformation>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<ShopInformation> shopInformation;
                    shopInformation = response.body();


                    loading.dismiss();

                    if (shopInformation.isEmpty()) {


                        Toasty.warning(ShopInformationActivity.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();


                    } else {


                        String shopName=shopInformation.get(0).getShopName();
                        String shopContact=shopInformation.get(0).getShopContact();
                        String shopEmail=shopInformation.get(0).getShopEmail();
                        String shopAddress=shopInformation.get(0).getShopAddress();
                        String tax=shopInformation.get(0).getTax();


                        etxtShopName.setText(shopName);
                        etxtShopContact.setText(shopContact);
                        etxtShopEmail.setText(shopEmail);
                        etxtShopAddress.setText(shopAddress);
                        etxtTax.setText(tax);




                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopInformation>> call, @NonNull Throwable t) {

                Toast.makeText(ShopInformationActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
                loading.dismiss();
            }
        });


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
}
