package com.example.moda.firebasebasedchat;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.example.moda.firebasebasedchat.AppStateChange.AppStateListener;
import com.example.moda.firebasebasedchat.AppStateChange.AppStateMonitor;
import com.example.moda.firebasebasedchat.AppStateChange.RxAppStateMonitor;
import com.example.moda.firebasebasedchat.Callbacks.DatabaseCallbackHandler;
import com.example.moda.firebasebasedchat.Notifications.RegistrationIntentService;
import com.example.moda.firebasebasedchat.Service.AppKilled;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by moda on 21/06/17.
 */

public class AppController extends Application implements Application.ActivityLifecycleCallbacks

{

    private boolean foreground = true;
    private static AppController mInstance;
    private String pushToken = null;
    private SharedPreferences sharedPref;
    private boolean signedIn;
    private String userId, activeReceiverId = "";
    private String activeChatName = "";
    public void setActiveChatName(String activeChatName) {
        this.activeChatName = activeChatName;
    }
    private DatabaseCallbackHandler databaseHandler;
    private boolean chatScreenDestroyed = false;




    @Override
    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        FacebookSdk.sdkInitialize(this);
        AppStateMonitor appStateMonitor = RxAppStateMonitor.create(this);
        appStateMonitor.addListener(new AppStateListener() {
            @Override
            public void onAppDidEnterForeground() {


                foreground = true;


                if (signedIn && userId != null) {


                    updatePresenceStatus(1, userId);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }

            }

            @Override
            public void onAppDidEnterBackground() {


                foreground = false;

                if (signedIn && userId != null) {
                    updatePresenceStatus(0, userId);

                }
            }
        });
        appStateMonitor.start();


        sharedPref = this.getSharedPreferences("defaultPreferences", Context.MODE_PRIVATE);

        pushToken = sharedPref.getString("pushToken", null);
        if (pushToken == null) {


            if (checkPlayServices()) {

                startService(new Intent(this, RegistrationIntentService.class));
            }

        }


        userId = sharedPref.getString("userId", null);
        signedIn = sharedPref.getBoolean("signedIn", false);


        registerActivityLifecycleCallbacks(mInstance);


        startService(new Intent(mInstance, AppKilled.class));
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public boolean isForeground() {
        return foreground;
    }


    public SharedPreferences getSharedPreferences() {

        return sharedPref;
    }


    public String getPushToken() {

        return pushToken;
    }

    public void setPushToken(String pushToken) {

        this.pushToken = pushToken;
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return resultCode == ConnectionResult.SUCCESS;
    }



    public String getUserId(){


        return userId;
    }


    public void setupPresenceSystem(String userId) {


        String str = "users/" + userId + "/";

// since I can connect from multiple devices, we store each connection instance separately
// any time that connectionsRef's value is null (i.e. has no children) I am offline
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference(str + "connections");

// stores the timestamp of my last disconnect (the last time I was seen online)
        final DatabaseReference lastOnlineRef = database.getReference(str + "lastOnline");

        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    DatabaseReference con = myConnectionsRef.push();
                    con.setValue(Boolean.TRUE);

                    // when this device disconnects, remove it
                    con.onDisconnect().removeValue();

                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });


        updateUserDetails(userId);
        updatePresenceStatus(1, userId);
    }


    public void updatePresenceStatus(int foreground, String userId) {


        String str = "users/" + userId + "/";

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference lastOnlineRef = database.getReference(str + "lastOnline");
        final DatabaseReference online = database.getReference(str + "online");
        if (foreground == 0) {

            lastOnlineRef.setValue(ServerValue.TIMESTAMP);
            online.setValue(Boolean.FALSE);
        } else {

            online.setValue(Boolean.TRUE);
        }


    }


    private void updateUserDetails(String userId) {

        this.userId = userId;
        this.signedIn = true;


        sharedPref.edit().putString("userId", userId).apply();
        sharedPref.edit().putBoolean("signedIn", true).apply();


    }

    public String getActiveReceiverId() {


        return activeReceiverId;
    }


    public void setActiveReceiverId(String receiverId) {


        this.activeReceiverId = receiverId;
    }

    public static void initDatabaseHandlerInstance(DatabaseCallbackHandler sktResponseHandler) {
        AppController.getInstance().databaseHandler = sktResponseHandler;


    }


    public void registerListenerForNewChats() {


        ChildEventListener chatsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot childDataSnapshot, String previousChildName) {
                /*
                 * Needed when a new chat has been initiated with any of the user
                 */

                if (AppController.getInstance().databaseHandler != null && AppController.getInstance().getActiveReceiverId().isEmpty()) {
                    try {
                        AppController.getInstance().databaseHandler.handleResponse(childDataSnapshot);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                /*
                 * To avoid the callback incase the user is on the chatmessagescreen,casue child changed will be called even for the first time as user will update the count of unread messages
                 */


                if (chatScreenDestroyed) {

                    chatScreenDestroyed = false;
                } else {
                    if (AppController.getInstance().databaseHandler != null && AppController.getInstance().getActiveReceiverId().isEmpty()) {
                        try {
                            AppController.getInstance().databaseHandler.handleResponse(dataSnapshot);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        };


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users_Chats").child(userId);


        ref.addChildEventListener(chatsListener);


        ref.onDisconnect();


    }

    public void setStatusAsOfflineForCurrentChat(String userId) {




        if (!AppController.getInstance().activeChatName.isEmpty()) {

            FirebaseDatabase.getInstance()
                    .getReference().child("Users_Chats").child(userId).child(activeChatName)
                    .child("online")
                    .setValue(0);
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {


    }

    @Override
    public void onActivityDestroyed(Activity activity) {


        if (activity.getClass().getSimpleName().equals("ChatMessagesScreen")) {
            chatScreenDestroyed = true;

        }

    }

    @Override
    public void onActivityPaused(Activity activity) {


    }

    @Override
    public void onActivityResumed(Activity activity) {


    }

    @Override
    public void onActivitySaveInstanceState(Activity activity,
                                            Bundle outState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {


    }


}
