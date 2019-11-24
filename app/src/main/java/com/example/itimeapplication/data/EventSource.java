package com.example.itimeapplication.data;

import android.content.Context;

import com.example.itimeapplication.data.model.Event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class EventSource {
    Context context;
    ArrayList<Event> events=new ArrayList<Event>();

    public ArrayList<Event> getEvents() {
        return events;
    }
    public EventSource(Context context) {
        this.context = context;
    }

    public void save()
    {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("dataEvent.txt",Context.MODE_PRIVATE));
            outputStream.writeObject(events);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Event> load() {      //用于加载
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("dataEvent.txt")
            );
            events = (ArrayList<Event>) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}
