package com.tids.shopncart.clockinclockout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.Clock;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.pos.ProductCart;
import com.tids.shopncart.utils.BaseActivity;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tids.shopncart.utils.Utils.isNetworkAvailable;

public class ClockInClockOutActivity extends BaseActivity {

    SharedPreferences sp;
    String staffName, staffId, shopID, ownerId;
    ImageView clockButton;
    TextView timeIn, timeOut, dateIn, dateOut, txtTotalTime, userName, txtButton, clockedText;
    Boolean isClockIn = false;
    DatabaseAccess databaseAccess;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_out);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.clock_in_clock_out);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        databaseAccess = DatabaseAccess.getInstance(this);
        clockButton = findViewById(R.id.img_btn_clk);
        timeIn = findViewById(R.id.txt_clock_in_time);
        timeOut = findViewById(R.id.txt_clock_out_time);
        dateIn = findViewById(R.id.txt_clock_in_Date);
        dateOut = findViewById(R.id.txt_clock_out_date);
        txtTotalTime = findViewById(R.id.txt_total_time);
        userName = findViewById(R.id.user_name);
        clockedText = findViewById(R.id.clocked_text);
        txtButton = findViewById(R.id.button_text);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");
        staffName = sp.getString(Constant.SP_STAFF_NAME, "");
        userName.setText(staffName);
        databaseAccess.open();
        HashMap<String, String> map = databaseAccess.getStaffClock();

        String dbStaffId = map.get("staff_id");
        String dbclockInDate = map.get("c_in_date");
        String dbclockOutDate = map.get("c_o_date");
        String dbclockInTime = map.get("clock_time_in");
        String dbclockOutTime = map.get("clock_time_out");
        String totalTime = map.get("time");
        String status = map.get("status");
        if (!staffId.equals(dbStaffId)) {
            txtButton.setText("Clock In");
            clockedText.setText("Welcome");
            isClockIn = true;
        } else {
            if (status != null) {
                if (status.equals("0")) {
                    txtButton.setText("Clock Out");
                    clockedText.setText("You Are Clocked In");
                    isClockIn = false;
                } else if (status.equals("1")) {
                    txtButton.setText("Clock In");
                    clockedText.setText("You Are Clocked Out");
                    isClockIn = true;
                }
            }
        }
        timeIn.setText(dbclockInTime);
        timeOut.setText(dbclockOutTime);
        dateIn.setText(dbclockInDate);
        dateOut.setText(dbclockOutDate);
        txtTotalTime.setText(totalTime);

        clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CLOCK","---"+isClockIn);
                getDataFromServer(isClockIn);


            }
        });
    }

    private void getDataFromServer(Boolean isClockIn) {

        if (isNetworkAvailable(ClockInClockOutActivity.this)) {
            getClockCall(shopID, ownerId, staffId, isClockIn);
        } else {
            Toasty.error(ClockInClockOutActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }


    public void getClockCall(String shopID, String ownerId, String staffId, final Boolean isClockDataIn) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Clock> call;
        call = apiInterface.getClockData(shopID, ownerId, staffId, isClockDataIn);
        call.enqueue(new Callback<Clock>() {
            @Override
            public void onResponse(@NonNull Call<Clock> call, @NonNull Response<Clock> response) {

                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();
                    String clockInDate = response.body().getClockInDate();
                    String clockInTime = response.body().getClockTimeIn();
                    String clockOutDate = response.body().getClockOutDate();
                    String clockOutTime = response.body().getClockTimeOut();
                    String clockID = response.body().getClockId();
                    String totalTime = response.body().getTime();
                    Log.e("dbclockInDateApi", "-----" + clockInDate);

                    if (!isClockDataIn) {
                        txtButton.setText("Clock In");
                        clockedText.setText("You Are Clocked Out");
                        databaseAccess.open();
                        isClockIn = true;
                        databaseAccess.updateClockData(staffId, clockInDate, clockOutDate, clockInTime, clockOutTime, totalTime, isClockIn, clockID);

                    } else {
                        txtButton.setText("Clock Out");
                        clockedText.setText("You Are Clocked In");
                        databaseAccess.open();
                        isClockIn = false;
                        databaseAccess.addClockData(staffId, clockInDate, clockOutDate, clockInTime, clockOutTime, totalTime, isClockIn, clockID);

                    }
                    databaseAccess.open();
                    databaseAccess.addClockData(staffId, clockInDate, clockOutDate, clockInTime, clockOutTime, totalTime, isClockIn, clockID);

                    timeIn.setText(clockInTime);
                    timeOut.setText(clockOutTime);
                    dateIn.setText(clockInDate);
                    dateOut.setText(clockOutDate);
                    txtTotalTime.setText(totalTime);


                }

            }

            @Override
            public void onFailure(@NonNull Call<Clock> call, @NonNull Throwable t) {

                Toast.makeText(ClockInClockOutActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
