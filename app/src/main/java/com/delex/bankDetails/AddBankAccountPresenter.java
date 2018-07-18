package com.delex.bankDetails;

import android.content.Context;

import com.delex.pojo.bank.BankList;

public class AddBankAccountPresenter implements AddBankAccountModel.MyProfileModelImplement {
    private AddBankAccountModel addBankAccountModel;
    private AddBankAccountPresenter.MyProfilePresenterImplement myProfilePresenterImplement;
    private Context context;

    AddBankAccountPresenter(AddBankAccountPresenter.MyProfilePresenterImplement myProfilePresenterImplement, Context context)
    {
        addBankAccountModel = new AddBankAccountModel(this);
        this.myProfilePresenterImplement = myProfilePresenterImplement;
        this.context=context;
    }

    void getProfileDetails(String token)
    {
       myProfilePresenterImplement.startProgressBar();
        addBankAccountModel.fetchData(token);
    }
    @Override
    public void onFailure(String failureMsg) {
        myProfilePresenterImplement.stopProgressBar();
        myProfilePresenterImplement.onFailure(failureMsg);
    }

    @Override
    public void onFailure() {
        myProfilePresenterImplement.stopProgressBar();
        myProfilePresenterImplement.onFailure();
    }

    @Override
    public void onSuccess(BankList bankList) {
       myProfilePresenterImplement.stopProgressBar();
        myProfilePresenterImplement.setProfileDetails(bankList);
    }


    interface MyProfilePresenterImplement
    {
        void startProgressBar();
        void stopProgressBar();
        void onFailure(String msg);
        void onFailure();
        void setProfileDetails(BankList bankList);
    }
}
