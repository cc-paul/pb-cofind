package com.pegp.eservicio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackForm extends AppCompatActivity {
    EditText etFeedBack;
    TextView tvIsLikedMessage;
    ImageView imgIsLiked;
    LinearLayout lnIsLiked,lnSave,lnBack;

    Bundle bundle;

    Integer isLiked,userID,freelancerID;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        etFeedBack = findViewById(R.id.etFeedBack);
        tvIsLikedMessage = findViewById(R.id.tvIsLikedMessage);
        imgIsLiked = findViewById(R.id.imgIsLiked);
        lnIsLiked = findViewById(R.id.lnIsLiked);
        lnSave = findViewById(R.id.lnSave);
        lnBack = findViewById(R.id.lnBack);

        bundle = getIntent().getExtras();
        isLiked = bundle.getInt("isLiked");
        freelancerID = bundle.getInt("freelancerID");
        userID = sp.getInt("currentID",0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        changeIsLike();

        lnIsLiked.setOnClickListener(view -> {

            if (isLiked == 1) {
                isLiked = 0;
            } else {
                isLiked = 1;
            }

            changeIsLike();
        });

        lnSave.setOnClickListener(view -> {
            if (etFeedBack.getText().toString().trim().length() == 0 && isLiked == 0) {
                Toast.makeText(this, "Nothing to save. Please provide a feedback or applause the Freelancer", Toast.LENGTH_SHORT).show();
            } else {
                sendLike();
            }
        });

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    public void changeIsLike() {
        tvIsLikedMessage.setText(isLiked == 1 ? "You already applaud this Freelancer. Click to remove" : "Give this Freelancer an Applause");
        imgIsLiked.setBackgroundResource(isLiked == 1 ? R.drawable.clapping_active : R.drawable.clapping_disabled);
    }

    public void  sendLike() {
        Links application = (Links) getApplicationContext();
        String sendLikeApi = application.sendLikeApi;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendLikeApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            //Toast.makeText(FeedbackForm.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            if (!error) {
                                if (etFeedBack.getText().toString().trim().length() != 0) {
                                    sendFeedBack();
                                } else {
                                    Toast.makeText(FeedbackForm.this, "Feedback has been sent. We appreciate that", Toast.LENGTH_SHORT).show();
                                    lnBack.performClick();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            dialog.dismiss();
                            Toast.makeText(FeedbackForm.this, "Unable to applause. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(FeedbackForm.this, "Unable to applause. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("likeID", freelancerID.toString());
                params.put("likedByID", userID.toString());
                params.put("isLiked", isLiked.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void sendFeedBack() {
        Links application = (Links) getApplicationContext();
        String sendMessageApi = application.sendMessageApi;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendMessageApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            //Toast.makeText(FeedbackForm.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            if (!error) {
                                Toast.makeText(FeedbackForm.this, "Feedback has been sent. We appreciate that", Toast.LENGTH_SHORT).show();
                                lnBack.performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            dialog.dismiss();
                            Toast.makeText(FeedbackForm.this, "Unable to applause. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(FeedbackForm.this, "Unable to applause. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("feedbackToID", freelancerID.toString());
                params.put("feedbackByID", userID.toString());
                params.put("feedback", etFeedBack.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}