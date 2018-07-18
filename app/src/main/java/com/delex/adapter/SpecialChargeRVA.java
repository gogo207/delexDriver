package com.delex.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delex.driver.R;

/**
 * Created by embed on 19/4/17.
 */

public class SpecialChargeRVA extends RecyclerView.Adapter<SpecialChargeRVA.ViewHolder> {

    private Context context;
    private View view;

    /**********************************************************************************************/
    public SpecialChargeRVA(Context context) {

        this.context = context;
    }

    /**********************************************************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_addspecial_charges, parent, false);
        return new ViewHolder(view);
    }

    /**********************************************************************************************/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    /**********************************************************************************************/
    @Override
    public int getItemCount() {
        return 2;
    }

    /**********************************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
