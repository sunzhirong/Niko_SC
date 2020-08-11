package cn.rongcloud.im.niko.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.Constant;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.model.AddMemberResult;
import cn.rongcloud.im.niko.model.GroupResult;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.net.request.GroupDataReq;
import cn.rongcloud.im.niko.ui.dialog.SelectPictureBottomDialog;
import cn.rongcloud.im.niko.ui.view.SealTitleBar;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.log.SLog;
import cn.rongcloud.im.niko.viewmodel.CreateGroupViewModel;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * 创建群组
 */
public class CreateGroupActivity extends TitleBaseActivity implements View.OnClickListener {
    private final String TAG = "CreateGroupActivity";

    private EditText groupNameEt;
    private AsyncImageView groupPortraitIv;

    private Uri groupPortraitUri;
    private CreateGroupViewModel createGroupViewModel;

    private List<String> memberList;
    private String createGroupName;
    /**
     * 是否返回创建群组结果
     */
    private boolean isReturnResult;
    private boolean isCreatingGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SealTitleBar titleBar = getTitleBar();
        titleBar.setTitle(R.string.seal_main_title_create_group);

        setContentView(R.layout.main_activity_create_group);


        Intent intent = getIntent();
        if (intent == null) {
            SLog.e(TAG, "intent is null, finish " + TAG);
            return;
        }

        memberList = intent.getStringArrayListExtra(IntentExtra.LIST_STR_ID_LIST);
        isReturnResult = intent.getBooleanExtra(IntentExtra.BOOLEAN_CREATE_GROUP_RETURN_RESULT, false);
        if (memberList == null || memberList.size() == 0) {
            SLog.e(TAG, "memberList is 0, finish" + TAG);
            return;
        }
        //加入自己
        memberList.add(0, RongIM.getInstance().getCurrentUserId());

        initView();
        initViewModel();
    }

    private void initView() {
        groupNameEt = findViewById(R.id.main_et_create_group_name);
        groupNameEt.setHint(getString(R.string.profile_group_name_word_limit_format, Constant.GROUP_NAME_MIN_LENGTH, Constant.GROUP_NAME_MAX_LENGTH));
        groupNameEt.setFilters(new InputFilter[]{emojiFilter, new InputFilter.LengthFilter(10)});
        groupPortraitIv = findViewById(R.id.main_iv_create_group_portrait);
        groupPortraitIv.setOnClickListener(this);
        findViewById(R.id.main_btn_confirm_create).setOnClickListener(this);
    }

    private void initViewModel() {
        createGroupViewModel = ViewModelProviders.of(this).get(CreateGroupViewModel.class);

        createGroupViewModel.getCreateGroupResult().observe(this, resource -> {
            if (resource.status == Status.SUCCESS) {
                // 处理创建群组结果
                processCreateResult(resource.data);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_iv_create_group_portrait:
                showSelectPortraitDialog();
                break;
            case R.id.main_btn_confirm_create:
                createGroup();
                break;
            default:
                break;
        }
    }

    /**
     * 显示选择图片对话框
     */
    private void showSelectPortraitDialog() {
        SelectPictureBottomDialog.Builder builder = new SelectPictureBottomDialog.Builder();
        builder.setOnSelectPictureListener(uri -> {
            SLog.d(TAG, "select picture, uri:" + uri);
            groupPortraitIv.setImageURI(null);
            groupPortraitIv.setImageURI(uri);
            groupPortraitUri = uri;
        });
        SelectPictureBottomDialog dialog = builder.build();
        dialog.show(getSupportFragmentManager(), null);
    }

    /**
     * 创建群组
     */
    private void createGroup() {
        if (isCreatingGroup) return;

        String groupName = groupNameEt.getText().toString();
        groupName = groupName.trim();

        if (groupName.length() < Constant.GROUP_NAME_MIN_LENGTH || groupName.length() > Constant.GROUP_NAME_MAX_LENGTH) {
            ToastUtils.showToast(getString(R.string.profile_group_name_word_limit_format, Constant.GROUP_NAME_MIN_LENGTH, Constant.GROUP_NAME_MAX_LENGTH));
            return;
        }

        if (AndroidEmoji.isEmoji(groupName) && groupName.length() < Constant.GROUP_NAME_EMOJI_MIN_LENGTH) {
            ToastUtils.showToast(getString(R.string.profile_group_name_emoji_too_short));
            return;
        }

        createGroupName = groupName;

        // 标记创建群组状态
        isCreatingGroup = true;

        GroupDataReq groupDataReq = new GroupDataReq();
        groupDataReq.setTitle(createGroupName);
        groupDataReq.setChatGrpID(0);
        List<Integer> integers = new ArrayList<>();
        try {
            for(String id:memberList){
                integers.add(Integer.parseInt(id));
            }
        }catch (Exception e){

        }

        groupDataReq.setUIDs(integers);
        createGroupViewModel.createGroup(groupDataReq);
    }

    /**
     * 处理创建结果
     *
     * @param groupResult
     */
    private void processCreateResult(Integer groupResult) {
        String groupId = groupResult.toString();

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
        sendMsg(groupId);

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

    /**
     * 跳转到群组聊天
     */
    private void toGroupChat(String groupId) {
        RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP, groupId, createGroupName);
        finish();
    }

    /**
     * 表情输入的过滤
     */
    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                ToastUtils.showToast("不支持输入表情");
                return "";
            }
            return null;
        }
    };
}
