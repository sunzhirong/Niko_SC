package cn.rongcloud.im.niko.sp;

import android.graphics.Color;


import com.google.gson.Gson;

import java.util.HashMap;

import cn.rongcloud.im.niko.model.niko.ProfileInfo;

public class ProfileUtils {
    public static ProfileInfo sProfileInfo;
    public static boolean hasSetPw;

    public static HashMap<String, Object> getUpdateInfo(int type, String key, Object value){
        HashMap<String, Object> map = new HashMap<>();
        map.put("Skip",0);
        map.put("Take",0);
        if(type==1) {
            map.put(key,value);
        }

        HashMap<String, Object> dataMap = new HashMap<>();

        dataMap.put("Bio",sProfileInfo.getBio());
        dataMap.put("Location",sProfileInfo.getLocation());
        dataMap.put("School",sProfileInfo.getSchool());
        dataMap.put("DOB",sProfileInfo.getDOB());

        if(type==2) {
            dataMap.put(key,value);
        }

        HashMap<String, Object> headMap = new HashMap<>();
        headMap.put("UID",sProfileInfo.getHead().getUID());
        headMap.put("Name",sProfileInfo.getHead().getName());
        headMap.put("NameColor",sProfileInfo.getHead().getNameColor());
        headMap.put("UserIcon",sProfileInfo.getHead().getUserIcon());
        headMap.put("Gender",sProfileInfo.getHead().isGender());

        if(type==3) {
            headMap.put(key,value);
        }

        dataMap.put("Head", new Gson().toJson(headMap));

        map.put("Data", new Gson().toJson(dataMap));
        return map;
    }

    public static int getNameColor(String color){
        try {
            return Color.parseColor("#"+color);

        }catch (Exception e){
            return Color.parseColor("#0A0A0B");
        }
    }

//    public static int getShowName(ProfileInfo info){
//        if(info.getHead().getName())
//    }
}
