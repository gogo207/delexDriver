package com.example.moda.firebasebasedchat.Utilities;

import android.util.Log;

import com.example.moda.firebasebasedchat.ModelClasses.Chatlist_item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by moda on 22/06/17.
 */

public class ChatsSorter implements Comparator {

    private Date date1, date2;


    @SuppressWarnings("unchecked")
    public int compare(Object firstObjToCompare, Object secondObjToCompare) {
        String firstDateString = ((Chatlist_item) firstObjToCompare).getNewMessageTime();
        String secondDateString = ((Chatlist_item) secondObjToCompare).getNewMessageTime();




        if (secondDateString == null || firstDateString == null) {
            return 0;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS z");


        try {

            date1 = sdf.parse(firstDateString);
            date2 = sdf.parse(secondDateString);


        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date1.after(date2)) return -1;
        else if (date1.before(date2)) return 1;
        else return 0;
    }

}