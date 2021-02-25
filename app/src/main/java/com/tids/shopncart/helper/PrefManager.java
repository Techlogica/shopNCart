package com.tids.shopncart.helper;

/**
 * Summary: to manage shared preference data.
 * Created by Eldho on 20/11/17.
 * Modified by Eldho on 12/1/18.
 */

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "SHOPNCART";
    // All Shared Preferences Keys
    private static final String KEY_IS_SYNCED = "isSynced";
    private static final String KEY_DEVICE = "device";


    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public boolean isSynced() {
        return pref.getBoolean(KEY_IS_SYNCED, false);
    }

    public void setSynced(boolean synced) {

        editor.putBoolean(KEY_IS_SYNCED, synced);
        editor.commit();
    }

    public  String getKeyDevice() {
        return pref.getString(KEY_DEVICE,"");

    }
    public  void setKeyDevice(String  count) {
        editor.putString(KEY_DEVICE, count);
        editor.commit();
    }
}
