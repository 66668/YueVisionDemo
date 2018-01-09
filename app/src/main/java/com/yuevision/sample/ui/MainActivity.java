package com.yuevision.sample.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.yuevision.sample.R;
import com.yuevision.sample.base.MyApplication;
import com.yuevision.sample.iview.IImgListener;
import com.yuevision.sample.myconfig.FaceDB;
import com.yuevision.sample.presenter.ImagePresenterImpl;
import com.yuevision.sample.utils.MLog;
import com.yuevision.sample.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CameraSurfaceView.OnCameraListener
        , View.OnTouchListener, Camera.AutoFocusCallback, IImgListener {

    private final String TAG = "SJY";

    @BindView(R.id.glsurfaceView)
    CameraGLSurfaceView mGLSurfaceView;

    @BindView(R.id.surfaceView)
    CameraSurfaceView surfaceView;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.img_state)
    ImageView img_state;
    //接口调用
    ImagePresenterImpl presenter;

    //jar库支持
    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();

    List<AFT_FSDKFace> result = new ArrayList<>();//人脸结果
    //变量
    private Camera mCamera;

    int mCameraID;
    int mCameraRotate;
    boolean mCameraMirror;
    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    Handler mHandler;
    //
    private int mWidth;
    private int mHeight;
    private int mFormat;
    //定时boolean
    private boolean isOpen = false;
    private boolean hasFace = false;
    //没有人脸，设置半透明
    Runnable stillStateRunnable = new Runnable() {
        @Override
        public void run() {
            imageView.setImageAlpha(127);

        }
    };

    //=========================================================生命周期调用的方法=========================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mGLSurfaceView.setOnTouchListener(this);
        initMyView();
        initErrorSDK_onCreate();
        initLooper();

        //状态图片更改
        img_state.setImageAlpha(255);
        imageView.setRotation(270);//后置90度
        img_state.setBackground(ContextCompat.getDrawable(MainActivity.this, R.mipmap.state_undo));

        //计时器
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!isOpen) {
                    isOpen = true;
                }
            }
        };
        timer.schedule(task, 0, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFRAbsLoop.shutdown();
        initErrorSDK_onDestroy();
    }

    //=========================================================生命周期调用的方法=========================================================

    private void initLooper() {
        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();
    }

    private void initErrorSDK_onDestroy() {
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

        ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
        Log.d(TAG, "ASAE_FSDK_UninitAgeEngine =" + err1.getCode());

        ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
        Log.d(TAG, "ASGE_FSDK_UninitGenderEngine =" + err2.getCode());
    }

    private void initErrorSDK_onCreate() {
        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
        Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
        error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
        Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + error.getCode());

        ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
        Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
        error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
        Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + error1.getCode());
    }

    private void initMyView() {
        presenter = new ImagePresenterImpl(this, this);
        mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCameraRotate = 270;//后置90 前置270
        mCameraMirror = true;//后置 true
        mWidth = 1280;//
        mHeight = 960;//
        mFormat = ImageFormat.NV21;
        mHandler = new Handler();

        surfaceView.setOnCameraListener(this);
        surfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
        surfaceView.debug_print_fps(true, false);

    }

    //=========================================================OnCameraListener的回调=========================================================
    @Override
    public Camera setupCamera() {
        mCamera = Camera.open(mCameraID);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewFormat(mFormat);

            Log.d(TAG, "获取摄像头支持的尺寸：");
            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
            }
            Log.d(TAG, "获取摄像头支持的格式：");
            for (Integer format : parameters.getSupportedPreviewFormats()) {
                Log.d(TAG, "FORMAT:" + format);
            }

            List<int[]> psRange = parameters.getSupportedPreviewFpsRange();
            for (int[] count : psRange) {
                Log.d(TAG, "T:");
                for (int data : count) {
                    Log.d(TAG, "V=" + data);
                }
            }

            mCamera.setParameters(parameters);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
        }

        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {

    }

    @Override
    public boolean startPreviewLater() {
        return false;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {

        //人脸检测
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        Log.d(TAG, "Face=" + result.size());
        for (AFT_FSDKFace face : result) {
            Log.d(TAG, "Face:" + face.toString());
        }
        //有无人脸数据
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
            } else {
                mHandler.postDelayed(stillStateRunnable, 3000);
            }
            hasFace = false;
        } else {
            hasFace = true;

        }
        //copy rects
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        //clear result.
        result.clear();
        //return the rects for render.
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        //绘制人脸框 颜色 宽度
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 5);
    }
    //=======================================================View.OnTouchListener的回调===========================================================

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CameraHelper.touchFocus(mCamera, event, v, this);
        return false;
    }

    //====================================================Camera.AutoFocusCallback回调==============================================================
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.d(TAG, "相机聚焦成功");
        }
    }


    //======================================================线程处理人脸数据============================================================

    /**
     * 识别流程
     */
    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();
        List<FaceDB.FaceRegist> mResgist = MyApplication.getInstance().mFaceDB.mRegister;
        //        List<ASAE_FSDKFace> face1 = new ArrayList<>();
        //        List<ASGE_FSDKFace> face2 = new ArrayList<>();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {
            MLog.d("发送数据1");
            if (mImageNV21 != null) {
                long time = System.currentTimeMillis();

                //移动端比对代码（用不到）
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                Log.d(TAG, "AFR_FSDK_ExtractFRFeature 获取人脸结果耗时 :" + (System.currentTimeMillis() - time) + "ms");
                Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());

                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;//阈值
                for (FaceDB.FaceRegist fr : mResgist) {
                    for (AFR_FSDKFace face : fr.mFaceList) {
                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                        Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                        MLog.d("截取人脸分数:" + score.getScore());
                        if (max < score.getScore()) {
                            max = score.getScore();
                        }
                    }
                }

                //截图
                byte[] data = mImageNV21;
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);

                //最终截图
                final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);

                try {
                    ops.close();//关闭流
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //移动端识别出人脸，则1s调一次接口
                if (max > 0.6f) {//本地识别（不使用）
                    //fr success.
                    mHandler.removeCallbacks(stillStateRunnable);
                    //异步显示结果
                    mHandler.post(new Runnable() {
                        @Override
                        public synchronized void run() {


                            imageView.setRotation(mCameraRotate);
                            //获取到的截图结果 显示
                            if (mCameraMirror) {
                                imageView.setScaleY(-1);
                            }
                            imageView.setImageAlpha(255);
                            imageView.setImageBitmap(bmp);

                        }
                    });
                } else {//截图 后台识别
                    if (hasFace) {//避免max=0一直调用
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public synchronized void run() {

                                imageView.setImageAlpha(255);
                                imageView.setRotation(mCameraRotate);
                                if (mCameraMirror) {
                                    imageView.setScaleY(-1);
                                }
                                imageView.setImageBitmap(bmp);

                                if (isOpen) {
                                    //发送获得的人脸数据给后台
                                    presenter.pGetImageResult(bmp);
                                    isOpen = false;
                                }
                            }
                        });
                    }
                }
                mImageNV21 = null;
            }

            //线程睡眠，减小cpu消耗
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

    //======================================================接口回调============================================================

    @Override
    public void onGetSuccess(Object object) {
        MLog.d("回调成功!");
    }

    @Override
    public void onGetFailed(String code, String result, Exception e) {
        ToastUtil.ToastShort(this, result);
    }
}
