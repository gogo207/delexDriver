package com.delex.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.utility.Utility;

public class ChangePasswordProfile extends AppCompatActivity implements View.OnClickListener {

    private EditText et_oldpass, et_newpass, et_re_entpass;
    private TextView tv_submit;

    private Typeface ClanaproNarrMedium;

    private String oldpass, newpass, re_entpass;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_profile);

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

        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.change_pass));
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

        Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        et_oldpass = (EditText) findViewById(R.id.et_oldpass);
        et_oldpass.setTypeface(ClanaproNarrNews);

        et_newpass = (EditText) findViewById(R.id.et_newpass);
        et_newpass.setTypeface(ClanaproNarrNews);

        et_re_entpass = (EditText) findViewById(R.id.et_re_entpass);
        et_re_entpass.setTypeface(ClanaproNarrNews);

        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_submit.setTypeface(ClanaproNarrMedium);
        tv_submit.setOnClickListener(this);


    }

    /**********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_submit:
                PasswordValidation();
                break;
        }

    }

    /**********************************************************************************************/
    /**
     * validating the old password and the new password
     * and checking whether the password is same or not
     */
    void PasswordValidation() {
        oldpass = et_oldpass.getText().toString();
        newpass = et_newpass.getText().toString();
        re_entpass = et_re_entpass.getText().toString();

        if (oldpass.matches("")) {
            Utility.BlueToast(this, getResources().getString(R.string.ent_pres_pass));
        } else if (newpass.matches("")) {
            Utility.BlueToast(this, getResources().getString(R.string.entNewPass));
        } else if (re_entpass.matches("")) {
            Utility.BlueToast(this, getResources().getString(R.string.reentrPass));
        } else if (!(oldpass.matches("") && newpass.matches("") && re_entpass.matches(""))) {
            if (newpass.matches(re_entpass)) {
                Utility.BlueToast(this, getResources().getString(R.string.pass_change_success));
                finish();
            } else {
                Utility.BlueToast(this, getResources().getString(R.string.passNotMactch));
            }
        }


    }
}
