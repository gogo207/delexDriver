package com.delex.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delex.forgotPassword.ForgotPasswordVerify;
import com.delex.country_picker.CountryPicker;
import com.delex.country_picker.CountryPickerListener;
import com.delex.driver.R;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, EditProfilePresenter.EditProfileImple {

    private static final String TAG = "EditPhoneNumber";
    private final int PHONE_VALIDATION_REQUEST_CODE = 700;
    String data = "",email;
    private EditText et_phone_num, etName, et_newpass, et_re_newpass;
    private TextView countryCode;
    private ImageView flag;
    private String mobileNumberWithoutZero;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private int maxPhoneLength, minPhoneLength = 0;
    private EditProfilePresenter editProfilePresenter;

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
    }

    /**********************************************************************************************/
                                                                      /*initializeViews*/
    /**********************************************************************************************/

    /**
     * <h1>initActionBar</h1>
     * initilize the action bar
     */
    private void initActionBar() {
    }

    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {
        data = getIntent().getStringExtra("data");
        email=getIntent().getStringExtra("data1");

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white_btn);
        }
        ImageView iv_search;
        TextView tv_title;

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setTypeface(ClanaproNarrMedium);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

        switch (data) {
            case "phone":
                tv_title.setText(getResources().getString(R.string.edit_phone));
                break;
            case "name":
                tv_title.setText(getResources().getString(R.string.change_name));
                break;
            default:
                tv_title.setText(getResources().getString(R.string.change_pass));
                break;
        }

        LinearLayout llPhone = (LinearLayout) findViewById(R.id.llPhone);
        LinearLayout llName = (LinearLayout) findViewById(R.id.llName);
        LinearLayout ll_password = (LinearLayout) findViewById(R.id.ll_password);

        switch (data) {
            case "phone":
                llPhone.setVisibility(View.VISIBLE);
                break;
            case "name":
                llName.setVisibility(View.VISIBLE);
                break;
            case "password":
                ll_password.setVisibility(View.VISIBLE);
                break;

        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);


        TextView tv_editphone_msg, tv_phone_num;

        tv_editphone_msg = (TextView) findViewById(R.id.tv_editphone_msg);
        tv_editphone_msg.setTypeface(ClanaproNarrNews);

        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        tv_phone_num.setTypeface(ClanaproNarrNews);

        TextView tv_save = (TextView) findViewById(R.id.tv_save);
        tv_save.setTypeface(ClanaproNarrMedium);
        tv_save.setOnClickListener(this);

        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        et_phone_num.setTypeface(ClanaproNarrNews);

        etName = (EditText) findViewById(R.id.etName);
        etName.setTypeface(ClanaproNarrNews);

        et_newpass = (EditText) findViewById(R.id.et_newpass);
        et_newpass.setTypeface(ClanaproNarrNews);

        et_re_newpass = (EditText) findViewById(R.id.et_re_newpass);
        et_re_newpass.setTypeface(ClanaproNarrNews);

        RelativeLayout countryPicker = (RelativeLayout) findViewById(R.id.countryPicker);
        countryCode = (TextView) findViewById(R.id.code);
        countryCode.setTypeface(ClanaproNarrNews);
        flag = (ImageView) findViewById(R.id.flag);
        String code = Utility.getCounrtyCode(EditProfileActivity.this);

        if (!code.isEmpty()) {
            String drawableName = "flag_"
                    + code.toLowerCase(Locale.ENGLISH);
            flag.setBackgroundResource(getResId(drawableName));


            String allCountriesCode = null;
            try {
                allCountriesCode = readEncodedJsonString(this);
                JSONArray countrArray = new JSONArray(allCountriesCode);
                JSONObject jsonObject;
                String dialCode = "";
                for (int index = 0; index < countrArray.length(); index++) {
                    jsonObject = countrArray.getJSONObject(index);
                    if (jsonObject.getString("code").equals(code)) {
                        dialCode = jsonObject.getString("dial_code");
                        maxPhoneLength = jsonObject.getInt("max_digits");
                        minPhoneLength = (!jsonObject.getString("min_digits").isEmpty()) ? jsonObject.getInt("min_digits") : 5;
                        Utility.printLog("Minimum length " + minPhoneLength);
                        break;
                    }
                }

                countryCode.setText(dialCode);
                sessionManager.setCountryCode(dialCode);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

     /*   InputFilter[] fArray = new InputFilter[1];
   *//*     if (maxPhoneLength == 0) {
            et_phone_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        } else {*//*
            fArray[0] = new InputFilter.LengthFilter(maxPhoneLength);
            et_phone_num.setFilters(fArray);
        *//*}*/
        if (et_phone_num.getText().toString().length() > 0) {
            if (et_phone_num.getText().toString().charAt(0) == '0') {
                mobileNumberWithoutZero = et_phone_num.getText().toString().substring(1);
            } else {
                mobileNumberWithoutZero = et_phone_num.getText().toString();
            }
        }

        countryPicker.setOnClickListener(this);

        editProfilePresenter = new EditProfilePresenter(this);
    }

    private static String readEncodedJsonString(Context context)
            throws java.io.IOException {
        String base64 = context.getResources().getString(R.string.countries_code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
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
                updateProfileDetails();
                break;

            case R.id.countryPicker:
                showDialoagforcountrypicker();
                break;
        }
    }

    private void updateProfileDetails() {
        switch (data) {
            case "phone":
                if (!et_phone_num.getText().toString().matches("")) {
                    try {
                        JSONObject validatePhoneJson = new JSONObject();
                        validatePhoneJson.put("mobile",  et_phone_num.getText().toString());
                        validatePhoneJson.put("validationType", 2);
                        validatePhoneJson.put("countryCode", countryCode.getText().toString());
                       validatePhoneJson.put("ent_email",email);

                        JSONObject otpJson = new JSONObject();
                        otpJson.put("mobile",  et_phone_num.getText().toString());
                        otpJson.put("userType", VariableConstant.USER_TYPE);
                       otpJson.put("countryCode", countryCode.getText().toString());
                       otpJson.put("email",email);

                        Log.d(TAG, "updateProfileDetails: " + validatePhoneJson);
                        editProfilePresenter.validatePhone(sessionManager.getSessionToken(), validatePhoneJson, otpJson);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.toastMessage(EditProfileActivity.this, getResources().getString(R.string.phone_mis));
                }
                break;

            case "name":
                try {
                    if (!etName.getText().toString().equals("")) {
                        JSONObject updateProfileName = new JSONObject();
                        updateProfileName.put("ent_name", etName.getText().toString());

                        editProfilePresenter.updateProfile(sessionManager.getSessionToken(), updateProfileName);
                    } else {
                        Utility.toastMessage(EditProfileActivity.this, getResources().getString(R.string.nameMiss));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "password":
                try {
                    if (validPassword()) {
                        JSONObject updateProfileName = new JSONObject();
                        updateProfileName.put("ent_password", et_newpass.getText().toString());

                        editProfilePresenter.updateProfile(sessionManager.getSessionToken(), updateProfileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:

                break;
        }
    }

    private boolean validPassword() {
        if (et_newpass.getText().toString().equals("")) {
            Utility.BlueToast(this, getString(R.string.password_miss));
            return false;
        } else if (et_re_newpass.getText().toString().equals("")) {
            Utility.BlueToast(this, getString(R.string.conform_password_miss));
            return false;
        } else if (!et_newpass.getText().toString().equals(et_re_newpass.getText().toString())) {
            Utility.BlueToast(this, getString(R.string.passNotMactch));
            return false;
        }
        return true;
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
                picker.dismiss();
            }
        });
    }

    @Override
    public void startProgressBar() {
        pDialog.show();
    }

    @Override
    public void stopProgressBar() {
        pDialog.dismiss();
    }

    @Override
    public void onFailure(String msg) {
        Utility.BlueToast(EditProfileActivity.this, msg);
    }

    @Override
    public void onFailure() {
        Utility.BlueToast(EditProfileActivity.this, getString(R.string.serverError));
    }

    @Override
    public void onSuccess(String succesMsg) {
        Utility.BlueToast(EditProfileActivity.this, succesMsg);
        VariableConstant.IS_PROFILE_EDITED = true;
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onSuccessPhoneValidation(String otp) {

        Utility.printLog(TAG+ "PK OTP: "+otp);
        Intent mIntent = new Intent(getApplicationContext(), ForgotPasswordVerify.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("from", "EditPhone");
        mBundle.putString("countryCode",countryCode.getText().toString());
        mBundle.putString("mobile",  et_phone_num.getText().toString());
        mBundle.putString("otp", otp);
        mIntent.putExtras(mBundle);
        startActivityForResult(mIntent, PHONE_VALIDATION_REQUEST_CODE);
    }

    @Override
    public void onSuccessPasswordUpdated(String successMsg) {
        sessionManager.setPassword(et_newpass.getText().toString());
        Utility.BlueToast(EditProfileActivity.this, successMsg);
        VariableConstant.IS_PROFILE_EDITED = true;
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHONE_VALIDATION_REQUEST_CODE:
                    JSONObject masterProfileJson = new JSONObject();
                    try {
                        masterProfileJson.put("ent_mobile", et_phone_num.getText().toString());
//                        masterProfileJson.put("countryCode", countryCode.getText().toString());
                        editProfilePresenter.updateProfile(sessionManager.getSessionToken(), masterProfileJson);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }
}
