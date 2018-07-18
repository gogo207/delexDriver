package com.example.moda.firebasebasedchat.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.moda.firebasebasedchat.Activities.ChatMessagesScreen;
import com.example.moda.firebasebasedchat.Activities.MediaHistory_FullScreenImage;
import com.example.moda.firebasebasedchat.Activities.MediaHistory_FullScreenVideo;
import com.example.moda.firebasebasedchat.BlurTransformation.BlurTransformation;
import com.example.moda.firebasebasedchat.DownloadFile.FileDownloadService;
import com.example.moda.firebasebasedchat.DownloadFile.ServiceGenerator;
import com.example.moda.firebasebasedchat.ModelClasses.Chat_Message_item;
import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.Utilities.ApiOnServer;
import com.example.moda.firebasebasedchat.Utilities.RingProgressBar;
import com.example.moda.firebasebasedchat.Utilities.Utilities;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderImageReceived;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderImageSent;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderMessageReceived;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderMessageSent;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderVideoReceived;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderVideoSent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moda on 19/06/17.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Chat_Message_item> mListData = new ArrayList<>();
    private final int MESSAGERECEIVED = 0;
    private final int MESSAGESENT = 1;
    private final int IMAGERECEIVED = 2;
    private final int IMAGESENT = 3;

    private final int VIDEORECEIVED = 4;
    private final int VIDEOSENT = 5;
    int i,i2;
    private HashMap<String, Object> map = new HashMap<>();


    private long fileSizeDownloaded;
    private Bitmap thumbnail;

    private Context mContext;


    private RelativeLayout root;

    public ChatMessageAdapter(Context mContext, ArrayList<Chat_Message_item> mListData, RelativeLayout root) {
        this.mListData = mListData;
        this.mContext = mContext;
        this.root = root;

    }


    @Override
    public int getItemCount() {
        return this.mListData.size();
    }


    @Override
    public int getItemViewType(int position) {
        String type = mListData.get(position).getMessageType();


        if (mListData.get(position).isSelf()) {


            if (type.equals("0")) {
                return MESSAGESENT;
            } else if (type.equals("1")) {


                return IMAGESENT;

            } else {
                return VIDEOSENT;
            }
        } else {

            if (type.equals("0")) {
                return MESSAGERECEIVED;
            } else if (type.equals("1")) {


                return IMAGERECEIVED;

            } else {
                return VIDEORECEIVED;
            }

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View v1;
        switch (viewType) {
            case MESSAGERECEIVED:
                v1 = inflater.inflate(R.layout.message_received, viewGroup, false);
                viewHolder = new ViewHolderMessageReceived(v1);
                break;
            case IMAGERECEIVED:
                v1 = inflater.inflate(R.layout.image_received, viewGroup, false);
                viewHolder = new ViewHolderImageReceived(v1);
                break;

            case VIDEORECEIVED:
                v1 = inflater.inflate(R.layout.video_received, viewGroup, false);
                viewHolder = new ViewHolderVideoReceived(v1);
                break;


            case MESSAGESENT:
                v1 = inflater.inflate(R.layout.message_sent, viewGroup, false);
                viewHolder = new ViewHolderMessageSent(v1);
                break;

            case IMAGESENT:
                v1 = inflater.inflate(R.layout.image_sent, viewGroup, false);
                viewHolder = new ViewHolderImageSent(v1);
                break;

            default:
                v1 = inflater.inflate(R.layout.video_sent, viewGroup, false);
                viewHolder = new ViewHolderVideoSent(v1);
                break;

        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        switch (viewHolder.getItemViewType()) {


            case MESSAGERECEIVED:
                ViewHolderMessageReceived vh2 = (ViewHolderMessageReceived) viewHolder;

                configureViewHolderMessageReceived(vh2, position);

                break;

            case IMAGERECEIVED:
                ViewHolderImageReceived vh3 = (ViewHolderImageReceived) viewHolder;
                configureViewHolderImageReceived(vh3, position);
                break;

            case VIDEORECEIVED:

                ViewHolderVideoReceived vh4 = (ViewHolderVideoReceived) viewHolder;

                configureViewHolderVideoReceived(vh4, position);
                break;


            case MESSAGESENT:


                ViewHolderMessageSent vh8 = (ViewHolderMessageSent) viewHolder;

                configureViewHolderMessageSent(vh8, position);

                break;

            case IMAGESENT:


                ViewHolderImageSent vh9 = (ViewHolderImageSent) viewHolder;
                configureViewHolderImageSent(vh9, position);
                break;

            default:

                ViewHolderVideoSent vh10 = (ViewHolderVideoSent) viewHolder;
                configureViewHolderVideoSent(vh10, position);
                break;


        }
    }


    private void configureViewHolderMessageReceived(ViewHolderMessageReceived vh2, final int position) {
        final Chat_Message_item message = mListData.get(position);
        if (message != null) {


//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
//            vh2.time.setTypeface(tf, Typeface.ITALIC);
//
//            vh2.date.setTypeface(tf, Typeface.ITALIC);
//            vh2.message.setTypeface(tf, Typeface.NORMAL);
//            vh2.date.setTypeface(tf, Typeface.ITALIC);


            try {
                vh2.message.setText(message.getMessage());

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            String date = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(message.getTimestamp())));


            vh2.time.setText(convert24to12hourformat(date.substring(0, 9)) + " ");
            vh2.comma.setVisibility(View.GONE);


            try {


                Chat_Message_item messagePrevious = mListData.get(position - 1);


                try {
                    if (!messagePrevious.getMessageDateOverlay().equals(message.getMessageDateOverlay())) {


                        vh2.comma.setVisibility(View.VISIBLE);

                        vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            } catch (IndexOutOfBoundsException e) {
                vh2.comma.setVisibility(View.VISIBLE);

                vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

            }


        }
    }


    private void configureViewHolderMessageSent(ViewHolderMessageSent vh2, final int position) {
        final Chat_Message_item message = mListData.get(position);
        if (message != null) {


//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
//            vh2.time.setTypeface(tf, Typeface.ITALIC);
//            vh2.date.setTypeface(tf, Typeface.ITALIC);
//            vh2.message.setTypeface(tf, Typeface.NORMAL);


            try {
                vh2.message.setText(message.getMessage());

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            vh2.comma.setVisibility(View.GONE);

            String date = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(message.getTimestamp())));


            vh2.time.setText(convert24to12hourformat(date.substring(0, 9)) + " ");
            try {


                Chat_Message_item messagePrevious = mListData.get(position - 1);


                if (!messagePrevious.getMessageDateOverlay().equals(message.getMessageDateOverlay())) {

                    vh2.comma.setVisibility(View.VISIBLE);


                    vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

                }


            } catch (IndexOutOfBoundsException e) {

                vh2.comma.setVisibility(View.VISIBLE);


                vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            } catch (NullPointerException e) {
                e.printStackTrace();

            }


            if (message.getDeliveryStatus().equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);

            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);


            }
        }


    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private void configureViewHolderImageSent(final ViewHolderImageSent vh2, final int position) {
        final Chat_Message_item message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);
//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
//            vh2.time.setTypeface(tf, Typeface.ITALIC);

//            vh2.date.setTypeface(tf, Typeface.ITALIC);


//            vh2.date.setVisibility(View.GONE);


            if (message.getImagePath() != null) {


                try {
                    Glide
                            .with(mContext)
                            .load(message.getImagePath())

                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .crossFade()
                            .centerCrop()
                            .placeholder(R.drawable.home_grid_view_image_icon)


                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                    return false;
                                }
                            })
                            .into(vh2.imageView);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                vh2.imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {


                        Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);

                        i.putExtra("imagePath", message.getImagePath());


                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) mContext, vh2.imageView, "image");
                        mContext.startActivity(i, options.toBundle());


                    }
                });
            }


            String status = message.getDeliveryStatus();

            if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);


            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);


            }
            vh2.comma.setVisibility(View.GONE);


            String date = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(message.getTimestamp())));


            vh2.time.setText(convert24to12hourformat(date.substring(0, 9)) + " ");

            try {


                Chat_Message_item messagePrevious = mListData.get(position - 1);


                if (!messagePrevious.getMessageDateOverlay().equals(message.getMessageDateOverlay())) {

                    vh2.comma.setVisibility(View.VISIBLE);


                    vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

                }


            } catch (IndexOutOfBoundsException e) {


                vh2.comma.setVisibility(View.VISIBLE);


                vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    private void configureViewHolderVideoSent(final ViewHolderVideoSent vh2, final int position) {
        final Chat_Message_item message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);
//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
//            vh2.time.setTypeface(tf, Typeface.ITALIC);

//            vh2.date.setTypeface(tf, Typeface.ITALIC);


            try {


                try {
                    Glide
                            .with(mContext)
                            .load(message.getThumbnailPath())
                            .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                            .placeholder(R.drawable.home_grid_view_image_icon)


                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                    return false;
                                }
                            })
                            .into(vh2.thumbnail);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


                vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, MediaHistory_FullScreenVideo.class);

                        i.putExtra("videoPath", message.getVideoPath());


                        i.putExtra("flag", true);
                        mContext.startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                    }
                });

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {


                e.printStackTrace();

            }


            String status = message.getDeliveryStatus();

            if (status.equals("1")) {

                vh2.clock.setVisibility(View.GONE);
                vh2.singleTick.setVisibility(View.VISIBLE);


            } else {


                vh2.clock.setVisibility(View.VISIBLE);
                vh2.singleTick.setVisibility(View.GONE);


            }

            String date = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(message.getTimestamp())));


            vh2.time.setText(convert24to12hourformat(date.substring(0, 9)) + " ");


            vh2.comma.setVisibility(View.GONE);
            try {


                Chat_Message_item messagePrevious = mListData.get(position - 1);


                if (!messagePrevious.getMessageDateOverlay().equals(message.getMessageDateOverlay())) {

                    vh2.comma.setVisibility(View.VISIBLE);


                    vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

                }


            } catch (IndexOutOfBoundsException e) {


                vh2.comma.setVisibility(View.VISIBLE);


                vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressWarnings("TryWithIdenticalCatches,unchecked")

    private void configureViewHolderImageReceived(final ViewHolderImageReceived vh2, final int position) {
        final Chat_Message_item message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);

//
//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
//            vh2.time.setTypeface(tf, Typeface.ITALIC);
//
//            vh2.date.setTypeface(tf, Typeface.ITALIC);


            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });


            try {


                if (message.getDownloadStatus() == 1) {

/*
 *
 * image already downloaded
 *
 * */
                    vh2.progressBar2.setVisibility(View.GONE);

                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.download.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);

                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        try {

                            try {
                                Glide
                                        .with(mContext)
                                        .load(message.getImagePath())


                                        .crossFade()
                                        .centerCrop()


                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .placeholder(R.drawable.home_grid_view_image_icon)
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                                return false;
                                            }
                                        })
                                        .into(vh2.imageView);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {


                                    Intent i = new Intent(mContext, MediaHistory_FullScreenImage.class);

                                    i.putExtra("imagePath", message.getImagePath());


                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation((Activity) mContext, vh2.imageView, "image");
                                    mContext.startActivity(i, options.toBundle());


                                }


                            });


                        } catch (Exception e) {
//                            vh2.fnf.setTypeface(tf, Typeface.NORMAL);
                            Glide.clear(vh2.imageView);
                            vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                            vh2.fnf.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Glide.clear(vh2.imageView);
//                        vh2.fnf.setTypeface(tf, Typeface.NORMAL);
                        vh2.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                        vh2.fnf.setVisibility(View.VISIBLE);


                        vh2.fnf.setText(R.string.string_211);
                    }


                } else {


                    if (message.isDownloading()) {


                        vh2.cancel.setVisibility(View.VISIBLE);


                        vh2.download.setVisibility(View.GONE);


                        vh2.progressBar2.setVisibility(View.VISIBLE);

                        vh2.progressBar.setVisibility(View.GONE);


                    } else {
                        vh2.download.setVisibility(View.VISIBLE);

                        vh2.progressBar2.setVisibility(View.GONE);
                        vh2.progressBar.setVisibility(View.GONE);
                        vh2.cancel.setVisibility(View.GONE);
                    }

                    try {
                        Glide
                                .with(mContext)
                                .load(message.getThumbnailPath())


                                .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))


                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                                .placeholder(R.drawable.home_grid_view_image_icon)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        vh2.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                        return false;
                                    }
                                })


                                .into(vh2.imageView);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    vh2.imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            if (!message.isDownloading()) {
                                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {


                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(mContext, 0);
                                    builder.setTitle(R.string.string_393);
                                    builder.setMessage(mContext.getString(R.string.string_506) + " " + message.getSize() + " " + mContext.getString(R.string.string_534));
                                    builder.setPositiveButton(R.string.string_580, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            String receiverUid = message.getReceiverUid();

                                            String messageId = message.getMessageId();


                                            message.setDownloading(true);


                                            ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //notifyItemChanged(viewHolder.getAdapterPosition());

                                                    notifyDataSetChanged();
                                                }
                                            });

                                            download(message.getImagePath(), message.getThumbnailPath(), Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".jpg", message, vh2);


                                            // dialog.dismiss();

                                            Context context = ((ContextWrapper) ((Dialog) dialog).getContext()).getBaseContext();


                                            if (context instanceof Activity) {


                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                                    if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                                        dialog.dismiss();
                                                    }
                                                } else {


                                                    if (!((Activity) context).isFinishing()) {
                                                        dialog.dismiss();
                                                    }
                                                }
                                            } else {


                                                try {
                                                    dialog.dismiss();
                                                } catch (final IllegalArgumentException e) {
                                                    e.printStackTrace();

                                                } catch (final Exception e) {
                                                    e.printStackTrace();

                                                }
                                            }


                                        }
                                    });
                                    builder.setNegativeButton(R.string.string_591, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            dialog.cancel();

                                        }
                                    });
                                    builder.show();
                                } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                    requestStorageAccessPermission("image");

                                }
                            } else {


                                Snackbar snackbar = Snackbar.make(root, R.string.string_38, Snackbar.LENGTH_SHORT);


                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }

                        }
                    });
                }


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            vh2.comma.setVisibility(View.GONE);


            String date = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(message.getTimestamp())));


            vh2.time.setText(convert24to12hourformat(date.substring(0, 9)) + " ");
            try {


                Chat_Message_item messagePrevious = mListData.get(position - 1);


                try {
                    if (!messagePrevious.getMessageDateOverlay().equals(message.getMessageDateOverlay())) {


                        vh2.comma.setVisibility(View.VISIBLE);


                        vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            } catch (IndexOutOfBoundsException e) {


                vh2.comma.setVisibility(View.VISIBLE);


                vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

            }
        }


    }


    @SuppressWarnings("TryWithIdenticalCatches,unchecked")
    private void configureViewHolderVideoReceived(final ViewHolderVideoReceived vh2, final int position) {
        final Chat_Message_item message = mListData.get(position);
        if (message != null) {


            vh2.fnf.setVisibility(View.GONE);
//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
//            vh2.time.setTypeface(tf, Typeface.ITALIC);
//
//            vh2.date.setTypeface(tf, Typeface.ITALIC);


            vh2.cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = (Call<ResponseBody>) map.get(message.getMessageId());


                    if (call != null)
                        call.cancel();

                }
            });

            try {


                if (message.getDownloadStatus() == 1) {

                    /*
                     *
                     * image already downloaded
                     *
                     * */
                    vh2.download.setVisibility(View.GONE);
                    vh2.progressBar.setVisibility(View.GONE);
                    vh2.progressBar2.setVisibility(View.GONE);
                    vh2.cancel.setVisibility(View.GONE);

                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        final File f = new File(message.getVideoPath());


                        if (f.exists()) {


                            thumbnail = ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND);


                            vh2.thumbnail.setImageBitmap(thumbnail);
                            vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                                                                 public void onClick(View v) {


                                                                     try {
                                                                         Uri intentUri;
                                                                         if (android.os.Build.VERSION.SDK_INT >= 24) {
                                                                             intentUri = Uri.parse(message.getVideoPath());
                                                                         } else {
                                                                             intentUri = Uri.fromFile(f);
                                                                         }


                                                                         Intent intent = new Intent();
                                                                         intent.setAction(Intent.ACTION_VIEW);


                                                                         intent.setDataAndType(intentUri, "video/*");

                                                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                                                                             intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                                                                         } else {


                                                                             List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                                                             for (ResolveInfo resolveInfo : resInfoList) {
                                                                                 String packageName = resolveInfo.activityInfo.packageName;
                                                                                 mContext.grantUriPermission(packageName, intentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                             }


                                                                         }


                                                                         mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                                                     } catch (ActivityNotFoundException e) {
                                                                         Intent i = new Intent(mContext, MediaHistory_FullScreenVideo.class);

                                                                         i.putExtra("videoPath", message.getVideoPath());
                                                                         mContext.startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext).toBundle());


                                                                     }
                                                                 }
                                                             }

                            );

                        } else {

                            Glide.clear(vh2.thumbnail);
                            vh2.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                            vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

//                            vh2.fnf.setTypeface(tf, Typeface.NORMAL);
                            vh2.fnf.setVisibility(View.VISIBLE);


                        }


                    } else {
                        Glide.clear(vh2.thumbnail);
                        vh2.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.chat_white_circle));
                        vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                        vh2.fnf.setVisibility(View.VISIBLE);
