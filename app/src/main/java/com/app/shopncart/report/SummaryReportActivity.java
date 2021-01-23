package com.app.shopncart.report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ahmedelsayed.sunmiprinterutill.PrintMe;
import com.app.shopncart.Constant;
import com.app.shopncart.R;
import com.app.shopncart.adapter.CustomerAdapter;
import com.app.shopncart.adapter.PayMethodAdapter;
import com.app.shopncart.customers.AddCustomersActivity;
import com.app.shopncart.model.Customer;
import com.app.shopncart.model.OrderDetails;
import com.app.shopncart.model.PayMethod;
import com.app.shopncart.networking.ApiClient;
import com.app.shopncart.networking.ApiInterface;
import com.app.shopncart.pos.ProductCart;
import com.app.shopncart.utils.BaseActivity;
import com.app.shopncart.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.shopncart.utils.DateTimeFormats.APP_DATE_FORMAT;
import static com.app.shopncart.utils.DateTimeFormats.APP_TIME_FORMAT;
import static com.app.shopncart.utils.Utils.isNetworkAvailable;
import static com.app.shopncart.utils.Utils.parseAppDisplay;
import static com.app.shopncart.utils.Utils.parseAppSqlite;
import static com.app.shopncart.utils.Utils.parseServerDateTime;
import static com.app.shopncart.utils.Utils.parseSqliteTimeApp;

public class SummaryReportActivity extends BaseActivity {


