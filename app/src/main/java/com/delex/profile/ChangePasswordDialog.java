package com.delex.profile;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

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

/**
 * Created by embed on 20/5/17.
 */


public class ChangePasswordDialog extends Dialog {
    RefreshProfile refreshProfile;
    private Context context;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private Dialog passwordDialog;
    private String TAG = ChangePasswordDialog.class.getSimpleName(), from;
    private EditText et_oldpass;
    private TextView tv_confirm;
    private String from_pass = "Password", from_name = "Name", from_signup = "from_signup",from_rcvr="receiver";
    private View view_;

    /**********************************************************************************************/
    public ChangePasswordDialog(Context context, String from, RefreshProfile refreshProfile) {
        super(context);
        this.context = context;
        this.from = from;
        this.refreshProfile = refreshProfile;
        this.setCanceledOnTouchOutside(true);

    }

    /**********************************************************************************************/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.getSessionManager(context);
        this.setCanceledOnTouchOutside(true);

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(context.getString(R.string.loading));
        pDialog.setCancelable(false);

        passwordDialog = new Dialog(context);
        passwordDialog.setCancelable(true);
        passwordDialog.setCanceledOnTouchOutside(true);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.setContentView(R.layout.single_password_conform_dialog);

        Typeface ClanaproNarrNews = Typeface.createFromAsset(getContext().getAssets(), "fonts/ClanPro-NarrNews.otf");
        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/ClanPro-NarrMedium.otf");
        TextView tv_verify_msg, tv_oldpass;

        tv_verify_msg = (TextView) passwordDialog.findViewById(R.id.tv_verify_msg);
        tv_verify_msg.setTypeface(ClanaproNarrNews);

        tv_oldpass = (TextView) passwordDialog.findViewById(R.id.tv_oldpass);
        tv_oldpass.setTypeface(ClanaproNarrNews);

        et_oldpass = (EditText) passwordDialog.findViewById(R.id.et_oldpass);
        tv_oldpass.setTypeface(ClanaproNarrNews);

        tv_confirm = (TextView) passwordDialog.findViewById(R.id.tv_confirm);
        tv_confirm.setTypeface(ClanaproNarrMedium);
        view_ = passwordDialog.findViewById(R.id.view_);

        if (from.equals(from_pass)) {
            tv_verify_msg.setText(context.getResources().getString(R.string.enter_old_pass));
            tv_oldpass.setText(context.getResources().getString(R.string.old_pas));
            tv_confirm.setText(context.getResources().getString(R.string.ok));
            et_oldpass.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else if (from.equals(from_signup)) {
            et_oldpass.setVisibility(View.GONE);
            tv_verify_msg.setText(context.getResources().getString(R.string.message));
            tv_oldpass.setText(context.getResources().getString(R.string.signup_success_msg));
            tv_confirm.setText(context.getResources().getString(R.string.ok));
            view_.setVisibility(View.GONE);

        }
        else if (from.equals(from_rcvr)) {
            et_oldpass.setVisibility(View.GONE);
            tv_verify_msg.setText(context.getResources().getString(R.string.message));
            tv_oldpass.setText(context.getResources().getString(R.string.collect_cash_from_rcvr));
            tv_oldpass.setTextSize(20);
            tv_confirm.setText(context.getResources().getString(R.string.ok));
            view_.setVisibility(View.GONE);

        }

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals(from_pass)) {
//                    refreshProfile.onRefresh();
                    OldPasswordVerify();
                } else /*if (from.equals(from_signup))*/ {
                    refreshProfile.onRefresh();
                }
            }
        });

    }
    /**********************************************************************************************/
    @Override
    public void show() {
        super.show();
        passwordDialog.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
        passwordDialog.dismiss();
    }

    /**********************************************************************************************/
    private void OldPasswordVerify() {
        String oldpass = et_oldpass.getText().toString();

        if (!oldpass.matches("")) {
            if (oldpass.equals(sessionManager.getPassword())) {
                refreshProfile.onRefresh();
                VariableConstant.EDIT_PASSWORD_DIALOG = true;
                ChangePasswordDialog.this.dismiss();
            } else {
                Utility.BlueToast(context, context.getResources().getString(R.string.invalid_pass));

            }

        } else {
            Utility.BlueToast(context, context.getResources().getString(R.string.err_pass));
        }

    }

    /**********************************************************************************************/
    /**
     * <H1>MasterProfile</H1>
     * <p>for Change the profile picture</p>
     */
    private void MasterProfile(String name) {
        pDialog.show();
        JSONObject reqOtpObject = new JSONObject();
        try {
//            reqOtpObject.put("token",sessionManager.getSessionToken());
            reqOtpObject.put("ent_name", name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.UPDATE_PROFILE, OkHttp3Connection.Request_type.PUT, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                System.out.println("ChangePasswordDialog : update Profile result " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                    switch (validatorPojo.getErrFlag()) {
                        case 0:

                            refreshProfile.onRefresh();
                            break;
                        case 1:
                            Utility.toastMessage(getContext(), validatorPojo.getErrMsg());
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
     * <p>Interface to close the dialog and refresh the MyProfile details after change the name</p>
     */
    public interface RefreshProfile {
        void onRefresh();
    }

}
