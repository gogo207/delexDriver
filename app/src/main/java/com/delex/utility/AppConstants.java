package com.delex.utility;

/**
 * Created by ads on 18/05/17.
 */

public enum AppConstants {

    getAptStatus_OnTheWay("6"),
    getAptStatus_Arrived("7"),
    getAptStatus_LoadedAndDelivery("8"),
    getAptStatus_reached_drop_loc("9"),
    getAptStatus_Unloaded("16"),
    getAptStatus_Completed("10");

    public String value;

    AppConstants(String value) {
        this.value = value;
    }

    public interface ACTION
    {
        String MAIN_ACTION = "com.dayrunner.foregroundservice.action.main";
        String STARTFOREGROUND_ACTION = "com.dayrunner.service.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.dayrunner.service.action.stopforeground";
        String PUSH_ACTION = "com.dayrunner.firebase.action";
    }

    public interface NOTIFICATION_ID
    {
        int FOREGROUND_SERVICE = 108;
    }
}
