package cn.rongcloud.im.niko.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.ui.widget.SideBar;

public class BaseContactFragment extends BaseFragment implements SideBar.OnTouchingLetterChangedListener {
    private static String TAG = "BaseContactFragment";
    protected RecyclerView recyclerView;
    protected SideBar sideBar;
    protected TagFlowLayout flowlayout;
    protected List<String> mList;
    protected TagAdapter<String> mTagAdapter;
    private TextView textView;


    public void clearAllCheck(){
        if(mList!=null) {
            mList.clear();
        }
        if(mTagAdapter!=null){
            mTagAdapter.notifyDataChanged();
        }
        if(flowlayout!=null){
            flowlayout.setVisibility(View.GONE);
        }

    }



    @Override
    protected int getLayoutResId() {
        return R.layout.main_fragment_contacts_list;
    }

    @Override
    protected void onInitView(Bundle savedInstanceState, Intent intent) {
        recyclerView = findView(R.id.rv_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sideBar = findView(R.id.sv_sidebar);
        sideBar.setOnTouchingLetterChangedListener(this);
        textView = findView(R.id.tv_group_dialog);
        sideBar.setTextView(textView);
        flowlayout =  findView(R.id.fl);
        mList = new ArrayList<>();
        mTagAdapter = new TagAdapter<String>(mList) {
            @Override
            public View getView(FlowLayout parent, int position, String name) {
                TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flowlayout_tv,
                        flowlayout, false);
                tv.setText("@"+name);
                return tv;
            }
        };
        flowlayout.setAdapter(mTagAdapter);
    }

    /**
     * 右侧字母点击
     *
     * @param s
     */
    @Override
    public void onTouchingLetterChanged(String s) {

    }

}
