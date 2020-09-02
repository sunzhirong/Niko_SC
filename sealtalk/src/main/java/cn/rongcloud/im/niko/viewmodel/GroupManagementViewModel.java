package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;

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
import cn.rongcloud.im.niko.model.GroupMember;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.task.GroupTask;
import cn.rongcloud.im.niko.utils.CharacterParser;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;
import cn.rongcloud.im.niko.utils.SingleSourceMapLiveData;

public class GroupManagementViewModel extends AndroidViewModel {
    private String groupId;
    private MediatorLiveData<Resource<List<GroupMember>>> groupManagements = new MediatorLiveData<>();
    private MutableLiveData<GroupMember> groupOwner = new MutableLiveData<>();
    private MediatorLiveData<GroupEntity> groupInfo = new MediatorLiveData<>();
    private GroupTask groupTask;
    private SingleSourceMapLiveData<Resource<List<GroupMember>>, List<GroupMember>> groupMembersWithoutGroupOwner;

    public GroupManagementViewModel(@NonNull Application application) {
        super(application);
    }

    public GroupManagementViewModel(String groupId, @NonNull Application application) {
        super(application);
        groupTask = new GroupTask(application);
        this.groupId = groupId;
        groupMemberInfo(groupId);
        getGroupInfo(groupId);
    }

    private void getGroupInfo(String groupId) {
        LiveData<GroupEntity> mGroupEntity = groupTask.getGroupInfoInDB(groupId);
        groupInfo.addSource(mGroupEntity, new Observer<GroupEntity>() {
            @Override
            public void onChanged(GroupEntity groupEntity) {
                if (groupEntity != null) {
                    groupInfo.removeSource(mGroupEntity);
                    groupInfo.postValue(groupEntity);
                }
            }
        });
    }


    /**
     * 获取群信息
     *
     * @return
     */
    public LiveData<GroupEntity> getGroupInfo() {
        return groupInfo;
    }

    private void groupMemberInfo(String groupId) {
        LiveData<Resource<List<GroupMember>>> getGroupMembers = groupTask.getGroupMemberInfoList(groupId);
        groupManagements.addSource(getGroupMembers, new Observer<Resource<List<GroupMember>>>() {
            @Override
            public void onChanged(Resource<List<GroupMember>> listResource) {
                List<GroupMember> managements = new ArrayList<>();
                if (listResource != null && listResource.data != null && listResource.data.size() > 0) {
                    List<GroupMember> data = listResource.data;
                    for (GroupMember member : data) {
                        if (member.getMemberRole() == GroupMember.Role.GROUP_OWNER) {
                            groupOwner.postValue(member);
                        } else if (member.getMemberRole() == GroupMember.Role.MANAGEMENT) {
                            managements.add(member);
                        }
                    }
                }

                groupManagements.postValue(new Resource<>(listResource.status, managements, listResource.code));
            }
        });


        groupMembersWithoutGroupOwner = new SingleSourceMapLiveData<>(new Function<Resource<List<GroupMember>>, List<GroupMember>>() {
            @Override
            public List<GroupMember> apply(Resource<List<GroupMember>> input) {
                List<GroupMember> withoutGroupOnwer = new ArrayList<>();
                if (input != null && input.data != null && input.data.size() > 0) {
                    List<GroupMember> data = input.data;
                    withoutGroupOnwer.addAll(data);
                    for (GroupMember member : data) {
                        if (member.getMemberRole() == GroupMember.Role.GROUP_OWNER) {
                            withoutGroupOnwer.remove(member);
                        }
                        String sortString = "#";
                        //汉字转换成拼音
                        String pinyin = CharacterParser.getInstance().getSpelling(member.getName());
                        if (pinyin != null) {
                            if (pinyin.length() > 0) {
                                sortString = pinyin.substring(0, 1).toUpperCase();
                            }
                        }
                        // 正则表达式，判断首字母是否是英文字母
                        if (sortString.matches("[A-Z]")) {
                            member.setNameSpelling(sortString.toUpperCase());
                        } else {
                            member.setNameSpelling("#");
                        }
                    }
                    Collections.sort(withoutGroupOnwer, new Comparator<GroupMember>() {
                        @Override
                        public int compare(GroupMember o1, GroupMember o2) {
                            if (o1.getNameSpelling().equals("@") || o2.getNameSpelling().equals("#")) {
                                return -1;
                            } else if (o1.getNameSpelling().equals("#") || o2.getNameSpelling().equals("@")) {
                                return 1;
                            } else {
                                return o1.getNameSpelling().compareTo(o2.getNameSpelling());
                            }
                        }
                    });
                    return withoutGroupOnwer;
                }
                return null;
            }
        });
        groupMembersWithoutGroupOwner.setSource(getGroupMembers);
    }

    /**
     * 群主
     *
     * @return
     */
    public LiveData<GroupMember> getGroupOwner() {
        return groupOwner;
    }

    /**
     * 获去群管理
     *
     * @return
     */
    public LiveData<Resource<List<GroupMember>>> getGroupManagements() {
        return groupManagements;
    }





    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private String groupId;
        private Application application;

        public Factory(String groupId, Application application) {
            this.groupId = groupId;
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(String.class, Application.class).newInstance(groupId, application);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }

}
