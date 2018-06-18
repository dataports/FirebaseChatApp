package com.example.a774261.firebasechatapp;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;

public class ChatMessage {
    public String uid;
    private String messageText;
    private String messageUser;
    private String messageTime;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        // Initialize to current time
      //  long messageTemp = new Date().getTime();
        Date messageDate = new Date();
        DateFormat longDf = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        messageTime = longDf.format(messageDate);
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    //check that the message doesn't exceed the character limit of 256
    public boolean checkMessageLength(String message){
        setMessageText(message);
        int length = messageText.length();

        if(length < 256){
            return true;
        }
        else{
            return false;
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("messageText", messageText);
        result.put("messageUser", messageUser);
        result.put("messageTime", messageTime);

        return result;
    }

}
