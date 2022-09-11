package com.techkiii.showimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.techkiii.Camera.CameraActivity;
import com.techkiii.R;

import org.wysaid.myUtils.ImageUtil;

import java.io.UnsupportedEncodingException;

public class ShowImageActivity extends Activity {

    Bitmap bmp;
    ImageView ivSave;
    ImageView ivCancel;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image2);

        initialize();
        initData();
        initListener();
    }

    private void initialize() {
        ivSave = findViewById(R.id.iv_save);
        ivCancel = findViewById(R.id.iv_cancel);
        ivImage = findViewById(R.id.iv_image);
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("BitmapImage");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        byte [] buf = byteArray;
        String s = null;
        try {
            s = new String(buf, "UTF-8");
            Uri uri = Uri.parse(s);
            ivImage.setImageURI(uri);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //ivImage.setImageBitmap(bmp);
    }


    private void initListener() {
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bmp != null) {
                    String s = ImageUtil.saveBitmap(bmp);
                    bmp.recycle();
                    Toast.makeText(ShowImageActivity.this,"Save picture success!",Toast.LENGTH_LONG).show();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
                    startActivity(new Intent(ShowImageActivity.this, CameraActivity.class));
                } else
                    Toast.makeText(ShowImageActivity.this,"Save picture failed!",Toast.LENGTH_LONG).show();
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowImageActivity.this, CameraActivity.class));
            }
        });
    }
}