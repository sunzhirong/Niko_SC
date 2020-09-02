package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.CitySelectEvent;
import cn.rongcloud.im.niko.model.AreaBean;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.BaseRvAdapter;
import cn.rongcloud.im.niko.ui.adapter.SelectCountryRvAdpter;
import cn.rongcloud.im.niko.utils.FileUtils;
import cn.rongcloud.im.niko.utils.ToastUtils;
import io.rong.eventbus.EventBus;

public class SelectCityActivity1 extends BaseActivity {
    @BindView(R.id.rv_city)
    RecyclerView mRvCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_city;
    }

    private void initView() {
        EventBus.getDefault().register(this);

        String json = FileUtils.getJson(this, "data.json");
        List<AreaBean> areaBeans = JSONObject.parseArray(json, AreaBean.class);
        SelectCountryRvAdpter selectCountryRvAdpter = new SelectCountryRvAdpter(mContext, areaBeans);
        mRvCity.setLayoutManager(new LinearLayoutManager(mContext));
        mRvCity.setAdapter(selectCountryRvAdpter);
        selectCountryRvAdpter.setOnItemClickListener(new BaseRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AreaBean areaBean = areaBeans.get(position);
                if (areaBean.getProvince().size() != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", (Serializable) areaBean.getProvince());
                    bundle.putString("country_code", areaBean.getState_code());
                    bundle.putString("country_name", areaBean.getState_name());
                    readyGo(SelectCityActivity2.class, bundle);
                } else {
                    EventBus.getDefault().post(new CitySelectEvent(
                            areaBeans.get(position).getState_name(),
                            areaBeans.get(position).getState_code(),
                            "",
                            ""
                    ));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(CitySelectEvent event) {
        finish();
    }

}
