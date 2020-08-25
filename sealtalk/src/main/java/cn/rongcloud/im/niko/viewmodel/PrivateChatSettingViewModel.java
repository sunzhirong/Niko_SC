package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import cn.rongcloud.im.niko.db.model.FriendDetailInfo;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.ScreenCaptureResult;
import cn.rongcloud.im.niko.task.FriendTask;
import cn.rongcloud.im.niko.task.PrivacyTask;
import cn.rongcloud.im.niko.task.UserTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;
import io.rong.imlib.model.Conversation;

/**
 * 私聊详情视图模型
 */
public class PrivateChatSettingViewModel extends AndroidViewModel {
    private final String TAG = "PrivateChatSettingViewModel";
    private UserTask userTask;
//    private MediatorLiveData<Resource<FriendShipInfo>> friendShipInfoLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<UserInfo>> friendShipInfoLiveData = new MediatorLiveData<>();

    private String targetId;
    private Conversation.ConversationType conversationType;
    private SingleSourceLiveData<Resource<Boolean>> isNotifyLiveData = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Boolean>> isTopLiveData = new SingleSourceLiveData<>();
    private FriendTask friendTask;
    private IMManager imManager;

    public PrivateChatSettingViewModel(@NonNull Application application) {
        super(application);
        friendTask = new FriendTask(application);
        imManager = IMManager.getInstance();
    }

    public PrivateChatSettingViewModel(@NonNull Application application, String targetId, Conversation.ConversationType conversationType) {
        super(application);

        userTask = new UserTask(application);
        friendTask = new FriendTask(application);
        imManager = IMManager.getInstance();
        this.targetId = targetId;
        this.conversationType = conversationType;

        isNotifyLiveData.setSource(imManager.getConversationNotificationStatus(conversationType, targetId));
        isTopLiveData.setSource(imManager.getConversationIsOnTop(conversationType, targetId));
    }


    /**
     * 请求好友信息.
     */
    public void requestFriendInfo() {
        // 支持加入信息是自己的话，支持查看自己， 则需要查询自己的信息
        friendShipInfoLiveData.addSource(friendTask.getUserInfo(targetId), new Observer<Resource<UserInfo>>() {
            @Override
            public void onChanged(Resource<UserInfo> friendShipInfoResource) {
                friendShipInfoLiveData.postValue(friendShipInfoResource);
            }
        });
    }

    /**
     * 获取好友信息
     *
     * @return
     */
    public LiveData<Resource<UserInfo>> getFriendInfo() {
        return friendShipInfoLiveData;
    }


    /**
     * 设置会话置顶
     *
     * @param isTop
     */
    public void setConversationOnTop(boolean isTop) {
        Resource<Boolean> value = isTopLiveData.getValue();
        if (value != null && value.data != null && value.data == isTop) return;

        isTopLiveData.setSource(imManager.setConversationToTop(conversationType, targetId, isTop));


        if(isTop) {
            friendTask.topChatYes(targetId);
        }else {
            friendTask.topChatNo(targetId);
        }
    }


    /**
     * 获取会话是否置顶
     *
     * @return
     */
    public MutableLiveData<Resource<Boolean>> getIsTop() {
        return isTopLiveData;
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
