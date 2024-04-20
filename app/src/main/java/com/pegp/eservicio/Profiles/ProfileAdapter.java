package com.pegp.eservicio.Profiles;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Bottoms.bottom_applaud;
import com.pegp.eservicio.Bottoms.bottom_details;
import com.pegp.eservicio.Bottoms.bottom_feedbacks;
import com.pegp.eservicio.DaySchedule;
import com.pegp.eservicio.Links;
import com.pegp.eservicio.Login;
import com.pegp.eservicio.MainActivity;
import com.pegp.eservicio.Profile;
import com.pegp.eservicio.R;
import com.pegp.eservicio.ScheduleList;
import com.pegp.eservicio.ServiceSelection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.internet.AddressException;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {
    Activity activity;
    private ArrayList<profileData> profileDataList;
    AlertDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public ProfileAdapter(ArrayList<profileData> profileData) {
        this.profileDataList = profileData;
    }

    @Override
    public ProfileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_profiles, parent, false);
        return new ProfileAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.MyViewHolder holder, int position) {
        profileData profileData = profileDataList.get(position);

        sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();



        holder.tvFullName.setText(profileData.getName());
        holder.tvMobileNumber.setText(profileData.getMobileNumber());
        holder.tvAddress.setText(profileData.getAddress());
        holder.tvCountRight.setText(profileData.getCountMessages() + " Feedback(s) • " + profileData.getCountServices() + " Schedule(s)");
        holder.tvCountLeft.setText(profileData.getCountLikers().toString() + " Applause");
        holder.imgIsLiked.setBackgroundResource(profileData.getIsLiked() == 1 ? R.drawable.clapping_active : R.drawable.clapping_disabled);

        if (profileData.getIsServiceMade() != 1) {
            holder.lnIsLiked.setEnabled(false);
            holder.lnIsLiked.setAlpha(0.5f);

//            holder.lnFeedBack.setEnabled(false);
//            holder.lnFeedBack.setAlpha(0.5f);
        }

        holder.lnFeedBack.setOnClickListener(view -> {
            editor.putInt("currentFreelancerID",profileData.getId());
            editor.putInt("profileRowIndex",position);
            editor.putBoolean("disableComment",profileData.getIsServiceMade() != 1 ? true : false);
            editor.putInt("isFNewsFeed",1);
            editor.commit();

            bottom_feedbacks bottomSheet = new bottom_feedbacks();
            bottomSheet.show(((AppCompatActivity) holder.view.getContext()).getSupportFragmentManager(), "ModalBottomSheet");
        });

        holder.lnIsLiked.setOnClickListener(view -> {
            Integer isLiked = profileData.getIsLiked();

            if (isLiked == 1) {
                isLiked = 0;
            } else {
                isLiked = 1;
            }

            profileData.setIsLiked(isLiked);

            holder.imgIsLiked.setBackgroundResource(profileData.getIsLiked() == 1 ? R.drawable.clapping_active : R.drawable.clapping_disabled);

            sendLike(profileData.getId(),sp.getInt("currentID",0),profileData.getIsLiked(),holder.view.getContext(),holder);
        });

        try {
            holder.tvService1.setText(profileData.getServices().split("~")[0]);
        } catch (Exception e) {
            holder.crdService1.setVisibility(View.GONE);
        }

        try {
            holder.tvService2.setText(profileData.getServices().split("~")[1]);
        } catch (Exception e) {
            holder.crdService2.setVisibility(View.GONE);
        }

        try {
            holder.tvService3.setText(profileData.getServices().split("~")[2]);
        } catch (Exception e) {
            holder.crdService3.setVisibility(View.GONE);
        }
        try {
            holder.tvService4.setText(profileData.getServices().split("~")[3]);
        } catch (Exception e) {
            holder.crdService4.setVisibility(View.GONE);
        }

        if (profileData.getServiceCount() <= 4) {
            holder.crdServiceOthers.setVisibility(View.GONE);
        }

        if (!profileData.getProfileLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(profileData.getProfileLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile);
        }

        holder.imgGoToProfile.setOnClickListener(view -> {
            editor.putInt("currentFreelancerID",profileData.getId());
            editor.putInt("profileRowIndex",position);
            editor.commit();

            Intent goToProfile = new Intent(holder.view.getContext(), Profile.class);
            goToProfile.putExtra("selectedID",profileData.getId());
            holder.view.getContext().startActivity(goToProfile);
        });

        holder.lnScheduleTab.setOnClickListener(view -> {
            Intent goToSchedule = new Intent(holder.view.getContext(), ScheduleList.class);
            goToSchedule.putExtra("selectedID",profileData.getId());
            holder.view.getContext().startActivity(goToSchedule);
        });

        holder.lnApplaud.setOnClickListener(view -> {
            editor.putInt("currentFreelancerID",profileData.getId());
            editor.commit();

            bottom_applaud bottomSheet = new bottom_applaud();
            bottomSheet.show(((AppCompatActivity) holder.view.getContext()).getSupportFragmentManager(), "ModalBottomSheet");
        });
    }

    public void sendLike(Integer likeID, Integer likedByID, Integer isLiked, Context context,MyViewHolder viewHolder) {
        Links application = (Links) context.getApplicationContext();
        String sendLikeApi = application.sendLikeApi;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendLikeApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            if (!error) {
                                viewHolder.tvCountLeft.setText(jsonResponse.getString("newTotalLike") + " Applaud you");
                                viewHolder.imgIsLiked.setBackgroundResource(isLiked == 1 ? R.drawable.clapping_active : R.drawable.clapping_disabled);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                viewHolder.imgIsLiked.setBackgroundResource(isLiked != 1 ? R.drawable.clapping_active : R.drawable.clapping_disabled);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(context, "Unable to applause. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Unable to applause. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("likeID", likeID.toString());
                params.put("likedByID", likedByID.toString());
                params.put("isLiked", isLiked.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return profileDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvFullName,tvMobileNumber,tvAddress,tvService1,tvService2,tvService3,tvService4,tvCountRight,tvCountLeft;
        public final CardView crdService1,crdService2,crdService3,crdService4,crdServiceOthers;
        public final ImageView imgProfile,imgGoToProfile;
        public final LinearLayout lnScheduleTab,lnIsLiked,lnFeedBack,lnApplaud;
        public final ImageView imgIsLiked;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvMobileNumber = view.findViewById(R.id.tvMobileNumber);
            tvFullName = view.findViewById(R.id.tvFullName);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvService1 = view.findViewById(R.id.tvService1);
            tvService2 = view.findViewById(R.id.tvService2);
            tvService3 = view.findViewById(R.id.tvService3);
            tvService4 = view.findViewById(R.id.tvService4);
            crdService1 = view.findViewById(R.id.crdService1);
            crdService2 = view.findViewById(R.id.crdService2);
            crdService3 = view.findViewById(R.id.crdService3);
            crdService4 = view.findViewById(R.id.crdService4);
            crdServiceOthers = view.findViewById(R.id.crdServiceOthers);
            imgProfile = view.findViewById(R.id.imgProfile);
            imgGoToProfile = view.findViewById(R.id.imgGoToProfile);
            lnScheduleTab = view.findViewById(R.id.lnScheduleTab);
            tvCountRight = view.findViewById(R.id.tvCountRight);
            tvCountLeft = view.findViewById(R.id.tvCountLeft);
            imgIsLiked = view.findViewById(R.id.imgIsLiked);
            lnIsLiked = view.findViewById(R.id.lnIsLiked);
            lnFeedBack = view.findViewById(R.id.lnFeedBack);
            lnApplaud = view.findViewById(R.id.lnApplaud);

        }
    }
}
