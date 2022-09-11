package com.techkiii.Camera;

import static android.hardware.Camera.Parameters.*;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.techkiii.R;
import com.techkiii.WifiCameraActivity;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wysaid.camera.CameraInstance;
import org.wysaid.myUtils.FileUtil;
import org.wysaid.myUtils.ImageUtil;
import org.wysaid.view.CameraRecordGLSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import com.techkiii.SlowMotionActivity;
import com.techkiii.VideoViewActivity;
import com.techkiii.utils.FileUtils;
import com.techkiii.videocompressor.VideoCompress;

public class CameraActivity extends AppCompatActivity {
    public static String lastVideoPathFileName = FileUtil.getPath() + "/lastVideoPath.txt";
    static String mCurrentConfig;
    boolean isValid = true;
    String recordFilename;
    ImageView ivImage, ivSave, iv_crop, ivBurst, ivNight, ivCancel, iv_back, ivMode;
    Bitmap bitmap;
    private String filePath;
    private Dialog progressDialog;
    RelativeLayout rvImage, rvMain, rl_camera;
    TextView power_perc;
    ImageView ivSetting;
    ImageView ivPower;
    CardView cvDialog;
    LinearLayout ll_bottom, ll_preset;
    LinearLayout ll_resolution, ll_1080_30fps, ll_1080_60fps, ll_1080_240fps, ll_4k_30fps, ll_4k_60fps;

    private OrientationEventListener mOrientationEventListener;
    private int mOrientation = -1;

    private static final int ORIENTATION_PORTRAIT_NORMAL = 1;
    private static final int ORIENTATION_PORTRAIT_INVERTED = 2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL = 3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED = 4;

    private String currentPhotoPath = "";
    ImageView imageview, iv_preset;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    RelativeLayout rv_1, rv_2, rv_3/*, rv_4*/;
    CardView rv_camera_1, rv_camera_2, rv_camera_3/*, //rv_camera_4*/;
    TextView tv_1, tv_2, tv_3/*, tv_4*/;

    Chronometer tvTimer;

    ImageView ivDot;

    RelativeLayout relativeLayout;
    RelativeLayout rlCameraPreset;

    public static final String TAG = "MyTag";

    private static final String JSON_URL = "https://simplifiedcoding.net/demos/view-flipper/heroes.php";

    SeekBar value;
    private boolean isOpenFirstTime = false;
    RelativeLayout rvTimer;
    ImageView recordBtn;
    ImageView ivPreset;
    int rate = 30;

    private CameraRecordGLSurfaceView mCameraView;
    private RelativeLayout rlAngle;
    public final static String LOG_TAG = CameraRecordGLSurfaceView.LOG_TAG;
    public static CameraActivity mCurrentInstance = null;

    static int filterPos = 0;
    static int filterPreset1Pos = 0;
    static int filterPreset2Pos = 0;
    static int filterPreset3Pos = 0;
    static int filterPreset4Pos = 0;
    static int preset1 = 0;
    static int resolution = 1;
    String applyResolution = "1080x1920";
    static int isResolution = 0;
    static int isSetAngle = 0;
    static int sAngle = 0;
    static int selectProgress = 0;
    float progress = 0;
    private FilterAdapter mAdapter;
    private RecyclerView mFilterListView;
    TextView textViewFilter;
    int height, width;
    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

    ImageView image_click_action, filter, flash_button, timer_button, slow_motion/*, camera_switch*/, iv_dot_photo, iv_dot_video, iv_dot_preset, iv_hdr, burst_click_action,iv_wifi, iv_resize;
    TextView photo_click, video_click, preset_click, tvSlowMotion;
    public static int is_video_or_photo = 2;
    public static int is_preset = 0;
    int bottom_hardware_height;
    SeekBar seekBar;
    int is_filter_open = 0;
    int is_hdr = 0, is_burst = 0;
    int is_front = 0;
    boolean is_camera_front = true;

    LinearLayout mFilterLayout;
    static int timer = 0;
    static int flash = 0;
    static int angle = 1;
    static int night = 0;
    static int selected_aspect_ratio = 0;
    boolean countCheck = false;
    LinearLayout ll_night;
    LinearLayout myGLSurfaceViewParent;
    WebView webview;
    String orientationMode;
    int burst_photo = 0;

    int left_h;
    int counter = 0;
    int slow_m_count = 0;
    int port = 0, land = 0;
    int addResolution = 0;
    int addAngle = 0;
    float intensity = 0.8f;
    int selectedProgress = 0;
    MediaPlayer mp;
    int preset_action = 0;
    long startTime;
    float mDist = 0;
    private boolean isWifiConnected = false;

    public CameraActivity() throws CameraAccessException {
    }

    //Record Video

    class RecordListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            cvDialog.setVisibility(View.GONE);
            close_filter();
            if (sharedPreferences.getString("land", "0").equalsIgnoreCase("1") && sharedPreferences.getString("night", "0").equalsIgnoreCase("1")) {
                mCameraView.setMode(SCENE_MODE_HDR);
                mCameraView.setNightMode(SCENE_MODE_NIGHT);
            } else if (sharedPreferences.getString("land", "0").equalsIgnoreCase("1")) {
                mCameraView.setMode(SCENE_MODE_HDR);
            } else if (sharedPreferences.getString("night", "0").equalsIgnoreCase("1")) {
                mCameraView.setNightMode(SCENE_MODE_NIGHT);
            } else {
                mCameraView.setMode(SCENE_MODE_AUTO);
            }
            mCameraView.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
            if (!isValid) {
                Log.e(LOG_TAG, "Please wait for the call...");
                return;
            }
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            isValid = false;
            if (!mCameraView.isRecording()) {
                recordBtn.setImageResource(R.drawable.img_video_icon);
                Log.i(LOG_TAG, "Start recording...");
                //recordFilename = outputDir + "/rec_" + System.currentTimeMillis() + ".mp4";
                recordFilename = FileUtil.getPath() + "/rec_" + System.currentTimeMillis() + ".mp4";
                mCameraView.startRecording(recordFilename, 1080, 1920, rate, new CameraRecordGLSurfaceView.StartRecordingCallback() {
                    @Override
                    public void startRecordingOver(boolean success) {
                        if (success) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTimer.setBase(SystemClock.elapsedRealtime());
                                    tvTimer.start();
                                    ivDot.startAnimation(animation);
                                }
                            });
                        } else {
                        }
                        isValid = true;
                    }
                });
            } else {
                recordBtn.setImageResource(R.drawable.img_video_icon_border);
                Log.i(LOG_TAG, "End recording...");
                mCameraView.endRecording(new CameraRecordGLSurfaceView.EndRecordingCallback() {
                    @Override
                    public void endRecordingOK() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvTimer.stop();
                                ivDot.clearAnimation();
                            }
                        });
                        Log.i(LOG_TAG, "End recording OK");
                        if (is_video_or_photo != 1) {
                            slow_motion.setAlpha(0.5F);
                            tvSlowMotion.setAlpha(0.5F);
                        } else {
                            slow_motion.setAlpha(1.0F);
                            tvSlowMotion.setAlpha(1.0F);
                        }
                        if (slow_m_count == 1) {
                            executeSlowMotionVideoCommand();
                        } else {
                            //String destPath = ImageUtil.getPath() + "/techkiii_rec_" + System.currentTimeMillis() + ".mp4";
                            String destPath = FileUtil.getPath() + "/techkiii_rec_" + System.currentTimeMillis() + ".mp4";
                            addVideo(destPath);
                            if (resolution == 2) {
                                VideoCompress.compressVideoMedium(recordFilename, destPath, new VideoCompress.CompressListener() {
                                    @Override
                                    public void onStart() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSuccess() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                        Intent intent = new Intent(CameraActivity.this, VideoViewActivity.class);
                                        intent.putExtra("recordFile", destPath);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFail() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onProgress(float percent) {
                                    }
                                });
                            } else if (resolution == 3) {
                                VideoCompress.compressVideoHigh(recordFilename, destPath, new VideoCompress.CompressListener() {
                                    @Override
                                    public void onStart() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSuccess() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                        Intent intent = new Intent(CameraActivity.this, VideoViewActivity.class);
                                        intent.putExtra("recordFile", destPath);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFail() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onProgress(float percent) {
                                    }
                                });
                            } else if (resolution == 4) {
                                VideoCompress.compressVideoHigh4K(recordFilename, destPath, new VideoCompress.CompressListener() {
                                    @Override
                                    public void onStart() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSuccess() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                        Intent intent = new Intent(CameraActivity.this, VideoViewActivity.class);
                                        intent.putExtra("recordFile", destPath);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFail() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onProgress(float percent) {
                                    }
                                });
                            } else {
                                VideoCompress.compressVideoLow(recordFilename, destPath, new VideoCompress.CompressListener() {
                                    @Override
                                    public void onStart() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSuccess() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                        Intent intent = new Intent(CameraActivity.this, VideoViewActivity.class);
                                        intent.putExtra("recordFile", destPath);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFail() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onProgress(float percent) {
                                    }
                                });
                            }
                        }
                        isValid = true;
                    }
                });
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tvTimer.setBase(SystemClock.elapsedRealtime());
        seekBar.setVisibility(View.GONE);
        rlAngle.setVisibility(View.VISIBLE);
        recordBtn.setImageResource(R.drawable.img_video_icon_border);
        View overlay = findViewById(R.id.rv_main_layout);

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public Uri addVideo(String destPath) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "techkiii_rec");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, destPath);
        return getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return size;
    }

    public void open_filter() {
        if (is_preset == 1) {
            rlCameraPreset.setVisibility(View.GONE);
        }
        iv_preset.setImageResource(R.drawable.img_preset_white);
        seekBar.setVisibility(View.VISIBLE);
        filter.setImageResource(R.drawable.img_filter_fill);
        is_filter_open = 1;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mFilterLayout.setVisibility(View.VISIBLE);
                if (is_video_or_photo == 0) {
                    rlAngle.setVisibility(View.VISIBLE);
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                    rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                } else {
                    rlAngle.setVisibility(View.GONE);
                }
            }

            @Override
                public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    public void close_filter() {
        is_filter_open = 0;
        seekBar.setVisibility(View.GONE);
        rlAngle.setVisibility(View.VISIBLE);
        filter.setImageResource(R.drawable.img_filter);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0, mFilterLayout.getHeight());
        animator.setDuration(200L);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mFilterLayout.setVisibility(View.INVISIBLE);
                if (is_video_or_photo == 0) {
                    rlAngle.setVisibility(View.GONE);
                } else {
                    rlAngle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mFilterLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    public void countDownTimerPhoto() {
        cvDialog.setVisibility(View.GONE);
        close_filter();
        mCameraView.takePicture(new CameraRecordGLSurfaceView.TakePictureCallback() {
            @Override
            public void takePictureOK(Bitmap bmp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bmp != null) {
                            String s = ImageUtil.saveBitmap(bmp);
                            bmp.recycle();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
                            burst_photo++;
                        }
                    }
                });
            }
        }, null, mCurrentConfig, intensity, true);
    }

    public void countDownTimerPhoto(int num) {
        cvDialog.setVisibility(View.GONE);
        close_filter();
        final int i = num;
        new CountDownTimer((long) num, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewFilter.setVisibility(View.VISIBLE);
                textViewFilter.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                countCheck = i == 0;
                textViewFilter.setVisibility(View.GONE);

                if (sharedPreferences.getString("port", "-1").equalsIgnoreCase("1")) {
                    mCameraView.setMode(SCENE_MODE_LANDSCAPE);
                    orientationMode = sharedPreferences.getString("port", "-1");
                } else if (sharedPreferences.getString("port", "-1").equalsIgnoreCase("2")) {
                    mCameraView.setMode(SCENE_MODE_AUTO);
                    orientationMode = sharedPreferences.getString("port", "-1");
                } else {
                    mCameraView.setMode(SCENE_MODE_PORTRAIT);
                    orientationMode = sharedPreferences.getString("port", "0");
                }

                mCameraView.takePicture(new CameraRecordGLSurfaceView.TakePictureCallback() {
                    @Override
                    public void takePictureOK(Bitmap bmp) {
                        if (bmp != null) {
                            bitmap = bmp;
                            rvImage.setVisibility(View.VISIBLE);
                            rvMain.setVisibility(View.GONE);
                            ivImage.setImageBitmap(bmp);
                        }
                    }
                }, null, mCurrentConfig, intensity, true);
            }
        }.start();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_demo);
        View overlay = findViewById(R.id.rv_main_layout);

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().setFlags(1024, 1024);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        Point p = getRealScreenSize(CameraActivity.this);
        Log.e("camsize", " : " + p.x + " : " + p.y);
        bottom_hardware_height = p.y - height;
        height = p.y;
        width = p.x;
        selected_aspect_ratio = 0;
        is_camera_front = true;

        left_h = (height - ((width / 3) * 4) - bottom_hardware_height);

        mCameraView = (CameraRecordGLSurfaceView) findViewById(R.id.myGLSurfaceView);


