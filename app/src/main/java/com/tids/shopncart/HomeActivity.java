package com.tids.shopncart;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tids.shopncart.about.AboutActivity;
import com.tids.shopncart.clockinclockout.ClockInClockOutActivity;
import com.tids.shopncart.customers.CustomersActivity;
import com.tids.shopncart.database.DatabaseAccess;
import com.tids.shopncart.expense.ExpenseActivity;
import com.tids.shopncart.helper.PrefManager;
import com.tids.shopncart.login.LoginActivity;
import com.tids.shopncart.model.SalesReport;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.orders.OrdersActivity;
import com.tids.shopncart.pos.PosActivity;
import com.tids.shopncart.pos.ProductCart;
import com.tids.shopncart.product.ProductActivity;
import com.tids.shopncart.report.ReportActivity;
import com.tids.shopncart.settings.SettingsActivity;
import com.tids.shopncart.settings.sync.SyncActivity;
import com.tids.shopncart.suppliers.SuppliersActivity;
import com.tids.shopncart.utils.BaseActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;
import static com.tids.shopncart.pos.ProductCart.RESULT_CART;
import static com.tids.shopncart.utils.Utils.isNetworkAvailable;

public class HomeActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {


    LinearLayout cardCustomers, cardProducts, cardSupplier, clockLayout, cardPos, cardOrderList, cardReport, cardClockInOut, cardSettings, cardExpense, cardAbout, cardLogout;
    //for double back press to exit
    private static final int TIME_DELAY = 2000;
    private static long backPressed;
    TextView txtNetSales;
    public static final int RESULT_CART = -2;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userType;
    TextView txtShopName, txtSubText, txtCounterText, clockTxt;
    DatabaseAccess databaseAccess;
    String currency = "";
    String shopID = "";
    String ownerId = "";
    String staffId = "";
    String deviceID = "";
    String clockTime = "";
    PrefManager pref;
    DecimalFormat decimn = new DecimalFormat("#,###,##0.00");
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        pref = new PrefManager(this);

        cardCustomers = findViewById(R.id.card_customers);
        cardSupplier = findViewById(R.id.card_suppliers);
        cardProducts = findViewById(R.id.card_products);
        cardPos = findViewById(R.id.card_pos);
        clockTxt = findViewById(R.id.clock_txt);
        cardOrderList = findViewById(R.id.card_all_orders);
        cardReport = findViewById(R.id.card_reports);
        cardClockInOut = findViewById(R.id.card_clock_in_out);
        cardSettings = findViewById(R.id.card_settings);
        cardExpense = findViewById(R.id.card_expense);
        clockLayout = findViewById(R.id.clock_layout);
//        cardAbout = findViewById(R.id.card_about_us);
//        cardLogout = findViewById(R.id.card_logout);
        txtShopName = findViewById(R.id.txt_shop_name);
        txtSubText = findViewById(R.id.txt_sub_text);
        txtCounterText = findViewById(R.id.home_cart_counter);
        txtNetSales = findViewById(R.id.txt_sales);

