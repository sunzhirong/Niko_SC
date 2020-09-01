package cn.rongcloud.im.niko.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.event.ContactsItemClickEvent;
import cn.rongcloud.im.niko.event.LogoutEvent;
import cn.rongcloud.im.niko.event.SelectFriendEvent;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.net.request.GroupDataReq;
import cn.rongcloud.im.niko.sp.SPUtils;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.MembersAdapter;
import cn.rongcloud.im.niko.ui.adapter.SelectMembersAdapter;
import cn.rongcloud.im.niko.ui.dialog.ClearCacheDialog;
import cn.rongcloud.im.niko.ui.dialog.CommonDialog;
import cn.rongcloud.im.niko.ui.view.SealTitleBar;
import cn.rongcloud.im.niko.ui.view.SettingItemView;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import cn.rongcloud.im.niko.viewmodel.CreateGroupViewModel;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.mention.SideBar;
import io.rong.imkit.tools.CharacterParser;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 带有标题和搜索输入框的基础类
 * 通过实现 {@link TitleAndSearchBaseActivity#onSearch} 方法来进行搜索操作
 */
public abstract class TitleAndSearchBaseActivity extends TitleBaseActivity {
    /**
     * 输入搜索文字相应延迟
     */
    public static final int SEARCH_TEXT_INPUT_DELAY_MILLIS = 500;
    private FrameLayout containerLayout;
    private TextView searchTv;
    private EditText etSearch;
    private SealTitleBar titleBar;
    private Handler delayHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentView = LayoutInflater.from(this).inflate(R.layout.common_activity_title_and_search_base, null);
        super.setContentView(contentView);

        containerLayout = findViewById(R.id.title_and_search_container);
        searchTv = findViewById(R.id.title_and_search_tv_search);
        etSearch = findViewById(R.id.title_et_search);
        titleBar = getTitleBar();

        delayHandler = new Handler();

        initTitleAndSearchView();
    }

    private void initTitleAndSearchView(){
        // 设置搜索框的点击事件
//        searchTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSearchTitle();
//            }
//        });

        // 设置搜索框的清除文本点击事件
//        titleBar.setOnSearchClearTextClickedListener(new SealTitleBar.OnSearchClearTextClickedListener() {
//            @Override
//            public void onSearchClearTextClicked() {
//                showNormalTitle();
//            }
//        });

        // 设置后退键的点击事件
//        getTitleBar().setOnBtnLeftClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                closeSearchOrExit();
//            }
//        });

        // 设置标题栏搜索输入框的文本变化监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeTextChanged","s="+s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged","s="+s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged","s="+s.toString());
                delayHandler.removeCallbacks(searchKeywordRunnable);
                String keyword = s.toString();
                int delay = SEARCH_TEXT_INPUT_DELAY_MILLIS;
                // 当输入空白时立即显示结果
                if(TextUtils.isEmpty(keyword)){
                    delay = 0;
                }
                delayHandler.postDelayed(searchKeywordRunnable, delay);
            }
        });
    }

    /**
     * 触发搜索操作，因为配合 delayHandler 使用，所以单独实现
     */
    private Runnable searchKeywordRunnable = new Runnable() {
        @Override
        public void run() {
            String keyword = etSearch.getText().toString().trim();
            onSearch(keyword);
//            if(titleBar != null) {
//                String keyword = titleBar.getEtSearch().getText().toString();
//                onSearch(keyword);
//            }
        }
    };

    /**
     * 当搜索中输入内容时会调用此方法
     * 此方法会在输入后
     * @param keyword
     */
    abstract public void onSearch(String keyword);

    /**
     * 设置确认按钮的可用情况
     * @param enable
     */
    public void enableConfirmButton(boolean enable) {
        TextView titleConfirmTv = titleBar.getTvRight();
        if (enable) {
            titleConfirmTv.setClickable(true);
            titleConfirmTv.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            titleConfirmTv.setClickable(false);
            titleConfirmTv.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    /**
     * 获取搜索文本框
     * @return
     */
    public TextView getSearchTextView(){
        return  searchTv;
    }

    @Override
    public void setContentView(View view) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.addView(view, lp);
    }

    /**
     * 显示搜索标题
     */
    private void showSearchTitle(){
        setTitleBarType(SealTitleBar.Type.SEARCH);
        searchTv.setVisibility(View.GONE);
    }

    /**
     * 显示普通标题
     */
    private void showNormalTitle(){
        titleBar.getEtSearch().setText("");
        setTitleBarType(SealTitleBar.Type.NORMAL);
        searchTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        closeSearchOrExit();
    }

    /**
     * 判断当前是该关闭搜索还是退出界面
     */
    private void closeSearchOrExit(){
        if(titleBar.getType() == SealTitleBar.Type.SEARCH){
            showNormalTitle();
        } else {
            finish();
        }
    }

//    @Override
//    public SealTitleBar getTitleBar() {
//        return titleBar;
//    }

    public static class SelectMemberActivity extends BaseActivity {
        @BindView(R.id.tv_cancel)
        TextView mTvCancel;
        @BindView(R.id.tv_complete)
        TextView mTvComplete;
        @BindView(R.id.et_search)
        EditText mEtSearch;
        @BindView(R.id.rc_list)
        ListView mRcList;
        @BindView(R.id.rc_popup_bg)
        TextView mRcPopupBg;
        @BindView(R.id.rc_sidebar)
        SideBar mRcSidebar;
        @BindView(R.id.fl)
        TagFlowLayout mFl;
        private List<FriendBean> mList;
        private String createGroupName;
        private boolean isReturnResult;
        private CreateGroupViewModel createGroupViewModel;
        private SelectMembersAdapter mAdapter;
        private List<MembersAdapter.MemberInfo> mAllMemberList;

        private boolean success;
        private TagAdapter<FriendBean> mTagAdapter;

        protected boolean keyboardEnable(){
            return false;
        }

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

            EventBus.getDefault().register(this);
            isReturnResult = intent.getBooleanExtra(IntentExtra.BOOLEAN_CREATE_GROUP_RETURN_RESULT, false);

            mList = new ArrayList<>();

            mTagAdapter = new TagAdapter<FriendBean>(mList) {
                @Override
                public View getView(FlowLayout parent, int position, FriendBean bean) {
                    TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.flowlayout_tv,
                            mFl, false);
                    tv.setText("@"+bean.getName());
                    return tv;
                }
            };
            mFl.setAdapter(mTagAdapter);
            mAdapter = new SelectMembersAdapter();
            mRcList.setAdapter(mAdapter);
            mAllMemberList = new ArrayList<>();

            mRcSidebar.setTextView(mRcPopupBg);
            initViewModel();

            //设置右侧触摸监听
            mRcSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
                @Override
                public void onTouchingLetterChanged(String s) {
                    //该字母首次出现的位置
                    int position = mAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        mRcList.setSelection(position);
                    }
                }
            });


            mEtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                    List<MembersAdapter.MemberInfo> filterDataList = new ArrayList<>();

                    if (TextUtils.isEmpty(s.toString())) {
                        filterDataList = mAllMemberList;
                    } else {
                        filterDataList.clear();
                        for (MembersAdapter.MemberInfo member : mAllMemberList) {
                            String name = member.userInfo.getName();
                            if (!TextUtils.isEmpty(member.userInfo.getAlias())) {
                                name = member.userInfo.getAlias();
                            }
                            if (name != null) {
                                if (name.contains(s) || CharacterParser.getInstance().getSelling(name).startsWith(s.toString())) {
                                    filterDataList.add(member);
                                }
                            }
                        }
                    }
                    // 根据a-z进行排序
                    Collections.sort(filterDataList, MembersAdapter.PinyinComparator.getInstance());
                    mAdapter.setData(filterDataList);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });



            initViewModel();
        }

        private void initViewModel() {
            createGroupViewModel = ViewModelProviders.of(this).get(CreateGroupViewModel.class);

            createGroupViewModel.getCreateGroupResult().observe(this, resource -> {
                if (resource.status == Status.SUCCESS) {
                    // 处理创建群组结果
                    if(!success){
                        success = true;
                        processCreateResult(String.valueOf(resource.data));
                    }
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
            createGroupViewModel.getFriendListResult().observe(this, result -> {
                if (result.RsCode == NetConstant.REQUEST_SUCCESS_CODE) {
                        mAllMemberList.clear();
                        List<FriendBean> rsData = result.getRsData();

                        for (FriendBean info : rsData) {
                            RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

                                /**
                                 * 获取设置用户信息. 通过返回的 userId 来封装生产用户信息.
                                 * @param userId 用户 ID
                                 */
                                @Override
                                public UserInfo getUserInfo(String userId) {
                                    return new UserInfo(String.valueOf(info.getUID()), info.getName(), Uri.parse(GlideImageLoaderUtil.getScString(info.getUserIcon())));
                                }

                            }, true);

                        }

                        for (int i = 0; i < rsData.size(); i++) {
                            FriendBean profileHeadInfo = rsData.get(i);
                            MembersAdapter.MemberInfo memberInfo = new MembersAdapter.MemberInfo(profileHeadInfo);
                            String sortString = "#";

                            //汉字转换成拼音
                            String pinyin = CharacterParser.getInstance().getSelling(profileHeadInfo.getName());
                            if (!TextUtils.isEmpty(profileHeadInfo.getAlias())) {
                                pinyin = CharacterParser.getInstance().getSelling(profileHeadInfo.getAlias());
                            }
                            if (pinyin != null) {
                                if (pinyin.length() > 0) {
                                    sortString = pinyin.substring(0, 1).toUpperCase();
                                }
                            }


                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                memberInfo.setLetter(sortString.toUpperCase());
                            } else {
                                memberInfo.setLetter("#");
                            }
                            mAllMemberList.add(memberInfo);
                        }
                        Collections.sort(mAllMemberList, MembersAdapter.PinyinComparator.getInstance());
                        mAdapter.setData(mAllMemberList);
                        mAdapter.notifyDataSetChanged();
                    }
    //                mRsData = result.RsData;
    //                mFriendRvAdapter.setDatas(mRsData);
            });

            createGroupViewModel.getFriendList(NetConstant.SKIP, NetConstant.TAKE);

        }

        /**
         * 处理创建结果
         *
         * @param
         */
        private void processCreateResult(String groupId) {
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
            TextMessage myTextMessage = TextMessage.obtain("欢迎加入" + createGroupName + "群聊");

            /* 生成 Message 对象。
             * "7127" 为目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
             * Conversation.ConversationType.PRIVATE 为私聊会话类型，根据需要，也可以传入其它会话类型，如群组。
             */
            Message myMessage = Message.obtain(groupId, Conversation.ConversationType.GROUP, myTextMessage);

            /**
             * <p>发送消息。
             * 通过 {@link IRongCallback.ISendMessageCallback}
             * 中的方法回调发送的消息状态及消息体。</p>
             *
             * @param message     将要发送的消息体。
             * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
             *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
             *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
             * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link PushNotificationMessage#getPushData()} 方法获取。
             * @param callback    发送消息的回调，参考 {@link IRongCallback.ISendMessageCallback}。
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
                    if (mList.size() != 0) {
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
            for (FriendBean info : mList) {
                integers.add(info.getUID());
            }
            groupDataReq.setUIDs(integers);
            integers.add(0, Integer.valueOf(IMManager.getInstance().getCurrentId()));

            createGroupName = RongUserInfoManager.getInstance().getUserInfo(IMManager.getInstance().getCurrentId()).getName();
            if (createGroupName.length() <= 10) {
                for (FriendBean info : mList) {
                    createGroupName = createGroupName + " " + info.getName();
                    if (createGroupName.length() > 10) {
                        break;
                    }
                }
            }

            groupDataReq.setTitle(createGroupName);

            createGroupViewModel.createGroup(groupDataReq);

        }


        public void onEventMainThread(SelectFriendEvent event) {
            if (!mList.contains(event.bean)) {
                mList.add(event.bean);
                mTvComplete.setText("创建" + mList.size());
            } else {
                mList.remove(event.bean);
                if (mList.size() == 0) {
                    mTvComplete.setText("创建");
                } else {
                    mTvComplete.setText("创建" + mList.size());
                }
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
        }


        public void onEventMainThread(ContactsItemClickEvent event) {
            MembersAdapter.MemberInfo memberInfo = mAllMemberList.get(event.position);
            FriendBean userInfo = memberInfo.userInfo;
            if (!mList.contains(userInfo)) {
                mList.add(userInfo);
                mTvComplete.setText("创建" + mList.size());
            } else {
                mList.remove(userInfo);
                if (mList.size() == 0) {
                    mTvComplete.setText("创建");
                } else {
                    mTvComplete.setText("创建" + mList.size());
                }
            }
            mTagAdapter.notifyDataChanged();
        }
    }

    public static class SettingActivity extends BaseActivity {
        @BindView(R.id.siv_info)
        SettingItemView mSivInfo;
        @BindView(R.id.siv_notification)
        SettingItemView mSivNotification;
        @BindView(R.id.siv_hobby)
        SettingItemView mSivHobby;
        @BindView(R.id.siv_contact)
        SettingItemView mSivContact;
        @BindView(R.id.siv_modify_pwd)
        SettingItemView mSivModifyPwd;
        @BindView(R.id.siv_company)
        SettingItemView mSivCompany;
        @BindView(R.id.siv_clear)
        SettingItemView mSivClear;
        @BindView(R.id.siv_logout)
        SettingItemView mSivLogout;

        private CommonDialog mLogoutDialog;
        private CommonDialog mClearCacheDialog;
        private UserInfoViewModel mUserInfoViewModel;


        protected int getLayoutId() {
            return R.layout.activity_setting;
        }
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
            mUserInfoViewModel.getHasSetPasswordResult().observe(this,result->{
                if(result.RsCode==3){
                    SPUtils.setHasPassword(mContext,result.getRsData());
                }
            });
            mUserInfoViewModel.hasSetPassword();

            mUserInfoViewModel.getLogoutResult().observe(this,resource->{
                if (resource.status == Status.SUCCESS) {
                    dismissLoadingDialog(new Runnable() {
                        @Override
                        public void run() {
                            //退出到login
                            readyGo(LoginActivity.class);
                            EventBus.getDefault().post(new LogoutEvent());
                            finish();
                        }
                    });

                } else if (resource.status == Status.LOADING) {
                    showLoadingDialog("");
                } else {
                    dismissLoadingDialog(new Runnable() {
                        @Override
                        public void run() {
                            showToast(resource.message);
                        }
                    });
                }
            });
        }

        @OnClick({R.id.siv_info, R.id.siv_notification, R.id.siv_hobby, R.id.siv_contact, R.id.siv_modify_pwd,
                R.id.siv_company, R.id.siv_clear, R.id.siv_logout})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.siv_info:
                    readyGo(SettingPersonInfoActivity.class);
                    break;
                case R.id.siv_notification:
                    readyGo(SettingNotificationActivity.class);
                    break;
                case R.id.siv_hobby:
                    break;
                case R.id.siv_contact:
                    readyGo(VipActivity.class);
                    break;
                case R.id.siv_modify_pwd:
                    if (!SPUtils.getHasPassword(mContext)) {
                        readyGo(SettingPwdActivity.class);
                    } else {
                        readyGo(ModifyPwdActivity.class);
                    }
                    break;
                case R.id.siv_company:
                    readyGo(ContactCompanyActivity.class);
                    break;
                case R.id.siv_clear:
                    showClearDialog();
                    break;
                case R.id.siv_logout:
                    logout();
                    break;
            }
        }

        private void showClearDialog() {
            if(mClearCacheDialog==null) {
                mClearCacheDialog = new ClearCacheDialog.Builder()
                        .setTitleText(R.string.seal_set_account_dialog_clear_cache_title)
                        .setContentMessage(getString(R.string.seal_set_account_dialog_clear_cache_message))
                        .setButtonText(R.string.common_clear, R.string.common_cancel)
                        .build();
            }
            mClearCacheDialog.show(getSupportFragmentManager(), "clear_cache");
        }


        private void logout() {
            if(mLogoutDialog==null) {
                mLogoutDialog = new CommonDialog.Builder()
                        .setTitleText(R.string.dialog_logout_title)
                        .setContentMessage(getString(R.string.dialog_logout_content))
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                mUserInfoViewModel.logout();
                            }

                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
            }
            mLogoutDialog.show(getSupportFragmentManager(), "logout");
        }
    }
}
