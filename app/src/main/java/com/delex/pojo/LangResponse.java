package com.delex.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by DELL on 25-12-2017.
 */

public class LangResponse implements Serializable {

    private String errNum;

    private String errMsg;

    private ArrayList<Languages> data;

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

    public ArrayList<Languages> getData() {
        return data;
    }

    public void setData(ArrayList<Languages> data) {
        this.data = data;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(String errFlag) {
        this.errFlag = errFlag;
    }
}
