package com.delex.app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delex.bankDetails.AddBankAccountActivity;
import com.delex.bankDetails.BankListFrag;
import com.delex.portal.PortalFrag;
import com.delex.wallet.activity.WalletFragment;
import com.example.moda.firebasebasedchat.AppController;
import com.example.moda.firebasebasedchat.ModelClasses.SelectUserItem;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.delex.pojo.SigninData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.delex.history.HistoryActivity;
import com.delex.history.HistoryFragment;
import com.delex.profile.MyProfileFrag;
import com.delex.bankDetails.BankNewAccountActivity;
import com.delex.driver.R;
import com.delex.home.HomeFrag2;
import com.delex.invite.InviteFrag;
import com.delex.pojo.GetConfig;
import com.delex.service.LocationUpdateService;
import com.delex.support.SupportFrag;
import com.delex.utility.AppConstants;
import com.delex.utility.AppPermissionsRunTime;
import com.delex.utility.CircleImageView;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/***************************************************************************************************
 * <h1>MainActivity</h1>
 *<p></p>
 * ************************************************************************************************/
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = MainActivity.class.getName();

    private ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList;
    private DrawerLayout drawer;
    private CircleImageView iv_profile;
    private ImageView iv_history, button_menu, toolbarMenu;
    private TextView tv_prof_name, tv_viewpof;
    private Fragment fragment,fragment1;
    private TextView tv_on_off_statas, tvTitle, tvTitle2, tv_prof_edit,netcheck;
    private RelativeLayout rl_profile_view, menu_layout;
    private AppBarLayout abarMain;
    private boolean isDeninedRTPs = false, showRationaleRTPs = false, isPermissionGranted = false;
    private ProgressDialog pDialog;
    private SessionManager sessionManager;
    private boolean homeOpen = true,portal=false;
    private boolean walletOpen=false;
    private String currentVersion = "";
    SigninData signinData;
    private BroadcastReceiver receiver;
    private NavigationView navigationView;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView netcheck=(TextView)findViewById(R.id.tv_net_check);
       /* if(Utility.isNetworkAvailable(this)){
            netcheck.setVisibility(View.GONE);

        }else if(!Utility.isNetworkAvailable(this)){
            netcheck.setVisibility(View.VISIBLE);
        }*/
        signinData=new SigninData();


        sessionManager = new SessionManager(MainActivity.this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        //Subscribing to topic
        if (!sessionManager.getPushTopic().isEmpty())
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + sessionManager.getPushTopic());


        try {
            currentVersion = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        initVar();
        initChatVariables();
        IntentFilter filter=new IntentFilter();
        filter.addAction(AppConstants.ACTION.PUSH_ACTION);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bucket = intent.getExtras();
                String action=bucket.getString("action");
                if(action!=null){
                    if(action.equals("53")||action.equals("54")||action.equals("49")){
                        String msg=bucket.getString("msg");

                        if(action.equals("53")||action.equals("54")){
//                            Utility.mShowMessage(getResources().getString(R.string.message),msg,MainActivity.this);
                            GetAppConfig(true);
                        }else {
                            GetAppConfig(false);
                        }
                    }
                }
            }
        };
        registerReceiver(receiver,filter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GetAppConfig(false);
        checkAndRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
     /*   if(Utility.isNetworkAvailable(this)){
            netcheck.setVisibility(View.GONE);

        }else if(!Utility.isNetworkAvailable(this)){
            netcheck.setVisibility(View.VISIBLE);
        }*/
        Utility.printLog(TAG + " onResume...");

    }

    /* *********************************************************************************************/

    /**
     * custom method to check and request for run time permissions
     * if not granted already
     */
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Already have permission
                if (!Utility.isMyServiceRunning(LocationUpdateService.class, MainActivity.this)) {
                    Intent startIntent = new Intent(MainActivity.this, LocationUpdateService.class);
                    startIntent.setAction(AppConstants.ACTION.STARTFOREGROUND_ACTION);
                    startService(startIntent);
                }
                sessionManager.setDeviceId(Utility.getDeviceId(MainActivity.this));
