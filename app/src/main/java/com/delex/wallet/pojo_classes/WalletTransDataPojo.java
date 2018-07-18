package com.delex.wallet.pojo_classes;

import java.util.ArrayList;

/**
 * @since 19/09/17.
 */

public class WalletTransDataPojo
{
   /* "data":{
    "debitArr":[],
    "creditArr":[],
    "creditDebitArr":[]}*/

   private ArrayList<WalletTransDataDetailsPojo> debitArr;
   private ArrayList<WalletTransDataDetailsPojo> creditArr;
   private ArrayList<WalletTransDataDetailsPojo> creditDebitArr;


    public ArrayList<WalletTransDataDetailsPojo> getDebitArr() {
        return debitArr;
    }

    public void setDebitArr(ArrayList<WalletTransDataDetailsPojo> debitArr) {
        this.debitArr = debitArr;
    }

    public ArrayList<WalletTransDataDetailsPojo> getCreditArr() {
        return creditArr;
    }

    public void setCreditArr(ArrayList<WalletTransDataDetailsPojo> creditArr) {
        this.creditArr = creditArr;
    }

    public ArrayList<WalletTransDataDetailsPojo> getCreditDebitArr() {
        return creditDebitArr;
    }

    public void setCreditDebitArr(ArrayList<WalletTransDataDetailsPojo> creditDebitArr) {
        this.creditDebitArr = creditDebitArr;
    }

    @Override
    public String toString() {
        return "WalletTransDataPojo{" +
                "debitArr=" + debitArr +
                ", creditArr=" + creditArr +
                ", creditDebitArr=" + creditDebitArr +
                '}';
    }
}
