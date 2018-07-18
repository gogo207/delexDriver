package com.delex.wallet.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.fcm.MyFirebaseMessagingService;


/**
 * <h1>Alerts</h1>
 * <p>
 *     class to show different types of alerts
 * </p>
 * @since 23/7/15.
 */
public class Alerts extends Activity {

    /**
     * <h2>showNetworkAlert</h2>
     * This method is used to show network alert box, for opening settings.
     * @param context
     */
    public void showNetworkAlert(final Context context)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(context.getResources().getString(R.string.network_alert_title));
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(context.getResources().getString(R.string.network_alert_message));
        // On pressing Settings button
        alertDialog.setPositiveButton(context.getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        boolean isbkrnd= MyFirebaseMessagingService.isApplicationSentToBackground(context);
      // Showing Alert Message
        if(isbkrnd == false)
            alertDialog.show();
    }


    /**
     * <h2>problemLoadingAlert</h2>
     * <p>
     *     method to show an alert for loading error
     * </p>
     * @param context
     * @param message
     */
    public void problemLoadingAlert(final Context context, String message)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_ok);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        TextView tv_ok = (TextView) dialog.findViewById(R.id.tv_ok);
        TextView tv_text = (TextView) dialog.findViewById(R.id.tv_text);
        tv_text.setText(message);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
