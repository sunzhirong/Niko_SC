package cn.rongcloud.im.niko.ui.fragment;

import androidx.lifecycle.ViewModelProviders;

import cn.rongcloud.im.niko.viewmodel.CommonListBaseViewModel;
import cn.rongcloud.im.niko.viewmodel.ForwardSelectContactViewModel;

public class ForwardSelectContactFragment extends CommonListBaseFragment {
    @Override
    protected CommonListBaseViewModel createViewModel() {
        return ViewModelProviders.of(this).get(ForwardSelectContactViewModel.class);
    }

    @Override
    protected boolean isUseSideBar() {
        return true;
    }
}
