package com.example.itimeapplication.data.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;

public class Event implements Serializable {


    private String name;
    private String description;
    private EventDate date;
    private int repeatDay;
    private File pictureFilePath;
    private int pic_resource_id;
    private long remain_time;


    public Event(String name, String description, EventDate date, int repeatDay, File pictureFilePath) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.repeatDay = repeatDay;
        this.pictureFilePath = pictureFilePath;
    }


    public int getPic_resource_id() {
        return pic_resource_id;
    }

    public void setPic_resource_id(int pic_resource_id) {
        this.pic_resource_id = pic_resource_id;
    }

    public long getRemain_time() {
        return remain_time;
   }

   public void setRemain_time(long remain_time) {
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

    public File getPictureFilePath() {
        return pictureFilePath;
    }

    public void setPictureFilePath(File pictureFilePath) {
        this.pictureFilePath = pictureFilePath;
    }

    public String display_remian_time()
    {
        String remain="";
        Calendar calendar1=Calendar.getInstance();
        Calendar calendar2=Calendar.getInstance();
        calendar2.set(this.date.getYear(),this.date.getMonth(),this.date.getDay(),date.getHour(),date.getMinute(),date.getSecond());
        long time1 = calendar1.getTimeInMillis();
        long time2 = calendar2.getTimeInMillis();
        remain_time=time2-time1;
        int between_days= (int) (remain_time/(1000*3600*24));
        if(between_days==0)
            remain="Today";
        else if(between_days>0)
            remain=between_days+" Days";
        else if(between_days<0) {
            between_days = -between_days;
            remain = between_days + " Days\n  Ago";
        }
        return remain;
    }


}
