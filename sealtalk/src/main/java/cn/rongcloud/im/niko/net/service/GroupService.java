package cn.rongcloud.im.niko.net.service;

import androidx.lifecycle.LiveData;

import java.util.List;

import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.db.model.GroupEntity;
import cn.rongcloud.im.niko.db.model.GroupExitedMemberInfo;
import cn.rongcloud.im.niko.db.model.GroupMemberInfoDes;
import cn.rongcloud.im.niko.model.AddMemberResult;
import cn.rongcloud.im.niko.model.CopyGroupResult;
import cn.rongcloud.im.niko.model.GroupInfoBean;
import cn.rongcloud.im.niko.model.GroupNoticeInfoResult;
import cn.rongcloud.im.niko.model.GroupNoticeResult;
import cn.rongcloud.im.niko.model.GroupMemberInfoResult;
import cn.rongcloud.im.niko.model.GroupResult;
import cn.rongcloud.im.niko.model.RegularClearStatusResult;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.net.ScUrl;
import cn.rongcloud.im.niko.net.SealTalkUrl;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroupService {



    @POST(SealTalkUrl.GROUP_SET_PORTRAIT_URL)
    LiveData<Result> setGroupPortraitUri(@Body RequestBody body);


    @GET(SealTalkUrl.GROUP_GET_NOTICE_INFO)
    LiveData<Result<List<GroupNoticeInfoResult>>> getGroupNoticeInfo();


    @POST(SealTalkUrl.GROUP_GET_EXITED)
    LiveData<Result<List<GroupExitedMemberInfo>>> getGroupExitedMemberInfo(@Body RequestBody body);

    @POST(SealTalkUrl.GROUP_GET_MEMBER_INFO_DES)
    LiveData<Result<GroupMemberInfoDes>> getGroupInfoDes(@Body RequestBody body);






    @POST(ScUrl.CREATE_GROUP)
    @Headers(NetConstant.JSON)
    LiveData<Result<Integer>> createGroup(@Body RequestBody body);

    @POST(ScUrl.GROUP_CHAT_INFO)
    @Headers(NetConstant.JSON)
    LiveData<Result<GroupInfoBean>> getGroupInfo(@Body RequestBody body);

    @POST(ScUrl.GROUP_CHAT_INFO)
    @Headers(NetConstant.JSON)
    LiveData<Result<GroupInfoBean>> getGroupMemberList(@Body RequestBody body);


    @POST(ScUrl.GROUP_CHAT_INVITE)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> addGroupMember(@Body RequestBody body);


    @POST(ScUrl.GROUP_CHAT_KICK)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> kickMember(@Body RequestBody body);





    @POST(ScUrl.GROUP_CHAT_END)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> dismissGroup(@Body RequestBody body);

    @POST(ScUrl.GROUP_CHAT_LEAVE)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> quitGroup(@Body RequestBody body);

    @POST(ScUrl.GROUP_CHAT_CONFIG)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> renameGroup(@Body RequestBody body);

    @POST(ScUrl.TOP_A_FRIEND_YES)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> topYes(@Body RequestBody body);

    @POST(ScUrl.TOP_A_FRIEND_NO)
    @Headers(NetConstant.JSON)
    LiveData<Result<Boolean>> topNo(@Body RequestBody body);
}
