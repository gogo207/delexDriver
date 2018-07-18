package com.delex.utility;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.delex.driver.R;
import com.delex.signup.SignupPersonal;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by embed on 20/10/16.
 */
public class ImageEditUpload implements ActionSheet.ActionSheetListener {

    private static final int CAMERA_PIC = 11, GALLERY_PIC = 12, CROP_IMAGE = 13, REMOVE_IMAGE = 14;
    private final int REQUEST_CODE_PERMISSION_MULTIPLE = 123;
    private Activity context;
    private ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList;
    private ActionSheet actionSheet;
    private File newFile, profilePicsDir;
    private String state;
    private File[] profilePicsDirectory;
    private String takenNewImage;
    private Uri newProfileImageUri;
    private SessionManager sessionManager;
    private Gson gson;
    private ProgressDialog progressDialog;
    private String imageType;

/**************************************************************************************************/
    /**************************************************************************************************/
    public ImageEditUpload(Activity context, String imageType) {

        this.context = context;
        this.imageType = imageType;
        sessionManager = SessionManager.getSessionManager(context);
        gson = new Gson();
       /* if(context!=null) {
            progressDialog = new ProgressDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage("Loading.....");
        }*/
        DocumentUpload();
    }
/**************************************************************************************************/
/**************************************************************************************************/
    /**<h1>DocumentUpload</h1>
     *<p>permission for the document select edit</p>
     */
    public void DocumentUpload() {

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow
            myPermissionConstantsArrayList = new ArrayList<>();
            //appPermissionsRunTime = new AppPermissionsRunTime();

            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_CAMERA);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_READ_EXTERNAL_STORAGE);

            if (AppPermissionsRunTime.checkPermission(context, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {
                actionSheetToChoosePic();
            }
        } else {
            // Pre-Marshmallow
            actionSheetToChoosePic();
        }

    }
/**************************************************************************************************/
                                                                          /*actionSheetToChoosePic*/
/**************************************************************************************************/
    /**
     * <h1>actionSheetToChoosePic</h1>
     * <p>this is the method for action sheet , will display the options for choose the way to upload profile pic</p>
     */
    private void actionSheetToChoosePic() {
        switch (imageType) {
            case "profile_pic":
            case "vehicle_pic":
            case "add_licence":
            case "id_proof_pic":
                /*actionSheet = ActionSheet.createBuilder(context, context.getFragmentManager())
                        .setCancelButtonTitle(context.getResources().getString(R.string.cancel))
                        .setOtherButtonTitles(context.getResources().getString(R.string.gallery),
                                context.getResources().getString(R.string.camera))
                        .setCancelableOnTouchOutside(true).setListener(this).show();*/
                mShowImageOptions();
                break;
        }



    }


    private void mShowImageOptions() {
        final Dialog mDialog = new Dialog(context);
        // mDialog.setTitle("Select photo from :");
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
        mDialog.setContentView(R.layout.profile_pic_options);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));


        Button btnCamera = (Button) mDialog.findViewById(R.id.camera);
        Button btnCancel = (Button) mDialog.findViewById(R.id.cancel);
        Button btnGallery = (Button) mDialog.findViewById(R.id.gallery);
        Button btnRemove = (Button) mDialog.findViewById(R.id.removephoto);
        View line = mDialog.findViewById(R.id.line3);

        /*if(!isPicturetaken)
        {
            btnRemove.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        else
        {
            btnRemove.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }*/
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicFromCamera();
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

      /*  btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ivProfilePic.setImageResource(R.drawable.pro_photo);
                isPicturetaken=false;
                mDialog.dismiss();
            }
        });*/

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.show();

    }

/**************************************************************************************************/
                                                                                       /*onDismiss*/
    /**************************************************************************************************/
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }
    /**************************************************************************************************/
    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        clearOrCreateDir();
        switch (index) {

            case 0:
                openGallery();
                break;

            case 1:
                takePicFromCamera();
                break;

            case 2:
                if (imageType.equals("profile_pic")) {
                    if (VariableConstant.isPictureTaken) {
                        VariableConstant.isPictureTaken = false;
                    }
                    if (VariableConstant.isVehPictureTaken) {
                        VariableConstant.isVehPictureTaken = false;
                    }
                }
                SignupPersonal ref = (SignupPersonal) context;
                ref.remove();
                break;
            default:
                break;
        }
    }

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        context.startActivityForResult(photoPickerIntent, ImageEditUpload.GALLERY_PIC);
        /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image*//*");
        context.startActivityForResult(photoPickerIntent, ImageEditUpload.GALLERY_PIC);
        context.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);*/
    }

