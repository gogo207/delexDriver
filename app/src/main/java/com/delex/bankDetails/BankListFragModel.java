package com.delex.bankDetails;

import android.util.Log;

import com.delex.pojo.bank.BankList;
import com.delex.pojo.bank.LegalEntity;
import com.delex.pojo.bank.StripeDetailsPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by murashid on 25-Aug-17.
 */

public class BankListFragModel {
    private static final String TAG = "SupportFragModel";
    private BankListFragModelImplement modelImplement;

    BankListFragModel(BankListFragModelImplement modelImplement) {
        this.modelImplement = modelImplement;
    }

    void fetchData(String token) {
        OkHttp3Connection.doOkHttp3Connection(token, ServiceUrl.FETCH_ADD_STRIPE_ACCOUNT, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (result != null) {
                        Log.d(TAG, "onSuccess: " + result);
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("legal_entity")) {
                            StripeDetailsPojo stripeDetailsPojo = new Gson().fromJson(result, StripeDetailsPojo.class);
                            modelImplement.onSuccess(stripeDetailsPojo.getLegal_entity(), stripeDetailsPojo.getExternal_accounts().getData());
                            return;
                        }
                        if (jsonObject.getString("errFlag").equals("1") && jsonObject.getString("errNum").equals("400")) {
                            modelImplement.noStipeAccount();
                        } else {
                            modelImplement.onFailure(jsonObject.getString("errMsg"));
                        }
                    } else {
                        modelImplement.onFailure();
                    }
                } catch (JSONException e) {
                    modelImplement.onFailure();
                }
            }

            @Override
            public void onError(String error) {
                modelImplement.onFailure();
            }
        }, token);
    }

    interface BankListFragModelImplement {
        void onFailure(String failureMsg);

        void onFailure();

        void onSuccess(LegalEntity legalEntity, ArrayList<BankList> bankLists);

        void noStipeAccount();
    }
}

