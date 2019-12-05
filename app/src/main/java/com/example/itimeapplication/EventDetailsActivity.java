package com.example.itimeapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.itimeapplication.data.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView imageViewPicture;
    private TextView textViewName,textViewDate;
    private String timeDescription;
    private int position;
    private FloatingActionButton fabBack,fabEdit,fabDelete;
    private static final int REQUEST_CODE_UPDATE_EVENT= 202;
    private static final int REQUEST_CODE_DELETE_EVENT = 204;
    private Event thisEvent;

    private TextView textViewDays;
    private TextView textViewHours;
    private TextView textViewMunites;
    private TextView textViewSeconds;

    Calendar calendar1,calendar2;
    int remain_time_s;    //保存事件时间和当前时间的差（秒为单位）


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)      //取消标题栏
            getSupportActionBar().hide();
        setContentView(R.layout.activity_event_details);

        position = getIntent().getIntExtra("time_position", 0);
        thisEvent= (Event) getIntent().getSerializableExtra("data2.txt");
        imageViewPicture = findViewById(R.id.image_view_det_pic);
        if(null!=thisEvent.getPictureFilePath()) {
            Bitmap bitmap = BitmapFactory.decodeFile(thisEvent.getPictureFilePath().toString());
            //把裁剪后的图片展示出来
            imageViewPicture.setImageBitmap(bitmap);
        }
        textViewName = findViewById(R.id.text_view_dis_name);
        textViewName.setText(thisEvent.getName());
        textViewDate = findViewById(R.id.text_view_dis_date);
        textViewDate.setText(thisEvent.getDate().display_date_and_time());

        //绑定倒计时的时分秒textview
        textViewDays=findViewById(R.id.text_view_dis_day);
        textViewHours=findViewById(R.id.text_view_dis_hour);
        textViewMunites=findViewById(R.id.text_view_dis_minute);
        textViewSeconds=findViewById(R.id.text_view_dis_second);


        calendar2=Calendar.getInstance();
        calendar2.set(thisEvent.getDate().getYear(),thisEvent.getDate().getMonth(),thisEvent.getDate().getDay(),
                thisEvent.getDate().getHour(), thisEvent.getDate().getMinute(),thisEvent.getDate().getSecond());


        //创建一个线程用于倒计时
        new Thread(new MyRunnable()).start();

        fabBack = findViewById(R.id.fab_det_back);    //返回按钮
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                //将要传递的值附加到Intent对象
                intent.putExtra("edit_position",position);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("data3.txt",thisEvent);
                intent.putExtras(mBundle);

                setResult(RESULT_OK,intent);
                EventDetailsActivity.this.finish();


            }
        });

        fabEdit=findViewById(R.id.fab_det_edit);    //编辑按钮
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EventDetailsActivity.this, EditEventActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("data4.txt",thisEvent);
                intent.putExtras(mBundle);
                intent.putExtra("edit_code",1);

                startActivityForResult(intent,REQUEST_CODE_UPDATE_EVENT);
            }
        });

        fabDelete=findViewById(R.id.fab_det_delete);    //删除按钮
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
                builder.setTitle("Do you want to delete this event?");
                builder.setIcon(R.drawable.ic_warning);
                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(EventDetailsActivity.this, MainActivity.class);
                        intent.putExtra("delete_code",1);  //传递一个删除码
                        intent.putExtra("delete_position",position);  //将删除位置传递
                        startActivityForResult(intent,REQUEST_CODE_DELETE_EVENT);
                    }
                });
                //设置反面按钮
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();

            }
        });
    }

    final Handler handler = new Handler(){     // handle
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    Log.i("测试：","运行到handler");
                    calendar1=Calendar.getInstance();
                    long time1 = calendar1.getTimeInMillis();
                    long time2 = calendar2.getTimeInMillis();
                    remain_time_s=(int)((time2-time1)/1000);
                    if(remain_time_s<0)
                        remain_time_s=-remain_time_s;
                    int betweenDays=remain_time_s/(3600*24);
                    int betweenHours=remain_time_s/(3600)-betweenDays*24;
                    int betweenMunites=remain_time_s/(60)-betweenDays*24*60-betweenHours*60;
                    int betweenSeconds=remain_time_s-betweenDays*24*3600-betweenHours*3600-betweenMunites*60;

                    textViewDays.setText(String.valueOf(betweenDays));
                    textViewHours.setText(String.valueOf(betweenHours));
                    textViewMunites.setText(String.valueOf(betweenMunites));
                    textViewSeconds.setText(String.valueOf(betweenSeconds));

            }
            super.handleMessage(msg);
        }
    };

    public class MyRunnable implements Runnable{   // thread
        @Override
        public void run(){
            while(true){
                try{
                    Thread.sleep(1000);   // sleep 1000ms
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }catch (Exception e) {
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_EVENT) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("edit_position", 0);
                Event event5= (Event) data.getSerializableExtra("data5.txt");
                thisEvent=event5;

                textViewName.setText(event5.getName());
                Bitmap bitmap = BitmapFactory.decodeFile(event5.getPictureFilePath().toString());
                imageViewPicture.setImageBitmap(bitmap);


            }
        }
    }

}
