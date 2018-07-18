package com.delex.wallet.pojo_classes;

/**
 * Created by PrashantSingh on 27/09/17.
 */

public class WalletPojo
{
    /*{
        "errNum":200,
            "errMsg":"Got The Details",
            "errFlag":0,
            "data":{}
    }*/


    private int errNum, errFlag;
    private String errMsg;
    private WalletDataPojo data;

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

    public WalletDataPojo getData() {
        return data;
    }

    public void setData(WalletDataPojo data) {
        this.data = data;
    }
}
