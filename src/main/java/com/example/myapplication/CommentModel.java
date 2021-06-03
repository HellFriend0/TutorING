package com.example.myapplication;

import java.sql.Timestamp;

public class CommentModel {
    private String meno;
    private String komentar;
    private Timestamp time;

    public CommentModel() {
    }

    public CommentModel(String meno, String komentar, Timestamp time) {
        this.meno = meno;
        this.komentar = komentar;
        this.time = time;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
}
