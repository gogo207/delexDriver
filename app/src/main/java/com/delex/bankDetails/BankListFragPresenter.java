package com.delex.bankDetails;


import com.delex.pojo.bank.BankList;
import com.delex.pojo.bank.LegalEntity;

import java.util.ArrayList;

/**
 * Created by murashid on 25-Aug-17.
 */

public class BankListFragPresenter implements BankListFragModel.BankListFragModelImplement {

    private BankListFragPresenterImplement bankListFragPresenterImplement;
    private BankListFragModel bankListFragModel;

    BankListFragPresenter(BankListFragPresenterImplement bankListFragPresenterImplement) {
        bankListFragModel = new BankListFragModel(this);
        this.bankListFragPresenterImplement = bankListFragPresenterImplement;
    }

    void getBankDetails(String token) {
        bankListFragPresenterImplement.startProgressBar();
        bankListFragModel.fetchData(token);
    }

    @Override
    public void onFailure(String failureMsg) {
        bankListFragPresenterImplement.stopProgressBar();
        bankListFragPresenterImplement.onFailure(failureMsg);
    }

    @Override
    public void onFailure() {
        bankListFragPresenterImplement.stopProgressBar();
        bankListFragPresenterImplement.onFailure();
    }

    @Override
    public void onSuccess(LegalEntity legalEntity, ArrayList<BankList> bankLists) {
        bankListFragPresenterImplement.stopProgressBar();
        bankListFragPresenterImplement.onSuccess(legalEntity, bankLists);
    }

    @Override
    public void noStipeAccount() {
        bankListFragPresenterImplement.stopProgressBar();
        bankListFragPresenterImplement.showAddStipe();
    }

    interface BankListFragPresenterImplement {
        void startProgressBar();

        void stopProgressBar();

        void onFailure(String msg);

        void onFailure();

        void onSuccess(LegalEntity legalEntity, ArrayList<BankList> bankLists);

        void showAddStipe();
    }
}
