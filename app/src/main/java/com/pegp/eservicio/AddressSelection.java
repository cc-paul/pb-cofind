package com.pegp.eservicio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.Location.locationAdapter;
import com.pegp.eservicio.Location.locationData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressSelection extends AppCompatActivity {
    LinearLayout lnBack;
    TextView tvLocationTitle,tvRecords;

    Intent intent;

    RecyclerView rvLocation;
    private RecyclerView.Adapter adapter;

    Dialog dialog;
    String addressType;
    String addressID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        lnBack = findViewById(R.id.lnBack);
        tvLocationTitle = findViewById(R.id.tvLocationTitle);
        tvRecords = findViewById(R.id.tvRecords);
        rvLocation = findViewById(R.id.rvLocation);

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        intent  = getIntent();
        Bundle extras = intent.getExtras();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        addressType = extras.getString("addressType","");
        addressID = extras.getInt("addressID",0) + "";

        switch (extras.getString("addressType","")) {
            case "province" :
                tvLocationTitle.setText("Select Province");
                break;
            case "municipality" :
                tvLocationTitle.setText("Select Municipality");
                break;
            case "barangay" :
                tvLocationTitle.setText("Select Barangay");
                break;
        }

        loadLocation();
    }

    private void loadLocation() {
        ArrayList<locationData> list = new ArrayList<>();

        Links application = (Links) getApplication();
        String locationApi = application.loacationAPI;

        Log.e("IDS",addressType + "  " + addressID);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, locationApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(AddressSelection.this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrLocation = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrLocation.length(); i++) {
                                JSONObject current_obj = arrLocation.getJSONObject(i);

                                list.add(new locationData(
                                        current_obj.getInt("id"),
                                        current_obj.getString("label")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                            rvLocation.setLayoutManager(mLayoutManager);
                            tvRecords.setText("Total Records : " + list.size());

                            adapter = new locationAdapter(list);
                            rvLocation.setAdapter(adapter);
                            dialog.dismiss();

                            if (arrLocation.length() == 0) {
                                Toast.makeText(this,"No records found", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(AddressSelection.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Toast.makeText(AddressSelection.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("addressType", addressType);
                params.put("id", addressID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getBack() {
        lnBack.performClick();
    }
}