package com.example.itimeapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewTimeActivity extends AppCompatActivity {

    private EditText editTextName,editTextDescription;
    private Button buttonBack,buttonOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_time);

        editTextName=findViewById(R.id.edit_text_name);
        editTextDescription=findViewById(R.id.edit_text_description);
        buttonBack=findViewById(R.id.button_back);
        buttonOk=findViewById(R.id.button_ok);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTimeActivity.this.finish();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                //将要传递的值附加到Intent对象
                intent.putExtra("time_name",editTextName.getText().toString().trim());
                intent.putExtra("time_description",editTextDescription.getText().toString().trim());
                setResult(1,intent);

                AddNewTimeActivity.this.finish();
            }
        });
    }
}
