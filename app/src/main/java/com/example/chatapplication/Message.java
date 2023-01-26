package com.example.chatapplication;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Timestamp;

public class Message {
    private String sender;
    private String receiver;
    private String msg;
    private int isseen;
    private Timestamp timetamp;


    public Message() {

    }

    public Message(String sender, String receiver, String msg,int isseen,Timestamp timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.isseen=isseen;
        this.timetamp=timestamp;
    }

    public Timestamp getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(Timestamp timetamp) {
        this.timetamp = timetamp;
    }

    public int getIsseen() {
        return isseen;
    }

    public void setIsseen(int isseen) {
        this.isseen = isseen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
