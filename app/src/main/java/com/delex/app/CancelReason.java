package com.delex.app;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.adapter.BookingCancelRVA;
import com.delex.driver.R;
import com.delex.pojo.CancelPojo;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CancelReason extends AppCompatActivity implements View.OnClickListener {

    public ArrayList<String> tv_cancel_reasons = new ArrayList<>();
    RelativeLayout rl_comment;
    Typeface ClanaproNarrMedium;
    Typeface ClanaproNarrNews;
    Typeface ClanaproMedium;
    private String TAG = CancelReason.class.getSimpleName();
    private Toolbar toolbar;
    private ImageView iv_search, iv_jobdetails;
    private TextView tv_title;
    private TextView tv_cancel_confirm;
    private EditText et_comment;
    private RecyclerView rv_cancel_reason;
    private BookingCancelRVA bookingCancelRVA;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private String bid;
    private String reason = "";

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reason);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        sessionManager = new SessionManager(this);

        bid = getIntent().getStringExtra("bid");

        initializeViews();
        initActionBar();
        getCancelReasons();
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
        tv_title.setText(getResources().getString(R.string.cancelReason));
        tv_title.setTypeface(ClanaproNarrMedium);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

        iv_jobdetails = (ImageView) findViewById(R.id.iv_jobdetails);
        iv_jobdetails.setVisibility(View.GONE);

    }

    /**********************************************************************************************/
                                                                      /*initializeViews*/
    /**********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        ClanaproMedium = Typeface.createFromAsset(getAssets(), "fonts/CLANPRO-MEDIUM.OTF");

        TextView tv_commmts;
        tv_commmts = (TextView) findViewById(R.id.tv_commmts);
        tv_commmts.setTypeface(ClanaproNarrNews);

        tv_cancel_confirm = (TextView) findViewById(R.id.tv_cancel_confirm);
        tv_cancel_confirm.setTypeface(ClanaproNarrMedium);
        tv_cancel_confirm.setOnClickListener(this);

        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setTypeface(ClanaproNarrNews);

        rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);

        rv_cancel_reason = (RecyclerView) findViewById(R.id.rv_cancel_reason);


    }

    /**********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        }
        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
    void getCancelReasons() {
        pDialog.show();
        int lang = 0;

        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.CANCELREASONS + "/" + VariableConstant.USER_TYPE + "/" + lang,
                OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        pDialog.dismiss();
                        Log.d(TAG, " getCancelReasons result " + result);
                        if (result != null) {
                            CancelPojo cancelPojo = new Gson().fromJson(result, CancelPojo.class);
                            switch (cancelPojo.getErrFlag()) {
                                case 0:
                                    if (cancelPojo.getData().size() != 0) {
                                        tv_cancel_reasons.addAll(cancelPojo.getData());
                                        rv_cancel_reason.setLayoutManager(new LinearLayoutManager(CancelReason.this));
                                        rv_cancel_reason.setNestedScrollingEnabled(true);
                                        bookingCancelRVA = new BookingCancelRVA(CancelReason.this, new BookingCancelRVA.ComentsCancel() {

                                            @Override
                                            public void oncommment(String reason) {
//                                                et_comment.setText(reason);
                                                CancelReason.this.reason = reason;
                                            }
                                        });
                                        rv_cancel_reason.setAdapter(bookingCancelRVA);
                                    } else {
                                        rv_cancel_reason.setVisibility(View.GONE);
                                    }

                                    break;
                                case 1:
                                    Toast.makeText(CancelReason.this, cancelPojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(CancelReason.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        pDialog.dismiss();
                        Toast.makeText(CancelReason.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                    }
                }, sessionManager.getSessionToken());
    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_confirm:
//                 reason = et_comment.getText().toString();
                   if(reason.isEmpty()){
                       reason = et_comment.getText().toString();
                   }
                if (!reason.equals("")) {
                    MasterCancelReason(reason);
                } else {
                    Utility.BlueToast(this, getResources().getString(R.string.emptyReason));
                }
                break;
        }
    }

    /**********************************************************************************************/
    /**********************************************************************************************/
    void MasterCancelReason(String reason) {
        pDialog.show();
        JSONObject cancelJson = new JSONObject();
        try {

            cancelJson.put("ent_booking_id", bid);
            cancelJson.put("ent_status", 4);
            cancelJson.put("ent_reason", reason);

            Utility.printLog("Cancel result " + cancelJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.CANCELBOOKING, OkHttp3Connection.Request_type.PUT, cancelJson, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                Utility.printLog("Cancel result " + result);

                Log.i("cancelll2",result);
                if (result != null) {

                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            Utility.toastMessage(CancelReason.this, validatorPojo.getErrMsg());
                            Intent intent = new Intent(CancelReason.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        case 1:
                            Utility.toastMessage(CancelReason.this, validatorPojo.getErrMsg());
                            
                            break;
                    }
                } else {
                }

            }

            @Override
            public void onError(String error) {
                pDialog.dismiss();
                Log.i("cancelll0",error);
                Toast.makeText(CancelReason.this,error+"",Toast.LENGTH_SHORT).show();

            }
        }, sessionManager.getSessionToken());
    }
}
