package com.delex.wallet.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.delex.driver.R;
import com.delex.utility.OkHttp3Connection;
import com.delex.utility.ServiceUrl;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;
import com.delex.wallet.pojo_classes.CardInfoPojo;
import com.delex.wallet.pojo_classes.GetCard_pojo;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>ChangeCardActivity</h1>
 * This class is used for choosing the card from card list
 * @author embed on 9/12/15.
 * @version v1.0
 */
public class ChangeCardActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener
{
    private ListView lv_payment_cards;
    private GettingCardsAdapter adapter;
    private List<CardInfoPojo> cardList;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment);
        initialize();
    }

    /**
     * <h2>initialize</h2>
     * <p>
     * initialize the views
     * </p>
     */
    private void initialize() {
        resources = ChangeCardActivity.this.getResources();

        LinearLayout add_actionbar = (LinearLayout) findViewById(R.id.actionbar);
        add_actionbar.setVisibility(View.INVISIBLE);

        RelativeLayout activity_action_bar = (RelativeLayout) findViewById(R.id.activity_action_bar);
        activity_action_bar.setVisibility(View.VISIBLE);

        TextView bar_title = (TextView) findViewById(R.id.tvToolBarTitle);
        bar_title.setText(resources.getString(R.string.paymentMethod));

        RelativeLayout back_Layout = (RelativeLayout) findViewById(R.id.rlBackArrow);
        lv_payment_cards = (ListView) findViewById(R.id.lv_payment_cards);
        TextView tv_payment_add_card = (TextView) findViewById(R.id.tv_payment_add_card);

        sessionManager=new SessionManager(ChangeCardActivity.this);
        cardList = new ArrayList<CardInfoPojo>();
        adapter = new GettingCardsAdapter(ChangeCardActivity.this, R.layout.item_card_list, cardList);
        lv_payment_cards.setAdapter(adapter);

        tv_payment_add_card.setOnClickListener(this);
        lv_payment_cards.setOnItemClickListener(this);
        back_Layout.setOnClickListener(this);
    }

    /**
     * <h1>onClick</h1>
     * <p>
     * Called when a view has been clicked.
     * </p>
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //on click of back button finish the activity
        if (v.getId() == R.id.rlBackArrow) {
            finish();
            overridePendingTransition(R.anim.mainfadein, R.anim.slide_down_acvtivity);
        }
        //start payment activity for adding card
        if (v.getId() == R.id.tv_payment_add_card) {
            Intent intent = new Intent(ChangeCardActivity.this, AddCardActivity.class);
            intent.putExtra("coming_From", "payment_Fragment");
            startActivity(intent);
            ChangeCardActivity.this.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        }
    }

    /**
     * <h1>onBackPressed</h1>
     * <p>
     * THis method is used when the device back button is clicked
     * </p>
     */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.mainfadein, R.anim.slide_down_acvtivity);
    }

    /**
     * <h1>onResume</h1>
     * <p>
     * calling all card API
     * </p>
     */
    @Override
    protected void onResume() {
        super.onResume();
        getCardsAPI();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CardInfoPojo row_details =(CardInfoPojo)lv_payment_cards.getItemAtPosition(position);
        if (row_details != null) {
            makeDefaultCard(row_details);
        }
        else
        {
            Toast.makeText(ChangeCardActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * <h2>getCardsAPI</h2>
     * <p>
     * api call for get all cards
     * </p>
     */
    public void getCardsAPI() {
        Utility.printLog(" in side get card service ");
        showProgressDialog(getString(R.string.loading));
        JSONObject jsonObject = new JSONObject();

        OkHttp3Connection.doOkHttp3Connection(sessionManager.getSessionToken(),ServiceUrl.GETCARD,
                OkHttp3Connection.Request_type.GET, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Utility.printLog("The Response get card " + jsonResponse);
                callGetCardServiceResponse(jsonResponse);
            }
            @Override
            public void onError(String error)
            {
                hideProgressDialog();
                Toast.makeText(ChangeCardActivity.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        },sessionManager.getSessionToken());
    }

    /**
     * <h2>makeDefaultCard</h2>
     * <p>
     * This method is used for making our Card as a Default Card.
     * </p>
     * @param row_details contains the data of a card.
     */
    public void makeDefaultCard(final CardInfoPojo row_details)
    {
        Utility.printLog(" in side get card service ");
        showProgressDialog(getString(R.string.loading));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cardId", row_details.getCard_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttp3Connection.doOkHttp3Connection(ServiceUrl.DEFAULT_CARD, OkHttp3Connection.Request_type.POST, jsonObject, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String jsonResponse)
            {
                Utility.printLog("The Response get card " + jsonResponse);
                //to store the default card details in share pref
                sessionManager.setLastCard(row_details.getCard_id());
                sessionManager.setLastCardNumber(row_details.getCard_numb());
                sessionManager.setCardType(row_details.getFunding());
                sessionManager.setLastCardImage(row_details.getCard_image());
                //to finish the activity and move to previous one
                hideProgressDialog();
                overridePendingTransition(R.anim.mainfadein, R.anim.slide_down_acvtivity);
                finish();
            }

            @Override
            public void onError(String error)
            {
                hideProgressDialog();
                Toast.makeText(ChangeCardActivity.this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        },sessionManager.getSessionToken());
    }

    /**
     * <h2>showProgressDialog</h2>
     * <p>
     *     method to show the progress dialog if
     *     its not already visible
     * </p>
     * @param pDialogMsg: contains the message to set along with the
     *                  progress dialog
     */
    private void showProgressDialog(String pDialogMsg)
    {
        if(pDialog!=null)
            pDialog.show();
        else
            {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage(pDialogMsg);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    /**
     * <h2>hideProgressDialog</h2>
     * <p>
     *     method to hide the progress dialog if
     *     its already visible
     * </p>
     */
    private void hideProgressDialog()
    {
        if (pDialog != null) {
            pDialog.cancel();
            pDialog.dismiss();
        }
    }


    /**]
     * <h1>callGetCardServiceResponse</h1>
     * <p>
     * handling response if card list size is greater then 0 then setting adapter
     * </p>
     * @param Response response of the API call
     */
    public void callGetCardServiceResponse(String Response) {
        Utility.printLog("error no get card" + Response);
        try {
            Gson gson = new Gson();
            GetCard_pojo response = gson.fromJson(Response, GetCard_pojo.class);
            if (response.getErrNum() == 200 && response.getErrFlag() == 0) {
                cardList.clear();
                if(response.getData().length>0)
                {
                    for (int i = 0; i < response.getData().length; i++) {
                        CardInfoPojo item = new CardInfoPojo(response.getData()[i].getBrand(),
                                response.getData()[i].getLast4(), response.getData()[i].getExp_month(),
                                response.getData()[i].getExp_year(), response.getData()[i].getId(),
                                response.getData()[i].getDefaultCard(),
                                response.getData()[i].getName(), response.getData()[i].getFunding());//id
                        cardList.add(item);
                        //to store the default card details
                        if (response.getData()[i].getDefaultCard())
                        {
                            sessionManager.setLastCard(response.getData()[i].getId());
                            sessionManager.setLastCardNumber(response.getData()[i].getLast4());
                            sessionManager.setCardType(response.getData()[i].getFunding());
                            sessionManager.setLastCardImage(response.getData()[i].getBrand());
                        }
                    }
                }
                else
                {
                    //to reset the default card details
                    sessionManager.setLastCard("");
                    sessionManager.setLastCardNumber("");
                    sessionManager.setCardType("");
                    sessionManager.setLastCardImage("");
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ChangeCardActivity.this, response.getErrMsg(), Toast.LENGTH_LONG).show();
            }
            if (pDialog != null) {
                pDialog.cancel();
                pDialog.dismiss();
            }
        }
        catch (Exception e) {
            if (pDialog != null) {
                pDialog.cancel();
                pDialog.dismiss();
            }
            e.printStackTrace();
        }
    }

    /**
     * <h2>createBundle</h2>
     * This method is used for creating the bundle to delete the card
     * @param row_details takes the object of CardInfoPojo
     * @return returns the bundle
     */
    public Bundle createBundle(CardInfoPojo row_details)
    {
        String expDate = row_details.getExp_month();
        if (expDate.length() == 1) {
            expDate = "0" + expDate;
        }
        expDate = expDate + "/" + row_details.getExp_year();
        Bundle bundle = new Bundle();
        bundle.putString("NUM", row_details.getCard_numb());
        bundle.putString("EXP", expDate);
        bundle.putString("ID", row_details.getCard_id());
        bundle.putString("NAM", row_details.getName());
        bundle.putBoolean("DFLT",row_details.getDefaultCard());
        bundle.putString("IMG", row_details.getCard_image());
        return bundle;
    }

    /**
     * <h2>deleteCardMethod</h2>
     * This method is used to delete the card selected
     * @param position position of the card to be deleted
     */
    public void deleteCardMethod(int position)
    {
        CardInfoPojo row_details = (CardInfoPojo) lv_payment_cards.getItemAtPosition(position);
        Intent intent = new Intent(ChangeCardActivity.this, DeleteCardActivity.class);
        intent.putExtras(createBundle(row_details));
        startActivity(intent);
    }

    /**
     * <h2>GettingCardsAdapter</h2>
     * </p>
     * adapter class for list view of cards
     * </p>
     */

    private class GettingCardsAdapter extends ArrayAdapter<CardInfoPojo> {
        Context context;
        GettingCardsAdapter(Context context, int resourceId, List<CardInfoPojo> items) {
            super(context, resourceId, items);
            this.context = context;
        }

        private class ViewHolder {
            ImageView iv_payment_card;
            TextView tv_payment_card_number;
            ImageView iv_payment_tick,iv_payment_delete;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            final CardInfoPojo rowItem = getItem(position);

            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_card_list, null);
                holder = new ViewHolder();

                holder.tv_payment_card_number = (TextView) convertView.findViewById(R.id.tv_payment_card_number);
                holder.iv_payment_card = (ImageView) convertView.findViewById(R.id.iv_payment_card);
                holder.iv_payment_tick = (ImageView) convertView.findViewById(R.id.iv_payment_tick);
                holder.iv_payment_delete = (ImageView) convertView.findViewById(R.id.iv_payment_delete);

                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder) convertView.getTag();

            holder.iv_payment_card.setImageResource(Utility.checkCardType(rowItem.getCard_image()));
            holder.tv_payment_card_number.setText(getString(R.string.card_ending_with) + " "+
                    rowItem.getCard_numb());
            Utility.printLog("value of card: "+rowItem.getCard_id()+" , stored card: "+
                    sessionManager.getLastCard());

            holder.iv_payment_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCardMethod(position);
                }
            });

            if (rowItem.getDefaultCard())
            {
                holder.tv_payment_card_number.setTextColor(ContextCompat.getColor(
                        ChangeCardActivity.this,R.color.black));
                holder.iv_payment_tick.setVisibility(View.VISIBLE);

                sessionManager.setLastCard(rowItem.getCard_id());
                sessionManager.setLastCardNumber(rowItem.getCard_numb());
                sessionManager.setCardType(rowItem.getFunding());
                sessionManager.setLastCardImage(rowItem.getCard_image());
            }
            else
            {
                holder.tv_payment_card_number.setTextColor(Color.parseColor("#000000"));
                holder.iv_payment_tick.setVisibility(View.GONE);
            }
            return convertView;
        }
    }
}
