package com.delex.bankDetails;

import org.json.JSONObject;

/**
 * Created by murashid on 26-Aug-17.
 */

public class BankNewAccountPresenter implements BankNewAccountModel.BankNewAccountModelImplement {
    private BankNewAccountPresenterImple bankNewAccountPresenterImple;
    private BankNewAccountModel bankNewAccountModel;
    BankNewAccountPresenter(BankNewAccountPresenterImple bankNewAccountPresenterImple)
    {
        this.bankNewAccountPresenterImple = bankNewAccountPresenterImple;
        bankNewAccountModel = new BankNewAccountModel(this);
    }

    void addBankDetails(String token,JSONObject jsonObject)
    {
        bankNewAccountPresenterImple.startProgressBar();
        bankNewAccountModel.addBankAccount(token,jsonObject);
    }

    @Override
    public void onFailure() {
        bankNewAccountPresenterImple.stopProgressBar();
        bankNewAccountPresenterImple.onFailure();
    }

    @Override
    public void onSuccess(String msg) {
        bankNewAccountPresenterImple.stopProgressBar();
        bankNewAccountPresenterImple.onSuccess(msg);
    }

    interface BankNewAccountPresenterImple
    {
        void startProgressBar();
        void stopProgressBar();
        void onSuccess(String msg);
        void onFailure();
    }
}
