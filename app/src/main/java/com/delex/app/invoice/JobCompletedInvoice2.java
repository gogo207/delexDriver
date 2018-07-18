package com.delex.app.invoice;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.delex.adapter.ImageUploadRVA;
import com.delex.adapter.SpecialChargeRVA;
import com.delex.app.MainActivity;
import com.delex.driver.R;
import com.delex.pojo.AssignedAppointments;
import com.delex.pojo.BookingInvoice;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.ImageEditUpload;
import com.delex.utility.MyImageHandler;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Upload_file_AmazonS3;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import eu.janmuller.android.simplecropimage.CropImage;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**************************************************************************************************/

/**
 * this is the activity which show after complete job.
 */

public class JobCompletedInvoice2 extends AppCompatActivity implements View.OnClickListener, SignaturePad.OnSignedListener, EasyPermissions.PermissionCallbacks {

    private static final int CAMERA_PIC = 11, GALLERY_PIC = 12, CROP_IMAGE = 13, REMOVE_IMAGE = 14;
    String base64dependentPic = "";
    private Typeface ClanaproNarrMedium, ClanaproNarrNews, ClanaproMedium;
    private SessionManager sessionManager;
    private TextView tv_waitingfare_value;
    private TextView tv_discount_value;
    private TextView tv_subtotal_value;
    private TextView tv_grandtotal_value;
    private TextView tv_confirm_bill;
    private TextView tv_cannot_edit;
    private TextView tv_submit;
    private TextView tv_bid;
    private TextView tv_adjustment_value;
    private TextView tv_bid_value,tv_vat_per,tv_vat_value_per;
    private EditText et_tollFee_value, et_handling_value;
    private SignaturePad signPad;
    private float rating;
    private LinearLayout ll_invoice_below,ll_vat_per;
    private EditText et_recievername, et_phonenum, et_tollFeeValue;
    private ProgressDialog mDialog;
    private String bid, cust_name, cust_phone;
    private double calculatedFare, grandTotalFarewithVat,vatAmount;
    private ImageEditUpload imageEditUpload;
    private BookingInvoice invoice;
    private Bitmap signBitmap;
    private String fileType = "image", fileName, takenNewImage, state;
    private String ImageType = "", profile_pic = "profile_pic", licence_pic = "add_licence";
    private String TAG = JobCompletedInvoice2.class.getSimpleName();
    private File takenNewSignature;
    private String newSignatureName;
    private ImageUploadRVA imageUploadRVA;
    private ArrayList<String> imagefile;
    private int imageCount;
    private Double grand_total = 0.0, toll_fee = 0.0, handling_fee = 0.0,vat_perc =0.0,finalTotal=0.0,sub_total=0.0;
    private AssignedAppointments appointment;
    private LinearLayout ll_basefare,ll_distancefare,ll_timefare,ll_waitingfare;
    private RelativeLayout rl_handling;
    private ScrollView scrollView;
    private String signatureUrl="";
    private int rate=0;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_completed_invoice2);

        sessionManager = SessionManager.getSessionManager(JobCompletedInvoice2.this);
        imagefile = new ArrayList<>();

        getAppointmentData();
        initActionBar();
        initializeViews();
    }

    private void getAppointmentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            appointment= (AssignedAppointments) bundle.getSerializable("appointment");
            bid = appointment.getBid();
            cust_name = appointment.getShipemntDetails().get(0).getName();
            cust_phone = appointment.getShipemntDetails().get(0).getMobile();
            invoice =  appointment.getInvoice();
            //
         //   vatAmount=bundle.getDouble("vat");
           //vatAmount= Double.parseDouble(appointment.getInvoice().getTaxPercentage());
           // Log.i("taxperc",appointment.getInvoice().getTaxPercentage());
            newSignatureName = bid + ".jpg";
            Utility.printLog(TAG + " Customer name " + cust_name + " customer mob " + cust_phone+" VAT amount "+vatAmount);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        methodRequiresOnePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUI(findViewById(R.id.rl_invoice_main));
    }

    /**********************************************************************************************/
    @AfterPermissionGranted(VariableConstant.RC_READ_WRITE_CAMERA_STATE)
    private void methodRequiresOnePermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.read_storage_and_camera_state_permission_message),
                    VariableConstant.RC_READ_WRITE_CAMERA_STATE, perms);
        }
    }
    /**********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_signup_close);
        }

        TextView tv_title, tv_help;

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setTypeface(ClanaproNarrMedium);

        tv_bid = (TextView) findViewById(R.id.tv_bid);
        tv_bid.setTypeface(ClanaproNarrNews);
        tv_bid.setText(getResources().getString(R.string.bid) + " " + bid);


        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_help.setTypeface(ClanaproNarrMedium);
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobCompletedInvoice2.this, com.livechatinc.inappchat.ChatWindowActivity.class);
                intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, "your_group_id");
                intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, "9354755");
                startActivity(intent);
            }
        });
    }

    /**********************************************************************************************/
                                                                      /*initializeViews*/
    /**********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {

        final TextView tv_paid_by_rcvr,tv_additional_charges, tv_charges, et_bil_detail, tv_distancefare, tv_timefare, tv_discount, tv_subtotal,tv_vat_value,tv_vat,tv_grandvat, tv_addspecial_charges, tv_grandtotal, tv_deliveredto, tv_adjustment, tv_currency1, tv_currency, tv_signature, tv_photo_doc, tv_takephoto, tv_rate_cust, tv_basefare, tv_tollFee, tv_waitingfare, tv_subtotalfare, tv_handling;

        final  LinearLayout ll_vat;
        mDialog = new ProgressDialog(JobCompletedInvoice2.this);
        ll_invoice_below = (LinearLayout) findViewById(R.id.ll_invoice_below);

        final InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches("^$?\\-?([1-9]{1}[0-9]{0,2}(\\,\\d{3})*(\\.\\d{0,2})?|[1-9]{1}\\d{0,}(\\.\\d{0,2})?|0(\\.\\d{0,2})?|(\\.\\d{1,2}))$|^\\-?$?([1-9]{1}\\d{0,2}(\\,\\d{3})*(\\.\\d{0,2})?|[1-9]{1}\\d{0,}(\\.\\d{0,2})?|0(\\.\\d{0,2})?|(\\.\\d{1,2}))$|^\\($?([1-9]{1}\\d{0,2}(\\,\\d{3})*(\\.\\d{0,2})?|[1-9]{1}\\d{0,}(\\.\\d{0,2})?|0(\\.\\d{0,2})?|(\\.\\d{1,2}))\\)$")) {
                        return "";
                    }
                }
                return null;
            }
        };



        tv_charges = (TextView) findViewById(R.id.tv_charges);
        tv_charges.setTypeface(ClanaproNarrNews);

        tv_grandvat=(TextView)findViewById(R.id.tv_grandvattotal_value);
        tv_grandvat.setTypeface(ClanaproNarrNews);

        tv_additional_charges = (TextView) findViewById(R.id.tv_additional_charges);
        tv_additional_charges.setTypeface(ClanaproNarrNews);

        tv_paid_by_rcvr = (TextView) findViewById(R.id.tv_paid_by_rcvr);
        tv_paid_by_rcvr.setTypeface(ClanaproNarrNews);

        tv_currency = (TextView) findViewById(R.id.tv_currency);
        tv_currency.setTypeface(ClanaproNarrNews);
        tv_currency.setText(sessionManager.getcurrencySymbol());

        tv_currency1 = (TextView) findViewById(R.id.tv_currency1);
        tv_currency1.setTypeface(ClanaproNarrNews);
        tv_currency1.setText(sessionManager.getcurrencySymbol());

        et_bil_detail = (TextView) findViewById(R.id.et_bil_detail);
        et_bil_detail.setTypeface(ClanaproNarrNews);

        tv_distancefare = (TextView) findViewById(R.id.tv_distancefare);
        tv_distancefare.setTypeface(ClanaproNarrNews);

        tv_basefare = (TextView) findViewById(R.id.tv_basefare);
        tv_basefare.setTypeface(ClanaproNarrNews);

        tv_timefare = (TextView) findViewById(R.id.tv_timefare);
        tv_timefare.setTypeface(ClanaproNarrNews);

        tv_waitingfare = (TextView) findViewById(R.id.tv_waitingfare);
        tv_waitingfare.setTypeface(ClanaproNarrNews);

        tv_subtotalfare = (TextView) findViewById(R.id.tv_subtotalfare);
        tv_subtotalfare.setTypeface(ClanaproNarrNews);

        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_discount.setTypeface(ClanaproNarrNews);


        tv_addspecial_charges = (TextView) findViewById(R.id.tv_addspecial_charges);
        tv_addspecial_charges.setTypeface(ClanaproNarrNews);

        tv_grandtotal = (TextView) findViewById(R.id.tv_grandtotal);
        tv_grandtotal.setTypeface(ClanaproNarrMedium);

        tv_deliveredto = (TextView) findViewById(R.id.tv_deliveredto);
        tv_deliveredto.setTypeface(ClanaproNarrNews);

        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_signature.setTypeface(ClanaproNarrNews);

        tv_photo_doc = (TextView) findViewById(R.id.tv_photo_doc);
        tv_photo_doc.setTypeface(ClanaproNarrNews);

        tv_takephoto = (TextView) findViewById(R.id.tv_takephoto);
        tv_takephoto.setTypeface(ClanaproNarrNews);

        tv_rate_cust = (TextView) findViewById(R.id.tv_rate_cust);
        tv_rate_cust.setTypeface(ClanaproNarrNews);

        tv_tollFee = (TextView) findViewById(R.id.tv_tollFee);
        tv_tollFee.setTypeface(ClanaproNarrNews);

        tv_confirm_bill = (TextView) findViewById(R.id.tv_confirm_bill);
        tv_confirm_bill.setTypeface(ClanaproNarrNews);

        tv_cannot_edit = (TextView) findViewById(R.id.tv_cannot_edit);
        tv_cannot_edit.setTypeface(ClanaproNarrNews);


        ll_vat_per=(LinearLayout)findViewById(R.id.ll_vat_per);
        ll_basefare=(LinearLayout)findViewById(R.id.ll_basefare);
        ll_timefare=(LinearLayout)findViewById(R.id.ll_timefare);
        ll_distancefare=(LinearLayout)findViewById(R.id.ll_distancefare);
        rl_handling=(RelativeLayout)findViewById(R.id.rl_handling);
        ll_waitingfare=(LinearLayout)findViewById(R.id.ll_waitingfare);

        tv_handling = (TextView) findViewById(R.id.tv_handling);
        tv_handling.setTypeface(ClanaproNarrNews);

        tv_adjustment = (TextView) findViewById(R.id.tv_adjustment);
        tv_adjustment.setTypeface(ClanaproNarrNews);
        if (appointment.getPricingType()==0){
            ll_basefare.setVisibility(View.VISIBLE);
            ll_distancefare.setVisibility(View.VISIBLE);
            ll_timefare.setVisibility(View.VISIBLE);
            ll_waitingfare.setVisibility(View.VISIBLE);
            rl_handling.setVisibility(View.VISIBLE);

        }else if (appointment.getPricingType()==1){
            ll_basefare.setVisibility(View.VISIBLE);
            ll_distancefare.setVisibility(View.GONE);
            ll_timefare.setVisibility(View.GONE);
            ll_waitingfare.setVisibility(View.VISIBLE);
            rl_handling.setVisibility(View.VISIBLE);

        }else if (appointment.getPricingType()==2){
            ll_basefare.setVisibility(View.VISIBLE);
            ll_distancefare.setVisibility(View.GONE);
            ll_timefare.setVisibility(View.GONE);
            ll_waitingfare.setVisibility(View.VISIBLE);
            rl_handling.setVisibility(View.VISIBLE);

        }

        /*******************************************************************************************/

        scrollView = (ScrollView) findViewById(R.id.scrollView);


        TextView tv_basefare_value = (TextView) findViewById(R.id.tv_basefare_value);
        tv_basefare_value.setTypeface(ClanaproNarrNews);
        tv_basefare_value.setText(sessionManager.getcurrencySymbol() + " " + invoice.getBaseFare());

        TextView tv_distancefare_value = (TextView) findViewById(R.id.tv_distancefare_value);
        tv_distancefare_value.setTypeface(ClanaproNarrNews);
        tv_distancefare_value.setText(sessionManager.getcurrencySymbol() + " " + invoice.getDistFare());

        TextView tv_timefare_value = (TextView) findViewById(R.id.tv_timefare_value);
        tv_timefare_value.setTypeface(ClanaproNarrNews);
        tv_timefare_value.setText(sessionManager.getcurrencySymbol() + " " + invoice.getTimeFare());


        tv_bid_value=(TextView)findViewById(R.id.tv_bid_value);
        tv_bid_value.setTypeface(ClanaproNarrNews);
        tv_bid_value.setText(appointment.getBid());

        tv_vat_per=(TextView) findViewById(R.id.tv_vat_per);
        tv_vat_per.setTypeface(ClanaproNarrNews);


        tv_vat_value_per=(TextView)findViewById(R.id.tv_vat_value_per);


        ll_vat=(LinearLayout)findViewById(R.id.ll_vat);
        if (invoice.getTaxEnable()){
            ll_vat.setVisibility(View.VISIBLE);
            ll_vat_per.setVisibility(View.VISIBLE);
        }else {
            ll_vat.setVisibility(View.GONE);
            ll_vat_per.setVisibility(View.GONE);
        }
        tv_vat_value= (TextView) findViewById(R.id.tv_vat_value);
        tv_vat_value.setTypeface(ClanaproMedium);

        tv_vat=(TextView)findViewById(R.id.tv_vat);
        tv_vat.setTypeface(ClanaproMedium);
        tv_vat.setText(invoice.getTaxTitle());

        tv_vat_value.setText(sessionManager.getCurrencySymbol()+" "+invoice.getTaxPercentage()+"");
        Log.i("vaat",invoice.getTaxPercentage()+"");

        tv_waitingfare_value = (TextView) findViewById(R.id.tv_waitingfare_value);
        tv_waitingfare_value.setTypeface(ClanaproNarrNews);
        tv_waitingfare_value.setText(sessionManager.getcurrencySymbol() + " " + invoice.getWatingFee());

        tv_discount_value = (TextView) findViewById(R.id.tv_discount_value);
        tv_discount_value.setTypeface(ClanaproNarrNews);
        tv_discount_value.setText(sessionManager.getcurrencySymbol() + " " + invoice.getDiscount());



        calculatedFare = invoice.getTotal();
        grandTotalFarewithVat=invoice.getTotal();

        tv_subtotal_value = (TextView) findViewById(R.id.tv_subtotal_value);
        tv_subtotal_value.setTypeface(ClanaproNarrMedium);
        tv_subtotal_value.setText(sessionManager.getcurrencySymbol() + " " + calculatedFare);

        tv_grandtotal_value = (TextView) findViewById(R.id.tv_grandtotal_value);
        tv_grandtotal_value.setTypeface(ClanaproNarrNews);

        et_tollFee_value = (EditText) findViewById(R.id.et_tollFee_value);
        et_tollFee_value.setTypeface(ClanaproNarrMedium);

        et_handling_value = (EditText) findViewById(R.id.et_handling_value);
        et_handling_value.setTypeface(ClanaproNarrMedium);



        tv_adjustment_value = (TextView) findViewById(R.id.tv_adjustment_value);
        tv_adjustment_value.setTypeface(ClanaproNarrNews);

      try {
          if (invoice.getTaxEnable())
          {
             if (!invoice.getTaxPercentage().equals(""))
           {


              vat_perc = (calculatedFare * (Double.parseDouble(invoice.getTaxPercentage()))) / 100;
              tv_vat_value.setText(String.format("%.2f",vat_perc));

              tv_vat_value_per.setText(invoice.getTaxPercentage());




          }
          }
      }catch (Exception e){

      }
        finalTotal=vat_perc+grandTotalFarewithVat;
        tv_grandtotal_value.setText(sessionManager.getcurrencySymbol() + " " + grandTotalFarewithVat);
        tv_grandvat.setText(sessionManager.getcurrencySymbol() + " " + finalTotal);




