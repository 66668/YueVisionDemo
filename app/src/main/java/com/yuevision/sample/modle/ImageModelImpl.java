package com.yuevision.sample.modle;

import android.content.Context;

import com.yuevision.sample.base.Constants;
import com.yuevision.sample.bean.ImageResultBean;
import com.yuevision.sample.http.aosenhttp.MyHttpService_AOSEN;
import com.yuevision.sample.iview.IImgListener;
import com.yuevision.sample.utils.MLog;
import com.yuevision.sample.utils.SPUtil;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by sjy on 2018/1/8.
 */
public class ImageModelImpl {
    Context context;

    public ImageModelImpl(Context context) {
        this.context = context;
    }

    public void getImageResult(byte[] date,final IImgListener listener) {
        //需要对file进行封装

        //token和图片形式
        // 需要加入到MultipartBody中，而不是作为参数传递
        //        MultipartBody.Builder builder = new MultipartBody.Builder()
        //                .setType(MultipartBody.FORM)//表单类型
        //                .addFormDataPart("RenqunID", SPUtil.getString(Constants.GROUP_ID, ""))
        //                .addFormDataPart("AppSecret", AppSecret)
        //                .addFormDataPart("AppID", AppID);
        //        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), date);
        //        builder.addFormDataPart("avatar", "faceimg.png", photoRequestBody);
        //        List<MultipartBody.Part> parts = builder.build().parts();

        //表单和图片上传
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), date);
        MultipartBody.Part part = MultipartBody.Part.createFormData("picture", "pic.jpg", requestBody);
        String RenqunID = SPUtil.getString(Constants.GROUP_ID, "");
        int AppID = 1;
        String AppSecret = "5940e7451d88e";
        //
        MLog.d("图片参数：", RenqunID, AppSecret, AppID);
        MyHttpService_AOSEN.Builder.getHttpServer_AOSEN()
                .postImage(RenqunID, AppSecret, AppID, part)//参数上传
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageResultBean>() {
                    @Override
                    public void onCompleted() {
                        MLog.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onGetFailed("-1", "获取异常", (Exception) e);
                    }

                    @Override
                    public void onNext(ImageResultBean bean) {
                        //处理返回结果
                        if (bean.getCode().equals("1")) {
                            listener.onGetSuccess(bean.getResult());
                        } else if (bean.getCode().equals("1000")) {
                            listener.onGetFailed(bean.getCode(), "人脸服务数量不够", new Exception("见message"));
                        } else if (bean.getCode().equals("1001")) {
                            listener.onGetFailed(bean.getCode(), "格式不正确", new Exception("见message"));
                        } else if (bean.getCode().equals("1002")) {
                            listener.onGetFailed(bean.getCode(), "参数错误", new Exception("见message"));
                        } else if (bean.getCode().equals("1003")) {
                            listener.onGetFailed(bean.getCode(), "应用秘钥不正确", new Exception("见message"));
                        } else if (bean.getCode().equals("1004")) {
                            listener.onGetFailed(bean.getCode(), "操作失败", new Exception("见message"));
                        } else if (bean.getCode().equals("1007") || bean.getCode().equals("1008")) {
                            listener.onGetFailed(bean.getCode(), "不存在相关人群信息", new Exception("见message"));
                        } else if (bean.getCode().equals("1009")) {
                            listener.onGetFailed(bean.getCode(), "没有此人", new Exception("见message"));
                        } else if (bean.getCode().equals("1010") || bean.getCode().equals("1011")) {
                            listener.onGetFailed(bean.getCode(), "图片保存失败", new Exception("见message"));
                        } else if (bean.getCode().equals("1020")) {
                            listener.onGetFailed(bean.getCode(), "未上传注册图片", new Exception("见message"));
                        } else if (bean.getCode().equals("1021")) {
                            listener.onGetFailed(bean.getCode(), "图片不存在人脸", new Exception("见message"));
                            MLog.e(bean.getCode(), "图片不存在人脸");
                        } else if (bean.getCode().equals("1022")) {
                            listener.onGetFailed(bean.getCode(), "获取特征失败", new Exception("见message"));
                        } else if (bean.getCode().equals("1023")) {
                            listener.onGetFailed(bean.getCode(), "检测人脸失败", new Exception("见message"));
                        } else if (bean.getCode().equals("1100")) {
                            listener.onGetFailed(bean.getCode(), bean.getMessage(), new Exception("操作异常"));
                        }

                    }
                });

    }

}