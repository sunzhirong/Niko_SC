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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.im.niko.SealApp;
import cn.rongcloud.im.niko.common.ErrorCode;
import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.common.ResultCallback;
import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.db.dao.FriendDao;
import cn.rongcloud.im.niko.db.dao.GroupDao;
import cn.rongcloud.im.niko.db.dao.UserDao;
import cn.rongcloud.im.niko.db.model.BlackListEntity;
import cn.rongcloud.im.niko.db.model.GroupEntity;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.file.FileManager;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.BlackListUser;
import cn.rongcloud.im.niko.model.ContactGroupResult;
import cn.rongcloud.im.niko.model.RegionResult;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.model.UserCacheInfo;
import cn.rongcloud.im.niko.model.UserSimpleInfo;
import cn.rongcloud.im.niko.model.niko.CommentBean;
import cn.rongcloud.im.niko.model.niko.FollowBean;
import cn.rongcloud.im.niko.model.niko.FollowRequestInfo;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.model.niko.MyLikeBean;
import cn.rongcloud.im.niko.model.niko.ProfileHeadInfo;
import cn.rongcloud.im.niko.model.niko.ProfileInfo;
import cn.rongcloud.im.niko.model.niko.TokenBean;
import cn.rongcloud.im.niko.net.HttpClientManager;
import cn.rongcloud.im.niko.net.RetrofitUtil;
import cn.rongcloud.im.niko.net.ScInterceptor;
import cn.rongcloud.im.niko.net.request.CommentAtReq;
import cn.rongcloud.im.niko.net.service.TokenService;
import cn.rongcloud.im.niko.net.service.UploadService;
import cn.rongcloud.im.niko.net.service.UserService;
import cn.rongcloud.im.niko.net.token.TokenHttpClientManager;
import cn.rongcloud.im.niko.net.upload.UploadHttpClientManager;
import cn.rongcloud.im.niko.sp.CountryCache;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.sp.UserCache;
import cn.rongcloud.im.niko.ui.adapter.models.VIPCheckBean;
import cn.rongcloud.im.niko.ui.adapter.models.VIPConfigBean;
import cn.rongcloud.im.niko.utils.CharacterParser;
import cn.rongcloud.im.niko.utils.FileUtils;
import cn.rongcloud.im.niko.utils.NetworkBoundResource;
import cn.rongcloud.im.niko.utils.NetworkOnlyResource;
import cn.rongcloud.im.niko.utils.RongGenerate;
import cn.rongcloud.im.niko.utils.SearchUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import cn.rongcloud.im.niko.utils.log.SLog;
import io.rong.imlib.model.Conversation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 用户相关业务处理
 */
public class UserTask {
    private FileManager fileManager;
    private UserService userService;
    private UploadService uploadService;
    private TokenService tokenService;
    private Context context;
    private DbManager dbManager;
    private IMManager imManager;
    //存储当前最新一次登录的用户信息
    private UserCache userCache;
    private CountryCache countryCache;


    public UserTask(Context context) {
        this.context = context.getApplicationContext();
        userService = HttpClientManager.getInstance(context).getClient().createService(UserService.class);
        tokenService = TokenHttpClientManager.getInstance(context).getClient().createService(TokenService.class);
        uploadService = UploadHttpClientManager.getInstance(context).getClient().createService(UploadService.class);


        dbManager = DbManager.getInstance(context.getApplicationContext());
        fileManager = new FileManager(context.getApplicationContext());
        userCache = new UserCache(context.getApplicationContext());
        countryCache = new CountryCache(context.getApplicationContext());
        imManager = IMManager.getInstance();
    }