//        CameraManager manager = (CameraManager) mCameraView.getContext().getSystemService(Context.CAMERA_SERVICE);
//        String cameraId = null;
//        try {
//            cameraId = manager.getCameraIdList()[1];
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        CameraCharacteristics characteristics = null;
//        try {
//            characteristics = manager.getCameraCharacteristics(cameraId);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        Range<Integer>[] fpsRanges = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

        rlAngle = (RelativeLayout) findViewById(R.id.rl_angle);
        progressDialog = new Dialog(this, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setTitle("Waiting for some Time..");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        power_perc = findViewById(R.id.power_perc);
        ivSetting = findViewById(R.id.iv_setting);
        ivPower = findViewById(R.id.iv_power);
        cvDialog = findViewById(R.id.cv_dialog);
        ll_night = (LinearLayout) findViewById(R.id.ll_night);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        rlCameraPreset = (RelativeLayout) findViewById(R.id.rl_camera_preset);
        imageview = findViewById(R.id.imageview);
        iv_preset = findViewById(R.id.iv_preset);
        value = findViewById(R.id.value);

        tvTimer = findViewById(R.id.tvTimer);
        ivDot = findViewById(R.id.iv_dot);
        rvTimer = findViewById(R.id.rv_timer);

        rv_1 = findViewById(R.id.rv_1);
        rv_2 = findViewById(R.id.rv_2);
        rv_3 = findViewById(R.id.rv_3);
        ////rv_4 = findViewById(R.id.//rv_4);

        rv_camera_1 = findViewById(R.id.rv_camera_1);
        rv_camera_2 = findViewById(R.id.rv_camera_2);
        rv_camera_3 = findViewById(R.id.rv_camera_3);
        //rv_camera_4 = findViewById(R.id.//rv_camera_4);

        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);
        ////tv_4 = findViewById(R.id.//tv_4);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            rv_3.setVisibility(View.GONE);
            rv_2.setVisibility(View.VISIBLE);
            //tv_4.setText("3");
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            rv_3.setVisibility(View.VISIBLE);
            rv_2.setVisibility(View.VISIBLE);
            //tv_4.setText("4");
        } else {
            rv_3.setVisibility(View.GONE);
            rv_2.setVisibility(View.GONE);
            //tv_4.setText("2");
        }

        ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
        rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
        rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
        rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));

        startTime = System.currentTimeMillis();

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        myGLSurfaceViewParent = (LinearLayout) findViewById(R.id.myGLSurfaceViewParent);
        webview = (WebView) findViewById(R.id.webview_wifi);
        webview.setVisibility(View.GONE);

        timer = 0;
        flash = 0;

        mFilterLayout = (LinearLayout) findViewById(R.id.filter_layout_base);
        ivImage = findViewById(R.id.iv_image);
        iv_back = findViewById(R.id.iv_back);
        ivMode = findViewById(R.id.iv_mode);
        iv_wifi = findViewById(R.id.iv_wifi);
        iv_dot_photo = findViewById(R.id.iv_dot_photo);
        iv_dot_video = findViewById(R.id.iv_dot_video);
        iv_dot_preset = findViewById(R.id.iv_dot_presets);
        rvImage = findViewById(R.id.rv_image);
        ivCancel = findViewById(R.id.iv_cancel);
        ivSave = findViewById(R.id.iv_save);
        iv_crop = findViewById(R.id.iv_crop);
        iv_crop.setVisibility(View.GONE);
        ivBurst = findViewById(R.id.iv_burst);
        ivNight = findViewById(R.id.iv_night);
        rvMain = findViewById(R.id.rv_main);
        rl_camera = findViewById(R.id.rl_camera);
        ll_bottom = findViewById(R.id.bottom);
        ll_preset = findViewById(R.id.ll_preset);
        ll_resolution = findViewById(R.id.ll_resolution);
        ll_1080_30fps = findViewById(R.id.ll_1080_30fps);
        ll_1080_60fps = findViewById(R.id.ll_1080_60fps);
        ll_1080_240fps = findViewById(R.id.ll_1080_240fps);
        ll_4k_30fps = findViewById(R.id.ll_4k_30fps);
        ll_4k_60fps = findViewById(R.id.ll_4k_60fps);
        flash_button = (ImageView) findViewById(R.id.iv_flash);
        timer_button = (ImageView) findViewById(R.id.iv_timer);
        slow_motion = (ImageView) findViewById(R.id.iv_slow_motion);
        tvSlowMotion = (TextView) findViewById(R.id.tv_slow_motion);
        //camera_switch = (ImageView) findViewById(R.id.iv_camera);
        photo_click = (TextView) findViewById(R.id.tv_photo);
        photo_click.setTextColor(getResources().getColor(R.color.blue));
        photo_click.setTextSize(18f);
        preset_click = (TextView) findViewById(R.id.tv_presets);
        video_click = (TextView) findViewById(R.id.tv_video);
        image_click_action = (ImageView) findViewById(R.id.iv_shut_camera);
        burst_click_action = (ImageView) findViewById(R.id.iv_burst_camera);
        iv_hdr = (ImageView) findViewById(R.id.iv_hdr);
        iv_resize = (ImageView) findViewById(R.id.iv_resize);
        filter = (ImageView) findViewById(R.id.iv_filter);

        textViewFilter = (TextView) findViewById(R.id.filterText);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        ivPreset = (ImageView) findViewById(R.id.iv_preset_add);
        lastVideoPathFileName = FileUtil.getPathInPackage(CameraActivity.this, true) + "/lastVideoPath.txt";
        recordBtn = (ImageView) findViewById(R.id.recordBtn);
        mCameraView.presetCameraForward(false);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setVisibility(View.INVISIBLE);

        recordBtn.setImageResource(R.drawable.img_video_icon_border);

        //mCameraView.setBackgroundColor(getResources().getColor(R.color.black));


        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                if (sharedPreferences.getString("port", "0").equalsIgnoreCase("1")) {
                    ivMode.setImageResource(R.drawable.img_land);
                    mCameraView.setMode(SCENE_MODE_LANDSCAPE);
                    //mCameraView.setMode("landscape");
                } else if (sharedPreferences.getString("port", "0").equalsIgnoreCase("2")) {
                    ivMode.setImageResource(R.drawable.img_port);
                    mCameraView.setMode(SCENE_MODE_PORTRAIT);
                    //mCameraView.setMode("portrait");
                } else {
                    ivMode.setImageResource(R.drawable.img_auto_rotate);
                    mCameraView.setMode(SCENE_MODE_AUTO);
                    //mCameraView.setMode("auto");
                }
            }
        }, 2000);



        ivMode.setOnClickListener(v -> {
            if (port == 0) {
                ivMode.setImageResource(R.drawable.img_land);
                myEdit.putString("port", "1");
                mCameraView.setMode(SCENE_MODE_LANDSCAPE);
                //mCameraView.setMode("landscape");
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                port = 2;
            } else if (port == 2) {
                ivMode.setImageResource(R.drawable.img_port);
                myEdit.putString("port", "2");
                mCameraView.setMode(SCENE_MODE_PORTRAIT);
                //mCameraView.setMode("portrait");
                port = 1;
            } else {
                ivMode.setImageResource(R.drawable.img_auto_rotate);
                myEdit.putString("port", "0");
                mCameraView.setMode(SCENE_MODE_AUTO);
                //mCameraView.setMode("auto");
                port = 0;
            }
            myEdit.commit();
        });

        rv_1.setOnClickListener(v -> {
            sAngle = 1;
            setAddAngle(sAngle);
        });

        rv_2.setOnClickListener(v -> {
            sAngle = 2;
            setAddAngle(sAngle);
        });

        rv_3.setOnClickListener(v -> {
            sAngle = 3;
            setAddAngle(sAngle);
        });