    private RecyclerView recyclerView;
    TextView txtTotalExpense, txtTotalSales;
    TextView txtDateFrom, txtDateTo;
    String shopName = "";
    String address = "";
    String customerName = "";
    String servedBy = "";
    String email = "";
    String contact = "";
    SharedPreferences sp;
    DecimalFormat decimn = new DecimalFormat("#,###,##0.00");
    String fromDate = "", toDate = "";
    private String fromTime = "";
    private String toTime = "";
    Calendar startTimeCalendar;
    Calendar endTimeCalendar;
    TextView txt_starttime, txt_endtime;
    Boolean mode = false;
    String currency = "";
    String shopID = "";
    String ownerId = "";
    Calendar calendar;
    Calendar calendar1;
    Button print;
    private PrintMe printMe;
    RelativeLayout loading, nodata;
    PayMethodAdapter payMethodAdapter;
    List<PayMethod> payMethodList;
    SimpleDateFormat sdf = new SimpleDateFormat(APP_DATE_FORMAT, Locale.ENGLISH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_report);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.summary_report);
        printMe = new PrintMe(this);

        recyclerView = findViewById(R.id.recyclerview);
        txtTotalSales = findViewById(R.id.txt_total_sale);
        txtTotalExpense = findViewById(R.id.txt_total_expense);
        txtDateFrom = (TextView) findViewById(R.id.txt_date_from);
        txtDateTo = (TextView) findViewById(R.id.txt_date_to);
        txt_starttime = (TextView) findViewById(R.id.txt_start_time);
        txt_endtime = (TextView) findViewById(R.id.txt_to_time);
        loading = (RelativeLayout) findViewById(R.id.loading_rl_id);
        nodata = (RelativeLayout) findViewById(R.id.nodata_rl_id);
        print = (Button) findViewById(R.id.btn_print1);
        calendar = Calendar.getInstance();
        calendar1 = Calendar.getInstance();
        startTimeCalendar = Calendar.getInstance();
        endTimeCalendar = Calendar.getInstance();


        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");
        shopName = sp.getString(Constant.SP_SHOP_NAME, "");
        address = sp.getString(Constant.SP_SHOP_ADDRESS, "");
        email = sp.getString(Constant.SP_EMAIL, "");
        contact = sp.getString(Constant.SP_SHOP_CONTACT, "");
        servedBy = sp.getString(Constant.SP_STAFF_NAME, "");

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);
        setDate();
        setCalander();
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printLayout();
            }
        });


        /*LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));*/
    }

    private void getDataFromServer() {
        if (isNetworkAvailable(SummaryReportActivity.this)) {
            getPayMethodData(shopID, ownerId);
        } else {
            Toasty.error(SummaryReportActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }
    }


/*    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            String totalSales = intent.getStringExtra("product_piece_T");
            String totalTax = intent.getStringExtra("product_value_tax");
            String totalDiscount = intent.getStringExtra("product_value_discount");
            String totalExpense = intent.getStringExtra("product_expense_amount");
            if (totalSales != null && !totalSales.equals("")) {
                txtTotalSales.setText(currency + " " + decimn.format(Double.valueOf(totalSales)));
            }
            if (totalExpense != null && !totalExpense.equals("")) {
                txtTotalExpense.setText(currency + " " + decimn.format(Double.valueOf(totalExpense)));
            }


        }
    };*/


    public void getPayMethodData(String shopId, String ownerId) {

        loading.setVisibility(View.VISIBLE);
        nodata.setVisibility(View.GONE);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<PayMethod>> call;
        call = apiInterface.getPaymethod(shopId, ownerId, parseAppSqlite(fromDate), parseAppSqlite(toDate), parseSqliteTimeApp(fromTime), parseSqliteTimeApp(toTime));
        Log.e("request : ", "----------" + shopId + "," + ownerId + "," + parseAppSqlite(fromDate) + "," + parseAppSqlite(toDate) + "," + parseSqliteTimeApp(fromTime) + "," + parseSqliteTimeApp(toTime));
        call.enqueue(new Callback<List<PayMethod>>() {
            @Override
            public void onResponse(@NonNull Call<List<PayMethod>> call, @NonNull Response<List<PayMethod>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    loading.setVisibility(View.GONE);
                    ;
                    payMethodList = response.body();

                    if (payMethodList.size() != 0) {

                        txtTotalSales.setVisibility(View.VISIBLE);
                        txtTotalExpense.setVisibility(View.VISIBLE);
                        nodata.setVisibility(View.GONE);

                        payMethodAdapter = new PayMethodAdapter(SummaryReportActivity.this, payMethodList, currency, txtTotalSales, txtTotalExpense);
                        recyclerView.setAdapter(payMethodAdapter);
                        payMethodAdapter.notifyDataSetChanged();

                    } else {
                        txtTotalSales.setVisibility(View.GONE);
                        txtTotalExpense.setVisibility(View.GONE);
                        nodata.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PayMethod>> call, @NonNull Throwable t) {
                loading.setVisibility(View.GONE);
                nodata.setVisibility(View.VISIBLE);
                Toast.makeText(SummaryReportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }

        });


    }


    //  setting from & to dates
    private void setDate() {

        toDate = sdf.format(calendar.getTime());
        txtDateTo.setText(parseAppDisplay(toDate));

        fromDate = sdf.format(calendar1.getTime());
        txtDateFrom.setText(parseAppDisplay(fromDate));

        startTimeCalendar.set(Calendar.HOUR, 12);
        startTimeCalendar.set(Calendar.MINUTE, 00);
        startTimeCalendar.set(Calendar.AM_PM, Calendar.PM);

        String myFormat = "HHmmss"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        txt_starttime.setText(parseSqliteTimeApp(sdf.format(startTimeCalendar.getTime())));
        fromTime = sdf.format(startTimeCalendar.getTime());

        String currentTime = new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(new Date()); //HH:mm:ss a
        toTime = currentTime;
        txt_endtime.setText(parseSqliteTimeApp(currentTime));

        getDataFromServer();

    }

    //setting calander
    private void setCalander() {

        //click action for fromdatesort
        txtDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                calendar1.set(Calendar.YEAR, year);
                                calendar1.set(Calendar.MONTH, monthOfYear);
                                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                fromDate = sdf.format(calendar1.getTime());
                                txtDateFrom.setText(parseAppDisplay(fromDate));
                                if (calendar.getTimeInMillis() < calendar1.getTimeInMillis()) {//check from date is always less than to date
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, monthOfYear);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    toDate = fromDate;
                                    txtDateTo.setText(parseAppDisplay(toDate));
                                }


                                if (!toDate.isEmpty()) {
                                    getDataFromServer();

                                }
                            }

                        },
                        calendar1.get(Calendar.YEAR),
                        calendar1.get(Calendar.MONTH),
                        calendar1.get(Calendar.DAY_OF_MONTH)
                );
                Calendar now = Calendar.getInstance();
                dpd.setMaxDate(now);
                dpd.show(getFragmentManager(), "Date picker dialog");


            }
        });


        //click action for todatesort
        txtDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!fromDate.isEmpty()) {

                    try {

                        StringTokenizer tokens = new StringTokenizer(fromDate, "/");
                        int min_date = Integer.parseInt(tokens.nextToken());
                        int min_month = Integer.parseInt(tokens.nextToken());
                        int min_year = Integer.parseInt(tokens.nextToken());


                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                        calendar.set(Calendar.YEAR, year);
                                        calendar.set(Calendar.MONTH, monthOfYear);
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        toDate = sdf.format(calendar.getTime());
//                                        txtDateTo.setText(toDate);
                                        txtDateTo.setText(parseAppDisplay(toDate));

                                        if (!fromDate.isEmpty()) {

                                            getDataFromServer();

                                        }
                                    }

                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );


                        Calendar min_cal = Calendar.getInstance();
                        min_cal.set(Calendar.YEAR, min_year);
                        min_cal.set(Calendar.MONTH, min_month - 1);
                        min_cal.set(Calendar.DAY_OF_MONTH, min_date);

                        dpd.setMinDate(min_cal);
                        Calendar now = Calendar.getInstance();
                        dpd.setMaxDate(now);

                        dpd.show(getFragmentManager(), "Datepickerdialog");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error, Please try again..!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select From Date", Toast.LENGTH_SHORT).show();
                }


            }
        });

        txt_starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimePickerStart();
            }
        });

        txt_endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimePickerEnd();
            }
        });


    }

    private void setTimePickerStart() {


        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                                                                                                                                                  @Override
                                                                                                                                                  public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

                                                                                                                                                      if (mode) {
                                                                                                                                                          startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                                                                                                          startTimeCalendar.set(Calendar.MINUTE, minute);
                                                                                                                                                          startTimeCalendar.set(Calendar.SECOND, second);
                                                                                                                                                      } else {
                                                                                                                                                          mode = true;
                                                                                                                                                          startTimeCalendar.set(Calendar.HOUR_OF_DAY, 9);
                                                                                                                                                          startTimeCalendar.set(Calendar.MINUTE, 0);
                                                                                                                                                          startTimeCalendar.set(Calendar.SECOND, 0);
                                                                                                                                                      }


                                                                                                                                                      fromTime = String.format(Locale.ENGLISH, "%02d", hourOfDay) + "" + String.format(Locale.ENGLISH, "%02d", minute) + "" + String.format(Locale.ENGLISH, "%02d", second);

//                                                                                                                                                      mLog("startTime", fromTime);
                                                                                                                                                      String output = "";

                                                                                                                                                      if (!fromTime.isEmpty()) {
                                                                                                                                                          try {
                                                                                                                                                              SimpleDateFormat format = new SimpleDateFormat("HHmmss", Locale.ENGLISH);
                                                                                                                                                              java.util.Date newDate = format.parse(fromTime);

                                                                                                                                                              format = new SimpleDateFormat(APP_TIME_FORMAT, Locale.ENGLISH);
                                                                                                                                                              output = format.format(newDate);
                                                                                                                                                          } catch (ParseException e) {
                                                                                                                                                              e.printStackTrace();
                                                                                                                                                          }
                                                                                                                                                          getDataFromServer();
                                                                                                                                                      }
                                                                                                                                                      txt_starttime.setText(output);

                                                                                                                                                  }
                                                                                                                                              },
                startTimeCalendar.get(Calendar.HOUR_OF_DAY),
                startTimeCalendar.get(Calendar.MINUTE),
                startTimeCalendar.get(Calendar.SECOND),
                false
        );

        // tpd.setMaxTime(calendarTime.getTime().getHours(),calendarTime.getTime().getMinutes(),calendarTime.getTime().getSeconds());


        tpd.show(getFragmentManager(), "Time picker dialog");

    }

    private void setTimePickerEnd() {


        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                                                @Override
                                                                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

                                                                    if (mode) {
                                                                        endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                        endTimeCalendar.set(Calendar.MINUTE, minute);
                                                                        endTimeCalendar.set(Calendar.SECOND, second);
                                                                    } else {
                                                                        mode = true;
                                                                        endTimeCalendar.set(Calendar.HOUR_OF_DAY, 9);
                                                                        endTimeCalendar.set(Calendar.MINUTE, 0);
                                                                        endTimeCalendar.set(Calendar.SECOND, 0);
                                                                    }


                                                                    toTime = String.format(Locale.ENGLISH, "%02d", hourOfDay) + "" + String.format(Locale.ENGLISH, "%02d", minute) + "" + String.format(Locale.ENGLISH, "%02d", second);

//                                                                    mLog("startTime", endTime);
                                                                    String output = "";

                                                                    if (!toTime.isEmpty()) {
                                                                        try {
                                                                            SimpleDateFormat format = new SimpleDateFormat("HHmmss", Locale.ENGLISH);
                                                                            java.util.Date newDate = format.parse(toTime);

                                                                            format = new SimpleDateFormat(APP_TIME_FORMAT, Locale.ENGLISH);
                                                                            output = format.format(newDate);
                                                                        } catch (ParseException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                    getDataFromServer();
                                                                    txt_endtime.setText(output);

                                                                }
                                                            },
                endTimeCalendar.get(Calendar.HOUR_OF_DAY),
                endTimeCalendar.get(Calendar.MINUTE),
                endTimeCalendar.get(Calendar.SECOND),
                false
        );

        // tpd.setMaxTime(calendarTime.getTime().getHours(),calendarTime.getTime().getMinutes(),calendarTime.getTime().getSeconds());


        tpd.show(getFragmentManager(), "Time picker dialog");

    }

    //print operation text setup
    public void printLayout() {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.print_summary,null);

        TextView txtShopName = dialogView.findViewById(R.id.txt_shop_name);
        TextView txtShopAddress = dialogView.findViewById(R.id.txt_address);
        TextView txtShopEmail = dialogView.findViewById(R.id.txt_email);
        TextView txtShopContact = dialogView.findViewById(R.id.txt_contact);
        TextView txtTotalSales = dialogView.findViewById(R.id.txt_total_sales);
        TextView txtTotalExpense = dialogView.findViewById(R.id.txt_total_expense);
        TextView txtServedBy = dialogView.findViewById(R.id.txt_served_by);
        TextView txtFrom = dialogView.findViewById(R.id.txt_from);
        TextView txtTo = dialogView.findViewById(R.id.txt_to);

        RecyclerView recyclerViewDialog = dialogView.findViewById(R.id.recycler_view_summary);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewDialog.setLayoutManager(layoutManager);
        recyclerViewDialog.setHasFixedSize(true);

        txtShopName.setText(shopName);
        txtShopAddress.setText(address);
        txtShopEmail.setText(email);
        txtShopContact.setText("Contact: " + contact);
        txtServedBy.setText("Gen.By: " + servedBy);
        txtFrom.setText(parseAppDisplay(fromDate)+"\n"+parseSqliteTimeApp(fromTime));
        txtTo.setText( parseAppDisplay(toDate)+"\n"+parseSqliteTimeApp(toTime));

        PrintSummaryAdapter mAdapter = new PrintSummaryAdapter(this, payMethodList,txtTotalSales,txtTotalExpense);
        recyclerViewDialog.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        printMe.sendViewToPrinter(dialogView);
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class PrintSummaryAdapter extends RecyclerView.Adapter<PrintSummaryAdapter.MyViewHolder> {


        private List<PayMethod> payMethodData;
        private Context context;
        private TextView tExpense;
        private TextView tTotal;
        Utils utils;
        DecimalFormat decimn = new DecimalFormat("#,###,##0.00");
        Double payValue = 0.0;


        public PrintSummaryAdapter(Context context, List<PayMethod> payMethodData, TextView txtTotalSales, TextView txtTotalExpense) {
            this.context = context;
            this.payMethodData = payMethodData;
            this.tExpense = txtTotalExpense;
            this.tTotal = txtTotalSales;
            utils = new Utils();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.print_summary_row, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            String value = payMethodData.get(position).getValue();

            if (value != null && !value.equals("")) {
                payValue = Double.valueOf(value);
            }
            holder.txtPayMethodName.setText(payMethodData.get(position).getName()+": ");
            holder.txtPayMethodValue.setText(currency + " " + String.valueOf(decimn.format(payValue)));
            String totalSales = payMethodData.get(position).getTotalSales();
            String totalExpense = payMethodData.get(position).getTotalExpense();

            if (totalSales != null && !totalSales.equals("")) {
                tTotal.setText(currency + " " + decimn.format(Double.valueOf(totalSales)));
            }

            if (totalExpense != null && !totalExpense.equals("")) {
                tExpense.setText(currency + " " + decimn.format(Double.valueOf(totalExpense)));
            }


        }

        @Override
        public int getItemCount() {
            return payMethodData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView  txtPayMethodValue,txtPayMethodName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                txtPayMethodValue = itemView.findViewById(R.id.txt_total_method_value);
                txtPayMethodName = itemView.findViewById(R.id.txt_total_method_name);

            }

        }


    }


}
