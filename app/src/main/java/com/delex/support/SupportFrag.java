package com.delex.support;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.delex.adapter.SupportRVA;
import com.delex.driver.R;
import com.delex.pojo.SupportData;

import java.util.ArrayList;

/**
 * Created by Admin on 8/3/2017.
 */

public class SupportFrag extends Fragment implements SupportFragPresenter.SupportFragPresenterImplement {
    private ProgressDialog pDialog;
    private String TAG = "SupportFrag";
    private ArrayList<SupportData> supportDatas;
    private SupportRVA supportRVA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.support_fragment, container, false);

        initLayout(rootView);
        SupportFragPresenter supportPresenter = new SupportFragPresenter(this);
        supportPresenter.getSupport();

        return rootView;
    }

    public void initLayout(View rootView) {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        supportDatas = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rvSupport);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        supportRVA = new SupportRVA(getActivity(), supportDatas, true);
        recyclerView.setAdapter(supportRVA);
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
    public void getSupportDetails(ArrayList<SupportData> supportDatas) {
        this.supportDatas.addAll(supportDatas);
        supportRVA.notifyDataSetChanged();
    }

}
