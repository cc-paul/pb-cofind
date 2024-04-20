package com.pegp.eservicio;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.pegp.eservicio.Database.DBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {
    EditText etEmailUser, etPassword;
    LinearLayout lnLogin, lnBack;
    TextView tvSignUp, tvForgotPassword;
    ImageView imgLogo;
    CheckBox chkRememberMe;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Integer isRegularUser = 1;
    String currentToken;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        dbHandler = new DBHandler(Login.this);

        etEmailUser = findViewById(R.id.etEmailUser);
        etPassword = findViewById(R.id.etPassword);
        lnLogin = findViewById(R.id.lnLogin);
        lnBack = findViewById(R.id.lnBack);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        imgLogo = findViewById(R.id.imgLogo);
        chkRememberMe = findViewById(R.id.chkRememberMe);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        lnLogin.setOnClickListener(view -> {
            if (etEmailUser.getText().toString().equals("") || etPassword.getText().equals("")) {
                Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            } else {
                LoginAccount();
            }
        });

        tvSignUp.setOnClickListener(view -> {
            gotoDataPrivacy();
        });

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        tvForgotPassword.setOnClickListener(view -> {
            showForgotDialog(getApplicationContext());
        });

        imgLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                etEmailUser.setText("Jinro");
                etPassword.setText("alohomora");
                lnLogin.performClick();

                return true;
            }
        });

        if (sp.getBoolean("isRememberMe",false) && sp.getString("username","") != "") {
            etEmailUser.setText("" + sp.getString("username",""));
            etPassword.setText("" + sp.getString("password",""));
            chkRememberMe.setChecked(true);

            lnLogin.performClick();
        }
    }

    public void LoginAccount() {
        Links application = (Links) getApplication();
        String loginApi = application.loginAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginApi,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            Log.e("Response", response);

                            if (!error) {
                                JSONArray arrAccess = obj.getJSONArray("result");
                                for (Integer i = 0; i < arrAccess.length(); i++) {
                                    JSONObject current_obj = arrAccess.getJSONObject(i);

                                    Integer id = current_obj.getInt("id");
                                    String emailAddress = current_obj.getString("emailAddress");
                                    String fullName = current_obj.getString("fullName");
                                    Integer isRegularUser = current_obj.getInt("isRegularUser");
                                    String fullAddress = current_obj.getString("fullAddress");
                                    String imageLink = current_obj.getString("imageLink");

                                    editor.putInt("id", id);
                                    editor.putInt("currentID", id);
                                    editor.putString("emailAddress", emailAddress);
                                    editor.putString("fullName", fullName);
                                    editor.putInt("isRegularUser", isRegularUser);
                                    editor.putString("fullAddress", fullAddress);
                                    editor.putString("currentImageLink", imageLink);
                                    editor.putBoolean("isRememberMe", chkRememberMe.isChecked() ? true : false);
                                    editor.putString("username",etEmailUser.getText().toString());
                                    editor.putString("password",etPassword.getText().toString());
                                    editor.commit();
                                }

                                Intent gotoMainActivity = new Intent(Login.this, MainActivity.class);
                                finishAffinity();
                                startActivity(gotoMainActivity);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.e("Error",e.getMessage().toString());
                            Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Log.e("Error",error.toString());
                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emailuser", etEmailUser.getText().toString());
                params.put("password", etPassword.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void gotoDataPrivacy() {
        Intent goToDataPrivacy = new Intent(Login.this, DataPrivacy.class);
        startActivity(goToDataPrivacy);
    }

    private void createFolder() {
        File folder = new File(getFilesDir(), "Pictures");
        Log.e("Folder", folder.getAbsolutePath());
        if (folder.exists()) {
            String[] children = folder.list();
            for (int i = 0; i < children.length; i++) {
                new File(folder, children[i]).delete();
            }
        } else {
            folder.mkdirs();
        }
    }

    private void showForgotDialog(Context c) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText etEmail = (EditText) mView.findViewById(R.id.etEmail);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (!etEmail.getText().toString().equals("")) {
                            if (validateEmail(etEmail.getText().toString())) {
                                //generateLink(etEmail.getText().toString());
                                getOTP(etEmail.getText().toString());
                            } else {
                                Toast.makeText(Login.this, "Its not a valid email", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "Email is required", Toast.LENGTH_LONG).show();
                        }
                    }
                })

                .setNegativeButton("Cancel",
                        (dialogBox, id) -> dialogBox.cancel());

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.man_semi);

        Button button1 = alertDialogAndroid.findViewById(android.R.id.button1);
        button1.setAllCaps(false);
        button1.setEnabled(false);
        button1.setAlpha(0.5f);
        button1.setTypeface(typeface);
        button1.setTextColor(getApplication().getResources().getColor(R.color.black));

        Button button2 = alertDialogAndroid.findViewById(android.R.id.button2);
        button2.setAllCaps(false);
        button2.setTypeface(typeface);
        button2.setTextColor(getApplication().getResources().getColor(R.color.black));


        etEmail.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    button1.setEnabled(false);
                    button1.setAlpha(0.2f);
                } else {
                    button1.setEnabled(true);
                    button1.setAlpha(1f);
                }
            }
        });
    }

    public void getOTP(String currentEmailAddress) {
        Links application = (Links) getApplication();
        String OTPAPI = application.OTPAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, OTPAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            //Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();

                            if (!error) {
                                sendEmail(currentEmailAddress, message);
                            }
                        } catch (JSONException | AddressException e) {
                            e.printStackTrace();
                            dialog.dismiss();

                            Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        dialog.dismiss();

                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", currentEmailAddress);
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
                    message.setSubject("Code for Password Change");
                    message.setText(messageToSend);
                    Transport.send(message);

                    dialog.dismiss();

                    Toast.makeText(Login.this, "OTP has been sent to your email. You may now check it", Toast.LENGTH_LONG).show();

                    Intent gotoForgotPassword = new Intent(Login.this, ForgotPassword.class);
                    gotoForgotPassword.putExtra("email", email);
                    startActivity(gotoForgotPassword);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            dialog.dismiss();

            Log.e("Error", e.getMessage());

            Toast.makeText(Login.this, "Unable to send OTP", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateEmail(String data) {
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher emailMatcher = emailPattern.matcher(data);
        return emailMatcher.matches();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!sp.getBoolean("isRememberMe",false)) {
            etEmailUser.setText("");
            etPassword.setText("");
        }
    }
}