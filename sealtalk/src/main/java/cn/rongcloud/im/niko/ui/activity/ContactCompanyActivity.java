package cn.rongcloud.im.niko.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;

import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.utils.ToastUtils;

public class ContactCompanyActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_company;
    }


    @OnClick(R.id.tv_copy)
    public void onViewClicked() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(getString(R.string.company_email));
        ToastUtils.showToast(R.string.copy_success);
    }
}
