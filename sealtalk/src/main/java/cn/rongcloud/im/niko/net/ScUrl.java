package cn.rongcloud.im.niko.net;

public class ScUrl {
    public static final String BASE_URL = "https://ttapi.alilusions.com/";


    public static final String USER_GET_SMS = "api/ScSMS/ping";
    public static final String USER_VERIFY_CODE = "api/ScSMS/VerifyCode";
    public static final String PROFILE_GET = "api/ScUser/ProfileGet";
    public static final String PROFILE_UPDATE = "api/ScUser/ProfileUpdate";
    public static final String LOG_OUT = "/api/ScUser/Logout";
    public static final String COMMENTS_LIST = "/api/ScMoment/CommentsCenter";//评论列表
    public static final String CMT_ADD = "/api/ScMoment/CmtAdd";//回复评论
    public static final String FOLLOWING_LIST = "/api/ScUser/FollowingsList";//选择@的人
    public static final String FOLLOWERS_LIST = "/api/ScUser/FriendsList";//通讯录
    public static final String FOLLOWERS_REQUEST_LIST = "/api/ScUser/FollowersListDetail";//好友请求
    public static final String FOLLOWINGS_REMOVE = "/api/ScUser/FollowingsRemove";//取消好友
    public static final String FOLLOWINGS_ADD = "/api/ScUser/FollowingsAdd";//添加关注


    public static final String GET_FRIEND_LIST ="/api/ScUser/FriendsList";//创建群的好友列表 以及通讯录

    public static final String VIP_CHECK = "/api/ScUser/VIPCheck";//VIP界面获取相关信息
    public static final String VIP_INFO = "/api/ScSocial/VIPCfgInfo";//获取VIP设置列表
    public static final String LIKE_LIST = "/api/ScMoment/MomentLikeList";//获取VIP设置列表


    public static final String GET_OTHER_PROFILE = "/api/ScUser/GetOtherProfile";//根据userid获取用户信息



    public static final String HAS_SET_PASSWORD = "/api/ScUser/HasSetPassword";



    public static final String GET_IM_TOKEN = "/api/ScIM/GetImUserToken";
    public static final String CREATE_GROUP = "/api/ScIM/GroupChatStart";//创建群组g
    public static final String GROUP_CHAT_INFO = "/api/ScIM/GroupChatInfo";//获取群信息 群名和用户信息
    public static final String GROUP_CHAT_INVITE = "/api/ScIM/GroupChatInvite";//群聊邀请
    public static final String GROUP_CHAT_KICK = "/api/ScIM/GroupChatKick";//提出群聊
    public static final String GROUP_CHAT_END = "/api/ScIM/GroupChatEnd";//解散群聊
    public static final String GROUP_CHAT_LEAVE = "/api/ScIM/GroupChatLeave";//离开群聊
    public static final String GROUP_CHAT_CONFIG = "/api/ScIM/GroupChatConfig";//设置群信息 标题和公告


    //单聊设置
    public static final String SET_FRIEND_ALIAS = "/api/ScUser/FriendAlias";
    public static final String BLOCKS_ADD = "/api/ScUser/BlocksAdd";
    public static final String BLOCKS_REMOVE = "/api/ScUser/BlocksRemove";
    public static final String BLOCKS_LIST = "/api/ScUser/BlocksList";

    //和IMSDK交互完了之前 需要请求后台
    // 聊天置顶
    public static final String TOP_A_FRIEND_YES = "/api/ScUser/TopAFriendYes";
    // 聊天取消置顶
    public static final String TOP_A_FRIEND_NO = "/api/ScUser/TopAFriendNo";


    //获取token相关
    public static final String TOKEN_BASE_URL = "https://ttid.alilusions.com/";
    public static final String CONNECT_TOKEN = "connect/token";
    public static final String CHANGE_PW_BY_CODE = "api/MemberApi/ChangePWbyCode";
    public static final String CHANGE_PW_BY_OLD_PW = "api/MemberApi/ChangePWbyOldPW";