//        //rv_4.setOnClickListener(v -> {
//            sAngle = 4;
//            setAddAngle(sAngle);
//        });

        if (sharedPreferences.getString("night", "0").equalsIgnoreCase("1")) {
            ivNight.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
            night = 1;
        } else {
            ivNight.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
            night = 0;
        }

        ll_night.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (night == 0) {
                    night = 1;
                    myEdit.putString("night", "1");
                    mCameraView.setNightMode(SCENE_MODE_NIGHT);
                    mCameraView.setWhiteBalance(WHITE_BALANCE_DAYLIGHT);
                    mCameraView.setExposureCompensation(2);
                    ivNight.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                    Toast.makeText(CameraActivity.this, "Night Mode on", Toast.LENGTH_LONG).show();
                } else {
                    night = 0;
                    myEdit.putString("night", "0");
                    mCameraView.setMode(SCENE_MODE_AUTO);
                    ivNight.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                    Toast.makeText(CameraActivity.this, "Night Mode Off", Toast.LENGTH_LONG).show();
                }
                myEdit.commit();
            } else {
                Toast.makeText(CameraActivity.this, "Night Mode Not Supported in this device", Toast.LENGTH_LONG).show();
            }
        });

        if (sharedPreferences.getString("land", "0").equalsIgnoreCase("1")) {
            iv_hdr.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
            land = 1;
        } else {
            iv_hdr.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
            land = 0;
        }

        iv_hdr.setOnClickListener(view -> {
            if (land == 0) {
                land = 1;
                myEdit.putString("land", "1");
                mCameraView.setMode(SCENE_MODE_HDR);
                iv_hdr.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                Toast.makeText(CameraActivity.this, "HDR Mode On! Only Work on Normal Filter", Toast.LENGTH_LONG).show();
            } else {
                land = 0;
                myEdit.putString("land", "0");
                mCameraView.setMode(SCENE_MODE_AUTO);
                iv_hdr.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                Toast.makeText(CameraActivity.this, "HDR Mode Off", Toast.LENGTH_LONG).show();
            }
            myEdit.commit();
            cvDialog.setVisibility(View.GONE);
            image_click_action.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.GONE);
            burst_click_action.setVisibility(View.GONE);
        });

        iv_resize.setOnClickListener(view -> {
            if (land == 0) {
                land = 1;
                myEdit.putString("land", "1");
                //mCameraView.setMode(Camera.Parameters.SCENE_MODE_HDR);
                mCameraView.setPictureSize(5184, 3880, true); // > 20mp
                mCameraView.setFitFullView(true);
                iv_resize.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                //Toast.makeText(CameraActivity.this, "HDR Mode On! Only Work on Normal Filter", Toast.LENGTH_LONG).show();
            } else {
                land = 0;
                myEdit.putString("land", "0");
                //mCameraView.setMode(Camera.Parameters.SCENE_MODE_AUTO);
                mCameraView.setPictureSize(1920, 1440, false);
                mCameraView.setFitFullView(true);
                iv_resize.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                //Toast.makeText(CameraActivity.this, "HDR Mode Off", Toast.LENGTH_LONG).show();
            }
            myEdit.commit();
            cvDialog.setVisibility(View.GONE);
            image_click_action.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.GONE);
            burst_click_action.setVisibility(View.GONE);
        });

        ivBurst.setOnClickListener(view -> {
            if (is_video_or_photo == 2) {
                is_burst = 1;

                if (counter == 0) {
                    counter = 1;
                    myEdit.putString("burst", "1");
                    ivBurst.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                    image_click_action.setVisibility(View.GONE);
                    recordBtn.setVisibility(View.GONE);
                    burst_click_action.setVisibility(View.VISIBLE);
                    Toast.makeText(CameraActivity.this, "Click the Photo Using Burst Camera", Toast.LENGTH_SHORT).show();
                } else {
                    counter = 0;
                    myEdit.putString("burst", "0");
                    ivBurst.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                    Toast.makeText(CameraActivity.this, "Burst Camera Off", Toast.LENGTH_SHORT).show();
                    burst_click_action.setVisibility(View.GONE);
                    image_click_action.setVisibility(View.VISIBLE);
                    myGLSurfaceViewParent.removeAllViews();
                    myGLSurfaceViewParent.addView(mCameraView);
                }
                myEdit.commit();
            } else {
                Toast.makeText(CameraActivity.this, "Burst Not Supported in Video", Toast.LENGTH_SHORT).show();
            }
            cvDialog.setVisibility(View.GONE);
        });

        rvMain.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                cvDialog.setVisibility(View.GONE);
            }

            @Override
            public void onDoubleClick(View v) {
                if (is_video_or_photo == 2) {
                    imageview.setVisibility(View.GONE);
                    imageview.setImageBitmap(null);
                    imageview.setImageDrawable(null);
                    mCameraView.cameraInstance().setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                    Toast.makeText(CameraActivity.this, "Take Picture Success", Toast.LENGTH_SHORT).show();
                    countDownTimerPhoto(0);
                } else if (is_video_or_photo == 1) {
                    mCameraView.cameraInstance().setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                }
            }
        });

        //Slow Motion

        if (is_video_or_photo != 1) {
            slow_motion.setAlpha(0.5F);
            tvSlowMotion.setAlpha(0.5F);
        } else {
            slow_motion.setAlpha(1.0F);
            tvSlowMotion.setAlpha(1.0F);
        }

        slow_motion.setOnClickListener(v -> {
            if(slow_m_count == 0) {
                slow_m_count = 1;
                slow_motion.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
            } else {
                slow_m_count = 0;
                slow_motion.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
            }
            cvDialog.setVisibility(View.GONE);
        });

        iv_back.setOnClickListener(view -> {
            cvDialog.setVisibility(View.GONE);
            rlAngle.setVisibility(View.VISIBLE);
            rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
            rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
            rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
            ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
            ll_bottom.setVisibility(View.GONE);
            rl_camera.setVisibility(View.VISIBLE);
            mFilterListView.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
            recordFilename = null;
            if (is_video_or_photo != 1) {
                slow_motion.setAlpha(0.5F);
                tvSlowMotion.setAlpha(0.5F);
            }
            recordBtn.setVisibility(View.GONE);
            imageview.setVisibility(View.VISIBLE);
            image_click_action.setVisibility(View.VISIBLE);
            burst_click_action.setVisibility(View.GONE);
            slow_motion.setEnabled(false);
            is_video_or_photo = 2;
        });

        iv_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWithoutPasswordToWifi("Debian_DAP");
            }
        });

        ivPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCameraView.cameraInstance().isPreviewing()) {
                    onPause();
                    myGLSurfaceViewParent.removeAllViews();
                } else {
                    myGLSurfaceViewParent.addView(mCameraView);
                    onResume();
                }
            }
        });

        // Dialog

        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvDialog.setVisibility(View.VISIBLE);
                imageview.setVisibility(View.GONE);

                imageview.setImageBitmap(null);
                imageview.setImageDrawable(null);

                if (is_video_or_photo == 0 || is_video_or_photo == 1) {
                    if (is_front == 1) {
                        ll_resolution.setVisibility(View.VISIBLE);
                    } else {
                        ll_resolution.setVisibility(View.GONE);
                    }
                } else {
                    ll_resolution.setVisibility(View.GONE);
                }

                if (is_video_or_photo != 1) {
                    slow_motion.setAlpha(0.5F);
                    tvSlowMotion.setAlpha(0.5F);
                } else {
                    slow_motion.setAlpha(1.0F);
                    tvSlowMotion.setAlpha(1.0F);
                }
            }
        });

        //Flash Light

        if (sharedPreferences.getString("flash", "0").equalsIgnoreCase("2")) {
            flash_button.setImageResource(R.drawable.camera_btn_flash_auto);
            flash_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
            mCameraView.setFlashLightMode(FLASH_MODE_AUTO);
        } else if (sharedPreferences.getString("flash", "0").equalsIgnoreCase("1")) {
            flash_button.setImageResource(R.drawable.camera_btn_flash_torch);
            flash_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
            mCameraView.setFlashLightMode(FLASH_MODE_TORCH);
        } else {
            flash_button.setImageResource(R.drawable.img_flash);
            flash_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
            mCameraView.setFlashLightMode(FLASH_MODE_OFF);
        }

        flash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_camera_front) {
                    Toast.makeText(CameraActivity.this, "Flash is available at back camera.", Toast.LENGTH_SHORT).show();
                } else {
                    if (selected_aspect_ratio == 2) {
                        if (flash == 0) {
                            flash = 1;
                            flash_button.setImageResource(R.drawable.camera_btn_flash_auto_b);
                            mCameraView.setFlashLightMode(FLASH_MODE_AUTO);
                        } else if (flash == 1) {
                            flash = 2;
                            flash_button.setImageResource(R.drawable.camera_btn_flash_torch_b);
                            mCameraView.setFlashLightMode(FLASH_MODE_TORCH);
                        } else {
                            flash = 0;
                            flash_button.setImageResource(R.drawable.camera_btn_flash_b);
                            mCameraView.setFlashLightMode(FLASH_MODE_OFF);
                        }
                    } else {
                        if (flash == 0) {
                            flash = 1;
                            myEdit.putString("flash", "2");
                            flash_button.setImageResource(R.drawable.camera_btn_flash_auto);
                            flash_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                            mCameraView.setFlashLightMode(FLASH_MODE_AUTO);
                        } else if (flash == 1) {
                            flash = 2;
                            myEdit.putString("flash", "1");
                            flash_button.setImageResource(R.drawable.camera_btn_flash_torch);
                            flash_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                            mCameraView.setFlashLightMode(FLASH_MODE_TORCH);
                        } else {
                            flash = 0;
                            myEdit.putString("flash", "0");
                            flash_button.setImageResource(R.drawable.img_flash);
                            flash_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                            mCameraView.setFlashLightMode(FLASH_MODE_OFF);
                        }
                    }
                    myEdit.commit();
                }
            }
        });

        //Timer

        timer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_video_or_photo == 2) {
                    if (selected_aspect_ratio == 2) {
                        if (timer == 0) {
                            timer = 3;
                            timer_button.setImageResource(R.drawable.camera_btn_timer_3s_b);
                        } else if (timer == 3) {
                            timer = 6;
                            timer_button.setImageResource(R.drawable.camera_btn_timer_6s_b_1);
                        } else {
                            timer = 0;
                            timer_button.setImageResource(R.drawable.camera_btn_timer_b);
                        }

                    } else {
                        if (timer == 0) {
                            timer = 3;
                            timer_button.setImageResource(R.drawable.camera_btn_timer_6s_b_1);
                            timer_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                        } else if (timer == 3) {
                            timer = 6;
                            timer_button.setImageResource(R.drawable.camera_btn_timer_3s);
                            timer_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.pink));
                        } else {
                            timer = 0;
                            timer_button.setImageResource(R.drawable.camera_btn_timer);
                            timer_button.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                        }
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "Timer Not Supported in Video", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Preset

        ll_preset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preset_action == 0) {
                    iv_preset.setImageResource(R.drawable.img_preset_pink);
                    is_preset = 1;
                    //is_video_or_photo = 0;
                    rv_camera_1.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                    rv_camera_2.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                    rv_camera_3.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                    //rv_camera_4.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                    seekBar.setVisibility(View.GONE);
                    mFilterLayout.setVisibility(View.GONE);
                    if (is_preset == 1 && is_video_or_photo != 0) {
                        rlAngle.setVisibility(View.GONE);
                    } else {
                        rlAngle.setVisibility(View.VISIBLE);
                        rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                        rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                        rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                        ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    }
                    rlCameraPreset.setVisibility(View.VISIBLE);
                    preset_action = 1;
                } else {
                    iv_preset.setImageResource(R.drawable.img_preset_white);
                    is_preset = 0;
                    seekBar.setVisibility(View.GONE);
                    mFilterLayout.setVisibility(View.GONE);
                    if (is_preset == 1 && is_video_or_photo != 0) {
                        rlAngle.setVisibility(View.GONE);
                    } else {
                        rlAngle.setVisibility(View.VISIBLE);
                        rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                        rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                        rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                        ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    }
                    rlCameraPreset.setVisibility(View.GONE);
                    preset_action = 0;
                }
            }
        });

        photo_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvTimer.setVisibility(View.GONE);
                cvDialog.setVisibility(View.GONE);
                iv_preset.setImageResource(R.drawable.img_preset_white);
                ll_preset.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.GONE);
                ivPreset.setVisibility(View.GONE);
                close_filter();
                recordFilename = null;
                slow_motion.setAlpha(0.5F);
                tvSlowMotion.setAlpha(0.5F);
                rlCameraPreset.setVisibility(View.GONE);
                recordBtn.setVisibility(View.GONE);
                imageview.setVisibility(View.VISIBLE);
                image_click_action.setVisibility(View.VISIBLE);
                burst_click_action.setVisibility(View.GONE);
                photo_click.setTextColor(getResources().getColor(R.color.blue));
                photo_click.setTextSize(18f);
                video_click.setTextSize(15f);
                preset_click.setTextSize(15f);
                video_click.setTextColor(getResources().getColor(R.color.gray));
                preset_click.setTextColor(getResources().getColor(R.color.gray));
                iv_dot_photo.setVisibility(View.VISIBLE);
                iv_dot_video.setVisibility(View.GONE);
                iv_dot_preset.setVisibility(View.GONE);
                photo_click.setGravity(Gravity.CENTER);
                video_click.setGravity(Gravity.END);
                rlAngle.setVisibility(View.VISIBLE);
                if (is_front == 1) {
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                } else {
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                }
                rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                slow_motion.setEnabled(false);
                is_video_or_photo = 2;

                if (selected_aspect_ratio == 0) {
                    image_click_action.setImageResource(R.drawable.img_shut_camera);
                } else {
                    image_click_action.setImageResource(R.drawable.img_shut_camera);
                }
            }
        });

        preset_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rvTimer.setVisibility(View.GONE);
                is_preset = 0;
                ivPreset.setVisibility(View.VISIBLE);
                ll_preset.setVisibility(View.GONE);
                rv_camera_1.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                rv_camera_2.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                rv_camera_3.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                //rv_camera_4.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                is_filter_open = 0;
                filter.setImageResource(R.drawable.img_filter);
                rlCameraPreset.setVisibility(View.VISIBLE);
                rlAngle.setVisibility(View.GONE);
                recordBtn.setVisibility(View.GONE);
                image_click_action.setVisibility(View.VISIBLE);
                burst_click_action.setVisibility(View.GONE);
                photo_click.setTextColor(getResources().getColor(R.color.gray));
                photo_click.setTextSize(15f);
                video_click.setTextSize(15f);
                preset_click.setTextSize(18f);
                preset_click.setTextColor(getResources().getColor(R.color.blue));
                video_click.setTextColor(getResources().getColor(R.color.gray));
                iv_dot_photo.setVisibility(View.GONE);
                iv_dot_video.setVisibility(View.GONE);
                iv_dot_preset.setVisibility(View.VISIBLE);
                photo_click.setGravity(Gravity.CENTER);
                video_click.setGravity(Gravity.END);
                slow_motion.setEnabled(false);
                imageview.setVisibility(View.GONE);
                is_video_or_photo = 0;
                open_filter();
                cvDialog.setVisibility(View.VISIBLE);
                if (is_video_or_photo == 0 || is_video_or_photo == 1) {
                    if (is_front == 1) {
                        ll_resolution.setVisibility(View.VISIBLE);
                    } else {
                        ll_resolution.setVisibility(View.GONE);
                    }
                } else {
                    ll_resolution.setVisibility(View.GONE);
                }
                image_click_action.setImageResource(R.drawable.img_shut_camera);
            }
        });

        video_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvDialog.setVisibility(View.GONE);
                ivPreset.setVisibility(View.GONE);
                ll_preset.setVisibility(View.VISIBLE);
                close_filter();
                is_hdr = 0;
                rlCameraPreset.setVisibility(View.GONE);
                recordBtn.setVisibility(View.VISIBLE);
                imageview.setVisibility(View.VISIBLE);
                image_click_action.setVisibility(View.GONE);
                burst_click_action.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
                video_click.setTextColor(getResources().getColor(R.color.blue));
                photo_click.setTextColor(getResources().getColor(R.color.gray));
                preset_click.setTextColor(getResources().getColor(R.color.gray));
                photo_click.setTextSize(15f);
                preset_click.setTextSize(15f);
                video_click.setTextSize(18f);
                iv_dot_photo.setVisibility(View.GONE);
                iv_dot_preset.setVisibility(View.GONE);
                iv_dot_video.setVisibility(View.VISIBLE);
                video_click.setGravity(Gravity.CENTER);
                photo_click.setGravity(Gravity.END);
                slow_motion.setEnabled(true);
                slow_motion.setAlpha(1.0F);
                tvSlowMotion.setAlpha(1.0F);
                rlAngle.setVisibility(View.VISIBLE);
                if (is_front == 1) {
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                } else {
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                }
                rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                slow_motion.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                if (is_video_or_photo == 0 || is_video_or_photo == 2) {
                    imageview.setVisibility(View.GONE);
                    is_video_or_photo = 1;
                    recordBtn.setImageResource(R.drawable.img_video_icon_border);
                }
                rvTimer.setVisibility(View.VISIBLE);
            }
        });

