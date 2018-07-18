package com.delex.app;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.delex.login.Login;
import com.delex.driver.R;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**************************************************************************************************/
public class  SplashScreen extends AppCompatActivity {

    private SessionManager sessionManager;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        sessionManager = SessionManager.getSessionManager(SplashScreen.this);
        sessionManager.setPushToken(FirebaseInstanceId.getInstance().getToken());
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. For showing app logo
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start app
                checkConfiguration();

            }
        }, 3000);

//        printHashKey();


        //initializeViews();



    }
    /**
     * <h2>checkConfiguration</h2>
     * <p>check user is logged in or not and
     * based on status it corresponding activity will open</p>
     */
    private void checkConfiguration() {
        if (sessionManager.isLogin()&&!sessionManager.getvehid().isEmpty()) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashScreen.this, Login.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utility.printLog("LANGUAGE",sessionManager.getLang());
        if(!sessionManager.getLang().isEmpty()){

            VariableConstant.LANGUAGE=sessionManager.getLang();
            if(sessionManager.getLang().equalsIgnoreCase("en")){
                setLocale("en");


            }
            else if(sessionManager.getLang().equalsIgnoreCase("ar")){
                setLocale("ar");

            }
        }

    }

    public void setLocale(String lang) {

        try {
            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);

        } catch (Exception e) {
            Utility.printLog("select_language inside Exception" + e.toString());
        }
    }


    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("Facebook", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Facebook", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("Facebook", "printHashKey()", e);
        }
    }
}
