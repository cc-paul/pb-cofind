package com.pegp.eservicio.ValidID;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Gallery;
import com.pegp.eservicio.R;
import com.pegp.eservicio.Service.serviceAdapter;
import com.pegp.eservicio.Service.serviceData;
import com.pegp.eservicio.ServiceSelection;

import java.util.ArrayList;

public class validIDAdapter extends RecyclerView.Adapter<validIDAdapter.MyViewHolder> {
    Activity activity;
    private ArrayList<validIDData> validIDDataList;


    public validIDAdapter(ArrayList<validIDData> validIDData) {
        this.validIDDataList = validIDData;
    }

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    public validIDAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_validids, parent, false);
        return new validIDAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull validIDAdapter.MyViewHolder holder, int position) {
        validIDData validIDData = validIDDataList.get(position);

        Glide.with(holder.view.getContext()).load(validIDData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgID);

        holder.crdDelete.setOnClickListener(view -> {
            if(holder.view.getContext() instanceof Gallery){
                ((Gallery)holder.view.getContext()).deleteImage(validIDData.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return validIDDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView imgID;
        public final CardView crdDelete;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            imgID = view.findViewById(R.id.imgID);
            crdDelete = view.findViewById(R.id.crdDelete);
        }
    }
}
