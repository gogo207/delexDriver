package com.example.moda.firebasebasedchat.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moda.firebasebasedchat.Adapters.ChatMessageAdapter;
import com.example.moda.firebasebasedchat.AppController;
import com.example.moda.firebasebasedchat.ModelClasses.Chat_Message_item;
import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.Utilities.ApiOnServer;
import com.example.moda.firebasebasedchat.Utilities.CustomLinearLayoutManager;
import com.example.moda.firebasebasedchat.Utilities.FloatingView;
import com.example.moda.firebasebasedchat.Utilities.OkHttp3Connection;
import com.example.moda.firebasebasedchat.Utilities.ServiceUrl;
import com.example.moda.firebasebasedchat.Utilities.Utilities;
import com.example.moda.firebasebasedchat.Utilities.VariableConstant;
import com.facebook.internal.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Created by moda on 19/06/17.
 */

public class ChatMessagesScreen extends AppCompatActivity {
    private String receiverUid,sessionToken,langId;
    private ChatMessageAdapter mAdapter;
    private String receiverName;
    private String userId, picturePath, videoPath;
    private ArrayList<Chat_Message_item> mChatData;
    private TextView receiverNameHeader;
    private String chatName;
    private EditText editText;
    private RelativeLayout root;
    private static final int RESULT_CAPTURE_IMAGE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_VIDEO = 2;
    private static int RESULT_CAPTURE_VIDEO = 3;
    private static final int IMAGE_QUALITY = 50;//change it to higher level if want,but then slower image sending
    private static final int IMAGE_CAPTURED_QUALITY = 50;//change it to higher level if want,but then slower image sending
    private static final double MAX_VIDEO_SIZE = 26 * 1024 * 1024;
    private Uri imageUri;
    private FirebaseStorage storage;
    private CustomLinearLayoutManager llm;
    private ChildEventListener childEventListener;
    private ValueEventListener postListener, presenceListener;
    private TextView presence;
    private int notificationId = -1;
    private ProgressDialog progressDialog = null;
    private ImageView camera;

    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    @Override
    public void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message_screen);

        storage = FirebaseStorage.getInstance();
        RelativeLayout close = (RelativeLayout) findViewById(R.id.close_rl);
        root = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        presence = (TextView) findViewById(R.id.presence);
        camera = (ImageView) findViewById(R.id.camera);
        mChatData = new ArrayList<>();
        mAdapter = new ChatMessageAdapter(ChatMessagesScreen.this, mChatData, root);
        RecyclerView recyclerView_chat = (RecyclerView) findViewById(R.id.list_view_messages);
        llm = new CustomLinearLayoutManager(ChatMessagesScreen.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_chat.setLayoutManager(llm);
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        recyclerView_chat.setAdapter(mAdapter);
        recyclerView_chat.setHasFixedSize(true);
        editText = (EditText) findViewById(R.id.chat_edit_text1);
        RelativeLayout sendButton = (RelativeLayout) findViewById(R.id.send_rl);
        RelativeLayout attachment = (RelativeLayout) findViewById(R.id.attachment);
        receiverNameHeader = (TextView) findViewById(R.id.title);
        try {
            userId =AppController.getInstance().getUserId();
        } catch (NullPointerException e) {
            supportFinishAfterTransition();
        }

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            receiverUid = bundle.getString("receiverUid");
            AppController.getInstance().setActiveReceiverId(receiverUid);
            receiverName = bundle.getString("receiverName");
            chatName = bundle.getString("chatName");
            notificationId = bundle.getInt("notificationId");
            sessionToken=bundle.getString("session");
            langId=bundle.getString("langId");
            AppController.getInstance().setActiveChatName(chatName);
        }
        receiverNameHeader.setText(receiverName);


        editText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // recyclerView_chat.scrollToPosition(mChatData.size() - 1);
                            llm.scrollToPositionWithOffset(mChatData.size() - 1, 0);

                        }
                    }, 500);

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });




