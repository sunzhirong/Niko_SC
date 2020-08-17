package cn.rongcloud.im.niko.net.request;

import java.util.List;

public class CommentAtReq {

    /**
     * TCmID : int 回复对象评论ID, 如何不是回复就为0
     * MmID : int 动态ID
     * Msg : int
     * AtUIDs : [{"UID":0,"Name":"int"}]
     */

    private int TCmID;
    private int MmID;
    private String Msg;
    private List<AtUIDsBean> AtUIDs;

    public int getTCmID() {
        return TCmID;
    }

    public void setTCmID(int TCmID) {
        this.TCmID = TCmID;
    }

    public int getMmID() {
        return MmID;
    }

    public void setMmID(int MmID) {
        this.MmID = MmID;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public List<AtUIDsBean> getAtUIDs() {
        return AtUIDs;
    }

    public void setAtUIDs(List<AtUIDsBean> AtUIDs) {
        this.AtUIDs = AtUIDs;
    }

    public static class AtUIDsBean {
        /**
         * UID : 0
         * Name : int
         */

        private int UID;
        private int Name;

        public int getUID() {
            return UID;
        }

        public void setUID(int UID) {
            this.UID = UID;
        }

        public int getName() {
            return Name;
        }

        public void setName(int Name) {
            this.Name = Name;
        }
    }
}
