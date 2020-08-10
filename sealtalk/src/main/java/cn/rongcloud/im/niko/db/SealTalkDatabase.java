package cn.rongcloud.im.niko.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import cn.rongcloud.im.niko.db.dao.FriendDao;
import cn.rongcloud.im.niko.db.dao.GroupDao;
import cn.rongcloud.im.niko.db.dao.GroupMemberDao;
import cn.rongcloud.im.niko.db.dao.UserDao;
import cn.rongcloud.im.niko.db.model.BlackListEntity;
import cn.rongcloud.im.niko.db.model.FriendDescription;
import cn.rongcloud.im.niko.db.model.FriendInfo;
import cn.rongcloud.im.niko.db.model.GroupEntity;
import cn.rongcloud.im.niko.db.model.GroupExitedMemberInfo;
import cn.rongcloud.im.niko.db.model.GroupMemberInfoDes;
import cn.rongcloud.im.niko.db.model.GroupMemberInfoEntity;
import cn.rongcloud.im.niko.db.model.GroupNoticeInfo;
import cn.rongcloud.im.niko.db.model.PhoneContactInfoEntity;
import cn.rongcloud.im.niko.db.model.UserInfo;

@Database(entities = {UserInfo.class, FriendInfo.class, GroupEntity.class, GroupMemberInfoEntity.class,
        BlackListEntity.class, GroupNoticeInfo.class, GroupExitedMemberInfo.class, FriendDescription.class,
        GroupMemberInfoDes.class,PhoneContactInfoEntity.class}, version = 3, exportSchema = false)
@TypeConverters(cn.rongcloud.im.niko.db.TypeConverters.class)
public abstract class SealTalkDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();

    public abstract FriendDao getFriendDao();

    public abstract GroupDao getGroupDao();

    public abstract GroupMemberDao getGroupMemberDao();
}
