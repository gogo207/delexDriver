package com.delex.signup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.delex.adapter.ImageUploadRVA;
import com.delex.country_picker.CountryPicker;
import com.delex.country_picker.CountryPickerListener;
import com.delex.driver.R;
import com.delex.pojo.SignupZonedata;
import com.delex.pojo.SignupZonesPojo;
import com.delex.pojo.ValidatorPojo;
import com.delex.utility.ImageEditUpload;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Upload_file_AmazonS3;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import eu.janmuller.android.simplecropimage.CropImage;

/**************************************************************************************************/
public class SignupPersonal extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int SELECT_AN_OPERATOR = 404;
    private static final int SELECT_AN_ZONE = 409;
    private static final int CAMERA_PIC = 11, GALLERY_PIC = 12, CROP_IMAGE = 13, REMOVE_IMAGE = 14;
    private final String TAG = SignupPersonal.class.getSimpleName();
    String base64dependentPic = "";
    String mobileNumberWithoutZero, fullMobileNumber;
    private EditText et_fname, et_lname, et_signup_mob, et_email, et_password, et_referral;
    private String str_fname, str_lname, str_countrycode, str_mob, str_email, str_pass, str_zone, str_referal_code;
    private TextView tv_next;
    private LinearLayout ll_nxt_progress;
    private ImageView iv_signup_pp, iv_camera_uploader3,iv_camera_uploader4;
    private ImageEditUpload imageEditUpload;
    private String fileType = "image", fileName, takenNewImage, state;
    private int selectedWorkType = 1;
    private TextView countryCode, tv_operator, tv_signup_zones;
    private ImageView flag;
    private ProgressDialog pDialog;
    private String ImageType = "", profile_pic = "profile_pic", licence_pic = "add_licence",id_proof_pic="id_proof_pic";
    private SessionManager sessionManager;
    private ArrayList<String> imagefile;
    private String Profile_pic_url = "", license_image = "",Id_proof_image="";
    private int imageCount;
    private ImageUploadRVA imageUploadRVA;
    private RelativeLayout ll_add_licence,ll_add_id_proof;
    private int Licence_image_no = 1;
    private String Zone_id = "";
    private ArrayList<SignupZonedata> zonedata = new ArrayList<>();
    private JSONArray selectedZones = new JSONArray();
    private double[] location;
    private double lat, lng;
    private boolean validReferral = false;
    private int maxPhoneLength, minPhoneLength = 0;
    private String lastMobNum = "", lastEmail = "";

    /**********************************************************************************************/
    public static int getResId(String drawableName) {
        try {
            Class<R.drawable> res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("CountryCodePicker", "Failure to get drawable id.", e);
        }
        return -1;
    }

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_personal);
        sessionManager = new SessionManager(SignupPersonal.this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        overridePendingTransition(R.anim.bottem_slide_down, R.anim.stay_activity);
        imagefile = new ArrayList<>();

        initActionBar();
        initializeViews();
    }

    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        if (VariableConstant.SIGNUP_PERSONAL) {
            finish();
            VariableConstant.SIGNUP_PERSONAL = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("ImageType", ImageType);
    }

    /**********************************************************************************************/

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.containsKey("ImageType");
//        ImageType = savedInstanceState.getString("ImageType");
        Log.d(TAG, "onRestoreInstanceState: " + ImageType);

    }

    /**********************************************************************************************/
                                                                      /*initializeViews*/
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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.selector_signup_close);
        }
        ImageView iv_search;
        TextView tv_title;

        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.signup_));
        tv_title.setTypeface(ClanaproNarrMedium);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setVisibility(View.GONE);
    }

    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {
        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        final TextView tv_personal, tv_licence, tv_zones, tv_camera_up;

        RadioGroup rg_WorkGroup = (RadioGroup) findViewById(R.id.rg_signup_work_group);
        rg_WorkGroup.setOnCheckedChangeListener(this);

        tv_personal = (TextView) findViewById(R.id.tv_personal);
        tv_personal.setTypeface(ClanaproNarrNews);

        tv_zones = (TextView) findViewById(R.id.tv_zones);
        tv_zones.setTypeface(ClanaproNarrNews);

        tv_camera_up = (TextView) findViewById(R.id.tv_camera_up);
        tv_camera_up.setTypeface(ClanaproNarrNews);

        tv_signup_zones = (TextView) findViewById(R.id.tv_signup_zones);
        tv_signup_zones.setTypeface(ClanaproNarrNews);
        tv_signup_zones.setOnClickListener(this);

        tv_licence = (TextView) findViewById(R.id.tv_licence);
        tv_licence.setTypeface(ClanaproNarrNews);

        et_fname = (EditText) findViewById(R.id.et_fname);
        et_fname.setTypeface(ClanaproNarrNews);
        //et_fname.addTextChangedListener(new CustomTextWatcher(et_fname, this));

        et_lname = (EditText) findViewById(R.id.et_lname);
        et_lname.setTypeface(ClanaproNarrNews);

        et_signup_mob = (EditText) findViewById(R.id.et_signup_mob);
        et_signup_mob.setTypeface(ClanaproNarrNews);

        et_email = (EditText) findViewById(R.id.et_email);
        et_email.setTypeface(ClanaproNarrNews);

        et_referral = (EditText) findViewById(R.id.et_referral);
        et_email.setTypeface(ClanaproNarrNews);

        et_password = (EditText) findViewById(R.id.et_password);
        et_password.setTypeface(ClanaproNarrNews);

        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_next.setTypeface(ClanaproNarrMedium);
        tv_next.setOnClickListener(this);

        iv_camera_uploader3 = (ImageView) findViewById(R.id.iv_camera_uploader3);
        iv_camera_uploader4 = (ImageView) findViewById(R.id.iv_camera_uploader4);
        iv_signup_pp = (ImageView) findViewById(R.id.iv_signup_pp);
        iv_signup_pp.setOnClickListener(this);

        pDialog = new ProgressDialog(SignupPersonal.this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        tv_operator = (TextView) findViewById(R.id.tv_signup_operator);
        tv_operator.setOnClickListener(this);

        ll_add_licence = (RelativeLayout) findViewById(R.id.ll_add_licence);
        ll_add_licence.setOnClickListener(this);

        ll_add_id_proof=(RelativeLayout)findViewById(R.id.ll_add_id_proof);
        ll_add_id_proof.setOnClickListener(this);

//        RecyclerView rv_license = (RecyclerView) findViewById(R.id.rv_signup_licence);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(SignupPersonal.this, LinearLayoutManager.HORIZONTAL, true);
//        rv_license.setLayoutManager(layoutManager);

        /*imageUploadRVA = new ImageUploadRVA(this, imagefile, new ImageUploadRVA.RemoveImage() {
            @Override
            public void ImageRemoved(int position) {
                imagefile.remove(position);
                imageCount--;
                imageUploadRVA.notifyDataSetChanged();
                if (imageCount < Licence_image_no) {
                    ll_add_licence.setVisibility(View.VISIBLE);
                }
            }
        });*/
//        rv_license.setAdapter(imageUploadRVA);
//        imageUploadRVA.notifyDataSetChanged();


        RelativeLayout countryPicker = (RelativeLayout) findViewById(R.id.countryPicker);
        countryCode = (TextView) findViewById(R.id.code);
        flag = (ImageView) findViewById(R.id.flag);

        String code = Utility.getCounrtyCode(SignupPersonal.this);
        if (!code.isEmpty()) {
            String drawableName = "flag_"
                    + code.toLowerCase(Locale.ENGLISH);
            flag.setBackgroundResource(getResId(drawableName));


            String allCountriesCode = null;
            try {
                allCountriesCode = readEncodedJsonString(this);
                JSONArray countrArray = new JSONArray(allCountriesCode);
                JSONObject jsonObject;
                String dialCode = "";
                for (int index = 0; index < countrArray.length(); index++) {
                    jsonObject = countrArray.getJSONObject(index);
                    if (jsonObject.getString("code").equals(code)) {
                        dialCode = jsonObject.getString("dial_code");
                        maxPhoneLength = jsonObject.getInt("max_digits");
                        minPhoneLength = (!jsonObject.getString("min_digits").isEmpty()) ? jsonObject.getInt("min_digits") : 5;
                        Utility.printLog("Minimum length " + minPhoneLength);
                        break;
                    }
                }

                countryCode.setText(dialCode);
                sessionManager.setCountryCode(dialCode);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        InputFilter[] fArray = new InputFilter[1];
        if (maxPhoneLength == 0) {
            et_signup_mob.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        } else {
            fArray[0] = new InputFilter.LengthFilter(maxPhoneLength);
            et_signup_mob.setFilters(fArray);
        }
        if (et_signup_mob.getText().toString().length() > 0) {
            if (et_signup_mob.getText().toString().charAt(0) == '0') {
                mobileNumberWithoutZero = et_signup_mob.getText().toString().substring(1);
            } else {
                mobileNumberWithoutZero = et_signup_mob.getText().toString();
            }
        }

        fullMobileNumber = countryCode.getText().toString() + mobileNumberWithoutZero;
        str_countrycode = countryCode.getText().toString();

        countryPicker.setOnClickListener(this);

        ll_nxt_progress = (LinearLayout) findViewById(R.id.ll_nxt_progress);
//        ll_nxt_progress.setVisibility(View.VISIBLE);


        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String email = et_email.getText().toString();
                    if (!email.isEmpty()) {
                        if (Utility.validateEmail(email)/* && !email.equals(lastEmail)*/) {
                            validateMainPhone(1);
                        } else {
//                            Utility.BlueToast(SignupPersonal.this, "Invalid email id.");
                            Utility.mShowMessage(getResources().getString(R.string.message),
                                    getResources().getString(R.string.invalidEmail),
                                    SignupPersonal.this);

                            et_email.post(new Runnable() {
                                public void run() {
                                    et_email.requestFocus();
                                }
                            });
                        }
                    }

                }

            }
        });
        et_signup_mob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String mob = et_signup_mob.getText().toString();
                    Utility.printLog(" mobile number : " + mob + " Last mobile number : " + lastMobNum);
                    if (!mob.isEmpty()) {
                        if (mob.length() >= minPhoneLength/* && !mob.equals(lastMobNum)*/) {
                            validateMainPhone(2);
                        } else {

                            Utility.mShowMessage(getResources().getString(R.string.message),
                                    getResources().getString(R.string.invalidPhone),
                                    SignupPersonal.this);
                            et_signup_mob.post(new Runnable() {
                                public void run() {
                                    et_signup_mob.requestFocus();
                                }
                            });

                        }
                    }

                }
            }
        });

        et_referral.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_referral.getText().toString().isEmpty()) {
                        validateReferralCode();
                    }

                }
            }
        });

        LinearLayout ll_layout = (LinearLayout) findViewById(R.id.ll_layout);
        ll_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utility.hideSoftKeyboard(SignupPersonal.this);
                Utility.printLog("rkkkk inside ontouch");
                return false;
            }
        });

       /* et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !et_password.getText().toString().isEmpty()) {
                    tv_signup_zones.callOnClick();
                }
            }
        });*/


    }

    /**********************************************************************************************/

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.tv_next:
                SignupPersonalValidate();
                break;
            case R.id.countryPicker:
                showDialoagforcountrypicker();
                break;
            case R.id.iv_signup_pp:
                imageEditUpload = new ImageEditUpload(this, profile_pic);
                ImageType = profile_pic;
                break;
            case R.id.tv_signup_operator:
                Intent intent = new Intent(SignupPersonal.this, SelectAnOperator.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("OPERATION", "signup_operator");
                mBundle.putString("TITLE", getResources().getString(R.string.title_activity_select_an_operator));
                intent.putExtras(mBundle);
                startActivityForResult(intent, SELECT_AN_OPERATOR);
                break;
            case R.id.tv_signup_zones:

                if (zonedata != null && zonedata.size() > 0) {
                    Intent intent1 = new Intent(SignupPersonal.this, GenericListActivity.class);
                    Bundle mBundle1 = new Bundle();
                    mBundle1.putString("TITLE", getResources().getString(R.string.select_zone));
                    mBundle1.putSerializable("DATA", zonedata);
                    mBundle1.putSerializable("TYPE", "ZONE");
                    intent1.putExtras(mBundle1);
                    startActivityForResult(intent1, SELECT_AN_ZONE);

                } else {
                    getZonesList();
                }


                break;
            case R.id.ll_add_licence:
                imageEditUpload = new ImageEditUpload(this, licence_pic);
                ImageType = licence_pic;

                break;
            case R.id.ll_add_id_proof:
                imageEditUpload = new ImageEditUpload(this, id_proof_pic);
                ImageType = id_proof_pic;

                break;


        }
    }

    /**
     * <p>checking wether the details filled or not,
     * </p>
     */
    void SignupPersonalValidate() {
        str_fname = et_fname.getText().toString();
        str_lname = et_lname.getText().toString();
        str_mob = et_signup_mob.getText().toString();
        str_email = et_email.getText().toString();
        str_pass = et_password.getText().toString();
        str_zone = tv_signup_zones.getText().toString();


        if (str_fname.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_fname), SignupPersonal.this);
            et_fname.requestFocus();
        } else if (str_mob.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_phone_no), SignupPersonal.this);
            et_signup_mob.requestFocus();
        } else if (str_email.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_email), SignupPersonal.this);
            et_email.requestFocus();
        } else if (str_pass.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.password_miss), SignupPersonal.this);
            et_password.requestFocus();
        } else if (Profile_pic_url.matches("")) {

            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.choose_prof_pic), SignupPersonal.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                            iv_signup_pp.callOnClick();
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });

        } else if (license_image.matches("")) {

            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.choose_licence_img), SignupPersonal.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                            ll_add_licence.callOnClick();
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });
        }  if (Id_proof_image.matches("")) {

            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.choose_id_proof_img), SignupPersonal.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                            ll_add_id_proof.callOnClick();
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });
        }else if (!Utility.validateEmail(str_email)) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.invalidEmail), SignupPersonal.this);
        } else if (!et_referral.getText().toString().isEmpty() && !validReferral) {
            validateReferralCode();
        } else {
            String currentVersion = "";

            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Intent mIntent = new Intent(SignupPersonal.this, SignupVehicle.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("profile_pic", Profile_pic_url);
            mBundle.putString("idCopy",Id_proof_image);
            mBundle.putString("ent_first_name", str_fname);
            mBundle.putString("ent_last_name", str_lname);
            mBundle.putString("str_countrycode", str_countrycode);
            mBundle.putString("ent_mobile", str_mob);
            mBundle.putString("ent_email", str_email);
            mBundle.putString("ent_password", str_pass);
            mBundle.putString("accountType", String.valueOf(selectedWorkType));
            mBundle.putString("zone", selectedZones.toString());
            mBundle.putString("referal_code", str_referal_code);
            mBundle.putString("ent_appversion", currentVersion);
            mBundle.putString("ent_devMake", Build.MANUFACTURER);
            mBundle.putString("ent_devModel", Build.MODEL);
            mBundle.putString("licence_urls", license_image);
            mIntent.putExtras(mBundle);
            startActivity(mIntent);

            Utility.printLog("person data : " + mBundle.toString() + "Zone_id : " + selectedZones.toString());

        }

    }

    /******************************************************for country picker**********************/
    private void showDialoagforcountrypicker() {
        final CountryPicker picker = CountryPicker.newInstance(getResources().getString(R.string.select_country));
        picker.show(getSupportFragmentManager(), "COUNTRY_CODE_PICKER");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int min, int max) {
                countryCode.setText(dialCode);
                str_countrycode = dialCode;
                String drawableName = "flag_"
                        + code.toLowerCase(Locale.ENGLISH);
                flag.setBackgroundResource(getResId(drawableName));
                if (et_signup_mob.getText().toString().length() > 0) {

                    if (et_signup_mob.getText().toString().charAt(0) == '0') {
                        mobileNumberWithoutZero = et_signup_mob.getText().toString().substring(1);
                    } else {
                        mobileNumberWithoutZero = et_signup_mob.getText().toString();

                    }
                }
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(max);
                et_signup_mob.setFilters(fArray);

                fullMobileNumber = countryCode.getText().toString() + mobileNumberWithoutZero;

                picker.dismiss();
            }
        });
    }

    /**********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
                                                                     /*onActivityResult*/

    /**********************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_AN_OPERATOR && data != null) {
            String operatorName = data.getStringExtra("operator");
            int operatorId = data.getIntExtra("operatorId", 0);
            Log.d(TAG, "operator" + operatorName + " " + operatorId);
            tv_operator.setText(operatorName);
        } else if (requestCode == SELECT_AN_ZONE && data != null) {

            zonedata = (ArrayList<SignupZonedata>) data.getSerializableExtra("DATA");
            selectedZones = new JSONArray();
            String zoneNames = "";
            for (SignupZonedata s : zonedata) {
                if (s.isSelected()) {
                    if (zoneNames.equals("")) {
                        zoneNames = s.getName();
                    } else {
                        zoneNames = zoneNames + "," + s.getName();
                    }
                    selectedZones.put(s.getId());
                }

            }
            Log.d(TAG, "ZONES " + zoneNames);

            tv_signup_zones.setText(zoneNames);
        } else if (resultCode != RESULT_OK) {
            return;
        } else if (requestCode != -1) {
            switch (requestCode) {
                case CAMERA_PIC:
                    fileType = "image";
                    startCropImage();
                    break;

                case GALLERY_PIC:
                    try {
                        Utility.printLog("RegistrationAct in GALLERY_PIC:");
                        takenNewImage = "";
                        state = Environment.getExternalStorageState();
                        takenNewImage = "EbbaDriver" + String.valueOf(System.nanoTime()) + ".png";

                        if (Environment.MEDIA_MOUNTED.equals(state)) {

                            VariableConstant.newFile = new File(Environment.getExternalStorageDirectory() /*+ "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages/"*/, takenNewImage);
                        } else {
                            VariableConstant.newFile = new File(getFilesDir() /*+ "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages/"*/, takenNewImage);
                        }
                        Utility.printLog("m file=" + VariableConstant.newFile.getAbsolutePath());

                        InputStream inputStream = getContentResolver().openInputStream(
                                data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(VariableConstant.newFile.getAbsolutePath()));

                        Utility.copyStream(inputStream, fileOutputStream);

                        fileOutputStream.close();
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        VariableConstant.newProfileImageUri = Uri.fromFile(VariableConstant.newFile);
                        Utility.printLog("RegistrationAct in GALLERY_PIC fileOutputStream: " + fileOutputStream);
                        startCropImage();
                    } catch (Exception e) {
                        Utility.printLog("RegistrationAct in GALLERY_PIC Error while creating newfile:" + e);
                    }
                    break;

                case CROP_IMAGE:
                    fileName = "";
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path == null) {
                        Utility.printLog("RegistrationAct CROP_IMAGE file path is null: " + VariableConstant.newFile.getPath());

                        return;
                    } else {
                        if (ImageType.equals(profile_pic)) {
                            VariableConstant.isPictureTaken = true;
                        }
                        Utility.printLog("RegistrationAct CROP_IMAGE FilePAth : " + VariableConstant.newFile.getPath());
                        VariableConstant.newProfileImageUri = Uri.fromFile(VariableConstant.newFile);
                        Utility.printLog("RegistrationAct CROP_IMAGE file URi: " + VariableConstant.newProfileImageUri);

                        fileName = VariableConstant.newFile.getName();
                        Utility.printLog("RegistrationAct CROP_IMAGE fileName: " + fileName);

                        try {
                            String[] type = fileName.split("\\.");
                            Log.d("mura", "File Type: " + type[1]);

                            byte[] bytes = new byte[(int) VariableConstant.newFile.length()];
                            InputStream inputStream = getContentResolver().openInputStream(VariableConstant.newProfileImageUri);
                            if (inputStream != null) {
                                inputStream.read(bytes);
                            }
                            byte[] encoded = Base64.encode(bytes, Base64.NO_WRAP);
                            base64dependentPic = new String(encoded);
                            base64dependentPic = "data:image/png;base64," + base64dependentPic;
                            Bitmap bMap = BitmapFactory.decodeFile(path);

                            if (ImageType.equals(profile_pic)) {

                                if (Utility.isNetworkAvailable(SignupPersonal.this)) {
                                    Bitmap circle_bMap = Utility.getCircleCroppedBitmap(bMap);
                                    System.out.println(TAG + " " + "circle_bMap=" + circle_bMap);
                                    iv_signup_pp.setImageBitmap(circle_bMap);
                                    VariableConstant.isPictureTaken = true;
                                    amzonUpload(new File(path));
                                } else {
                                    Utility.BlueToast(SignupPersonal.this, getResources().getString(R.string.no_network));
                                }

                            } else if (ImageType.equals(licence_pic)) {

                                if (Utility.isNetworkAvailable(SignupPersonal.this)) {
                                    iv_camera_uploader3.setVisibility(View.VISIBLE);
                                    bMap = Bitmap.createScaledBitmap(bMap, iv_camera_uploader3.getWidth(), iv_camera_uploader3.getHeight(), true);
                                    iv_camera_uploader3.setImageBitmap(bMap);
                                    amzonUpload(new File(path));
                                } else {
                                    Utility.BlueToast(SignupPersonal.this, getResources().getString(R.string.no_network));
                                }

                            }
                            else if (ImageType.equals(id_proof_pic)) {

                                if (Utility.isNetworkAvailable(SignupPersonal.this)) {
                                    iv_camera_uploader4.setVisibility(View.VISIBLE);
                                    bMap = Bitmap.createScaledBitmap(bMap, iv_camera_uploader4.getWidth(), iv_camera_uploader4.getHeight(), true);
                                    iv_camera_uploader4.setImageBitmap(bMap);
                                    amzonUpload(new File(path));
                                } else {
                                    Utility.BlueToast(SignupPersonal.this, getResources().getString(R.string.no_network));
                                }

                            }
                        } catch (IOException e) {
                            Utility.printLog("RegistrationAct in CROP_IMAGE exception while copying file = " + e.toString());
                        }
                    }
                    break;

                default:

                    Utility.BlueToast(SignupPersonal.this, getResources().getString(R.string.smthWentWrong));

                    break;
            }
        }

    }

    /**********************************************************************************************/
                                                                       /*startCropImage*/

    /**********************************************************************************************/
    private void startCropImage() {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, VariableConstant.newFile.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 4);
        startActivityForResult(intent, SignupPersonal.CROP_IMAGE);
    }

    /**********************************************************************************************/
                                                                               /*remove*/

    /**********************************************************************************************/
    public void remove() {
        if (ImageType.equals(profile_pic)) {
            iv_signup_pp.setImageResource(R.drawable.signup_profile_default_image);
            VariableConstant.isPictureTaken = false;
        }

    }

    /***************************************************************************************/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_activity, R.anim.bottem_slide_up);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.rb_signup_work_operator:
                tv_operator.setVisibility(View.VISIBLE);
