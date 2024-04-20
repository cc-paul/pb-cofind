package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pegp.eservicio.Bottoms.bottom_feedbacks;
import com.pegp.eservicio.Photos.GalleryAdapter;
import com.pegp.eservicio.Photos.GalleryData;
import com.pegp.eservicio.ValidID.validIDData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Profile extends AppCompatActivity {
    LinearLayout lnBack,lnEditProfile,lnProfile,lnImages,lnGallery,lnChangePassword,lnProfessionalism,btnAddImage,lnSocials,lnScedules,lnFeedBack,lnChat;
    TextView tvFullName,tvEmailAddress,tvGender,tvBirthDay,tvMobileNumber,tvAddress,tvSetOfSkills,tvProfileName;
    ImageView imgProfile,imgNotif;

    Dialog dialog;
    Boolean isGalleryDisplayed = false;

    RecyclerView rvGallery;
    private RecyclerView.Adapter adapter;

    int SELECT_PICTURES = 1;
    ArrayList arrImageList = new ArrayList<Uri>();
    ArrayList arrImageLink = new ArrayList<String>();
    Integer imageCounter,imageIndex,userID = 0;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageTask uploadTask;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String provinceName,municipalityName,barangayName,streetName,serviceIDs;
    Integer provinceID,municipalityID,barangayID,senderID = 0,receiverID = 0;
    Boolean isIDFromNewsFeed = false;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        userID = sp.getInt("currentID",0);
        senderID = userID;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Log.e("User ID",userID + "");

        bundle = getIntent().getExtras();

        try {
            userID = bundle.getInt("selectedID");
            receiverID = userID;
            isIDFromNewsFeed = true;

            getProfile();
        } catch (Exception e) {
            Log.e("Error","This is add account");
        }

        lnBack = findViewById(R.id.lnBack);
        lnEditProfile = findViewById(R.id.lnEditProfile);
        lnProfile = findViewById(R.id.lnProfile);
        lnChangePassword = findViewById(R.id.lnChangePassword);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        tvGender = findViewById(R.id.tvGender);
        tvBirthDay = findViewById(R.id.tvBirthday);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvSetOfSkills = findViewById(R.id.tvSetOfSkills);
        rvGallery = findViewById(R.id.rvGallery);
        lnImages = findViewById(R.id.lnImages);
        lnGallery = findViewById(R.id.lnGallery);
        btnAddImage = findViewById(R.id.btnAddImage);
        imgProfile = findViewById(R.id.imgProfile);
        lnProfessionalism = findViewById(R.id.lnProfessionalism);
        tvProfileName = findViewById(R.id.tvProfileName);
        lnSocials = findViewById(R.id.lnSocials);
        lnScedules = findViewById(R.id.lnSchedules);
        imgNotif = findViewById(R.id.imgNotif);
        lnFeedBack = findViewById(R.id.lnFeedBack);
        lnChat = findViewById(R.id.lnChat);

        lnProfile.setVisibility(View.GONE);
        //lnImages.setVisibility(View.GONE);

        if (isIDFromNewsFeed) {
            lnEditProfile.setVisibility(View.GONE);
            btnAddImage.setVisibility(View.GONE);
            lnChangePassword.setVisibility(View.GONE);

            /* Code to Show Social Medial Buttons */
            lnSocials.setVisibility(View.VISIBLE);
        } else {
            lnEditProfile.setVisibility(View.VISIBLE);
            btnAddImage.setVisibility(View.VISIBLE);
            lnChangePassword.setVisibility(View.VISIBLE);

            /* Code to Hide Social Medial Buttons */
            lnSocials.setVisibility(View.GONE);
        }

        if (senderID == receiverID) {
            lnChat.setEnabled(false);
            lnChat.setAlpha(0.50f);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        lnBack.setOnClickListener(view -> {
            if (isGalleryDisplayed) {
//                lnProfile.setVisibility(View.VISIBLE);
//                lnImages.setVisibility(View.GONE);
//                isGalleryDisplayed = false;
                getProfile();
            } else {
                super.onBackPressed();
            }
        });

        lnGallery.setOnClickListener(view -> {
            isGalleryDisplayed = true;
            //lnImages.setVisibility(View.VISIBLE);
            //lnProfile.setVisibility(View.GONE);
        });

        btnAddImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES);
        });

        lnChangePassword.setOnClickListener(view -> {
            Intent gotoChangePassword = new Intent(Profile.this, ChangePassword.class);
            gotoChangePassword.putExtra("email", tvEmailAddress.getText().toString());
            startActivity(gotoChangePassword);
        });

        lnEditProfile.setOnClickListener(view -> {
            Intent gotoEditProfile = new Intent(Profile.this, UpdateProfile.class);
            gotoEditProfile.putExtra("mobileNumber", tvMobileNumber.getText().toString());
            gotoEditProfile.putExtra("provinceID", provinceID);
            gotoEditProfile.putExtra("provinceName", provinceName);
            gotoEditProfile.putExtra("municipalityID", municipalityID);
            gotoEditProfile.putExtra("municipalityName", municipalityName);
            gotoEditProfile.putExtra("barangayID", barangayID);
            gotoEditProfile.putExtra("barangayName", barangayName);
            gotoEditProfile.putExtra("streetName", streetName);
            gotoEditProfile.putExtra("serviceIDs", serviceIDs);
            gotoEditProfile.putExtra("servicesName", tvSetOfSkills.getText().toString());
            gotoEditProfile.putExtra("userID", userID);
            startActivity(gotoEditProfile);
        });

        lnScedules.setOnClickListener(view -> {
            Intent goToSchedule = new Intent(Profile.this, ScheduleList.class);
            goToSchedule.putExtra("isIDFromNewsFeed",isIDFromNewsFeed);
            goToSchedule.putExtra("selectedID",userID);
            startActivity(goToSchedule);
        });

        lnFeedBack.setOnClickListener(view -> {
            Activity activity =  unwrap(view.getContext());
            editor.putBoolean("disableComment",true);
            editor.commit();

            bottom_feedbacks bottomSheet = new bottom_feedbacks();
            bottomSheet.show(((AppCompatActivity) activity).getSupportFragmentManager(), "ModalBottomSheet");
        });

        lnChat.setOnClickListener(view -> {
            createGroupChat();
        });
    }

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }

    @Override
    public void onResume(){
        super.onResume();

        if (!isGalleryDisplayed) {
            getProfile();
        }
    }

    @Override
    public void onBackPressed() {
        if (isGalleryDisplayed) {
//            lnProfile.setVisibility(View.VISIBLE);
//            lnImages.setVisibility(View.GONE);
//            isGalleryDisplayed = false;
            getProfile();
        } else {
            Profile.super.onBackPressed();
        }
    }

    public void createGroupChat() {
        Links application = (Links) getApplication();
        String chatGroupAPI = application.chatGroupAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, chatGroupAPI,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            String chatID = obj.getString("chatID");

                            dialog.dismiss();
                            Log.e("Response",response);

                            if (error) {
                                Toast.makeText(Profile.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Intent gotoChatActivity = new Intent(Profile.this, Messaging.class);
                                gotoChatActivity.putExtra("receiversName",tvFullName.getText().toString());
                                gotoChatActivity.putExtra("chatID",chatID + "");
                                startActivity(gotoChatActivity);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("senderID", senderID.toString());
                params.put("receiverID", receiverID.toString());
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

                                    tvFullName.setText(current_obj.getString("fullName"));
                                    tvEmailAddress.setText(current_obj.getString("emailAddress"));
                                    tvGender.setText(current_obj.getString("gender"));
                                    tvBirthDay.setText(current_obj.getString("birthDate"));
                                    tvMobileNumber.setText(current_obj.getString("mobileNumber"));
                                    tvAddress.setText(current_obj.getString("address"));
                                    tvSetOfSkills.setText(current_obj.getString("services").replace(",","\n"));
                                    lnProfile.setVisibility(View.VISIBLE);
                                    //lnImages.setVisibility(View.INVISIBLE);

                                    provinceID = current_obj.getInt("provinceID");
                                    municipalityID = current_obj.getInt("municipalityID");
                                    barangayID = current_obj.getInt("barangayID");
                                    provinceName = current_obj.getString("province");
                                    municipalityName = current_obj.getString("municipalityName");
                                    barangayName = current_obj.getString("barangayName");
                                    streetName = current_obj.getString("streetName");
                                    serviceIDs = current_obj.getString("serviceIDs");

                                    if (isIDFromNewsFeed) {
                                        tvProfileName.setText(current_obj.getString("fullName"));
                                    }

                                    if (current_obj.getString("serviceIDs").equals("0")) {
                                        lnProfessionalism.setVisibility(View.GONE);
                                    }

                                    if (!current_obj.getString("imageLink").equals("0")) {
                                        Glide.with(getApplicationContext()).load(current_obj.getString("imageLink")).diskCacheStrategy(DiskCacheStrategy.DATA).into(imgProfile);
                                    }

                                    ArrayList images = new ArrayList<GalleryData>();
                                    JSONArray arrImages = current_obj.getJSONArray("images");

                                    for (Integer x = 0; x < arrImages.length(); x++) {
                                        JSONObject current_image = arrImages.getJSONObject(x);

                                        images.add(new GalleryData(Integer.parseInt(current_image.getString("id")),current_image.getString("imageLink"),isIDFromNewsFeed));
                                    }

                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                                    rvGallery.setLayoutManager(mLayoutManager);
                                    adapter = new GalleryAdapter(images);
                                    rvGallery.setAdapter(adapter);

//                                    if (isGalleryDisplayed) {
//                                        lnProfile.setVisibility(View.GONE);
//                                        lnImages.setVisibility(View.VISIBLE);
//                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            lnBack.performClick();
                            Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        lnBack.performClick();
                        Toast.makeText(Profile.this, error.toString(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURES) {
            if(resultCode == Activity.RESULT_OK) {
                arrImageLink.clear();

                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    arrImageList.clear();
                    dialog.show();

                    for(int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        Log.e("Image Link",imageUri.toString());
                        arrImageList.add(imageUri);
                    }

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

                                arrImageLink.add(uri.toString());
                                saveImage();

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
                    arrImageLink.add(uri.toString());

                    if (imageCounter < arrImageList.size()) {
                        imageCounter++;
                        imageIndex++;

                        uploadImage();
                    } else {
                        saveImage();
                    }
                }).addOnFailureListener(this::onFailure);
            }
        });
    }

    private void saveImage() {
        Links application = (Links) getApplication();
        String uploadImageApi = application.uploadImageApi;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadImageApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        Log.e("Response",response);

                        Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();

                        if (!error) {
                            getProfile();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userID + "");
                params.put("imageLinks", String.join(",",arrImageLink));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void ImageOptions(Integer indexOptions,Integer imageID) {
        Links application = (Links) getApplication();
        String profileOptionsAPI = application.profileOptionsAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, profileOptionsAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        Log.e("Response",response);

                        Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();
                        dialog.dismiss();

                        if (!error) {
                            getProfile();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("optionID", indexOptions + "");
                params.put("userID", userID.toString());
                params.put("imageID", imageID + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}