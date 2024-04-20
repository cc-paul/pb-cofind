package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.pegp.eservicio.Messenger.messengerAdapter;
import com.pegp.eservicio.Messenger.messengerData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Messaging extends AppCompatActivity {
    RecyclerView rvChats;

    Bundle bundle;
    Integer senderID,countMessage = 1,isAll = 1;
    String currentMessage = "",chatID;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private RecyclerView.Adapter adapter;

    TextView tvFullName;
    LinearLayout lnBack;
    EditText etMessage;
    ImageView imgSend;

    final Handler handler = new Handler();

    ArrayList<messengerData> listMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        tvFullName = findViewById(R.id.tvFullName);
        lnBack = findViewById(R.id.lnBack);
        etMessage = findViewById(R.id.etMessage);
        imgSend = findViewById(R.id.imgSend);
        rvChats = findViewById(R.id.rvChats);

        bundle = getIntent().getExtras();

        tvFullName.setText(bundle.getString("receiversName"));
        chatID = bundle.getString("chatID");
        senderID = sp.getInt("currentID",0);

        Log.e("Test",chatID + "");

        imgSend.setEnabled(false);
        imgSend.setAlpha(0.50f);

        lnBack.setOnClickListener(view -> {
            handler.removeMessages(0);
            super.onBackPressed();
        });

        etMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imgSend.setEnabled(s.toString().trim().length() != 0 ? true : false);
                imgSend.setAlpha(s.toString().trim().length() != 0 ? 1f : 0.50f);
            }
        });

        imgSend.setOnClickListener(view -> {
            sendMessage();
        });

        loadMessage();

        isAll = 0;

        handler.postDelayed(new Runnable() {
            public void run() {
                loadMessageOne();
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        handler.removeMessages(0);
        super.onBackPressed();
    }

    public void loadMessageOne() {
        Links application = (Links) getApplication();
        String chatOneAPI = application.chatOneAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, chatOneAPI,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            Log.e("Response",response);

                            if (error) {
                                Toast.makeText(Messaging.this,message,Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray arrMessage = obj.getJSONArray("result");
                                for (Integer i = 0; i < arrMessage.length(); i++) {
                                    JSONObject current_obj = arrMessage.getJSONObject(i);

                                    listMessage.add(new messengerData(
                                            current_obj.getInt("id"),
                                            current_obj.getString("imageLink"),
                                            current_obj.getString("message"),
                                            current_obj.getInt("senderID")
                                    ));

                                    adapter.notifyDataSetChanged();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(Messaging.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(Messaging.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("chatID", chatID);
                params.put("senderID", senderID.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void loadMessage() {
        Links application = (Links) getApplication();
        String chatAllAPI = application.chatAllAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, chatAllAPI,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            Log.e("Response",response);
                            Log.e("Response",obj.getString("sql"));

                            if (error) {
                                Toast.makeText(Messaging.this,message,Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray arrMessage = obj.getJSONArray("result");
                                for (Integer i = 0; i < arrMessage.length(); i++) {
                                    JSONObject current_obj = arrMessage.getJSONObject(i);

                                    listMessage.add(new messengerData(
                                            current_obj.getInt("id"),
                                            current_obj.getString("imageLink"),
                                            current_obj.getString("message"),
                                            current_obj.getInt("senderID")
                                    ));

                                    countMessage++;
                                }

                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(Messaging.this,1);
                                rvChats.setLayoutManager(mLayoutManager);

                                adapter = new messengerAdapter(listMessage);
                                rvChats.setAdapter(adapter);
                                rvChats.scrollToPosition(listMessage.size() - 1);

                                if (arrMessage.length() == 0 && isAll == 1) {
                                    Toast.makeText(Messaging.this,"No message found. Enter your message to start conversation", Toast.LENGTH_LONG).show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(Messaging.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(Messaging.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("chatID", chatID);
                params.put("senderID", senderID.toString());
                params.put("isAll", isAll + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void sendMessage() {
        Links application = (Links) getApplication();
        String sendMessageAPI = application.sendMessageAPI;

        currentMessage = etMessage.getText().toString();
        etMessage.setText("");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendMessageAPI,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            String imageLink = obj.getString("imageLink");
                            Integer id = obj.getInt("id");

                            if (error) {
                                Toast.makeText(Messaging.this,message,Toast.LENGTH_LONG).show();
                                etMessage.setText(currentMessage);
                                etMessage.setSelection(etMessage.getText().length());
                            } else {

                                listMessage.add(rvChats.getAdapter().getItemCount() ,new messengerData(
                                        id,
                                        imageLink,
                                        currentMessage,
                                        senderID
                                ));

                                adapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(Messaging.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            etMessage.setText(currentMessage);
                            etMessage.setSelection(etMessage.getText().length());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(Messaging.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        etMessage.setText(currentMessage);
                        etMessage.setSelection(etMessage.getText().length());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("senderID", senderID.toString());
                params.put("chatID", chatID.toString());
                params.put("message", currentMessage);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}