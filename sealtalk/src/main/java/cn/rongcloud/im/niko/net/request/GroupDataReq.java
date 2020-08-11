package cn.rongcloud.im.niko.net.request;

import java.util.List;

public class GroupDataReq {

    /**
     * ChatGrpID : 0
     * Title : string
     * UIDs : [0]
     */

    private int ChatGrpID;
    private String Title;
    private List<Integer> UIDs;

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

    public List<Integer> getUIDs() {
        return UIDs;
    }

    public void setUIDs(List<Integer> UIDs) {
        this.UIDs = UIDs;
    }
}
