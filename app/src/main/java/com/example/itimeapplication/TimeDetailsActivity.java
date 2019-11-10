package com.example.itimeapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.itimeapplication.data.model.Time;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimeDetailsActivity extends AppCompatActivity {

    private ImageView imageViewPicture;
    private TextView textViewName,textViewDate;
    private String timeDescription;
    private int position;
    private FloatingActionButton fabBack,fabEdit,fabDelete;
    private static final int REQUEST_CODE_UPDATE_TIME = 202;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)      //取消标题栏
            getSupportActionBar().hide();
        setContentView(R.layout.activity_time_details);

        position = getIntent().getIntExtra("time_position", 0);
        imageViewPicture = findViewById(R.id.image_view_det_pic);
        textViewName = findViewById(R.id.text_view_det_name);
        textViewName.setText(getIntent().getStringExtra("time_name"));
        textViewDate = findViewById(R.id.text_view_det_date);
        textViewDate.setText(getIntent().getStringExtra("time_date"));
        timeDescription=getIntent().getStringExtra("time_description");


        fabBack = findViewById(R.id.fab_det_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                //将要传递的值附加到Intent对象
                intent.putExtra("edit_position",position);
                intent.putExtra("time_name",textViewName.getText().toString().trim());
                intent.putExtra("time_description",timeDescription);
                setResult(RESULT_OK,intent);

                TimeDetailsActivity.this.finish();
            }
        });

        fabEdit=findViewById(R.id.fab_det_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TimeDetailsActivity.this, EditTimeActivity.class);
                intent.putExtra("time_name",textViewName.getText().toString().trim());
                intent.putExtra("time_date",textViewDate.getText().toString().trim());
                intent.putExtra("time_description",timeDescription);

                startActivityForResult(intent,REQUEST_CODE_UPDATE_TIME);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_TIME) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("time_name");

                timeDescription = data.getStringExtra("time_description");

                textViewName.setText(name);

            }
        }
    }

}