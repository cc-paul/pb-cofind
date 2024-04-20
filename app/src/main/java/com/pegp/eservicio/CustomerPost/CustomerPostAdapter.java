package com.pegp.eservicio.CustomerPost;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Bottoms.bottom_feedbacks;
import com.pegp.eservicio.Bottoms.bottom_feedbacks2;
import com.pegp.eservicio.R;

import java.util.ArrayList;

public class CustomerPostAdapter extends RecyclerView.Adapter<CustomerPostAdapter.MyViewHolder> {
    Activity activity;
    private ArrayList<CustomerPostData> customerPostDataList;


    public CustomerPostAdapter(ArrayList<CustomerPostData> customerPostData) {
        this.customerPostDataList = customerPostData;
    }

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    public CustomerPostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_profiles_customer, parent, false);
        return new CustomerPostAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CustomerPostAdapter.MyViewHolder holder, int position) {
        CustomerPostData customerPostData = customerPostDataList.get(position);

        sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        holder.tvFullName.setText(customerPostData.getFullName());
        holder.tvMobileNumber.setText(customerPostData.getMobileNumber());
        holder.tvAddress.setText(customerPostData.getAddress());
        holder.tvAgo.setText("Posted " + customerPostData.getDateCreated());
        holder.tvPost.setText(customerPostData.getContent());

        Integer pics = 5;

        if (!customerPostData.getImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(customerPostData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile);
        }

        try {
            Glide.with(holder.view.getContext()).load(customerPostData.getImageLinks().split("~")[0]).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.img1);
        } catch (Exception e) {
            holder.crd1.setVisibility(View.GONE);
            pics -= 1;
        }

        try {
            Glide.with(holder.view.getContext()).load(customerPostData.getImageLinks().split("~")[1]).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.img2);
        } catch (Exception e) {
            holder.crd2.setVisibility(View.GONE);
            pics -= 1;
        }

        try {
            Glide.with(holder.view.getContext()).load(customerPostData.getImageLinks().split("~")[2]).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.img3);
        } catch (Exception e) {
            holder.crd3.setVisibility(View.GONE);
            pics -= 1;
        }

        try {
            Glide.with(holder.view.getContext()).load(customerPostData.getImageLinks().split("~")[3]).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.img4);
        } catch (Exception e) {
            holder.crd4.setVisibility(View.GONE);
            pics -= 1;
        }

        try {
            Glide.with(holder.view.getContext()).load(customerPostData.getImageLinks().split("~")[4]).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.img5);
        } catch (Exception e) {
            holder.crd5.setVisibility(View.GONE);
            pics -= 1;
        }

        holder.hvImages.setVisibility(pics == 0 || customerPostData.getImageLinks().length() == 0 ? View.GONE : View.VISIBLE);

        holder.tvCountRight.setText(customerPostData.getCountComment() + " Comment(s)");

        holder.lnSendComment.setOnClickListener(view -> {
            editor.putInt("postID",customerPostData.getId());
            editor.putInt("posterID",customerPostData.getUserID());
            editor.putInt("customerRowIndex",position);
            editor.putString("posterEmail",customerPostData.getEmail());
            editor.putInt("isFNewsFeed",0);
            editor.commit();

            bottom_feedbacks2 bottomSheet = new bottom_feedbacks2();
            bottomSheet.show(((AppCompatActivity) holder.view.getContext()).getSupportFragmentManager(), "ModalBottomSheet");
        });

        holder.tvCountRight.setOnClickListener(view -> {
            holder.lnSendComment.performClick();
        });
    }

    @Override
    public int getItemCount() {
        return customerPostDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvFullName,tvMobileNumber,tvAddress,tvAgo,tvPost,tvCountRight;
        public final CardView crd1,crd2,crd3,crd4,crd5;
        public final ImageView img1,img2,img3,img4,img5,imgProfile;
        public final HorizontalScrollView hvImages;
        public final LinearLayout lnSendComment;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvCountRight = view.findViewById(R.id.tvCountRight);
            tvFullName = view.findViewById(R.id.tvFullName);
            tvMobileNumber = view.findViewById(R.id.tvMobileNumber);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvAgo = view.findViewById(R.id.tvAgo);
            tvPost = view.findViewById(R.id.tvPost);
            crd1 = view.findViewById(R.id.crd1);
            crd2 = view.findViewById(R.id.crd2);
            crd3 = view.findViewById(R.id.crd3);
            crd4 = view.findViewById(R.id.crd4);
            crd5 = view.findViewById(R.id.crd5);
            img1 = view.findViewById(R.id.img1);
            img2 = view.findViewById(R.id.img2);
            img3 = view.findViewById(R.id.img3);
            img4 = view.findViewById(R.id.img4);
            img5 = view.findViewById(R.id.img5);
            hvImages = view.findViewById(R.id.hvImages);
            imgProfile = view.findViewById(R.id.imgProfile);
            lnSendComment = view.findViewById(R.id.lnSendComment);
        }
    }
}
