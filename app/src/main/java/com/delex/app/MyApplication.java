package com.delex.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.delex.service.CouchDbHandler;
import com.facebook.FacebookSdk;

/**
 * Created by Admin on 7/5/2017.
 */

public class MyApplication extends Application {

    CouchDbHandler couchDBHandler;
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
        FacebookSdk.sdkInitialize(this);

//        couchDBHandler = new CouchDbHandler(MyApplication.this);

    }

    public CouchDbHandler getDBHandler() {
        return couchDBHandler;
    }


}

