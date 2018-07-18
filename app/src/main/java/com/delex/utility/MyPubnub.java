package com.delex.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.delex.app.bookingRequest.BookingPopUp;
import com.delex.app.MainActivity;
import com.delex.app.SplashScreen;
import com.delex.driver.R;
import com.delex.pojo.PubnubResponse;
import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;
import java.util.HashMap;

public class MyPubnub {
    private static MyPubnub myPubnub;
    private static PNConfiguration pnConfiguration;
    private static PubNub pubNub;
    private static Context mContext;
    boolean isbackground;
    private SessionManager sessionMgr;

    /******************************************************/
    private MyPubnub(Context context) {
        //isbackground = GcmBroadcastReceiver.isApplicationSentToBackground(context);
        sessionMgr = SessionManager.getSessionManager(context);
        initPubNub();
    }

    public static MyPubnub getInstance(Context context) {
        mContext = context;
        if (myPubnub == null) {
            myPubnub = new MyPubnub(context);
        }
        return myPubnub;
    }

    /************************************************************/
    public void initPubNub() {
        pnConfiguration = getPubnubConfiguration();
        pubNub = getPubnubInstance();
    }

    /************************************************************/
    private PNConfiguration getPubnubConfiguration() {
        PNConfiguration pnConfig = new PNConfiguration();
        pnConfig.setPublishKey(sessionMgr.getPublishKey());
        pnConfig.setSubscribeKey(sessionMgr.getSubscribeKey());
        pnConfig.setUuid(sessionMgr.getDriverUuid());
        pnConfig.setPresenceTimeoutWithCustomInterval(30, 10);

        return pnConfig;
    }

    /************************************************************/
    public synchronized PubNub getPubnubInstance() {
        if (pubNub == null) {
            if (pnConfiguration == null) {
                pnConfiguration = getPubnubConfiguration();
            }
            pubNub = new PubNub(pnConfiguration);
        }
        return pubNub;
    }

    /************************************************************/
    public void subscribe() {
        /*if(!sessionMgr.getIsPubnubSubscribed())
        {
            sessionMgr.setIsPubnubSubscribed(true);*/

        Utility.printLog("MyPubnubTest MyPubnub", "sessionMgr.getPresenceChannel() " + sessionMgr.getPresenceChannel() + " sessionMgr.getListenerChannel() = " + sessionMgr.getDriverChannel());
        try {
            unsubscribePubnub();
            pubNub.subscribe()
                    .withPresence()
                    .channels(Arrays.asList(sessionMgr.getPresenceChannel(), sessionMgr.getDriverChannel()))
                    .execute(); // subscribe to channels.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                // handle any status
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // internet got lost, do some magic and call reconnect when ready
                    if (Utility.isNetworkAvailable(mContext))
                        pubNub.reconnect();
                } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    Utility.printLog("MyPubnubTest MyPubnub", "subscribe() PNConnectedCategory :");
                }

