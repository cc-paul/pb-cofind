package com.pegp.eservicio.FreelanceMessageList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Messaging;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;
import com.pegp.eservicio.Profile;
import com.pegp.eservicio.R;

import java.util.ArrayList;

public class freelanceMessageListAdapter extends RecyclerView.Adapter<freelanceMessageListAdapter.MyViewHolder> {
    private ArrayList<freelanceMessageListData> freelanceMessageListDataList;

    public freelanceMessageListAdapter(ArrayList<freelanceMessageListData> freelanceMessageListData) {
        this.freelanceMessageListDataList = freelanceMessageListData;
    }

    @Override
    public freelanceMessageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_messagelist, parent, false);
        return new freelanceMessageListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull freelanceMessageListAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        freelanceMessageListData freelanceMessageListData = freelanceMessageListDataList.get(position);

        if (!freelanceMessageListData.getImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(freelanceMessageListData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile);
        }

        holder.tvFullName.setText(freelanceMessageListData.getName());
        holder.tvMessage.setText(freelanceMessageListData.getLastMessage());
        holder.tvAgo.setText(freelanceMessageListData.getLastDate());
        holder.tvNotifCount.setText(freelanceMessageListData.getNotifCount().toString());

        if (freelanceMessageListData.getNotifCount() == 0) {
            holder.frmNotifCount.setVisibility(View.GONE);
        }

        holder.lnMessageRow.setOnClickListener(view -> {
            Intent gotoChatActivity = new Intent(holder.view.getContext(), Messaging.class);
            gotoChatActivity.putExtra("receiversName",freelanceMessageListData.getName());
            gotoChatActivity.putExtra("chatID",freelanceMessageListData.getChatID() + "");
            holder.view.getContext().startActivity(gotoChatActivity);
        });
    }

    @Override
    public int getItemCount() {
        return freelanceMessageListDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvFullName,tvMessage,tvAgo,tvNotifCount;
        public final ImageView imgProfile;
        public final LinearLayout lnMessageRow;
        public final FrameLayout frmNotifCount;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvFullName = view.findViewById(R.id.tvFullName);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvAgo = view.findViewById(R.id.tvAgo);
            imgProfile = view.findViewById(R.id.imgProfile);
            lnMessageRow = view.findViewById(R.id.lnMessageRow);
            tvNotifCount = view.findViewById(R.id.tvNotifCount);
            frmNotifCount = view.findViewById(R.id.frmNotifCount);
        }
    }
}
