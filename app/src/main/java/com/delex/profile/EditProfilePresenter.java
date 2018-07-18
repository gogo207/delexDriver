package com.delex.profile;

import org.json.JSONObject;

/**
 * Created by murashid on 28-Aug-17.
 */

public class EditProfilePresenter implements EditProfileModel.EditProfileModelImple {

    private EditProfileImple editProfileImple;
    private EditProfileModel editProfileModel;
    EditProfilePresenter(EditProfileImple editProfileImple)
    {
        this.editProfileImple = editProfileImple;
        editProfileModel = new EditProfileModel(this);
    }

    void updateProfile(String token, JSONObject jsonObject)
    {
        editProfileModel.updateProfileDetails(token,jsonObject);
        editProfileImple.startProgressBar();
    }

    void validatePhone(final String token, JSONObject validatePhoneJson,JSONObject otpJson) {
        editProfileModel.validatePhone(token,validatePhoneJson,otpJson);
        editProfileImple.startProgressBar();
    }

    @Override
    public void onFailure(String failureMsg) {
        editProfileImple.stopProgressBar();
        editProfileImple.onFailure(failureMsg);
    }

    @Override
    public void onFailure() {
        editProfileImple.stopProgressBar();
        editProfileImple.onFailure();
    }

    @Override
    public void onSuccess(String succesMsg) {
        editProfileImple.stopProgressBar();
        editProfileImple.onSuccess(succesMsg);
    }

    @Override
    public void onSuccessPhoneValidation(String successMsg) {
        editProfileImple.stopProgressBar();
        editProfileImple.onSuccessPhoneValidation(successMsg);
    }

    @Override
    public void onSuccessPasswordUpdated(String successMsg) {
        editProfileImple.onSuccessPasswordUpdated(successMsg);
    }

    interface EditProfileImple
    {
        void startProgressBar();
        void stopProgressBar();
        void onFailure(String msg);
        void onFailure();
        void onSuccess(String succesMsg);
        void onSuccessPhoneValidation(String successMsg);
        void onSuccessPasswordUpdated(String successMsg);
    }
}
