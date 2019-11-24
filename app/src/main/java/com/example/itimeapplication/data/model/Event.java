package com.example.itimeapplication.data.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Event implements Serializable {

    private int pic_resource_id;
    private String remain_time;
    private String name;
    private String description;
    private EventDate date;
    private int repeatDay;
    private Bitmap picture;

    public Event(String name, String description, EventDate date, int repeatDay) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.repeatDay = repeatDay;

    }


    public int getPic_resource_id() {
        return pic_resource_id;
    }

    public void setPic_resource_id(int pic_resource_id) {
        this.pic_resource_id = pic_resource_id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventDate getDate() {
        return date;
    }

    public void setDate(EventDate date) {
        this.date = date;
    }

    public int getRepeatDay() {
        return repeatDay;
    }

    public void setRepeatDay(int repeatDay) {
        this.repeatDay = repeatDay;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
