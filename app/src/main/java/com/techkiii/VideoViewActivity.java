package com.techkiii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import org.wysaid.myUtils.MsgUtil;

import com.techkiii.Camera.CameraActivity;

import com.techkiii.R;

public class VideoViewActivity extends AppCompatActivity {

    VideoView videoview;
    ImageView ivSave,ivCancel;
    String viewSource = "/storage/sdcard1/video/this_is_the_end_trailer.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        ivSave = findViewById(R.id.iv_save);
        ivCancel = findViewById(R.id.iv_cancel);

        Intent intent = getIntent();
        viewSource = intent.getStringExtra("recordFile");
        videoview = (VideoView)findViewById(R.id.videoview);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoview);
        videoview.setVideoURI(Uri.parse(viewSource));
        videoview.setMediaController(mediaController);
        videoview.requestFocus();
        videoview.start();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                //Toast.makeText(VideoViewActivity.this, "Store Video At : " + viewSource, Toast.LENGTH_SHORT).show();
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
                String recording = "Recorded as: " + viewSource;
                MsgUtil.toastMsg(VideoViewActivity.this, "Video Save Successfully");
                Intent intent1 = new Intent(VideoViewActivity.this, CameraActivity.class);
                startActivity(intent1);
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(VideoViewActivity.this, CameraActivity.class);
                startActivity(intent1);
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