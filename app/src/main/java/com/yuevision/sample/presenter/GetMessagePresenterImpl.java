package com.yuevision.sample.presenter;

import android.content.Context;

import com.yuevision.sample.iview.IGetMessageView;
import com.yuevision.sample.modle.GetMessageModelImpl;

/**
 * Created by sjy on 2018/1/8.
 */

public class GetMessagePresenterImpl implements IGetMessageView {
    IGetMessageView iGetMessageView;
    Context context;
    GetMessageModelImpl model;

    public GetMessagePresenterImpl(Context context, IGetMessageView iGetMessageView) {
        this.iGetMessageView = iGetMessageView;
        this.context = context;
        model = new GetMessageModelImpl(context);
    }

    public void pGetMessage(String uuid) {
        model.mGetMessage(uuid,iGetMessageView);
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
