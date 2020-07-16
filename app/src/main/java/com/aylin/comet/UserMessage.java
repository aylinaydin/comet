package com.aylin.comet;

/**
 * Created by Aylin on 12.03.2018.
 */

public class UserMessage {

    //public String username;
    private String message;
    private String senderId;
    private String receiverId;
    private String messageTime;
    private String groupId;
    private String groupName;
    //private long messageTime;


    public UserMessage(String message, String senderId, String groupName, String messageTime) {
        this.message = message;
        this.senderId = senderId;
        this.messageTime = messageTime;
        this.groupName = groupName;

    }

    public UserMessage() {

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName() {
        this.groupName = groupName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setReceiverId() {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setSenderId() {
        this.senderId = senderId;
    }

    public void setMessage() {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

}
