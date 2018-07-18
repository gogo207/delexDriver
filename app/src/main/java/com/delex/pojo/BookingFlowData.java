package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by ads on 24/05/17.
 */

public class BookingFlowData implements Serializable {

    private int status;
    private BookingInvoice invoice;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BookingInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(BookingInvoice invoice) {
        this.invoice = invoice;
    }
}
