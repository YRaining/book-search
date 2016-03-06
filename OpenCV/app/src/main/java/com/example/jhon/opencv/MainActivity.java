package com.example.jhon.opencv;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private Button button;
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt=(TextView)findViewById(R.id.textview);
        img=(ImageView)findViewById(R.id.img);
        button=(Button)findViewById(R.id.button);

        verifyStoragePermissions(this);
        
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent capIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri = Uri.fromFile(getTempImage());
                capIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(capIntent, 100);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                //显示图片到ImageView上

                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize=5;
                Bitmap bitmap = BitmapFactory.decodeFile(getTempImage().getPath(),options);
                img.setImageBitmap(bitmap);
                if(bitmap!=null){
                    Toast.makeText(MainActivity.this,"拍照成功", Toast.LENGTH_SHORT).show();
                    final int w = bitmap.getWidth(), h = bitmap.getHeight();
                    final int[] pix = new int[w * h];
                    bitmap.getPixels(pix, 0, w, 0, 0, w, h);
                    new Thread(new Runnable() {
                        public void run() {
                            final String booknum = OpenCVHelper.gray(pix, w, h);
                            /*txt.post(new Runnable() {
                                @Override
                                public void run() {
                                    txt.setText(booknum);
                                }
                            });*/
                            if(booknum.equals("无匹配结果")) {
                                txt.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        txt.setText(booknum);
                                    }
                                });
                            }
                            else{
                            Intent intent=new Intent(MainActivity.this,InfoShow.class);
                           intent.putExtra("bookNum",booknum);
                            startActivity(intent);
                            }
                        }
                    }).start();
                }
            } else if(resultCode == RESULT_CANCELED){
                //提示用户未拍照成功
                Toast.makeText(MainActivity.this,"拍照未成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
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

    public static File getTempImage() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return tempFile;
        }
        return null;
    }
}
