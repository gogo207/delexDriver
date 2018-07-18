package com.delex.support;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.delex.adapter.SupportRVA;
import com.delex.driver.R;
import com.delex.pojo.SupportData;

import java.util.ArrayList;
import java.util.Collection;

public class SupportSubCategoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_sub_category);

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_back_btn);
        }

        Intent intent = getIntent();
        ArrayList<SupportData> supportDatas = new ArrayList<>();
        supportDatas.addAll((Collection<? extends SupportData>) intent.getSerializableExtra("data"));

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(intent.getStringExtra("title"));

        RecyclerView rvSupportSubCateogry = (RecyclerView) findViewById(R.id.rvSupportSubCateogry);
        rvSupportSubCateogry.setLayoutManager(new LinearLayoutManager(this));

        SupportRVA supportRVA = new SupportRVA(this, supportDatas, false);
        rvSupportSubCateogry.setAdapter(supportRVA);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
