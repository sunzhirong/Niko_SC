package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.NicknameColorSelectEvent;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.NicknameRvAdapter;
import cn.rongcloud.im.niko.ui.adapter.models.VIPConfigBean;
import cn.rongcloud.im.niko.utils.ToastUtils;
import io.rong.eventbus.EventBus;

public class SelectNickNameColorActivity extends BaseActivity {
    @BindView(R.id.rv_nickname)
    RecyclerView mRvNickname;


    //    private VipViewModel mVipViewModel;
    private NicknameRvAdapter mNicknameRvAdapter;
    private List<VIPConfigBean> mRsData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_nickname_color;
    }

    private void initView() {
        EventBus.getDefault().register(this);
        mRvNickname.setLayoutManager(new LinearLayoutManager(mContext));
        mNicknameRvAdapter = new NicknameRvAdapter(mContext, new ArrayList<>());
        mRvNickname.setAdapter(mNicknameRvAdapter);

//        initViewModel();
    }

//    private void initViewModel() {
//        mVipViewModel = ViewModelProviders.of(this).get(VipViewModel.class);
//        mVipViewModel.getUpdateProfile().observe(this, profileInfoResult -> {
//            if (profileInfoResult.RsCode == 3) {
//                finish();
//            }
//        });
//
//
//        mVipViewModel.getVipConfigResult().observe(this, result -> {
//            if (result.RsCode == 3) {
//                mRsData = result.RsData;
//                mNicknameRvAdapter.setDatas(mRsData);
//
//            }
//        });
//
//        mVipViewModel.vipConfigInfo();
//    }

    public void onEventMainThread(NicknameColorSelectEvent event) {
        for (VIPConfigBean bean : mRsData) {
            bean.setSelect(false);
        }
        mRsData.get(event.position).setSelect(event.selected);
        mNicknameRvAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_submit)
    public void onViewClicked() {
        VIPConfigBean selectBean = null;
        for (VIPConfigBean bean : mRsData) {
            if (bean.isSelect()) {
                selectBean = bean;
                break;
            }
        }
        if (selectBean == null) {
            ToastUtils.showToast("请选择色号");
        } else {
//            mVipViewModel.updateProfile(3,"NameColor",selectBean.getNameColor());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
