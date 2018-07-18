package com.delex.wallet.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.delex.app.MainActivity;
import com.delex.driver.R;
import com.delex.utility.AppPermissionsRunTime;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.wallet.interface_callbacks.ResponseListener;
import com.delex.wallet.model.CardPaymentModel;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


/**
 * <h>AddCardActivity</h>
 * <p>
 *     Activity to add new card to make payment
 * </p>
 * @since  11/10/16.
 *
 */
public class AddCardActivity extends AppCompatActivity implements
		View.OnClickListener, ResponseListener.CardResponseListener
{
    private final int PERMISSION_CAMERA = 1005;
    public static final int REQUEST_CODE_PERMISSION_MULTIPLE = 127;
	private static final String TAG = "AddCardActivity";
	private RelativeLayout rlPoweredBy_addCard;
	private TextView tvScanCard_addCard, tvDone_addCard;
	private RelativeLayout rlScanCard_addCard, rlDone_addCard;
	private String coming_From;
	private ProgressBar progressbar;
	private SessionManager sessionMgr;
	private boolean scanflag = false;
	private CreditCard scanResult;
	private AppPermissionsRunTime permissionsRunTime;
	public String PUBLISHABLE_KEY;
	private CardPaymentModel cardPaymentModel;
	private CardInputWidget card_input_widget_card;
	Typeface ClanaproNarrMedium;
	Typeface ClanaproNarrNews;
	Typeface ClanaproMedium;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		setContentView(R.layout.activity_add_card);

		ClanaproNarrMedium = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrMedium.otf");
		ClanaproNarrNews = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
		ClanaproMedium = Typeface.createFromAsset(getAssets(), "fonts/CLANPRO-MEDIUM.OTF");

		initVariablesAndHelperClasses();
		initToolbar();
		initializeViews();
	}

	/**
	 * <h2>initVariablesAndHelperClasses</h2>
	 * <p>
	 *     method to initialize variables and other helper
	 *     classes
	 * </p>
	 */
	private void initVariablesAndHelperClasses()
	{
		Intent intent=getIntent();
		if(intent!=null)
		{
			coming_From = getIntent().getStringExtra("coming_From");
			Utility.printLog("coming_From signup payment :: " + coming_From);
		}

		sessionMgr = new SessionManager(this);
		cardPaymentModel = new CardPaymentModel();
		PUBLISHABLE_KEY = sessionMgr.getStripeKey();
		progressbar = (ProgressBar) findViewById(R.id.progressBar_addCard);
	}

	/**
	 * <h2>initToolbar</h2>
	 * <p>
	 *     method to initialize the tool bar of this activity
	 * </p>
	 */
	private void initToolbar()
	{
		Toolbar toolBar_addCard = (Toolbar) findViewById(R.id.toolBar_addCard);
		setSupportActionBar(toolBar_addCard);

		TextView tvToolBarTitle_addCard = (TextView) findViewById(R.id.tvToolBarTitle_addCard);
		tvToolBarTitle_addCard.setTypeface(ClanaproNarrMedium);

		TextView tvSkipToolBar_addCard = (TextView) findViewById(R.id.tvSkipToolBar_addCard);
		tvSkipToolBar_addCard.setTypeface(ClanaproNarrNews);
		tvSkipToolBar_addCard.setOnClickListener(this);

		if(coming_From.equals("payment_Fragment") || coming_From.equals("LiveBooking"))
		{
			toolBar_addCard.setNavigationIcon(R.drawable.back_selector);
			getSupportActionBar().setTitle("");

			toolBar_addCard.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
			tvSkipToolBar_addCard.setVisibility(View.GONE);
		}
	}
	/**
	 * <h2>initViews</h2>
	 * <p>
	 *     method to initializes the views of this activity
	 * </p>
	 */
	private void initializeViews()
	{
		rlPoweredBy_addCard = (RelativeLayout) findViewById(R.id.rlPoweredBy_addCard);
		card_input_widget_card= (CardInputWidget) findViewById(R.id.card_input_widget_card);

		tvDone_addCard = (TextView) findViewById(R.id.tvDone_addCard);
		tvDone_addCard.setOnClickListener(this);
		tvDone_addCard.setTypeface(ClanaproNarrNews);
		tvDone_addCard.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE)
				{
					cardPaymentModel.validateCardDetails(card_input_widget_card.getCard(),
							AddCardActivity.this);
					return true;
				}
				return false;
			}
		});

		tvScanCard_addCard = (TextView) findViewById(R.id.tvScanCard_addCard);
		tvScanCard_addCard.setTypeface(ClanaproNarrNews);
		tvScanCard_addCard.setOnClickListener(this);

		TextView tvPoweredBy_addCard = (TextView) findViewById(R.id.tvPoweredBy_addCard);
		tvPoweredBy_addCard.setTypeface(ClanaproNarrNews);

		rlScanCard_addCard = (RelativeLayout) findViewById(R.id.rlScanCard_addCard);
		rlDone_addCard = (RelativeLayout) findViewById(R.id.rlDone_addCard);

		EditText et_card_number= (EditText) card_input_widget_card.findViewById(R.id.et_card_number);
		et_card_number.setTypeface(ClanaproNarrNews);
		et_card_number.setTextSize(16);

		EditText et_expiry_date= (EditText) card_input_widget_card.findViewById(R.id.et_expiry_date);
		et_expiry_date.setTypeface(ClanaproNarrNews);
		et_expiry_date.setTextSize(16);

		EditText et_cvc_number= (EditText) card_input_widget_card.findViewById(R.id.et_cvc_number);
		et_cvc_number.setTextSize(16);
		et_cvc_number.setTypeface(ClanaproNarrNews);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tvSkipToolBar_addCard:
				Intent intent = new Intent(AddCardActivity.this,MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
				break;

			case R.id.tvDone_addCard:
				cardPaymentModel.validateCardDetails(card_input_widget_card.getCard(),
						AddCardActivity.this);
				break;

			case R.id.tvScanCard_addCard:
				if (Build.VERSION.SDK_INT >= 23) {
					ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList = new ArrayList<AppPermissionsRunTime.MyPermissionConstants>();
					myPermissionConstantsArrayList.clear();
					myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_CAMERA);

					if (AppPermissionsRunTime.checkPermission(this,myPermissionConstantsArrayList,PERMISSION_CAMERA )) {
						onScanPress();
					}
				}
				else {
					onScanPress();
				}
				break;
			default:
				break;
		}
	}

	/**
	 *
	 */
	private void onScanPress()
	{
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false
		scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO,true);
		scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON,false);
		scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME,true );
		int MY_SCAN_REQUEST_CODE = 100;
		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
	}

	/**
	 *<h2>callDoneButton</h2>
	 * This method is used to create the stripe token
	 */
	public void callDoneButton()
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(tvDone_addCard.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

		progressbar.setVisibility(View.VISIBLE);
		rlPoweredBy_addCard.setVisibility(View.VISIBLE);
		updateUi(true,getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimary));

		final ResponseListener.CardResponseListener respon = this;
		new Stripe(this).createToken(card_input_widget_card.getCard(), PUBLISHABLE_KEY, new TokenCallback() {
			@Override
			public void onError(Exception error)
			{
				Utility.printLog(TAG+"cardrequest onError "+error);
				updateUi(true,getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimary));
			}
			@Override
			public void onSuccess(Token token)
			{
				Utility.printLog(TAG+"cardrequest onSuccess "+token);
				//access_token=token.getId();
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("cardToken",token.getId());
					Log.i("TAG","cardrequest "+jsonObject);
					cardPaymentModel.addCardService(jsonObject, sessionMgr,respon);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if(scanflag)
		{
			scanCardResult();
		}
	}

	/**
	 * <h2>scanCardResult</h2>
	 * <p>
	 *     method to get values from the scanned card
	 * </p>
	 */
	private void scanCardResult()
	{
		card_input_widget_card.setCardNumber(scanResult.cardNumber);
		if(scanResult.expiryMonth>=1 && scanResult.expiryMonth<=12)
			card_input_widget_card.setExpiryDate(scanResult.expiryMonth,scanResult.expiryYear);
		card_input_widget_card.setCvcCode(scanResult.cvv);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT))
		{
			scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
			scanflag = true;
		}
	}
	/**
	 * predefined method to check run time permissions list call back
	 * @param permissions: contains the list of requested permissions
	 * @param grantResults: contains granted and un granted permissions result list
	 */
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		boolean isDenine = false;
		switch (requestCode)
		{
			case REQUEST_CODE_PERMISSION_MULTIPLE:
				for (int grantResult : grantResults) {
					if (grantResult != PackageManager.PERMISSION_GRANTED) {
						isDenine = true;
					}
				}
				if (isDenine)
				{
					Log.i("Permission","Denied ");
				}
				else
				{
					onScanPress();
					//TODO: if permissions granted by user, move forward
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}
	}

	/**
	 * <h2>updateUi</h2>
	 * <p>
	 *     method to update ui
	 * </p>
	 * @param b: to set clickable
	 * @param color: to set view background color
	 * @param color1: to set Text color
	 */
	private void updateUi(boolean b, int color, int color1)
	{
		rlDone_addCard.setBackgroundColor(color);
		tvDone_addCard.setTextColor(color1);
		tvDone_addCard.setClickable(b);
		tvDone_addCard.setEnabled(b);

		rlScanCard_addCard.setBackgroundColor(color);
		tvScanCard_addCard.setTextColor(color1);
		tvScanCard_addCard.setClickable(b);
		tvScanCard_addCard.setEnabled(b);
	}

	@Override
	public void onSuccess()
	{
		if (coming_From.equals("confirm_no")) {
			Intent intent = new Intent(AddCardActivity.this, MainActivity.class);
			startActivity(intent);
			finish();

		} else if (coming_From.equals("payment_Fragment")) {
			finish();

		} else if (coming_From.equals("LiveBooking")) {
			finish();
		}
	}
	@Override
	public void onError(String errorMsg) {
		progressbar.setVisibility(View.GONE);
		rlPoweredBy_addCard.setVisibility(View.VISIBLE);
		updateUi(true,getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.white));
		Toast.makeText(AddCardActivity.this, errorMsg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onValidOfCard() {
		callDoneButton();
	}

	@Override
	public void onInvalidOfCard() {
		Utility.showAlert(getString(R.string.You_did_not_enter_valid_card),this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		finish();
	}
}
