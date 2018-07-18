package com.example.moda.firebasebasedchat.Utilities;

import android.net.Uri;


import java.io.File;

/**
 * Created by embed on 17/6/16.
 */
public class VariableConstant {

    public static final String PARENT_FOLDER = "7Moola";
    public static final String PREF_NAME = "get7moola";
    public static final int RC_READ_WRITE_CAMERA_STATE = 102;

    public static final String DEVICE_MODEL = android.os.Build.MODEL;
    public static final String DEVICE_MAKER = android.os.Build.MANUFACTURER;
    public static final int DEVICE_TYPE = 2; //1 for ios 2 for android
    public static final int USER_TYPE = 2; //1 for customer 2 for driver
    public static String LANGUAGE = "en";
    public static String langg="";
    //permission constants
    public static final int RC_READ_PHONE_STATE = 101;
    public static final int RC_LOCATION_STATE = 102;
    public static final String COGNITO_POOL_ID = "us-east-2:d2af78f3-f4a3-4139-a60f-b4b65257b51b";
    public static final String BUCKET_NAME = "moolas";
    public static final String PROFILE_PIC = "Drivers/ProfilePics";
    public static final String LICENCE = "Drivers/DriverLincence";
    public static final String VEHICLE = "Vehicles/VehicleImage";
    public static final String VEHICLE_DOCUMENTS = "Vehicles/VehicleDocuments";
    public static final String SIGNATURE_UPLOAD = "Drivers/signature";
    public static final String BANK_PROOF = "Drivers/BankProof";
    public static final String SIGNATURE_PIC_DIR = PARENT_FOLDER + "/sign";
    public static final String AMAZON_BASE_URL = "https://s3.amazonaws.com/";
    public static boolean IS_PROFILE_EDITED = false;
    public static boolean SIGNUP_PERSONAL = false;
    public static boolean EDIT_PHONE_NUMBER = false;
    public static boolean EDIT_PASSWORD_DIALOG = false;
    public static Uri newProfileImageUri;
    public static File newFile;
    public static boolean isPictureTaken;
    public static boolean isVehPictureTaken;
    public static boolean VECHICLESELECTED = false;
    public static String VECHICLEID = "";
    public static String VECHICLE_TYPE_ID = "";
    public static String PROFILE_IMAGE_URL = "";
    public  static int flag=0;

    public static boolean IS_POP_UP_OPEN = false;
    public static boolean IS_STRIPE_ADDED = false;

    public static boolean isWalletFragActive=false;
    public static boolean isWalletUpdateCalled=false;
}
