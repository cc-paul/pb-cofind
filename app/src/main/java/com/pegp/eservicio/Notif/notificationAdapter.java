package com.pegp.eservicio.Notif;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pegp.eservicio.Photos.GalleryAdapter;
import com.pegp.eservicio.Photos.GalleryData;
import com.pegp.eservicio.R;

import java.util.ArrayList;


public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.MyViewHolder> {
    private ArrayList<notificationData> notificationDataList;

    public notificationAdapter(ArrayList<notificationData> notificationData) {
        this.notificationDataList = notificationData;
    }

    @Override
    public notificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_notif, parent, false);
        return new notificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull notificationAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        notificationData notificationData = notificationDataList.get(position);

        holder.tvMessage.setText(notificationData.getMessage());
        holder.tvAgo.setText(notificationData.getDateCreated());
    }

    @Override
    public int getItemCount() {
        return notificationDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvMessage,tvAgo;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvMessage = view.findViewById(R.id.tvMessage);
            tvAgo = view.findViewById(R.id.tvAgo);
        }
    }
}
