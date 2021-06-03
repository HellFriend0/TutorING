package com.example.myapplication;

import java.sql.Timestamp;
import java.util.Date;

public class MessageModel {
    private String fullName;
    private String fakulta;
    private String text;
    private String ID;
    private String documentID;
    private Date time;

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public MessageModel() {
    }
    public MessageModel(String fullName, String fakulta, String text,String ID,String documentID,Date time) {
        this.fullName = fullName;
        this.fakulta = fakulta;
        this.time = time;
        this.text = text;
        this.ID = ID;
        this.documentID = documentID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFakulta() {
        return fakulta;
    }

    public void setFakulta(String fakulta) {
        this.fakulta = fakulta;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
