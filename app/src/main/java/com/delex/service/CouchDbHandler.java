package com.delex.service;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.delex.utility.SessionManager;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 31-08-2017.
 */

public class CouchDbHandler {

    private static final String TAG = "CouchDB ";
    private Manager manager;
    private AndroidContext androidContext;
    private Database database;
    private SessionManager sessionManager;
    private String indexAppDocID;
    private static CouchDbHandler couchDbHandler;

    private CouchDbHandler(Context context) {
        this.androidContext = new AndroidContext(context);

        sessionManager = new SessionManager(context);
        mCreateManager();
        getDatabase();
        mGetDocument();
    }

    public static CouchDbHandler getCouhDbHandler(Context context){
        if(couchDbHandler==null){
            couchDbHandler=new CouchDbHandler(context);
        }
        return couchDbHandler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mCreateManager() {
        if (manager == null) {
            try {
                manager = new Manager(androidContext, Manager.DEFAULT_OPTIONS);

            } catch (IOException e) {
                Log.e(TAG, "Cannot create manager object");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getDatabase() {
        try {

            if (manager != null) {
                database = manager.getDatabase("couchdbdayrunner");
            }
            Log.d(TAG, "Database created");

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get database");
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void mGetDocument() {
        indexAppDocID = sessionManager.getDocumentID();
        if (indexAppDocID.isEmpty()) {
            indexAppDocID = createAppDocumnent();
            sessionManager.setDocumentID(indexAppDocID);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String createAppDocumnent() {

        Document document = database.createDocument();

        Map<String, Object> map = document.getProperties();

        if (map != null) {
            map.clear();
        } else {
            map = new HashMap<String, Object>();
        }
        map.put("lattitude", new ArrayList<>());
        map.put("longitude", new ArrayList<>());
        try {
            document.putProperties(map);
            return document.getId();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public JSONArray retriveDocument() {

        Document retrievedDocument = database.getDocument(sessionManager.getDocumentID());
        Map map = new HashMap();
        if(retrievedDocument!=null)
             map = retrievedDocument.getProperties();
        ArrayList latList = (ArrayList) map.get("lattitude");
        ArrayList longList = (ArrayList) map.get("longitude");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < latList.size(); i++) {
            jsonArray.put(String.valueOf(latList.get(i)) + "," + String.valueOf(longList.get(i)));
        }
        return jsonArray;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void updateDocument(double lat, double lng) {

        Document retrievedDocument = database.getDocument(sessionManager.getDocumentID());
        Map map = retrievedDocument.getProperties();
        ArrayList latList = (ArrayList) map.get("lattitude");
        ArrayList longList = (ArrayList) map.get("longitude");

        latList.add(lat);
        longList.add(lng);

        Log.e(TAG, "UpdateDocument " + latList.toString());

        Map map1 = new HashMap(map);
        map1.put("lattitude", latList);
        map1.put("longitude", longList);

        try {
            retrievedDocument.putProperties(map1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void deleteDocument() {
        Document retrievedDocument = database.getDocument(sessionManager.getDocumentID());
        Map map = retrievedDocument.getProperties();
        ArrayList latList = (ArrayList) map.get("lattitude");
        ArrayList longList = (ArrayList) map.get("longitude");

        latList.clear();
        longList.clear();

        Map map1 = new HashMap(map);
        map1.put("lattitude", latList);
        map1.put("longitude", longList);

        try {
            retrievedDocument.putProperties(map1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
