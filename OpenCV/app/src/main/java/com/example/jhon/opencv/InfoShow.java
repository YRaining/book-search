package com.example.jhon.opencv;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoShow extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private List<Map<String,Object>> mData;
    private String[] bookPriceStr={};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_show);

        TextView bookName=(TextView)findViewById(R.id.bookName);
        TextView bookInfo=(TextView)findViewById(R.id.bookInfo);
        ListView list=(ListView)findViewById(R.id.list);
        ImageView bookSample=(ImageView)findViewById(R.id.bookSample);

        Intent intent=getIntent();
        String bookNum=intent.getStringExtra("bookNum");
      
        verifyStoragePermissions(this);
        
        File sdCardDir = Environment.getExternalStorageDirectory();
        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inSampleSize=1;
        //Bitmap bookBitmap = BitmapFactory.decodeFile(sdCardDir.getPath()+"/bookData/pic/"+bookNum+".jpg",opts);
        Bitmap bookBitmap = BitmapFactory.decodeFile("/storage/sdcard1/bookData/pic/"+bookNum+".jpg",opts);
        //Bitmap bookBitmap=(Bitmap)BitmapFactory.decodeResource(getResources(),R.drawable.csapp,opts);
        bookSample.setImageBitmap(bookBitmap);

        //bookNmae.setText("深入理解计算机系统");
        //bookInfo.setText("Randal E.Bryant/David O'Hallaron\n机械工业出版社\n2014-11-1");
        String bookNameStr="";
        String bookInfoStr="";

        try{
            FileInputStream fis = new FileInputStream("/storage/sdcard1/bookData/info/"+bookNum+".txt");
            BufferedReader br = new BufferedReader(new
                    InputStreamReader(fis));

            bookNameStr=br.readLine();
            bookInfoStr=br.readLine();
            bookPriceStr=br.readLine().split("#");
            fis.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        bookName.setText(bookNameStr);
        bookInfo.setText(bookInfoStr.replace("#","\n"));

        mData = getData();
        MyAdapter adapter = new MyAdapter(this);
        list.setAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("price",  bookPriceStr[0]);
        map.put("img", R.drawable.j1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("price", bookPriceStr[1]);
        map.put("img", R.drawable.j2);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("price", bookPriceStr[2]);
        map.put("img", R.drawable.j3);
        list.add(map);

        return list;
    }

    

    public final class ViewHolder{
        public ImageView img;
        public TextView price;
        public Button viewBtn;
    }

    public void showInfo(int storeNum,String price){
        String storeName="";
        switch (storeNum){
            case 0:storeName="亚马逊";
                break;
            case 1:storeName="当当";
                break;
            case 2:storeName="京东";
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle("购买确认")
                .setMessage("将为您在"+storeName+"以"+price+"购买该书？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "订单成功",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();


    }
    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {

                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.vlist2, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                holder.price = (TextView)convertView.findViewById(R.id.price);
                //holder.info = (TextView)convertView.findViewById(R.id.info);
                holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
                //holder.viewBtn = (ImageButton)convertView.findViewById(R.id.view_btn);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            final int itmun=position;
            holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
            holder.price.setText((String) mData.get(position).get("price"));
            //holder.info.setText((String) mData.get(position).get("info"));
            //Toast.makeText(getApplicationContext(), (String)mData.get(position).get("info"),
            // Toast.LENGTH_SHORT).show();
            holder.viewBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showInfo(itmun,(String) mData.get(itmun).get("price"));
                }
            });


            return convertView;
        }
    }
}
