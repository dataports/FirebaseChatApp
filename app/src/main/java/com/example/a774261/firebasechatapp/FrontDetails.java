package com.example.a774261.firebasechatapp;
import android.graphics.Bitmap;

public class FrontDetails {

    public int getImage() {
        return image;
    }
    public void setImage(int imageN) {
        this.image = imageN;
    }


    public String getMsgType() {
        return MsgType;
    }
    public void setMsgType(String text) {
        this.MsgType = text;
    }



    private int image;
    private String MsgType;
}
