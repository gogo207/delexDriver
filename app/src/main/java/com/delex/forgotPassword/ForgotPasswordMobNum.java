package com.delex.forgotPassword;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delex.country_picker.CountryPicker;
import com.delex.country_picker.CountryPickerListener;
import com.delex.login.Login;
import com.delex.driver.R;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;

public class ForgotPasswordMobNum extends AppCompatActivity implements View.OnClickListener {

    String mobileNumberWithoutZero, fullMobileNumber;
    private TextView tv_forgot_next;
    private EditText et_forgot_mob;
    private TextView countryCode;
    private ImageView flag;
    private ProgressDialog pDialog;
    private LinearLayout sv_signup;
    private boolean isEmail=false;
    private SessionManager sessionManager;
    private int minPhoneLength=0,maxPhoneLength=15;
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
        setContentView(R.layout.activity_forgot_password_mob_num);
        overridePendingTransition(R.anim.bottem_slide_down, R.anim.stay_activity);
        sessionManager = SessionManager.getSessionManager(ForgotPasswordMobNum.this);
        if(sessionManager.getLangName().equalsIgnoreCase("Arabic")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        initActionBar();
        initializeViews();
    }

    /**********************************************************************************************/

    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();

    }

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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_signup_close);
        }
        ImageView iv_search;
        TextView tv_title;

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.forgotPassword));
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
    /**********************************************************************************************/

    /**********************************************************************************************/
                                                                      /*initializeViews*/

    /**********************************************************************************************/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_activity, R.anim.bottem_slide_up);
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

        final TextView tv_forgot_msg, tv_forgot_phoneno;

        tv_forgot_msg = (TextView) findViewById(R.id.tv_forgot_msg);
        tv_forgot_msg.setTypeface(ClanaproNarrNews);

        tv_forgot_phoneno = (TextView) findViewById(R.id.tv_forgot_phoneno);
        tv_forgot_phoneno.setTypeface(ClanaproNarrNews);

        et_forgot_mob = (EditText) findViewById(R.id.et_forgot_mob);
        et_forgot_mob.setTypeface(ClanaproNarrNews);
        et_forgot_mob.setHint(getResources().getString(R.string.phone_number));

        tv_forgot_next = (TextView) findViewById(R.id.tv_forgot_next);
        tv_forgot_next.setTypeface(ClanaproNarrMedium);
        tv_forgot_next.setOnClickListener(this);

        RelativeLayout countryPicker = (RelativeLayout) findViewById(R.id.countryPicker);
        countryCode = (TextView) findViewById(R.id.code);
        countryCode.setTypeface(ClanaproNarrNews);
        flag = (ImageView) findViewById(R.id.flag);


        String code=Utility.getCounrtyCode(this);
        if(!code.isEmpty()){
            String drawableName = "flag_"
                    + code.toLowerCase(Locale.ENGLISH);
            flag.setBackgroundResource(getResId(drawableName));




            String allCountriesCode = null;
            try {
                allCountriesCode = readEncodedJsonString(this);
                JSONArray countrArray = new JSONArray(allCountriesCode);
                JSONObject jsonObject;
                String dialCode="";
                for(int index=0;index<countrArray.length();index++){
                    jsonObject=countrArray.getJSONObject(index);
                    if(jsonObject.getString("code").equals(code)){
                        dialCode=jsonObject.getString("dial_code");
                        maxPhoneLength=jsonObject.getInt("max_digits");
                        minPhoneLength=(!jsonObject.getString("min_digits").isEmpty())?jsonObject.getInt("min_digits"):5;
                        Utility.printLog("Minimum length "+minPhoneLength);
                        break;
                    }
                }

                countryCode.setText(dialCode);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setMaxLength(maxPhoneLength);

        if (et_forgot_mob.getText().toString().length() > 0) {
            if (et_forgot_mob.getText().toString().charAt(0) == '0') {
                mobileNumberWithoutZero = et_forgot_mob.getText().toString().substring(1);
            } else {
                mobileNumberWithoutZero = et_forgot_mob.getText().toString();
            }
        }

        fullMobileNumber = countryCode.getText().toString() + mobileNumberWithoutZero;

        flag.setOnClickListener(this);

        sv_signup = (LinearLayout) findViewById(R.id.ll_first);
        sv_signup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utility.hideSoftKeyboard(ForgotPasswordMobNum.this);

                return false;
            }
        });

        et_forgot_mob.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    PhoneValidation();
                    return true;
                }
                return false;
            }
        });
        et_forgot_mob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(et_forgot_mob.getText().toString().isEmpty()){
                    tv_forgot_next.setFocusable(false);
                    tv_forgot_next.setBackgroundColor(getResources().getColor(R.color.gray));
                }
                else {

                    if (Patterns.EMAIL_ADDRESS.matcher(et_forgot_mob.getText().toString()).matches()) {
                        tv_forgot_next.setFocusable(true);
                        tv_forgot_next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    } else if (Patterns.PHONE.matcher(et_forgot_mob.getText().toString()).matches() && et_forgot_mob.getText().toString().length()>=minPhoneLength ) {
                        tv_forgot_next.setFocusable(true);
                        tv_forgot_next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                    else {
                        tv_forgot_next.setFocusable(false);
                        tv_forgot_next.setBackgroundColor(getResources().getColor(R.color.gray));
                    }


                }

            }
        });

        RadioGroup rg_WorkGroup = (RadioGroup) findViewById(R.id.rgForgotPass);
        rg_WorkGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbEmail:
                        isEmail=true;
                        et_forgot_mob.setHint(getResources().getString(R.string.Email));
                        et_forgot_mob.setText("");
                        tv_forgot_msg.setText(getResources().getString(R.string.forgotMsgEmail));
                        et_forgot_mob.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        flag.setVisibility(View.GONE);
                        countryCode.setVisibility(View.GONE);
                        setMaxLength(100);
                        break;
                    case R.id.rbPhone:
                        isEmail=false;
                        et_forgot_mob.setText("");
                        et_forgot_mob.setHint(getResources().getString(R.string.phone_number));
                        tv_forgot_msg.setText(getResources().getString(R.string.forgotMsgMob));
                        et_forgot_mob.setInputType(InputType.TYPE_CLASS_NUMBER);
                        flag.setVisibility(View.VISIBLE);
                        countryCode.setVisibility(View.VISIBLE);
                        setMaxLength(maxPhoneLength);
                        break;

                }
            }
        });
    }
    /**********************************************************************************************/

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_forgot_next:
                PhoneValidation();
                break;

            case R.id.flag:
                showDialoagforcountrypicker();
                break;
        }
    }

    /**
     * <h1>PhoneValidation</h1>
     */
    private void PhoneValidation() {
        String mob_mail = et_forgot_mob.getText().toString();
        if (!mob_mail.matches("")) {
            if (Patterns.EMAIL_ADDRESS.matcher(mob_mail).matches()) {
                ForgotPasswordService(2);
            } else if (Patterns.PHONE.matcher(mob_mail).matches() && mob_mail.length()>=minPhoneLength && mob_mail.length()<=maxPhoneLength) {
                ForgotPasswordService(1);
            } else {
                if(isEmail){
                    Utility.mShowMessage(getResources().getString(R.string.message),
                            getResources().getString(R.string.invalidEmail),ForgotPasswordMobNum.this);
                }else {
                    Utility.mShowMessage(getResources().getString(R.string.message),
                            getResources().getString(R.string.invalidPhone),ForgotPasswordMobNum.this);
                }
            }
        } else {

//            Utility.toastMessage(ForgotPasswordMobNum.this, getResources().getString(R.string.phone_miss));
        }
    }

    /******************************************************for country picker**********************/
    private void showDialoagforcountrypicker() {
        final CountryPicker picker = CountryPicker.newInstance(getResources().getString(R.string.select_country));
        picker.show(getSupportFragmentManager(), "COUNTRY_CODE_PICKER");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,int minLength,int maxLength) {
                countryCode.setText(dialCode);
                String drawableName = "flag_"
                        + code.toLowerCase(Locale.ENGLISH);
                flag.setBackgroundResource(getResId(drawableName));
                if (et_forgot_mob.getText().toString().length() > 0) {

                    if (et_forgot_mob.getText().toString().charAt(0) == '0') {
                        mobileNumberWithoutZero = et_forgot_mob.getText().toString().substring(1);
                    } else {
                        mobileNumberWithoutZero = et_forgot_mob.getText().toString();

                    }
                }
                minPhoneLength=minLength;
                maxPhoneLength=maxLength;

                setMaxLength(maxLength);

                fullMobileNumber = countryCode.getText().toString() + mobileNumberWithoutZero;

                picker.dismiss();
            }
        });
    }

    /**********************************************************************************************/
    void ForgotPasswordService(final int type) {

        Utility.hideSoftKeyboard(ForgotPasswordMobNum.this);

        pDialog.show();
        final String phone =/*countryCode.getText().toString()+*/et_forgot_mob.getText().toString();
        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("ent_email_mobile", phone);
            reqOtpObject.put("userType", VariableConstant.USER_TYPE);
            reqOtpObject.put("ent_type", type);//1- mobile , 2- email
            reqOtpObject.put("countryCode",countryCode.getText().toString());

            Utility.printLog("forgotpasswordMobnum request: " + reqOtpObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.FORGOTPASSWORD, OkHttp3Connection.Request_type.POST, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                System.out.println("forgotpasswordMobnum result " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            if (type == 1) {
                                Intent intent = new Intent(ForgotPasswordMobNum.this, ForgotPasswordVerify.class);
                                Bundle mbundle = new Bundle();
                                mbundle.putString("mobile", phone);
                                mbundle.putString("from", "ForgotPass");
                                mbundle.putString("countryCode",countryCode.getText().toString());
                                intent.putExtras(mbundle);
                                startActivity(intent);
                            } else {
                                Utility.mShowMessage(getResources().getString(R.string.message),validatorPojo.getErrMsg(),ForgotPasswordMobNum.this);

                                AlertDialog.Builder dialog= new AlertDialog.Builder(ForgotPasswordMobNum.this);
                                dialog.setTitle("Message");
                                dialog.setMessage("We have sent a link to your email, please check");
                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ForgotPasswordMobNum.this, Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                });
                                dialog.show();

                            }

                            break;
                        case 1:
//                            Utility.BlueToast(ForgotPasswordMobNum.this, validatorPojo.getErrMsg());
                            Utility.mShowMessage(getResources().getString(R.string.message),validatorPojo.getErrMsg(),ForgotPasswordMobNum.this);

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

    private static String readEncodedJsonString(Context context)
            throws java.io.IOException {
        String base64 = context.getResources().getString(R.string.countries_code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }


    public void setMaxLength(int length){
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(length);
        et_forgot_mob.setFilters(fArray);
    }
}
