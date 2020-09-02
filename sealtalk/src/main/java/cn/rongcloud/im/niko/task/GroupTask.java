package cn.rongcloud.im.niko.task;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.rongcloud.im.niko.common.ErrorCode;
import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.db.dao.GroupDao;
import cn.rongcloud.im.niko.db.dao.GroupMemberDao;
import cn.rongcloud.im.niko.db.dao.UserDao;
import cn.rongcloud.im.niko.db.model.GroupEntity;
import cn.rongcloud.im.niko.db.model.GroupExitedMemberInfo;
import cn.rongcloud.im.niko.db.model.GroupMemberInfoDes;
import cn.rongcloud.im.niko.db.model.GroupMemberInfoEntity;
import cn.rongcloud.im.niko.db.model.GroupNoticeInfo;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.file.FileManager;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.AddMemberResult;
import cn.rongcloud.im.niko.model.CopyGroupResult;
import cn.rongcloud.im.niko.model.GroupInfoBean;
import cn.rongcloud.im.niko.model.GroupMember;
import cn.rongcloud.im.niko.model.GroupMemberInfoResult;
import cn.rongcloud.im.niko.model.GroupNoticeInfoResult;
import cn.rongcloud.im.niko.model.GroupNoticeResult;
import cn.rongcloud.im.niko.model.GroupResult;
import cn.rongcloud.im.niko.model.RegularClearStatusResult;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.model.UserSimpleInfo;
import cn.rongcloud.im.niko.model.niko.ProfileHeadInfo;
import cn.rongcloud.im.niko.net.HttpClientManager;
import cn.rongcloud.im.niko.net.RetrofitUtil;
import cn.rongcloud.im.niko.net.request.GroupDataReq;
import cn.rongcloud.im.niko.net.service.GroupService;
import cn.rongcloud.im.niko.ui.adapter.models.SearchGroupMember;
import cn.rongcloud.im.niko.utils.NetworkBoundResource;
import cn.rongcloud.im.niko.utils.NetworkOnlyResource;
import cn.rongcloud.im.niko.utils.RongGenerate;
import cn.rongcloud.im.niko.utils.SearchUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import cn.rongcloud.im.niko.utils.log.SLog;
import io.rong.imkit.RongIM;
import io.rong.imkit.tools.CharacterParser;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.RequestBody;

public class GroupTask {
    private GroupService groupService;
    private Context context;
    private DbManager dbManager;
    private FileManager fileManager;

    public GroupTask(Context context) {
        this.context = context.getApplicationContext();
        groupService = HttpClientManager.getInstance(context).getClient().createService(GroupService.class);
        dbManager = DbManager.getInstance(context);
        fileManager = new FileManager(context);
    }

