package com.example.itimeapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView imageViewPicture;
    private TextView textViewName,textViewDate;
    private String timeDescription;
    private int position;
    private FloatingActionButton fabBack,fabEdit,fabDelete;
    private static final int REQUEST_CODE_UPDATE_EVENT= 202;
    private static final int REQUEST_CODE_DELETE_EVENT = 204;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)      //取消标题栏
            getSupportActionBar().hide();
        setContentView(R.layout.activity_event_details);

        position = getIntent().getIntExtra("time_position", 0);
        imageViewPicture = findViewById(R.id.image_view_det_pic);
        textViewName = findViewById(R.id.text_view_det_name);
        textViewName.setText(getIntent().getStringExtra("time_name"));
        textViewDate = findViewById(R.id.text_view_det_date);
        textViewDate.setText(getIntent().getStringExtra("time_date"));
        timeDescription=getIntent().getStringExtra("time_description");


        fabBack = findViewById(R.id.fab_det_back);    //返回按钮
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                //将要传递的值附加到Intent对象
                intent.putExtra("edit_position",position);
                intent.putExtra("time_name",textViewName.getText().toString().trim());
                intent.putExtra("time_description",timeDescription);
                setResult(RESULT_OK,intent);

                EventDetailsActivity.this.finish();
            }
        });

        fabEdit=findViewById(R.id.fab_det_edit);    //编辑按钮
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EventDetailsActivity.this, EditEventActivity.class);
                intent.putExtra("time_name",textViewName.getText().toString().trim());
                intent.putExtra("time_date",textViewDate.getText().toString().trim());
                intent.putExtra("time_description",timeDescription);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_EVENT) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("time_name");

                timeDescription = data.getStringExtra("time_description");

                textViewName.setText(name);

            }
        }
    }

}
