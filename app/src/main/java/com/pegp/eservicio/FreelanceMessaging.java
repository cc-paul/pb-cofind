package com.pegp.eservicio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.FreelanceMessageList.freelanceMessageListAdapter;
import com.pegp.eservicio.FreelanceMessageList.freelanceMessageListData;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FreelanceMessaging extends AppCompatActivity {
    RecyclerView rvMessageList;
    EditText etSearch;
    LinearLayout lnBack;
    ArrayList<freelanceMessageListData> list;

    private RecyclerView.Adapter adapter;
    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Integer userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelance_messaging);

        lnBack = findViewById(R.id.lnBack);
        rvMessageList = findViewById(R.id.rvMessageList);
        etSearch = findViewById(R.id.etSearch);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        userID = sp.getInt("currentID",0);

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMessages();
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

        loadMessages();
    }

    public void loadMessages() {
        list = new ArrayList<>();

        Links application = (Links) getApplication();
        String getMessageListAPI = application.getMessageListAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getMessageListAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);
                        Log.e("SQL",obj.getString("sql"));

                        if (error){
                            Toast.makeText(this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrMessage = obj.getJSONArray("result");

                            list.clear();

                            for (Integer i = 0; i < arrMessage.length(); i++) {
                                JSONObject current_obj = arrMessage.getJSONObject(i);

                                list.add(new freelanceMessageListData(
                                        current_obj.getString("imageLink"),
                                        current_obj.getString("name"),
                                        current_obj.getString("lastMessage"),
                                        current_obj.getString("lastDate"),
                                        current_obj.getString("chatID"),
                                        current_obj.getInt("countUnread")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,1);
                            rvMessageList.setLayoutManager(mLayoutManager);

                            adapter = new freelanceMessageListAdapter(list);
                            rvMessageList.setAdapter(adapter);

//                            if (arrMessage.length() == 0) {
//                                Toast.makeText(this,"No messages found", Toast.LENGTH_LONG).show();
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Log.e("Error",e.getMessage());
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Log.e("Error",error.toString());
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userID + "");
                params.put("search", etSearch.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}