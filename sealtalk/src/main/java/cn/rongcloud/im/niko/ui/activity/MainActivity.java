package cn.rongcloud.im.niko.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.event.CommentHeightEvent;
import cn.rongcloud.im.niko.event.ItemCommentEvent;
import cn.rongcloud.im.niko.event.LogoutEvent;
import cn.rongcloud.im.niko.event.ShowMoreEvent;
import cn.rongcloud.im.niko.sp.SPUtils;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.fragment.ChatFragment;
import cn.rongcloud.im.niko.ui.view.MainBottomTabGroupView;
import cn.rongcloud.im.niko.ui.view.MainBottomTabItem;
import cn.rongcloud.im.niko.ui.widget.ChatTipsPop;
import cn.rongcloud.im.niko.ui.widget.DragPointView;
import cn.rongcloud.im.niko.ui.widget.TabGroupView;
import cn.rongcloud.im.niko.ui.widget.TabItem;
import cn.rongcloud.im.niko.viewmodel.MainViewModel;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;

public class MainActivity extends BaseActivity  {
    public static final String PARAMS_TAB_INDEX = "tab_index";
    private static final int REQUEST_START_CHAT = 0;
    private static final int REQUEST_START_GROUP = 1;
    private static final String TAG = "MainActivity";
    private FrameLayout mFlOrderLayout;
    private Fragment mSelectFragment;
    private boolean showPop;
    private ChatTipsPop mPop;
    private MainBottomTabGroupView tabGroupView;
    public MainViewModel mainViewModel;

    private View mask;
    private int mMaskMarginHeight;
    private int mCurrentItem;

    private boolean canShowUnread = false;

    /**
     * tab 项枚举
     */
    public enum Tab {
        /**
         * 聊天
         */
        CHAT(0),
        /**
         * 联系人
         */
        CONTACTS(1),
        /**
         * 发现
         */
        FIND(2),
        /**
         * 我的
         */
        ME(3);

        private int value;

