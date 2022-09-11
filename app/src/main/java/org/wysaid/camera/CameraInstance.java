package org.wysaid.camera;

import static android.content.Context.CAMERA_SERVICE;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.core.app.NotificationManagerCompat;

import org.wysaid.view.CameraGLSurfaceView;
import org.wysaid.view.CameraRecordGLSurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraInstance {
    static final /* synthetic */ boolean $assertionsDisabled = (!CameraInstance.class.desiredAssertionStatus());
    private static final String ASSERT_MSG = "检测到CameraDevice 为 null! 请检查";
    public static final int DEFAULT_PREVIEW_RATE = 30;
    public static final String LOG_TAG = "libCGE_java";
    private static CameraInstance mThisInstance;
    private Comparator<Size> comparatorBigger = new C15731();
    private Camera mCameraDevice;
    //private android.hardware.camera2.CameraDevice mCameraDevice;
    private int mDefaultCameraID = -1;
    private CameraCharacteristics mCameraCharacteristics;
    private int mFacing = 0;
    private boolean mIsPreviewing = false;
    private Parameters mParams;
    private int mPictureHeight = 1000;
    private int mPictureWidth = 1000;
    private int mPreferPreviewHeight = 640;
    private int mPreferPreviewWidth = 640;
    private int mPreviewHeight;
    private int mPreviewWidth;

    private static final String TAG = "Recorder";
    private Button captureButton;

    /* renamed from: org.wysaid.camera.CameraInstance$1 */
    class C15731 implements Comparator<Size> {
        C15731() {
        }

        public int compare(Size lhs, Size rhs) {
            int w = rhs.width - lhs.width;
            if (w == 0) {
                return rhs.height - lhs.height;
            }
            return w;
        }
    }

    /* renamed from: org.wysaid.camera.CameraInstance$2 */
    class C15742 implements Comparator<Size> {
        C15742() {
        }

        public int compare(Size lhs, Size rhs) {
            int w = lhs.width - rhs.width;
            if (w == 0) {
                return lhs.height - rhs.height;
            }
            return w;
        }
    }

    public void setAngles(int zoomLevel){
        if (this.mCameraDevice != null) {
            mParams = mCameraDevice.getParameters();
            if (mParams != null) {
                if (mParams.isZoomSupported()) {
                    mParams.setZoom(zoomLevel);
                    mParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    mCameraDevice.setParameters(mParams);
                }
            }
        }
    }

    public void setExposure(){
        if (this.mCameraDevice != null) {
            mParams = mCameraDevice.getParameters();
            mParams.setExposureCompensation(1);
            mCameraDevice.setParameters(mParams);
        }
    }

    public interface CameraOpenCallback {
        void cameraReady();
    }

    private CameraInstance() {
    }

    public static synchronized CameraInstance getInstance() {
        CameraInstance cameraInstance;
        synchronized (CameraInstance.class) {
            if (mThisInstance == null) {
                mThisInstance = new CameraInstance();
            }
            cameraInstance = mThisInstance;
        }
        return cameraInstance;
    }

    public boolean isPreviewing() {
        return this.mIsPreviewing;
    }

    public int previewWidth() {
        return this.mPreviewWidth;
    }

    public int previewHeight() {
        return this.mPreviewHeight;
    }

    public int pictureWidth() {
        return this.mPictureWidth;
    }

    public int pictureHeight() {
        return this.mPictureHeight;
    }

    public void setPreferPreviewSize(int w, int h, int rate) {
        this.mPreferPreviewHeight = w;
        this.mPreferPreviewWidth = h;
    }

    public int getFacing() {
        return this.mFacing;
    }

    public synchronized boolean tryOpenCamera(CameraOpenCallback callback, int facing) {
        boolean z = false;
        synchronized (this) {
            Log.i("libCGE_java", "try open camera...");
            try {
                int numberOfCameras = Camera.getNumberOfCameras();
                CameraInfo cameraInfo = new CameraInfo();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == facing) {
                        this.mDefaultCameraID = i;
                        this.mFacing = facing;
                    }
                }

                stopPreview();
                if (this.mCameraDevice != null) {
                    this.mCameraDevice.release();
                }
                if (this.mDefaultCameraID >= 0) {
                    this.mCameraDevice = Camera.open(this.mDefaultCameraID);
                } else {
                    this.mCameraDevice = Camera.open();
                    this.mFacing = 0;
                }
                if (this.mCameraDevice != null) {
                    Log.i("libCGE_java", "Camera opened!");
                    try {
                        if (this.mDefaultCameraID != 1) {
                            setAngles(0);
                        }
                        initCamera(30);
                        if (callback != null) {
                            callback.cameraReady();
                        }
                        z = true;
                    } catch (Exception e) {
                        this.mCameraDevice.release();
                        this.mCameraDevice = null;
                    }
                }
            } catch (Exception e2) {
                Log.e("libCGE_java", "Open Camera Failed!");
                e2.printStackTrace();
                this.mCameraDevice = null;
            }
        }
        return z;
    }

    public class CameraItem implements java.io.Serializable {
        public int Key;
        public String Description;

        public CameraItem(int key, String desc) {
            Key = key;
            Description = desc;
        }
    }

    public synchronized void stopCamera() {
        if (this.mCameraDevice != null) {
            this.mIsPreviewing = false;
            this.mCameraDevice.stopPreview();
            this.mCameraDevice.setPreviewCallback(null);
            this.mCameraDevice.release();
            this.mCameraDevice = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean hasCamera2(Context context) {
        if (context == null) return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;
        try {
            CameraManager manager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
            String[] idList = manager.getCameraIdList();
            boolean notNull = true;
            if (idList.length == 0) {
                notNull = false;
            } else {
                for (final String str : idList) {
                    if (str == null || str.trim().isEmpty()) {
                        notNull = false;
                        break;
                    }
                    Log.e("TAG", "hasCamera2: " + idList[2]);
                    final CameraCharacteristics characteristics = manager.getCameraCharacteristics(str);
                    final int supportLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        notNull = false;
                        break;
                    }
                    if(!contains(characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES),
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)) {
                        continue;
                    }
                    mCameraCharacteristics = characteristics;
                }
            }
            return notNull;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private boolean contains(int[] ints, int requestAvailableCapabilitiesRaw) {
        return Arrays.asList(ints).contains(requestAvailableCapabilitiesRaw);
    }

    public boolean isCameraOpened() {
        return this.mCameraDevice != null;
    }

    public synchronized void startPreview(SurfaceTexture texture) {
        Log.i("libCGE_java", "Camera startPreview...");
        if (this.mIsPreviewing) {
            Log.e("libCGE_java", "Err: camera is previewing...");
        } else if (this.mCameraDevice != null) {
            try {
                this.mCameraDevice.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mCameraDevice.startPreview();
            this.mIsPreviewing = true;
        }
    }

    public synchronized void stopPreview() {
        if (this.mIsPreviewing && this.mCameraDevice != null) {
            Log.i("libCGE_java", "Camera stopPreview...");
            this.mIsPreviewing = false;
            this.mCameraDevice.stopPreview();
        }
    }

    public synchronized Parameters getParams() {
        Parameters parameters;
        if (this.mCameraDevice != null) {
            parameters = this.mCameraDevice.getParameters();

        } else if ($assertionsDisabled || this.mCameraDevice != null) {
            parameters = null;
        } else {
            throw new AssertionError(ASSERT_MSG);
        }
        return parameters;
    }

    public synchronized void setParams(Parameters param) {
        if (this.mCameraDevice != null) {
            this.mParams = param;
            this.mCameraDevice.setParameters(this.mParams);
        }
        if (!$assertionsDisabled && this.mCameraDevice == null) {
            throw new AssertionError(ASSERT_MSG);
        }
    }

    public Camera getCameraDevice() {
        return this.mCameraDevice;
    }

    public void initCamera(int previewRate) {
        if (this.mCameraDevice == null) {
            Log.e("libCGE_java", "initCamera: Camera is not opened!");
            return;
        }
        this.mParams = this.mCameraDevice.getParameters();
        for (Integer intValue : this.mParams.getSupportedPictureFormats()) {
            int fmt = intValue.intValue();
            Log.i("libCGE_java", String.format("Picture Format: %x", new Object[]{Integer.valueOf(fmt)}));
        }
        this.mParams.setPictureFormat(ImageFormat.JPEG);
        List<Size> picSizes = this.mParams.getSupportedPictureSizes();
        Size picSz = null;
        Collections.sort(picSizes, this.comparatorBigger);

        for (Size sz : picSizes)
        {
            Log.i("libCGE_java", String.format("Supported picture size: %d x %d", new Object[]{Integer.valueOf(sz.width), Integer.valueOf(sz.height)}));

            if (picSz == null || (sz.width >= this.mPictureWidth && sz.height >= this.mPictureHeight)) {
                picSz = sz;
            }
        }

        List<Size> prevSizes = this.mParams.getSupportedPreviewSizes();
        Size prevSz = null;
        Collections.sort(prevSizes, this.comparatorBigger);
        for (Size sz2 : prevSizes) {
            Log.i("libCGE_java", String.format("Supported preview size: %d x %d", new Object[]{Integer.valueOf(sz2.width), Integer.valueOf(sz2.height)}));
            if (prevSz == null || (sz2.width >= this.mPreferPreviewWidth && sz2.height >= this.mPreferPreviewHeight))
            {
                Log.e("testdata"," if");
                Log.e("testdata"," "+sz2.width+" : "+this.mPreferPreviewWidth+" : "+sz2.height+" : "+this.mPreferPreviewHeight);
                prevSz = sz2;
            }
            else
            {
                Log.e("testdata"," else");

            }
        }

        int fpsMax = 0;
        for (Integer n : this.mParams.getSupportedPreviewFrameRates()) {
            Log.i("libCGE_java", "Supported frame rate: " + n);
            if (fpsMax < n.intValue()) {
                fpsMax = n.intValue();
            }
        }

        this.mParams.setPreviewSize(prevSz.width, prevSz.height);
        this.mParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        this.mParams.setPictureSize(picSz.width, picSz.height);
        this.mParams.setPreviewFrameRate(previewRate);

        if (this.mParams.getSupportedFocusModes().contains("continuous-video")) {
            this.mParams.setFocusMode("continuous-video");
        }
        this.mParams.setPreviewFrameRate(fpsMax);
        try {
            this.mCameraDevice.setParameters(this.mParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mParams = this.mCameraDevice.getParameters();

        Size szPic = this.mParams.getPictureSize();
        Size szPrev = this.mParams.getPreviewSize();
        this.mPreviewWidth = szPrev.width;
        this.mPreviewHeight = szPrev.height;
        this.mPictureWidth = szPic.width;
        this.mPictureHeight = szPic.height;

        Log.i("libCGE_java", String.format("Camera Picture Size: %d x %d", new Object[]{Integer.valueOf(szPic.width), Integer.valueOf(szPic.height)}));
        Log.i("libCGE_java", String.format("Camera Preview Size: %d x %d", new Object[]{Integer.valueOf(szPrev.width), Integer.valueOf(szPrev.height)}));

        Log.e("sizes: preview"," : "+mPreviewHeight+" : "+mPreviewWidth);
        Log.e("sizes: picutre"," : "+mPictureHeight+" : "+mPictureWidth);
    }

    public static boolean adjustVideoSurface(int screenWidth,
                                             int screenHeight,
                                             int width,
                                             int height,
                                             CameraRecordGLSurfaceView surfaceView) {


        int preferredWidth = (width < height) ? width : height;
        int preferredHeight = (width < height) ? height : width;

        float ratioW = (float) screenWidth / preferredWidth;
        float ratioH = (float) screenHeight / preferredHeight;
        RelativeLayout.LayoutParams layoutParams;


        int displayedHeight = (int) (ratioW * preferredHeight);
        int displayedWidth = (int) (ratioH * preferredWidth);

        layoutParams = new RelativeLayout.LayoutParams(screenWidth, displayedHeight);

        if (displayedHeight > screenHeight) {
            layoutParams = new RelativeLayout.LayoutParams(displayedWidth, screenHeight);
        }

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(layoutParams);
        surfaceView.requestLayout();

        return Math.round(ratioH - ratioW) == 0;
    }

    public synchronized void setFocusMode(String focusMode) {
        if (this.mCameraDevice != null) {
            this.mParams = this.mCameraDevice.getParameters();
            if (this.mParams.getSupportedFocusModes().contains(focusMode)) {
                this.mParams.setFocusMode(focusMode);
                mCameraDevice.setParameters(this.mParams);
            }
        }
    }

    public synchronized void setPictureSize(int width, int height, boolean isBigger) {
        if (this.mCameraDevice != null) {
            CameraGLSurfaceView.mRecordWidth = width;
            CameraGLSurfaceView.mRecordHeight = height;
        }
    }

    public void focusAtPoint(float x, float y, AutoFocusCallback callback) {
        focusAtPoint(x, y, 0.2f, callback);
    }

    public synchronized void focusAtPoint(float x, float y, float radius, AutoFocusCallback callback) {
        if (this.mCameraDevice == null) {
            Log.e("libCGE_java", "Error: focus after release.");
        } else {
            this.mParams = this.mCameraDevice.getParameters();
            if (this.mParams.getMaxNumMeteringAreas() > 0) {
                int focusRadius = (int) (radius * 1000.0f);
                int left = ((int) ((x * 2000.0f) - 1000.0f)) - focusRadius;
                int top = ((int) ((y * 2000.0f) - 1000.0f)) - focusRadius;
                Rect focusArea = new Rect();
                focusArea.left = Math.max(left, NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                focusArea.top = Math.max(top, NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                focusArea.right = Math.min(left + focusRadius, 1000);
                focusArea.bottom = Math.min(top + focusRadius, 1000);
                List<Camera.Area> meteringAreas = new ArrayList();
                meteringAreas.add(new Camera.Area(focusArea, 800));
                try {
                    this.mCameraDevice.cancelAutoFocus();
                    this.mParams.setFocusMode("auto");
                    this.mParams.setFocusAreas(meteringAreas);
                    this.mCameraDevice.setParameters(this.mParams);
                    this.mCameraDevice.autoFocus(callback);
                } catch (Exception e) {
                    Log.e("libCGE_java", "Error: focusAtPoint failed: " + e.toString());
                }
            } else {
                Log.i("libCGE_java", "The device does not support metering areas...");
                try {
                    this.mCameraDevice.autoFocus(callback);
                } catch (Exception e2) {
                    Log.e("libCGE_java", "Error: focusAtPoint failed: " + e2.toString());
                }
            }
        }
    }
}
