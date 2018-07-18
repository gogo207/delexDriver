package com.delex.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delex.app.bookingUpdate.BookingFlow;
import com.delex.driver.R;
import com.delex.pojo.AssignedAppointments;
import com.delex.service.LocationUpdateService;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**************************************************************************************************/

/**
 * Created by vibin on 31/3/17.
 */

public class CurrentUpcomingJobRVA extends RecyclerView.Adapter<CurrentUpcomingJobRVA.ViewHolder> {

    SimpleDateFormat simpleDateFormat;
    Date lastModifiedDate;
    Date currentDate;
    private Context context;
    private View view;
    private String from;

    // private HomeCurrentJobFrag currentFragment;
    private ArrayList<AssignedAppointments> mData;
    private SessionManager sessionManager;

    /**********************************************************************************************/
    public CurrentUpcomingJobRVA(Context context, String from, ArrayList<AssignedAppointments> mData) {
        this.context = context;
        this.from = from;
        // this.currentFragment = currentFragment;
        this.mData = mData;
        sessionManager = new SessionManager(context);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    /**********************************************************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_currentupcoming_job, parent, false);

        return new ViewHolder(view);
    }

    /**********************************************************************************************/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int index = holder.getAdapterPosition();
        if (from.matches("CURRENT")) {
            try {
                if (mData.get(position).getStatusCode().equals("6")) {
                    holder.cv_singlerow_job.setCardBackgroundColor(context.getResources().getColor(R.color.notstarted));
                    lastModifiedDate = simpleDateFormat.parse(mData.get(position).getApntDate());
                    holder.tv_deliveryfee.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_job_id.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_time_left_txt.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_drop_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_pickup_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_time_left_txt.setText(context.getResources().getString(R.string.time_left));
                    holder.tv_date_time.setText(Utility.DateFormatChange(mData.get(position).getApntDate(), 0));
                } else if (mData.get(position).getStatusCode().equals("7")){

                    holder.cv_singlerow_job.setCardBackgroundColor(context.getResources().getColor(R.color.started));
                    holder.tv_deliveryfee.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_time_left_txt.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_drop_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_job_id.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_pickup_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    lastModifiedDate = simpleDateFormat.parse(mData.get(position).getDrop_dt());
                    holder.tv_time_left_txt.setText(context.getResources().getString(R.string.time_left_drop));
                    holder.tv_date_time.setText(Utility.DateFormatChange(mData.get(position).getDrop_dt(), 0));
                }
                else if (mData.get(position).getStatusCode().equals("8") || mData.get(position).getStatusCode().equals("9")){
                    holder.cv_singlerow_job.setCardBackgroundColor(context.getResources().getColor(R.color.notdropped));
                    holder.tv_deliveryfee.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_job_id.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_time_left_txt.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_drop_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_pickup_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    lastModifiedDate = simpleDateFormat.parse(mData.get(position).getDrop_dt());
                    holder.tv_time_left_txt.setText(context.getResources().getString(R.string.time_left_drop));
                    holder.tv_date_time.setText(Utility.DateFormatChange(mData.get(position).getDrop_dt(), 0));
                }

                if (mData.get(position).getStatusCode().equals("16") ){
                    holder.cv_singlerow_job.setCardBackgroundColor(context.getResources().getColor(R.color.unload));
                    holder.tv_deliveryfee.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_time_left_txt.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_drop_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_job_id.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_pickup_loc.setTextColor(context.getResources().getColor(R.color.white));
                    holder.tv_date_time.setTextColor(context.getResources().getColor(R.color.white));
                    lastModifiedDate = simpleDateFormat.parse(mData.get(position).getDrop_dt());
                    holder.tv_time_left_txt.setText(context.getResources().getString(R.string.time_left_drop));
                    holder.tv_date_time.setText(Utility.DateFormatChange(mData.get(position).getDrop_dt(), 0));
                }
                currentDate = simpleDateFormat.parse(Utility.date());

                Utility.printLog("dateeeee  :" + lastModifiedDate);
                Utility.printLog("dateeeee  :" + currentDate);

                DateTimeDifference(currentDate, lastModifiedDate, holder);


            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.cv_singlerow_job.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!Utility.isMyServiceRunning(LocationUpdateService.class,((Activity)context) )){
                        Intent intent = new Intent(context,LocationUpdateService.class);
                        context.startService(intent);
                    }

                    Intent intent = new Intent(context, BookingFlow.class);
                    Utility.printLog("printing : " + mData.get(index).toString());
                    intent.putExtra("data", mData.get(index));
                    context.startActivity(intent);

                }
            });
        }

        Utility.printLog("Booking id is : " + mData.get(position).getBid());
        holder.tv_job_id.setText(context.getString(R.string.id) + mData.get(position).getBid());
        holder.tv_pickup_loc.setText(mData.get(position).getAddrLine1());
        holder.tv_drop_loc.setText(mData.get(position).getDropLine1());
        holder.tv_deliveryfee.setText(/*context.getString(R.string.currency)*/sessionManager.getcurrencySymbol() + " " + mData.get(position).getShipemntDetails().get(0).getFare());

    }

    /**********************************************************************************************/
    @Override
    public int getItemCount() {
        return mData.size();
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public void DateTimeDifference(Date startDate, Date endDate, final ViewHolder holder) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        Utility.printLog("dateee days :" + elapsedDays + " hours :" + elapsedHours + " minutes :" + elapsedMinutes + " elapsedSeconds :" + elapsedSeconds);

        if (elapsedSeconds >= 0) {
            Utility.printLog("dateeeee  min + :" + elapsedMinutes);
            if (elapsedDays > 0) {
                holder.tv_deliverytime.setText(elapsedDays + "Days, " + elapsedHours + "Hrs, " + elapsedMinutes + "Min");
            } else if (elapsedHours > 0) {
                holder.tv_deliverytime.setText(elapsedHours + "Hrs, " + elapsedMinutes + "Min");
            } else if (elapsedMinutes > 0) {
                holder.tv_deliverytime.setText(elapsedMinutes + "Min");
            }
        } else {
            Utility.printLog("dateeeee  min: - " + elapsedMinutes);
            holder.tv_deliverytime.setText(context.getResources().getString(R.string.now));
        }

    }

    /**********************************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_singlerow_job;
        private TextView tv_job_id, tv_deliveryfee, tv_time_left_txt, tv_deliverytime, tv_pickup_loc, tv_drop_loc, tv_date_time;

        public ViewHolder(View itemView) {
            super(itemView);

            Typeface ClanaproNarrMedium = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrMedium.otf");
            Typeface ClanaproNarrNews = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrNews.otf");

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
