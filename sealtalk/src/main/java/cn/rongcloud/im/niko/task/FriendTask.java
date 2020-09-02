package cn.rongcloud.im.niko.task;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.common.ThreadManager;
import cn.rongcloud.im.niko.contact.PhoneContactManager;
import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.db.dao.FriendDao;
import cn.rongcloud.im.niko.db.dao.GroupMemberDao;
import cn.rongcloud.im.niko.db.dao.UserDao;
import cn.rongcloud.im.niko.db.model.FriendDescription;
import cn.rongcloud.im.niko.db.model.FriendDetailInfo;
import cn.rongcloud.im.niko.db.model.FriendInfo;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.db.model.FriendStatus;
import cn.rongcloud.im.niko.db.model.PhoneContactInfoEntity;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.file.FileManager;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.AddFriendResult;
import cn.rongcloud.im.niko.model.GetContactInfoResult;
import cn.rongcloud.im.niko.model.PhoneContactInfo;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.SearchFriendInfo;
import cn.rongcloud.im.niko.model.SimplePhoneContactInfo;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.model.niko.ProfileInfo;
import cn.rongcloud.im.niko.net.HttpClientManager;
import cn.rongcloud.im.niko.net.RetrofitUtil;
import cn.rongcloud.im.niko.net.service.FriendService;
import cn.rongcloud.im.niko.utils.BirthdayToAgeUtil;
import cn.rongcloud.im.niko.utils.CharacterParser;
import cn.rongcloud.im.niko.utils.NetworkBoundResource;
import cn.rongcloud.im.niko.utils.NetworkOnlyResource;
import cn.rongcloud.im.niko.utils.RongGenerate;
import cn.rongcloud.im.niko.utils.SearchUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import cn.rongcloud.im.niko.utils.log.SLog;
import io.rong.imlib.model.Conversation;
import okhttp3.RequestBody;

public class FriendTask {
    private static final String TAG = "FriendTask";
    private Context context;
    private FriendService friendService;
    private DbManager dbManager;
    private FileManager fileManager;

    public FriendTask(Context context) {
        this.context = context.getApplicationContext();
        friendService = HttpClientManager.getInstance(this.context).getClient().createService(FriendService.class);
        dbManager = DbManager.getInstance(this.context);
        fileManager = new FileManager(context);
    }

    /**
     * 获取所有好友信息
     *
     * @return
     */
    public LiveData<Resource<List<FriendShipInfo>>> getAllFriends() {
        SLog.i(TAG, "getAllFriends()");
        return new NetworkBoundResource<List<FriendShipInfo>, Result<List<FriendBean>>>() {
            @Override
            protected void saveCallResult(@NonNull Result<List<FriendBean>> item) {
                List<FriendBean> rsData = item.getRsData();
                List<FriendShipInfo> list = new ArrayList<>();
                if(rsData==null||rsData.size()==0){return;}
                for(FriendBean friendBean : rsData){
                    FriendShipInfo friendShipInfo = new FriendShipInfo();
                    friendShipInfo.setDisplayName(friendBean.getAlias());
                    friendShipInfo.setStatus(friendBean.isIsFriend()?FriendStatus.IS_FRIEND.getStatusCode():FriendStatus.NONE.getStatusCode());
                    FriendDetailInfo detailInfo = new FriendDetailInfo();
                    detailInfo.setId(String.valueOf(friendBean.getUID()));
                    detailInfo.setNickname(friendBean.getName());
                    detailInfo.setPortraitUri(GlideImageLoaderUtil.getScString(friendBean.getUserIcon()));
                    friendShipInfo.setUser(detailInfo);
                    friendShipInfo.setNameColor(friendBean.getNameColor());
                    list.add(friendShipInfo);
                }
                //将freindbean转成
                SLog.i(TAG, "saveCallResult() list.size() :" + list.size());
                UserInfo userInfo = null;
                FriendInfo friendInfo = null;
                List<UserInfo> userInfoList = new ArrayList<>();
                List<FriendInfo> friendInfoList = new ArrayList<>();
                for (FriendShipInfo friendShipInfo : list) {
                    userInfo = new UserInfo();
                    friendInfo = new FriendInfo();
                    userInfo.setId(friendShipInfo.getUser().getId());
                    userInfo.setName(friendShipInfo.getUser().getNickname());

                    String portraitUri = friendShipInfo.getUser().getPortraitUri();
                    // 若头像为空则生成默认头像
                    if (TextUtils.isEmpty(portraitUri)) {
                        portraitUri = RongGenerate.generateDefaultAvatar(context, friendShipInfo.getUser().getId(), friendShipInfo.getUser().getNickname());
                    }
                    userInfo.setPortraitUri(portraitUri);
                    userInfo.setAlias(friendShipInfo.getDisplayName());
                    userInfo.setFriendStatus(friendShipInfo.getStatus());
                    userInfo.setPhoneNumber(friendShipInfo.getUser().getPhone());
                    userInfo.setRegion(friendShipInfo.getUser().getRegion());
                    userInfo.setAliasSpelling(SearchUtils.fullSearchableString(friendShipInfo.getDisplayName()));
                    userInfo.setAliasSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getDisplayName()));

                    userInfo.setNameSpelling(SearchUtils.fullSearchableString(friendShipInfo.getUser().getNickname()));
                    userInfo.setNameSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getUser().getNickname()));