                // Happens as part of our regular operation. This event happens when
                // radio / connectivity is lost, then regained.
                else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                    Utility.printLog("MyPubnubTest MyPubnub", "subscribe() PNReconnectedCategory :");
                }

                // Handle messsage decryption error. Probably client configured to
                // encrypt messages and on live data feed it received plain text.
                else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
                    Utility.printLog("MyPubnubTest MyPubnub", "subscribe() PNDecryptionErrorCategory :");
                } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                    // do some magic and call reconnect when ready
                    Utility.printLog("MyPubnubTest MyPubnub", "subscribe() PNConnectedCategory :");
                    pubnub.reconnect();
                }
            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
                Utility.printLog("MyPubnubTest MyPubnub", "message " + message.getMessage().toString());
                try {

                        /*JSONObject jsonObject = new JSONObject(String.valueOf(message.getMessage()));*/

                    PubnubResponse pubnubResponse;
                    if (message.getMessage() != null) {
                        pubnubResponse = new Gson().fromJson(message.getMessage().toString(), PubnubResponse.class);
                        switch (pubnubResponse.getA()) {
                            case 11:
                                //System.out.println("Last Booking ID "+sessionMgr.getLastBookingId() +
                                //        "Current Booking ID"+pubnubResponse.getBid());
//                                if (sessionMgr.getLastBookingId() != (Long.parseLong(pubnubResponse.getBid()))) {

                                    sessionMgr.setLastBookingId(Long.parseLong(pubnubResponse.getBid()));

                                    AcknowledgeHelper helper = new AcknowledgeHelper(sessionMgr.getSessionToken(), pubnubResponse.getBid(),
                                            pubnubResponse.getServerTime(), pubnubResponse.getPingId());
                                    helper.updateAptRequest(new AcknowledgementCallback() {
                                        @Override
                                        public void callback(String bid) {
                                            //System.out.println("Saving Last Booking ID "+bid);
                                            Intent intent = new Intent(mContext, BookingPopUp.class);
                                            intent.putExtra("booking_Data", message.getMessage().toString());
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            mContext.startActivity(intent);
                                        }
                                    });
//                                }
                                break;
                        }
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                Utility.printLog("MyPubnubTest MyPubnub", "presence " + presence.getEvent());
            }
        });
        //}
    }

    /*****************************************************/
    public void publishForNotify(String bid) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("a", "11");
        hashMap.put("receiveDt", Utility.date());
        hashMap.put("bid", bid);
        hashMap.put("mid", "" + sessionMgr.getDriverUuid());
        hashMap.put("chn", sessionMgr.getServerChannel());

        Utility.printLog("AAA Publish NotifyChannel =" + hashMap.toString());
        publishOnPubnub(sessionMgr.getServerChannel(), hashMap);
    }

    /*****************************************************/
    private void sendNotification(String msg, String action) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, msg, when);
        String title = mContext.getString(R.string.app_name);
        PendingIntent intent = null;

        Intent notificationIntent;

        if ("15".equals(action)) {
            //notificationIntent = new Intent(mContext, PopupActivity.class);
            notificationIntent = new Intent(mContext, SplashScreen.class);
        } else if ("16".equals(action)) {
            //notificationIntent = new Intent(mContext,ArrivedActivity.class);
            notificationIntent = new Intent(mContext, SplashScreen.class);
        } else {
            notificationIntent = new Intent(mContext, MainActivity.class);
        }

        notificationIntent.putExtra("Type", "1");

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        //notification.setLatestEventInfo(this, title, msg, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound

        Bitmap icon1 = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_launcher);

        //Assign inbox style notification
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(mContext.getResources().getString(R.string.app_name));
        bigText.setBigContentTitle(title);
        //bigText.setSummaryText("Alert");

        //build notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(intent)
                        .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                        .setPriority(NotificationCompat.PRIORITY_MAX) //must give priority to High, Max which will considered as heads-up notification)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(msg)
                        .setLargeIcon(icon1)
                        .setStyle(bigText);

        //mBuilder.setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.tone));

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    /*****************************************************/
    public void publishLocation() {
        if (sessionMgr.isLogin() && sessionMgr.getDriverStatus() == 3) {
            HashMap<String, String> hashMap = new HashMap();

            hashMap.put("a", "4");
            //hashMap.put("e_id",sessionMgr.getDriverUuid());
            hashMap.put("mid", "" + sessionMgr.getDriverUuid());
            hashMap.put("tp", sessionMgr.getTypeId());
            hashMap.put("lt", "" + sessionMgr.getDriverCurrentLat());
            hashMap.put("lg", "" + sessionMgr.getDriverCurrentLng());
            //hashMap.put("fname",sessionMgr.getDriverName());
            //hashMap.put("profilePic",sessionMgr.getDriverProfilePic());
            hashMap.put("chn", "" + sessionMgr.getDriverChannel());
            // hashMap.put("phone",sessionMgr.getDriverPhone());
            hashMap.put("carType", sessionMgr.getTypeId());
            // hashMap.put("cityid",sessionMgr.getCityId());
            // hashMap.put("driverid",sessionMgr.getDriverId());

            publishOnPubnub(sessionMgr.getServerChannel(), hashMap);
            hashMap = null;

            /*if (sessionMgr.getIsOrderStarted())
            {
                pubNub.hereNow()
                        .channels(Arrays.asList(sessionMgr.getDriverChannel())) // who is present on those channels?
                        .includeState(false) // include state with request (false by default)
                        .includeUUIDs(false) // if false, only shows occupancy count
                        .async(new PNCallback<PNHereNowResult>()
                        {
                            @Override
                            public void onResponse(PNHereNowResult result, PNStatus status)
                            {
                                if (result != null && !"".equals(result))
                                {
                                    if (!"".equals(VariableConstants.DRIVER_STATUS))
                                    {
                                        publishLocOnDriverChannel(VariableConstants.DRIVER_STATUS);
                                    }
                                }
                            }
                        });
            }*/
        }
        return;
    }

    /***************************************************************/
    private void publishLocOnDriverChannel(String status) {
        HashMap<String, String> msg = new HashMap<>();
       /* msg.put("a", staus);
        msg.put("e_id", sessionMgr.getDriverEmailId());
        msg.put("tp", sessionMgr.getVehTypeId());
        msg.put("lt",""+sessionMgr.getDriverCurrentLat());
        msg.put("lg",""+sessionMgr.getDriverCurrentLng());
        msg.put("n", sessionMgr.getDriverName());
        msg.put("chn", sessionMgr.getDriverChannel());
        msg.put("bid",sessionMgr.getjobId());
        msg.put("profilePic",sessionMgr.getDriverProfilePic());
        msg.put("fname",sessionMgr.getDriverName());
        msg.put("cityid",sessionMgr.getCityId());
        msg.put("carType",sessionMgr.getVehicleType());*/
        msg.put("lt", +sessionMgr.getDriverCurrentLat() + "");
        msg.put("lg", sessionMgr.getDriverCurrentLng() + "");
        msg.put("chn", sessionMgr.getDriverChannel() + "");
        msg.put("a", status + "");
        msg.put("vt", sessionMgr.getTypeId());
        msg.put("mid", sessionMgr.getDriverUuid());

        publishOnPubnub(sessionMgr.getServerChannel(), msg);
        //publishOnPubnub(sessionMgr.getDriverChannel(), msg);
        msg = null;
        return;
    }

    /***************************************************************/
    /**
     * custom method to publishOnPubnub given message on the given channel
     *
     * @param channelName : (String) is the channel name on which the message is to be published
     * @param message:    (Hashmap) contains the message to be published
     */
    public void publishOnPubnub(final String channelName, HashMap message) {
        if (pubNub != null) {
            Utility.printLog("MyPubnub", "publishOnPubnub msg: " + message);
            pubNub.publish()
                    .channel(channelName).message(message)
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            if (result != null) {
                                Utility.printLog("MyPubnub", "publishOnPubnub timeToken: " + result.getTimetoken());
                            }
                        }
                    });
        }
        message = null;
        return;
    }

    /******************************************************/
    public void publishNewOrderAccepted(String bid, String receiveDt) {
        HashMap<String, String> hashMap1 = new HashMap();
        hashMap1.put("a", "11");
        hashMap1.put("e_id", sessionMgr.getDriverUuid());
        hashMap1.put("tp", sessionMgr.getTypeId());
        hashMap1.put("lt", "" + sessionMgr.getDriverCurrentLat());
        hashMap1.put("lg", "" + sessionMgr.getDriverCurrentLng());
        hashMap1.put("bid", bid);
        hashMap1.put("receiveDt", receiveDt);
        publishOnPubnub(sessionMgr.getServerChannel(), hashMap1);
    }

    /************************************************************/
    /**
     * custom method to update apt next status
     */
    public void publishOrderStatus(String bid, String passengerChannel, String orderDate, String orderStatus) {
        HashMap<String, String> message = new HashMap<>();
        message.put("a", orderStatus);
        message.put("e_id", sessionMgr.getDriverUuid());
        message.put("lt", "" + sessionMgr.getDriverCurrentLat());
        message.put("lg", "" + sessionMgr.getDriverCurrentLng());
        //message.put("ph", sessionMgr.getDriverPhoneNo());
        message.put("dt", orderDate);
        message.put("bid", bid);
        message.put("chn", sessionMgr.getDriverChannel());

        Utility.printLog("getchannel publish =" + message + "  passcha = " + passengerChannel);
        publishOnPubnub(passengerChannel, message);

    }

    /************************************************************/
    /**
     * Custom method to update apt status as apt canceled
     * @param reason
     */
    /* public void publishAptCanceled(String bid, String passengerChannel, String reason)
    {
        HashMap<String, String> message = new HashMap<>();
        message.put("a", AppConstants.getAptStatus_Canceled.value);
        message.put("e_id", sessionMgr.getDriverEmailId());
        message.put("lt", String.valueOf(VariableConstants.LATITUDE_LAST_UPDATED));
        message.put("lg", String.valueOf(VariableConstants.LONGITUDE_LAST_UPDATED));
        message.put("r", reason);
        message.put("bid", bid);
        message.put("chn", sessionMgr.getDriverChannel());

        Log.i("MyPubnub", "Publish Passenger Channel"+passengerChannel+"  message: "+message.toString());
        publishOnPubnub(passengerChannel, message);
    }*/

    /************************************************************/
    public void unsubscribePubnub() {


        try {
            pubNub.unsubscribe()
                    .channels(Arrays.asList(sessionMgr.getPresenceChannel(),sessionMgr.getDriverChannel()))
                    .execute();
        } catch (Exception
                e) {
            e.printStackTrace();
        }
        Log.i("MyPubnub", "PstopPubnub called");

    }

    /************************************************************/
    public void stopPubnub() {
        Log.i("MyPubnub", "PstopPubnub unsubscribePubnub called");
        unsubscribePubnub();
        pnConfiguration = null;
        pubNub = null;
        myPubnub = null;
    }
    /************************************************************/
}
