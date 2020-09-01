package cn.rongcloud.im.niko.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.common.ThreadManager;
import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.db.model.FriendDescription;
import cn.rongcloud.im.niko.db.model.FriendDetailInfo;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.db.model.ScLikeDetail;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.event.DeleteFriendEvent;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.dialog.CommonDialog;
import cn.rongcloud.im.niko.ui.view.SealTitleBar;
import cn.rongcloud.im.niko.ui.view.SettingItemView;
import cn.rongcloud.im.niko.ui.widget.SelectableRoundedImageView;
import cn.rongcloud.im.niko.utils.ImageLoaderUtils;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.log.SLog;
import cn.rongcloud.im.niko.viewmodel.PrivateChatSettingViewModel;
import cn.rongcloud.im.niko.viewmodel.UserDetailViewModel;
import io.rong.eventbus.EventBus;
import io.rong.imlib.model.Conversation;

public class PrivateChatSettingActivity extends TitleBaseActivity implements View.OnClickListener {
    private final String TAG = "PrivateChatSettingActivity";


    private PrivateChatSettingViewModel privateChatSettingViewModel;

    private SettingItemView isTopSb;

    private String targetId;
    private Conversation.ConversationType conversationType;
    private SelectableRoundedImageView portraitIv;
    private TextView nameTv;
    private SettingItemView sivDescription;
    private SettingItemView blacklistSiv;
    private UserDetailViewModel userDetailViewModel;
    private boolean isInBlackList = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SealTitleBar titleBar = getTitleBar();
        titleBar.setTitle("聊天设置");

