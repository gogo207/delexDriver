package com.delex.signup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.delex.forgotPassword.ForgotPasswordVerify;
import com.delex.adapter.ImageUploadRVA;
import com.delex.driver.R;
import com.delex.pojo.ValidatorPojo;
import com.delex.pojo.VehMakeData;
import com.delex.pojo.VehTypeData;
import com.delex.pojo.VehTypeSepecialities;
import com.delex.pojo.VehicleMakeModel;
import com.delex.pojo.VehicleMakePojo;
import com.delex.pojo.VehicleTypePojo;
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
import java.io.InputStream;
import java.util.ArrayList;

import eu.janmuller.android.simplecropimage.CropImage;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**************************************************************************************************/
public class SignupVehicle extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_AN_TYPE = 405;
    private static final int SELECT_AN_SPECIALITY = 124;
    private static final int SELECT_AN_Make = 407;
    private static final int SELECT_AN_MODEL = 408;
    private static final String TAG = SignupVehicle.class.getSimpleName();
    private static final int CAMERA_PIC = 11, GALLERY_PIC = 12, CROP_IMAGE = 13, REMOVE_IMAGE = 14;
    String base64dependentPic = "";
    SessionManager sessionManager;
    private RelativeLayout ll_add_insurance;
    private RelativeLayout ll_carriage_permit;
    private RelativeLayout ll_reg_certificate;
    private ImageView iv_signup_vp, iv_insurance, iv_reg_cert, iv_carriage_permit,iv_insurance_exp_date,iv_reg_exp_date,iv_carriage_exp_date;
    private EditText et_plate_no, et_color;
    private TextView tv_type, tv_sepecialities, tv_make, tv_model,exp_date_value,exp_date_value1,exp_date_value3;
    private String str_plate_no, str_color, str_type = "", str_make = "", str_model = "", str_speciality = "", make_id = "", model_id = "";
    private TextView tv_finish;
    private JSONArray speciality_id;
    private String Profile_pic, appversion, deviceMake, deviceModel,id_proof, Fname, Lname, Countrycode, PhoneNumber, Email, Password, AccountType, Zone, ReferalCode;
    private String LicenceUrs;
    private ArrayList<VehTypeSepecialities> vehTypeSepecialities;// = new ArrayList<>();
    private ArrayList<VehTypeData> vehicleTypeData;// = new ArrayList<>();
    private ArrayList<VehicleMakeModel> vehicleMakeModels;// = new ArrayList<>();
    private ImageEditUpload imageEditUpload;
    private String fileType = "image", fileName, takenNewImage, state;
    private String carriageImage = "", registrationImage = "", insuranceImage = "";
    private String ImageType, vehicle_pic = "vehicle_pic", licence_pic = "add_licence", carriage_permit = "carriage_permit", reg_certificate = "reg_certificate";
    //    private ArrayList<String> imagefile;
    private String vehicle_pic_url = "";
    private int imageCount;
    private ImageUploadRVA imageUploadRVA;
    private ProgressDialog pDialog;
    private int Insurance_image_no = 3;
    private ArrayList<VehMakeData> vehMakeData;

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_vehicle);
        overridePendingTransition(R.anim.bottem_slide_down, R.anim.stay_activity);
        sessionManager = new SessionManager(SignupVehicle.this);
        //checkAndRequestPermissions();
        if(sessionManager.getLang().equalsIgnoreCase("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }


        /* = new ArrayList<>(3);
        imagefile.add("imagefile");
        imagefile.add("");
        imagefile.add("");*/
        FetchValues();
        initActionBar();
        initializeViews();
    }
    /**********************************************************************************************/
    /**
     * <h1>FetchValues</h1>
     * <p>The values fetch from the SignupPersonal Activity for signup.</p>
     */
    private void FetchValues() {
        Bundle mBundle = getIntent().getExtras();
        Profile_pic = mBundle.getString("profile_pic");
        Fname = mBundle.getString("ent_first_name");
        id_proof=mBundle.getString("idCopy");
        Lname = mBundle.getString("ent_last_name");
        Countrycode = mBundle.getString("str_countrycode");
        PhoneNumber = mBundle.getString("ent_mobile");
        Email = mBundle.getString("ent_email");
        Password = mBundle.getString("ent_password");
        AccountType = mBundle.getString("accountType");
        Zone = mBundle.getString("zone");
        ReferalCode = mBundle.getString("referal_code");
        LicenceUrs = mBundle.getString("licence_urls");
        appversion = mBundle.getString("ent_appversion");
        deviceMake = mBundle.getString("ent_devMake");
        deviceModel = mBundle.getString("ent_devModel");
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

    /**********************************************************************************************/
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

    /**********************************************************************************************/
      /*initializeViews*/
    /**********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {
        Typeface ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
        Typeface ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");

        pDialog = new ProgressDialog(SignupVehicle.this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);

        TextView tv_vechicle, tv_header_certificate, tv_header_insurance, tv_header_registration, tv_type_head, tv_sepecialities_head, tv_make_head, tv_model_head, tv_camera_up;

        ll_add_insurance = (RelativeLayout) findViewById(R.id.ll_add_insurance);
        ll_add_insurance.setOnClickListener(this);

        ll_carriage_permit = (RelativeLayout) findViewById(R.id.ll_carriage_permit);
        ll_carriage_permit.setOnClickListener(this);

        ll_reg_certificate = (RelativeLayout) findViewById(R.id.ll_reg_certificate);
        ll_reg_certificate.setOnClickListener(this);

        tv_camera_up = (TextView) findViewById(R.id.tv_camera_up1);
        tv_camera_up.setTypeface(ClanaproNarrNews);

        tv_vechicle = (TextView) findViewById(R.id.tv_vechicle);
        tv_vechicle.setTypeface(ClanaproNarrNews);

        tv_header_certificate = (TextView) findViewById(R.id.tv_header_certificate);
        tv_header_certificate.setTypeface(ClanaproNarrNews);

        tv_header_insurance = (TextView) findViewById(R.id.tv_header_insurance);
        tv_header_insurance.setTypeface(ClanaproNarrNews);

        tv_header_registration = (TextView) findViewById(R.id.tv_header_registration);
        tv_header_registration.setTypeface(ClanaproNarrNews);


        et_plate_no = (EditText) findViewById(R.id.et_plate_no);
        et_plate_no.setTypeface(ClanaproNarrNews);

        et_color = (EditText) findViewById(R.id.et_color);
        et_color.setTypeface(ClanaproNarrNews);

        tv_type_head = (TextView) findViewById(R.id.tv_type_head);
        tv_type_head.setTypeface(ClanaproNarrNews);

        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_type.setTypeface(ClanaproNarrNews);
        tv_type.setOnClickListener(this);

        tv_sepecialities_head = (TextView) findViewById(R.id.tv_sepecialities_head);
        tv_sepecialities_head.setTypeface(ClanaproNarrNews);

        tv_sepecialities = (TextView) findViewById(R.id.tv_sepecialities);
        tv_sepecialities.setTypeface(ClanaproNarrNews);
        tv_sepecialities.setOnClickListener(this);

        tv_make_head = (TextView) findViewById(R.id.tv_make_head);
        tv_make_head.setTypeface(ClanaproNarrNews);

        tv_make = (TextView) findViewById(R.id.tv_make);
        tv_make.setTypeface(ClanaproNarrNews);
        tv_make.setOnClickListener(this);

        tv_model_head = (TextView) findViewById(R.id.tv_model_head);
        tv_model_head.setTypeface(ClanaproNarrNews);

        tv_model = (TextView) findViewById(R.id.tv_model);
        tv_model.setTypeface(ClanaproNarrNews);
        tv_model.setOnClickListener(this);


        exp_date_value1=(TextView)findViewById(R.id.exp_date_value1);
        exp_date_value1.setTypeface(ClanaproNarrNews);
        exp_date_value=(TextView)findViewById(R.id.exp_date_value);
        exp_date_value.setTypeface(ClanaproNarrNews);


        exp_date_value3=(TextView) findViewById(R.id.exp_date_value3);
        exp_date_value3.setTypeface(ClanaproNarrNews);


        tv_finish = (TextView) findViewById(R.id.tv_finish);
        tv_finish.setTypeface(ClanaproNarrMedium);
        tv_finish.setOnClickListener(this);

        iv_insurance = (ImageView) findViewById(R.id.iv_camera_uploader1);

        iv_signup_vp = (ImageView) findViewById(R.id.iv_signup_vp);
        iv_signup_vp.setOnClickListener(this);

        iv_carriage_permit = (ImageView) findViewById(R.id.iv_camera_uploader2);

        iv_reg_cert = (ImageView) findViewById(R.id.iv_camera_uploader3);
        iv_insurance_exp_date=(ImageView)findViewById(R.id.iv_insurance_exp_date);
        iv_insurance_exp_date.setOnClickListener(this);

        iv_reg_exp_date=(ImageView)findViewById(R.id.iv_reg_exp_date) ;
        iv_reg_exp_date.setOnClickListener(this);

        iv_carriage_exp_date=(ImageView)findViewById(R.id.iv_carriage_exp_date);
        iv_carriage_exp_date.setOnClickListener(this);

        /*RecyclerView rv_signup_insurance = (RecyclerView) findViewById(R.id.rv_signup_insurance);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        rv_signup_insurance.setLayoutManager(layoutManager);
        imageUploadRVA = new ImageUploadRVA(this, imagefile, new ImageUploadRVA.RemoveImage() {
            @Override
            public void ImageRemoved(int position) {
                imagefile.remove(position);
                imageCount--;
                imageUploadRVA.notifyDataSetChanged();

                if(imageCount<Insurance_image_no)
                {
                    ll_add_insurance.setVisibility(View.VISIBLE);
                }
            }
        });
        rv_signup_insurance.setAdapter(imageUploadRVA);
        imageUploadRVA.notifyDataSetChanged();*/
    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this, GenericListActivity.class);
        Bundle mBundle = new Bundle();

        switch (v.getId()) {
            case R.id.iv_signup_vp:
                ImageType = vehicle_pic;
                Utility.printLog(TAG+" ImageType "+ImageType);
                imageEditUpload = new ImageEditUpload(this, vehicle_pic);

                break;

            case R.id.ll_add_insurance:
                ImageType = licence_pic;
                Utility.printLog(TAG+" ImageType "+ImageType);
                imageEditUpload = new ImageEditUpload(this, licence_pic);

                break;

            case R.id.ll_carriage_permit:
                ImageType = carriage_permit;
                Utility.printLog(TAG+" ImageType "+ImageType);
                imageEditUpload = new ImageEditUpload(this, licence_pic);

                break;

            case R.id.ll_reg_certificate:
                ImageType = reg_certificate;
                Utility.printLog(TAG+" ImageType "+ImageType);
                imageEditUpload = new ImageEditUpload(this, licence_pic);

                break;

            case R.id.tv_finish:
                methodRequiresOnePermission();
                if (sessionManager.getDeviceId() != null)
                    SignupVechicleValidate();
                break;

            case R.id.tv_type:


                if (vehicleTypeData != null && vehicleTypeData.size() > 0) {
                    mBundle.putSerializable("DATA", vehicleTypeData);
                    mBundle.putSerializable("TYPE", "VEHICLE_TYPE");
                    mBundle.putString("TITLE", getResources().getString(R.string.err_type));
                    intent.putExtras(mBundle);
                    startActivityForResult(intent, SELECT_AN_TYPE);
                } else {
                    getTypeList();
                }

                break;

            case R.id.tv_sepecialities:
                if (vehTypeSepecialities != null && vehTypeSepecialities.size() != 0) {
                    mBundle.putString("TITLE", getResources().getString(R.string.err_specialities));
                    mBundle.putString("TYPE", "VEHICLE_SPECIALITIES");
                    mBundle.putSerializable("DATA", vehTypeSepecialities);
                    intent.putExtras(mBundle);
                    startActivityForResult(intent, SELECT_AN_SPECIALITY);
                }
                break;

            case R.id.tv_make:


                if (vehMakeData != null && vehMakeData.size() > 0) {
                    mBundle.putSerializable("DATA", vehMakeData);
                    mBundle.putSerializable("TYPE", "VEHICLE_MAKE");
                    mBundle.putString("TITLE", getResources().getString(R.string.err_make));
                    intent.putExtras(mBundle);
                    startActivityForResult(intent, SELECT_AN_Make);
                } else {
                    getMakeList();
                }
                break;

            case R.id.tv_model:
                if (vehicleMakeModels != null && vehicleMakeModels.size() != 0) {
                    mBundle.putString("TITLE", getResources().getString(R.string.err_model));
                    mBundle.putString("TYPE", "VEHICLE_MODEL");
                    mBundle.putSerializable("DATA", vehicleMakeModels);
                    intent.putExtras(mBundle);
                    startActivityForResult(intent, SELECT_AN_MODEL);
                }

                break;

            case R.id.iv_reg_exp_date:

                Utility.openDate_Picker(SignupVehicle.this,exp_date_value);
                break;


            case R.id.iv_insurance_exp_date:
                Utility.openDate_Picker(SignupVehicle.this,exp_date_value1);
                break;

            case R.id.iv_carriage_exp_date:
                Utility.openDate_Picker(SignupVehicle.this,exp_date_value3);
                break;



        }

    }

    /**********************************************************************************************/
    /**
     * <H1>SignupVechicleValidate</H1>
     * <p>checking wether the details filled or not</p>
     */
    void SignupVechicleValidate() {

        str_plate_no = et_plate_no.getText().toString();
        //str_type = tv_type.getText().toString();
        str_speciality = tv_sepecialities.getText().toString();
        str_make = tv_make.getText().toString();
        str_model = tv_model.getText().toString();

        Utility.printLog(TAG+" validate carriageImage : "+carriageImage);
        Utility.printLog(TAG+" validate insuranceImage : "+insuranceImage);
        Utility.printLog(TAG+" validate registrationImage : "+registrationImage);

        if (str_plate_no.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_plate),
                    SignupVehicle.this);
        }
        else if (str_type.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_type),
                    SignupVehicle.this);

        }
        else if (str_speciality.matches(""))
        {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_specialities),
                    SignupVehicle.this);
        }
        else if (str_make.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_make),
                    SignupVehicle.this);

        }
        else if (str_model.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_model),
                    SignupVehicle.this);
        }
        else if (vehicle_pic_url.matches("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.err_veh_img),
                    SignupVehicle.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                            iv_signup_vp.callOnClick();
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });
        }
        else if (registrationImage.equals("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.reg_certficate),
                    SignupVehicle.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                            ll_reg_certificate.callOnClick();
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });
        }
        else if (insuranceImage.equals("")) {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.Insurance_miss),
                    SignupVehicle.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                            ll_add_insurance.callOnClick();
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });
        }
        else if (carriageImage.equals(""))
        {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.certificate_miss),
                    SignupVehicle.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                            ll_carriage_permit.callOnClick();
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });

        }



        if (exp_date_value.getText().toString().equals("dd/mm/yyyy"))
        {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.reg_expdate_msg),
                    SignupVehicle.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                           /* ll_carriage_permit.callOnClick();*/
                            Utility.openDate_Picker(SignupVehicle.this,exp_date_value);
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });

        }
        else if (exp_date_value1.getText().toString().equals("dd/mm/yyyy"))
        {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.ins_expdate_msg),
                    SignupVehicle.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                           /* ll_carriage_permit.callOnClick();*/
                            Utility.openDate_Picker(SignupVehicle.this,exp_date_value1);
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });

        }

        else if (exp_date_value3.getText().toString().equals("dd/mm/yyyy"))
        {
            Utility.mShowMessage(getResources().getString(R.string.message),
                    getResources().getString(R.string.carr_expdate_msg),
                    SignupVehicle.this, new Utility.AlertDialogCallBack() {
                        @Override
                        public void onOkPressed() {
                           /* ll_carriage_permit.callOnClick();*/
                            Utility.openDate_Picker(SignupVehicle.this,exp_date_value3);
                        }

                        @Override
                        public void onCancelPressed() {

                        }
                    });

        }
        else if(carriageImage.equals("UPLOADING")||insuranceImage.equals("UPLOADING")||registrationImage.equals("UPLOADING")||vehicle_pic_url.equals("UPLOADING")){
            Utility.BlueToast(SignupVehicle.this,getResources().getString(R.string.uploading_doc));
        }
        else {
            signupApiCalling();
        }

    }

    @AfterPermissionGranted(VariableConstant.RC_READ_PHONE_STATE)
    private void methodRequiresOnePermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission
            sessionManager.setDeviceId(Utility.getDeviceId(this));
        } else {
            // Do not have permissions, requesting permission
            EasyPermissions.requestPermissions(this, getString(R.string.read_phone_state_permission_message),
                    VariableConstant.RC_READ_PHONE_STATE, perms);
        }
    }

    /**********************************************************************************************/
    private void signupApiCalling() {
        pDialog.show();
        final JSONObject reqObject = new JSONObject();

        double[] latlong = Utility.getLocation(this);

        try {
            reqObject.put("ent_first_name", Fname);
            reqObject.put("ent_last_name", Lname);
            reqObject.put("ent_email", Email);
            reqObject.put("ent_password", Password);
            reqObject.put("ent_country_code", Countrycode);
            reqObject.put("ent_mobile", PhoneNumber);
            reqObject.put("ent_zipcode", "1234");
            reqObject.put("ent_latitude", "" + latlong[0]);
            reqObject.put("ent_longitude", "" + latlong[1]);
            reqObject.put("ent_profile_pic", Profile_pic);
            reqObject.put("deviceId", sessionManager.getDeviceId());
            reqObject.put("ent_plat_no", str_plate_no);
            reqObject.put("ent_type", str_type);
            reqObject.put("ent_specialities", speciality_id);
            reqObject.put("ent_make", make_id);
            reqObject.put("ent_model", model_id);
            reqObject.put("referral", ReferalCode);
            reqObject.put("zones", Zone);
            reqObject.put("idCopy",id_proof);
            reqObject.put("ent_insurance_photo", insuranceImage);
            reqObject.put("ent_reg_cert", registrationImage);
            reqObject.put("ent_carriage_permit", carriageImage);
            //reqObject.put("ent_operator",);
            reqObject.put("driverLicense", LicenceUrs);
            reqObject.put("ent_vehicleImage", vehicle_pic_url);
            reqObject.put("accountType", AccountType);
//            reqObject.put("ent_dev_type",2);
            reqObject.put("deviceType", 2);
            reqObject.put("ent_appversion", appversion);
            reqObject.put("ent_devMake", deviceMake);
            reqObject.put("ent_devModel", deviceModel);
            reqObject.put("ent_regExpiryDate",exp_date_value.getText().toString());
            reqObject.put("ent_insuranceExpiryDate",exp_date_value1.getText().toString());
            reqObject.put("ent_carraige_permitExpiryDate",exp_date_value3.getText().toString());
            Utility.printLog("siginup request: " + reqObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("mobile",   PhoneNumber);
            reqOtpObject.put("email",Email);
            reqOtpObject.put("userType", VariableConstant.USER_TYPE);
            reqOtpObject.put("countryCode",Countrycode);

            Utility.printLog("siginup otp request: " + reqOtpObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.SIGNUPOTP, OkHttp3Connection.Request_type.POST, reqOtpObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                pDialog.dismiss();
                System.out.println("Signup result " + result);
                if (result != null) {
                    ValidatorPojo ValidatorPojo = new Gson().fromJson(result, ValidatorPojo.class);

                    switch (ValidatorPojo.getErrFlag()) {
                        case 0:
//                            Utility.BlueToast(SignupVehicle.this, ValidatorPojo.getErrMsg());
                            Intent mIntent = new Intent(SignupVehicle.this, ForgotPasswordVerify.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("from", "SignUpVahicle");
                            mBundle.putString("signupdata", reqObject.toString());
                            mBundle.putString("mobile",PhoneNumber);
                            mBundle.putString("countryCode",Countrycode);
                            mBundle.putString("otp", ValidatorPojo.getOtp());
                            mIntent.putExtras(mBundle);
                            startActivity(mIntent);
                            break;
                        case 1:
                            Utility.BlueToast(SignupVehicle.this, ValidatorPojo.getErrMsg());
                            break;

                    }

                } else {

                }

            }

            @Override
            public void onError(String error) {
                System.out.println("signin error " + error);

            }
        }, "ordinory");
    }

    /**********************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SELECT_AN_SPECIALITY && data != null) {


            vehTypeSepecialities = (ArrayList<VehTypeSepecialities>) data.getSerializableExtra("DATA");

            String specialities = "";
            speciality_id = new JSONArray();
            for (VehTypeSepecialities v : vehTypeSepecialities) {
                if (v.isSelected()) {
                    if (!specialities.equals("")) {
                        specialities = specialities + ", " + v.getName();
                    } else {
                        specialities = v.getName();
                    }

                    speciality_id.put(v.getId());
                }

            }

            tv_sepecialities.setText(specialities);

        }
        if (requestCode == SELECT_AN_TYPE && data != null) {
            tv_type.setText("");
            str_type="";
            if (vehicleTypeData != null) {
                vehicleTypeData.clear();

            }
            vehicleTypeData = (ArrayList<VehTypeData>) data.getSerializableExtra("DATA");
            for (VehTypeData v : vehicleTypeData) {
                if (v.isSelected()) {
                    tv_type.setText(v.getName());
                    str_type = v.getId();
                    if (vehTypeSepecialities != null && vehTypeSepecialities.size() > 0) {
                        vehTypeSepecialities.clear();
                        vehTypeSepecialities.addAll(v.getSepecialities());
                    } else {
                        vehTypeSepecialities = v.getSepecialities();
                    }
                }

            }
            tv_sepecialities.setText("");
        }


        if (requestCode == SELECT_AN_Make && data != null) {
            if (vehMakeData != null) {
                vehMakeData.clear();
            }
            tv_make.setText("");
            vehMakeData = (ArrayList<VehMakeData>) data.getSerializableExtra("DATA");
            for (VehMakeData v : vehMakeData) {
                if (v.isSelected()) {
                    tv_make.setText(v.getName());
                    make_id = v.getId();
                    if (vehicleMakeModels != null && vehicleMakeModels.size() > 0) {
                        vehicleMakeModels.clear();
                        vehicleMakeModels.addAll(v.getModels());
                    } else {
                        vehicleMakeModels = v.getModels();
                    }


                }
            }


            tv_model.setText("");

        } else if (requestCode == SELECT_AN_MODEL && data != null) {
            tv_model.setText("");
            vehicleMakeModels = (ArrayList<VehicleMakeModel>) data.getSerializableExtra("DATA");

            for (VehicleMakeModel v : vehicleMakeModels) {
                if (v.isSelected()) {
                    tv_model.setText(v.getName());
                    model_id = v.getId();
                }
            }

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
                        takenNewImage = VariableConstant.PARENT_FOLDER + String.valueOf(System.nanoTime()) + ".png";

                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            VariableConstant.newFile = new File(Environment.getExternalStorageDirectory() /*+ "/" + VariableConstant.PARENT_FOLDER*/ /*+ "/Media/Images/CropImages/"*/, takenNewImage);
                        } else {
                            VariableConstant.newFile = new File(getFilesDir()/* + "/" + VariableConstant.PARENT_FOLDER*/ /*+ "/Media/Images/CropImages/"*/, takenNewImage);
                        }

                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(VariableConstant.newFile);

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
                        if (ImageType.equals(vehicle_pic)) {
                            VariableConstant.isVehPictureTaken = true;
                        }
                        Utility.printLog("RegistrationAct CROP_IMAGE FilePAth : " + VariableConstant.newFile.getPath());
                        VariableConstant.newProfileImageUri = Uri.fromFile(VariableConstant.newFile);
                        Utility.printLog("RegistrationAct CROP_IMAGE file URi: " + VariableConstant.newProfileImageUri);

                        fileName = VariableConstant.newFile.getName();
                        Utility.printLog("RegistrationAct CROP_IMAGE fileName: " + fileName);

                        try {
                            String[] type = fileName.split("\\.");
                            Log.d("file ", "File Type: " + type[1]);

                            byte[] bytes = new byte[(int) VariableConstant.newFile.length()];
                            InputStream inputStream = getContentResolver().openInputStream(VariableConstant.newProfileImageUri);
                            if (inputStream != null) {
                                inputStream.read(bytes);
                            }
                            byte[] encoded = Base64.encode(bytes, Base64.NO_WRAP);
                            base64dependentPic = new String(encoded);
                            base64dependentPic = "data:image/png;base64," + base64dependentPic;


                            if (ImageType.equals(vehicle_pic)) {
                                Bitmap bMap = BitmapFactory.decodeFile(path);
                                Bitmap circle_bMap = Utility.getCircleCroppedBitmap(bMap);
                                iv_signup_vp.setImageBitmap(circle_bMap);
                                VariableConstant.isVehPictureTaken = true;
                                amzonUpload(new File(path));
                            } else if ( ImageType.equals(reg_certificate) ) {
                                iv_reg_cert.setVisibility(View.VISIBLE);
                                Bitmap bMap = BitmapFactory.decodeFile(path);
                                bMap = Bitmap.createScaledBitmap(bMap, iv_reg_cert.getWidth(), iv_reg_cert.getHeight(), true);
                                iv_reg_cert.setImageBitmap(bMap);
                                amzonUpload(new File(path));
                            }else if (ImageType.equals(licence_pic)) {
                                iv_insurance.setVisibility(View.VISIBLE);
                                Bitmap bMap = BitmapFactory.decodeFile(path);
                                bMap = Bitmap.createScaledBitmap(bMap, iv_insurance.getWidth(), iv_insurance.getHeight(), true);
                                iv_insurance.setImageBitmap(bMap);

                                amzonUpload(new File(path));
                            }else if (ImageType.equals(carriage_permit)) {
                                iv_carriage_permit.setVisibility(View.VISIBLE);
                                Bitmap bMap = BitmapFactory.decodeFile(path);
                                bMap = Bitmap.createScaledBitmap(bMap, iv_carriage_permit.getWidth(), iv_carriage_permit.getHeight(), true);
                                iv_carriage_permit.setImageBitmap(bMap);
                                amzonUpload(new File(path));
                            }
                        } catch (Exception e) {
                            Utility.printLog("RegistrationAct in CROP_IMAGE exception while copying file = " + e.toString());
                        }
                    }
                    break;

                default:

                    //Toast.makeText(this, getResources().getString(R.string.oops)+" "+getResources().getString(R.string.smthWentWrong), Toast.LENGTH_LONG).show();
                    break;
            }
        }

    }


    private void amzonUpload(File file) {
        String BUCKETSUBFOLDER = "";

        /*if (ImageType.equals(carriage_permit)) {
            pDialog.show();
        }*/

        switch (ImageType){
            case "vehicle_pic" :
                vehicle_pic_url="UPLOADING";
                BUCKETSUBFOLDER = VariableConstant.VEHICLE;
                break;

            case "add_licence" :
                insuranceImage = "UPLOADING";
                BUCKETSUBFOLDER = VariableConstant.VEHICLE_DOCUMENTS;
                break;

            case "reg_certificate" :
                registrationImage = "UPLOADING";
                BUCKETSUBFOLDER = VariableConstant.VEHICLE_DOCUMENTS;
                break;

            case "carriage_permit" :
                carriageImage = "UPLOADING";
                BUCKETSUBFOLDER = VariableConstant.VEHICLE_DOCUMENTS;
                break;
        }

        Upload_file_AmazonS3 amazonS3 = Upload_file_AmazonS3.getInstance(this, VariableConstant.COGNITO_POOL_ID);


        amazonS3.Upload_data(ImageType,VariableConstant.BUCKET_NAME, BUCKETSUBFOLDER + "/" + file.getName(), file, new Upload_file_AmazonS3.Upload_CallBack() {
            @Override
            public void sucess(String sucess) {

            }

            @Override
            public void sucess(String url, String type) {
                pDialog.dismiss();

                Log.d(TAG, "URL : " + url);
                switch (type){
                    case "vehicle_pic" :
                        vehicle_pic_url = url;
                        Utility.printLog(TAG+" type "+type);
                        break;

                    case "add_licence" :
                        insuranceImage = url;
                        Utility.printLog(TAG+" type "+type);
                        break;

                    case "reg_certificate" :
                        registrationImage = url;
                        Utility.printLog(TAG+" type "+type);
                        break;

                    case "carriage_permit" :
                        carriageImage = url;
                        Utility.printLog(TAG+" type "+type);
                        break;
                }

            }

            @Override
            public void error(String type) {
                pDialog.dismiss();
                switch (type){
                    case "vehicle_pic" :
                        iv_signup_vp.setImageResource(R.drawable.signup_vechicle_default_image);
                        vehicle_pic_url="";
                        break;

                    case "add_licence" :
                        iv_insurance.setVisibility(View.GONE);
                        insuranceImage="";
                        break;

                    case "reg_certificate" :
                        iv_reg_cert.setVisibility(View.GONE);
                        registrationImage="";
                        break;

                    case "carriage_permit" :
                        iv_carriage_permit.setVisibility(View.GONE);
                        carriageImage="";
                        break;
                }
            }
        });
    }


    /* *********************************************************************************************/
                                                                       /*startCropImage*/

    /**********************************************************************************************/

    private void startCropImage() {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, VariableConstant.newFile.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 4);
        startActivityForResult(intent, SignupVehicle.CROP_IMAGE);
    }
    /* *********************************************************************************************/
                                                                               /*remove*/

    /**********************************************************************************************/
    public void remove() {
        if (ImageType.equals(vehicle_pic)) {
            iv_signup_vp.setImageResource(R.drawable.signup_profile_default_image);
            VariableConstant.isVehPictureTaken = false;
        }

    }
    /***************************************************************************************/
