package com.example.itimeapplication;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.itimeapplication.data.model.Event;
import com.example.itimeapplication.data.model.EventDate;
import com.example.itimeapplication.data.model.OtherCondition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class EditEventActivity extends AppCompatActivity {

    Event thisEvent;    //保存本次编辑的倒计时事件

    private static final int CHOICE_FROM_ALBUM_REQUEST_CODE = 208;
    private static final int CROP_PHOTO_REQUEST_CODE = 209;
    private static final int SER_KEY = 219;
    private EditText editTextName, editTextDescription;
    private FloatingActionButton fabBack, fabOk;

    private ArrayList<OtherCondition> otherConditions = new ArrayList<OtherCondition>();
    private ConditionsArrayAdapter theConditionAdapter;
    private int editPosition;
    ListView listViewCondition;
    String condition_date_explain;
    String condition_repeat_explain;

    private int repeat_day=0;     //保存周期的天数
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private EventDate mDate;

    private ImageView imageViewPic;

    //拍照功能参数
    private static final int CHOOSE_PHOTO = 301;
    private final static int CROP_IMAGE = 302;
    //imageUri照片真实路径
    private Uri imageUri;
    //照片存储
    File filePath;

    private int editCode;

    //private Bitmap myBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.a1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)      //取消标题栏
            getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_event);

        imageViewPic = findViewById(R.id.image_view_pic);

        //获取当前时间,并将mDate初始化为当前时间
        calendar = Calendar.getInstance();
        mDate = new EventDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        editCode=getIntent().getIntExtra("edit_code", 0);
        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);
        if(editCode==1) {   //若是修改编辑倒计时事件，则需要对一些数据初始赋值
            thisEvent = (Event) getIntent().getSerializableExtra("data4.txt");
            editPosition = getIntent().getIntExtra("time_position", 0);
            editTextName.setText(thisEvent.getName());
            editTextDescription.setText(thisEvent.getDescription());

            if(null!=thisEvent.getPictureFilePath()) {  //若事件设置了图片
                Bitmap bitmap = BitmapFactory.decodeFile(thisEvent.getPictureFilePath().toString());
                imageViewPic.setImageBitmap(bitmap);
            }

        }

        fabBack = findViewById(R.id.fab_edit_back);
        fabOk = findViewById(R.id.fab_edit_ok);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditEventActivity.this.finish();
            }
        });

        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextName.getText().toString().isEmpty())     //如果未输入name
                    Toast.makeText(getApplicationContext(), "You did not enter an event name!", Toast.LENGTH_LONG).show();
                else {
                    //将编辑的各个属性装入倒计时事件thisEvent中
                    thisEvent = new Event(editTextName.getText().toString().trim(), editTextDescription.getText().toString().trim()
                            , mDate, repeat_day, filePath);
                    if (editCode == 0)  //新建事件
                    {
                        Intent intent = new Intent(EditEventActivity.this,MainActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("data.txt", thisEvent);

                        intent.putExtras(mBundle);
                        setResult(RESULT_OK, intent);
                        EditEventActivity.this.finish();
                    }
                    else if(editCode==1)    //修改事件
                    {
                        Intent intent = new Intent(EditEventActivity.this,EventDetailsActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("data5.txt", thisEvent);
                        intent.putExtra("edit_position", editPosition);
                        intent.putExtras(mBundle);
                        setResult(RESULT_OK, intent);
                        EditEventActivity.this.finish();

                    }

                }

            }
        });

        condition_date_explain = "Long press to use Date Calculator";
        condition_repeat_explain = "None";
        InitData();
        theConditionAdapter = new ConditionsArrayAdapter(this, R.layout.list_item_extra_condition, otherConditions);

        listViewCondition = this.findViewById(R.id.list_view_conditions);
        listViewCondition.setAdapter(theConditionAdapter);

        //ListView中item的长按事件，只需实现Date的
        listViewCondition.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Log.i("长按", "Date"); //日志
                    showDateCalculator();
                }
                return true;
            }
        });

        //ListView中各item的点击事件
        listViewCondition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String m = String.valueOf(i);    //调试使用
                Log.i("点击了一个item ", m);
                if (i == 0) {       //选择日期
                    showDailog();
                }
                if (i == 1) {  //选择周期
                    showRepeat();
                }
                if (i == 2) {   //选择图片
                    openAlbum();
                }
                if (i == 3) {   //添加标签

                }
            }
        });
    }

    //打开相册
    private void openAlbum() {
        Intent intent_album = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent_album, CHOOSE_PHOTO);
    }

    //剪切图片
    private void startImageCrop(File saveToFile, Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        Log.i("测试：", "startImageCrop: " + "执行到压缩图片了" + "uri is " + uri);
        intent.setDataAndType(uri, "image/*");//设置Uri及类型
        //uri权限，如果不加的话会产生无法加载图片的问题
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //进行剪切的一些设置
        intent.putExtra("crop", "true");//
        intent.putExtra("aspectX", 460);//X方向上的比例
        intent.putExtra("aspectY", 250);//Y方向上的比例
        intent.putExtra("outputX", 460);//裁剪区的X方向宽
        intent.putExtra("outputY", 250);//裁剪区的Y方向宽
        intent.putExtra("scale", true);//是否保留比例
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("return-data", false);//是否将数据保留在Bitmap中返回dataParcelable相应的Bitmap数据，防止造成OOM，置位false
        //判断文件是否存在
        if (!saveToFile.getParentFile().exists()) {
            saveToFile.getParentFile().mkdirs();
        }
        //将剪切后的图片存储到此文件
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveToFile));
        Log.i("测试：", "startImageCrop: " + "即将跳到剪切图片");
        startActivityForResult(intent, CROP_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //选中相册照片显示
                    Log.i("测试：", "onActivityResult: 执行到打开相册了");
                    try {
                        imageUri = data.getData(); //获取系统返回的照片的Uri
                        Log.i("测试：", "onActivityResult: uriImage is " + imageUri);

                        //设置照片存储文件及剪切图片
                        String name = DateFormat.format("eventBackground"+imageUri, Calendar.getInstance(Locale.CHINA)) + ".png";
                        Log.i("测试：", " name : " + name);
                        //定义图片存放的位置
                        filePath = new File(EditEventActivity.this.getExternalCacheDir(), name);

                        startImageCrop(filePath, imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_IMAGE:
                if (resultCode == RESULT_OK) {
                    Log.i("Test", "onActivityResult: CROP_IMAGE" + "进入了CROP");
                    //通过FileName拿到图片
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath.toString());
                    //把裁剪后的图片展示出来
                    imageViewPic.setImageBitmap(bitmap);



                }
                break;
            default:
                break;
        }
    }


    private void InitData() {
        otherConditions.add(new OtherCondition(R.drawable.clock, "Date", condition_date_explain));
        otherConditions.add(new OtherCondition(R.drawable.repeat, "Repeat", condition_repeat_explain));
        otherConditions.add(new OtherCondition(R.drawable.picture, "Picture", ""));
        otherConditions.add(new OtherCondition(R.drawable.label, "Add Label", ""));
    }

    private void showDailog() {     //日历弹出选择框
        calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(EditEventActivity.this, R.style.Theme_AppCompat_Light_Dialog,
                null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = datePickerDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                Log.d("测试：year", Integer.toString(year));
                Log.d("测试：month", Integer.toString(month));
                Log.d("测试：day", Integer.toString(day));
                mDate.setYear(year);
                mDate.setMonth(month);
                mDate.setDay(day);

                //修改详情界面Date的描述
                condition_date_explain = mDate.display_date();
                otherConditions.set(0, new OtherCondition(R.drawable.clock, "Date", condition_date_explain));
                theConditionAdapter.notifyDataSetChanged();

                showTime();  //继续设置时间
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        datePickerDialog.show();

    }

    private void showTime() {       //时间弹出选择框
        timePickerDialog = new TimePickerDialog(this, R.style.Theme_AppCompat_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("测试：hour", Integer.toString(hourOfDay));
                Log.d("测试：minite", Integer.toString(minute));
                mDate.setHour(hourOfDay);
                mDate.setMinute(minute);

                //修改详情界面Date的描述
                condition_date_explain = mDate.display_date_and_time();
                otherConditions.set(0, new OtherCondition(R.drawable.clock, "Date", condition_date_explain));
                theConditionAdapter.notifyDataSetChanged();

            }
        },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showDateCalculator() {
        final Calendar[] calCalendar = {Calendar.getInstance()};    //获取当前时间
        final EventDate afterDate = new EventDate(calCalendar[0].get(Calendar.YEAR), calCalendar[0].get(Calendar.MONTH),
                calCalendar[0].get(Calendar.DAY_OF_MONTH), calCalendar[0].get(Calendar.HOUR),
                calCalendar[0].get(Calendar.MINUTE), calCalendar[0].get(Calendar.SECOND));
        final EventDate beforeDate = new EventDate(calCalendar[0].get(Calendar.YEAR), calCalendar[0].get(Calendar.MONTH),
                calCalendar[0].get(Calendar.DAY_OF_MONTH), calCalendar[0].get(Calendar.HOUR),
                calCalendar[0].get(Calendar.MINUTE), calCalendar[0].get(Calendar.SECOND));
        final View alertDialogView = getLayoutInflater().inflate(R.layout.alertdialog_calculator_layout, null, false);
        final AlertDialog.Builder inputDayAlertDialog = new AlertDialog.Builder(EditEventActivity.this);
        inputDayAlertDialog.setView(alertDialogView);

        TextView textViewCalendar = alertDialogView.findViewById(R.id.text_view_calendar);
        textViewCalendar.setText(afterDate.display_date());
        final TextView textViewAfter = alertDialogView.findViewById(R.id.text_view_after);
        final TextView textViewBefore = alertDialogView.findViewById(R.id.text_view_before);
        textViewAfter.setText("days after: " + afterDate.display_date());
        textViewBefore.setText("days before: " + afterDate.display_date());
        final EditText editTextAfter = alertDialogView.findViewById(R.id.edit_text_after);
        final EditText editTextBefore = alertDialogView.findViewById(R.id.edit_text_before);

        editTextAfter.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {

                Log.i("editText:",editTextAfter.getEditableText().toString());
                int valueAfter=0;
                if (!editTextAfter.getText().toString().equals("")&& arg2.getAction() == KeyEvent.ACTION_UP) {
                    valueAfter = Integer.parseInt(editTextAfter.getText().toString());
                }
                calCalendar[0] = Calendar.getInstance();
                calCalendar[0].add(Calendar.DAY_OF_MONTH, valueAfter);
                afterDate.setYear(calCalendar[0].get(Calendar.YEAR));
                afterDate.setMonth(calCalendar[0].get(Calendar.MONTH));
                afterDate.setDay(calCalendar[0].get(Calendar.DAY_OF_MONTH));
                Log.i("calDate.day", String.valueOf(afterDate.getDay()));
                textViewAfter.setText("days after: " + afterDate.display_date());

                return false;
            }
        });

        editTextBefore.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {

                Log.i("editText:",editTextAfter.getEditableText().toString());
                int valueBefore=0;
                if (!editTextBefore.getText().toString().equals("")&& arg2.getAction() == KeyEvent.ACTION_UP) {
                    valueBefore = Integer.parseInt(editTextBefore.getText().toString());
                }
                calCalendar[0] = Calendar.getInstance();
                calCalendar[0].add(Calendar.DAY_OF_MONTH, -valueBefore);
                beforeDate.setYear(calCalendar[0].get(Calendar.YEAR));
                beforeDate.setMonth(calCalendar[0].get(Calendar.MONTH));
                beforeDate.setDay(calCalendar[0].get(Calendar.DAY_OF_MONTH));
                Log.i("calDate.day", String.valueOf(beforeDate.getDay()));
                textViewBefore.setText("days before: " + beforeDate.display_date());

                return false;
            }
        });

        inputDayAlertDialog.setNegativeButton ("CANCEL", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "CANCEL！", Toast.LENGTH_LONG).show();

            }
        });

        final AlertDialog dialog = inputDayAlertDialog.show ();

        Button buttonAfterPick;
        buttonAfterPick=alertDialogView.findViewById(R.id.button_after);
        buttonAfterPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDate.setYear(afterDate.getYear());
                mDate.setMonth(afterDate.getMonth());
                mDate.setDay(afterDate.getDay());
                dialog.dismiss();
                showTime();
            }
        });

        Button buttonBeforePick;
        buttonBeforePick=alertDialogView.findViewById(R.id.button_before);
        buttonBeforePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDate.setYear(beforeDate.getYear());
                mDate.setMonth(beforeDate.getMonth());
                mDate.setDay(beforeDate.getDay());
                dialog.dismiss();
                showTime();
            }
        });


    }

    private void showRepeat()
    {
        String[] repeat = new String[]{"Weak","Month","Year","Custom","None"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
        builder.setTitle("Repeat");
        builder.setItems(repeat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg=String.valueOf(which);   //调试
                Log.i("点击了周期: ", msg);
                if(which==0) {    //week
                    repeat_day = 7;
                    condition_repeat_explain="Week";
                    otherConditions.set(1,new OtherCondition(R.drawable.repeat,"Repeat",condition_repeat_explain));
                    theConditionAdapter.notifyDataSetChanged();
                }
                if(which==1) {    //month
                    repeat_day = 30;
                    condition_repeat_explain="Month";
                    otherConditions.set(1,new OtherCondition(R.drawable.repeat,"Repeat",condition_repeat_explain));
                    theConditionAdapter.notifyDataSetChanged();
                }
                if(which==2) {    //year
                    repeat_day = 365;
                    condition_repeat_explain = "Year";
                    otherConditions.set(1, new OtherCondition(R.drawable.repeat, "Repeat", condition_repeat_explain));
                    theConditionAdapter.notifyDataSetChanged();
                }
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
                                        condition_repeat_explain=repeat_day+"DAYS LEFT";
                                        otherConditions.set(1,new OtherCondition(R.drawable.repeat,"Repeat",condition_repeat_explain));
                                        theConditionAdapter.notifyDataSetChanged();
                                    }
                                }
                            })
                            .setNegativeButton("CANCEL", null)
                            .show();


                }
                if(which==4) {    //不重复
                    repeat_day = 0;
                    condition_repeat_explain="None";
                    otherConditions.set(1,new OtherCondition(R.drawable.repeat,"Repeat",condition_repeat_explain));
                    theConditionAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.create().show();

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