//                        vh2.fnf.setTypeface(tf, Typeface.NORMAL);

                        vh2.fnf.setText(R.string.string_211);
                    }


                } else {


                    if (message.isDownloading()) {


                        vh2.download.setVisibility(View.GONE);


                        vh2.progressBar2.setVisibility(View.VISIBLE);

                        vh2.progressBar.setVisibility(View.GONE);
                        vh2.cancel.setVisibility(View.VISIBLE);


                    } else {
                        vh2.download.setVisibility(View.VISIBLE);
                        vh2.progressBar2.setVisibility(View.GONE);
                        vh2.cancel.setVisibility(View.GONE);
                        vh2.progressBar.setVisibility(View.GONE);
                    }
                    try {
                        Glide
                                .with(mContext)
                                .load(message.getThumbnailPath())
                                .bitmapTransform(new CenterCrop(mContext), new BlurTransformation(mContext))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)


                                .placeholder(R.drawable.home_grid_view_image_icon)


                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        vh2.thumbnail.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
                                        return false;
                                    }
                                })
                                .into(vh2.thumbnail);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {


                            if (!message.isDownloading()) {
                                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {


                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(mContext, 0);
                                    builder.setTitle(R.string.string_393);
                                    builder.setMessage(mContext.getString(R.string.string_506) + " " + message.getSize() + " " + mContext.getString(R.string.string_535));
                                    builder.setPositiveButton(R.string.string_578, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            String messageId = message.getMessageId();

                                            String receiverUid = message.getReceiverUid();
                                            message.setDownloading(true);


                                            ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //notifyItemChanged(viewHolder.getAdapterPosition());

                                                    notifyDataSetChanged();
                                                }
                                            });


                                            download(message.getVideoPath(), message.getThumbnailPath(), Environment.getExternalStorageDirectory().getPath() + ApiOnServer.CHAT_DOWNLOADS_FOLDER + receiverUid + messageId + ".mp4", message, vh2);

                                            //    dialog.dismiss();


                                            Context context = ((ContextWrapper) ((Dialog) dialog).getContext()).getBaseContext();


                                            if (context instanceof Activity) {


                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                                    if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                                                        dialog.dismiss();
                                                    }
                                                } else {


                                                    if (!((Activity) context).isFinishing()) {
                                                        dialog.dismiss();
                                                    }
                                                }
                                            } else {


                                                try {
                                                    dialog.dismiss();
                                                } catch (final IllegalArgumentException e) {
                                                    e.printStackTrace();

                                                } catch (final Exception e) {
                                                    e.printStackTrace();

                                                }
                                            }


                                        }
                                    });
                                    builder.setNegativeButton(R.string.string_591, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {


                                            dialog.cancel();

                                        }
                                    });
                                    builder.show();


                                } else {


                                    /*
                                     *
                                     * have to request permission
                                     *
                                     * */


                                    requestStorageAccessPermission("video");


                                }
                            } else {

                                Snackbar snackbar = Snackbar.make(root, R.string.string_38, Snackbar.LENGTH_SHORT);


                                snackbar.show();
                                View view = snackbar.getView();
                                TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                            }

                        }
                    });


                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            String date = Utilities.formatDate(Utilities.tsFromGmt(Utilities.epochtoGmt(message.getTimestamp())));


            vh2.time.setText(convert24to12hourformat(date.substring(0, 9)) + " ");

            vh2.comma.setVisibility(View.GONE);
            try {


                Chat_Message_item messagePrevious = mListData.get(position - 1);


                if (!messagePrevious.getMessageDateOverlay().equals(message.getMessageDateOverlay())) {

                    vh2.comma.setVisibility(View.VISIBLE);


                    vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");

                }
            } catch (IndexOutOfBoundsException e) {


                vh2.comma.setVisibility(View.VISIBLE);


                vh2.date.setText(findOverlayDate(message.getMessageDateOverlay()) + " ");


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }


    }

    private void download(final String url, final String thumbnailPath, final String filePath,
                          final Chat_Message_item message,
                          final RecyclerView.ViewHolder viewHolder) {


        final FileDownloadService downloadService =
                ServiceGenerator.createService(FileDownloadService.class);


        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlAsync(url);


        map.put(message.getMessageId(), call);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {


                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), filePath, viewHolder, message.getMessageType(), message.getMessageId());


                            message.setDownloading(false);

                            ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    notifyDataSetChanged();
                                }
                            });


                            if (writtenToDisk) {


                                if (thumbnailPath != null) {
/*
 *
 * incase of image or video delete the thumbnail
 *
 * */


                                    File fdelete = new File(thumbnailPath);
                                    if (fdelete.exists()) fdelete.delete();


                                }


                                message.setDownloadStatus(1);

                                String type = message.getMessageType();

                                if (type.equals("1")) {
                                    message.setImagePath(filePath);
                                } else if (type.equals("2")) {
                                    message.setVideoPath(filePath);
                                }


                                ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });


                            } else {
/*
 *
 * failed to download the file from the server
 *
 *
 * */


                                ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Snackbar snackbar = Snackbar.make(root, R.string.string_39, Snackbar.LENGTH_SHORT);


                                        snackbar.show();
                                        View view = snackbar.getView();
                                        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                                    }
                                });


                            }


                            return null;


                        }
                    }.execute();


                } else {


                    ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            message.setDownloading(false);
                            notifyDataSetChanged();


                            Snackbar snackbar = Snackbar.make(root, R.string.string_40, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                        }
                    });
                }


            }

            @Override
            public void onFailure(final Call<ResponseBody> call, Throwable t) {


                t.printStackTrace();


                ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message.setDownloading(false);


                        notifyDataSetChanged();


                        if (call.isCanceled()) {


                            Snackbar snackbar = Snackbar.make(root, R.string.string_41, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

                        } else {


                            Snackbar snackbar = Snackbar.make(root, R.string.string_4, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                        }


                    }
                });


            }

        });


    }


    @SuppressWarnings("all")
    private boolean writeResponseBodyToDisk(ResponseBody body, String filePath,
                                            final RecyclerView.ViewHolder viewHolder, String messageType, final String messageId) {


        fileSizeDownloaded = 0;


        if (messageType.equals("1")) {
            ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {
                        if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {
                            if (((ViewHolderImageReceived) viewHolder).progressBar.getVisibility() == View.GONE) {
                                ((ViewHolderImageReceived) viewHolder).progressBar.setVisibility(View.VISIBLE);
                            }
                        }


                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    ((ViewHolderImageReceived) viewHolder).progressBar2.setVisibility(View.GONE);

                }
            });


            ((ViewHolderImageReceived) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                @Override
                public void progressToComplete() {


                    ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            Snackbar snackbar = Snackbar.make(root, R.string.string_42, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                        }
                    });

                }
            });

        } else if (messageType.equals("2")) {

            ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {
                        if (mListData.get(viewHolder.getAdapterPosition()).getMessageId().equals(messageId)) {

                            if (((ViewHolderVideoReceived) viewHolder).progressBar.getVisibility() == View.GONE) {
                                ((ViewHolderVideoReceived) viewHolder).progressBar.setVisibility(View.VISIBLE);
                            }
                        }


                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    ((ViewHolderVideoReceived) viewHolder).progressBar2.setVisibility(View.GONE);


                }
            });


            ((ViewHolderVideoReceived) viewHolder).progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {

                @Override
                public void progressToComplete() {
                    ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            Snackbar snackbar = Snackbar.make(root, R.string.string_43, Snackbar.LENGTH_SHORT);


                            snackbar.show();
                            View view = snackbar.getView();
                            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            txtv.setGravity(Gravity.CENTER_HORIZONTAL);


                        }
                    });

                }
            });
        }

        try {
            // todo change the file location/name according to your needs


            File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/modaChat");


            //  File folder = new File(getFilesDir(),"modaClient/receivedThumbnails");

            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }

            File file = new File(filePath);


            if (!file.exists()) {
                file.createNewFile();
            }


            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();


                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;


                    try {
                        if (messageType.equals("1")) {


                            ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    ((ViewHolderImageReceived) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));


                                }
                            });

                        } else if (messageType.equals("2")) {


                            ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    ((ViewHolderVideoReceived) viewHolder).progressBar.setProgress((int) ((fileSizeDownloaded * 100) / fileSize));


                                }
                            });


                        }

                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                try {
                    if (messageType.equals("1")) {


                        ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ViewHolderImageReceived) viewHolder).progressBar.setVisibility(View.GONE);
                            }

                        });

                    } else if (messageType.equals("2")) {


                        ((ChatMessagesScreen) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ViewHolderVideoReceived) viewHolder).progressBar.setVisibility(View.GONE);
                            }

                        });
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e)

        {
            return false;
        }

    }


    private String convert24to12hourformat(String d) {

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

        try{
            int i = Integer.parseInt(d1.substring(11) + m1 + d1.substring(4, 6));
            int i2= Integer.parseInt(d2.substring(11) + m2 + d2.substring(4, 6));
        }catch(NumberFormatException ex){


        }
        if (sdf.format(new Date()).equals(date)) {


            m2 = null;
            m1 = null;
            d2 = null;
            d1 = null;
            sdf = null;
            return "Today";


        } else if (i - i2 == 1) {

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

    private void requestStorageAccessPermission(String type) {


        if (ActivityCompat.shouldShowRequestPermissionRationale((ChatMessagesScreen) mContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar snackbar = Snackbar.make(root, mContext.getString(R.string.string_45, type),
                    Snackbar.LENGTH_INDEFINITE).setAction(mContext.getString(R.string.string_580), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions((ChatMessagesScreen) mContext, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            21);
                }
            });


            snackbar.show();


            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);

        } else

        {

            ActivityCompat.requestPermissions((ChatMessagesScreen) mContext, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    21);
        }


    }
}