package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by embed on 19/5/17.
 */

public class ProfileData implements Serializable {

    private String typeName;

    private String platNo;

    private String Name;

    private String phone;

    private String pPic;

    private String email;

    private String planName;
    private boolean bankDetails;
    private String make;
    private String model;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPlatNo() {
        return platNo;
    }

    public void setPlatNo(String platNo) {
        this.platNo = platNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getpPic() {
        return pPic;
    }

    public void setpPic(String pPic) {
        this.pPic = pPic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(boolean bankDetails) {
        this.bankDetails = bankDetails;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
