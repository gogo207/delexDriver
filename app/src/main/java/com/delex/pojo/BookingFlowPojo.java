package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by ads on 24/05/17.
 */

public class BookingFlowPojo implements Serializable {
    private int errNum;
    private int errFlag = -1;
    private String errMsg;
    private BookingFlowData data;

    //{"errNum":200,"errMsg":"Updated Successfully.",
    // "errFlag":0,"data":{"status":9,"invoice":{"timeFare":30,"distFare":30,"contractPays":40,"Discount":0}}}
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

    public BookingFlowData getData() {
        return data;
    }

    public void setData(BookingFlowData data) {
        this.data = data;
    }
}
