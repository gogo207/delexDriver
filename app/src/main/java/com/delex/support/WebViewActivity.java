package com.delex.support;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.delex.driver.R;

public class WebViewActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private WebView webView;
    private String url;
    private String TAG = "WebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        url = getIntent().getStringExtra("URL");

        Log.d(TAG, "onCreate: " + url);
        initActionBar();
        initializeViews();

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_back_btn);
        }
        ImageView iv_search;
        TextView tv_title;

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("SUPPORT");
        tv_title.setTypeface(ClanaproNarrMedium);

    }

    /**********************************************************************************************/

    private void initializeViews() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(true);

        webView = (WebView) findViewById(R.id.webView);
        startWebView(url);

    }


    private void startWebView(String url) {

        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                pDialog.dismiss();
            }

        });
        webView.loadUrl(url);

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
}
