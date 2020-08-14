package cn.rongcloud.im.niko.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.rongcloud.im.niko.SealApp;
import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.utils.log.SLog;

public class FileUtils {
    public static String saveBitmapToFile(Bitmap bitmap, File toFile) {
        try {
            FileOutputStream fos = new FileOutputStream(toFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        SLog.e(LogTag.FILE, "save image to path:" + toFile.getPath());

        return toFile.getPath();
    }

    public static String saveBitmapToPublicPictures(Bitmap bitmap, String fileName) {
        File saveFileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!saveFileDirectory.exists()) {
            saveFileDirectory.mkdirs();
        }

        File saveFile = new File(saveFileDirectory, fileName);
        return saveBitmapToFile(bitmap, saveFile);
    }

    public static String saveBitmapToCache(Bitmap bitmap, String fileName) {
        File saveFileDirectory = SealApp.getApplication().getExternalCacheDir();
        if(saveFileDirectory == null){
            saveFileDirectory = SealApp.getApplication().getCacheDir();
        }
        if (!saveFileDirectory.exists()) {
            saveFileDirectory.mkdirs();
        }

        File saveFile = new File(saveFileDirectory, fileName);
        return saveBitmapToFile(bitmap, saveFile);
    }

    /**
     * 从asset路径下读取对应文件转String输出
     * @param mContext
     * @return
     */
    public static String getJson(Context mContext, String fileName) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }

}