//        camera_switch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCameraView.switchCamera();
//                //mCameraView.resumePreview();
//                cvDialog.setVisibility(View.GONE);
//                if (is_camera_front) {
//                    is_camera_front = false;
//                    is_front = 1;
//                    mCameraView.resumePreview();
//                    mCameraView.cameraInstance().setAngles(0);
//                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
//                    ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
//                } else {
//                    is_camera_front = true;
//                    is_front = 0;
//                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
//                    ////rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
//                }
//                rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
//                rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
//            }
//        });

        image_click_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("land", "0").equalsIgnoreCase("1") && sharedPreferences.getString("night", "0").equalsIgnoreCase("1")) {
                    mCameraView.setMode(SCENE_MODE_HDR);
                    mCameraView.setNightMode(SCENE_MODE_NIGHT);
                } else if (sharedPreferences.getString("land", "0").equalsIgnoreCase("1")) {
                    mCameraView.setMode(SCENE_MODE_HDR);
                } else if (sharedPreferences.getString("night", "0").equalsIgnoreCase("1")) {
                    mCameraView.setNightMode(SCENE_MODE_NIGHT);
                }

                if (sharedPreferences.getString("port", "0").equalsIgnoreCase("1")) {
                    mCameraView.setMode(SCENE_MODE_LANDSCAPE);
                } else if (sharedPreferences.getString("port", "0").equalsIgnoreCase("2")) {
                    mCameraView.setMode(SCENE_MODE_AUTO);
                } else {
                    mCameraView.setMode(SCENE_MODE_PORTRAIT);
                }
                if (timer == 0) {
                    countDownTimerPhoto(0);
                } else if (timer == 3) {
                    countDownTimerPhoto(3000);
                } else if (timer == 6) {
                    countDownTimerPhoto(6000);
                } else {
                    countDownTimerPhoto(0);
                }

                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        savePicture();
                    }
                    }, 2000);
            }
        });

        burst_click_action.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 500);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        myGLSurfaceViewParent.removeAllViews();
                        myGLSurfaceViewParent.addView(mCameraView);
                        image_click_action.setVisibility(View.VISIBLE);
                        burst_click_action.setVisibility(View.GONE);
                        ivBurst.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
                        Toast.makeText(CameraActivity.this, "Burst Camera Off", Toast.LENGTH_SHORT).show();
                        return false;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    countDownTimerPhoto();
                    System.out.println("Performing action...");
                    mHandler.postDelayed(this, 500);
                }
            };

        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter.setImageResource(R.drawable.img_filter_fill);
                mFilterListView.setVisibility(View.VISIBLE);
                if (is_filter_open == 0) {
                    open_filter();
                    if (CameraActivity.filterPos == 0) {
                        seekBar.setVisibility(View.INVISIBLE);
                    } else {
                        seekBar.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (is_video_or_photo == 1 || is_video_or_photo == 2) {
                        rlAngle.setVisibility(View.VISIBLE);
                        rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                        rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                        rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                        //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    }
                    close_filter();
                    seekBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePicture();
                rvMain.setVisibility(View.VISIBLE);
                rvImage.setVisibility(View.GONE);
            }
        });

        iv_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
                    bitmap = drawable.getBitmap();
                    String s = ImageUtil.saveBitmap(bitmap);
                    Uri sourceUri = Uri.parse("file://" + s);
                    File file = null;
                    try {
                        file = getImageFile();
                        Uri destinationUri = Uri.fromFile(file);
                        openCropActivity(sourceUri, destinationUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(CameraActivity.this, "Save picture failed!", Toast.LENGTH_LONG).show();
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvImage.setVisibility(View.GONE);
                rvMain.setVisibility(View.VISIBLE);
            }
        });

        recordBtn.setOnClickListener(new RecordListener());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intensity = progress / 100.0f;
                seekBar.setProgress(progress);
                selectProgress = progress;
                mCameraView.setFilterIntensity(intensity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCurrentInstance = this;

        mCameraView.presetRecordingSize(480, 640, 30);
        mCameraView.setPictureSize(2048, 2048, true); // > 4MP
        mCameraView.setZOrderOnTop(false);
        mCameraView.setZOrderMediaOverlay(true);

        mCameraView.setOnCreateCallback(new CameraRecordGLSurfaceView.OnCreateCallback() {
            @Override
            public void createOver(boolean z) {
                Log.i(LOG_TAG, "view onCreate");
            }
        });

        myGLSurfaceViewParent.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                cvDialog.setVisibility(View.GONE);
            }

            @Override
            public void onDoubleClick(View v) {
                if (is_video_or_photo == 2) {
                    imageview.setVisibility(View.GONE);

                    imageview.setImageBitmap(null);
                    imageview.setImageDrawable(null);
                    mCameraView.cameraInstance().setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                    Toast.makeText(CameraActivity.this, "Take Picture Success", Toast.LENGTH_SHORT).show();
                    countDownTimerPhoto(0);
                } else if (is_video_or_photo == 1) {
                    mCameraView.cameraInstance().setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                }
            }
        });

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                Parameters params = mCameraView.cameraInstance().getParams();

                if (event.getPointerCount() > 1) {
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else {
                        handleZoom(event, params);
                    }
                } else {
                    // handle single touch events
                    if (action == MotionEvent.ACTION_UP) {
                        mCameraView.cameraInstance().setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCameraView.cameraInstance().setExposure();
                        cvDialog.setVisibility(View.GONE);
                        imageview.setVisibility(View.VISIBLE);
                        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
                        imageview.startAnimation(animZoomOut);
                        imageview.setColorFilter(getResources().getColor(R.color.white));
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(80, 80);
                        lp.height = 80;
                        lp.width = 80;
                        ((ViewGroup) v).removeView(imageview);
                        imageview.setImageBitmap(null);
                        imageview.setImageDrawable(null);
                        lp.setMargins(x, y, 0, 0);
                        imageview.setLayoutParams(lp);
                        imageview.setImageDrawable(getResources().getDrawable(
                                R.drawable.img_square));
                        ((ViewGroup) v).addView(imageview);

                        new CountDownTimer(3000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                imageview.setColorFilter(getResources().getColor(R.color.bottom_bg_view_topbar));
                            }

                            public void onFinish() {
                                imageview.setVisibility(View.GONE);
                                imageview.setImageBitmap(null);
                                imageview.setImageDrawable(null);
                            }

                        }.start();
                    }
                }
                return false;
            }
        });

        relativeLayout.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                if (is_video_or_photo == 2) {
                    imageview.setVisibility(View.GONE);

                    imageview.setImageBitmap(null);
                    imageview.setImageDrawable(null);
                    mCameraView.cameraInstance().setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                    Toast.makeText(CameraActivity.this, "Take Picture Success", Toast.LENGTH_SHORT).show();
                    countDownTimerPhoto(0);
                } else if (is_video_or_photo == 1) {
                    mCameraView.cameraInstance().setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                }
            }
        });

        //Preset_1 Camera Click
        rv_camera_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_preset == 0) {
                    open_filter();
                }
                rv_camera_1.setCardBackgroundColor(getResources().getColor(R.color.color_chooser_cirlce));
                rv_camera_2.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                rv_camera_3.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                //rv_camera_4.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                preset1 = 1;
                isResolution = 1;
                isSetAngle = 1;
                if (sharedPreferences.getInt("preset1", -1) != -1) {
                    filterPreset1Pos = sharedPreferences.getInt("preset1", -1);
                    progress = sharedPreferences.getFloat("progress1", 0.8f);
                    intensity = progress;
                    selectedProgress = sharedPreferences.getInt("selectProgress1", 80);
                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, filterPreset1Pos, left_h * 46 / 100);
                    mFilterListView.setAdapter(mAdapter);
                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPreset1Pos];
                    showText1(filterPreset1Pos, null);
                    try {
                        if (filterPreset1Pos != 0) {
                            seekBar.setProgress(selectedProgress);
                        } else {
                            seekBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                    }
                    try {
                        mCameraView.post(new Runnable() {
                            public void run() {
                                try {
                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPreset1Pos]);
                                    mCameraView.setFilterIntensity(progress);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e2) {
                    }
                } else {
                    filterPos = 0;
                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, 0, left_h * 46 / 100);
                    mFilterListView.setAdapter(mAdapter);
                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPos];
                    showText1(filterPos, null);
                    try {
                        if (filterPos != 0) {
                            seekBar.setProgress(80);
                        } else {
                            seekBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                    }
                    try {
                        mCameraView.post(new Runnable() {
                            public void run() {
                                try {
                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPos]);
                                    mCameraView.setFilterIntensity(0.8f);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e2) {
                    }
                }
                if (is_video_or_photo != 2) {
                    if (is_front == 1) {
                        cvDialog.setVisibility(View.VISIBLE);
                        ll_resolution.setVisibility(View.VISIBLE);
                    } else {
                        cvDialog.setVisibility(View.GONE);
                        ll_resolution.setVisibility(View.GONE);
                    }
                    if (sharedPreferences.getInt("resolution1", -1) != 0) {
                        addResolution = sharedPreferences.getInt("resolution1", -1);
                        setAddResolution(addResolution);
                    } else {
                        setAddResolution(0);
                    }
                }
                if (sharedPreferences.getInt("angle1", -1) != 0) {
                    addAngle = sharedPreferences.getInt("angle1", -1);
                    setAddAngle(addAngle);
                } else {
                    setAddAngle(2);
                }
            }
        });

        //Preset_2 Camera Click
        rv_camera_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_preset == 0) {
                    open_filter();
                }
                rv_camera_1.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                rv_camera_2.setCardBackgroundColor(getResources().getColor(R.color.color_chooser_cirlce));
                rv_camera_3.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                //rv_camera_4.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                preset1 = 2;
                isResolution = 2;
                isSetAngle = 2;
                if (sharedPreferences.getInt("preset2", -1) != -1) {
                    filterPreset2Pos = sharedPreferences.getInt("preset2", -1);
                    progress = sharedPreferences.getFloat("progress2", 0.8f);
                    intensity = progress;
                    selectedProgress = sharedPreferences.getInt("selectProgress2", 80);
                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, filterPreset2Pos, left_h * 46 / 100);
                    mFilterListView.setAdapter(mAdapter);
                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPreset2Pos];
                    showText1(filterPreset2Pos, null);
                    try {
                        if (filterPreset2Pos != 0) {
                            seekBar.setProgress(selectedProgress);
                        } else {
                            seekBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                    }
                    try {
                        mCameraView.post(new Runnable() {
                            public void run() {
                                try {
                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPreset2Pos]);
                                    mCameraView.setFilterIntensity(progress);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e2) {
                    }
                } else {
                    filterPos = 0;
                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, 0, left_h * 46 / 100);
                    mFilterListView.setAdapter(mAdapter);
                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPos];
                    showText1(filterPos, null);
                    try {
                        if (filterPos != 0) {
                            seekBar.setProgress(80);
                        } else {
                            seekBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                    }
                    try {
                        mCameraView.post(new Runnable() {
                            public void run() {
                                try {
                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPos]);
                                    mCameraView.setFilterIntensity(0.8f);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e2) {
                    }
                }

                if (is_video_or_photo != 2) {
                    if (is_front == 1) {
                        cvDialog.setVisibility(View.VISIBLE);
                        ll_resolution.setVisibility(View.VISIBLE);
                    } else {
                        cvDialog.setVisibility(View.GONE);
                        ll_resolution.setVisibility(View.GONE);
                    }
                    if (sharedPreferences.getInt("resolution2", -1) != 0) {
                        addResolution = sharedPreferences.getInt("resolution2", -1);
                        setAddResolution(addResolution);
                    } else {
                        setAddResolution(0);
                    }
                }
                if (sharedPreferences.getInt("angle2", -1) != 0) {
                    addAngle = sharedPreferences.getInt("angle2", -1);
                    setAddAngle(addAngle);
                } else {
                    setAddAngle(2);
                }
            }
        });

        //Preset_3 Camera Click
        rv_camera_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_preset == 0) {
                    open_filter();
                }
                rv_camera_1.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                rv_camera_2.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                rv_camera_3.setCardBackgroundColor(getResources().getColor(R.color.color_chooser_cirlce));
                //rv_camera_4.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                preset1 = 3;
                isResolution = 3;
                isSetAngle = 3;
                if (sharedPreferences.getInt("preset3", -1) != -1) {
                    filterPreset3Pos = sharedPreferences.getInt("preset3", -1);
                    progress = sharedPreferences.getFloat("progress3", 0.8f);
                    intensity = progress;
                    selectedProgress = sharedPreferences.getInt("selectProgress3", 80);
                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, filterPreset3Pos, left_h * 46 / 100);
                    mFilterListView.setAdapter(mAdapter);
                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPreset3Pos];
                    showText1(filterPreset3Pos, null);
                    try {
                        if (filterPreset3Pos != 0) {
                            seekBar.setProgress(selectedProgress);
                        } else {
                            seekBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                    }
                    try {
                        mCameraView.post(new Runnable() {
                            public void run() {
                                try {
                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPreset3Pos]);
                                    mCameraView.setFilterIntensity(progress);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e2) {
                    }
                } else {
                    filterPos = 0;
                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, 0, left_h * 46 / 100);
                    mFilterListView.setAdapter(mAdapter);
                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPos];
                    showText1(filterPos, null);
                    try {
                        if (filterPos != 0) {
                            seekBar.setProgress(80);
                        } else {
                            seekBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                    }
                    try {
                        mCameraView.post(new Runnable() {
                            public void run() {
                                try {
                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPos]);
                                    mCameraView.setFilterIntensity(0.8f);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e2) {
                    }
                }

                if (is_video_or_photo != 2) {
                    if (is_front == 1) {
                        cvDialog.setVisibility(View.VISIBLE);
                        ll_resolution.setVisibility(View.VISIBLE);
                    } else {
                        cvDialog.setVisibility(View.GONE);
                        ll_resolution.setVisibility(View.GONE);
                    }
                    if (sharedPreferences.getInt("resolution3", -1) != 0) {
                        addResolution = sharedPreferences.getInt("resolution3", -1);
                        setAddResolution(addResolution);
                    } else {
                        setAddResolution(0);
                    }
                }
                if (sharedPreferences.getInt("angle3", -1) != 0) {
                    addAngle = sharedPreferences.getInt("angle3", -1);
                    setAddAngle(addAngle);
                } else {
                    setAddAngle(2);
                }
            }
        });

        //Preset_4 Camera Click
