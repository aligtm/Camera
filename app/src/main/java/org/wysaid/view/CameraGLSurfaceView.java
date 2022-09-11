package org.wysaid.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.wysaid.camera.CameraInstance;
import org.wysaid.camera.CameraInstance.CameraOpenCallback;
import org.wysaid.common.Common;
import org.wysaid.common.FrameBufferObject;
import org.wysaid.nativePort.CGEFrameRecorder;
import org.wysaid.nativePort.CGENativeLibrary;
import org.wysaid.texUtils.TextureRenderer.Viewport;

public class CameraGLSurfaceView extends GLSurfaceView implements Renderer, OnFrameAvailableListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!CameraGLSurfaceView.class.desiredAssertionStatus());
    public static final String LOG_TAG = "libCGE_java";
    public ClearColor mClearColor;
    protected Viewport mDrawViewport = new Viewport();
    protected boolean mFitFullView = false;
    protected CGEFrameRecorder mFrameRecorder;
    protected long mFramesCount2 = 0;
    protected boolean mIsCameraBackForward = true;
    protected boolean mIsTransformMatrixSet = false;
    protected boolean mIsUsingMask = false;
    protected long mLastTimestamp2 = 0;
    protected float mMaskAspectRatio = 1.0f;
    public int mMaxPreviewHeight = 1280;
    public int mMaxPreviewWidth = 1280;
    public int mMaxTextureSize = 0;
    protected OnCreateCallback mOnCreateCallback;
    public static int mRecordHeight = 640;
    public static int mRecordWidth = 480;
    protected SurfaceTexture mSurfaceTexture;
    protected int mTextureID;
    protected long mTimeCount2 = 0;
    protected float[] mTransformMatrix = new float[16];
    public int mViewHeight;
    public int mViewWidth;
    public int rate;
    SurfaceHolder holder;

    /* renamed from: org.wysaid.view.CameraGLSurfaceView$1 */
    class C15761 implements Runnable {
        C15761() {
        }

        public void run() {
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glClearColor(CameraGLSurfaceView.this.mClearColor.f39r, CameraGLSurfaceView.this.mClearColor.f38g, CameraGLSurfaceView.this.mClearColor.f37b, CameraGLSurfaceView.this.mClearColor.f36a);
            GLES20.glClear(16640);
        }
    }

    /* renamed from: org.wysaid.view.CameraGLSurfaceView$2 */
    class C15782 implements Runnable {

        /* renamed from: org.wysaid.view.CameraGLSurfaceView$2$1 */
        class C15771 implements CameraOpenCallback {
            C15771() {
            }

            public void cameraReady() {
                if (!CameraGLSurfaceView.this.cameraInstance().isPreviewing()) {
                    Log.i("libCGE_java", "## switch camera -- start preview...");
                    CameraGLSurfaceView.this.cameraInstance().startPreview(CameraGLSurfaceView.this.mSurfaceTexture);
                    CameraGLSurfaceView.this.mFrameRecorder.srcResize(CameraGLSurfaceView.this.cameraInstance().previewHeight(), CameraGLSurfaceView.this.cameraInstance().previewWidth());
                }
            }
        }

        C15782() {
        }

        public void run() {
            if (CameraGLSurfaceView.this.mFrameRecorder == null) {
                Log.e("libCGE_java", "Error: switchCamera after release!!");
                return;
            }
            CameraGLSurfaceView.this.cameraInstance().stopCamera();
            int facing = CameraGLSurfaceView.this.mIsCameraBackForward ? 0 : 1;
            CameraGLSurfaceView.this.mFrameRecorder.setSrcRotation(1.5707964f);
            CameraGLSurfaceView.this.mFrameRecorder.setRenderFlipScale(1.0f, -1.0f);
            if (CameraGLSurfaceView.this.mIsUsingMask) {
                CameraGLSurfaceView.this.mFrameRecorder.setMaskTextureRatio(CameraGLSurfaceView.this.mMaskAspectRatio);
            }
            CameraGLSurfaceView.this.cameraInstance().tryOpenCamera(new C15771(), facing);
            CameraGLSurfaceView.this.requestRender();
        }
    }

    /* renamed from: org.wysaid.view.CameraGLSurfaceView$8 */
    class C15848 implements Runnable {
        C15848() {
        }

        public void run() {
            CameraGLSurfaceView.this.cameraInstance().stopPreview();
        }
    }

    /* renamed from: org.wysaid.view.CameraGLSurfaceView$9 */
    class C15859 implements CameraOpenCallback {
        C15859() {
        }

        public void cameraReady() {
            Log.i("libCGE_java", "tryOpenCamera OK...");
        }
    }

    public class ClearColor {
        /* renamed from: a */
        public float f36a;
        /* renamed from: b */
        public float f37b;
        /* renamed from: g */
        public float f38g;
        /* renamed from: r */
        public float f39r;
    }

    public interface OnCreateCallback {
        void createOver(boolean z);
    }

    public interface ReleaseOKCallback {
        void releaseOK();
    }

    public interface SetMaskBitmapCallback {
        void setMaskOK(CGEFrameRecorder cGEFrameRecorder);
    }

    public interface TakePictureCallback {
        void takePictureOK(Bitmap bitmap);
    }

    public void setFitFullView(boolean fit) {
        this.mFitFullView = fit;
        if (this.mFrameRecorder != null) {
            calcViewport();
        }
    }

    public CameraInstance cameraInstance() {
        return CameraInstance.getInstance();
    }

    public void presetCameraForward(boolean isBackForward) {
        this.mIsCameraBackForward = isBackForward;
    }

    public void presetRecordingSize(int width, int height,int rate) {
        if (width > this.mMaxPreviewWidth || height > this.mMaxPreviewHeight) {
            float scaling = Math.min(((float) this.mMaxPreviewWidth) / ((float) width), ((float) this.mMaxPreviewHeight) / ((float) height));
            width = (int) (((float) width) * scaling);
            height = (int) (((float) height) * scaling);
        }
        this.mRecordWidth = width;
        this.mRecordHeight = height;
        this.rate = rate;
        cameraInstance().setPreferPreviewSize(width, height,rate);
    }

    public synchronized void switchCamera() {
        this.mIsCameraBackForward = !this.mIsCameraBackForward;
        if (this.mFrameRecorder != null) {
            queueEvent(new C15782());
        }
    }

    public synchronized void setFilterWithConfig(final String config) {
        queueEvent(new Runnable() {
            public void run() {
                if (CameraGLSurfaceView.this.mFrameRecorder != null) {
                    CameraGLSurfaceView.this.mFrameRecorder.setFilterWidthConfig(config);
                } else {
                    Log.e("libCGE_java", "setFilterWithConfig after release!!");
                }
            }
        });
    }

    public void setFilterIntensity(final float intensity) {
        queueEvent(new Runnable() {
            public void run() {
                if (CameraGLSurfaceView.this.mFrameRecorder != null) {
                    CameraGLSurfaceView.this.mFrameRecorder.setFilterIntensity(intensity);
                } else {
                    Log.e("libCGE_java", "setFilterIntensity after release!!");
                }
            }
        });
    }

    public void setOnCreateCallback(final OnCreateCallback callback) {
        if (!$assertionsDisabled && callback == null) {
            throw new AssertionError("Invalid Operation!");
        } else if (this.mFrameRecorder == null) {
            this.mOnCreateCallback = callback;
        } else {
            queueEvent(new Runnable() {
                public void run() {
                    callback.createOver(CameraGLSurfaceView.this.cameraInstance().getCameraDevice() != null);
                }
            });
        }
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("libCGE_java", "MyGLSurfaceView Construct...");
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 8, 0);
        getHolder().setFormat(1);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        setZOrderOnTop(true );
        this.mClearColor = new ClearColor();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        boolean z = true;
        Log.i("libCGE_java", "onSurfaceCreated...");
        GLES20.glDisable(2929);
        GLES20.glDisable(2960);
        gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glBlendFunc(770, 771);
        int[] texSize = new int[1];
        GLES20.glGetIntegerv(3379, texSize, 0);
        this.mMaxTextureSize = texSize[0];
        this.mTextureID = Common.genSurfaceTextureID();
        this.mSurfaceTexture = new SurfaceTexture(this.mTextureID);
        this.mSurfaceTexture.setOnFrameAvailableListener(this);
        this.mFrameRecorder = new CGEFrameRecorder();
        this.mIsTransformMatrixSet = false;
        if (!this.mFrameRecorder.init(this.mRecordWidth, this.mRecordHeight, this.mRecordWidth, this.mRecordHeight)) {
            Log.e("libCGE_java", "Frame Recorder init failed!");
        }
        this.mFrameRecorder.setSrcRotation(1.5707964f);
        this.mFrameRecorder.setSrcFlipScale(1.0f, -1.0f);
        this.mFrameRecorder.setRenderFlipScale(1.0f, -1.0f);
        requestRender();
        if (!cameraInstance().isCameraOpened()) {
            if (!cameraInstance().tryOpenCamera(null, this.mIsCameraBackForward ? 0 : 1)) {
                Log.e("libCGE_java", "相机启动失败!!");
            }
        }
        if (this.mOnCreateCallback != null) {
            OnCreateCallback onCreateCallback = this.mOnCreateCallback;
            if (cameraInstance().getCameraDevice() == null) {
                z = false;
            }
            onCreateCallback.createOver(z);
        }
    }

    protected void calcViewport() {
        float scaling;
        int w;
        int h;
        if (this.mIsUsingMask) {
            scaling = this.mMaskAspectRatio;
        } else {
            scaling = ((float) this.mRecordWidth) / ((float) this.mRecordHeight);
        }
        float s = scaling / (((float) this.mViewWidth) / ((float) this.mViewHeight));
        if (this.mFitFullView) {
            if (((double) s) > 1.0d) {
                w = (int) (((float) this.mViewHeight) * scaling);
                h = this.mViewHeight;
            } else {
                w = this.mViewWidth;
                h = (int) (((float) this.mViewWidth) / scaling);
            }
        } else if (((double) s) > 1.0d) {
            w = this.mViewWidth;
            h = (int) (((float) this.mViewWidth) / scaling);
        } else {
            h = this.mViewHeight;
            w = (int) (((float) this.mViewHeight) * scaling);
        }
        this.mDrawViewport.width = w;
        this.mDrawViewport.height = h;
        this.mDrawViewport.f34x = (this.mViewWidth - this.mDrawViewport.width) / 2;
        this.mDrawViewport.f35y = (this.mViewHeight - this.mDrawViewport.height) / 2;
        Log.i("libCGE_java", String.format("View port: %d, %d, %d, %d", new Object[]{Integer.valueOf(this.mDrawViewport.f34x), Integer.valueOf(this.mDrawViewport.f35y), Integer.valueOf(this.mDrawViewport.width), Integer.valueOf(this.mDrawViewport.height)}));
    }

    public void setupResolution(){
        Camera.Parameters parameters = cameraInstance().getParams();

        List<Camera.Size> sizes = parameters.getSupportedVideoSizes();

        Camera.Size cs = sizes.get(1);

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
        profile.videoFrameWidth = cs.width;
        profile.videoFrameHeight = cs.height;

        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        cameraInstance().setParams(parameters);
    }

    protected void onRelease() {
    }

    public synchronized void release(final ReleaseOKCallback callback) {
        if (this.mFrameRecorder != null) {
            queueEvent(new Runnable() {
                public void run() {
                    if (CameraGLSurfaceView.this.mFrameRecorder != null) {
                        CameraGLSurfaceView.this.onRelease();
                        CameraGLSurfaceView.this.mFrameRecorder.release();
                        CameraGLSurfaceView.this.mFrameRecorder = null;
                        GLES20.glDeleteTextures(1, new int[]{CameraGLSurfaceView.this.mTextureID}, 0);
                        CameraGLSurfaceView.this.mTextureID = 0;
                        CameraGLSurfaceView.this.mSurfaceTexture.release();
                        CameraGLSurfaceView.this.mSurfaceTexture = null;
                        Log.i("libCGE_java", "GLSurfaceview release...");
                        if (callback != null) {
                            callback.releaseOK();
                        }
                    }
                }
            });
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i("libCGE_java", String.format("onSurfaceChanged: %d x %d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
       // gl.glViewport(0, 0, width, height);
        this.mViewWidth = width;
        this.mViewHeight = height;
        calcViewport();
        if (!cameraInstance().isPreviewing()) {
            cameraInstance().startPreview(this.mSurfaceTexture);
            this.mFrameRecorder.srcResize(cameraInstance().previewHeight(), cameraInstance().previewWidth());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        cameraInstance().stopCamera();
    }

    public void stopPreview() {
        queueEvent(new C15848());
    }

    public synchronized void resumePreview() {
        if (this.mFrameRecorder == null) {
            Log.e("libCGE_java", "resumePreview after release!!");
        } else {
            cameraInstance().stopCamera();
            cameraInstance().tryOpenCamera(new C15859(), this.mIsCameraBackForward ? 0 : 1);
            if (!cameraInstance().isPreviewing()) {
                cameraInstance().startPreview(this.mSurfaceTexture);
                this.mFrameRecorder.srcResize(cameraInstance().previewHeight(), cameraInstance().previewWidth());
            }
            requestRender();
        }
    }

    public synchronized void refreshPreview(int id) {
        if (this.mFrameRecorder == null) {
            Log.e("libCGE_java", "resumePreview after release!!");
        } else {
            cameraInstance().stopCamera();
            cameraInstance().tryOpenCamera(new C15859(), this.mIsCameraBackForward ? 0 : 1);
            if (!cameraInstance().isPreviewing()) {
                cameraInstance().startPreview(this.mSurfaceTexture);
                this.mFrameRecorder.srcResize(cameraInstance().previewHeight(), cameraInstance().previewWidth());
            }
            requestRender();
        }
    }

    public void onDrawFrame(GL10 gl) {
        if (this.mSurfaceTexture != null && cameraInstance().isPreviewing()) {
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            this.mSurfaceTexture.updateTexImage();
            this.mSurfaceTexture.getTransformMatrix(this.mTransformMatrix);
            this.mFrameRecorder.update(this.mTextureID, this.mTransformMatrix);
            this.mFrameRecorder.runProc();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glClear(16384);
            if (this.mIsUsingMask) {
                GLES20.glEnable(3042);
                GLES20.glBlendFunc(1, 771);
            }
            this.mFrameRecorder.render(this.mDrawViewport.f34x, this.mDrawViewport.f35y, this.mDrawViewport.width, this.mDrawViewport.height);
            GLES20.glDisable(3042);
        }
    }

    public void onResume() {
        super.onResume();
        Log.i("libCGE_java", "glsurfaceview onResume...");
    }

    public void onPause() {
        Log.i("libCGE_java", "glsurfaceview onPause in...");
        cameraInstance().stopCamera();
        super.onPause();
        Log.i("libCGE_java", "glsurfaceview onPause out...");
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
        if (this.mLastTimestamp2 == 0) {
            this.mLastTimestamp2 = System.currentTimeMillis();
        }
        long currentTimestamp = System.currentTimeMillis();
        this.mFramesCount2++;
        this.mTimeCount2 += currentTimestamp - this.mLastTimestamp2;
        this.mLastTimestamp2 = currentTimestamp;
        if (this.mTimeCount2 >= 1000) {
            Log.i("libCGE_java", String.format("camera sample rate: %d", new Object[]{Long.valueOf(this.mFramesCount2)}));
            this.mTimeCount2 %= 1000;
            this.mFramesCount2 = 0;
        }
    }

    public void takeShot(TakePictureCallback callback) {
        takeShot(callback, true);
    }

    public synchronized void takeShot(final TakePictureCallback callback, final boolean noMask) {
        if (!$assertionsDisabled && callback == null) {
            throw new AssertionError("callback must not be null!");
        } else if (this.mFrameRecorder == null) {
            Log.e("libCGE_java", "Recorder not initialized!");
            callback.takePictureOK(null);
        } else {
            queueEvent(new Runnable() {
                public void run() {
                    int bufferTexID;
                    Bitmap bmp;
                    FrameBufferObject frameBufferObject = new FrameBufferObject();
                    IntBuffer buffer;
                    if (noMask || !CameraGLSurfaceView.this.mIsUsingMask) {
                        bufferTexID = Common.genBlankTextureID(CameraGLSurfaceView.this.mRecordWidth, CameraGLSurfaceView.this.mRecordHeight);
                        frameBufferObject.bindTexture(bufferTexID);
                        GLES20.glViewport(0, 0, CameraGLSurfaceView.this.mRecordWidth, CameraGLSurfaceView.this.mRecordHeight);
                        CameraGLSurfaceView.this.mFrameRecorder.drawCache();
                        buffer = IntBuffer.allocate(CameraGLSurfaceView.this.mRecordWidth * CameraGLSurfaceView.this.mRecordHeight);
                        GLES20.glReadPixels(0, 0, CameraGLSurfaceView.this.mRecordWidth, CameraGLSurfaceView.this.mRecordHeight, 6408, 5121, buffer);
                        bmp = Bitmap.createBitmap(CameraGLSurfaceView.this.mRecordWidth, CameraGLSurfaceView.this.mRecordHeight, Config.ARGB_8888);
                        bmp.copyPixelsFromBuffer(buffer);
                        Log.i("libCGE_java", String.format("w: %d, h: %d", new Object[]{Integer.valueOf(CameraGLSurfaceView.this.mRecordWidth), Integer.valueOf(CameraGLSurfaceView.this.mRecordHeight)}));
                    } else {
                        bufferTexID = Common.genBlankTextureID(CameraGLSurfaceView.this.mDrawViewport.width, CameraGLSurfaceView.this.mDrawViewport.height);
                        frameBufferObject.bindTexture(bufferTexID);
                        int w = Math.min(CameraGLSurfaceView.this.mDrawViewport.width, CameraGLSurfaceView.this.mViewWidth);
                        int h = Math.min(CameraGLSurfaceView.this.mDrawViewport.height, CameraGLSurfaceView.this.mViewHeight);
                        CameraGLSurfaceView.this.mFrameRecorder.setRenderFlipScale(1.0f, 1.0f);
                        CameraGLSurfaceView.this.mFrameRecorder.setMaskTextureRatio(CameraGLSurfaceView.this.mMaskAspectRatio);
                        CameraGLSurfaceView.this.mFrameRecorder.render(0, 0, w, h);
                        CameraGLSurfaceView.this.mFrameRecorder.setRenderFlipScale(1.0f, -1.0f);
                        CameraGLSurfaceView.this.mFrameRecorder.setMaskTextureRatio(CameraGLSurfaceView.this.mMaskAspectRatio);
                        Log.i("libCGE_java", String.format("w: %d, h: %d", new Object[]{Integer.valueOf(w), Integer.valueOf(h)}));
                        buffer = IntBuffer.allocate(w * h);
                        GLES20.glReadPixels(0, 0, w, h, 6408, 5121, buffer);
                        bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
                        bmp.copyPixelsFromBuffer(buffer);
                    }
                    frameBufferObject.release();
                    GLES20.glDeleteTextures(1, new int[]{bufferTexID}, 0);
                    callback.takePictureOK(bmp);
                }
            });
        }
    }

    public void setPictureSize(int width, int height, boolean isBigger) {
        cameraInstance().setPictureSize(height, width, isBigger);
    }

    public synchronized void takePicture(TakePictureCallback photoCallback, ShutterCallback shutterCallback, String config, float intensity, boolean isFrontMirror) {
        Parameters params = cameraInstance().getParams();
        if (photoCallback == null || params == null) {
            Log.e("libCGE_java", "takePicture after release!");
            if (photoCallback != null) {
                photoCallback.takePictureOK(null);
            }
        } else {
            try {
                params.setRotation(90);
                cameraInstance().setParams(params);
                final boolean z = isFrontMirror;
                final String str = config;
                final float f = intensity;
                final TakePictureCallback takePictureCallback = photoCallback;
                cameraInstance().getCameraDevice().takePicture(shutterCallback, null, new PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bmp;
                        int width;
                        int height;
                        boolean shouldRotate;
                        Bitmap bmp2;
                        Size sz = camera.getParameters().getPictureSize();
                        if (sz.width != sz.height) {
                            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            width = bmp.getWidth();
                            height = bmp.getHeight();
                            if ((sz.width <= sz.height || width <= height) && (sz.width >= sz.height || width >= height)) {
                                shouldRotate = false;
                            } else {
                                shouldRotate = true;
                            }
                        } else {
                            Log.i("libCGE_java", "Cache image to get exif.");
                            try {
                                String tmpFilename = CameraGLSurfaceView.this.getContext().getExternalCacheDir() + "/picture_cache000.jpg";
                                BufferedOutputStream bufferOutStream = new BufferedOutputStream(new FileOutputStream(tmpFilename));
                                bufferOutStream.write(data);
                                bufferOutStream.flush();
                                bufferOutStream.close();
                                switch (new ExifInterface(tmpFilename).getAttributeInt("Orientation", 1)) {
                                    case 6:
                                        shouldRotate = true;
                                        break;
                                    default:
                                        shouldRotate = false;
                                        break;
                                }
                                bmp = BitmapFactory.decodeFile(tmpFilename);
                                width = bmp.getWidth();
                                height = bmp.getHeight();
                            } catch (IOException e) {
                                Log.e("libCGE_java", "Err when saving bitmap...");
                                e.printStackTrace();
                                return;
                            }
                        }
                        if (width > CameraGLSurfaceView.this.mMaxTextureSize || height > CameraGLSurfaceView.this.mMaxTextureSize) {
                            float scaling = Math.max(((float) width) / ((float) CameraGLSurfaceView.this.mMaxTextureSize), ((float) height) / ((float) CameraGLSurfaceView.this.mMaxTextureSize));
                            Log.i("libCGE_java", String.format("目标尺寸(%d x %d)超过当前设备OpenGL 能够处理的最大范围(%d x %d)， 现在将图片压缩至合理大小!", new Object[]{Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(CameraGLSurfaceView.this.mMaxTextureSize), Integer.valueOf(CameraGLSurfaceView.this.mMaxTextureSize)}));
                            bmp = Bitmap.createScaledBitmap(bmp, (int) (((float) width) / scaling), (int) (((float) height) / scaling), false);
                            width = bmp.getWidth();
                            height = bmp.getHeight();
                        }
                        Canvas canvas;
                        Matrix mat;
                        if (shouldRotate) {
                            bmp2 = Bitmap.createBitmap(height, width, Config.ARGB_8888);
                            canvas = new Canvas(bmp2);
                            int halfLen;
                            if (CameraGLSurfaceView.this.cameraInstance().getFacing() == 0) {
                                mat = new Matrix();
                                halfLen = Math.min(width, height) / 2;
                                mat.setRotate(90.0f, (float) halfLen, (float) halfLen);
                                canvas.drawBitmap(bmp, mat, null);
                            } else {
                                mat = new Matrix();
                                if (z) {
                                    mat.postTranslate((float) ((-width) / 2), (float) ((-height) / 2));
                                    mat.postScale(-1.0f, 1.0f);
                                    mat.postTranslate((float) (width / 2), (float) (height / 2));
                                    halfLen = Math.min(width, height) / 2;
                                    mat.postRotate(90.0f, (float) halfLen, (float) halfLen);
                                } else {
                                    halfLen = Math.max(width, height) / 2;
                                    mat.postRotate(-90.0f, (float) halfLen, (float) halfLen);
                                }
                                canvas.drawBitmap(bmp, mat, null);
                            }
                            bmp.recycle();
                        } else if (CameraGLSurfaceView.this.cameraInstance().getFacing() == 0) {
                            bmp2 = bmp;
                        } else {
                            bmp2 = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                            canvas = new Canvas(bmp2);
                            mat = new Matrix();
                            if (z) {
                                mat.postTranslate((float) ((-width) / 2), (float) ((-height) / 2));
                                mat.postScale(1.0f, -1.0f);
                                mat.postTranslate((float) (width / 2), (float) (height / 2));
                            } else {
                                mat.postTranslate((float) ((-width) / 2), (float) ((-height) / 2));
                                mat.postScale(-1.0f, -1.0f);
                                mat.postTranslate((float) (width / 2), (float) (height / 2));
                            }
                            canvas.drawBitmap(bmp, mat, null);
                        }
                        if (str != null) {
                            CGENativeLibrary.filterImage_MultipleEffectsWriteBack(bmp2, str, f);
                        }
                        takePictureCallback.takePictureOK(bmp2);
                        CameraGLSurfaceView.this.cameraInstance().getCameraDevice().startPreview();
                    }
                });
            } catch (Exception e) {
                Log.e("libCGE_java", "Error when takePicture: " + e.toString());
                if (photoCallback != null) {
                    photoCallback.takePictureOK(null);
                }
            }
        }
        /*Parameters params = cameraInstance().getParams();
        if (photoCallback == null || params == null) {
            Log.e("libCGE_java", "takePicture after release!");
            if (photoCallback != null) {
                photoCallback.takePictureOK(null);
            }
        } else {
            try {
                params.setRotation(90);
                cameraInstance().setParams(params);
                final boolean z = isFrontMirror;
                final String str = config;
                final float f = intensity;
                final TakePictureCallback takePictureCallback = photoCallback;
                cameraInstance().getCameraDevice().takePicture(shutterCallback, null, new PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bmp2;
                        Camera.Parameters params = cameraInstance().getParams();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        bmp2 = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        params.setRotation(90);
                        cameraInstance().setParams(params);
                        if (str != null) {
                            CGENativeLibrary.filterImage_MultipleEffectsWriteBack(bmp2, str, f);
                        }
                        takePictureCallback.takePictureOK(bmp2);
                        CameraGLSurfaceView.this.cameraInstance().getCameraDevice().startPreview();
                    }
                });
            } catch (Exception e) {
                Log.e("libCGE_java", "Error when takePicture: " + e.toString());
                if (photoCallback != null) {
                    photoCallback.takePictureOK(null);
                }
            }
        }*/
    }

    public synchronized boolean setFlashLightMode(String mode) {
        boolean z = false;
        synchronized (this) {
            if (!getContext().getPackageManager().hasSystemFeature("android.hardware.camera.flash")) {
                Log.e("libCGE_java", "No flash light is supported by current device!");
            } else if (this.mIsCameraBackForward) {
                Parameters parameters = cameraInstance().getParams();
                if (parameters != null) {
                    try {
                        if (parameters.getSupportedFlashModes().contains(mode)) {
                            parameters.setFlashMode(mode);
                            cameraInstance().setParams(parameters);
                            z = true;
                        } else {
                            Log.e("libCGE_java", "Invalid Flash Light Mode!!!");
                        }
                    } catch (Exception e) {
                        Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                    }
                }
            }
        }
        return z;
    }

    private Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            camera.cancelAutoFocus();
        }
    };

    public synchronized boolean setNightMode(String mode) {
        boolean z = false;
        synchronized (this) {
            Parameters parameters = cameraInstance().getParams();
            if (parameters != null) {
                try {
                    if (parameters.getSupportedSceneModes().contains(mode)) {
                        parameters.setSceneMode(mode);
                        cameraInstance().setParams(parameters);
                        z = true;
                    } else {
                        Log.e("libCGE_java", "Invalid Flash Light Mode!!!");
                    }
                } catch (Exception e) {
                    Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                }
            }
        }
        return z;
    }

    public void setResolution() {
        Parameters parameters = cameraInstance().getParams();
        if (parameters != null) {
            try {
                parameters.setPreviewSize(mRecordWidth,mRecordHeight);
                parameters.setPreviewFrameRate(rate);
                cameraInstance().setParams(parameters);
            } catch (Exception e) {
                Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
            }
        }
    }

    public synchronized boolean setWhiteBalance(String mode) {
        boolean z = false;
        synchronized (this) {
            Parameters parameters = cameraInstance().getParams();
            if (parameters != null) {
                try {
                    if (parameters.getSupportedWhiteBalance().contains(mode)) {
                        parameters.setWhiteBalance(mode);
                        cameraInstance().setParams(parameters);
                        z = true;
                    } else {
                        Log.e("libCGE_java", "Invalid Flash Light Mode!!!");
                    }
                } catch (Exception e) {
                    Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                }
            }
        }
        return z;
    }

    public synchronized boolean setExposureCompensation(int value) {
        boolean z = false;
        synchronized (this) {
            Parameters parameters = cameraInstance().getParams();
            if (parameters != null) {
                try {
                    parameters.setExposureCompensation(value);
                    cameraInstance().setParams(parameters);
                    z = true;
                } catch (Exception e) {
                    Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                }
            }
        }
        return z;
    }

    public synchronized boolean setMode(String mode) {
        boolean z = false;
        synchronized (this) {
            //noinspection deprecation
            Parameters parameters = cameraInstance().getParams();
            if (parameters != null) {
                try {
                    //noinspection deprecation
                    if (parameters.getSupportedSceneModes().contains(mode)) {
                        //noinspection deprecation
                        parameters.setSceneMode(mode);
                        cameraInstance().setParams(parameters);
                        z = true;
                    } else {
                        Log.e("libCGE_java", "Invalid Flash Light Mode!!!");
                    }
                } catch (Exception e) {
                    Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                }
            }
        }
        return z;
    }

    public synchronized void setColorFilter(String color) {
        synchronized (this) {
            Parameters parameters = cameraInstance().getParams();
            if (parameters != null) {
                try {
                    if (parameters.getColorEffect().contains(color)) {
                        parameters.setColorEffect(color);
                        cameraInstance().setParams(parameters);
                    } else {
                        Log.e("libCGE_java", "Invalid Flash Light Mode!!!");
                    }
                } catch (Exception e) {
                    Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                }
            }
        }
    }

    public synchronized boolean setFocusMode(String mode) {
        boolean z = false;
        synchronized (this) {
            Parameters parameters = cameraInstance().getParams();
            if (parameters != null) {
                try {
                    if (parameters.getSupportedFocusModes().contains(mode)) {
                        parameters.setFocusMode(mode);
                        cameraInstance().setParams(parameters);
                        z = true;
                    } else {
                        Log.e("libCGE_java", "Invalid Flash Light Mode!!!");
                    }
                } catch (Exception e) {
                    Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                }
            }
        }
        return z;
    }

    public synchronized boolean setFocus() {
        boolean z = false;
        synchronized (this) {
            Parameters parameters = cameraInstance().getParams();
            if (parameters != null) {
                try {
                    if (parameters.getMaxNumMeteringAreas() > 0){ // check that metering areas are supported
                        List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();

                        Rect areaRect1 = new Rect(-100, -100, 100, 100);    // specify an area in center of image
                        meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to 60%
                        Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right of image
                        meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
                        parameters.setMeteringAreas(meteringAreas);
                    }

                    cameraInstance().setParams(parameters);
                } catch (Exception e) {
                    Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
                }
            }
        }
        return z;
    }
}
