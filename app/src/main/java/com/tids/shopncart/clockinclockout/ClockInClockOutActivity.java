package com.tids.shopncart.clockinclockout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.tids.shopncart.R;
import com.tids.shopncart.model.Device;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.report.SummaryReportActivity;
import com.tids.shopncart.settings.categories.CategoriesActivity;
import com.tids.shopncart.settings.payment_method.PaymentMethodActivity;
import com.tids.shopncart.settings.shop.ShopInformationActivity;
import com.tids.shopncart.settings.sync.SyncActivity;
import com.tids.shopncart.utils.BaseActivity;
import com.tids.shopncart.utils.LocaleManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClockInClockOutActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_out);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.clock_in_clock_out);

    }


    public void getDeviceCount(String shopId) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Device> call;

        call = apiInterface.getDeviceCount(shopId);

        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(@NonNull Call<Device> call, @NonNull Response<Device> response) {

                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();
                    String count = response.body().getDeviceCount();
                    if (count != null && !count.equals("")) {
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Device> call, @NonNull Throwable t) {

                Toast.makeText(ClockInClockOutActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


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
