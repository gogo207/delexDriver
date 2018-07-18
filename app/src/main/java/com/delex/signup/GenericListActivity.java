package com.delex.signup;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.delex.adapter.GenericListAdapter;
import com.delex.driver.R;
import com.delex.utility.SessionManager;

import java.util.ArrayList;

/**
 * Created by Admin on 8/1/2017.
 */

public class GenericListActivity extends AppCompatActivity {
    private RecyclerView recyclerList;
    private GenericListAdapter adapter;
    private Typeface ClanaproNarrMedium,ClanaproNarrNews;

    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_an_operator);
        overridePendingTransition(R.anim.bottem_slide_down, R.anim.stay_activity);
        sessionManager = new SessionManager(GenericListActivity.this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        Bundle mBundle = getIntent().getExtras();
        String type = mBundle.getString("TYPE");
        String title = mBundle.getString("TITLE");
        ArrayList data = (ArrayList) mBundle.getSerializable("DATA");

        adapter = new GenericListAdapter(GenericListActivity.this, data, type);

        initActionBar(title);
        initializeViews();

        if (data != null) {

            recyclerList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void initializeViews() {

        recyclerList = (RecyclerView) findViewById(R.id.rv_operators_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GenericListActivity.this);
        recyclerList.setLayoutManager(layoutManager);
        //recyclerList.addItemDecoration(new SimpleItemDecorative(getApplicationContext()));
    }

    private void initActionBar(String title) {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_signup_close);
        }
        ImageView iv_search;
        TextView tv_title;

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        tv_title.setTypeface(ClanaproNarrMedium);

        TextView tv_done = (TextView) findViewById(R.id.tv_done);
        tv_done.setTypeface(ClanaproNarrMedium);
        tv_done.setVisibility(View.VISIBLE);
        tv_done.setOnClickListener(adapter);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_activity, R.anim.bottem_slide_up);
    }

    public void sendResult(ArrayList list, String type) {
        overridePendingTransition(R.anim.stay_activity, R.anim.bottem_slide_up);
        Intent intent = new Intent();
        intent.putExtra("TYPE", type);
        intent.putExtra("DATA", list);
        setResult(RESULT_OK, intent);
        finish();
    }
}
