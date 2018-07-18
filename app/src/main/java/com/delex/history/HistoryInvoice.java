package com.delex.history;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.pojo.TripsPojo.Appointments;
import com.delex.utility.ImageLoadedCallback;
import com.delex.utility.Scaler;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**************************************************************************************************/
public class HistoryInvoice extends AppCompatActivity {

    TextView tv_distancefare_value, tv_timefare_value, tv_discount_value, tv_subtotal_value, tv_addspecial_charges, tv_waiting_char_value,tv_length_value,tv_width_value,tv_height_value;
    TextView tv_tollchar_value, tv_grandtotal_value, tv_cardend_value, tv_rec_name, tv_rec_id, tv_received_name, tv_received_id,tv_vat_value,tv_doc_no,tv_subtotal_fee;
    private Typeface ClanaproNarrMedium, ClanaproNarrNews, ClanaproMedium;
    private SessionManager sessionManager;
    LinearLayout ll_basefare,ll_distancefare,ll_timefare,ll_waitingfare,ll_toll,ll_handling,ll_discountfare,ll_vat;
    private ImageView iv_prof_img_his, ivSignature,ivDoccuments;
    private TextView tvEarnedAmt_value, tvAppCommision_value, tv_handling_value, tv_baseFare_value, tvTollFare_value, tvWaitingFee_value, tv_sendername, tv_sender_id, tv_total_charge, tv_rating_num, tv_mi_value, tv_min_value, tv_pickuploc, tv_popup_droploc;
    private Appointments appointment;
    private LinearLayout ll_inflater;
    private TextView tv_load_type_title,tv_load_type_value,tv_quantity_title,tv_quantity_value;
    private Dialog dialog;
    private  double vat_amount=0.0;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_invoice);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        sessionManager = new SessionManager(HistoryInvoice.this);
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        appointment = (Appointments) getIntent().getSerializableExtra("data");

        Log.d("adas",appointment.getAddrLine1());
        initActionBar();
        initializeViews();

        if (appointment != null) {
            setValues();
        }
    }

    /**********************************************************************************************/
    /**
     * <h1>initActionBar</h1>
     * initilize the action bar
     */
    private void initActionBar() {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        ClanaproMedium = Typeface.createFromAsset(getAssets(), "fonts/CLANPRO-MEDIUM.OTF");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_back_btn);
        }

        TextView tv_title, tv_bid, tv_help;
        LinearLayout ll_actionbar;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MMM dd", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        try {

            Date apnt = dateFormat.parse(appointment.getApntDt());
            tv_title.setText(simpleDateFormat.format(apnt));

        } catch (Exception e) {
            Utility.printLog("HistoryInvoice Crash" + e.getMessage());
            e.printStackTrace();
        }
        tv_title.setTypeface(ClanaproNarrNews);
        tv_title.setTextSize(13);

        tv_bid = (TextView) findViewById(R.id.tv_bid);
        tv_bid.setTypeface(ClanaproNarrMedium);
        tv_bid.setText("ID: " + appointment.getBid());

        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_help.setTypeface(ClanaproNarrMedium);
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryInvoice.this, com.livechatinc.inappchat.ChatWindowActivity.class);
                intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, "your_group_id");
                intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, "9354755");
                startActivity(intent);
            }
        });

        ll_actionbar = (LinearLayout) findViewById(R.id.ll_actionbar);
        ll_actionbar.setPadding(-120, 0, 0, 0);
    }

    /**********************************************************************************************/
                                                                      /*initializeViews*/
    /**********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {

        TextView tv_senderdet, tv_yourated, tv_mi, tv_min;
        TextView tv_bil_detail, tv_distancefare, tv_timefare, tv_discount, tv_subtotal, tv_waiting_char, tv_tollcharge, tv_grandtotal, tv_payment,tv_vat;
        TextView tv_cardend, tv_receiverdetail, tv_rec_by, tv_document, tv_baseFare, tv_handling, tvWaitingFee, tvTollFee, tvEarnedAmt, tvAppCommision;

        tv_senderdet = (TextView) findViewById(R.id.tv_senderdet);
        tv_senderdet.setTypeface(ClanaproNarrNews);

        tv_yourated = (TextView) findViewById(R.id.tv_yourated);
        tv_yourated.setTypeface(ClanaproNarrNews);

        tv_vat=(TextView)findViewById(R.id.tv_vat);
        tv_vat.setTypeface(ClanaproNarrNews);

        tv_mi = (TextView) findViewById(R.id.tv_mi);
        tv_mi.setTypeface(ClanaproNarrNews);

        tv_min = (TextView) findViewById(R.id.tv_min);
        tv_min.setTypeface(ClanaproNarrNews);

        tv_bil_detail = (TextView) findViewById(R.id.tv_bil_detail);
        tv_bil_detail.setTypeface(ClanaproNarrNews);

        tv_distancefare = (TextView) findViewById(R.id.tv_distancefare);
        tv_distancefare.setTypeface(ClanaproNarrNews);

        tv_timefare = (TextView) findViewById(R.id.tv_timefare);
        tv_timefare.setTypeface(ClanaproNarrNews);

        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_discount.setTypeface(ClanaproNarrNews);

        tv_subtotal = (TextView) findViewById(R.id.tv_subtotal);
        tv_subtotal.setTypeface(ClanaproNarrMedium);

        tv_grandtotal = (TextView) findViewById(R.id.tv_grandtotal);
        tv_grandtotal.setTypeface(ClanaproNarrMedium);

        tv_payment = (TextView) findViewById(R.id.tv_payment);
        tv_payment.setTypeface(ClanaproNarrNews);

        tv_cardend = (TextView) findViewById(R.id.tv_cardend);
        tv_cardend.setTypeface(ClanaproNarrNews);



        tv_receiverdetail = (TextView) findViewById(R.id.tv_receiverdetail);
        tv_receiverdetail.setTypeface(ClanaproNarrNews);

        tv_rec_by = (TextView) findViewById(R.id.tv_rec_by);
        tv_rec_by.setTypeface(ClanaproNarrNews);

        tv_document = (TextView) findViewById(R.id.tv_document);
        tv_document.setTypeface(ClanaproNarrNews);

        tv_baseFare = (TextView) findViewById(R.id.tv_baseFare);
        tv_baseFare.setTypeface(ClanaproNarrNews);

        tv_handling = (TextView) findViewById(R.id.tv_handling);
        tv_handling.setTypeface(ClanaproNarrNews);

        tvWaitingFee = (TextView) findViewById(R.id.tvWaitingFee);
        tvWaitingFee.setTypeface(ClanaproNarrNews);

        tvTollFee = (TextView) findViewById(R.id.tvTollFee);
        tvTollFee.setTypeface(ClanaproNarrNews);

        tvEarnedAmt = (TextView) findViewById(R.id.tvEarnedAmt);
        tvEarnedAmt.setTypeface(ClanaproNarrNews);

        tvAppCommision = (TextView) findViewById(R.id.tvAppCommision);
        tvAppCommision.setTypeface(ClanaproNarrNews);

        /**************************************************************************/

        iv_prof_img_his = (ImageView) findViewById(R.id.iv_prof_img_his);
        ivSignature = (ImageView) findViewById(R.id.ivSignature);
        //ivDoccuments=(ImageView)findViewById(R.id.ivDoccuments);

        ll_basefare=(LinearLayout)findViewById(R.id.ll_basefare);
        ll_distancefare=(LinearLayout)findViewById(R.id.ll_distancefare);
        ll_timefare=(LinearLayout)findViewById(R.id.ll_timefare);
        ll_waitingfare=(LinearLayout)findViewById(R.id.ll_waitingfare);
        ll_toll=(LinearLayout)findViewById(R.id.ll_toll);
        ll_handling=(LinearLayout)findViewById(R.id.ll_handling);
        ll_discountfare=(LinearLayout)findViewById(R.id.ll_discountfare);


        tv_sendername = (TextView) findViewById(R.id.tv_sendername);
        tv_sendername.setTypeface(ClanaproNarrNews);

        tv_sender_id = (TextView) findViewById(R.id.tv_sender_id);
        tv_sender_id.setTypeface(ClanaproNarrNews);

        tv_handling_value = (TextView) findViewById(R.id.tv_handling_value);
        tv_handling_value.setTypeface(ClanaproNarrNews);

        tvEarnedAmt_value = (TextView) findViewById(R.id.tvEarnedAmt_value);
        tvEarnedAmt_value.setTypeface(ClanaproNarrNews);

        tvAppCommision_value = (TextView) findViewById(R.id.tvAppCommision_value);
        tvAppCommision_value.setTypeface(ClanaproNarrNews);

        tv_baseFare_value = (TextView) findViewById(R.id.tv_baseFare_value);
        tv_baseFare_value.setTypeface(ClanaproNarrNews);

        tvTollFare_value = (TextView) findViewById(R.id.tvTollFare_value);
        tvTollFare_value.setTypeface(ClanaproNarrNews);


        tv_load_type_value=(TextView)findViewById(R.id.goods_type_value) ;
        tv_load_type_value.setTypeface(ClanaproNarrNews);

        tv_quantity_value=(TextView)findViewById(R.id.quantity_value);
        tv_quantity_value.setTypeface(ClanaproNarrNews);

        tv_timefare_value = (TextView) findViewById(R.id.tv_timefare_value);
        tv_timefare_value.setTypeface(ClanaproMedium);

        tvWaitingFee_value = (TextView) findViewById(R.id.tvWaitingFee_value);
        tvWaitingFee_value.setTypeface(ClanaproNarrNews);

        tv_total_charge = (TextView) findViewById(R.id.tv_total_charge);
        tv_total_charge.setTypeface(ClanaproMedium);

        tv_rating_num = (TextView) findViewById(R.id.tv_rating_num);
        tv_rating_num.setTypeface(ClanaproNarrNews);

        tv_mi_value = (TextView) findViewById(R.id.tv_mi_value);
        tv_mi_value.setTypeface(ClanaproNarrNews);

        tv_min_value = (TextView) findViewById(R.id.tv_min_value);
        tv_min_value.setTypeface(ClanaproNarrNews);

        tv_pickuploc = (TextView) findViewById(R.id.tv_pickuploc);
        tv_pickuploc.setTypeface(ClanaproNarrNews);

        tv_popup_droploc = (TextView) findViewById(R.id.tv_popup_droploc);
        tv_popup_droploc.setTypeface(ClanaproNarrNews);

        tv_distancefare_value = (TextView) findViewById(R.id.tv_distancefare_value);
        tv_distancefare_value.setTypeface(ClanaproNarrNews);

        tv_timefare_value = (TextView) findViewById(R.id.tv_timefare_value);
        tv_timefare_value.setTypeface(ClanaproNarrNews);

        tv_discount_value = (TextView) findViewById(R.id.tv_discount_value);
        tv_discount_value.setTypeface(ClanaproNarrNews);

        tv_subtotal_value = (TextView) findViewById(R.id.tv_subtotal_value);
        tv_subtotal_value.setTypeface(ClanaproNarrMedium);

        tv_addspecial_charges = (TextView) findViewById(R.id.tv_addspecial_charges);
        tv_addspecial_charges.setTypeface(ClanaproNarrNews);

        tv_grandtotal_value = (TextView) findViewById(R.id.tv_grandtotal_value);
        tv_grandtotal_value.setTypeface(ClanaproNarrMedium);


        ll_vat=(LinearLayout)findViewById(R.id.ll_vat);

        tv_subtotal_fee=(TextView)findViewById(R.id.tv_subtotal_fare);
        tv_subtotal_fee.setTypeface(ClanaproMedium);
        tv_vat_value = (TextView) findViewById(R.id.tv_vat_value);
        tv_vat_value.setTypeface(ClanaproMedium);

        tv_length_value=(TextView)findViewById(R.id.tv_length_value);
        tv_length_value.setTypeface(ClanaproNarrNews);
        tv_length_value.setText(appointment.getShipemntDetails().get(0).getLength()+appointment.getShipemntDetails().get(0).getDimensionUnit());

        tv_width_value=(TextView)findViewById(R.id.tv_width_value);
        tv_width_value.setTypeface(ClanaproNarrNews);
        tv_width_value.setText(appointment.getShipemntDetails().get(0).getWidth()+appointment.getShipemntDetails().get(0).getDimensionUnit());

        tv_height_value=(TextView)findViewById(R.id.tv_height_value);
        tv_height_value.setTypeface(ClanaproNarrNews);
        tv_height_value.setText(appointment.getShipemntDetails().get(0).getHeight()+appointment.getShipemntDetails().get(0).getDimensionUnit());





        tv_cardend_value = (TextView) findViewById(R.id.tv_cardend_value);
        tv_cardend_value.setTypeface(ClanaproNarrNews);

        tv_rec_name = (TextView) findViewById(R.id.tv_rec_name);
        tv_rec_name.setTypeface(ClanaproNarrNews);

        tv_rec_id = (TextView) findViewById(R.id.tv_rec_id);
        tv_rec_id.setTypeface(ClanaproNarrNews);

        tv_received_name = (TextView) findViewById(R.id.tv_received_name);
        tv_received_name.setTypeface(ClanaproNarrNews);

        tv_received_id = (TextView) findViewById(R.id.tv_received_id);
        tv_received_id.setTypeface(ClanaproNarrNews);

        ll_inflater = (LinearLayout) findViewById(R.id.ll_inflater);

        mAddJobPhotos();
    }

    /**********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        }
        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/

    public void setValues() {
        sessionManager = new SessionManager(HistoryInvoice.this);
        tv_sendername.setText(appointment.getCustomerName());

        tv_sender_id.setText(appointment.getCustomerPhone());



        tv_rating_num.setText(appointment.getRating());

        if (appointment.getPricingType()==0){
            ll_basefare.setVisibility(View.VISIBLE);
            ll_distancefare.setVisibility(View.VISIBLE);
            ll_timefare.setVisibility(View.VISIBLE);
            ll_waitingfare.setVisibility(View.VISIBLE);
            ll_handling.setVisibility(View.VISIBLE);

        }else if (appointment.getPricingType()==1){
            ll_basefare.setVisibility(View.VISIBLE);
            ll_distancefare.setVisibility(View.GONE);
            ll_timefare.setVisibility(View.GONE);
            ll_waitingfare.setVisibility(View.VISIBLE);
            ll_handling.setVisibility(View.VISIBLE);

        }else if (appointment.getPricingType()==2){
            ll_basefare.setVisibility(View.VISIBLE);
            ll_distancefare.setVisibility(View.GONE);
            ll_timefare.setVisibility(View.GONE);
            ll_waitingfare.setVisibility(View.VISIBLE);
            ll_handling.setVisibility(View.VISIBLE);

        }

       Log.d("taxena",appointment.getInvoice().getTaxEnable()+"");

        if (appointment.getInvoice().getTaxEnable()){
            ll_vat.setVisibility(View.VISIBLE);

            Log.d("taxvball",appointment.getInvoice().getTax()+""+appointment.getInvoice().getTax());


            TextView tv_vat = (TextView) findViewById(R.id.tv_vat);
            tv_vat.setTypeface(ClanaproMedium);
            tv_vat.setText(appointment.getInvoice().getTaxTitle());
            tv_vat_value.setText(sessionManager.getCurrencySymbol()+""+ appointment.getInvoice().getTax());
        }else {

            ll_vat.setVisibility(View.GONE);
        }
        tv_total_charge.setText(sessionManager.getCurrencySymbol()+appointment.getInvoice().getTotal());
     /*   if (appointment.getInvoice().getBaseFare().equals("0.00")){
            ll_basefare.setVisibility(View.GONE);

        }else if (!appointment.getInvoice().getBaseFare().equals("0.00")){
            ll_basefare.setVisibility(View.VISIBLE);

        } if (appointment.getInvoice().getTimeFare().equals("0.00")){
            ll_timefare.setVisibility(View.GONE);

        }else  if (!appointment.getInvoice().getTimeFare().equals("0.00")){
            ll_timefare.setVisibility(View.VISIBLE);

        } if (appointment.getInvoice().getWatingFee().equals("0.00")){
            ll_waitingfare.setVisibility(View.GONE);

        }else  if (!appointment.getInvoice().getWatingFee().equals("0.00")){
            ll_waitingfare.setVisibility(View.VISIBLE);

        }
        if (appointment.getInvoice().getDistFare().equals("")){
            ll_distancefare.setVisibility(View.GONE);

        }else  if (!appointment.getInvoice().getDistFare().equals("")){
            ll_distancefare.setVisibility(View.VISIBLE);

        }*/
        if (appointment.getInvoice().getTollFee().equals("0")){
            ll_toll.setVisibility(View.GONE);

        }else if (!appointment.getInvoice().getTollFee().equals("0")) {
            ll_toll.setVisibility(View.VISIBLE);
        }
            if (appointment.getInvoice().getWatingFee().equals("0.00")){
                ll_waitingfare.setVisibility(View.GONE);

            }else  if (!appointment.getInvoice().getWatingFee().equals("0.00")) {
                ll_waitingfare.setVisibility(View.VISIBLE);
            }
      /*  }  if (appointment.getInvoice().getHandlingFee()==0){
            ll_handling.setVisibility(View.GONE);

        }else  if (appointment.getInvoice().getHandlingFee()!=0){
            ll_handling.setVisibility(View.VISIBLE);

        }*/
            if (appointment.getInvoice().getDiscount().equals("0.00")) {
                ll_discountfare.setVisibility(View.GONE);

            } else if (!appointment.getInvoice().getDiscount().equals("0.00")) {
                ll_discountfare.setVisibility(View.VISIBLE);

            }

            if (appointment.getInvoice().getHandlingFee()==0){
                ll_handling.setVisibility(View.GONE);
            }else {
                ll_handling.setVisibility(View.VISIBLE);
            }
      /*  if (appointment.getInvoice().getVAT().equals("0")){
            ll_vat.setVisibility(View.GONE);

        }else  if (!appointment.getInvoice().getVAT().equals("0")){
            ll_vat.setVisibility(View.VISIBLE);

        }*/




      if (appointment.getShipemntDetails().get(0).getPhoto().isEmpty()){

      }

      if (appointment.getShipemntDetails().get(0).getGoodType().isEmpty()){
          tv_load_type_value.setText(R.string.notenterd);
      }else {
          tv_load_type_value.setText(appointment.getShipemntDetails().get(0).getGoodType());
      }

      if (appointment.getShipemntDetails().get(0).getQuantity().isEmpty()){
          tv_quantity_value.setText(R.string.notenterd);
      }else {
          tv_quantity_value.setText(appointment.getShipemntDetails().get(0).getQuantity());
      }

            int day = (int)TimeUnit.SECONDS.toDays(appointment.getInvoice().getTotalTime());
            long hours = TimeUnit.SECONDS.toHours(appointment.getInvoice().getTotalTime()) - (day *24);
            long minute = TimeUnit.SECONDS.toMinutes(appointment.getInvoice().getTotalTime()) - (TimeUnit.SECONDS.toHours(appointment.getInvoice().getTotalTime())* 60);
            long second = TimeUnit.SECONDS.toSeconds(appointment.getInvoice().getTotalTime()) - (TimeUnit.SECONDS.toMinutes(appointment.getInvoice().getTotalTime()) *60);
           /* long hours = TimeUnit.SECONDS.toHours(appointment.getInvoice().getTotalTime());
            long minute = TimeUnit.SECONDS.toMinutes(appointment.getInvoice().getTotalTime());*/

            tv_mi_value.setText(appointment.getInvoice().getDistance() + sessionManager.getmileage_metric());


            if (hours==0){
                tv_min_value.setText(minute+"mins :"+second+"secs");
            }else if (hours!=0)
            {
                tv_min_value.setText(hours+"hrs :"+minute+"mins :"+second+"secs");
            }

            tv_pickuploc.setText(appointment.getAddrLine1());
            Log.d("adfrs",appointment.getAddrLine1()+" "+appointment.getDropLine1());

            tv_popup_droploc.setText(appointment.getDropLine1());

            tv_subtotal_fee.setText(sessionManager.getcurrencySymbol() + "" + appointment.getInvoice().getSubtotal());

            tv_distancefare_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getDistFare());

            tv_timefare_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getTimeFare());

            tv_baseFare_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getBaseFare());

            tvWaitingFee_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getWatingFee());

            tvTollFare_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getTollFee());

            tv_handling_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getHandlingFee());

            tv_discount_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getDiscount());

            tv_subtotal_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getTotal());

            tvEarnedAmt_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getMasEarning());

            tvAppCommision_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getAppcom());


            //tv_vat_value.setText(sessionManager.getcurrencySymbol() + " " + appointment.getInvoice().getVAT());
            //Log.i("vb",appointment.getBookingInvoice().getVatPercentage()+"");
            tv_received_name.setText(appointment.getShipemntDetails().get(0).getName());

            tv_received_id.setText(appointment.getShipemntDetails().get(0).getMobile());

            tv_cardend_value.setText(appointment.getPaymentType());

            if (!appointment.getShipemntDetails().get(0).getSignatureUrl().isEmpty()) {

                Picasso.with(HistoryInvoice.this)
                        .load(appointment.getShipemntDetails().get(0).getSignatureUrl())
                        .into(ivSignature);
            }



    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mAddJobPhotos() {

        double size[] = Scaler.getScalingFactor(HistoryInvoice.this);
        int no_of_images = 0;
        ArrayList<String> photos = appointment.getShipemntDetails().get(0).getDocumentImage();
       // Log.d("emptyui",appointment.getShipemntDetails().get(0).getDocumentImage().get(0));

   try {
      if (!appointment.getShipemntDetails().get(0).getDocumentImage().get(0).equals("")) {
        no_of_images = photos.size();

        for (int i = 0; i < no_of_images; i++) {
            final String url = (String) photos.get(i);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.single_row_job_photos, null);
            if(sessionManager.getLang().equalsIgnoreCase("ar")){
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.jobPhoto);
            TextView textView = (TextView) view.findViewById(R.id.tvNoImages);
            textView.setTypeface(ClanaproNarrNews);
            imageView.setPadding(10,10,10,10);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBigImage(Uri.parse(url));
                }
            });

            ll_inflater.addView(view);


            Utility.printLog();
            int height = (int) size[0] * 100;
            int width = (int) size[1] * 100;
            try {

                Picasso.with(this)
                        .load(url)
                        .resize(height, width)
                        .placeholder(R.drawable.job_details_product_default_image)
                        .into(imageView, new ImageLoadedCallback(progressBar));

            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("emptyui",appointment.getShipemntDetails().get(0).getDocumentImage().get(0));

        }
    } else if (appointment.getShipemntDetails().get(0).getDocumentImage().get(0).equals("")){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_row_job_photos, null);
       /* if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }*/
        ImageView imageView = (ImageView) view.findViewById(R.id.jobPhoto);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);

        TextView textView = (TextView) view.findViewById(R.id.tvNoImages);
        textView.setTypeface(ClanaproNarrNews);
        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(R.string.NO_IMAGES);

        ll_inflater.addView(view);
    }
}catch (Exception e){

       LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
       View view = inflater.inflate(R.layout.single_row_job_photos, null);
       /* if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }*/
       ImageView imageView = (ImageView) view.findViewById(R.id.jobPhoto);

       ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);

       TextView textView = (TextView) view.findViewById(R.id.tvNoImages);
       textView.setTypeface(ClanaproNarrNews);
       imageView.setVisibility(View.GONE);
       textView.setVisibility(View.VISIBLE);
       textView.setText(R.string.NO_IMAGES);

       ll_inflater.addView(view);

}


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showBigImage(Uri url)
    {
        dialog=new Dialog(HistoryInvoice.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_imageview);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView bigImageView= (ImageView) dialog.findViewById(R.id.ivBigImage);

        Picasso.with(HistoryInvoice.this)
                .load(url)
                .into(bigImageView);

        dialog.show();

    }
}
