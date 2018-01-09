package com.yuevision.sample.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.yuevision.sample.iview.IImgListener;
import com.yuevision.sample.modle.ImageModelImpl;
import com.yuevision.sample.utils.MLog;

import java.nio.ByteBuffer;

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

    public void pGetImageResult(Bitmap bmp) {
        int bytes = bmp.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bmp.copyPixelsToBuffer(buf);
        byte[] byteArray = buf.array();
        MLog.d("上传图片大小："+byteArray.length/1024+"Kb");
        model.getImageResult(byteArray, "1", "5940e7451d88e", iGetMessageView);
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
