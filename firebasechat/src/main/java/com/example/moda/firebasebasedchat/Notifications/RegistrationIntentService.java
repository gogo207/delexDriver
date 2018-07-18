package com.example.moda.firebasebasedchat.Notifications;

/*
 * Created by moda on 17/2/16.
 */

import android.app.IntentService;
import android.content.Intent;

import com.example.moda.firebasebasedchat.AppController;
import com.google.firebase.iid.FirebaseInstanceId;


/*
* Service obtaining token and update that token in shared preferences
*/
public class RegistrationIntentService extends IntentService {


    String token = "";

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        try {


            token = FirebaseInstanceId.getInstance().getToken();


            AppController.getInstance().getSharedPreferences().edit().putString("pushToken", token).apply();


            AppController.getInstance().setPushToken(token);


        } catch (Exception e) {
            e.printStackTrace();

        }

    }


}