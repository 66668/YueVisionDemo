package com.yuevision.sample.presenter;

import android.content.Context;

import com.yuevision.sample.iview.IImgListener;
import com.yuevision.sample.modle.ImageModelImpl;
import com.yuevision.sample.utils.MLog;

/**
 * Created by sjy on 2018/1/8.
 */

public class ImagePresenterImpl implements IImgListener {
    IImgListener iGetMessageView;
    Context context;
    ImageModelImpl model;

    public ImagePresenterImpl(Context context, IImgListener iGetMessageView) {
        this.iGetMessageView = iGetMessageView;
        this.context = context;
        model = new ImageModelImpl(context);
    }

    public void pGetImageResult(byte[] byteArray) {
        
        MLog.d("上传图片大小：" + byteArray.length / 1024 + "Kb");
        model.getImageResult(byteArray, iGetMessageView);


    }

    @Override
    public void onGetSuccess(Object object) {
        iGetMessageView.onGetSuccess(object);
    }

    @Override
    public void onGetFailed(String code, String result, Exception e) {
        iGetMessageView.onGetFailed(code, result, e);
    }


}
