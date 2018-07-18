package com.delex.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.delex.driver.R;
import com.delex.utility.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by embed on 16/5/17.
 */

public class ImageUploadRVA extends RecyclerView.Adapter<ImageUploadRVA.ViewHolder> {

    RemoveImage removeImage;
    private View view;
    private Context context;
    private ArrayList<String> imagefile;
    private ImageView iv_licence_delete, iv_licence;
    private Dialog dialog;

    /**********************************************************************************************/
    public ImageUploadRVA(Context context, ArrayList<String> imagefile, RemoveImage removeImage) {
        this.context = context;
        this.imagefile = imagefile;
        this.removeImage = removeImage;


    }

    /**********************************************************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_camera_uploader, parent, false);
        return new ImageUploadRVA.ViewHolder(view);
    }

    /**********************************************************************************************/
    @Override
    public void onBindViewHolder(ImageUploadRVA.ViewHolder holder, final int position) {

        iv_licence_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage.ImageRemoved(position);
            }
        });
        iv_licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBigImage(Uri.parse(imagefile.get(position)));
            }
        });

        Utility.printLog("ImageUploadRVA Position: "+position+" link: "+imagefile.get(position));
        Picasso.with(context)
                .load(imagefile.get(position))
//                .placeholder(R.drawable.piccaso_load_animation)
                .into(iv_licence);
    }

    /**********************************************************************************************/
    @Override
    public int getItemCount() {
        return imagefile.size();
    }

    public interface RemoveImage {
        void ImageRemoved(int position);
    }

    /**********************************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

            iv_licence_delete = (ImageView) itemView.findViewById(R.id.iv_licence_delete);
            iv_licence = (ImageView) itemView.findViewById(R.id.iv_licence);

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showBigImage(Uri url)
    {
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_imageview);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView bigImageView= (ImageView) dialog.findViewById(R.id.ivBigImage);

        Picasso.with(context)
                .load(url)
                .into(bigImageView);

        dialog.show();

    }
}
