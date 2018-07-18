package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by ads on 15/05/17.
 */

public class AssignedTripsPojo implements Serializable {
    private int errFlag = -1;
    private int errNum;
    private String errMsg;
    private String statusCode;
    private String message;
    private AssignedTripData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
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

    public AssignedTripData getData() {
        return data;
    }

    public void setData(AssignedTripData data) {
        this.data = data;
    }
}
