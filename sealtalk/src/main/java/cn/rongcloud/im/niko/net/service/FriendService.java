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

//    /**
//     * 获取好友信息
//     *
//     * @param friendId
//     * @return
//     */
//    @GET(SealTalkUrl.GET_FRIEND_PROFILE)
//    LiveData<Result<FriendShipInfo>> getFriendInfo(@Path("friendId") String friendId);

    /**
     * 同意添加好友
     *
     * @return
     */
    @POST(SealTalkUrl.ARGEE_FRIENDS)
    LiveData<Result<Boolean>> agreeFriend(@Body RequestBody body);

    /**
     * 忽略好友请求
     *
     * @return
     */
    @POST(SealTalkUrl.INGORE_FRIENDS)
    LiveData<Result<Void>> ingoreFriend(@Body RequestBody body);


    /**
     * 申请添加好友
     *
     * @param body
     * @return
     */
    @POST(SealTalkUrl.INVITE_FRIEND)
    LiveData<Result<AddFriendResult>> inviteFriend(@Body RequestBody body);

    /**
     * 搜索好友
     *
     * @param queryMap
     * @return
     */
    @GET(SealTalkUrl.FIND_FRIEND)
    LiveData<Result<SearchFriendInfo>> searchFriend(@QueryMap(encoded = true) Map<String, String> queryMap);

    @POST(SealTalkUrl.DELETE_FREIND)
    LiveData<Result> deleteFriend(@Body RequestBody body);

    /**
     * 获取手机通讯录中的人员信息
     *
     * @param body
     * @return
     */
    @POST(SealTalkUrl.GET_CONTACTS_INFO)
    LiveData<Result<List<GetContactInfoResult>>> getContactsInfo(@Body RequestBody body);


    @POST(SealTalkUrl.GET_FRIEND_DESCRIPTION)
    LiveData<Result<FriendDescription>> getFriendDescription(@Body RequestBody body);

    /**
     * 批量删除好友
     *
     * @param body
     * @return
     */
    @POST(SealTalkUrl.MULTI_DELETE_FRIEND)
    LiveData<Result> deleteMultiFriend(@Body RequestBody body);



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
