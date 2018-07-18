package com.delex.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delex.history.HistoryInvoice;
import com.delex.driver.R;
import com.delex.pojo.TripsPojo.Appointments;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;

import java.util.ArrayList;

/**
 * Created by embed on 23/5/17.
 */

public class HistoryTripsRVA extends RecyclerView.Adapter<HistoryTripsRVA.ViewHolder> {

    private Context context;
    private View view;
    private ArrayList<Appointments> appointments;

    SessionManager sessionManager;
    private CardView cv_singlerow_job;


    /**********************************************************************************************/
    public HistoryTripsRVA(Context context, ArrayList<Appointments> appointments) {
        setHasStableIds(true);
        this.context = context;
        this.appointments = appointments;
    }

    /**********************************************************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_currentupcoming_job, parent, false);

        sessionManager=new SessionManager(context);
        return new HistoryTripsRVA.ViewHolder(view);
    }

    /**********************************************************************************************/
    @Override
    public void onBindViewHolder(HistoryTripsRVA.ViewHolder holder, final int position) {
        final int index = holder.getAdapterPosition();
        holder.setIsRecyclable(false);

        Utility.printLog("HistoryTripsRVA AdapterPosition "+index+"  Bid: "+appointments.get(index));
        cv_singlerow_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HistoryInvoice.class);
                intent.putExtra("data", appointments.get(index));
                context.startActivity(intent);
            }
        });

        holder.tv_job_id.setText(context.getResources().getString(R.string.id) + " " + appointments.get(position).getBid());
        holder.tv_pickup_loc.setText(appointments.get(position).getAddrLine1());
        holder.tv_drop_loc.setText(appointments.get(position).getDropLine1());
        holder.tv_deliveryfee.setText(sessionManager.getCurrencySymbol() + "" + appointments.get(position).getInvoice().getTotal());
        String date = Utility.DateFormatChange(appointments.get(position).getApntDt());
        holder.tv_date_time.setText(date);


        Utility.printLog("SetTripsTime :" + date);
    }

    /**********************************************************************************************/
    @Override
    public int getItemCount() {
        return appointments.size();
    }

    /**********************************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_job_id, tv_deliveryfee, tv_time_left_txt, tv_deliverytime, tv_pickup_loc, tv_drop_loc, tv_date_time;
        public ViewHolder(View itemView) {
            super(itemView);

            Typeface ClanaproNarrMedium = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrMedium.otf");
            Typeface ClanaproNarrNews = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrNews.otf");

            LinearLayout ll_timeleft = (LinearLayout) itemView.findViewById(R.id.ll_timeleft);
            ll_timeleft.setVisibility(View.GONE);

            tv_job_id = (TextView) itemView.findViewById(R.id.tv_job_id);
            tv_job_id.setTypeface(ClanaproNarrNews);

            tv_deliveryfee = (TextView) itemView.findViewById(R.id.tv_deliveryfee);
            tv_deliveryfee.setTypeface(ClanaproNarrMedium);

            tv_time_left_txt = (TextView) itemView.findViewById(R.id.tv_time_left_txt);
            tv_time_left_txt.setTypeface(ClanaproNarrNews);

            tv_deliverytime = (TextView) itemView.findViewById(R.id.tv_deliverytime);
            tv_deliverytime.setTypeface(ClanaproNarrMedium);

            tv_pickup_loc = (TextView) itemView.findViewById(R.id.tv_pickup_loc);
            tv_pickup_loc.setTypeface(ClanaproNarrNews);

            tv_drop_loc = (TextView) itemView.findViewById(R.id.tv_drop_loc);
            tv_drop_loc.setTypeface(ClanaproNarrNews);

            tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
            tv_date_time.setTypeface(ClanaproNarrNews);

            cv_singlerow_job = (CardView) itemView.findViewById(R.id.cv_singlerow_job);

        }
    }
}

