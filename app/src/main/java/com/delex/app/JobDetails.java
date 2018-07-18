package com.delex.app;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.pojo.AssignedAppointments;
import com.delex.utility.ImageLoadedCallback;
import com.delex.utility.Scaler;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.squareup.picasso.Picasso;

public class JobDetails extends AppCompatActivity {

    Typeface ClanaproNarrMedium, ClanaproNarrNews;
    private TextView tv_bid_value, tv_delfee_value, tv_pickup_date, tv_pickuploc, tv_drop_date, tv_droploc, tv_sender_name, tv_sender_id, iv_reciver_name, iv_reciver_id,tv_length_value,tv_width_value,tv_height_value;
    private TextView tv_goodstype_value, tv_quantity_value, tv_additonalnote;
    private AssignedAppointments appointment;
    private LinearLayout ll_inflater;
    private Dialog dialog;
    private SessionManager sessionManager;
    private ImageView iv_call_reciever,iv_call_sender;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        sessionManager = new SessionManager(JobDetails.this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        receiveBookingDetails();
        initActionBar();
        initializeViews();
    }

    /**********************************************************************************************/
    /**
     * <h1>initActionBar</h1>
     * initilize the action bar*/
    private void initActionBar() {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_back_btn);
        }
        ImageView iv_search;
        TextView tv_title;

        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.jobdetails));
        tv_title.setTypeface(ClanaproNarrMedium);



        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

    }

    /**********************************************************************************************/
                                                                      /*initializeViews*/
    /**********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {

        TextView tv_handler,tv_handler_value,tv_bid, tv_delfee, tv_pickdrop, tv_pickup, tv_drop, tv_sndandrec, tv_sender, iv_reciver, tv_shipment, tv_goodsview, tv_quantity, tv_photos, tv_additinalnote;

        tv_bid = (TextView) findViewById(R.id.tv_bid);
        tv_bid.setTypeface(ClanaproNarrNews);

        tv_delfee = (TextView) findViewById(R.id.tv_delfee);
        tv_delfee.setTypeface(ClanaproNarrNews);

        tv_pickdrop = (TextView) findViewById(R.id.tv_pickdrop);
        tv_pickdrop.setTypeface(ClanaproNarrNews);

        tv_pickup = (TextView) findViewById(R.id.tv_pickup);
        tv_pickup.setTypeface(ClanaproNarrNews);

        tv_drop = (TextView) findViewById(R.id.tv_drop);
        tv_drop.setTypeface(ClanaproNarrNews);

        tv_sndandrec = (TextView) findViewById(R.id.tv_sndandrec);
        tv_sndandrec.setTypeface(ClanaproNarrNews);

        tv_sender = (TextView) findViewById(R.id.tv_sender);
        tv_sender.setTypeface(ClanaproNarrNews);

        iv_reciver = (TextView) findViewById(R.id.iv_reciver);
        iv_reciver.setTypeface(ClanaproNarrNews);

        tv_shipment = (TextView) findViewById(R.id.tv_shipment);
        tv_shipment.setTypeface(ClanaproNarrNews);

        tv_goodsview = (TextView) findViewById(R.id.tv_goodsview);
        tv_goodsview.setTypeface(ClanaproNarrNews);

        tv_quantity = (TextView) findViewById(R.id.tv_quantity);
        tv_quantity.setTypeface(ClanaproNarrNews);

        tv_photos = (TextView) findViewById(R.id.tv_photos);
        tv_photos.setTypeface(ClanaproNarrNews);

        tv_handler = (TextView) findViewById(R.id.tv_handler);
        tv_handler.setTypeface(ClanaproNarrNews);



        tv_additinalnote = (TextView) findViewById(R.id.tv_additinalnote);
        tv_additinalnote.setTypeface(ClanaproNarrNews);

        ll_inflater = (LinearLayout) findViewById(R.id.ll_inflater);
        /***************************************************/


        tv_handler_value = (TextView) findViewById(R.id.tv_handler_value);
        tv_handler_value.setTypeface(ClanaproNarrNews);
        tv_handler_value.setText(appointment.getHelpers());

        tv_bid_value = (TextView) findViewById(R.id.tv_bid_value);
        tv_bid_value.setTypeface(ClanaproNarrNews);
        tv_bid_value.setText(appointment.getBid());

        tv_delfee_value = (TextView) findViewById(R.id.tv_delfee_value);
        tv_delfee_value.setTypeface(ClanaproNarrNews);
        tv_delfee_value.setText(sessionManager.getCurrencySymbol() + appointment.getShipemntDetails().get(0).getFare());

        tv_pickup_date = (TextView) findViewById(R.id.tv_pickup_date);
        tv_pickup_date.setTypeface(ClanaproNarrNews);
        tv_pickup_date.setText(Utility.formatDate(appointment.getApntDate()));

        tv_pickuploc = (TextView) findViewById(R.id.tv_pickuploc);
        tv_pickuploc.setTypeface(ClanaproNarrNews);
        tv_pickuploc.setText(appointment.getAddrLine1());

        tv_drop_date = (TextView) findViewById(R.id.tv_drop_date);
        tv_drop_date.setTypeface(ClanaproNarrNews);
        tv_drop_date.setText(Utility.formatDate(appointment.getDrop_dt()));

        tv_droploc = (TextView) findViewById(R.id.tv_droploc);
        tv_droploc.setTypeface(ClanaproNarrNews);
        tv_droploc.setText(appointment.getDropLine1());

        tv_sender_name = (TextView) findViewById(R.id.tv_sender_name);
        tv_sender_name.setTypeface(ClanaproNarrNews);
        tv_sender_name.setText(appointment.getCustomerName());

        tv_sender_id = (TextView) findViewById(R.id.tv_sender_id);
        tv_sender_id.setTypeface(ClanaproNarrNews);
        tv_sender_id.setText(appointment.getCustomerCountryCode()+appointment.getCustomerPhone());

        iv_reciver_name = (TextView) findViewById(R.id.iv_reciver_name);
        iv_reciver_name.setTypeface(ClanaproNarrNews);
        iv_reciver_name.setText(appointment.getShipemntDetails().get(0).getName());

        iv_reciver_id = (TextView) findViewById(R.id.iv_reciver_id);
        iv_reciver_id.setTypeface(ClanaproNarrNews);
        iv_reciver_id.setText(appointment.getCustomerCountryCode()+appointment.getShipemntDetails().get(0).getMobile());

        tv_goodstype_value = (TextView) findViewById(R.id.tv_goodstype_value);
        tv_goodstype_value.setTypeface(ClanaproNarrNews);
        tv_goodstype_value.setText(appointment.getShipemntDetails().get(0).getGoodType());


        tv_length_value=(TextView)findViewById(R.id.tv_length_value);
        tv_length_value.setTypeface(ClanaproNarrNews);
        tv_length_value.setText(appointment.getShipemntDetails().get(0).getLength()+appointment.getShipemntDetails().get(0).getDimensionUnit());

        tv_width_value=(TextView)findViewById(R.id.tv_width_value);
        tv_width_value.setTypeface(ClanaproNarrNews);
        tv_width_value.setText(appointment.getShipemntDetails().get(0).getWidth()+appointment.getShipemntDetails().get(0).getDimensionUnit());

        tv_height_value=(TextView)findViewById(R.id.tv_height_value);
        tv_height_value.setTypeface(ClanaproNarrNews);
        tv_height_value.setText(appointment.getShipemntDetails().get(0).getHeight()+appointment.getShipemntDetails().get(0).getDimensionUnit());


        tv_quantity_value = (TextView) findViewById(R.id.tv_quantity_value);
        tv_quantity_value.setTypeface(ClanaproNarrNews);

        if (!appointment.getShipemntDetails().get(0).getQuantity().equals("")) {
            tv_quantity_value.setText(appointment.getShipemntDetails().get(0).getQuantity());
        }

        tv_additonalnote = (TextView) findViewById(R.id.tv_additonalnote_val);
        tv_additonalnote.setTypeface(ClanaproNarrNews);

        if (!appointment.getExtraNotes().isEmpty()) {
            tv_additonalnote.setText(appointment.getExtraNotes());
        }

        iv_call_reciever = (ImageView) findViewById(R.id.iv_call_reciever);
        iv_call_reciever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.MakePhoneCall(appointment.getShipemntDetails().get(0).getMobile(),appointment.getCustomerCountryCode(),JobDetails.this);
            }
        });
        iv_call_sender = (ImageView) findViewById(R.id.iv_call_sender);
        iv_call_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.MakePhoneCall(appointment.getCustomerPhone(),appointment.getCustomerCountryCode(), JobDetails.this);
            }
        });

        mAddJobPhotos();
    }

    private void receiveBookingDetails() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            appointment = (AssignedAppointments) bundle.getSerializable("data");
        }
    }
    /**********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mAddJobPhotos() {

        double size[] = Scaler.getScalingFactor(JobDetails.this);
        int no_of_images = 0;
        String[] photos = appointment.getShipemntDetails().get(0).getPhoto();

        if (photos.length > 0) {
            no_of_images = photos.length;

            for (int i = 0; i < no_of_images; i++) {
                final String url = photos[i];

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.single_row_job_photos, null);

                ImageView imageView = (ImageView) view.findViewById(R.id.jobPhoto);
                imageView.setBackgroundResource(R.drawable.job_details_product_default_image);

                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);


                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBigImage(Uri.parse(url));
                    }
                });

                ll_inflater.addView(view);


                Utility.printLog();
                int height = (int) size[0] * 50;
                int width = (int) size[1] * 50;
                try {

                    Picasso.with(this)
                            .load(url)
                            .resize(height, width)
                            .placeholder(R.drawable.job_details_product_default_image)
                            .into(imageView, new ImageLoadedCallback(progressBar));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.single_row_job_photos, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.jobPhoto);

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);

            TextView textView = (TextView) view.findViewById(R.id.tvNoImages);
            textView.setTypeface(ClanaproNarrNews);

            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            ll_inflater.addView(view);
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showBigImage(Uri url)
    {
        dialog=new Dialog(JobDetails.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_imageview);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView bigImageView= (ImageView) dialog.findViewById(R.id.ivBigImage);

        Picasso.with(JobDetails.this)
                .load(url)
                .into(bigImageView);

        dialog.show();

    }
}
