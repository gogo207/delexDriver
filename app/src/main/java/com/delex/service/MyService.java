package com.delex.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.delex.pojo.LocationPojo;
import com.delex.utility.MyPubnub;
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
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by ads on 15/05/17.
 */
public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String METER = "meters";
    private static final String KILOMETER = "Kilometers";
    private static final String NAUTICAL_MILES = "nauticalMiles";
    public static double distancespd;
    private static int counter = 1;
    public JSONArray jsonArray, jsonArrayPickUp, jsonArrayArrived;
    public double prevLatTimer = 0.0, prevLongTimer = 0.0;
    Timer myTimer_publish;
    TimerTask myTimerTask_publish;
    double prevLat = 0.0, prevLng = 0.0;
    double distanceKm;
    String strDouble;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MyPubnub myPubnub;
    private boolean isMyServiceStarted;
    private SessionManager sessionManager;
    private PubNub pubnub;
    private String TAG = MyService.class.getSimpleName();
    private double strayLat = -1.0, strayLng = -1.0;
    private String locationChk = "0";
    private int BataryPerLevel = 0;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            BataryPerLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        }
    };
    private String version = "";


    public MyService() {

    }

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

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /***************************************************************/
    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        isMyServiceStarted = false;
        pubnub = MyPubnub.getInstance(MyService.this).getPubnubInstance();
        sessionManager = SessionManager.getSessionManager(MyService.this);
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, iFilter);


        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = info.versionName;


        jsonArrayPickUp = new JSONArray();
        jsonArrayArrived = new JSONArray();

        if (sessionManager.getBeginJourney()) {
            distancespd = sessionManager.getDistanceInDouble();
        } else {
            distancespd = 0.0;
        }

        if (!sessionManager.getRouteArray().isEmpty() && (sessionManager.getAppointmentStatus() == 8)) {
            try {
                jsonArray = new JSONArray(sessionManager.getRouteArray());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has("bid")) {
                    if (!jsonObject.get("bid").equals(sessionManager.getBid())) {
                        sessionManager.setRouteArray("");
                        jsonArray = new JSONArray();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            jsonArray = new JSONArray();
        }

    }

    /***************************************************************/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        counter = 1;
        if (myPubnub == null) {
            myPubnub = MyPubnub.getInstance(MyService.this);
            Log.d(TAG, " startService getInstance ");
        }


        if (!isMyServiceStarted) {
            isMyServiceStarted = true;
            mGoogleApiClient.connect();
            startPublishingWithTimer();
            Log.d(TAG, " startService isMyServiceStarted ");
        }
        return START_STICKY;
    }


    /***************************************************************/

    /***************************************************************/
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * This method run in every 8 seconds.
     */
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
                    /*if(prevLatTimer==0.0 || prevLongTimer==0.0)
                    {
                        prevLatTimer=sessionManager.getDriverCurrentLat();
                        prevLongTimer=sessionManager.getDriverCurrentLng();
                    }
                    if(distance(prevLatTimer,prevLongTimer,sessionManager.getDriverCurrentLat(),sessionManager.getDriverCurrentLng(),METER)>=sessionManager.getMinRadiusForPubnubPublish())
                    {*/
                    prevLatTimer = sessionManager.getDriverCurrentLat();
                    prevLongTimer = sessionManager.getDriverCurrentLng();
                    publishLocation(sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng());
