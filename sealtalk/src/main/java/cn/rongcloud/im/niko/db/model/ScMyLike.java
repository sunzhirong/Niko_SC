package cn.rongcloud.im.niko.db.model;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sc_my_like")
public class ScMyLike {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "message_uuid")
    private String messageUuid;


    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

}
