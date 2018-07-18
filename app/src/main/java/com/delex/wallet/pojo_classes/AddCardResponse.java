package com.delex.wallet.pojo_classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by embed on 2/10/15.
 */
public class AddCardResponse implements Serializable {
    private String errNum;

    private String errMsg;
    private String errFlag;
    private ArrayList<Cards> cards;


    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(String errFlag) {
        this.errFlag = errFlag;
    }

    public ArrayList<Cards> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Cards> cards) {
        this.cards = cards;
    }



}
