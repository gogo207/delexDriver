package com.delex.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.adapter.OperatorsAdapter;
import com.delex.driver.R;
import com.delex.pojo.OperatorsData;
import com.delex.pojo.OperatorsPojo;
import com.delex.pojo.SignupZonedata;
import com.delex.pojo.SignupZonesPojo;
import com.delex.pojo.VehMakeData;
import com.delex.pojo.VehTypeData;
import com.delex.pojo.VehTypeSepecialities;
import com.delex.pojo.VehicleMakeModel;
import com.delex.pojo.VehicleMakePojo;
import com.delex.pojo.VehicleTypePojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SimpleItemDecorative;
import com.delex.utility.Utility;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class SelectAnOperator extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = SelectAnOperator.class.getSimpleName();
    public ArrayList<OperatorsData> operatorData = new ArrayList<>();
    public ArrayList<SignupZonedata> signupZonedata = new ArrayList<>();
    public ArrayList<VehTypeData> vehTypeData = new ArrayList<>();
    public String selectedOperatorName = "";
    public String selectedOperatorId;
    public ArrayList<VehTypeSepecialities> vehTypeSepecialities = new ArrayList<>();
    public ArrayList<VehMakeData> vehMakeDatas = new ArrayList<>();
    public ArrayList<VehicleMakeModel> vehicleMakeModels = new ArrayList<>();
    RecyclerView operatorsList;
    private ProgressDialog pDialog;
    private OperatorsAdapter oAdapter;
    private boolean SPECIALITY = false;
    private boolean MODELS = false;
