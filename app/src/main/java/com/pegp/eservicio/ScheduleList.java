package com.pegp.eservicio;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pegp.eservicio.Others.Days;
import com.pegp.eservicio.Others.MonthYearPickerDialog;
import com.pegp.eservicio.Others.Months;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class ScheduleList extends AppCompatActivity {
    TextView tvMonth,tvYear;
    FloatingActionButton btnSubmit;
    ImageView btnPrevMonth,btnNextMonth;
    LinearLayout lnBack;
    View root;

    Typeface face;
    Dialog dialog;

    Integer currentDay,taskCount,monthIndex = 0,userID = 0,otherUserID = 0;
    String currentMonth = "",currentYear;
    ArrayList<Months> months = new ArrayList<>();
    Boolean isIdFromNewsFeed = false;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ArrayList<String> arrDatesToPass = new ArrayList<>();

    Bundle bundle;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = LayoutInflater.from(this).inflate(R.layout.activity_schedule_list, null);
        setContentView(root);

        DateFormat monthFormat = new SimpleDateFormat("MMMM");
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();
        currentMonth = monthFormat.format(date);
        currentYear = yearFormat.format(date);

        bundle = getIntent().getExtras();
        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        userID = sp.getInt("currentID",0);

        try {
            userID = bundle.getInt("selectedID");
            otherUserID = bundle.getInt("selectedID");
            isIdFromNewsFeed = true;
        } catch (Exception e) {
            Log.e("Error1",e.getMessage());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        face = ResourcesCompat.getFont(this, R.font.man_semi);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvMonth = findViewById(R.id.tvMonth);
        tvYear = findViewById(R.id.tvYear);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        lnBack = findViewById(R.id.lnBack);

        tvMonth.setText(currentMonth);
        tvYear.setText(currentYear);

        for (int i = 0; i < 12; i++) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            cal.set(Calendar.MONTH, i);
            String month_name = month_date.format(cal.getTime());

            if (currentMonth.equals(month_name)) {
                monthIndex = i;
            }

            months.add(new Months(i + 1,month_name));
            Log.e("Months", month_name);
        }


//        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
//        int noOfMonths = 12;
//        YearMonth current = YearMonth.of(Calendar.YEAR,0);
//        for (int i = 0; i < noOfMonths; i++) {
////            System.out.println(current.format(monthFormatter));
////            current = current.plusMonths(1);
//            String month_name = current.format(monthFormatter);
//
//            if (currentMonth.equals(month_name)) {
//                monthIndex = i;
//            }
//
//            months.add(new Months(i,month_name));
//            current = current.plusMonths(1);
//
//            Log.e("Testing Month", "Index: " + i + " Month: " + month_name);
//        }

        if (isIdFromNewsFeed) {
            btnSubmit.setVisibility(View.GONE);
        }

        btnSubmit.setOnClickListener(view -> {
            Intent goToAddSchedule = new Intent(ScheduleList.this, Scheduler.class);
            startActivity(goToAddSchedule);
        });

        btnPrevMonth.setOnClickListener(view -> {
            if (monthIndex != 0) {
                monthIndex -= 1;

                tvMonth.setText(months.get(monthIndex).getMonthName());
                setupDays();
                getSchedule(months.get(monthIndex).getMonthID());
            }
        });

        btnNextMonth.setOnClickListener(view -> {
            if (monthIndex != 11) {
                monthIndex += 1;

                tvMonth.setText(months.get(monthIndex).getMonthName());
                setupDays();
                getSchedule(months.get(monthIndex).getMonthID());
            }
        });

        tvYear.setOnClickListener(view -> {
            MonthYearPickerDialog newFragment = new MonthYearPickerDialog();
            newFragment.setValue(Integer.parseInt(currentYear));
            newFragment.show(getSupportFragmentManager(), "DatePicker");
        });

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        currentDay = null;
        taskCount = 0;
        setupDays();
        getSchedule(monthIndex + 1);
    }

    public void setupDays() {
        //Log.e("Current Month","" + months.get(monthIndex).getMonthID());
        //Log.e("Current Year",currentYear);
        //Log.e("Total Days",getMonthDays(months.get(monthIndex).getMonthID(),Integer.parseInt(currentYear)) + "");

        ArrayList<Days> arrRevDate = new ArrayList<>();
        ArrayList<Days> arrFinalDate = new ArrayList<>();

        Integer currentDay = 0,
                previousMonthDays = 0,
                reverseDate = 0,
                countDaysNext = 0,
                daysNextLeft = 0,
                tvCount = 0,
                notFormattedMonth = months.get(monthIndex).getMonthID(),
                totalDays = getMonthDays(months.get(monthIndex).getMonthID(),Integer.parseInt(currentYear));

        String  day1Days = getDays( notFormattedMonth+ "/" + 01 + "/" + currentYear),
                getPrevMonth = notFormattedMonth < 10 ? "0" + notFormattedMonth : "" + notFormattedMonth,
                getPrevDate = getPreviousDate(  + 01 + "-" + getPrevMonth + "-" + currentYear);

        switch (day1Days) {
            case "Sun" :
                    previousMonthDays = 0;
                break;
            case "Mon" :
                    previousMonthDays = 1;
                break;
            case "Tue" :
                    previousMonthDays = 2;
                break;
            case "Wed" :
                    previousMonthDays = 3;
                break;
            case "Thu" :
                    previousMonthDays = 4;
                break;
            case "Fri" :
                    previousMonthDays = 5;
                break;
            case "Sat" :
                    previousMonthDays = 6;
                break;
        }

        arrDatesToPass.clear();
        reverseDate = Integer.parseInt(getPrevDate.split("-")[0]);

        for (Integer i = 0; i < previousMonthDays; i++) {
            arrRevDate.add(new Days(reverseDate,null));
            reverseDate--;
        }

        for (Integer i = arrRevDate.size() - 1; i >= 0; i--) {
            arrFinalDate.add(new Days(arrRevDate.get(i).getDays(),false));
            arrDatesToPass.add(arrRevDate.get(i).getDays().toString());
        }

        for (Integer i = 0; i < totalDays; i++) {
            currentDay++;
            arrFinalDate.add(new Days(currentDay,true));
        }

        daysNextLeft = 42 - (totalDays + arrRevDate.size());

        for (Integer i = 0; i < daysNextLeft; i++) {
            countDaysNext++;
            arrFinalDate.add(new Days(countDaysNext,false));
        }

        for (Integer i = 0; i < arrFinalDate.size(); i++) {
            tvCount++;
            String textViewID = "tvRowCol" + tvCount;
            int tvResID = getResources().getIdentifier(textViewID, "id", getPackageName());
            final TextView currentTVDayCount = (TextView) findViewById(tvResID);
            currentTVDayCount.setText("" + arrFinalDate.get(i).getDays());

            Log.e("Days","TextView : " + textViewID + "  Value " + arrFinalDate.get(i).getDays());

            if (arrFinalDate.get(i).getCurrentMonth()) {
                currentTVDayCount.setTag("Tag" + arrFinalDate.get(i).getDays());
            }

            if (!arrFinalDate.get(i).getCurrentMonth()) {
                currentTVDayCount.setAlpha(0.2f);
            } else {
                currentTVDayCount.setAlpha(1f);
            }
        }
    }

    private String getPreviousDate(String inputDate){
        //inputDate = "15-12-2015"; // for example
        SimpleDateFormat  format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = format.parse(inputDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            c.add(Calendar.DATE, -1);
            inputDate = format.format(c.getTime());
            Log.d("asd", "selected date : "+inputDate);

            System.out.println(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            inputDate ="";
        }
        return inputDate;
    }

    public String getDays(String passedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EE");
        Date d = new Date(passedDate);
        return sdf.format(d);
    }

    public static int getMonthDays(int month, int year) {
        int daysInMonth ;
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        }
        else {
            if (month == 2) {
                daysInMonth = (year % 4 == 0) ? 29 : 28;
            } else {
                daysInMonth = 31;
            }
        }
        return daysInMonth;
    }

    public void callScheduleViaYear(String year) {
        currentYear = year;
        tvYear.setText(year);
        setupDays();
        getSchedule(monthIndex + 1);
    }


    public void getSchedule(Integer monthID) {
        Links application = (Links) getApplication();
        String getScheduleAPI = application.getScheduleAPI;

        Log.e("Month ID",monthID.toString());

        dialog.show();

        Log.e("Pass",TextUtils.join(",",arrDatesToPass));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getScheduleAPI,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");


                            dialog.dismiss();
                            Log.e("Response", response);

                            for (Integer i = 1; i <= 42; i++) {
                                deleteView(i);
                            }

                            if (!error) {
                                JSONArray arrSchedule = obj.getJSONArray("result");

                                if (arrSchedule.length() != 0) {
                                    for (Integer i = 0; i < arrSchedule.length(); i++) {
                                        JSONObject current_obj = arrSchedule.getJSONObject(i);

                                        Integer day = current_obj.getInt("day");
                                        String scheduleFrom = current_obj.getString("scheduleFrom");
                                        String scheduleTo = current_obj.getString("scheduleTo");
                                        Integer doNotAdd = current_obj.getInt("doNotAdd");

                                        if (currentDay != day) {
                                            currentDay = day;
                                            taskCount  = 0;

                                            //deleteView(currentDay);
                                        }

                                        taskCount++;

                                        if (taskCount <= 4) {
                                            createDynamicSchedule(day,scheduleFrom + "\n" + scheduleTo,doNotAdd);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(ScheduleList.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();

                            Log.e("Error 1",e.getMessage());
                            Toast.makeText(ScheduleList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Log.e("Error 2",error.getMessage());
                        Toast.makeText(ScheduleList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss(); 
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("monthNumber", monthID + "");
                params.put("yearNumber", currentYear + "");
                params.put("createdBy", userID + "");
                params.put("otherDays", TextUtils.join(",",arrDatesToPass));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String convertToDay(String date) throws ParseException {
        String input_date = date;
        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
        Date dt1=format1.parse(input_date);
        DateFormat format2 = new SimpleDateFormat("EE");
        String finalDay = format2.format(dt1);

        Log.e("Date Parsed",date);

        return finalDay;
    }

    private void deleteView(int id) throws ParseException {
        String buttonID = "lnRowCol" + id;
        String textViewID = "tvDay" + id;
        String currentDay = id < 10 ? "0" + id : "" + id;
        int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
        int tvResID = getResources().getIdentifier(textViewID, "id", getPackageName());
        final LinearLayout currentLnDay = (LinearLayout) findViewById(resID);
        final TextView currentTVDay = (TextView) findViewById(tvResID);

        //currentTVDay.setText(convertToDay(months.get(monthIndex).getMonthID() + "/" + currentDay + "/" + currentYear));

        currentLnDay.removeAllViews();
        currentLnDay.setOnClickListener(null);
    }

    private void createDynamicSchedule(int day,String schedule,int doNotAdd) {
        if (doNotAdd == 0) {
            TextView tvDay = (TextView) root.findViewWithTag("Tag" + day);

            Integer currentMonth = months.get(monthIndex).getMonthID();
            String sMonth = currentMonth < 10 ? "0" + currentMonth : currentMonth.toString();
            Integer newDate = day + 0;
            String sDay = newDate < 10 ? "0" + newDate : "" + newDate;
            String buttonID = root.getResources().getResourceEntryName(tvDay.getId()).replace("tv","ln");

            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            int tvID = getResources().getIdentifier( root.getResources().getResourceEntryName(tvDay.getId()), "id", getPackageName());

            final LinearLayout currentLnDay = (LinearLayout) findViewById(resID);
            final TextView currentLnDayHeader = (TextView) findViewById(tvID);
            final TextView currentTvSchedule = new TextView(this);
            final TextView currentMoreTv = new TextView(this);
            final TextView currentDayTv = new TextView(this);
            final View lineView = new View(this);
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, 0,1);
            final LinearLayout.LayoutParams lpV = new LinearLayout.LayoutParams(MATCH_PARENT,1,0);
            final LinearLayout.LayoutParams lpVMore = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT,0);

            if (doNotAdd != 1) {
                currentTvSchedule.setText(schedule);
                currentTvSchedule.setTextColor(Color.parseColor("#FFFFFF"));
                currentTvSchedule.setBackgroundColor(Color.parseColor("#5E8CAD"));
                currentTvSchedule.setGravity(Gravity.CENTER);
                currentTvSchedule.setTypeface(face);
                currentTvSchedule.setTextSize(7);
                currentTvSchedule.setLayoutParams(lp);

                lineView.setBackgroundColor(Color.parseColor("#F3F5F9"));
                lineView.setLayoutParams(lpV);

                currentLnDay.addView(currentTvSchedule);
                currentLnDay.addView(lineView);

                currentLnDay.setOnClickListener(view -> {
                    Log.e("Is from news feed",isIdFromNewsFeed.toString());

                    Intent goToScheduleDay = new Intent(ScheduleList.this, DaySchedule.class);
                    goToScheduleDay.putExtra("dbDate",tvYear.getText().toString() + "-" + sMonth + "-" + sDay);
                    goToScheduleDay.putExtra("formattedDate",  sMonth + "/" + sDay + "/" + tvYear.getText().toString());
                    goToScheduleDay.putExtra("isIdFromNewsFeed",  isIdFromNewsFeed);
                    goToScheduleDay.putExtra("otherUserID",  otherUserID);
                    startActivity(goToScheduleDay);
                });

                Log.e("Task Counter","" + taskCount);

                if (taskCount == 4) {
                    currentMoreTv.setText("More...");
                    currentMoreTv.setTextColor(Color.parseColor("#000000"));
                    currentMoreTv.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    currentMoreTv.setGravity(Gravity.LEFT);
                    currentMoreTv.setTypeface(face);
                    currentMoreTv.setTextSize(14);
                    currentMoreTv.setLayoutParams(lpVMore);
                    currentLnDay.addView(currentMoreTv);
                }
            }
        }
    }

    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return dpd;
    }
}