package cn.rongcloud.im.niko.ui.interfaces;

import cn.rongcloud.im.niko.ui.adapter.models.CheckableContactModel;
import io.rong.imlib.model.Conversation;

public interface OnCheckConversationClickListener {
    void onCheckConversationClick(CheckableContactModel<Conversation> conversation);
}
