package com.delex.wallet.interface_callbacks;

/**
 * <h1>PresenterNotifierBase</h1>
 * <p>
 *     interface to notify the controller classes
 * </p>
 * @since  05/09/17.
 */

public interface PresenterNotifierBase
{
    void showToastNotifier(String msg, int duration);
    void showAlertNotifier(String title, String msg);
    //void apiErrorNotifier(boolean isToShowToast, String errorMsg, int duration);
}
