package com.delex.wallet.pojo_classes;

import java.io.Serializable;

/**
 * @since  19/09/17.
 */

public class WalletTransDataDetailsPojo
        implements Serializable
{
    /*{
        "txnId":"WAL-1505457166-643.4764675744467",
            "trigger":"TRIP",
            "txnType":"DEBIT",
            "comment":"InvoiceACtivity payment by customer",
            "currency":"usd",
            "openingBal":0,
            "amount":-35.35,
            "closingBal":0,
            "paymentType":"CARD",
            "timestamp":1505457166,
            "paymentTxnId":"ch_1B2DIl2876tVKl2MfY4uLX3I",
            "intiatedBy":"N/A",
            "tripId":2450
    },*/

    private String txnId, trigger, txnType, comment, currency;
    private String openingBal, amount, closingBal,timestamp, tripId;
    private String paymentType, paymentTxnId, intiatedBy;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOpeningBal() {
        return openingBal;
    }

    public void setOpeningBal(String openingBal) {
        this.openingBal = openingBal;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getClosingBal() {
        return closingBal;
    }

    public void setClosingBal(String closingBal) {
        this.closingBal = closingBal;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentTxnId() {
        return paymentTxnId;
    }

    public void setPaymentTxnId(String paymentTxnId) {
        this.paymentTxnId = paymentTxnId;
    }

    public String getIntiatedBy() {
        return intiatedBy;
    }

    public void setIntiatedBy(String intiatedBy) {
        this.intiatedBy = intiatedBy;
    }

    @Override
    public String toString() {
        return "WalletTransDataDetailsPojo{" +
                "txnId='" + txnId + '\'' +
                ", trigger='" + trigger + '\'' +
                ", txnType='" + txnType + '\'' +
                ", comment='" + comment + '\'' +
                ", currency='" + currency + '\'' +
                ", openingBal='" + openingBal + '\'' +
                ", amount='" + amount + '\'' +
                ", closingBal='" + closingBal + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", tripId='" + tripId + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", paymentTxnId='" + paymentTxnId + '\'' +
                ", intiatedBy='" + intiatedBy + '\'' +
                '}';
    }
}
