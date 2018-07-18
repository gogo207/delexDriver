package com.delex.wallet.pojo_classes;

import java.io.Serializable;

/**
 * Created by embed on 25/11/15.
 */
public class GetCard_pojo implements Serializable {

    private Integer errNum;

    private String errMsg;
    private Integer errFlag;


    private GetAllCardsPojo[] data;


    public Integer getErrNum() {
        return errNum;
    }

    public void setErrNum(Integer errNum) {
        this.errNum = errNum;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Integer getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(Integer errFlag) {
        this.errFlag = errFlag;
    }

    public GetAllCardsPojo[] getData() {
        return data;
    }

    public void setData(GetAllCardsPojo[] cards) {
        this.data = cards;
    }
}
