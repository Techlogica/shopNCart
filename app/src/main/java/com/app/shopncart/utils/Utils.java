package com.app.shopncart.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {





    public boolean isNetworkAvailable(Context context) {

        boolean isConnected = false;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            return false;
        } else {

            return true;

        }
    }
}
