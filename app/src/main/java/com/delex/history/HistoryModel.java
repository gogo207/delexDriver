package com.delex.history;

import android.util.Log;

import com.delex.pojo.TripsPojo.Appointments;
import com.delex.pojo.TripsPojo.TripsPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistoryModel {

    private static final String TAG = "HistoryModel";
    private HistoryModelImplement historyModelImplement;

    private SimpleDateFormat XAxisFormat;
    private int differenceDays = 0;
    private ArrayList<Float> totalsForBar;
    private ArrayList<BarEntry> barEntries;

    HistoryModel(HistoryModelImplement historyModelImplement)
    {
        this.historyModelImplement = historyModelImplement;
        XAxisFormat = new SimpleDateFormat("EEE",Locale.US);
        totalsForBar=new ArrayList<>();
        barEntries=new ArrayList<>();

        initDays();
    }

    private void initDays() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String currentDay = XAxisFormat.format(c.getTime()).toUpperCase();
        differenceDays = 0;
        switch (currentDay)
        {
            case "SUN":
                differenceDays = 0;
                break;
            case "MUN":
                differenceDays = 1;
                break;
            case "TUE":
                differenceDays = 2;
                break;
            case "WED":
                differenceDays = 3;
                break;
            case "THU":
                differenceDays = 4;
                break;
            case "FRI":
                differenceDays = 5;
                break;
            case "SAT":
                differenceDays = 6;
                break;
        }

        ArrayList<String> currenCycleDays = new ArrayList<>();
        c.add(Calendar.DATE, -differenceDays);
        for (int i = 0; i <= differenceDays; i++) {
            currenCycleDays.add(XAxisFormat.format(c.getTime()).toUpperCase());
            c.add(Calendar.DATE, +1);
            Log.d(TAG, "currentCycleDays: " + currenCycleDays.get(i));
        }

        ArrayList<String> pastCycleDays = new ArrayList<>();
        pastCycleDays.add("SUN");
        pastCycleDays.add("MON");
        pastCycleDays.add("TUE");
        pastCycleDays.add("WED");
        pastCycleDays.add("THR");
        pastCycleDays.add("FRI");
        pastCycleDays.add("SAT");

        historyModelImplement.onDayInitialized(differenceDays,currenCycleDays,pastCycleDays);
    }

     void fetchData(String token, JSONObject jsonObject, final int selectedTabPosition, final int tabcount)
    {
        OkHttp3Connection.doOkHttp3Connection(token, ServiceUrl.MASTERTRIPS, OkHttp3Connection.Request_type.POST, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: "+result);
                if (result!=null){
                    TripsPojo tripsPojo = new Gson().fromJson(result,TripsPojo.class);

                    if(result.contains("statusCode") && tripsPojo.getStatusCode()==401){
                        historyModelImplement.onFailure(401);
                    }
                    else {
                        switch (tripsPojo.getErrFlag()){
                            case 0:
                                handleDate(tripsPojo,selectedTabPosition,tabcount);
                                break;
                            case 1:
                                historyModelImplement.onFailure(tripsPojo.getErrMsg());
                                break;
                        }
                    }

                }
                else {
                    historyModelImplement.onFailure();
                }
            }
            @Override
            public void onError(String error) {
                historyModelImplement.onFailure();
            }
        },token);
    }

    private void handleDate(TripsPojo tripsPojo, int selectedTabPosition, int tabcount)
    {
        double amountEarned=0;
        for(int i=0;i<tripsPojo.getData().getTotalEarnings().size();i++)
        {
//            amountEarned+=Double.parseDouble(tripsPojo.getData().getAppointments().get(i).getShipemntDetails().get(0).getFare());
            amountEarned+=Double.parseDouble(tripsPojo.getData().getTotalEarnings().get(i).getAmt());
        }

        totalsForBar.clear();
        for(int i=0;i<tripsPojo.getData().getTotalEarnings().size();i++)
        {
            String amt=tripsPojo.getData().getTotalEarnings().get(i).getAmt();
            if(amt!=null && !amt.isEmpty())
            {
                if(!amt.equals("NaN"))
                {
                    totalsForBar.add(Float.parseFloat(amt));
                }
                else
                {
                    totalsForBar.add(0.0f);
                }
            }
            else
            {
                totalsForBar.add(0.0f);
            }
        }

        barEntries.clear();
        float highestvalue = 0;
        int highestPosition = 0 ;

        if(selectedTabPosition == tabcount)
        {
            for(int i = 0; i <= differenceDays; i++)
            {
                barEntries.add(new BarEntry(i,totalsForBar.get(i)));

                if(totalsForBar.get(i) > highestvalue)
                {
                    highestPosition = i;
                    highestvalue = totalsForBar.get(i);
                }
            }
        }
        else
        {
            for(int i= 0 ; i < 7 ; i++)
            {
                barEntries.add(new BarEntry(i,totalsForBar.get(i)));

                if(totalsForBar.get(i) > highestvalue)
                {
                    highestPosition = i;
                    highestvalue = totalsForBar.get(i);
                }
            }
        }

        historyModelImplement.setValues(tripsPojo.getData().getAppointments(),barEntries, String.valueOf(amountEarned),highestPosition);
    }

    interface HistoryModelImplement
    {
        void onDayInitialized(int differenceDays,ArrayList<String> currentCycleDays,ArrayList<String> postCycleDays);
        void setValues(ArrayList<Appointments> appointments,ArrayList<BarEntry> barEntries,String total,int highestPosition);
        void onFailure(String failureMsg);
        void onFailure(int failure);
        void onFailure();
    }
}
