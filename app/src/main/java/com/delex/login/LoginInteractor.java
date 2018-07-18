package com.delex.login;

import android.content.Context;

public interface LoginInteractor {

    void login(String username, String password, OnLoginFinishedListener listener, Context context);

    interface OnLoginFinishedListener {

        void onUsernameError(String message);

        void onPasswordError(String message);

        void onSuccess();

        void onError(String error);
    }

}
