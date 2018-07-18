package com.example.moda.firebasebasedchat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.moda.firebasebasedchat.Activities.Chatlist;
import com.example.moda.firebasebasedchat.ModelClasses.Chatlist_item;
import com.example.moda.firebasebasedchat.R;
import com.example.moda.firebasebasedchat.Utilities.TextDrawable;
import com.example.moda.firebasebasedchat.Utilities.Utilities;
import com.example.moda.firebasebasedchat.ViewHolders.ViewHolderChatlist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by moda on 20/06/17.
 */

public class ChatlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private ArrayList<Chatlist_item> mOriginalListData = new ArrayList<>();
    private ArrayList<Chatlist_item> mFilteredListData;

    private Context mContext;


    private int density;


    private ArrayList<String> colors = new ArrayList<>();

    /**
     * @param mContext  Context
     * @param mListData ArrayList<ChatList_item>
     */
    public ChatlistAdapter(Context mContext, ArrayList<Chatlist_item> mListData) {
        this.mOriginalListData = mListData;


        this.mFilteredListData = mListData;
        this.mContext = mContext;

        setBackgroundColorArray();
        density = (int) mContext.getResources().getDisplayMetrics().density;
    }

    /**
     * @return number of items
     */
    @Override
    public int getItemCount() {
        return this.mFilteredListData.size();
    }


    /**
     * @param position item position
     * @return item viewType
     */
    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    /**
     * @param viewGroup ViewGroup
     * @param viewType  item viewType
     * @return RecyclerView.ViewHolder
     */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        View v = inflater.inflate(R.layout.chatlist_item, viewGroup, false);
        viewHolder = new ViewHolderChatlist(v);

        return viewHolder;
    }


    /**
     * @param viewHolder RecyclerView.ViewHolder
     * @param position   item position
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        ViewHolderChatlist vh2 = (ViewHolderChatlist) viewHolder;
        configureViewHolderChatlist(vh2, position);

    }


    /**
     * @param vh       ViewHolderChatlist
     * @param position item position
     */
    private void configureViewHolderChatlist(ViewHolderChatlist vh, int position) {
        final Chatlist_item chat = mFilteredListData.get(position);
        if (chat != null) {


//            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
//            vh.newMessageCount.setTypeface(tf, Typeface.BOLD);
//            vh.newMessageDate.setTypeface(tf, Typeface.NORMAL);
//            vh.newMessageTime.setTypeface(tf, Typeface.NORMAL);
//            vh.newMessage.setTypeface(tf, Typeface.NORMAL);


            vh.newMessageDate.setText("");
            vh.newMessageTime.setText("");
            vh.newMessageCount.setText("");


            vh.storeName.setText(chat.getReceiverName());


            vh.newMessage.setText(chat.getNewMessage());


            try {

                String formatedDate = Utilities.formatDate(Utilities.tsFromGmt(chat.getNewMessageTime()));

                if ((chat.getNewMessageTime().substring(0, 8)).equals((Utilities.tsInGmt().substring(0, 8)))) {


                    vh.newMessageDate.setText(R.string.string_207);
                } else if ((Integer.parseInt((Utilities.tsInGmt().substring(0, 8))) - Integer.parseInt((chat.getNewMessageTime().substring(0, 8)))) == 1) {


                    vh.newMessageDate.setText(R.string.string_208);

                } else {
                    vh.newMessageDate.setText(formatedDate.substring(9, 24));

                }
                String last = convert24to12hourformat(formatedDate.substring(0, 9));

                vh.newMessageTime.setText(last);


                if (chat.hasNewMessage()) {


                    vh.newMessageCount.setText(chat.getNewMessageCount());

                    vh.rl.setVisibility(View.VISIBLE);
                } else {
                    vh.newMessageCount.setText("");
                    vh.rl.setVisibility(View.GONE);

                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            vh.storeImage.setImageDrawable(TextDrawable.builder()


                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .fontSize(20 * density) /* size in px */
                    .bold()
                    .toUpperCase()
                    .endConfig()


                    .buildRound((chat.getReceiverName().trim()).charAt(0) + "", Color.parseColor(getColorCode(vh.getAdapterPosition() % 19))));


        }
    }


    /*
     *
     * to convert date from  24 hour format to the 12 hour format
     * */

    /**
     * @param d date in 24 hour format
     * @return date in 12 hour format
     */

    private String convert24to12hourformat(String d) {

        String datein12hour = null;

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(d);


            datein12hour = new SimpleDateFormat("h:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }


        return datein12hour;

    }

    /**
     * @return list of filtered items
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredListData = (ArrayList<Chatlist_item>) results.values;
                ChatlistAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Chatlist_item> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = mOriginalListData;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());

                    if (filteredResults.size() == 0) {


                        ((Chatlist) mContext).showNoSearchResults(constraint);
                    }

                }

                FilterResults results = new Filter.FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }


    /**
     * @param constraint query to search for
     * @return ArrayList<ChatList_item>
     */

    private ArrayList<Chatlist_item> getFilteredResults(String constraint) {
        ArrayList<Chatlist_item> results = new ArrayList<>();

        for (Chatlist_item item : mOriginalListData) {
            if (item.getReceiverName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }


    /**
     * @return ArrayList<ChatList_item>
     */

    public ArrayList<Chatlist_item> getList() {


        return mFilteredListData;
    }


    private void setBackgroundColorArray() {


        colors = new ArrayList<>();


        colors.add("#FFCDD2");
        colors.add("#D1C4E9");
        colors.add("#B3E5FC");
        colors.add("#C8E6C9");
        colors.add("#FFF9C4");
        colors.add("#FFCCBC");
        colors.add("#CFD8DC");
        colors.add("#F8BBD0");
        colors.add("#C5CAE9");
        colors.add("#B2EBF2");
        colors.add("#DCEDC8");
        colors.add("#FFECB3");
        colors.add("#D7CCC8");
        colors.add("#F5F5F5");
        colors.add("#FFE0B2");
        colors.add("#F0F4C3");
        colors.add("#B2DFDB");
        colors.add("#BBDEFB");
        colors.add("#E1BEE7");


    }


    public String getColorCode(int position) {


        return colors.get(position);

    }

}

