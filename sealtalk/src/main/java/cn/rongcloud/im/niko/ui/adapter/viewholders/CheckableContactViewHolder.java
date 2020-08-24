package cn.rongcloud.im.niko.ui.adapter.viewholders;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.db.model.FriendDetailInfo;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.model.GroupMember;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.adapter.models.CheckableContactModel;
import cn.rongcloud.im.niko.ui.interfaces.OnCheckContactClickListener;
import cn.rongcloud.im.niko.ui.widget.SelectableRoundedImageView;
import cn.rongcloud.im.niko.utils.ImageLoaderUtils;

public class CheckableContactViewHolder extends CheckableBaseViewHolder<CheckableContactModel> {

    private TextView nameTextView;
    private SelectableRoundedImageView protraitImageView;
    private OnCheckContactClickListener checkableItemClickListener;
    private CheckableContactModel model;
    private ImageView checkBox;

//    private boolean canSelect;

    public CheckableContactViewHolder(@NonNull View itemView, OnCheckContactClickListener listener,boolean canSelect) {
        super(itemView);
        checkableItemClickListener = listener;
        protraitImageView = itemView.findViewById(R.id.iv_portrait);
        nameTextView = itemView.findViewById(R.id.tv_contact_name);
        checkBox = itemView.findViewById(R.id.cb_select);
//        this.canSelect = canSelect;

//        if(canSelect) {
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    checkableItemClickListener.onContactContactClick(model);
//                }
//            });
//        }
        checkBox.setVisibility(canSelect?View.VISIBLE:View.GONE);
    }

    @Override
    public void update(CheckableContactModel contactModel,boolean canSelect) {
        model = contactModel;
        String name = null;
        String portraitUrl = null;
        int nameColor = ProfileUtils.getNameColor("#0A0A0B");
        if (contactModel.getBean() instanceof FriendShipInfo) {
            FriendShipInfo friendShipInfo = (FriendShipInfo) contactModel.getBean();
            FriendDetailInfo info = friendShipInfo.getUser();
            String groupDisplayName = friendShipInfo.getGroupDisplayName();
            String displayName = friendShipInfo.getDisplayName();
            if (!TextUtils.isEmpty(groupDisplayName)) {
                name = groupDisplayName;
            } else if (!TextUtils.isEmpty(displayName)) {
                name = displayName;
            } else {
                name = info.getNickname();
            }
            portraitUrl = info.getPortraitUri();
            nameColor = ProfileUtils.getNameColor(friendShipInfo.getNameColor());
            Log.e("update","friendShipInfo.getNameColor()="+friendShipInfo.getNameColor());
        } else if (contactModel.getBean() instanceof GroupMember) {
            GroupMember groupMember = (GroupMember) contactModel.getBean();
            name = groupMember.getGroupNickName();
            if (TextUtils.isEmpty(name)) {
                name = groupMember.getName();
            }
            portraitUrl = groupMember.getPortraitUri();
            nameColor = ProfileUtils.getNameColor(groupMember.getNameColor());
            Log.e("update","groupMember.getNameColor()="+groupMember.getNameColor());

        }

        nameTextView.setText(name);
        nameTextView.setTextColor(nameColor);
        ImageLoaderUtils.displayUserPortraitImage(portraitUrl, protraitImageView);
        updateCheck(checkBox, contactModel.getCheckType());

        if(canSelect) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkableItemClickListener.onContactContactClick(model);

                }
            });
        }else {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        checkBox.setVisibility(canSelect?View.VISIBLE:View.GONE);
    }

}
