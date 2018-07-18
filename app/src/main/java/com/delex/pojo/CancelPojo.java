package com.delex.pojo;

import java.util.ArrayList;

/**
 * Created by embed on 5/6/17.
 */

public class CancelPojo {

    private int errNum;

    private String errMsg;

    private ArrayList<String> data;

    private int errFlag;

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

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
    }
}
