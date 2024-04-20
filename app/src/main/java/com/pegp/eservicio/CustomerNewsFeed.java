package com.pegp.eservicio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.CustomerPost.CustomerPostAdapter;
import com.pegp.eservicio.CustomerPost.CustomerPostData;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerNewsFeed extends AppCompatActivity {
    RecyclerView rvProfilesCustomer;
    LinearLayout lnBack;
    ImageView imgPost;
    EditText etSearch;

    private RecyclerView.Adapter adapter;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ArrayList<CustomerPostData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_news_feed);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        rvProfilesCustomer = findViewById(R.id.rvProfilesCustomer);
        lnBack = findViewById(R.id.lnBack);
        imgPost = findViewById(R.id.imgPost);
        etSearch = findViewById(R.id.etSearch);

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        imgPost.setOnClickListener(view -> {
            Intent gotoCustomerNewsFeed = new Intent(CustomerNewsFeed.this, customer_post.class);
            startActivity(gotoCustomerNewsFeed);
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadFeed();
                        }
                    }, 1000);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        loadFeed();
    }

    public void updateCustomerList(Integer countComment) {
        Integer updateIndex = sp.getInt("customerRowIndex",0);

        Log.e("Index",updateIndex + "");

        list.get(updateIndex).setCountComment(countComment);
        rvProfilesCustomer.setAdapter(adapter);
        adapter.notifyItemChanged(updateIndex);
    }

    public void loadFeed() {
        list = new ArrayList<>();

        Links application = (Links) getApplication();
        String customerFeedAPI = application.customerFeedAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, customerFeedAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrPost = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrPost.length(); i++) {
                                JSONObject current_obj = arrPost.getJSONObject(i);

                                list.add(new CustomerPostData(
                                        current_obj.getInt("id"),
                                        current_obj.getString("imageLink"),
                                        current_obj.getString("mobileNumber"),
                                        current_obj.getString("address"),
                                        current_obj.getString("content"),
                                        current_obj.getString("dateCreated"),
                                        current_obj.getString("imageLinks"),
                                        current_obj.getString("fullName"),
                                        current_obj.getInt("userID"),
                                        current_obj.getInt("countComment"),
                                        current_obj.getString("email")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                            rvProfilesCustomer.setLayoutManager(mLayoutManager);

                            adapter = new CustomerPostAdapter(list);
                            rvProfilesCustomer.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("search",etSearch.getText().toString().replace("'",""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}