/*
 * To send the message and current message to the list of messages
 */
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() > 0) {



                    String timestamp = new Utilities().gmtToEpoch(Utilities.tsInGmt());
                    Chat_Message_item item = new Chat_Message_item(editText.getText().toString().trim(),
                            userId, timestamp);
                    item.setMessageType("0");
                    FirebaseDatabase.getInstance()
                            .getReference().child("messages").child(chatName)
                            .push()
                            .setValue(item);
                    item.setDeliveryStatus("0");
                    item.setMessageDateOverlay(Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(timestamp))).substring(9, 24));
                    item.setSelf(true);
                    mChatData.add(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mAdapter.notifyItemInserted(mChatData.size() - 1);
                                llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    sendMessage(editText.getText().toString().trim(),userId);
                    // Clear the input
                    editText.setText("");
                }


            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermissionImage();
            }
        });

        //FirebaseDatabase.getInstance().getReference().child("messages").removeValue();

        /*
         * Child listener has to be before value listener,otherwise sometimes childvalue fired wrongly(without any child being added)
         */
        setListenerForRecentMessages();
        loadPreviousMessages();
        addListenerForPresence();

        if (notificationId != -1) {

            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            nMgr.cancel(chatName, notificationId);
        }


        updateUnreadMessagesCount();

    }

    private void sendMessage(String msg,String userid) {
        Log.d("notsent",msg+"  "+userid+"  "+receiverUid);
        if(Utilities.isNetworkAvailable(ChatMessagesScreen.this)) {
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("text", msg);
                jsonObject.put("chatId", userid);
                jsonObject.put("receiverId", receiverUid);

                OkHttp3Connection.doOkHttp3Connection(sessionToken,langId, ServiceUrl.SEND_MSG, OkHttp3Connection.Request_type.POST,jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {

                            @Override
                            public void onSuccess(String result) {
                              /*  progressDialog.dismiss();*/
                            }
                            @Override
                            public void onError(String error) {

                                Log.d("notsent",error+"  "+sessionToken+"  "+langId);

                            }
                        }


                ,sessionToken);
            }catch (Exception e){
                e.printStackTrace();
                Log.d("excty",e+"");
            }
        }
        else{
           /* Alerts alerts=new Alerts();
            alerts.showNetworkAlert(ChatMessagesScreen.this);
        }
                */

            }
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiverNameHeader = null;
        receiverName = null;
        try {
            FirebaseDatabase.getInstance().
                    getReference().
                    child("messages").
                    child(chatName).removeEventListener(childEventListener);
            FirebaseDatabase.getInstance().
                    getReference().
                    child("messages").
                    child(chatName).removeEventListener(postListener);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUid).
                    removeEventListener(presenceListener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        AppController.getInstance().setStatusAsOfflineForCurrentChat(userId);
        AppController.getInstance().setActiveReceiverId("");
        AppController.getInstance().setActiveChatName("");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.supportFinishAfterTransition();
    }

    /*
     * to convert string from the 24 hour format to 12 hour format
     */
    public String convert24to12hourformat(String d) {

        String datein12hour = null;

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(d);


            datein12hour = new SimpleDateFormat("h:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }


        return datein12hour;

    }


    /*
     * To find the date to be shown for the date overlay on top,which shows the date of message currently ontop
     */
    private String findOverlayDate(String date) {


        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MMM/yyyy");


        String m1 = "", m2 = "";


        String month1, month2;

        String d1, d2;


        d1 = sdf.format(new Date());

        d2 = date;


        month1 = d1.substring(7, 10);


        month2 = d2.substring(7, 10);

        if (month1.equals("Jan")) {
            m1 = "01";
        } else if (month1.equals("Feb")) {
            m1 = "02";
        } else if (month2.equals("Mar")) {
            m2 = "03";
        } else if (month1.equals("Apr")) {
            m1 = "04";
        } else if (month1.equals("May")) {
            m1 = "05";
        } else if (month1.equals("Jun")) {
            m1 = "06";
        } else if (month1.equals("Jul")) {
            m1 = "07";
        } else if (month1.equals("Aug")) {
            m1 = "08";
        } else if (month1.equals("Sep")) {
            m1 = "09";
        } else if (month1.equals("Oct")) {
            m1 = "10";
        } else if (month1.equals("Nov")) {
            m1 = "11";
        } else if (month1.equals("Dec")) {
            m1 = "12";
        }


        if (month2.equals("Jan")) {
            m2 = "01";
        } else if (month2.equals("Feb")) {
            m2 = "02";
        } else if (month1.equals("Mar")) {
            m1 = "03";
        } else if (month2.equals("Apr")) {
            m2 = "04";
        } else if (month2.equals("May")) {
            m2 = "05";
        } else if (month2.equals("Jun")) {
            m2 = "06";
        } else if (month2.equals("Jul")) {
            m2 = "07";
        } else if (month2.equals("Aug")) {
            m2 = "08";
        } else if (month2.equals("Sep")) {
            m2 = "09";
        } else if (month2.equals("Oct")) {
            m2 = "10";
        } else if (month2.equals("Nov")) {
            m2 = "11";
        } else if (month2.equals("Dec")) {
            m2 = "12";
        }
        month1 = null;
        month2 = null;


        if (sdf.format(new Date()).equals(date)) {


            m2 = null;
            m1 = null;
            d2 = null;
            d1 = null;
            sdf = null;
            return "Today";
        } else if ((Integer.parseInt(d1.substring(11) + m1 + d1.substring(4, 6)) - Integer.parseInt(d2.substring(11) + m2 + d2.substring(4, 6))) == 1) {

            m2 = null;
            m1 = null;
            d2 = null;
            d1 = null;
            sdf = null;
            return "Yesterday";

        } else {

            m2 = null;
            m1 = null;
            d2 = null;
            d1 = null;
            sdf = null;
            return date;
        }


    }


    private void loadPreviousMessages() {


        progressDialog = new ProgressDialog(ChatMessagesScreen.this);
        progressDialog.setMessage("Loading messages...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat_Message_item item;
                mChatData.clear();


                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {


                    item = new Chat_Message_item();


                    String type = (String) childDataSnapshot.child("messageType").getValue();
                    item.setSenderId((String) childDataSnapshot.child("senderId").getValue());
                    item.setTimestamp((String) childDataSnapshot.child("timestamp").getValue());
                    item.setMessageDateOverlay(Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(item.getTimestamp()))).substring(9, 24));
                    item.setMessageType(type);

                    item.setMessageId(childDataSnapshot.getKey());
                    if (type.equals("1") || type.equals("2")) {


                        item.setThumbnailPath((String) childDataSnapshot.child("thumbnailPath").getValue());


                        if (type.equals("1")) {

                            item.setImagePath((String) childDataSnapshot.child("imagePath").getValue());
                        } else {
                            item.setVideoPath((String) childDataSnapshot.child("videoPath").getValue());
                        }


                        if (!(childDataSnapshot.child("senderId").getValue()).equals(userId)) {


                            item.setReceiverUid((String) childDataSnapshot.child("senderId").getValue());


                            if (type.equals("1")) {


                                if (new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + childDataSnapshot.child("senderId").getValue() + childDataSnapshot.getKey() + ".jpg").exists()) {
                                    item.setDownloadStatus(1);

                                    item.setImagePath(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + childDataSnapshot.child("senderId").getValue() + childDataSnapshot.getKey() + ".jpg");
                                } else {
                                    item.setDownloadStatus(0);
//                                    item.setImagePath((String) childDataSnapshot.child("imagePath").getValue());
                                }
                            } else {


                                if (new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + childDataSnapshot.child("senderId").getValue() + childDataSnapshot.getKey() + ".mp4").exists()) {
                                    item.setDownloadStatus(1);
                                    item.setVideoPath(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + childDataSnapshot.child("senderId").getValue() + childDataSnapshot.getKey() + ".mp4");


                                } else {
                                    item.setDownloadStatus(0);
//                                    item.setVideoPath((String) childDataSnapshot.child("videoPath").getValue());
                                }


                            }

                            item.setSize((String) childDataSnapshot.child("size").getValue());
                        }
//                        else {
//
//
//
//                        }

                    } else {


                        item.setMessage((String) childDataSnapshot.child("message").getValue());
                    }


                    if ((childDataSnapshot.child("senderId").getValue()).equals(userId)) {
                        item.setDeliveryStatus("1");
                        item.setSelf(true);
                    } else {


                        item.setSelf(false);

                    }


                    mChatData.add(item);

                    try {
                        mAdapter.notifyItemInserted(mChatData.size() - 1);


                        llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }}
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

        /*
         * To fetch the details of previous messages for the first time when u you have entered the
         */
        FirebaseDatabase.getInstance().getReference().child("messages").child(chatName).addListenerForSingleValueEvent(postListener);
    }


    private void setListenerForRecentMessages() {


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot childDataSnapshot, String previousChildName) {


                Chat_Message_item item = new Chat_Message_item();


                String type = (String) childDataSnapshot.child("messageType").getValue();


                if ((childDataSnapshot.child("senderId").getValue()).equals(userId)) {
                    drawSingleTick((String) childDataSnapshot.child("timestamp").getValue());
                } else {

                    item.setSenderId((String) childDataSnapshot.child("senderId").getValue());
                    item.setTimestamp((String) childDataSnapshot.child("timestamp").getValue());


                    item.setMessageDateOverlay(Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(item.getTimestamp()))).substring(9, 24));


                    item.setMessageType(type);


                    item.setMessageId(childDataSnapshot.getKey());
                    if (type.equals("1") || type.equals("2")) {


                        item.setThumbnailPath((String) childDataSnapshot.child("thumbnailPath").getValue());


                        if (type.equals("1")) {

                            item.setImagePath((String) childDataSnapshot.child("imagePath").getValue());
                        } else {
                            item.setVideoPath((String) childDataSnapshot.child("videoPath").getValue());
                        }


                        item.setReceiverUid((String) childDataSnapshot.child("senderId").getValue());
                        if (type.equals("1")) {


                            if (new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + childDataSnapshot.child("senderId").getValue() + childDataSnapshot.getKey() + ".jpg").exists()) {
                                item.setDownloadStatus(1);

                            } else {
                                item.setDownloadStatus(0);

                            }
                        } else {

                            if (new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + childDataSnapshot.child("senderId").getValue() + childDataSnapshot.getKey() + ".mp4").exists()) {
                                item.setDownloadStatus(1);

                            } else {
                                item.setDownloadStatus(0);

                            }


                        }

                        item.setSize((String) childDataSnapshot.child("size").getValue());


                    } else {


                        item.setMessage((String) childDataSnapshot.child("message").getValue());
                    }


                    item.setSelf(false);

                    mChatData.add(item);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                mAdapter.notifyItemInserted(mChatData.size() - 1);
                                llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);

                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        FirebaseDatabase.getInstance().

                getReference().

                child("messages").

                child(chatName).

                addChildEventListener(childEventListener);

    }
 /*
     * To show the dialog for selecting the type of message to send
     */

    public void openDialog() {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View inflatedView;

        inflatedView = layoutInflater.inflate(
                R.layout.custom_dialog_options_menu, null, false);

        LinearLayout layoutGallery, layoutVideo;
//        TextView tvGallery, tvVideo;
//        tvGallery = (TextView) inflatedView.findViewById(R.id.tvGallery);
//
//        tvVideo = (TextView) inflatedView.findViewById(R.id.tvVideo);


//        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Condensed.ttf");
//        tvGallery.setTypeface(face, Typeface.NORMAL);
//        tvPhoto.setTypeface(face, Typeface.NORMAL);
//        tvVideo.setTypeface(face, Typeface.NORMAL);
//        tvAudio.setTypeface(face, Typeface.NORMAL);
//        tvContacts.setTypeface(face, Typeface.NORMAL);
//        tvLocation.setTypeface(face, Typeface.NORMAL);


        layoutGallery = (LinearLayout) inflatedView.findViewById(R.id.layoutGallery);
        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FloatingView.dismissWindow();
                checkReadImage();


            }
        });


        layoutVideo = (LinearLayout) inflatedView.findViewById(R.id.layoutVideo);
        layoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingView.dismissWindow();

                checkReadVideo();


            }
        });


        FloatingView.onShowPopup(this, inflatedView);
    }
 /*
    *
    * To check for access gallery permission to select image
    * */

    private void checkReadImage() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.string_804)), RESULT_LOAD_IMAGE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.string_804)), RESULT_LOAD_IMAGE);
            }


        } else {

            requestReadImagePermission(1);
        }

    }
    /*
    *
    * To check for access gallery permission to select video
    * */

    private void checkReadVideo() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(intent, RESULT_LOAD_VIDEO);

        } else {

            requestReadVideoPermission(1);
        }

    }

    /*
*
* To request access gallery permission to select video
* */
    private void requestReadVideoPermission(int k) {

        if (k == 1) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar snackbar = Snackbar.make(root, R.string.string_67,
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.string_580), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(ChatMessagesScreen.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                27);
                    }
                });


                snackbar.show();


                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);


            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        27);

            }
        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar snackbar = Snackbar.make(root, R.string.string_982,
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.string_580), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(ChatMessagesScreen.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                38);

                    }
                });


                snackbar.show();


                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);


            } else {


                ActivityCompat.requestPermissions(ChatMessagesScreen.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        38);

            }

        }
    }
       /*
    *
    * To request access gallery permission to select image
    * */


    private void requestReadImagePermission(int k) {
        if (k == 1) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar snackbar = Snackbar.make(root, R.string.string_67,
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.string_580), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(ChatMessagesScreen.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                26);
                    }
                });


                snackbar.show();


                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);


            } else {


                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        26);
            }
        } else if (k == 0) {




            /*
             * For capturing the image permission
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatMessagesScreen.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(ChatMessagesScreen.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar snackbar = Snackbar.make(root, R.string.string_981,
                        Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.string_580), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions(ChatMessagesScreen.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                37);
                    }
                });


                snackbar.show();


                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);


            } else {


                ActivityCompat.requestPermissions(ChatMessagesScreen.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        37);
            }


        }

    }



     /*
     * Result of request permission
     */

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 21) {


            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {


                    Snackbar snackbar = Snackbar.make(root, R.string.string_55,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                } else {


                    Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                }
            } else {


                Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

            }


        } else if (requestCode == 26) {

            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.string_804)), RESULT_LOAD_IMAGE);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.string_804)), RESULT_LOAD_IMAGE);
                    }


                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 27) {

            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(intent, RESULT_LOAD_VIDEO);

                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (requestCode == 37) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(ChatMessagesScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                        } else {


                            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }


                        }

                        startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
                    } else {
                        Snackbar snackbar = Snackbar.make(root, R.string.string_61,
                                Snackbar.LENGTH_SHORT);
                        snackbar.show();


                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }

            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }

        } else if (requestCode == 38) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.checkSelfPermission(ChatMessagesScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);//mms quality video not hd
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);//max 120s video
                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 26214400L);//max 25 mb size recording


                    startActivityForResult(intent, RESULT_CAPTURE_VIDEO);
                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.string_57,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }

        }
        else if (requestCode == 24) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.checkSelfPermission(ChatMessagesScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)


                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (intent.resolveActivity(getPackageManager()) != null) {

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                            } else {


                                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                for (ResolveInfo resolveInfo : resInfoList) {
                                    String packageName = resolveInfo.activityInfo.packageName;
                                    grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }


                            }

                            startActivityForResult(intent, RESULT_CAPTURE_IMAGE);


                        } else {
                            Snackbar snackbar = Snackbar.make(root, R.string.string_61,
                                    Snackbar.LENGTH_SHORT);
                            snackbar.show();


                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    } else {


                        requestReadImagePermission(0);
                    }
                } else {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_62,
                            Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {

                Snackbar snackbar = Snackbar.make(root, R.string.string_62,
                        Snackbar.LENGTH_SHORT);


                snackbar.show();
                View view = snackbar.getView();
                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }

    }
 /*
     * utility methods
     */


    @SuppressWarnings("all")
    private Uri setImageUri() {
        String name = Utilities.tsInGmt();
        name = new Utilities().gmtToEpoch(name);


        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/modaChat");

        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }


        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/modaChat", name + ".jpg");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Uri imgUri = FileProvider.getUriForFile(ChatMessagesScreen.this, getApplicationContext().getPackageName() + ".provider", file);
        this.imageUri = imgUri;

        this.picturePath = file.getAbsolutePath();


        name = null;
        folder = null;
        file = null;


        return imgUri;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        outState.putParcelable("file_uri", imageUri);

        outState.putString("file_path", picturePath);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable("file_uri");

            picturePath = savedInstanceState.getString("file_path");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    /*
    * To get details of the type of the attachment selected
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {


            picturePath = getPath(this, data.getData());


            Uri uri = null;
            Bitmap bm = null;
            String id = null;

            try {

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);


                int height = options.outHeight;
                int width = options.outWidth;

                float density = getResources().getDisplayMetrics().density;
                int reqHeight;


                if (width != 0) {
                    reqHeight = (int) ((150 * density) * (height / width));

                    bm = decodeSampledBitmapFromResource(picturePath, (int) (150 * density), reqHeight);

                    if (bm != null) {


                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bm.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, baos);


                        // bm = null;
                        byte[] b = baos.toByteArray();

                        try {
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        baos = null;

                        //   b = compress(b);


                        id = new Utilities().gmtToEpoch(Utilities.tsInGmt());

                        File f = convertByteArrayToFile(b, id, ".jpg");
                        b = null;

                        uri = Uri.fromFile(f);
                        f = null;


                    } else {
                        if (root != null) {

                            Snackbar snackbar = Snackbar.make(root, R.string.string_48, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }


                } else {
                    if (root != null) {

                        Snackbar snackbar = Snackbar.make(root, R.string.string_48, Snackbar.LENGTH_SHORT);


                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();

                if (root != null) {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_49, Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }


            }


            if (uri != null) {


                /*
                 *
                 *
                 * make thumbnail
                 *
                 * */


                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);


                bm = null;
                byte[] b = baos.toByteArray();

                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                baos = null;


                String timestamp = new Utilities().gmtToEpoch(Utilities.tsInGmt());

                Chat_Message_item item = new Chat_Message_item("",
                        userId, timestamp);

                item.setMessageType("1");

                item.setDeliveryStatus("0");
                item.setSelf(true);

                item.setImagePath(picturePath);
                item.setMessageDateOverlay(Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(timestamp))).substring(9, 24));

                mChatData.add(item);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter.notifyDataSetChanged();


                        try {


                            llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);

                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });

                uploadFile(1, b, uri, timestamp);


                uri = null;
                b = null;
                bm = null;
            }


        } else if (requestCode == RESULT_CAPTURE_IMAGE && resultCode == RESULT_OK) {


            Uri uri = null;
            String id = null;
            Bitmap bm = null;

            try {

//  picturePath = getPath(this, imageUri);


                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);


                int height = options.outHeight;
                int width = options.outWidth;

                float density = getResources().getDisplayMetrics().density;
                int reqHeight;


                if (width != 0) {


                    reqHeight = (int) ((150 * density) * (height / width));


                    bm = decodeSampledBitmapFromResource(picturePath, (int) (150 * density), reqHeight);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();


                    if (bm != null) {

                        bm.compress(Bitmap.CompressFormat.JPEG, IMAGE_CAPTURED_QUALITY, baos);
                        //bm = null;
                        byte[] b = baos.toByteArray();
                        try {
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        baos = null;
//                b = compress(b);


                        id = new Utilities().gmtToEpoch(Utilities.tsInGmt());

                        File f = convertByteArrayToFile(b, id, ".jpg");
                        b = null;

                        uri = Uri.fromFile(f);
                        f = null;


                    } else {


                        if (root != null) {

                            Snackbar snackbar = Snackbar.make(root, R.string.string_50, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }


                    }


                } else {


                    if (root != null) {

                        Snackbar snackbar = Snackbar.make(root, R.string.string_50, Snackbar.LENGTH_SHORT);


                        snackbar.show();
                        View view = snackbar.getView();
                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();


                if (root != null) {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_49, Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }


            }


            if (uri != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);


                bm = null;
                byte[] b = baos.toByteArray();

                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                baos = null;


                String timestamp = new Utilities().gmtToEpoch(Utilities.tsInGmt());

                Chat_Message_item item = new Chat_Message_item("",
                        userId, timestamp);

                item.setMessageType("1");

                item.setDeliveryStatus("0");
                item.setSelf(true);
                item.setMessageDateOverlay(Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(timestamp))).substring(9, 24));

                item.setImagePath(picturePath);

                mChatData.add(item);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter.notifyDataSetChanged();
                        try {

                            llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);

                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });
                uploadFile(1, b, uri, timestamp);

                uri = null;
                b = null;
                bm = null;

            }


        } else if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK && null != data) {

            Uri uri = null;
            String id = null;
            try {


                videoPath = getPath(ChatMessagesScreen.this, data.getData());


                File video = new File(videoPath);

                if (video.length() <= (MAX_VIDEO_SIZE)) {

                    try {


                        byte[] b = convertFileToByteArray(video);
                        video = null;
                        //        b = compress(b);


                        id = new Utilities().gmtToEpoch(Utilities.tsInGmt());

                        File f = convertByteArrayToFile(b, id, ".mp4");
                        b = null;

                        uri = Uri.fromFile(f);
                        f = null;

                        b = null;


                    } catch (OutOfMemoryError e) {

                        if (root != null) {

                            Snackbar snackbar = Snackbar.make(root, R.string.string_51, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }


                    }

                    if (uri != null) {


                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bm = ThumbnailUtils.createVideoThumbnail(videoPath,
                                MediaStore.Images.Thumbnails.MINI_KIND);

                        if (bm != null) {

                            bm.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                            bm = null;
                            byte[] b = baos.toByteArray();


                            try {
                                baos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            baos = null;


                            String timestamp = new Utilities().gmtToEpoch(Utilities.tsInGmt());

                            Chat_Message_item item = new Chat_Message_item("",
                                    userId, timestamp);

                            item.setMessageType("2");
                            item.setMessageDateOverlay(Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(timestamp))).substring(9, 24));

                            item.setDeliveryStatus("0");
                            item.setSelf(true);


                            /*
                             * Have to create a temp thumbnail file
                             */


                            item.setThumbnailPath(saveVideoThumbnails(b, timestamp));


                            item.setVideoPath(videoPath);

                            mChatData.add(item);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mAdapter.notifyDataSetChanged();
                                    try {


                                        llm.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);

                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            uploadFile(2, b, uri, timestamp);


                            uri = null;
                            b = null;

                        }
                    } else {


                        if (root != null) {

                            Snackbar snackbar = Snackbar.make(root, getString(R.string.string_52) + " " + MAX_VIDEO_SIZE / (1024 * 1024) + " " + getString(R.string.string_56), Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }


                    }

                }


            } catch (NullPointerException e) {


                if (root != null) {

                    Snackbar snackbar = Snackbar.make(root, R.string.string_764, Snackbar.LENGTH_SHORT);


                    snackbar.show();
                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }

            }


        }

    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private static byte[] convertFileToByteArray(File f) {


        byte[] byteArray = null;
        byte[] b;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            InputStream inputStream = new FileInputStream(f);
            b = new byte[2663];

            int bytesRead;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }


            inputStream = null;

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            b = null;

            try {
                bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


            bos = null;
        }


        return byteArray;
    }


    /*
     * To save the byte array received in to file
     */
    @SuppressWarnings("all")
    public File convertByteArrayToFile(byte[] data, String name, String extension) {


        File file = null;

        try {


            File folder = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_UPLOAD_THUMBNAILS_FOLDER);

            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }


            file = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_UPLOAD_THUMBNAILS_FOLDER, name + extension);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return file;

    }
    /*
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }


            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {


            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /*
   * Get the value of the data column for this Uri. This is useful for
   * MediaStore Uris, and other file-based ContentProviders.
   *
   * @param context       The context.
   * @param uri           The Uri to query.
   * @param selection     (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight) {


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);


        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);

    }

    /*
    * To calculate the required dimensions of image without actually loading the bitmap in to the memory
    */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {


        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * Type 1 - image
     * 2 - video
     */

    private void uploadFile(final int messageType, final byte[] thumbnail, Uri fileUri, final String timestamp) {







        /*
         * Will upload both the image and the thumbnail
         */


        String filename = userId + new Utilities().gmtToEpoch(Utilities.tsInGmt());


        final StorageReference mainImageRef, thumbnailRef;

        if (messageType == 1) {

            filename = filename + ".jpg";


        } else if (messageType == 2) {

            filename = filename + ".mp4";


        }

        mainImageRef = storage.getReference().child("uploads/" + filename);
        thumbnailRef = storage.getReference().child("thumbnails/" + filename);


        /*
         * Upload main file
         */

        UploadTask uploadTask = mainImageRef.putFile(fileUri);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.


                uploadThumbnail(thumbnail, taskSnapshot.getDownloadUrl(), thumbnailRef, String.valueOf(messageType), timestamp, taskSnapshot.getMetadata().getSizeBytes());


            }
        });


        /*
         * Upload main file
         */


    }


    private void uploadThumbnail(byte[] thumbnail, final Uri fileUrl, StorageReference spaceRef, final String messageType, final String timestamp, final long numberOfBytes) {
        UploadTask uploadTask = spaceRef.putBytes(thumbnail);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri thumbnailUrl = taskSnapshot.getDownloadUrl();


                Chat_Message_item item = new Chat_Message_item("",
                        userId, timestamp);

                item.setMessageType(messageType);

                item.setThumbnailPath(thumbnailUrl.toString());


                if (messageType.equals("1")) {
                    item.setImagePath(fileUrl.toString());
                } else {
                    item.setVideoPath(fileUrl.toString());
                }

                String size;

                if (numberOfBytes < 1024) {


                    size = numberOfBytes + " bytes";

                } else if (numberOfBytes >= 1024 && numberOfBytes <= 1048576) {


                    size = (numberOfBytes / 1024) + " KB";

                } else {


                    size = (numberOfBytes / 1048576) + " MB";
                }


                item.setSize(String.valueOf(size));

                FirebaseDatabase.getInstance()
                        .getReference().child("messages").child(chatName)
                        .push()
                        .setValue(item)
                ;


            }
        });

    }

    /**
     * @param id assumed as unique identifier instead of the messageid
     */
    private void drawSingleTick(String id) {


        try {
            for (int i = mChatData.size() - 1; i >= 0; i--) {
                if (mChatData.get(i).isSelf() && (mChatData.get(i).getTimestamp()).equals(id)) {


                    if (!(mChatData.get(i).getDeliveryStatus().equals("1"))) {
                        mChatData.get(i).setDeliveryStatus("1");


                        final int k = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                mAdapter.notifyItemChanged(k);


                            }
                        });


                        deleteVideoThumbnail(id);


                        break;
                    }


                }
            }


        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    private void addListenerForPresence() {


        presenceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Object type = dataSnapshot.child("connections").getValue();

                if (type == null || !((boolean) dataSnapshot.child("online").getValue())) {

                    try{
                        presence.setText(findPresenceTime((Long) dataSnapshot.child("lastOnline").getValue()));

                    }
                    catch (Exception e)
                    {
                        presence.setText("Offline");

                    }

                } else {
                    presence.setText("Online");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Cancelled", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        /*
         * To fetch the details of previous messages for the first time when u you have entered the
         */
        FirebaseDatabase.getInstance().getReference().child("users").child(receiverUid).addValueEventListener(presenceListener);


    }


    private String findPresenceTime(long str) {


        String lastSeen = Utilities.changeStatusDateFromGMTToLocal(Utilities.epochtoGmt(String.valueOf(str)));


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS z");

        Date date2 = new Date();
        String current_date = sdf.format(date2);

        current_date = current_date.substring(0, 8);
        String onlineStatus = "Connecting...";
        if (lastSeen != null) {


            if (current_date.equals(lastSeen.substring(0, 8))) {


                lastSeen = convert24to12hourformat(lastSeen.substring(8, 10) + ":" + lastSeen.substring(10, 12) + ":" + lastSeen.substring(12, 14));
                onlineStatus = "Last Seen:Today " + lastSeen;

                lastSeen = null;

            } else {


                String last = convert24to12hourformat(lastSeen.substring(8, 10) + ":" + lastSeen.substring(10, 12) + ":" + lastSeen.substring(12, 14));


                String date = lastSeen.substring(6, 8) + "-" + lastSeen.substring(4, 6) + "-" + lastSeen.substring(0, 4);


                onlineStatus = "Last Seen:" + date + " " + last;


                last = null;
                date = null;

            }


        }
        return onlineStatus;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private String saveVideoThumbnails(byte[] data, String name) {


        File file = null;

        try {


            File folder = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.VIDEO_THUMBNAILS);

            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }


            file = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.VIDEO_THUMBNAILS, name + ".jpg");
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return file.getPath();

    }


    private void deleteVideoThumbnail(String name) {

        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + ApiOnServer.VIDEO_THUMBNAILS, name + ".jpg");

            if (f.exists()) {
                f.delete();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }


    private void updateUnreadMessagesCount() {


        /*
         * To update all the message count as read
         */
        FirebaseDatabase.getInstance()
                .getReference().child("Users_Chats").child(userId).child(chatName).child("online")
                .setValue(1);
        FirebaseDatabase.getInstance()
                .getReference().child("Users_Chats").child(userId).child(chatName).child("unreadMessageCount")
                .setValue(0);
    }

    /*
       *
       * To check for camera permission
       * */
    private void checkCameraPermissionImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(ChatMessagesScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                    } else {


                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }


                    }

                    startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
                } else {
                    Snackbar snackbar = Snackbar.make(root, R.string.string_61,
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();


                    View view = snackbar.getView();
                    TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            } else {


                /*
                 *permission required to save the image captured
                 */
                requestReadImagePermission(0);


            }


        } else {

            requestCameraPermissionImage();
        }

    }
    /*
    *
    * To request access camera permission to capture image
    * */

    private void requestCameraPermissionImage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            Snackbar snackbar = Snackbar.make(root, R.string.string_65,
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.string_580), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(ChatMessagesScreen.this, new String[]{Manifest.permission.CAMERA},
                            24);
                }
            });


            snackbar.show();


            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


        } else {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    24);
        }
    }



}