package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.event.SelectMyLikeEvent;
import cn.rongcloud.im.niko.model.niko.MyLikeBean;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.MyLikedRvAdapter;
import cn.rongcloud.im.niko.ui.widget.TitleBar;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.RichContentMessage;

public class MyLikedActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.rv_my_liked)
    RecyclerView mRvMyLiked;
    private TextView mTitleBarTvRight;
    private UserInfoViewModel mUserInfoViewModel;
    private MyLikedRvAdapter mMyLikedRvAdapter;
    private List<MyLikeBean> mList = new ArrayList<>();
    private String mTargetId;
    private boolean mIsPrivate;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_liked;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTargetId = getIntent().getStringExtra("targetId");
        mIsPrivate = getIntent().getBooleanExtra("isPrivate", false);
        initView();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        mTitleBarTvRight = mTitleBar.getTitleBarTvRight();
        mTitleBarTvRight.setText("发送");
        mTitleBarTvRight.setOnClickListener(v -> {
            sendMyLike();
        });
        mRvMyLiked.setLayoutManager(new LinearLayoutManager(mContext));
        mMyLikedRvAdapter = new MyLikedRvAdapter(mContext, new ArrayList<>());
        mRvMyLiked.setAdapter(mMyLikedRvAdapter);

        initViewModel();
    }

    private void sendMyLike() {
        for(MyLikeBean bean:mList){
            sendMyLike(bean);
        }

        finish();
    }

    private void sendMyLike(MyLikeBean bean) {
        /**
         * 生成RichContentMessage对象。
         */
        RichContentMessage richContentMessage = RichContentMessage.obtain(
                bean.getTitle(), bean.getMsg(), GlideImageLoaderUtil.getScString(bean.getMdGuid()));

        //"9517" 为目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
        //Conversation.ConversationType.PRIVATE 为会话类型。
        Message myMessage = Message.obtain(mTargetId, mIsPrivate? Conversation.ConversationType.PRIVATE:Conversation.ConversationType.GROUP, richContentMessage);
        /**
         * 根据会话类型，发送位置消息
         */
//        RongIMClient.getInstance().sendLocationMessage(myMessage, "", "", new IRongCallback.ISendMessageCallback(){
        RongIMClient.getInstance().sendMessage(myMessage, "", "", new IRongCallback.ISendMessageCallback(){
            @Override
            public void onAttached(Message message) {
                Log.d("MyLikedActivity","onAttached");
            }

            @Override
            public void onSuccess(Message message) {
                Log.d("MyLikedActivity","onSuccess");

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                Log.d("MyLikedActivity","onError"+errorCode.getValue());

            }
        });
    }

    private void initViewModel() {
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserInfoViewModel.getMyLiekListResult().observe(this, result -> {
            if(result.RsCode== NetConstant.REQUEST_SUCCESS_CODE){
                if(result.getRsData().size()==0){
                    mRvMyLiked.setVisibility(View.GONE);
                    return;
                }
                mMyLikedRvAdapter.setDatas(result.getRsData());
            }else {
                mRvMyLiked.setVisibility(View.GONE);
            }
        });
        mUserInfoViewModel.myLiekList(NetConstant.SKIP, NetConstant.TAKE);
    }

    public void onEventMainThread(SelectMyLikeEvent event) {
        if(!mList.contains(event.bean)) {
            mList.add(event.bean);
            mTitleBarTvRight.setText("发送" + mList.size());
        }else {
            mList.remove(event.bean);
            if(mList.size()==0){
                mTitleBarTvRight.setText("发送");
            }else {
                mTitleBarTvRight.setText("发送" + mList.size());
            }
        }
        mTitleBarTvRight.setEnabled(mList.size()!=0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
