package com.tids.shopncart.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class MultiLanguageApp extends Application {

    private static MultiLanguageApp mInstance;

    public static synchronized MultiLanguageApp getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

}
