package com.delex.profile;

import android.util.Log;

import com.delex.pojo.PofilePojo;
import com.delex.pojo.ProfileData;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.Upload_file_AmazonS3;
import com.delex.utility.VariableConstant;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by murashid on 26-Aug-17.
 */

public class MyProfileModel {
    private static final String TAG = "SupportFragModel";
    private MyProfileModelImplement myProfileModelImplement;

    MyProfileModel(MyProfileModelImplement myProfileModelImplement)
    {
        this.myProfileModelImplement = myProfileModelImplement;
    }

    void fetchData(String token)
    {
        OkHttp3Connection.doOkHttp3Connection(token, ServiceUrl.GET_PROFILE ,
                OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, " getMasterProfile result " + result);
                        if (result != null) {

                            PofilePojo pofilePojo = new Gson().fromJson(result, PofilePojo.class);
                            if(pofilePojo.getStatusCode()!=null && pofilePojo.getStatusCode().equals("401")){
                               /* getActivity().startActivity(new Intent(getActivity(), Login.class));
                                getActivity().finish();*/
                                myProfileModelImplement.onFailure();
                            }else {
                                switch (pofilePojo.getErrFlag()) {
                                    case 0:
                                        myProfileModelImplement.onSuccess(pofilePojo.getData());
                                        break;
                                    case 1:
                                        myProfileModelImplement.onFailure(pofilePojo.getErrMsg());
                                        break;
                                }
                            }

                        }else{
                            myProfileModelImplement.onFailure();
                        }
                    }
                    @Override
                    public void onError(String error) {
                        myProfileModelImplement.onFailure();
                    }
                },token);
    }

    void updateProfilePic(final String token, JSONObject jsonObject)
    {
        OkHttp3Connection.doOkHttp3Connection(token,ServiceUrl.UPDATE_PROFILE, OkHttp3Connection.Request_type.PUT, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, " updateProfilePic result " + result);
                if (result!=null){
                    ValidatorPojo validatorPojo = new Gson().fromJson(result,ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()){
                        case 0:
                            fetchData(token);
                            break;
                        case 1:
                            myProfileModelImplement.onFailure(validatorPojo.getErrMsg());
                            break;
                    }
                }
                else {
                    myProfileModelImplement.onFailure();
                }
            }

            @Override
            public void onError(String error) {
                myProfileModelImplement.onFailure();
            }
        },token);
    }

    public void amzonUpload(File file,Upload_file_AmazonS3 amazonS3) {
        String BUCKETSUBFOLDER = "";
        BUCKETSUBFOLDER = VariableConstant.PROFILE_PIC;
        Log.d(TAG, "amzonUpload: " + file);
        final String imageUrl = VariableConstant.AMAZON_BASE_URL + VariableConstant.BUCKET_NAME + BUCKETSUBFOLDER + file.getName();
        Log.d(TAG, "amzonUpload: " + imageUrl);
        Log.d(TAG, "amzonUpload: " + BUCKETSUBFOLDER + file.getName());

        amazonS3.Upload_data(VariableConstant.BUCKET_NAME, BUCKETSUBFOLDER + file.getName(), file, new Upload_file_AmazonS3.Upload_CallBack() {
            @Override
            public void sucess(String url) {

                Log.d(TAG, "sucess: u" + url);

//                VariableConstant.PROFILE_IMAGE_URL = url;
                myProfileModelImplement.onImageUploaded(url);

            }

            @Override
            public void sucess(String url, String type) {

            }

            @Override
            public void error(String errormsg) {

                Log.d(TAG, "error: u" + errormsg);
            }
        });
    }


    interface MyProfileModelImplement
    {
        void onFailure(String failureMsg);
        void onFailure();
        void onSuccess(ProfileData profileData);
        void uploadToAmazon(File fileName);
        void onImageUploaded(String url);
    }
}
