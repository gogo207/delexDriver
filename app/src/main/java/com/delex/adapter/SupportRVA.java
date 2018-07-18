package com.delex.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.driver.R;
import com.delex.pojo.SupportData;
import com.delex.support.SupportSubCategoryActivity;
import com.delex.support.WebViewActivity;

import java.util.ArrayList;

/**
 * Created by murashid on 25-Aug-17.
 */

public class SupportRVA extends RecyclerView.Adapter<SupportRVA.ViewHolder> implements View.OnClickListener {

    private ArrayList<SupportData> supportDatas;
    private boolean isMainCategory;
    private Activity mActivity;

    public SupportRVA(Activity mActivity, ArrayList<SupportData> supportDatas, boolean isMainCategory) {
        this.mActivity = mActivity;
        this.supportDatas = supportDatas;
        this.isMainCategory = isMainCategory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_support_text, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(supportDatas.get(position).getName());
        holder.tvName.setOnClickListener(this);
        holder.tvName.setTag(holder);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvName) {
            int adapterPosition = ((ViewHolder) v.getTag()).getAdapterPosition();
            if (isMainCategory) {
                if (supportDatas.get(adapterPosition).getSubcat().size() > 0) {
                    Intent intent = new Intent(mActivity, SupportSubCategoryActivity.class);
                    intent.putExtra("data", supportDatas.get(adapterPosition).getSubcat());
                    intent.putExtra("title", supportDatas.get(adapterPosition).getName());
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.nosubCategory), Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("URL", supportDatas.get(adapterPosition).getLink());
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
            }
        }
    }

    @Override
    public int getItemCount() {
        return supportDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

}