//        tv_adjustment_value.setText(sessionManager.getcurrencySymbol()+" "+invoice.getAdjustment());

        LinearLayout ll_basefare, ll_distancefare, ll_timefare, ll_waitingfare, ll_discountfare, ll_adjustment;
        ll_basefare = (LinearLayout) findViewById(R.id.ll_basefare);
        ll_distancefare = (LinearLayout) findViewById(R.id.ll_distancefare);
        ll_timefare = (LinearLayout) findViewById(R.id.ll_timefare);
        ll_waitingfare = (LinearLayout) findViewById(R.id.ll_waitingfare);
        ll_discountfare = (LinearLayout) findViewById(R.id.ll_discountfare);
        ll_adjustment = (LinearLayout) findViewById(R.id.ll_adjustment);

        if ((invoice.getBaseFare() == 0) || invoice.getBaseFare() == 0.00) {
            ll_basefare.setVisibility(View.GONE);
        }
        if (invoice.getDistFare() == 0 || invoice.getDistFare() == 0.00) {
            ll_distancefare.setVisibility(View.GONE);
        }
        if (invoice.getTimeFare() == 0 || invoice.getTimeFare() == 0.00) {
            ll_timefare.setVisibility(View.GONE);
        }
        if (invoice.getWatingFee() == 0 || invoice.getWatingFee() == 0.00) {
            ll_waitingfare.setVisibility(View.GONE);
        }
        if (invoice.getDiscount() == 0 || invoice.getDiscount() == 0.00) {
            ll_discountfare.setVisibility(View.GONE);
        }
        /*if(invoice.getAdjustment().equals("0") || invoice.getAdjustment().equals("0.00"))
        {
            ll_adjustment.setVisibility(View.GONE);
        }*/

        /*grand_total = Double.parseDouble(invoice.getTotal());
        tv_grandtotal_value.setText(String.format("%.2f", grand_total));*/

        et_tollFee_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                {
                    String str = et_tollFee_value.getText().toString();

                    int indexOFdec =  str.indexOf(".");

                    if(indexOFdec >=0) {
                        if(str.substring(indexOFdec).length() >2)
                        {
                            et_tollFee_value.setFilters(filters);
                        }
                    }
                }

                if (!et_tollFee_value.getText().toString().matches("")) {
                    toll_fee = Double.parseDouble(et_tollFee_value.getText().toString());
                } else {
                    toll_fee = 0.00;
                }

                grand_total = (invoice.getTotal()) + handling_fee + toll_fee;
                Utility.printLog("the subtotal is :" + grand_total);



                tv_grandtotal_value.setText(String.format("%.2f", grand_total));


                try {
                    if (invoice.getTaxEnable()) {
                        if (!invoice.getTaxPercentage().equals("")) {
                            vat_perc = (grand_total * (Double.parseDouble(invoice.getTaxPercentage()))) / 100;
                        }
                    }
                }catch (Exception e){

                }
                finalTotal=vat_perc+grand_total;

                //

                Log.i("am",vat_perc+"");

               //
                tv_vat_value.setText(String.format("%.2f",vat_perc));
                tv_grandvat.setText(String.format("%.2f",finalTotal));

            }
        });

        et_handling_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_handling_value.getText().toString().matches("")) {
                    handling_fee = Double.parseDouble(et_handling_value.getText().toString());
                } else {
                    handling_fee = 0.0;
                }


                grand_total = (invoice.getTotal()) + handling_fee + toll_fee;


                //

                Log.i("am",vat_perc+"");
                tv_grandtotal_value.setText(String.format("%.2f", grand_total));



