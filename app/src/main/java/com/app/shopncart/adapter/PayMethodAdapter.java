package com.app.shopncart.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopncart.Constant;
import com.app.shopncart.R;
import com.app.shopncart.customers.EditCustomersActivity;
import com.app.shopncart.model.Customer;
import com.app.shopncart.model.PayMethod;
import com.app.shopncart.networking.ApiClient;
import com.app.shopncart.networking.ApiInterface;
import com.app.shopncart.utils.Utils;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.text.DecimalFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class PayMethodAdapter extends RecyclerView.Adapter<PayMethodAdapter.MyViewHolder> {


    private List<PayMethod> payMethodData;
    private Context context;
    private String currency;
    private TextView tExpense;
    private TextView tTotal;
    Utils utils;
    DecimalFormat decimn = new DecimalFormat("#,###,##0.00");
    Double payValue = 0.0;


    public PayMethodAdapter(Context context, List<PayMethod> payMethodData, String currency, TextView txtTotalSales, TextView txtTotalExpense) {
        this.context = context;
        this.payMethodData = payMethodData;
        this.currency = currency;
        this.tExpense = txtTotalExpense;
        this.tTotal = txtTotalSales;
        utils = new Utils();

    }


    @NonNull
    @Override
    public PayMethodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paymethod_row, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PayMethodAdapter.MyViewHolder holder, int position) {

        final String payName = payMethodData.get(position).getName();
        String value = payMethodData.get(position).getValue();
        if (value != null && !value.equals("")) {
            payValue = Double.valueOf(value);
        }
        holder.txtPayMethod.setText(payName + ": " + currency + " " + String.valueOf(decimn.format(payValue)));

        String totalSales = payMethodData.get(position).getTotalSales();
        String totalExpense = payMethodData.get(position).getTotalExpense();

        if (totalSales != null && !totalSales.equals("")){
            tTotal.setText(context.getResources().getString(R.string.net_sales) + ":" + currency + " " + decimn.format(Double.valueOf(totalSales)));}

        if (totalExpense != null && !totalExpense.equals("")){
            tExpense.setText(context.getResources().getString(R.string.total_expense) + ":" + currency + " " + decimn.format(Double.valueOf(totalExpense)));}

    }

    @Override
    public int getItemCount() {
        return payMethodData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtPayMethod;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPayMethod = itemView.findViewById(R.id.txt_total_method_name);


        }

    }


}