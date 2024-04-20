package com.pegp.eservicio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.pegp.eservicio.ScheduleDays.scheduleDaysAdapter;
import com.pegp.eservicio.ScheduleDays.scheduleDaysData;
import com.pegp.eservicio.Service.serviceAdapter;
import com.pegp.eservicio.Service.serviceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DaySchedule extends AppCompatActivity {
    RecyclerView rvSchedules;
    LinearLayout lnBack;
    TextView tvRecords,tvDay;

    private RecyclerView.Adapter adapter;
    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Integer userID;
    Bundle bundle;
    Boolean isFromNewsFeed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_schedule);

        lnBack = findViewById(R.id.lnBack);
        rvSchedules = findViewById(R.id.rvSchedules);
        tvRecords = findViewById(R.id.tvRecords);
        tvDay = findViewById(R.id.tvDay);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        bundle = getIntent().getExtras();
        tvDay.setText(bundle.getString("formattedDate"));

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        userID = sp.getInt("currentID",0);

        if (bundle.getBoolean("isIdFromNewsFeed")) {
            isFromNewsFeed = true;
            userID = bundle.getInt("otherUserID");
        }

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        getScheduleDays();
    }

    public void getScheduleDays() {
        ArrayList<scheduleDaysData> list = new ArrayList<>();

        Links application = (Links) getApplication();
        String getScheduleDayAPI = application.getScheduleDayAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getScheduleDayAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(DaySchedule.this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrSchedules = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrSchedules.length(); i++) {
                                JSONObject current_obj = arrSchedules.getJSONObject(i);

                                list.add(new scheduleDaysData(
                                        current_obj.getInt("id"),
                                        current_obj.getString("title"),
                                        current_obj.getString("scheduleFrom"),
                                        current_obj.getString("scheduleTo"),
                                        current_obj.getString("remarks"),
                                        current_obj.getString("s24HourFrom"),
                                        current_obj.getString("s24HourTo"),
                                        current_obj.getString("rate"),
                                        bundle.getString("dbDate"),
                                        bundle.getString("formattedDate"),
                                        current_obj.getString("bidAmount"),
                                        isFromNewsFeed,
                                        current_obj.getInt("isPass"),
                                        current_obj.getInt("hasBid"),
                                        current_obj.getInt("isAccepted"),
                                        current_obj.getString("emailAddress")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,1);
                            rvSchedules.setLayoutManager(mLayoutManager);
                            tvRecords.setText("Total Records : " + list.size());

                            adapter = new scheduleDaysAdapter(list);
                            rvSchedules.setAdapter(adapter);
                            dialog.dismiss();

                            if (arrSchedules.length() == 0) {
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
                params.put("currentDate", bundle.getString("dbDate"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}