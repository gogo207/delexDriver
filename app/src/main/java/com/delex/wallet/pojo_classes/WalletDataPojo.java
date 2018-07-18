package com.delex.wallet.pojo_classes;

/**
 * @since 27/09/17.
 */

public class WalletDataPojo
{
    /*"data":{
    "enableCard":true,
            "enableWallet":false,
            "paidByReceiver":true,
            "walletAmount":"-499.43",
            "softLimit":0,
            "hardLimit":0
            reachedSoftLimit = false;
            reachedHardLimit = false;
    }*/

    private boolean enableCard, enableWallet, paidByReceiver;
    private int softLimit, hardLimit;
    private String walletAmount;
    private boolean reachedSoftLimit = false, reachedHardLimit = false;

    public boolean isEnableCard() {
        return enableCard;
    }

    public void setEnableCard(boolean enableCard) {
        this.enableCard = enableCard;
    }

    public boolean isEnableWallet() {
        return enableWallet;
    }

    public void setEnableWallet(boolean enableWallet) {
        this.enableWallet = enableWallet;
    }

    public boolean isPaidByReceiver() {
        return paidByReceiver;
    }

    public void setPaidByReceiver(boolean paidByReceiver) {
        this.paidByReceiver = paidByReceiver;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public int getSoftLimit() {
        return softLimit;
    }

    public void setSoftLimit(int softLimit) {
        this.softLimit = softLimit;
    }

    public int getHardLimit() {
        return hardLimit;
    }

    public void setHardLimit(int hardLimit) {
        this.hardLimit = hardLimit;
    }

    public boolean isReachedSoftLimit() {
        return reachedSoftLimit;
    }

    public void setReachedSoftLimit(boolean reachedSoftLimit) {
        this.reachedSoftLimit = reachedSoftLimit;
    }

    public boolean isReachedHardLimit() {
        return reachedHardLimit;
    }

    public void setReachedHardLimit(boolean reachedHardLimit) {
        this.reachedHardLimit = reachedHardLimit;
    }
}

