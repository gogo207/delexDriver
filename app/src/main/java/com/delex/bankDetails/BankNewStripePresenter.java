package com.delex.bankDetails;

import org.json.JSONObject;

/**
 * Created by murashid on 25-Aug-17.
 */

public class BankNewStripePresenter implements BankNewStripeModel.BankNewStripModelImplement {

    private BankNewStripePresenterImplement bankNewStripePresenterImplement;
    private BankNewStripeModel bankNewStripeModel;

    BankNewStripePresenter(BankNewStripePresenterImplement bankNewStripePresenterImplement) {
        this.bankNewStripePresenterImplement = bankNewStripePresenterImplement;
        bankNewStripeModel = new BankNewStripeModel(this);
    }

    void getIp() {
        bankNewStripeModel.fetchIp();
    }

    @Override
    public void ipAddress(String ip) {
        bankNewStripePresenterImplement.ipAddress(ip);
    }



    void addBankDetails(String token, JSONObject jsonObject) {
        bankNewStripePresenterImplement.startProgressBar();
        bankNewStripeModel.addBankAccount(token, jsonObject);
    }

    @Override
    public void onFailure() {
        bankNewStripePresenterImplement.stopProgressBar();
        bankNewStripePresenterImplement.onFailure();
    }

    @Override
    public void onSuccess(String msg) {
        bankNewStripePresenterImplement.stopProgressBar();
        bankNewStripePresenterImplement.onSuccess(msg);
    }


    interface BankNewStripePresenterImplement {
        void startProgressBar();

        void stopProgressBar();

        void onSuccess(String msg);

        void onFailure();

        void ipAddress(String ip);
    }
}
