package com.example.moda.firebasebasedchat.Callbacks;
/*
 * Created by moda on 02/04/16.
 */

import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;


public abstract class DatabaseCallbackHandler {
    private Context context;

    public abstract void execute(DataSnapshot jsonObject) throws JSONException;

    public DatabaseCallbackHandler(Context ctx) {
        this.context = ctx;
    }

    public void handleResponse(DataSnapshot jsonObject) throws Exception {
        execute(jsonObject);

    }
}