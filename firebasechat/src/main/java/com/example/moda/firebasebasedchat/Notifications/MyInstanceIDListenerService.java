package com.example.moda.firebasebasedchat.Notifications;

/*
 * Created by moda on 24/2/16.
 */

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/*
 *To automatically change and generate new token when system feels the need for it
 */


public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {


        Intent intent = new Intent(this, RegistrationIntentService.class);


        startService(intent);


    }


}