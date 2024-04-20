package com.pegp.eservicio.Feedbacks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegp.eservicio.Links;
import com.pegp.eservicio.Login;
import com.pegp.eservicio.MainActivity;
import com.pegp.eservicio.Messaging;
import com.pegp.eservicio.Notif.notificationAdapter;
import com.pegp.eservicio.Notif.notificationData;
import com.pegp.eservicio.Profile;
import com.pegp.eservicio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class feedbackAdapter extends RecyclerView.Adapter<feedbackAdapter.MyViewHolder> {
    private ArrayList<feedBackData> feedBackDataList;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Dialog dialog;

    public feedbackAdapter(ArrayList<feedBackData> feedBackData) {
        this.feedBackDataList = feedBackData;
    }

    @Override
    public feedbackAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_comments, parent, false);
        return new feedbackAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull feedbackAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        feedBackData feedBackData = feedBackDataList.get(position);

        sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        holder.tvName.setText(feedBackData.getName());
        holder.tvComment.setText(feedBackData.getFeedback());
        holder.tvTime.setText(feedBackData.getDateCreated());
        holder.tvName_sc.setText(feedBackData.getReplyName());
        holder.tvComment_sc.setText(feedBackData.getReply());

        AlertDialog.Builder builder = new AlertDialog.Builder(holder.view.getContext());
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        if (feedBackData.getIsYours() != 1) {
            holder.tvRemove.setVisibility(View.GONE);
            holder.lnRemoveHolder.setVisibility(View.GONE);
        }

        if (!feedBackData.getImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(feedBackData.getImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile);
        }

        if (!feedBackData.getReplyImageLink().equals("-")) {
            Glide.with(holder.view.getContext()).load(feedBackData.getReplyImageLink()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile_sc);
        }

        if (holder.tvComment_sc.getText().toString().trim().length() != 0) {
            holder.lnReplyHolder.setVisibility(View.VISIBLE);
        } else {
            holder.lnReplyHolder.setVisibility(View.GONE);
        }

        //Toast.makeText(holder.view.getContext(), "" + sp.getInt("isFNewsFeed",0), Toast.LENGTH_SHORT).show();

        holder.tvRemove.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(holder.view.getContext());
            alert.setTitle("Confirmation");
            alert.setMessage("Are you sure you want to remove this feedback?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    removeAt(position,holder.view.getContext(),feedBackData.getId(),holder.view);
//
//                    Toast.makeText(holder.view.getContext(), "" + sp.getInt("isFNewsFeed",0), Toast.LENGTH_SHORT).show();

                    if (sp.getInt("isFNewsFeed",0) == 1) {
                        removeAt(position,holder.view.getContext(),feedBackData.getId(),holder.view);
                    } else {
                        removeAt2(position,holder.view.getContext(),feedBackData.getId(),holder.view);
                    }
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

        if (sp.getBoolean("disableComment",true)) {
            holder.tvRemove.setVisibility(View.GONE);
        }

        if (feedBackData.getAbleToReply() == 1) {
            holder.lnReplyHolderButton.setVisibility(View.VISIBLE);
        } else {
            holder.lnReplyHolderButton.setVisibility(View.GONE);
        }

        holder.lnMessageHolder.setVisibility(feedBackData.getIsYours() == 1 ? View.GONE : View.VISIBLE);

        holder.lnBid.setVisibility(feedBackData.getBidAmount() == 0 ? View.GONE : View.VISIBLE);
        holder.tvBid.setText(feedBackData.getBidAmount().toString().replace(".0","").replace(".00",".0") + "");

        holder.tvReply.setOnClickListener(view -> {
            showReplyDialog(holder.view.getContext(),holder,feedBackData.getId());
        });

        holder.tvMessage.setOnClickListener(view -> {
            createGroupChat(feedBackData.getName(), sp.getInt("currentID",0), feedBackData.getUserID(),holder.view.getContext());
        });
    }

    public void createGroupChat(String fullName,Integer senderID,Integer receiverID, Context context) {
        Links application = (Links) context.getApplicationContext();
        String chatGroupAPI = application.pubchatGroupAPI;

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
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Intent gotoChatActivity = new Intent(context, Messaging.class);
                                gotoChatActivity.putExtra("receiversName",fullName);
                                gotoChatActivity.putExtra("chatID",chatID + "");
                                context.startActivity(gotoChatActivity);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    void removeAt(int position, Context context,int id,View view) {
        Links application = (Links) context.getApplicationContext();
        String deleteCommentApi = application.deleteCommentApi;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteCommentApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");
                            Integer countComment = jsonResponse.getInt("countComment");
                            Integer countSchedule = jsonResponse.getInt("countSchedule");

                            if (!error) {
                                feedBackDataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, feedBackDataList.size());

                                Activity activity =  unwrap(view.getContext());
                                ((MainActivity) activity).updateProfileList(countComment,countSchedule);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(context, "Unable to delete comment. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Unable to delete comment. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("feedbackToID", sp.getInt("currentFreelancerID",0) + "");
                params.put("id", id + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    void removeAt2(int position, Context context,int id,View view) {
        Links application = (Links) context.getApplicationContext();
        String deleteCommentApi = application.deleteCommentApi2;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteCommentApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");
                            Integer countComment = jsonResponse.getInt("countComment");

                            if (!error) {
                                feedBackDataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, feedBackDataList.size());

                                Activity activity =  unwrap(view.getContext());
                                //((MainActivity) activity).updateProfileList(countComment,countSchedule);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(context, "Unable to delete comment. Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Unable to delete comment. Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("feedbackToID", sp.getInt("currentFreelancerID",0) + "");
                params.put("id", id + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }

    @Override
    public int getItemCount() {
        return feedBackDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvName,tvComment,tvTime,tvRemove,tvReply,tvComment_sc,tvName_sc,tvBid,tvMessage;
        public final ImageView imgProfile,imgProfile_sc;
        public final LinearLayout lnRemoveHolder,lnReplyHolderButton,lnReplyHolder,lnBid,lnMessageHolder;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvName = view.findViewById(R.id.tvName);
            tvComment = view.findViewById(R.id.tvComment);
            tvTime = view.findViewById(R.id.tvTime);
            tvRemove = view.findViewById(R.id.tvRemove);
            imgProfile = view.findViewById(R.id.imgProfile);
            tvReply = view.findViewById(R.id.tvReply);
            lnRemoveHolder = view.findViewById(R.id.lnRemoveHolder);
            lnReplyHolderButton = view.findViewById(R.id.lnReplyHolderButton);
            lnReplyHolder = view.findViewById(R.id.lnReplyHolder);
            tvComment_sc = view.findViewById(R.id.tvComment_sc);
            tvName_sc = view.findViewById(R.id.tvName_sc);
            imgProfile_sc = view.findViewById(R.id.imgProfile_sc);
            lnBid = view.findViewById(R.id.lnBid);
            tvBid = view.findViewById(R.id.tvBid);

            tvMessage = view.findViewById(R.id.tvMessage);
            lnMessageHolder = view.findViewById(R.id.lnMessageHolder);
        }
    }

    private void showReplyDialog(Context c,MyViewHolder holder,Integer feedbackID) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_reply, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText etReply = (EditText) mView.findViewById(R.id.etReply);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (!etReply.getText().toString().equals("")) {


                            sendReply(c,holder,feedbackID,etReply.getText().toString());
                        } else {
                            Toast.makeText(c, "Reply is required", Toast.LENGTH_LONG).show();
                        }
                    }
                })

                .setNegativeButton("Cancel",
                        (dialogBox, id) -> dialogBox.cancel());

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        Typeface typeface = ResourcesCompat.getFont(c, R.font.man_semi);

        Button button1 = alertDialogAndroid.findViewById(android.R.id.button1);
        button1.setAllCaps(false);
        button1.setEnabled(false);
        button1.setAlpha(0.5f);
        button1.setTypeface(typeface);
        button1.setTextColor(c.getResources().getColor(R.color.black));

        Button button2 = alertDialogAndroid.findViewById(android.R.id.button2);
        button2.setAllCaps(false);
        button2.setTypeface(typeface);
        button2.setTextColor(c.getResources().getColor(R.color.black));


        etReply.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 || etReply.getText().toString().trim().length() == 0) {
                    button1.setEnabled(false);
                    button1.setAlpha(0.2f);
                } else {
                    button1.setEnabled(true);
                    button1.setAlpha(1f);
                }
            }
        });
    }

    private void sendReply(Context c,MyViewHolder holder,Integer id,String reply) {
//

        Links application = (Links) c.getApplicationContext();
        String sendReplyAPI = application.sendReplyAPI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendReplyAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            if (!error) {
                                holder.lnReplyHolder.setVisibility(View.VISIBLE);
                                holder.tvComment_sc.setText(reply);
                                holder.tvName_sc.setText(jsonResponse.getString("replyName"));


                                if (!jsonResponse.getString("replyImageLink").equals("-")) {
                                    Glide.with(holder.view.getContext()).load(jsonResponse.getString("replyImageLink")).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgProfile_sc);
                                }
                            } else {
                                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.e("Error",e.getMessage());
                            Toast.makeText(c, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error",error.getMessage());
                        Toast.makeText(c, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("reply", reply);
                params.put("id", id + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
    }
}
