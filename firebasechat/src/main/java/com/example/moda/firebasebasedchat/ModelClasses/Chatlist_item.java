package com.example.moda.firebasebasedchat.ModelClasses;

/**
 * Created by moda on 20/06/17.
 */

public class Chatlist_item {


    private String receiverUid, documentId, newMessage, newMessageCount, newMessageTime, newMessageDate, receiverName;

    private boolean isNewMessage;


    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String ReceiverName) {
        this.receiverName = ReceiverName;
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String DocumentId) {
        this.documentId = DocumentId;
    }


    public String getNewMessageCount() {
        return newMessageCount;
    }

    public void setNewMessageCount(String newMessageCount) {
        this.newMessageCount = newMessageCount;
    }


    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }


    public String getNewMessageTime() {
        return newMessageTime;
    }

    public void setNewMessageTime(String newMessageTime) {
        this.newMessageTime = newMessageTime;
    }


    public boolean hasNewMessage() {
        return isNewMessage;
    }

    public void sethasNewMessage(boolean isNewMessage) {
        this.isNewMessage = isNewMessage;
    }


    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String ReceiverUid) {
        this.receiverUid = ReceiverUid;
    }



    private String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
