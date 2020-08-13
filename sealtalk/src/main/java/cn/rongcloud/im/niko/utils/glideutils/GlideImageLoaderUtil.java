package cn.rongcloud.im.niko.utils.glideutils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.rongcloud.im.niko.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class GlideImageLoaderUtil {



    //不带裁剪方式
    public static void loadImage(Context context, final ImageView iv, String imgUrl) {
        imgUrl = getScString(imgUrl);
        if (isContextExisted(context) && iv != null) {
            Glide.with(context).load(imgUrl)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.DATA))
                    .error(R.drawable.load_loading_image)
                    .into(iv);
        }
    }

    public static String getScString(String imgUrl) {
        if(!TextUtils.isEmpty(imgUrl)&&imgUrl.startsWith("_")) {
            imgUrl = "https://ttmedia.alilusions.com" + imgUrl.replace("_", "/");
        }
        return imgUrl;
    }
    public static void loadCircleImage(Context context, final ImageView iv, String imgUrl){
        imgUrl = getScString(imgUrl);
        if (isContextExisted(context) && iv != null) {
            Glide.with(context).load(imgUrl)
                    .apply(new RequestOptions().circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .error(R.drawable.load_loading_image))
                    .transition(withCrossFade())
                    .into(iv);
        }
    }

    //CenterCrop裁剪
    public static void loadCenterCrop(Context context, final ImageView iv, String imgUrl) {
        imgUrl = getScString(imgUrl);
        if (isContextExisted(context) && iv != null) {
            Glide.with(context).load(imgUrl)
                    .apply(new RequestOptions().centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .error(R.drawable.load_loading_image))
                    .transition(withCrossFade())
                    .into(iv);
        }
    }

    //可设置占位图
    public static void loadCenterCrop(Context context, ImageView iv, String imgUrl, int defaultImgResource) {
        imgUrl = getScString(imgUrl);
        loadCenterCrop(context, iv, imgUrl, defaultImgResource, R.drawable.load_loading_image);
    }


    public static void loadCenterCrop(Context context, ImageView iv, String imgUrl, int defaultImgResource, int errorImgResource) {
        imgUrl = getScString(imgUrl);
        if (isContextExisted(context) && iv != null) {
            Glide.with(context).load(imgUrl)
                    .apply(new RequestOptions().dontAnimate()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(defaultImgResource)
                            .error(errorImgResource))
                    .transition(withCrossFade())
                    .into(iv);
        }
    }


    /**
     * 加载矩形圆角
     */
    public static void loadRoundImg(final Context context, final ImageView iv, String imgUrl, int radius) {
        imgUrl = getScString(imgUrl);
        loadRoundImg(context, iv, imgUrl, radius, R.drawable.load_loading_image);
    }

    /**
     * 加载矩形圆角
     *
     * @param defaultImgResource 占位图
     */
    public static void loadRoundImg(final Context context, final ImageView iv, String imgUrl, int radius, int defaultImgResource) {
        imgUrl = getScString(imgUrl);
        loadRoundImg(context, iv, imgUrl, radius, defaultImgResource, RoundedCornersTransformation.CornerType.ALL);
    }


    /**
     * 加载矩形圆角
     *
     * @param radius     圆角角度
     * @param cornerType 指定圆角类型
     */
    public static void loadRoundImg(final Context context, final ImageView iv, String imgUrl, int radius, int defaultImgResource, RoundedCornersTransformation.CornerType cornerType) {
        imgUrl = getScString(imgUrl);
        if (isContextExisted(context) && iv != null) {
            if (TextUtils.isEmpty(imgUrl)) {
                imgUrl = "android.resource://com.amkj.dmsh/" + defaultImgResource;
            }
            Glide.with(context).load(imgUrl)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(defaultImgResource)
                            .error(defaultImgResource)
                            .transform(new CenterCrop(), new RoundedCornersTransformation(radius, 0, cornerType)))
                    .into(iv);
        }
    }

    /**
     * 图片加载监听
     *
     * @param loaderFinishListener 加载回调
     */
    public static void setLoadImgFinishListener(final Context context, String imgUrl, final ImageLoaderFinishListener loaderFinishListener) {
        imgUrl = getScString(imgUrl);
        if (isContextExisted(context)) {
            Glide.with(context).asBitmap().load(imgUrl)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.load_loading_image))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition transition) {
                            loaderFinishListener.onSuccess(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            loaderFinishListener.onError();
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            loaderFinishListener.onError();
                        }
                    });
        }
    }

    /**
     * gif加载监听
     *
     * @param loaderFinishListener 加载回调
     */
    public static void setLoadGifFinishListener(final Context context, String imgUrl, final GifLoaderFinishListener loaderFinishListener) {
        if (isContextExisted(context)) {
            Glide.with(context).asGif().load(imgUrl)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.load_loading_image)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(new CustomTarget<GifDrawable>() {
                        @Override
                        public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition transition) {
                            loaderFinishListener.onSuccess(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            loaderFinishListener.onError();
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            loaderFinishListener.onError();
                        }
                    });
        }
    }

    /**
     * 清除Glide图片缓存
     */
    public static void clearCache(Context context) {
        if (null != context) {
            Glide.get(context).clearMemory();//清除内存缓存
            Glide.get(context).clearDiskCache();//清除磁盘缓存
        }
    }

    public interface ImageLoaderFinishListener {
        void onSuccess(Bitmap bitmap);

        void onError();
    }

    public interface OriginalLoaderFinishListener {

        void onSuccess(File file);

        void onError();
    }

    public interface GifLoaderFinishListener {

        void onSuccess(GifDrawable drawable);

        void onError();
    }



    public static boolean isContextExisted(Context context) {
        return context != null && (context instanceof Activity && !((Activity) context).isFinishing()
                || context instanceof Service && isServiceExisted(context, context.getClass().getName())
                || context instanceof Application);
    }

    private static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;

            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
