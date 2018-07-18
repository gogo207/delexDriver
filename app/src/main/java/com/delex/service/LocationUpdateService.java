package com.delex.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.delex.app.MainActivity;
import com.delex.driver.R;
import com.delex.pojo.LocationPojo;
import com.delex.utility.AppConstants;
import com.delex.utility.DistanceChangeListner;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Admin on 7/24/2017.
 */

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String METER = "meters";
    private static final String KILOMETER = "Kilometers";
    private static final String NAUTICAL_MILES = "nauticalMiles";
    private static DistanceChangeListner distanceChangeListner;
    public double distancespd;
    private int battery_level;
    private GoogleApiClient mGoogleApiClient;
    private SessionManager sessionManager;
    private String version;
    private LocationRequest mLocationRequest;
    private String TAG = LocationUpdateService.class.getSimpleName();
    private double prevLat, prevLng, strayLat = -1.0, strayLng = -1.0;
    private double distanceKm;
    private String strDouble;
    private Timer myTimer_publish;
    private TimerTask myTimerTask_publish;
    private Location prevLocation = null;
    private float bearing;
    private double minDistance=0.0;
    private boolean updted = true;
    private CouchDbHandler couchDBHandle;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.app.driverapp.internetStatus")) {

                Bundle bucket = intent.getExtras();
                String status1 = bucket.getString("STATUS");
                Utility.printLog(TAG + " " + status1);

                if ("1".equals(status1)) {
                    if (!updted) {
                        updateLocationLogs();
//                        updted=true;
                    }
                } else {
                    updted = false;
                }

            } else {
                battery_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            }

        }
    };


    public static double distance(double lat1, double lng1, double lat2, double lng2, String unit) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        if (KILOMETER.equals(unit))                // Kilometer
        {
            dist = dist * 1.609344;
        } else if (NAUTICAL_MILES.equals(unit))            // Nautical Miles
        {
            dist = dist * 0.8684;
        } else if (METER.equals(unit))            // meter
        {
            dist = dist * 1609.344;

        }

        return dist;
    }

    public static void setDistanceChangeListner(DistanceChangeListner changeListner) {
        distanceChangeListner = changeListner;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        sessionManager = SessionManager.getSessionManager(LocationUpdateService.this);

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        iFilter.addAction("com.app.driverapp.internetStatus");
        registerReceiver(mBroadcastReceiver, iFilter);

//        MyApplication controller = (MyApplication) getApplicationContext();
//        couchDBHandle = controller.getDBHandler();
        couchDBHandle = CouchDbHandler.getCouhDbHandler(LocationUpdateService.this);
//        couchDBHandle.deleteDocument();

        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = info.versionName;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000); // Update location every 2 second
        mLocationRequest.setSmallestDisplacement(20);


        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//                return;
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location currentLoc) {
                            double currentLat = currentLoc.getLatitude();
                            double currentLng = currentLoc.getLongitude();

                            sessionManager.setDriverCurrentLat("" + currentLat);
                            sessionManager.setDriverCurrentLng("" + currentLng);

                            if (prevLocation == null) {
                                prevLocation = currentLoc;
                            } else {
                                bearing = prevLocation.bearingTo(currentLoc);
                            }

                            mCalculateRouteArray();

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try
        {

            if (intent.getAction().equals(AppConstants.ACTION.STARTFOREGROUND_ACTION))
            {
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction(AppConstants.ACTION.MAIN_ACTION);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_RECEIVER_FOREGROUND);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher);

                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setTicker("")
                        .setContentText("Running...")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                      .setContentIntent(pendingIntent)
                        .setOngoing(true).build();

                mGoogleApiClient.connect();
                startPublishingWithTimer();


                startForeground(AppConstants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        notification);


            }
            else if (intent.getAction().equals(
                    AppConstants.ACTION.STOPFOREGROUND_ACTION)) {
                stopForeground(true);
                stopSelf();
            }

        }catch (NullPointerException e)
        {
            e.printStackTrace();
            Utility.printLog("Crashed in forground service");
        }

        return START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void mCalculateRouteArray() {

        if (prevLat == 0.0 || prevLng == 0.0) {
            prevLat = sessionManager.getDriverCurrentLat();
            prevLng = sessionManager.getDriverCurrentLng();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lat", prevLat);
                jsonObject.put("long", prevLng);
                jsonObject.put("dist", sessionManager.getDistance());
                jsonObject.put("bid", sessionManager.getBid());
                jsonObject.put("timestamp", new Date().getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        double curLat = sessionManager.getDriverCurrentLat();
        double curLong = sessionManager.getDriverCurrentLng();
        double dis = distance(prevLat, prevLng, curLat, curLong, METER);
        double distanceInMtr = 0.0;

        double disFromStaryPts = -1.0;

        if (strayLat != -1.0 && strayLng != -1.0) {
            disFromStaryPts = distance(strayLat, strayLng, curLat, curLong, METER);
        }


        if (((dis >= sessionManager.getMinDistForRouteArray()) && (dis <= 300)) || (disFromStaryPts != -1.0 && ((disFromStaryPts >= sessionManager.getMinDistForRouteArray()) && (disFromStaryPts <= 300)))) {

            strayLat = prevLat = sessionManager.getDriverCurrentLat();
            strayLng = prevLng = sessionManager.getDriverCurrentLng();

            distanceInMtr = (dis > disFromStaryPts) ? dis : disFromStaryPts;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lat", curLat);
                jsonObject.put("long", curLong);
                jsonObject.put("timestamp", new Date().getTime());
                jsonObject.put("bid", sessionManager.getBid());
                jsonObject.put("dist", sessionManager.getDistanceInDouble());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = new JSONArray(sessionManager.getBookings());
                Utility.printLog(TAG + " jsonnarray " + jsonArray.toString());
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);

                        if (object.getInt("status") == 6 || object.getInt("status") == 8)
                        {
                            distancespd = (object.getDouble("distance") / Double.parseDouble(sessionManager.getDISTANCE_CONVERSION_UNIT()));
                            distancespd += distanceInMtr;
                            Log.d(TAG, "Myservice Total distance" + distancespd);
                            sessionManager.setDistanceInDouble("" + distancespd);
                            distanceKm = distancespd * Double.parseDouble(sessionManager.getDISTANCE_CONVERSION_UNIT());
                            strDouble = String.format(Locale.US, "%.2f", distanceKm);
                            Log.d(TAG, "MyService Distance format: " + strDouble + "   BID " + object.getString("bid"));
                            object.put("distance", strDouble);
                            sessionManager.setDistance(strDouble);
                        }
                    }
                }
                sessionManager.setBookings(jsonArray.toString());
                Utility.printLog("MyService bookings " + sessionManager.getBookings());
                if (distanceChangeListner != null) {
                    distanceChangeListner.onDistanceChanged(jsonArray);
                }
                //publish location

             /*   if(minDistance>=sessionManager.getMinDistForRouteArray()){
                    minDistance=0.0;
*/
                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        if (updted) {
                            publishLocation(sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng(),1);
                        } else {
                            updateLocationLogs();
                        }
                    } else {
                        Log.d(TAG, "calling updateDocument...");
                        couchDBHandle.updateDocument(sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng());
                    }
                /*}else {
                    minDistance+=distanceInMtr;
                }*/


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (dis > 300 && disFromStaryPts > 300) {
                strayLat = curLat;
                strayLng = curLong;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        if(myTimerTask_publish!=null){
            myTimerTask_publish.cancel();
            myTimer_publish.cancel();
        }


        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }

    }

    /********************************************************************************************/

    private void startPublishingWithTimer() {
        if (myTimer_publish != null) {
            Log.d(TAG, "Timer already started");
            return;
        }
        myTimer_publish = new Timer();

        myTimerTask_publish = new TimerTask() {
            @Override
            public void run() {
                if (Utility.isNetworkAvailable(getApplicationContext())) {

                    publishLocation(sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng(),0);

                }

            }

        };
        Log.d(TAG, "myTimer_publish interval " + sessionManager.getPubnubScheduleInterval());
        myTimer_publish.schedule(myTimerTask_publish, 0, (long) sessionManager.getPubnubScheduleInterval());
    }
    /**********************************************************************************************/
    public void publishLocation(double latitude, double longitude,int tranist) {

        if (sessionManager.isLogin()) {
            String locationChk = "";
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            Intent gps_inIntent = new Intent("com.driver.gps");
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            if (gps_enabled) {
                locationChk = "1";
                gps_inIntent.putExtra("action", "0");
            } else {
                locationChk = "0";
                gps_inIntent.putExtra("action", "1");

            }
            sendBroadcast(gps_inIntent);
            Utility.printLog("gps enabled or not " + gps_enabled);
            JSONArray jsonArray;
            String pubnubStr = "";
            try {
                if (!sessionManager.getBookings().isEmpty()) {
                    jsonArray = new JSONArray(sessionManager.getBookings());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (pubnubStr.isEmpty()) {
                            pubnubStr = jsonObject.getString("bid") + "|" + jsonObject.getString("custChn") + "|" + jsonObject.getString("status");
                        } else {
                            pubnubStr = pubnubStr + ", " + jsonObject.getString("bid") + "|" + jsonObject.getString("custChn") + "|" + jsonObject.getString("status");
                        }
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject reqObject = new JSONObject();
            try {
                reqObject.put("lg", longitude);
                reqObject.put("lt", latitude);
                reqObject.put("vt", sessionManager.getvehid());
                reqObject.put("pubnubStr", pubnubStr);
                reqObject.put("app_version", version);
                reqObject.put("battery_Per", String.valueOf(battery_level));
                reqObject.put("location_check", locationChk);
                reqObject.put("device_type", String.valueOf(VariableConstant.DEVICE_TYPE));
                reqObject.put("location_Heading", bearing);
                reqObject.put("transit", tranist+"");

                System.out.println("LOCATION request " + reqObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.LOCATION, OkHttp3Connection.Request_type.PUT, reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("LOCATION result " + result);
                    if (result != null) {

                        LocationPojo locationPojo = new Gson().fromJson(result, LocationPojo.class);
                        if(result.contains("statusCode")){
                            if(locationPojo.getStatusCode()!=null && locationPojo.getStatusCode().equals("401")){

                                stopForeground(true);
                                stopSelf();
                            }
                        }else {
                            switch (locationPojo.getErrFlag()) {
                                case 0:
                                    break;
                                case 1:
                                    Utility.toastMessage(LocationUpdateService.this, locationPojo.getErrMsg());
                                    break;
                            }
                        }


                    }

                }

                @Override
                public void onError(String error) {

                }
            }, "");
        } else
            Log.d(TAG, "MyService" + "isLogin  " + false);
    }


    public void updateLocationLogs() {


        JSONArray list = couchDBHandle.retriveDocument();

        Utility.printLog(TAG + "retriveDocument list " + list.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lt_lg", list);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        System.out.println("LOCATION_LOGS req " + jsonObject.toString());

        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.LOCATION_LOGS, OkHttp3Connection.Request_type.PUT, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                System.out.println("LOCATION_LOGS result " + result);
                if (result != null) {
                    LocationPojo locationPojo = new Gson().fromJson(result, LocationPojo.class);
                    switch (locationPojo.getErrFlag()){
                        case 0:
                            updted=true;
                            couchDBHandle.deleteDocument();
                            break;
                        case 1:
                            Utility.toastMessage(LocationUpdateService.this,locationPojo.getErrMsg());
                            break;
                    }

                }


            }

            @Override
            public void onError(String error) {

            }
        }, "");
    }
}
