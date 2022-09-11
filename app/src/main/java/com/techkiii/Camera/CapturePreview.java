package com.techkiii.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.wysaid.nativePort.CGEFrameRecorder;

public class CapturePreview extends SurfaceView implements SurfaceHolder.Callback{

    public static Bitmap mBitmap;
    SurfaceHolder holder;
    static Camera mCamera;
    protected CGEFrameRecorder mFrameRecorder;

    public CapturePreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.getSupportedPreviewSizes();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(holder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
    }
    /***
     *
     *  Take a picture and and convert it from bytes[] to Bitmap.
     *
     */

    public synchronized void setFilterWithConfig(final String config) {
        if (CapturePreview.this.mFrameRecorder != null) {
            CapturePreview.this.mFrameRecorder.setFilterWidthConfig(config);
        } else {
            Log.e("libCGE_java", "setFilterWithConfig after release!!");
        }
    }

    public static void takeAPicture(){

        Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            }
        };
        mCamera.takePicture(null, null, mPictureCallback);
    }
}
