package com.delex.app;

import android.util.Log;

import com.delex.driver.R;
import com.delex.pojo.AssignedAppointments;
import com.delex.pojo.AssignedTripsPojo;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ads on 13/05/17.
 *
 */

public class MainWorkerClass {


    private MainPresenter presenter;
    private SessionManager sessionManager;
    private int errorNumber = 0;
    private String TAG = MainWorkerClass.class.getSimpleName();

    public MainWorkerClass(SessionManager sessionManager, MainPresenter presenter) {
        this.presenter = presenter;
        this.sessionManager = sessionManager;
    }

    /**
     * <h2>updateStatus</h2>
     * <p>this method is used to update driver status
     * 3. update online status
     * 4. update offline status</p>
     */
    public void updateStatus() {
        presenter.showProgress();
        JSONObject reqObject = new JSONObject();
        try {
            JSONArray jsonArray;
            String pubnubStr = "";
            try {
                if (!sessionManager.getBookings().isEmpty()) {
                    jsonArray = new JSONArray(sessionManager.getBookings());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (pubnubStr.isEmpty()) {
                            pubnubStr = jsonObject.getString("bid") + "|" + jsonObject.getString("custChn") + "|" + jsonObject.getString("status");
                        } else {
                            pubnubStr = pubnubStr + ", " + jsonObject.getString("bid") + "|" + jsonObject.getString("custChn") + "|" + jsonObject.getString("status");
                        }
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //reqObject.put("token",sessionManager.getSessionToken());
            reqObject.put("pubnubStr", pubnubStr);
            Utility.printLog("MainWorkerclass DriverStatus :" + sessionManager.getDriverStatus());
            switch (sessionManager.getDriverStatus()) {

                case 3:
                    reqObject.put("ent_status", 4);
                    break;
                case 4:
                    reqObject.put("ent_status", 3);
                    break;
                default:
                    reqObject.put("ent_status", 4);
                    sessionManager.setDriverStatus(4);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            presenter.hideProgress();
        }


        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.UPDATE_STATUS, OkHttp3Connection.Request_type.PUT,
                reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println("updateStatus result " + result);

                        if (result != null) {
                            ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                            switch (validatorPojo.getErrFlag()) {
                                case 0:
                                    int updatedStatus = sessionManager.getDriverStatus() == 3 ? 4 : 3;
                                    sessionManager.setDriverStatus(updatedStatus);
                                    presenter.updatedStatus(updatedStatus);
                                    break;
                                case 1:
                                    errorNumber = 1;
                                    presenter.apiError(validatorPojo.getErrMsg(), errorNumber);
                                    break;
                            }
                        } else {
                            errorNumber = 2;
                            presenter.apiError(R.string.smthWentWrong + "", errorNumber);
                        }
                        presenter.hideProgress();

                    }
                    @Override
                    public void onError(String error) {
                        errorNumber = 3;
                        System.out.println("updateStatus error " + error);
                        presenter.apiError(R.string.network_problem + "", errorNumber);
                        presenter.hideProgress();
                    }
                }, sessionManager.getSessionToken());
    }

    public void getCurrentStatus() {

        presenter.showProgress();
        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.GET_CURRENT_STATUS,
                OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, " getMasterCurrentStatus result " + result);
                        if (result != null) {
                            AssignedTripsPojo tripsPojo = new Gson().fromJson(result, AssignedTripsPojo.class);
                            switch (tripsPojo.getErrFlag()) {
                                case 0:

                                    try {
                                        JSONArray oldArray = new JSONArray();
                                        if (!sessionManager.getBookings().isEmpty()) {
                                            oldArray = new JSONArray(sessionManager.getBookings());
                                        }

                                        JSONArray jsonArray = new JSONArray();

                                        for (int i = 0; i < tripsPojo.getData().getAppointments().size(); i++) {

                                            boolean found = false;
                                            AssignedAppointments appointments = tripsPojo.getData().getAppointments().get(i);
                                            for (int j = 0; j < oldArray.length(); j++) {
                                                JSONObject jsonObject = oldArray.getJSONObject(j);
                                                if (jsonObject.get("bid").equals(appointments.getBid())) {
                                                    jsonArray.put(jsonObject);
                                                    found = true;
                                                    break;
                                                }

                                            }
                                            if (!found) {
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("bid", appointments.getBid());
                                                    jsonObject.put("distance", 0.0);
                                                    jsonObject.put("custChn", appointments.getCustomerChn());
                                                    jsonObject.put("status", appointments.getStatusCode());
                                                    jsonObject.put("time_paused", 0);
                                                    jsonObject.put("time_elapsed", 0);

                                                    jsonArray.put(jsonObject);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }

                                        sessionManager.setBookings(jsonArray.toString());
                                        Utility.printLog("MainworkerClass " + sessionManager.getBookings());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    sessionManager.setDriverStatus(tripsPojo.getData().getMasterStatus());
                                    presenter.updatedStatus(tripsPojo.getData().getMasterStatus());

//                                    if (tripsPojo.getData().getAppointments().size()>0) {
                                    presenter.updateAppointments(tripsPojo);
//                                    }
                                    break;
                                case 1:
                                    errorNumber = 1;
                                    presenter.apiError(tripsPojo.getErrMsg(), errorNumber);
                                    break;
                            }
                            if (tripsPojo.getStatusCode() != null && tripsPojo.getStatusCode().equals("401")) {
                                errorNumber = 401;
                                presenter.apiError(tripsPojo.getMessage(), errorNumber);
                            }

                        } else {
                            errorNumber = 2;
                            presenter.apiError(R.string.smthWentWrong + "", errorNumber);
                        }
                        presenter.hideProgress();
                    }
                    @Override
                    public void onError(String error) {
                        errorNumber = 3;
                        Log.d(TAG, " getMasterCurrentStatus error " + error);
                        presenter.apiError(R.string.network_problem + "", errorNumber);
                        presenter.hideProgress();
                    }
                }, sessionManager.getSessionToken());
    }

}
