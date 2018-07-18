package com.delex.bankDetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.driver.R;
import com.delex.utility.SessionManager;

import org.json.JSONObject;

public class BankNewAccountActivity extends AppCompatActivity implements BankNewAccountPresenter.BankNewAccountPresenterImple {
    private EditText etName,etAccountNo,etRoutingNo,etBankName,etSwiftCode,etIBINNumber;
    private TextInputLayout tilName,tilAccountNo,tilRoutingNo,tilBankName,tilSwiftCode,tilIBINNumber;
    private ProgressDialog pDialog;
    Typeface ClanaproNarrMedium;
    LinearLayout llBankDetails,ll_add_bank;
    private TextView tv_title,tv_acc_no,tv_acc_holder_name,tv_bank_name,tv_edit_bank;
    private BankNewAccountPresenter bankNewAccountPresenter;
    private SessionManager sessionManager;
    private  String bankname="";
    private String bankacc="";
    private  String holdername="";
    private String swiftcode="";
    private String ibannumber="";
    private String routingno="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_details_new);
        sessionManager = new SessionManager(BankNewAccountActivity.this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Bundle p=getIntent().getExtras();
        if(p!=null){
            bankacc=p.getString("accountno");
            bankname=p.getString("bankname");
            holdername=p.getString("holdername");
            swiftcode=p.getString("swift");
            routingno=p.getString("routing");
            ibannumber=p.getString("iban");

        }
        initViews();
    }
    private void initViews()
    { /*sessionManager = new SessionManager(this);
        bankNewAccountPresenter = new BankNewAccountPresenter(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_cancel);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        etName = (EditText)findViewById(R.id.etName);
        etAccountNo = (EditText)findViewById(R.id.etAccountNo);
        etRoutingNo =(EditText) findViewById(R.id.etRoutingNo);
        etName.setTypeface(typeface);
        etAccountNo.setTypeface(typeface);
        etRoutingNo.setTypeface(typeface);

        tilName = (TextInputLayout)findViewById(R.id.tilName);
        tilAccountNo = (TextInputLayout)findViewById(R.id.tilAccountNo);
        tilRoutingNo =(TextInputLayout) findViewById(R.id.tilRoutingNo);

        TextView tvSave = (TextView) findViewById(R.id.tvSave);
        tvSave.setTypeface(typeface);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid())
                {
                    requestBankDetails();
                }
            }
        });*/
        sessionManager = new SessionManager(this);
        bankNewAccountPresenter = new BankNewAccountPresenter(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_cancel);
        }*/
       /* toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)*/;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_back_btn);
        }
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.bank_details));
        tv_title.setTypeface(ClanaproNarrMedium);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);


        llBankDetails=(LinearLayout)findViewById(R.id.llBankDetails);
        ll_add_bank=(LinearLayout)findViewById(R.id.ll_add_bank);

        tv_acc_holder_name=(TextView)findViewById(R.id.tvAccountHolder);
        tv_acc_no=(TextView)findViewById(R.id.tvAccountNo);
        tv_bank_name=(TextView)findViewById(R.id.tvbanknamedetails) ;
        tv_edit_bank=(TextView)findViewById(R.id.tv_edit_bank) ;

        etBankName=(EditText)findViewById(R.id.etBankName);
        etIBINNumber=(EditText)findViewById(R.id.etIBIN_No);
        etName = (EditText)findViewById(R.id.etAccHolderName);
        etAccountNo = (EditText)findViewById(R.id.etAccountNo);
        etRoutingNo =(EditText) findViewById(R.id.etRoutingNo);
        etSwiftCode=(EditText)findViewById(R.id.etSwiftNo);


        tv_acc_holder_name.setTypeface(typeface);
        tv_acc_no.setTypeface(typeface);
        tv_bank_name.setTypeface(typeface);

        etName.setTypeface(typeface);
        etAccountNo.setTypeface(typeface);
        etRoutingNo.setTypeface(typeface);
        etSwiftCode.setTypeface(typeface);
        etIBINNumber.setTypeface(typeface);
        etBankName.setTypeface(typeface);


        etName.setText(holdername);
        etAccountNo.setText(bankacc);
        etBankName.setText(bankname);
        etIBINNumber.setText(ibannumber);
        etSwiftCode.setText(swiftcode);
        etRoutingNo.setText(routingno);


        tilName = (TextInputLayout)findViewById(R.id.tilName);
        tilAccountNo = (TextInputLayout)findViewById(R.id.tilAccountNo);
        tilRoutingNo =(TextInputLayout) findViewById(R.id.tilRoutingNo);
        tilBankName=(TextInputLayout)findViewById(R.id.tilBankName);
        tilIBINNumber=(TextInputLayout)findViewById(R.id.tilIBIN_No);
        tilSwiftCode=(TextInputLayout)findViewById(R.id.tilSwiftNo);

        TextView addbank = (TextView) findViewById(R.id.tvAddBankAccount);
        addbank.setTypeface(typeface);

        addbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBankDetails();

            }
        });

    }

    private boolean valid() {
        if(etName.getText().toString().equals(""))
        {
            tilName.setErrorEnabled(true);
            tilName.setError(getString(R.string.enterAccountHoldername));
            return false;
        }
        else if(etAccountNo.getText().toString().equals(""))
        {
            tilName.setErrorEnabled(false);

            tilAccountNo.setErrorEnabled(true);
            tilAccountNo.setError(getString(R.string.enterAccountNo));
            return false;
        }
        else if(etRoutingNo.getText().toString().equals(""))
        {
            tilName.setErrorEnabled(false);
            tilAccountNo.setErrorEnabled(false);

            tilRoutingNo.setErrorEnabled(true);
            tilRoutingNo.setError(getString(R.string.enterRoutinNo));
            return false;
        }
        else
        {
            tilName.setErrorEnabled(false);
            tilAccountNo.setErrorEnabled(false);
            tilRoutingNo.setErrorEnabled(false);

            return true;
        }
    }

    private void requestBankDetails() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountHolderName",etName.getText().toString());
            jsonObject.put("accountNumber",etAccountNo.getText().toString());
            jsonObject.put("routingNumber",etRoutingNo.getText().toString());
            jsonObject.put("ibanNumber",etIBINNumber.getText().toString());
            jsonObject.put("swiftCode",etSwiftCode.getText().toString());
            jsonObject.put("name",etBankName.getText().toString());

            bankNewAccountPresenter.addBankDetails(sessionManager.getSessionToken(),jsonObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            closeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
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
    public void onSuccess(String msg) {
       closeActivity();
       Intent intent =new Intent(BankNewAccountActivity.this,AddBankAccountActivity.class);
       startActivity(intent);


    }

    @Override
    public void onFailure() {
        Toast.makeText(this,getString(R.string.accountAdditionError),Toast.LENGTH_SHORT).show();
    }

    private void closeActivity()
    {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
    }
}
