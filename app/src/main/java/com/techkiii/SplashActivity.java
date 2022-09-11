package com.techkiii;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import com.techkiii.Camera.CameraActivity;

import com.techkiii.R;

public class SplashActivity extends Activity {
    private ProgressBar mProgress;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean isOpenFisrtTime = false;
    Boolean isFirstTime;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= 23 && !(checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0 && checkSelfPermission("android.permission.CAMERA") == 0 && checkSelfPermission("android.permission.RECORD_AUDIO") == 0)) {
            permissionDialog();
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        isFirstTime = preferences.getBoolean("isFirstTime", false);
        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);
        getWindow().setFlags(1024, 1024);

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(findViewById(R.id.icon), "rotationY", new float[]{0.0f, 180.0f});
        animation1.setDuration(2000);
        animation1.setInterpolator(new AccelerateDecelerateInterpolator());
        animation1.start();

        if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0 || checkSelfPermission("android.permission.CAMERA") == 0 || checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    mProgress.setProgress(100);

                    Intent i;
                    if (isFirstTime) {
                        i = new Intent(SplashActivity.this, CameraActivity.class);
                    } else {
                        //i = new Intent(SplashActivity.this, ComingSoonActivity.class);
                        i = new Intent(SplashActivity.this, CameraActivity.class);
                    }
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }
    }

    private void permissionDialog() {
        final Dialog dialog = new Dialog(this, 16974128);
        dialog.setContentView(R.layout.permissionsdialog);
        dialog.setTitle(getResources().getString(R.string.permission).toString());
        dialog.setCancelable(false);
        ((Button) dialog.findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    SplashActivity.this.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.RECORD_AUDIO"}, 922);
                }
                dialog.dismiss();
            }
        });
        if (this.isOpenFisrtTime) {
            Button setting = (Button) dialog.findViewById(R.id.settings);
            setting.setVisibility(View.VISIBLE);
            setting.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", SplashActivity.this.getPackageName(), null));
                    intent.addFlags(268435456);
                    SplashActivity.this.startActivityForResult(intent, 922);
                    dialog.dismiss();
                }
            });
        } else {
            this.isOpenFisrtTime = true;
        }
        dialog.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 922 && Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0 || checkSelfPermission("android.permission.CAMERA") != 0 || checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                permissionDialog();
            }else {
                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        mProgress.setProgress(100);

                        Intent i;
                        if (isFirstTime) {
                            i = new Intent(SplashActivity.this, CameraActivity.class);
                        } else {
                            //i = new Intent(SplashActivity.this, ComingSoonActivity.class);
                            i = new Intent(SplashActivity.this, CameraActivity.class);
                        }
                        startActivity(i);
                        finish();
                    }
                }, 3000);
            }
        }
    }

    private void doWork() {
        for (int progress=0; progress<100; progress+=10) {
            try {
                Thread.sleep(1000);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG",e.getMessage());
            }
        }
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 922 && Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0 || checkSelfPermission("android.permission.CAMERA") != 0 || checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                permissionDialog();
            }
        }
    }
}