    //上传图片
    public static final String UPLOAD_BASE_URL = "https://ttmedia.alilusions.com/";
    public static final String UPLOAD_AVATAR = "api/jjUpload/UUupload";


//// 接口
//// 获取IM_token
//    static NSString * const Get_IM_Token_Interface = @"/api/ScIM/GetImUserToken";
//// 获取用户信息
//    static NSString * const Get_IM_User_Interface = @"/api/ScUser/GetOtherProfile";
//// 用户加入黑名单
//    static NSString * const Set_IM_AddUserBlock_Interface = @"/api/ScUser/BlocksAdd";
//// 用户移除黑名单
//    static NSString * const Set_IM_RemoveUserBlock_Interface = @"/api/ScUser/BlocksRemove";
//// 获取黑名单信息
//    static NSString * const Get_IM_UserBlock_Interface = @"/api/ScUser/BlocksLists";
//// 设置好友名称
//    static NSString * const Set_IM_FriendsAlias_Interface = @"/api/ScUser/FriendAlias";
//// 获取全部好友
//    static NSString * const Get_IM_Friends_Interface = @"/api/ScUser/FriendsList";
//// 获取全部好友请求
//    static NSString * const Get_IM_Friend_Requests_Interface = @"/api/ScUser/FollowersListDetail";
//// 添加关注好友请求
//    static NSString * const IM_Add_Following_Requests_Interface = @"/api/ScUser/FollowingsAdd";
//// 取消关注好友
//    static NSString * const IM_Remove_Following_Requests_Interface = @"/api/ScUser/FollowingsRemove";
//// 获取评论数据
//    static NSString * const GET_IM_Moment_Center_Interface = @"/api/ScMoment/CommentsCenter";
//// 发出评论
//    static NSString * const IM_Add_Moment_Center_Interface = @"/api/ScMoment/CmtAdd";
//// 获取我的点赞
//    static NSString * const GET_IM_My_Like_Interface = @"/api/ScMoment/MomentLikeList";
//// 聊天置顶
//    static NSString * const GET_IM_ChatIsTop_Interface = @"/api/ScUser/TopAFriendYes";
//// 聊天取消置顶
//    static NSString * const GET_IM_ChatIsNotTop_Interface = @"/api/ScUser/TopAFriendNo";
//// 群聊创建
//    static NSString * const IM_GroupChatCreate_Interface = @"/api/ScIM/GroupChatStart";
//// 管理员解散群
//    static NSString * const IM_GroupChatDismiss_Interface = @"/api/ScIM/GroupChatEnd";
//// 拉人入群
//    static NSString * const IM_GroupChatAddMember_Interface = @"/api/ScIM/GroupChatInvite";
//// 踢人出群
//    static NSString * const IM_GroupChatRemoveMember_Interface = @"/api/ScIM/GroupChatKick";
//// 获取群信息
//    static NSString * const IM_GroupChatGetInfo_Interface = @"/api/ScIM/GroupChatInfo";
//// 设置群信息
//    static NSString * const IM_GroupChatSetInfo_Interface = @"/api/ScIM/GroupChatConfig";
//// 主动退出群
//    static NSString * const IM_GroupChatLeave_Interface = @"/api/ScIM/GroupChatLeave";
//
//
//
//
//
//// auth-获取Token
//    static NSString * const GetToken_Interface = @"/connect/token";
//// 验证码修改密码
//    static NSString * const ChangePWbyCode_Interface = @"/api/MemberApi/ChangePWbyCode";
//// 旧密码修改密码
//    static NSString * const ChangePWbyOldPW_Interface = @"/api/MemberApi/ChangePWbyOldPW";
//// 查询用户是否已经设置密码
//    static NSString * const HasSetPassword_Interface = @"/api/ScUser/HasSetPassword";
//// 登出
//    static NSString * const Logout_Interface = @"/api/ScUser/Logout";
//// media-上传
//    static NSString * const UploadMedia_Interface = @"/api/jjUpload/UUupload";
//// 获取登录短信
//    static NSString * const GetLoginSMS_Interface = @"/api/ScSMS/ping";
//// 验证登录短信
//    static NSString * const VerifyLoginSMS_Interface = @"/api/ScSMS/VerifyCode";
//// 获取用户信息
//    static NSString * const GetUserProfile_Interface = @"/api/ScUser/ProfileGet";
//// 更新用户信息
//    static NSString * const SetUserProfile_Interface = @"/api/ScUser/ProfileUpdate";
//// 获取用户VIP状态
//    static NSString * const GetUserVIPStatus_Interface = @"/api/ScUser/VIPCheck";
//// 获取用户VIP颜色列表
//    static NSString * const GetUserVIPColorList_Interface = @"/api/ScSocial/VIPCfgInfo";


}
