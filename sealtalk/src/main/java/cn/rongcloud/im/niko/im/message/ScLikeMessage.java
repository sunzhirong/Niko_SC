package cn.rongcloud.im.niko.im.message;

import android.os.Parcel;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cn.rongcloud.im.niko.common.LogTag;
import cn.rongcloud.im.niko.utils.log.SLog;
import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 点赞消息
 */
//@MessageTag(value = "ST:LikeMsg", flag = MessageTag.NONE | MessageTag.ISCOUNTED)
@MessageTag(value = "ST:LikeMsg")
public class ScLikeMessage extends MessageContent {

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

//    {
//        "content": "点赞了你的消息",
//            "extra": "helloExtra",
//            "messageUUID": "BJLP-F2VJ-7ME5-EEEE",
//            "targetMessageUUID": "BJLP-F2VJ-7ME5-EUA2",
//            "userId": "2169",
//            "groupId": "0",
//            " senderUserId": "2219",
//            "senderAvatar": "_aa_UserIcon.jpg",
//            "textDescription": "1"
//    }











    private String content;
    private String extra;
    private String messageUUID;
    private String targetMessageUUID;
    private String userId = "0";
    private String groupId = "0";
    private String senderUserId;
    private String senderAvatar;
    private String textDescription;

    private ScLikeMessage() {
    }

    public ScLikeMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, StandardCharsets.UTF_8);
            JSONObject jsonObj = new JSONObject(jsonStr);
            content = jsonObj.optString("content");
            extra = jsonObj.optString("extra");
            messageUUID = jsonObj.optString("messageUUID");
            targetMessageUUID = jsonObj.optString("targetMessageUUID");
            userId = jsonObj.optString("userId");
            groupId = jsonObj.optString("groupId");
            senderUserId = jsonObj.optString("senderUserId");
            senderAvatar = jsonObj.optString("senderAvatar");
            textDescription = jsonObj.optString("textDescription");
        } catch (Exception e) {
            SLog.e(LogTag.IM, "ScLikeMessage parse error:" + e.toString());
        }

    }

    public ScLikeMessage(Parcel in) {
        this.content = in.readString();
        this.extra = in.readString();
        this.messageUUID = in.readString();
        this.targetMessageUUID = in.readString();
        this.userId = in.readString();
        this.groupId = in.readString();
        this.senderUserId = in.readString();
        this.senderAvatar = in.readString();
        this.textDescription = in.readString();
//        content = ParcelUtils.readFromParcel(in);
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public void setMessageUUID(String messageUUID) {
        this.messageUUID = messageUUID;
    }

    public String getTargetMessageUUID() {
        return targetMessageUUID;
    }

    public void setTargetMessageUUID(String targetMessageUUID) {
        this.targetMessageUUID = targetMessageUUID;
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

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }


    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("content", this.content);
            jsonObj.put("extra", this.extra);
            jsonObj.put("messageUUID", this.messageUUID);
            jsonObj.put("targetMessageUUID", this.targetMessageUUID);
            jsonObj.put("userId", this.userId);
            jsonObj.put("groupId", this.groupId);
            jsonObj.put("senderUserId", this.senderUserId);
            jsonObj.put("senderAvatar", this.senderAvatar);
            jsonObj.put("textDescription", this.textDescription);
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            e.printStackTrace();
            SLog.e(LogTag.IM, "ScLikeMessage encode error:" + e.toString());
        }

        return null;
    }

    public static ScLikeMessage obtain() {
//        ScLikeMessage pokeMessage = new ScLikeMessage();
//        pokeMessage.content = content;
//        return pokeMessage;

        return new ScLikeMessage();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        ParcelUtils.writeToParcel(dest, this.content);
        dest.writeString(this.content);
        dest.writeString(this.extra);
        dest.writeString(this.messageUUID);
        dest.writeString(this.targetMessageUUID);
        dest.writeString(this.userId);
        dest.writeString(this.groupId);
        dest.writeString(this.senderUserId);
        dest.writeString(this.senderAvatar);
        dest.writeString(this.textDescription);
    }

    public static final Creator<ScLikeMessage> CREATOR = new Creator<ScLikeMessage>() {
        public ScLikeMessage createFromParcel(Parcel source) {
            return new ScLikeMessage(source);
        }

        public ScLikeMessage[] newArray(int size) {
            return new ScLikeMessage[size];
        }
    };

}
