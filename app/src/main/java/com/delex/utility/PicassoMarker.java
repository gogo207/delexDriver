package com.delex.utility;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by DELL on 19-09-2017.
 */

public class PicassoMarker implements Target
{
    Marker mMarker;
    public PicassoMarker(Marker marker) {
        mMarker = marker;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
    {
        try
        {
            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Marker getmMarker() {
        return mMarker;
    }

    public void setmMarker(Marker mMarker) {
        this.mMarker = mMarker;
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
