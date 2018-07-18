package com.delex.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.delex.driver.R;
import com.delex.logout.LogoutPopup;
import com.delex.pojo.ProfileData;
import com.delex.utility.CircleImageView;
import com.delex.utility.ImageEditUpload;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.utility.VariableConstant;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import eu.janmuller.android.simplecropimage.CropImage;

import static android.app.Activity.RESULT_OK;

/**************************************************************************************************/
public class MyProfileFrag extends Fragment implements View.OnClickListener, MyProfilePresenter.MyProfilePresenterImplement {

    private String TAG = MyProfileFrag.class.getSimpleName();
    private View rootView;
    private CircleImageView iv_prof_img;
    private TextView tv_plan_type;
    private TextView tv_prof_name;
    private TextView tv_prof_phone;
    private TextView tv_prof_vechtype;
    private String email;
    private TextView tv_prof_vech_number,tv_prof_vech_make,tv_prof_vech_model;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private ChangePasswordDialog changePasswordDialog;
    private static final int CAMERA_PIC = 11, GALLERY_PIC = 12, CROP_IMAGE = 13, REMOVE_IMAGE = 14;
    private final int PERMISSION_MULTIPLE_CODE = 12345;
    String base64dependentPic = "";
    private ImageView iv_profpic_prog;
    private String token = "";
    private String fileType = "image", fileName, takenNewImage, state;


    private MyProfilePresenter myProfilePresenter;

