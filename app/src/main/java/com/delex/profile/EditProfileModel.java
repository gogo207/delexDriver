package com.delex.profile;

import android.util.Log;

import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by murashid on 28-Aug-17.
 */

public class EditProfileModel {

    private static final String TAG = "EditProfile";
    private EditProfileModelImple editProfileModelImple;
    EditProfileModel(EditProfileModelImple editProfileModelImple)
    {
        this.editProfileModelImple = editProfileModelImple;
    }

    void validatePhone(final String token, JSONObject validatePhoneJson, final JSONObject otpJson)
    {
        OkHttp3Connection.doOkHttp3Connection(token,ServiceUrl.VERIFY_EMAIL_PHONE, OkHttp3Connection.Request_type.POST, validatePhoneJson, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: validatePhone  "+result);
                if (result!=null){
                    ValidatorPojo validatorPojo = new Gson().fromJson(result,ValidatorPojo.class);
                    switch (validatorPojo.getErrFlag()){
                        case 0:
                            callOTP(otpJson);
                            break;
                        case 1:
                            editProfileModelImple.onFailure(validatorPojo.getErrMsg());
                            break;
                    }
                }
                else
                {
                    editProfileModelImple.onFailure();
                }
            }

            @Override
            public void onError(String error) {
                editProfileModelImple.onFailure();
            }
        },token);
    }


    void callOTP(JSONObject otpJson)
    {
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.SIGNUPOTP, OkHttp3Connection.Request_type.POST, otpJson, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: callOTP "+result);
                if (result!=null){
                    ValidatorPojo validatorPojo = new Gson().fromJson(result,ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()){
                        case 0:
                            editProfileModelImple.onSuccessPhoneValidation(validatorPojo.getOtp());
                            break;
                        case 1:
                            editProfileModelImple.onFailure(validatorPojo.getErrMsg());
                            break;

                    }
                }
                else {
                    editProfileModelImple.onFailure();
                }

            }

            @Override
            public void onError(String error) {
                editProfileModelImple.onFailure();

            }
        },"ordinory");
    }
    void updateProfileDetails(String token, final JSONObject jsonObject)
    {
        OkHttp3Connection.doOkHttp3Connection(token,ServiceUrl.UPDATE_PROFILE, OkHttp3Connection.Request_type.PUT, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: updateProfileDetails  "+result);
                if (result!=null){
                    ValidatorPojo validatorPojo = new Gson().fromJson(result,ValidatorPojo.class);
                    switch (validatorPojo.getErrFlag()){
                        case 0:
                            if(jsonObject.has(""))
                            {
                                editProfileModelImple.onSuccessPasswordUpdated(validatorPojo.getErrMsg());
                            }
                            else
                            {
                                editProfileModelImple.onSuccess(validatorPojo.getErrMsg());
                            }
                            break;
                        case 1:
                            editProfileModelImple.onFailure(validatorPojo.getErrMsg());
                            break;
                    }

                }
                else {
                    editProfileModelImple.onFailure();
                }
            }

            @Override
            public void onError(String error) {
                editProfileModelImple.onFailure();
            }

        },token);
    }

    interface EditProfileModelImple
    {
        void onFailure(String failureMsg);
        void onFailure();
        void onSuccess(String successMsg);
        void onSuccessPhoneValidation(String successMsg);
        void onSuccessPasswordUpdated(String successMsg);
    }
}
