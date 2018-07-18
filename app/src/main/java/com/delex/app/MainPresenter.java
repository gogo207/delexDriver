package com.delex.app;

import com.delex.pojo.AssignedTripsPojo;
import com.delex.utility.ProgressIndicator;

/**
 * Created by ads on 13/05/17.
 */

public interface MainPresenter extends ProgressIndicator {


    void updatedStatus(int status);

    void apiError(String message, int flag);

    void updateAppointments(AssignedTripsPojo tripsPojo);
}
