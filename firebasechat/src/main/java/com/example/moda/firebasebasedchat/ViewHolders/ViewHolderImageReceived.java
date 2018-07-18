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
 * View holder for image received recycler view item
 */
public class ViewHolderImageReceived extends RecyclerView.ViewHolder {


    public TextView time, date, fnf,comma;

    public ImageView download;

    public  RingProgressBar progressBar;

    public ProgressBar progressBar2;


    public AdjustableImageView imageView;


    public ImageView cancel;

    public  ViewHolderImageReceived(View view) {
        super(view);
        comma = (TextView) view.findViewById(R.id.comma);

        date = (TextView) view.findViewById(R.id.date);
        imageView = (AdjustableImageView) view.findViewById(R.id.imgshow);

        time = (TextView) view.findViewById(R.id.ts);

        progressBar = (RingProgressBar) view.findViewById(R.id.progress);


        cancel = (ImageView) view.findViewById(R.id.cancel);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        download = (ImageView) view.findViewById(R.id.download);
        fnf = (TextView) view.findViewById(R.id.fnf);
    }
}
