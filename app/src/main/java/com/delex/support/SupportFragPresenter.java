package com.delex.support;

import com.delex.pojo.SupportData;

import java.util.ArrayList;

/**
 * Created by murashid on 25-Aug-17.
 */

public class SupportFragPresenter implements SupportFragModel.SupportFragModelImplement {

    private SupportFragModel supportFragModel;
    private SupportFragPresenterImplement presenterImplement;

    SupportFragPresenter(SupportFragPresenterImplement presenterImplement) {
        supportFragModel = new SupportFragModel(this);
        this.presenterImplement = presenterImplement;
    }

    void getSupport() {
        presenterImplement.startProgressBar();
        supportFragModel.fetchData();
    }

    @Override
    public void onFailure(String failureMsg) {
        presenterImplement.stopProgressBar();
        presenterImplement.onFailure(failureMsg);
    }

    @Override
    public void onFailure() {
        presenterImplement.stopProgressBar();
        presenterImplement.onFailure();
    }

    @Override
    public void onSuccess(ArrayList<SupportData> supportDatas) {
        presenterImplement.stopProgressBar();
        presenterImplement.getSupportDetails(supportDatas);
    }

    interface SupportFragPresenterImplement {
        void startProgressBar();

        void stopProgressBar();

        void onFailure(String msg);

        void onFailure();

        void getSupportDetails(ArrayList<SupportData> supportDatas);
    }
}

