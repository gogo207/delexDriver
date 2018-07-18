package com.delex.forgotPassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.delex.profile.ChangePasswordDialog;
import com.delex.profile.EditProfileActivity;
import com.delex.driver.R;
import com.delex.login.Login;
import com.delex.pojo.SignUpResponsePojo;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import org.json.JSONException;
import org.json.JSONObject;


/**************************************************************************************************/
public class ForgotPasswordVerify extends AppCompatActivity implements View.OnClickListener {

    private EditText et_otp1, et_otp2, et_otp3, et_otp4;
    private CountDownTimer countDownTimer;
    private JSONObject jsonObjectSignUp;

    private String phone;
    private String from = "", SignUpVahicle = "SignUpVahicle", ForgotPass = "ForgotPass", EditPhone = "EditPhone";

    private ChangePasswordDialog changePasswordDialog;
    private String OTP = "";
    private ProgressDialog pDialog;
    private SessionManager sessionManager;
    private TextView tvTimer, tv_resendcode;
    private String otp="",countryCode="";

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_verify);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        sessionManager = new SessionManager(this);

        FetchValues();
        initActionBar();
        initializeViews();

        startTimer(60);
    }

    /**********************************************************************************************/
    /**
     * <h1>FetchValues</h1>
     * <p>The values fetch from the SignupVehicle Activity for Signup.</p>
     */
    private void FetchValues() {
        Bundle mBundle = getIntent().getExtras();
        from = mBundle.getString("from");
        if (from.equals(SignUpVahicle)) {
            String siginUpData = mBundle.getString("signupdata");
            Utility.printLog("Json String for Signup :" + siginUpData);
            try {
                jsonObjectSignUp = new JSONObject(siginUpData);
            } catch (Exception e) {

            }
            phone = mBundle.getString("mobile");
            otp = mBundle.getString("otp");
            countryCode=mBundle.getString("countryCode");
        }
        else if (from.equals(EditPhone)){
            countryCode=mBundle.getString("countryCode");
            phone = mBundle.getString("mobile");
            otp = mBundle.getString("otp");
        }
        else if (from.equals(ForgotPass)){
            countryCode=mBundle.getString("countryCode");
            phone = mBundle.getString("mobile");
            otp = mBundle.getString("otp");
        }
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
        tv_title.setText(getResources().getString(R.string.verify_mob));
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
    @Override
    protected void onStart() {
        super.onStart();
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

        TextView tv_verify_txt, tv_verify_msg;

        tv_verify_txt = (TextView) findViewById(R.id.tv_verify_txt);
        tv_verify_txt.setTypeface(ClanaproNarrMedium);
        tv_verify_txt.setOnClickListener(this);

        tv_verify_msg = (TextView) findViewById(R.id.tv_verify_msg);
        tv_verify_msg.setTypeface(ClanaproNarrNews);

        String mob=phone;
        if(mob.startsWith("+")){
            mob=mob.substring(3);
        }
        tv_verify_msg.setText(getString(R.string.verify_msg1) +" "+phone+". "+ getString(R.string.verify_msg2));

        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvTimer.setTypeface(ClanaproNarrNews);

        et_otp1 = (EditText) findViewById(R.id.et_otp1);
        et_otp1.setTypeface(ClanaproNarrMedium);
        et_otp1.requestFocus();
        et_otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_otp1.requestFocus();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    et_otp2.requestFocus();
                } else {
                    et_otp1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                et_otp2.requestFocus();
            }
        });

        et_otp2 = (EditText) findViewById(R.id.et_otp2);
        et_otp2.setTypeface(ClanaproNarrMedium);
        et_otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    et_otp3.requestFocus();
                } else {
                    et_otp1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                et_otp3.requestFocus();
            }
        });

        et_otp3 = (EditText) findViewById(R.id.et_otp3);
        et_otp3.setTypeface(ClanaproNarrMedium);
        et_otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    et_otp4.requestFocus();
                } else {
                    et_otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                et_otp4.requestFocus();
            }
        });

        et_otp4 = (EditText) findViewById(R.id.et_otp4);
        et_otp4.setTypeface(ClanaproNarrMedium);
        et_otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    et_otp4.requestFocus();
                    Utility.hideSoftKeyboard(ForgotPasswordVerify.this);
                    OTPValidation();
                } else {
                    et_otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Utility.hideSoftKeyboard(ForgotPasswordVerify.this);
//                OTPValidation();
            }
        });

        tv_resendcode = (TextView) findViewById(R.id.tv_resendcode);
        tv_resendcode.setTypeface(ClanaproNarrNews);
        tv_resendcode.setOnClickListener(this);


        LinearLayout ll_first = (LinearLayout) findViewById(R.id.ll_first);
        ll_first.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utility.hideSoftKeyboard(ForgotPasswordVerify.this);
                return false;
            }
        });

        if(otp!=null  && !otp.isEmpty()){
            et_otp1.setText(otp.substring(0,1));
            et_otp2.setText(otp.substring(1,2));
            et_otp3.setText(otp.substring(2,3));
            et_otp4.setText(otp.substring(3));
        }

    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_verify_txt:
                OTPValidation();
                break;

            case R.id.tv_resendcode:
                tv_resendcode.setEnabled(false);
                tv_resendcode.setTextColor(getResources().getColor(R.color.gray_heading));

                if (from.equals(ForgotPass)){

                    resendotpForgot();
                }

                else ResendOTP();
                break;

        }
    }

    private void resendotpForgot() {


        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("ent_email_mobile", phone);
            reqOtpObject.put("userType", VariableConstant.USER_TYPE);
            reqOtpObject.put("ent_type", 1);//1- mobile , 2- email
            reqOtpObject.put("countryCode",countryCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.FORGOTPASSWORD, OkHttp3Connection.Request_type.POST, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                System.out.println("Signup result " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            startTimer(60);
//                            Toast.makeText(ForgotPasswordVerify.this, validatorPojo.getOtp(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ForgotPasswordVerify.this, validatorPojo.getErrMsg(), Toast.LENGTH_SHORT).show();
//                            Utility.BlueToast(ForgotPasswordVerify.this, validatorPojo.getErrMsg());
                            Utility.mShowMessage(getResources().getString(R.string.message),validatorPojo.getErrMsg(),ForgotPasswordVerify.this);
                            break;
                        case 1:
                            tv_resendcode.setTextColor(getResources().getColor(R.color.black1));
                            tv_resendcode.setEnabled(true);
                            Utility.BlueToast(ForgotPasswordVerify.this, validatorPojo.getErrMsg());
                            break;
                    }
                } else {
                    Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.smthWentWrong));
                }

            }

            @Override
            public void onError(String error) {
                tv_resendcode.setTextColor(getResources().getColor(R.color.black1));
                tv_resendcode.setEnabled(true);
                Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.network_problem));
                System.out.println("signin error " + error);

            }
        }, "ordinory");

    }

    /**********************************************************************************************/
    /**
     * <h1>ResendOTP</h1>
     * <p>if the otp need to receive again this service will call, it is the same service when we call for OTP. </p>
     */
    void ResendOTP() {
        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("mobile", phone);
            reqOtpObject.put("userType", VariableConstant.USER_TYPE);

           // reqOtpObject.put("email",Email);
            reqOtpObject.put("countryCode",countryCode);
            /*reqOtpObject.put("validationType", 1);*/
            reqOtpObject.put("email","emil");
            Utility.printLog("siginup otp request: " + reqOtpObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.SIGNUPOTP, OkHttp3Connection.Request_type.POST, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                System.out.println("Signup result " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()) {
                        case 0:
                            startTimer(60);
//                            Toast.makeText(ForgotPasswordVerify.this, validatorPojo.getOtp(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ForgotPasswordVerify.this, validatorPojo.getErrMsg(), Toast.LENGTH_SHORT).show();
//                            Utility.BlueToast(ForgotPasswordVerify.this, validatorPojo.getErrMsg());
                            Utility.mShowMessage(getResources().getString(R.string.message),validatorPojo.getErrMsg(),ForgotPasswordVerify.this);
                            break;
                        case 1:
                            tv_resendcode.setTextColor(getResources().getColor(R.color.black1));
                            tv_resendcode.setEnabled(true);
                            Utility.BlueToast(ForgotPasswordVerify.this, validatorPojo.getErrMsg());
                            break;
                    }
                } else {
                    Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.smthWentWrong));
                }

            }

            @Override
            public void onError(String error) {
                tv_resendcode.setTextColor(getResources().getColor(R.color.black1));
                tv_resendcode.setEnabled(true);
                Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.network_problem));
                System.out.println("signin error " + error);

            }
        }, "ordinory");
    }
    /**********************************************************************************************/
    /**
     * <h1>OTPValidation</h1>
     * <p>this is the method is call from when the OTP verify is click,
     * the method first check whether the otp entered or not if the OTP is empty then will show the error Toast message,
     * else the service call. the service call used for forgot password , Signup time and change phone number time also.
     * processType 1 for forgotpassword,
     * processType 2 for SignUp and Change phone.</p>
     */
    void OTPValidation() {

        if (et_otp1.getText().toString().matches("") ||
                et_otp2.getText().toString().matches("") ||
                et_otp3.getText().toString().matches("") ||
                et_otp4.getText().toString().matches("")) {
            Utility.BlueToast(this, getResources().getString(R.string.enter_otp));

        } else {
            pDialog.show();
            OTP = et_otp1.getText().toString() + et_otp2.getText().toString() + et_otp3.getText().toString() + et_otp4.getText().toString();
            String authentication = sessionManager.getSessionToken();
            JSONObject reqOtpObject = new JSONObject();
            try {
                reqOtpObject.put("mobile", phone);
                reqOtpObject.put("code", OTP);
                if (from.equals(ForgotPass)) {
                    reqOtpObject.put("processType", 1);
                    authentication = "ordinory";
                } else if (from.equals(EditPhone)) {
                    reqOtpObject.put("processType", 2);
                    authentication = sessionManager.getSessionToken();
                    authentication = sessionManager.getSessionToken();
                } else {
                    reqOtpObject.put("processType", 2);
                    authentication = "ordinory";
                }

                reqOtpObject.put("userType", VariableConstant.USER_TYPE);

                Utility.printLog("siginup otp request: " + reqOtpObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(ServiceUrl.VERIFYOTP, OkHttp3Connection.Request_type.POST, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    pDialog.dismiss();
                    System.out.println("Signup result " + result);
                    if (result != null) {
                        ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                        switch (validatorPojo.getErrFlag()) {
                            case 0:
//                                Utility.BlueToast(ForgotPasswordVerify.this, validatorPojo.getErrMsg());
                                if (from.equals(SignUpVahicle)) {
                                    SignUpJSON();
                                } else if (from.equals(ForgotPass)) {
                                    Intent intent = new Intent(ForgotPasswordVerify.this, ForgotPasswordChangePass.class);
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString("OTP", OTP);
                                    mBundle.putString("phone", phone);
                                    intent.putExtras(mBundle);
                                    startActivity(intent);
                                } else if (from.equals(EditPhone)) {
                                    Intent intent = new Intent(ForgotPasswordVerify.this, EditProfileActivity.class);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                                break;
                            case 1:
                                Utility.BlueToast(ForgotPasswordVerify.this, validatorPojo.getErrMsg());
                                break;
                        }

                    } else {
                        Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.smthWentWrong));
                    }
                }

                @Override
                public void onError(String error) {
                    Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.network_problem));
                    System.out.println("signin error " + error);
                }
            }, authentication);
        }
    }

    /**********************************************************************************************/
    /**
     * <h1>SignUpJSON</h1>
     * <p>if the OTP is correct the Method will call, and SIGN_UP Api will call, and make the registration done or not.
     * the jsonObjectSignUp json object is contain the Personal details and the vehicle details also withe the documents,
     * the documents contain the images and the image is uploaded in amazon and the image url is passing here.
     * </p>
     */
    void SignUpJSON() {
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.SIGN_UP, OkHttp3Connection.Request_type.POST, jsonObjectSignUp, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                System.out.println("Signup result " + result);

                if (result != null) {
                    SignUpResponsePojo signUpResponsePojo = new Gson().fromJson(result, SignUpResponsePojo.class);
                    switch (signUpResponsePojo.getErrFlag()) {
                        case 0:

                            Intent intent = new Intent(ForgotPasswordVerify.this, Login.class);
                            intent.putExtra("success_msg",getResources().getString(R.string.signup_success_msg));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                           /* changePasswordDialog = new ChangePasswordDialog(ForgotPasswordVerify.this, "from_signup", new ChangePasswordDialog.RefreshProfile() {
                                @Override
                                public void onRefresh() {
                                    changePasswordDialog.dismiss();
                                    Intent intent = new Intent(ForgotPasswordVerify.this, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                            changePasswordDialog.show();*/
                            break;
                        case 1:

                            break;
                    }
                    if (signUpResponsePojo.getStatusCode() != null) {
                        Utility.BlueToast(ForgotPasswordVerify.this, signUpResponsePojo.getErrMsg());
                    }

                } else {
                    Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.smthWentWrong));
                }

            }

            @Override
            public void onError(String error) {
                Utility.BlueToast(ForgotPasswordVerify.this, getResources().getString(R.string.network_problem));
                System.out.println("signin error " + error);

            }
        }, "ordinory");
    }

    private void startTimer(long j) {

        tv_resendcode.setEnabled(false);
        tv_resendcode.setTextColor(getResources().getColor(R.color.gray_heading));
        final long finalTime = j;
        countDownTimer = new CountDownTimer(finalTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;

                int barVal = (int) seconds;
                tvTimer.setText("00:" + String.format("%02d", seconds % 60));

            }

            public void onFinish() {
                tvTimer.setText("");
                tv_resendcode.setTextColor(getResources().getColor(R.color.black1));
                tv_resendcode.setEnabled(true);
            }
        }.start();

    }
}