                    if (!TextUtils.isEmpty(friendShipInfo.getDisplayName())) {
                        userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getDisplayName()));
                    } else {
                        userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getUser().getNickname()));
                    }

                    friendInfo.setId(friendShipInfo.getUser().getId());
                    friendInfo.setMessage(friendShipInfo.getMessage());
                    friendInfo.setUpdatedAt(friendShipInfo.getUpdatedAt());
                    userInfoList.add(userInfo);
                    friendInfoList.add(friendInfo);

                    // 更新 IMKit 显示缓存
                    String name = userInfo.getAlias();
                    if (TextUtils.isEmpty(name)) {
                        name = userInfo.getName();
                    }
                    IMManager.getInstance().updateUserInfoCache(userInfo.getId(), name, Uri.parse(userInfo.getPortraitUri()));
                }

                UserDao userDao = dbManager.getUserDao();
                if (userDao != null) {
                    userDao.insertUserList(userInfoList);
                }

                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    friendDao.insertFriendShipList(friendInfoList);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<FriendShipInfo>> loadFromDb() {
                SLog.i(TAG, "getAllFriends() loadFromDb()");
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    return friendDao.getAllFriendListDB();
                } else {
                    return new MutableLiveData<>(null);
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<List<FriendBean>>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("Skip", NetConstant.SKIP);
                paramsMap.put("Take", NetConstant.TAKE);
                paramsMap.put("Data", 0);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return friendService.getAllFriendList(requestBody);
            }

        }.asLiveData();

    }

    public LiveData<List<FriendShipInfo>> searchFriendsFromDB(String match) {
        return dbManager.getFriendDao().searchFriendShip(match);
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public LiveData<Resource<UserInfo>> getUserInfo(final String userId) {
        return new NetworkBoundResource<UserInfo, Result<ProfileInfo>>() {
            @Override
            protected void saveCallResult(@NonNull Result<ProfileInfo> item) {
                ProfileInfo rsData = item.getRsData();
                UserInfo userInfo = new UserInfo();

                if (rsData == null) {
                    return;
                }

                userInfo.setId(String.valueOf(rsData.getHead().getUID()));
                userInfo.setAlias(rsData.getHead().getAlias());
                userInfo.setAliasSpelling(SearchUtils.fullSearchableString(rsData.getHead().getAlias()));
                userInfo.setName(rsData.getHead().getName());
                userInfo.setPortraitUri(GlideImageLoaderUtil.getScString(rsData.getHead().getUserIcon()));
                userInfo.setNameColor(rsData.getHead().getNameColor());
                userInfo.setDob(BirthdayToAgeUtil.longToString(rsData.getDOB()));
                userInfo.setBio(rsData.getBio());
                userInfo.setLocation(rsData.getLocation());
                userInfo.setSchool(rsData.getSchool());
                userInfo.setMan(rsData.getHead().isGender());
                SLog.e(LogTag.DB, "NetworkBoundResource saveCallResult Impl:" + JSON.toJSONString(userInfo));

                UserDao userDao = dbManager.getUserDao();
                if (userDao != null) {
                    String nameSpelling = SearchUtils.fullSearchableString(userInfo.getName());

                    userInfo.setNameSpelling(nameSpelling);
                    String portraitUri = userInfo.getPortraitUri();

                    // 当没有头像时生成默认头像
                    if (TextUtils.isEmpty(portraitUri)) {
                        portraitUri = RongGenerate.generateDefaultAvatar(context, userInfo.getId(), userInfo.getName());
                        userInfo.setPortraitUri(portraitUri);
                    }

                    String stAccount = userInfo.getStAccount();
                    if (!TextUtils.isEmpty(stAccount)) {
                        userDao.updateSAccount(userInfo.getId(), stAccount);
                    }
                    String gender = userInfo.getGender();
                    if (!TextUtils.isEmpty(gender)) {
                        userDao.updateGender(userInfo.getId(), gender);
                    }
                }

                // 更新 IMKit 显示缓存
                String alias = "";
                if (userDao != null) {
                    alias = userDao.getUserByIdSync(userInfo.getId()).getAlias();
                }
                //有备注名的时，使用备注名
                String name = TextUtils.isEmpty(alias) ? userInfo.getName() : alias;
                IMManager.getInstance().updateUserInfoCache(userInfo.getId(), name, Uri.parse(userInfo.getPortraitUri()));
            }

            @NonNull
            @Override
            protected LiveData<UserInfo> loadFromDb() {
                UserDao userDao = dbManager.getUserDao();
                if (userDao != null) {
                    return userDao.getUserById(userId);
                } else {
                    return new MediatorLiveData<>();
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<ProfileInfo>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("Data", userId);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return friendService.getFriendInfo(requestBody);
            }

        }.asLiveData();
    }

    /**
     * 获取好友信息
     *
     * @param userId
     * @return
     */
    public LiveData<Resource<FriendShipInfo>> getFriendInfo(String userId) {
        return new NetworkBoundResource<FriendShipInfo, Result<FriendShipInfo>>() {
            @Override
            protected void saveCallResult(@NonNull Result<FriendShipInfo> item) {
                UserDao userDao = dbManager.getUserDao();
                FriendDao friendDao = dbManager.getFriendDao();
                if (userDao == null || friendDao == null) return;

                FriendShipInfo friendShipInfo = item.getRsData();
                if (friendShipInfo == null) return;

                UserInfo userInfo = new UserInfo();
                FriendInfo friendInfo = new FriendInfo();
                userInfo.setId(friendShipInfo.getUser().getId());
                userInfo.setName(friendShipInfo.getUser().getNickname());
                String portraitUri = friendShipInfo.getUser().getPortraitUri();
                // 若头像为空则生成默认头像
                if (TextUtils.isEmpty(portraitUri)) {
                    portraitUri = RongGenerate.generateDefaultAvatar(context, friendShipInfo.getUser().getId(), friendShipInfo.getUser().getNickname());
                }
                userInfo.setPortraitUri(portraitUri);
                userInfo.setAlias(friendShipInfo.getDisplayName());
                userInfo.setFriendStatus(FriendStatus.IS_FRIEND.getStatusCode());
                userInfo.setPhoneNumber(friendShipInfo.getUser().getPhone());
                userInfo.setRegion(friendShipInfo.getUser().getRegion());
                userInfo.setAliasSpelling(SearchUtils.fullSearchableString(friendShipInfo.getDisplayName()));
                userInfo.setAliasSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getDisplayName()));
                userInfo.setNameSpelling(SearchUtils.fullSearchableString(friendShipInfo.getUser().getNickname()));
                userInfo.setNameSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getUser().getNickname()));
                if (!TextUtils.isEmpty(friendShipInfo.getDisplayName())) {
                    userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getDisplayName()));
                } else {
                    userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getUser().getNickname()));
                }

                friendInfo.setId(friendShipInfo.getUser().getId());
                friendInfo.setMessage(friendShipInfo.getMessage());
                friendInfo.setUpdatedAt(friendShipInfo.getUpdatedAt() == null ? friendShipInfo.getUser().getUpdatedAt() : friendShipInfo.getUpdatedAt());

                userDao.insertUser(userInfo);
                friendDao.insertFriendShip(friendInfo);

                // 更新 IMKit 显示缓存
                String name = userInfo.getAlias();
                if (TextUtils.isEmpty(name)) {
                    name = userInfo.getName();
                }
                IMManager.getInstance().updateUserInfoCache(userInfo.getId(), name, Uri.parse(userInfo.getPortraitUri()));
            }

            @NonNull
            @Override
            protected LiveData<FriendShipInfo> loadFromDb() {
                FriendDao friendDao = dbManager.getFriendDao();
                LiveData<FriendShipInfo> friendInfo;
                if (friendDao == null) {
                    friendInfo = new MutableLiveData<>(null);
                } else {
                    friendInfo = friendDao.getFriendInfo(userId);
                }
                return friendInfo;
            }

            @NonNull
            @Override
            protected LiveData<Result<FriendShipInfo>> createCall() {
                return friendService.getFriendInfo(userId);
            }
        }.asLiveData();
    }


    public FriendShipInfo getFriendShipInfoFromDBSync(String userId) {
        return dbManager.getFriendDao().getFriendInfoSync(userId);
    }




    public LiveData<List<FriendShipInfo>> getAllFriendsExcludeGroup(String excludeGroupId) {
        return dbManager.getFriendDao().getAllFriendsExcludeGroup(excludeGroupId);
    }

    public LiveData<List<FriendShipInfo>> searchFriendsExcludeGroup(String excludeGroupId, String matchSearch) {
        return dbManager.getFriendDao().searchFriendsExcludeGroup(excludeGroupId, matchSearch);
    }



    public LiveData<FriendShipInfo> getFriendShipInfoFromDB(String userId) {
        return dbManager.getFriendDao().getFriendInfo(userId);
    }



    /**
     * 设置朋友描述信息
     *
     * @param friendId
     * @param displayName
     * @return
     */
    public LiveData<Resource<Boolean>> setFriendDescription(String friendId, String displayName) {

        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                HashMap<String, Object> dataMap = new HashMap<>();
                dataMap.put("UID", Integer.parseInt(friendId));
                dataMap.put("Alias", displayName);
                bodyMap.put("Data",dataMap);
                return friendService.setAlias(RetrofitUtil.createJsonRequest(bodyMap));
            }

            @Override
            protected void saveCallResult(@NonNull Boolean item) {
//                super.saveCallResult(item);
                FriendDescription friendDescription = new FriendDescription();
                friendDescription.setId(friendId);
                if (displayName != null) {
                    friendDescription.setDisplayName(displayName);
                    //更新用户别名 以及缓存信息
                    updateAlias(friendId, displayName);
                }
                FriendDao friendDao = dbManager.getFriendDao();
                friendDao.insertFriendDescription(friendDescription);
            }
        }.asLiveData();
    }

    /**
     * 更新 备注名
     *
     * @param friendId
     * @param displayName
     */
    private void updateAlias(String friendId, String displayName) {
        UserDao userDao = dbManager.getUserDao();
        if (userDao != null) {
            String aliasSpelling = CharacterParser.getInstance().getSpelling(displayName);
            userDao.updateAlias(friendId, displayName, aliasSpelling);

            UserInfo userInfo = userDao.getUserByIdSync(friendId);
            // 更新 IMKit 显示缓存
            String name = userInfo.getAlias();
            if (TextUtils.isEmpty(name)) {
                name = userInfo.getName();
            }
            IMManager.getInstance().updateUserInfoCache(userInfo.getId(), name, Uri.parse(userInfo.getPortraitUri()));
            // 需要获取此用户所在自己的哪些群组， 然后遍历修改其群组的个人信息。
            // 用于当有备注的好友在群组时， 显示备注名称
            GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
            List<String> groupIds = groupMemberDao.getGroupIdListByUserId(friendId);
            if (groupIds != null && groupIds.size() > 0) {
                for (String groupId : groupIds) {
                    //如果有设置群昵称，则不设置好友别名
                    if (TextUtils.isEmpty(groupMemberDao.getGroupMemberInfoDes(groupId, friendId).getGroupNickname())) {
                        IMManager.getInstance().updateGroupMemberInfoCache(groupId, friendId, name);
                    }
                }
            }
        }
    }


    public LiveData<Resource<Boolean>> topChatYes(String id) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Data",Integer.parseInt(id));
                return friendService.topYes(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> topChatNo(String id) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Data",Integer.parseInt(id));
                return friendService.topNo(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }






    public LiveData<Result<List<FriendBean>>> getFriendList(int skip, int take){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Skip", skip);
        paramsMap.put("Take", take);
        paramsMap.put("Data", 0);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return friendService.getAllFriendList(requestBody);
    }
}
