package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.ValidID.validIDData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    EditText etMobileNumber,etProvince,etMunicipality,etBarangay,etStreetName,etTypeOfService;
    LinearLayout lnBack,lnUpdate,lnTypeOfService;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Intent intent;
    String serviceIDs;

    String provinceName = "",municipalityName = "",barangayName = "";
    Integer provinceID = 0,municipalityID = 0,barangayID = 0;
    String groupOfServiceID = "0";
    String statusType = "";
    Integer addressID = 0,userID;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etMobileNumber = findViewById(R.id.etMobileNumber);
        etProvince = findViewById(R.id.etProvince);
        etMunicipality = findViewById(R.id.etMunicipality);
        etBarangay = findViewById(R.id.etBarangay);
        etStreetName = findViewById(R.id.etStreetName);
        etTypeOfService = findViewById(R.id.etTypeOfService);
        lnBack = findViewById(R.id.lnBack);
        lnUpdate = findViewById(R.id.lnUpdate);
        lnTypeOfService = findViewById(R.id.lnTypeOfService);

        intent  = getIntent();
        Bundle extras = intent.getExtras();
        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        editor.putInt("isServiceChanged",0);
        editor.commit();

        userID = extras.getInt("userID",0);

        Log.e("User ID",userID + "");

        serviceIDs = extras.getString("serviceIDs","0");
        etMobileNumber.setText(extras.getString("mobileNumber",""));
        etProvince.setText(extras.getString("provinceName",""));
        etMunicipality.setText(extras.getString("municipalityName",""));
        etBarangay.setText(extras.getString("barangayName",""));
        etStreetName.setText(extras.getString("streetName",""));
        etTypeOfService.setText(extras.getString("servicesName","").replace("\n",","));
        municipalityID = extras.getInt("municipalityID",0);
        provinceID = extras.getInt("provinceID",0);
        barangayID = extras.getInt("barangayID",0);
        groupOfServiceID = extras.getString("serviceIDs","0");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        if (serviceIDs.equals("0")) {
            lnTypeOfService.setVisibility(View.GONE);
        }

        etTypeOfService.setOnClickListener(view -> {
            editor.putInt("isServiceChanged",0);
            editor.commit();

            Intent gotoServiceSelection = new Intent(UpdateProfile.this, ServiceSelection.class);
            gotoServiceSelection.putExtra("serviceIDs",serviceIDs);
            startActivity(gotoServiceSelection);
        });

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
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

        lnUpdate.setOnClickListener(view -> {
            if (serviceIDs.equals("0")) {
                etTypeOfService.setText("~");
            }

            if (etMobileNumber.getText().toString().equals("") || etProvince.getText().toString().equals("") || etMunicipality.getText().toString().equals("") ||
                    etBarangay.getText().toString().equals("") || etStreetName.getText().toString().equals("") || etTypeOfService.getText().toString().equals("")) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else if (etMobileNumber.getText().toString().length() < 11) {
                Toast.makeText(this,"Mobile Number must be 11 digit",Toast.LENGTH_LONG).show();
            } else {
                updateAccount();
            }
        });
    }

    private void updateAccount() {
        Links application = (Links) getApplication();
        String updateProfileAPI = application.updateProfileAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateProfileAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        Log.e("Response",response);

                        Toast.makeText(UpdateProfile.this, message, Toast.LENGTH_LONG).show();
                        dialog.dismiss();

                        if (!error) {
                            lnBack.performClick();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(UpdateProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Toast.makeText(UpdateProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userID + "");
                params.put("mobileNumber", etMobileNumber.getText().toString());
                params.put("provinceID", provinceID.toString());
                params.put("municipalityID", municipalityID.toString());
                params.put("barangayID", barangayID.toString());
                params.put("streetName", etStreetName.getText().toString());
                params.put("serviceIDs", groupOfServiceID);
                params.put("serviceIDs2", etTypeOfService.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

        Intent gotoAddressSelection = new Intent(UpdateProfile.this, AddressSelection.class);
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

    @Override
    public void onResume() {
        super.onResume();

        if (sp.getInt("isLocationChanged",0) != 0) {
            getAddressValue();
        }

        if (sp.getInt("isServiceChanged",0) != 0) {
            etTypeOfService.setText(sp.getString("serviceGroupName",""));
            groupOfServiceID = sp.getString("serviceGroupID","0");
            serviceIDs = sp.getString("serviceGroupID","0");
        }
    }
}