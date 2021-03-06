package com.delex.history;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.adapter.HistoryTripsRVA;
import com.delex.login.Login;
import com.delex.driver.R;
import com.delex.pojo.TripsPojo.Appointments;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HistoryActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, HistoryPresenter.HistoryPresenterImplement {

    private static final String TAG = "HistoryFragment";
    protected BarChart mChart;
    int tabcount = 12;
    int tabIncrement = 5;
    HistoryTripsRVA historyTripsRVA;
    private TextView tv_amount_earned, tv_earned_value;
    private ProgressDialog pDialog;
    private TabLayout tabLayout;
    private SimpleDateFormat tabDateFormat, apiDateFormat;
    private String selectedWeeks;
    private XAxis xAxis;
    private ArrayList<Date> apiStartWeek;
    private int differenceDays = 0;
    private int selectedTabPosition = 0;
    private SessionManager sessionManager;
    private ArrayList<Appointments> appointments;
    private ArrayList<String> currentCycleDays, pastCycleDays;
    private HistoryPresenter historyPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        sessionManager = new SessionManager(HistoryActivity.this);
        //checkAndRequestPermissions();
        Utility.isNetworkAvailable(this);
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        initViews();
    }


    private void initViews() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        sessionManager = new SessionManager(this);

        tabDateFormat = new SimpleDateFormat("MMM dd", Locale.US);
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Typeface fontBold = Utility.getFontBold(this);
        Typeface font = Utility.getFontRegular(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_cancel);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_job_home);
        appointments = new ArrayList<>();
        historyTripsRVA = new HistoryTripsRVA(this, appointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(historyTripsRVA);

        tv_amount_earned = (TextView) findViewById(R.id.tv_amount_earned);
        tv_amount_earned.setTypeface(fontBold);

        tv_earned_value = (TextView) findViewById(R.id.tv_earned_value);
        tv_earned_value.setTypeface(font);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mChart = (BarChart) findViewById(R.id.mChart);

        apiStartWeek = new ArrayList<>();
        currentCycleDays = new ArrayList<>();
        pastCycleDays = new ArrayList<>();

        historyPresenter = new HistoryPresenter(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            closeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
    }

    @Override
    public void onDayInitialized(int differenceDays, ArrayList<String> currentCycleDays, ArrayList<String> postCycleDays) {
        this.differenceDays = differenceDays;
        this.currentCycleDays.addAll(currentCycleDays);
        this.pastCycleDays.addAll(postCycleDays);
        initTabLayout(tabcount);
        initBarChart();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initTabLayout(final int selectableTab) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        tabLayout.removeOnTabSelectedListener(this);
        tabLayout.removeAllTabs();
        apiStartWeek.clear();

        for (int i = 0; i <= tabcount; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }

        for (int i = tabcount; i >= 0; i--) {
            String startDate = "";
            String endDate = "";
            if (i == tabcount) {
                endDate = tabDateFormat.format(c.getTime());
                c.add(Calendar.DATE, -differenceDays);
                startDate = tabDateFormat.format(c.getTime());
                apiStartWeek.add(c.getTime());
                c.add(Calendar.DATE, -1);

                if (differenceDays != 0) {
                    tabLayout.getTabAt(i).setText(startDate + "-" + endDate);
                } else {
                    tabLayout.getTabAt(i).setText(startDate);
                }
            } else {
                endDate = tabDateFormat.format(c.getTime());
                c.add(Calendar.DATE, -6);
                startDate = tabDateFormat.format(c.getTime());
                apiStartWeek.add(c.getTime());
                c.add(Calendar.DATE, -1);
                tabLayout.getTabAt(i).setText(startDate + "-" + endDate);
            }
        }

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.getTabAt(selectableTab).select();
                    }
                }, 100);

        tabLayout.addOnTabSelectedListener(this);

        selectedWeeks = tabLayout.getTabAt(tabcount).getText().toString();

    }


    private void initBarChart() {
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(true);// scaling can now only be done on x- and y-axis separately
        mChart.setDrawGridBackground(false);// scaling can now only be done on x- and y-axis separately
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        selectedTabPosition = tab.getPosition();

        if (selectedTabPosition == 0) {
            tabcount += tabIncrement;
            Log.d(TAG, "onTabSelected: " + tabcount);
            initTabLayout(tabIncrement);
            return;
        }

        if (selectedTabPosition == tabcount) {
            int count = differenceDays + 1;
            IndexAxisValueFormatter indexAxisValueFormatter=new IndexAxisValueFormatter(currentCycleDays);
            xAxis.setValueFormatter(indexAxisValueFormatter);
            xAxis.setLabelCount(count);
            /*XYMarkerView  xyMarkerView=new XYMarkerView(this,indexAxisValueFormatter);
            xyMarkerView.setChartView(mChart);
            mChart.setMarker(xyMarkerView);*/
            mChart.notifyDataSetChanged();
        } else {
            xAxis.setValueFormatter(new IndexAxisValueFormatter(pastCycleDays));
            xAxis.setLabelCount(7);
            mChart.notifyDataSetChanged();
        }

        selectedWeeks = tabLayout.getTabAt(selectedTabPosition).getText().toString();

        int position = tabcount - selectedTabPosition;
        String apiSelectedDate = apiDateFormat.format(apiStartWeek.get(position));

        Log.d(TAG, "onTabSelected: after minus tabPosition " + apiSelectedDate);

        getHistoy(apiSelectedDate, selectedTabPosition, tabcount);
    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**********************************************************************************************/
    /**
     * <h1>getHistoy</h1>
     * <p>the service call for the trip history</p>
     */
    void getHistoy(String startDate, int selectedTabPosition, int tabcount) {
        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("ent_page_index", "1");
            reqOtpObject.put("ent_start_date", startDate);
            reqOtpObject.put("ent_end_date", startDate);
            historyPresenter.getHistory(sessionManager.getSessionToken(), reqOtpObject, selectedTabPosition, tabcount);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void startProgressBar() {
        pDialog.show();
    }

    @Override
    public void stopProgressBar() {
        pDialog.dismiss();
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(int failure) {
        if(failure==401){
            startActivity(new Intent(HistoryActivity.this, Login.class));
            finish();

        }
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, getString(R.string.serverError), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setValues(ArrayList<Appointments> appointments, ArrayList<BarEntry> barEntries, String total, int highestPosition) {
        this.appointments.clear();
        this.appointments.addAll(appointments);
        historyTripsRVA.notifyDataSetChanged();

        tv_earned_value.setText(getString(R.string.currency) + " " + Utility.getFormattedPrice(total));


        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(barEntries);
            set1.setLabel("The Week " + selectedWeeks);
            set1.setHighLightColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            Highlight highlight = new Highlight(highestPosition, 0, 0);
            mChart.highlightValue(highlight, false);

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(barEntries, "The Week " + selectedWeeks);
            set1.setDrawIcons(false);
            set1.setColors(ContextCompat.getColor(this, R.color.colorPrimaryLight));
            set1.setHighLightColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.8f);
            data.setHighlightEnabled(true);


            mChart.setData(data);


            Highlight highlight = new Highlight(highestPosition, 0, 0);
            mChart.highlightValue(highlight, false);
        }

        mChart.invalidate();

        mChart.animateXY(1000, 500);

    }
}
