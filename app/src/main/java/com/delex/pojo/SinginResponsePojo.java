package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by ads on 11/05/17.
 */

public class SinginResponsePojo implements Serializable {
    private int errNum;
    private int errFlag = -1;
    private String errMsg;
    private SigninData data;

    public int getErrNum() {
        return errNum;
    }

    public void setErrNum(int errNum) {
        this.errNum = errNum;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public SigninData getData() {
        return data;
    }

    public void setData(SigninData data) {
        this.data = data;
    }
}
