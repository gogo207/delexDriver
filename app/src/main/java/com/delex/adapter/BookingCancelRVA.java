package com.delex.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delex.app.CancelReason;
import com.delex.driver.R;

import java.util.ArrayList;

/**
 * Created by embed on 14/4/17.
 */

public class BookingCancelRVA extends RecyclerView.Adapter<BookingCancelRVA.ViewHolder> {

    ComentsCancel comentsCancel;
    private Context context;
    private View view;
    private ArrayList<String> str_cancel_reasons = new ArrayList<>();
    private TextView last_select_txt;

    /**********************************************************************************************/
    public BookingCancelRVA(Context context, ComentsCancel comentsCancel) {
        this.context = context;
        this.comentsCancel = comentsCancel;

        str_cancel_reasons = ((CancelReason) context).tv_cancel_reasons;
    }

    /**********************************************************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_cancel_reason, parent, false);
        return new BookingCancelRVA.ViewHolder(view);

    }

    /**********************************************************************************************/
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.tv_cancel_reason.setText(str_cancel_reasons.get(position));
        holder.tv_cancel_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (last_select_txt != null) {
                    last_select_txt.setTextColor(context.getResources().getColor(R.color.color2));
                }
                holder.tv_cancel_reason.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                comentsCancel.oncommment(holder.tv_cancel_reason.getText().toString());

                last_select_txt = holder.tv_cancel_reason;
            }
        });

    }

    /**********************************************************************************************/
    @Override
    public int getItemCount() {
        return str_cancel_reasons.size();
    }

    /**
     * interface for cancel , if select any reason then the comment will appear
     */
    public interface ComentsCancel {
        void oncommment(String reason);
    }

    /**********************************************************************************************/

    /**********************************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_cancel_reason;

        public ViewHolder(View itemView) {
            super(itemView);

            Typeface ClanaproNarrNews = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrNews.otf");

            tv_cancel_reason = (TextView) itemView.findViewById(R.id.tv_cancel_reason);
            tv_cancel_reason.setTypeface(ClanaproNarrNews);

        }
    }


}