//    private ArrayList<VehTypeData> vehTypeDatas=new ArrayList<>();

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_an_operator);
        overridePendingTransition(R.anim.bottem_slide_down, R.anim.stay_activity);

        Bundle mBundle = getIntent().getExtras();
        String list = mBundle.getString("OPERATION");
        String title = mBundle.getString("TITLE");
        ArrayList<SignupZonedata> zonedata = (ArrayList<SignupZonedata>) mBundle.getSerializable("ZONES");
        vehTypeData = (ArrayList<VehTypeData>) mBundle.getSerializable("VEHTYPEDATA");
        vehMakeDatas = (ArrayList<VehMakeData>) mBundle.getSerializable("VehMakeData");
        if (vehTypeData == null) {
            vehTypeData = new ArrayList<>();
        }

        initActionBar(title);
        initializeViews();

        if (list.matches("signup_operator")) {
            getOperatorsList();
        }
        if (list.matches("signup_zones")) {
            if (zonedata != null && zonedata.size() > 0) {
                oAdapter = new OperatorsAdapter(SelectAnOperator.this, 6);
                operatorsList.setAdapter(oAdapter);
                signupZonedata.addAll(zonedata);
                oAdapter.notifyDataSetChanged();

            } else {
                getZonesList();
            }

        }

        if (list.matches("signup_veh_type")) {
            if (vehTypeData != null && vehTypeData.size() > 0) {
                oAdapter = new OperatorsAdapter(SelectAnOperator.this, 2);
                operatorsList.setAdapter(oAdapter);
                oAdapter.notifyDataSetChanged();
            } else {
                getTypeList();
            }

        }
        if (list.matches("signup_veh_speciality")) {
            vehTypeSepecialities = (ArrayList<VehTypeSepecialities>) mBundle.getSerializable("sepecialities");
            SPECIALITY = true;
            getSpecialityList();
        }
        if (list.matches("signup_veh_make")) {
            getMakeList();
        }
        if (list.matches("signup_veh_model")) {
            vehicleMakeModels = (ArrayList<VehicleMakeModel>) mBundle.getSerializable("models");
            MODELS = true;
            getModelList();
        }
    }

    /**********************************************************************************************/
    /**
     * <h1>initActionBar</h1>
     * initialize the action bar
     */
    private void initActionBar(String title) {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_signup_close);
        }
        ImageView iv_search;
        TextView tv_title;

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        tv_title.setTypeface(ClanaproNarrNews);

        TextView tv_logout = (TextView) findViewById(R.id.tv_done);
        tv_logout.setVisibility(View.VISIBLE);
        tv_logout.setOnClickListener(this);
        tv_logout.setTypeface(ClanaproNarrNews);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

    }
    /**********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * initialize the views inside activity
     */
    private void initializeViews() {
        pDialog = new ProgressDialog(SelectAnOperator.this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);
        operatorsList = (RecyclerView) findViewById(R.id.rv_operators_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SelectAnOperator.this);
        operatorsList.setLayoutManager(layoutManager);
        operatorsList.addItemDecoration(new SimpleItemDecorative(getApplicationContext()));
    }

    /**********************************************************************************************/
    /**
     * <h1>getOperatorsList</h1>
     * <p>this is the service call for the result of operator provided</p>
     */
    private void getOperatorsList() {
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.OPERATORS, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Operators Result" + result);
                if (result != null) {
                    OperatorsPojo operatorsPojo = new Gson().fromJson(result, OperatorsPojo.class);
                    manageOperatorResponse(operatorsPojo);
                } else {
                    Toast.makeText(SelectAnOperator.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Operators Error" + error);
                Toast.makeText(SelectAnOperator.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }, "ordinory");
    }

    /**********************************************************************************************/
    /**
     * <h1>getZonesList</h1>
     * <p>this is the service call for the result of zones provided</p>
     */
    private void getZonesList() {
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.ZONE, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Operators Result" + result);
                if (result != null) {
                    SignupZonesPojo signupZonesPojo = new Gson().fromJson(result, SignupZonesPojo.class);
                    manageZoneResponse(signupZonesPojo);
                } else {
                    Toast.makeText(SelectAnOperator.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Operators Error" + error);
                Toast.makeText(SelectAnOperator.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }, "ordinory");
    }

    /**********************************************************************************************/
    /**
     * <h1>getTypeList</h1>
     * <p>this is the service call for the result of Vehicle Type list provided</p>
     */
    private void getTypeList() {
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.VEHICLETYPE, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {

                    Utility.printLog("SelectAnOperator VehicleType response " + result);
                    VehicleTypePojo vehicleTypePojo = new Gson().fromJson(result, VehicleTypePojo.class);
                    manageVehTypeResponse(vehicleTypePojo);

                } else {
                    Toast.makeText(SelectAnOperator.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Operators Error" + error);
                Toast.makeText(SelectAnOperator.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }, "ordinory");
    }

    /**********************************************************************************************/
    /**
     * <h1>getSpecialityList</h1>
     * <p>this is the method for set the adapter for Speciality if vehicle ,
     * if the vehicle type select then only the Speciality vehicle can select.</p>
     */
    private void getSpecialityList() {
        oAdapter = new OperatorsAdapter(SelectAnOperator.this, 3);
        operatorsList.setAdapter(oAdapter);
        oAdapter.notifyDataSetChanged();
    }
    /**********************************************************************************************/
    /**
     * <h1>getModelList</h1>
     * <p>this is the method for set the adapter for Models if vehicle ,
     * if the vehicle Make select then only the models vehicle can select.</p>
     */
    private void getModelList() {
        oAdapter = new OperatorsAdapter(SelectAnOperator.this, 5);
        operatorsList.setAdapter(oAdapter);
        oAdapter.notifyDataSetChanged();
    }
    /**********************************************************************************************/
    /**
     * <h1>getMakeList</h1>
     * <p>this is the service call for the result of Vehicle Make list provided</p>
     */
    private void getMakeList() {
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.MAKEMODEL, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Makemodel Result" + result);
                if (result != null) {
                    VehicleMakePojo vehicleMakePojo = new Gson().fromJson(result, VehicleMakePojo.class);
                    manageVehMakeModelResponse(vehicleMakePojo);

                } else {
                    Toast.makeText(SelectAnOperator.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Operators Error" + error);
                Toast.makeText(SelectAnOperator.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }, "ordinory");
    }


    /**********************************************************************************************/
    /**
     * <p>ResultHandler for Operator </p>
     *
     * @param operatorsPojo
     */
    private void manageOperatorResponse(OperatorsPojo operatorsPojo) {
        switch (operatorsPojo.getErrFlag()) {
            case 0:
                oAdapter = new OperatorsAdapter(SelectAnOperator.this, 1);
                operatorsList.setAdapter(oAdapter);
                operatorData.addAll(operatorsPojo.getData());
                oAdapter.notifyDataSetChanged();
                break;
            case 1:
                Toast.makeText(SelectAnOperator.this, operatorsPojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**********************************************************************************************/
    /**
     * <p>ResultHandler for Zones </p>
     *
     * @param signupZonesPojo
     */
    private void manageZoneResponse(SignupZonesPojo signupZonesPojo) {
        switch (signupZonesPojo.getErrFlag()) {
            case 0:
                oAdapter = new OperatorsAdapter(SelectAnOperator.this, 6);
                operatorsList.setAdapter(oAdapter);
                signupZonedata.addAll(signupZonesPojo.getData());
                oAdapter.notifyDataSetChanged();
                break;
            case 1:
                Toast.makeText(SelectAnOperator.this, signupZonesPojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**********************************************************************************************/
    /**
     * <p>ResultHandler for Operator </p>
     *
     * @param
     */
    private void manageVehTypeResponse(VehicleTypePojo vehicleTypePojo) {
        switch (vehicleTypePojo.getErrFlag()) {
            case 0:
                oAdapter = new OperatorsAdapter(SelectAnOperator.this, 2);
                operatorsList.setAdapter(oAdapter);
                vehTypeData.addAll(vehicleTypePojo.getData());
                oAdapter.notifyDataSetChanged();
                break;
            case 1:
                Toast.makeText(SelectAnOperator.this, vehicleTypePojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**********************************************************************************************/
    /**
     * <p>ResultHandler for Operator </p>
     *
     * @param
     */
    private void manageVehMakeModelResponse(VehicleMakePojo vehicleMakePojo) {
        switch (vehicleMakePojo.getErrFlag()) {
            case 0:
                oAdapter = new OperatorsAdapter(SelectAnOperator.this, 4);
                operatorsList.setAdapter(oAdapter);
                vehMakeDatas.addAll(vehicleMakePojo.getData());
                oAdapter.notifyDataSetChanged();
                break;
            case 1:
                Toast.makeText(SelectAnOperator.this, vehicleMakePojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_activity, R.anim.bottem_slide_up);
    }

    /**********************************************************************************************/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_done:
                overridePendingTransition(R.anim.stay_activity, R.anim.bottem_slide_up);
                Intent intent = new Intent();
                intent.putExtra("operator", selectedOperatorName);
                intent.putExtra("operatorId", selectedOperatorId);
                if (vehTypeSepecialities != null) {
                    ArrayList<VehTypeSepecialities> localList = new ArrayList<>();
                    for (VehTypeSepecialities v : vehTypeSepecialities) {
                        if (v.isSelected()) {
                            localList.add(v);
                        }
                    }
                    intent.putExtra("vehTypeSepecialities", vehTypeSepecialities);
                    intent.putExtra("selectedVehTypeSpecialities", localList);
                }
                if (signupZonedata != null) {

                   /* ArrayList<SignupZonedata> localList = new ArrayList<>();
                    for (SignupZonedata data : signupZonedata){
                        if (data.isSelected()){
                            localList.add(data);
                        }
                    }*/
                    intent.putExtra("selectedZones", signupZonedata);
                }
                if (vehTypeData != null) {
                    intent.putExtra("vehicleTypeData", vehTypeData);
                }
                if (vehMakeDatas != null) {
                    intent.putExtra("VehMakeData", vehMakeDatas);
                }
                if (vehicleMakeModels != null) {
                    intent.putExtra("vehicleMakeModels", vehicleMakeModels);
                }
                Log.d(TAG, "operator " + selectedOperatorName + " " + selectedOperatorId);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
