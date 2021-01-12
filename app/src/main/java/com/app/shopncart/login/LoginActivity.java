package com.app.shopncart.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.shopncart.Constant;
import com.app.shopncart.HomeActivity;
import com.app.shopncart.R;
import com.app.shopncart.model.Login;
import com.app.shopncart.networking.ApiClient;
import com.app.shopncart.networking.ApiInterface;
import com.app.shopncart.utils.BaseActivity;
import com.app.shopncart.utils.Utils;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    EditText etxtEmail, etxtPassword;
    TextView txtLogin, login;
    ImageView logo, title;
    View line;
    Animation topAnim, bottomAnim, sideAnim, fadeAnim;
    SharedPreferences sp;
    ProgressDialog loading;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().hide();

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_animation);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.fade_animation);

        etxtEmail = findViewById(R.id.etxt_email);
        etxtPassword = findViewById(R.id.etxt_password);
        txtLogin = findViewById(R.id.txt_login);
        login = findViewById(R.id.login);
        logo = findViewById(R.id.applogo);
        title = findViewById(R.id.apptitle);
        line = findViewById(R.id.line);
        utils = new Utils();

        logo.setAnimation(topAnim);
        title.setAnimation(topAnim);
        login.setAnimation(fadeAnim);
        etxtEmail.setAnimation(sideAnim);
        etxtPassword.setAnimation(sideAnim);
        txtLogin.setAnimation(bottomAnim);
        line.setAnimation(fadeAnim);


        utils = new Utils();

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String email = sp.getString(Constant.SP_EMAIL, "");
        String password = sp.getString(Constant.SP_PASSWORD, "");


        etxtEmail.setText(email);
        etxtPassword.setText(password);


        if (email.length() >= 3 && password.length() >= 3) {
            if (utils.isNetworkAvailable(LoginActivity.this)) {
                login(email, password);
            } else {
                Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }
        }


        txtLogin.setOnClickListener(v -> {
            String email1 = etxtEmail.getText().toString().trim();
            String password1 = etxtPassword.getText().toString().trim();

            if (email1.isEmpty() || !email1.contains("@") || !email1.contains(".")) {
                etxtEmail.setError(getString(R.string.enter_valid_email));
                etxtEmail.requestFocus();
            } else if (password1.isEmpty()) {
                etxtPassword.setError(getString(R.string.please_enter_password));
                etxtPassword.requestFocus();
            } else {


                if (utils.isNetworkAvailable(LoginActivity.this)) {
                    login(email1, password1);
                } else {
                    Toasty.error(LoginActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    //login method
    private void login(String email, String password) {

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
        Log.e("","test");
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Login> call = apiInterface.login(email, password);
        Log.d("TEMP_TAG", "req url: " + call.request().toString());
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(@NonNull Call<Login> call, @NonNull Response<Login> response) {

                Log.d("Login", "response " + response.body());

                if (response.body() != null && response.isSuccessful()) {
                    String value = response.body().getValue();
                    String message = response.body().getMassage();
                    String staffId = response.body().getStaffId();
                    String staffName = response.body().getName();
                    String userType = response.body().getUserType();


                    String shopName = response.body().getShopName();
                    String shopAddress = response.body().getShopAddress();
                    String shopCountry = response.body().getShopCountry();
                    String shopContact = response.body().getShopContact();
                    String shopEmail = response.body().getShopEmail();
                    String tax = response.body().getTax();
                    String currencySymbol = response.body().getCurrencySymbol();
                    String shopStatus = response.body().getShopStatus();

                    String shopId = response.body().getShopId();
                    String ownerId = response.body().getOwnerID();

                    if (shopName != null || shopAddress != null || shopContact != null || shopEmail != null || tax != null || currencySymbol != null || shopStatus != null || staffId != null || staffName != null || userType != null) {


                        if (shopStatus.equals(Constant.STATUS_CLOSED)) {
                            Toasty.error(LoginActivity.this, R.string.shop_closed_now, Toast.LENGTH_SHORT).show();

                            loading.dismiss();
                        } else if (value.equals(Constant.SUCCESS)) {
                            loading.dismiss();
                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sp.edit();
                            //Adding values to editor

                            editor.putString(Constant.SP_EMAIL, email);
                            editor.putString(Constant.SP_PASSWORD, password);


                            editor.putString(Constant.SP_STAFF_ID, staffId);
                            editor.putString(Constant.SP_STAFF_NAME, staffName);
                            editor.putString(Constant.SP_USER_TYPE, userType);


                            editor.putString(Constant.SP_SHOP_NAME, shopName);
                            editor.putString(Constant.SP_SHOP_COUNTRY, shopCountry);
                            editor.putString(Constant.SP_SHOP_ADDRESS, shopAddress);
                            editor.putString(Constant.SP_SHOP_EMAIL, shopEmail);
                            editor.putString(Constant.SP_SHOP_CONTACT, shopContact);
                            editor.putString(Constant.SP_SHOP_STATUS, shopStatus);
                            editor.putString(Constant.SP_CURRENCY_SYMBOL, currencySymbol);
                            editor.putString(Constant.SP_SHOP_ID, shopId);
                            editor.putString(Constant.SP_OWNER_ID, ownerId);


                            //Saving values to Share preference
                            editor.apply();

                            Toasty.success(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);

                        }

                    } else {
                        loading.dismiss();
                        Toasty.error(LoginActivity.this, R.string.invalid_email_or_password, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loading.dismiss();

                }
            }

            @Override
            public void onFailure(@NonNull Call<Login> call, @NonNull Throwable t) {

                loading.dismiss();
                Log.e("error","------"+t.getMessage());
                Toasty.error(LoginActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
