package com.delex.fcm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.delex.app.SplashScreen;
import com.delex.driver.R;
import com.delex.service.LocationUpdateService;
import com.delex.utility.AppConstants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.delex.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    JSONObject received;
    private String message,bid;
    private int action=-1;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                received = new JSONObject(remoteMessage.getData());

                if(received.has("a")){
                    action = received.getInt("a");
                }
                if(received.has("action")){
                    action = received.getInt("action");
                }
                if(received.has("msg")){
                    message = received.getString("msg");
                }
                if(received.has("message")){
                    message = received.getString("message");
                }
                if(received.has("body")){
                    message = received.getString("body");
                }
                if(received.has("bid")){
                    bid = received.getString("bid");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(action==3){
                if(!isApplicationSentToBackground(this)){

                    Bundle bundle=new Bundle();
                    bundle.putString("action",action+"");
                    bundle.putString("message",message);
                    bundle.putString("bid",bid);

                    Intent homeIntent=new Intent(AppConstants.ACTION.PUSH_ACTION);
                    homeIntent.putExtras(bundle);
                    sendBroadcast(homeIntent);
                }
            }
            if("506".equals(action))
            {
               if(!Utility.isMyServiceRunning(LocationUpdateService.class,MyFirebaseMessagingService.this)){
                    Intent startIntent = new Intent(MyFirebaseMessagingService.this, LocationUpdateService.class);
                    startIntent.setAction(AppConstants.ACTION.STARTFOREGROUND_ACTION);
                    startService(startIntent);
                }

            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }




        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(message, received);
    }


    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, JSONObject received) {

        Intent intent;
        intent = new Intent(MyFirebaseMessagingService.this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(messageBody);
        bigText.setBigContentTitle(getString(R.string.app_name));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(messageBody))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setStyle(bigText);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    public static boolean isApplicationSentToBackground(final Context context)
    {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty())
        {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName()))
            {
                return true;
            }
        }
        return false;
    }
}