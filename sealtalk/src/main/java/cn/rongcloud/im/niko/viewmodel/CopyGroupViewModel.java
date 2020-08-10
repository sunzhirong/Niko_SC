package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cn.rongcloud.im.niko.db.model.GroupEntity;
import cn.rongcloud.im.niko.model.CopyGroupResult;
import cn.rongcloud.im.niko.model.GroupMember;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.task.GroupTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;

public class CopyGroupViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<GroupEntity>> groupInfo = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<CopyGroupResult>> copyGroupResult = new SingleSourceLiveData<>();
    private GroupTask groupTask;

    public CopyGroupViewModel(@NonNull Application application) {
        super(application);

        groupTask = new GroupTask(application);
    }

    public void requestGroupInfo(String groupId) {
        groupInfo.setSource(groupTask.getGroupInfo(groupId));
    }

    public LiveData<Resource<GroupEntity>> getGroupInfo() {
        return groupInfo;
    }

    /**
     * 复制群组
     *
     * @param groupId
     * @param name
     * @param portraitUri
     */
    public void copyGroup(String groupId, String name, String portraitUri) {
        copyGroupResult.setSource(groupTask.copyGroup(groupId, name, portraitUri));
    }

    public LiveData<Resource<CopyGroupResult>> getCopyGroupResult() {
        return copyGroupResult;
    }

    public LiveData<List<GroupMember>> getGroupMemberInfoList(String groupId) {
        return groupTask.getGroupMemberInfoListInDB(groupId);
    }
}

