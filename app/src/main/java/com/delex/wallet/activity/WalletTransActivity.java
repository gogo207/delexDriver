package com.delex.wallet.activity;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.driver.R;
import com.delex.wallet.adapter.Alerts;
import com.delex.wallet.adapter.WalletViewPagerAdapter;
import com.delex.wallet.interface_callbacks.WalletTransInteractor;
import com.delex.wallet.presenter.WalletTransPresenter;

/**
 * <h1>WalletFragment</h1>
 * <p>
 *     Class to load WalletTransActivity and show all transactions list
 * </p>
 * @since 25/09/17.
 */
public class WalletTransActivity extends AppCompatActivity implements WalletTransInteractor.WalletTransViewNotifier
{
    private final String TAG = "WalletTransactionAct";
    private WalletTransPresenter walletTransPresenter;
    private WalletTransactionsList allTransactionsFrag, debitsFrag, creditsFrag;

    private Alerts alerts;
    private ProgressDialog pDialog;
    Typeface ClanaproNarrMedium;
    Typeface ClanaproNarrNews;
    Typeface ClanaproMedium;


    public WalletTransActivity()
    {
        // Required empty public constructor
    }
    //====================================================================


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_transactions);
        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        ClanaproMedium = Typeface.createFromAsset(getAssets(), "fonts/CLANPRO-MEDIUM.OTF");

        alerts = new Alerts();
        walletTransPresenter =  new WalletTransPresenter(this, this);
        initToolBar();
        initViews();
    }
    //====================================================================


    /* <h2>initToolBar</h2>
       * <p>
       *     method to initialize customer toolbar
       * </p>
       */
    private void initToolBar()
    {
        if(getActionBar() != null)
        {
            getActionBar().hide();
        }
        Toolbar toolBar = (Toolbar) findViewById(R.id.mToolBarCustom);
        setSupportActionBar(toolBar);


        RelativeLayout rlABarBackBtn = (RelativeLayout) findViewById(R.id.rlABarBackBtn);
        rlABarBackBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        TextView tvAbarTitle = (TextView) findViewById(R.id.tvAbarTitle);
        tvAbarTitle.setTypeface(ClanaproMedium);
        tvAbarTitle.setText(getString(R.string.recentTransactions));
    }
    //====================================================================

    /**
     *<h2>initViews</h2>
     * <P>
     *  custom method to initializes all the views of the screen
     * </P>
     */
    private void initViews()
    {



        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        String tabTitles[]  = new String[]{getString(R.string.all), getString(R.string.debit), getString(R.string.credit)};
        WalletViewPagerAdapter viewPagerAdapter = new WalletViewPagerAdapter(getSupportFragmentManager());

        //====== init all no of frags list
        this.allTransactionsFrag = WalletTransactionsList.getNewInstance(walletTransPresenter.getCurrencySymbol());
        viewPagerAdapter.addFragment(allTransactionsFrag, tabTitles[0]);

        this.debitsFrag = WalletTransactionsList.getNewInstance(walletTransPresenter.getCurrencySymbol());
        viewPagerAdapter.addFragment(debitsFrag, tabTitles[1]);

        this.creditsFrag = WalletTransactionsList.getNewInstance(walletTransPresenter.getCurrencySymbol());
        viewPagerAdapter.addFragment(creditsFrag, tabTitles[2]);
        viewPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(viewPagerAdapter);
    }
    //====================================================================

    @Override
    public void onResume()
    {
        super.onResume();
        loadTransactions(false, false);
    }
    //====================================================================

    /**
     * <h2>loadTransactions</h2>
     * <p>
     *     method to init getTransactionsList api
     * </p>
     * @param isToLoadMore: true if is to load more
     */
    public void loadTransactions(boolean isToLoadMore, boolean isFromOnRefresh)
    {
        walletTransPresenter.initLoadTransactions(isToLoadMore, isFromOnRefresh);
    }
    //====================================================================

    /**
     *<h2>hideProgressDialog</h2>
     * <p>
     *     method to hide progress dialog if already visible
     * </p>
     */
    private void hideProgressDialog()
    {
        if(pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }
    //====================================================================

    /**
     *<h2>showProgressDialog</h2>
     * <p>
     *     method to show progress dialog if already not visible
     * </p>
     * @param msg: message to be displayed along with the progress dialog
     */
    @Override
    public void showProgressDialog(String msg)
    {
        if(pDialog == null)
        {
            pDialog = new ProgressDialog(this);
        }

        if(!pDialog.isShowing())
        {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage(getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }
    //====================================================================
    /**
     *<h2>showToast</h2>
     * <p>
     *     method to show toast message
     * </p>
     * @param msg: message to be displayed along with the Toast
     * @param duration: returns the toast visibility duration
     */
    @Override
    public void showToast(String msg, int duration)
    {
        hideProgressDialog();
        Toast.makeText(this, msg, duration).show();
    }
    //====================================================================

    /**
     *<h2>showAlert</h2>
     * <p>
     *     method to show toast message
     * </p>
     * @param title: contains the title to set for the alert dialog
     * @param msg: message to be displayed along with the Toast
     */
    @Override
    public void showAlert(String title, String msg)
    {
        hideProgressDialog();
    }
    //====================================================================

    /**
     *<h2>showAlert</h2>
     * <p>
     *     method to show alert that these is no
     *     internet connectivity there
     * </p>
     */
    @Override
    public void noInternetAlert()
    {
        alerts.showNetworkAlert(this);
    }
    //====================================================================


    /**
     *<h2>walletTransactionsApiSuccessViewNotifier</h2>
     * <p>
     *     method to update fields data on the success response of api
     * </p>
     */
    @Override
    public void walletTransactionsApiSuccessViewNotifier()
    {
        hideProgressDialog();
        Log.d(TAG, "walletTransactionsApiSuccessViewNotifier onSuccess: ");

        if(this.allTransactionsFrag != null)
        {
            this.allTransactionsFrag.hideRefreshingLayout();
            this.allTransactionsFrag.notifyDataSetChangedCustom(walletTransPresenter.getAllTransactions());
        }

        if(this.debitsFrag != null)
        {
            this.debitsFrag.hideRefreshingLayout();
            this.debitsFrag.notifyDataSetChangedCustom(walletTransPresenter.getAllDebitTransactions());
        }

        if(this.creditsFrag != null)
        {
            this.creditsFrag.hideRefreshingLayout();
            this.creditsFrag.notifyDataSetChangedCustom(walletTransPresenter.getAllCreditTransactions());
        }
    }
    //====================================================================

    /**
     *<h2>walletDetailsApiErrorViewNotifier</h2>
     * <p>
     *     method to notify api error
     * </p>
     */
    @Override
    public void walletTransactionsApiErrorViewNotifier(String error)
    {
        Log.d(TAG, "walletTransactionsApiErrorViewNotifier  error: "+error);
        if(this.allTransactionsFrag != null)
        {
            this.allTransactionsFrag.hideRefreshingLayout();
        }

        if(this.debitsFrag != null)
        {
            this.debitsFrag.hideRefreshingLayout();
        }

        if(this.creditsFrag != null)
        {
            this.creditsFrag.hideRefreshingLayout();
        }
    }
    //====================================================================


    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    //====================================================================
}
