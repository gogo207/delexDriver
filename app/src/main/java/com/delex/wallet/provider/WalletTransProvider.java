package com.delex.wallet.provider;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.delex.driver.R;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.wallet.interface_callbacks.WalletTransInteractor;
import com.delex.wallet.pojo_classes.WalletTransDataDetailsPojo;
import com.delex.wallet.pojo_classes.WalletTransPojo;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <h>WalletTransProvider</h>
 * <p>
 *     Activity to handle Wallet all wallet transactions list
 *     also contains view pager to show all, debits and credits transaction fragments
 * </p>
 * @since 18/09/17.
 */

public class WalletTransProvider
{
    private final String TAG = "WalletTransProvider";
    private String currency_symbol, bid;
    private int pageIndex = 0;
    //private final int pageLimit = 10;

    private Context mContext;
    private SessionManager sessionMgr;
    private WalletTransInteractor.WalletTransPresenterNotifier presenterNotifier;

    private WalletTransPojo walletTransPojo;
    private ArrayList<WalletTransDataDetailsPojo> allTransactionsAL;
    private ArrayList<WalletTransDataDetailsPojo> debitTransactionsAL;
    private ArrayList<WalletTransDataDetailsPojo> creditTransactionsAL;


    public WalletTransProvider(Context context, WalletTransInteractor.WalletTransPresenterNotifier _presenterNotifier)
    {
        mContext = context;
        this.presenterNotifier = _presenterNotifier;
        sessionMgr = new SessionManager(mContext);
        currency_symbol = sessionMgr.getCurrencySymbol();

        this.walletTransPojo = new WalletTransPojo();
        this.allTransactionsAL = new ArrayList<WalletTransDataDetailsPojo>();
        this.debitTransactionsAL = new ArrayList<WalletTransDataDetailsPojo>();
        this.creditTransactionsAL = new ArrayList<WalletTransDataDetailsPojo>();
    }
    //====================================================================


    public String getBid()
    {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public ArrayList<WalletTransDataDetailsPojo> getAllTransactionsAL()
    {
        return this.allTransactionsAL;
    }

    public ArrayList<WalletTransDataDetailsPojo> getDebitTransactionsAL()
    {
        return this.debitTransactionsAL;
    }

    public ArrayList<WalletTransDataDetailsPojo> getCreditTransactionsAL()
    {
        return this.creditTransactionsAL;
    }
    //====================================================================

    /**
     *<h2>getTransactionsHistory</h2>
     * <p>
     *     method to make api call to get list of transactions
     *     with load more option
     * </p>
     * @param isToLoadMore: true if is to load more transactions
     */
    public void getTransactionsHistory(final boolean isToLoadMore)
    {
        try
        {
            int pageIndexTemp = 0;
            if(isToLoadMore)
            {
                pageIndexTemp = pageIndex +1;
            }

            String url = ServiceUrl.WALLET_TRANSACTIONS+sessionMgr.getSid()+"/2/"+pageIndexTemp;
            //Log.d(TAG, "getTransactionsHistory  url: "+url);
            OkHttp3Connection.doOkHttp3Connection(url,
                    OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback()
                    {

                        @Override
                        public void onSuccess(final String result)
                        {
                            Log.d(TAG, "getTransactionsHistory onSuccess result: "+result);
                            getTransactionsHistoryHandler(result, isToLoadMore);
                        }

                        @Override
                        public void onError(String error)
                        {
                            Log.d(TAG, "getTransactionsHistory error: "+error);
                            presenterNotifier.walletTransactionsApiErrorNotifier(mContext.getString(R.string.something_went_wrong));
                        }
                    },sessionMgr.getSessionToken());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //====================================================================


    /**
     *<h2>getTransactionsHistoryHandler</h2>
     * <p>
     *     method to parse and handle the response of
     *     getTransactionsHistory() api call
     * </p>
     * @param response : retrieved response from getTransactionsHistoryHandler() api
     * @param isToLoadMore: true if is to load more transactions
     */
    private void getTransactionsHistoryHandler(String response, final boolean isToLoadMore)
    {
        try
        {
            if(response != null && !response.isEmpty())
            {
                walletTransPojo = new Gson().fromJson(response, WalletTransPojo.class);
                if(walletTransPojo.getErrNum() == 200)
                {
                    Log.d(TAG, "getTransactionsHistoryHandler isToLoadMore: "+isToLoadMore);
                    if(!isToLoadMore)
                    {
                        pageIndex = 0;
                        this.allTransactionsAL.clear();
                        this.debitTransactionsAL.clear();
                        this.creditTransactionsAL.clear();
                    }

                    Log.d(TAG, "getTransactionsHistoryHandler CreditDebitArr().size: "+walletTransPojo.getData().getCreditDebitArr().size());
                    if(walletTransPojo.getData().getCreditDebitArr() != null &&
                            walletTransPojo.getData().getCreditDebitArr().size() > 0)
                    {
                        if(isToLoadMore)
                        {
                            pageIndex ++;
                        }
                        this.allTransactionsAL.addAll(walletTransPojo.getData().getCreditDebitArr());
                    }

                    Log.d(TAG, "getTransactionsHistoryHandler DebitArr().size: "+walletTransPojo.getData().getDebitArr().size());
                    if(walletTransPojo.getData().getDebitArr() != null &&
                            walletTransPojo.getData().getDebitArr().size() > 0)
                    {
                        this.debitTransactionsAL.addAll(walletTransPojo.getData().getDebitArr());
                    }


                    Log.d(TAG, "getTransactionsHistoryHandler CreditArr().size: "+walletTransPojo.getData().getCreditArr().size());
                    if(walletTransPojo.getData().getCreditArr() != null &&
                            walletTransPojo.getData().getCreditArr().size() > 0)
                    {
                        this.creditTransactionsAL.addAll(walletTransPojo.getData().getCreditArr());
                    }

                    presenterNotifier.walletTransactionsApiSuccessNotifier();
                }
                else if(walletTransPojo.getErrNum() == 400)
                {
                    presenterNotifier.showToastNotifier(walletTransPojo.getErrMsg(), Toast.LENGTH_SHORT);
                }
                else if(walletTransPojo.getErrNum() == 401)
                {
                    Toast.makeText(mContext, mContext.getString(R.string.force_logout_msg), Toast.LENGTH_SHORT).show();
                    Utility.sessionExpire(mContext);
                }
                else
                {
                    presenterNotifier.walletTransactionsApiErrorNotifier(walletTransPojo.getErrMsg());
                }
            }
            else
            {
                presenterNotifier.walletTransactionsApiErrorNotifier(mContext.getString(R.string.something_went_wrong));
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            presenterNotifier.walletTransactionsApiErrorNotifier(mContext.getString(R.string.something_went_wrong));
        }
    }
    //====================================================================
}
