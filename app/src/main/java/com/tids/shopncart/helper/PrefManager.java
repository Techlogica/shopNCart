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
    private static final String KEY_LOGIN = "login";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_DEVICE_KEY = "device_key";
    private static final String KEY_EDIT_FLAG = "edit_flag";
    private static final String KEY_EDIT_VALUE = "edit_value";
    private static final String KEY_TIME = "time";
    private static final String KEY_INV_NO = "invoice_no";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_WEB_URL = "web_url";


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

    public String getKeyDevice() {
        return pref.getString(KEY_DEVICE, "");

    }

    public void setKeyDevice(String count) {
        editor.putString(KEY_DEVICE, count);
        editor.commit();
    }

    public String getImageUrl() {
        return pref.getString(KEY_IMAGE_URL, "");

    }

    public void setImageUrl(String imageUrl) {
        editor.putString(KEY_IMAGE_URL, imageUrl);
        editor.commit();
    }

    public String getWebUrl() {
        return pref.getString(KEY_WEB_URL, "");

    }

    public void setWebUrl(String url) {
        editor.putString(KEY_WEB_URL, url);
        editor.commit();
    }

    public String getKeyDeviceId() {
        return pref.getString(KEY_DEVICE_KEY, "");

    }

    public void setKeyDeviceId(String count) {
        editor.putString(KEY_DEVICE_KEY, count);
        editor.commit();
    }
    public Boolean getKeyEditFlag() {
        return pref.getBoolean(KEY_EDIT_FLAG, false);

    }

    public void setKeyEditFlag(Boolean count) {
        editor.putBoolean(KEY_EDIT_FLAG, count);
        editor.commit();
    }
    public String getKeyEditValue() {
        return pref.getString(KEY_EDIT_VALUE, "");

    }

    public void setKeyEditValue(String count) {
        editor.putString(KEY_EDIT_VALUE, count);
        editor.commit();
    }

    public  String getInvNo(){
        return pref.getString(KEY_INV_NO, "");
    }

    public void setInvNo(String no){
        editor.putString(KEY_INV_NO, no);
        editor.commit();
    }

    public long getTime() {
        return pref.getLong(KEY_TIME,0);

    }

    public void setTime(long time) {
        editor.putLong(KEY_TIME, time);
        editor.commit();
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_LOGIN, false);
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_LOGIN, isLoggedIn);
        // commit changes
        editor.commit();

    }
}
