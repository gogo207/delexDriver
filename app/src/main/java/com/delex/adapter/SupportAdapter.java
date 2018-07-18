package com.delex.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.pojo.SupportData;
import com.delex.pojo.SupportSubCat;
import com.delex.support.WebViewActivity;

import java.util.ArrayList;

/**
 * Created by Admin on 8/3/2017.
 */

public class SupportAdapter extends RecyclerView.Adapter {
    private ArrayList data;
    private Activity context;
    private String type;

    public SupportAdapter(ArrayList data, Activity context, String type) {
        this.data = data;
        this.context = context;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewsd = LayoutInflater.from(context).inflate(R.layout.single_row_support_text, parent, false);
        SupportAdapter.MyViewHolder myViewHolder = new SupportAdapter.MyViewHolder(viewsd);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SupportAdapter.MyViewHolder) {
            final SupportAdapter.MyViewHolder viewHolder = (SupportAdapter.MyViewHolder) holder;

            final Object object = data.get(position);

            if (object instanceof SupportData) {
                final SupportData supportData = ((SupportData) data.get(position));
                viewHolder.tvName.setText(supportData.getName());
                viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.d("LINK ",supportData.getLink());
                        if (((SupportData) object).getSubcat().size() > 0) {
                            data.clear();
                            data.addAll(((SupportData) object).getSubcat());
                            notifyDataSetChanged();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("URL", supportData.getLink());
                            Intent intent = new Intent(context, WebViewActivity.class);
                            intent.putExtras(bundle);
                            context.startActivity(intent);

                        }
                    }
                });
            } else if (object instanceof SupportSubCat) {
                final SupportSubCat supportSubCat = ((SupportSubCat) data.get(position));
                viewHolder.tvName.setText(supportSubCat.getName());
                viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("URL", supportSubCat.getLink());
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtras(bundle);
                        intent.putExtra("URL", ((SupportSubCat) data.get(position)).getLink());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