//                    }

                } else {
                    Log.d(TAG, "Internet connection illa");
                }

            }

        };
        Log.d(TAG, "myTimer_publish interval " + sessionManager.getPubnubScheduleInterval());
        myTimer_publish.schedule(myTimerTask_publish, 0, (long) sessionManager.getPubnubScheduleInterval());

    }

    public void here(final Context context, final String channel, final String key, final HashMap hashMap) {
        pubnub.hereNow()
                .includeUUIDs(true)
                .channels(Arrays.asList(channel))
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        if (status != null) {
                            if (status.getStatusCode() == 200) {
                                if (result != null) {

                                    if (result.getTotalOccupancy() > 0) {
                                        Map<String, PNHereNowChannelData> map = result.getChannels();
                                        List<PNHereNowOccupantData> list = map.get(channel).getOccupants();

                                        for (int i = 0; i < list.size(); i++) {
                                            if (list.get(i).getUuid().equals(key)) {

                                                MyPubnub.getInstance(context).publishOnPubnub(channel, hashMap);
                                                Log.d(TAG, "MyService :" + key + " present");
                                                break;
                                            } else {
                                                Log.d(TAG, "MyService :" + key + " is not present");
                                            }

                                        }

                                    }
                                }
                            }
                        }
                    }

                });
    }

    public void publishLocation(double latitude, double longitude) {
        //locationupdateCount=0;

        // String driverName=sessionManager.getDriverName();
        if (sessionManager.isLogin()) {

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


            JSONObject reqObject = new JSONObject();
            try {
                reqObject.put("lg", longitude);
                reqObject.put("lt", latitude);
                reqObject.put("vt", sessionManager.getvehid());
                reqObject.put("pubnubStr", "");
                reqObject.put("app_version", version);
                reqObject.put("battery_Per", String.valueOf(BataryPerLevel));
                reqObject.put("location_check", locationChk);
                reqObject.put("device_type", String.valueOf(VariableConstant.DEVICE_TYPE));
                reqObject.put("location_Heading", "");
            } catch (JSONException e) {
                e.printStackTrace();

            }

            OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.LOCATION, OkHttp3Connection.Request_type.PUT, reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("LOCATION result " + result);
                    if (result != null) {
                        LocationPojo locationPojo = new Gson().fromJson(result, LocationPojo.class);
                        switch (locationPojo.getErrFlag()) {
                            case 0:
                                /*if(!locationPojo.getData().getBid().matches("0"))
                                {
                                    //Utility.toastMessage(MyService.this,"booking id "+locationPojo.getData().getBid());
                                }
                                else {
                                    //Utility.toastMessage(MyService.this,"booking id "+locationPojo.getData().getBid());
                                }*/
                                break;
                            case 1:
                                Utility.toastMessage(MyService.this, locationPojo.getErrMsg());
                                break;
                        }

                    } else {
                    }

                }

                @Override
                public void onError(String error) {

                }
            }, "");

           /* int status=sessionManager.getAppointmentStatus();

            HashMap<String,String> hashMap=new HashMap<String, String>();

            hashMap.put("lt",latitude+"");
            hashMap.put("lg",longitude+"");
            hashMap.put("chn",sessionManager.getDriverChannel());
            hashMap.put("a",status+"");
            hashMap.put("vt",sessionManager.getTypeId());
            String mid = sessionManager.getDriverUuid().replaceAll("m_","");
            hashMap.put("mid",mid);

            if (sessionManager.getAppointmentStatus()!=4)
            {
                hashMap.put("bid",sessionManager.getBid());
                Log.d(TAG,"MyService"+"Message  " +hashMap.toString());
                Log.d("MyService","channel name  " +sessionManager.getBookingChannel()+" passenger email " );

                here(this,sessionManager.getDriverChannel(), "s_"+sessionManager.getSID() ,  hashMap);
                MyPubnub.getInstance(this).publishOnPubnub(sessionManager.getBookingChannel(),hashMap);
                MyPubnub.getInstance(this).publishOnPubnub(sessionManager.getDriverChannel(),hashMap);

            }
            else
            {
                Log.d(TAG,"MyService"+"Message  " +hashMap.toString());
                Log.d("MyService","channel name  " +sessionManager.getServerChannel() );
                MyPubnub.getInstance(this).publishOnPubnub(sessionManager.getServerChannel(),hashMap);

            }
*/
        } else
            Log.d(TAG, "MyService" + "isLogin  " + false);
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
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location currentLoc) {
                            double currentLat = currentLoc.getLatitude();
                            double currentLng = currentLoc.getLongitude();
                            sessionManager.setDriverCurrentLat("" + currentLat);
                            sessionManager.setDriverCurrentLng("" + currentLng);

                            if (sessionManager.getAppointmentStatus() != 4) {
                                Log.d(TAG, "status...." + sessionManager.getAppointmentStatus());
                                mCalculateRouteArray();
                            } else {
                                if (jsonArrayPickUp.length() > 0) {
                                    jsonArrayPickUp = new JSONArray();
                                }
                            }

                            Log.d(TAG, "MyService  lat:" + sessionManager.getDriverCurrentLat() + " long: " + sessionManager.getDriverCurrentLng());


                            if (sessionManager.isBookingCompleted()) {
                                sessionManager.setBookingCompleted(false);
                                distancespd = 0.0;
                                sessionManager.setDistanceInDouble("" + 0.0);

                                jsonArray = new JSONArray();


                                Log.d(TAG, " jsonArray " + jsonArray.toString());
                            }


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

    /***************************************************************/
    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        myTimerTask_publish.cancel();
        myTimer_publish.cancel();
//        myPubnub.stopPubnub();
        isMyServiceStarted = false;
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }

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

            if (sessionManager.getAppointmentStatus() == 8) {

                jsonArray.put(jsonObject);
                sessionManager.setRouteArray(jsonArray.toString());
                Log.d(TAG, "Route :" + sessionManager.getRouteArray());


            } else if (sessionManager.getAppointmentStatus() == 6) {
                jsonArrayPickUp.put(jsonObject);
                sessionManager.setPickUpRouteArray(jsonArrayPickUp.toString());
                Log.d(TAG, "Route Pickup:" + sessionManager.getPickUpRouteArray());

            } else if (sessionManager.getAppointmentStatus() == 7) {
                jsonArrayArrived.put(jsonObject);
                sessionManager.setArrivedRouteArray(jsonArrayArrived.toString());
                Log.d(TAG, "Route Pickup::" + sessionManager.getArrivedRouteArray());

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


        if (((dis >= sessionManager.getMinDistForRouteArray()) && (dis <= 200)) || (disFromStaryPts != -1.0 && ((disFromStaryPts >= sessionManager.getMinDistForRouteArray()) && (disFromStaryPts <= 200)))) {

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
            if (sessionManager.getAppointmentStatus() == 8) {
                if (jsonArrayArrived.length() > 0) {
                    jsonArrayArrived = new JSONArray();
                }
                jsonArray.put(jsonObject);

                sessionManager.setRouteArray(jsonArray.toString());
                Log.d(TAG, "Route pickup:1" + sessionManager.getRouteArray());
            } else if (sessionManager.getAppointmentStatus() == 6) {
                jsonArrayPickUp.put(jsonObject);

                sessionManager.setPickUpRouteArray(jsonArrayPickUp.toString());
                Log.d(TAG, "Route pickup:1" + sessionManager.getPickUpRouteArray());
            } else if (sessionManager.getAppointmentStatus() == 7) {
                if (jsonArrayPickUp.length() > 0) {
                    jsonArrayPickUp = new JSONArray();
                }
                jsonArrayArrived.put(jsonObject);

                sessionManager.setArrivedRouteArray(jsonArrayArrived.toString());
                Log.d(TAG, "Route pickup:1" + sessionManager.getArrivedRouteArray());
            }

            distancespd += distanceInMtr;
            Log.d(TAG, "Myservice Total distance" + distancespd);
            sessionManager.setDistanceInDouble("" + distancespd);
            //distanceKm = distancespd *(0.00062137);
            distanceKm = distancespd * Double.parseDouble(sessionManager.getDISTANCE_CONVERSION_UNIT());
            strDouble = String.format(Locale.US, "%.2f", distanceKm);
            Log.d(TAG, "MyService Distance format: " + strDouble);
            sessionManager.setDistance(strDouble);
            Log.d(TAG, " mCalculateArray : Dis " + dis + " , Stray Dis " + disFromStaryPts);
            Log.d(TAG, " mCalculateArray : distanceInMtr " + distanceInMtr);

        } else {
            if (dis > 200 && disFromStaryPts > 200) {
                strayLat = curLat;
                strayLng = curLong;
            }
        }

    }
}
