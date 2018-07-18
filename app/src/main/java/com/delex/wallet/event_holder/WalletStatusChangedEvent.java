package com.delex.wallet.event_holder;

/**
 * <h1>WalletStatusChangedEvent</h1>
 * <p>
 *     Class to handle event that wallet status (Enabled/disabled) modified to
 *     update wallet fragment data if its active
 * </p>
 *@since  04/10/17.
 */

public class WalletStatusChangedEvent
{
    private boolean isWalletEnabled = false;
    private String walletAmount = "";

    public boolean isWalletEnabled() {
        return isWalletEnabled;
    }

    public void setWalletEnabled(boolean walletEnabled) {
        isWalletEnabled = walletEnabled;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }
}
