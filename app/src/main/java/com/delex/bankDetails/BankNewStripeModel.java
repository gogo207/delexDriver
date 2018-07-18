package com.delex.bankDetails;

import android.os.AsyncTask;
import android.util.Log;

import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class BankNewStripeModel {

    private static final String TAG = "BankNewStripeModel";
    private BankNewStripModelImplement bankNewStripModelImplement;

    BankNewStripeModel(BankNewStripModelImplement bankNewStripModelImplement) {
        this.bankNewStripModelImplement = bankNewStripModelImplement;
    }

    void fetchIp() {
        new SendfeedbackJob().execute();
    }

    void addBankAccount(String token, JSONObject jsonObject) {
        OkHttp3Connection.doOkHttp3Connection(token, ServiceUrl.FETCH_ADD_STRIPE_ACCOUNT, OkHttp3Connection.Request_type.POST, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (result != null) {
                        Log.d(TAG, "onSuccess: " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("errFlag").equals("0")) {
                            bankNewStripModelImplement.onSuccess(jsonObject.getString("errMsg"));
                        } else {
                            bankNewStripModelImplement.onFailure();
                        }
                    } else {
                        bankNewStripModelImplement.onFailure();
                    }
                } catch (Exception e) {
                    bankNewStripModelImplement.onFailure();
                }
            }

            @Override
            public void onError(String error) {
                bankNewStripModelImplement.onFailure();
            }
        }, token);
    }

    interface BankNewStripModelImplement {
        void onFailure();

        void onSuccess(String failureMsg);

        void ipAddress(String ip);


    }

    private class SendfeedbackJob extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            // do above Server call here
            try {
                return InetAddress.getLocalHost().toString();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return "007";
            }
        }

        @Override
        protected void onPostExecute(String message) {
            bankNewStripModelImplement.ipAddress(message);
        }
    }

}
