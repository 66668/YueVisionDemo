package com.yuevision.sample.http;

import com.yuevision.sample.bean.GetMessagBean;
import com.yuevision.sample.bean.PersonBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by jingbin on 16/11/21.
 * 网络请求类（一个接口一个方法）
 * <p>
 * 注:使用Observable<BaseBean<AddnewReturnBean>> AddnewEmployee(@Field("Obj") String bean);形式时出现gson解析bug,没发用泛型，后续解决
 */

public interface MyHttpService {
    class Builder {

        /**
         */
        public static MyHttpService getHttpServer() {
            return HttpUtils.getInstance().getServer(MyHttpService.class);
        }
    }


    /**
     * 初次登录，返回所有注册人员信息
     */
    @FormUrlEncoded
    @POST(URLUtils.GET_MESSAGE)
    Observable<GetMessagBean<PersonBean>> getMessage(@Field("uuid") String uuid);

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