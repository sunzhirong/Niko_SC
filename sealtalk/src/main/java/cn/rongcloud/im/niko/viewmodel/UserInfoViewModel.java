package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;
import android.net.Uri;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.niko.CommentBean;
import cn.rongcloud.im.niko.model.niko.FollowBean;
import cn.rongcloud.im.niko.model.niko.FollowRequestInfo;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.model.niko.MyLikeBean;
import cn.rongcloud.im.niko.net.request.CommentAtReq;
import cn.rongcloud.im.niko.task.UserTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;
import cn.rongcloud.im.niko.utils.log.SLog;

public class UserInfoViewModel extends AndroidViewModel {

    private final UserTask userTask;
    private IMManager imManager;
    private SingleSourceLiveData<Resource<UserInfo>> userInfo = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Result>> setNameResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Result>> uploadPotraitResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Result>> changePasswordResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Result>> setStAccountResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Result>> setGenderResult = new SingleSourceLiveData<>();

    //niko
    private SingleSourceLiveData<Result<List<MyLikeBean>>> myLiekListResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<List<CommentBean>>> commentResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<Integer>> cmtAddResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<List<FriendBean>>> followerListResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<List<FollowRequestInfo>>> getFollowerRequestListResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<Boolean>> removeFollowingsResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<Boolean>> addFollowingsResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<List<FollowBean>>> followResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Void>> updateProfileResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<String>> uploadResult =  new SingleSourceLiveData<>();




    public UserInfoViewModel(@NonNull Application application) {
        super(application);
        imManager = IMManager.getInstance();
        userTask = new UserTask(application);
        requestUserInfo(imManager.getCurrentId());
    }

    public UserInfoViewModel(String userId, @NonNull Application application) {
        super(application);
        userTask = new UserTask(application);
        requestUserInfo(userId);
    }


    /**
     * 获取 UserInfo
     *
     * @return
     */
    public LiveData<Resource<UserInfo>> getUserInfo() {
        return userInfo;
    }

    /**
     * 设置name 结果
     *
     * @return
     */
    public LiveData<Resource<Result>> getSetNameResult() {
        return setNameResult;
    }

    /**
     * 设置 StAccount 结果
     *
     * @return
     */
    public LiveData<Resource<Result>> getSetStAccountResult() {
        return setStAccountResult;
    }

    /**
     * 设置性别结果
     *
     * @return
     */
    public LiveData<Resource<Result>> getSetGenderResult() {
        return setGenderResult;
    }

    /**
     * 上传头像结果
     *
     * @return
     */
    public LiveData<Resource<Result>> getUploadPortraitResult() {
        return uploadPotraitResult;
    }

    /**
     * 密码修改
     *
     * @return
     */
    public LiveData<Resource<Result>> getChangePasswordResult() {
        return changePasswordResult;
    }

    /**
     * 设置用户名
     *
     * @param newName
     */
    public void setName(String newName) {
        setNameResult.setSource(userTask.setMyNickName(newName));
    }

    /**
     * 设置自己的 SealTalk 账号
     *
     * @param stAccount
     */
    public void setStAccount(String stAccount) {
        setStAccountResult.setSource(userTask.setStAccount(stAccount));
    }

    /**
     * 设置性别
     *
     * @param gender
     */
    public void setGender(String gender) {
        setGenderResult.setSource(userTask.setGender(gender));
    }

    /**
     * 上传头像
     *
     * @param uri
     */
    public void uploadPortrait(Uri uri) {
        uploadPotraitResult.setSource(userTask.setPortrait(uri));
    }

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     */
    public void changePassword(String oldPassword, String newPassword) {
        changePasswordResult.setSource(userTask.changePassword(oldPassword, newPassword));
    }

    /**
     * 请求用户信息
     *
     * @param userId
     */
    private void requestUserInfo(String userId) {
        SLog.d("ss_usertask", "userId == " + userId);
        userInfo.setSource(userTask.getUserInfo(userId));
    }


    /**
     * 退出
     */
    public void logout() {
        imManager.logout();
        userTask.logout();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private String userId;
        private Application application;

        public Factory(String userId, Application application) {
            this.userId = userId;
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(String.class, Application.class).newInstance(userId, application);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }


    public void myLiekList(int skip,int take){
        myLiekListResult.setSource(userTask.myLiekList(skip,take));
    }

    public SingleSourceLiveData<Result<List<MyLikeBean>>> getMyLiekListResult() {
        return myLiekListResult;
    }

    public void getCommentList(int skip,int take){
        commentResult.setSource(userTask.getCommentList(skip,take));
    }

    public SingleSourceLiveData<Result<List<CommentBean>>> getCommentListResult() {
        return commentResult;
    }

    public void cmtAdd(CommentAtReq data){
        cmtAddResult.setSource(userTask.cmtAdd(data));
    }

    public SingleSourceLiveData<Result<Integer>> getCmtAddResult() {
        return cmtAddResult;
    }

    public void getFollowerList(int skip,int take){
        followerListResult.setSource(userTask.getFollowerList(skip,take));
    }

    public SingleSourceLiveData<Result<List<FriendBean>>> getFollowerListResult() {
        return followerListResult;
    }

    public void getFollowerRequestList(int skip,int take){
        getFollowerRequestListResult.setSource(userTask.getFollowerRequestList(skip,take));
    }

    public SingleSourceLiveData<Result<List<FollowRequestInfo>>> getFollowerRequestListResult() {
        return getFollowerRequestListResult;
    }

    public void removeFollowings(int uid){
        removeFollowingsResult.setSource(userTask.removeFollowings(uid));
    }

    public SingleSourceLiveData<Result<Boolean>> getRemoveFollowingsResult() {
        return removeFollowingsResult;
    }

    public void addFollowings(int uid){
        addFollowingsResult.setSource(userTask.addFollowings(uid));
    }

    public SingleSourceLiveData<Result<Boolean>> getAddFollowingsResult() {
        return addFollowingsResult;
    }

    public void getFollowList(int skip,int take){
        followResult.setSource(userTask.getFollowList(skip,take));
    }

    public SingleSourceLiveData<Result<List<FollowBean>>> getFollowListResult() {
        return followResult;
    }

    public SingleSourceLiveData<Resource<Void>> getUpdateProfile() {
        return updateProfileResult;
    }

    public void updateProfile(int type, String key, Object value) {
        updateProfileResult.setSource(userTask.updateProfile(type, key, value));
    }

    public void uploadAvatar(Uri uri){
        uploadResult.setSource(userTask.upload(uri));
    }

    public SingleSourceLiveData<Resource<String>> getUploadResult(){
        return uploadResult;
    }
}
