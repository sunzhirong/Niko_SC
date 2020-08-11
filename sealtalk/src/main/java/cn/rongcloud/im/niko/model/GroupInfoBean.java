package cn.rongcloud.im.niko.model;

import java.util.List;

import cn.rongcloud.im.niko.model.niko.ProfileHeadInfo;

public class GroupInfoBean {

    /**
     * ChatGrpID : 0
     * Title : string
     * Note : string
     * UserHeads : [{"UID":0,"Name":"string","NameColor":"string","UserIcon":"string","Gender":true}]
     * CreatorUID : 0
     * IsEnd : true
     */

    private int ChatGrpID;
    private String Title;
    private String Note;
    private int CreatorUID;
    private boolean IsEnd;
    private List<ProfileHeadInfo> UserHeads;

    public int getChatGrpID() {
        return ChatGrpID;
    }

    public void setChatGrpID(int ChatGrpID) {
        this.ChatGrpID = ChatGrpID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }

    public int getCreatorUID() {
        return CreatorUID;
    }

    public void setCreatorUID(int CreatorUID) {
        this.CreatorUID = CreatorUID;
    }

    public boolean isIsEnd() {
        return IsEnd;
    }

    public void setIsEnd(boolean IsEnd) {
        this.IsEnd = IsEnd;
    }

    public List<ProfileHeadInfo> getUserHeads() {
        return UserHeads;
    }

    public void setUserHeads(List<ProfileHeadInfo> UserHeads) {
        this.UserHeads = UserHeads;
    }
}
