package cn.rongcloud.im.niko.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.SealApp;
import cn.rongcloud.im.niko.common.ErrorCode;

/**
 * Toast 工具类
 */
public class ToastUtils {
    private static Toast lastToast;
    private static TextView mTextView;

    public static void showErrorToast(ErrorCode errorCode) {
        //根据错误码进行对应错误提示
        String message = errorCode.getMessage();
        if (TextUtils.isEmpty(message)) return;
        lastToast = makeToast(SealApp.getApplication(), message);
        lastToast.show();
    }
//    rc_bg_toast
    public static void showErrorToast(int errorCode) {
        showErrorToast(ErrorCode.fromCode(errorCode));
    }

    public static void showToast(int resourceId) {
        showToast(resourceId, Toast.LENGTH_SHORT);
    }

    public static void showToast(int resourceId, int duration) {
        showToast(SealApp.getApplication().getResources().getString(resourceId), duration);
    }

    public static void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    public static void showToast(String message, int duration) {
        if (TextUtils.isEmpty(message)) return;

        // 9.0 以上直接用调用即可防止重复的显示的问题，且如果复用 Toast 会出现无法再出弹出对话框问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            Toast toast = Toast.makeText(SealApp.getApplication(), message, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            lastToast = makeToast(SealApp.getApplication(), message);
            lastToast.show();
        }
    }



    private static Toast makeToast(Context context,String message) {
        if (lastToast == null) {
            lastToast = new Toast(context);
             //设置自定义Toast的位置
            View layout = View.inflate(context, R.layout.toast, null);
            mTextView = ((TextView) layout.findViewById(R.id.message));
            mTextView.setText(message);
            lastToast.setView(layout);
            //设置Toast的位置在屏幕中间
            lastToast.setGravity(Gravity.CENTER, 0, 0);
            lastToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            mTextView.setText(message);
        }
        return lastToast;


    }
}
