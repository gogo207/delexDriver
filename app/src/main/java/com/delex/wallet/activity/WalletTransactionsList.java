package com.delex.wallet.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.delex.driver.R;
import com.delex.wallet.adapter.WalletTransactionsAdapter;
import com.delex.wallet.pojo_classes.WalletTransDataDetailsPojo;

import java.util.ArrayList;

/**
 * <h1>WalletTransactionsList</h1>
 * <p>
 *     Fragment to show wallet all transactions list
 *     according to fragment instances for the view pager of WalletTransActivity
 * </p>
 * A simple {@link Fragment} subclass.
 */
public class WalletTransactionsList extends Fragment
{
    private static final String ARG_CURRENCY_SYMBOL = "param2";

    private ArrayList<WalletTransDataDetailsPojo> transactionsAL;
    private String currencySymbol = "";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvTransactions;
    private WalletTransactionsAdapter walletTransactionsRVA;
    private LinearLayout llNoTransactions;
    private WalletTransActivity walletTransActivity;


    public WalletTransactionsList()
    {
    }
    //====================================================================


    /**
     * <h2>getNewInstance</h2>
     * <p>
     *     method to return the instance of this fragment
     * </p>
     * @param currencySymbol: Currency symbol of the app
     * @return WalletTransactionsList: Instance of the fragment
     */
    public static WalletTransactionsList getNewInstance(String currencySymbol)
    {
        WalletTransactionsList walletTransactionsList = new WalletTransactionsList();

        Bundle args = new Bundle();
        args.putString(ARG_CURRENCY_SYMBOL, currencySymbol);
        walletTransactionsList.setArguments(args);
        return walletTransactionsList;
    }
    //====================================================================

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        walletTransActivity = (WalletTransActivity)getActivity();
        transactionsAL = new ArrayList<WalletTransDataDetailsPojo>();

        if (getArguments() != null)
        {
            currencySymbol = getArguments().getString(ARG_CURRENCY_SYMBOL);
        }
    }
    //====================================================================


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet_tansactions_list, container, false);

        initViews(view);
        return view;
    }
    //====================================================================


    /**
     * <h2>initViews</h2>
     * <p>
     *     method to notify adapter or update views if the
     *     transactions list size changed
     * </p>
     * @param view: reference of root view of this fragment
     */
    private void initViews(View view)
    {
        llNoTransactions = (LinearLayout) view.findViewById(R.id.llNoTransactions);

        rvTransactions = (RecyclerView) view.findViewById(R.id.rvTransactions);
        rvTransactions.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvTransactions.setLayoutManager(llm);
        walletTransactionsRVA = new WalletTransactionsAdapter(getActivity(), transactionsAL, currencySymbol);
        rvTransactions.setAdapter(walletTransactionsRVA);

        // Pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                walletTransActivity.loadTransactions(false, true);
            }
        });

      rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener()
      {
          @Override
          public void onScrollStateChanged(RecyclerView recyclerView, int newState)
          {
              super.onScrollStateChanged(recyclerView, newState);
              LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

              //Request for the next 10 posts if the user reaches (total - 5) posts or the end currently loaded items
              int viewPosition = manager.findLastVisibleItemPosition();
              int itemCount = manager.getItemCount();
              if (itemCount > 0 && viewPosition == itemCount - 2)
              {
                  walletTransActivity.loadTransactions(false, false);
              }
          }
      });
        updateView();
    }
    //====================================================================


    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("test", "onResume : ");
    }
    //====================================================================

    /**
     * <h2>notifyDataSetChangedCustom</h2>
     * <p>
     *     method to notify adapter or update views if the
     *     transactions list size changed
     * </p>
     */
    public void notifyDataSetChangedCustom(ArrayList<WalletTransDataDetailsPojo> _transactionsAL)
    {
        transactionsAL.clear();
        transactionsAL.addAll(_transactionsAL);
        updateView();
    }
    //====================================================================

    /**
     * <h2>hideRefreshingLayout</h2>
     * <p>
     *     method to hide refresh layout
     * </p>
     */
    public void hideRefreshingLayout()
    {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }
    //====================================================================

    /**
     * <h2>updateView</h2>
     * <p>
     *     method to show or hide the list and notItems views
     *     according to the size of the list
     * </p>
     */
    private void updateView()
    {
        if(transactionsAL.size() > 0)
        {
            llNoTransactions.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
        else
        {
            rvTransactions.setVisibility(View.GONE);
            llNoTransactions.setVisibility(View.VISIBLE);
        }
        walletTransactionsRVA.notifyDataSetChanged();
    }
    //====================================================================

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }
    //====================================================================

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
    //====================================================================
}
