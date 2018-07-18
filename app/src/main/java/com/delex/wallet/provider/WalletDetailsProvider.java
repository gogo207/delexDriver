package com.delex.wallet.provider;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.delex.app.MainActivity;
import com.delex.app.SplashScreen;
import com.delex.driver.R;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.wallet.interface_callbacks.WalletFragInteractor;
import com.delex.wallet.pojo_classes.WalletPojo;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <h1>WalletDetailsProvider</h1>
 * provider class for wallet fragment which provide essentials details
 * required to represent wallet details
 *
 * @since 27/09/17.
 */

public class WalletDetailsProvider {
    private final String TAG = "WalletDetailsProvider";
    private String currency_symbol;
    private String rechargeAmount = "0.00";

    private Context mContext;
    private SessionManager sessionMgr;
    private WalletFragInteractor.WalletFragPresenterNotifier presenterNotifier;

    public WalletDetailsProvider(Context context, WalletFragInteractor.WalletFragPresenterNotifier _presenterNotifier) {
        mContext = context;
        this.presenterNotifier = _presenterNotifier;
        sessionMgr = new SessionManager(mContext);
        currency_symbol = sessionMgr.getCurrencySymbol();
    }
    //====================================================================

    public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public String getRechargeAmount() {
        return Utility.getFormattedPrice(rechargeAmount);
    }

    /**
     * <h2>getLastCard_No</h2>
     * <p>
     * method to get the last selected card no from the shared preferences
     * </p>
     *
     * @return String: last selected card no
     */
    public String getLastCard_No() {
        Log.d(TAG, "Last card no: " + sessionMgr.getLastCardNumber());
        return sessionMgr.getLastCardNumber();
    }

    //====================================================================

    /**
     * <h2>getWalletDetails</h2>
     * <p>
     * api call to get wallet setting details
     * </p>
     */
    public void getWalletDetails() {
        try {
            OkHttp3Connection.doOkHttp3Connection(sessionMgr.getSessionToken(), ServiceUrl.WALLET_DETAILS,
                    OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "getWalletDetails result: " + result);
                            getWalletDetailsResponseHandler(result);
                        }

                        @Override
                        public void onError(String error) {
                            presenterNotifier.showToastNotifier(mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                        }
                    }, sessionMgr.getSessionToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //====================================================================

    /**
     * <h2>getWalletDetailsResponseHandler</h2>
     * <p>
     * parsing response retrieved from getWalletDetails api call()
     * </p>
     *
     * @param jsonResponse: retrieved response from the api
     */
    private void getWalletDetailsResponseHandler(String jsonResponse) {
        try {
            WalletPojo walletPojo = new Gson().fromJson(jsonResponse, WalletPojo.class);

            if (walletPojo != null && walletPojo.getErrNum() == 200) {
                if (walletPojo.getData() != null) {
                    sessionMgr.setWalletSettings(walletPojo.getData());
                    presenterNotifier.walletDetailsApiSuccessNotifier(sessionMgr.getWalletSettings(), currency_symbol);
                }
            } else if (walletPojo.getErrNum() == 401) {
                Toast.makeText(mContext, mContext.getString(R.string.force_logout_msg), Toast.LENGTH_SHORT).show();
                Utility.sessionExpire(mContext);
            } else {
                presenterNotifier.showToastNotifier(walletPojo.getErrMsg(), Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            presenterNotifier.showToastNotifier(mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
        }
    }
    //====================================================================


    /**
     * <h2>rechargeWalletApi</h2>
     * <p>
     * method to do api call to recharge the wallet
     * </P>
     */
    public void rechargeWalletApi() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cardId", sessionMgr.getLastCard());
            jsonObject.put("amount", rechargeAmount);
            jsonObject.put("cardType", sessionMgr.getCardType());
            jsonObject.put("card", sessionMgr.getLastCard());
            OkHttp3Connection.doOkHttp3Connection(sessionMgr.getSessionToken(),ServiceUrl.RECHARGE_WALLET,
                    OkHttp3Connection.Request_type.POST, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                        @Override
                        public void onSuccess(String result) {
                            //Log.d(TAG, "rechargeWalletApi result: "+result);
                            rechargeWalletApisResponseHandler(result);
                        }

                        @Override
                        public void onError(String error) {
                            presenterNotifier.showToastNotifier(mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
                        }
                    },sessionMgr.getSessionToken());
        } catch (Exception e) {
            e.printStackTrace();
            presenterNotifier.showToastNotifier(mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
        }
    }
    //====================================================================

    /**
     * <h2>rechargeWalletApisResponseHandler</h2>
     * <p>
     * method to parse the data retrieved from rechargeWalletApi() call
     * and handle the respective case
     * <p>
     * </p>
     *
     * @param result: retrieved from rechargeWalletApi() call
     */
    private void rechargeWalletApisResponseHandler(final String result) {
        if (!result.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("errNum") == 200) {
                    Log.d(TAG, "rechargeWalletApi errNum == 200: ");
                    getWalletDetails();
                } else if (jsonObject.has("errNum") && (jsonObject.getInt("errNum") == 400)) {
                    presenterNotifier.showToastNotifier(jsonObject.getString("errMsg"), Toast.LENGTH_SHORT);
                } else if (jsonObject.has("errNum") && (jsonObject.getInt("errNum") == 401)) {
                    Toast.makeText(mContext, mContext.getString(R.string.force_logout_msg), Toast.LENGTH_SHORT).show();
                    sessionMgr.setIsLogin(false);
                    sessionMgr.setImageUrl("");
                    Intent intent = new Intent(mContext, SplashScreen.class);
                    mContext.startActivity(intent);
                    ((MainActivity) mContext).finish();
                } else {
                    Log.d(TAG, "rechargeWalletApi erro: ");
                    presenterNotifier.showToastNotifier(jsonObject.getString("errMsg"), Toast.LENGTH_SHORT);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                presenterNotifier.showToastNotifier(mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT);
            }
        }
    }
    //====================================================================

    /**
     * <h2>validateFieldsToRechargeWalletApi</h2>
     * <p>
     * method to validate the fields values to update
     * </p>
     *
     * @return returns true is the conditions are valid
     */
    public boolean validateFieldsToRechargeWalletApi() {
        if (rechargeAmount.isEmpty()) {
            presenterNotifier.showToastNotifier(mContext.getString(R.string.addAmountToAdd), Toast.LENGTH_SHORT);
            return false;
        } else {
            float temp = Float.parseFloat(rechargeAmount);

            if (temp <= 0) {
                presenterNotifier.showToastNotifier(mContext.getString(R.string.addAmountToAdd), Toast.LENGTH_SHORT);
                return false;
            }
        }

        if (sessionMgr.getLastCard().trim().isEmpty() || sessionMgr.getLastCardNumber().isEmpty()) {
            presenterNotifier.showToastNotifier(mContext.getString(R.string.cardDetailsNotFound), Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }
    //====================================================================

}
