package com.delex.profile;


import android.content.Context;

import com.delex.pojo.ProfileData;
import com.delex.utility.Upload_file_AmazonS3;
import com.delex.utility.VariableConstant;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by murashid on 26-Aug-17.
 */

public class MyProfilePresenter implements MyProfileModel.MyProfileModelImplement {

    private MyProfileModel myProfileModel;
    private MyProfilePresenterImplement myProfilePresenterImplement;
    private Context context;

    MyProfilePresenter(MyProfilePresenterImplement myProfilePresenterImplement, Context context)
    {
        myProfileModel = new MyProfileModel(this);
        this.myProfilePresenterImplement = myProfilePresenterImplement;
        this.context=context;
    }

    void getProfileDetails(String token)
    {
        myProfilePresenterImplement.startProgressBar();
        myProfileModel.fetchData(token);
    }

    void setProfilePic(String token, JSONObject jsonObject)
    {
        myProfilePresenterImplement.startProgressBar();
        myProfileModel.updateProfilePic(token,jsonObject);
    }

    @Override
    public void onFailure(String failureMsg) {
        myProfilePresenterImplement.stopProgressBar();
        myProfilePresenterImplement.onFailure(failureMsg);
    }

    @Override
    public void onFailure() {
        myProfilePresenterImplement.stopProgressBar();
        myProfilePresenterImplement.onFailure();
    }

    @Override
    public void onSuccess(ProfileData profileData) {
        myProfilePresenterImplement.stopProgressBar();
        myProfilePresenterImplement.setProfileDetails(profileData);
    }

    @Override
    public void uploadToAmazon(File fileName) {
        Upload_file_AmazonS3 amazonS3 = Upload_file_AmazonS3.getInstance(context, VariableConstant.COGNITO_POOL_ID);
        myProfilePresenterImplement.startProgressBar();
        myProfileModel.amzonUpload(fileName,amazonS3);
    }


    @Override
    public void onImageUploaded(String url) {
        myProfilePresenterImplement.stopProgressBar();
        myProfilePresenterImplement.onImageUploadedResult(url);
    }

    interface MyProfilePresenterImplement
    {
        void startProgressBar();
        void stopProgressBar();
        void onFailure(String msg);
        void onFailure();
        void onImageUploadedResult(String url);
        void setProfileDetails(ProfileData profileData);
    }
}
