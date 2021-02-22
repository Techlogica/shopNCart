package com.app.shopncart.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.app.shopncart.utils.DateTimeFormats.APP_DATE_DISPLAY_FORMAT;
import static com.app.shopncart.utils.DateTimeFormats.APP_DATE_FORMAT;
import static com.app.shopncart.utils.DateTimeFormats.APP_TIME_FORMAT;
import static com.app.shopncart.utils.DateTimeFormats.SERVER_DATE_TIME_FORMAT;
import static com.app.shopncart.utils.DateTimeFormats.SQLITE_DATE_FORMAT;

public class Utils {





    public static boolean isNetworkAvailable(Context context) {

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

    public static String parseServerDateTime(String input) {
        String output = "";
        if (!input.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.ENGLISH);
                Date newDate = format.parse(input);

                format = new SimpleDateFormat(APP_DATE_DISPLAY_FORMAT, Locale.ENGLISH);
                output = format.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public static String parseAppToServer(String input) {
        String output = "";
        if (!input.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(APP_DATE_FORMAT, Locale.ENGLISH);
                Date newDate = format.parse(input);

                format = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.ENGLISH);
                output = format.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public static String parseDBDateTime(String input) {
        String output = "";
        if (!input.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.ENGLISH);
                Date newDate = format.parse(input);

                format = new SimpleDateFormat(SQLITE_DATE_FORMAT, Locale.ENGLISH);
                output = format.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public static String parseAppDisplay(String input) {
        String output = "";
        if (!input.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(APP_DATE_FORMAT, Locale.ENGLISH);
                Date newDate = format.parse(input);

                format = new SimpleDateFormat(APP_DATE_DISPLAY_FORMAT, Locale.ENGLISH);
                output = format.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public static String parseAppSqlite(String input) {
        String output = "";
        if (!input.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(APP_DATE_FORMAT, Locale.ENGLISH);
                Date newDate = format.parse(input);

                format = new SimpleDateFormat(SQLITE_DATE_FORMAT, Locale.ENGLISH);
                output = format.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public static String parseSqliteTimeApp(String input) {
        String output = "";
        if (!input.isEmpty()) {

                try {
                    SimpleDateFormat format = new SimpleDateFormat("HHmmss", Locale.ENGLISH);
                    java.util.Date newDate = format.parse(input);

                    format = new SimpleDateFormat(APP_TIME_FORMAT, Locale.ENGLISH);
                    output = format.format(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }
        return output;
    }

    public static double roundOff2Decimal(double a) {
        return (double) Math.round((a * 100d) / 100d);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
