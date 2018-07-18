package com.delex.bankDetails;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.driver.BuildConfig;
import com.delex.driver.R;
import com.delex.utility.SessionManager;
import com.delex.utility.Upload_file_AmazonS3;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import eu.janmuller.android.simplecropimage.CropImage;
import pub.devrel.easypermissions.EasyPermissions;

public class BankNewStripeActivity extends AppCompatActivity implements View.OnClickListener, BankNewStripePresenter.BankNewStripePresenterImplement {

    private static final String TAG = "BankNewStripe";
    private final int REQUEST_CODE_GALLERY = 0x1;
    private final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private final int REQUEST_CODE_CROP_IMAGE = 0x3;
    ImageView ivAddFile;
    private static String sentingCaimDate;
    Typeface ClanaproNarrMedium;
    String[] dob;
    private EditText etName, etLName, etPersonalId, etState, etPostalCode, etCity, etCountry, etAddress;
    private TextInputLayout tilName, tilLastName, tilDob, tilPersonalId, tilState, tilPostalCode, tilCity, tilCountry, tilAddress;
    private ProgressDialog pDialog;
    private String imageUrl = "";
    private Bitmap bitmap;
    private boolean isPicturetaken;
    private File mFileTemp;
    private String token = "", ip = "";
    private BankNewStripePresenter bankNewStripePresenter;
    private DatePickerFragment datePickerFragment;
    private static EditText etDob;
    private LinearLayout llOuter;

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_new_stripe_details);
        initViews();
        bankNewStripePresenter = new BankNewStripePresenter(this);
        bankNewStripePresenter.getIp();
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        closeActivity();
    }

    @Override
    public void onFailure() {
    }

    /**********************************************************************************************/

    @Override
    public void ipAddress(String ip) {
        this.ip = ip;
        Log.d(TAG, "ipAddress: " + ip);
    }

    /**
     * <h1>initActionBar</h1>
     * initilize the action bar
     */

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        datePickerFragment = new DatePickerFragment();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_cancel);
        }
        String state = Environment.getExternalStorageState();
        SessionManager sessionManager = new SessionManager(this);
        String filename =System.currentTimeMillis() + ".png";
        token = sessionManager.getSessionToken();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), filename);
        } else {
            mFileTemp = new File(getFilesDir(), filename);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");

        TextView tv_title = (TextView) findViewById(R.id.tvBankDetails);
        tv_title.setTypeface(ClanaproNarrMedium);

        TextView tvSave = (TextView) findViewById(R.id.tvSave);
        tvSave.setTypeface(ClanaproNarrMedium);
        tvSave.setOnClickListener(this);

        ivAddFile = (ImageView) findViewById(R.id.ivAddFile);
        ivAddFile.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etLName = (EditText) findViewById(R.id.etLName);
        etDob = (EditText) findViewById(R.id.etDob);
        etPersonalId = (EditText) findViewById(R.id.etPersonalId);
        etState = (EditText) findViewById(R.id.etState);
        etPostalCode = (EditText) findViewById(R.id.etPostalCode);
        etCity = (EditText) findViewById(R.id.etCity);
        etCountry = (EditText) findViewById(R.id.etCountry);
        etAddress = (EditText) findViewById(R.id.etAddress);

        int maxLength = 6;
        etPostalCode.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});


        tilName = (TextInputLayout) findViewById(R.id.tilName);
        tilLastName = (TextInputLayout) findViewById(R.id.tilLastName);
        tilDob = (TextInputLayout) findViewById(R.id.tilDob);
        tilPersonalId = (TextInputLayout) findViewById(R.id.tilPersonalId);
        tilState = (TextInputLayout) findViewById(R.id.tilState);
        tilPostalCode = (TextInputLayout) findViewById(R.id.tilPostalCode);
        tilCity = (TextInputLayout) findViewById(R.id.tilCity);
        tilCountry = (TextInputLayout) findViewById(R.id.tilCountry);
        tilAddress = (TextInputLayout) findViewById(R.id.tilAddress);
        llOuter = (LinearLayout) findViewById(R.id.llOuter);
        llOuter.setOnClickListener(this);

        tvSave.setTypeface(ClanaproNarrMedium);
        etName.setTypeface(ClanaproNarrMedium);
        etLName.setTypeface(ClanaproNarrMedium);
        etDob.setTypeface(ClanaproNarrMedium);
        etPersonalId.setTypeface(ClanaproNarrMedium);
        etState.setTypeface(ClanaproNarrMedium);
        etPostalCode.setTypeface(ClanaproNarrMedium);
        etCity.setTypeface(ClanaproNarrMedium);
        etAddress.setTypeface(ClanaproNarrMedium);

        dob = new String[3];
      /*  etDob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before - count == 1) {
                    etDob.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 || s.length() == 5) {
                    etDob.setText(s + "/");
                    etDob.setSelection(etDob.getText().length());
                }

            }
        });*/
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!datePickerFragment.isResumed())
                {
                    datePickerFragment.show(getFragmentManager(), "dataPicker");
                }
            }
        });

        Bundle bundleBankDetails = getIntent().getExtras();
        if (bundleBankDetails != null) {
            setValues(bundleBankDetails);
        }

    }

    private void setValues(Bundle bundleBankDetails) {
        try {
            etName.setText(bundleBankDetails.getString("fname"));
            etLName.setText(bundleBankDetails.getString("lname"));
            etCountry.setText(bundleBankDetails.getString("country"));
            etState.setText(bundleBankDetails.getString("state"));
            etCity.setText(bundleBankDetails.getString("city"));
            etAddress.setText(bundleBankDetails.getString("address"));
            etDob.setText(bundleBankDetails.getString("month") + "/" + bundleBankDetails.getString("day") + "/" + bundleBankDetails.getString("year"));
            etPostalCode.setText(bundleBankDetails.getString("postalcode"));

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAddFile:
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (EasyPermissions.hasPermissions(this, perms)) {
                    selectImage();
                } else {
                    // Do not have permissions, requesting permission
                    EasyPermissions.requestPermissions(this, getString(R.string.read_storage_and_camera_state_permission_message),
                            VariableConstant.RC_LOCATION_STATE, perms);
                }

                break;

            case R.id.tvSave:
                if (valid()) {
                   // amzonUpload();
                    addBankStripeDetais();
                }
            case R.id.llOuter:
                Utility.hideSoftKeyboard(this);
                break;
        }
    }

    private boolean valid() {
        dob = etDob.getText().toString().split("/");

        if (etName.getText().toString().equals("")) {
            tilName.setErrorEnabled(true);
            tilName.setError(getString(R.string.enterAccountHoldername));
            return false;
        } else if (etLName.getText().toString().equals("")) {
            tilName.setErrorEnabled(false);

            tilLastName.setErrorEnabled(true);
            tilLastName.setError(getString(R.string.enterLastName));
            return false;
        } else if (etDob.getText().toString().equals("")) {
            tilName.setErrorEnabled(false);
            tilLastName.setErrorEnabled(false);

            tilDob.setErrorEnabled(true);
            tilDob.setError(getString(R.string.enterDob));
            return false;
        }
        /*else if (etDob.getText().toString().length() != 10) {
            tilName.setErrorEnabled(false);
            tilLastName.setErrorEnabled(false);

            tilDob.setErrorEnabled(true);
            tilDob.setError(getString(R.string.enterValidDob));
            return false;
        }*/

        /*int month = Integer.parseInt(dob[0]);
        int day = Integer.parseInt(dob[1]);
        int year = Integer.parseInt(dob[2]);

        if (month > 12) {
            tilName.setErrorEnabled(false);
            tilLastName.setErrorEnabled(false);
            tilDob.setErrorEnabled(true);
            tilDob.setError(getString(R.string.enterValidMonth));
            return false;
        } else if (day > 31) {
            tilName.setErrorEnabled(false);
            tilLastName.setErrorEnabled(false);

            tilDob.setErrorEnabled(true);
            tilDob.setError(getString(R.string.enterValidDay));
            return false;
        } else if (year > 2017) {
            tilName.setErrorEnabled(false);
            tilLastName.setErrorEnabled(false);

            tilDob.setErrorEnabled(true);
            tilDob.setError(getString(R.string.enterValidYear));
            return false;
        }*/ else if (etPersonalId.getText().toString().equals("")) {
            tilLastName.setErrorEnabled(false);
            tilName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);

            tilPersonalId.setErrorEnabled(true);
            tilPersonalId.setError(getString(R.string.enterPersonalid));
            return false;
        } else if (etPersonalId.getText().toString().length() != 9) {
            tilLastName.setErrorEnabled(false);
            tilName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);

            tilPersonalId.setErrorEnabled(true);
            tilPersonalId.setError(getString(R.string.enterValidPersonalid));
            return false;
        } else if (etAddress.getText().toString().equals("")) {
            tilLastName.setErrorEnabled(false);
            tilName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);
            tilPersonalId.setErrorEnabled(false);

            tilAddress.setErrorEnabled(true);
            tilAddress.setError(getString(R.string.enterAddress));
            return false;
        } else if (etCity.getText().toString().equals("")) {
            tilLastName.setErrorEnabled(false);
            tilName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);
            tilPersonalId.setErrorEnabled(false);
            tilAddress.setErrorEnabled(false);

            tilCity.setErrorEnabled(true);
            tilCity.setError(getString(R.string.enterCity));
            return false;
        } else if (etState.getText().toString().equals("")) {
            tilLastName.setErrorEnabled(false);
            tilName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);
            tilPersonalId.setErrorEnabled(false);
            tilCity.setErrorEnabled(false);
            tilAddress.setErrorEnabled(false);

            tilState.setErrorEnabled(true);
            tilState.setError(getString(R.string.enterState));
            return false;
        }
       /* else if(etCountry.getText().toString().equals(""))
        {
            tilLastName.setErrorEnabled(false);
            tilName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);
            tilPersonalId.setErrorEnabled(false);
            tilCity.setErrorEnabled(false);
            tilAddress.setErrorEnabled(false);
            tilState.setErrorEnabled(false);

            tilCountry.setErrorEnabled(true);
            tilCountry.setError(getString(R.string.enteCountry));
            return false;
        }*/
        else if (etPostalCode.getText().toString().equals("")) {
            tilName.setErrorEnabled(false);
            tilLastName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);
            tilPersonalId.setErrorEnabled(false);
            tilState.setErrorEnabled(false);
            tilCity.setErrorEnabled(false);
            tilAddress.setErrorEnabled(false);

            tilPostalCode.setErrorEnabled(true);
            tilPostalCode.setError(getString(R.string.enterPostalCode));

            return false;
        }


        else if (!isPicturetaken) {
            tilName.setErrorEnabled(false);
            tilLastName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);
            tilPersonalId.setErrorEnabled(false);
            tilState.setErrorEnabled(false);
            tilPostalCode.setErrorEnabled(false);
            tilCity.setErrorEnabled(false);
            tilAddress.setErrorEnabled(false);

            Toast.makeText(this, getString(R.string.plsUploadIdProf), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            tilLastName.setErrorEnabled(false);
            tilName.setErrorEnabled(false);
            tilDob.setErrorEnabled(false);
            tilPersonalId.setErrorEnabled(false);
            tilState.setErrorEnabled(false);
            tilPostalCode.setErrorEnabled(false);
            tilCity.setErrorEnabled(false);
            tilAddress.setErrorEnabled(false);
            return true;
        }
    }

    private void amzonUpload() {
        String BUCKETSUBFOLDER = VariableConstant.BANK_PROOF;
        Upload_file_AmazonS3 amazonS3 = Upload_file_AmazonS3.getInstance(this, VariableConstant.COGNITO_POOL_ID);
        imageUrl = VariableConstant.AMAZON_BASE_URL + VariableConstant.BUCKET_NAME + "/" +BUCKETSUBFOLDER+ "/" + mFileTemp.getName();


        amazonS3.Upload_data(VariableConstant.BUCKET_NAME, BUCKETSUBFOLDER +"/"+ mFileTemp.getName(), mFileTemp, new Upload_file_AmazonS3.Upload_CallBack() {
            @Override
            public void sucess(String url) {
                Log.d(TAG, "amzonUpload: " + url);
            }

            @Override
            public void sucess(String url, String type) {

            }

            @Override
            public void error(String errormsg) {
                Log.d(TAG, "error: " + errormsg);
            }
        });
    }

    private void addBankStripeDetais() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("city", etCity.getText().toString());
            jsonObject.put("country", "US");
            jsonObject.put("line1", etAddress.getText().toString());
            jsonObject.put("postal_code", etPostalCode.getText().toString());
            jsonObject.put("state", etState.getText().toString());
            jsonObject.put("month", dob[0]);
            jsonObject.put("day", dob[1]);
            jsonObject.put("year", dob[2]);
            jsonObject.put("first_name", etName.getText().toString());
            jsonObject.put("last_name", etLName.getText().toString());
            jsonObject.put("document", imageUrl);
            jsonObject.put("personal_id_number", etPersonalId.getText().toString());
            jsonObject.put("date", Utility.date());
            jsonObject.put("ip", ip);

            bankNewStripePresenter.addBankDetails(token, jsonObject);
        } catch (Exception e) {
            Log.d(TAG, "addBankStripeDetais: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selectImage() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.profile_pic_options, null);
        alertDialogBuilder.setView(view);

        final AlertDialog mDialog = alertDialogBuilder.create();
        mDialog.setCancelable(false);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btnCamera = (Button) view.findViewById(R.id.camera);
        Button btnCancel = (Button) view.findViewById(R.id.cancel);
        Button btnGallery = (Button) view.findViewById(R.id.gallery);
        Button btnRemove = (Button) view.findViewById(R.id.removephoto);
        TextView tvHeader = (TextView) view.findViewById(R.id.tvHeader);

        btnCamera.setTypeface(ClanaproNarrMedium);
        btnCancel.setTypeface(ClanaproNarrMedium);
        btnGallery.setTypeface(ClanaproNarrMedium);
        btnRemove.setTypeface(ClanaproNarrMedium);
        tvHeader.setTypeface(ClanaproNarrMedium);

        if (isPicturetaken) {
            btnRemove.setVisibility(View.VISIBLE);
        } else {
            btnRemove.setVisibility(View.GONE);
        }

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                mDialog.dismiss();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                mDialog.dismiss();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivAddFile.setImageResource(R.drawable.vector_add_file);
                isPicturetaken = false;
                mDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            mImageCaptureUri = Uri.fromFile(mFileTemp);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mImageCaptureUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", mFileTemp);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (Exception e) {
            Log.d("error", "cannot take picture", e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode != RESULT_OK) {
                return;
            }

            switch (requestCode) {

                case REQUEST_CODE_GALLERY:

                    try {
                        InputStream inputStream = this.getContentResolver().openInputStream(
                                data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                        Log.d("", "inputStream" + inputStream);
                        Log.d("", "fileOutputStream" + fileOutputStream);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        inputStream.close();


                        bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                        bitmap = Bitmap.createScaledBitmap(bitmap, ivAddFile.getWidth(), ivAddFile.getHeight(), true);
                        ivAddFile.setImageBitmap(bitmap);

                        isPicturetaken = true;
                        //startCropImage();

                    } catch (Exception e) {

                        Log.d("", "Error while creating temp file", e);
                    }

                    break;
                case REQUEST_CODE_TAKE_PICTURE:
                    isPicturetaken = true;
                    bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                    bitmap = Bitmap.createScaledBitmap(bitmap, ivAddFile.getWidth(), ivAddFile.getHeight(), true);
                    ivAddFile.setImageBitmap(bitmap);

                    //startCropImage();
                    break;

                case REQUEST_CODE_CROP_IMAGE:

                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    Log.d("", "path fileOutputStream " + path);

                    if (path == null) {

                        return;
                    }

                    bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                    bitmap = Bitmap.createScaledBitmap(bitmap, ivAddFile.getWidth(), ivAddFile.getHeight(), true);
                    ivAddFile.setImageBitmap(bitmap);
                    ivAddFile.setBackgroundDrawable(null);


                    Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                    BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Paint paint = new Paint();
                    paint.setShader(shader);
                    paint.setAntiAlias(true);
                    Canvas c = new Canvas(circleBitmap);
                    c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);


                    isPicturetaken = true;
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, "onActivityResult: " + e);
        }


    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            sentingCaimDate = Utility.sentingDateFormat(year, monthOfYear, dayOfMonth);
            etDob.setText(Utility.displayDateFormat(year, monthOfYear, dayOfMonth));
        }

    }

}
