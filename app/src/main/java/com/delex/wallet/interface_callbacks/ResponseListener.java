package com.delex.wallet.interface_callbacks;


import com.delex.pojo.SupportPojo;

/**
 * <h>ResponseListener</h>
 * Responce Listner has three Method which has their own
 * Created by ${Ali} on 8/19/2017.
 */

public interface ResponseListener
{

    void onSupportSuccess(SupportPojo support_pojo);
    void onError(String errormsg);

    interface CardResponseListener
    {
        void onSuccess();
        void onError(String errormsg);
        void onValidOfCard();
        void onInvalidOfCard();
    }
}
