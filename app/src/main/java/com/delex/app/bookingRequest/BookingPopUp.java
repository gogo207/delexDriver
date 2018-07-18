package com.delex.app.bookingRequest;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.driver.R;
import com.delex.pojo.PubnubResponse;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.google.gson.Gson;


/**************************************************************************************************/
public class BookingPopUp extends AppCompatActivity implements View.OnClickListener ,BookingPopUpMainMVP.ViewOperations{

    private TextView tvBID, tv_popup_pickuploc, tv_pickuptime, tv_popup_droploc, tv_droptime, tv_timer, tv_delivery_charge;

    private LinearLayout ll_booking_popup;
    private ProgressBar circular_progress_bar;
    private String TAG = BookingPopUp.class.getSimpleName();
    private CountDownTimer countDownTimer = null;
    private MediaPlayer mediaPlayer;
    private PubnubResponse pubnubResponse;
    private ProgressDialog mDialog;
    private SessionManager sessionManager;
    private Button btnReject;
    private Presenter presenter;


    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_pop_up);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sessionManager = SessionManager.getSessionManager(BookingPopUp.this);
        presenter=new Presenter(this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        VariableConstant.IS_POP_UP_OPEN=true;
        getBookingData();
        initializeViews();
        long time = pubnubResponse.getExpiryTimer();

        if (time > 0) {
//            startTimer(time);
            Utility.printLog(TAG+" pubnubResponse.getExpiryTimer "+pubnubResponse.getExpiryTimer());
            mediaPlayer.start();
            presenter.startTimer(time,circular_progress_bar);
        }
    }

    private void getBookingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("booking_Data")) {
            NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            String response = bundle.getString("booking_Data");
            pubnubResponse = new Gson().fromJson(response, PubnubResponse.class);
        }
    }

    private void initializeViews() {

        Typeface ClanaproNarrNews, ClanaproNarrMedium;
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");

        TextView tv_popup_pickup, tv_popup_drop, tv_lefttoaccept, tv_deliveryfee, tv_popup_cur;


        tv_popup_pickup = (TextView) findViewById(R.id.tv_popup_pickup);
        tv_popup_pickup.setTypeface(ClanaproNarrNews);

        tvBID = (TextView) findViewById(R.id.tvBID);
        tvBID.setTypeface(ClanaproNarrNews);

        TextView tvHeaderDistance = (TextView) findViewById(R.id.tvHeaderDistance);
        tvHeaderDistance.setTypeface(ClanaproNarrNews);

        TextView tvHeaderHandelers = (TextView) findViewById(R.id.tvHeaderHandelers);
        tvHeaderHandelers.setTypeface(ClanaproNarrNews);

        TextView tvHeaderPayment = (TextView) findViewById(R.id.tvHeaderPayment);
        tvHeaderPayment.setTypeface(ClanaproNarrNews);

        TextView tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDistance.setTypeface(ClanaproNarrNews);

        TextView tvHandelrs = (TextView) findViewById(R.id.tvHandelrs);
        tvHandelrs.setTypeface(ClanaproNarrNews);

        TextView tvPayment = (TextView) findViewById(R.id.tvPayment);
        tvPayment.setTypeface(ClanaproNarrNews);

        btnReject = (Button) findViewById(R.id.btnReject);
        btnReject.setTypeface(ClanaproNarrNews);


        tv_popup_drop = (TextView) findViewById(R.id.tv_popup_drop);
        tv_popup_drop.setTypeface(ClanaproNarrNews);


        tv_lefttoaccept = (TextView) findViewById(R.id.tv_lefttoaccept);
        tv_lefttoaccept.setTypeface(ClanaproNarrNews);

        tv_deliveryfee = (TextView) findViewById(R.id.tv_deliveryfee);
        tv_deliveryfee.setTypeface(ClanaproNarrNews);

        tv_popup_cur = (TextView) findViewById(R.id.tv_popup_cur);
        tv_popup_cur.setTypeface(ClanaproNarrMedium);
        tv_popup_cur.setText(sessionManager.getcurrencySymbol());

        tvDistance.setText(pubnubResponse.getDis()+" "+sessionManager.getmileage_metric());
        tvHandelrs.setText(pubnubResponse.getHelpers());
        tvPayment.setText(pubnubResponse.getPaymentType());

        tv_popup_pickuploc = (TextView) findViewById(R.id.tv_popup_pickuploc);
        tv_popup_pickuploc.setTypeface(ClanaproNarrNews);
        tv_popup_pickuploc.setText(pubnubResponse.getAdr1());

        tv_pickuptime = (TextView) findViewById(R.id.tv_pickuptime);
        tv_pickuptime.setTypeface(ClanaproNarrNews);
        tv_pickuptime.setText(Utility.formatDateWeek(pubnubResponse.getDt()));

        tv_popup_droploc = (TextView) findViewById(R.id.tv_popup_droploc);
        tv_popup_droploc.setTypeface(ClanaproNarrNews);
        tv_popup_droploc.setText(pubnubResponse.getDrop1());

        tv_droptime = (TextView) findViewById(R.id.tv_droptime);
        tv_droptime.setTypeface(ClanaproNarrNews);
        tv_droptime.setText(Utility.formatDateWeek(pubnubResponse.getDropDt()));

        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_timer.setTypeface(ClanaproNarrMedium);

        tv_delivery_charge = (TextView) findViewById(R.id.tv_delivery_charge);
        tv_delivery_charge.setTypeface(ClanaproNarrMedium);
        tv_delivery_charge.setText(pubnubResponse.getAmount());

        ll_booking_popup = (LinearLayout) findViewById(R.id.ll_booking_popup);
        ll_booking_popup.setOnClickListener(this);

        tvBID.setText("BID: " + pubnubResponse.getBid());

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                updateAptRequest("3");
                presenter.updateApptRequest("3",pubnubResponse.getBid(),sessionManager);
            }
        });

        circular_progress_bar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        mediaPlayer = MediaPlayer.create(this, R.raw.taxina);
        mediaPlayer.setLooping(true);

        mDialog = new ProgressDialog(BookingPopUp.this);


    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_booking_popup:
                presenter.updateApptRequest("6",pubnubResponse.getBid(),sessionManager);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(String msg) {
//        Toast.makeText(BookingPopUp.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String error) {
        if(error.isEmpty())
            Toast.makeText(BookingPopUp.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(BookingPopUp.this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar() {
        mDialog.setMessage(getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    public void dismissProgressbar() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog.cancel();
        }
    }

    @Override
    public void onTimerChanged(String time) {
        tv_timer.setText(time);
    }

    @Override
    public void onFinish() {
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
        VariableConstant.IS_POP_UP_OPEN=false;
        finish();
    }
}
