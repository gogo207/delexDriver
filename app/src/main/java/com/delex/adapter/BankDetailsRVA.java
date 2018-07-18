package com.delex.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delex.bankDetails.BankBottomSheetFragment;
import com.delex.driver.R;
import com.delex.pojo.bank.BankList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by murashid on 20-Mar-17.
 */

public class BankDetailsRVA extends RecyclerView.Adapter<BankDetailsRVA.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "BankDetailsRVA";
    private ArrayList<BankList> bankLists;
    private Typeface fontRegular, fontLight;
    private RefreshBankDetails refreshBankDetails;
    private FragmentManager fragmentManager;

    public BankDetailsRVA(Context context, ArrayList<BankList> bankLists, FragmentManager fragmentManager, RefreshBankDetails refreshBankDetails)
    {
        this.bankLists = bankLists;
        fontRegular = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrMedium.otf");
        fontLight = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrNews.otf");
        this.fragmentManager = fragmentManager;
        this.refreshBankDetails = refreshBankDetails;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BankDetailsRVA.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_bank_details, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.tvAccountNo.setText("xxxxxxxx"+ bankLists.get(position).getLast4());
        holder.tvAccountHolder.setText(bankLists.get(position).getAccount_holder_name());

        /*
        change to default account when we get the proper responce
        * */
        if(position==0)
        {
//            holder.ivCheck.setVisibility(View.VISIBLE);
        }
        else
        {
//            holder.ivCheck.setVisibility(View.INVISIBLE);
        }

        holder.llBankDetails.setOnClickListener(this);
        holder.llBankDetails.setTag(holder);

    }

    @Override
    public int getItemCount() {
        return bankLists.size();
    }

    @Override
    public void onClick(View v)
    {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        int position = viewHolder.getAdapterPosition();

        switch (v.getId())
        {
            case R.id.llBankDetails:
                BottomSheetDialogFragment bottomSheetDialogFragment = BankBottomSheetFragment.newInstance(bankLists.get(position),refreshBankDetails);
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                break;

        }
    }

    public interface RefreshBankDetails extends Serializable {
        void onRefresh();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView ivCheck;
        TextView tvAccountNoLabel, tvAccountNo, tvAccountHolderLabel, tvAccountHolder;
        LinearLayout llBankDetails;

        public ViewHolder(View itemView)
        {
            super(itemView);

            ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
            tvAccountNoLabel = (TextView) itemView.findViewById(R.id.tvAccountNoLabel);
            tvAccountNo = (TextView) itemView.findViewById(R.id.tvAccountNo);
            tvAccountHolderLabel = (TextView) itemView.findViewById(R.id.tvAccountHolderLabel);
            tvAccountHolder = (TextView) itemView.findViewById(R.id.tvAccountHolder);
            llBankDetails = (LinearLayout) itemView.findViewById(R.id.llBankDetails);

            tvAccountNoLabel.setTypeface(fontLight);
            tvAccountHolderLabel.setTypeface(fontLight);

            tvAccountNo.setTypeface(fontRegular);
            tvAccountHolder.setTypeface(fontRegular);
        }

    }

}
