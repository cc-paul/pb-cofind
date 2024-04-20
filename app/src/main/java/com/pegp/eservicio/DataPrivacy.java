package com.pegp.eservicio;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.pegp.eservicio.Database.DBHandler;

import java.io.File;
import java.util.ArrayList;

public class DataPrivacy extends AppCompatActivity {
    CheckBox chkAgree;
    LinearLayout lnBack,lnProceed;

    Boolean isAgree;
    Integer isRegularUser;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_privacy);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        dbHandler = new DBHandler(DataPrivacy.this);

        chkAgree = findViewById(R.id.chkAgree);
        lnBack = findViewById(R.id.lnBack);
        lnProceed = findViewById(R.id.lnProceed);
        isAgree = false;

        chkAgree.setOnClickListener(view -> {
            isAgree = !isAgree;
        });

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        lnProceed.setOnClickListener(view -> {
            if (!isAgree) {
                Toast.makeText(this, "Please agree first before proceeding", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(DataPrivacy.this).create();
                alertDialog.setTitle("Register an Account");
                alertDialog.setMessage("Please select any role before you proceed in registration");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Customer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                isRegularUser = 1;
                                gotoRegistration();
                            }
                        } );

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Freelancer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                isRegularUser = 0;
                                gotoRegistration();
                            }
                        } );

                alertDialog.show();
            }
        });
    }

    public void gotoRegistration() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                editor.putString("serviceGroupID","");
                editor.putString("serviceGroupName","");
                editor.commit();

                dbHandler.createDatabase();
                createFolder();

                Intent goToRegistration = new Intent(DataPrivacy.this, Register.class);
                goToRegistration.putExtra("isRegularUser", isRegularUser);
                startActivity(goToRegistration);
                finish();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(DataPrivacy.this, "Some permissions were denied. Unable to use the registration", Toast.LENGTH_LONG).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
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
}