        Tab(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Tab getType(int value) {
            for (Tab type : Tab.values()) {
                if (value == type.getValue()) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * tabs 的图片资源
     */
    private int[] tabImageRes = new int[]{
            R.drawable.seal_ic_my,
            R.drawable.seal_ic_two,
            R.drawable.seal_ic_chat,
            R.drawable.seal_ic_me
    };

    /**
     * 各个 Fragment 界面
     */
    private List<Fragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_main);
        initView();
        initViewModel();
        clearBadgeStatu();
    }

    //清除华为的角标
    private void clearBadgeStatu() {
        if (Build.MANUFACTURER.equalsIgnoreCase("HUAWEI")) {
            try {
                String packageName = getPackageName();
                String launchClassName = getPackageManager()
                        .getLaunchIntentForPackage(packageName)
                        .getComponent().getClassName();
                Bundle bundle = new Bundle();//需要存储的数据
                bundle.putString("package", packageName);//包名
                bundle.putString("class", launchClassName);//启动的Activity完整名称
                bundle.putInt("badgenumber", 0);//未读信息条数清空
                getContentResolver().call(
                        Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                        "change_badge", null, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 初始化布局
     */
    protected void initView() {

        EventBus.getDefault().register(this);
        mFlOrderLayout = findViewById(R.id.fl_order_layout);
        if (mFlOrderLayout.getForeground() != null) {
            mFlOrderLayout.getForeground().setAlpha(0);
        }
        int tabIndex = getIntent().getIntExtra(PARAMS_TAB_INDEX, Tab.CHAT.getValue());

        // title
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SealSearchActivity.class);
                startActivity(intent);
            }
        });


        // 底部按钮
        tabGroupView = findViewById(R.id.tg_bottom_tabs);

        // 初始化底部 tabs
        initTabs();
        // 初始化 fragment 的 viewpager
        initFragments();

        // 设置当前的选项为聊天界面
        tabGroupView.setSelected(tabIndex);

        initViewModel();
        clearBadgeStatu();

        mask = findViewById(R.id.mask);
        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mask.setVisibility(View.GONE);
                Fragment fragment = fragments.get(2);
                if(fragment instanceof ChatFragment) {
                    ChatFragment mChatFragment = (ChatFragment) fragment;
                    if(mChatFragment.getCommentFragment()!=null){
                        if(mChatFragment.getCommentFragment().getLlInput()!=null&&mChatFragment.getCommentFragment().getLlInput().getVisibility()==View.VISIBLE){
                            mChatFragment.getCommentFragment().getLlInput().setVisibility(View.GONE);
                            mChatFragment.getCommentFragment().hideInput();
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化 Tabs
     */
    private void initTabs() {
        // 初始化 tab
        List<TabItem> items = new ArrayList<>();
        String[] stringArray = getResources().getStringArray(R.array.tab_names);

        for (Tab tab : Tab.values()) {
            TabItem tabItem = new TabItem();
            tabItem.id = tab.getValue();
            tabItem.text = stringArray[tab.getValue()];
            tabItem.drawable = tabImageRes[tab.getValue()];
            items.add(tabItem);
        }

        tabGroupView.initView(items, new TabGroupView.OnTabSelectedListener() {
            @Override
            public void onSelected(View view, TabItem item) {
                // 当点击 tab 的后， 也要切换到正确的 fragment 页面
//                int currentItem = vpFragmentContainer.getCurrentItem();
//                if (currentItem != item.id) {
//                    // 切换布局
//                    vpFragmentContainer.setCurrentItem(item.id);
//                    // 如果是我的页面， 则隐藏红点
//                    if (item.id == Tab.ME.getValue()) {
//                        ((MainBottomTabItem) tabGroupView.getView(Tab.ME.getValue())).setRedVisibility(View.GONE);
//                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//                        startActivity(intent);
//                    }
//                }
                if (item.id!=mCurrentItem&&item.id == Tab.ME.getValue()) {
                    mCurrentItem = item.id;
                    Intent intent = new Intent(MainActivity.this, TitleAndSearchBaseActivity.SettingActivity.class);
                    startActivity(intent);
                    return;
                }
                if(item.id == Tab.ME.getValue()){
                    return;
                }
                changeFragment(fragments.get(item.id));
                mCurrentItem = item.id;

            }
        });

//        tabGroupView.setOnTabDoubleClickListener(new MainBottomTabGroupView.OnTabDoubleClickListener() {
//            @Override
//            public void onDoubleClick(TabItem item, View view) {
//                // 双击定位到某一个未读消息位置
//                if (item.id == Tab.CHAT.getValue()) {
//                    MainConversationListFragment fragment = (MainConversationListFragment) fragments.get(Tab.CHAT.getValue());
//                    fragment.focusUnreadItem();
//                }
//            }
//        });

        // 未读数拖拽
        ((MainBottomTabItem) tabGroupView.getView(Tab.CHAT.getValue())).setTabUnReadNumDragListener(new DragPointView.OnDragListencer() {

            @Override
            public void onDragOut() {
                ((MainBottomTabItem) tabGroupView.getView(Tab.CHAT.getValue())).setNumVisibility(View.GONE);
                showToast(getString(R.string.seal_main_toast_unread_clear_success));
                clearUnreadStatus();
            }
        });
//        ((MainBottomTabItem) tabGroupView.getView(Tab.CHAT.getValue())).setNumVisibility(View.VISIBLE);
    }


    /**
     * 初始化 initFragmentViewPager
     */
    private void initFragments() {


        fragments.add(new Fragment());
        fragments.add(new Fragment());
        fragments.add(new ChatFragment());
        fragments.add(new Fragment());


        for (Fragment fragment : fragments) {
            addFragment(fragment);
        }
        mSelectFragment = fragments.get(0);
        changeFragment(fragments.get(0));

    }

    private void addFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_container, fragment);
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    private void changeFragment(Fragment lastFragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.hide(mSelectFragment);
        fragmentTransaction.show(lastFragment);
        fragmentTransaction.commit();
        mSelectFragment = lastFragment;
    }


    /**
     * 初始化ViewModel
     */
    private void initViewModel() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // 未读消息
        mainViewModel.getUnReadNum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                SPUtils.setUnreadMsgCount(mContext,count);
                if(canShowUnread) {
                    MainBottomTabItem chatTab = (MainBottomTabItem) tabGroupView.getView(Tab.FIND.getValue());
                    if (count == 0) {
                        chatTab.setNumVisibility(View.GONE);
                    } else if (count > 0 && count < 100) {
                        chatTab.setNumVisibility(View.VISIBLE);
                        chatTab.setNum(String.valueOf(count));
                    } else {
                        chatTab.setVisibility(View.VISIBLE);
                        chatTab.setNum(getString(R.string.seal_main_chat_tab_more_read_message));
                    }
                }
            }
        });

        // 新朋友数量
        mainViewModel.getNewFriendNum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                MainBottomTabItem chatTab = tabGroupView.getView(Tab.CONTACTS.getValue());
                if (count > 0) {
                    chatTab.setRedVisibility(View.VISIBLE);
                } else {
                    chatTab.setRedVisibility(View.GONE);
                }
            }
        });

        mainViewModel.getPrivateChatLiveData().observe(this, new Observer<FriendShipInfo>() {
            @Override
            public void onChanged(FriendShipInfo friendShipInfo) {
                RongIM.getInstance().startPrivateChat(MainActivity.this,
                        friendShipInfo.getUser().getId(),
                        TextUtils.isEmpty(friendShipInfo.getDisplayName()) ?
                                friendShipInfo.getUser().getNickname() : friendShipInfo.getDisplayName());
            }
        });

    }


    /**
     * 清理未读消息状态
     */
    private void clearUnreadStatus() {
        if (mainViewModel != null) {
            mainViewModel.clearMessageUnreadStatus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_START_CHAT:
                    mainViewModel.startPrivateChat(data.getStringExtra(IntentExtra.STR_TARGET_ID));
                    break;
                default:
                    break;
            }
        }


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ShowMoreEvent event) {
        if (event.show) {
            mFlOrderLayout.getForeground().setAlpha(153);
        } else {
            mFlOrderLayout.getForeground().setAlpha(0);
        }
    }

    private void showTipsPop() {
        mPop = new ChatTipsPop(this);
        int friendsRequestCount = SPUtils.getFriendsRequestCount(mContext);
        int unreadMsgCount = SPUtils.getUnreadMsgCount(mContext);
        mPop.setLeftRightCount(friendsRequestCount,unreadMsgCount);
        if(friendsRequestCount==0&&unreadMsgCount==0){
            return;
        }
//        View tabsView = tabGroupView.getView(Tab.FIND.getValue()).findViewById(R.id.iv_tab_img);
        View tabsView = tabGroupView.getView(Tab.FIND.getValue()).findViewById(R.id.ll_tab);
        mPop.showUp(tabsView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!showPop) {
            showTipsPop();
            showPop = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    canShowUnread = true;
                    if (isDestroyed()) {
                        return;
                    }
                    if (mPop != null && mPop.isShowing()) {
                        mPop.dismiss();
                        if (tabGroupView != null) {
                            int unreadMsgCount = SPUtils.getUnreadMsgCount(mContext);
                            if(unreadMsgCount!=0) {
                                ((MainBottomTabItem) tabGroupView.getView(Tab.FIND.getValue())).setNum(String.valueOf(unreadMsgCount));
                                ((MainBottomTabItem) tabGroupView.getView(Tab.FIND.getValue())).setNumVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }, 3000);
        }
    }

    protected void onKeyBoardChange(boolean isPopup){
        tabGroupView.setVisibility(isPopup?View.GONE:View.VISIBLE);
    }

    public void onEventMainThread(ItemCommentEvent event) {
        mask.setVisibility(View.VISIBLE);
    }
    public void onEventMainThread(LogoutEvent event) {
        finish();
    }

    public void onEventMainThread(CommentHeightEvent event) {
        if(event.height==-1){
            //表示发布评论成功
            mask.setVisibility(View.GONE);
        }
        if(mask.getVisibility()==View.GONE){return;}
        if(mMaskMarginHeight == event.height){return;}
        mMaskMarginHeight = event.height;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.bottomMargin = mMaskMarginHeight;
        mask.setLayoutParams(layoutParams);
    }

}
