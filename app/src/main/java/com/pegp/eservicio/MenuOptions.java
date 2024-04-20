package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Photos.GalleryAdapter;
import com.pegp.eservicio.Photos.GalleryData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuOptions extends AppCompatActivity {
    LinearLayout lnBack;
    ImageView imgExit;
    CardView crdProfile,crdSchedule,crdNotif,crdAcceptedReservation,crdMessage;
    TextView tvFullName,tvEmailAddress;
    ImageView imgProfile,imgNotif,imgNotifMessage;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Integer userID,isRegularUser;


    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_options);

        lnBack = findViewById(R.id.lnBack);
        imgExit = findViewById(R.id.imgExit);
        crdProfile = findViewById(R.id.crdProfile);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        imgProfile = findViewById(R.id.imgProfile);
        imgNotif = findViewById(R.id.imgNotif);
        imgNotifMessage = findViewById(R.id.imgNotifMessage);
        crdSchedule = findViewById(R.id.crdSchedule);
        crdNotif = findViewById(R.id.crdNotif);
        crdAcceptedReservation = findViewById(R.id.crdAcceptedReservation);
        crdMessage = findViewById(R.id.crdMessage);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        tvFullName.setText(sp.getString("fullName",""));
        tvEmailAddress.setText(sp.getString("emailAddress",""));

        userID = sp.getInt("currentID",0);
        isRegularUser = sp.getInt("isRegularUser", 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        if (isRegularUser == 1) {
            //crdSchedule.setVisibility(View.GONE);
            crdSchedule.setEnabled(false);
            crdSchedule.setAlpha(0.5f);
        }

        imgExit.setOnClickListener(view -> {
            editor.putBoolean("isRememberMe", false);
            editor.putString("username","");
            editor.putString("password","");
            editor.commit();

            Intent gotoLogin = new Intent(MenuOptions.this, Login.class);
            finishAffinity();
            startActivity(gotoLogin);
        });

        crdProfile.setOnClickListener(view -> {
            Intent gotoProfile = new Intent(MenuOptions.this, Profile.class);
            startActivity(gotoProfile);
        });

        crdSchedule.setOnClickListener(view -> {
            Intent goToSchedule = new Intent(MenuOptions.this, ScheduleList.class);
            startActivity(goToSchedule);
        });

        crdNotif.setOnClickListener(view -> {
            Intent gotoNotif = new Intent(MenuOptions.this, Notification.class);
            startActivity(gotoNotif);
        });

        crdAcceptedReservation.setOnClickListener(view -> {
            Intent gotoAccepted = new Intent(MenuOptions.this, AReservation.class);
            gotoAccepted.putExtra("isFromAccepted",true);
            startActivity(gotoAccepted);
        });

        crdMessage.setOnClickListener(view -> {
            Intent gotoMessage = new Intent(MenuOptions.this, FreelanceMessaging.class);
            startActivity(gotoMessage);
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        loadCountNotif();
        getProfile();
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

                        imgNotif.setVisibility(countNotif != 0 ? View.VISIBLE : View.GONE);
                        imgNotifMessage.setVisibility(countMessage != 0 ? View.VISIBLE : View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(MenuOptions.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(MenuOptions.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void getProfile() {
        Links application = (Links) getApplication();
        String profileApi = application.profileApi;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, profileApi,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");

                            dialog.dismiss();
                            Log.e("Response", response);

                            if (!error) {
                                JSONArray arrProfile = obj.getJSONArray("result");
                                for (Integer i = 0; i < arrProfile.length(); i++) {
                                    JSONObject current_obj = arrProfile.getJSONObject(i);

                                    if (!current_obj.getString("imageLink").equals("0")) {
                                        Glide.with(getApplicationContext()).load(current_obj.getString("imageLink")).diskCacheStrategy(DiskCacheStrategy.DATA).into(imgProfile);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            lnBack.performClick();
                            Toast.makeText(MenuOptions.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        lnBack.performClick();
                        Toast.makeText(MenuOptions.this, error.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userID + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}