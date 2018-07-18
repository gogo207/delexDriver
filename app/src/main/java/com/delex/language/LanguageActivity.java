package com.delex.language;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.delex.driver.R;
import com.delex.pojo.LangResponse;
import com.delex.pojo.Languages;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;

public class LanguageActivity extends AppCompatActivity {


    private ArrayList<Languages> languages=new ArrayList<>();
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private TextView tv_title;
    private ImageView iv_search;
    private ImageView iv_jobdetails;
    private ProgressDialog pDialog;
    private String TAG=LanguageActivity.class.getSimpleName();
    private LanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        sessionManager=SessionManager.getSessionManager(this);
        //checkAndRequestPermissions();



        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.rvLanguage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        languageAdapter=new LanguageAdapter(this,languages);
        recyclerView.setAdapter(languageAdapter);

        TextView tvDone= (TextView) findViewById(R.id.tvDone);
        tvDone.setOnClickListener(languageAdapter);

        initActionBar();

        getLanguages();
    }

    /**
     * <h1>initActionBar</h1>
     * initilize the action bar*/
    private void initActionBar() {

        pDialog = new ProgressDialog(LanguageActivity.this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_back_btn);
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.select_language));
        tv_title.setTypeface(ClanaproNarrMedium);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);

        iv_jobdetails = (ImageView) findViewById(R.id.iv_jobdetails);
        iv_jobdetails.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void getLanguages(){
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.LANG, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Languages Result" + result);
                if (result != null) {
                    Gson gson=new Gson();
                    LangResponse langResponse=gson.fromJson(result,LangResponse.class);

                    languages.clear();
                    languages.addAll(langResponse.getData());
                    languageAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(LanguageActivity.this, getString(R.string.smthWentWrong), Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Languages Error" + error);
                Toast.makeText(LanguageActivity.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }, "ordinory");
    }
}
