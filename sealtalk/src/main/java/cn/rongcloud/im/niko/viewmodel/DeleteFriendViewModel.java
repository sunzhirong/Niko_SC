package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.task.FriendTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;

/**
 * 删除好友视图模型
 */
public class DeleteFriendViewModel extends AndroidViewModel {
    private FriendTask friendTask;
    private SingleSourceLiveData<Resource<Object>> deleteFriendsResult = new SingleSourceLiveData<>();

    public DeleteFriendViewModel(@NonNull Application application) {
        super(application);

        friendTask = new FriendTask(application);
    }

    /**
     * 批量删除好友
     *
     * @param friendIdList
     */
    public void deleteFriends(List<String> friendIdList) {
        deleteFriendsResult.setSource(friendTask.deleteFriends(friendIdList));
    }

    /**
     * 获取删除好友结果
     *
     * @return
     */
    public SingleSourceLiveData<Resource<Object>> getDeleteFriendsResult() {
        return deleteFriendsResult;
    }
}
