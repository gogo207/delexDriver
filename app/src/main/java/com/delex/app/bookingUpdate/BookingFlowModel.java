package com.delex.app.bookingUpdate;

import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.delex.pojo.BookingFlowPojo;
import com.delex.pojo.BookingInvoice;
import com.delex.utility.AppConstants;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.PicassoMarker;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class BookingFlowModel implements BookingFlowMainMvp.ModelOperations
{
    private BookingFlowMainMvp.PresenterOperations presenterOperations;
    private BookingInvoice invoice;
    private Location mCurrentLoc,mPreviousLoc;


    public BookingFlowModel(BookingFlowMainMvp.PresenterOperations presenterOperations) {
        this.presenterOperations = presenterOperations;
    }

    @Override
    public void updateBookingStatus(final String aptStatus, String bid, SessionManager sessionManager) {

            JSONObject reqObject = new JSONObject();
//            tv_status_text.setVisibility(View.VISIBLE);


            String distance=null;
            if(aptStatus.equals(AppConstants.getAptStatus_Arrived.value)||aptStatus.equals(AppConstants.getAptStatus_reached_drop_loc.value)){

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(sessionManager.getBookings());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        if (jsonObject.get("bid").equals(bid)) {
                            distance=jsonObject.getString("distance");
                        }
                    }
                    Utility.printLog("BookingFlowModel "+sessionManager.getBookings());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                reqObject.put("ent_status", aptStatus);
                reqObject.put("ent_booking_id", bid);
                reqObject.put("lat", sessionManager.getDriverCurrentLat());
                reqObject.put("long", sessionManager.getDriverCurrentLng());
                reqObject.put("distance",distance);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.UPDATE_BOOKING_STATUS, OkHttp3Connection.Request_type.PUT,
                    reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("Booking Flow", " result " + result);
                            if (result != null) {
                                BookingFlowPojo pojo = new Gson().fromJson(result, BookingFlowPojo.class);
                                switch (pojo.getErrFlag()) {
                                    case 0:

//                                        seekBar.setProgress(0);
//                                        appointment.setStatusCode(aptStatus);
                                        if (pojo.getData().getInvoice() != null) {
                                            invoice = pojo.getData().getInvoice();
                                        }
                                        presenterOperations.onSuccess(aptStatus,invoice );
//                                        setDistanceAndTimer(aptStatus);
//                                        setCurrentStatus(aptStatus);

                                        break;
                                    default:
                                        presenterOperations.onError(pojo.getErrMsg());
//                                        Toast.makeText(BookingFlow.this, pojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                                        break;
                                }

                            }

                        }

                        @Override
                        public void onError(String error) {
//                            Toast.makeText(BookingFlow.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                            Log.d("Booking Flow", " error " + error);

                        }
                    }, sessionManager.getSessionToken());

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void setCarMarker(final Location location, final PicassoMarker marker, GoogleMap map)
    {
        mCurrentLoc=location;

        if(mPreviousLoc==null)
        {
            mPreviousLoc=location;
        }

        final float bearing = mPreviousLoc.bearingTo(mCurrentLoc);
        if(marker!=null)
        {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = map.getProjection();
            Point startPoint = proj.toScreenLocation(new LatLng(mPreviousLoc.getLatitude(),mPreviousLoc.getLongitude()));
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * mCurrentLoc.getLongitude() + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * mCurrentLoc.getLatitude() + (1 - t)
                            * startLatLng.latitude;
                    marker.getmMarker().setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                    marker.getmMarker().setAnchor(0.5f, 0.5f);
                    marker.getmMarker().setRotation(bearing);
                    marker.getmMarker().setFlat(true);


                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }

        mPreviousLoc=mCurrentLoc;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void setCarMarker(LatLng latLng,PicassoMarker marker,GoogleMap map)
    {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        map.getUiSettings().setZoomControlsEnabled(false);

        if(marker!=null)
            marker.getmMarker().setPosition(latLng);
    }

}
