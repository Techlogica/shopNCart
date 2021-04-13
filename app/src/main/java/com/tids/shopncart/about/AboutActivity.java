package com.tids.shopncart.about;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;

public class AboutActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String country="";
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.about_us);
        logo=findViewById(R.id.logo);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        country = sp.getString(Constant.SP_SHOP_COUNTRY, "");
        if(country.equals("INDIA")){
            logo.setBackground(getResources().getDrawable(R.drawable.kun_logo));
        }else{
            logo.setBackground(getResources().getDrawable(R.drawable.techlogica_logo));
        }
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
