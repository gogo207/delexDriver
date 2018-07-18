package com.delex.app.bookingUpdate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moda.firebasebasedchat.Activities.ChatMessagesScreen;
import com.example.moda.firebasebasedchat.ModelClasses.MembersItem;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.FirebaseDatabase;
import com.delex.profile.ChangePasswordDialog;
import com.delex.app.CancelReason;
import com.delex.app.invoice.JobCompletedInvoice2;
import com.delex.app.JobDetails;
import com.delex.driver.R;
import com.delex.pojo.AssignedAppointments;
import com.delex.pojo.BookingInvoice;
import com.delex.utility.AppConstants;
import com.delex.utility.AppPermissionsRunTime;
import com.delex.utility.DistanceChangeListner;
import com.delex.utility.LocationUtil;
import com.delex.utility.PicassoMarker;
import com.delex.utility.SessionManager;
import com.delex.utility.Slider;
import com.delex.utility.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class BookingFlow extends AppCompatActivity implements View.OnClickListener,  OnMapReadyCallback, LocationUtil.GetLocationListener, BookingFlowMainMvp.ViewOperations {

    private final int PERMISSION_MAP_CODE = 1005;
    Typeface ClanaproNarrMedium;
    Typeface ClanaproNarrNews;
    Typeface ClanaproMedium;
    BookingInvoice invoice;
    private ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList;
    private Toolbar toolbar;
    private ImageView iv_search, iv_jobdetails;
    private TextView tv_title;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private SessionManager sessionManager;
    private ImageView iv_pickup_loc, iv_cust_img, iv_call_btn, iv_cancel_btn;
    private TextView tv_name, tv_bid, tv_drop_loc, tv_distance_value, tv_timer_value, tv_distance, tv_timer, tv_status_text;
    private AssignedAppointments appointment;
    private LinearLayout ll_googlemap, ll_waze;
    private Slider seekBar;
    private ProgressDialog mDialog;
    private boolean isDeninedRTPs = false, showRationaleRTPs = false, isPermissionGranted = false;
    private String Phone,CountryCode;
    private int timeConsumedSecond = 0;
    private Handler handler;
    private boolean runTimer = false;
    private Runnable myRunnable;
    private JSONObject mJsonObject;
    private double vatAmount;
    private PicassoMarker marker;
    private Location mCurrentLoc,mPreviousLoc;
    private ChangePasswordDialog changePasswordDialog;
    private PresenterImpl presenterImpl;
    private BroadcastReceiver receiver;
    private Marker customer_marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_flow);
        sessionManager = SessionManager.getSessionManager(BookingFlow.this);
        presenterImpl=new PresenterImpl(this,this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        receiveBookingDetails();
        initializeViews();
        initActionBar();

        presenterImpl.setLocationObj(BookingFlow.this,this);
        presenterImpl.setDistanceChangeListner(new DistanceChangeListner() {
            @Override
            public void onDistanceChanged(JSONArray jsonArray) {
                Utility.printLog("onDistance changed ...." + appointment.getBid());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Utility.printLog("onDistance changed ...." + jsonObject.get("bid"));
                        if (jsonObject.get("bid").equals(appointment.getBid())) {
                            tv_distance_value.setText(jsonObject.get("distance") + " " + sessionManager.getmileage_metric());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION.PUSH_ACTION);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                presenterImpl.onBrodcastRecieve(context,intent,appointment.getBid());
            }
        };
        registerReceiver(receiver,filter);
    }

    private void receiveBookingDetails() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            appointment = (AssignedAppointments) bundle.getSerializable("data");
            sessionManager.setBid(appointment.getBid());
        }
    }


    /**********************************************************************************************/
    /**
     * <h1>initActionBar</h1>
     * initilize the action bar*/
    private void initActionBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_back_btn);
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.onthepickup));
        tv_title.setTypeface(ClanaproNarrMedium);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

        iv_jobdetails = (ImageView) findViewById(R.id.iv_jobdetails);
        iv_jobdetails.setVisibility(View.VISIBLE);

        iv_jobdetails.setOnClickListener(this);

    }

    /* ********************************************************************************************/
                                                                      /*initializeViews*/
    /* ********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {
        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        ClanaproMedium = Typeface.createFromAsset(getAssets(), "fonts/CLANPRO-MEDIUM.OTF");

        TextView tv_googlemap, tv_waze;

        tv_googlemap = (TextView) findViewById(R.id.tv_googlemap);
        tv_googlemap.setTypeface(ClanaproMedium);
        tv_googlemap.setText(Html.fromHtml(getString(R.string.googlemaps_color)));
        tv_googlemap.setOnClickListener(this);

        tv_waze = (TextView) findViewById(R.id.tv_waze);
        tv_waze.setTypeface(ClanaproNarrMedium);
        tv_waze.setOnClickListener(this);

        Phone = appointment.getCustomerPhone();
        CountryCode=appointment.getCustomerCountryCode();

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setTypeface(ClanaproNarrMedium);
        tv_name.setText(appointment.getCustomerName());

        tv_bid = (TextView) findViewById(R.id.tv_bid);
        tv_bid.setTypeface(ClanaproNarrNews);
        tv_bid.setText(appointment.getBid());

        tv_drop_loc = (TextView) findViewById(R.id.tv_drop_loc);
        tv_drop_loc.setTypeface(ClanaproNarrNews);


        tv_distance_value = (TextView) findViewById(R.id.tv_distance_value);
        tv_distance_value.setTypeface(ClanaproNarrMedium);

        tv_timer_value = (TextView) findViewById(R.id.tv_timer_value);
        tv_timer_value.setTypeface(ClanaproNarrMedium);

        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_distance.setTypeface(ClanaproNarrNews);

        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_timer.setTypeface(ClanaproNarrNews);

        tv_status_text = (TextView) findViewById(R.id.tv_status_text);
        tv_status_text.setTypeface(ClanaproMedium);

        iv_cust_img = (ImageView) findViewById(R.id.iv_cust_img);

        iv_call_btn = (ImageView) findViewById(R.id.iv_call_btn);
        iv_call_btn.setOnClickListener(this);

        iv_pickup_loc = (ImageView) findViewById(R.id.iv_pickup_loc);

        iv_cancel_btn = (ImageView) findViewById(R.id.iv_cancel_btn);
        iv_cancel_btn.setOnClickListener(this);

        seekBar = (Slider) findViewById(R.id.myseek);
        seekBar.setSliderProgressCallback(new Slider.SliderProgressCallback() {
            @Override
            public void onSliderProgressChanged(int progress) {
                if (progress > 65) {

                    seekBar.setProgress(100);

                    if (Utility.isNetworkAvailable(BookingFlow.this)) {
                        if (AppConstants.getAptStatus_OnTheWay.value.equals(appointment.getStatusCode())) {
//                            updateBookingStatus(AppConstants.getAptStatus_Arrived.value);
                            presenterImpl.updateBookingStatus(AppConstants.getAptStatus_Arrived.value,appointment.getBid());
                        } else if (AppConstants.getAptStatus_Arrived.value.equals(appointment.getStatusCode())) {
//                            updateBookingStatus(AppConstants.getAptStatus_LoadedAndDelivery.value);
                            presenterImpl.updateBookingStatus(AppConstants.getAptStatus_LoadedAndDelivery.value,appointment.getBid());

                        } else if (AppConstants.getAptStatus_LoadedAndDelivery.value.equals(appointment.getStatusCode())) {
//                            updateBookingStatus(AppConstants.getAptStatus_reached_drop_loc.value);
                            presenterImpl.updateBookingStatus(AppConstants.getAptStatus_reached_drop_loc.value,appointment.getBid());

                        } else if (AppConstants.getAptStatus_reached_drop_loc.value.equals(appointment.getStatusCode())) {
//                            updateBookingStatus(AppConstants.getAptStatus_Unloaded.value);
                            presenterImpl.updateBookingStatus(AppConstants.getAptStatus_Unloaded.value,appointment.getBid());

                        }

                    }


                }
            }
        });

        mDialog = new ProgressDialog(BookingFlow.this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tv_distance_value.setText("0.00 " + sessionManager.getmileage_metric());
        tv_timer_value.setText("0 min");

        try {
            JSONArray jsonArray = new JSONArray(sessionManager.getBookings());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("bid").equals(appointment.getBid())) {
                    mJsonObject = jsonArray.getJSONObject(i);
                    tv_distance_value.setText(jsonObject.getString("distance") + " " + sessionManager.getmileage_metric());
                }
            }

            if (mJsonObject != null && (mJsonObject.getLong("time_elapsed") == 0)) {
                runTimer = true;
                mJobTimer();

            } else if (mJsonObject != null) {
                mSubStractTimePaused(mJsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MembersItem membersItem=new MembersItem();
                membersItem.setMemberId1(sessionManager.getMid());//my id
                membersItem.setMemberId2(appointment.getCustomerId());//cust

                Utility.printLog("Booking ID: "+sessionManager.getBid());

                FirebaseDatabase.getInstance().getReference().child("members").child(sessionManager.getBid()).setValue(membersItem);
                Intent intent=new Intent(BookingFlow.this, ChatMessagesScreen.class);

                intent.putExtra("receiverUid",appointment.getCustomerId());// custmor id
                intent.putExtra("receiverName",appointment.getCustomerName()); // custmr name
                intent.putExtra("chatName",sessionManager.getBid());// order id
                intent.putExtra("session",sessionManager.getSessionToken());
                intent.putExtra("langId",sessionManager.getLang());
                startActivity(intent);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestPermissions();
//        checkStatus();
    }


    /**********************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_jobdetails:
                Intent intent = new Intent(BookingFlow.this, JobDetails.class);
                intent.putExtra("data", appointment);
                startActivity(intent);
                break;

            case R.id.iv_call_btn:
                Utility.MakePhoneCall(Phone,CountryCode, this);
                break;

            case R.id.iv_cancel_btn:
                Intent intent1 = new Intent(this, CancelReason.class);
                intent1.putExtra("bid", appointment.getBid());
                startActivity(intent1);
                break;
            case R.id.tv_googlemap:
                if (appointment.getStatusCode().equals("6")) {
                    startGoogleMap(appointment.getPickup_ltg());
                } else {
                    startGoogleMap(appointment.getDrop_ltg());
                }

                break;
            case R.id.tv_waze:
                if (appointment.getStatusCode().equals("6")) {
                    startWazeMap(appointment.getPickup_ltg());
                } else {
                    startWazeMap(appointment.getDrop_ltg());
                }
                break;

        }
    }
    /* *********************************************************************************************/
    /**
     * custom method to check and request for run time permissions
     * if not granted already
     */
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            myPermissionConstantsArrayList = new ArrayList<>();

            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_ACCESS_COARSE_LOCATION);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_ACCESS_FINE_LOCATION);

            if (AppPermissionsRunTime.checkPermission(this, myPermissionConstantsArrayList, PERMISSION_MAP_CODE)) {
                onPermissionGranted();
            }
        } else {
            // Pre-Marshmallow
            onPermissionGranted();
        }
    }

    /* *********************************************************************************************/
    /**
     * custom method to execute after run time permissions
     * granted or if it run time permission no required at all
     */
    private void onPermissionGranted() {
        isDeninedRTPs = false;
        isPermissionGranted = true;
    }

    /* *********************************************************************************************/
    /**
     * predefined method to check run time permissions list call back
     * @param requestCode : to handle the corresponding request
     * @param permissions: contains the list of requested permissions
     * @param grantResults: contains granted and un granted permissions result list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_MAP_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        isDeninedRTPs = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            showRationaleRTPs = shouldShowRequestPermissionRationale(permission);
                        }
                    }
                    break;
                }
                onPermissionDenied();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**********************************************************************************************/
    private void onPermissionDenied() {
        if (isDeninedRTPs) {
            if (!showRationaleRTPs) {
                //goToSettings();
                AppPermissionsRunTime.aDialogOnPermissionDenied(BookingFlow.this);
            } else {
                isDeninedRTPs = showRationaleRTPs = false;

                AppPermissionsRunTime.checkPermission(BookingFlow.this,
                        myPermissionConstantsArrayList, PERMISSION_MAP_CODE);
            }
        } else {
            onPermissionGranted();
        }
    }

    /**
     * custom method to open google map to get direction
     */
    private void startGoogleMap(String ltg) {
        String[] latLong = ltg.split(",");
        Double lat = null, lng = null;
        if (latLong.length > 0) {
            lat = Double.parseDouble(latLong[0]);
            lng = Double.parseDouble(latLong[1]);
        }
        if (lat != null && lng != null) {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                    sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng(),
                    lat, lng);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        }

    }
    /**
     * custom method to open waze map to get direction
     */
    private void startWazeMap(String ltg) {
        String[] latLong = ltg.split(",");
        Double lat = null, lng = null;
        if (latLong.length > 0) {
            lat = Double.parseDouble(latLong[0]);
            lng = Double.parseDouble(latLong[1]);
        }

        if (lat != null && lng != null) {
            try {
                String url = "waze://?ll=" + lat + "," + lng + "&navigate=yes";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Intent intent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                startActivity(intent);
            }
        }

    }

    @Override
    public void setCurrentStatus(String aptStatus) {
        Log.d("BookingFlow", " : " + aptStatus);

        if ("6".equals(aptStatus)) {

            if(appointment.isPaidByReceiver()){
                changePasswordDialog = new ChangePasswordDialog(BookingFlow.this, "receiver", new ChangePasswordDialog.RefreshProfile() {
                    @Override
                    public void onRefresh() {
                        changePasswordDialog.dismiss();
                    }
                });
                changePasswordDialog.show();
            }

            tv_distance.setVisibility(View.VISIBLE);
            tv_distance_value.setVisibility(View.VISIBLE);
            tv_timer.setText(getString(R.string.timer));
            tv_title.setText(getString(R.string.onthepickup));
            tv_status_text.setText(

                    getString(R.string.arrivedToPick));
            tv_drop_loc.setText(appointment.getAddrLine1());

            String[] latLong=appointment.getPickup_ltg().split(",");
            LatLng ltg = new LatLng(Double.valueOf(latLong[0]), Double.valueOf(latLong[1]));
            customer_marker.setPosition(ltg);

            customer_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.home_pickup_icon));

        } else if ("7".equals(aptStatus)) {
            tv_title.setText(getString(R.string.start_trip));
            tv_status_text.setText(getString(R.string.start));
            tv_timer.setText(getString(R.string.loading_time));
            tv_distance.setVisibility(View.GONE);
            tv_distance_value.setVisibility(View.GONE);
            tv_drop_loc.setText(appointment.getDropLine1());
            iv_pickup_loc.setImageResource(R.drawable.home_dropoff_icon);

            String[] latLong=appointment.getDrop_ltg().split(",");
            LatLng ltg = new LatLng(Double.valueOf(latLong[0]), Double.valueOf(latLong[1]));
            customer_marker.setPosition(ltg);
            customer_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.home_dropoff_icon));

            // jsonObject.put("app_route", sessionManager.getArrivedRouteArray().toString());
        } else if ("8".equals(aptStatus)) {
            tv_distance.setVisibility(View.VISIBLE);
            tv_distance_value.setVisibility(View.VISIBLE);
            iv_pickup_loc.setImageResource(R.drawable.home_dropoff_icon);
            tv_timer.setText(getString(R.string.timer));
            iv_cancel_btn.setVisibility(View.GONE);
            tv_title.setText(getString(R.string.onTrip));
            tv_status_text.setText(getString(R.string.reached_at_drop_location));
            tv_drop_loc.setText(appointment.getDropLine1());
            customer_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.home_dropoff_icon));

            // jsonObject.put("ent_actual_pickup_lat",sessionManager.getDriverCurrentLat());
            // jsonObject.put("ent_actual_pickup_lng",sessionManager.getDriverCurrentLng());
            // jsonObject.put("app_route", sessionManager.getPickUpRouteArray().toString());
        } else if ("9".equals(aptStatus)) {
            tv_distance.setVisibility(View.GONE);
            tv_distance_value.setVisibility(View.GONE);
            iv_pickup_loc.setImageResource(R.drawable.home_dropoff_icon);
            iv_cancel_btn.setVisibility(View.GONE);
            tv_drop_loc.setText(appointment.getDropLine1());
            tv_timer.setText(getString(R.string.unloading_time));
            tv_title.setText(getString(R.string.unload));
            tv_status_text.setText(getString(R.string.unload).toUpperCase());
            customer_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.home_dropoff_icon));

                            /*jsonObject.put("ent_dropAddress", dropAdrs);
                            jsonObject.put("ent_drop_lat", sessionManager.getDriverCurrentLat());
                            jsonObject.put("ent_dropLong", sessionManager.getDriverCurrentLng());
                            jsonObject.put("ent_dist", sessionManager.getDistanceInDouble());
                            jsonObject.put("app_route", sessionManager.getRouteArray().toString());*/
        } else if ("16".equals(aptStatus)) {
                           /* tv_title.setText();
                            tv_status_text.setText();*/

            if (myRunnable != null) {
                handler.removeCallbacks(myRunnable);
            }
            runTimer = false;

            sessionManager.setTimeWhilePaused(0);
            sessionManager.setElapsedTime(0);
            sessionManager.setTimerStarted(false);
            timeConsumedSecond = 0;


                Intent intent = new Intent(BookingFlow.this, JobCompletedInvoice2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle=new Bundle();
                bundle.putSerializable("appointment",appointment);

            if(invoice==null){
                invoice =  appointment.getInvoice();
            }
            //Log.i("vat",invoice.getVAT()+"");
            //vatAmount=invoice.getVAT();
            //bundle.putDouble("vat",vatAmount);
            intent.putExtras(bundle);

                startActivity(intent);

        } else {
            Log.d("BookingFlow", " unknown : " + aptStatus);
        }

    }

    @Override
    public void onPushReceived(String action, String msg) {
        if(action.equals("3")){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            Resources r = getResources();
            //convert our dp margin into pixels
            int marginPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, marginPixels, marginPixels);
        }
        if (ActivityCompat.checkSelfPermission(BookingFlow.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookingFlow.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LatLng latLng = new LatLng(sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        googleMap.setMyLocationEnabled(false);

        try{
            marker = new PicassoMarker(googleMap.addMarker(new MarkerOptions().position(latLng).title("First Point")));

            if(!sessionManager.getVehicleImage().equals(""))
            {
                String url= sessionManager.getVehicleImage();
                Picasso.with(this).load(R.drawable.ic_launcher).resize(75,75).into(marker);
            }
            else
            {
                Picasso.with(this).load(R.drawable.ic_launcher).into(marker);
            }
//            setCarMarker(latLng);
            String[] latLong=appointment.getDrop_ltg().split(",");
            LatLng ltg = new LatLng(Double.valueOf(latLong[0]), Double.valueOf(latLong[1]));
            customer_marker=googleMap.addMarker(new MarkerOptions().position(ltg));
            presenterImpl.setCarMarker(latLng,marker,map);
            presenterImpl.setCurrentStatus(appointment.getStatusCode());
        }
        catch (Exception e)
        {
            Log.e("Picasso","caught an exception");
        }

    }

    @Override
    public void updateLocation(Location location) {
        if (map != null) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f));

        }
        if(marker!=null){
//            setCarMarker(location);
            presenterImpl.setCarMarker(location,marker,map);
        }
    }

    @Override
    public void location_Error(String error) {
        Toast.makeText(BookingFlow.this, error, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void setDistanceAndTimer(String status) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(sessionManager.getBookings());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (jsonObject.get("bid").equals(appointment.getBid())) {
                    jsonObject.put("distance", 0.0);
                    jsonObject.put("status", status);
                }

            }
            sessionManager.setBookings(jsonArray.toString());
            tv_distance_value.setText("0 " + sessionManager.getmileage_metric());

            Utility.printLog("BookingFlow "+sessionManager.getBookings());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_timer_value.setText("0 min");

        if (myRunnable != null) {
            handler.removeCallbacks(myRunnable);
        }

        sessionManager.setTimeWhilePaused(0);
        sessionManager.setElapsedTime(0);
        sessionManager.setTimerStarted(false);
        timeConsumedSecond = 0;


        runTimer = true;
        mJobTimer();

    }


    private void mSubStractTimePaused(JSONObject jsonObject) {
        try {

            if (jsonObject.getLong("time_elapsed") > 0) {
                long mTimeWhilePaused = jsonObject.getLong("time_paused");
                if (!"0".equals(mTimeWhilePaused)) {
                    long currentTime = System.currentTimeMillis() - mTimeWhilePaused;
                    Utility.printLog("TIME ELAPSED EQUALS" + currentTime);
                    timeConsumedSecond = jsonObject.getInt("time_elapsed");
                    timeConsumedSecond = timeConsumedSecond + Math.round(((float) (currentTime)) / 1000f);
                    timeConsumedSecond += 1;//for error apprx
                } else {
                    timeConsumedSecond = (0);
                }
            } else {
                timeConsumedSecond = (0);
            }
        } catch (JSONException e) {


        }
        runTimer = true;

        mJobTimer();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mJobTimer() {
        sessionManager.setTimerStarted(true);
        Log.d("timer",timeConsumedSecond+"");
        handler = new Handler();
        myRunnable = new Runnable() {

            @Override
            public void run() {
                if (runTimer) {
                    timeConsumedSecond = timeConsumedSecond + 1;
                    tv_timer_value.setText("" + Utility.getDurationString(timeConsumedSecond));

                    handler.postDelayed(this, 1000);
                } else {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(sessionManager.getBookings());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            if (jsonObject.get("bid").equals(appointment.getBid())) {
                                jsonObject.put("time_paused", System.currentTimeMillis());
                                jsonObject.put("time_elapsed", timeConsumedSecond);
                            }

                        }
                        sessionManager.setBookings(jsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        handler.postDelayed(myRunnable, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        runTimer = false;
        sessionManager.setTimeWhilePaused(System.currentTimeMillis());
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSuccess(String aptStatus, BookingInvoice invoice) {
        seekBar.setProgress(0);
        appointment.setStatusCode(aptStatus);
        if(invoice!=null){
            this.invoice=invoice;
            appointment.setInvoice(invoice);
        }
    }

    @Override
    public void onError(String error) {
        seekBar.setProgress(0);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar() {
        mDialog.setMessage(getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void dismissProgressbar() {
        if (mDialog != null  && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog.cancel();
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


}
