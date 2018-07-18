package com.delex.pojo.bank;

import java.io.Serializable;

/**
 * Created by murashid on 29-Aug-17.
 */

public class BankList implements Serializable {
    private String id;
    private String object;
    private String accountNumber;
    private String accountHolderName;
    private String account_holder_type;
    private String bank_name;
    private String country;
    private String currency;
    private String default_for_currency;
    private String fingerprint;
    private String last4;
    private String routingNumber;
    private String status;
    private String name;
    private String ibanNumber;
    private String swiftCode;

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getIbanNumber() {
        return ibanNumber;
    }

    public void setIbanNumber(String ibanNumber) {
        this.ibanNumber = ibanNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }



    public String getAccount_holder_name() {
        return accountHolderName;
    }

    public void setAccount_holder_name(String account_holder_name) {
        this.accountHolderName = account_holder_name;
    }

    public String getAccount_holder_type() {
        return account_holder_type;
    }

    public void setAccount_holder_type(String account_holder_type) {
        this.account_holder_type = account_holder_type;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDefault_for_currency() {
        return default_for_currency;
    }

    public void setDefault_for_currency(String default_for_currency) {
        this.default_for_currency = default_for_currency;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getRouting_number() {
        return routingNumber;
    }

    public void setRouting_number(String routing_number) {
        this.routingNumber = routing_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