/**********************************************************************************************/
    /**
     * <h1>getTypeList</h1>
     * <p>this is the service call for the result of Vehicle Type list provided</p>
     */
    private void getTypeList() {
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.VEHICLETYPE, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {

                    Utility.printLog("SelectAnOperator VehicleType response " + result);
                    VehicleTypePojo vehicleTypePojo = new Gson().fromJson(result, VehicleTypePojo.class);
                    if (vehicleTypePojo.getErrFlag() == 0) {
                        Intent intent = new Intent(SignupVehicle.this, GenericListActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("DATA", vehicleTypePojo.getData());
                        mBundle.putSerializable("TYPE", "VEHICLE_TYPE");
                        mBundle.putString("TITLE", getResources().getString(R.string.err_type));
                        intent.putExtras(mBundle);
                        startActivityForResult(intent, SELECT_AN_TYPE);
                    }
//                    manageVehTypeResponse(vehicleTypePojo);

                } else {
                    Utility.BlueToast(SignupVehicle.this, getString(R.string.smthWentWrong));
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Operators Error" + error);
                Utility.BlueToast(SignupVehicle.this, getString(R.string.network_problem));
                pDialog.dismiss();
            }
        }, "ordinory");
    }


    private void getMakeList() {
        pDialog.show();
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.MAKEMODEL, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Makemodel Result" + result);
                if (result != null) {
                    VehicleMakePojo vehicleMakePojo = new Gson().fromJson(result, VehicleMakePojo.class);

                    if (vehicleMakePojo.getErrFlag() == 0) {
                        Intent intent = new Intent(SignupVehicle.this, GenericListActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("DATA", vehicleMakePojo.getData());
                        mBundle.putSerializable("TYPE", "VEHICLE_MAKE");
                        mBundle.putString("TITLE", getResources().getString(R.string.err_make));
                        intent.putExtras(mBundle);
                        startActivityForResult(intent, SELECT_AN_Make);
                    }

                } else {
                    Utility.BlueToast(SignupVehicle.this, getString(R.string.smthWentWrong));
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Operators Error" + error);
                Utility.BlueToast(SignupVehicle.this, getString(R.string.network_problem));
                pDialog.dismiss();
            }
        }, "ordinory");
    }
}