        databaseAccess = DatabaseAccess.getInstance(this);
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        userType = sp.getString(Constant.SP_USER_TYPE, "");
        String shopName = sp.getString(Constant.SP_SHOP_NAME, "");
        String staffName = sp.getString(Constant.SP_STAFF_NAME, "");
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "");
        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");
        deviceID = pref.getKeyDeviceId();
        clockTime = sp.getString(Constant.SP_CLOCK_TIME, "");

        txtShopName.setText(shopName);
        txtSubText.setText("Hi " + staffName);

        String netSales = sp.getString(Constant.SP_TODAY_SALES, "");
        double todaySales = Double.parseDouble(netSales);
        txtNetSales.setText(getString(R.string.daily) + ":" + currency + " " + decimn.format(todaySales));
        if (clockTime.equals("yes")) {
            clockLayout.setVisibility(View.VISIBLE);
        } else {
            clockLayout.setVisibility(View.GONE);
        }

        clockText();
        counterSetiings();

        findViewById(R.id.menu_bar).setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
            popupMenu.setOnMenuItemClickListener(HomeActivity.this);
            popupMenu.inflate(R.menu.home_menu);
            popupMenu.show();
        });

        if (Build.VERSION.SDK_INT >= 23) //Android MarshMellow Version or above
        {
            requestPermission();

        }

        //        Admob initialization
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        cardCustomers.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CustomersActivity.class);
            startActivity(intent);
        });

        cardSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SuppliersActivity.class);
            startActivity(intent);
        });

        cardProducts.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
            startActivity(intent);
        });

        cardPos.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PosActivity.class);
            startActivityForResult(intent, 1);
        });

        cardOrderList.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
            startActivity(intent);
        });

        cardReport.setOnClickListener(v -> {

//            if (userType.equals(Constant.ADMIN) || userType.equals(Constant.MANAGER)) {
                Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(intent);
//            } else {
//                Toasty.error(HomeActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();
//            }

        });

        cardClockInOut.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, ClockInClockOutActivity.class);
            startActivityForResult(intent, 2);

        });

        cardExpense.setOnClickListener(v -> {

                Intent intent = new Intent(HomeActivity.this, ExpenseActivity.class);
                startActivity(intent);

        });

        cardSettings.setOnClickListener(v -> {

                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);

        });

        findViewById(R.id.home_cart).setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, ProductCart.class);
            startActivityForResult(intent, 1);

        });

        /*
        cardAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        */


        /*
        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(HomeActivity.this);
                dialogBuilder
                        .withTitle(getString(R.string.logout))
                        .withMessage(R.string.want_to_logout_from_app)
                        .withEffect(Slidetop)
                        .withDialogColor("#2979ff") //use color code for dialog
                        .withButton1Text(getString(R.string.yes))
                        .withButton2Text(getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putString(Constant.SP_PHONE, "");
                                editor.putString(Constant.SP_PASSWORD, "");
                                editor.putString(Constant.SP_USER_NAME, "");
                                editor.putString(Constant.SP_USER_TYPE, "");
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
            }
        });
*/

    }

    //double back press to exit
    @Override
    public void onBackPressed() {
        if (backPressed + TIME_DELAY > System.currentTimeMillis()) {

            finishAffinity();

        } else {
            Toasty.info(this, R.string.press_once_again_to_exit,
                    Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            //write your action if needed
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_about_us:
                Intent intent1 = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(intent1);
                return true;
            case R.id.item_sync:
                Intent intent2 = new Intent(HomeActivity.this, SyncActivity.class);
                startActivity(intent2);
                return true;
            case R.id.item_settings:
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.set_invoice:
                setInvoiceNumber();
                return true;
            case R.id.item_logout:

                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(HomeActivity.this);
                dialogBuilder
                        .withTitle(getString(R.string.logout))
                        .withMessage(R.string.want_to_logout_from_app)
                        .withEffect(Slidetop)
                        .withDialogColor("#2979ff") //use color code for dialog
                        .withButton1Text(getString(R.string.yes))
                        .withButton2Text(getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putString(Constant.SP_PHONE, "");
                                editor.putString(Constant.SP_PASSWORD, "");
                                editor.putString(Constant.SP_USER_NAME, "");
                                editor.putString(Constant.SP_USER_TYPE, "");
                                editor.apply();
                                pref.setLogin(false);

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
                return true;
        }
        return true;

    }

    private void setInvoiceNumber() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilder.setCancelable(true);
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View popupInputDialogView = layoutInflater.inflate(R.layout.row_invoice_number_layout, null);
        alertDialogBuilder.setView(popupInputDialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        EditText etxt_invoice_number = popupInputDialogView.findViewById(R.id.etxt_invoice_number);
        String invNo = pref.getInvNo().toString();
        if (!invNo.equalsIgnoreCase("")) {
            String[] no = invNo.split("-");
            etxt_invoice_number.setText(no[1]);
        }
        AppCompatButton bt_invoice_number = popupInputDialogView.findViewById(R.id.btYes);

        bt_invoice_number.setOnClickListener(view -> {
            String number = etxt_invoice_number.getText().toString().trim();
            if (!number.isEmpty()){
                int num = Integer.parseInt(number);
                pref.setInvNo(shopID+ownerId+deviceID+"-"+number);
                databaseAccess.open();
                databaseAccess.updateInvoice(num);
                alertDialog.cancel();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    counterSetiings();
                    getSalesReport("Today", shopID, ownerId, staffId,deviceID);
                }
                if (resultCode == RESULT_CART) {
                    counterSetiings();
                }
            }
            if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    clockText();
                }

            }
        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void clockText() {
        databaseAccess.open();
        HashMap<String, String> map = databaseAccess.getStaffClock();
        String dbStaffId = map.get("staff_id");
        String status = map.get("status");

        if (!staffId.equals(dbStaffId)) {
            clockTxt.setText("Clock In");
        } else {
            if (status != null) {
                if (status.equals("0")) {
                    clockTxt.setText("Clock Out");
                } else if (status.equals("1")) {
                    clockTxt.setText("Clock In");
                }
            }
        }
    }

    private void counterSetiings() {
        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> cartProductList;
        cartProductList = databaseAccess.getCartProduct();
        if (cartProductList != null && cartProductList.size() != 0) {
            txtCounterText.setVisibility(View.VISIBLE);
            txtCounterText.setText(String.valueOf(cartProductList.size()));
        } else {
            txtCounterText.setVisibility(View.GONE);

        }
    }

    public void getSalesReport(String type, String shopId, String ownerId, String staffId, String deviceId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<SalesReport>> call;
        call = apiInterface.getSalesReport(type, shopId, ownerId, staffId, deviceId);

        call.enqueue(new Callback<List<SalesReport>>() {
            @Override
            public void onResponse(@NonNull Call<List<SalesReport>> call, @NonNull Response<List<SalesReport>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<SalesReport> salesReport;
                    salesReport = response.body();

                    if (salesReport.isEmpty()) {

                        Log.d("Data", "Empty");

                    } else {

                        String totalOrderPrice = salesReport.get(0).getTotalOrderPrice();
                        String totalTax = salesReport.get(0).getTotalTax();
                        String totalDiscount = salesReport.get(0).getTotalDiscount();

                        Double orderPrice = 0.0;
                        Double getTax = 0.0;
                        Double getDiscount = 0.0;
                        if (totalOrderPrice != null && !totalOrderPrice.equals("")) {
                            orderPrice = Double.parseDouble(totalOrderPrice);
                        }
                        if (totalTax != null && !totalTax.equals("")) {
                            getTax = Double.parseDouble(totalTax);
                        }

                        if (totalDiscount != null && !totalDiscount.equals("")) {
                            getDiscount = Double.parseDouble(totalDiscount);
                        }

                        Double netSales = (orderPrice - getDiscount) + getTax;
                        txtNetSales.setText(getString(R.string.daily) + ":" + currency + " " + decimn.format(netSales));
                        editor.putString(Constant.SP_TODAY_SALES, String.valueOf(netSales));
                        editor.apply();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SalesReport>> call, @NonNull Throwable t) {

                Toast.makeText(HomeActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });

    }


}
