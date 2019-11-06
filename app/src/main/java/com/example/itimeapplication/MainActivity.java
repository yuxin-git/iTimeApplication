package com.example.itimeapplication;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.itimeapplication.data.model.Time;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Time> times;
    private TimesArrayAdapter theAdapter;
    ListView listViewTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitData();
        theAdapter=new TimesArrayAdapter(this,R.layout.list_item_time, times);
        listViewTime= this.findViewById(R.id.list_view_time);
        listViewTime.setAdapter(theAdapter);
    }

    private void InitData()
    {
        times =new ArrayList<Time>();
        times.add(new Time("DAYS","Birthday1","2001"));
        times.add(new Time("2DAYS","bir2","2003"));
        times.add(new Time("ADAYS","birth4","1999"));
    }

    public class TimesArrayAdapter extends ArrayAdapter<Time>
    {
        private  int resourceId;
        public TimesArrayAdapter(@NonNull MainActivity context, @LayoutRes int resource, @NonNull ArrayList<Time> objects) {
            super(context, resource, objects);
            resourceId=resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View item = mInflater.inflate(this.resourceId,null);

            TextView remain_time = (TextView)item.findViewById(R.id.text_view_remain_time);
            TextView name = (TextView)item.findViewById(R.id.text_view_name);
            TextView date = (TextView)item.findViewById(R.id.text_view_date);

            Time time_item = this.getItem(position);
            remain_time.setText(time_item.getRemain_time());
            Resources resources=getBaseContext().getResources();
            Drawable drawable=resources.getDrawable(R.drawable.a1);
            remain_time.setBackgroundDrawable(drawable);
            name.setText(time_item.getName());
            date.setText(time_item.getDate());


            return item;
        }
    }

}
