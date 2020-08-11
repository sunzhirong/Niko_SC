package cn.rongcloud.im.niko.net.service;

import androidx.lifecycle.LiveData;

import cn.rongcloud.im.niko.model.PrivacyResult;
import cn.rongcloud.im.niko.model.Result;

import cn.rongcloud.im.niko.model.ScreenCaptureResult;
import cn.rongcloud.im.niko.net.SealTalkUrl;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PrivacyService {
    @POST(SealTalkUrl.SET_PRIVACY)
    LiveData<Result> setPrivacy(@Body RequestBody body);

    @GET(SealTalkUrl.GET_PRIVACY)
    LiveData<Result<PrivacyResult>> getPrivacy();

}
