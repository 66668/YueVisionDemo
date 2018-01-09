package com.yuevision.sample.http.aosenhttp;

import com.yuevision.sample.bean.ImageResultBean;
import com.yuevision.sample.http.URLUtils;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by jingbin on 16/11/21.
 * 网络请求类（一个接口一个方法）
 * <p>
 * 注:使用Observable<BaseBean<AddnewReturnBean>> AddnewEmployee(@Field("Obj") String bean);形式时出现gson解析bug,没发用泛型，后续解决
 */

public interface MyHttpService_AOSEN {
    class Builder {

        /**
         */
        public static MyHttpService_AOSEN getHttpServer_AOSEN() {
            return HttpUtils_AOSEN.getInstance().getAosenServer(MyHttpService_AOSEN.class);
        }
    }


    /**
     * 上传图片流和参数
     */
    @Multipart
    @POST(URLUtils.FACE)
    Observable<ImageResultBean> postImage(
            @Part("RenqunID") String RenqunID
            , @Part("AppSecret") String AppSecret
            , @Part("AppID") String AppID
            , @Part MultipartBody.Part part);
    //    Observable<ImageResultBean> postImage(@Part List<MultipartBody.Part> list);

    /**
     * 注册
     */
    //    @POST(URLUtils.REGIST)
    //    Observable<CommonBean<LoginBean>> register(@Body RequestBody requestBody);

    /**
     * 证件号检测
     */
    //    @POST(URLUtils.CHECK_IDNUM)
    //    Observable<CommonBean> checkId(@Body RequestBody requestBody);


    //    /**
    //     *
    //     *
    //     * @return
    //     */
    //    @FormUrlEncoded
    //    @POST(URLUtils.Mine.CHANGENAME_PHOTO)
    //    Observable<CommonBean> changeNamePost(
    //            @Field("token") String token,
    //            @Field("name") String name);
    //
    //    /**
    //     *
    //     *
    //     * @return
    //     */
    //    @Multipart
    //    @POST(URLUtils.Mine.CHANGENAME_PHOTO)
    //    Observable<CommonBean<PhotoBean>> changePhotoPost(
    //            @Part List<MultipartBody.Part> list);
    //
    //
    //    /**
    //     *
    //     */
    //    @GET(URLUtils.InstitutionOrFilter.AREA)
    //    Observable<InstitutionListAreaBean> getHotData();


    //    /**
    //     * 添加访客
    //     * <p>
    //     * 文本和图片上传
    //     * post
    //     *
    //     * @return
    //     */
    //    @Multipart
    //    @POST(URLUtils.ADD_VISITOR)
    //    Observable<BaseBean> addVisitor(
    //            @Part("obj") String obj
    //            , @Part MultipartBody.Part file);
}