package cn.rongcloud.im.niko.ui.fragment;

import androidx.lifecycle.ViewModelProviders;

import cn.rongcloud.im.niko.viewmodel.ForwardRecentListViewModel;

/**
 *  转发最近联系人列表
 */
public class ForwordRecentListFragment extends CommonListBaseFragment {

    @Override
    protected boolean isUseSideBar() {
        return false;
    }


    /**
     * 创建 viewmodel
     * @return
     */
    @Override
    protected ForwardRecentListViewModel createViewModel() {
        ForwardRecentListViewModel forwardFragmentViewModel = ViewModelProviders.of(this).get(ForwardRecentListViewModel.class);
        return forwardFragmentViewModel;
    }

}
