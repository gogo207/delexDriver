package com.delex.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ads on 15/05/17.
 */

public class AssignedAppointments implements Serializable {
    private String customerName;
    private  String customerCountryCode;
    private String dropLine1;
    private String status;
    private String vehicleImg;
    private String booking_time;
    private String customerPhone;
    private String customerChn;
    private String customerEmail;
    private String customerId;
    private String extraNotes;
    private String drop_ltg;
    private String statusCode;
    private String addrLine1;
    private String drop_dt;
    private String DriverChn;
    private String apntDt;
    private String email;
    private String dorpzoneId;
    private String vehicleType;
    private String bid;
    private boolean paidByReceiver;
    private String helpers;
    private ArrayList<ShipmentDetails> shipemntDetails;
    private String pickup_ltg;
    private String apntDate;
    private int pricingType;
    private BookingInvoice invoice;

    public String getCustomerCountryCode() {
        return customerCountryCode;
    }

    public void setCustomerCountryCode(String customerCountryCode) {
        this.customerCountryCode = customerCountryCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isPaidByReceiver() {
        return paidByReceiver;
    }

    public void setPaidByReceiver(boolean paidByReceiver) {
        this.paidByReceiver = paidByReceiver;
    }

    public String getHelpers() {
        return helpers;
    }

    public void setHelpers(String helpers) {
        this.helpers = helpers;
    }

    public String getCustomerChn() {
        return customerChn;
    }

    public void setCustomerChn(String customerChn) {
        this.customerChn = customerChn;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDropLine1() {
        return dropLine1;
    }

    public void setDropLine1(String dropLine1) {
        this.dropLine1 = dropLine1;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleImg() {
        return vehicleImg;
    }

    public void setVehicleImg(String vehicleImg) {
        this.vehicleImg = vehicleImg;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getExtraNotes() {
        return extraNotes;
    }

    public void setExtraNotes(String extraNotes) {
        this.extraNotes = extraNotes;
    }

    public String getDrop_ltg() {
        return drop_ltg;
    }

    public void setDrop_ltg(String drop_ltg) {
        this.drop_ltg = drop_ltg;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getAddrLine1() {
        return addrLine1;
    }

    public void setAddrLine1(String addrLine1) {
        this.addrLine1 = addrLine1;
    }

    public String getDrop_dt() {
        return drop_dt;
    }

    public void setDrop_dt(String drop_dt) {
        this.drop_dt = drop_dt;
    }

    public String getDriverChn() {
        return DriverChn;
    }

    public void setDriverChn(String DriverChn) {
        this.DriverChn = DriverChn;
    }

    public String getApntDt() {
        return apntDt;
    }

    public void setApntDt(String apntDt) {
        this.apntDt = apntDt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDorpzoneId() {
        return dorpzoneId;
    }

    public void setDorpzoneId(String dorpzoneId) {
        this.dorpzoneId = dorpzoneId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public ArrayList<ShipmentDetails> getShipemntDetails() {
        return shipemntDetails;
    }

    public void setShipemntDetails(ArrayList<ShipmentDetails> shipemntDetails) {
        this.shipemntDetails = shipemntDetails;
    }

    public String getPickup_ltg() {
        return pickup_ltg;
    }

    public void setPickup_ltg(String pickup_ltg) {
        this.pickup_ltg = pickup_ltg;
    }

    public String getApntDate() {
        return apntDate;
    }

    public void setApntDate(String apntDate) {
        this.apntDate = apntDate;
    }

    public BookingInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(BookingInvoice invoice) {
        this.invoice = invoice;
    }

    public int getPricingType() {
        return pricingType;
    }

    public void setPricingType(int pricingType) {
        this.pricingType = pricingType;
    }
}
