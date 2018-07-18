package com.delex.bankDetails;

import android.util.Log;

import com.google.gson.Gson;

import com.delex.pojo.bank.BankList;
import com.delex.pojo.bank.BankPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;

import org.json.JSONObject;

/**
 * Created by muthu on 1/25/2018.
 */

public class AddBankAccountModel {

    private static final String TAG = "SupportFragModel";
    private AddBankAccountModel.MyProfileModelImplement myProfileModelImplement;

    AddBankAccountModel(AddBankAccountModel.MyProfileModelImplement myProfileModelImplement)
    {
        this.myProfileModelImplement = myProfileModelImplement;
    }

    void fetchData(String token)
    {
        OkHttp3Connection.doOkHttp3Connection(token, ServiceUrl.BANK_DETAILS ,
                OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, " getMasterProfile result " + result);
                        if (result != null) {

                            BankPojo pofilePojo = new Gson().fromJson(result, BankPojo.class);
                            if(pofilePojo.getStatusCode()!=null && pofilePojo.getStatusCode().equals("401")){
                               /* getActivity().startActivity(new Intent(getActivity(), Login.class));
                                getActivity().finish();*/
                                myProfileModelImplement.onFailure();
                            }else {
                                switch (pofilePojo.getErrFlag()) {
                                    case 0:
                                        myProfileModelImplement.onSuccess(pofilePojo.getData());
                                        break;
                                    case 1:
                                        myProfileModelImplement.onFailure(pofilePojo.getErrMsg());
                                        break;
                                }
                            }

                        }else{
                            myProfileModelImplement.onFailure();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                },token);
    }
    interface MyProfileModelImplement
    {
        void onFailure(String failureMsg);
        void onFailure();
        void onSuccess(BankList bankList);

    }
}
