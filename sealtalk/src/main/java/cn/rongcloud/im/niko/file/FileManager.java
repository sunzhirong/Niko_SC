package cn.rongcloud.im.niko.file;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.rongcloud.im.niko.SealApp;
import cn.rongcloud.im.niko.common.ErrorCode;
import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.common.ThreadManager;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.model.UploadTokenResult;
import cn.rongcloud.im.niko.net.HttpClientManager;
import cn.rongcloud.im.niko.net.RetrofitClient;
import cn.rongcloud.im.niko.net.service.UserService;
import cn.rongcloud.im.niko.utils.FileUtils;
import cn.rongcloud.im.niko.utils.NetworkOnlyResource;
import cn.rongcloud.im.niko.utils.log.SLog;
import io.rong.message.utils.BitmapUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileManager {
    private Context context;
    private UserService userService;

    public FileManager(Context context) {
        this.context = context.getApplicationContext();
        RetrofitClient client = HttpClientManager.getInstance(context).getClient();
        userService = client.createService(UserService.class);
    }

    /**
     * 保存图片至公共下载下载中
     *
     * @param bitmap
     * @return
     */
    public LiveData<Resource<String>> saveBitmapToPictures(Bitmap bitmap, String fileName) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        ThreadManager.getInstance().runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                String path = FileUtils.saveBitmapToPublicPictures(bitmap, fileName);
                result.postValue(Resource.success(path));
            }
        });
        return result;
    }

    /**
     * 保存图片至缓存文件中
     *
     * @param bitmap
     * @return
     */
    public LiveData<Resource<String>> saveBitmapToCache(Bitmap bitmap, String fileName) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        ThreadManager.getInstance().runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                String path = FileUtils.saveBitmapToCache(bitmap, fileName);
                result.postValue(Resource.success(path));
            }
        });
        return result;
    }





    /**
     * 获取本地文件真实 uri
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
