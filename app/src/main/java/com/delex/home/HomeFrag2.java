package com.delex.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.delex.app.MainActivity;
import com.delex.app.MainPresenter;
import com.delex.app.MainWorkerClass;
import com.delex.driver.R;
import com.delex.login.Login;
import com.delex.pojo.AssignedAppointments;
import com.delex.pojo.AssignedTripsPojo;
import com.delex.service.LocationUpdateService;
import com.delex.utility.AppConstants;
import com.delex.utility.AppRater;
import com.delex.utility.LocationUtil;
import com.delex.utility.MyPubnub;
import com.delex.utility.NetworkConnection;
import com.delex.utility.PicassoMarker;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;

import java.util.ArrayList;


public class HomeFrag2 extends Fragment implements OnMapReadyCallback, LocationUtil.GetLocationListener, MainPresenter, View.OnClickListener {

    //    private Slider myseek_online,myseek_ofline;
//    private RelativeLayout Bottomlayout_online,Bottomlayout_ofline;

    AlertDialog alertDialogGpsWarning;
    private View rootView;
    private String TAG = HomeFrag2.class.getSimpleName();
    private SessionManager sessionManager;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private MainActivity activity;
    private ArrayList<AssignedAppointments> currentList;
    private HomeCurrentJobFrag homeCurrentJobFrag;
    private MainWorkerClass workerClass;
    private ProgressDialog pDialog;
    private TextView tv_on_off_statas, tv_no_network, tvHeaderCash,tvHeaderCas, tvCash, tvHeaderSLimit, tvSoftLim, tvHeaderHLimit, tvHardLim,netcheck;
    private LinearLayout ll_bookings;
    private RelativeLayout ll_frag_map;
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    private boolean updted = false;
    private LocationUtil locationUtilObj;
    private Location mCurrentLoc;
    private Location mPreviousLoc;
    private PicassoMarker marker;
    private ImageView ivMyLocation;
    private  LinearLayout ll_limit;
    private boolean first=false;

