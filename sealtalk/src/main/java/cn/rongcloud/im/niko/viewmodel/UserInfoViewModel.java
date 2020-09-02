package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;
import android.net.Uri;

import java.io.File;
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
import cn.rongcloud.im.niko.ui.adapter.models.VIPCheckBean;
import cn.rongcloud.im.niko.ui.adapter.models.VIPConfigBean;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;
import cn.rongcloud.im.niko.utils.log.SLog;

public class UserInfoViewModel extends AndroidViewModel {

    private final UserTask userTask;
    private IMManager imManager;
    private SingleSourceLiveData<Resource<UserInfo>> userInfo = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Result>> changePasswordResult = new SingleSourceLiveData<>();

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
    private SingleSourceLiveData<Result<VIPCheckBean>> vipCheckResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<List<VIPConfigBean>>> vipConfigResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result<Boolean>> hasSetPasswordResult =  new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Boolean>> changePwResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Boolean>> setPwResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<Void>> logoutResult = new SingleSourceLiveData<>();



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
     * 请求用户信息
     *
     * @param userId
     */
    private void requestUserInfo(String userId) {
        SLog.d("ss_usertask", "userId == " + userId);
        if(userId .equals( imManager.getCurrentId())){
            userInfo.setSource(userTask.getCurrentUserInfo(userId));
        }else {
            userInfo.setSource(userTask.getUserInfo(userId));
        }
    }


    /**
     * 退出
     */
    public void logout() {
        logoutResult.setSource(userTask.logout());
    }

    public LiveData<Resource<Void>> getLogoutResult() {
        return logoutResult;
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

    public void uploadAvatar(File uri){
//        uploadResult.setSource(userTask.upload(uri));
        uploadResult.setSource(userTask.upload(uri));
    }

    public SingleSourceLiveData<Resource<String>> getUploadResult(){
        return uploadResult;
    }

    public SingleSourceLiveData<Result<VIPCheckBean>> getVipCheckResult() {
        return vipCheckResult;
    }

    public void vipCheck(){
        vipCheckResult.setSource(userTask.checkVip());
    }



    public SingleSourceLiveData<Result<List<VIPConfigBean>>> getVipConfigResult() {
        return vipConfigResult;
    }

    public void vipConfigInfo(){
        vipConfigResult.setSource(userTask.vipInfo());
    }


    public SingleSourceLiveData<Result<Boolean>> getHasSetPasswordResult() {
        return hasSetPasswordResult;
    }

    public void hasSetPassword() {
        hasSetPasswordResult.setSource(userTask.hasSetPassword());
    }

    public void changePw(String oldPw,String newPw){
        changePwResult.setSource(userTask.changePw(oldPw,newPw));
    }

    public SingleSourceLiveData<Resource<Boolean>> getChangePwResult() {
        return changePwResult;
    }

    public void setPw(String newPw){
        setPwResult.setSource(userTask.setPw(newPw));
    }

    public SingleSourceLiveData<Resource<Boolean>> getSetPwResult() {
        return setPwResult;
    }
}
