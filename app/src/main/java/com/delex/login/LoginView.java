package com.delex.login;

/**
 * Created by ads on 11/05/17.
 * <h2>LoginView</h2>
 * <p>Interface have control of all view which interact via methods</p>
 */

public interface LoginView {
    /**
     * <h2>showProgress</h2>
     * <p>use to show progress bar in activity while loading
     * to the user</p>
     */
    void showProgress();

    /**
     * <h2>hideProgress</h2>
     * <p>use to hide progress bar in activity while loading
     * to the user</p>
     */
    void hideProgress();

    /**
     * <h2>setUsernameError</h2>
     * <p>give error message to the calling view after validating username</p>
     *
     * @param message Error message get from validation of email and phone number
     */
    void setUsernameError(String message);

    /**
     * <h2>setPasswordError</h2>
     * <p>give error message to the calling view after validating password</p>
     *
     * @param message Error message get from validation of password
     */
    void setPasswordError(String message);

    /**
     * <h2>showError</h2>
     * <p>if user get a error from api</p>
     *
     * @param error what error user get at run time
     */
    void showError(String error);

    /**
     * <h2>navigateToHome</h2>
     * <p>if successfully validates all the field user enters
     * it will navigate to the home page of application</p>
     */
    void navigateToHome();


    void setLoginCreds(String username,String  pass);
}
