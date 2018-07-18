package com.delex.wallet.pojo_classes;

/**
 * @since 19/09/17.
 */

public class WalletTransPojo
{
        /*"errNum":200,
    "errMsg":"Got The Details",
    "errFlag":0,
    "data":{
    "debitArr":[],
    "creditArr":[],
    "creditDebitArr":[*/

    private int errNum, errFlag;
    private String errMsg;
    private WalletTransDataPojo data;

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

    public WalletTransDataPojo getData() {
        return data;
    }

    public void setData(WalletTransDataPojo data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "WalletTransPojo{" +
                "errNum=" + errNum +
                ", errFlag=" + errFlag +
                ", errMsg='" + errMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
