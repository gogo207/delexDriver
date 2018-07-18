package com.delex.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 15/5/17.
 */

public class VehicleTypePojo implements Serializable {

    private String errNum;

    private String errMsg;

    private ArrayList<VehTypeData> data;

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

    public ArrayList<VehTypeData> getData() {
        return data;
    }

    public void setData(ArrayList<VehTypeData> data) {
        this.data = data;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
    }
}