        setContentView(R.layout.profile_activity_private_chat_setting);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        targetId = intent.getStringExtra(IntentExtra.STR_TARGET_ID);
        conversationType = (Conversation.ConversationType) intent.getSerializableExtra(IntentExtra.SERIA_CONVERSATION_TYPE);
        initView();
        initViewModel();
        initData();
        EventBus.getDefault().register(this);


    }

    private void initView() {
        portraitIv = findViewById(R.id.profile_siv_user_header);

        // 用户名
        nameTv = findViewById(R.id.profile_tv_user_name);

        isTopSb = findViewById(R.id.siv_conversation_top);
        isTopSb.setSwitchCheckListener((buttonView, isChecked) ->
                privateChatSettingViewModel.setConversationOnTop(isChecked));


        // 设置备注
        sivDescription = findViewById(R.id.profile_siv_detail_alias);
        sivDescription.setOnClickListener(this);

        // 加入，移除黑名单
        blacklistSiv = findViewById(R.id.profile_siv_detail_blacklist);
        blacklistSiv.setOnClickListener(this);
    }


    private void initViewModel() {
        privateChatSettingViewModel = ViewModelProviders.of(this, new PrivateChatSettingViewModel.Factory(getApplication(), targetId, conversationType)).get(PrivateChatSettingViewModel.class);
        privateChatSettingViewModel.getFriendInfo().observe(this, friendShipInfoResource -> {
            UserInfo data = friendShipInfoResource.data;
//            FriendShipInfo data = friendShipInfoResource.data;
            if (data == null) return;

            String displayName = TextUtils.isEmpty(data.getAlias())?data.getName():data.getAlias();

            // 设置备注名
            nameTv.setText(displayName);
            nameTv.setTextColor(ProfileUtils.getNameColor(data.getNameColor()));
            ImageLoaderUtils.displayUserPortraitImage(data.getPortraitUri(), portraitIv);
        });


        // 获取是否消息置顶状态
        privateChatSettingViewModel.getIsTop().observe(this, resource -> {
            if (resource.data != null) {
                if (resource.status == Status.SUCCESS) {
                    isTopSb.setChecked(resource.data);
                } else {
                    isTopSb.setCheckedImmediately(resource.data);
                }
            }

            if (resource.status == Status.ERROR) {
                if (resource.data != null) {
                    ToastUtils.showToast(R.string.common_set_failed);
                } else {
                    // do nothing
                }
            }
        });

        userDetailViewModel = ViewModelProviders.of(this, new UserDetailViewModel.Factory(getApplication(), targetId)).get(UserDetailViewModel.class);

        // 获取黑名单状态
        userDetailViewModel.getIsInBlackList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isInBlackList) {
                updateBlackListItem(isInBlackList);
            }
        });

        // 获取添加到黑名单结果
        userDetailViewModel.getAddBlackListResult().observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> resource) {
                if (resource.status == Status.SUCCESS) {
                    ToastUtils.showToast(R.string.common_add_successful);
                } else if (resource.status == Status.ERROR) {
                    ToastUtils.showToast(resource.message);
                }
            }
        });

        // 获取移除黑名单结果
        userDetailViewModel.getRemoveBlackListResult().observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> resource) {
                if (resource.status == Status.SUCCESS) {
                    ToastUtils.showToast(R.string.common_remove_successful);
                } else if (resource.status == Status.ERROR) {
                    ToastUtils.showToast(resource.message);
                }
            }
        });


        userDetailViewModel.getFriendDescription().observe(this, new Observer<Resource<FriendDescription>>() {
            @Override
            public void onChanged(Resource<FriendDescription> friendDescriptionResource) {
                if (friendDescriptionResource.status != Status.LOADING && friendDescriptionResource.data != null) {
                    updateDescription(friendDescriptionResource.data);
                }
            }
        });

    }

    /**
     * 刷新更多中黑名单选项
     *
     * @param isInBlackList
     */
    private void updateBlackListItem(boolean isInBlackList) {
        this.isInBlackList = isInBlackList;
        if (isInBlackList) {
            blacklistSiv.setContent(R.string.profile_detail_remove_from_blacklist);
        } else {
            blacklistSiv.setContent(R.string.profile_detail_join_the_blacklist);
        }
    }

    private void initData() {
        privateChatSettingViewModel.requestFriendInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_siv_detail_alias:
                toSetAliasName();
                break;
            case R.id.profile_siv_detail_blacklist:
                toBlackList(!isInBlackList);
                break;
            default:
        }
    }

    /**
     * 是否加到黑名单
     *
     * @param isToBlack true 代表加到黑名单，false 代表移除掉黑名单
     */
    private void toBlackList(boolean isToBlack) {
        if (isToBlack) {
            // 显示确认对话框
            CommonDialog commonDialog = new CommonDialog.Builder()
                    .setContentMessage(getString(R.string.profile_add_to_blacklist_tips))
                    .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                        @Override
                        public void onPositiveClick(View v, Bundle bundle) {
                            userDetailViewModel.addToBlackList();
                        }

                        @Override
                        public void onNegativeClick(View v, Bundle bundle) {
                        }
                    })
                    .build();
            commonDialog.show(getSupportFragmentManager(), null);
        } else {
            userDetailViewModel.removeFromBlackList();
        }
    }


    private void updateDescription(FriendDescription data) {
        if (!TextUtils.isEmpty(data.getDescription())) {
            sivDescription.setContent(R.string.profile_set_display_des);
            sivDescription.setValue(data.getDescription());
            sivDescription.getValueView().setSingleLine();
            sivDescription.getValueView().setMaxEms(10);
            sivDescription.getValueView().setEllipsize(TextUtils.TruncateAt.END);
        } else {
            // 同时为空显示'设置备注和描述'
            if (TextUtils.isEmpty(data.getPhone())) {
                sivDescription.setContent(R.string.profile_set_display_name);
            } else {
                sivDescription.setContent(R.string.profile_set_display_des);
            }
            sivDescription.setValue("");
        }

    }

    /**
     * 跳转到设置备注名
     */
    private void toSetAliasName() {
        Intent intent = new Intent(this, EditUserDescribeActivity.class);//EditAliasActivity
        intent.putExtra(IntentExtra.STR_TARGET_ID, targetId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 删除联系人成功的事件
     *
     * @param event 删除结果事件
     */
    public void onEventMainThread(DeleteFriendEvent event) {
        if (event.result && event.userId.equals(targetId)) {
            SLog.i(TAG, "DeleteFriend Success");
            finish();
        }
    }
}
