package com.delex.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 7/20/2017.
 */

public class ConfigData implements Serializable {

    private String driverApiInterval;

    private String onJobInterval;

    private String currency;

    private boolean stripeEnabled=true;

    private String driverAcceptTime;

    private String DistanceForLogingLatLongs;

    private String mileage_metric;

    private String laterBookingTimeInterval;

    private String currencySymbol;

    private String presenceTime;

    private String tripStartedInterval;

    private String appVersion;

    private boolean mandatory;

    private String softLimit;

    private String reachedSoftLimit;

    private String reachedHardLimit;

    private String hardLimit;

    private String walletAmount;

    private boolean enableWallet;
    private String stripeKey;

    private ArrayList<String> pushTopics;

    public ArrayList<String> getPushTopics() {
        return pushTopics;
    }

    public void setPushTopics(ArrayList<String> pushTopics) {
        this.pushTopics = pushTopics;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDriverApiInterval() {
        return driverApiInterval;
    }

    public void setDriverApiInterval(String driverApiInterval) {
        this.driverApiInterval = driverApiInterval;
    }

    public String getOnJobInterval() {
        return onJobInterval;
    }

    public void setOnJobInterval(String onJobInterval) {
        this.onJobInterval = onJobInterval;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDriverAcceptTime() {
        return driverAcceptTime;
    }

    public void setDriverAcceptTime(String driverAcceptTime) {
        this.driverAcceptTime = driverAcceptTime;
    }

    public String getDistanceForLogingLatLongs() {
        return DistanceForLogingLatLongs;
    }

    public void setDistanceForLogingLatLongs(String distanceForLogingLatLongs) {
        DistanceForLogingLatLongs = distanceForLogingLatLongs;
    }

    public String getMileage_metric() {
        return mileage_metric;
    }

    public void setMileage_metric(String mileage_metric) {
        this.mileage_metric = mileage_metric;
    }

    public String getLaterBookingTimeInterval() {
        return laterBookingTimeInterval;
    }

    public void setLaterBookingTimeInterval(String laterBookingTimeInterval) {
        this.laterBookingTimeInterval = laterBookingTimeInterval;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getPresenceTime() {
        return presenceTime;
    }

    public void setPresenceTime(String presenceTime) {
        this.presenceTime = presenceTime;
    }

    public String getTripStartedInterval() {
        return tripStartedInterval;
    }

    public void setTripStartedInterval(String tripStartedInterval) {
        this.tripStartedInterval = tripStartedInterval;
    }

    public String getSoftLimit() {
        return softLimit;
    }

    public void setSoftLimit(String softLimit) {
        this.softLimit = softLimit;
    }

    public String getReachedSoftLimit() {
        return reachedSoftLimit;
    }

    public void setReachedSoftLimit(String reachedSoftLimit) {
        this.reachedSoftLimit = reachedSoftLimit;
    }

    public String getReachedHardLimit() {
        return reachedHardLimit;
    }

    public void setReachedHardLimit(String reachedHardLimit) {
        this.reachedHardLimit = reachedHardLimit;
    }

    public String getHardLimit() {
        return hardLimit;
    }

    public void setHardLimit(String hardLimit) {
        this.hardLimit = hardLimit;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public boolean isEnableWallet() {
        return enableWallet;
    }

    public void setEnableWallet(boolean enableWallet) {
        this.enableWallet = enableWallet;
    }

    public String getStripeKey() {
        return stripeKey;
    }

    public void setStripeKey(String stripeKey) {
        this.stripeKey = stripeKey;
    }

    public boolean isStripeEnabled() {
        return stripeEnabled;
    }

    public void setStripeEnabled(boolean stripeEnabled) {
        this.stripeEnabled = stripeEnabled;
    }
}
