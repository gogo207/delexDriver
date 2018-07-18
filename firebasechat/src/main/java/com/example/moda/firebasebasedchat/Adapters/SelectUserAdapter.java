package com.example.moda.firebasebasedchat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moda.firebasebasedchat.ModelClasses.SelectUserItem;
import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderSelectUser;

import java.util.ArrayList;

/**
 * Created by moda on 19/06/17.
 */

public class SelectUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SelectUserItem> mFilteredListData = new ArrayList<>();


    private Context mContext;


    public SelectUserAdapter(Context mContext, ArrayList<SelectUserItem> mListData) {

        this.mFilteredListData = mListData;
        this.mContext = mContext;


    }


    @Override
    public int getItemCount() {
        return this.mFilteredListData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        View v = inflater.inflate(R.layout.select_user_item, viewGroup, false);
        viewHolder = new ViewHolderSelectUser(v);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        ViewHolderSelectUser vh2 = (ViewHolderSelectUser) viewHolder;
        configureViewHolderSelectUser(vh2, position);

    }


    private void configureViewHolderSelectUser(final ViewHolderSelectUser vh, int position) {


        final SelectUserItem chat = mFilteredListData.get(position);


        vh.userName.setText(chat.getUserName());


        vh.email.setText(chat.getEmail());


    }


}