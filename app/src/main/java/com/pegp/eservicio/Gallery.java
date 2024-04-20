package com.pegp.eservicio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pegp.eservicio.Database.DBHandler;
import com.pegp.eservicio.Service.serviceAdapter;
import com.pegp.eservicio.Service.serviceData;
import com.pegp.eservicio.ValidID.validIDAdapter;
import com.pegp.eservicio.ValidID.validIDData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.UUID;

public class Gallery extends AppCompatActivity {
    LinearLayout lnBack;
    ImageView imgGallery;
    TextView tvRecords;
    private DBHandler dbHandler;

    int SELECT_PICTURES = 1;
    ArrayList arrImageList = new ArrayList<Uri>();
    Integer imageCounter,imageIndex;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageTask uploadTask;

    RecyclerView rvGallery;
    private RecyclerView.Adapter adapter;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        lnBack = findViewById(R.id.lnBack);
        imgGallery = findViewById(R.id.imgGallery);
        tvRecords = findViewById(R.id.tvRecords);
        rvGallery = findViewById(R.id.rvGallery);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dbHandler = new DBHandler(Gallery.this);

        Builder builder = new Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);


        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        imgGallery.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES);
        });

        loadGallery();
    }

    public void loadGallery() {
        ArrayList<validIDData> list = dbHandler.getImages();

        Log.e("Response",list.toString());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvGallery.setLayoutManager(mLayoutManager);
        tvRecords.setText("Total Images : " + list.size());

        adapter = new validIDAdapter(list);
        rvGallery.setAdapter(adapter);
    }

    public void deleteImage(String id) {
        dbHandler.deleteImage(id);
        loadGallery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURES) {
            if(resultCode == Activity.RESULT_OK) {


                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    arrImageList.clear();

                    for(int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        arrImageList.add(imageUri);
                    }

                    dialog.show();
                    imageCounter = 1;
                    imageIndex = 0;

                    uploadImage();
                } else {
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
                                uri.toString();
                                dbHandler.addImage(uri.toString());
                                Log.e("Image Link",uri.toString());
                                dialog.dismiss();
                                loadGallery();
                            }).addOnFailureListener(this::onFailure);
                        }
                    });
                }
            }
        }
    }

    public void uploadImage() {
        String pictureID = UUID.randomUUID().toString().substring(0,5);
        StorageReference imageRef = storageReference.child("images/" + pictureID + ".png");
        uploadTask = imageRef.putFile((Uri) arrImageList.get(imageIndex));

        uploadTask.addOnFailureListener(exception -> {

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            private void onFailure(Exception exception) {
                // Handle any errors
            }

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child("images/" + pictureID + ".png").getDownloadUrl().addOnSuccessListener(uri -> {
                    uri.toString();
                    dbHandler.addImage(uri.toString());

                    if (imageCounter < arrImageList.size()) {
                        imageCounter++;
                        imageIndex++;
                        uploadImage();
                    } else {
                        dialog.dismiss();
                        loadGallery();
                    }
                }).addOnFailureListener(this::onFailure);
            }
        });
    }
}