    /**
     * 用户登录
     *
     * @param region   国家区号
     * @param phone    手机号码
     * @param password 密码
     */
    public LiveData<Resource<String>> login(String region, String phone, String password) {


        MediatorLiveData<Resource<String>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Resource<String>> resourceLiveData = new NetworkOnlyResource<String, Result<String>>() {

            @NonNull
            @Override
            protected LiveData<Result<String>> createCall() {
                return userService.getIMToken();
            }
        }.asLiveData();
        result.addSource(resourceLiveData, loginResultResource -> {
            if (loginResultResource.status == Status.SUCCESS) {
                result.removeSource(resourceLiveData);
                String data = loginResultResource.data;
                if (!TextUtils.isEmpty(data)) {
                    imManager.connectIM(data, true, new ResultCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            result.postValue(Resource.success(s));
                            // 存储当前登录成功的用户信息
                            UserCacheInfo info = new UserCacheInfo(s, data, phone, password, region, countryCache.getCountryInfoByRegion(region));
                            userCache.saveUserCache(info);
                        }

                        @Override
                        public void onFail(int errorCode) {
                            result.postValue(Resource.error(errorCode, null));
                        }
                    });
                } else {
                    result.setValue(Resource.error(ErrorCode.API_ERR_OTHER.getCode(), null));
                }
            } else if (loginResultResource.status == Status.ERROR) {
                result.setValue(Resource.error(loginResultResource.code, null));
            } else {
                // do nothing
            }
        });
        return result;
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

                if(userId.equals(imManager.getCurrentId())){
                    ProfileUtils.sProfileInfo = rsData;
                }

                userInfo.setId(String.valueOf(rsData.getHead().getUID()));
                userInfo.setAlias(rsData.getHead().getAlias());
                userInfo.setAliasSpelling(SearchUtils.fullSearchableString(rsData.getHead().getAlias()));
                userInfo.setName(rsData.getHead().getName());
                userInfo.setPortraitUri(GlideImageLoaderUtil.getScString(rsData.getHead().getUserIcon()));
                userInfo.setNameColor(rsData.getHead().getNameColor());
                userInfo.setDob(rsData.getDOB());
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
                    // 更新现有用户信息若没有则创建新的用户信息，防止覆盖其他已有字段
                    int resultCount = userDao.updateNameAndPortrait(userInfo.getId(), userInfo.getName(), nameSpelling, portraitUri);
                    if (resultCount == 0) {
                        // 当前用户的话， 判断是否有电话号码， 没有则从缓存中取出
                        if (userInfo.getId().equals(imManager.getCurrentId())) {
                            UserCacheInfo cacheInfo = userCache.getUserCache();
                            if (cacheInfo != null && cacheInfo.getId().equals(userInfo.getId())) {
                                userInfo.setPhoneNumber(cacheInfo.getPhoneNumber());
                            }
                        }

                        userDao.insertUser(userInfo);
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
                return userService.getUserInfo(requestBody);
            }

        }.asLiveData();
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public UserInfo getUserInfoSync(final String userId) {
        return dbManager.getUserDao().getUserByIdSync(userId);
    }

    /**
     * 获取地区的消息列表
     *
     * @return
     */
    public LiveData<Resource<List<RegionResult>>> getRegionList() {
        return new NetworkOnlyResource<List<RegionResult>, Result<List<RegionResult>>>() {

            @NonNull
            @Override
            protected LiveData<Result<List<RegionResult>>> createCall() {
                return userService.getRegionList();
            }
        }.asLiveData();
    }


    /**
     * 设置自己的昵称
     *
     * @param nickName
     * @return
     */
    public LiveData<Resource<Result>> setMyNickName(String nickName) {
        return new NetworkOnlyResource<Result, Result>() {
            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("nickname", nickName);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return userService.setMyNickName(body);
            }

            @Override
            protected Result transformRequestType(Result response) {
                return response;
            }

            @Override
            protected void saveCallResult(@NonNull Result item) {
                //更新 nickName
                saveAndSyncNickname(nickName);
            }
        }.asLiveData();
    }

    /**
     * 设置 SealTalk 账号
     *
     * @param stAccount
     * @return
     */
    public LiveData<Resource<Result>> setStAccount(String stAccount) {
        return new NetworkOnlyResource<Result, Result>() {
            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> paramMap = new HashMap<>();
                paramMap.put("stAccount", stAccount);
                RequestBody body = RetrofitUtil.createJsonRequest(paramMap);
                return userService.setStAccount(body);
            }

            @Override
            protected Result transformRequestType(Result response) {
                return response;
            }

            @Override
            protected void saveCallResult(@NonNull Result item) {
                updateStAccount(IMManager.getInstance().getCurrentId(), stAccount);
            }
        }.asLiveData();

    }

    public LiveData<Resource<Result>> setGender(String gender) {
        return new NetworkOnlyResource<Result, Result>() {
            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> paramMap = new HashMap<>();
                paramMap.put("gender", gender);
                RequestBody body = RetrofitUtil.createJsonRequest(paramMap);
                return userService.setGender(body);
            }

            @Override
            protected Result transformRequestType(Result response) {
                return response;
            }

            @Override
            protected void saveCallResult(@NonNull Result item) {
                updateGender(IMManager.getInstance().getCurrentId(), gender);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> setPortrait(Uri imageUri) {
        MediatorLiveData<Resource<Result>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Resource<String>> uploadResource = fileManager.uploadImage(imageUri);
        result.addSource(uploadResource, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> resource) {
                if (resource.status != Status.LOADING) {
                    result.removeSource(uploadResource);
                }

                if (resource.status == Status.ERROR) {
                    result.setValue(Resource.error(resource.code, null));
                    return;
                }

                if (resource.status == Status.SUCCESS) {
                    LiveData<Resource<Result>> setPortrait = setPortrait(resource.data);
                    result.addSource(setPortrait, new Observer<Resource<Result>>() {
                        @Override
                        public void onChanged(Resource<Result> resultResource) {

                            if (resultResource.status != Status.LOADING) {
                                result.removeSource(setPortrait);
                            }

                            if (resultResource.status == Status.ERROR) {
                                result.setValue(Resource.error(resultResource.code, null));
                                return;
                            }
                            if (resultResource.status == Status.SUCCESS) {
                                result.setValue(resultResource);
                            }
                        }
                    });
                }
            }
        });

        return result;
    }

    /**
     * 设置头像信息
     *
     * @param portraitUrl
     * @return
     */
    private LiveData<Resource<Result>> setPortrait(String portraitUrl) {
        return new NetworkOnlyResource<Result, Result>() {

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("portraitUri", portraitUrl);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return userService.setPortrait(body);
            }

            @Override
            protected Result transformRequestType(Result response) {
                return response;
            }

            @Override
            protected void saveCallResult(@NonNull Result item) {
                //更新 头像
                saveAndSyncPortrait(IMManager.getInstance().getCurrentId(), portraitUrl);
            }

        }.asLiveData();
    }


    /**
     * 保存并同步用户的头像
     *
     * @param userId
     * @param portraitUrl
     */
    public void saveAndSyncPortrait(String userId, String portraitUrl) {
        saveAndSyncUserInfo(userId, null, portraitUrl);
//
//        UserDao userDao = dbManager.getUserDao();
//        if (userDao != null) {
//            int i = userDao.updatePortrait(userId, portraitUrl);
//            SLog.d("ss_update", "i=" + i);
//        }
    }

    /**
     * 保存并同步用户的昵称
     *
     * @param nickName
     */
    public void saveAndSyncNickname(String nickName) {
        saveAndSyncUserInfo(IMManager.getInstance().getCurrentId(), nickName, null);
    }

    /**
     * 保存并同步用户的昵称和头像
     *
     * @param userId
     * @param nickName
     * @param portraitUrl
     */
    public void saveAndSyncUserInfo(String userId, String nickName, String portraitUrl) {
        UserDao userDao = dbManager.getUserDao();
        if (userDao != null) {
            UserInfo userInfo = userDao.getUserByIdSync(userId);
            if (nickName == null) {
                nickName = userInfo == null ? "" : userInfo.getName();
            }

            if (portraitUrl == null) {
                portraitUrl = userInfo == null ? "" : userInfo.getPortraitUri();
            }
            int i = userDao.updateNameAndPortrait(userId, nickName, CharacterParser.getInstance().getSpelling(nickName), portraitUrl);
            SLog.d("ss_update", "i=" + i);

            IMManager.getInstance().updateUserInfoCache(userId, nickName, Uri.parse(portraitUrl));
        }
    }

    /**
     * 更新数据库 SealTalk 号信息
     *
     * @param userId
     * @param stAccount
     */
    public void updateStAccount(String userId, String stAccount) {
        UserDao userDao = dbManager.getUserDao();
        if (userDao != null) {
            int i = userDao.updateSAccount(userId, stAccount);
            SLog.d("st_update", "i=" + i);
        }
    }

    public void updateGender(String userId, String gender) {
        UserDao userDao = dbManager.getUserDao();
        if (userDao != null) {
            int i = userDao.updateGender(userId, gender);
            SLog.d("gender_update", "i=" + i);
        }
    }


    /**
     * 修改用户密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public LiveData<Resource<Result>> changePassword(String oldPassword, String newPassword) {
        return new NetworkOnlyResource<Result, Result>() {

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("oldPassword", oldPassword);
                paramsMap.put("newPassword", newPassword);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return userService.changePassword(body);
            }

            @Override
            protected Result transformRequestType(Result response) {
                return response;
            }
        }.asLiveData();
    }

    /**
     * 获取最新一次登录用户的缓存信息
     *
     * @return
     */
    public UserCacheInfo getUserCache() {
        return userCache.getUserCache();
    }


    /**
     * 获取黑名单用户
     *
     * @return
     */
    public LiveData<Resource<List<UserSimpleInfo>>> getBlackList() {
        return new NetworkBoundResource<List<UserSimpleInfo>, Result<List<ProfileHeadInfo>>>() {
            @Override
            protected void saveCallResult(@NonNull Result<List<ProfileHeadInfo>> item) {
                List<ProfileHeadInfo> rsData = item.getRsData();
                if (rsData == null||rsData.size()==0) return;
                List<BlackListUser> result = new ArrayList<>();

                //封装到BlackListUser中

                for(ProfileHeadInfo info : rsData){
                    BlackListUser blackListUser = new BlackListUser();
                    blackListUser.setId(String.valueOf(info.getUID()));
                    blackListUser.setNickname(info.getName());
                    blackListUser.setPortraitUri(GlideImageLoaderUtil.getScString(info.getUserIcon()));
                    result.add(blackListUser);
                }


                List<BlackListEntity> blackList = new ArrayList<>();
                BlackListEntity addBlack;
                UserInfo user;

                UserDao userDao = dbManager.getUserDao();

                for (BlackListUser blackUser : result) {
//                    BlackListUser blackUser = blackInfo.getUser();
//                    if (blackUser == null) continue;

                    // 将黑名单中的用户信息更新用户表
                    user = new UserInfo();
                    user.setId(blackUser.getId());
                    String nickname = blackUser.getNickname();
                    String nameSpelling = SearchUtils.fullSearchableString(nickname);
                    user.setNameSpelling(nameSpelling);
                    user.setName(nickname);
                    String portraitUri = blackUser.getPortraitUri();

                    // 当没有头像时生成默认头像
                    if (TextUtils.isEmpty(portraitUri)) {
                        portraitUri = RongGenerate.generateDefaultAvatar(context, blackUser.getId(), nickname);
                        user.setPortraitUri(portraitUri);
                    } else {
                        user.setPortraitUri(portraitUri);
                    }

                    // 更新现有用户信息若没有则创建新的用户信息，防止覆盖其他已有字段
                    int resultCount = userDao.updateNameAndPortrait(user.getId(), user.getName(), nameSpelling, portraitUri);
                    if (resultCount == 0) {
                        userDao.insertUser(user);
                    }

                    // 添加到黑名单
                    addBlack = new BlackListEntity();
                    addBlack.setId(blackUser.getId());
                    blackList.add(addBlack);
                }

                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    // 每次清除之前有的黑名单
                    friendDao.deleteAllBlackList();
                    friendDao.updateBlackList(blackList);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<UserSimpleInfo>> loadFromDb() {
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    return friendDao.getBlackListUser();
                } else {
                    return new MutableLiveData<>(null);
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<List<ProfileHeadInfo>>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Skip", NetConstant.SKIP);
                bodyMap.put("Take", NetConstant.TAKE);
                return userService.getFriendBlackList(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }


    /**
     * 添加到黑名单
     *
     * @return
     */
    public LiveData<Resource<Boolean>> addToBlackList(String userId) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    BlackListEntity blackListEntity = new BlackListEntity();
                    blackListEntity.setId(userId);
                    friendDao.addToBlackList(blackListEntity);
                }

                IMManager.getInstance().clearConversationAndMessage(userId, Conversation.ConversationType.PRIVATE);
            }

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Data", Integer.parseInt(userId));
                return userService.addToBlackList(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    /**
     * 移除黑名单
     *
     * @return
     */
    public LiveData<Resource<Boolean>> removeFromBlackList(String userId) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    friendDao.removeFromBlackList(userId);
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Data", Integer.parseInt(userId));
                return userService.removeFromBlackList(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    /**
     * 获取通讯录群组列表
     *
     * @return
     */
    public LiveData<Resource<List<GroupEntity>>> getContactGroupList() {
        return new NetworkBoundResource<List<GroupEntity>, Result<ContactGroupResult>>() {
            @Override
            protected void saveCallResult(@NonNull Result<ContactGroupResult> item) {
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao == null) return;
                // 先清除所有群组在通讯录状态
                groupDao.clearAllGroupContact();

                ContactGroupResult result = item.getRsData();
                if (result == null) return;
                List<GroupEntity> list = result.getList();
                if (list != null && list.size() > 0) {
                    // 设置默认头像和名称拼音
                    for (GroupEntity groupEntity : list) {
                        String portraitUri = groupEntity.getPortraitUri();
                        if (TextUtils.isEmpty(portraitUri)) {
                            portraitUri = RongGenerate.generateDefaultAvatar(context, groupEntity.getId(), groupEntity.getName());
                            groupEntity.setPortraitUri(portraitUri);
                        }
                        groupEntity.setNameSpelling(SearchUtils.fullSearchableString(groupEntity.getName()));
                        groupEntity.setNameSpellingInitial(SearchUtils.initialSearchableString(groupEntity.getName()));
                        groupEntity.setOrderSpelling(CharacterParser.getInstance().getSpelling(groupEntity.getName()));
                        // 设置在该群是在通讯录中
                        groupEntity.setIsInContact(1);
                    }

                    groupDao.insertGroup(list);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<GroupEntity>> loadFromDb() {
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    return groupDao.getContactGroupInfoList();
                }
                return new MutableLiveData<>(null);
            }

            @NonNull
            @Override
            protected LiveData<Result<ContactGroupResult>> createCall() {
                return userService.getGroupListInContact();
            }
        }.asLiveData();
    }


    /**
     * 从数据中获取用户是否在黑名单
     *
     * @param friendId
     * @return
     */
    public LiveData<Resource<UserSimpleInfo>> getInBlackListUser(String friendId) {
        /*
         * 由于目前没有查询单一用户是否在黑名单的 API ，所以先从获取所有的黑名单列表
         * 然后再从数据库中返回单一黑名单用户的信息
         */
        MediatorLiveData<Resource<UserSimpleInfo>> result = new MediatorLiveData<>();

        // API 请求后的数据库数据源
        LiveData<Resource<List<UserSimpleInfo>>> blackListResource = getBlackList();

        // 单一黑名单用户的数据源
        LiveData<UserSimpleInfo> dbSource;
        FriendDao friendDao = dbManager.getFriendDao();
        if (friendDao != null) {
            dbSource = friendDao.getUserInBlackList(friendId);
        } else {
            dbSource = new MutableLiveData<>(null);
        }

        result.addSource(blackListResource, resource -> {
            if (resource.status != Status.LOADING) {
                result.removeSource(blackListResource);
            }

            if (resource.status == Status.SUCCESS) {
                result.addSource(dbSource, newData -> {
                    result.setValue(Resource.success(newData));
                });
            } else if (resource.status == Status.ERROR) {
                result.addSource(dbSource, newData -> {
                    result.setValue(Resource.error(resource.code, newData));
                });
            }
        });

        return result;
    }

    /**
     * 退出登录
     */
    public void logout() {
        userCache.logoutClear();
        dbManager.closeDb();
    }

    /**
     * 设置是否接收戳一下消息
     *
     * @param isReceive
     * @return
     */
    public LiveData<Resource<Void>> setReceivePokeMessageState(boolean isReceive) {
        return new NetworkOnlyResource<Void, Result>() {
            @Override
            protected void saveCallResult(@NonNull Void item) {
                IMManager.getInstance().updateReceivePokeMessageStatus(isReceive);
            }

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("pokeStatus", isReceive ? 1 : 0); // 0 不允许; 1 允许
                return userService.setReceivePokeMessageStatus(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }


    public LiveData<TokenBean> getAccessToken() {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("grant_type", "client_credentials");
        paramsMap.put("scope", "jjApiScope");
        NetConstant.Authorization = "Basic ampBcHBBcGlDbGllbnQ6Q2lyY2xlMjAyMEBXb3JsZA==";

        Map<String, RequestBody> stringRequestBodyMap = RetrofitUtil.generateRequestBody(paramsMap);
        return tokenService.connectToken(stringRequestBodyMap);
    }


    public LiveData<Result> getSms(String phone) {
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("PhoneNumber", phone);
        paramsMap.put("PhoneCountry", "86");
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.getSms(body);
    }

    public LiveData<Result> smsVerify(String phone) {
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("PhoneNumber", phone);
        paramsMap.put("PhoneCountry", "86");
        paramsMap.put("VCode", "9999");
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.verifyCodeNiko(body);
    }

    public LiveData<TokenBean> getUserToken(String phone) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("grant_type", "password");
        paramsMap.put("scope", "jjApiScope");
        paramsMap.put("UserName", phone);
        paramsMap.put("Password", ScInterceptor.getDV() + "9999");
//        paramsMap.put("Password","20200210" + "9999");
        paramsMap.put("VCode", "9999");
        NetConstant.Authorization = "Basic ampBcHBBcGlDbGllbnQ6Q2lyY2xlMjAyMEBXb3JsZA==";
        Map<String, RequestBody> stringRequestBodyMap = RetrofitUtil.generateRequestBody(paramsMap);
        return tokenService.connectToken(stringRequestBodyMap);
    }

    public LiveData<Resource<String>> getImToken() {
        return new NetworkOnlyResource<String, Result<String>>() {

            @NonNull
            @Override
            protected LiveData<Result<String>> createCall() {
                return userService.getIMToken();
            }
        }.asLiveData();
    }


    public LiveData<Result<List<MyLikeBean>>> myLiekList(int skip, int take){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Skip", skip);
        paramsMap.put("Take", take);
        paramsMap.put("Data", 0);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.getMyLiekList(requestBody);
    }

    public LiveData<Result<List<CommentBean>>> getCommentList(int skip, int take){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Skip", skip);
        paramsMap.put("Take", take);
        paramsMap.put("Data", 0);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.getCommentList(requestBody);
    }

    public LiveData<Result<Integer>> cmtAdd(CommentAtReq data){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Data", data);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.cmtAdd(requestBody);
    }

    public LiveData<Result<List<FriendBean>>> getFollowerList(int skip, int take){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Skip", skip);
        paramsMap.put("Take", take);
        paramsMap.put("Data", 0);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.getFollowerList(requestBody);
    }

    public LiveData<Result<List<FollowRequestInfo>>> getFollowerRequestList(int skip, int take){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Skip", skip);
        paramsMap.put("Take", take);
        paramsMap.put("Data", 0);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.getFollowerRequestList(requestBody);
    }

    public LiveData<Result<Boolean>> addFollowings(int uid){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Data", uid);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.addFollowings(requestBody);
    }

    public LiveData<Result<Boolean>> removeFollowings(int uid){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Data", uid);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.removeFollowings(requestBody);
    }
    public LiveData<Result<List<FollowBean>>> getFollowList(int skip, int take){
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("Skip", skip);
        paramsMap.put("Take", take);
        paramsMap.put("Data", 0);
        RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
        return userService.getFollowList(requestBody);
    }


    public LiveData<Resource<Void>> updateProfile(int type,String key,Object value){
        return new NetworkOnlyResource<Void, Result<Void>>() {
            @NonNull
            @Override
            protected LiveData<Result<Void>> createCall() {
                return userService.updateProfileInfo(RetrofitUtil.createJsonRequest(ProfileUtils.getUpdateInfo(type,key,value)));
            }

            @Override
            protected void saveCallResult(@NonNull Void item) {
                UserDao userDao = dbManager.getUserDao();
                String userId = imManager.getCurrentId();
                if (userDao != null) {
                    switch (key){
                        case "Name":
                            userDao.updateNickName(userId, (String)value,CharacterParser.getInstance().getSpelling((String)value));
                            break;
                        case "Bio":
                            userDao.updateBIO(userId, (String)value);
                            break;
                        case "Location":
                            userDao.updateLocation(userId, (String)value);
                            break;
                        case "School":
                            userDao.updateSchool(userId, (String)value);
                            break;
                        case "DOB":
                            userDao.updateDOB(userId, (String)value);
                            break;
                        case "Gender":
                            userDao.updateGender(userId, (boolean)value);
                            break;
                        case "NameColor":
                            userDao.updateNameColor(userId, (String)value);
                            break;
                    }

                }
            }
        }.asLiveData();
    }

    public LiveData<Resource<String>> upload(Uri uri){
        return new NetworkOnlyResource<String, Result<String>>() {
            @NonNull
            @Override
            protected LiveData<Result<String>> createCall() {
                File uploadFile = new File(uri.getPath());
                if (!uploadFile.exists()) {
                    uploadFile = new File(FileUtils.getRealPathFromUri(SealApp.getApplication(),uri));
                }
                RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile);
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//表单类型
//                builder.addFormDataPart("uploadFile", uploadFile.getPath(), imageBody);//imgfile 后台接收图片流的参数名
                builder.addFormDataPart("uploadFile", uploadFile.getAbsolutePath(), imageBody);//imgfile 后台接收图片流的参数名

                List<MultipartBody.Part> parts = builder.build().parts();
                SLog.e("niko",JSON.toJSONString(imageBody)+"--"+JSON.toJSONString(uploadFile));

                String imageBase64 = FileUtils.getImageBase64(uploadFile.getAbsolutePath());
                SLog.e("niko","base64 = "+ imageBase64);


                return uploadService.uploadAvatar(parts);
            }


            @Override
            protected void saveCallResult(@NonNull String item) {
                UserDao userDao = dbManager.getUserDao();
                String userId = imManager.getCurrentId();
                if (userDao != null) {
                    userDao.updateAvatar(userId, item);
                }

            }
        }.asLiveData();
    }

    public LiveData<Result<VIPCheckBean>> checkVip(){
        return userService.vipCheck();
    }

    public LiveData<Result<List<VIPConfigBean>>> vipInfo(){
        return userService.vipInfo();
    }

    public LiveData<Result<Boolean>> hasSetPassword(){
        return userService.hasSetPassword();
    }

    public LiveData<Resource<Boolean>> changePw(String oldPw,String newPw){

        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("OldPassword", oldPw);
                paramsMap.put("NewPassword", newPw);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return tokenService.changePwByOldPw(requestBody);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> setPw(String newPw){

        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("PhoneNumber", userCache.getUserCache().getPhoneNumber());
                paramsMap.put("PhoneCountry", "86");
                paramsMap.put("VCode", "9999");
                paramsMap.put("Password", newPw);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return tokenService.changePwByCode(requestBody);
            }
        }.asLiveData();
    }
}
