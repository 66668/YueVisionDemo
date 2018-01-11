package com.yuevision.sample.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yuevision.sample.R;
import com.yuevision.sample.bean.PersonBean;
import com.yuevision.sample.iview.IGetMessageView;
import com.yuevision.sample.permissions.PermissionListener;
import com.yuevision.sample.permissions.PermissionsUtil;
import com.yuevision.sample.presenter.GetMessagePresenterImpl;
import com.yuevision.sample.sqlite.PersonSQL;
import com.yuevision.sample.utils.DeviceUtils;
import com.yuevision.sample.utils.MLog;
import com.yuevision.sample.utils.ToastUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sjy on 2018/1/4.
 */

public class WaitActivity extends AppCompatActivity implements IGetMessageView {
    GetMessagePresenterImpl presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_welcome);
        //读写相关 需要权限
        if (PermissionsUtil.hasPermission(this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//ACCESS_FINE_LOCATION ACCESS_COARSE_LOCATION这两个是一组，用一个判断就够了
            //授权通过
            getMessage();
        } else {
            //第一次使用该权限调用
            PermissionsUtil.requestPermission(this
                    , new PermissionListener() {
                        //授权通过
                        @Override
                        public void permissionGranted(@NonNull String[] permissions) {
                            getMessage();
                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permissions) {
                            ToastUtil.toastLong(WaitActivity.this, "请确保读写权限，否则无法正常使用");
                        }
                    }
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA);
        }

    }

    private void getMessage() {
        presenter = new GetMessagePresenterImpl(this, this);
        presenter.pGetMessage(DeviceUtils.getUniqueId(this));
    }

    /**
     * =========================================================================================================
     */

    @Override
    public void onGetSuccess(Object object) {

        //处理返回数据
        PersonSQL dao = new PersonSQL(this);
        List<PersonBean> list = (List<PersonBean>) object;
        dao.addList(list);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(WaitActivity.this, MainActivity.class);
                startActivity(intent1);
                WaitActivity.this.finish();
            }
        };
        timer.schedule(timerTask, 2100);
    }

    @Override
    public void onGetFailed(String code, String result, Exception e) {
        if (code.equals("-1")) {
            ToastUtil.ToastShort(this, "接口异常，请通知移动开发人员");
        } else if (code.equals("0")) {
            ToastUtil.toastLong(this, "调用失败，请先注册");
        } else {
            ToastUtil.toastLong(this, result);
        }
        MLog.e(code, result, e.toString());
    }
}
