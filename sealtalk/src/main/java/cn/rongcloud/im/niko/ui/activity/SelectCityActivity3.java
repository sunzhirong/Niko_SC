package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;

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
import cn.rongcloud.im.niko.ui.adapter.SelectCityRvAdpter;
import cn.rongcloud.im.niko.utils.ToastUtils;
import io.rong.eventbus.EventBus;

public class SelectCityActivity3 extends BaseActivity {
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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final List<AreaBean.ProvinceBean.CityBean> list = (List<AreaBean.ProvinceBean.CityBean>) bundle.getSerializable("bean");
            String countryCode = bundle.getString("country_code");
            String countryName = bundle.getString("country_name");
            String stateCode = bundle.getString("state_code");
            String stateName = bundle.getString("state_name");
            SelectCityRvAdpter selectCityRvAdpter = new SelectCityRvAdpter(mContext, list);
            mRvCity.setLayoutManager(new LinearLayoutManager(mContext));
            mRvCity.setAdapter(selectCityRvAdpter);
            selectCityRvAdpter.setOnItemClickListener(new BaseRvAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    ToastUtils.showToast("无下级，提交");
                    EventBus.getDefault().post(new CitySelectEvent(
                            countryName + stateName + list.get(position).getCity_name(),
                            countryCode,
                            stateCode,
                            list.get(position).getCity_code()
                    ));
                    finish();

                }
            });
        }
    }
}
