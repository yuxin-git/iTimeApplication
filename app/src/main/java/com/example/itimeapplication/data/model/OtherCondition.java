package com.example.itimeapplication.data.model;

public class OtherCondition {

    private int con_picture;
    private String con_name;
    private String con_explain;

    public OtherCondition(int con_picture, String con_name, String con_explain) {
        this.con_picture = con_picture;
        this.con_name = con_name;
        this.con_explain = con_explain;
    }

    public int getCon_picture() {
        return con_picture;
    }

    public void setCon_picture(int con_picture) {
        this.con_picture = con_picture;
    }

    public String getCon_name() {
        return con_name;
    }

    public void setCon_name(String con_name) {
        this.con_name = con_name;
    }

    public String getCon_explain() {
        return con_explain;
    }

    public void setCon_explain(String con_explain) {
        this.con_explain = con_explain;
    }


}
