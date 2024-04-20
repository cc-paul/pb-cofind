package com.pegp.eservicio.Service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.pegp.eservicio.AddressSelection;
import com.pegp.eservicio.Location.locationData;
import com.pegp.eservicio.R;
import com.pegp.eservicio.ServiceSelection;

import java.util.ArrayList;

public class serviceAdapter extends RecyclerView.Adapter<serviceAdapter.MyViewHolder> {
    Activity activity;
    private ArrayList<serviceData> serviceDataList;


    public serviceAdapter(ArrayList<serviceData> serviceData) {
        this.serviceDataList = serviceData;
    }

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    public serviceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_services, parent, false);
        return new serviceAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull serviceAdapter.MyViewHolder holder, int position) {
        serviceData serviceData = serviceDataList.get(position);

        holder.tvLabel.setText(serviceData.getServiceName());
        holder.chkSelected.setChecked(serviceData.getSelected());

        if (serviceData.getSelected()) {
            ((ServiceSelection)holder.view.getContext()).setServiceIDAndName(serviceData.getId(),serviceData.getServiceName(),serviceData.getSelected());
        }

        holder.chkSelected.setOnClickListener(v -> {
            Boolean isAdded = ((CheckBox)v).isChecked();

            if(holder.view.getContext() instanceof ServiceSelection){
                ((ServiceSelection)holder.view.getContext()).setServiceIDAndName(serviceData.getId(),serviceData.getServiceName(),isAdded);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvLabel;
        public final LinearLayout lnRow;
        public final CheckBox chkSelected;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvLabel = view.findViewById(R.id.tvLabel);
            lnRow = view.findViewById(R.id.lnRow);
            chkSelected = view.findViewById(R.id.chkSelected);
        }
    }
}
