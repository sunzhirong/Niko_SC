package cn.rongcloud.im.niko.net.upload;

import android.content.Context;
import android.text.TextUtils;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.net.LiveDataCallAdapterFactory;
import cn.rongcloud.im.niko.net.ScInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadRetrofitClient {
    private Context mContext;
    private Retrofit mRetrofit;

    public UploadRetrofitClient(Context context, String baseUrl) {
        mContext = context;


        /*
         * 当 baseUrl 没有以 "/" 结尾时加入 "/"
         * 防止当 baseUrl 为非纯域名的，如：域名+ path 时，如果不以 "/" 结尾，Retrofit 会抛出异常
         */
        if (!TextUtils.isEmpty(baseUrl)
                && baseUrl.lastIndexOf("/") != baseUrl.length() - 1) {
            baseUrl = baseUrl + "/";
        }
        mRetrofit = new Retrofit.Builder()
                .client(getUnsafeOkHttpClient())
                .baseUrl(baseUrl) //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .addCallAdapterFactory(new LiveDataCallAdapterFactory()) //设置请求响应适配 LiveData
                .build();
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .addInterceptor(new ScInterceptor())
                    .connectTimeout(NetConstant.API_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(NetConstant.API_READ_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(NetConstant.API_WRITE_TIME_OUT, TimeUnit.SECONDS);
            okHttpBuilder.sslSocketFactory(sslSocketFactory);
            okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    public <T> T createService(Class<T> service) {
        return mRetrofit.create(service);
    }
}