/**************************************************************************************************/
                                                                                /*clearOrCreateDir*/
/**************************************************************************************************/
    /**
     * <h1>clearOrCreateDir</h1>
     * <p>this is the method for the directory creation in internal or external storage </p>
     */
    private void clearOrCreateDir() {
        try {
            state = Environment.getExternalStorageState();
            File cropImagesDir;
            File[] cropImagesDirectory;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                cropImagesDir = new File(Environment.getExternalStorageDirectory() + "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages");
                profilePicsDir = new File(Environment.getExternalStorageDirectory() + "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/Profile_Pictures");
            } else {
                cropImagesDir = new File(context.getFilesDir() + "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages");
                profilePicsDir = new File(context.getFilesDir() + "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/Profile_Pictures");
            }

            if (!cropImagesDir.isDirectory()) {
                cropImagesDir.mkdirs();
            } else {
                cropImagesDirectory = cropImagesDir.listFiles();

                if (cropImagesDirectory.length > 0) {
                    for (File aCropImagesDirectory : cropImagesDirectory) {
                        aCropImagesDirectory.delete();
                    }
                    Utility.printLog("RegistrationAct CropImages cleared successfully:");
                } else {
                    Utility.printLog("RegistrationAct CropImages Dir empty  or null: " + cropImagesDirectory.length);
                }
            }

            if (!profilePicsDir.isDirectory()) {
                profilePicsDir.mkdirs();
                Utility.printLog("RegistrationAct profilePicsDir is created:" + profilePicsDir);
            } else {
                profilePicsDirectory = profilePicsDir.listFiles();

                if (profilePicsDirectory.length > 0) {
                    for (File aProfilePicsDirectory : profilePicsDirectory) {

                        aProfilePicsDirectory.delete();
                    }
                    Utility.printLog("RegistrationAct profilePicsDir cleared successfully:");
                } else {
                    Utility.printLog("RegistrationAct profilePicsDir empty  or null: " + profilePicsDirectory.length);
                }
            }
        } catch (Exception e) {
            Utility.printLog("RegistrationAct Error while creating newfile:" + e);
        }
    }
/**************************************************************************************************/
                                                                               /*takePicFromCamera*/
/**************************************************************************************************/
    /**
     * <h1>takePicFromCamera</h1>
     * <p>this is the method for take image from the camera</p>
     */
    private void takePicFromCamera() {
        Utility.printLog("RegistrationAct Inside takePicFromCamera():");
        try {
            Log.d("camerapic", "takePicFromCamera: ");
            takenNewImage = "";
            state = Environment.getExternalStorageState();
            takenNewImage = VariableConstant.PARENT_FOLDER + String.valueOf(System.nanoTime()) + ".png";


            if (Environment.MEDIA_MOUNTED.equals(state)) {

                newFile = new File(Environment.getExternalStorageDirectory() /*+ "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages/"*/, takenNewImage);
                VariableConstant.newFile = newFile;
            } else {
                newFile = new File(context.getFilesDir() /*+ "/" + VariableConstant.PARENT_FOLDER + "/Media/Images/CropImages/"*/, takenNewImage);
                VariableConstant.newFile = newFile;
            }


            if (Build.VERSION.SDK_INT >= 24) {
                newProfileImageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", newFile);
            } else {
                newProfileImageUri = Uri.fromFile(newFile);
            }


            VariableConstant.newProfileImageUri = newProfileImageUri;

            Utility.printLog("RegistrationAct FilePAth in takePicFromCamera()  new: " + newFile.getPath() + " new profileUri = " + newProfileImageUri);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, newProfileImageUri);
            intent.putExtra("return-data", true);
            context.startActivityForResult(intent, CAMERA_PIC);
        } catch (ActivityNotFoundException e) {
            Utility.printLog("RegistrationAct cannot take picture: " + e);
        }
    }
/**************************************************************************************************/
/**************************************************************************************************/

}
