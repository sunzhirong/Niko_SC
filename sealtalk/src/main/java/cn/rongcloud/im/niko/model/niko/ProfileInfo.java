package cn.rongcloud.im.niko.model.niko;



public class ProfileInfo {
    /**
     * Head : {"UID":0,"Name":"string","NameColor":"string","UserIcon":"string","Gender":true}
     * Bio : string
     * Location : string
     * School : string
     * Followers : 0
     * Followings : 0
     * Likes : 0
     * Moments : 0
     * FavorTopicIDs : [0]
     */



    private int id ;
    private ProfileHeadInfo Head;

    private String Bio;//个人简介
    private String Location;
    private String School;
    private int Followers;
    private int Followings;
    private int Likes;
    private int Moments;
    private long DOB;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDOB() {
        return DOB;
    }

    public void setDOB(long DOB) {
        this.DOB = DOB;
    }

    public ProfileHeadInfo getHead() {
        return Head;
    }

    public void setHead(ProfileHeadInfo Head) {
        this.Head = Head;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String Bio) {
        this.Bio = Bio;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String School) {
        this.School = School;
    }

    public int getFollowers() {
        return Followers;
    }

    public void setFollowers(int Followers) {
        this.Followers = Followers;
    }

    public int getFollowings() {
        return Followings;
    }

    public void setFollowings(int Followings) {
        this.Followings = Followings;
    }

    public int getLikes() {
        return Likes;
    }

    public void setLikes(int Likes) {
        this.Likes = Likes;
    }

    public int getMoments() {
        return Moments;
    }

    public void setMoments(int Moments) {
        this.Moments = Moments;
    }

//    public List<Integer> getFavorTopicIDs() {
//        return FavorTopicIDs;
//    }
//
//    public void setFavorTopicIDs(List<Integer> FavorTopicIDs) {
//        this.FavorTopicIDs = FavorTopicIDs;
//    }


}
