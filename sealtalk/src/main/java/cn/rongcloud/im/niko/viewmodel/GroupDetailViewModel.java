package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.rongcloud.im.niko.db.model.GroupEntity;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.GroupMember;
import cn.rongcloud.im.niko.model.GroupNoticeResult;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.net.request.GroupDataReq;
import cn.rongcloud.im.niko.task.GroupTask;
import cn.rongcloud.im.niko.task.PrivacyTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;
import cn.rongcloud.im.niko.utils.SingleSourceMapLiveData;
import cn.rongcloud.im.niko.utils.log.SLog;
import io.rong.imlib.model.Conversation;

/**
 * 群组详情视图模型
 */
public class GroupDetailViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<GroupEntity>> groupInfoLiveData = new SingleSourceLiveData<>();
    private SingleSourceMapLiveData<Resource<List<GroupMember>>, Resource<List<GroupMember>>> groupMemberListLiveData;

    private String groupId;
    private Conversation.ConversationType conversationType;
    private GroupTask groupTask;

    private SingleSourceLiveData<Resource<Boolean>> isNotifyLiveData = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Boolean>> isTopLiveData = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Void>> uploadPortraitResult = new SingleSourceLiveData<>();
    private SingleSourceMapLiveData<Resource<Boolean>, Resource<Boolean>> addGroupMemberResult;
    private SingleSourceMapLiveData<Resource<Boolean>, Resource<Boolean>> removeGroupMemberResult;
    private SingleSourceLiveData<Resource<Boolean>> renameGroupNameResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Boolean>> exitGroupResult = new SingleSourceLiveData<>();
    private MediatorLiveData<GroupMember> myselfInfo = new MediatorLiveData<>();

    private IMManager imManager;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);

        groupTask = new GroupTask(application);
        imManager = IMManager.getInstance();
    }

    public GroupDetailViewModel(@NonNull Application application, String targetId, Conversation.ConversationType conversationType) {
        super(application);
        this.groupId = targetId;
        this.conversationType = conversationType;

        groupTask = new GroupTask(application);
        imManager = IMManager.getInstance();

        groupInfoLiveData.setSource(groupTask.getGroupInfo(groupId));

        groupMemberListLiveData = new SingleSourceMapLiveData<>(new Function<Resource<List<GroupMember>>, Resource<List<GroupMember>>>() {
            @Override
            public Resource<List<GroupMember>> apply(Resource<List<GroupMember>> input) {
                if (input != null && input.data != null) {
                    List<GroupMember> tmpList = new ArrayList<>();
                    tmpList.addAll(input.data);

                    Collections.sort(tmpList, new Comparator<GroupMember>() {
                        @Override
                        public int compare(GroupMember lhs, GroupMember rhs) {
                            if (lhs.getRole() == GroupMember.Role.GROUP_OWNER.getValue()) {
                                return -1;
                            } else if (lhs.getRole() != GroupMember.Role.GROUP_OWNER.getValue() && rhs.getRole() == GroupMember.Role.GROUP_OWNER.getValue()) {
                                return 1;
                            } else if (lhs.getRole() == GroupMember.Role.MANAGEMENT.getValue() && rhs.getRole() == GroupMember.Role.MEMBER.getValue()) {
                                return -1;
                            } else if (lhs.getRole() == GroupMember.Role.MEMBER.getValue() && rhs.getRole() == GroupMember.Role.MANAGEMENT.getValue()) {
                                return 1;
                            } else if (lhs.getRole() == GroupMember.Role.MANAGEMENT.getValue() && rhs.getRole() == GroupMember.Role.MANAGEMENT.getValue()) {
                                return lhs.getJoinTime() > rhs.getJoinTime() ? 1 : -1;
                            } else if (lhs.getRole() == GroupMember.Role.MEMBER.getValue() && rhs.getRole() == GroupMember.Role.MEMBER.getValue()) {
                                return lhs.getJoinTime() > rhs.getJoinTime() ? 1 : -1;
                            }

                            return 0;
                        }
                    });
                    return new Resource<>(input.status, tmpList, input.code);
                }
                return new Resource<>(input.status, null, input.code);
            }
        });

        groupMemberListLiveData.setSource(groupTask.getGroupMemberInfoList(groupId));

        isNotifyLiveData.setSource(imManager.getConversationNotificationStatus(conversationType, targetId));
        isTopLiveData.setSource(imManager.getConversationIsOnTop(conversationType, targetId));

        addGroupMemberResult = new SingleSourceMapLiveData<>(resource -> {
            // 考虑到新增成员后一些数据需要同步所以重新加载群组信息和新成员信息
            refreshGroupInfo();
            refreshGroupMemberList();
            return resource;
        });

        removeGroupMemberResult = new SingleSourceMapLiveData<>(resource -> {
            // 删除成员后一些数据需要同步所以重新加载群组信息，因为是删除操作所以不需要再加载成员信息
            refreshGroupInfo();
            return resource;
        });

        myselfInfo.addSource(groupMemberListLiveData, new Observer<Resource<List<GroupMember>>>() {
            @Override
            public void onChanged(Resource<List<GroupMember>> listResource) {
                if (listResource.data != null && listResource.data.size() > 0) {
                    for (GroupMember member : listResource.data) {
                        if (member.getUserId().equals(imManager.getCurrentId())) {
                            myselfInfo.postValue(member);
                            break;
                        }
                    }
                }
            }
        });

    }

    /**
     * 刷新群组信息
     * 此方法一般不需要调用，默认在初始化时会数据刷新，仅在特殊情况需要请求网络同步最新数据时需要
     */
    public void refreshGroupInfo() {
        groupInfoLiveData.setSource(groupTask.getGroupInfo(groupId));
    }

    /**
     * 刷新群组成员列表
     * 此方法一般不需要调用，默认在初始化时会数据刷新，仅在特殊情况需要请求网络同步最新数据时需要
     */
    public void refreshGroupMemberList() {
        groupMemberListLiveData.setSource(groupTask.getGroupMemberInfoList(groupId));
    }

    /**
     * 设置是否消息免打扰
     *
     * @param isNotify
     */
    public void setIsNotifyConversation(final boolean isNotify) {
        Resource<Boolean> value = isNotifyLiveData.getValue();
        if (value != null && value.data != null && value.data == isNotify) return;

        isNotifyLiveData.setSource(imManager.setConversationNotificationStatus(conversationType, groupId, isNotify));
    }

    /**
     * 设置会话置顶
     *
     * @param isTop
     */
    public void setConversationOnTop(boolean isTop) {
        Resource<Boolean> value = isTopLiveData.getValue();
        if (value != null && value.data != null && value.data == isTop) return;

        isTopLiveData.setSource(imManager.setConversationToTop(conversationType, groupId, isTop));

        if(isTop) {
            groupTask.topChatYes(groupId);
        }else {
            groupTask.topChatNo(groupId);
        }
    }

    /**
     * 获取会话是否接受消息通知
     *
     * @return
     */
    public MutableLiveData<Resource<Boolean>> getIsNotify() {
        return isNotifyLiveData;
    }

    /**
     * 获取会话是否置顶
     *
     * @return
     */
    public MutableLiveData<Resource<Boolean>> getIsTop() {
        return isTopLiveData;
    }



    /**
     * 获取群组信息
     *
     * @return
     */
    public LiveData<Resource<GroupEntity>> getGroupInfo() {
        return groupInfoLiveData;
    }

    /**
     * 上传并设置头像
     *
     * @param imageUri
     */
    public void setGroupPortrait(Uri imageUri) {
        uploadPortraitResult.setSource(groupTask.uploadAndSetGroupPortrait(groupId, imageUri));
    }

    /**
     * 获取上传群组头像结果
     *
     * @return
     */
    public LiveData<Resource<Void>> getUploadPortraitResult() {
        return uploadPortraitResult;
    }

    /**
     * 获取群组成员列表
     *
     * @return
     */
    public LiveData<Resource<List<GroupMember>>> getGroupMemberList() {
        return groupMemberListLiveData;
    }

    /**
     * 添加群组成员
     *
     * @param memberIdList
     */
    public void addGroupMember(List<String> memberIdList) {
        if (memberIdList != null && memberIdList.size() > 0) {
            try {
                GroupDataReq groupDataReq = new GroupDataReq();
                groupDataReq.setChatGrpID(Integer.parseInt(groupId));
                List<Integer> list = new ArrayList<>();
                for (String id : memberIdList){
                    list.add(Integer.parseInt(id));
                }
                groupDataReq.setUIDs(list);
                addGroupMemberResult.setSource(groupTask.addGroupMember(groupDataReq));
            }catch (Exception e){
                SLog.e("addGroupMember",e.getMessage());
            }

        }
    }

    /**
     * 移除群组成员
     *
     * @param memberIdList
     */
    public void removeGroupMember(List<String> memberIdList) {
        if (memberIdList != null && memberIdList.size() > 0) {
            removeGroupMemberResult.setSource(groupTask.kickGroupMember(groupId, memberIdList));
        }
    }

    /**
     * 获取添加群组成员结果
     *
     * @return
     */
    public LiveData<Resource<Boolean>> getAddGroupMemberResult() {
        return addGroupMemberResult;
    }

    /**
     * 获取日出群组成员结果
     *
     * @return
     */
    public LiveData<Resource<Boolean>> getRemoveGroupMemberResult() {
        return removeGroupMemberResult;
    }

    /**
     * 修改群组名称
     *
     * @param newGroupName
     */
    public void renameGroupName(String newGroupName) {
        renameGroupNameResult.setSource(groupTask.renameGroup(groupId, newGroupName));
    }

    /**
     * 获取修改群组名称结果
     *
     * @return
     */
    public LiveData<Resource<Boolean>> getRenameGroupResult() {
        return renameGroupNameResult;
    }

    /**
     * 解散群组
     */
    public void dismissGroup() {
        exitGroupResult.setSource(groupTask.dismissGroup(groupId));
    }

    /**
     * 退出群组
     */
    public void exitGroup() {
        exitGroupResult.setSource(groupTask.quitGroup(groupId));
    }

    /**
     * 获取退出或解散群组的结果
     *
     * @return
     */
    public LiveData<Resource<Boolean>> getExitGroupResult() {
        return exitGroupResult;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public LiveData<GroupMember> getMyselfInfo() {
        return myselfInfo;
    }






    public static class Factory implements ViewModelProvider.Factory {
        private String targetId;
        private Conversation.ConversationType conversationType;
        private Application application;

        public Factory(Application application, String targetId, Conversation.ConversationType conversationType) {
            this.conversationType = conversationType;
            this.targetId = targetId;
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(Application.class, String.class, Conversation.ConversationType.class).newInstance(application, targetId, conversationType);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }
}
