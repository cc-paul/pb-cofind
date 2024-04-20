package com.pegp.eservicio.Bottoms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.pegp.eservicio.Applaud.applaudAdapter;
import com.pegp.eservicio.Applaud.applaudData;
import com.pegp.eservicio.Feedbacks.feedBackData;
import com.pegp.eservicio.Feedbacks.feedbackAdapter;
import com.pegp.eservicio.Links;
import com.pegp.eservicio.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class bottom_applaud extends BottomSheetDialogFragment {
    View v;
    LinearLayout lnBack;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    RecyclerView rvApplaud;
    private RecyclerView.Adapter adapter;

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
        v = inflater.inflate(R.layout.bottom_applaud, container, false);

        lnBack = v.findViewById(R.id.lnBack);
        rvApplaud = v.findViewById(R.id.rvApplaud);

        sp = v.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        lnBack.setOnClickListener(view -> {
            this.dismiss();
        });

        loadLikers(v.getContext());

        return v;
    }

    public void loadLikers(Context context) {
        ArrayList<applaudData> list = new ArrayList<>();

        Links application = (Links) context.getApplicationContext();
        String getLikerApi = application.getLikerApi;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getLikerApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            if (!error) {
                                JSONArray arrApplaud = jsonResponse.getJSONArray("result");
                                for (Integer i = 0; i < arrApplaud.length(); i++) {
                                    JSONObject current_obj = arrApplaud.getJSONObject(i);



                                    list.add(new applaudData(
                                            current_obj.getString("fullName"),
                                            current_obj.getString("imageLink")
                                    ));
                                }

                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                                rvApplaud.setLayoutManager(mLayoutManager);

                                adapter = new applaudAdapter(list);
                                rvApplaud.setAdapter(adapter);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(context, "Unable to get applaud. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, "Unable to get applaud. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", sp.getInt("currentFreelancerID",0) + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public bottom_applaud() {  }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), 0);
        dialog.setContentView(R.layout.bottom_applaud);

        View bottomSheetContainer = dialog.findViewById(R.id.bottomSheetContainer);
        View parent = (View) bottomSheetContainer.getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) params.getBehavior();
        View inflatedView = View.inflate(getContext(), R.layout.bottom_applaud, null);
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
