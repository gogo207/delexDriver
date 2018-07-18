package com.example.moda.firebasebasedchat.Activities;


/*
 * Created by moda on 15/04/16.
 */

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.Utilities.TouchImageView;


/*
*
* Activity containing the full screen imageview with functionality to pinch and zoom
*
* */
public class MediaHistory_FullScreenImage extends AppCompatActivity {

    private TouchImageView imgDisplay;


    private ImageView close;

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_fullscreen_image);

        imgDisplay = (TouchImageView) findViewById(R.id.imgDisplay);


//        TextView title = (TextView) findViewById(R.id.title);
//
//
//        title.setText(R.string.string_280);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String path = extras.getString("imagePath");

            try {


                Glide
                        .with(MediaHistory_FullScreenImage.this)
                        .load(path)


                        .crossFade()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.home_grid_view_image_icon)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                imgDisplay.setBackgroundColor(ContextCompat.getColor(MediaHistory_FullScreenImage.this, R.color.color_white))
                                ;
                                return false;
                            }
                        })
                        .into(imgDisplay);


            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }


        close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                onBackPressed();

            }
        });

    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();

        supportFinishAfterTransition();
    }


}
