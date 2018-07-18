package com.example.moda.firebasebasedchat.Activities;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moda.firebasebasedchat.Adapters.ChatlistAdapter;
import com.example.moda.firebasebasedchat.AppController;
import com.example.moda.firebasebasedchat.Callbacks.DatabaseCallbackHandler;
import com.example.moda.firebasebasedchat.ModelClasses.Chatlist_item;
import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.TopSnackBar.TSnackbar;
import com.example.moda.firebasebasedchat.Utilities.ChatsSorter;
import com.example.moda.firebasebasedchat.Utilities.CustomLinearLayoutManager;
import com.example.moda.firebasebasedchat.Utilities.RecyclerItemClickListener;
import com.example.moda.firebasebasedchat.Utilities.Utilities;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by moda on 20/06/17.
 */

public class Chatlist extends AppCompatActivity {

    private ChatlistAdapter mAdapter;

    private ArrayList<Chatlist_item> mChatData;
    private SearchView searchView;


    private TextView tv;


    private String userId;

    private ValueEventListener chatsListener;
    private CoordinatorLayout searchRoot;


    private CustomLinearLayoutManager llm;
    private ProgressDialog progressDialog = null;

    private boolean firstTime = true;

    @Override

    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {


        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.chatlist);

        userId = getIntent().getStringExtra("userId");
        searchRoot = (CoordinatorLayout) findViewById(R.id.root2);
        tv = (TextView) findViewById(R.id.notLoggedIn);

        ImageView close = (ImageView) findViewById(R.id.close);