//        rv_camera_4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (is_preset == 0) {
//                    open_filter();
//                }
//                rv_camera_1.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
//                rv_camera_2.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
//                rv_camera_3.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
//                //rv_camera_4.setCardBackgroundColor(getResources().getColor(R.color.color_chooser_cirlce));
//                preset1 = 4;
//                isResolution = 4;
//                isSetAngle = 4;
//                if (sharedPreferences.getInt("preset4", -1) != -1) {
//                    filterPreset4Pos = sharedPreferences.getInt("preset4", -1);
//                    progress = sharedPreferences.getFloat("progress4", 0.8f);
//                    intensity = progress;
//
//                    selectedProgress = sharedPreferences.getInt("selectProgress4", 80);
//                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, filterPreset4Pos, left_h * 46 / 100);
//                    mFilterListView.setAdapter(mAdapter);
//                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPreset4Pos];
//                    showText1(filterPreset4Pos, null);
//                    try {
//                        if (filterPreset4Pos != 0) {
//                            seekBar.setProgress(selectedProgress);
//                        } else {
//                            seekBar.setVisibility(View.INVISIBLE);
//                        }
//                    } catch (Exception e) {
//                    }
//                    try {
//                        mCameraView.post(new Runnable() {
//                            public void run() {
//                                try {
//                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPreset4Pos]);
//                                    mCameraView.setFilterIntensity(progress);
//                                } catch (Exception e) {
//                                }
//                            }
//                        });
//                    } catch (Exception e2) {
//                    }
//                } else {
//                    filterPos = 0;
//                    mAdapter = new FilterAdapter(CameraActivity.this, MainActivity.imageFilter2, 0, left_h * 46 / 100);
//                    mFilterListView.setAdapter(mAdapter);
//                    mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPos];
//                    showText1(filterPos, null);
//                    try {
//                        if (filterPos != 0) {
//                            seekBar.setProgress(80);
//                        } else {
//                            seekBar.setVisibility(View.INVISIBLE);
//                        }
//                    } catch (Exception e) {
//                    }
//                    try {
//                        mCameraView.post(new Runnable() {
//                            public void run() {
//                                try {
//                                    mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPos]);
//                                    mCameraView.setFilterIntensity(0.8f);
//                                } catch (Exception e) {
//                                }
//                            }
//                        });
//                    } catch (Exception e2) {
//                    }
//                }
//
//                if (is_video_or_photo != 2) {
//                    if (is_front == 1) {
//                        cvDialog.setVisibility(View.VISIBLE);
//                        ll_resolution.setVisibility(View.VISIBLE);
//                    } else {
//                        cvDialog.setVisibility(View.GONE);
//                        ll_resolution.setVisibility(View.GONE);
//                    }
//                    if (sharedPreferences.getInt("resolution4", -1) != 0) {
//                        addResolution = sharedPreferences.getInt("resolution4", -1);
//                        setAddResolution(addResolution);
//                    } else {
//                        setAddResolution(0);
//                    }
//                }
//                if (sharedPreferences.getInt("angle3", -1) != 0) {
//                    addAngle = sharedPreferences.getInt("angle3", -1);
//                    setAddAngle(addAngle);
//                } else {
//                    setAddAngle(2);
//                }
//            }
//        });

        ll_1080_60fps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mp = MediaPlayer.create(CameraActivity.this, R.raw.camera_focus);
                    mp.start();
                    ll_1080_60fps.setBackgroundResource(R.drawable.bg_resolution_back);
                    ll_1080_30fps.setBackgroundResource(0);
                    ll_4k_60fps.setBackgroundResource(0);
                    ll_4k_30fps.setBackgroundResource(0);
                    resolution = 2;
                    if (is_video_or_photo == 1) {
                        setAddResolution(resolution);
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "1080/60fps Not Supported in this device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_1080_240fps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mp = MediaPlayer.create(CameraActivity.this, R.raw.camera_focus);
                    mp.start();
                    ll_1080_240fps.setBackgroundResource(R.drawable.bg_resolution_back);
                    ll_1080_60fps.setBackgroundResource(0);
                    ll_1080_30fps.setBackgroundResource(0);
                    ll_4k_60fps.setBackgroundResource(0);
                    ll_4k_30fps.setBackgroundResource(0);
                    resolution = 5;
                    if (is_video_or_photo == 1) {
                        setAddResolution(resolution);
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "1080/240fps Not Supported in this device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_1080_30fps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = MediaPlayer.create(CameraActivity.this, R.raw.camera_focus);
                mp.start();
                ll_1080_240fps.setBackgroundResource(0);
                ll_1080_60fps.setBackgroundResource(0);
                ll_1080_30fps.setBackgroundResource(R.drawable.bg_resolution_back);
                ll_4k_60fps.setBackgroundResource(0);
                ll_4k_30fps.setBackgroundResource(0);
                resolution = 1;
                if (is_video_or_photo == 1) {
                    setAddResolution(resolution);
                }
            }
        });

        ll_4k_60fps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    mp = MediaPlayer.create(CameraActivity.this, R.raw.camera_focus);
                    mp.start();
                    ll_1080_240fps.setBackgroundResource(0);
                    ll_1080_60fps.setBackgroundResource(0);
                    ll_1080_30fps.setBackgroundResource(0);
                    ll_4k_60fps.setBackgroundResource(R.drawable.bg_resolution_back);
                    ll_4k_30fps.setBackgroundResource(0);
                    resolution = 4;
                    if (is_video_or_photo == 1) {
                        setAddResolution(resolution);
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "4K Not Supported in this device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_4k_30fps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    mp = MediaPlayer.create(CameraActivity.this, R.raw.camera_focus);
                    mp.start();
                    ll_1080_240fps.setBackgroundResource(0);
                    ll_1080_60fps.setBackgroundResource(0);
                    ll_1080_30fps.setBackgroundResource(0);
                    ll_4k_60fps.setBackgroundResource(0);
                    ll_4k_30fps.setBackgroundResource(R.drawable.bg_resolution_back);
                    resolution = 3;
                    if (is_video_or_photo == 1) {
                        setAddResolution(resolution);
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "4K Not Supported in this device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Store Preset
        ivPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterPos != 0) {
                    if (preset1 == 1) {
                        myEdit.putInt("preset1", filterPos);
                        myEdit.putFloat("progress1", intensity);
                        myEdit.putInt("selectProgress1", selectProgress);
                        myEdit.commit();
                        preset1 = 0;
                        setResolution();
                        setAngle();
                    } else if (preset1 == 2) {
                        myEdit.putInt("preset2", filterPos);
                        myEdit.putFloat("progress2", intensity);
                        myEdit.putInt("selectProgress2", selectProgress);
                        myEdit.commit();
                        preset1 = 0;
                        setResolution();
                        setAngle();
                    } else if (preset1 == 3) {
                        myEdit.putInt("preset3", filterPos);
                        myEdit.putFloat("progress3", intensity);
                        myEdit.putInt("selectProgress3", selectProgress);
                        myEdit.commit();
                        preset1 = 0;
                        setResolution();
                        setAngle();
                    } else {
                        myEdit.putInt("preset4", filterPos);
                        myEdit.putFloat("progress4", intensity);
                        myEdit.putInt("selectProgress4", selectProgress);
                        myEdit.commit();
                        preset1 = 0;
                        setResolution();
                        setAngle();
                    }
                    Toast.makeText(CameraActivity.this, "Preset Add Successfully in Image/Video", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CameraActivity.this, "Please select filter first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.mAdapter = new FilterAdapter(this, MainActivity.imageFilter2, 0, left_h * 46 / 100);
        this.mFilterListView.setAdapter(this.mAdapter);
        this.mFilterListView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemClick(View view, final int position) {
                filterPos = position;

                mCurrentConfig = MainActivity.EFFECT_CONFIGS[filterPos];
                showText1(filterPos, null);
                try {
                    if (filterPos != 0) {
                        seekBar.setVisibility(View.VISIBLE);
                        seekBar.setProgress(80);
                        intensity = 0.8f;
                    } else {
                        seekBar.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                }
                try {
                    mCameraView.post(new Runnable() {
                        public void run() {
                            try {
                                mCameraView.setFilterWithConfig(MainActivity.EFFECT_CONFIGS[filterPos]);
                                mCameraView.setFilterIntensity(0.8f);
                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception e2) {
                }
            }
        }));

        mCameraView.setPictureSize(1920, 1440, false);
        mCameraView.setFitFullView(true);
        screenBrightness(200.0);

        // Add Power Percentage to the power View
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void savePicture() {
        if (bitmap != null) {
            BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
            bitmap = drawable.getBitmap();
            String s = ImageUtil.saveBitmap(bitmap);
            bitmap.recycle();
            Toast.makeText(CameraActivity.this, "Save picture success!", Toast.LENGTH_LONG).show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
        } else
            Toast.makeText(CameraActivity.this, "Save picture failed!", Toast.LENGTH_LONG).show();
    }

    public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float)scale;
            int batteryPctInt = (int) batteryPct;
            power_perc.setText(String.valueOf(batteryPctInt) + "%");
        }
    };

    public void openWifiCameraActivity() {
        String camAddress = "http://192.168.43.231:6677"; // your URL here

        /* Method_2: with new activity */
        Intent intent = new Intent(CameraActivity.this, WifiCameraActivity.class);
        intent.putExtra("camAddress", camAddress);
        startActivity(intent);
        finish();
    }

    public void connectWithoutPasswordToWifi(String networkSSID) {
        android.net.wifi.WifiConfiguration conf = new android.net.wifi.WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (android.net.wifi.WifiConfiguration connect : list) {

            if (connect.SSID != null && connect.SSID.equals("\"" + networkSSID + "\"")) {
                Toast.makeText(CameraActivity.this, "Wifi Connect Success", Toast.LENGTH_SHORT).show();
                wifiManager.disconnect();
                wifiManager.enableNetwork(connect.networkId, true);
                wifiManager.reconnect();
                isWifiConnected = true;
                openWifiCameraActivity();
                break;
            }
        }
    }

    //Manage Zoom Level
    public float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void handleZoom(MotionEvent event, Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCameraView.cameraInstance().setParams(params);
    }

    private void screenBrightness(double newBrightnessValue) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float newBrightness = (float) newBrightnessValue;
        lp.screenBrightness = newBrightness / (float) 255;
        getWindow().setAttributes(lp);
        changeBrightnessMode();
    }

    void changeBrightnessMode() {
        try {
            int brightnessMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }

        } catch (Exception e) {
        }
    }

    private void sendAndRequestResponse() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray heroArray = obj.getJSONArray("heroes");
                            for (int i = 0; i < heroArray.length(); i++) {
                                JSONObject heroObject = heroArray.getJSONObject(i);
                                Toast.makeText(getApplicationContext(), heroObject.getString("name"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.getMessage());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void setupWebView(WebView webView) {
        webView.setWebViewClient(new WebViewClient(){
            boolean anyError = false;
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                anyError = false;
            }

            @Override public void onReceivedError(WebView view, WebResourceRequest request,
                                                  WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Do something
                anyError = true;
                view.setVisibility(View.GONE);
                //view.setBackgroundColor(Color.BLACK);
            }

            // Load the URL
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //return super.shouldOverrideUrlLoading(view, request);
                view.loadUrl(request.getUrl().getPath());
                return false;
            }

            // ProgressBar will disappear once page is loaded
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //progressBar.setVisibility(View.GONE);
                if(anyError) {
                    view.reload();
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Set Wide,Ultra Wide,Telephoto Angle
    private void setAddAngle(int angle1) {
        if(isWifiConnected) {
//            String camAddress = "http://192.168.43.231:6677"; // your URL here
//
//            /* Method_1:  with same activity */
//
//            setupWebView(webview);
//            webview.loadUrl(camAddress);
//
//            onPause();
//            myGLSurfaceViewParent.removeAllViews();
//            myGLSurfaceViewParent.addView(webview);
//            webview.setVisibility(View.VISIBLE);
//            myGLSurfaceViewParent.setOnClickListener(null);
//            rvMain.setOnClickListener(null);
            //myGLSurfaceViewParent.addView(webview);

            //onPause();
            //mCameraView.setVisibility(View.GONE);
            //webview.setVisibility(View.VISIBLE);

            /* Method_2: with new activity */
            //Intent intent = new Intent(CameraActivity.this, WifiCameraViewActivity.class);
            //intent.putExtra("camAddress", camAddress);
            //startActivity(intent);
            //finish();




            /* Below To be archived */
//            String url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Techkiii/test.mp4"; // your URL here
//            //Uri uri = Uri.parse(url);
//            //int port = uri.getPort();
//            //mp = MediaPlayer.create(CameraActivity.this, uri);
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            // Set the transparency
////            try {
////                //mediaPlayer.setDataSource(CameraActivity.this, uri);
////                //mediaPlayer.prepare();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//            //mCameraView.setVisibility(View.GONE);
//            //getWindow().setFormat(PixelFormat.TRANSPARENT);
//            SurfaceHolder surfaceHolder = mCameraView.getHolder();
//            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
//                    mediaPlayer.setDisplay(surfaceHolder);
//                    //String url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Techkiii/test.mp4"; // your URL here
//                    try {
//                        mediaPlayer.setDataSource(url);
//                        mediaPlayer.prepare();
//                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mediaPlayer) {
//                                mediaPlayer.start();
//                            }
//                        });
//                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
//
//                }
//
//
//            });
//            mCameraView.setKeepScreenOn(true);
            //mp.setDisplay();
            //mp.start();

//            Intent intent = new Intent(CameraActivity.this, VideoViewActivity.class);
//            intent.putExtra("recordFile", url);
//            startActivity(intent);
            //finish();

            //setContentView(mv);
            //mCameraView.setSource(MjpegInputStream.read(URL));
            //mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            //mv.showFps(false);

            //MediaPlayer mediaPlayer = new MediaPlayer();
            //mediaPlayer.setAudioAttributes(
            //        new AudioAttributes.Builder()
            //                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            //                .setUsage(AudioAttributes.USAGE_MEDIA)
            //                .build()
            //);
            //try {
                //mediaPlayer.setDataSource(url);
                //mediaPlayer.prepare(); // might take long! (for buffering, etc)
            //} catch (IOException e) {
             //   e.printStackTrace();
            //}
            //mediaPlayer.start();

            return;
        }
        if (angle1 == 1) {
            if (is_camera_front) {
                is_camera_front = false;
                mCameraView.switchCamera();
                mCameraView.resumePreview();
                if(isWifiConnected) {
                    //mCameraView.
                } else {
                    mCameraView.cameraInstance().setAngles(0);
                }
                rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                is_front = 1;
            } else {
                mCameraView.resumePreview();
                mCameraView.cameraInstance().setAngles(0);
                rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                is_front = 0;
            }
            angle = 1;
            //is_front = 1;
        } else if (angle1 == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (is_camera_front) {
                    is_camera_front = false;
                    mCameraView.switchCamera();
                    mCameraView.resumePreview();
                    mCameraView.cameraInstance().setAngles(19);
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                    rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    is_front = 1;
                } else {
                    mCameraView.resumePreview();
                    mCameraView.cameraInstance().setAngles(19);
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                    rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    is_front = 0;
                }
                angle = 2;
                //is_front = 1;
            }
        } else if (angle1 == 3) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                if (is_camera_front) {
                    is_camera_front = false;
                    mCameraView.switchCamera();
                    mCameraView.resumePreview();
                    mCameraView.cameraInstance().setAngles(0);
                    rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    is_front = 1;
                } else {
                    mCameraView.resumePreview();
                    mCameraView.cameraInstance().setAngles(0);
                    rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
                    rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
                    is_front = 0;
                }
                angle = 0;
                //is_front = 1;
            } else {
                Toast.makeText(CameraActivity.this, "Not Supported on this device", Toast.LENGTH_SHORT).show();
            }
        } else if (angle1 == 4) {
            if (!is_camera_front) {
                is_camera_front = true;
                is_front = 0;
                mCameraView.switchCamera();
            } else {
                is_front = 1;
            }
            angle = 0;
           // mCameraView.resumePreview();
            //mCameraView.cameraInstance().setAngles(0);
            //rv_4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
            rv_1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
            rv_2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
            rv_3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.black)));
        }
        cvDialog.setVisibility(View.GONE);
    }

    //Set Resolution on video

    private void setAddResolution(int resolution1) {
        if (resolution1 == 1) {
            ll_1080_240fps.setBackgroundResource(0);
            ll_1080_60fps.setBackgroundResource(0);
            ll_1080_30fps.setBackgroundResource(R.drawable.bg_resolution_back);
            ll_4k_60fps.setBackgroundResource(0);
            ll_4k_30fps.setBackgroundResource(0);
            rate = 30;
            mCameraView.refreshPreview(angle);
        } else if (resolution1 == 2) {
            ll_1080_240fps.setBackgroundResource(0);
            ll_1080_60fps.setBackgroundResource(R.drawable.bg_resolution_back);
            ll_1080_30fps.setBackgroundResource(0);
            ll_4k_60fps.setBackgroundResource(0);
            ll_4k_30fps.setBackgroundResource(0);
            ll_4k_30fps.setBackgroundResource(0);
            rate = 60;
            mCameraView.refreshPreview(angle);
        } else if (resolution1 == 3) {
            ll_1080_240fps.setBackgroundResource(0);
            ll_1080_60fps.setBackgroundResource(0);
            ll_1080_30fps.setBackgroundResource(0);
            ll_4k_60fps.setBackgroundResource(0);
            ll_4k_30fps.setBackgroundResource(R.drawable.bg_resolution_back);
            if (!is_camera_front) {
                rate = 30;
                mCameraView.refreshPreview(angle);
            }
        } else if (resolution1 == 4) {
            ll_1080_240fps.setBackgroundResource(0);
            ll_1080_60fps.setBackgroundResource(0);
            ll_1080_30fps.setBackgroundResource(0);
            ll_4k_60fps.setBackgroundResource(R.drawable.bg_resolution_back);
            ll_4k_30fps.setBackgroundResource(0);
            if (!is_camera_front) {
                rate = 60;
                mCameraView.refreshPreview(angle);
            }
        } else if (resolution1 == 5) {
            ll_1080_240fps.setBackgroundResource(R.drawable.bg_resolution_back);
            ll_1080_60fps.setBackgroundResource(0);
            ll_1080_30fps.setBackgroundResource(0);
            ll_4k_60fps.setBackgroundResource(0);
            ll_4k_30fps.setBackgroundResource(0);
            if (!is_camera_front) {
                rate = 240;
                mCameraView.refreshPreview(angle);
            }
        } else {
            ll_1080_240fps.setBackgroundResource(0);
            ll_1080_60fps.setBackgroundResource(0);
            ll_1080_30fps.setBackgroundResource(R.drawable.bg_resolution_back);
            ll_4k_60fps.setBackgroundResource(0);
            ll_4k_30fps.setBackgroundResource(0);
            rate = 30;
            mCameraView.refreshPreview(angle);
        }
    }

    private void setResolution() {
        if (isResolution == 1) {
            myEdit.putInt("resolution1", resolution);
            isResolution = 0;
        } else if (isResolution == 2) {
            myEdit.putInt("resolution2", resolution);
            isResolution = 0;
        } else if (isResolution == 3) {
            myEdit.putInt("resolution3", resolution);
            isResolution = 0;
        } else if (isResolution == 4) {
            myEdit.putInt("resolution4", resolution);
            isResolution = 0;
        }
        myEdit.commit();
        cvDialog.setVisibility(View.GONE);
    }

    private void setAngle() {
        if (isSetAngle == 1) {
            myEdit.putInt("angle1", sAngle);
            isSetAngle = 0;
        } else if (isSetAngle == 2) {
            myEdit.putInt("angle2", sAngle);
            isSetAngle = 0;
        } else if (isSetAngle == 3) {
            myEdit.putInt("angle3", sAngle);
            isSetAngle = 0;
        } else if (isSetAngle == 4) {
            myEdit.putInt("angle4", sAngle);
            isSetAngle = 0;
        }
        myEdit.commit();
        cvDialog.setVisibility(View.GONE);
    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                //Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "beauty_camera"
                FileUtil.getPath()
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private File getRawImageFile() throws IOException {
        String imageFileName = "RAW_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "beauty_camera_raw"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".dng", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private void showImage(Uri imageUri) {
        try {
            File file;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                file = FileUtils.getFile(CameraActivity.this, imageUri);
            } else {
                file = new File(currentPhotoPath);
            }
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            String s = ImageUtil.saveBitmap(bitmap);
            bitmap.recycle();
            Toast.makeText(CameraActivity.this, "Save picture success!", Toast.LENGTH_LONG).show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
            rvMain.setVisibility(View.VISIBLE);
            rvImage.setVisibility(View.GONE);
        } catch (Exception e) {
            //uiHelper.toast(this, "Please select different profile picture.");
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(100, 100)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
                lastClickTime = 0;
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);

        public abstract void onDoubleClick(View v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = UCrop.getOutput(data);
                showImage(uri);
            }
        }
    }

    private void execFFmpegBinary(final String[] command) {

        Config.enableLogCallback(new LogCallback() {
            @Override
            public void apply(LogMessage message) {
                Log.e(Config.TAG, message.getText());
            }
        });
        Config.enableStatisticsCallback(new StatisticsCallback() {
            @Override
            public void apply(Statistics newStatistics) {
                Log.e(Config.TAG, String.format("frame: %d, time: %d", newStatistics.getVideoFrameNumber(), newStatistics.getTime()));
                Log.d("TAG", "Started command : ffmpeg " + Arrays.toString(command));
                Log.d("TAG", "progress : " + newStatistics.toString());
            }
        });
        Log.d("TAG", "Started command : ffmpeg " + Arrays.toString(command));

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                progressDialog.show();
            }
        });

        long executionId = com.arthenica.mobileffmpeg.FFmpeg.executeAsync(command, (executionId1, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Log.d("TAG", "Finished command : ffmpeg " + Arrays.toString(command));
                Intent intent = new Intent(CameraActivity.this, SlowMotionActivity.class);
                intent.putExtra("recordFile", filePath);
                startActivity(intent);
                h.post(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                Log.e("TAG", "Async command execution cancelled by user.");
                if (progressDialog != null) progressDialog.dismiss();
            } else {
                Log.e("TAG", String.format("Async command execution failed with returnCode=%d.", returnCode));
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        Log.e("TAG", "execFFmpegMergeVideo executionId-" + executionId);
        slow_m_count = 0;
        slow_motion.setColorFilter(ContextCompat.getColor(CameraActivity.this, R.color.white));
    }

    private void execFFmpegBinary1(final String[] command) {

        Config.enableLogCallback(new LogCallback() {
            @Override
            public void apply(LogMessage message) {
                Log.e(Config.TAG, message.getText());
            }
        });
        Config.enableStatisticsCallback(new StatisticsCallback() {
            @Override
            public void apply(Statistics newStatistics) {
                Log.e(Config.TAG, String.format("frame: %d, time: %d", newStatistics.getVideoFrameNumber(), newStatistics.getTime()));
                Log.d("TAG", "Started command : ffmpeg " + Arrays.toString(command));
                Log.d("TAG", "progress : " + newStatistics.toString());
            }
        });
        Log.d("TAG", "Started command : ffmpeg " + Arrays.toString(command));

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                progressDialog.show();
            }
        });

        long executionId = com.arthenica.mobileffmpeg.FFmpeg.executeAsync(command, (executionId1, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Log.d("TAG", "Finished command : ffmpeg " + Arrays.toString(command));
                if (filePath != null) {
                    Intent intent = new Intent(CameraActivity.this, VideoViewActivity.class);
                    intent.putExtra("recordFile", filePath);
                    startActivity(intent);
                }
                h.post(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                Log.e("TAG", "Async command execution cancelled by user.");
                if (progressDialog != null) progressDialog.dismiss();
            } else {
                Log.e("TAG", String.format("Async command execution failed with returnCode=%d.", returnCode));
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        Log.e("TAG", "execFFmpegMergeVideo executionId-" + executionId);
    }

    /**
     * Command for creating slow motion video
     */
    private void executeSlowMotionVideoCommand() {
        //File moviesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + "beauty_camera");
        //File moviesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + "Beauty_Camera");
        //boolean done = moviesDir.mkdirs();

        //Log.v(TAG, "Created");

        File moviesDir = new File(FileUtil.getPath());

        String filePrefix = "techkiii_video_slow_" + System.currentTimeMillis();
        String fileExtn = ".mp4";

        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d("TAG", "startTrim: src: " + recordFilename);
        Log.d("TAG", "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-y", "-i", recordFilename, "-filter_complex", "[0:v]setpts=2.9*PTS[v];[0:a]atempo=0.9[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", "-preset", "ultrafast", filePath};
        //ffmpeg -i input.mkv -filter_complex "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]" -map "[v]" -map "[a]" output.mkv

        //String[] complexCommand = {"-y", "-i", recordFilename, "-filter_complex", "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", filePath};
        execFFmpegBinary(complexCommand);
    }

    /**
     * Command for creating slow motion video
     */
    private void executeResolutionCommand(String resolution, String rate) {
        //File moviesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + "Techkiii");
        File moviesDir = new File(FileUtil.getPath());

        String filePrefix = "techkiii_video_" + System.currentTimeMillis();
        String fileExtn = ".mp4";

        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d("TAG", "startTrim: src: " + recordFilename);
        Log.d("TAG", "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();
        String[] commandArray = new String[]{"-y", "-i", recordFilename, "-s", resolution, "-r", rate,
                "-vcodec", "libx265", "-b:v", "2600k", "-b:a", "128k", "-ac", "2", "-ar", "44100", "-preset:v", "ultrafast", "-crf", "28", filePath};

        execFFmpegBinary1(commandArray);
    }

    /**
     * Executing ffmpeg binary
     */

    void showText1(final int msg, final String msg1) {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);
        fadeIn.setStartOffset(100);
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(1000);
        textViewFilter.setVisibility(View.VISIBLE);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                textViewFilter.startAnimation(fadeOut);
                textViewFilter.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
                if (msg1 == null) {
                    textViewFilter.setText(FilterAdapter.filterName1(msg));
                } else {
                    textViewFilter.setText(msg1);
                }
            }
        });
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                textViewFilter.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        this.textViewFilter.startAnimation(fadeIn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_demo, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            image_click_action.setImageResource(R.drawable.img_shut_camera);
            if (mCameraView.isRecording()) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + recordFilename)));
                mCameraView.endRecording(new CameraRecordGLSurfaceView.EndRecordingCallback() {
                    @Override
                    public void endRecordingOK() {
                        Log.i(LOG_TAG, "End recording OK");
                        isValid = true;
                    }
                });
                recordFilename = "";
            }
        } catch (Exception e) {
        }
        CameraInstance.getInstance().stopCamera();
        Log.i(LOG_TAG, "activity onPause...");
        mCameraView.release(null);
        mCameraView.onPause();
        mOrientationEventListener.disable();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.onResume();
        try {
            if (filterPos != 0) {
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setProgress(80);
            } else {
                seekBar.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }

        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

                @Override
                public void onOrientationChanged(int orientation) {

                    // determine our orientation based on sensor response
                    int lastOrientation = mOrientation;

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                        }
                    } else if (orientation < 315 && orientation >= 225) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                        }
                    } else if (orientation < 225 && orientation >= 135) {
                        if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                            mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                        }
                    } else { // orientation <135 && orientation > 45
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                        }
                    }

                    if (lastOrientation != mOrientation) {
                        changeRotation(mOrientation, lastOrientation);
                    }
                }
            };
        }
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void changeRotation(int orientation, int lastOrientation) {
        switch (orientation) {
            case ORIENTATION_PORTRAIT_NORMAL:
                Log.v("CameraActivity", "Orientation = 90");
                break;
            case ORIENTATION_LANDSCAPE_NORMAL:
                Log.v("CameraActivity", "Orientation = 0");
                break;
            case ORIENTATION_PORTRAIT_INVERTED:
                Log.v("CameraActivity", "Orientation = 270");
                break;
            case ORIENTATION_LANDSCAPE_INVERTED:
                Log.v("CameraActivity", "Orientation = 180");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        int style;
        style = 16974126;
        new AlertDialog.Builder(this, style).setTitle(getResources().getString(R.string.techkiii)).setIcon(R.mipmap.ic_launcher).setMessage(getResources().getString(R.string.exit_app)).setNegativeButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CameraActivity.this.finish();
            }
        }).setPositiveButton(getResources().getString(R.string.no), (dialog, which) -> dialog.cancel()).create().show();
    }
}
