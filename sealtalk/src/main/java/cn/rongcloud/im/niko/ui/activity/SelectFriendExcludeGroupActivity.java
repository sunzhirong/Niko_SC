package cn.rongcloud.im.niko.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.ui.fragment.SelectFriendsExcludeGroupFragment;
import cn.rongcloud.im.niko.ui.fragment.SelectMultiFriendFragment;

import static cn.rongcloud.im.niko.common.IntentExtra.STR_TARGET_ID;

/**
 * 除了当前群组 groupId 之外的人
 * 点击邀请
 */
public class SelectFriendExcludeGroupActivity extends SelectMultiFriendsActivity {
    private String groupId;
    private boolean canSelect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        groupId = getIntent().getStringExtra(STR_TARGET_ID);
        canSelect = getIntent().getBooleanExtra(IntentExtra.CAN_SELECT,false);
        super.onCreate(savedInstanceState);
        getTitleBar().setTitle("选择好友");
    }

    @Override
    protected SelectMultiFriendFragment getSelectMultiFriendFragment() {
        SelectFriendsExcludeGroupFragment fragment = new SelectFriendsExcludeGroupFragment();
        fragment.setGroupId(groupId);
        fragment.setCanSelect(canSelect);
        return fragment;
    }

    protected void setRightTvText(int selectCount) {
        if(selectCount==0){
            getTitleConfirmTv().setText("确认");
        }else {
            getTitleConfirmTv().setText("确认"+selectCount);
        }
    }

    @Override
    protected void onConfirmClicked(ArrayList<String> selectIds, ArrayList<String> selectGroups) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(IntentExtra.LIST_STR_ID_LIST, selectIds);
        setResult(RESULT_OK, intent);
        finish();
    }
}
