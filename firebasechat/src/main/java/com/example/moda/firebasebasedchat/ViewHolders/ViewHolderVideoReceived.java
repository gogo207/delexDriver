package com.example.moda.firebasebasedchat.ViewHolders;
/*
 * Created by moda on 02/04/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.Utilities.RingProgressBar;
import com.example.moda.firebasebasedchat.Utilities.AdjustableImageView;


/**
 * View holder for video received recycler view item
 */
public class ViewHolderVideoReceived extends RecyclerView.ViewHolder {

    public TextView time, date, fnf,comma;


    public ImageView download;


    public ProgressBar progressBar2;

    public RingProgressBar progressBar;

    public AdjustableImageView thumbnail;


    public ImageView cancel;


    public ViewHolderVideoReceived(View view) {
        super(view);
        date = (TextView) view.findViewById(R.id.date);

        comma = (TextView) view.findViewById(R.id.comma);
        time = (TextView) view.findViewById(R.id.ts);
        thumbnail = (AdjustableImageView) view.findViewById(R.id.vidshow);


        cancel = (ImageView) view.findViewById(R.id.cancel);

        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        progressBar = (RingProgressBar) view.findViewById(R.id.progress);


        download = (ImageView) view.findViewById(R.id.download);
        fnf = (TextView) view.findViewById(R.id.fnf);

    }
}
