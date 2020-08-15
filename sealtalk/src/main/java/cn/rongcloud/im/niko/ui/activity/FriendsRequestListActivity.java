package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.event.AddFollowCompleteEvent;
import cn.rongcloud.im.niko.event.FollowEvent;
import cn.rongcloud.im.niko.model.niko.FollowRequestInfo;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.FriendsRequestRvAdapter;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;

public class FriendsRequestListActivity extends BaseActivity {
    @BindView(R.id.rv_request)
    RecyclerView mRvRequest;
    private UserInfoViewModel mUserInfoViewModel;
    private ArrayList<FollowRequestInfo> mBeans;
    private int pos;
    private FriendsRequestRvAdapter mFriendsRequestRvAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friends_request;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    protected void initView() {
        EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            mBeans = (ArrayList<FollowRequestInfo>) bundle.getSerializable("beans");
            if(mBeans !=null) {
                mRvRequest.setLayoutManager(new LinearLayoutManager(mContext));
                mFriendsRequestRvAdapter = new FriendsRequestRvAdapter(mContext, mBeans);
                mRvRequest.setAdapter(mFriendsRequestRvAdapter);
            }
        }

        initViewModel();
    }

    private void initViewModel() {
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserInfoViewModel.getAddFollowingsResult().observe(this,result->{
            if(result.RsCode== NetConstant.REQUEST_SUCCESS_CODE) {
                EventBus.getDefault().post(new AddFollowCompleteEvent(mBeans.get(pos).getUID()));
                mBeans.remove(pos);
                mFriendsRequestRvAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onEventMainThread(FollowEvent event) {
        pos  = event.position;
        mUserInfoViewModel.addFollowings(mBeans.get(event.position).getUID());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
