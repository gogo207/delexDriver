package com.delex.wallet.event_holder;

/**
 * <h1>WalletDataChangedEvent</h1>
 * <p>
 *     Class to handle event that wallet data modified to
 *     update wallet fragment data if its active
 * </p>
 *@since  04/10/17.
 */

public class WalletDataChangedEvent
{
    /* a = 53;
    msg = "wallet enabled";
    userHardLimit = 200;
    userSoftLimit = 102;
    walletAmount = "0.00";
    */

    private int a, userHardLimit, userSoftLimit;
    private String msg, walletAmount;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getUserHardLimit() {
        return userHardLimit;
    }

    public void setUserHardLimit(int userHardLimit) {
        this.userHardLimit = userHardLimit;
    }

    public int getUserSoftLimit() {
        return userSoftLimit;
    }

    public void setUserSoftLimit(int userSoftLimit) {
        this.userSoftLimit = userSoftLimit;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }
}
