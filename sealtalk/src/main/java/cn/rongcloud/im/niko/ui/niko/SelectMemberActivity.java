package cn.rongcloud.im.niko.ui.niko;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.event.SelectFriendEvent;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.net.request.GroupDataReq;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.FriendRvAdapter;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.viewmodel.CreateGroupViewModel;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class SelectMemberActivity extends BaseActivity {
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_complete)
    TextView mTvComplete;
    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.rv_group)
    RecyclerView mRvGroup;
    private FriendRvAdapter mFriendRvAdapter;
    private List<FriendBean> mRsData;
    private List<FriendBean> mList;
    private String createGroupName;
    private boolean isReturnResult;
    private CreateGroupViewModel createGroupViewModel;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_group_member;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    //    @Override
    private void initView() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        isReturnResult = intent.getBooleanExtra(IntentExtra.BOOLEAN_CREATE_GROUP_RETURN_RESULT, false);

        EventBus.getDefault().register(this);
        mList = new ArrayList<>();

        mRvGroup.setLayoutManager(new LinearLayoutManager(mContext));
        mFriendRvAdapter = new FriendRvAdapter(mContext, new ArrayList<>());
        mRvGroup.setAdapter(mFriendRvAdapter);

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                if(TextUtils.isEmpty(content)){
                    mFriendRvAdapter.setDatas(mRsData);
                }else {
                    List<FriendBean> infos = new ArrayList<>();
                    for(FriendBean info :mRsData){
                        if(info.getName().contains(content)){
                            infos.add(info);
                        }
                    }
                    mFriendRvAdapter.setDatas(infos);
                }
            }
        });
        initViewModel();
    }

    private void initViewModel() {
        createGroupViewModel = ViewModelProviders.of(this).get(CreateGroupViewModel.class);

        createGroupViewModel.getCreateGroupResult().observe(this, resource -> {
            if (resource.status == Status.SUCCESS) {
                // 处理创建群组结果
                processCreateResult(String.valueOf(resource.data));
            } else if (resource.status == Status.ERROR) {
                // 当有结果时代表群组创建成功，但上传图片失败
//                if (resource.data != null) {
//                    // 处理创建群组结果
//                    processCreateResult(resource.data);
//                } else {
//                    // 仅有创建失败时重置创建群组状态
//                    isCreatingGroup = false;
//                }

                ToastUtils.showToast(resource.message);
            }
        });
        createGroupViewModel.getFriendListResult().observe(this,result->{
            if (result.RsCode == NetConstant.REQUEST_SUCCESS_CODE) {
                mRsData = result.RsData;
                mFriendRvAdapter.setDatas(mRsData);
            }
        });

        createGroupViewModel.getFriendList(NetConstant.SKIP,NetConstant.TAKE);
//        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
//        mUserInfoViewModel.getCreateGroupResult().observe(this, result -> {
//            if (result.RsCode == NetConstant.REQUEST_SUCCESS_CODE) {
//                processCreateResult(String.valueOf(result.RsData));
////                finish();
//            }
//        });
//
//        mUserInfoViewModel.getFriendListResult().observe(this,result->{
//            if (result.RsCode == NetConstant.REQUEST_SUCCESS_CODE) {
//                mRsData = result.RsData;
//                mFriendRvAdapter.setDatas(mRsData);
//            }
//        });
//        mUserInfoViewModel.getFriendList(NetConstant.SKIP,NetConstant.TAKE);

    }

    /**
     * 处理创建结果
     *
     * @param
     */
//    private void processCreateResult(GroupResult groupResult) {
    private void processCreateResult(String groupId) {
//        String groupId = "";
//        if (groupResult != null) {
//            groupId = groupResult.id;

//            // 判断是否有人需要同意加入群，显示提示信息
//            if (groupResult.userStatus != null && groupResult.userStatus.size() > 0) {
//                String tips = getString(R.string.seal_create_success);
//                //1 为已加入, 2 为等待管理员同意, 3 为等待被邀请者同意
//                for (AddMemberResult result : groupResult.userStatus) {
//                    if (result.status == 3) {
//                        tips = getString(R.string.seal_add_need_member_agree);
//                        break;
//                    }
//                }
//                ToastUtils.showToast(tips);
//            }
//        }

        // 返回结果时候设置结果并结束
        if (isReturnResult) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(IntentExtra.GROUP_ID, groupId);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            //不返回结果时，创建成功后跳转到群组聊天中
            toGroupChat(groupId);
        }
    }

    /**
     * 跳转到群组聊天
     */
    private void toGroupChat(String groupId) {
        sendMsg(groupId);
        RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP, groupId, createGroupName);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                DbManager.getInstance(mContext).getGroupDao().updateGroupName(groupId,createGroupName,createGroupName);
            }
        }).start();
        finish();
    }

    private void sendMsg(String groupId) {
        // 构造 TextMessage 实例
        TextMessage myTextMessage = TextMessage.obtain("欢迎加入"+createGroupName+"群聊");

        /* 生成 Message 对象。
         * "7127" 为目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
         * Conversation.ConversationType.PRIVATE 为私聊会话类型，根据需要，也可以传入其它会话类型，如群组。
         */
        Message myMessage = Message.obtain(groupId, Conversation.ConversationType.GROUP, myTextMessage);

        /**
         * <p>发送消息。
         * 通过 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}
         * 中的方法回调发送的消息状态及消息体。</p>
         *
         * @param message     将要发送的消息体。
         * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
         *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
         *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
         * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
         * @param callback    发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
         */
        RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                //消息本地数据库存储成功的回调
            }

            @Override
            public void onSuccess(Message message) {
                //消息通过网络发送成功的回调
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                //消息发送失败的回调
            }
        });
    }


    @OnClick({R.id.tv_cancel, R.id.tv_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                if(mList.size()!=0) {
                    createGroup();
                }
                break;
        }
    }

    private void createGroup() {
        GroupDataReq groupDataReq = new GroupDataReq();
        groupDataReq.setChatGrpID(0);
        List<Integer> integers = new ArrayList<>();
        //加入自己
        for(FriendBean info : mList){
            integers.add(info.getUID());
        }
        groupDataReq.setUIDs(integers);
        integers.add(0, Integer.valueOf(IMManager.getInstance().getCurrentId()));

        createGroupName = RongUserInfoManager.getInstance().getUserInfo(IMManager.getInstance().getCurrentId()).getName();
        if(createGroupName.length()<=10){
            for(FriendBean info : mList){
                createGroupName = createGroupName + " " + info.getName();
                if(createGroupName.length()>10){
                    break;
                }
            }
        }

        groupDataReq.setTitle(createGroupName);

        createGroupViewModel.createGroup(groupDataReq);

//        mUserInfoViewModel.createGroup(groupDataReq);
    }


    public void onEventMainThread(SelectFriendEvent event) {
        if(!mList.contains(event.bean)) {
            mList.add(event.bean);
            mTvComplete.setText("创建" + mList.size());
        }else {
            mList.remove(event.bean);
            if(mList.size()==0){
                mTvComplete.setText("创建");
            }else {
                mTvComplete.setText("创建" + mList.size());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
