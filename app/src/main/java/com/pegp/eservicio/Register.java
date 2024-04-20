package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.log.Logger;
import com.pegp.eservicio.Database.DBHandler;
import com.pegp.eservicio.Location.locationAdapter;
import com.pegp.eservicio.Location.locationData;
import com.pegp.eservicio.ValidID.validIDData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    LinearLayout lnBack,lnRegister,lnTypeOfService;
    EditText etFirstName,etMiddleName,etLastName,etMobileNumber,etProvince,etMunicipality,etBarangay,etBirthdate,
            etStreetName,etTypeOfService,etUsername,etEmailAddress,etPassword,etRepeatPassword;
    RadioButton rdMale,rdFemale;
    TextView tvUpload;

    final Calendar myCalendar = Calendar.getInstance();

    Dialog dialog;
    String statusType = "",gender = "";
    Integer addressID = 0;

    String provinceName = "",municipalityName = "",barangayName = "";
    Integer provinceID = 0,municipalityID = 0,barangayID = 0,isRegularUser;
    String groupOfServiceID = "0";

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Intent intent;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        dbHandler = new DBHandler(Register.this);

        lnBack = findViewById(R.id.lnBack);
        lnRegister = findViewById(R.id.lnRegister);
        lnTypeOfService = findViewById(R.id.lnTypeOfService);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etProvince = findViewById(R.id.etProvince);
        etMunicipality = findViewById(R.id.etMunicipality);
        etBarangay = findViewById(R.id.etBarangay);
        etBirthdate = findViewById(R.id.etBirthdate);
        etStreetName = findViewById(R.id.etStreetName);
        etTypeOfService = findViewById(R.id.etTypeOfService);
        etUsername = findViewById(R.id.etUsername);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        rdMale = findViewById(R.id.rdMale);
        rdFemale = findViewById(R.id.rdFemale);
        tvUpload = findViewById(R.id.tvUpload);

        editor.putInt("isServiceChanged",0);
        editor.commit();

        intent  = getIntent();
        Bundle extras = intent.getExtras();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        isRegularUser = extras.getInt("isRegularUser",0);

        if (extras.getInt("isRegularUser",0) == 1) {
            groupOfServiceID = "0";
            etTypeOfService.setText("-");
            lnTypeOfService.setVisibility(View.GONE);
        }

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabel();
        };

        etBirthdate.setOnClickListener(view -> {
            new DatePickerDialog(Register.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etProvince.setOnClickListener(view -> {
            setAddressType("province");
        });

        etMunicipality.setOnClickListener(view -> {
            setAddressType("municipality");
        });

        etBarangay.setOnClickListener(view -> {
            setAddressType("barangay");
        });

        etTypeOfService.setOnClickListener(view -> {
            editor.putInt("isServiceChanged",0);
            editor.commit();

            Intent gotoServiceSelection = new Intent(Register.this, ServiceSelection.class);
            startActivity(gotoServiceSelection);
        });

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        tvUpload.setOnClickListener(view -> {
            Intent gotoValidIDUpload = new Intent(Register.this, Gallery.class);
            startActivity(gotoValidIDUpload);
        });

        rdMale.setOnClickListener(view -> {
            gender = "male";
        });

        rdFemale.setOnClickListener(view -> {
            gender = "female";
        });

        lnRegister.setOnClickListener(view -> {
            ArrayList<validIDData> images = dbHandler.getImages();

            if (isRegularUser == 1 && images.size() == 0) {
                images.add(new validIDData(null,"~~~"));
            }

            if (etFirstName.getText().toString().equals("")  || etLastName.getText().toString().equals("") || etMobileNumber.getText().toString().equals("")
            || etProvince.getText().toString().equals("") || etMunicipality.getText().toString().equals("") || etBarangay.getText().toString().equals("") || etBirthdate.getText().toString().equals("") ||
            gender.equals("") || etStreetName.getText().toString().equals("") || etTypeOfService.getText().toString().equals("") || etUsername.getText().toString().equals("") || etEmailAddress.getText().toString().equals("")
            || etPassword.getText().toString().equals("") || etRepeatPassword.getText().toString().equals("")) {
                Toast.makeText(this,"Please fill in all required fields",Toast.LENGTH_LONG).show();
            } else if (etMobileNumber.getText().toString().length() < 11) {
                Toast.makeText(this,"Mobile Number must be 11 digit",Toast.LENGTH_LONG).show();
            } else if (getAge(etBirthdate.getText().toString()) < 18) {
                Toast.makeText(this,"Age must be 18 years old and above",Toast.LENGTH_LONG).show();
            } else if (!validateEmail(etEmailAddress.getText().toString().trim())) {
                Toast.makeText(this,"Please provide proper email address",Toast.LENGTH_LONG).show();
            } else if (!etPassword.getText().toString().equals(etRepeatPassword.getText().toString())) {
                Toast.makeText(this,"Password and Repeat Password does not match",Toast.LENGTH_LONG).show();
            } else if (etPassword.getText().toString().length() < 8) {
                Toast.makeText(Register.this, "Password must be 8 characters long", Toast.LENGTH_LONG).show();
            } else if (images.size() == 0) {
                Toast.makeText(this, "Please provide at least 1 valid ID", Toast.LENGTH_LONG).show();
            } else {
                registerAccount();
            }
        });
    }

    private void registerAccount() {
        Links application = (Links) getApplication();
        String registerAPI = application.registerAPI;

        ArrayList<String> arrImageLink = new ArrayList<>();
        ArrayList<validIDData> images = dbHandler.getImages();
        arrImageLink.clear();

        if (isRegularUser == 1 && images.size() == 0) {
            arrImageLink.add("~~~");
        }

        for (Integer i = 0; i < images.size(); i++) {
            arrImageLink.add(images.get(i).getImageLink());
        }

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        Log.e("Response",response);

                        Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                        dialog.dismiss();

                        if (!error) {
                            lnBack.performClick();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("isRegularUser", isRegularUser.toString());
                params.put("firstName", etFirstName.getText().toString());
                params.put("middleName", etMiddleName.getText().toString());
                params.put("lastName", etLastName.getText().toString());
                params.put("mobileNumber", etMobileNumber.getText().toString());
                params.put("provinceID", provinceID.toString());
                params.put("municipalityID", municipalityID.toString());
                params.put("barangayID", barangayID.toString());
                params.put("birthDate", etBirthdate.getText().toString());
                params.put("gender", gender);
                params.put("streetName", etStreetName.getText().toString());
                params.put("username", etUsername.getText().toString());
                params.put("emailAddress", etEmailAddress.getText().toString());
                params.put("password", etPassword.getText().toString());
                params.put("serviceIDs", groupOfServiceID);
                params.put("imageLinks", String.join(",",arrImageLink));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sp.getInt("isLocationChanged",0) != 0) {
            getAddressValue();
        }

        if (sp.getInt("isServiceChanged",0) != 0) {
            etTypeOfService.setText(sp.getString("serviceGroupName",""));
            groupOfServiceID = sp.getString("serviceGroupID","0");
        }
    }

    private void setAddressType(String currentStatusType) {
        statusType = currentStatusType;

        switch (statusType) {
            case "province" :
                    addressID = 0;
                break;
            case "municipality" :
                    addressID = provinceID;
                break;
            case "barangay" :
                    addressID = municipalityID;
                break;
        }

        editor.putInt("isLocationChanged",0);
        editor.commit();

        Intent gotoAddressSelection = new Intent(Register.this, AddressSelection.class);
        gotoAddressSelection.putExtra("addressID", addressID);
        gotoAddressSelection.putExtra("addressType", statusType);
        startActivity(gotoAddressSelection);
    }

    private void getAddressValue() {
        //Logger.e("Address ID",statusType +  Integer.parseInt(sp.getString("id","0")) + "");

        switch (statusType) {
            case "province" :

                provinceID = Integer.parseInt(sp.getString("id","0"));
                provinceName = sp.getString("location", "");
                etProvince.setText(provinceName);
                etMunicipality.setText("");
                etBarangay.setText("");


                break;
            case "municipality" :

                municipalityID = Integer.parseInt(sp.getString("id","0"));
                municipalityName = sp.getString("location", "");
                etMunicipality.setText(municipalityName);
                etBarangay.setText("");

                break;
            case "barangay" :

                barangayID = Integer.parseInt(sp.getString("id","0"));
                barangayName = sp.getString("location", "");
                etBarangay.setText(barangayName);

                break;
        }
    }

    private void updateLabel(){
        String myFormat="MM/dd/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etBirthdate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private int getAge(String dobString) {

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }

    private boolean validateEmail(String data) {
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher emailMatcher = emailPattern.matcher(data);
        return emailMatcher.matches();
    }
}