    /**********************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**********************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initializeViews();

        myProfilePresenter = new MyProfilePresenter(this,getActivity());
        myProfilePresenter.getProfileDetails(token);
        /*if (!VariableConstant.PROFILE_IMAGE_URL.matches("")) {
            updateMasterPic();
            VariableConstant.PROFILE_IMAGE_URL = "";
        }*/
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VariableConstant.IS_PROFILE_EDITED) {
            VariableConstant.IS_PROFILE_EDITED = false;
            myProfilePresenter.getProfileDetails(token);
        }
    }

    /*********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {
        sessionManager = new SessionManager(getContext());

        token = sessionManager.getSessionToken();

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);


        Typeface clanaproNarrNews = Typeface.createFromAsset(getContext().getAssets(), "fonts/ClanPro-NarrNews.otf");

        TextView tv_name, tv_phone, tv_pass, tv_vechtype, tv_vech_number,tv_logout;

        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_name.setTypeface(clanaproNarrNews);

        tv_logout = (TextView) rootView.findViewById(R.id.tvLogout);
        tv_logout.setTypeface(clanaproNarrNews);
        tv_logout.setOnClickListener(this);

        tv_phone = (TextView) rootView.findViewById(R.id.tv_phone);
        tv_phone.setTypeface(clanaproNarrNews);

        tv_pass = (TextView) rootView.findViewById(R.id.tv_pass);
        tv_pass.setTypeface(clanaproNarrNews);

        tv_vechtype = (TextView) rootView.findViewById(R.id.tv_vechtype);
        tv_vechtype.setTypeface(clanaproNarrNews);

        tv_vech_number = (TextView) rootView.findViewById(R.id.tv_vech_number);
        tv_vech_number.setTypeface(clanaproNarrNews);

        iv_prof_img = (CircleImageView) rootView.findViewById(R.id.iv_prof_img);
        iv_profpic_prog = (ImageView) rootView.findViewById(R.id.iv_profpic_prog);

        ImageView iv_name_edit = (ImageView) rootView.findViewById(R.id.iv_name_edit);
        iv_name_edit.setOnClickListener(this);

        ImageView iv_phone_edit = (ImageView) rootView.findViewById(R.id.iv_phone_edit);
        iv_phone_edit.setOnClickListener(this);

        ImageView iv_password_edit = (ImageView) rootView.findViewById(R.id.iv_password_edit);
        iv_password_edit.setOnClickListener(this);


        tv_prof_name = (TextView) rootView.findViewById(R.id.tv_prof_name);
        tv_prof_name.setTypeface(clanaproNarrNews);

        TextView tv_plan = (TextView) rootView.findViewById(R.id.tv_plan);
        tv_plan.setTypeface(clanaproNarrNews);

        tv_plan_type = (TextView) rootView.findViewById(R.id.tv_plan_type);
        tv_plan_type.setTypeface(clanaproNarrNews);

        tv_prof_phone = (TextView) rootView.findViewById(R.id.tv_prof_phone);
        tv_prof_phone.setTypeface(clanaproNarrNews);

        TextView tv_prof_pass = (TextView) rootView.findViewById(R.id.tv_prof_pass);
        tv_prof_pass.setTypeface(clanaproNarrNews);

        tv_prof_vechtype = (TextView) rootView.findViewById(R.id.tv_prof_vechtype);
        tv_prof_vechtype.setTypeface(clanaproNarrNews);

        tv_prof_vech_number = (TextView) rootView.findViewById(R.id.tv_prof_vech_number);
        tv_prof_vech_number.setTypeface(clanaproNarrNews);
        tv_prof_vech_model=(TextView)rootView.findViewById(R.id.tv_prof_vech_model);
        tv_prof_vech_model.setTypeface(clanaproNarrNews);

        tv_prof_vech_make=(TextView)rootView.findViewById(R.id.tv_prof_vech_make);
        tv_prof_vech_make.setTypeface(clanaproNarrNews);
        iv_prof_img.setOnClickListener(this);

    }

    /**********************************************************************************************/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_name_edit:
                Intent intentName = new Intent(getActivity(), EditProfileActivity.class);
                intentName.putExtra("data", "name");
                startActivity(intentName);
                getActivity().overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                break;

            case R.id.iv_phone_edit:
                Intent intentPhone = new Intent(getActivity(), EditProfileActivity.class);
                intentPhone.putExtra("data", "phone");
                //intentPhone.putExtra("email",)
                intentPhone.putExtra("data1",email);
                startActivity(intentPhone);
                getActivity().overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                break;

            case R.id.iv_password_edit:
                changePasswordDialog = new ChangePasswordDialog(getActivity(), "Password", new ChangePasswordDialog.RefreshProfile() {
                    @Override
                    public void onRefresh() {
                        changePasswordDialog.dismiss();
                        changePasswordDialog.cancel();
                        Intent intentPass = new Intent(getActivity(), EditProfileActivity.class);
                        intentPass.putExtra("data", "password");
                        startActivity(intentPass);
                        getActivity().overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                    }
                });

                changePasswordDialog.show();
                break;

            case R.id.iv_prof_img:
                VariableConstant.isVehPictureTaken = false;
                ImageEditUpload imageEditUpload = new ImageEditUpload(getActivity(), "profile_pic");
                break;

            case R.id.tvLogout:
                LogoutPopup logoutPopup = new LogoutPopup(getActivity());
                logoutPopup.setCancelable(false);
                logoutPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                logoutPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logoutPopup.show();
        }
    }
    /**********************************************************************************************/
    /**
     * <H1>MasterProfile</H1>
     * <p>for Change the profile picture</p>
     */
    private void updateMasterPic(String url) {
        pDialog.show();

        JSONObject reqOtpObject = new JSONObject();
        try {
            reqOtpObject.put("ent_profile", url);
            myProfilePresenter.setProfilePic(token, reqOtpObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onFailure(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure() {
        Toast.makeText(getActivity(), getString(R.string.serverError), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageUploadedResult(String url) {
        updateMasterPic(url);
    }

    /**
     * <h1>setProfileDetails</h1>
     * <p>this is the handler whic handle the response of getmaster profile.
     * in this method the profile details are setting and load the image through Picasso and till load the url the progress is visible.</p>
     *
     * @param profileData :pojo class which pass the the getmasterprofile response
     */
    @Override
    public void setProfileDetails(ProfileData profileData) {

        sessionManager.setMyName(profileData.getName());
        tv_prof_name.setText(profileData.getName());
        tv_prof_phone.setText(profileData.getPhone());
        tv_plan_type.setText(profileData.getPlanName());
        tv_prof_vechtype.setText(profileData.getTypeName());
        tv_prof_vech_number.setText(profileData.getPlatNo());
        tv_prof_vech_make.setText(profileData.getMake());
        tv_prof_vech_model.setText(profileData.getModel());
        email=profileData.getEmail();
        String url = profileData.getpPic();

        if (!profileData.getpPic().equals(null) || !profileData.getpPic().equals("")) {
            if (url.contains(" ")) {
                url = url.replace(" ", "%20");
            }
            Utility.printLog(TAG+"  url  "+url);
            iv_profpic_prog.setVisibility(View.VISIBLE);
            Picasso.with(getContext())
                    .load(url)
                    .resize(200, 200)
//                    .placeholder(R.drawable.signup_profile_default_image)//signup_profile_default_image)
//                    .transform(new CircleTransform())
                    .into(iv_prof_img, new Callback() {
                        @Override
                        public void onSuccess() {
                            Utility.printLog(TAG+"  onSuccess  ");
                            iv_profpic_prog.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Utility.printLog(TAG+"  onError  ");
                            iv_profpic_prog.setVisibility(View.GONE);
                        }
                    });
            sessionManager.setProfilePic(url);

//            ((MainActivity)getActivity()).o
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Utility.printLog(TAG+" onActivtyResult "+requestCode);
        if (resultCode != RESULT_OK) {
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
                            VariableConstant.newFile = new File(Environment.getExternalStorageDirectory() + "/" + VariableConstant.PARENT_FOLDER /*+ "/Media/Images/CropImages/"*/, takenNewImage);
                        } else {
                            VariableConstant.newFile = new File(getActivity().getFilesDir() + "/" + VariableConstant.PARENT_FOLDER /*+ "/Media/Images/CropImages/"*/, takenNewImage);
                        }

                        InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
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

                        VariableConstant.newProfileImageUri = Uri.fromFile(VariableConstant.newFile);

                        fileName = VariableConstant.newFile.getName();

                        try {
                            String[] type = fileName.split("\\.");

                            byte[] bytes = new byte[(int) VariableConstant.newFile.length()];
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(VariableConstant.newProfileImageUri);
                            if (inputStream != null) {
                                inputStream.read(bytes);
                            }
                           /* byte[] encoded = Base64.encode(bytes, Base64.NO_WRAP);
                            base64dependentPic = new String(encoded);
                            base64dependentPic = "data:image/png;base64," + base64dependentPic;*/
                            Bitmap bMap = BitmapFactory.decodeFile(path);
                            Bitmap circle_bMap = Utility.getCircleCroppedBitmap(bMap);
                            iv_prof_img.setImageBitmap(circle_bMap);
                           // amzonUpload(new File(path));
                            myProfilePresenter.uploadToAmazon(new File(path));

                        } catch (Exception e) {
                            Utility.printLog("RegistrationAct in CROP_IMAGE exception while copying file = " + e.toString());
                        }
                    }
                    break;

                default:

                    Toast.makeText(getActivity(), getResources().getString(R.string.oops)
                            + " " + getResources().getString(R.string.smthWentWrong), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**********************************************************************************************/

    private void startCropImage() {
        Intent intent = new Intent(getActivity(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, VariableConstant.newFile.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 4);
        getActivity().startActivityForResult(intent, MyProfileFrag.CROP_IMAGE);
    }

    /**********************************************************************************************/
}
