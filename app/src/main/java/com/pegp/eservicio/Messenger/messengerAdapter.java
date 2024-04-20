package com.pegp.eservicio.Messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Feedbacks.feedBackData;
import com.pegp.eservicio.Feedbacks.feedbackAdapter;
import com.pegp.eservicio.R;

import java.util.ArrayList;

public class messengerAdapter extends RecyclerView.Adapter<messengerAdapter.MyViewHolder> {

    private ArrayList<messengerData> messengerDataList;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public messengerAdapter(ArrayList<messengerData> messengerData) {
        this.messengerDataList = messengerData;
    }

    @Override
    public messengerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_chats, parent, false);
        return new messengerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull messengerAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        messengerData messengerData = messengerDataList.get(position);

        sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        holder.tvSendersMessage.setText(messengerData.getMessage());
        holder.tvReceiversMessage.setText(messengerData.getMessage());

        if (!messengerData.getImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(messengerData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgSendersImage);
        }

        if (!messengerData.getImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(messengerData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgReceiversImage);
        }

        if (sp.getInt("currentID",0) == messengerData.getSenderID()) {
            holder.lnChatSender.setVisibility(View.VISIBLE);
            holder.lnChatReceiver.setVisibility(View.GONE);
        } else {
            holder.lnChatSender.setVisibility(View.GONE);
            holder.lnChatReceiver.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messengerDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final LinearLayout lnChatSender,lnChatReceiver;
        public final TextView tvReceiversMessage,tvSendersMessage;
        public final ImageView imgReceiversImage,imgSendersImage;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            lnChatSender = view.findViewById(R.id.lnChatSender);
            lnChatReceiver = view.findViewById(R.id.lnChatReceiver);
            tvReceiversMessage = view.findViewById(R.id.tvReceiversMessage);
            tvSendersMessage = view.findViewById(R.id.tvSendersMessage);
            imgReceiversImage = view.findViewById(R.id.imgReceiversImage);
            imgSendersImage = view.findViewById(R.id.imgSendersImage);
        }
    }
}
