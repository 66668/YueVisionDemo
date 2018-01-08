package com.yuevision.sample.modle;

import android.content.Context;

import com.yuevision.sample.base.Constants;
import com.yuevision.sample.bean.GetMessagBean;
import com.yuevision.sample.bean.PersonBean;
import com.yuevision.sample.http.MyHttpService;
import com.yuevision.sample.iview.IGetMessageView;
import com.yuevision.sample.utils.MLog;
import com.yuevision.sample.utils.SPUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by sjy on 2018/1/8.
 */

public class GetMessageModelImpl {
    Context context;

    public GetMessageModelImpl(Context context) {
        this.context = context;
    }

    public void mGetMessage(String uuid, final IGetMessageView listener) {
        MLog.d("uuid==:" + uuid);
        MyHttpService.Builder.getHttpServer().getMessage(uuid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetMessagBean<PersonBean>>() {
                    @Override
                    public void onCompleted() {
                        MLog.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onGetFailed("-1", "获取异常", (Exception) e);
                    }

                    @Override
                    public void onNext(GetMessagBean<PersonBean> bean) {
                        //处理返回结果
                        if (bean.getCode().equals("1")) {
                            listener.onGetSuccess(bean.getData());
                            //保存groupid
                            if (!bean.getGroup_id().isEmpty()) {
                                SPUtil.putString(Constants.GROUP_ID, bean.getGroup_id());
                            }
                        } else if (bean.getCode().equals("2")) {
                            listener.onGetFailed(bean.getCode(), bean.getMsg(), new Exception("见message"));
                        } else if (bean.getCode().equals("0")) {
                            listener.onGetFailed(bean.getCode(), bean.getMsg(), new Exception("见message"));
                        }
                    }
                });

    }

}