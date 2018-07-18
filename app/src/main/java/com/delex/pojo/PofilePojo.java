package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by embed on 19/5/17.
 */

public class PofilePojo implements Serializable {

    private int errNum;

    private String errMsg;

    private String statusCode;

    private ProfileData data;

    private int errFlag;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public int getErrNum() {
        return errNum;
    }

    public void setErrNum(int errNum) {
        this.errNum = errNum;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public ProfileData getData() {
        return data;
    }

    public void setData(ProfileData data) {
        this.data = data;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
    }
}
