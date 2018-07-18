package com.delex.app.bookingRequest;

import android.widget.ProgressBar;

import com.delex.utility.SessionManager;

/**
 * Created by DELL on 21-09-2017.
 */

public interface BookingPopUpMainMVP {

    interface ViewOperations{
        void onSuccess(String msg);
        void onError(String error);
        void showProgressbar();
        void dismissProgressbar();
        void onTimerChanged(String time);
        void onFinish();
    }
    interface PresenterOperations{

        //presenter -view operations
        void updateApptRequest(String status, String bid, SessionManager manager);
        void startTimer(long time, ProgressBar progressBar);

        //presenter-model operations
        void onRequestUpdated(String msg);
        void onUpdateRequestFail(String error);
        void onTimerChanged(String time);
        void onFinish();

    }
    interface ModelOperations{
        void updateApptRequest(String status, String bid, SessionManager manager);
        void startTimer(long time, ProgressBar progressBar);
    }

}

