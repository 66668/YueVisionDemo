package com.yuevision.sample.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
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
import com.yuevision.sample.bean.ImageResultBean;
import com.yuevision.sample.bean.PersonBean;
import com.yuevision.sample.dialog.OkDialog;
import com.yuevision.sample.iview.IImgListener;
import com.yuevision.sample.myconfig.FaceDB;
import com.yuevision.sample.presenter.ImagePresenterImpl;
import com.yuevision.sample.sqlite.PersonSQL;
import com.yuevision.sample.utils.MLog;

import java.io.ByteArrayOutputStream;
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

    @BindView(R.id.img_state)
    ImageView img_state;

    //接口调用
    ImagePresenterImpl presenter;
    List<ImageResultBean.Img> resultData;
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
    private final int changeSize = 50;
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
    private boolean isOpen = false;//连续识别过程中，控制发送后台时间3s已发送。
    private boolean hasFace = false;
    private boolean isTaskOver = false;//从发送数据到弹窗显示和显示结束，为一个task,没有完成就是false;
    OkDialog dialog;
    PersonSQL dao;
    List<PersonBean> messageList;

    //没有人脸，设置半透明
    Runnable stillStateRunnable = new Runnable() {
        @Override
        public void run() {

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
        //初始化
        initMyView();
        initErrorSDK_onCreate();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!isOpen) {
                    isOpen = true;
                }
            }
        };
        timer.schedule(task, 2000, 2000);
        initLooper();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFRAbsLoop.shutdown();
        initErrorSDK_onDestroy();
    }

    //=========================================================生命周期调用的方法=========================================================
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
        //状态图片更改
        img_state.setImageAlpha(255);
        img_state.setBackground(ContextCompat.getDrawable(MainActivity.this, R.mipmap.state_undo));
        //初始状态
        isTaskOver = true;
        isOpen = false;
        //获取所有sqlite数据
        dao = new PersonSQL(this);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                messageList = dao.getList();
                if (messageList == null || messageList.size() <= 0) {
                    MLog.d("获取数据库数据失败");
                    messageList = new ArrayList<>();
                }
            }
        });

    }

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
        MLog.d("onPreview", "FaceSize=" + result.size());
        for (AFT_FSDKFace face : result) {
            MLog.d("onPreview", "Rect大小:" + face.toString());
        }
        //有无人脸数据
        if (mImageNV21 == null) {
            //重新拿到相机人脸数据
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
            } else {
                mHandler.postDelayed(stillStateRunnable, 3000);
            }
            hasFace = true;
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

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        /**
         * loop个人限制说明：（1）isTaskOver：当一次task任务完成，才进行下一次task取数据（task总耗时没有限制，与网络有关）、
         * task:发送后台数据，返回数据，显示结果，为一次task
         * （2）hasFace：在取数据过程中，有人脸才触发发送，没人脸loop继续循环，不执行发送操作
         */
        @Override
        public void loop() {
            if (mImageNV21 != null && isTaskOver) {

                //截图并修正截图大小（外扩50）
                byte[] data = mImageNV21;
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                Rect cropSitRect = mAFT_FSDKFace.getRect();

                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;

                if (cropSitRect.left < changeSize) {
                    left = cropSitRect.left;
                } else {
                    left = cropSitRect.left - changeSize;
                }
                if (cropSitRect.top < changeSize) {
                    top = cropSitRect.top;
                } else {
                    top = cropSitRect.top - changeSize;
                }

                if (cropSitRect.right > surfaceView.getWidth() - changeSize) {
                    right = cropSitRect.right;
                } else {
                    right = cropSitRect.right - changeSize;
                }

                if (cropSitRect.bottom > surfaceView.getHeight() - changeSize) {
                    bottom = cropSitRect.bottom;
                } else {
                    bottom = cropSitRect.bottom - changeSize;
                }
                cropSitRect.set(left, top, right, bottom);
                yuv.compressToJpeg(cropSitRect, 80, ops);

                //最终截图
                final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);

                try {
                    ops.close();//关闭流

                    if (hasFace) {//避免max=0一直调用
                        MLog.d("hasFace");
                        final Bitmap newBmp = adjustPhotoRotation(bmp, mCameraRotate);

                        // 图片流
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        newBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        final byte[] byteArray = baos.toByteArray();

                        baos.close();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public synchronized void run() {//synchronized
                                //发送获得的人脸数据给后台
                                if (isOpen) {
                                    isTaskOver = false;
                                    presenter.pGetImageResult(byteArray);
                                    isOpen = false;
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mImageNV21 = null;
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }

    }

    //旋转图片 根据获取的bitmap的角度修改
    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);

            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    //======================================================接口回调============================================================

    @Override
    public void onGetSuccess(Object object) {
        //接口识别返回
        resultData = (List<ImageResultBean.Img>) object;

        int score = (int) (resultData.get(0).getScore() * 100);//分数转换
        String faceid = resultData.get(0).getFaceId();

        MLog.d("score=" + score, "faceid=" + faceid);
        if (score > 90) {
            int i = 0;
            int length = messageList.size();
            for (i = 0; i < length; i++) {
                String name = "";
                String imgUrl = "";
                String currentfaceid = messageList.get(i).getFaceid().get(0);
                if (i == (length - 1)) {
                    show("无此人", false, "");
                }
                if (currentfaceid.equals(faceid)) {
                    name = messageList.get(i).getName();
                    imgUrl = messageList.get(i).getFaceimage().get(0);

                    show("您好，" + name, true, imgUrl);
                    break;
                } else {
                    continue;
                }

            }


        } else {
            isTaskOver = true;
        }
    }

    @Override
    public void onGetFailed(String code, String result, Exception e) {
        show("未识别", false, "");
        MLog.e(result);
    }

    private void show(String name, boolean isOk, String urlImg) {
        //有弹窗提示，则改变state的图标
        img_state.setBackground(ContextCompat.getDrawable(this, R.mipmap.state_done));
        //显示2s关闭
        dialog = new OkDialog.Builder(MainActivity.this)
                .setName(name)
                .setOk(isOk)
                .setImgUrl(urlImg)
                .build();
        dialog.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                isTaskOver = true;
                img_state.setBackground(ContextCompat.getDrawable(MainActivity.this, R.mipmap.state_undo));
            }
        }, 3000);

    }

}
