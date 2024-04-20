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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.Profiles.ProfileAdapter;
import com.pegp.eservicio.Profiles.profileData;
import com.pegp.eservicio.Service.serviceAdapter;
import com.pegp.eservicio.Service.serviceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ImageView imgOptions,imgNotif;
    EditText etSearch;
    TextView tvCustomerNewsFeed;

    RecyclerView rvProfiles;
    private RecyclerView.Adapter adapter;
    Dialog dialog;
    Integer userID;

    ArrayList<profileData> profileDataArray = new ArrayList <>();
    ArrayList<profileData> list;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        imgOptions = findViewById(R.id.imgOptions);
        imgNotif = findViewById(R.id.imgNotif);
        rvProfiles = findViewById(R.id.rvProfiles);
        etSearch = findViewById(R.id.etSearch);
        tvCustomerNewsFeed = findViewById(R.id.tvCustomerNewsFeed);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        userID = sp.getInt("currentID",0);

        imgOptions.setOnClickListener(view -> {
            Intent gotoProfileOption = new Intent(MainActivity.this, MenuOptions.class);
            startActivity(gotoProfileOption);
        });

        tvCustomerNewsFeed.setOnClickListener(view -> {
            Intent gotoCustomerNewsFeed = new Intent(MainActivity.this, CustomerNewsFeed.class);
            startActivity(gotoCustomerNewsFeed);
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadProfiles();
                        }
                    }, 1000);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        loadProfiles();
        loadCountNotif();
    }



    private void loadCountNotif() {
        Links application = (Links) getApplication();
        String countNotifApi = application.countNotif;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, countNotifApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Integer countNotif = obj.getInt("countNotif");
                        Integer countMessage = obj.getInt("countMessage");

                        //Toast.makeText(application, "" + count, Toast.LENGTH_SHORT).show();

                        imgNotif.setVisibility(countNotif + countMessage != 0 ? View.VISIBLE : View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", "" + userID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadProfiles() {
        Links application = (Links) getApplication();
        String profilesAPI = application.getProfilesAPI;

        list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, profilesAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(MainActivity.this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrProfile = obj.getJSONArray("result");

                            list.clear();

                            for (Integer i = 0; i < arrProfile.length(); i++) {
                                JSONObject current_obj = arrProfile.getJSONObject(i);

                                list.add(new profileData(
                                        current_obj.getString("imageLink"),
                                        current_obj.getString("fullName"),
                                        current_obj.getString("mobileNumber"),
                                        current_obj.getString("fullAddress"),
                                        current_obj.getString("services"),
                                        current_obj.getString("gender"),
                                        current_obj.getInt("servicesCount"),
                                        current_obj.getInt("id"),
                                        current_obj.getInt("countLikers"),
                                        current_obj.getInt("countMessages"),
                                        current_obj.getInt("countServices"),
                                        current_obj.getInt("isLiked"),
                                        current_obj.getInt("isServiceMade")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                            rvProfiles.setLayoutManager(mLayoutManager);

                            adapter = new ProfileAdapter(list);
                            rvProfiles.setAdapter(adapter);
                            dialog.dismiss();

                            if (arrProfile.length() == 0) {
                                //Toast.makeText(this,"No records found", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("search", etSearch.getText().toString());
                params.put("id", "" + userID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void updateProfileList(Integer countComment,Integer countSchedule) {
        Integer updateIndex = sp.getInt("profileRowIndex",0);

        Log.e("Index",updateIndex + "");

        list.get(updateIndex).setCountMessages(countComment);
        list.get(updateIndex).setCountServices(countSchedule);
        rvProfiles.setAdapter(adapter);
        adapter.notifyItemChanged(updateIndex);
    }
}