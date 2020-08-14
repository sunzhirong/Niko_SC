package cn.rongcloud.im.niko.net;

import android.util.Log;

import java.io.IOException;

import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.utils.log.SLog;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ScInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request requestOld = chain.request();

        Request request = requestOld.newBuilder()
//                .header("Content-Type", "application/json")
                .header("Authorization", getAuthorization())
                .header("DV", getDV())
                .header("LG", getLG())
                .header("VI", getVI())
                .method(requestOld.method(), requestOld.body())
                .build();

        Log.e("retrofitResponse", String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers(), request.headers("Cookie")));
        Response response = chain.proceed(request);
        ResponseBody body = response.peekBody(1024 * 1024);
        String responseString = body.string();
        Log.e("retrofitResponse", request.url() + "---------" + responseString);

        return response;
    }


    private static String getAuthorization() {
        return NetConstant.Authorization;
    }

    public static String getDV() {
//        return "niko";
        return "20200210n1111";
    }

    private static String getLG() {
        return "CN";
    }

    private static String getVI() {
        return "A101";
    }
}
