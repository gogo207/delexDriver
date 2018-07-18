package com.delex.wallet.pojo_classes;

import java.io.Serializable;

/**
 * Created by embed on 2/10/15.
 */
public class Cards implements Serializable {

    private String uniqueNumberIdentifier;
    private String expired;
    private String last4;
    private String cardType;
    private String type;
    private String expirationMonth;
    private String expirationYear;
    private String imageUrl;
    private String token;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }




    public String getUniqueNumberIdentifier() {
        return uniqueNumberIdentifier;
    }

    public void setUniqueNumberIdentifier(String uniqueNumberIdentifier) {
        this.uniqueNumberIdentifier = uniqueNumberIdentifier;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }
}
