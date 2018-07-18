package com.delex.wallet.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.app.MainActivity;
import com.delex.driver.R;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.delex.wallet.adapter.Alerts;
import com.delex.wallet.event_holder.WalletDataChangedEvent;
import com.delex.wallet.interface_callbacks.WalletFragInteractor;
import com.delex.wallet.pojo_classes.WalletDataPojo;
import com.delex.wallet.presenter.WalletFragPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * <h1>WalletFragment</h1>
 * <p>
 *     Class to load Wallet Fragment and show wallet details
 * </p>
 * @since 25/09/17.
 */

public class WalletFragment extends Fragment
        implements View.OnClickListener, WalletFragInteractor.WalletFragViewNotifier
{
    private TextView tvWalletBalance, tvSoftLimitValue, tvHardLimitValue;
    private TextView tvCardNo, tvCurrencySymbol,tvalertMsg;


    private Alerts alerts;
    private ProgressDialog pDialog;
    private WalletFragPresenter walletFragPresenter;
    Typeface ClanaproNarrMedium;
    Typeface ClanaproNarrNews;
    Typeface ClanaproMedium;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    //====================================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ClanaproNarrMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ClanPro-NarrNews.otf");
        ClanaproMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CLANPRO-MEDIUM.OTF");

        sessionManager=new SessionManager(getActivity());

        initToolBar(view);
        initViews(view);
        initPayViews(view);
        initSoftHardLimitDescriptionsView(view);
        return view;
    }

    //====================================================================

    /** <h2>initToolBar</h2>
     * <p>
     *     method to initialize customer toolbar
     * </p>
      */
    private void initToolBar(View view)
    {
        ImageView ivMenuBtnToolBar = (ImageView) view.findViewById(R.id.ivMenuBtnToolBar);

        ivMenuBtnToolBar.setOnClickListener(this);
        TextView tvTitleToolbar = (TextView) view.findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setTypeface(ClanaproMedium);
        tvTitleToolbar.setText(getString(R.string.recentTransactions));
    }
    //====================================================================

    /**
     *<h2>initViews</h2>
     * <P>
     *  custom method to load top views of the screen
     * </P>
     * @param view: root view of the fragment
     */
    private void initViews(View view)
    {
        TextView tvCurCreditLabel = (TextView) view.findViewById(R.id.tvCurCreditLabel);
        tvCurCreditLabel.setTypeface(ClanaproNarrNews);

        tvWalletBalance = (TextView) view.findViewById(R.id.tvWalletBalance);
        tvWalletBalance.setTypeface(ClanaproNarrNews);
        tvalertMsg=(TextView)view.findViewById(R.id.tvalertMsg);
        tvalertMsg.setTypeface(ClanaproNarrNews);
        TextView tvSoftLimitLabel = (TextView) view.findViewById(R.id.tvSoftLimitLabel);
        tvSoftLimitLabel.setTypeface(ClanaproNarrNews);

        tvSoftLimitValue = (TextView) view.findViewById(R.id.tvSoftLimitValue);
        tvSoftLimitValue.setTypeface(ClanaproNarrNews);

        TextView tvHardLimitLabel = (TextView) view.findViewById(R.id.tvHardLimitLabel);
        tvHardLimitLabel.setTypeface(ClanaproNarrNews);

        tvHardLimitValue = (TextView) view.findViewById(R.id.tvHardLimitValue);
        tvHardLimitValue.setTypeface(ClanaproNarrNews);

        Button btnRecentTransactions = (Button) view.findViewById(R.id.btnRecentTransactions);
        btnRecentTransactions.setTypeface(ClanaproNarrNews);
        btnRecentTransactions.setOnClickListener(this);

        setValues();
    }
    //====================================================================

    /**
     *<h2>initPayViews</h2>
     * <P>
     *  custom method to load payment views of the screen
     * </P>
     * @param view: root view of the fragment
     */
    private void initPayViews(View view)
    {
        TextView tvPayUsingCardLabel = (TextView) view.findViewById(R.id.tvPayUsingCardLabel);
        tvPayUsingCardLabel.setTypeface(ClanaproMedium);

        tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
        tvCardNo.setTypeface(ClanaproNarrNews);
        tvCardNo.setOnClickListener(this);

        TextView tvPayAmountLabel = (TextView) view.findViewById(R.id.tvPayAmountLabel);
        tvPayAmountLabel.setTypeface(ClanaproMedium);

        tvCurrencySymbol = (TextView) view.findViewById(R.id.tvCurrencySymbol);
        tvCurrencySymbol.setTypeface(ClanaproNarrNews);


        TextView tvHardLimitLabel = (TextView) view.findViewById(R.id.tvHardLimitLabel);
        tvHardLimitLabel.setTypeface(ClanaproNarrNews);

        tvHardLimitValue = (TextView) view.findViewById(R.id.tvHardLimitValue);
        tvHardLimitValue.setTypeface(ClanaproNarrNews);

        EditText etPayAmountValue = (EditText) view.findViewById(R.id.etPayAmountValue);
        etPayAmountValue.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                walletFragPresenter.setRechargeAmount(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
    }
    //====================================================================

    /**
     *<h2>initSoftHardLimitDescriptionsView</h2>
     * <P>
     *  custom method to init soft and hard limit description views of the screen
     * </P>
     * @param view: root view of the fragment
     */
    private void initSoftHardLimitDescriptionsView(View view)
    {
        TextView tvSoftLimitMsgLabel = (TextView) view.findViewById(R.id.tvSoftLimitMsgLabel);
        tvSoftLimitMsgLabel.setTypeface(ClanaproNarrNews);

        TextView tvSoftLimitMsg = (TextView) view.findViewById(R.id.tvSoftLimitMsg);
        tvSoftLimitMsg.setTypeface(ClanaproNarrNews);
        tvSoftLimitMsg.setText(getString(R.string.softLimitMsg));

        TextView tvHardLimitMsgLabel = (TextView) view.findViewById(R.id.tvHardLimitMsgLabel);
        tvHardLimitMsgLabel.setTypeface(ClanaproNarrNews);

        TextView tvHardLimitMsg = (TextView) view.findViewById(R.id.tvHardLimitMsg);
        tvHardLimitMsg.setTypeface(ClanaproNarrNews);
        tvHardLimitMsg.setText(getString(R.string.hardLimitMsg));

        Button btnConfirmAndPay = (Button) view.findViewById(R.id.btnConfirmAndPay);
        btnConfirmAndPay.setTypeface(ClanaproNarrNews);
        btnConfirmAndPay.setOnClickListener(this);
    }
    //====================================================================

    @Override
    public void onResume()
    {
        super.onResume();
        if(alerts == null)
            alerts = new Alerts();

        if(walletFragPresenter == null)
            walletFragPresenter = new WalletFragPresenter(getActivity(), WalletFragment.this);

//        walletFragPresenter.initGetOrderDetails(); pk
        VariableConstant.isWalletFragActive = true;
        EventBus.getDefault().register(this);
    }
    //====================================================================

    /**
     * <p>
     *     to handle events from pubNub or push
     *     that wallet setting or data has modified
     * </p>
     * @param walletDataChangedEvent: retrieved wallet date from pubNub
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WalletDataChangedEvent walletDataChangedEvent)
    {
        walletFragPresenter.initGetOrderDetails();
    }
    //====================================================================


    @Override
    public void onClick(View view)
    {
        Utility.hideSoftKeyBoard(tvWalletBalance);
        switch (view.getId())
        {
            case R.id.ivMenuBtnToolBar:
                DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                ((MainActivity)getActivity()).moveDrawer(mDrawerLayout);

                break;

            case R.id.btnRecentTransactions:
                Intent intent = new Intent(getActivity(), WalletTransActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                break;

            case R.id.tvCardNo:
                Intent cardsIntent = new Intent(getActivity(), ChangeCardActivity.class);
                startActivityForResult(cardsIntent, 1);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay_activity);
                break;

            case R.id.btnConfirmAndPay:
                showRechargeConfirmationAlert(walletFragPresenter.getCurrencySymbol()
                        +" "+walletFragPresenter.getRechargeValue());
                break;
        }
    }
    //====================================================================


    private void hideProgressDialog()
    {
        if(pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }
    //====================================================================


    @Override
    public void showProgressDialog(String msg)
    {
        if(pDialog == null)
        {
            pDialog = new ProgressDialog(getActivity());
        }

        if(!pDialog.isShowing())
        {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }
    //====================================================================

    @Override
    public void showToast(String msg, int duration)
    {
        hideProgressDialog();
        Toast.makeText(getActivity(), msg, duration).show();
    }
    //====================================================================

    @Override
    public void showAlert(String title, String msg)
    {
        hideProgressDialog();
        Toast.makeText(getActivity(), "SHOW ALERT", Toast.LENGTH_SHORT).show();
    }
    //====================================================================

    @Override
    public void noInternetAlert()
    {
        alerts.showNetworkAlert(getActivity());
    }
    //====================================================================

    /**
     *<h2>walletDetailsApiSuccessViewNotifier</h2>
     * <p>
     *     method to update fields data on the success response of api
     * </p>
     * @param walletDataPojo: Response retrieved from the api call
     * @param currencySymbol: app currency symbol
     */
    @Override
    public void walletDetailsApiSuccessViewNotifier(WalletDataPojo walletDataPojo, String currencySymbol)
    {
        hideProgressDialog();
        if(walletDataPojo.isReachedHardLimit())
        {
            tvWalletBalance.setTextColor(getResources().getColor(R.color.red_light));
        }
        else if(walletDataPojo.isReachedSoftLimit())
        {
            tvWalletBalance.setTextColor(getResources().getColor(R.color.yellow_light));
        }
        else
        {
            tvWalletBalance.setTextColor(getResources().getColor(R.color.black));
        }
        sessionManager.setWalletAmount(walletDataPojo.getWalletAmount());
        if (Double.valueOf(sessionManager.getWalletAmount())>0){
            tvalertMsg.setText(getString(R.string.positive_wallet_alert)+getString(R.string.app_name)+sessionManager.getCurrencySymbol()+sessionManager.getWalletAmount());
        }else if (Double.valueOf(sessionManager.getWalletAmount())<0){
            tvalertMsg.setText(getString(R.string.app_name)+getString(R.string.app_name)+sessionManager.getCurrencySymbol()+sessionManager.getWalletAmount().substring(1));
        }
        tvWalletBalance.setText(currencySymbol+" "+walletDataPojo.getWalletAmount());
        tvSoftLimitValue.setText(currencySymbol+" -"+ Utility.getFormattedPrice(String.valueOf(walletDataPojo.getSoftLimit())));
        tvHardLimitValue.setText(currencySymbol+" -"+ Utility.getFormattedPrice(String.valueOf(walletDataPojo.getHardLimit())));


        Log.d("lastdigit",walletFragPresenter.getLastCardNo());
        tvCardNo.setText(getString(R.string.cardNoHidden)+walletFragPresenter.getLastCardNo());
        tvCurrencySymbol.setText(currencySymbol);
    }
    //====================================================================


    /**
     *<h2>walletDetailsApiErrorViewNotifier</h2>
     * <p>
     *     method to notify api error
     * </p>
     */
    @Override
    public void walletDetailsApiErrorViewNotifier(String error)
    {
        showToast(error, Toast.LENGTH_SHORT);
    }
    //====================================================================


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
    //====================================================================

    @Override
    public void onPause()
    {
        super.onPause();
        VariableConstant.isWalletFragActive = false;
        EventBus.getDefault().unregister(this);
    }
    //====================================================================

    /**
     * <h2>showRechargeConfirmationAlert</h2>
     * <p>
     *     method to show an alert dialog to take user
     *     confirmation to proceed to recharge
     * </p>
     */
    private void showRechargeConfirmationAlert(String amount)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getActivity().getString(R.string.confirmPayment));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getActivity().getString(R.string.paymentMsg1)
        + " "+ getActivity().getString(R.string.app_name) +" "+getActivity().getString(R.string.paymentMsg2)+" "+amount);

        alertDialog.setPositiveButton(getActivity().getString(
                R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                walletFragPresenter.initRechargeWalletApi();
                dialog.dismiss();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    //====================================================================


    public void setValues(){


        if (Double.valueOf(sessionManager.getWalletAmount())>0){
            tvalertMsg.setText(getString(R.string.positive_wallet_alert)+getString(R.string.app_name)+sessionManager.getCurrencySymbol()+sessionManager.getWalletAmount());
        }else if (Double.valueOf(sessionManager.getWalletAmount())<0){
            tvalertMsg.setText(getString(R.string.app_name)+getString(R.string.app_name)+sessionManager.getCurrencySymbol()+sessionManager.getWalletAmount().substring(1));
        }
        tvSoftLimitValue.setText(sessionManager.getCurrencySymbol()+" -"+ Utility.getFormattedPrice(String.valueOf(sessionManager.getSoftLimit())));
        tvHardLimitValue.setText(sessionManager.getCurrencySymbol()+" -"+ Utility.getFormattedPrice(String.valueOf(sessionManager.getHardLimit())));
        tvWalletBalance.setText(sessionManager.getCurrencySymbol()+" "+ Utility.getFormattedPrice(String.valueOf(sessionManager.getWalletAmount())));

    }
}
