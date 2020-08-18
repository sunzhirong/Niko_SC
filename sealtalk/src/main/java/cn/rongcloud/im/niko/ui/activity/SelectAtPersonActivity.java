package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.event.SelectAtEvent;
import cn.rongcloud.im.niko.event.SelectCompleteEvent;
import cn.rongcloud.im.niko.model.niko.FollowBean;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.FollowRvAdapter;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;

public class SelectAtPersonActivity extends BaseActivity {
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_complete)
    TextView mTvComplete;
    @BindView(R.id.fl)
    TagFlowLayout mFl;
    @BindView(R.id.rv_follow)
    RecyclerView mRvFollow;
    @BindView(R.id.et_search)
    EditText mEtSearch;
    private TagAdapter<FollowBean> mTagAdapter;
    private List<FollowBean> mList;
    private UserInfoViewModel mUserInfoViewModel;
    private FollowRvAdapter mFollowRvAdapter;
    private List<FollowBean> mRsData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_at_person;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        mList = new ArrayList<>();
        mTagAdapter = new TagAdapter<FollowBean>(mList) {
            @Override
            public View getView(FlowLayout parent, int position, FollowBean bean) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.flowlayout_tv,
                        mFl, false);
                tv.setText("@"+bean.getName());
                return tv;
            }
        };
        mFl.setAdapter(mTagAdapter);


        mRvFollow.setLayoutManager(new LinearLayoutManager(mContext));
        mFollowRvAdapter = new FollowRvAdapter(mContext, new ArrayList<>());
        mRvFollow.setAdapter(mFollowRvAdapter);

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
                    mFollowRvAdapter.setDatas(mRsData);
                }else {
                    List<FollowBean> infos = new ArrayList<>();
                    for(FollowBean info :mRsData){
                        if(info.getName().contains(content)){
                            infos.add(info);
                        }
                    }
                    mFollowRvAdapter.setDatas(infos);
                }
            }
        });

        initViewModel();

    }

    private void initViewModel() {
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserInfoViewModel.getFollowListResult().observe(this, result -> {
            if (result.RsCode == NetConstant.REQUEST_SUCCESS_CODE) {
                mRsData = result.getRsData();
                mFollowRvAdapter.setDatas(mRsData);
            }
        });

        mUserInfoViewModel.getFollowList(NetConstant.SKIP, NetConstant.TAKE);
    }


    @OnClick({R.id.tv_cancel, R.id.tv_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_complete:
                complete();
                break;
        }
    }

    private void complete() {
        EventBus.getDefault().post(new SelectCompleteEvent(mList));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(SelectAtEvent event) {
        if(!mList.contains(event.bean)) {
            mList.add(event.bean);
            mTagAdapter.notifyDataChanged();
            mTvComplete.setText("完成" + mList.size());
        }else {
            mList.remove(event.bean);
            mTagAdapter.notifyDataChanged();
            if(mList.size()==0){
                mTvComplete.setText("完成");
            }else {
                mTvComplete.setText("完成" + mList.size());
            }
        }
    }




}
