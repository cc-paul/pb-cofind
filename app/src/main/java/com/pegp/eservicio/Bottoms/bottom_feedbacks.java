package com.pegp.eservicio.Bottoms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pegp.eservicio.DaySchedule;
import com.pegp.eservicio.Feedbacks.feedBackData;
import com.pegp.eservicio.Feedbacks.feedbackAdapter;
import com.pegp.eservicio.Links;
import com.pegp.eservicio.MainActivity;
import com.pegp.eservicio.Notif.notificationData;
import com.pegp.eservicio.Profile;
import com.pegp.eservicio.Profiles.ProfileAdapter;
import com.pegp.eservicio.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class bottom_feedbacks extends BottomSheetDialogFragment {
    RecyclerView rvComments;
    private RecyclerView.Adapter adapter;

    View v;
    LinearLayout lnBack,lnCommentContainer;
    EditText etFeedBack;
    ImageView imgSend;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Dialog dialog;

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bottom_comments, container, false);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        sp = v.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        lnBack = v.findViewById(R.id.lnBack);
        lnCommentContainer = v.findViewById(R.id.lnCommentContainer);
        etFeedBack = v.findViewById(R.id.etFeedBack);
        imgSend = v.findViewById(R.id.imgSend);
        rvComments = v.findViewById(R.id.rvComments);

        imgSend.setEnabled(false);
        imgSend.setAlpha(0.50f);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        if (sp.getBoolean("disableComment",false)) {
            lnCommentContainer.setAlpha(0.5f);
            etFeedBack.setEnabled(false);
            imgSend.setEnabled(false);
        }

        etFeedBack.addTextChangedListener(new TextWatcher() {

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

        lnBack.setOnClickListener(view -> {
            this.dismiss();
        });

        imgSend.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Confirmation");
            alert.setMessage("Are you sure you want to submit this feedback?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    sendFeedBack(v.getContext());
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();
        });

        loadComments(v.getContext());

        return v;
    }

    public void loadComments(Context context) {
        ArrayList<feedBackData> list = new ArrayList<>();

        Links application = (Links) context.getApplicationContext();
        String getCommentsApi = application.getCommentsApi;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getCommentsApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            if (!error) {
                                JSONArray arrComments = jsonResponse.getJSONArray("result");
                                for (Integer i = 0; i < arrComments.length(); i++) {
                                    JSONObject current_obj = arrComments.getJSONObject(i);

                                    list.add(new feedBackData(
                                            current_obj.getInt("id"),
                                            current_obj.getString("fullName"),
                                            current_obj.getString("dateCreated"),
                                            current_obj.getString("imageLink"),
                                            current_obj.getString("feedback"),
                                            current_obj.getInt("isYours"),
                                            current_obj.getInt("ableToReply"),
                                            current_obj.getString("replyName"),
                                            current_obj.getString("replyImageLink"),
                                            current_obj.getString("reply"),
                                            current_obj.getDouble("bidAmount"),
                                            current_obj.getInt("userID")
                                    ));
                                }

                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                                rvComments.setLayoutManager(mLayoutManager);

                                adapter = new feedbackAdapter(list);
                                rvComments.setAdapter(adapter);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            dialog.dismiss();
                            Toast.makeText(context, "Unable to get feedback. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.dismiss();
                        Toast.makeText(context, "Unable to get feedback. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", sp.getInt("currentFreelancerID",0) + "");
                params.put("myId", sp.getInt("currentID",0) + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void sendFeedBack(Context context) {
        Links application = (Links) context.getApplicationContext();
        String sendMessageApi = application.sendMessageApi;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendMessageApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");
                            Integer countComment = jsonResponse.getInt("countComment");
                            Integer countSchedule = jsonResponse.getInt("countSchedule");

                            dialog.dismiss();
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Log.e("Response",response);

                            if (!error) {
                                etFeedBack.setText("");
                                loadComments(context);
                                ((MainActivity)getActivity()).updateProfileList(countComment,countSchedule);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            dialog.dismiss();
                            Toast.makeText(context, "Unable to send feedback. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.dismiss();
                        Toast.makeText(context, "Unable to send feedback. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("feedbackToID", sp.getInt("currentFreelancerID",0) + "");
                params.put("feedbackByID", sp.getInt("id",0) + "");
                params.put("feedback", etFeedBack.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public bottom_feedbacks() {  }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), 0);
        dialog.setContentView(R.layout.bottom_comments);

        View bottomSheetContainer = dialog.findViewById(R.id.bottomSheetContainer);
        View parent = (View) bottomSheetContainer.getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) params.getBehavior();
        View inflatedView = View.inflate(getContext(), R.layout.bottom_comments, null);
        inflatedView.measure(0, 0);
        int screenHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
        int statusBarHeight = getStatusBarHeight();

        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setPeekHeight(screenHeight);
            bottomSheetContainer.getLayoutParams().height = bottomSheetBehavior.getPeekHeight();
        }

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetContainer.getLayoutParams().height = screenHeight-statusBarHeight;
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetContainer.getLayoutParams().height = bottomSheetBehavior.getPeekHeight();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        dismiss();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float slideOffset) {
            }
        });

        return dialog;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
