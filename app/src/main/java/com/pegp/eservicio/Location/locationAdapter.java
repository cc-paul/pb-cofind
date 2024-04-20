package com.pegp.eservicio.Location;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pegp.eservicio.AddressSelection;
import com.pegp.eservicio.R;

import java.util.ArrayList;

public class locationAdapter extends RecyclerView.Adapter<locationAdapter.MyViewHolder> {
    Activity activity;
    private ArrayList<locationData> locationDataList;


    public locationAdapter(ArrayList<locationData> locationData) {
        this.locationDataList = locationData;
    }

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    public locationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_location, parent, false);
        return new locationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull locationAdapter.MyViewHolder holder, int position) {
        locationData locationData = locationDataList.get(position);

        holder.tvLabel.setText(locationData.getLabel());

        holder.lnRow.setOnClickListener(view -> {
            sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
            editor = sp.edit();

            editor.putString("id", "" + locationData.getId());
            editor.putString("location",locationData.getLabel());
            editor.putInt("isLocationChanged",1);
            editor.commit();

            if(holder.view.getContext() instanceof AddressSelection){
                ((AddressSelection)holder.view.getContext()).getBack();
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvLabel;
        public final LinearLayout lnRow;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvLabel = view.findViewById(R.id.tvLabel);
            lnRow = view.findViewById(R.id.lnRow);
        }
    }
}
