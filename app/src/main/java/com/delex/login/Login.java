package com.delex.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.delex.forgotPassword.ForgotPasswordMobNum;
import com.delex.driver.R;
import com.delex.language.LanguageActivity;
import com.delex.signup.SignupPersonal;
import com.delex.utility.CustomTextWatcher;
import com.delex.utility.DisableError;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.delex.vehiclelist.VehicleList;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**************************************************************************************************/
public class Login extends AppCompatActivity implements View.OnClickListener, LoginView, DisableError, EasyPermissions.PermissionCallbacks {
    Typeface ClanaproNarrMedium;
    Typeface ClanaproNarrNews;
    private TextView tv_log_login;
    private EditText et_log_mob, et_log_pass;
    private TextInputLayout til_log_mob, til_log_pass;
    private ProgressDialog pDialog;
    private LoginPresenterImpl loginPresenter;
    private SessionManager sessionManager;
    private TextView tvLanguage;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginPresenter = new LoginPresenterImpl(Login.this, Login.this);
        tvLanguage = (TextView) findViewById(R.id.tvLanguage);
        tvLanguage.setTypeface(ClanaproNarrNews);
        tvLanguage.setOnClickListener(this);
        sessionManager = SessionManager.getSessionManager(Login.this);
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        initializeViews();
        sessionManager.setPushToken(FirebaseInstanceId.getInstance().getToken());
        String msg=getIntent().getStringExtra("success_msg");
        if(msg!=null){
            Utility.mShowMessage(getResources().getString(R.string.message),msg,this);
        }

    }
    /* *********************************************************************************************/
                                    /*initializeViews*/
    /* *********************************************************************************************/

    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {
        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        sessionManager = SessionManager.getSessionManager(Login.this);
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Typeface SFAutomaton = Typeface.createFromAsset(getAssets(), "fonts/SF Automaton.ttf");

        TextView tv_appname = (TextView) findViewById(R.id.tv_appname);
        tv_appname.setTypeface(SFAutomaton);

        TextView tv_splash_msg = (TextView) findViewById(R.id.tv_splash_msg);
        tv_splash_msg.setTypeface(ClanaproNarrMedium);

        tv_log_login = (TextView) findViewById(R.id.tv_log_login);
        tv_log_login.setTypeface(ClanaproNarrMedium);
        tv_log_login.setOnClickListener(this);

        TextView tv_log_forgortpass = (TextView) findViewById(R.id.tv_log_forgortpass);
        tv_log_forgortpass.setTypeface(ClanaproNarrNews);
        tv_log_forgortpass.setOnClickListener(this);



        TextView tv_log_signup = (TextView) findViewById(R.id.tv_log_signup);
        tv_log_signup.setText(Html.fromHtml(getString(R.string.login_signup_txt)));
        tv_log_signup.setOnClickListener(this);
        tv_log_signup.setTypeface(ClanaproNarrNews);

        et_log_mob = (EditText) findViewById(R.id.et_log_mail_mob);
        et_log_mob.setTypeface(ClanaproNarrNews);

        til_log_mob = (TextInputLayout) findViewById(R.id.til_log_mail_mob);
        til_log_mob.setTypeface(ClanaproNarrNews);

        et_log_pass = (EditText) findViewById(R.id.et_log_password);
        et_log_pass.setTypeface(ClanaproNarrNews);

        et_log_mob.addTextChangedListener(new CustomTextWatcher(et_log_mob, et_log_pass, this));
        et_log_pass.addTextChangedListener(new CustomTextWatcher(et_log_mob, et_log_pass, this));

        til_log_pass = (TextInputLayout) findViewById(R.id.til_log_password);
        til_log_pass.setTypeface(ClanaproNarrNews);
        til_log_mob = (TextInputLayout) findViewById(R.id.til_log_mail_mob);

        pDialog = new ProgressDialog(Login.this);
        pDialog.setMessage(getString(R.string.login_in));
        pDialog.setCancelable(false);

        loginPresenter.getLoginCreds();


        final View viewTop = findViewById(R.id.viewTop);
        final View activityRootView = findViewById(R.id.activityRoot);
        final CardView cvBottom = (CardView) findViewById(R.id.cvBottom);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(Login.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    viewTop.setVisibility(View.GONE);
                    cvBottom.setVisibility(View.GONE);
                    tvLanguage.setVisibility(View.GONE);
                }else {
                    viewTop.setVisibility(View.VISIBLE);
                    cvBottom.setVisibility(View.VISIBLE);
                    tvLanguage.setVisibility(View.VISIBLE);
                }
            }
        });




    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvLanguage:
                startActivity(new Intent(Login.this, LanguageActivity.class));
                break;

                case R.id.tv_log_login:
                //validate user input field
                //LoginDataValidation();
                methodRequiresOnePermission();
                if (sessionManager.getDeviceId() != null)
                    loginPresenter.validateCredentials(et_log_mob.getText().toString(), et_log_pass.getText().toString());
                break;

            case R.id.tv_log_forgortpass:
                //open activity forget password
                startActivity(new Intent(Login.this, ForgotPasswordMobNum.class));
                break;
            case R.id.tv_log_signup:
                //open signup activity
                startActivity(new Intent(Login.this, SignupPersonal.class));
                break;
        }

    }

    @AfterPermissionGranted(VariableConstant.RC_READ_PHONE_STATE)
    private void methodRequiresOnePermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission
            Utility.printLog("Login Device ID " + Utility.getDeviceId(Login.this));
            sessionManager.setDeviceId(Utility.getDeviceId(Login.this));

        } else {
            // Do not have permissions, requesting permission
            EasyPermissions.requestPermissions(this, getString(R.string.read_phone_state_permission_message),
                    VariableConstant.RC_READ_PHONE_STATE, perms);
        }
    }

    /**
     * show progress indicator
     */
    @Override
    public void showProgress() {
        pDialog.show();
    }

    /**
     * hide progress dialog
     */

    @Override
    public void hideProgress() {
        pDialog.dismiss();
    }

    /**
     * set username error that receive from validation
     *
     * @param message Error message get from validation of email and phone number
     */
    @Override
    public void setUsernameError(String message) {
        til_log_mob.setErrorEnabled(true);
        til_log_mob.setError(message);
    }

    /**
     * set password error that receive from validation
     *
     * @param message Error message get from validation of password
     */
    @Override
    public void setPasswordError(String message) {
        til_log_pass.setErrorEnabled(true);
        til_log_pass.setError(message);
    }

    /**
     * <p>on successful validating and positive response from api
     * it will navigate to the VehicleList activity</p>
     */
    @Override
    public void navigateToHome() {
        startActivity(new Intent(this, VehicleList.class));
        finish();
    }

    @Override
    public void setLoginCreds(String username, String pass) {
        et_log_mob.setText(username);
        et_log_pass.setText(pass);
    }

    /**
     * <h2>showError</h2>
     * <p>this method will toast user the api error message</p>
     *
     * @param message have api error message that will show to user
     */
    @Override
    public void showError(String message) {
//        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
        Utility.mShowMessage(getResources().getString(R.string.message),message,Login.this);
    }

    /**
     * <h2>onDestroy</h2>
     * <p>will called when activity popping out from
     * the back stack and will null esisting view</p>
     */
    @Override
    protected void onDestroy() {
        loginPresenter.onDestroy();
        super.onDestroy();
    }

    /**
     * <h2>setDisableError</h2>
     * <p>method will check which view is used by user and corresponding that
     * view will hide the error that is showing to user</p>
     *
     * @param view which is on focused and user is working on it
     */
    @Override
    public void setDisableError(View view) {
        switch (view.getId()) {
            case R.id.et_log_mail_mob:
                til_log_mob.setError(null);
                til_log_mob.setErrorEnabled(false);
                break;
            case R.id.et_log_password:
                til_log_pass.setError(null);
                til_log_pass.setErrorEnabled(false);
                break;

        }
    }

    @Override
    public void enableSighUp() {
        tv_log_login.setBackground(ContextCompat.getDrawable(this, R.drawable.selector_login_back));
    }

    @Override
    public void disableSighUp() {
        tv_log_login.setBackground(ContextCompat.getDrawable(this, R.drawable.sigin_back_grey));
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Utility.printLog("Login Device ID " + Utility.getDeviceId(Login.this));
        sessionManager.setDeviceId(Utility.getDeviceId(Login.this));

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {


        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else if (requestCode == VariableConstant.RC_READ_PHONE_STATE) {
            EasyPermissions.requestPermissions(Login.this, getString(R.string.read_phone_state_permission_message)
                    , VariableConstant.RC_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forwarding results to for permission check
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }




    @Override
    protected void onResume() {
        super.onResume();

        sessionManager = SessionManager.getSessionManager(Login.this);

        Utility.printLog("LANGUAGE",sessionManager.getLang());
        if(!sessionManager.getLang().isEmpty()){
//            setLocale(sessionManager.getLang());
            //lang=sessionManager.getLangName();
            tvLanguage.setText(sessionManager.getLangName());
        }


        /*if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }*/
    }
}
