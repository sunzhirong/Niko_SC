package cn.rongcloud.im.niko.im.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

import androidx.fragment.app.Fragment;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.ui.activity.MyLikedActivity;
import cn.rongcloud.im.niko.utils.ToastUtils;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

public class ScLikePlugin implements IPluginModule {
    /**
     * 记录发送戳一下消息时间
     * Key：会话 id
     * Value：最近一次发送戳一下消息的时间戳，毫秒
     */

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.rc_ext_plugin_poke_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return "我的赞";
//        return context.getString(R.string.im_plugin_poke_title);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        final String targetId = rongExtension.getTargetId();
        // 判断当前会话是私人还是群组，决定对话框的显示形式
        Conversation.ConversationType conversationType = rongExtension.getConversationType();
//        ToastUtils.showToast("点击了点赞，跳转我的点赞");
        Intent intent = new Intent(fragment.getContext(), MyLikedActivity.class);
        intent.putExtra("targetId",targetId);
        if(conversationType == Conversation.ConversationType.PRIVATE){
            intent.putExtra("isPrivate",true);
        }else {
            intent.putExtra("isPrivate",false);
        }


        if(fragment.getActivity()!=null) {
            fragment.getActivity().startActivity(intent);
        }

    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
