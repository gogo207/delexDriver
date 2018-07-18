package com.delex.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delex.forgotPassword.ForgotPasswordVerify;
import com.delex.country_picker.CountryPicker;
import com.delex.country_picker.CountryPickerListener;
import com.delex.driver.R;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Locale;

public class EditPhoneNumber extends AppCompatActivity implements View.OnClickListener {

    private EditText et_phone_num;
    private TextView tv_save;
    private TextView countryCode, tv_operator, tv_signup_zones;
    private ImageView flag;
    private String mobileNumberWithoutZero, fullMobileNumber, phone_number;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private int maxPhoneLength, minPhoneLength = 0;

    /**********************************************************************************************/
    public static int getResId(String drawableName) {
        try {
            Class<R.drawable> res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("CountryCodePicker", "Failure to get drawable id.", e);
        }
        return -1;
    }
//ada
    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone_number);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        sessionManager = new SessionManager(getApplicationContext());

        initActionBar();
        initializeViews();
    }
    /**********************************************************************************************/

    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        if (VariableConstant.EDIT_PHONE_NUMBER) {
            finish();
            VariableConstant.EDIT_PHONE_NUMBER = false;
        }
    }

    /**********************************************************************************************/
                                                                      /*initializeViews*/
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
        tv_title.setText(getResources().getString(R.string.edit_phone));
        tv_title.setTypeface(ClanaproNarrMedium);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);
    }

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

        TextView tv_editphone_msg, tv_phone_num;

        tv_editphone_msg = (TextView) findViewById(R.id.tv_editphone_msg);
        tv_editphone_msg.setTypeface(ClanaproNarrNews);

        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        tv_phone_num.setTypeface(ClanaproNarrNews);

        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_save.setTypeface(ClanaproNarrMedium);
        tv_save.setOnClickListener(this);

        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        et_phone_num.setTypeface(ClanaproNarrNews);

        RelativeLayout countryPicker = (RelativeLayout) findViewById(R.id.countryPicker);
        countryCode = (TextView) findViewById(R.id.code);
        countryCode.setTypeface(ClanaproNarrNews);
        flag = (ImageView) findViewById(R.id.flag);


        InputFilter[] fArray = new InputFilter[1];
      /*  if (maxPhoneLength == 0) {
            et_phone_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        } else {*/
            fArray[0] = new InputFilter.LengthFilter(maxPhoneLength);
            et_phone_num.setFilters(fArray);
//        }
        if (et_phone_num.getText().toString().length() > 0) {
            if (et_phone_num.getText().toString().charAt(0) == '0') {
                mobileNumberWithoutZero = et_phone_num.getText().toString().substring(1);
            } else {
                mobileNumberWithoutZero = et_phone_num.getText().toString();
            }
        }

        fullMobileNumber = countryCode.getText().toString() + mobileNumberWithoutZero;

        countryPicker.setOnClickListener(this);
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
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_save:
                if (!et_phone_num.getText().toString().matches("")) {
                    validatePhone();
                } else {
                    Utility.toastMessage(EditPhoneNumber.this, getResources().getString(R.string.phone_mis));
                }
                break;

            case R.id.countryPicker:
                showDialoagforcountrypicker();
                break;
        }

    }

    /******************************************************for country picker**********************/
    private void showDialoagforcountrypicker() {
        final CountryPicker picker = CountryPicker.newInstance(getResources().getString(R.string.select_country));
        picker.show(getSupportFragmentManager(), "COUNTRY_CODE_PICKER");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,int min,int max) {
                countryCode.setText(dialCode);
                String drawableName = "flag_"
                        + code.toLowerCase(Locale.ENGLISH);
                flag.setBackgroundResource(getResId(drawableName));
                if (et_phone_num.getText().toString().length() > 0) {

                    if (et_phone_num.getText().toString().charAt(0) == '0') {
                        mobileNumberWithoutZero = et_phone_num.getText().toString().substring(1);
                    } else {
                        mobileNumberWithoutZero = et_phone_num.getText().toString();

                    }
                }
                fullMobileNumber = countryCode.getText().toString() + mobileNumberWithoutZero;

                picker.dismiss();
            }
        });
    }

    /**********************************************************************************************/
    void MasterProfile() {
        pDialog.show();
        phone_number =/*countryCode.getText().toString()+*/et_phone_num.getText().toString();
        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("token", sessionManager.getSessionToken());
            reqOtpObject.put("ent_mobile", phone_number);

            Utility.printLog("siginup otp request: " + reqOtpObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.UPDATE_PROFILE, OkHttp3Connection.Request_type.PUT, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                System.out.println("Signup result " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            Utility.toastMessage(getApplicationContext(), validatorPojo.getErrMsg());
                            Intent mIntent = new Intent(getApplicationContext(), ForgotPasswordVerify.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("from", "EditPhone");
                            mBundle.putString("mobile", phone_number);
                            mBundle.putString("countryCode",countryCode.getText().toString());
                            mIntent.putExtras(mBundle);
                            startActivity(mIntent);
                            break;
                        case 1:
                            Utility.toastMessage(getApplicationContext(), validatorPojo.getErrMsg());
                            break;
                    }

                } else {

                }

            }

            @Override
            public void onError(String error) {
                System.out.println("signin error " + error);

            }
        }, sessionManager.getSessionToken());
    }

    /**********************************************************************************************/
    /**
     * <h1>validatePhone</h1>
     * <p1>the method is for validate the phonw number which entered.
     * if the result is success then the masterprofile is call, which is for OTP.
     * </p1>
     */
    private void validatePhone() {
        pDialog.show();
        JSONObject reqObject = new JSONObject();
        try {
            reqObject.put("mobile", et_phone_num.getText().toString());
            reqObject.put("countryCode",countryCode.getText().toString());
            reqObject.put("validationType", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(),ServiceUrl.VERIFY_EMAIL_PHONE, OkHttp3Connection.Request_type.POST, reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Utility.printLog("phone validation : " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            MasterProfile();
                            break;
                        case 1:
                            Utility.BlueToast(EditPhoneNumber.this, validatorPojo.getErrMsg());
                            break;
                    }
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                pDialog.dismiss();
            }
        }, sessionManager.getSessionToken());
    }
}
