package com.example.moda.firebasebasedchat.ModelClasses;
/*
 * Created by moda on 02/04/16.
 */

/*
 * Setter-Getter(POJO) class for chat messages
 */
public class Chat_Message_item {


    public Chat_Message_item(String messageText, String senderId, String timestamp) {
        this.message = messageText;
        this.senderId = senderId;


        this.timestamp = timestamp;


    }


    public Chat_Message_item() {
    }

    private String message;
    private boolean isSelf, isDownloading;

    private String messageId, messageType, messageDateOverlay;

//    private Uri thumbnailPath;


    private String thumbnailPath;

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    private String receiverUid;
    private String size;
    private int downloadStatus;

    public String getSize() {
        return size;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }


    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    private String deliveryStatus;

    //    public Uri getThumbnailPath() {
//        return thumbnailPath;
//    }
//
//    public void setThumbnailPath(Uri thumbnailPath) {
//        this.thumbnailPath = thumbnailPath;
//    }
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

//    private Uri imagePath, videoPath;
//
//    public Uri getImagePath() {
//        return imagePath;
//    }
//
//    public void setImagePath(Uri imagePath) {
//        this.imagePath = imagePath;
//    }
//
//    public Uri getVideoPath() {
//        return videoPath;
//    }
//
//    public void setVideoPath(Uri videoPath) {
//        this.videoPath = videoPath;
//    }


    private String imagePath, videoPath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    private String timestamp;


    private String senderId;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    public String getMessageDateOverlay() {
        return messageDateOverlay;
    }

    public void setMessageDateOverlay(String MessageDateOverlay) {
        this.messageDateOverlay = MessageDateOverlay;
    }


}
