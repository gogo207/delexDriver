package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by ads on 24/05/17.
 */

public class BookingInvoice implements Serializable {
    private double timeFare;
    private double distFare;
    private double contractPays;
    private double Discount;
    private double tollFare;
    private double baseFare;
    private double watingFee;
    private double total;
    private double cancelationFee;
    private String taxPercentage;
    private Boolean taxEnable=false;
    private String taxTitle;
    private String tax;
    private double VAT;


    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getWatingFee() {
        return watingFee;
    }

    public void setWatingFee(double watingFee) {
        this.watingFee = watingFee;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getCancelationFee() {
        return cancelationFee;
    }

    public void setCancelationFee(double cancelationFee) {
        this.cancelationFee = cancelationFee;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getTimeFare() {
        return timeFare;
    }

    public void setTimeFare(double timeFare) {
        this.timeFare = timeFare;
    }

    public double getDistFare() {
        return distFare;
    }

    public void setDistFare(double distFare) {
        this.distFare = distFare;
    }

    public double getContractPays() {
        return contractPays;
    }

    public void setContractPays(double contractPays) {
        this.contractPays = contractPays;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double discount) {
        Discount = discount;
    }

    public double getTollFare() {
        return tollFare;
    }

    public void setTollFare(double tollFare) {
        this.tollFare = tollFare;
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

    public Boolean getTaxEnable() {
        return taxEnable;
    }

    public void setTaxEnable(Boolean taxEnable) {
        this.taxEnable = taxEnable;
    }
}
