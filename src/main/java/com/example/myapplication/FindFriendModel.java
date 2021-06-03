package com.example.myapplication;

public class FindFriendModel {
    public String fullName;
    public String fakulta;
    public String profileImage;
    private String ID;

    public FindFriendModel(String fullName, String fakulta, String ID) {
        this.fullName = fullName;
        this.fakulta = fakulta;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public FindFriendModel() {
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
}
