package com.delex.app.bookingRequest;

import android.widget.ProgressBar;

import com.delex.utility.SessionManager;

/**
 * Created by DELL on 26-09-2017.
 */

public class Presenter implements BookingPopUpMainMVP.PresenterOperations {

    BookingPopUpMainMVP.ViewOperations viewImpl;
    BookingPopUpModel bookingPopUpModel=new BookingPopUpModel(this);

    public Presenter(BookingPopUpMainMVP.ViewOperations viewImpl) {
        this.viewImpl=viewImpl;
    }

    @Override
    public void updateApptRequest(String status, String bid, SessionManager manager) {
        bookingPopUpModel.updateApptRequest(status,bid,manager);
        viewImpl.showProgressbar();
    }

    @Override
    public void startTimer(long time, ProgressBar progressBar) {
        bookingPopUpModel.startTimer(time,progressBar);
    }

    @Override
    public void onRequestUpdated(String success) {
        viewImpl.dismissProgressbar();
        viewImpl.onSuccess(success);

    }

    @Override
    public void onUpdateRequestFail(String error) {
        viewImpl.dismissProgressbar();
        viewImpl.onError(error);
    }

    @Override
    public void onTimerChanged(String time) {
        viewImpl.onTimerChanged(time);
    }

    @Override
    public void onFinish() {
        viewImpl.onFinish();
    }
}
