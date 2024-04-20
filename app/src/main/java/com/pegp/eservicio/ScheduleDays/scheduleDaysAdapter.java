package com.pegp.eservicio.ScheduleDays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pegp.eservicio.BidDetails;
import com.pegp.eservicio.Bottoms.bottom_details;
import com.pegp.eservicio.DaySchedule;
import com.pegp.eservicio.R;
import com.pegp.eservicio.Scheduler;

import java.util.ArrayList;

public class scheduleDaysAdapter extends RecyclerView.Adapter<scheduleDaysAdapter.MyViewHolder> {
    Activity activity;
    private ArrayList<scheduleDaysData> scheduleDaysDataList;
    String title = "",remarks = "";
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public scheduleDaysAdapter(ArrayList<scheduleDaysData> scheduleDaysData) {
        this.scheduleDaysDataList = scheduleDaysData;
    }

    @Override
    public scheduleDaysAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_schedules, parent, false);
        return new scheduleDaysAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull scheduleDaysAdapter.MyViewHolder holder, int position) {
        scheduleDaysData scheduleDaysData = scheduleDaysDataList.get(position);

        holder.tvTitle.setText(scheduleDaysData.getTitle());
        holder.tvTime.setText(scheduleDaysData.getScheduleFrom() + " - " + scheduleDaysData.getScheduleTo());
        holder.tvRemarks.setText(scheduleDaysData.getRemarks());
        holder.tvMinimumRate.setText("P" + scheduleDaysData.getRate());

        if (scheduleDaysData.getTitle().equals("No Title Indicated")) {
            holder.tvTitle.setAlpha(0.30f);
        } else {
            holder.tvTitle.setAlpha(1f);
        }

        if (scheduleDaysData.getRemarks().equals("No Remarks Included")) {
            holder.tvRemarks.setAlpha(0.30f);
        } else {
            holder.tvRemarks.setAlpha(1f);
        }

        holder.crdRowSchedule.setOnClickListener(view -> {
            Log.e("From News Feed",scheduleDaysData.getFromNewsFeed().toString());

            if (!scheduleDaysData.getFromNewsFeed()) {

            }
        });

        if (scheduleDaysData.getIsPass() == 1) {
            holder.crdColor.getBackground().setTint(Color.parseColor("#FF9800"));
        } else {
            if (scheduleDaysData.getHasBid() == 1) {
                holder.crdColor.getBackground().setTint(Color.parseColor("#4CAF50"));
            } if (scheduleDaysData.getIsBidAccepted() == 1) {
                holder.crdColor.getBackground().setTint(Color.parseColor("#03A9F4"));
            }
        }

        holder.imgMore.setOnClickListener(view -> {
            openOptionMenu(view,holder.view.getContext(),holder,scheduleDaysData);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openOptionMenu(View v, Context context,@NonNull scheduleDaysAdapter.MyViewHolder holder,scheduleDaysData scheduleDaysData){
        PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.RIGHT);
        popup.getMenuInflater().inflate(R.menu.daysched_menu, popup.getMenu());

        sp = context.getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        if (scheduleDaysData.getFromNewsFeed()) {
            popup.getMenu().findItem(R.id.navEdit).setVisible(false);
            popup.getMenu().findItem(R.id.navView).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.navEdit).setVisible(true);
            popup.getMenu().findItem(R.id.navView).setVisible(false);
        }

        if (scheduleDaysData.getIsPass() == 1) {
            popup.getMenu().findItem(R.id.navEdit).setVisible(false);
            popup.getMenu().findItem(R.id.navView).setVisible(false);
        }

        if (scheduleDaysData.getIsPass() == 1 || scheduleDaysData.getHasBid() == 1) {
            /* Disable edit if the schedule is already pass or if theres a bid */
            popup.getMenu().findItem(R.id.navEdit).setVisible(false);
        }

        if (scheduleDaysData.getHasBid() == 1) {
            popup.getMenu().findItem(R.id.navViewBid).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.navViewBid).setVisible(false);
        }

        if (scheduleDaysData.getIsPass() == 1) {
            popup.getMenu().findItem(R.id.navViewBid).setVisible(false);
        } else {
            if (scheduleDaysData.getHasBid() == 1) {
                if (!scheduleDaysData.getFromNewsFeed()) {
                    popup.getMenu().findItem(R.id.navViewBid).setVisible(true);
                } else {
                    popup.getMenu().findItem(R.id.navViewBid).setVisible(false);
                }
            } else {
                popup.getMenu().findItem(R.id.navViewBid).setVisible(false);
            }
        }

        if (!holder.tvTitle.getText().toString().equals("No Title Indicated")) {
            title = holder.tvTitle.getText().toString();
        }

        if (!holder.tvRemarks.getText().toString().equals("No Remarks Included")) {
            remarks = holder.tvRemarks.getText().toString();
        }

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            switch (id) {
                case R.id.navEdit:

                    Log.e("Date",scheduleDaysData.getS24HourTo());

                    Intent goToScheduleEditor = new Intent(v.getContext(), Scheduler.class);
                    goToScheduleEditor.putExtra("id",scheduleDaysData.getId());
                    goToScheduleEditor.putExtra("title",title);
                    goToScheduleEditor.putExtra("formattedDate",scheduleDaysData.getFormattedDate());
                    goToScheduleEditor.putExtra("dbDate",scheduleDaysData.getDbDate());
                    goToScheduleEditor.putExtra("minimumRate",scheduleDaysData.getRate().replace(",",""));
                    goToScheduleEditor.putExtra("scheduleFrom",scheduleDaysData.getScheduleFrom());
                    goToScheduleEditor.putExtra("s24HourFrom",scheduleDaysData.getS24HourFrom());
                    goToScheduleEditor.putExtra("scheduleTo",scheduleDaysData.getScheduleTo());
                    goToScheduleEditor.putExtra("s24HourTo",scheduleDaysData.getS24HourTo());
                    goToScheduleEditor.putExtra("remarks",remarks);
                    v.getContext().startActivity(goToScheduleEditor);

                    break;
                case R.id.navViewBid:

                    Log.e("Date",scheduleDaysData.getS24HourTo());

                    Intent gotoBidDetails = new Intent(v.getContext(), BidDetails.class);
                    gotoBidDetails.putExtra("id",scheduleDaysData.getId());
                    gotoBidDetails.putExtra("title",title);
                    gotoBidDetails.putExtra("formattedDate",scheduleDaysData.getFormattedDate());
                    gotoBidDetails.putExtra("minimumRate",scheduleDaysData.getRate().replace(",",""));
                    gotoBidDetails.putExtra("scheduleFrom",scheduleDaysData.getScheduleFrom());
                    gotoBidDetails.putExtra("scheduleTo",scheduleDaysData.getScheduleTo());
                    gotoBidDetails.putExtra("remarks",remarks);
                    gotoBidDetails.putExtra("isAccepted",scheduleDaysData.getIsBidAccepted());
                    v.getContext().startActivity(gotoBidDetails);

                    break;
                case R.id.navView:

                    String formattedTitle = scheduleDaysData.getTitle();
                    String formattedRemarks = scheduleDaysData.getRemarks();
                    String formattedSched = scheduleDaysData.getScheduleFrom() + " - " + scheduleDaysData.getScheduleTo();
                    String formattedRate  = scheduleDaysData.getRate().replace(",","");
                    String formattedDate = scheduleDaysData.getFormattedDate();

                    editor.putString("scheduleDetails",formattedTitle + "~" + formattedSched + "~" + formattedRate + "~" + formattedRemarks + "~" + scheduleDaysData.getId() + "~" + scheduleDaysData.getBidAmount() + "~" + scheduleDaysData.getIsBidAccepted() + "~" + formattedDate + "~" + scheduleDaysData.getEmailAddress());
                    editor.commit();

                    bottom_details bottomSheet = new bottom_details();
                    bottomSheet.show(((DaySchedule)v.getContext()).getSupportFragmentManager(), "ModalBottomSheet");

                    break;
            }

            return true;
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return scheduleDaysDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvTitle,tvTime,tvRemarks,tvMinimumRate;
        public final CardView crdRowSchedule;
        public final ImageView imgMore;
        public final CardView crdColor;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvTitle = view.findViewById(R.id.tvTitle);
            tvTime = view.findViewById(R.id.tvTime);
            tvRemarks = view.findViewById(R.id.tvRemarks);
            tvMinimumRate = view.findViewById(R.id.tvMinimumRate);
            crdRowSchedule = view.findViewById(R.id.crdRowSchedule);
            imgMore = view.findViewById(R.id.imgMore);
            crdColor = view.findViewById(R.id.crdColor);
        }
    }
}