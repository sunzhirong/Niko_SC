package cn.rongcloud.im.niko.ui.activity;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.widget.TitleBar;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;

public class MyLikedActivity extends BaseActivity {
//    @BindView(R.id.title_bar)
//    TitleBar mTitleBar;
//    @BindView(R.id.rv_my_liked)
//    RecyclerView mRvMyLiked;
//    private TextView mTitleBarTvRight;
//    private UserInfoViewModel mUserInfoViewModel;
//    private MyLikedRvAdapter mMyLikedRvAdapter;
//    private List<MyLikeBean> mList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_liked;
    }

//    @Override
//    private void initView() {
//        EventBus.getDefault().register(this);
//        mTitleBarTvRight = mTitleBar.getTitleBarTvRight();
//        mTitleBarTvRight.setText("发送");
//
//
//        mRvMyLiked.setLayoutManager(new LinearLayoutManager(mContext));
//        mMyLikedRvAdapter = new MyLikedRvAdapter(mContext, new ArrayList<>());
//        mRvMyLiked.setAdapter(mMyLikedRvAdapter);
//
//        initViewModel();
//    }
//
//    private void initViewModel() {
//        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
//        mUserInfoViewModel.getMyLiekListResult().observe(this, result -> {
//            if(result.RsCode== NetConstant.REQUEST_SUCCESS_CODE){
//                mMyLikedRvAdapter.setDatas(result.getRsData());
//            }
//        });
//        mUserInfoViewModel.myLiekList(NetConstant.SKIP, NetConstant.TAKE);
//    }
//
//    public void onEventMainThread(SelectMyLikeEvent event) {
//        if(!mList.contains(event.bean)) {
//            mList.add(event.bean);
//            mTitleBarTvRight.setText("发送" + mList.size());
//        }else {
//            mList.remove(event.bean);
//            if(mList.size()==0){
//                mTitleBarTvRight.setText("发送");
//            }else {
//                mTitleBarTvRight.setText("发送" + mList.size());
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
}
