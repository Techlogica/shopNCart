package com.tids.shopncart.adapter;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;

import com.tids.shopncart.Constant;
import com.tids.shopncart.R;
import com.tids.shopncart.model.Suppliers;
import com.tids.shopncart.networking.ApiClient;
import com.tids.shopncart.networking.ApiInterface;
import com.tids.shopncart.suppliers.EditSuppliersActivity;
import com.tids.shopncart.utils.Utils;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder> {


    private final List<Suppliers> supplierData;
    private final Context context;
    Utils utils;


    public SupplierAdapter(Context context, List<Suppliers> supplierData) {
        this.context = context;
        this.supplierData = supplierData;
        utils=new Utils();

    }


    @NonNull
    @Override
    public SupplierAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final SupplierAdapter.MyViewHolder holder, int position) {

        final String suppliersId = supplierData.get(position).getSuppliersId();
        String name = supplierData.get(position).getSuppliersName();
        String contactPerson = supplierData.get(position).getSuppliersContactPerson();
        String cell = supplierData.get(position).getSuppliersCell();
        String email = supplierData.get(position).getSuppliersEmail();
        String address = supplierData.get(position).getSuppliersAddress();

        holder.txtSuppliersName.setText(name);
        holder.txtSupplierContactPerson.setText(contactPerson);
        holder.txtSupplierCell.setText(cell);
        holder.txtSupplierEmail.setText(email);
        holder.txtSupplierAddress.setText(address);


        holder.imgCall.setOnClickListener(v -> {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String phone = "tel:" + cell;
                callIntent.setData(Uri.parse(phone));
                context.startActivity(callIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        holder.imgDelete.setOnClickListener(v -> {


            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder
                    .withTitle(context.getString(R.string.delete))
                    .withMessage(context.getString(R.string.want_to_delete_supplier))
                    .withEffect(Slidetop)
                    .withDialogColor("#2979ff") //use color code for dialog
                    .withButton1Text(context.getString(R.string.yes))
                    .withButton2Text(context.getString(R.string.cancel))
                    .setButton1Click(v1 -> {

                        if (Utils.isNetworkAvailable(context)) {
                            deleteSupplier(suppliersId);
                            supplierData.remove(holder.getAdapterPosition());
                            dialogBuilder.dismiss();
                        }
                        else
                        {
                            dialogBuilder.dismiss();
                            Toasty.error(context, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setButton2Click(v12 -> dialogBuilder.dismiss())
                    .show();


        });

    }

    @Override
    public int getItemCount() {
        return supplierData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtSuppliersName, txtSupplierContactPerson, txtSupplierCell, txtSupplierEmail, txtSupplierAddress;
        ImageView imgDelete, imgCall;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSuppliersName = itemView.findViewById(R.id.txt_supplier_name);
            txtSupplierContactPerson = itemView.findViewById(R.id.txt_contact_person);
            txtSupplierCell = itemView.findViewById(R.id.txt_supplier_cell);
            txtSupplierEmail = itemView.findViewById(R.id.txt_supplier_email);
            txtSupplierAddress = itemView.findViewById(R.id.txt_supplier_address);

            imgDelete = itemView.findViewById(R.id.img_delete);
            imgCall = itemView.findViewById(R.id.img_call);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, EditSuppliersActivity.class);
            i.putExtra(Constant.SUPPLIERS_ID, supplierData.get(getAdapterPosition()).getSuppliersId());
            i.putExtra(Constant.SUPPLIERS_NAME, supplierData.get(getAdapterPosition()).getSuppliersName());
            i.putExtra(Constant.SUPPLIERS_CONTACT_PERSON, supplierData.get(getAdapterPosition()).getSuppliersContactPerson());
            i.putExtra(Constant.SUPPLIERS_CELL, supplierData.get(getAdapterPosition()).getSuppliersCell());
            i.putExtra(Constant.SUPPLIERS_EMAIL, supplierData.get(getAdapterPosition()).getSuppliersEmail());
            i.putExtra(Constant.SUPPLIERS_ADDRESS, supplierData.get(getAdapterPosition()).getSuppliersAddress());
            context.startActivity(i);
        }
    }



    //delete from server
    private void deleteSupplier(String supplierId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Suppliers> call = apiInterface.deleteSupplier(supplierId);
        call.enqueue(new Callback<Suppliers>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<Suppliers> call, @NonNull Response<Suppliers> response) {


                if (response.isSuccessful() && response.body() != null) {

                    String value = response.body().getValue();

                    if (value.equals(Constant.KEY_SUCCESS)) {
                        Toasty.error(context, R.string.supplier_deleted, Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();

                    }

                    else if (value.equals(Constant.KEY_FAILURE)){
                        Toasty.error(context, R.string.error, Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Toast.makeText(context, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Suppliers> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error! " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
