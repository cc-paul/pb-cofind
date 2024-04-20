package com.pegp.eservicio.Applaud;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;
import com.pegp.eservicio.R;

import java.util.ArrayList;

public class applaudAdapter extends RecyclerView.Adapter<applaudAdapter.MyViewHolder> {
    private ArrayList<applaudData> applaudDataList;

    public applaudAdapter(ArrayList<applaudData> applaudData) {
        this.applaudDataList = applaudData;
    }

    @Override
    public applaudAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_applaud, parent, false);
        return new applaudAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull applaudAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        applaudData applaudData = applaudDataList.get(position);

        holder.tvName.setText(applaudData.getName());

        if (!applaudData.getImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(applaudData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile);
        }
    }

    @Override
    public int getItemCount() {
        return applaudDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvName;
        public final ImageView imgProfile;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvName = view.findViewById(R.id.tvName);
            imgProfile = view.findViewById(R.id.imgProfile);
        }
    }
}
