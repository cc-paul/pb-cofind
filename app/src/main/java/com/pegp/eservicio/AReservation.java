package com.pegp.eservicio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;
import com.pegp.eservicio.Reservation.ReservationAdapter;
import com.pegp.eservicio.Reservation.ReservationData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class AReservation extends AppCompatActivity {
    RecyclerView rvReservation;
    LinearLayout lnBack;
    TextView tvRecords;
    KonfettiView viewKonfetti;

    private RecyclerView.Adapter adapter;
    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Integer userID,isRegularUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        lnBack = findViewById(R.id.lnBack);
        rvReservation = findViewById(R.id.rvReservation);
        tvRecords = findViewById(R.id.tvRecords);
        viewKonfetti = findViewById(R.id.viewKonfetti);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        userID = sp.getInt("currentID",0);

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        loadReservation();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void finalizeReservation(Integer taskID,Integer isLiked,Integer freelancerID) {
        Links application = (Links) getApplication();
        String doneService = application.doneService;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, doneService,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Integer countDone = obj.getInt("countDone");
                        String message = obj.getString("message");
                        Boolean error = obj.getBoolean("error");

                        if (!error) {
                            if (countDone == 1) {
                                viewKonfetti.build()
                                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                                        .setDirection(0.0, 359.0)
                                        .setSpeed(1f, 5f)
                                        .setFadeOutEnabled(true)
                                        .setTimeToLive(2000L)
                                        .addShapes(Shape.RECT, Shape.CIRCLE)
                                        .addSizes(new Size(12, 5))
                                        .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                                        .streamFor(300, 2000L);

                                SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("New feature unlocked!");
                                pDialog.setContentText("You can now give feedback and applause to a freelancer who has accomplished their task");
                                pDialog.setCancelable(false);
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        pDialog.dismiss();

                                        goToFeedBackForm(isLiked,freelancerID);
                                    }
                                });
                                pDialog.show();
                            } else {
                                Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
                                goToFeedBackForm(isLiked,freelancerID);
                            }
                        } else {
                            Toast.makeText(application, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskID", "" + taskID);
                params.put("doneBy", userID + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void goToFeedBackForm(Integer isLiked,Integer freelancerID) {
        Intent gotoFeedBackForm = new Intent(AReservation.this, FeedbackForm.class);
        gotoFeedBackForm.putExtra("isLiked",isLiked);
        gotoFeedBackForm.putExtra("freelancerID",freelancerID);
        startActivity(gotoFeedBackForm);
    }

    public void loadReservation() {
        ArrayList<ReservationData> list = new ArrayList<>();

        Links application = (Links) getApplication();
        String getReservationAPI = application.getReservation;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getReservationAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrReservation = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrReservation.length(); i++) {
                                JSONObject current_obj = arrReservation.getJSONObject(i);

                                list.add(new ReservationData(
                                        current_obj.getInt("bidID"),
                                        current_obj.getInt("taskID"),
                                        current_obj.getString("imageLink"),
                                        current_obj.getString("freelancerName"),
                                        current_obj.getString("address"),
                                        current_obj.getString("mobileNumber"),
                                        current_obj.getString("emailAddress"),
                                        current_obj.getString("title"),
                                        current_obj.getString("venue"),
                                        current_obj.getString("dateSched"),
                                        current_obj.getString("timeSched"),
                                        current_obj.getString("bidAmount"),
                                        current_obj.getString("remarks"),
                                        current_obj.getInt("setToDisable"),
                                        current_obj.getInt("isYours"),
                                        current_obj.getInt("isLiked"),
                                        current_obj.getInt("freelancerID"),
                                        current_obj.getInt("isServiceDone"),

                                        current_obj.getString("customerImageLink"),
                                        current_obj.getString("customerName"),
                                        current_obj.getString("customerAddress"),
                                        current_obj.getString("customerMobileNumber"),
                                        current_obj.getString("customerEmailAddress"),
                                        current_obj.getInt("customerID")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,1);
                            rvReservation.setLayoutManager(mLayoutManager);
                            tvRecords.setText("Total Records : " + list.size());

                            adapter = new ReservationAdapter(list);
                            rvReservation.setAdapter(adapter);
                            dialog.dismiss();

                            if (arrReservation.length() == 0) {
                                Toast.makeText(this,"No records found", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(this, "Message 1 : Something went wrong : " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Log.e("Error",error.toString());
                    Toast.makeText(this, "Message 2 : Something went wrong : " + error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userID + "");
                params.put("isFreelancer",sp.getInt("isRegularUser",0) + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}