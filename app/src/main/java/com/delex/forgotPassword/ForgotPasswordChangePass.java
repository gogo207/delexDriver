package com.delex.forgotPassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delex.login.Login;
import com.delex.driver.R;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**************************************************************************************************/
public class ForgotPasswordChangePass extends AppCompatActivity implements View.OnClickListener {

    private EditText et_new_pass, et_reenter_pass;
    private String OTP, MobNum;
    private ProgressDialog pDialog;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_change_pass);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        FetchValues();
        initActionBar();
        initializeViews();
    }

/**********************************************************************************************/
    /**
     * <h1>FetchValues</h1>
     * <p>The values fetch from the ForgotPasswordVerify Activity for changepassword.</p>
     */
    private void FetchValues() {
        Bundle mBundle = getIntent().getExtras();
        OTP = mBundle.getString("OTP");
        MobNum = mBundle.getString("phone");

    }
    /**********************************************************************************************/
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white_btn);
        }
        ImageView iv_search;
        TextView tv_title;

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.EntNewPass));
        tv_title.setTypeface(ClanaproNarrMedium);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

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
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
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

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        TextView tv_newpass_msg, tv_new_pass, tv_reenter_pass, tv_continue;

        tv_newpass_msg = (TextView) findViewById(R.id.tv_newpass_msg);
        tv_newpass_msg.setTypeface(ClanaproNarrNews);

        tv_new_pass = (TextView) findViewById(R.id.tv_new_pass);
        tv_new_pass.setTypeface(ClanaproNarrNews);

        tv_reenter_pass = (TextView) findViewById(R.id.tv_reenter_pass);
        tv_reenter_pass.setTypeface(ClanaproNarrNews);

        tv_continue = (TextView) findViewById(R.id.tv_continue);
        tv_continue.setTypeface(ClanaproNarrMedium);
        tv_continue.setOnClickListener(this);

        et_new_pass = (EditText) findViewById(R.id.et_new_pass);
        et_new_pass.setTypeface(ClanaproNarrNews);

        et_reenter_pass = (EditText) findViewById(R.id.et_reenter_pass);
        et_reenter_pass.setTypeface(ClanaproNarrNews);


        LinearLayout ll_first = (LinearLayout) findViewById(R.id.sv_signup);
        ll_first.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utility.hideSoftKeyboard(ForgotPasswordChangePass.this);
                return false;
            }
        });

    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_continue:
                PasswordValidation();
                break;

        }
    }

    /**********************************************************************************************/
    /**
     * <p>which is validate the new password,
     * checking wether the both password (new password and reentered password) is same or not,
     * if the password is same it will sucess</p>
     */
    void PasswordValidation() {
        pDialog.onStart();
        String new_pass = et_new_pass.getText().toString();
        String re_ent_pass = et_reenter_pass.getText().toString();

        Log.d("baby", "PasswordValidation: " + new_pass + "   " + re_ent_pass);

        if (new_pass.matches("") && re_ent_pass.matches("")) {
            Utility.BlueToast(this, getResources().getString(R.string.entNewPass));
        } else if (new_pass.matches("")) {
            Utility.BlueToast(this, getResources().getString(R.string.entNewPass));
        } else if (re_ent_pass.matches("")) {
            Utility.BlueToast(this, getResources().getString(R.string.reentrPass));
        } else {
            if (!new_pass.matches(re_ent_pass)) {
                Utility.BlueToast(this, getResources().getString(R.string.passNotMactch));
            } else {
                UpdatePasswordService(new_pass);
            }
        }

    }

    /**********************************************************************************************/
    /**
     * <H1>UpdatePasswordService</H1>
     */
    void UpdatePasswordService(String new_pass) {
        pDialog.show();
        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("password", new_pass);
            reqOtpObject.put("securenumber", OTP);
            reqOtpObject.put("userType", VariableConstant.USER_TYPE);
            reqOtpObject.put("ent_mobile", MobNum);
           // reqOtpObject.put("",)

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.UPDATEPASSWORD, OkHttp3Connection.Request_type.POST, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                System.out.println("Signup result " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            Utility.BlueToast(ForgotPasswordChangePass.this, validatorPojo.getErrMsg());
                            Intent intent = new Intent(ForgotPasswordChangePass.this, Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                            break;
                        case 1:
                            Utility.BlueToast(ForgotPasswordChangePass.this, validatorPojo.getErrMsg());
                            break;

                    }

                } else {

                }

            }

            @Override
            public void onError(String error) {
                System.out.println("signin error " + error);

            }
        }, "ordinory");
    }
}
