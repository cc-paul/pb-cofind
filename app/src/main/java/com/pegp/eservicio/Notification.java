package com.pegp.eservicio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;
import com.pegp.eservicio.ScheduleDays.scheduleDaysAdapter;
import com.pegp.eservicio.ScheduleDays.scheduleDaysData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notification extends AppCompatActivity {
    RecyclerView rvNotif;
    LinearLayout lnBack;
    TextView tvRecords;

    private RecyclerView.Adapter adapter;
    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Integer userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lnBack = findViewById(R.id.lnBack);
        rvNotif = findViewById(R.id.rvNotif);
        tvRecords = findViewById(R.id.tvRecords);

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
    }

    @Override
    public void onResume(){
        super.onResume();

        loadNotif();
    }

    public void loadNotif() {
        ArrayList<notificationData> list = new ArrayList<>();

        Links application = (Links) getApplication();
        String getNotifApi = application.getNotif;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getNotifApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(Notification.this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrNotif = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrNotif.length(); i++) {
                                JSONObject current_obj = arrNotif.getJSONObject(i);

                                list.add(new notificationData(
                                        current_obj.getInt("id"),
                                        current_obj.getInt("receiverID"),
                                        current_obj.getInt("taskID"),
                                        current_obj.getInt("isRead"),
                                        current_obj.getString("message"),
                                        current_obj.getString("dateCreated")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,1);
                            rvNotif.setLayoutManager(mLayoutManager);
                            tvRecords.setText("Total Records : " + list.size());

                            adapter = new notificationAdapter(list);
                            rvNotif.setAdapter(adapter);
                            dialog.dismiss();

                            if (arrNotif.length() == 0) {
                                Toast.makeText(this,"No records found", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Log.e("Error",error.toString());
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("createdBy", userID + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}