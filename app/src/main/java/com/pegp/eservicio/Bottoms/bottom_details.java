package com.pegp.eservicio.Bottoms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pegp.eservicio.DaySchedule;
import com.pegp.eservicio.ForgotPassword;
import com.pegp.eservicio.Links;
import com.pegp.eservicio.Login;
import com.pegp.eservicio.MainActivity;
import com.pegp.eservicio.R;
import com.pegp.eservicio.ScheduleDays.scheduleDaysData;
import com.pegp.eservicio.Scheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

public class bottom_details extends BottomSheetDialogFragment {
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    TextView tvTitle, tvTime, tvMinimumRate, tvRemarks,tvBidAmount;
    CheckBox chkDefault;
    EditText etBid, etAddress;
    LinearLayout lnSave,lnAddressHolder;

    Dialog dialog;

    View v;

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bottom_details, container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        tvTitle = v.findViewById(R.id.tvTitle);
        tvTime = v.findViewById(R.id.tvTime);
        tvMinimumRate = v.findViewById(R.id.tvMinimumRate);
        tvRemarks = v.findViewById(R.id.tvRemarks);
        chkDefault = v.findViewById(R.id.chkDefault);
        etBid = v.findViewById(R.id.etBid);
        etAddress = v.findViewById(R.id.etAddress);
        lnSave = v.findViewById(R.id.lnSave);
        tvBidAmount = v.findViewById(R.id.tvBidAmount);
        lnAddressHolder = v.findViewById(R.id.lnAddressHolder);

        Activity activity = unwrap(v.getContext());
        sp = (SharedPreferences) v.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);

        tvTitle.setText(sp.getString("scheduleDetails", "").split("~")[0]);
        tvTime.setText(sp.getString("scheduleDetails", "").split("~")[1]);
        tvMinimumRate.setText("P" + sp.getString("scheduleDetails", "").split("~")[2]);
        tvRemarks.setText(sp.getString("scheduleDetails", "").split("~")[3]);
        tvBidAmount.setText("P" + sp.getString("scheduleDetails", "").split("~")[5]);
        etBid.setText(tvMinimumRate.getText().toString().replace(",","").replace("P",""));

        if (sp.getInt("isAccepted",0) == 1) {
            lnAddressHolder.setVisibility(View.GONE);
            lnSave.setVisibility(View.GONE);
        }

        if (sp.getString("scheduleDetails", "").split("~")[6].equals("1")) {
            Toast.makeText(activity, "Unable to book. This schedule is already closed", Toast.LENGTH_SHORT).show();
            etBid.setEnabled(false);
            etAddress.setEnabled(false);
            chkDefault.setEnabled(false);
            lnSave.setEnabled(false);
            lnSave.setAlpha(0.3F);
        }

        chkDefault.setOnClickListener(v1 -> {
            if (((CheckBox) v1).isChecked()) {
                etAddress.setEnabled(false);
                etAddress.setText(sp.getString("fullAddress", ""));
            } else {
                etAddress.setEnabled(true);
                etAddress.setText("");
            }
        });

        lnSave.setOnClickListener(view -> {
            String messageEmail = "Your appointment with a date of " + sp.getString("scheduleDetails", "").split("~")[7] + " and time of " + tvTime.getText() + " has a book with an amount of P" + GenerateFormat(Double.parseDouble(etBid.getText().toString()));

            if (etBid.getText().toString().equals("") || etAddress.getText().toString().equals("")) {
                Toast.makeText(v.getContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(etBid.getText().toString()) < Double.parseDouble(tvMinimumRate.getText().toString().replace(",","").replace("P",""))) {
                Toast.makeText(v.getContext(), "Unable to book. Amount was too low", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(etBid.getText().toString()) < Double.parseDouble(tvBidAmount.getText().toString().replace(",","").replace("P",""))) {
                Toast.makeText(v.getContext(), "Unable to book. Amount was too low", Toast.LENGTH_SHORT).show();
            } else {
                Links application = (Links) getActivity().getApplication();
                String bidApi = application.bidApi;

                dialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, bidApi,
                        new Response.Listener<String>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    Boolean error = obj.getBoolean("error");
                                    String message = obj.getString("message");

                                    Toast.makeText(v.getContext(), message, Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    Log.e("Response", response);


                                    if (!error) {
                                        dismiss();
                                        ((DaySchedule)activity).getScheduleDays();

                                        if (!message.equals("") || !message.equals(null)) {
                                            sendEmail(sp.getString("scheduleDetails", "").split("~")[8],messageEmail);
                                        }
                                    }

                                } catch (JSONException | AddressException e) {
                                    e.printStackTrace();

                                    Toast.makeText(v.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error.toString()

                                Toast.makeText(v.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("taskID", sp.getString("scheduleDetails", "").split("~")[4]);
                        params.put("userID", sp.getInt("id", 0) + "");
                        params.put("bidAmount", etBid.getText().toString());
                        params.put("address",etAddress.getText().toString());
                        params.put("message",messageEmail);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                requestQueue.add(stringRequest);
            }
        });

        return v;
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
                String fullName    = sp.getString("fullName","");
                String currentDate = sp.getString("scheduleDetails", "").split("~")[7];

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(senderEmail));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    message.setSubject("Booking Confirmation");
                    message.setText(fullName + " booked your schedule on " + currentDate + " at " + tvTime.getText().toString());
                    Transport.send(message);

                    dialog.dismiss();

                    Toast.makeText(v.getContext(), "Book has been sent. Please wait for the freelancers to contact you", Toast.LENGTH_LONG).show();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            dialog.dismiss();

            Log.e("Error", e.getMessage());

            Toast.makeText(v.getContext(), "Unable to send email", Toast.LENGTH_LONG).show();
        }
    }

    public static String GenerateFormat(Double value) {
        if (value == null) return "";
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
        if (value%1 == 0) nf.setMinimumFractionDigits(0);
        else nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(true);
        return nf.format(value);
    }
}
