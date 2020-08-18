package cn.rongcloud.im.niko.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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



    /**
     * 获取本地文件真实 uri
     *
     * @param contentUri
     * @return
     */
    public static String getRealPathFromUri(Context context,Uri contentUri) {
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

    public static String getImageBase64(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        int options = 100;
        while (bos.toByteArray().length / 1024 > 200){
            bos.reset();
            options -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG,options,bos);
        }
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
