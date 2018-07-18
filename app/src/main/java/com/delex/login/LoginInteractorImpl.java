package com.delex.login;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.delex.driver.R;
import com.delex.pojo.SinginResponsePojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginInteractorImpl implements LoginInteractor {
    private Context context;
    private String username, password;
    private OnLoginFinishedListener listener;
    private SessionManager sessionManager;
    @Override
    public void login(final String username, final String password, final OnLoginFinishedListener listener, final Context context) {

        this.context = context;
        this.username = username;
        this.password = password;
        this.listener = listener;
        sessionManager = SessionManager.getSessionManager(context);
        boolean error = false;
        if (TextUtils.isEmpty(username)) {
            listener.onUsernameError(context.getString(R.string.phone_miss));
            error = true;
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches() && !Patterns.PHONE.matcher(username).matches()) {
            if(!Patterns.EMAIL_ADDRESS.matcher(username).matches())
                listener.onUsernameError(context.getString(R.string.invalidEmail));
            else
                listener.onUsernameError(context.getString(R.string.invalidPhone));

            error = true;
            return;
        } else if (TextUtils.isEmpty(password)) {
            listener.onPasswordError(context.getString(R.string.password_miss));
            error = true;
            return;
        } else {
            signupApiCalling();
        }
    }

    /**
     * <h2>signupApiCalling</h2>
     * <p>call api for validating email and phone</p>
     */
    private void signupApiCalling() {

        JSONObject reqObject = new JSONObject();

        try {
            reqObject.put("mobile", username);
            reqObject.put("password", password);
            reqObject.put("ent_deviceId", sessionManager.getDeviceId());
            reqObject.put("ent_push_token", sessionManager.getPushToken());
            reqObject.put("ent_appversion", VariableConstant.APP_VERSION);
            reqObject.put("ent_devMake", VariableConstant.DEVICE_MAKER);
            reqObject.put("ent_devModel", VariableConstant.DEVICE_MODEL);
            reqObject.put("ent_devtype", VariableConstant.DEVICE_TYPE);
            reqObject.put("ent_device_time", Utility.date());

            System.out.println("Signin request " + reqObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError(e.getMessage());
        }

        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.SIGN_IN, OkHttp3Connection.Request_type.POST, reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                System.out.println("Signin result " + result);
                if (result != null) {
                    SinginResponsePojo signinPojo = new Gson().fromJson(result, SinginResponsePojo.class);

                    switch (signinPojo.getErrFlag()) {
                        case 0:
                            Utility.setPreference(context, LoginInteractorImpl.this.username, "USER_EMAIL");
                            Utility.setPreference(context, LoginInteractorImpl.this.password, "USER_PASSWORD");

                            sessionManager.setPushTopic(signinPojo.getData().getPushTopic());
                            sessionManager.setSessionToken(signinPojo.getData().getToken());
                            sessionManager.setMyName(signinPojo.getData().getName());
                            sessionManager.setReferralCode(signinPojo.getData().getCode());
                            sessionManager.setProfilePic(signinPojo.getData().getProfilePic());
                            sessionManager.setBANKD(signinPojo.getData().isBankDetails());
                            sessionManager.setSubscribeKey(signinPojo.getData().getSub_key());
                            sessionManager.setPublishKey(signinPojo.getData().getPub_key());
                            sessionManager.setServerChannel(signinPojo.getData().getServer_chn());
                            sessionManager.setPresenceChannel(signinPojo.getData().getPresence_chn());
                            sessionManager.setVehicle(signinPojo.getData().getVehicles().toString());
                            sessionManager.setDriverChannel(signinPojo.getData().getChn());
                            sessionManager.setDriverStatus(4);
                            sessionManager.setDriverUuid("m_" + signinPojo.getData().getMid());
                            sessionManager.setMid(signinPojo.getData().getMid());
                            sessionManager.setPassword(password);
                            System.out.println("Signin vehicle " + sessionManager.getVehicle());
                            listener.onSuccess();

                            sessionManager.setIsLogin(true);
                            break;
                        case 1:
                            listener.onError(signinPojo.getErrMsg());
                            break;
                    }

                } else {
                    listener.onError(context.getString(R.string.smthWentWrong));
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("signin error " + error);
                listener.onError(context.getString(R.string.network_problem));
            }
        }, "ordinory");
    }
}
