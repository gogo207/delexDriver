package com.delex.utility;

import com.delex.pojo.ValidatorPojo;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;

interface AcknowledgementCallback {
    void callback(String bid);
}

/**
 * Created by ads on 17/05/17.
 */

public class AcknowledgeHelper {
    private String bid;
    private long serverTime;
    private int pingId;
    private String token;

    public AcknowledgeHelper(String token, String bid, long severTime, int pingId) {
        this.bid = bid;
        this.serverTime = severTime;
        this.pingId = pingId;
        this.token = token;
    }

    public void updateAptRequest(final AcknowledgementCallback callback) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("ent_booking_id", bid);
            jsonObject.put("serverTime", serverTime);
            jsonObject.put("PingId", pingId);

        } catch (JsonIOException | JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(token, ServiceUrl.ACKNOWLEDGE_TO_BOOKING, OkHttp3Connection.Request_type.POST, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    System.out.println("Booking Acknowledgement Result =  " + result);
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            callback.callback(bid);
                            System.out.println("Booking Acknowledgement Result 0 " + validatorPojo.getErrMsg());

                            break;
                        default:
                            System.out.println("Booking Acknowledgement Result 1" + validatorPojo.getErrMsg());
                            break;
                    }

                }
            }

            @Override
            public void onError(String error) {

                System.out.println("Booking Acknowledgement Error" + error);
            }
        }, token);
    }
}