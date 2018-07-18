package com.delex.login;

import android.content.Context;

import com.delex.utility.Utility;

public class LoginPresenterImpl implements LoginPresenter, LoginInteractor.OnLoginFinishedListener {

    private LoginView loginView;
    private LoginInteractor loginInteractor;
    private Context context;

    public LoginPresenterImpl(LoginView loginView, Context context) {
        this.loginView = loginView;
        this.loginInteractor = new LoginInteractorImpl();
        this.context = context;
    }

    @Override
    public void validateCredentials(String username, String password) {
        if (loginView != null) {
            loginView.showProgress();
        }

        loginInteractor.login(username, password, this, context);
    }

    @Override
    public void onDestroy() {
        loginView = null;
    }

    @Override
    public void getLoginCreds() {
        String name = Utility.getPreference(context, "USER_EMAIL");
        String pass = Utility.getPreference(context, "USER_PASSWORD");
        loginView.setLoginCreds(name,pass);
    }

    @Override
    public void setLanguage(String lang) {


    }

    @Override
    public void onUsernameError(String message) {
        if (loginView != null) {
            loginView.setUsernameError(message);
            loginView.hideProgress();
        }
    }

    @Override
    public void onPasswordError(String message) {
        if (loginView != null) {
            loginView.setPasswordError(message);
            loginView.hideProgress();
        }
    }

    /**
     * <h2>onSuccess<h2/>
     * <p>this method will trigger on success response of api and
     * will stop progress dialog</p>
     */
    @Override
    public void onSuccess() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.navigateToHome();
        }
    }

    /**
     * <h2>onError<h2/>
     * <p>this method have a api error message and this message will
     * show to their and will stop progress dialog</p>
     *
     * @param error will get api error message and show to the user
     */
    @Override
    public void onError(String error) {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.showError(error);
        }
    }
}
