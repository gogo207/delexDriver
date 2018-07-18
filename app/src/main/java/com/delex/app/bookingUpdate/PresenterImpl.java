package com.delex.app.bookingUpdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.delex.pojo.BookingInvoice;
import com.delex.service.LocationUpdateService;
import com.delex.utility.AppConstants;
import com.delex.utility.DistanceChangeListner;
import com.delex.utility.LocationUtil;
import com.delex.utility.PicassoMarker;
import com.delex.utility.SessionManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DELL on 03-10-2017.
 */

public class PresenterImpl implements BookingFlowMainMvp.PresenterOperations {

    BookingFlowMainMvp.ViewOperations viewImpl;
    private SessionManager sessionManager;
    private BookingFlowModel model=new BookingFlowModel(this);

    public PresenterImpl(BookingFlowMainMvp.ViewOperations viewImpl, Context context) {
        this.viewImpl = viewImpl;
        sessionManager=new SessionManager(context);
    }

    @Override
    public void setLocationObj(Activity activity, LocationUtil.GetLocationListener listener) {
        LocationUtil locationUtil=new LocationUtil(activity,listener);
    }

    @Override
    public void setDistanceChangeListner(DistanceChangeListner distanceChangeListner) {
        LocationUpdateService.setDistanceChangeListner(distanceChangeListner);
    }

    @Override
    public void updateBookingStatus(String value, String bid) {
        if (value.equals("10")) {
            viewImpl.setCurrentStatus(value);
        }else {
            model.updateBookingStatus(value,bid,sessionManager);
            viewImpl.showProgressbar();
        }

    }

    @Override
    public void setCarMarker(Location location, PicassoMarker marker, GoogleMap map) {
        model.setCarMarker(location,marker,map);
    }

    @Override
    public void setCarMarker(LatLng latLng, PicassoMarker marker, GoogleMap map) {
        model.setCarMarker(latLng,marker,map);
    }

    @Override
    public void setCurrentStatus(String status) {
        viewImpl.setCurrentStatus(status);
    }

    @Override
    public void onBrodcastRecieve(Context context, Intent intent,String bid) {
        if(intent.getAction().equals(AppConstants.ACTION.PUSH_ACTION)){
            Bundle bundle=intent.getExtras();
            String action = null, _bid, msg = null;
            if(bundle.containsKey("action")){
                action=bundle.getString("action");
            }
            if(bundle.containsKey("message")){
                msg=bundle.getString("message");
            }
            if(action!=null && action.equals("3")){
                _bid=bundle.getString("bid");
                if(_bid.equals(bid))
                    viewImpl.onPushReceived(action,msg);
            }
        }
    }

    @Override
    public void onSuccess(String status, BookingInvoice invoice) {
        viewImpl.onSuccess(status,invoice);
        viewImpl.dismissProgressbar();
        viewImpl.setCurrentStatus(status);
        viewImpl.setDistanceAndTimer(status);
    }

    @Override
    public void onError(String error) {
        viewImpl.onError(error);
        viewImpl.dismissProgressbar();
    }
}
