package com.delex.wallet.interface_callbacks;

/**
 * <h1>ViewNotifierBase</h1>
 * Interface to handle views callbacks
 * @since  31/08/17.
 */

public interface ViewNotifierBase
{

    /**
     * <h2>showToastNotifier</h2>
     * <p>
     *     method to trigger activity/fragment show progress dialog interface
     *
     * </p>
     * @param msg: message to be shown along with the progress dialog
     */
    void showProgressDialog(String msg);

    /**
     * <h2>showToastNotifier</h2>
     * <p>
     *     method to trigger activity/fragment showToast interface
     *     to show test
     * </p>
     * @param msg: message to be shown in toast
     * @param duration: toast duration
     */
    void showToast(String msg, int duration);

    /**
     * <h2>showAlertNotifier</h2>
     * <p>
     *     method to trigger activity/fragment showAlertNotifier interface
     *     to show alert
     * </p>
     * @param title: alert title to be set
     * @param msg: alert message to be displayed
     */
    void showAlert(String title, String msg);


    /**
     * <h2>noInternetAlert</h2>
     * <p>
     *     method to trigger activity/fragment alert interface
     *     to show alert that there isnot internet connectivity
     * </p>
     */
    void noInternetAlert();
}
