package cn.rongcloud.im.niko.model.niko;

public class MyLikeBean {

    /**
     * ID : 0
     * MmID : 0
     * UserHead : {"UID":0,"Name":"string","NameColor":"string","UserIcon":"string","Gender":true}
     * Msg : string
     * Utc : 2020-08-03T11:01:39.721Z
     * Title : string
     * MdGuid : string
     */

    private int ID;
    private int MmID;
    private ProfileHeadInfo UserHead;
    private String Msg;
    private String Utc;
    private String Title;
    private String MdGuid;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMmID() {
        return MmID;
    }

    public void setMmID(int MmID) {
        this.MmID = MmID;
    }

    public ProfileHeadInfo getUserHead() {
        return UserHead;
    }

    public void setUserHead(ProfileHeadInfo UserHead) {
        this.UserHead = UserHead;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public String getUtc() {
        return Utc;
    }

    public void setUtc(String Utc) {
        this.Utc = Utc;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getMdGuid() {
        return MdGuid;
    }

    public void setMdGuid(String MdGuid) {
        this.MdGuid = MdGuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyLikeBean that = (MyLikeBean) o;
        return ID == that.ID &&
                MmID == that.MmID;
    }

}
