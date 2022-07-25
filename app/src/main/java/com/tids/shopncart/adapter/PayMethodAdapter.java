package com.tids.shopncart.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tids.shopncart.R;
import com.tids.shopncart.model.PayMethod;
import com.tids.shopncart.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class PayMethodAdapter extends RecyclerView.Adapter<PayMethodAdapter.MyViewHolder> {


    private final List<PayMethod> payMethodData;
    private final Context context;
    private final String currency;
    private final TextView tExpense;
    private final TextView tReturn;
    private final TextView tTotal;
    Utils utils;
    DecimalFormat decimn = new DecimalFormat("#,###,##0.00");
    Double payValue = 0.0;


    public PayMethodAdapter(Context context, List<PayMethod> payMethodData, String currency, TextView txtTotalSales, TextView txtTotalExpense, TextView txtTotalReturn) {
        this.context = context;
        this.payMethodData = payMethodData;
        this.currency = currency;
        this.tExpense = txtTotalExpense;
        this.tTotal = txtTotalSales;
        this.tReturn = txtTotalReturn;
        utils = new Utils();

    }


    @NonNull
    @Override
    public PayMethodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paymethod_row, parent, false);
        return new MyViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PayMethodAdapter.MyViewHolder holder, int position) {

        final String payName = payMethodData.get(position).getName();
        String value = payMethodData.get(position).getValue();
        if (value != null && !value.equals("")) {
            payValue = Double.valueOf(value);
        }
        holder.txtPayMethod.setText(payName + ": " + currency + " " + decimn.format(payValue));

        String totalSales = payMethodData.get(position).getTotalSales();
        String totalExpense = payMethodData.get(position).getTotalExpense();
        String totalReturn = payMethodData.get(position).getTotalReturn();

        if (totalSales != null && !totalSales.equals("")) {
            tTotal.setText(context.getResources().getString(R.string.net_sales) + ":" + currency + " " + decimn.format(Double.valueOf(totalSales)));
        }

        if (totalExpense != null && !totalExpense.equals("")) {
            tExpense.setText(context.getResources().getString(R.string.total_expense) + ":" + currency + " " + decimn.format(Double.valueOf(totalExpense)));
        }
        if (totalReturn != null && !totalReturn.equals("")) {
            tReturn.setText(context.getResources().getString(R.string.total_return) + ":" + currency + " " + decimn.format(Double.valueOf(totalReturn)));
        }

    }

    @Override
    public int getItemCount() {
        return payMethodData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtPayMethod;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPayMethod = itemView.findViewById(R.id.txt_total_method_name);


        }

    }


}