    /**********************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home2, container, false);
        activity = (MainActivity) getActivity();
        TextView netcheck=rootView.findViewById(R.id.tv_net_check);
     /*   if(Utility.isNetworkAvailable(getContext())){
            netcheck.setVisibility(View.GONE);

        }else if(!Utility.isNetworkAvailable(getContext())){
            netcheck.setVisibility(View.VISIBLE);
        }*/
        tv_on_off_statas = (TextView) activity.findViewById(R.id.tv_on_off_statas);
        tv_on_off_statas.setOnClickListener(this);
        sessionManager = SessionManager.getSessionManager(activity);
        workerClass = new MainWorkerClass(sessionManager, this);
        locationUtilObj = new LocationUtil(activity, this);
        alertDialogGpsWarning = Utility.DisplayPromptForEnablingGPS(getActivity());
        BroadcastReciever_check();
        AppRater.app_launched(getContext());
        initializeViews(rootView);
        return rootView;
    }

    private void BroadcastReciever_check() {
        filter = new IntentFilter();
        filter.addAction("com.app.driverapp.internetStatus");
        filter.addAction("com.driver.gps");
        filter.addAction(AppConstants.ACTION.PUSH_ACTION);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals("com.driver.gps")) {
                    String action = intent.getStringExtra("action");
                    if (action.equals("1")) {
                        //alertDialogGpsWarning.show();
                    } else {
                        if (alertDialogGpsWarning.isShowing()) {
                            //alertDialogGpsWarning.dismiss();
                        }
                    }
                    return;
                }
                Bundle bucket = intent.getExtras();
                String status1 = bucket.getString("STATUS");
                String action=bucket.getString("action");


                if ("1".equals(status1)) {
                    if (!updted) {
//                        setStatusUpdate(4);
//                        workerClass.updateStatus();
                        workerClass.getCurrentStatus();
                        updted = true;
                        tv_no_network.setVisibility(View.GONE);
                        //Toast.makeText(context, "network found", Toast.LENGTH_SHORT).show();
                    }

                } else if ("0".equals(status1)){
                    updted = false;
                    tv_no_network.setVisibility(View.VISIBLE);
                    if (!Utility.isNetworkAvailable(context)) {
                        // Toast.makeText(context, "no network found", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!NetworkConnection.isConnectedFast(context)) {
                        //Toast.makeText(context, "low network found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                if(action!=null && !action.isEmpty()){
                    if(action.equals("3")||action.equals(3)||action.equals("201")||action.equals(201)){
                        workerClass.getCurrentStatus();
                    }
                }
            }
        };

        if (receiver != null) {
            getActivity().registerReceiver(receiver, filter);
        }

    }


    /**********************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        //BroadcastReciever_check();
        workerClass.getCurrentStatus();
        if (locationUtilObj != null /*&& !locationUtilObj.isGoogleAPIConnected()*/) {
            locationUtilObj.checkLocationSettings();
            locationUtilObj.restart_location_update();
        }
    }

    /**********************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationUtilObj != null) {
            locationUtilObj.stop_Location_Update();
        }

    }

    /* ********************************************************************************************/

    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews(View view) {


        currentList = new ArrayList<>();
        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/ClanPro-NarrMedium.otf");


        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        ll_bookings = (LinearLayout) rootView.findViewById(R.id.ll_bookings);
        ll_frag_map = (RelativeLayout) rootView.findViewById(R.id.ll_frag_map);
        /*ll_limit=(LinearLayout)rootView.findViewById(R.id.ll_limit);


        if (sessionManager.isWalletEnabled()){
            ll_limit.setVisibility(View.VISIBLE);
        }
        else if (!sessionManager.isWalletEnabled()){
            ll_limit.setVisibility(View.GONE);
        }*/

        tv_no_network = (TextView) rootView.findViewById(R.id.tv_no_network);
        tv_no_network.setTypeface(ClanaproNarrMedium);

        tvHeaderCash = (TextView) rootView.findViewById(R.id.tvHeaderCash);
        tvHeaderCash.setTypeface(ClanaproNarrMedium);
        tvHeaderCash.setSelected(true);

        tvCash = (TextView) rootView.findViewById(R.id.tvCash);
        tvCash.setTypeface(ClanaproNarrMedium);

        tvHeaderCas=(TextView)rootView.findViewById(R.id.tvHeaderCas);
        tvHeaderCas.setTypeface(ClanaproNarrMedium);

        tvHeaderSLimit = (TextView) rootView.findViewById(R.id.tvHeaderSLimit);
        tvHeaderSLimit.setTypeface(ClanaproNarrMedium);

        tvSoftLim = (TextView) rootView.findViewById(R.id.tvSoftLim);
        tvSoftLim.setTypeface(ClanaproNarrMedium);

        tvHeaderHLimit = (TextView) rootView.findViewById(R.id.tvHeaderHLimit);
        tvHeaderHLimit.setTypeface(ClanaproNarrMedium);

        tvHardLim = (TextView) rootView.findViewById(R.id.tvHardLim);
        tvHardLim.setTypeface(ClanaproNarrMedium);

        ivMyLocation = (ImageView) rootView.findViewById(R.id.ivMyLocation);



        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map);
        mapFragment.getMapAsync(this);

        setLimits();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        first=true;
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
            map.getUiSettings().setMyLocationButtonEnabled(false);


        }
        setGoogleMap();


    }

    @Override
    public void updateLocation(Location location) {
        if (map != null) {
            if(first){
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f));
                first=false;
            }


            if (marker != null) {
                setCarMarker(location);
            }
        }
    }

    @Override
    public void location_Error(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        pDialog.show();
    }

    @Override
    public void hideProgress() {
        if(pDialog.isShowing()&&isAdded())
            pDialog.dismiss();
    }


    @Override
    public void updatedStatus(int status) {

        if (isAdded()) {
            setStatusUpdate(status);
        }

    }

    public void setGoogleMap() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {
            map.setMyLocationEnabled(false);
            map.clear();
            LatLng latlng = new LatLng(sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f));
            marker = new PicassoMarker(map.addMarker(new MarkerOptions().position(latlng).title("First Point")));
            if (!sessionManager.getVehicleImage().equals("")) {
                String url = sessionManager.getVehicleImage();
                Utility.printLog(TAG + "  mapImageUrl  " + url);
//                Picasso.with(getActivity()).load(R.drawable.truckr_map_logo).resize(50, 50).into(marker);
                Picasso.with(getActivity()).load(R.drawable.ic_launcher).resize(80, 80).into(marker);
            } else {
                Picasso.with(getActivity()).load(R.drawable.ic_launcher).into(marker);
            }
            setCarMarker(latlng);
        } catch (Exception e) {
            Log.e("Picasso", "caught an exception");
        }
        ivMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latlng = new LatLng(sessionManager.getDriverCurrentLat(), sessionManager.getDriverCurrentLng());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f));
            }
        });
    }

    @Override
    public void apiError(String message, int flag) {
        switch (flag) {
            case 1:
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity(), getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                }
                break;
            case 401:
//                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                if (isAdded()) {

                    sessionManager.setIsLogin(false);
                    Utility.mUnSubscribeToTopics(sessionManager.getPushTopics());

                    if(Utility.isMyServiceRunning(LocationUpdateService.class,(Activity) getActivity()))
                    {
                        Intent stopIntent = new Intent(getActivity(), LocationUpdateService.class);
                        stopIntent.setAction(AppConstants.ACTION.STOPFOREGROUND_ACTION);
                        getActivity().startService(stopIntent);
                    }
                    sessionManager.clearSharedPredf();
                    getActivity().startActivity(new Intent(getActivity(), Login.class));
                    getActivity().finish();
                }
                break;
        }

    }

    @Override
    public void updateAppointments(AssignedTripsPojo tripsPojo) {

        if (tripsPojo.getData().getAppointments().size() == 0) {

            ll_bookings.setVisibility(View.GONE);
//            ll_frag_map.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));

        } else {


            //     ll_frag_map.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,280));

            ll_bookings.setVisibility(View.VISIBLE);
//            ll_frag_map.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int)(getResources().getDisplayMetrics().density*280)));
        }
        homeCurrentJobFrag = HomeCurrentJobFrag.getHomeCurrentJobFrag(tripsPojo.getData());

        if (isAdded()) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container_frag, homeCurrentJobFrag)
                    .commitAllowingStateLoss();
        }


    }

    private void setStatusUpdate(int status) {
        switch (status) {
            case 3:
                tv_on_off_statas.setText(getString(R.string.go_offline));
                tv_on_off_statas.setSelected(true);
                MyPubnub.getInstance(getActivity()).stopPubnub();
                MyPubnub.getInstance(getActivity()).subscribe();
                break;
            case 4:
                tv_on_off_statas.setText(getString(R.string.go_online));
                tv_on_off_statas.setSelected(false);
                MyPubnub.getInstance(getActivity()).stopPubnub();
                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_on_off_statas:
                workerClass.updateStatus();
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void setCarMarker(final Location location) {
        mCurrentLoc = location;

        if (mPreviousLoc == null) {
            mPreviousLoc = location;
        }

        final float bearing = mPreviousLoc.bearingTo(mCurrentLoc);
        if (marker != null) {

            /*carMarker.setPosition(new LatLng(lat, lng));
            carMarker.setAnchor(0.5f, 0.5f);
            carMarker.setRotation(bearing);
            carMarker.setFlat(true);*/

            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = map.getProjection();
            Point startPoint = proj.toScreenLocation(new LatLng(mPreviousLoc.getLatitude(), mPreviousLoc.getLongitude()));
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * mCurrentLoc.getLongitude() + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * mCurrentLoc.getLatitude() + (1 - t)
                            * startLatLng.latitude;
                    marker.getmMarker().setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    marker.getmMarker().setAnchor(0.5f, 0.5f);
                    marker.getmMarker().setRotation(bearing);
                    marker.getmMarker().setFlat(true);


                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        /*if (hideMarker) {
                            marker.setVisible(false);
						} else {
							marker.setVisible(true);
						}*/
                    }
                }
            });
        }

        mPreviousLoc = mCurrentLoc;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////

    public void setCarMarker(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        map.getUiSettings().setZoomControlsEnabled(false);

        if (marker != null)
            marker.getmMarker().setPosition(latLng);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            try {
                getActivity().unregisterReceiver(receiver);

            } catch (Exception e) {

            }
        }
    }

    public void setLimits(){
        tvSoftLim.setText(sessionManager.getCurrencySymbol()+" -"+sessionManager.getSoftLimit());
        tvHardLim.setText(sessionManager.getCurrencySymbol()+" -"+sessionManager.getHardLimit());
        tvCash.setText(sessionManager.getCurrencySymbol()+""+sessionManager.getWalletAmount());
    }
}
