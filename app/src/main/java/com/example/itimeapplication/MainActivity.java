package com.example.itimeapplication;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.itimeapplication.data.model.Time;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Time> times=new ArrayList<Time>();
    private TimesArrayAdapter theAdapter;
    ListView listViewTime;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitData();
        theAdapter=new TimesArrayAdapter(this,R.layout.list_item_time, times);

        listViewTime= this.findViewById(R.id.list_view_time);
        listViewTime.setAdapter(theAdapter);
        buttonAdd=findViewById(R.id.button_add);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, AddNewTimeActivity.class);
                startActivityForResult(intent,1);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            if (resultCode == 1) {
                String name = data.getStringExtra("time_name");
                String description = data.getStringExtra("time_description");

                times.add(new Time(R.drawable.a1,"未知", name, "data", description));
                theAdapter.notifyDataSetChanged();
            }
        }

    }

    private void InitData()
    {
        times.add(new Time(R.drawable.a1,"DAYS","Birthday1","2001","无"));
        times.add(new Time(R.drawable.a1,"2DAYS","bir2","2003","无"));
        times.add(new Time(R.drawable.a1,"ADAYS","birth4","1999","无"));
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

            ImageView pic=(ImageView)item.findViewById(R.id.image_view_pic);
            TextView remain_time = (TextView)item.findViewById(R.id.text_view_remain_time);
            TextView name = (TextView)item.findViewById(R.id.text_view_name);
            TextView date = (TextView)item.findViewById(R.id.text_view_date);
            TextView description = (TextView)item.findViewById(R.id.text_view_description);

            Time time_item = this.getItem(position);
            pic.setImageResource(time_item.getPic_resource_id());
            remain_time.setText(time_item.getRemain_time());
            name.setText(time_item.getName());
            date.setText(time_item.getDate());
            description.setText(time_item.getDescription());


            return item;
        }
    }

}
