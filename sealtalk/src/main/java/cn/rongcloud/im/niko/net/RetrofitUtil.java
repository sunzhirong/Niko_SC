package cn.rongcloud.im.niko.net;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RetrofitUtil {
    private final static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");

    /**
     * 通过参数 Map 合集
     * @param paramsMap
     * @return
     */
    public static RequestBody createJsonRequest(HashMap<String,Object> paramsMap){
        Gson gson = new Gson();
        String strEntity = gson.toJson(paramsMap);
        return RequestBody.create(MEDIA_TYPE_JSON,strEntity);
    }

    /**
     * 转换为 form-data
     *
     * @param requestDataMap
     * @return
     */
    public static Map<String, RequestBody> generateRequestBody(Map<String, String> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                    requestDataMap.get(key) == null ? "" : requestDataMap.get(key));
            requestBodyMap.put(key, requestBody);
        }
        Log.e("retrofit RequestBody", new Gson().toJson(requestDataMap));

        return requestBodyMap;
    }
}
