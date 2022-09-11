package com.techkiii;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.techkiii.R;

public class SlowMotionActivity extends AppCompatActivity {

    VideoView videoview;
    ImageView ivSave,ivCancel;
    String viewSource = "/storage/sdcard1/video/this_is_the_end_trailer.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slow_motion);


        ivSave = findViewById(R.id.iv_save);
        ivCancel = findViewById(R.id.iv_cancel);

        Intent intent = getIntent();
        viewSource = intent.getStringExtra("recordFile");
        videoview = (VideoView)findViewById(R.id.videoview);
        videoview.setVideoURI(Uri.parse(viewSource));
        videoview.start();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                //Toast.makeText(SlowMotionActivity.this, "Store Video At : " + viewSource, Toast.LENGTH_SHORT).show();
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoview.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoview.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoview.pause();
        finish();
    }
}