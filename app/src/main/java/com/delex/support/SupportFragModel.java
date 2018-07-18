package com.delex.support;

import android.util.Log;

import com.delex.pojo.SupportData;
import com.delex.pojo.SupportPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by murashid on 25-Aug-17.
 */

public class SupportFragModel {
    private static final String TAG = "SupportFragModel";
    private SupportFragModelImplement modelImplement;

    SupportFragModel(SupportFragModelImplement modelImplement) {
        this.modelImplement = modelImplement;
    }

    void fetchData() {
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.SUPPORT + "/0" + "/2", OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Log.d(TAG, "onSuccess: " + result);
                    SupportPojo supportPojo = new Gson().fromJson(result, SupportPojo.class);
                    if (supportPojo.getErrFlag().equals("0")) {
                        modelImplement.onSuccess(supportPojo.getData());
                    } else {
                        modelImplement.onFailure(supportPojo.getErrMsg());
                    }
                } else {
                    modelImplement.onFailure();
                }
            }

            @Override
            public void onError(String error) {
                modelImplement.onFailure();
            }
        }, "ordinory");
    }

    interface SupportFragModelImplement {
        void onFailure(String failureMsg);

        void onFailure();

        void onSuccess(ArrayList<SupportData> supportDatas);
    }
}
