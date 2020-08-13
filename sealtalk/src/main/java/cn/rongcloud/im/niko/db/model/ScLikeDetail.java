package cn.rongcloud.im.niko.db.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName = "sc_like_detail")
public class ScLikeDetail {
    //    CREATE TABLE t_message_like_detail (
//            id                  INTEGER   PRIMARY KEY AUTOINCREMENT,
//            message_uuid        TEXT      NOT NULL,
//            target_message_uuid TEXT      NOT NULL,
//            user_id             TEXT      NOT NULL,
//            group_id            TEXT      NOT NULL,
//            sender_user_id      TEXT      NOT NULL,
//            sender_avatar       TEXT,
//            description         TEXT,
//            created_time        TIMESTAMP NOT NULL
//                    DEFAULT (datetime('now', 'localtime') )
//            );

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "message_uuid")
    private String messageUuid;
    @ColumnInfo(name = "target_message_uuid")
    private String targetMessageUuid;
    @ColumnInfo(name = "user_id")
    private String userId = "0";
    @ColumnInfo(name = "group_id")
    private String groupId = "0";
    @ColumnInfo(name = "sender_user_id")
    private String senderUserId;
    @ColumnInfo(name = "sender_avatar")
    private String senderAvatar;
    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "created_time")
    private Date createdTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    public String getTargetMessageUuid() {
        return targetMessageUuid;
    }

    public void setTargetMessageUuid(String targetMessageUuid) {
        this.targetMessageUuid = targetMessageUuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
