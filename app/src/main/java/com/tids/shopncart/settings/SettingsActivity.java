package com.tids.shopncart.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.tids.shopncart.Constant;
import com.tids.shopncart.HomeActivity;
import com.tids.shopncart.R;
import com.tids.shopncart.report.SummaryReportActivity;
import com.tids.shopncart.settings.categories.CategoriesActivity;
import com.tids.shopncart.settings.payment_method.PaymentMethodActivity;
import com.tids.shopncart.settings.shop.ShopInformationActivity;
import com.tids.shopncart.utils.BaseActivity;
import com.tids.shopncart.utils.LocaleManager;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends BaseActivity {


    CardView cardShopInfo, cardCategory, cardPaymentMethod, cardSummaryReport;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.action_settings);


        cardShopInfo = findViewById(R.id.card_shop_info);
        cardCategory = findViewById(R.id.card_category);
        cardPaymentMethod = findViewById(R.id.card_payment_method);
        cardSummaryReport = findViewById(R.id.card_summary_report);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        userType = sp.getString(Constant.SP_USER_TYPE, "");


        cardShopInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals(Constant.ADMIN)) {
                    Intent intent = new Intent(SettingsActivity.this, ShopInformationActivity.class);
                    startActivity(intent);
                } else {
                    Toasty.error(SettingsActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();
                }


            }
        });


        cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals(Constant.ADMIN)) {
                    Intent intent = new Intent(SettingsActivity.this, CategoriesActivity.class);
                    startActivity(intent);
                } else {
                    Toasty.error(SettingsActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();
                }

            }
        });


        cardPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals(Constant.ADMIN)) {
                    Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
                    startActivity(intent);
                } else {
                    Toasty.error(SettingsActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();
                }

            }
        });

        cardSummaryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, SummaryReportActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        int id = item.getItemId();
        switch (id) {


            case R.id.local_french:
                setNewLocale(this, LocaleManager.FRENCH);
                return true;


            case R.id.local_english:
                setNewLocale(this, LocaleManager.ENGLISH);
                return true;


            case R.id.local_bangla:
                setNewLocale(this, LocaleManager.BANGLA);
                return true;

            case R.id.local_spanish:
                setNewLocale(this, LocaleManager.SPANISH);
                return true;

            case R.id.local_hindi:
                setNewLocale(this, LocaleManager.HINDI);
                return true;
            case R.id.local_malayalam:
                setNewLocale(this, LocaleManager.MALAYALAM);
                return true;
            case R.id.local_arabic:
                setNewLocale(this, LocaleManager.ARABIC);
                return true;
            default:
                Log.d("Default", "default");

        }

        return super.onOptionsItemSelected(item);
    }


    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
