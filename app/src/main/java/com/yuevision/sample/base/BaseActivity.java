package com.yuevision.sample.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.yuevision.sample.utils.MLog;

import java.lang.ref.WeakReference;
import java.util.List;



/**
 * actvitiy基类
 * SupportActivity 支持fragment的操作
 * 继承了 libs的拍照功能，用于拍照使用
 */

public class BaseActivity extends AppCompatActivity {
    public final Handler handler = new WeakRefHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 避免fragment切换时黑屏
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    /**
     * 用于处理 fragment多层嵌套的无返回处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int index = 0; index < fragmentManager.getFragments().size(); index++) {
            Fragment fragment = fragmentManager.getFragments().get(index); //找到第一层Fragment
            if (fragment == null)
                MLog.e("Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
            else
                handleResult(fragment, requestCode, resultCode, data);
        }
    }

    /**
     * 递归调用，对所有的子Fragment生效
     *
     * @param fragment
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("RestrictedApi")
    private void handleResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);//调用每个Fragment的onActivityResult
        List<Fragment> childFragment = fragment.getChildFragmentManager().getFragments(); //找到第二层Fragment
        if (childFragment != null)
            for (Fragment f : childFragment)
                if (f != null) {
                    handleResult(f, requestCode, resultCode, data);
                }
        if (childFragment == null)
            MLog.e("BaseActivity");
    }

    /**
     * act页面跳转简化操作
     *
     * @param newClass
     */
    public void startActivity(Class<?> newClass) {
        Intent intent = new Intent(this, newClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // (2)
    public void startActivity(Class<?> newClass, Bundle extras) {
        Intent intent = new Intent(this, newClass);
        intent.putExtras(extras);
        //退出多个Activity的程序
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * =================================================
     * ================设置异步handler操作 弱引用==================
     * =================================================
     */
    public static final int MESSAGE_TOAST = 1001;

    public class WeakRefHandler extends Handler {

        private final WeakReference<BaseActivity> mFragmentReference;

        public WeakRefHandler(BaseActivity activity) {
            mFragmentReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseActivity activity = mFragmentReference.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    /**
     * @param msg
     */
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_TOAST:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出程序的广播
        //		unRegisterExitReceiver();
    }
}
