package com.example.moda.firebasebasedchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.moda.firebasebasedchat.R;

/**
 * Created by moda on 19/06/17.
 */

public class ViewHolderSelectUser extends RecyclerView.ViewHolder {
    public TextView userName,email;


    public ViewHolderSelectUser(View view) {
        super(view);


        userName = (TextView) view.findViewById(R.id.userName);
        email = (TextView) view.findViewById(R.id.email);

    }
}
