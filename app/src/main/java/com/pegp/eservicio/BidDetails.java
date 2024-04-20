package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class BidDetails extends AppCompatActivity {
    EditText etFirstName,etMiddleName,etLastName,etAddress,etMobileNumber,etEmailAddress,etTitle,etDate,etMinimumRate,etFrom,etTo,etRemarks,etOffer;
    LinearLayout lnAcceptBid,lnBack;
    RadioButton rdMale,rdFemale;
    ImageView imgProfile;
    KonfettiView viewKonfetti;

    Bundle bundle;
    Dialog dialog;

    Integer bidID;
    String taskID;
    String notifMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_details);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etAddress = findViewById(R.id.etAddress);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etMinimumRate = findViewById(R.id.etMinimumRate);
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        etRemarks = findViewById(R.id.etRemarks);
        etOffer = findViewById(R.id.etOffer);
        imgProfile = findViewById(R.id.imgProfile);
        viewKonfetti = findViewById(R.id.viewKonfetti);

        lnAcceptBid = findViewById(R.id.lnAcceptBid);
        lnBack = findViewById(R.id.lnBack);

        rdMale = findViewById(R.id.rdMale);
        rdFemale = findViewById(R.id.rdFemale);

        bundle = getIntent().getExtras();

        etTitle.setText(bundle.getString("title"));
        etDate.setText(bundle.getString("formattedDate"));
        etFrom.setText(bundle.getString("scheduleFrom"));
        etTo.setText(bundle.getString("scheduleTo"));
        etMinimumRate.setText(bundle.getString("minimumRate"));
        etRemarks.setText(bundle.getString("remarks"));

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        taskID = bundle.getInt("id") + "";
        loadBid();

        notifMessage = "Your offer for the schedule of " + etDate.getText().toString() + " with a time of " + etFrom.getText().toString() + " - " + etTo.getText().toString() + " has been accepted by the freelancer";

        lnAcceptBid.setOnClickListener(view -> {
            acceptBid();
        });
    }

    public void loadBid() {
        Links application = (Links) getApplication();
        String hiBidAPI = application.getHiBidAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, hiBidAPI,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            dialog.dismiss();
                            Log.e("Response", response);

                            if (!error) {
                                JSONArray arrBid = obj.getJSONArray("result");
                                for (Integer i = 0; i < arrBid.length(); i++) {
                                    JSONObject current_obj = arrBid.getJSONObject(i);

                                    etOffer.setText(current_obj.getString("bidAmount"));
                                    etAddress.setText(current_obj.getString("address"));
                                    bidID = current_obj.getInt("id");
                                    getProfile(current_obj.getInt("userID"));

                                    if (current_obj.getInt("isAccepted") == 1) {
                                        lnAcceptBid.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(BidDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(BidDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskID", taskID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getProfile(Integer userID) {
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

                                    etFirstName.setText(current_obj.getString("fullName"));
                                    etMobileNumber.setText(current_obj.getString("mobileNumber"));
                                    etEmailAddress.setText(current_obj.getString("emailAddress"));

                                    if (current_obj.getString("gender").equals("Male")) {
                                        rdMale.setChecked(true);
                                    } else {
                                        rdFemale.setChecked(true);
                                    }

                                    if (!current_obj.getString("imageLink").equals("0")) {
                                        Glide.with(getApplicationContext()).load(current_obj.getString("imageLink")).diskCacheStrategy(DiskCacheStrategy.DATA).into(imgProfile);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            lnBack.performClick();
                            Toast.makeText(BidDetails.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        lnBack.performClick();
                        Toast.makeText(BidDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userID.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void acceptBid() {
        Links application = (Links) getApplication();
        String acceptBidAPI = application.acceptBid;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, acceptBidAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");
                            dialog.dismiss();

                            Toast.makeText(BidDetails.this, message, Toast.LENGTH_LONG).show();

                            if (!error) {
                                lnAcceptBid.setVisibility(View.INVISIBLE);

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

                                sendEmail(etEmailAddress.getText().toString(),notifMessage);
                            }
                        } catch (JSONException | AddressException e) {
                            e.printStackTrace();
                            dialog.dismiss();

                            Toast.makeText(BidDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        dialog.dismiss();

                        Toast.makeText(BidDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", bidID + "");
                params.put("message", notifMessage + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void sendEmail(String email, String currentMessage) throws AddressException {
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final String senderEmail = "servicio.ggploternity@gmail.com";
                final String password = "psdbqalpbbkgwavw";
                final String messageToSend = currentMessage;

                Log.e("Current Message", currentMessage);

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, password);
                    }
                });

                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(senderEmail));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    message.setSubject("Bid Accepted");
                    message.setText(messageToSend);
                    Transport.send(message);

                    dialog.dismiss();

                    Toast.makeText(BidDetails.this, "Bid has been accepted. You may now cooperate with your freelancer", Toast.LENGTH_LONG).show();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            dialog.dismiss();

            Log.e("Error", e.getMessage());

            Toast.makeText(BidDetails.this, "Unable to send email", Toast.LENGTH_LONG).show();
        }
    }
}