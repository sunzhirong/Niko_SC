package cn.rongcloud.im.niko.task;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.HashMap;

import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.model.PrivacyResult;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.ScreenCaptureResult;
import cn.rongcloud.im.niko.net.HttpClientManager;
import cn.rongcloud.im.niko.net.RetrofitUtil;
import cn.rongcloud.im.niko.net.service.PrivacyService;
import cn.rongcloud.im.niko.sp.UserConfigCache;
import cn.rongcloud.im.niko.utils.NetworkOnlyResource;

public class PrivacyTask {

    private DbManager dbManager;
    private PrivacyService privacyService;
    private Context context;
    private UserConfigCache userConfigCache;

    public PrivacyTask(Context context) {
        this.context = context.getApplicationContext();
        userConfigCache = new UserConfigCache(context);
        dbManager = DbManager.getInstance(context);
        privacyService = HttpClientManager.getInstance(context).getClient().createService(PrivacyService.class);
    }

    /**
     * 用户隐私设置（可同时设置多项，传-1为不设置，0允许，1不允许）
     *
     * @param phoneVerify    是否可以通过电话号码查找
     * @param stSearchVerify 是否可以通过 SealTalk 号查找
     * @param friVerify      加好友验证
     * @param groupVerify    允许直接添加至群聊
     * @return
     */
    public LiveData<Resource<Void>> setPrivacy(int phoneVerify, int stSearchVerify,
                                               int friVerify, int groupVerify) {
        return new NetworkOnlyResource<Void, Result>() {

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> paramMap = new HashMap<>();
                if (phoneVerify != -1) {
                    paramMap.put("phoneVerify", phoneVerify);
                }
                if (stSearchVerify != -1) {
                    paramMap.put("stSearchVerify", stSearchVerify);
                }
                if (friVerify != -1) {
                    paramMap.put("friVerify", friVerify);
                }
                if (groupVerify != -1) {
                    paramMap.put("groupVerify", groupVerify);
                }
                return privacyService.setPrivacy(RetrofitUtil.createJsonRequest(paramMap));
            }
        }.asLiveData();
    }

    /**
     * 获取个人隐私设置
     *
     * @return
     */
    public LiveData<Resource<PrivacyResult>> getPrivacyState() {
        return new NetworkOnlyResource<PrivacyResult, Result<PrivacyResult>>() {

            @NonNull
            @Override
            protected LiveData<Result<PrivacyResult>> createCall() {
                return privacyService.getPrivacy();
            }
        }.asLiveData();
    }


}
