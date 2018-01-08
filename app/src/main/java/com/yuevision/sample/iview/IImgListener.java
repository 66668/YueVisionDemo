package com.yuevision.sample.iview;

/**
 * Created by sjy on 2018/1/8.
 */

public interface IImgListener {

    void onGetSuccess(Object object);

    void onGetFailed(String code, String result, Exception e);
}
