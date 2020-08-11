package cn.rongcloud.im.niko.net;

import androidx.lifecycle.LiveData;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.rongcloud.im.niko.common.ApiErrorCodeMap;
import cn.rongcloud.im.niko.common.ErrorCode;
import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.utils.log.SLog;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

import static cn.rongcloud.im.niko.common.NetConstant.REQUEST_SUCCESS_CODE;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<R>> {
    private final Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<R> adapt(Call<R> call) {
        return new LiveData<R>() {
            AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                            R body = response.body();
                            String path = call.request().url().encodedPath();

                            if(response.code()==401){
                                //这个是apiservice表示token过期
                                SLog.e(LogTag.API,"token 过期");
                                postValue(body);
                                return;
                            }

                            // 当没有信息体时通过 http code 判断业务错误
                            if (body == null && !response.isSuccessful()) {
                                Result result = new Result();
                                int errorCode = ApiErrorCodeMap.getApiErrorCode(path, response.code());
                                result.setRsCode(errorCode);
                                try {
//                                    body = (R) result;
                                } catch (Exception e) {
                                    // 可能部分接口并不是由 result 包裹，此时无法获取错误码
                                }
                            } else if (body instanceof Result) {
                                Result result = (Result) body;
                                // 当请求失败时，转义API错误码到全局错误码
                                if (result.RsCode != REQUEST_SUCCESS_CODE) {
                                    int errorCode = ApiErrorCodeMap.getApiErrorCode(path, result.RsCode);
                                    result.setRsCode(errorCode);
                                }
                            }
                            postValue(body);
                        }

                        @Override
                        public void onFailure(Call<R> call, Throwable throwable) {
                            String url = call.request().url().toString();
                            SLog.d(LogTag.API, "onFailure:" + url + ", error:" + throwable.getMessage());
                            //加入 apiurl判断
                            if (url.contains(ScUrl.TOKEN_BASE_URL)) {
                                //说明是获取token类型 是没有外层包裹的
                                postValue(null);
                            } else if (url.contains(ScUrl.BASE_URL)) {
                                //都需要塞入list中
                                if (throwable instanceof ConnectException) {
                                    R body = null;
                                    Result result = new Result();
                                    result.setRsCode(ErrorCode.NETWORK_ERROR.getCode());
                                    List<Result> list = new ArrayList<>();
                                    list.add(result);
                                    try {
                                        body = (R) list;
                                    } catch (Exception e) {
                                        // 可能部分接口并不是由 result 包裹，此时无法获取错误码
                                    }
                                    postValue(body);
                                }
                            }else if (throwable instanceof ConnectException) {
                                R body = null;
                                //暂不处理
                                SLog.d(LogTag.API, "onFailure: 提示应为" + ErrorCode.NETWORK_ERROR.getCode());

//                                Result result = new Result();
//                                result.setRsCode(ErrorCode.NETWORK_ERROR.getCode());
//                                try {
//                                    body = (R) result;
//                                } catch (Exception e) {
//                                    // 可能部分接口并不是由 result 包裹，此时无法获取错误码
//                                }
                                postValue(body);
                            } else {
                                postValue(null);
                            }
                        }

                    });

                }
            }
        };
    }
}
