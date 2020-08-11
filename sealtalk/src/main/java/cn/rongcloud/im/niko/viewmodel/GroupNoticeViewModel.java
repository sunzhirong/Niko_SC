package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import cn.rongcloud.im.niko.db.model.GroupEntity;
import cn.rongcloud.im.niko.model.GroupNoticeResult;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.task.GroupTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;

public class GroupNoticeViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<GroupEntity>> groupInfo = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<GroupNoticeResult>> groupNotice = new SingleSourceLiveData<>();

    private GroupTask groupTask;
    private String groupId;

    public GroupNoticeViewModel(@NonNull Application application) {
        super(application);

        groupTask = new GroupTask(application);
    }

    public GroupNoticeViewModel(@NonNull Application application, String groupId){
        this(application);

        this.groupId = groupId;

        requestGroupInfo(groupId);

    }

    /**
     * 请求群组信息
     *
     * @param groupId
     */
    public void requestGroupInfo(String groupId){
        groupInfo.setSource(groupTask.getGroupInfo(groupId));
    }

    /**
     * 获取群组信息
     *
     * @return
     */
    public LiveData<Resource<GroupEntity>> getGroupInfo(){
        return groupInfo;
    }



    public static class Factory implements ViewModelProvider.Factory {
        private String targetId;
        private Application application;

        public Factory(Application application,String targetId) {
            this.targetId = targetId;
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(Application.class, String.class).newInstance(application, targetId);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }
}
