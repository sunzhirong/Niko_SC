package cn.rongcloud.im.niko.model.niko;

public class CommentBean {

    /**
     * MmID : 0
     * MmWriter : 0
     * UserHead : {"UID":0,"Name":"string","NameColor":"string","UserIcon":"string","Gender":true}
     * CmID : 0
     * Msg : string
     * Utc : 2020-07-27T14:04:58.540Z
     * UID : 0
     * TUID : 0
     * GCmID : 0
     * AtUID : 0
     */

    private int MmID;
    private int MmWriter;
    private ProfileHeadInfo UserHead;
    private int CmID;
    private String Msg;
    private long Utc;
    private String MdGuid;
    private int UID;
    private int TUID;
    private int GCmID;
    private int AtUID;

    public String getMdGuid() {
        return MdGuid;
    }

    public void setMdGuid(String mdGuid) {
        MdGuid = mdGuid;
    }

    public int getMmID() {
        return MmID;
    }

    public void setMmID(int MmID) {
        this.MmID = MmID;
    }

    public int getMmWriter() {
        return MmWriter;
    }

    public void setMmWriter(int MmWriter) {
        this.MmWriter = MmWriter;
    }

    public ProfileHeadInfo getUserHead() {
        return UserHead;
    }

    public void setUserHead(ProfileHeadInfo UserHead) {
        this.UserHead = UserHead;
    }

    public int getCmID() {
        return CmID;
    }

    public void setCmID(int CmID) {
        this.CmID = CmID;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public long getUtc() {
        return Utc;
    }

    public void setUtc(long Utc) {
        this.Utc = Utc;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public int getTUID() {
        return TUID;
    }

    public void setTUID(int TUID) {
        this.TUID = TUID;
    }

    public int getGCmID() {
        return GCmID;
    }

    public void setGCmID(int GCmID) {
        this.GCmID = GCmID;
    }

    public int getAtUID() {
        return AtUID;
    }

    public void setAtUID(int AtUID) {
        this.AtUID = AtUID;
    }


}
