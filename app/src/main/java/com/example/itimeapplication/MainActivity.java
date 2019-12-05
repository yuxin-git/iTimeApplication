package com.example.itimeapplication;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itimeapplication.data.EventSource;
import com.example.itimeapplication.data.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

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

    private ActionBarDrawerToggle drawerbar;
    public DrawerLayout drawerLayout;
    private RelativeLayout main_left_drawer_layout;

    private AppBarConfiguration mAppBarConfiguration;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
       Toolbar toolbar = findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

       NavigationView navigationView = findViewById(R.id.nav_view);
       drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

       //设置菜单内容之外其他区域的背景色
       drawerLayout.setScrimColor(Color.TRANSPARENT);

       ActionBarDrawerToggle actionBarDrawerToggle=
               new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
       //同步状态，刷新页面
       drawerLayout.addDrawerListener(actionBarDrawerToggle);
       actionBarDrawerToggle.syncState();

       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               switch (menuItem.getItemId()) {
                   case R.id.nav_event:
                       drawerLayout.closeDrawers();
                       break;
                   case R.id.nav_color:



               }
               return false;
           }
       });




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

    //将原图裁剪成正方形，适应listView显示
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
        if(null == bitmap || edgeLength <= 0)
        {
            return  null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if(widthOrg > edgeLength && heightOrg > edgeLength)
        {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try{
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            }
            catch(Exception e){
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try{
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            }
            catch(Exception e){
                return null;
            }
        }

        return result;
    }


    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
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
            remain_time.setText(event_item.display_remian_time());
            name.setText(event_item.getName());
            date.setText(event_item.getDate().display_date_and_time());
            description.setText(event_item.getDescription());

            if(null!=event_item.getPictureFilePath()) {
                Bitmap bitmap = BitmapFactory.decodeFile(event_item.getPictureFilePath().toString());
                //将原图裁剪成正方形
                Bitmap newBitmap=centerSquareScaleBitmap(bitmap,210);
                Bitmap blurBitmap=doBlur(newBitmap,10, true);
                //把裁剪模糊后的图片展示出来
                pic.setImageBitmap(blurBitmap);
            }
            return item;
        }
    }

}
