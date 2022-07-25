package com.tids.shopncart.clockinclockout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.model.Clock;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.utils.BaseActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tids.shopncart.utils.Utils.isNetworkAvailable;

public class ClockInClockOutActivity extends BaseActivity {

    SharedPreferences sp;
    String staffName, staffId, shopID, ownerId;
    ImageView clockButton, backBtn;
    TextView timeIn, timeOut, dateIn, dateOut, txtTotalTime, userName, txtButton, clockedText;
    Boolean isClockIn = false;
    DatabaseAccess databaseAccess;
    PrefManager pref;
    int repeatCounter = 1;//incrementing for every 60 sec
    CountDownTimer tripTimeCounter;
    String text = "";
    Toolbar toolbar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_out);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.menu_back);
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(view -> {
            setResult(RESULT_OK);
        finish();
        });
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        databaseAccess = DatabaseAccess.getInstance(this);
        pref = new PrefManager(this);
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
                    long endTime = System.currentTimeMillis();
                    Log.e("endTime","----"+endTime);
                    startTimeCounter(endTime);


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

        clockButton.setOnClickListener(view -> {
            Log.e("CLOCK", "---" + isClockIn);

            getDataFromServer(isClockIn);

        });
    }

    private void getDataFromServer(Boolean isClockIn) {

        if (isNetworkAvailable(ClockInClockOutActivity.this)) {
            getClockCall(shopID, ownerId, staffId, isClockIn);
        } else {
            Toasty.error(ClockInClockOutActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }
    }

    public void startTimeCounter(long endTime) {
        tripTimeCounter = new CountDownTimer(60 * 1000, 1000) {

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                repeatCounter = repeatCounter + 1;
                startTimeCounter(endTime);//follow the recursion on finish of the limit of 60 seconds & increment the repeat counter
            }

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                long time = 0;
                if (endTime != 0) {
                    time = endTime - pref.getTime();

                }
                text = formatter.format(new Date(time + ((repeatCounter * 60L) * 1000) - millisUntilFinished));

                txtTotalTime.setText(text);
            }

        }.start();
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
            @SuppressLint("SetTextI18n")
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
                        txtTotalTime.setText(totalTime);
                        pref.setTime(0);
                        repeatCounter = 1;
                        if (tripTimeCounter != null) {
                            tripTimeCounter.cancel();
                            tripTimeCounter = null;
                        }

                        txtTotalTime.setText(text);
                        databaseAccess.updateClockData(staffId, clockInDate, clockOutDate, clockInTime, clockOutTime, totalTime, isClockIn, clockID);

                    } else {
                        txtButton.setText("Clock Out");
                        clockedText.setText("You Are Clocked In");
                        long startTime = System.currentTimeMillis();
                        pref.setTime(startTime);
                        startTimeCounter(0);
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


                }

            }

            @Override
            public void onFailure(@NonNull Call<Clock> call, @NonNull Throwable t) {

                Toast.makeText(ClockInClockOutActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


}
