package com.example.moda.firebasebasedchat.Utilities;


public class ServiceUrl {
 public static final String FACEBOOK_URL = "http:www.facebook.com/";

 public static final String TWITTER_URL = "https://twitter.com/7moola_/";

// private static final String BASE_URL = "http://192.241.153.106:7002/";

// private static final String BASE_URL = "https://api.dayrunnerapp.com/";
 private static final String BASE_URL = "http://api.ebba.cr/";

 private static final String APP_URL = BASE_URL + "app/";


// public static final String CONFIG = APP_URL + "config";

 public static final String LANG = APP_URL + "lang";

 public static final String OPERATORS = APP_URL + "operators";

 public static final String MAKEMODEL = APP_URL + "makeModel";

 public static final String VEHICLETYPE = APP_URL + "vehicleType";

 public static final String LOGOUT = APP_URL + "LogOut";

 public static final String SIGNUPOTP = APP_URL + "signupOtp";

 public static final String VERIFYOTP = APP_URL + "verifyOtp";

 public static final String FORGOTPASSWORD = APP_URL + "forgotpassword";

 public static final String UPDATEPASSWORD = APP_URL + "updatePassword";

 public static final String VALIDATE_REFERRAL_CODE = APP_URL + "validatereferralcode";

 public static final String CANCELREASONS = APP_URL + "cancelReasons";

 public static final String SUPPORT = APP_URL + "support";

 private static final String MASTER_URL = BASE_URL + "master/";

 public static final String BANK_URL= MASTER_URL+"bank";

 public static final String BANK_DETAILS= BANK_URL+"/me";

 public static final String CONFIG = MASTER_URL + "app/config";

 public static final String LOCATION = MASTER_URL + "location";

 public static final String LOCATION_LOGS = MASTER_URL + "locationLogs";

 public static final String VERIFY_EMAIL_PHONE = MASTER_URL + "EmailPhoneValidate";

 public static final String SIGN_IN = MASTER_URL + "signin";

 public static final String SIGN_UP = MASTER_URL + "signup";
 public static final String SEND_MSG= MASTER_URL+"firebase/send";
 public static final String VEHICLE_DEFAULT = MASTER_URL + "vehicleDefault";

 public static final String GET_CURRENT_STATUS = MASTER_URL + "assignedtrips";

 public static final String UPDATE_STATUS = MASTER_URL + "status";

 public static final String ACKNOWLEDGE_TO_BOOKING = MASTER_URL + "ackbooking";

 public static final String RESPOND_TO_BOOKING = MASTER_URL + "respondToRequest";

 public static final String UPDATE_BOOKING_STATUS = MASTER_URL + "bookingStatus";

 public static final String ZONE = MASTER_URL + "zone";

 public static final String GET_PROFILE = MASTER_URL + "profile/me";

 public static final String UPDATE_PROFILE = MASTER_URL + "profile/me";

 public static final String MASTERTRIPS = MASTER_URL + "trips";

 public static final String CANCELBOOKING = MASTER_URL + "cancelBooking";

 public static final String FETCH_ADD_STRIPE_ACCOUNT = MASTER_URL + "stripe/me";

 public static final String ADD_DELETE_BANK_DETAILS = MASTER_URL + "stripe/bank/me";

 public static final String SET_DEFAULT_BANK = MASTER_URL + "stripe/bankdefault/me";

 public static final String WALLET_DETAILS = BASE_URL + "master/paymentsettings";

 public static final String RECHARGE_WALLET = BASE_URL+"master/rechargeWallet";

 public static final String WALLET_TRANSACTIONS = BASE_URL+"wallet/transction/";

 public static final String ADDCARD=BASE_URL+"paymentGateway/master/card/me";      //+"addCard";

 public static final String GETCARD=BASE_URL+"paymentGateway/master/cards/me";      //+"getCards";              //"getSlaveAppointmentDetails";

 public static final String REMOVE_CARD=BASE_URL+"paymentGateway/master/card/me";

 public static final String DEFAULT_CARD=BASE_URL+"paymentGateway/master/card/default/me";


}
