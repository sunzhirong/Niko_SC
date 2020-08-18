package cn.rongcloud.im.niko.net.service;

import java.util.List;

import androidx.lifecycle.LiveData;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.net.ScUrl;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {
    @Multipart
    @POST(ScUrl.UPLOAD_AVATAR)
    LiveData<Result<String>> uploadAvatar(@Part List<MultipartBody.Part> partList);
}
