package cn.rongcloud.im.niko.net.service;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.db.model.FriendDescription;
import cn.rongcloud.im.niko.db.model.FriendInfo;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.model.AddFriendResult;
import cn.rongcloud.im.niko.model.GetContactInfoResult;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.SearchFriendInfo;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.model.niko.ProfileInfo;
import cn.rongcloud.im.niko.net.ScUrl;
import cn.rongcloud.im.niko.net.SealTalkUrl;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface FriendService {


    /**
     * 获取所有好友信息
     *
     * @return
     */
    @POST(ScUrl.FOLLOWERS_LIST)
    @Headers(NetConstant.JSON)
    LiveData<Result<List<FriendBean>>> getAllFriendList(@Body RequestBody body);

    /**
     * 设置朋友备注和描述
     *
     * @param body
     * @return
     */
    @POST(ScUrl.SET_FRIEND_ALIAS)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> setAlias(@Body RequestBody body);


    @POST(ScUrl.TOP_A_FRIEND_YES)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> topYes(@Body RequestBody body);

    @POST(ScUrl.TOP_A_FRIEND_NO)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> topNo(@Body RequestBody body);


    /**
     * 获取好友信息
     *
     * @param friendId
     * @return
     */
    @GET(SealTalkUrl.GET_FRIEND_PROFILE)
    LiveData<Result<FriendShipInfo>> getFriendInfo(@Path("friendId") String friendId);

    @POST(ScUrl.GET_OTHER_PROFILE)
    @Headers(NetConstant.JSON)
    LiveData<Result<ProfileInfo>> getFriendInfo(@Body RequestBody body);
}
