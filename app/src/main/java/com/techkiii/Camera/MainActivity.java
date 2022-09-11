package com.techkiii.Camera;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.wysaid.common.Common;
import org.wysaid.nativePort.CGENativeLibrary;

import java.io.IOException;
import java.io.InputStream;

import com.techkiii.R;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "wysaid";
    public static String[] imageBeauty = new String[]{
            "Eyes",
            "Face",
            "Nose"
    };

    public static Integer[] imageFilter2 = new Integer[]{
            Integer.valueOf(R.drawable.img_none),
            Integer.valueOf(R.drawable.beautify1_sample),
            Integer.valueOf(R.drawable.beautify2_sample),
           /* Integer.valueOf(R.drawable.beautify3_sample),
            Integer.valueOf(R.drawable.beautify4_sample),
            Integer.valueOf(R.drawable.film_sample),
            Integer.valueOf(R.drawable.gold_vibes_sample),
            Integer.valueOf(R.drawable.lime_gourment_sample),
            Integer.valueOf(R.drawable.nature_sample),
            Integer.valueOf(R.drawable.sky_blue_sample),
            Integer.valueOf(R.drawable.vivid_sample),
            Integer.valueOf(R.drawable.black_white_sample)*/};

    public static final String[] camFilNormal = new String[]{"@beautify face 1 720 720", "@beautify face 1 600 900 @adjust lut lut37.png", "@beautify face 1 600 900 @adjust lut lut1.png", "@beautify face 1 600 900 @adjust lut lut2.png", "@beautify face 1 600 900 @adjust lut lut3.png", "@beautify face 1 600 900 @adjust lut lut4.png", "@beautify face 1 600 900 @vigblend mix 10 10 30 255 91 0 1.0 0.5 0.5 3 @curve R(0, 31)(35, 75)(81, 139)(109, 174)(148, 207)(255, 255)G(0, 24)(59, 88)(105, 146)(130, 171)(145, 187)(180, 214)(255, 255)B(0, 96)(63, 130)(103, 157)(169, 194)(255, 255)", "@beautify face 1 600 900 @adjust lut lut6.png", "@beautify face 1 600 900 @adjust lut lut7.png", "@beautify face 1 600 900 @adjust lut lut8.png", "@beautify face 1 600 900 @curve R(0, 0)(71, 74)(164, 165)(255, 255) @pixblend screen 0.94118 0.29 0.29 1 20", "@beautify face 1 600 900 @adjust lut lut10.png", "@beautify face 1 600 900 @curve G(0, 0)(101, 127)(255, 255) @pixblend colordodge 0.937 0.482 0.835 1 20", "@beautify face 1 600 900 @adjust lut lut11.png", "@beautify face 1 600 900 @adjust lut lut13.png", "@beautify face 1 600 900 @adjust lut lut14.png", "@beautify face 1 600 900 @curve R(15, 0)(92, 133)(255, 234)G(0, 20)(105, 128)(255, 255)B(0, 0)(120, 132)(255, 214)", "@beautify face 1 600 900 @adjust hsv -0.4 -0.64 -1.0 -0.4 -0.88 -0.88 @curve R(0, 0)(119, 160)(255, 255)G(0, 0)(83, 65)(163, 170)(255, 255)B(0, 0)(147, 131)(255, 255)", "@beautify face 1 600 900 @adjust lut lut17.png", "@beautify face 1 600 900 @curve B(0, 0)(70, 87)(140, 191)(255, 255) @pixblend pinlight 0.247 0.49 0.894 1 20", "@beautify face 1 600 900 @curve R(4, 35)(65, 82)(117, 148)(153, 208)(206, 255)G(13, 5)(74, 78)(109, 144)(156, 201)(250, 250)B(6, 37)(93, 104)(163, 184)(238, 222)(255, 237) @adjust hsv -0.2 -0.2 -0.44 -0.2 -0.2 -0.2", "@beautify face 1 600 900 @adjust lut lut20.png", "@beautify face 1 600 900 @adjust lut lut21.png", "@beautify face 1 600 900 @adjust lut lut22.png", "@beautify face 1 600 900 @curve R(5, 49)(85, 173)(184, 249)G(23, 35)(65, 76)(129, 145)(255, 199)B(74, 69)(158, 107)(255, 126)", "@beautify face 1 600 900 @adjust lut lut24.png", "@beautify face 1 600 900 @adjust lut lut25.png", "@beautify face 1 600 900 @adjust lut lut26.png", "@beautify face 1 600 900 @adjust lut lut27.png", "@beautify face 1 600 900 @adjust lut lut28.png", "@beautify face 1 600 900 @adjust lut lut29.png", "@beautify face 1 600 900 @adjust lut lut30.png", "@beautify face 1 600 900 @adjust lut lut31.png", "@beautify face 1 600 900 @curve R(2, 2)(16, 30)(72, 112)(135, 185)(252, 255)G(2, 1)(30, 42)(55, 84)(157, 207)(238, 249)B(1, 0)(26, 17)(67, 106)(114, 165)(231, 250)", "@beautify face 1 600 900 @adjust lut lut33.png", "@beautify face 1 600 900 @adjust lut lut34.png", "@beautify face 1 600 900 @adjust lut lut35.png", "@beautify face 1 600 900 @curve G(0, 0)(144, 166)(255, 255) @pixblend screen 0.94118 0.29 0.29 1 20", "#unpack @dynamic wave 1", "@dynamic wave 0.5", "#unpack @style sketch 0.9", "#unpack @krblend sr beffil.jpg 100 @adjust lut bfoggy_night.png", "#unpack @krblend sr beiffel_tower.jpg 100 @adjust lut bfoggy_night.png", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b1.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b2.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b3.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b4.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b5.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b6.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b7.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b8.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b9.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b10.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b11.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b12.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b13.png 100", "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b14.png 100"};

    public static final String[] EFFECT_CONFIGS = new String[]
            {
                    "@beautify face 1 720 720"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @curve R(0, 0)(71, 74)(164, 165)(255, 255) @pixblend screen 0.94118 0.29 0.29 1 20"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @vigblend mix 10 10 30 255 91 0 1.0 0.5 0.5 3 @curve R(0, 31)(35, 75)(81, 139)(109, 174)(148, 207)(255, 255)G(0, 24)(59, 88)(105, 146)(130, 171)(145, 187)(180, 214)(255, 255)B(0, 96)(63, 130)(103, 157)(169, 194)(255, 255)"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut6.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust hsv -0.4 -0.64 -1.0 -0.4 -0.88 -0.88 @curve R(0, 0)(119, 160)(255, 255)G(0, 0)(83, 65)(163, 170)(255, 255)B(0, 0)(147, 131)(255, 255)"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @curve G(0, 0)(101, 127)(255, 255) @pixblend colordodge 0.937 0.482 0.835 1 20"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png"
                    , "@beautify face 1 600 900 @curve B(0, 0)(70, 87)(140, 191)(255, 255) @pixblend pinlight 0.247 0.49 0.894 1 20"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust hsv -0.4 -0.64 -1.0 -0.4 -0.88 -0.88 @curve R(0, 0)(119, 160)(255, 255)G(0, 0)(83, 65)(163, 170)(255, 255)B(0, 0)(147, 131)(255, 255)"
                    , "#unpack @style sketch 0.9"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @curve B(0, 0)(70, 87)(140, 191)(255, 255) @pixblend pinlight 0.247 0.49 0.894 1 20"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @curve R(4, 35)(65, 82)(117, 148)(153, 208)(206, 255)G(13, 5)(74, 78)(109, 144)(156, 201)(250, 250)B(6, 37)(93, 104)(163, 184)(238, 222)(255, 237) @adjust hsv -0.2 -0.2 -0.44 -0.2 -0.2 -0.2"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut20.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut21.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut22.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @curve R(5, 49)(85, 173)(184, 249)G(23, 35)(65, 76)(129, 145)(255, 199)B(74, 69)(158, 107)(255, 126)"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut25.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut26.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut27.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut28.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut29.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut30.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut31.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @curve R(2, 2)(16, 30)(72, 112)(135, 185)(252, 255)G(2, 1)(30, 42)(55, 84)(157, 207)(238, 249)B(1, 0)(26, 17)(67, 106)(114, 165)(231, 250)"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut33.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut34.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut35.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @curve G(0, 0)(144, 166)(255, 255) @pixblend screen 0.94118 0.29 0.29 1 20"
                    , "@adjust sharpen 5 1.5 #unpack @dynamic wave 1"
                    , "@adjust sharpen 5 1.5 @dynamic wave 0.5"
                    , "#unpack @style sketch 0.9"
                    , "@adjust sharpen 5 1.5 #unpack @krblend sr beffil.jpg 100 @adjust lut bfoggy_night.png"
                    , "@adjust sharpen 5 1.5 #unpack @krblend sr beiffel_tower.jpg 100 @adjust lut bfoggy_night.png"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b1.png 50"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b2.png 100"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b3.png 100"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b4.png 100"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b5.png 100"
                    , "@adjust sharpen 5 1.5 @beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b6.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr gray_scale.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b8.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b9.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b10.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b11.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b12.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b13.png 100"
                    , "@beautify face 1 600 900 @adjust lut lut2.png #unpack @krblend sr b14.png 100"
            };

    public CGENativeLibrary.LoadImageCallback mLoadImageCallback = new CGENativeLibrary.LoadImageCallback() {

        //Notice: the 'name' passed in is just what you write in the rule, e.g: 1.jpg
        //注意， 这里回传的name不包含任何路径名， 仅为具体的图片文件名如 1.jpg
        @Override
        public Bitmap loadImage(String name, Object arg) {

            Log.i(Common.LOG_TAG, "Loading file: " + name);
            AssetManager am = getAssets();
            InputStream is;
            try {
                is = am.open(name);
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, "Can not open file " + name);
                return null;
            }

            return BitmapFactory.decodeStream(is);
        }

        @Override
        public void loadImageOK(Bitmap bmp, Object arg) {
            Log.i(Common.LOG_TAG, "Loading bitmap over, you can choose to recycle or cache");

            //The bitmap is which you returned at 'loadImage'.
            //You can call recycle when this function is called, or just keep it for further usage.
            //唯一不需要马上recycle的应用场景为 多个不同的滤镜都使用到相同的bitmap
            //那么可以选择缓存起来。
            bmp.recycle();
        }
    };

    public static class DemoClassDescription {
        String activityName;
        String title;

        DemoClassDescription(String _name, String _title) {
            activityName = _name;
            title = _title;
        }
    }

    private static final DemoClassDescription mDemos[] = new DemoClassDescription[]{
            new DemoClassDescription("BasicImageDemoActivity", "Basic Image Filter Demo"),
            new DemoClassDescription("ImageDemoActivity", "Advanced Image Filter Demo"),
            new DemoClassDescription("CameraDemoActivity", "Camera Filter Demo"),
            new DemoClassDescription("SimplePlayerDemoActivity", "Simple Player Demo"),
            new DemoClassDescription("VideoPlayerDemoActivity", "Video Player Demo"),
            new DemoClassDescription("FaceTrackingDemoActivity", "Face Tracking Demo"),
            new DemoClassDescription("TestCaseActivity", "Test Cases")
    };

    public class DemoButton extends Button implements View.OnClickListener {
        private DemoClassDescription mDemo;

        public void setDemo(DemoClassDescription demo) {
            mDemo = demo;
            setAllCaps(false);
            setText(mDemo.title);
            setOnClickListener(this);
        }

        DemoButton(Context context) {
            super(context);
        }

        @Override
        public void onClick(final View v) {
            Log.i(LOG_TAG, String.format("%s is clicked!", mDemo.title));
            Class cls;
            try {
                cls = Class.forName("org.wysaid.cgeDemo." + mDemo.activityName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            try {
                if (cls != null)
                    startActivity(new Intent(MainActivity.this, cls));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);

        LinearLayout mLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        for (DemoClassDescription demo : mDemos) {
            DemoButton btn = new DemoButton(this);
            btn.setDemo(demo);
            mLayout.addView(btn);
        }

        //The second param will be passed as the second arg of the callback function.
        //第二个参数根据自身需要设置， 将作为 loadImage 第二个参数回传
        CGENativeLibrary.setLoadImageCallback(mLoadImageCallback, null);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        System.exit(0);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        System.exit(0);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
