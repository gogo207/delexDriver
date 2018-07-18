package com.delex.bankDetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.adapter.BankDetailsRVA;
import com.delex.driver.R;
import com.delex.pojo.bank.BankList;
import com.delex.pojo.bank.LegalEntity;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import java.util.ArrayList;

/**
 * Created by Admin on 8/5/2017.
 */


public class BankListFrag extends Fragment implements BankListFragPresenter.BankListFragPresenterImplement, BankDetailsRVA.RefreshBankDetails, View.OnClickListener {

    SessionManager sessionManager;

    private ProgressDialog pDialog;
    private TextView tvStep2,tvStep1,tvStatus, tvStipeAccountNo, tvAddBankAccount,tvAddStripeAccount;
    private CardView cvStipeDetails,cvLinkBankAcc;
    private BankDetailsRVA bankDetailsRVA;
    private ArrayList<BankList> bankLists;
    private BankListFragPresenter bankListFragPresenter;
    private Bundle bundleBankDetails;
    private ImageView ivStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bank_list_fragment2, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        bundleBankDetails = new Bundle();
        sessionManager = new SessionManager(getActivity());
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ClanPro-NarrNews.otf");

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);


        tvStatus = (TextView) rootView.findViewById(R.id.tvStatus);
        tvStep2 = (TextView) rootView.findViewById(R.id.tvStep2);
        tvStep1 = (TextView) rootView.findViewById(R.id.tvStep1);
        ivStatus = (ImageView) rootView.findViewById(R.id.ivStatus);
        tvStipeAccountNo = (TextView) rootView.findViewById(R.id.tvStipeAccountNo);
        tvAddStripeAccount = (TextView) rootView.findViewById(R.id.tvAddStripeAccount);
        tvAddBankAccount = (TextView) rootView.findViewById(R.id.tvAddBankAccount);
        cvStipeDetails = (CardView) rootView.findViewById(R.id.cvStipeDetails);
        cvLinkBankAcc = (CardView) rootView.findViewById(R.id.cvLinkBankAcc);



        tvStep2.setTypeface(typeface);
        tvStep1.setTypeface(typeface);
        tvStatus.setTypeface(typeface);
        tvStipeAccountNo.setTypeface(typeface);
        tvAddBankAccount.setTypeface(typeface);
        tvAddStripeAccount.setTypeface(typeface);

        RecyclerView rvBank = (RecyclerView) rootView.findViewById(R.id.rvBank);
        rvBank.setLayoutManager(new LinearLayoutManager(getActivity()));
        bankLists = new ArrayList<>();
        bankDetailsRVA = new BankDetailsRVA(getActivity(), bankLists, getFragmentManager(), this);
        rvBank.setAdapter(bankDetailsRVA);

        bankListFragPresenter = new BankListFragPresenter(this);

        tvAddStripeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BankNewStripeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.bottom_to_top, R.anim.stay);
            }
        });

        cvStipeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BankNewStripeActivity.class);
                intent.putExtras(bundleBankDetails);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.bottom_to_top, R.anim.stay);
            }
        });

        tvAddBankAccount.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
       Utility.printLog("BankListFrag onResume...");
        bankListFragPresenter.getBankDetails(sessionManager.getSessionToken());
    }

    @Override
    public void startProgressBar() {
        pDialog.show();
    }

    @Override
    public void stopProgressBar() {
        pDialog.dismiss();
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure() {
        Toast.makeText(getActivity(), getString(R.string.serverError), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(LegalEntity legalEntity, ArrayList<BankList> bankLists) {
       VariableConstant.IS_STRIPE_ADDED = true;
        tvAddStripeAccount.setVisibility(View.GONE);

        tvStipeAccountNo.setText("XXXXX");
        tvStatus.setText(legalEntity.getVerification().getStatus());

        if (legalEntity.getVerification().getStatus().equals("verified")) {
            ivStatus.setImageResource(R.drawable.verified);
            tvStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            tvAddBankAccount.setFocusable(true);
            tvAddBankAccount.setOnClickListener(this);

            cvStipeDetails.setOnClickListener(null);

            if(bankLists.size()>0){
                cvLinkBankAcc.setVisibility(View.GONE);
            }

        } else {
            ivStatus.setImageResource(R.drawable.unverified);
            tvStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            tvAddBankAccount.setFocusable(false);
            tvAddBankAccount.setOnClickListener(null);
            tvAddBankAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_corner_bank_acc_gunsmoke));
            tvAddBankAccount.setTextColor(getResources().getColor(R.color.gunsmoke));
        }

        this.bankLists.clear();
        this.bankLists.addAll(bankLists);
        bankDetailsRVA.notifyDataSetChanged();

        bundleBankDetails.putString("fname", legalEntity.getFirstName());
        bundleBankDetails.putString("lname", legalEntity.getLastName());
        bundleBankDetails.putString("country", legalEntity.getAddress().getCountry());
        bundleBankDetails.putString("state", legalEntity.getAddress().getState());
        bundleBankDetails.putString("city", legalEntity.getAddress().getCity());
        bundleBankDetails.putString("address", legalEntity.getAddress().getLine1());
        bundleBankDetails.putString("postalcode", legalEntity.getAddress().getPostalCode());
        bundleBankDetails.putString("month", legalEntity.getDob().getMonth());
        bundleBankDetails.putString("day", legalEntity.getDob().getDay());
        bundleBankDetails.putString("year", legalEntity.getDob().getYear());
    }

    @Override
    public void showAddStipe() {
        tvAddStripeAccount.setVisibility(View.VISIBLE);
       tvStatus.setVisibility(View.GONE);
        ivStatus.setVisibility(View.GONE);
        tvStipeAccountNo.setVisibility(View.GONE);
        tvAddBankAccount.setOnClickListener(null);
       tvAddBankAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_corner_bank_acc_gunsmoke));
        tvAddBankAccount.setTextColor(getResources().getColor(R.color.gunsmoke));
    }

    @Override
    public void onRefresh() {
        bankListFragPresenter.getBankDetails(sessionManager.getSessionToken());
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tvAddBankAccount){
            Intent intent = new Intent(getActivity(), BankNewAccountActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.bottom_to_top, R.anim.stay);
        }
    }
}
