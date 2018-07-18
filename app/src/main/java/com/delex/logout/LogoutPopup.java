package com.delex.logout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.delex.login.Login;
import com.delex.driver.R;
import com.delex.service.LocationUpdateService;
import com.delex.utility.AppConstants;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by embed on 1/12/15.
 */
public class LogoutPopup extends Dialog {
    private Context context;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private AlertDialog.Builder alertDialog;
    private String TAG = LogoutPopup.class.getSimpleName();

    /**
     * * Initialize Dialog Constructor
     *
     * @param context from where it is calling
     */
    public LogoutPopup(Context context) {
        super(context);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.getSessionManager(context);

        pDialog = new ProgressDialog(context);
        alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.logout));

        // Setting Dialog Message
        alertDialog.setMessage(context.getString(R.string.logoutmessage));

        alertDialog.setCancelable(false);


        // On pressing Settings button
        alertDialog.setPositiveButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        alertDialog.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * calling logout service
     * <p>Service Parameter-session token,,user type 2(master) </p>
     */
    private void logout() {
        pDialog.setMessage(context.getResources().getString(R.string.logging_out));
        pDialog.show();
        final JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("token", sessionManager.getSessionToken());
            jsonObject.put("userType", VariableConstant.USER_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
            dismiss();
        }
        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.LOGOUT, OkHttp3Connection.Request_type.PUT, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "Logout Service Response" + result);
                sessionManager.setIsLogin(false);
                pDialog.dismiss();

                if(Utility.isMyServiceRunning(LocationUpdateService.class,(Activity) context))
                {
                    Intent stopIntent = new Intent(context, LocationUpdateService.class);
                    stopIntent.setAction(AppConstants.ACTION.STOPFOREGROUND_ACTION);
                    context.startService(stopIntent);
                }


                sessionManager.clearSharedPredf();
                context.startActivity(new Intent(context, Login.class));
                dismiss();
                ((Activity) context).finish();

            }

            //if any network failure happen
            @Override
            public void onError(String error) {
                Toast.makeText(context, context.getResources().getString(R.string.network_problem), Toast.LENGTH_LONG).show();
                pDialog.dismiss();
                dismiss();

            }
        }, sessionManager.getSessionToken());

    }
}
