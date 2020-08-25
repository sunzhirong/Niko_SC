package cn.rongcloud.im.niko.viewmodel;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import cn.rongcloud.im.niko.common.ErrorCode;
import cn.rongcloud.im.niko.model.RegisterResult;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.model.UserCacheInfo;
import cn.rongcloud.im.niko.model.niko.TokenBean;
import cn.rongcloud.im.niko.task.UserTask;
import cn.rongcloud.im.niko.utils.SingleSourceLiveData;
import cn.rongcloud.im.niko.utils.SingleSourceMapLiveData;

public class LoginViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<String>> loginResult = new SingleSourceLiveData<>();

    private MediatorLiveData<Resource> loadingState = new MediatorLiveData<>();

    private MutableLiveData<UserCacheInfo> lastLoginUserCache = new MutableLiveData<>();

    private SingleSourceLiveData<TokenBean> getTokenResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result> getSmsResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Result> verifyResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<TokenBean> getUserTokenResult = new SingleSourceLiveData<>();


    private UserTask userTask;

    public LoginViewModel(@NonNull Application application) {
        super(application);

        userTask = new UserTask(application);
        loadingState.addSource(loginResult, resource -> loadingState.setValue(resource));

        UserCacheInfo userCache = userTask.getUserCache();
        if (userCache != null) {
            lastLoginUserCache.setValue(userCache);
        }

    }

    public void login(String region, String phone, String pwd){
        loginResult.setSource(userTask.login(region, phone, pwd));
        //TODO 示例代码，当需要转换类型时参考
        //loginResultNoResource.setSource(userTask.login(region, phone, pwd));
    }

    public LiveData<Resource<String>> getLoginResult(){
        return loginResult;
    }


    /**
     * 最后一次的用户信息
     * @return
     */
    public LiveData<UserCacheInfo> getLastLoginUserCache() {
        return lastLoginUserCache;
    }



    @Override
    protected void onCleared() {
        super.onCleared();

    }


    public void getToken() {
        getTokenResult.setSource(userTask.getAccessToken());
    }

    public SingleSourceLiveData<TokenBean> getGetTokenResult() {
        return getTokenResult;
    }

    public SingleSourceLiveData<Result> getGetSmsResult() {
        return getSmsResult;
    }

    public void getSms(String phone) {
        getSmsResult.setSource(userTask.getSms(phone));
    }

    public void verifySms(String phone) {
        verifyResult.setSource(userTask.smsVerify(phone));
    }

    public SingleSourceLiveData<Result> getVerifyResult() {
        return verifyResult;
    }

    public void getUserToken(String phone,String pwd) {
        getUserTokenResult.setSource(userTask.getUserToken(phone,pwd));
    }

    public SingleSourceLiveData<TokenBean> getGetUserTokenResult() {
        return getUserTokenResult;
    }

}
