package com.delex.pojo.bank;

/**
 * Created by muthu on 1/25/2018.
 */

public class BankPojo {
    private int errNum;

    private String errMsg;

    private String statusCode;

    private BankList data;

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

    public BankList getData() {
        return data;
    }

    public void setData(BankList data) {
        this.data = data;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
    }
}
