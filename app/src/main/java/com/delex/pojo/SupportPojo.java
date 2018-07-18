package com.delex.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 8/3/2017.
 */

public class SupportPojo implements Serializable {
    private String errNum;

    private String errMsg;

    private ArrayList<SupportData> data;

    private String errFlag;

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

    public String getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(String errFlag) {
        this.errFlag = errFlag;
    }

    public ArrayList<SupportData> getData() {
        return data;
    }

    public void setData(ArrayList<SupportData> data) {
        this.data = data;
    }
}
