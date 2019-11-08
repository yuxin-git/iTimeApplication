package com.example.itimeapplication.data.model;

public class Time {

    private String remain_time;
    private String name;
    private String date;
    private String description;

    public Time(String remain_time, String name, String date,String description) {
        this.remain_time = remain_time;
        this.name = name;
        this.date = date;
        this.description=description;
    }


    public String getRemain_time() {
        return remain_time;
    }

    public void setRemain_time(String remain_time) {
        this.remain_time = remain_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}