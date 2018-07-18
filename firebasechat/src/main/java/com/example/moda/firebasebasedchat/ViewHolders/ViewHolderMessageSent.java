package com.example.moda.firebasebasedchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moda.firebasebasedchat.R;

/**
 * Created by moda on 19/06/17.
 */

public class ViewHolderMessageSent extends RecyclerView.ViewHolder {


    public TextView message, time, date,comma;

    public ImageView singleTick, clock;

    public  ViewHolderMessageSent(View view) {
        super(view);
        comma = (TextView) view.findViewById(R.id.comma);
        date = (TextView) view.findViewById(R.id.date);


        message = (TextView) view.findViewById(R.id.txtMsg);

        time = (TextView) view.findViewById(R.id.ts);
        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);


        clock = (ImageView) view.findViewById(R.id.clock);

    }
}
