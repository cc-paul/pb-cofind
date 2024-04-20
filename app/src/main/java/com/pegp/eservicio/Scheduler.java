package com.pegp.eservicio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegp.eservicio.Others.RangeTimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class Scheduler extends AppCompatActivity {
    LinearLayout lnSaveSchedule,lnBack;
    EditText etTitle,etDate,etMinimumRate,etFrom,etTo,etRemarks;
    Dialog dialog;

    final Calendar myCalendar = Calendar.getInstance();

    Boolean isDateFrom,isSchedule1Hour = false;
    String from24Hour,to24Hour,dbDate,o_from24Hour = "",o_to24Hour = "",o_dbDate = "";
    Integer userID,taskID = 0;
    Boolean isEdit = false;
    String oldDate = "";
    String oldTimeStamp;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Bundle bundle;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);

        lnSaveSchedule = findViewById(R.id.lnSaveSchedule);
        lnBack = findViewById(R.id.lnBack);
        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etMinimumRate = findViewById(R.id.etMinimumRate);
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        etRemarks = findViewById(R.id.etRemarks);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        userID = sp.getInt("currentID",0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        bundle = getIntent().getExtras();

        try {
            etTitle.setText(bundle.getString("title"));
            etDate.setText(bundle.getString("formattedDate"));
            oldDate = bundle.getString("formattedDate");
            etFrom.setText(bundle.getString("scheduleFrom"));
            etTo.setText(bundle.getString("scheduleTo"));
            etMinimumRate.setText(bundle.getString("minimumRate"));
            etRemarks.setText(bundle.getString("remarks"));
            taskID = bundle.getInt("id");
            from24Hour = bundle.getString("s24HourFrom");
            to24Hour = bundle.getString("s24HourTo");
            dbDate = bundle.getString("dbDate");

            o_dbDate = dbDate;
            o_from24Hour = from24Hour;
            o_to24Hour = to24Hour;
            isEdit = false;
            oldTimeStamp = dbDate.replace("-","") + from24Hour.replace(":","");
            Log.e("Old Date",oldTimeStamp);

            getDifference();

            Log.e("ID",from24Hour + "----" + to24Hour);
        } catch (Exception e) {
            Log.e("Error","This is add account");
        }

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        lnSaveSchedule.setOnClickListener(view -> {
            if (from24Hour != null && to24Hour != null) {
                try {
                    getDifference();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }



            if (etDate.getText().toString().equals("") || etMinimumRate.getText().toString().equals("") || etFrom.getText().toString().equals("") || etTo.getText().toString().equals("")) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show();
            } else if (!isSchedule1Hour) {
                Toast.makeText(this, "Your schedule is to short. Please provide a schedule with at least 1 hour duration", Toast.LENGTH_SHORT).show();
            } else {
                String selecteDateTime = from24Hour.replace(":","");
                Boolean isCurrentDateGreater = false;

                if (selecteDateTime.length() == 3) {
                    selecteDateTime = from24Hour.replace(":","") + "0";
                }

                if (!o_from24Hour.equals(from24Hour) || !o_to24Hour.equals(to24Hour)) {
                    isCurrentDateGreater = checkIfDateIsGreater(dbDate.replace("-","") + "" + selecteDateTime);
                }

                if (isCurrentDateGreater) {
                    Toast.makeText(this, "Selected date and time must be greater than current date and time", Toast.LENGTH_SHORT).show();
                } else {
                    saveSchedule();
                }
            }
        });

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabel();
        };

        etDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog =  new DatePickerDialog(this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        etFrom.setOnClickListener(view -> {
            isDateFrom = true;
            openClock();
        });

        etTo.setOnClickListener(view -> {
            isDateFrom = false;
            openClock();
        });
    }

    private boolean checkIfDateIsGreater(String selectedDateTime) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String currentDateTime = df.format(Calendar.getInstance().getTime());
        String selec = df.format(Calendar.getInstance().getTime());

        Log.e("Current Date and Time",currentDateTime);
        Log.e("Slected Date and Time",selectedDateTime);
        Log.e("Test",from24Hour);

        return Long.parseLong(currentDateTime) > Long.parseLong(selectedDateTime) ? true : false;
    }

    private void saveSchedule() {
        Links application = (Links) getApplication();
        String saveScheduleAPI = application.saveScheduleAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, saveScheduleAPI,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            Log.e("Response",response);

                            Toast.makeText(Scheduler.this, message, Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            if (!error) {
                                lnBack.performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(Scheduler.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()
                        Log.e("Error", error.toString());

                        Toast.makeText(Scheduler.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", etTitle.getText().toString());
                params.put("scheduleFrom", dbDate + " " + from24Hour);
                params.put("scheduleTo", dbDate + " " + to24Hour);
                params.put("o_scheduleFrom", o_dbDate + " " + o_from24Hour);
                params.put("o_scheduleTo", o_dbDate + " " + o_to24Hour);
                params.put("remarks", etRemarks.getText().toString());
                params.put("createdBy",userID + "");
                params.put("rate", etMinimumRate.getText().toString());
                params.put("id", taskID + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getDifference() throws ParseException {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
//        Date startDate = simpleDateFormat.parse(from24Hour);
//        Date endDate = simpleDateFormat.parse(to24Hour);
//
//        Log.e("From", startDate.getTime() + "");
//        Log.e("To",endDate.toString());
//
//        long difference = startDate.getTime() - endDate.getTime();
//        Log.e("Diff1",difference + "");
//
//        if (difference<0) {
//            Date dateMax = simpleDateFormat.parse(from24Hour);
//            Date dateMin = simpleDateFormat.parse(to24Hour);
//            difference=(dateMax.getTime() -startDate.getTime() )+(endDate.getTime()-dateMin.getTime());
//        }
//
//        int days = (int) (difference / (1000*60*60*24));
//        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
//        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
//        Log.e("log_tag","Hours: "+hours+", Mins: "+min + "Difference: " + difference);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

        Date date1 = simpleDateFormat.parse(etFrom.getText().toString());
        Date date2 = simpleDateFormat.parse(etTo.getText().toString());

        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);
        Log.i("======= Hours"," :: "+hours);

        if (hours == 0) {
            isSchedule1Hour = false;
            Toast.makeText(this, "Your schedule is to short. Please provide a schedule with at least 1 hour duration", Toast.LENGTH_SHORT).show();
        } else {
            isSchedule1Hour = true;
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy";
        String dbFormat = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(dbFormat, Locale.US);
        etDate.setText(dateFormat.format(myCalendar.getTime()));
        dbDate = dateFormat2.format(myCalendar.getTime());

        Log.e("Date",dbDate);
    }

    private void openClock() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        String title = isDateFrom ? "From" : "To";

        RangeTimePickerDialog mTimePicker;
        mTimePicker = new RangeTimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String current24Hour = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                String current24Min  = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;


                String time = "", md = " AM";
                if(selectedHour == 0) selectedHour = 12;
                else if(selectedHour > 12) {selectedHour -= 12; md = " PM";}
                else if(selectedHour == 12) md = " PM";

                if(selectedHour < 10) time = "0";
                time += selectedHour;
                time += ":";
                if(selectedMinute < 10) time += "0";
                time += selectedMinute;
                time += md;

                if (isDateFrom) {
                    etFrom.setText(time);
                    from24Hour = current24Hour + ":" + selectedMinute;
                } else {
                    etTo.setText(time);
                    to24Hour = current24Hour + ":" + selectedMinute;
                }
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time " + title);
        mTimePicker.setMin(hour, minute);
        mTimePicker.show();
    }


}