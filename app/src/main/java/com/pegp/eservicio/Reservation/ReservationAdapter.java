package com.pegp.eservicio.Reservation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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
import com.pegp.eservicio.AReservation;
import com.pegp.eservicio.Login;
import com.pegp.eservicio.MainActivity;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;
import com.pegp.eservicio.R;
import com.pegp.eservicio.Report;
import com.pegp.eservicio.ServiceSelection;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.MyViewHolder> {
    private ArrayList<ReservationData> reservationDataList;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public ReservationAdapter(ArrayList<ReservationData> ReservationData) {
        this.reservationDataList = ReservationData;
    }

    @Override
    public ReservationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_reservation, parent, false);
        return new ReservationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReservationData rvData = reservationDataList.get(position);

        holder.tvFreelancersName.setText(rvData.getFreelancerName());
        holder.tvFreelancersAddress.setText(rvData.getAddress());
        holder.tvFreelancersMobileNumber.setText(rvData.getMobileNumber());
        holder.tvFreelancersEmail.setText(rvData.getEmailAddress());

        holder.tvCustomersName.setText(rvData.getCustomerName());
        holder.tvCustomersAddress.setText(rvData.getCustomerAddress());
        holder.tvCustomersMobileNumber.setText(rvData.getCustomerMobileNumber());
        holder.tvCustomersEmail.setText(rvData.getCustomerEmailAddress());

        holder.tvTypeOfService.setText(rvData.getTitle());
        holder.tvVenue.setText(rvData.getVenue());
        holder.tvDate.setText(rvData.getDateSched());
        holder.tvTime.setText(rvData.getTimeSched());
        holder.tvBidAmount.setText(rvData.getBidAmount());
        holder.tvRemarks.setText(rvData.getRemarks());

        sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        if (!rvData.getImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(rvData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile);
        }

        if (!rvData.getImageLinkCustomer().equals("-")) {
            Glide.with(holder.view.getContext()).load(rvData.getImageLinkCustomer()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfileCustomer);
        }

        if (rvData.getIsServiceDone() != 1) {
            holder.lnReport.setEnabled(false);
            holder.lnReport.setAlpha(0.5f);
        } else {
            holder.lnReport.setEnabled(true);
            holder.lnReport.setAlpha(1f);
        }

        if (rvData.getSetToDisable() == 1) {
            holder.lnDone.setEnabled(false);
            holder.lnDone.setAlpha(0.5f);
        } else {
            holder.lnDone.setEnabled(true);
            holder.lnDone.setAlpha(1f);
        }

//        if (rvData.getIsYours() == 1) {
//            holder.lnDone.setVisibility(View.VISIBLE);
//            holder.lnYours.setVisibility(View.GONE);
//        } else {
//            holder.lnYours.setVisibility(View.VISIBLE);
//            holder.lnDone.setVisibility(View.GONE);
//        }

        holder.lnReport.setOnClickListener(view -> {
            Intent gotoReport = new Intent(holder.view.getContext(), Report.class);
            gotoReport.putExtra("reportedID", sp.getInt("isRegularUser",0) == 1 ? rvData.getFreelancerID() : rvData.getCustomerID());
            holder.view.getContext().startActivity(gotoReport);
        });

        holder.lnDone.setOnClickListener(view -> {
            if(holder.view.getContext() instanceof AReservation){
                ((AReservation)holder.view.getContext()).finalizeReservation(rvData.getTaskID(),rvData.getIsLiked(),rvData.getFreelancerID());

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.lnDone.setEnabled(false);
                        holder.lnDone.setAlpha(0.5f);

                        holder.lnReport.setEnabled(true);
                        holder.lnReport.setAlpha(1f);
                    }
                }, 1000);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservationDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view,vwDivider;
        public final TextView tvFreelancersName,tvFreelancersAddress,tvFreelancersMobileNumber,tvFreelancersEmail,tvTypeOfService,tvVenue,tvDate,tvTime,tvBidAmount,tvRemarks;
        public final TextView tvCustomersName,tvCustomersAddress,tvCustomersMobileNumber,tvCustomersEmail;
        public final ImageView imgProfile,imgProfileCustomer;
        public final LinearLayout lnDone,lnYours,lnReport;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            imgProfile = view.findViewById(R.id.imgProfile);
            tvFreelancersName = view.findViewById(R.id.tvFreelancersName);
            tvFreelancersAddress = view.findViewById(R.id.tvFreelancersAddress);
            tvFreelancersMobileNumber = view.findViewById(R.id.tvFreelancersMobileNumber);
            tvFreelancersEmail = view.findViewById(R.id.tvFreelancersEmail);

            imgProfileCustomer = view.findViewById(R.id.imgProfileCustomer);
            tvCustomersName = view.findViewById(R.id.tvCustomersName);
            tvCustomersAddress = view.findViewById(R.id.tvCustomersAddress);
            tvCustomersMobileNumber = view.findViewById(R.id.tvCustomersMobileNumber);
            tvCustomersEmail = view.findViewById(R.id.tvCustomersEmail);

            tvTypeOfService = view.findViewById(R.id.tvTypeOfService);
            tvVenue = view.findViewById(R.id.tvVenue);
            tvDate = view.findViewById(R.id.tvDate);
            tvTime = view.findViewById(R.id.tvTime);
            tvBidAmount = view.findViewById(R.id.tvBidAmount);
            tvRemarks = view.findViewById(R.id.tvRemarks);
            lnDone = view.findViewById(R.id.lnDone);
            lnYours = view.findViewById(R.id.lnYours);
            vwDivider = view.findViewById(R.id.vwDivider);
            lnReport = view.findViewById(R.id.lnReport);
        }
    }
}
