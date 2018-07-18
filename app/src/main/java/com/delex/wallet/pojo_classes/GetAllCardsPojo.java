package com.delex.wallet.pojo_classes;

import java.io.Serializable;

/**
 * Created by embed on 25/11/15.
 */
public class GetAllCardsPojo implements Serializable {

    private String id;

    private String last4;
    private String brand;
     private String exp_month, funding;
    private String exp_year,name;
    private boolean defaultCard;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String type) {
        this.brand = type;
    }

    public String getExp_month() {
        return exp_month;
    }

    public void setExp_month(String exp_month) {
        this.exp_month = exp_month;
    }

    public String getExp_year() {
        return exp_year;
    }

    public void setExp_year(String exp_year) {
        this.exp_year = exp_year;
    }

    public boolean getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(boolean defaultCard) {
        this.defaultCard = defaultCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }
}