    /**
     * 创建群组
     *
     * @return
     */
    public LiveData<Resource<Integer>> createGroup(GroupDataReq data) {
        return new NetworkOnlyResource<Integer, Result<Integer>>() {
            @NonNull
            @Override
            protected LiveData<Result<Integer>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("Data", data);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return groupService.createGroup(requestBody);
            }
        }.asLiveData();
    }

    /**
     * 添加群成员
     *
     * @return
     */
    public LiveData<Resource<Boolean>> addGroupMember(GroupDataReq data) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("Data", data);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return groupService.addGroupMember(requestBody);
            }
        }.asLiveData();
    }


    /**
     * 群主或群管理将群成员移出群组
     *
     * @param groupId
     * @param memberList
     * @return
     */
    public LiveData<Resource<Boolean>> kickGroupMember(String groupId, List<String> memberList) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                if(item) {
                    GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
                    if (groupMemberDao != null) {
                        groupMemberDao.deleteGroupMember(groupId, memberList);
                    }
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {

                GroupDataReq groupDataReq = new GroupDataReq();
                groupDataReq.setChatGrpID(Integer.parseInt(groupId));
                List<Integer> list = new ArrayList<>();
                for (String id : memberList){
                    list.add(Integer.parseInt(id));
                }
                groupDataReq.setUIDs(list);

                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("Data", groupDataReq);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return groupService.kickMember(requestBody);
            }
        }.asLiveData();
    }

    /**
     * 退出群组
     *
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> quitGroup(String groupId) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    groupDao.deleteGroup(groupId);
                }

                GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
                if (groupMemberDao != null) {
                    groupMemberDao.deleteGroupMember(groupId);
                }

                IMManager.getInstance().clearConversationAndMessage(groupId, Conversation.ConversationType.GROUP);
            }

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Data", Integer.parseInt(groupId));
                return groupService.quitGroup(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    /**
     * 解散群组
     *
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> dismissGroup(String groupId) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    groupDao.deleteGroup(groupId);
                }

                GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
                if (groupMemberDao != null) {
                    groupMemberDao.deleteGroupMember(groupId);
                }

                IMManager.getInstance().clearConversationAndMessage(groupId, Conversation.ConversationType.GROUP);
            }

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Data", Integer.parseInt(groupId));
                return groupService.dismissGroup(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }


    /**
     * 重命名群名称
     *
     * @param groupId
     * @param groupName
     * @return
     */
    public LiveData<Resource<Boolean>> renameGroup(String groupId, String groupName) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                // 更新数据库中群组的名称
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    int updateResult;
                    updateResult = groupDao.updateGroupName(groupId, groupName, CharacterParser.getInstance().getSelling(groupName));

                    // 更新成时同时更新缓存
                    if (updateResult > 0) {
                        GroupEntity groupInfo = groupDao.getGroupInfoSync(groupId);
                        if (groupInfo != null) {
                            IMManager.getInstance().updateGroupInfoCache(groupId, groupName, Uri.parse(groupInfo.getPortraitUri()));
                        }
                    }
                }

            }

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                HashMap<String, Object> dataMap = new HashMap<>();
                dataMap.put("ChatGrpID",Integer.parseInt(groupId));
                dataMap.put("Title",groupName);
                dataMap.put("Note","");
                bodyMap.put("Data",dataMap);
                return groupService.renameGroup(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }



    public LiveData<Resource<Boolean>> topChatYes(String id) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {
            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("Data",Integer.parseInt(id));
                return groupService.topYes(RetrofitUtil.createJsonRequest(bodyMap));
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
                return groupService.topNo(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    /**
     * 上传并设置群组头像
     *
     * @param groupId
     * @param portraitUrl
     * @return
     */
    public LiveData<Resource<Void>> uploadAndSetGroupPortrait(String groupId, Uri portraitUrl) {
        MediatorLiveData<Resource<Void>> result = new MediatorLiveData<>();
        // 先上传图片文件
//        LiveData<Resource<String>> uploadResource = fileManager.uploadImage(portraitUrl);
//        result.addSource(uploadResource, resource -> {
//            if (resource.status != Status.LOADING) {
//                result.removeSource(uploadResource);
//            }
//
//            if (resource.status == Status.ERROR) {
//                result.setValue(Resource.error(resource.code, null));
//                return;
//            }
//
//            if (resource.status == Status.SUCCESS) {
//                String uploadUrl = resource.data;
//
//                // 获取上传成功的地址后更新地址
//                LiveData<Resource<Void>> setPortraitResource = setGroupPortrait(groupId, uploadUrl);
//                result.addSource(setPortraitResource, portraitResultResource -> {
//                    if (portraitResultResource.status != Status.LOADING) {
//                        result.removeSource(setPortraitResource);
//                    }
//
//                    if (portraitResultResource.status == Status.ERROR) {
//                        result.setValue(Resource.error(portraitResultResource.code, null));
//                        return;
//                    }
//
//                    if (portraitResultResource.status == Status.SUCCESS) {
//                        result.setValue(Resource.success(null));
//                    }
//                });
//            }
//        });

        return result;
    }

    /**
     * 设置群组头像
     *
     * @param groupId
     * @param portraitUrl 云存储空间的 url
     * @return
     */
    private LiveData<Resource<Void>> setGroupPortrait(String groupId, String portraitUrl) {
        return new NetworkOnlyResource<Void, Result>() {
            @Override
            protected void saveCallResult(@NonNull Void item) {
                // 更新数据库中群组的头像
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    int updateResult;
                    updateResult = groupDao.updateGroupPortrait(groupId, portraitUrl);

                    // 更新成时同时更新缓存
                    if (updateResult > 0) {
                        GroupEntity groupInfo = groupDao.getGroupInfoSync(groupId);
                        IMManager.getInstance().updateGroupInfoCache(groupId, groupInfo.getName(), Uri.parse(portraitUrl));
                    }
                }
            }

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("groupId", groupId);
                bodyMap.put("portraitUri", portraitUrl);
                return groupService.setGroupPortraitUri(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }


    /**
     * 获取群组信息
     *
     * @param groupId
     * @return
     */
    public LiveData<Resource<GroupEntity>> getGroupInfo(final String groupId) {
        return new NetworkBoundResource<GroupEntity, Result<GroupInfoBean>>() {
            @Override
            protected void saveCallResult(@NonNull Result<GroupInfoBean> item) {
                if (item.getRsData() == null) return;

                GroupInfoBean rsData = item.getRsData();
                GroupEntity groupEntity = new GroupEntity();

                groupEntity.setId(String.valueOf(rsData.getChatGrpID()));
                groupEntity.setName(rsData.getTitle());
                groupEntity.setBulletin(rsData.getNote());
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    // 判断是否在通讯录中
                    int groupIsContact = groupDao.getGroupIsContactSync(groupId);
                    int regularClearState = groupDao.getRegularClearSync(groupId);

                    String portraitUri = groupEntity.getPortraitUri();
                    if (TextUtils.isEmpty(portraitUri)) {
                        portraitUri = RongGenerate.generateDefaultAvatar(context, groupEntity.getId(), groupEntity.getName());
                        groupEntity.setPortraitUri(portraitUri);
                    }
                    groupEntity.setNameSpelling(SearchUtils.fullSearchableString(groupEntity.getName()));
                    groupEntity.setNameSpellingInitial(SearchUtils.initialSearchableString(groupEntity.getName()));
                    groupEntity.setOrderSpelling(CharacterParser.getInstance().getSelling(groupEntity.getName()));
                    groupEntity.setIsInContact(groupIsContact);
                    groupEntity.setRegularClearState(regularClearState);
                    groupDao.insertGroup(groupEntity);
                }

                // 更新 IMKit 缓存群组数据
                IMManager.getInstance().updateGroupInfoCache(groupEntity.getId(), groupEntity.getName(), Uri.parse(groupEntity.getPortraitUri()));
            }

            @NonNull
            @Override
            protected LiveData<GroupEntity> loadFromDb() {
                GroupDao groupDao = dbManager.getGroupDao();
                LiveData<GroupEntity> groupInfo = null;
                if (groupDao != null) {
                    groupInfo = groupDao.getGroupInfo(groupId);
                } else {
                    groupInfo = new MutableLiveData<>(null);
                }
                return groupInfo;
            }

            @NonNull
            @Override
            protected LiveData<Result<GroupInfoBean>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("Data", groupId);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return groupService.getGroupInfo(requestBody);
            }
        }.asLiveData();
    }

    /**
     * 获取群组信息 ( 同步方法 )
     *
     * @param groupId
     * @return
     */
    public GroupEntity getGroupInfoSync(final String groupId) {
        return dbManager.getGroupDao().getGroupInfoSync(groupId);
    }



    public LiveData<GroupEntity> getGroupInfoInDB(String groupIds) {
        GroupDao groupDao = dbManager.getGroupDao();
        LiveData<GroupEntity> groupInfo = null;
        if (groupDao != null) {
            groupInfo = groupDao.getGroupInfo(groupIds);
        } else {
            groupInfo = new MutableLiveData<>(null);
        }
        return groupInfo;
    }

    /**
     * 获取群成员列表,通过成员名称筛选
     *
     * @param groupId
     * @param filterByName 通过姓名模糊匹配
     * @return
     */
    public LiveData<Resource<List<GroupMember>>> getGroupMemberInfoList(final String groupId, String filterByName) {
        return new NetworkBoundResource<List<GroupMember>, Result<GroupInfoBean>>() {
            @Override
            protected void saveCallResult(@NonNull Result<GroupInfoBean> item) {
                if (item.getRsData() == null) return;
                GroupInfoBean rsData = item.getRsData();
                if(rsData.getUserHeads()==null||rsData.getUserHeads().size()==0)return;

                GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
                UserDao userDao = dbManager.getUserDao();

                // 获取新数据前清除掉原成员信息
                if (groupMemberDao != null) {
                    groupMemberDao.deleteGroupMember(groupId);
                }
                List<ProfileHeadInfo> result = rsData.getUserHeads();
                List<GroupMemberInfoEntity> groupEntityList = new ArrayList<>();
                List<UserInfo> newUserList = new ArrayList<>();
                for (ProfileHeadInfo info : result) {
                    if(info.getUID()==0||TextUtils.isEmpty(info.getName())){continue;}
                    UserSimpleInfo user = new UserSimpleInfo();
                    GroupMemberInfoEntity groupEntity = new GroupMemberInfoEntity();
                    groupEntity.setGroupId(groupId);
                    user.setId(String.valueOf(info.getUID()));
                    user.setPortraitUri(GlideImageLoaderUtil.getScString(info.getUserIcon()));
                    user.setName(TextUtils.isEmpty(info.getAlias()) ? info.getName() : info.getAlias());
                    user.setNameColor(info.getNameColor());
                    // 默认优先显示群备注名。当没有群备注时，则看此用户为当前用户的好友，如果是好友则显示备注名称。其次再试显示用户名
                    String displayName = TextUtils.isEmpty(info.getAlias()) ? info.getName() : info.getAlias();
                    String nameInKitCache = displayName;

                    if (TextUtils.isEmpty(nameInKitCache)) {
                        nameInKitCache = user.getName();
                    }
                    groupEntity.setNickName(displayName);
                    groupEntity.setNickNameSpelling(SearchUtils.fullSearchableString(displayName));
                    groupEntity.setUserId(user.getId());
                    groupEntity.setRole(info.getUID() == rsData.getCreatorUID()?GroupMember.Role.GROUP_OWNER.getValue():GroupMember.Role.MEMBER.getValue());
                    groupEntity.setNameColor(info.getNameColor());
                    groupEntityList.add(groupEntity);

                    // 更新 IMKit 缓存群组成员数据
                    IMManager.getInstance().updateGroupMemberInfoCache(groupId, user.getId(), nameInKitCache);

                    if (userDao != null) {
                        // 更新已存在的用户信息
                        String portraitUri = user.getPortraitUri();

                        // 当没有头像时生成默认头像
                        if (TextUtils.isEmpty(portraitUri)) {
                            portraitUri = RongGenerate.generateDefaultAvatar(context, user.getId(), user.getName());
                            user.setPortraitUri(portraitUri);
                        }
                        int updateResult = userDao.updateNameAndPortrait(user.getId(), user.getName(), CharacterParser.getInstance().getSelling(user.getName()), user.getPortraitUri());

                        // 当没有更新成功时，添加到新用户列表中
                        if (updateResult == 0) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.setId(user.getId());
                            userInfo.setName(user.getName());
                            userInfo.setNameSpelling(SearchUtils.fullSearchableString(user.getName()));
                            userInfo.setPortraitUri(user.getPortraitUri());
                            userInfo.setNameColor(info.getNameColor());
                            newUserList.add(userInfo);
                        }
                    }
                }

                // 更新群组成员
                if (groupMemberDao != null) {
                    groupMemberDao.insertGroupMemberList(groupEntityList);
                }

                if (userDao != null) {
                    // 插入新的用户信息
                    userDao.insertUserListIgnoreExist(newUserList);
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<GroupMember> data) {
                boolean shouldFetch = true;
                // 当数据库中有群成员数据时，当用姓名进行筛选时不进行网络请求
                if (data != null && data.size() > 0 && !TextUtils.isEmpty(filterByName)) {
                    shouldFetch = false;
                }
                return shouldFetch;
            }

            @NonNull
            @Override
            protected LiveData<List<GroupMember>> loadFromDb() {
                GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
                if (groupMemberDao != null) {
                    if (TextUtils.isEmpty(filterByName)) {
                        return groupMemberDao.getGroupMemberList(groupId);
                    } else {
                        return groupMemberDao.getGroupMemberList(groupId, filterByName);
                    }
                }
                return new MutableLiveData<>(null);
            }

            @NonNull
            @Override
            protected LiveData<Result<GroupInfoBean>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("Data", groupId);
                RequestBody requestBody = RetrofitUtil.createJsonRequest(paramsMap);
                return groupService.getGroupMemberList(requestBody);
            }
        }.asLiveData();
    }

    /**
     * 获取群成员列表
     *
     * @param groupId
     * @return
     */
    public LiveData<Resource<List<GroupMember>>> getGroupMemberInfoList(final String groupId) {
        return getGroupMemberInfoList(groupId, null);
    }


    public LiveData<List<GroupMember>> searchGroupMemberInDB(final String groupId, String searchKey) {
        GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
        if (groupMemberDao != null) {
            return groupMemberDao.searchGroupMember(groupId, searchKey);
        }
        return null;
    }

    public LiveData<List<SearchGroupMember>> searchGroup(String match) {
        return dbManager.getGroupDao().searchGroup(match);
    }

    public LiveData<List<GroupEntity>> searchGroupByName(String match) {
        return dbManager.getGroupDao().searchGroupByName(match);
    }



    /**
     * 获取所有群信息
     *
     * @return
     */
    public LiveData<List<GroupEntity>> getAllGroupInfoList() {
        return dbManager.getGroupDao().getAllGroupInfoList();
    }


    /**
     * 获取群通知消息详情
     *
     * @return
     */

    public LiveData<Resource<List<GroupNoticeInfo>>> getGroupNoticeInfo() {
        return new NetworkBoundResource<List<GroupNoticeInfo>, Result<List<GroupNoticeInfoResult>>>() {

            @Override
            protected void saveCallResult(@NonNull Result<List<GroupNoticeInfoResult>> item) {
                if (item.getRsData() == null) return;

                GroupDao groupDao = dbManager.getGroupDao();

                List<GroupNoticeInfoResult> resultList = item.getRsData();
                List<GroupNoticeInfo> infoList = new ArrayList<>();
                if (resultList != null && resultList.size() > 0) {
                    List<String> idList = new ArrayList<>();
                    for (GroupNoticeInfoResult infoResult : resultList) {
                        GroupNoticeInfo noticeInfo = new GroupNoticeInfo();
                        noticeInfo.setId(infoResult.id);
                        idList.add(infoResult.id);
                        noticeInfo.setCreatedAt(infoResult.createdAt);
                        noticeInfo.setCreatedTime(infoResult.timestamp);
                        noticeInfo.setType(infoResult.type);
                        noticeInfo.setStatus(infoResult.status);
                        if (infoResult.receiver != null) {
                            noticeInfo.setReceiverId(infoResult.receiver.id);
                            noticeInfo.setReceiverNickName(infoResult.receiver.nickname);
                        }
                        if (infoResult.requester != null) {
                            noticeInfo.setRequesterId(infoResult.requester.id);
                            noticeInfo.setRequesterNickName(infoResult.requester.nickname);
                        }
                        if (infoResult.group != null) {
                            noticeInfo.setGroupId(infoResult.group.id);
                            noticeInfo.setGroupNickName(infoResult.group.name);
                        }
                        infoList.add(noticeInfo);
                    }
                    //防止直接 delteAll 导致的返回数据 success 状态导致返回错误的空数据结果
                    groupDao.deleteAllGroupNotice(idList);
                    groupDao.insertGroupNotice(infoList);
                } else if (resultList != null) {
                    // 返回无通知数据时清空数据库的数据
                    groupDao.deleteAllGroupNotice();
                }

            }


            @NonNull
            @Override
            protected LiveData<List<GroupNoticeInfo>> loadFromDb() {
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    LiveData<List<GroupNoticeInfo>> liveInfoList = groupDao.getGroupNoticeList();
                    return liveInfoList;
                }
                return new MutableLiveData<>(null);
            }

            @NonNull
            @Override
            protected LiveData<Result<List<GroupNoticeInfoResult>>> createCall() {
                return groupService.getGroupNoticeInfo();
            }

        }.asLiveData();
    }



    /**
     * 获取群通知消息详情
     *
     * @return
     */

    public LiveData<Resource<List<GroupExitedMemberInfo>>> getGroupExitedMemberInfo(String groupId) {
        return new NetworkBoundResource<List<GroupExitedMemberInfo>, Result<List<GroupExitedMemberInfo>>>() {

            @Override
            protected void saveCallResult(@NonNull Result<List<GroupExitedMemberInfo>> item) {
                if (item.getRsData() == null) return;

                GroupDao groupDao = dbManager.getGroupDao();

                List<GroupExitedMemberInfo> resultList = item.getRsData();
                if (groupDao != null) {
                    groupDao.deleteAllGroupExited();
                }
                if (resultList != null && resultList.size() > 0) {
                    groupDao.insertGroupExited(resultList);
                }
            }


            @NonNull
            @Override
            protected LiveData<List<GroupExitedMemberInfo>> loadFromDb() {
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    LiveData<List<GroupExitedMemberInfo>> liveInfoList = groupDao.getGroupExitedList();
                    return liveInfoList;
                }
                return new MutableLiveData<>(null);
            }

            @NonNull
            @Override
            protected LiveData<Result<List<GroupExitedMemberInfo>>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("groupId", groupId);
                return groupService.getGroupExitedMemberInfo(RetrofitUtil.createJsonRequest(bodyMap));
            }

        }.asLiveData();
    }

    /**
     * 获取群成员用户信息
     *
     * @param groupId
     * @param memberId
     * @return
     */

    public LiveData<Resource<GroupMemberInfoDes>> getGroupMemberInfoDes(String groupId, String memberId) {
        return new NetworkBoundResource<GroupMemberInfoDes, Result<GroupMemberInfoDes>>() {

            @Override
            protected void saveCallResult(@NonNull Result<GroupMemberInfoDes> item) {
                if (item.getRsData() == null) return;

                GroupDao groupDao = dbManager.getGroupDao();

                GroupMemberInfoDes info = item.getRsData();
                info.setGroupId(groupId);
                info.setMemberId(memberId);
                if (groupDao != null && info != null) {
                    groupDao.insertGroupMemberInfoDes(info);
                }
            }


            @NonNull
            @Override
            protected LiveData<GroupMemberInfoDes> loadFromDb() {
                GroupDao groupDao = dbManager.getGroupDao();
                if (groupDao != null) {
                    LiveData<GroupMemberInfoDes> info = groupDao.getGroupMemberInfoDes(groupId, memberId);
                    return info;
                }
                return new MutableLiveData<>(null);
            }

            @NonNull
            @Override
            protected LiveData<Result<GroupMemberInfoDes>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("groupId", groupId);
                bodyMap.put("memberId", memberId);
                return groupService.getGroupInfoDes(RetrofitUtil.createJsonRequest(bodyMap));
            }

        }.asLiveData();
    }
}
