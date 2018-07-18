package com.delex.login;

/**
 * <h2>LoginPresenter</h2>
 * <p>This interface is i</p>
 */
public interface LoginPresenter {

    /**
     * <h2>validateCredentials</h2>
     * <p>method will validate the username and password entered by user</p>
     *
     * @param username can be mobile number and email
     * @param password any varchar string will be place here
     */
    void validateCredentials(String username, String password);

    /**
     * <h2>onDestroy</h2>
     * <p>use to freed the exsisting reference loaded into memory<p/>
     */
    void onDestroy();

    void getLoginCreds();

    void setLanguage(String lang);
}
