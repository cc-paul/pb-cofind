package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class customer_post extends AppCompatActivity {

    LinearLayout lnUploadImage,lnSubmitReport,lnBack;
    ImageView img1,img2,img3,img4,img5;
    CardView crd1,crd2,crd3,crd4,crd5;
    EditText etContents;

    int SELECT_PICTURES = 1;
    ArrayList arrImageLink = new ArrayList<String>();
    Dialog dialog;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageTask uploadTask;
    Bundle bundle;
    Integer reportedID,reportedByID;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_post);

        Fresco.initialize(this);

        crd1 = findViewById(R.id.crd1);
        crd2 = findViewById(R.id.crd2);
        crd3 = findViewById(R.id.crd3);
        crd4 = findViewById(R.id.crd4);
        crd5 = findViewById(R.id.crd5);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        etContents = findViewById(R.id.etContents);
        lnSubmitReport = findViewById(R.id.lnSubmitReport);
        lnUploadImage = findViewById(R.id.lnUploadImage);
        lnBack = findViewById(R.id.lnBack);

        hideCards();

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        bundle = getIntent().getExtras();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        reportedByID  = sp.getInt("currentID",0);

        lnUploadImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES);
        });

        crd1.setOnClickListener(view -> {
            ImageOptionDialog(0);
        });

        crd2.setOnClickListener(view -> {
            ImageOptionDialog(1);
        });

        crd3.setOnClickListener(view -> {
            ImageOptionDialog(2);
        });

        crd4.setOnClickListener(view -> {
            ImageOptionDialog(3);
        });

        crd5.setOnClickListener(view -> {
            ImageOptionDialog(4);
        });

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        lnSubmitReport.setOnClickListener(view -> {
            if (etContents.getText().toString().equals("")) {
                Toast.makeText(this, "Please provide content", Toast.LENGTH_SHORT).show();
            } else {
                Links application = (Links) getApplication();
                String customerPostAPI = application.customerPostAPI;

                dialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, customerPostAPI,
                        new Response.Listener<String>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    Boolean error = obj.getBoolean("error");
                                    String message = obj.getString("message");


                                    Toast.makeText(customer_post.this, message, Toast.LENGTH_LONG).show();

                                    dialog.dismiss();

                                    if (!error) {
                                        lnBack.performClick();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    Toast.makeText(customer_post.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error.toString()

                                Toast.makeText(customer_post.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("createdBy", reportedByID.toString());
                        params.put("content", etContents.getText().toString());
                        params.put("imageLinks", TextUtils.join("~",arrImageLink));
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        });
    }

    public void hideCards() {
        crd1.setVisibility(View.INVISIBLE);
        crd2.setVisibility(View.INVISIBLE);
        crd3.setVisibility(View.INVISIBLE);
        crd4.setVisibility(View.INVISIBLE);
        crd5.setVisibility(View.INVISIBLE);
    }

    public void ImageOptionDialog(Integer index) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final Integer[] selectedIndex = new Integer[1];

        builder.setTitle("Choose an Option.");
        final String[] choices = new String[]{
                "View Image",
                "Delete"
        };
        builder.setSingleChoiceItems(
                choices,
                -1,
                (dialogInterface, i) -> {
                    selectedIndex[0] = i;

                    Log.e("Selected Index", selectedIndex[0] + "");
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selectedIndex[0] == null) {
                    Toast.makeText(customer_post.this, "Please select an option first", Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedIndex[0] == 0) {
                        new ImageViewer.Builder(customer_post.this, arrImageLink)
                                .setStartPosition(index)
                                .show();
                    } else {
                        arrImageLink.remove(index.intValue());
                        Log.e("Test",arrImageLink.toString());
                        hideCards();
                        loadImage();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURES) {
            if(resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                String pictureID = UUID.randomUUID().toString().substring(0,5);
                dialog.show();

                StorageReference imageRef = storageReference.child("images/" + pictureID + ".png");
                uploadTask = imageRef.putFile(imageUri);

                uploadTask.addOnFailureListener(exception -> {

                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    private void onFailure(Exception exception) {
                        // Handle any errors
                    }
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child("images/" + pictureID + ".png").getDownloadUrl().addOnSuccessListener(uri -> {

                            arrImageLink.add(uri.toString());
                            dialog.dismiss();
                            loadImage();

                        }).addOnFailureListener(this::onFailure);
                    }
                });
            }
        }
    }

    public void loadImage() {
        img1.setImageDrawable(null);
        img2.setImageDrawable(null);
        img3.setImageDrawable(null);
        img4.setImageDrawable(null);
        img5.setImageDrawable(null);



        lnUploadImage.setAlpha(arrImageLink.size() == 5 ? 0.50f : 1f);
        lnUploadImage.setEnabled(arrImageLink.size() == 5 ? false : true);

        if (arrImageLink.size() == 1) {
            Glide.with(this).load(arrImageLink.get(0)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img1);
            crd1.setVisibility(View.VISIBLE);
            crd2.setVisibility(View.INVISIBLE);
            crd3.setVisibility(View.INVISIBLE);
            crd4.setVisibility(View.INVISIBLE);
            crd5.setVisibility(View.INVISIBLE);
        } else if (arrImageLink.size() == 2) {
            Glide.with(this).load(arrImageLink.get(0)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img1);
            Glide.with(this).load(arrImageLink.get(1)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img2);
            crd1.setVisibility(View.VISIBLE);
            crd2.setVisibility(View.VISIBLE);
            crd3.setVisibility(View.INVISIBLE);
            crd4.setVisibility(View.INVISIBLE);
            crd5.setVisibility(View.INVISIBLE);
        } else if (arrImageLink.size() == 3) {
            Glide.with(this).load(arrImageLink.get(0)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img1);
            Glide.with(this).load(arrImageLink.get(1)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img2);
            Glide.with(this).load(arrImageLink.get(2)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img3);
            crd1.setVisibility(View.VISIBLE);
            crd2.setVisibility(View.VISIBLE);
            crd3.setVisibility(View.VISIBLE);
            crd4.setVisibility(View.INVISIBLE);
            crd5.setVisibility(View.INVISIBLE);
        } else if (arrImageLink.size() == 4) {
            Glide.with(this).load(arrImageLink.get(0)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img1);
            Glide.with(this).load(arrImageLink.get(1)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img2);
            Glide.with(this).load(arrImageLink.get(2)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img3);
            Glide.with(this).load(arrImageLink.get(3)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img4);
            crd1.setVisibility(View.VISIBLE);
            crd2.setVisibility(View.VISIBLE);
            crd3.setVisibility(View.VISIBLE);
            crd4.setVisibility(View.VISIBLE);
            crd5.setVisibility(View.INVISIBLE);
        } else if (arrImageLink.size() == 5) {
            Glide.with(this).load(arrImageLink.get(0)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img1);
            Glide.with(this).load(arrImageLink.get(1)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img2);
            Glide.with(this).load(arrImageLink.get(2)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img3);
            Glide.with(this).load(arrImageLink.get(3)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img4);
            Glide.with(this).load(arrImageLink.get(4)).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(img5);
            crd1.setVisibility(View.VISIBLE);
            crd2.setVisibility(View.VISIBLE);
            crd3.setVisibility(View.VISIBLE);
            crd4.setVisibility(View.VISIBLE);
            crd5.setVisibility(View.VISIBLE);
        }
    }
}