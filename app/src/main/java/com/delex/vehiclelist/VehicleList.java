package com.delex.vehiclelist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.delex.app.MainActivity;
import com.delex.driver.R;
import com.delex.logout.LogoutPopup;
import com.delex.pojo.SigninDriverVehicle;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**************************************************************************************************/
public class VehicleList extends AppCompatActivity implements View.OnClickListener {

    public ArrayList<SigninDriverVehicle> vDataList;
    private SessionManager sessionManager;
    private VechicleListRVA currentJobRVA;
    private ProgressDialog pDialog;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);
        sessionManager = SessionManager.getSessionManager(VehicleList.this);

        VariableConstant.VECHICLESELECTED = false;

        initActionBar();
        initializeViews();


    }

    /* *********************************************************************************************/

    /**
     * <h1>initActionBar</h1>
     * initilize the action bar
     */
    private void initActionBar() {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_signup_close);
        }
        ImageView iv_search;
        TextView tv_title, tv_logout;

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);


        tv_title.setText(getResources().getString(R.string.selectVeh_vl));
        tv_title.setTypeface(ClanaproNarrMedium);

        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_logout.setVisibility(View.VISIBLE);
        tv_logout.setTypeface(ClanaproNarrMedium);
        tv_logout.setOnClickListener(this);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

        RecyclerView rv_vehiclelist = (RecyclerView) findViewById(R.id.rv_vehiclelist);


        rv_vehiclelist.setLayoutManager(new LinearLayoutManager(this));
        rv_vehiclelist.setNestedScrollingEnabled(true);
        JSONArray jsonArray;
        Gson gson = new Gson();
        vDataList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(sessionManager.getVehicle());

            for (int i = 0; i < jsonArray.length(); i++) {
                SigninDriverVehicle signinDriverVehicle = gson.fromJson(jsonArray.getString(i), SigninDriverVehicle.class);

                vDataList.add(signinDriverVehicle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentJobRVA = new VechicleListRVA(this);
        rv_vehiclelist.setAdapter(currentJobRVA);


    }

    /* *********************************************************************************************/
                                                                      /*initializeViews*/
    /*  *********************************************************************************************/

    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        pDialog = new ProgressDialog(VehicleList.this);
        pDialog.setMessage(getString(R.string.loading));

        TextView tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setTypeface(ClanaproNarrMedium);
        tv_confirm.setOnClickListener(this);
    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_logout:
                LogoutPopup logoutPopup = new LogoutPopup(VehicleList.this);
                logoutPopup.setCancelable(false);
                logoutPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                logoutPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logoutPopup.show();
                break;

            case R.id.tv_confirm:
                if (VariableConstant.VECHICLESELECTED) {
                    makeDefaultVehicle();
                } else {
                    Utility.BlueToast(this, getResources().getString(R.string.selectVechicle));
                }
                break;
        }

    }


    private void makeDefaultVehicle() {
        pDialog.show();

        JSONObject mastervehicleDefault = new JSONObject();

        try {
            mastervehicleDefault.put("workplace_id", VariableConstant.VECHICLEID);
            mastervehicleDefault.put("type_id", VariableConstant.VECHICLE_TYPE_ID);
            mastervehicleDefault.put("mid", sessionManager.getMid());

        } catch (JSONException e) {
            e.printStackTrace();
            pDialog.dismiss();
        }
        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.VEHICLE_DEFAULT, OkHttp3Connection.Request_type.POST, mastervehicleDefault, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    System.out.println("makeDefaultVehicle Result " + result);
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            sessionManager.setVehicleImage("https://s3-us-west-2.amazonaws.com/dayrunner/VehicleTypes/vehicleMapImages/file201782322254.png");

                            startActivity(new Intent(VehicleList.this, MainActivity.class));
                            sessionManager.setvehid(VariableConstant.VECHICLE_TYPE_ID);
                            finish();
                            break;
                        case 1:
                            Toast.makeText(VehicleList.this, validatorPojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                            break;
                    }

                } else {
                    Toast.makeText(VehicleList.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                System.out.println("makeDefaultVehicle Error " + error);
                Toast.makeText(VehicleList.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }, sessionManager.getSessionToken());
    }
}
