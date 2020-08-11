package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import cn.rongcloud.im.niko.db.model.FriendDescription;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.task.FriendTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;

/**
 * 用户详细视图模型
 */
public class EditUserDescribeViewModel extends AndroidViewModel {

    private SingleSourceLiveData<Resource<FriendDescription>> friendDescription = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Boolean>> setFriendDescriptionResult = new SingleSourceLiveData<>();
    private FriendTask friendTask;
    private String userId;

    public EditUserDescribeViewModel(@NonNull Application application) {
        super(application);
    }

    public EditUserDescribeViewModel(@NonNull Application application, String userId) {
        super(application);
        this.userId = userId;
        this.friendTask = new FriendTask(application);
        requestFriendDescription();
    }

    /**
     * 获取朋友描述
     */
    public void requestFriendDescription() {
        friendDescription.setSource(friendTask.getFriendDescription(userId));
    }

    public LiveData<Resource<FriendDescription>> getFriendDescription() {
        return friendDescription;
    }

    /**
     * 设置朋友描述
     *
     * @param friendId
     * @param displayName
     */
    public void setFriendDescription(String friendId, String displayName) {
        setFriendDescriptionResult.setSource(friendTask.setFriendDescription(friendId, displayName));
    }

    public LiveData<Resource<Boolean>> setFriendDescriptionResult() {
        return setFriendDescriptionResult;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private Application application;
        private String userId;

        public Factory(Application application, String userId) {
            this.application = application;
            this.userId = userId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(Application.class, String.class).newInstance(application, userId);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }
}
