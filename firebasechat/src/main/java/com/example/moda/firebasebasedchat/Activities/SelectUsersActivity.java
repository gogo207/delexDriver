package com.example.moda.firebasebasedchat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.moda.firebasebasedchat.Adapters.SelectUserAdapter;
import com.example.moda.firebasebasedchat.AppController;
import com.example.moda.firebasebasedchat.ModelClasses.MembersItem;
import com.example.moda.firebasebasedchat.ModelClasses.SelectUserItem;
import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.Utilities.CustomLinearLayoutManager;
import com.example.moda.firebasebasedchat.Utilities.RecyclerItemClickListener;
import com.facebook.internal.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by moda on 19/06/17.
 */

public class SelectUsersActivity extends AppCompatActivity {


    private SelectUserAdapter mAdapter;
    private String chatName;

    private ArrayList<SelectUserItem> mUserData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list);


        RecyclerView recyclerViewUsers = (RecyclerView) findViewById(R.id.list_of_users);
        recyclerViewUsers.setHasFixedSize(true);
        mAdapter = new SelectUserAdapter(SelectUsersActivity.this, mUserData);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setLayoutManager(new CustomLinearLayoutManager(SelectUsersActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerViewUsers.setAdapter(mAdapter);


        final ProgressDialog progressDialog = new ProgressDialog(SelectUsersActivity.this);
        progressDialog.setMessage("Loading users...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                SelectUserItem item;
                Log.i("Log",dataSnapshot.toString());

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                    item = new SelectUserItem();

                    item.setUserId((String) childDataSnapshot.child("userId").getValue());





/*
 * Have to avoid current user
 */
                    if (!(childDataSnapshot.child("userId").getValue()).equals(AppController.getInstance().getUserId())) {
                        item.setEmail((String) childDataSnapshot.child("email").getValue());
                        item.setUserName((String) childDataSnapshot.child("userName").getValue());

                        mUserData.add(item);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Cancelled", "loadPost:onCancelled", databaseError.toException());
                // ...
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

            }
        };
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(postListener);


        recyclerViewUsers.addOnItemTouchListener(new RecyclerItemClickListener(SelectUsersActivity.this, recyclerViewUsers, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {


                if (position >= 0) {


                    /*
                     * To add a conversation,we add a members nested collection
                     */
                    SelectUserItem item2 = mUserData.get(position);

                    MembersItem item = new MembersItem();
                    item.setMemberId1(AppController.getInstance().getUserId());

                    item.setMemberId2(item2.getUserId());
                    writeNewChat(item, item2.getUserId(), item2.getUserName());


                }


            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        ImageView close = (ImageView) findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }


    private void writeNewChat(final MembersItem membersItem, final String userId, final String userName) {


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String req_id1, req_id2;


                req_id1 = membersItem.isMemberId1();
                req_id2 = membersItem.isMemberId2();

                String id1, id2;


                chatName = "";


                boolean flag = true;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {


                    id1 = (String) childDataSnapshot.child("memberId1").getValue();
                    id2 = (String) childDataSnapshot.child("memberId2").getValue();


/*
 * Have to avoid current user
 */
                    if (req_id1.equals(id1) && req_id2.equals(id2)) {
                        flag = false;

                        chatName = id1 + "_" + id2;


                        break;
                    } else if (req_id1.equals(id2) && req_id2.equals(id1)) {
                        flag = false;
                        chatName = id1 + "_" + id2;

                        break;
                    }

                }


                if (flag) {
                    chatName = membersItem.isMemberId1() + "_" + membersItem.isMemberId2();
                    FirebaseDatabase.getInstance().getReference().child("members").child(membersItem.isMemberId1() + "_" + membersItem.isMemberId2()).setValue(membersItem);
                }


                Intent i = new Intent(SelectUsersActivity.this, ChatMessagesScreen.class);


                i.putExtra("receiverUid", userId);

                i.putExtra("receiverName", userName);
                i.putExtra("chatName", chatName);


                startActivity(i);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Cancelled", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        /*
         * To fetch the details of the members who have already initiated chats
         */
        FirebaseDatabase.getInstance().getReference().child("members").addListenerForSingleValueEvent(postListener);


    }
}