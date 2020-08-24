package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.ui.fragment.SelectMultiFriendFragment;
import cn.rongcloud.im.niko.ui.interfaces.OnSelectCountChangeListener;
import cn.rongcloud.im.niko.ui.view.SealTitleBar;
import cn.rongcloud.im.niko.viewmodel.SelectMultiViewModel;

/**
 * 不要直接请求此 Activity
 */
public class SelectMultiFriendsActivity extends SelectBaseActivity implements View.OnClickListener, OnSelectCountChangeListener {
    private SelectMultiFriendFragment selectMultiFriendFragment;
    private SelectMultiViewModel selectMultiViewModel;
    private TextView titleConfirmTv;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SealTitleBar sealTitleBar = getTitleBar();
        titleConfirmTv = sealTitleBar.getTvRight();
        titleConfirmTv.setText(R.string.seal_select_confirm);
        titleConfirmTv.setOnClickListener(this);
        selectMultiFriendFragment = getSelectMultiFriendFragment();
        selectMultiFriendFragment.setOnSelectCountChangeListener(this);
        sealTitleBar.setTitle(getString(R.string.seal_select_group_member));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_fragment_container, selectMultiFriendFragment);
        transaction.commit();

        initViewModel();
    }

    protected boolean keyboardEnable(){
        return false;
    }

    private void initViewModel() {
        selectMultiViewModel = ViewModelProviders.of(this).get(SelectMultiViewModel.class);

        selectMultiViewModel.getSelectedCount().observe(this, selectCount -> {
            if (selectCount > 0) {
                setConfirmEnable(true);
            } else if (confirmEnabledWhenNoChecked()) {
                setConfirmEnable(true);
            } else {
                setConfirmEnable(false);
            }
            setRightTvText(selectCount);
        });
    }

    protected void setRightTvText(int selectCount) {

    }

    protected void cancelAndClearSelect(){
        selectMultiViewModel.cancelAllCheck();
    }

    /**
     * 设置可以点击确定
     *
     * @param isEnable
     */
    private void setConfirmEnable(boolean isEnable) {
        if (isEnable) {
            titleConfirmTv.setClickable(true);
            titleConfirmTv.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            titleConfirmTv.setClickable(false);
            titleConfirmTv.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }


    protected SelectMultiFriendFragment getSelectMultiFriendFragment() {
        return new SelectMultiFriendFragment();
    }

    /**
     * @param v 右上角点击
     */
    @Override
    public void onClick(View v) {
        onConfirmClicked(selectMultiFriendFragment.getCheckedList(), selectMultiFriendFragment.getCheckedInitGroupList());
    }

    /**
     * 右下角点击事件
     */
    @Override
    protected void onConfirmClick() {
        onConfirmClicked(selectMultiFriendFragment.getCheckedList(), selectMultiFriendFragment.getCheckedInitGroupList());
    }


    @Override
    public void onSelectCountChange(int groupCount, int userCount) {
    }

    public ArrayList<String> getCheckedFriendIds() {
        return selectMultiFriendFragment.getCheckedFriendList();
    }

    public ArrayList<String> getCheckedGroupIds() {
        return selectMultiFriendFragment.getCheckedGroupList();
    }

    /**
     * 是否在没有选择时可以点击确定,默认为未选择时不可点击
     * 重写此方法以开启状态在未选择时可点击确认
     *
     * @return
     */
    public boolean confirmEnabledWhenNoChecked() {
        return false;
    }

    @Override
    protected boolean isSearchable() {
        return true;
    }

    @Override
    public void onSearch(String keyword) {
        if(selectMultiFriendFragment != null){
            if(TextUtils.isEmpty(keyword)){
                selectMultiFriendFragment.loadAll();
            }else {
                selectMultiFriendFragment.search(keyword);
            }
        }
    }

    public TextView getTitleConfirmTv() {
        return titleConfirmTv;
    }
}
