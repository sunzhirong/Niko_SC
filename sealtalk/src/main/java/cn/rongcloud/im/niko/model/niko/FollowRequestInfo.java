package cn.rongcloud.im.niko.model.niko;

import java.io.Serializable;

public class FollowRequestInfo implements Serializable {

    /**
     * ReqMsg : string
     * ReqUtc : 2020-07-28T15:18:38.996Z
     * IsFriend : true
     * UID : 0
     * Name : string
     * NameColor : string
     * UserIcon : string
     * Gender : true
     */

    private String ReqMsg;
    private String ReqUtc;
    private boolean IsFriend;
    private int UID;
    private String Name;
    private String NameColor;
    private String UserIcon;
    private boolean Gender;

    public String getReqMsg() {
        return ReqMsg;
    }

    public void setReqMsg(String ReqMsg) {
        this.ReqMsg = ReqMsg;
    }

    public String getReqUtc() {
        return ReqUtc;
    }

    public void setReqUtc(String ReqUtc) {
        this.ReqUtc = ReqUtc;
    }

    public boolean isIsFriend() {
        return IsFriend;
    }

    public void setIsFriend(boolean IsFriend) {
        this.IsFriend = IsFriend;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getNameColor() {
        return NameColor;
    }

    public void setNameColor(String NameColor) {
        this.NameColor = NameColor;
    }

    public String getUserIcon() {
        return UserIcon;
    }

    public void setUserIcon(String UserIcon) {
        this.UserIcon = UserIcon;
    }

    public boolean isGender() {
        return Gender;
    }

    public void setGender(boolean Gender) {
        this.Gender = Gender;
    }
}
