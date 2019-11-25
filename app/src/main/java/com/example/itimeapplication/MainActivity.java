package com.example.itimeapplication;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itimeapplication.data.EventSource;
import com.example.itimeapplication.data.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Event> events =new ArrayList<Event>();
    private TimesArrayAdapter theAdapter;
    ListView listViewEvent;
    private EventSource eventSource;

    private static final int REQUEST_CODE_NEW_EVENT = 201;
    private static final int REQUEST_CODE_DETAILS = 203;
    private FloatingActionButton fabAdd;

    private int deleteCode=0; //控制删除操作，为1则执行删除操作，否则不执行
    private int deleteItemPosition;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventSource=new EventSource(this);
        events=eventSource.load();


        theAdapter=new TimesArrayAdapter(this,R.layout.list_item_event, events);

        listViewEvent= this.findViewById(R.id.list_view_time);
        listViewEvent.setAdapter(theAdapter);
        listViewEvent.setDivider(null);     //取消listview中item间的边框

        //点击+按钮，跳转至EditTimeActivity新建一条数据
        fabAdd=findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, EditEventActivity.class);
                intent.putExtra("edit_code",0);
                startActivityForResult(intent,REQUEST_CODE_NEW_EVENT);
            }
        });

        //设置listview中item的点击事件，详情界面
        listViewEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("点击了一个item ","1");

                Intent intent=new Intent(MainActivity.this, EventDetailsActivity.class);
                intent.putExtra("time_position",i);
                Event thisEvent=events.get(i);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("data2.txt",thisEvent);
                intent.putExtras(mBundle);

                startActivityForResult(intent,REQUEST_CODE_DETAILS);
            }
        });

        deleteCode=getIntent().getIntExtra("delete_code",0);
        deleteItemPosition=getIntent().getIntExtra("delete_position",0);
        if(deleteCode==1)   //执行删除操作
            DeleteEvent(deleteItemPosition); //自定义删除操作函数



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventSource.save();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case REQUEST_CODE_NEW_EVENT:
                if (resultCode == RESULT_OK) {

                    Event event1= (Event) data.getSerializableExtra("data.txt");
                    events.add(event1);
                    theAdapter.notifyDataSetChanged();

                }
                break;
            case REQUEST_CODE_DETAILS:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("edit_position", 0);

                    Event event3= (Event) data.getSerializableExtra("data3.txt");
                    events.set(position,event3);
                    theAdapter.notifyDataSetChanged();
                }
                break;
        }

    }

    private void InitData()
    {
        /*
        events.add(new Event("Birthday1","2001","无"));
        events.add(new Event(R.drawable.a1,"2DAYS","bir2","2003","无"));
        events.add(new Event(R.drawable.a1,"ADAYS","birth4","1999","无"));

         */
    }

    private void DeleteEvent(int position)
    {
        events.remove(position);
        theAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "delete successfully！", Toast.LENGTH_SHORT).show();
        deleteCode=0;   //还原
    }

    public class TimesArrayAdapter extends ArrayAdapter<Event>
    {
        private  int resourceId;
        public TimesArrayAdapter(@NonNull MainActivity context, @LayoutRes int resource, @NonNull ArrayList<Event> objects) {
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

            Event event_item = this.getItem(position);
            pic.setImageResource(R.drawable.a1);
            remain_time.setText(event_item.getRemain_time());
            name.setText(event_item.getName());
            date.setText(event_item.getDate().display_date_and_time());
            description.setText(event_item.getDescription());


            return item;
        }
    }

}
