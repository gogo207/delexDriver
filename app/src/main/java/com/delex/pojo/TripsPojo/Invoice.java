package com.delex.pojo.TripsPojo;

import java.io.Serializable;

/**
 * Created by Admin on 7/21/2017.
 */

public class Invoice implements Serializable {
    private String total;

    private String pgComm;

    private String goodType;

    private String quantity;
    private String baseFare;

    private String cancelationFee;

    private String appcom;

    private String distFare;

    private String watingFee;

    private String subtotal;

    private int handlingFee;

    private String timeFare;

    private String distance;

    private String time;

    private String tollFee;

    private String appliedAmount;

    private String appProfitLoss;

    private String masEarning;

    private long totalTime;
    private String Discount;
    private String taxPercentage;
    private Boolean taxEnable=false;
    private String taxTitle;
    private String taxValue;
    private String tax;
    private String watingtime;
    private  String VAT;

    public String getVAT() {
        return VAT;
    }

    public void setVAT(String VAT) {
        this.VAT = VAT;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPgComm() {
        return pgComm;
    }

    public void setPgComm(String pgComm) {
        this.pgComm = pgComm;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getCancelationFee() {
        return cancelationFee;
    }

    public void setCancelationFee(String cancelationFee) {
        this.cancelationFee = cancelationFee;
    }

    public String getAppcom() {
        return appcom;
    }

    public void setAppcom(String appcom) {
        this.appcom = appcom;
    }

    public String getDistFare() {
        return distFare;
    }

    public void setDistFare(String distFare) {
        this.distFare = distFare;
    }

    public String getWatingFee() {
        return watingFee;
    }

    public void setWatingFee(String watingFee) {
        this.watingFee = watingFee;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public int getHandlingFee() {
        return handlingFee;
    }

    public void setHandlingFee(int handlingFee) {
        this.handlingFee = handlingFee;
    }

    public String getTimeFare() {
        return timeFare;
    }

    public void setTimeFare(String timeFare) {
        this.timeFare = timeFare;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTollFee() {
        return tollFee;
    }

    public void setTollFee(String tollFee) {
        this.tollFee = tollFee;
    }

    public String getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(String appliedAmount) {
        this.appliedAmount = appliedAmount;
    }

    public String getAppProfitLoss() {
        return appProfitLoss;
    }

    public void setAppProfitLoss(String appProfitLoss) {
        this.appProfitLoss = appProfitLoss;
    }

    public String getMasEarning() {
        return masEarning;
    }

    public void setMasEarning(String masEarning) {
        this.masEarning = masEarning;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getWatingtime() {
        return watingtime;
    }

    public void setWatingtime(String watingtime) {
        this.watingtime = watingtime;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public Boolean getTaxEnable() {
        return taxEnable;
    }

    public void setTaxEnable(Boolean taxEnable) {
        this.taxEnable = taxEnable;
    }

    public String getTaxTitle() {
        return taxTitle;
    }

    public void setTaxTitle(String taxTitle) {
        this.taxTitle = taxTitle;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
