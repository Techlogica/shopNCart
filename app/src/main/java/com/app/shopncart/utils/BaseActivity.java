package com.app.shopncart.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import java.util.Locale;

import static android.content.pm.PackageManager.GET_META_DATA;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetTitles();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    protected void resetTitles() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
            if (info.labelRes != 0) {
                setTitle(info.labelRes);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    //for Android Android N
    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        Locale locale = new Locale(LocaleManager.getLanguagePref(this));
        Locale.setDefault(locale);
        overrideConfiguration.setLocale(locale);
        super.applyOverrideConfiguration(overrideConfiguration);
    }


    /**
     * set title
     * @param title title name
     */
    void setMyTitle(String title){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
        }
    }


    /**
     * set back
     */
    void setBack(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }



  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.hexprint, menu);
        return true;
    }

    EditTextDialog mEditTextDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_print:
                mEditTextDialog = DialogCreater.createEditTextDialog(this,
                    getResources().getString(R.string.cancel), getResources().getString(R.string.confirm), getResources().getString(R.string.input_order), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditTextDialog.cancel();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = mEditTextDialog.getEditText().getText().toString();
                        byte[] data = BytesUtil.getBytesFromHexString(text);
                        if(BluetoothUtil.isBlueToothPrinter){
                            BluetoothUtil.sendData(data);
                        }else{
                            SunmiPrintHelper.getInstance().sendRawData(data);
                        }
                        mEditTextDialog.cancel();
                    }
                }, null);
                mEditTextDialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }*/
}



