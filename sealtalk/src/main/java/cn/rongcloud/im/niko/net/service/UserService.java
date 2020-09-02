package cn.rongcloud.im.niko.net.service;

import androidx.lifecycle.LiveData;

import java.util.List;

import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.model.LoginResult;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.niko.CommentBean;
import cn.rongcloud.im.niko.model.niko.FollowBean;
import cn.rongcloud.im.niko.model.niko.FollowRequestInfo;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.model.niko.MyLikeBean;
import cn.rongcloud.im.niko.model.niko.ProfileHeadInfo;
import cn.rongcloud.im.niko.model.niko.ProfileInfo;
import cn.rongcloud.im.niko.net.ScUrl;
import cn.rongcloud.im.niko.net.SealTalkUrl;
import cn.rongcloud.im.niko.ui.adapter.models.VIPCheckBean;
import cn.rongcloud.im.niko.ui.adapter.models.VIPConfigBean;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @GET(SealTalkUrl.GET_TOKEN)
    Call<Result<LoginResult>> getToken();




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


    @POST(ScUrl.PROFILE_GET)
    @Headers(NetConstant.JSON)
    LiveData<Result<ProfileInfo>> getUserInfo();
    /**
     * 获取黑名单信息
     *
     * @return
     */

    @POST(ScUrl.BLOCKS_LIST)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<ProfileHeadInfo>>> getFriendBlackList(@Body RequestBody body);

    /**
     * 添加到黑名单
     *
     * @param body
     * @return
     */
    @POST(ScUrl.BLOCKS_ADD)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> addToBlackList(@Body RequestBody body);

    /**
     * 移除黑名单
     *
     * @param body
     * @return
     */
    @POST(ScUrl.BLOCKS_REMOVE)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> removeFromBlackList(@Body RequestBody body);

    /**
     * 获取我的点赞列表
     * @param body
     * @return
     */
    @POST(ScUrl.LIKE_LIST)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<MyLikeBean>>> getMyLiekList(@Body RequestBody body);

    @POST(ScUrl.COMMENTS_LIST)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<CommentBean>>> getCommentList(@Body RequestBody body);

    @POST(ScUrl.CMT_ADD)
    @Headers(NetConstant.JSON)
    LiveData<Result<Integer>> cmtAdd(@Body RequestBody body);

    @POST(ScUrl.FOLLOWERS_LIST)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<FriendBean>>> getFollowerList(@Body RequestBody body);

    @POST(ScUrl.FOLLOWERS_REQUEST_LIST)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<FollowRequestInfo>>> getFollowerRequestList(@Body RequestBody body);


    @POST(ScUrl.FOLLOWINGS_ADD)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> addFollowings(@Body RequestBody body);

    @POST(ScUrl.FOLLOWINGS_REMOVE)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> removeFollowings(@Body RequestBody body);

    @POST(ScUrl.FOLLOWING_LIST)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<FollowBean>>> getFollowList(@Body RequestBody body);

    @POST(ScUrl.PROFILE_UPDATE)
    @Headers(NetConstant.JSON)
    LiveData<Result<Void>> updateProfileInfo(@Body RequestBody body);

    @POST(ScUrl.VIP_CHECK)
    @Headers(NetConstant.JSON)
    LiveData<Result<VIPCheckBean>> vipCheck();


    @POST(ScUrl.VIP_INFO)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<VIPConfigBean>>> vipInfo();


    @POST(ScUrl.HAS_SET_PASSWORD)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> hasSetPassword();


    @POST(ScUrl.LOG_OUT)
    @Headers(NetConstant.JSON)
    LiveData<Result<Void>> logout();

}
