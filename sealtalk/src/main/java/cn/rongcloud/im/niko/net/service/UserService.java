package cn.rongcloud.im.niko.net.service;

import androidx.lifecycle.LiveData;

import java.util.List;

import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.db.model.FriendBlackInfo;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.model.ContactGroupResult;
import cn.rongcloud.im.niko.model.GetPokeResult;
import cn.rongcloud.im.niko.model.LoginResult;
import cn.rongcloud.im.niko.model.RegionResult;
import cn.rongcloud.im.niko.model.RegisterResult;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.UploadTokenResult;
import cn.rongcloud.im.niko.model.VerifyResult;
import cn.rongcloud.im.niko.model.niko.ProfileInfo;
import cn.rongcloud.im.niko.net.ScUrl;
import cn.rongcloud.im.niko.net.SealTalkUrl;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @POST(SealTalkUrl.LOGIN)
    LiveData<Result<LoginResult>> loginLiveData(@Body RequestBody body);

    @GET(SealTalkUrl.GET_TOKEN)
    Call<Result<LoginResult>> getToken();

    @GET(SealTalkUrl.GET_USER_INFO)
    LiveData<Result<UserInfo>> getUserInfo(@Path("user_id") String userId);


    @GET(SealTalkUrl.REGION_LIST)
    LiveData<Result<List<RegionResult>>> getRegionList();


    @POST(SealTalkUrl.SET_NICK_NAME)
    LiveData<Result> setMyNickName(@Body RequestBody requestBody);

    @POST(SealTalkUrl.SET_ST_ACCOUNT)
    LiveData<Result> setStAccount(@Body RequestBody requestBody);

    @POST(SealTalkUrl.SET_GENDER)
    LiveData<Result> setGender(@Body RequestBody requestBody);

    @GET(SealTalkUrl.GET_IMAGE_UPLOAD_TOKEN)
    LiveData<Result<UploadTokenResult>> getImageUploadToken();

    @POST(SealTalkUrl.SET_PORTRAIT)
    LiveData<Result> setPortrait(@Body RequestBody body);

    @POST(SealTalkUrl.CHANGE_PASSWORD)
    LiveData<Result> changePassword(@Body RequestBody body);


    /**
     * 获取黑名单信息
     *
     * @return
     */
    @GET(SealTalkUrl.GET_BLACK_LIST)
    LiveData<Result<List<FriendBlackInfo>>> getFriendBlackList();

    /**
     * 添加到黑名单
     *
     * @param body
     * @return
     */
    @POST(SealTalkUrl.ADD_BLACK_LIST)
    LiveData<Result> addToBlackList(@Body RequestBody body);

    /**
     * 移除黑名单
     *
     * @param body
     * @return
     */
    @POST(SealTalkUrl.REMOVE_BLACK_LIST)
    LiveData<Result> removeFromBlackList(@Body RequestBody body);

    /**
     * 获取通讯录中的群组列表
     *
     * @return
     */
    @GET(SealTalkUrl.GROUP_GET_ALL_IN_CONTACT)
    LiveData<Result<ContactGroupResult>> getGroupListInContact();

    /**
     * 设置接收戳一下消息状态
     *
     * @param body
     * @return
     */
    @POST(SealTalkUrl.SET_RECEIVE_POKE_MESSAGE_STATUS)
    LiveData<Result> setReceivePokeMessageStatus(@Body RequestBody body);










    @POST(ScUrl.USER_GET_SMS)
    @Headers(NetConstant.JSON)
    LiveData<Result> getSms(@Body RequestBody body);

    @POST(ScUrl.USER_VERIFY_CODE)
    @Headers(NetConstant.JSON)
    LiveData<Result> verifyCodeNiko(@Body RequestBody body);

    @POST(ScUrl.GET_IM_TOKEN)
    @Headers(NetConstant.JSON)
    LiveData<Result<String>> getIMToken();

    @POST(ScUrl.GET_OTHER_PROFILE)
    @Headers(NetConstant.JSON)
    LiveData<Result<ProfileInfo>> getUserInfo(@Body RequestBody body);

}
