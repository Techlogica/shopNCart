package com.tids.shopncart.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.tids.shopncart.Constant;
import com.tids.shopncart.HomeActivity;
import com.tids.shopncart.R;
import com.tids.shopncart.helper.PrefManager;

public class AboutActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String country="";
    ImageView logo, backBtn, editDetails;
    String imageUrl = "";
    Toolbar toolbar;
    TextView url;
    PrefManager pref;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        pref = new PrefManager(this);

        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.menu_back);
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(view -> finish());
        logo=findViewById(R.id.logo);
        url = findViewById(R.id.text_url);
        editDetails = findViewById(R.id.edit_details);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        country = sp.getString(Constant.SP_SHOP_COUNTRY, "");

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editShopDetails();
            }
        });

//        imageUrl = "https://lh3.googleusercontent.com/u/0/d/1haABwN9lTWELdSmBfolDCQCxbT-hSmXo=w462-h345-p-k-nu-iv1";
        if (!pref.getImageUrl().isEmpty() ) {
            Glide.with(getApplicationContext())
                    .load(pref.getImageUrl())
                    .into(logo);
        }else {
            Glide.with(getApplicationContext()).load(R.drawable.applogo).into(logo);
        }
        if (!pref.getWebUrl().isEmpty()){
            url.setText(pref.getWebUrl());
        }else {
            url.setText("info@shopncart.co");
        }
    }

    private void editShopDetails() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AboutActivity.this);
        alertDialogBuilder.setCancelable(true);
        LayoutInflater layoutInflater = LayoutInflater.from(AboutActivity.this);
        View popupInputDialogView = layoutInflater.inflate(R.layout.row_edit_details_layout, null);
        alertDialogBuilder.setView(popupInputDialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        EditText etxt_web_url = popupInputDialogView.findViewById(R.id.etxt_web_url);
        EditText etxt_image_url = popupInputDialogView.findViewById(R.id.etxt_image_url);
        AppCompatButton bt_submit = popupInputDialogView.findViewById(R.id.edit_details_btn);

        bt_submit.setOnClickListener(view -> {
            String Weburl = etxt_web_url.getText().toString().trim();
            String imageUrl = etxt_image_url.getText().toString().trim();
            if (!Weburl.isEmpty() || !imageUrl.isEmpty()){
                pref.setWebUrl(Weburl);
                pref.setImageUrl(imageUrl);
                if (!pref.getImageUrl().isEmpty() ) {
                    Glide.with(getApplicationContext())
                            .load(pref.getImageUrl())
                            .into(logo);
                }else {
                    Glide.with(getApplicationContext()).load(R.drawable.applogo).into(logo);
                }
                if (!pref.getWebUrl().isEmpty()){
                    url.setText(pref.getWebUrl());
                }else {
                    url.setText("info@shopncart.co");
                }
                alertDialog.cancel();
            }
        });
    }
}
