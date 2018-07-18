package com.delex.history;

import com.delex.pojo.TripsPojo.Appointments;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by murashid on 29-Aug-17.
 */

public class HistoryPresenter implements HistoryModel.HistoryModelImplement {

    private HistoryPresenterImplement historyPresenterImplement;
    private HistoryModel historyModel;
    HistoryPresenter(HistoryPresenterImplement historyPresenterImplement)
    {
        this.historyPresenterImplement = historyPresenterImplement;
        this.historyModel = new HistoryModel(this);
    }

    void getHistory(String token, JSONObject jsonObject, int selectedTabPosition, int tabcount)
    {
        historyPresenterImplement.startProgressBar();
        historyModel.fetchData(token,jsonObject,selectedTabPosition,tabcount);
    }

    @Override
    public void onDayInitialized(int differenceDays, ArrayList<String> currentCycleDays, ArrayList<String> postCycleDays) {
        historyPresenterImplement.onDayInitialized(differenceDays,currentCycleDays,postCycleDays);
    }

    @Override
    public void setValues(ArrayList<Appointments> appointments, ArrayList<BarEntry> barEntries, String total, int highestPosition) {
        historyPresenterImplement.stopProgressBar();
        historyPresenterImplement.setValues(appointments,barEntries,total,highestPosition);
    }

    @Override
    public void onFailure(String failureMsg) {
        historyPresenterImplement.stopProgressBar();
        historyPresenterImplement.onFailure(failureMsg);
    }

    @Override
    public void onFailure(int failure) {
        historyPresenterImplement.stopProgressBar();
        historyPresenterImplement.onFailure(failure);
    }

    @Override
    public void onFailure() {
        historyPresenterImplement.stopProgressBar();
        historyPresenterImplement.onFailure();
    }
    
    interface HistoryPresenterImplement
    {
        void onDayInitialized(int differenceDays, ArrayList<String> currentCycleDays, ArrayList<String> postCycleDays);
        void setValues(ArrayList<Appointments> appointments, ArrayList<BarEntry> barEntries, String total,int highestPosition);
        void startProgressBar();
        void stopProgressBar();
        void onFailure(String msg);
        void onFailure(int failure);
        void onFailure();
    }
}
