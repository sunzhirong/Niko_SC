package cn.rongcloud.im.niko.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.db.model.FriendShipInfo;
import cn.rongcloud.im.niko.model.GroupMember;
import cn.rongcloud.im.niko.ui.adapter.SelectContactAdapter;
import cn.rongcloud.im.niko.ui.adapter.models.CheckType;
import cn.rongcloud.im.niko.ui.adapter.models.CheckableContactModel;
import cn.rongcloud.im.niko.ui.adapter.models.ContactModel;
import cn.rongcloud.im.niko.ui.interfaces.OnCheckContactClickListener;
import cn.rongcloud.im.niko.viewmodel.SelectBaseViewModel;
import cn.rongcloud.im.niko.utils.log.SLog;

import static cn.rongcloud.im.niko.common.IntentExtra.LIST_ALREADY_CHECKED_USER_ID_LIST;
import static cn.rongcloud.im.niko.common.IntentExtra.LIST_ALREADY_CHECKED_GROUP_ID_LIST;
import static cn.rongcloud.im.niko.common.IntentExtra.LIST_CAN_NOT_CHECK_ID_LIST;
import static cn.rongcloud.im.niko.common.IntentExtra.LIST_EXCLUDE_ID_LIST;

public class SelectBaseFragment extends BaseContactFragment implements OnCheckContactClickListener {
    private static final String TAG = "SelectBaseFragment";
    private SelectContactAdapter adapter;
    private SelectBaseViewModel viewModel;
    protected ArrayList<String> uncheckableInitIdList;//初始化 不能选的列表
    protected ArrayList<String> excludeInitIdList; // 初始化 从列表中排除
    protected ArrayList<String> checkedInitIdList;//初始化 已经选择的列表
    protected ArrayList<String> checkedInitGroupList;//初始化 已经选择群组的列表


    private boolean canSelect;

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
        if(adapter==null){return;}
        adapter.setCanSelect(canSelect);
        adapter.notifyDataSetChanged();
    }
    public SelectBaseFragment() {

    }


    @Override
    protected void onInitView(Bundle savedInstanceState, Intent intent) {
        super.onInitView(savedInstanceState, intent);
        uncheckableInitIdList = intent.getStringArrayListExtra(LIST_CAN_NOT_CHECK_ID_LIST); //不可选的列表
        excludeInitIdList = intent.getStringArrayListExtra(LIST_EXCLUDE_ID_LIST); // 不在列表中
        adapter = getAdapter();
        adapter.setCanSelect(canSelect);
        recyclerView.setAdapter(adapter);
        viewModel = getViewModel();
        if (viewModel == null) return;
        checkedInitIdList = intent.getStringArrayListExtra(LIST_ALREADY_CHECKED_USER_ID_LIST);    //已经选择的列表
        checkedInitGroupList = intent.getStringArrayListExtra(LIST_ALREADY_CHECKED_GROUP_ID_LIST); //已经选择的群组
        viewModel.getFriendShipLiveData().observe(this, observable);
        viewModel.getGroupFriendsLiveData().observe(this, observable);
        viewModel.getExcludeGroupLiveData().observe(this, observable);
        viewModel.getAllGroupMemberLiveData().observe(this, observable);
        onLoadData(viewModel);
    }

    protected SelectContactAdapter getAdapter() {
        return new SelectContactAdapter(this);
    }

    private Observer observable = new Observer<List<ContactModel>>() {

        @Override
        public void onChanged(List<ContactModel> models) {
            SLog.i(TAG, "onChanged()");
            adapter.setData(models);
            onDataShowed();
        }
    };

    /**
     * 数据已经展示
     */
    protected void onDataShowed() {

    }

    protected void onLoadData(SelectBaseViewModel viewModel) {
        viewModel.loadFriendShip(uncheckableInitIdList, checkedInitIdList, checkedInitGroupList);
    }

    /**
     * 搜索好友
     *
     * @param keyword
     */
    public void searchFriend(String keyword) {
        viewModel.searchFriend(keyword);
    }

    public String getTitle() {
        return getString(R.string.seal_select_group_member);
    }

    protected SelectBaseViewModel getViewModel() {
        return null;
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        recyclerView.scrollToPosition(viewModel.getIndex(s));
    }

    public ArrayList<String> getCheckedList() {
        return viewModel.getCheckedList();
    }

    public ArrayList<String> getCheckedInitGroupList() {
        return checkedInitGroupList;//直接返回了，现在选人列表不支持选群组，无变化
    }

    @Override
    public void onContactContactClick(CheckableContactModel contactModel) {
        viewModel.onItemClicked(contactModel);
        adapter.notifyDataSetChanged();
        refreshFlowlayout(contactModel);
    }

    private void refreshFlowlayout(CheckableContactModel contactModel) {

        boolean isCheck = false;
        if (contactModel.getCheckType() == CheckType.CHECKED) {
            isCheck = true;
        }
        String name = "";

        if (contactModel.getBean() instanceof FriendShipInfo) {
            if (!TextUtils.isEmpty(((FriendShipInfo) contactModel.getBean()).getDisplayName())) {
                name = ((FriendShipInfo) contactModel.getBean()).getDisplayName();
            } else {
                name = ((FriendShipInfo) contactModel.getBean()).getUser().getNickname();
            }


        } else if (contactModel.getBean() instanceof GroupMember) {
            name = ((GroupMember) contactModel.getBean()).getName();
        }
        if (TextUtils.isEmpty(name)) {
            return;
        }
        if (isCheck) {
            mList.add(name);
        } else {
            mList.remove(name);
        }
        mTagAdapter.notifyDataChanged();
        flowlayout.setVisibility(mList.size() == 0 ? View.GONE : View.VISIBLE);

    }

    public ArrayList<String> getCheckedFriendList() {
        return viewModel.getCheckedFriendIdList();
    }

    public ArrayList<String> getCheckedGroupList() {
        return viewModel.getCheckedGroupList();
    }

}
