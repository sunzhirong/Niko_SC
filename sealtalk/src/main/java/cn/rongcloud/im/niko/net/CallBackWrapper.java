package cn.rongcloud.im.niko.net;

import cn.rongcloud.im.niko.common.ErrorCode;
import cn.rongcloud.im.niko.common.ResultCallback;
import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.utils.log.SLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallBackWrapper<R> implements Callback<Result<R>> {
    private ResultCallback<R> mCallBack;

    public CallBackWrapper(ResultCallback<R> callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onResponse(Call<Result<R>> call, Response<Result<R>> response) {
        Result<R> body = response.body();
        if (body != null) {
            int code = body.getRsCode();
            if (code == 200) {
                mCallBack.onSuccess(body.getRsData());
            } else {
                mCallBack.onFail(code);
            }
            SLog.e(LogTag.API, "url:" + call.request().url().toString()
                    + " ,code:" + body.getRsCode());
        } else {
            SLog.e(LogTag.API, "url:" + call.request().url().toString() + ", no response body");
            mCallBack.onFail(ErrorCode.API_ERR_OTHER.getCode());
        }
    }

    @Override
    public void onFailure(Call<Result<R>> call, Throwable t) {
        SLog.e(LogTag.API, call.request().url().toString() + " - " + (t != null ? t.getMessage() : ""));
        mCallBack.onFail(ErrorCode.NETWORK_ERROR.getCode());
    }
}
