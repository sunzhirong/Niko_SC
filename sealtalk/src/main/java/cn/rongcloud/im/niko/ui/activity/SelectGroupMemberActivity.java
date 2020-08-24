package cn.rongcloud.im.niko.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.ui.fragment.SelectGroupMemberMultiFragment;
import cn.rongcloud.im.niko.ui.fragment.SelectMultiFriendFragment;
import cn.rongcloud.im.niko.ui.view.SealTitleBar;

import static cn.rongcloud.im.niko.common.IntentExtra.STR_TARGET_ID;

/**
 * 选择当前群组 groupId 内的人
 */
public class SelectGroupMemberActivity extends SelectMultiFriendsActivity {
    private String groupId;
    private boolean canSelect;
    private SelectGroupMemberMultiFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        groupId = getIntent().getStringExtra(STR_TARGET_ID);
        canSelect = getIntent().getBooleanExtra(IntentExtra.CAN_SELECT,false);
        super.onCreate(savedInstanceState);
        getTitleBar().getTvRight().setVisibility(View.GONE);
        if(canSelect){
            getTitleBar().getTvRight().setText("删除");
            tvManager.setVisibility(View.VISIBLE);
            tvManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvManager.setVisibility(View.GONE);
                    getTitleBar().getTvRight().setVisibility(View.VISIBLE);
                    getTitleBar().getBtnLeft().setVisibility(View.GONE);
                    tvCancel.setVisibility(View.VISIBLE);
                    mFragment.setCanSelect(true);
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTitleBar().getTvRight().setVisibility(View.GONE);
                    getTitleBar().getBtnLeft().setVisibility(View.VISIBLE);
                    tvManager.setVisibility(View.VISIBLE);
                    mFragment.setCanSelect(false);
                    tvCancel.setVisibility(View.GONE);
                    cancelAndClearSelect();
                    setRightTvText(0);
                    //清除流式布局
                    mFragment.clearAllCheck();
                }
            });
        }
        getTitleBar().setTitle("群聊成员");
    }

    protected void setRightTvText(int selectCount) {
        if(selectCount==0){
            getTitleConfirmTv().setText("删除");
        }else {
            getTitleConfirmTv().setText("删除"+selectCount);
        }
    }

    @Override
    protected SelectMultiFriendFragment getSelectMultiFriendFragment() {
        mFragment = new SelectGroupMemberMultiFragment();
        mFragment.setGroupId(groupId);
//        fragment.setCanSelect(canSelect);
        return mFragment;
    }

    @Override
    protected void onConfirmClicked(ArrayList<String> selectIds, ArrayList<String> selectGroups) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(IntentExtra.LIST_STR_ID_LIST, selectIds);
        setResult(RESULT_OK, intent);
        finish();
    }
}
