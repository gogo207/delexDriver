package com.delex.country_picker;

public class Country {
    private String code;
    private String name;
    private String dialCode;
    private int minDigits;
    private int maxDigits;

    public int getMinDigits() {
        return minDigits;
    }

    public void setMinDigits(int minDigits) {
        this.minDigits = minDigits;
    }

    public int getMaxDigits() {
        return maxDigits;
    }

    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}