package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.log.Logger;
import com.pegp.eservicio.Location.locationAdapter;
import com.pegp.eservicio.Location.locationData;
import com.pegp.eservicio.Service.serviceAdapter;
import com.pegp.eservicio.Service.serviceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceSelection extends AppCompatActivity {
    LinearLayout lnBack;
    TextView tvRecords;
    ImageView imgSave;

    Intent intent;

    RecyclerView rvService;
    private RecyclerView.Adapter adapter;

    Dialog dialog;
    String serviceName;
    String serviceIDs;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ArrayList<serviceData> serviceDataArray = new ArrayList <>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_selection);

        lnBack = findViewById(R.id.lnBack);
        tvRecords = findViewById(R.id.tvRecords);
        rvService = findViewById(R.id.rvService);
        imgSave = findViewById(R.id.imgSave);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        intent  = getIntent();
        Bundle extras = intent.getExtras();

        try {
            serviceIDs = extras.getString("serviceIDs","0");
        } catch (Exception e) {
            serviceIDs = "0";
        }

        Log.e("Service ID",serviceIDs);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        loadService();

        imgSave.setOnClickListener(view -> {
            if (serviceDataArray.size() == 0) {
                Toast.makeText(this,"Please select at least 1 service type",Toast.LENGTH_LONG).show();
            } else {
                editor.putInt("isServiceChanged",1);
                editor.commit();
                lnBack.performClick();
            }
        });
    }

    private void loadService() {
        ArrayList<serviceData> list = new ArrayList<>();

        Links application = (Links) getApplication();
        String serviceAPI = application.serviceAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serviceAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(ServiceSelection.this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrService = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrService.length(); i++) {
                                JSONObject current_obj = arrService.getJSONObject(i);
                                Boolean isChecked = false;

                                String[] serviceIDArray = serviceIDs.split(",");

                                for (Integer x = 0; x < serviceIDArray.length; x++) {
                                    if (Integer.parseInt(serviceIDArray[x]) == current_obj.getInt("id")) {
                                        isChecked = true;
                                    }
                                }

                                list.add(new serviceData(
                                        current_obj.getInt("id"),
                                        current_obj.getString("label"),
                                        isChecked
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                            rvService.setLayoutManager(mLayoutManager);
                            tvRecords.setText("Total Records : " + list.size());

                            adapter = new serviceAdapter(list);
                            rvService.setAdapter(adapter);
                            dialog.dismiss();

                            if (arrService.length() == 0) {
                                Toast.makeText(this,"No records found", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(ServiceSelection.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(ServiceSelection.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setServiceIDAndName(Integer serviceID, String serviceName, Boolean isAdded) {
        if (isAdded) {
            serviceDataArray.add(new serviceData(serviceID,serviceName,isAdded));
        } else {
            for (Integer i = 0; i < serviceDataArray.size(); i++) {
                if (serviceDataArray.get(i).getId() == serviceID) {
                    serviceDataArray.remove(serviceDataArray.get(i));
                }
            }
        }

        ArrayList<String> arrServiceID = new ArrayList<>();
        ArrayList<String> arrServiceName = new ArrayList<>();

        arrServiceID.clear();
        arrServiceName.clear();

        for (Integer i = 0; i < serviceDataArray.size(); i++) {
            arrServiceID.add(serviceDataArray.get(i).getId().toString());
            arrServiceName.add(serviceDataArray.get(i).getServiceName());
        }

        editor.putString("serviceGroupID",String.join(",",arrServiceID));
        editor.putString("serviceGroupName",String.join(",",arrServiceName));
        editor.commit();
    }

    public void getBack() {
        lnBack.performClick();
    }
}