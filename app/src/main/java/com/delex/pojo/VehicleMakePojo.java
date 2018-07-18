package com.delex.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 16/5/17.
 */

public class VehicleMakePojo implements Serializable {

    private String errNum;

    private String errMsg;

    private ArrayList<VehMakeData> data;

    private int errFlag;

    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public ArrayList<VehMakeData> getData() {
        return data;
    }

    public void setData(ArrayList<VehMakeData> data) {
        this.data = data;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
    }
}
