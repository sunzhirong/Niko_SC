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
    private static final HashMap<String, Long> sendPokeTimeMap = new HashMap<>();

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
        Long lastSendTime = sendPokeTimeMap.get(targetId);
        long currentTimeMillis = System.currentTimeMillis();


        // 判断当前会话是私人还是群组，决定对话框的显示形式
        Conversation.ConversationType conversationType = rongExtension.getConversationType();
        String targetName = "";
        boolean isToMulti = false;

        ToastUtils.showToast("点击了点赞，跳转我的点赞");

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

//        if (conversationType == Conversation.ConversationType.PRIVATE) {
//            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
//            targetName = userInfo.getName();
//            isToMulti = false;
//        } else if (conversationType == Conversation.ConversationType.GROUP) {
//            // 当是群组会话时，判断是否为群主或管理员，仅群主和管理员可以发送戳一下消息
//            FragmentActivity activity = fragment.getActivity();
//            if(activity instanceof ConversationActivity){
//                ConversationActivity conversationActivity = (ConversationActivity) activity;
//                boolean groupOwner = conversationActivity.isGroupOwner();
//                boolean groupManager = conversationActivity.isGroupManager();
//                if(!groupOwner && !groupManager){
//                    ToastUtils.showToast(R.string.poke_only_group_owner_and_manager_can_send);
//                    return;
//                }
//            }
//
//            Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(targetId);
//            targetName = groupInfo.getName();
//            isToMulti = true;
//        }
//
//        SendPokeDialog pokeDialog = new SendPokeDialog();
//        pokeDialog.setTargetName(targetName);
//        pokeDialog.setIsMultiSelect(isToMulti);
//        pokeDialog.setTargetId(targetId);
//        pokeDialog.setOnSendPokeClickedListener((isMultiSelect, userIds, pokeMessage) -> {
//            if (isMultiSelect) {
//                IMManager.getInstance().sendPokeMessageToGroup(targetId, pokeMessage, userIds, new IRongCallback.ISendMessageCallback() {
//                    @Override
//                    public void onAttached(Message message) {
//                    }
//                    @Override
//                    public void onSuccess(Message message) {
//                        // 记录当前发送的时间
//                        sendPokeTimeMap.put(targetId, System.currentTimeMillis());
//                    }
//                    @Override
//                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                    }
//                });
//            } else {
//                IMManager.getInstance().sendPokeMessageToPrivate(targetId, pokeMessage, new IRongCallback.ISendMediaMessageCallback() {
//                    @Override
//                    public void onProgress(Message message, int i) {
//                    }
//                    @Override
//                    public void onCanceled(Message message) {
//                    }
//                    @Override
//                    public void onAttached(Message message) {
//                    }
//                    @Override
//                    public void onSuccess(Message message) {
//                        // 记录当前发送的时间
//                        sendPokeTimeMap.put(targetId, System.currentTimeMillis());
//                    }
//                    @Override
//                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                    }
//                });
//            }
//        });

//        // 显示对话框，收起扩展栏
//        if (fragment != null && fragment.getFragmentManager() != null) {
////            pokeDialog.show(fragment.getFragmentManager(), null);
//            rongExtension.collapseExtension();
//        }
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
