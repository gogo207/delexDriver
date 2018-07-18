package com.example.moda.firebasebasedchat.Notifications;

/*
 * Created by moda on 9/12/15.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.example.moda.firebasebasedchat.Activities.ChatMessagesScreen;
import com.example.moda.firebasebasedchat.AppController;
import com.example.moda.firebasebasedchat.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


/***
 *  FirebaseMessagingService to receive and display the push notifications received via firebase
 *
 */

public class MyFcmListenerService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(final RemoteMessage message) {
        super.onMessageReceived(message);


        Handler handler = new Handler(Looper.getMainLooper());


        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!AppController.getInstance().isForeground() || !(AppController.getInstance().getActiveReceiverId().equals(message.getData().get("senderId")))) {
                    createChatNotification(message.getData());
                }
            }
        });
    }


    private void createChatNotification(Map data) {

        String str2 = String.valueOf(System.currentTimeMillis()).substring(9);
        Intent intent = new Intent(this, ChatMessagesScreen.class);

        intent.putExtra("receiverUid", (String) data.get("senderId"));
        intent.putExtra("receiverName", (String) data.get("senderName"));
        intent.putExtra("chatName", (String) data.get("chatId"));
        intent.putExtra("notificationId", Integer.parseInt(str2));


        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(str2), intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        String str = data.get("senderName") + ": " + data.get("message");


        NotificationCompat.Builder
                notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(str)
                .setTicker(str)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_HIGH);


        if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((String) data.get("chatId"), Integer.parseInt(str2), notificationBuilder.build());


    }

}


