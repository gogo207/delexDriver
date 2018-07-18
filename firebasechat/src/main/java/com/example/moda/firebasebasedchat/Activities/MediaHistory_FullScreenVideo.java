package com.example.moda.firebasebasedchat.Activities;

/*
 * Created by moda on 15/04/16.
 */

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.moda.firebasebasedchat.R;


/**
 * Activity containing the full screen videoview to play video incase android video player is not found
 */
public class MediaHistory_FullScreenVideo extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_fullscreen_video);

        VideoView video = (VideoView) findViewById(R.id.video);


//        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Condensed.ttf");
//        TextView title = (TextView) findViewById(R.id.title);
//        title.setTypeface(tf, Typeface.BOLD);


//        title.setText(R.string.string_281);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String path = extras.getString("videoPath");
            try {
                if (extras.containsKey("flag")) {


                    video.setVideoURI(Uri.parse(path));


                } else {

                    video.setVideoPath(path);
                }

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(video);

            video.setMediaController(mediaController);


//            video.seekTo(2);
            video.start();


            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    onBackPressed();
                }
            });
        }


        ImageView close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                onBackPressed();

            }
        });
    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();
        this.supportFinishAfterTransition();
    }


}
