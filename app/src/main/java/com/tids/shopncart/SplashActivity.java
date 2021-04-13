package com.tids.shopncart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView logo, appName, cart, shade;
    Animation side, fade, bottamAnim, sideAnim;
    View text;
    TextView txtVersion;
    String version = "";
    PrefManager pref;

    public static int splashTimeOut = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        pref = new PrefManager(this);

        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.appName);
        text = findViewById(R.id.textmain);
        cart = findViewById(R.id.cart);
        shade = findViewById(R.id.shade);

        txtVersion = findViewById(R.id.txt_version);

        side = AnimationUtils.loadAnimation(this, R.anim.side);
        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_animation);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        bottamAnim = AnimationUtils.loadAnimation(this, R.anim.bottom);


        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!version.equals("")) {
            txtVersion.setText("v " + version);
        } else {
            txtVersion.setVisibility(View.GONE);
        }

        logo.setAnimation(fade);
        appName.setAnimation(fade);
        cart.setAnimation(fade);
        text.setAnimation(bottamAnim);
        shade.setAnimation(fade);

        new Handler().postDelayed(() -> {

            if (pref.isLoggedIn()) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}

