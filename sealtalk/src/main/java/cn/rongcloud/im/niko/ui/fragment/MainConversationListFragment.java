package cn.rongcloud.im.niko.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.db.model.GroupNoticeInfo;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.ui.activity.MainActivity;
import cn.rongcloud.im.niko.ui.adapter.ConversationListAdapterEx;
import cn.rongcloud.im.niko.viewmodel.GroupNoticeInfoViewModel;
import io.rong.common.RLog;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MainConversationListFragment extends ConversationListFragment {

    private ConversationListAdapterEx conversationListAdapterEx;
    private GroupNoticeInfoViewModel groupNoticeInfoViewModel;
    private MainActivity mainActivity;
    private int position = 0;

    private Conversation.ConversationType[] conversationTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,Conversation.ConversationType.GROUP};
    private List<UIConversation> mList;

    public static MainConversationListFragment getInstance(int position) {
        MainConversationListFragment sf = new MainConversationListFragment();
        sf.position = position;
        return sf;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUri();
        initViewModel();
        view.setTag(position);

//        mList = (ListView) view.findViewById( R.id.rc_list);
        EditText editText = (EditText) view.findViewById(R.id.rc_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mList==null||mList.size()==0){return;}
                String content = s.toString().trim();
                if(!TextUtils.isEmpty(content)){
                    List<UIConversation> conversations = new ArrayList<>();
                    for (UIConversation data:mList){
                        if(data.getUIConversationTitle().contains(s)){
                            conversations.add(data);
                        }
                    }
                    conversationListAdapterEx.setList(conversations);
                }else {
                    conversationListAdapterEx.setList(mList);
//                    makeUiConversationList(mDatas);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    private void setUri() {
        // 此处是因为适配 Androidx 库， 所以才会有红色警告， 但是不影响编译运行。
        // 设置会话列表所要支持的会话类型。 并且会话类型后面的  boolean 值表示此会话类型是否内举。
        Uri uri = Uri.parse("rong://" +
                getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                .build();

        setUri(uri);
        if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.UNCONNECTED)) {
            RLog.d(TAG, "RongCloud haven't been connected yet, so the conversation list display blank !!!");
        } else {
            this.getList(conversationTypes, false);
        }

    }




    @Override
    public ConversationListAdapter onResolveAdapter(Context context) {
        if (conversationListAdapterEx == null) {
            conversationListAdapterEx = new ConversationListAdapterEx(context);
            conversationListAdapterEx.setGroupApplyMessageListener(new ConversationListAdapterEx.GroupApplyMessageListener() {
                @Override
                public void updateGroupUnReadCount(int count) {
                    updateGroupNotifyUnReadCount(count);
                }
            });
        }
        return conversationListAdapterEx;
    }

    /**
     * 更新群通知未读消息的数量
     *
     * @param num
     */
    public void updateGroupNotifyUnReadCount(int num) {
        if (mainActivity != null) {
            mainActivity.mainViewModel.setGroupNotifyUnReadNum(num);
        }
    }

    private void initViewModel() {
        groupNoticeInfoViewModel = ViewModelProviders.of(this).get(GroupNoticeInfoViewModel.class);
        groupNoticeInfoViewModel.getGroupNoticeInfo().observe(this, new Observer<Resource<List<GroupNoticeInfo>>>() {
            @Override
            public void onChanged(Resource<List<GroupNoticeInfo>> listResource) {
                if (listResource.status != Status.LOADING) {
                    if (conversationListAdapterEx != null) {
                        conversationListAdapterEx.updateNoticeInfoData(listResource.data);
                    }
                }
            }
        });
    }




    private void getList(Conversation.ConversationType[] conversationTypes, final boolean isLoadMore) {
        this.getConversationList(conversationTypes, new IHistoryDataResultCallback<List<Conversation>>() {
            public void onResult(List<Conversation> data) {
                if (data != null && data.size() > 0) {
                    makeUiConversationList(data);
                    mList = conversationListAdapterEx.getList();
                    Log.e(ConversationListFragment.TAG, "getConversationList = niko"+data.size());
                } else {

                }

            }

            public void onError() {
                Log.e(ConversationListFragment.TAG, "getConversationList Error");

            }
        }, isLoadMore);
    }

    public void onEventMainThread(Event.OnReceiveMessageEvent event) {
        super.onEventMainThread(event);
        if (event.isOffline()) {
            if (event.getLeft() == 0) {
                this.getList(conversationTypes, false);
            }

        }
    }


    private void makeUiConversationList(List<Conversation> conversationList) {
        Iterator var3 = conversationList.iterator();
        while(var3.hasNext()) {
            Conversation conversation = (Conversation)var3.next();
            Conversation.ConversationType conversationType = conversation.getConversationType();
            String targetId = conversation.getTargetId();
            boolean gatherState = this.getGatherState(conversationType);
            UIConversation uiConversation;
            int originalIndex;
            if (gatherState) {
                originalIndex = this.conversationListAdapterEx.findGatheredItem(conversationType);
                if (originalIndex >= 0) {
                    uiConversation = (UIConversation)this.conversationListAdapterEx.getItem(originalIndex);
                    uiConversation.updateConversation(conversation, true);
                } else {
                    uiConversation = UIConversation.obtain(this.getActivity(), conversation, true);
                    this.onUIConversationCreated(uiConversation);
                    this.conversationListAdapterEx.add(uiConversation);
                }
            } else {
                originalIndex = this.conversationListAdapterEx.findPosition(conversationType, targetId);
                int index;
                if (originalIndex < 0) {
                    uiConversation = UIConversation.obtain(this.getActivity(), conversation, false);
                    this.onUIConversationCreated(uiConversation);
                    index = this.getPosition(uiConversation);
                    this.conversationListAdapterEx.add(uiConversation, index);
                } else {
                    uiConversation = (UIConversation)this.conversationListAdapterEx.getItem(originalIndex);
                    if (uiConversation.getUIConversationTime() <= conversation.getSentTime()) {
                        this.conversationListAdapterEx.remove(originalIndex);
                        uiConversation.updateConversation(conversation, false);
                        index = this.getPosition(uiConversation);
                        this.conversationListAdapterEx.add(uiConversation, index);
                    } else {
                        uiConversation.setUnReadMessageCount(conversation.getUnreadMessageCount());
                    }
                }
            }
        }

    }
    private int getPosition(UIConversation uiConversation) {
        int count = this.conversationListAdapterEx.getCount();
        int position = 0;

        for(int i = 0; i < count; ++i) {
            if (uiConversation.isTop()) {
                if (!((UIConversation)this.conversationListAdapterEx.getItem(i)).isTop() || ((UIConversation)this.conversationListAdapterEx.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }
                ++position;
            } else {
                if (!((UIConversation)this.conversationListAdapterEx.getItem(i)).isTop() && ((UIConversation)this.conversationListAdapterEx.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }
                ++position;
            }
        }
        return position;
    }
}
