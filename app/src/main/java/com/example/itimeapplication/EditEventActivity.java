package com.example.itimeapplication;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itimeapplication.data.model.OtherCondition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EditEventActivity extends AppCompatActivity {

    private EditText editTextName,editTextDescription;
    private FloatingActionButton fabBack,fabOk;

    private ArrayList<OtherCondition> otherConditions=new ArrayList<OtherCondition>();
    private ConditionsArrayAdapter theConditionAdapter;
    private int editPosition;
    ListView listViewCondition;

    private int repeat_day;     //保存周期的天数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)      //取消标题栏
            getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_event);

        editPosition=getIntent().getIntExtra("time_position",0);
        editTextName=findViewById(R.id.edit_text_name);
        editTextName.setText(getIntent().getStringExtra("time_name"));
        editTextDescription=findViewById(R.id.edit_text_description);
        editTextDescription.setText(getIntent().getStringExtra("time_description"));
        fabBack=findViewById(R.id.fab_edit_back);
        fabOk=findViewById(R.id.fab_edit_ok);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditEventActivity.this.finish();
            }
        });

        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextName.getText().toString().isEmpty())     //如果未输入name
                    Toast.makeText(getApplicationContext(), "You did not enter an event name!", Toast.LENGTH_LONG).show();
                else{
                    Intent intent=new Intent();
                    //将要传递的值附加到Intent对象
                    intent.putExtra("edit_position",editPosition);
                    intent.putExtra("time_name",editTextName.getText().toString().trim());
                    intent.putExtra("time_description",editTextDescription.getText().toString().trim());
                    setResult(RESULT_OK,intent);

                    EditEventActivity.this.finish();
                }

            }
        });

        InitData();
        theConditionAdapter=new ConditionsArrayAdapter(this,R.layout.list_item_extra_condition, otherConditions);

        listViewCondition= this.findViewById(R.id.list_view_conditions);
        listViewCondition.setAdapter(theConditionAdapter);

        listViewCondition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String m= String.valueOf(i);    //调试使用
                Log.i("点击了一个item ", m);
                if(i==0){       //选择日期
                    //待实现，需要自定义一个AlertDialog
                }
                if(i==1){  //选择周期
                    String[] repeat = new String[]{"Weak","Month","Year","Custom","None"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
                    builder.setTitle("Repeat");
                    builder.setItems(repeat, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String msg=String.valueOf(which);   //调试
                            Log.i("点击了周期: ", msg);
                            if(which==0)    //week
                                repeat_day=7;
                            if(which==1)    //month
                                repeat_day=30;
                            if(which==2)    //year
                                repeat_day=365;
                            if(which==3)    //自定义天数，再创建一个AlertDialog
                            {
                                final EditText et = new EditText(EditEventActivity.this);
                                et.setHint("enter Period(days)");
                                et.setInputType(InputType.TYPE_CLASS_NUMBER);   //设置输入文本框数据为数字
                                new AlertDialog.Builder(EditEventActivity.this).setTitle("Reapeat")
                                        .setView(et)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                String input = et.getText().toString();
                                                if (input.equals("")) {
                                                    Toast.makeText(getApplicationContext(), "Input can't be empty！", Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    repeat_day= Integer.parseInt(input);    //将String转化为int
                                                }
                                            }
                                        })
                                        .setNegativeButton("CANCEL", null)
                                        .show();

                            }
                        }
                    });
                    builder.create().show();
                }
                if(i==2){   //选择图片

                }
                if(i==3){   //添加标签

                }
            }
        });
    }

    private void InitData()
    {
        otherConditions.add(new OtherCondition(R.drawable.clock,"Date","Long press to use Date Calculator"));
        otherConditions.add(new OtherCondition(R.drawable.repeat,"Repeat","None"));
        otherConditions.add(new OtherCondition(R.drawable.picture,"Picture",""));
        otherConditions.add(new OtherCondition(R.drawable.label,"Add Label",""));
    }

    public class ConditionsArrayAdapter extends ArrayAdapter<OtherCondition>
    {
        private  int resourceId;
        public ConditionsArrayAdapter(@NonNull EditEventActivity context, @LayoutRes int resource, @NonNull ArrayList<OtherCondition> objects) {
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


            OtherCondition condition_item = this.getItem(position);
            con_pic.setImageResource(condition_item.getCon_picture());
            con_name.setText(condition_item.getCon_name());
            explain.setText(condition_item.getCon_explain());

            return item;
        }
    }
}