try {

    if (invoice.getTaxEnable()){
        if (!invoice.getTaxPercentage().equals("")){
            vat_perc=(grand_total*(Double.parseDouble(invoice.getTaxPercentage())))/100;
        }
    }

}catch (Exception e){

}

                tv_vat_value.setText(String.format("%.2f",vat_perc));

                finalTotal=vat_perc+grand_total;
                tv_grandvat.setText(String.format("%.2f",finalTotal));
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0)
                {
                    String str = et_handling_value.getText().toString();

                    int indexOFdec =  str.indexOf(".");

                    if(indexOFdec >=0) {
                        if(str.substring(indexOFdec).length() >2)
                        {
                            et_handling_value.setFilters(filters);
                        }
                    }
                }
            }
        });

        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_submit.setTypeface(ClanaproMedium);
        tv_submit.setOnClickListener(this);

        if(appointment.isPaidByReceiver()){
            tv_submit.setText(getResources().getString(R.string.collect_cash));
            tv_paid_by_rcvr.setVisibility(View.VISIBLE);
        }else {
            tv_submit.setText(getResources().getString(R.string.confirm));
            tv_paid_by_rcvr.setVisibility(View.GONE);
        }

        et_recievername = (EditText) findViewById(R.id.et_recievername);
        et_recievername.setTypeface(ClanaproNarrNews);
        et_recievername.setText(cust_name);

        et_phonenum = (EditText) findViewById(R.id.et_phonenum);
        et_phonenum.setTypeface(ClanaproNarrNews);
        et_phonenum.setText(cust_phone);

        signPad = (SignaturePad) findViewById(R.id.signature_pad);
        signPad.setOnSignedListener(this);

        LinearLayout ll_takePhotos = (LinearLayout) findViewById(R.id.ll_takephoto);
        ll_takePhotos.setOnClickListener(this);

        RecyclerView rv_completed_documents = (RecyclerView) findViewById(R.id.rv_completed_documents);
        LinearLayoutManager layoutManager = new LinearLayoutManager(JobCompletedInvoice2.this, LinearLayoutManager.HORIZONTAL, true);
        rv_completed_documents.setLayoutManager(layoutManager);

        imageUploadRVA = new ImageUploadRVA(this, imagefile, new ImageUploadRVA.RemoveImage() {
            @Override
            public void ImageRemoved(int position) {
                imagefile.remove(position);
                imageCount--;
                imageUploadRVA.notifyDataSetChanged();
               /* if(imageCount<Licence_image_no)
                {
                    ll_add_licence.setVisibility(View.VISIBLE);
                }*/
            }
        });
        rv_completed_documents.setAdapter(imageUploadRVA);
        imageUploadRVA.notifyDataSetChanged();

        ImageView signClear = (ImageView) findViewById(R.id.iv_signClear);
        signClear.setOnClickListener(this);

        RecyclerView rv_specialcharges;
        rv_specialcharges = (RecyclerView) findViewById(R.id.rv_specialcharges);
        rv_specialcharges.setLayoutManager(new LinearLayoutManager(this));
        rv_specialcharges.setNestedScrollingEnabled(true);

        SpecialChargeRVA specialChargeRVA = new SpecialChargeRVA(this);
        rv_specialcharges.setAdapter(specialChargeRVA);

        final RatingBar driverRating = (RatingBar) findViewById(R.id.ratingbar);
        driverRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rate=1;
                rating = v;
            }
        });


        tv_confirm_bill.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                if (validatesFields()) {

                    if(appointment.isPaidByReceiver()){
                        showAmtCollectPopUp();
                    }else {
                        createFile();
                        amzonUpload(takenNewSignature,2);
                        updateBookingStatus("");
                    }

                }

                break;
            case R.id.iv_signClear:
                signPad.clear();
                break;
            case R.id.ll_takephoto:
                imageEditUpload = new ImageEditUpload(this, licence_pic);
                ImageType = licence_pic;
                break;
            case R.id.tv_confirm_bill:
                tv_confirm_bill.setVisibility(View.GONE);
                tv_cannot_edit.setVisibility(View.GONE);
                ll_invoice_below.setVisibility(View.VISIBLE);
                et_tollFee_value.setEnabled(false);
                et_handling_value.setEnabled(false);
                tv_submit.setVisibility(View.VISIBLE);

               scrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

                break;
        }
    }

    private boolean validatesFields() {
        boolean result = true;
        if (TextUtils.isEmpty(et_recievername.getText())) {
            result = false;
            Utility.showAlert("Enter Receiver Name",JobCompletedInvoice2.this);
//            Toast.makeText(JobCompletedInvoice2.this, "Enter Receiver Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(et_phonenum.getText())) {
            result = false;
            Utility.showAlert("Enter Receiver Phone",JobCompletedInvoice2.this);
//            Toast.makeText(JobCompletedInvoice2.this, "Enter Receiver Phone", Toast.LENGTH_SHORT).show();
        } else if (signBitmap == null) {
            result = false;
            Utility.showAlert("Please sign on the pad",JobCompletedInvoice2.this);
//            Toast.makeText(JobCompletedInvoice2.this, "Please sign on the pad", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void createFile() {
        MyImageHandler myImageHandler = MyImageHandler.getInstance();
        File dir = myImageHandler.getAlbumStorageDir(this, VariableConstant.SIGNATURE_PIC_DIR, true);
        takenNewSignature = new File(dir, newSignatureName);
        try {
            saveBitmapToJPG(signBitmap, takenNewSignature);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    /**********************************************************************************************/

    private void updateBookingStatus(String cash) {

        JSONObject reqObject = new JSONObject();

        String docs="";
        if(imagefile.size()>0){
            for(int i=0;i<imagefile.size();i++){
                if(docs.isEmpty()){
                    docs=imagefile.get(i);
                }else {
                    docs=docs+","+imagefile.get(i);
                }
            }
        }
        mDialog.setMessage(getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        try {
//            reqObject.put("token", sessionManager.getSessionToken());
            reqObject.put("ent_status", 10);
            reqObject.put("ent_booking_id", bid);
            reqObject.put("ent_signatureUrl", signatureUrl);
            reqObject.put("ent_tollFee", toll_fee);
            reqObject.put("ent_handlingFee", handling_fee);
            reqObject.put("ent_receiverName", et_recievername.getText().toString());
            reqObject.put("ent_receiverPhone", et_phonenum.getText().toString());
            reqObject.put("ent_rating", rate==0?5:rating);
            reqObject.put("lat", sessionManager.getDriverCurrentLat());
            reqObject.put("long", sessionManager.getDriverCurrentLng());
            reqObject.put("ent_documents", docs);
            reqObject.put("cashCollected", cash);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(), ServiceUrl.UPDATE_BOOKING_STATUS, OkHttp3Connection.Request_type.PUT,
                reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Booking Flow", " result " + result);
                        if (result != null) {
                            ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                            switch (validatorPojo.getErrFlag()) {
                                case 0:
                                    Intent intent = new Intent(JobCompletedInvoice2.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                                default:
                                    Toast.makeText(JobCompletedInvoice2.this, validatorPojo.getErrMsg(), Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(JobCompletedInvoice2.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                        Log.d("Booking Flow", " error " + error);
                        mDialog.dismiss();
                    }
                }, sessionManager.getSessionToken());
    }

    @Override
    public void onStartSigning() {

    }

    @Override
    public void onSigned() {
        signBitmap = signPad.getSignatureBitmap();
    }

    @Override
    public void onClear() {
        signBitmap = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        } else if (requestCode != -1) {
            switch (requestCode) {
                case CAMERA_PIC:
                    fileType = "image";
                    startCropImage();
                    break;

                case GALLERY_PIC:
                    try {
                        Utility.printLog("RegistrationAct in GALLERY_PIC:");
                        takenNewImage = "";
                        state = Environment.getExternalStorageState();
                        takenNewImage = VariableConstant.PARENT_FOLDER + String.valueOf(System.nanoTime()) + ".png";

                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            VariableConstant.newFile = new File(Environment.getExternalStorageDirectory()/* + "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages/"*/, takenNewImage);
                        } else {
                            VariableConstant.newFile = new File(getFilesDir() /*+ "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages/"*/, takenNewImage);
                        }

                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(VariableConstant.newFile);

                        Utility.copyStream(inputStream, fileOutputStream);

                        fileOutputStream.close();
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        VariableConstant.newProfileImageUri = Uri.fromFile(VariableConstant.newFile);
                        Utility.printLog("RegistrationAct in GALLERY_PIC fileOutputStream: " + fileOutputStream);
                        startCropImage();
                    } catch (Exception e) {
                        Utility.printLog("RegistrationAct in GALLERY_PIC Error while creating newfile:" + e);
                    }
                    break;

                case CROP_IMAGE:
                    fileName = "";
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path == null) {
                        Utility.printLog("RegistrationAct CROP_IMAGE file path is null: " + VariableConstant.newFile.getPath());

                        return;
                    } else {
                        if (ImageType.equals(profile_pic)) {
                            VariableConstant.isPictureTaken = true;
                        }
                        Utility.printLog("RegistrationAct CROP_IMAGE FilePAth : " + VariableConstant.newFile.getPath());
                        VariableConstant.newProfileImageUri = Uri.fromFile(VariableConstant.newFile);
                        Utility.printLog("RegistrationAct CROP_IMAGE file URi: " + VariableConstant.newProfileImageUri);

                        fileName = VariableConstant.newFile.getName();
                        Utility.printLog("RegistrationAct CROP_IMAGE fileName: " + fileName);

                        try {
                            String[] type = fileName.split("\\.");

                            byte[] bytes = new byte[(int) VariableConstant.newFile.length()];
                            InputStream inputStream = getContentResolver().openInputStream(VariableConstant.newProfileImageUri);
                            if (inputStream != null) {
                                inputStream.read(bytes);
                            }
                            byte[] encoded = Base64.encode(bytes, Base64.NO_WRAP);
                            base64dependentPic = new String(encoded);
                            base64dependentPic = "data:image/png;base64," + base64dependentPic;
//                            Bitmap bMap = BitmapFactory.decodeFile(path);
//                            Bitmap circle_bMap = Utility.getCircleCroppedBitmap(bMap);
                            amzonUpload(new File(path),1);
                        } catch (Exception e) {
                            Utility.printLog("RegistrationAct in CROP_IMAGE exception while copying file = " + e.toString());
                        }
                    }
                    break;

                default:

                    Toast.makeText(this, getResources().getString(R.string.oops)
                            + " " + getResources().getString(R.string.smthWentWrong), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void startCropImage() {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, VariableConstant.newFile.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 4);
        startActivityForResult(intent, JobCompletedInvoice2.CROP_IMAGE);
    }
    /**********************************************************************************************/
    private void amzonUpload(File file, final int type) {
        String BUCKETSUBFOLDER = VariableConstant.SIGNATURE_UPLOAD;
        Log.d(TAG, "amzonUpload: " + file);
        mDialog.show();
        Upload_file_AmazonS3 amazonS3 = Upload_file_AmazonS3.getInstance(this, VariableConstant.COGNITO_POOL_ID);
        final String imageUrl = VariableConstant.AMAZON_BASE_URL + VariableConstant.BUCKET_NAME + "/" + BUCKETSUBFOLDER + file.getName();
        Log.d(TAG, "amzonUpload: " + imageUrl);
        Log.d(TAG, "amzonUpload: " + BUCKETSUBFOLDER + file.getName());
        if(type==2){
            signatureUrl=imageUrl;
        }

        amazonS3.Upload_data(VariableConstant.BUCKET_NAME, BUCKETSUBFOLDER  + file.getName(), file, new Upload_file_AmazonS3.Upload_CallBack() {
            @Override
            public void sucess(String url) {
                mDialog.dismiss();
                Log.d("UploadUrl", url);

                if(type==1){
                    imagefile.add(imageCount, url);
                    imageCount++;
                    imageUploadRVA.notifyDataSetChanged();
                }else
                {
                    signatureUrl=url;
                }



            }

            @Override
            public void sucess(String url, String type) {

            }

            @Override
            public void error(String errormsg) {
                mDialog.dismiss();
                Log.d("URL", "error");
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forwarding results to for permission check
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else if (requestCode == VariableConstant.RC_READ_WRITE_CAMERA_STATE) {
            methodRequiresOnePermission();
        }

    }

    /**********************************************************************************************/
    /**
     *<h1>setupUI</h1>
     * <p>the method is calling for hide the keybaord. if the edit text is focuesd for edit then only the keyboard is visibe.
     * if touch outside of the Edittext then the key board will hide.</p>
     * @param view : view is the parent which is the main layout id.
     */
    public void setupUI(final View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Utility.hideSoftKeyboard(view);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void showAmtCollectPopUp(){

        final Dialog dialog=new Dialog(JobCompletedInvoice2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_collect_cash);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        TextView tvGrandTotalHeader= (TextView) dialog.findViewById(R.id.tvGrandTotalHeader);
        tvGrandTotalHeader.setTypeface(ClanaproNarrNews);

        TextView tvGrandTotal= (TextView) dialog.findViewById(R.id.tvGrandTotal);
        tvGrandTotal.setTypeface(ClanaproNarrNews);

        TextView tvEnterAmtHeader= (TextView) dialog.findViewById(R.id.tvEnterAmtHeader);
        tvEnterAmtHeader.setTypeface(ClanaproNarrMedium);

        TextView tvDollar= (TextView) dialog.findViewById(R.id.tvDollar);
        tvDollar.setTypeface(ClanaproNarrMedium);
        tvDollar.setText(sessionManager.getcurrencySymbol());

        TextView tv_confirm= (TextView) dialog.findViewById(R.id.tv_confirm);
        tv_confirm.setTypeface(ClanaproNarrNews);

        final EditText etAmtCollected= (EditText) dialog.findViewById(R.id.etAmtCollected);
        etAmtCollected.setTypeface(ClanaproNarrMedium);
        Double amt=appointment.getInvoice().getTotal()+handling_fee+toll_fee+vat_perc;
        tvGrandTotal.setText(sessionManager.getcurrencySymbol()+String.format("%.2f",amt));
        etAmtCollected.setText(String.format("%.2f",amt));
        etAmtCollected.setSelection(etAmtCollected.getText().length());

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAmtCollected.getText().toString().isEmpty()){
                    createFile();
                    amzonUpload(takenNewSignature,2);
                    updateBookingStatus(etAmtCollected.getText().toString());
                    sessionManager.setVAT_AMOUNT(vat_perc+"");
                    dialog.dismiss();
                }
            }
        });



        dialog.show();

    }


}
