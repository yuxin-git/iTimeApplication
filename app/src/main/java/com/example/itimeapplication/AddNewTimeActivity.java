package com.example.itimeapplication;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.itimeapplication.data.model.Condition;

import java.util.ArrayList;

public class AddNewTimeActivity extends AppCompatActivity {

    private EditText editTextName,editTextDescription;
    private Button buttonBack,buttonOk;

    private ArrayList<Condition> conditions=new ArrayList<Condition>();
    private ConditionsArrayAdapter theConditionAdapter;
    ListView listViewCondition;

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

        InitData();
        theConditionAdapter=new ConditionsArrayAdapter(this,R.layout.list_item_extra_condition, conditions);

        listViewCondition= this.findViewById(R.id.list_view_conditions);
        listViewCondition.setAdapter(theConditionAdapter);
    }

    private void InitData()
    {
        conditions.add(new Condition(R.drawable.con1,"    Date","       Long press to use Date Calculator"));
        conditions.add(new Condition(R.drawable.con2,"    Repeat","       None"));
        conditions.add(new Condition(R.drawable.con3,"    Picture",""));
        conditions.add(new Condition(R.drawable.con4,"    Add Label",""));
    }

    public class ConditionsArrayAdapter extends ArrayAdapter<Condition>
    {
        private  int resourceId;
        public ConditionsArrayAdapter(@NonNull AddNewTimeActivity context, @LayoutRes int resource, @NonNull ArrayList<Condition> objects) {
            super(context, resource, objects);
            resourceId=resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View item = mInflater.inflate(this.resourceId,null);

            ImageView con_pic=(ImageView)item.findViewById(R.id.image_view_pic);
            TextView con_name = (TextView)item.findViewById(R.id.text_view_name);
            TextView explain = (TextView)item.findViewById(R.id.text_view_explain);


            Condition condition_item = this.getItem(position);
            con_pic.setImageResource(condition_item.getCon_picture());
            con_name.setText(condition_item.getCon_name());
            explain.setText(condition_item.getCon_explain());

            return item;
        }
    }
}
