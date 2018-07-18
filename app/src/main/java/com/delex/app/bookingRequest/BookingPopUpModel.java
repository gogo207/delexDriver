package com.delex.app.bookingRequest;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DELL on 26-09-2017.
 */

public class BookingPopUpModel implements BookingPopUpMainMVP.ModelOperations {

    private final String TAG = getClass().getName();
    private BookingPopUpMainMVP.PresenterOperations presenterImpl;
    private CountDownTimer countDownTimer;

    public BookingPopUpModel(BookingPopUpMainMVP.PresenterOperations presenterImpl) {
        this.presenterImpl = presenterImpl;
    }

    @Override
    public void updateApptRequest(final String status, final String bid, final SessionManager manager) {

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("ent_booking_id", bid);
                jsonObject.put("ent_status", status);
                jsonObject.put("lat", manager.getDriverCurrentLat());
                jsonObject.put("long", manager.getDriverCurrentLng());

            } catch (JsonIOException | JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(manager.getSessionToken(), ServiceUrl.RESPOND_TO_BOOKING, OkHttp3Connection.Request_type.POST, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    if (result != null) {
                        Log.d(TAG, "Booking Activity getAppointmentDetails result =  " + result);
                       /* if(!status.equals("3"))
                            manager.setLastBookingId(Long.parseLong(bid));*/
                        ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                        switch (validatorPojo.getErrFlag()) {
                            case 0:
                                presenterImpl.onRequestUpdated(validatorPojo.getErrMsg());
                                break;
                            case 1:
                                presenterImpl.onUpdateRequestFail(validatorPojo.getErrMsg());
                                break;
                        }

                    } else {
                        presenterImpl.onUpdateRequestFail("");
                    }

                    cancelCoutdownTimer();
                }

                @Override
                public void onError(String error) {

                    Log.d(TAG, "Booking Activity  Error" + error);
                    cancelCoutdownTimer();
                }
            }, manager.getSessionToken());

    }

    @Override
    public void startTimer(long time, final ProgressBar progressBar) {
        progressBar.setProgress((int) time);

        final long finalTime = time;
        countDownTimer = new CountDownTimer(finalTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);

                int res = (int) (((float) seconds / finalTime) * 100);
                Log.i(TAG, "leftTimeInMilliseconds: " + seconds + "    final time " + finalTime + "    bar value " + res);
                progressBar.setProgress(res);
                presenterImpl.onTimerChanged(String.format("%02d", seconds % 60));

            }

            public void onFinish() {

                progressBar.setProgress(0);
                presenterImpl.onTimerChanged("00");
                cancelCoutdownTimer();

            }
        }.start();


    }

    private void cancelCoutdownTimer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        presenterImpl.onFinish();
    }

}
