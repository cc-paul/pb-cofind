package com.pegp.eservicio.Photos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.pegp.eservicio.Gallery;
import com.pegp.eservicio.Profile;
import com.pegp.eservicio.R;
import com.pegp.eservicio.ValidID.validIDAdapter;
import com.pegp.eservicio.ValidID.validIDData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    Activity activity;
    private ArrayList<GalleryData> galleryDataList;
    Integer row_index = -1;


    public GalleryAdapter(ArrayList<GalleryData> galleryData) {
        this.galleryDataList = galleryData;
    }

    @Override
    public GalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_images, parent, false);
        return new GalleryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GalleryData galleryData = galleryDataList.get(position);
        Glide.with(holder.view.getContext()).load(galleryData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgID);

        holder.imgID.setOnClickListener(view -> {
            row_index = position;
            notifyDataSetChanged();
        });

        if (!galleryData.isDisabled) {
            holder.imgID.setOnLongClickListener(view -> {
                row_index = position;
                notifyDataSetChanged();
                showDialogOptions(galleryData.getId(),holder.view.getContext());

                return false;
            });
        }

        if (row_index == position){
            holder.imgID.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            holder.imgID.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return galleryDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView imgID;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            imgID = view.findViewById(R.id.imgID);

        }
    }

    public void showDialogOptions(Integer id,Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final Integer[] selectedIndex = new Integer[1];

        builder.setTitle("Choose an Option.");
        final String[] choices = new String[]{
                "Set as Profile",
                "Delete Photo"
        };
        builder.setSingleChoiceItems(
                choices,
                -1,
                (dialogInterface, i) -> {
                    selectedIndex[0] = i;

                    Log.e("Selected Index", selectedIndex[0] + "");
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selectedIndex[0] == null) {
                    Toast.makeText(context, "Please select an option first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(context instanceof Profile){
                    ((Profile)context).ImageOptions(selectedIndex[0],id);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
