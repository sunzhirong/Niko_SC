package cn.rongcloud.im.niko.db.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import cn.rongcloud.im.niko.db.model.ScLikeDetail;
import cn.rongcloud.im.niko.db.model.ScMyLike;

@Dao
public interface ScLikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScLikeDetail scLikeDetail);

//    @Query("SELECT * FROM (SELECT * FROM sc_like_detail WHERE target_message_uuid = :id ORDER BY created_time DESC  LIMIT 100000 ) t  GROUP BY sender_user_id")
    @Query("SELECT * FROM sc_like_detail WHERE target_message_uuid = :id  GROUP BY sender_user_id ORDER BY created_time DESC LIMIT 10000000000")
    List<ScLikeDetail> getDetailsById(String id);


    @Query("SELECT * FROM sc_like_detail WHERE target_message_uuid = :id AND sender_user_id = :senderId LIMIT 1")
    ScLikeDetail getDetailsByIdOneUser(String id,String senderId);

    @Query("SELECT * FROM sc_like_detail")
    List<ScLikeDetail> getAllDetails();


    @Query("SELECT * FROM sc_my_like WHERE message_uuid = :id LIMIT 1")
    ScMyLike getMyLike(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMyLike(ScMyLike scLikeDetail);

    @Query("DELETE FROM sc_my_like WHERE message_uuid=:message_uuid")
    void deleteMyLike(String message_uuid);

}
