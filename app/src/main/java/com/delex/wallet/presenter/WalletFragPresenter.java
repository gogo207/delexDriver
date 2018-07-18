package com.delex.wallet.presenter;

import android.content.Context;

import com.delex.driver.R;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.delex.wallet.interface_callbacks.WalletFragInteractor;
import com.delex.wallet.pojo_classes.WalletDataPojo;
import com.delex.wallet.provider.WalletDetailsProvider;


/**
 * <h>WalletFragPresenter</h>
 * <p>
 *     Presenter class for Wallet Fragment
 * </p>
 * @since 27/09/17.
 */

public class WalletFragPresenter implements WalletFragInteractor.WalletFragPresenterNotifier
{
    private Context mContext;
    private WalletDetailsProvider walletDetailsProvider;
    private WalletFragInteractor.WalletFragViewNotifier viewUpdater;

    public WalletFragPresenter(Context context, WalletFragInteractor.WalletFragViewNotifier ui_notifier)
    {
        this.mContext = context;
        this.viewUpdater = ui_notifier;
        this.walletDetailsProvider = new WalletDetailsProvider(mContext, this);
    }
    //====================================================================

    public void setRechargeAmount(String walletAmount)
    {
        walletDetailsProvider.setRechargeAmount(walletAmount);
    }

    public String getCurrencySymbol() {
        return walletDetailsProvider.getCurrency_symbol();
    }

    public String getRechargeValue() {
        return walletDetailsProvider.getRechargeAmount();
    }

    /**
     * <h2>getLastCardNo</h2>
     * <p>
     *     method to get the last 4 digit of the last stored card no
     * </p>
     * @return String:last 4 digit of card
     */
    public String getLastCardNo()
    {
        return walletDetailsProvider.getLastCard_No();
    }

    //====================================================================

    /**
     * <h2>initGetOrderDetails</h2>
     * <p>
     *  method to init the getOrderDetails api()
     *  if network connectivity is available
     * </p>
     */
    public void initGetOrderDetails()
    {
        if( Utility.isNetworkAvailable(mContext))
        {
            VariableConstant.isWalletUpdateCalled = true;
            viewUpdater.showProgressDialog(mContext.getString(R.string.wait));
            walletDetailsProvider.getWalletDetails();
        }
        else
        {
            viewUpdater.noInternetAlert();
        }
    }
    //====================================================================

    /**
     *<h2>initRechargeWalletApi</h2>
     * <p>
     * This method is used to initialize the recharge the wallet
     * api call if network connectivity is available
     * </p>
     */
    public void initRechargeWalletApi()
    {
        if(Utility.isNetworkAvailable(mContext))
        {
            if(walletDetailsProvider.validateFieldsToRechargeWalletApi() )
            {
                viewUpdater.showProgressDialog(mContext.getString(R.string.wait));
                walletDetailsProvider.rechargeWalletApi();
            }
        }
        else
        {
            viewUpdater.noInternetAlert();
        }
    }
    //====================================================================

    @Override
    public void showToastNotifier(String msg, int duration)
    {
        viewUpdater.showToast(msg, duration);
    }

    @Override
    public void showAlertNotifier(String title, String msg)
    {

    }

    /**
     * <h2>walletDetailsApiErrorNotifier</h2>
     * <p>
     *     method to trigger walletDetailsApiErrorViewNotifier() interface
     *     on walletDetailsApi() error
     * </p>
     * @param errorMsg
     */
    @Override
    public void walletDetailsApiErrorNotifier(String errorMsg)
    {
        viewUpdater.walletDetailsApiErrorViewNotifier(errorMsg);
    }


    /**
     * <h2>walletDetailsApiSuccessNotifier</h2>
     * <p>
     *     method to trigger walletDetailsApiSuccessViewNotifier() interface
     *     on walletDetailsApi() success response to update the views
     * </p>
     */
    @Override
    public void walletDetailsApiSuccessNotifier(WalletDataPojo walletDataPojo, String currencySymbol)
    {
        viewUpdater.walletDetailsApiSuccessViewNotifier(walletDataPojo, currencySymbol);
        VariableConstant.isWalletUpdateCalled = false;
    }
    //====================================================================

}