//                ll_nxt_progress.setVisibility(View.GONE);
                tv_next.setText(getResources().getString(R.string.finish));
                selectedWorkType = 2;
                break;
            case R.id.rb_signup_work_free:
                selectedWorkType = 1;
                tv_operator.setVisibility(View.GONE);
//                ll_nxt_progress.setVisibility(View.VISIBLE);
                tv_next.setText(getResources().getString(R.string.next));
                break;

        }

    }

    /**********************************************************************************************/
    private void amzonUpload(File file) {
        String BUCKETSUBFOLDER = "";
        if (ImageType.equals(profile_pic)) {
            BUCKETSUBFOLDER = VariableConstant.PROFILE_PIC;
        } else if (ImageType.equals(licence_pic)) {
            BUCKETSUBFOLDER = VariableConstant.LICENCE;
        }
        Log.d(TAG, "amzonUpload: " + file);
        pDialog.show();
        Upload_file_AmazonS3 amazonS3 = Upload_file_AmazonS3.getInstance(this, VariableConstant.COGNITO_POOL_ID);
        final String imageUrl = VariableConstant.AMAZON_BASE_URL + "/" + VariableConstant.BUCKET_NAME + BUCKETSUBFOLDER + "/" + file.getName();
        Log.d(TAG, "amzonUpload: " + imageUrl);
        Log.d(TAG, "amzonUpload: " + BUCKETSUBFOLDER + file.getName());

        amazonS3.   Upload_data(VariableConstant.BUCKET_NAME, BUCKETSUBFOLDER + "/" + fileName, file, new Upload_file_AmazonS3.Upload_CallBack() {
            @Override
            public void sucess(String url) {
                pDialog.dismiss();
                Log.d("URL", url);

                if (ImageType.equals(profile_pic)) {
                    Profile_pic_url = url;
                    Utility.printLog(TAG + " url  " + url);
                    Log.d("Profile_pic_url", Profile_pic_url);
                    /*Picasso.with(SignupPersonal.this)
                            .load(url)
//                            .placeholder(R.drawable.piccaso_load_animation)//signup_profile_default_image)
                            .transform(new CircleTransformation())
                            .into(iv_signup_pp);*/
                } else if (ImageType.equals(licence_pic)) {
                    license_image = url;
                    Log.d("license_image", license_image);
                    /*imagefile.add(imageCount, url);
                    imageCount++;
                    imageUploadRVA.notifyDataSetChanged();

                    if (imageCount > (Licence_image_no - 1)) {
                        ll_add_licence.setVisibility(View.GONE);
                    }*/
                }
                else if (ImageType.equals(id_proof_pic)) {
                    Id_proof_image = url;
                    Log.d("license_image", Id_proof_image);
                }
            }

            @Override
            public void sucess(String url, String type) {

            }

            @Override
            public void error(String errormsg) {
                pDialog.dismiss();
                Log.d("Suri", "error");
            }
        });
    }

    /**********************************************************************************************/
    /**
     * <h2>validateMainPhone</h2>
     * <p>call api for validating email and phone</p>
     *
     * @param choice is used to validate mail or email
     *               1 is to validate mail
     *               2 is to validate mobile phone
     */
    private void validateMainPhone(final int choice) {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.validating));
        JSONObject reqObject = new JSONObject();
        switch (choice) {
            case 1:
                try {
                    lastEmail = et_email.getText().toString();
                    reqObject.put("ent_email", lastEmail);
                    reqObject.put("validationType", choice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    lastMobNum = et_signup_mob.getText().toString();
                    reqObject.put("mobile", lastMobNum);
                    reqObject.put("countryCode", countryCode.getText().toString());
                    reqObject.put("validationType", choice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

        Log.d("authh",sessionManager.getSessionToken()+"gyjryr");
        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(),ServiceUrl.VERIFY_EMAIL_PHONE, OkHttp3Connection.Request_type.POST, reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "validateMainPhone Result " + result);
                if (result != null) {
                    ValidatorPojo validatorPojo = new Gson().fromJson(result, ValidatorPojo.class);
                    switch (validatorPojo.getErrFlag()) {
                        case 0:

                            break;
                        case 1:
                            if (choice == 1) {

                                Utility.mShowMessage(getResources().getString(R.string.message),
                                        validatorPojo.getErrMsg(),
                                        SignupPersonal.this);
//                                Utility.BlueToast(SignupPersonal.this, getResources().getString(R.string.email_reg_err));
                                et_email.setText("");
                                et_email.requestFocus();
                            } else if (choice == 2) {
                                Utility.mShowMessage(getResources().getString(R.string.message),
                                        validatorPojo.getErrMsg(),
                                        SignupPersonal.this);
                                et_signup_mob.setText("");
                                et_signup_mob.requestFocus();

                            }
                            break;
                    }
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "validateMainPhone error " + error);
                pDialog.dismiss();
            }
        }, sessionManager.getSessionToken());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        location = Utility.getLocation(SignupPersonal.this);
        if (location != null && location.length > 0) {
            lat = location[0];
            lng = location[1];
        }
        Utility.printLog("SignupPersonal onResume lat: " + lat, " long: " + lng);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

    public void validateReferralCode() {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.validating));
        JSONObject reqObject = new JSONObject();

        try {
            reqObject.put("code", et_referral.getText().toString());
            reqObject.put("type", 1);
            reqObject.put("lat", lat);
            reqObject.put("long", lng);

            Utility.printLog("validatereferralcode request " + reqObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.VALIDATE_REFERRAL_CODE, OkHttp3Connection.Request_type.POST, reqObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                Log.d(TAG, "validatereferralcode Result " + result);
                if (result != null && !result.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("errFlag")) {
                            if (jsonObject.getInt("errFlag") == 1) {
                                validReferral = false;
                                Utility.mShowMessage(getResources().getString(R.string.message),
                                        jsonObject.getString("errMsg"),
                                        SignupPersonal.this);

//                                Toast.makeText(SignupPersonal.this,jsonObject.getString("errMsg"),Toast.LENGTH_LONG);
                                et_referral.setText("");
                                et_referral.requestFocus();
                            } else if (jsonObject.getInt("errFlag") == 0) {
                                validReferral = true;
                                str_referal_code = et_referral.getText().toString();
                                SignupPersonalValidate();
                            }
                        }

                    } catch (JSONException e) {
                    }
                }
            }

            @Override
            public void onError(String error) {
                pDialog.dismiss();
            }
        }, "ordinory");
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    private void getZonesList() {
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.ZONE, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Operators Result" + result);
                if (result != null) {
                    SignupZonesPojo signupZonesPojo = new Gson().fromJson(result, SignupZonesPojo.class);
//                    manageZoneResponse(signupZonesPojo);
                    if (signupZonesPojo.getErrFlag() == 0) {
                        Intent intent = new Intent(SignupPersonal.this, GenericListActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("TITLE", getResources().getString(R.string.select_zone));
                        mBundle.putSerializable("DATA", signupZonesPojo.getData());
                        mBundle.putSerializable("TYPE", "ZONE");
                        intent.putExtras(mBundle);
                        startActivityForResult(intent, SELECT_AN_ZONE);
                    }
                } else {
                    Utility.BlueToast(SignupPersonal.this, getString(R.string.smthWentWrong));
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Operators Error" + error);
                Utility.BlueToast(SignupPersonal.this, getString(R.string.network_problem));
                pDialog.dismiss();
            }
        }, "ordinory");
    }


    private static String readEncodedJsonString(Context context)
            throws java.io.IOException {
        String base64 = context.getResources().getString(R.string.countries_code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

}
