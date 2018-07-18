package com.delex.wallet.model;

import android.util.Log;

import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.wallet.interface_callbacks.ResponseListener;
import com.delex.wallet.pojo_classes.GetCard_pojo;
import com.google.gson.Gson;
import com.stripe.android.model.Card;

import org.json.JSONObject;

/**
 * <h>CardPaymentModel</h>
 * this model is to check the validity of the card and call api
 * Created by ${Ali} on 8/19/2017.
 */

public class CardPaymentModel
{
    private ResponseListener.CardResponseListener resListnr;

    /**
     * <h2>addCardService</h2>
     * This method is used to call the add card API
     * @param jsonObject parameters to send to API
     * @param manager shared pref object
     * @param resListnr interface to listen the response type
     */
    public void addCardService(JSONObject jsonObject, SessionManager manager,
                               final ResponseListener.CardResponseListener resListnr)
    {
        Log.d("TAG", "addCardService: "+jsonObject);

        this.resListnr = resListnr;
        OkHttp3Connection.doOkHttp3Connection(manager.getSessionToken(),ServiceUrl.ADDCARD, OkHttp3Connection.Request_type.PUT, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result)
            {
                Log.d("TAGCARD", "onSuccess: "+result);
                handleAddCardResponse(result);
            }

            @Override
            public void onError(String error) {

                resListnr.onError(error);
            }
        },manager.getSessionToken());
    }

    /**
     * |<h2>handleAddCardResponse</h2>
     * @param result response from add card API
     */
    private void handleAddCardResponse(String result)
    {
        try {
            Gson gson = new Gson();
            GetCard_pojo response = gson.fromJson(result, GetCard_pojo.class);
            if(response.getErrNum()==200 && response.getErrFlag()==0)
            {
                resListnr.onSuccess();
            }
            else
            {
                resListnr.onError(response.getErrMsg());
                Log.d("efgt",response.getErrMsg());
            }
        }
        catch (Exception e) {

            resListnr.onError(e+"");
            Log.d("efgt",e+"");
        }
    }

    /**
     * <h2>validateCardDetails</h2>
     * This method is used to validate the card details
     * @param card Card details
     */
    public void validateCardDetails(Card card,final ResponseListener.CardResponseListener
            responseListener)
    {
        if(card==null)
            responseListener.onInvalidOfCard();
        else
            responseListener.onValidOfCard();
    }
}