//                onPermissionGranted();
            } else {
                // Do not have permissions, requesting permission
                EasyPermissions.requestPermissions(this, getString(R.string.location_permission_message),
                        VariableConstant.RC_LOCATION_STATE, perms);
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
        if (!Utility.isMyServiceRunning(LocationUpdateService.class, MainActivity.this)) {
            Intent startIntent = new Intent(MainActivity.this, LocationUpdateService.class);
            startIntent.setAction(AppConstants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
        }
        if (homeOpen) {
            ((HomeFrag2) fragment).setGoogleMap();
        }
    }

    /* *********************************************************************************************/

    /**
     * predefined method to check run time permissions list call back
     *
     * @param requestCode   : to handle the corresponding request
     * @param permissions:  contains the list of requested permissions
     * @param grantResults: contains granted and un granted permissions result list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
       /* if (requestCode == PERMISSION_MULTIPLE_CODE)
        {
            if(grantResults.length > 0)
            {
                for (int i =0; i < permissions.length; i++)
                {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        isDeninedRTPs = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            showRationaleRTPs = shouldShowRequestPermissionRationale(permission);
                        }
                    }
                    break;
                }
                onPermissionDenied();
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }*/
    }


    /********************************************************************************
     *
     * Initiaze the views,home fragment and SharedPrefernces
     * If sharedPrefernce contain values and automatically fill the text
     *
     *********************************************************************************/
    private void initVar() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);


        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        final Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        final Typeface ClanaproNarrBook = Typeface.createFromAsset(getAssets(), "fonts/CLANPRO-NARRBOOK.OTF");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        abarMain = (AppBarLayout) findViewById(R.id.abarMain);

        button_menu = (ImageView) findViewById(R.id.button_menu);
        button_menu.setOnClickListener(this);

        toolbarMenu = (ImageView) findViewById(R.id.toolbarMenu);
        toolbarMenu.setOnClickListener(this);

        iv_history = (ImageView) findViewById(R.id.iv_history);
        iv_history.setOnClickListener(this);

        menu_layout = (RelativeLayout) findViewById(R.id.menu_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setTypeface(ClanaproNarrNews);
        tvVersion.setText(getString(R.string.version) + ": " + currentVersion);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {

                rl_profile_view = (RelativeLayout) findViewById(R.id.rl_profile_view);
                rl_profile_view.setOnClickListener(MainActivity.this);

                tv_viewpof = (TextView) findViewById(R.id.tv_viewpof);
                tv_viewpof.setTypeface(ClanaproNarrBook);


                tv_prof_name = (TextView) findViewById(R.id.tv_prof_name);
                tv_prof_name.setTypeface(ClanaproNarrNews);
                tv_prof_name.setText(sessionManager.getMyName());

                Utility.printLog(TAG + " name " + sessionManager.getMyName());
                iv_profile = (CircleImageView) findViewById(R.id.iv_profile);
                Picasso.with(MainActivity.this)
                        .load(sessionManager.getProfilePic())
                        .resize(200, 200)
//                        .transform(new CircleTransform())
                        .into(iv_profile, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("success", "onDrawerOpened: ");
                            }

                            @Override
                            public void onError() {
                                Log.d("error", "onDrawerOpened: ");
                            }
                        });
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };

        drawer.addDrawerListener(toggle);

        fragment = new HomeFrag2();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
        tv_on_off_statas = (TextView) findViewById(R.id.tv_on_off_statas);
        tv_on_off_statas.setTypeface(ClanaproNarrMedium);

        tv_prof_edit = (TextView) findViewById(R.id.tv_prof_edit);
        tv_prof_edit.setTypeface(ClanaproNarrMedium);
        tv_prof_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VariableConstant.IS_STRIPE_ADDED) {
                    Intent intent = new Intent(MainActivity.this, BankNewAccountActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottom_to_top, R.anim.stay);
                } else {
                    Utility.BlueToast(MainActivity.this, getString(R.string.plsAddStripeFirst));
                }
            }
        });

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTypeface(ClanaproNarrMedium);

        tvTitle2 = (TextView) findViewById(R.id.tvTitle2);
        tvTitle2.setTypeface(ClanaproNarrMedium);

        menu_layout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));
        abarMain.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(portal){
                ((PortalFrag)fragment).onBackPress();
            }else {
                super.onBackPressed();
            }

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {

            case R.id.nav_home:
                fragment = new HomeFrag2();

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                homeOpen = true;
                portal = false;
                tv_on_off_statas.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tv_prof_edit.setVisibility(View.GONE);
//               tv_prof_edit.setVisibility(View.VISIBLE);
                iv_history.setVisibility(View.VISIBLE);
                button_menu.setImageResource(R.drawable.selector_hamburger);
                menu_layout.setVisibility(View.VISIBLE);
                abarMain.setVisibility(View.GONE);
                menu_layout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));
                break;

            case R.id.nav_history:
                homeOpen = false;
                portal = false;
                fragment = new HistoryFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();

                tv_on_off_statas.setVisibility(View.GONE);
                tv_prof_edit.setVisibility(View.GONE);
                tvTitle.setText((getResources().getString(R.string.history)));
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle2.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                menu_layout.setVisibility(View.GONE);
                abarMain.setVisibility(View.VISIBLE);
                button_menu.setImageResource(R.drawable.selector_hamburger_white);
                break;

            case R.id.nav_portal:
                homeOpen = false;
                portal = true;
                fragment = new PortalFrag();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                tv_on_off_statas.setVisibility(View.GONE);
                tvTitle.setText("Portal");
                tv_prof_edit.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                tv_prof_edit.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                menu_layout.setVisibility(View.GONE);
                abarMain.setVisibility(View.VISIBLE);
                button_menu.setImageResource(R.drawable.selector_hamburger_white);
                break;

            case R.id.nav_support:
                homeOpen = false;
                portal = false;
                fragment = new SupportFrag();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();

                tv_on_off_statas.setVisibility(View.GONE);
                tv_prof_edit.setVisibility(View.GONE);
                tvTitle.setText((getResources().getString(R.string.support)));
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle2.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                menu_layout.setVisibility(View.GONE);
                abarMain.setVisibility(View.VISIBLE);

                button_menu.setImageResource(R.drawable.selector_hamburger_white);
                break;

            case R.id.nav_invite:
                homeOpen = false;
                portal = false;
                fragment = new InviteFrag();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();

                tv_on_off_statas.setVisibility(View.GONE);
                tvTitle.setText((getResources().getString(R.string.invite)));
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle2.setVisibility(View.GONE);
                tv_prof_edit.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                menu_layout.setVisibility(View.GONE);
                abarMain.setVisibility(View.VISIBLE);
                button_menu.setImageResource(R.drawable.selector_hamburger_white);
                break;

            case R.id.nav_bank_details:
                homeOpen = false;
                portal = false;
                fragment = new BankListFrag();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                tv_on_off_statas.setVisibility(View.GONE);
                tvTitle.setText((getResources().getString(R.string.bank_details)));
                tv_prof_edit.setText(getResources().getString(R.string.add));
                tvTitle2.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                tv_prof_edit.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                menu_layout.setVisibility(View.GONE);
                abarMain.setVisibility(View.VISIBLE);
                button_menu.setImageResource(R.drawable.selector_hamburger_white);
    /*           if(sessionManager.getBANKD()) {
                   Intent intent1 = new Intent(MainActivity.this, AddBankAccountActivity.class);
                   startActivity(intent1);
               }
               else if (!sessionManager.getBANKD()){
                   Intent intent1 = new Intent(MainActivity.this, BankNewAccountActivity.class);
                   startActivity(intent1);
               }

*/
                break;

            case R.id.nav_bank_details_1:
                homeOpen = false;
                portal = false;
                Intent intent1=new Intent(MainActivity.this,AddBankAccountActivity.class);
                startActivity(intent1);

                tv_on_off_statas.setVisibility(View.GONE);
                tvTitle.setText((getResources().getString(R.string.bank_details)));
                tv_prof_edit.setText(getResources().getString(R.string.add));
                tvTitle2.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                tv_prof_edit.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                menu_layout.setVisibility(View.GONE);
                abarMain.setVisibility(View.GONE);
                button_menu.setImageResource(R.drawable.selector_hamburger_white);

                break;

            case R.id.nav_need_help:
                if (homeOpen) {
                    abarMain.setVisibility(View.GONE);
                }
                menu_layout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));

                try {

                    Intent intent = new Intent(MainActivity.this, com.livechatinc.inappchat.ChatWindowActivity.class);
                    intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, "your_group_id");
                    intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, "4711811");
                    startActivity(intent);

                } catch (Exception e) {
                    Utility.printLog(TAG + " caught : " + e.getMessage());
                }


                break;
            case R.id.nav_payment:
                homeOpen = false;
                boolean walletOpen = true;
                fragment=new WalletFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                tv_on_off_statas.setVisibility(View.GONE);
                tv_prof_edit.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
