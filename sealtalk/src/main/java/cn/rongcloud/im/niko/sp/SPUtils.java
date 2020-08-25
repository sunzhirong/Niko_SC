package cn.rongcloud.im.niko.sp;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    private static final String NAME = "seal";

    private static final String LOGIN="login";
    private static final String USER_TOKEN="user_token";
    private static final String IM_TOKEN="im_token";
    private static final String IM_USER_ID="im_user_id";
    private static final String HAS_SET_PASSWORD="has_set_password";
    private static final String FRIENDS_REQUEST_COUNT="friends_request_count";
    private static final String UNREAD_MSG_COUNT="unread_msg_count";



    private static SharedPreferences getPreference(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp;
    }


    public static void setLogin(Context context, boolean value) {
        SharedPreferences sp = getPreference(context);
        sp.edit().putBoolean(LOGIN, value).commit();
    }


    public static boolean getLogin(Context context) {
        SharedPreferences sp = getPreference(context);
        return sp.getBoolean(LOGIN, false);
    }



    public static void setUnreadMsgCount(Context context, int value) {
        SharedPreferences sp = getPreference(context);
        sp.edit().putInt(UNREAD_MSG_COUNT, value).commit();
    }


    public static int getUnreadMsgCount(Context context) {
        SharedPreferences sp = getPreference(context);
        return sp.getInt(UNREAD_MSG_COUNT, 0);
    }
    public static void setFriendsRequestCount(Context context, int value) {
        SharedPreferences sp = getPreference(context);
        sp.edit().putInt(FRIENDS_REQUEST_COUNT, value).commit();
    }


    public static int getFriendsRequestCount(Context context) {
        SharedPreferences sp = getPreference(context);
        return sp.getInt(FRIENDS_REQUEST_COUNT, 0);
    }
    public static void setUserToken(Context context, String value) {
        SharedPreferences sp = getPreference(context);
        sp.edit().putString(USER_TOKEN, value).commit();
    }


    public static String getUserToken(Context context) {
        SharedPreferences sp = getPreference(context);
        return sp.getString(USER_TOKEN, "");
    }

    public static void setIMToken(Context context, String value) {
        SharedPreferences sp = getPreference(context);
        sp.edit().putString(IM_TOKEN, value).commit();
    }


    public static String getIMToken(Context context) {
        SharedPreferences sp = getPreference(context);
        return sp.getString(IM_TOKEN, "");
    }

    public static void setIMUserId(Context context, String value) {
        SharedPreferences sp = getPreference(context);
        sp.edit().putString(IM_USER_ID, value).commit();
    }


    public static String getIMIMUserId(Context context) {
        SharedPreferences sp = getPreference(context);
        return sp.getString(IM_USER_ID, "");
    }


    public static void setHasPassword(Context context, boolean value) {
        SharedPreferences sp = getPreference(context);
        sp.edit().putBoolean(HAS_SET_PASSWORD, value).commit();
    }


    public static boolean getHasPassword(Context context) {
        SharedPreferences sp = getPreference(context);
        return sp.getBoolean(HAS_SET_PASSWORD, false);
    }


}
