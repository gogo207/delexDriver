package com.delex.app.bookingUpdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;


import com.delex.pojo.BookingInvoice;
import com.delex.utility.DistanceChangeListner;
import com.delex.utility.LocationUtil;
import com.delex.utility.PicassoMarker;
import com.delex.utility.SessionManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DELL on 03-10-2017.
 */

public interface BookingFlowMainMvp {

    interface ViewOperations{
        void onSuccess(String msg, BookingInvoice invoice);
        void onError(String error);
        void showProgressbar();
        void dismissProgressbar();
        void setDistanceAndTimer(String status);
        void setCurrentStatus(String status);
        void onPushReceived(String action,String msg);

    }

    interface PresenterOperations{

        //presenter-view operation
        void setLocationObj(Activity activity, LocationUtil.GetLocationListener listener);
        void setDistanceChangeListner(DistanceChangeListner distanceChangeListner);
        void updateBookingStatus(String value, String bid);
        void setCarMarker(final Location location, final PicassoMarker marker, GoogleMap map);
        void setCarMarker(LatLng latLng, PicassoMarker marker, GoogleMap map);
        void setCurrentStatus(String status);
        void onBrodcastRecieve(Context context, Intent intent,String bid);

        //presenter-model operation
        void onSuccess(String msg, BookingInvoice invoice);
        void onError(String error);
    }

    interface ModelOperations{
        void updateBookingStatus(String value, String bid,SessionManager manager);
        void setCarMarker(final Location location, final PicassoMarker marker, GoogleMap map);
        void setCarMarker(LatLng latLng, PicassoMarker marker, GoogleMap map);
    }
}