        llm = new CustomLinearLayoutManager(Chatlist.this, LinearLayoutManager.VERTICAL, false);
        mChatData = new ArrayList<>();
        RecyclerView recyclerView_chat = (RecyclerView) findViewById(R.id.rv);
        recyclerView_chat.setHasFixedSize(true);
        mAdapter = new ChatlistAdapter(Chatlist.this, mChatData);
        recyclerView_chat.setLayoutManager(llm);
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        recyclerView_chat.setAdapter(mAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        searchView = (SearchView) findViewById(R.id.search);

        recyclerView_chat.addOnItemTouchListener(new RecyclerItemClickListener(Chatlist.this, recyclerView_chat, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Chatlist_item item = mAdapter.getList().get(position);
                Intent intent = new Intent(view.getContext(), ChatMessagesScreen.class);
                intent.putExtra("receiverUid", item.getReceiverUid());
                intent.putExtra("receiverName", item.getReceiverName());
                intent.putExtra("chatName", item.getChatId());
                Log.i("","recvr ID "+item.getReceiverUid()+" "+item.getReceiverName()+" "+item.getChatId());
                if (item.hasNewMessage()) {
                    item.sethasNewMessage(false);
                    mAdapter.notifyItemChanged(position);
                }
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(Chatlist.this).toBundle());
                // finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });


        searchView.setIconified(true);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                searchView.setIconifiedByDefault(true);
                searchView.setIconified(true);
                searchView.setQuery("", false);
                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);

                return false;
            }
        });


        searchView.setBackgroundColor(ContextCompat.getColor(Chatlist.this, R.color.color_white));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Chatlist.this, SelectUsersActivity.class);
                i.putExtra("userId", userId);
                startActivity(i);
            }
        });


        DatabaseCallbackHandler handler = new DatabaseCallbackHandler(Chatlist.this) {
            @Override
            public void execute(DataSnapshot childDataSnapshot) throws JSONException {
                String chatId = childDataSnapshot.getKey();
                long count = (long) childDataSnapshot.child("unreadMessageCount").getValue();
                Chatlist_item chat = new Chatlist_item();
                chat.setReceiverUid((String) childDataSnapshot.child("senderId").getValue());
                chat.setNewMessage((String) childDataSnapshot.child("lastMessage").getValue());
                chat.setReceiverName((String) childDataSnapshot.child("senderName").getValue());
                chat.setChatId(chatId);
                if (!(childDataSnapshot.child("actualSenderId").getValue()).equals(userId))
                {
                    if (count > 0) {
                        chat.setNewMessageCount(String.valueOf(count));
                        chat.sethasNewMessage(true);
                    } else {
                        chat.sethasNewMessage(false);
                    }
                }
                chat.setNewMessageTime(Utilities.epochtoGmt((String) childDataSnapshot.child("timestamp").getValue()));
                int alreadyInContact = alreadyInContact(chatId);
                if (alreadyInContact == -1) {
                    mChatData.add(0, chat);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            mAdapter.notifyDataSetChanged();


                            tv.setVisibility(View.GONE);

                        }
                    });

                } else {


                    mChatData.remove(alreadyInContact);
                    mChatData.add(0, chat);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            mAdapter.notifyDataSetChanged();
                            tv.setVisibility(View.GONE);
                        }
                    });


                }


            }
        };
        AppController.initDatabaseHandlerInstance(handler);
        AppController.getInstance().registerListenerForNewChats();
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onResume() {


        super.onResume();

        SharedPreferences.Editor prefsEditor = getSharedPreferences("defaultPreferences", Context.MODE_PRIVATE).edit();


        prefsEditor.putString("chatNotificationArray", new Gson().toJson(new ArrayList<Map<String, String>>()));
        prefsEditor.apply();

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        nMgr.cancel(0);
        addChat();


    }


    /**
     * To fetch the chats from the couchdb, stored locally
     */

    @SuppressWarnings("unchecked")

    public void addChat() {


        if (firstTime) {


            progressDialog = new ProgressDialog(Chatlist.this);
            progressDialog.setMessage("Loading chats...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            firstTime = false;
        }


        chatsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chatlist_item item;
                mChatData.clear();

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    item = new Chatlist_item();
                    /*
                     * For those chats in which no message has been sent yet
                     */
                    if (childDataSnapshot.child("timestamp").getValue() != null) {
                        item.setReceiverUid((String) childDataSnapshot.child("senderId").getValue());
                        item.setReceiverName((String) childDataSnapshot.child("senderName").getValue());
                        item.setNewMessage((String) childDataSnapshot.child("lastMessage").getValue());
                        item.setNewMessageTime(Utilities.epochtoGmt((String) childDataSnapshot.child("timestamp").getValue()));
                        long count = (long) childDataSnapshot.child("unreadMessageCount").getValue();
                        if (count > 0) {
                            item.setNewMessageCount(String.valueOf(count));
                            item.sethasNewMessage(true);
                        } else {
                            item.sethasNewMessage(false);
                        }


                        item.setChatId(childDataSnapshot.getKey());
                        mChatData.add(item);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mAdapter.notifyItemInserted(mChatData.size() - 1);
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }


                Collections.sort(mChatData, new ChatsSorter());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            llm.scrollToPositionWithOffset(0, 0);

                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }


                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (mChatData.size() == 0) {


                            tv.setText("No chats to show");
                            tv.setVisibility(View.VISIBLE);
                        } else {


                            tv.setVisibility(View.GONE);
                        }
                    }
                });


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Cancelled", "loadPost:onCancelled", databaseError.toException());
                // ...
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        };

        FirebaseDatabase.getInstance().getReference().child("Users_Chats").child(userId).addListenerForSingleValueEvent(chatsListener);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.supportFinishAfterTransition();
    }

    public void showNoSearchResults(CharSequence constraint) {
        TSnackbar snackbar = TSnackbar
                .make(searchRoot, getString(R.string.string_948) + " " + constraint, TSnackbar.LENGTH_SHORT);
        snackbar.setMaxWidth(3000); //for fullsize on tablets
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.WHITE);
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#222222"));
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Chatlist.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            Intent i = new Intent(Chatlist.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
        }
        return true;
    }

    public int alreadyInContact(String chatId) {
        int j = -1;
        for (int i = 0; i < mChatData.size(); i++) {
            if (mChatData.get(i).getChatId().equals(chatId)) {
                j = i;
                break;
            }
        }
        return j;
    }
}
