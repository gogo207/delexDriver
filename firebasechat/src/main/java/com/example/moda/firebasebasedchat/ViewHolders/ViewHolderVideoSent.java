package com.example.moda.firebasebasedchat.ViewHolders;
/*
 * Created by moda on 02/04/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.Utilities.AdjustableImageView;


/**
 * View holder for video sent recycler view item
 */
public class ViewHolderVideoSent extends RecyclerView.ViewHolder {


    public TextView time, date, fnf, comma;

    public ImageView singleTick, clock;


    public AdjustableImageView thumbnail;

    public ViewHolderVideoSent(View view) {
        super(view);

        comma = (TextView) view.findViewById(R.id.comma);
        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);


        date = (TextView) view.findViewById(R.id.date);
        clock = (ImageView) view.findViewById(R.id.clock);
        thumbnail = (AdjustableImageView) view.findViewById(R.id.vidshow);


        fnf = (TextView) view.findViewById(R.id.fnf);
    }
}