//                tvTitle.setText(getString(R.string.payments));
                tvTitle2.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                menu_layout.setVisibility(View.GONE);
                abarMain.setVisibility(View.GONE);
                break;

          /*  case R.id.nav_logout:
                if(homeOpen){
                    abarMain.setVisibility(View.GONE);
                }
                menu_layout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));

                LogoutPopup logoutPopup = new LogoutPopup(MainActivity.this);
                logoutPopup.setCancelable(false);
                logoutPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                logoutPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logoutPopup.show();
                break;*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**********************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_menu:
            case R.id.toolbarMenu: {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
               else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;
            }

            case R.id.iv_history:
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_to_top, R.anim.stay);
                break;

            case R.id.rl_profile_view:
                homeOpen = false;
                fragment = new MyProfileFrag();

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                tv_on_off_statas.setVisibility(View.GONE);
                iv_history.setVisibility(View.GONE);
                tv_prof_edit.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.VISIBLE);
                tvTitle2.setText(getString(R.string.profile));
                abarMain.setVisibility(View.GONE);
                menu_layout.setVisibility(View.VISIBLE);
                button_menu.setImageResource(R.drawable.selector_hamburger_white);
                break;

        }
    }


    /**********************************************************************************************/
                                                                     /*onActivityResult*/

    /**********************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fragment.onActivityResult(requestCode, resultCode, data);


    }

    /* *********************************************************************************************/
                                                                       /*startCropImage*/


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        onPermissionGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else if (requestCode == VariableConstant.RC_LOCATION_STATE) {
            EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.location_permission_message)
                    , VariableConstant.RC_LOCATION_STATE, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private void GetAppConfig(final boolean walletChanged) {

        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.CONFIG + "/1",
                OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        pDialog.dismiss();
                        Log.d(TAG, " CONFIG result " + result);
                        if (result != null && !result.contains("statusCode")) {
                            GetConfig getConfig = new Gson().fromJson(result, GetConfig.class);
                            switch (getConfig.getErrFlag()) {
                                case "0":
                                    sessionManager.setMinDistForRouteArray(Long.parseLong(getConfig.getData().getDistanceForLogingLatLongs()));
                                    sessionManager.setcurrencySymbol(getConfig.getData().getCurrencySymbol());
                                    sessionManager.setmileage_metric(getConfig.getData().getMileage_metric());
                                    sessionManager.setStripeKey(getConfig.getData().getStripeKey());
                                    sessionManager.setpresence_interval(Integer.parseInt(getConfig.getData().getPresenceTime()));
                                    Utility.printLog("the presence interval is: " + sessionManager.getpresence_interval());

                                    if (getConfig.getData().getMileage_metric().equals("Km")) {
                                        sessionManager.setDISTANCE_CONVERSION_UNIT("0.001");
                                    } else {
                                        //if it is in miles
                                        sessionManager.setDISTANCE_CONVERSION_UNIT("0.00062137");
                                    }




                                    if (getConfig.getData().isStripeEnabled()){
                                        Menu menu=navigationView.getMenu();
                                        MenuItem stripe= menu.getItem(6);
                                        MenuItem bank=menu.getItem(7);
                                        stripe.setVisible(true);
                                        bank.setVisible(false);
                                        navigationView.invalidate();
                                    }else if (!getConfig.getData().isStripeEnabled()){
                                        Menu menu=navigationView.getMenu();
                                        MenuItem stripe= menu.getItem(6);
                                        MenuItem bank=menu.getItem(7);
                                        stripe.setVisible(false);
                                        bank.setVisible(true);
                                        navigationView.invalidate();
                                    }


                                    if(getConfig.getData().isEnableWallet()){
                                        sessionManager.setWalletAmount(getConfig.getData().getWalletAmount());
                                        sessionManager.setSoftLimit(getConfig.getData().getSoftLimit());
                                        sessionManager.setHardLimit(getConfig.getData().getHardLimit());
                                        if(homeOpen){
                                            ((HomeFrag2)fragment).setLimits();
                                        }

                                        setWallet(getConfig.getData().getWalletAmount().toString());
                                    }else {
                                        hideWallet();
                                    }
                                    if(walletChanged){

                                        if(getConfig.getData().isEnableWallet()){
                                            Utility.mShowMessage(getResources().getString(R.string.message),getResources().getString(R.string.wallet_enabled),MainActivity.this);
                                        }else {
                                            Utility.mShowMessage(getResources().getString(R.string.message),getResources().getString(R.string.wallet_disabled),MainActivity.this);
                                        }
                                    }

                                    if (getConfig.getData().getAppVersion() != null && !getConfig.getData().getAppVersion().isEmpty()) {
                                        checkVersion(getConfig.getData().getAppVersion(), getConfig.getData().isMandatory());
                                    }
                                    if (getConfig.getData().getPushTopics() != null && getConfig.getData().getPushTopics().size() > 0) {
                                        mSubscribeToTopics(getConfig.getData().getPushTopics());
                                    }
                                   /* Utility.printLog("getconfig .... currencySymbol : "+sessionManager.getcurrencySymbol()+"distance in : "+sessionManager.getmileage_metric());
                                    sessionManager.setIsLogin(true);
                                    startActivity(new Intent(MainActivity.this,VehicleList.class));
                                    finish();*/
                                    break;

                                case "1":
                                    break;

                            }

                        } else {
                            pDialog.dismiss();

                        }
                    }

                    @Override
                    public void onError(String error) {
                        pDialog.dismiss();
                        Utility.toastMessage(MainActivity.this, getString(R.string.network_problem));
                    }
                }, sessionManager.getSessionToken());
    }

    public void checkVersion(String onlineVersion, boolean manadatory) {

        Utility.printLog("Pkkkkk onlineVersion version " + onlineVersion + " Current version " + currentVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {
                /*if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
					//show dialog
					UpdateAlert("");
				}*/
            if (currentVersion.compareTo(onlineVersion) < 0) {
                UpdateAlert(manadatory);
            }
        }
    }

    public void UpdateAlert(boolean mandatory) {
        String msg = getResources().getString(R.string.update_non_mandatory);

        try {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // set title
            alertDialogBuilder.setTitle(getResources().getString(R.string.update_available));

            // set dialog message
            alertDialogBuilder
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.Update), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            if (Utility.isNetworkAvailable(MainActivity.this)) {
                                Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
                                }
                            }

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.not_now), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //closing the application
                            dialog.dismiss();
                        }
                    });

            if (mandatory) {
                alertDialogBuilder.setNegativeButton("", null);
                alertDialogBuilder.setMessage(getResources().getString(R.string.update_msg));
            }

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        } catch (Exception e) {

        }

    }

    public void mSubscribeToTopics(ArrayList<String> topics) {
        for (int index = 0; index < topics.size(); index++) {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topics.get(index));
        }
        System.out.println();
    }


    private void initChatVariables()
    {
        SelectUserItem item = new SelectUserItem();
//        item.setUserId(sessionController.getSessionInstance().getProviderId());
        item.setUserId(sessionManager.getMid());
//        item.setUserName(sessionController.getSessionInstance().getName());
        item.setUserName(sessionManager.getMyName());
//        item.setEmail(sessionController.getSessionInstance().getName());
        item.setEmail(sessionManager.getMyName());
        item.setPushToken(sessionManager.getPushToken());

        writeNewUser(item);

        AppController.getInstance().setupPresenceSystem(item.getUserId());



        //   * Will update the push token for the user

        if (AppController.getInstance().getPushToken() != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(item.getUserId()).child("pushToken").setValue(AppController.getInstance().getPushToken());
        }


        AppController.getInstance().setupPresenceSystem(item.getUserId());

    }
    private void writeNewUser(SelectUserItem user) {

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserId()).setValue(user);
    }

    public void hideWallet(){
        Menu menu=navigationView.getMenu();
        MenuItem menuItem= menu.getItem(5);
        menuItem.setVisible(false);
        navigationView.invalidate();

       /* if(walletOpen){
            homeOpen=true;
            walletOpen=false;
            menuItem=menu.getItem(0);
            onNavigationItemSelected(menuItem);
        }*/
    }
    public void setWallet(String s){
        Menu menu=navigationView.getMenu();
        MenuItem menuItem= menu.getItem(5);
        menuItem.setTitle(getResources().getString(R.string.payments)+" ("+sessionManager.getCurrencySymbol()+s+")");
        menuItem.setVisible(true);
        navigationView.invalidate();

        /*if(walletOpen){
            ((WalletFragment)fragment).setValues();
        }*/
    }

    public void moveDrawer(DrawerLayout mDrawerLayout) {
        // Drawer State checking
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            Menu menu=navigationView.getMenu();
            MenuItem menuItem= menu.getItem(5);
            menuItem.setTitle(getResources().getString(R.string.payments)+" ("+sessionManager.getCurrencySymbol()+sessionManager.getWalletAmount()+")");
            //mDrawerLayout.
        }
    }
}
