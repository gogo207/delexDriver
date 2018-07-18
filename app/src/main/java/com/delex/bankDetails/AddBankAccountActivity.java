package com.delex.bankDetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.app.MainActivity;
import com.delex.driver.R;
import com.delex.pojo.bank.BankList;
import com.delex.utility.SessionManager;
import com.delex.utility.VariableConstant;

public class AddBankAccountActivity extends AppCompatActivity implements View.OnClickListener, AddBankAccountPresenter.MyProfilePresenterImplement {

    private TextView tv_bank_acc_no,tv_bank_name,tv_bank_state,tv_title,tv_edit;
    private LinearLayout ll_add_bank;
    Typeface ClanaproNarrMedium;
    private String token = "";
    private ProgressDialog pDialog;
    private AddBankAccountPresenter addBankAccountPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);
        ll_add_bank=(LinearLayout) findViewById(R.id.llBankDetails);
        tv_edit=(TextView)findViewById(R.id.tv_edit);
        tv_bank_acc_no=(TextView)findViewById(R.id.tvAccountNo) ;
        tv_bank_name=(TextView)findViewById(R.id.tvAccountHolder);
        SessionManager sessionManager = new SessionManager(AddBankAccountActivity.this);
        addBankAccountPresenter = new AddBankAccountPresenter(this,getApplicationContext());
        token = sessionManager.getSessionToken();
        addBankAccountPresenter.getProfileDetails(token);
        VariableConstant.flag=1;
        Log.i("sfd", sessionManager.getBankName());

        /*if(sessionManager.getBankNumber() == null && sessionManager.getBankName()==null){
            tv_bank_state.setText("Add your Bank Account");
        }
        else {
            tv_bank_state.setText("Edit your Bank Account");
        }*/




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
            getSupportActionBar().setTitle("Bank Details");
        }
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.bank_details));
        tv_title.setTypeface(ClanaproNarrMedium);
       /* pDialog = new ProgressDialog(getApplication());
      //  pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);*/

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
           finish();
            Intent intent=new Intent(AddBankAccountActivity.this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addBankAccountPresenter.getProfileDetails(token);
       /* tv_bank_name.setText(sessionManager.getBankName());
        tv_bank_acc_no.setText(sessionManager.getBankNumber());*/

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
        Intent intent=new Intent(AddBankAccountActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void startProgressBar() {
      //pDialog.show();
    }

    @Override
    public void stopProgressBar() {
      // pDialog.dismiss();
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, getString(R.string.serverError), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void setProfileDetails(final BankList bankList) {
        tv_bank_name.setText(bankList.getName());
        tv_bank_acc_no.setText(bankList.getAccountNumber());
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddBankAccountActivity.this,BankNewAccountActivity.class);
                intent.putExtra("bankname",bankList.getName());
                intent.putExtra("accountno",bankList.getAccountNumber());
                intent.putExtra("holdername",bankList.getAccount_holder_name());
                intent.putExtra("routing",bankList.getRouting_number());
                intent.putExtra("iban",bankList.getIbanNumber());
                intent.putExtra("swift",bankList.getSwiftCode());
                startActivity(intent);
            }
        });

        Log.i("bank",bankList.getBank_name()+"no"+bankList.getAccountNumber());


        }
    }

