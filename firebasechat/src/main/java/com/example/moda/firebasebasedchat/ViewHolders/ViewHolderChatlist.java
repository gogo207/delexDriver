package com.example.moda.firebasebasedchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moda.firebasebasedchat.R;

/**
 * Created by moda on 20/06/17.
 */

public class ViewHolderChatlist extends RecyclerView.ViewHolder {
    public  TextView newMessageTime, newMessage, storeName, newMessageDate, newMessageCount;
    public ImageView storeImage;
    public   RelativeLayout rl;

    public   ViewHolderChatlist(View view) {
        super(view);


        newMessageTime = (TextView) view.findViewById(R.id.newMessageTime);
        newMessage = (TextView) view.findViewById(R.id.newMessage);
        newMessageDate = (TextView) view.findViewById(R.id.newMessageDate);
        storeName = (TextView) view.findViewById(R.id.storeName);
        storeImage = (ImageView) view.findViewById(R.id.storeImage2);

        rl = (RelativeLayout) view.findViewById(R.id.rl);


        newMessageCount = (TextView) view.findViewById(R.id.newMessageCount);


    }
}
