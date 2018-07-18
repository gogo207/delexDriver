package com.example.moda.firebasebasedchat.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.example.moda.firebasebasedchat.AppController;

/**
 * Created by moda on 21/06/17.
 */

public class AppKilled extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public void onTaskRemoved(Intent rootIntent) {


        SharedPreferences sh = AppController.getInstance().getSharedPreferences();


        if (sh.getBoolean("signedIn", false) && (sh.getString("userId", null) != null)) {

            AppController.getInstance().updatePresenceStatus(0, sh.getString("userId", null));


//            /*
//             * Means app is closed from the ChatMessageScreen.(This is basically used to trigger the cloud functions)
//             */
//            if (!AppController.getInstance().getActiveReceiverId().isEmpty()) {
//
//                FirebaseDatabase.getInstance().getReference().child("PresenceForUnreadCounts").child(userId).child("online").setValue(0);
//            }


            if (!AppController.getInstance().getActiveReceiverId().isEmpty()) {


                AppController.getInstance().setStatusAsOfflineForCurrentChat(sh.getString("userId", null));
            }
        }


        stopSelf();
    }
}