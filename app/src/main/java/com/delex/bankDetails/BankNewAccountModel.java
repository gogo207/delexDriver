package com.delex.bankDetails;

import android.util.Log;

import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;

import org.json.JSONObject;

/**
 * Created by murashid on 26-Aug-17.
 */

public class BankNewAccountModel {
    private static final String TAG = "BankNewAccountModel";
    private BankNewAccountModelImplement bankNewAccountModelImplement;

    BankNewAccountModel(BankNewAccountModelImplement bankNewAccountModelImplement)
    {
        this.bankNewAccountModelImplement = bankNewAccountModelImplement;
    }

    void addBankAccount(String token,JSONObject jsonObject)
    {
        OkHttp3Connection.doOkHttp3Connection(token, ServiceUrl.BANK_URL, OkHttp3Connection.Request_type.POST, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (result!=null)
                    {
                        Log.d(TAG, "onSuccess: "+result);
                        JSONObject jsonObject  = new JSONObject(result);
                        if(jsonObject.getString("errFlag").equals("0"))
                        {
                            bankNewAccountModelImplement.onSuccess(jsonObject.getString("errMsg"));
                        }
                        else
                        {
                            bankNewAccountModelImplement.onFailure();
                        }
                    }
                    else
                    {
                        bankNewAccountModelImplement.onFailure();
                    }
                }
                catch (Exception e)
                {
                    bankNewAccountModelImplement.onFailure();
                }
            }

            @Override
            public void onError(String error)
            {
                bankNewAccountModelImplement.onFailure();
            }
        },token);
    }

    interface  BankNewAccountModelImplement
    {
        void onFailure();
        void onSuccess(String failureMsg);
    }

}
