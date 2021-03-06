package com.yline.address.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yline.address.R;
import com.yline.address.data.RegionDataManager;
import com.yline.view.recycler.adapter.AbstractRecyclerAdapter;
import com.yline.view.recycler.callback.OnRecyclerItemClickListener;
import com.yline.view.recycler.holder.RecyclerViewHolder;

import java.util.List;

/**
 * @author yline 2020-04-09 -- 08:38
 */
public class RegionView extends LinearLayout {
    private static final int TAB_ONE = 10;
    private static final int TAB_TWO = 20;
    private static final int TAB_THREE = 30;
    private static final int TAB_FOUR = 40;

    private int mTabValue;
    private RegionTabView mRegionTabView;

    private OnLastItemClickListener mOnLastItemClickListener;
    private RegionAdapter mRegionAdapter;
    private LinearLayoutManager mRecycleManager;
    private String checkProvince;
    private String checkCity;
    private String checkArea;
    private String checkStreet;

    public RegionView(Context context) {
        this(context, null);
    }

    public RegionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_region, this, true);

        findViews();
        bindListeners();
    }

    private void findViews() {
        mRegionTabView = findViewById(R.id.view_region_tab);
        RecyclerView recycleView = findViewById(R.id.recycleView);

        mRecycleManager = new LinearLayoutManager(getContext());
        mRegionAdapter = new RegionAdapter(getContext());
        recycleView.setLayoutManager(mRecycleManager);
        recycleView.setAdapter(mRegionAdapter);
    }

    private void bindListeners() {
        // 点击 第一个tab
        mRegionTabView.setOnOneClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegionDataManager.getProvinceList(new RegionDataManager.OnDataResultCallback() {
                    @Override
                    public void onResult(List<String> dataList) {
                        mTabValue = TAB_ONE;
                        mRegionAdapter.setDataList(dataList, checkProvince);

                        // 定位到已选项，若不存在，则置顶
                        int index = dataList.indexOf(checkProvince);
                        scrollToPosition(index);
                    }
                });
            }
        });

        // 点击 第二个tab
        mRegionTabView.setOnTwoClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegionDataManager.getCityList(checkProvince, new RegionDataManager.OnDataResultCallback() {
                    @Override
                    public void onResult(List<String> dataList) {
                        mTabValue = TAB_TWO;
                        mRegionAdapter.setDataList(dataList, checkCity);

                        // 定位到已选项，若不存在，则置顶
                        int index = dataList.indexOf(checkCity);
                        scrollToPosition(index);
                    }
                });
            }
        });

        // 点击 第三个tab
        mRegionTabView.setOnThreeClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegionDataManager.getAreaList(checkProvince, checkCity, new RegionDataManager.OnDataResultCallback() {
                    @Override
                    public void onResult(List<String> dataList) {
                        mTabValue = TAB_THREE;
                        mRegionAdapter.setDataList(dataList, checkArea);

                        // 定位到已选项，若不存在，则置顶
                        int index = dataList.indexOf(checkArea);
                        scrollToPosition(index);
                    }
                });
            }
        });

        // 点击 第四个tab
        mRegionTabView.setOnFourClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegionDataManager.getStreetList(checkProvince, checkCity, checkArea, new RegionDataManager.OnDataResultCallback() {
                    @Override
                    public void onResult(List<String> dataList) {
                        mTabValue = TAB_FOUR;
                        mRegionAdapter.setDataList(dataList, checkStreet);

                        // 定位到已选项，若不存在，则置顶
                        int index = dataList.indexOf(checkStreet);
                        scrollToPosition(index);
                    }
                });
            }
        });

        mRegionAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener<String>() {
            @Override
            public void onItemClick(RecyclerViewHolder viewHolder, String value, int position) {
                if (mTabValue == TAB_ONE) {
                    checkProvince = value;
                    mTabValue = TAB_TWO;

                    checkCity = null;
                    checkArea = null;
                    checkStreet = null;
                    updateData();
                } else if (mTabValue == TAB_TWO) {
                    checkCity = value;
                    mTabValue = TAB_THREE;

                    checkArea = null;
                    checkStreet = null;
                    updateData();
                } else if (mTabValue == TAB_THREE) {
                    checkArea = value;
                    mTabValue = TAB_FOUR;

                    checkStreet = null;
                    updateData();
                } else {
                    checkStreet = value;
                    mTabValue = TAB_FOUR;

                    // 选择四层结束
                    if (null != mOnLastItemClickListener) {
                        mOnLastItemClickListener.onItemClick(checkProvince, checkCity, checkArea, checkStreet);
                    }
                }
            }
        });
    }

    private void scrollToPosition(int position) {
        if (position == -1) {
            mRecycleManager.scrollToPositionWithOffset(0, 0);
        } else {
            mRecycleManager.scrollToPositionWithOffset(position, 0);
        }
    }

    /**
     * 这个是设置初始数据
     */
    public void setData(String province, String city, final String area, final String street) {
        this.checkProvince = province;
        this.checkCity = city;
        this.checkArea = area;
        this.checkStreet = street;

        // 依据province、city、area、street的值确定tabValue的值
        mTabValue = TAB_ONE;
        if (!TextUtils.isEmpty(checkProvince)) {
            mTabValue = TAB_TWO;
        }
        if (!TextUtils.isEmpty(checkCity)) {
            mTabValue = TAB_THREE;
        }
        if (!TextUtils.isEmpty(checkArea)) {
            mTabValue = TAB_THREE;
        }
        if (!TextUtils.isEmpty(checkStreet)) {
            mTabValue = TAB_FOUR;
        }

        updateData();
    }

    /**
     * 依据TabValue，刷新RecyclerView的数据
     * 这里还需要调整调整
     */
    private void updateData() {
        if (mTabValue == TAB_ONE) {
            // 省份，还未选择 或 已经选择
            RegionDataManager.getProvinceList(new RegionDataManager.OnDataResultCallback() {
                @Override
                public void onResult(List<String> dataList) {
                    mRegionTabView.selectOneState(checkProvince);
                    mRegionAdapter.setDataList(dataList, checkProvince);

                    // 定位到已选项
                    int index = dataList.indexOf(checkProvince);
                    scrollToPosition(index);
                }
            });
        } else if (mTabValue == TAB_TWO) {
            // 城市，还未选择 或 已经选择
            RegionDataManager.getCityList(checkProvince, new RegionDataManager.OnDataResultCallback() {
                @Override
                public void onResult(List<String> dataList) {
                    mRegionTabView.selectTwoState(checkProvince, checkCity);
                    mRegionAdapter.setDataList(dataList, checkCity);

                    // 定位到已选项
                    int index = dataList.indexOf(checkCity);
                    scrollToPosition(index);
                }
            });
        } else if (mTabValue == TAB_THREE) {
            // 地区，还未选择 或 已经选择
            RegionDataManager.getAreaList(checkProvince, checkCity, new RegionDataManager.OnDataResultCallback() {
                @Override
                public void onResult(List<String> dataList) {
                    if (null == dataList || dataList.isEmpty()) {
                        // 已经结束了，就不再选择了
                        mRegionTabView.selectTwoState(checkProvince, checkCity);
                    } else {
                        mRegionTabView.selectThreeState(checkProvince, checkCity, checkArea);
                        mRegionAdapter.setDataList(dataList, checkArea);

                        // 定位到已选项
                        int index = dataList.indexOf(checkArea);
                        scrollToPosition(index);
                    }
                }
            });
        } else {
            RegionDataManager.getStreetList(checkProvince, checkCity, checkArea, new RegionDataManager.OnDataResultCallback() {
                @Override
                public void onResult(List<String> dataList) {
                    if (null == dataList || dataList.isEmpty()) {
                        // 已经结束了，就不再选择了
                        mRegionTabView.selectThreeState(checkProvince, checkCity, checkArea);

                        // 选择三层就结束了
                        if (null != mOnLastItemClickListener) {
                            mOnLastItemClickListener.onItemClick(checkProvince, checkCity, checkArea, null);
                        }
                    } else {
                        mRegionTabView.selectFourState(checkProvince, checkCity, checkArea, checkStreet);
                        mRegionAdapter.setDataList(dataList, checkStreet);

                        // 定位到已选项
                        int index = dataList.indexOf(checkArea);
                        scrollToPosition(index);
                    }
                }
            });
        }
    }

    /**
     * 点击❌
     */
    public void setOnCloseClickListener(OnClickListener listener) {
        findViewById(R.id.view_region_close).setOnClickListener(listener);
    }

    public void setOnLastItemClickListener(OnLastItemClickListener listener) {
        this.mOnLastItemClickListener = listener;
    }

    public interface OnLastItemClickListener {
        /**
         * 最后的选择
         *
         * @param province 省
         * @param city     市
         * @param area     区/县
         * @param street   可能为null
         */
        void onItemClick(String province, String city, String area, String street);
    }

    private static class RegionAdapter extends AbstractRecyclerAdapter<String> {
        private String mSelectData;
        private final int redColor;
        private final int blackColor;

        private OnRecyclerItemClickListener<String> mOnRecyclerItemClickListener;

        private RegionAdapter(Context context) {
            redColor = ContextCompat.getColor(context, R.color.ff5000);
            blackColor = ContextCompat.getColor(context, R.color.v666666);
        }

        @Override
        public int getItemRes() {
            return R.layout.item_region_view;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewHolder viewHolder, int i) {
            final String value = get(i);

            // 更新数据
            if (value.equals(mSelectData)) {
                viewHolder.setText(R.id.item_region_view_name, value)
                        .setTextColor(redColor);
                viewHolder.get(R.id.item_region_view_select).setVisibility(View.VISIBLE);
            } else {
                viewHolder.setText(R.id.item_region_view_name, value)
                        .setTextColor(blackColor);
                viewHolder.get(R.id.item_region_view_select).setVisibility(View.GONE);
            }

            // 处理点击事件
            viewHolder.getItemView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int newPosition = viewHolder.getAdapterPosition();
                    final String newValue = value;

                    // 更新旧的item
                    final int oldSelectPosition = indexOf(mSelectData);
                    if (oldSelectPosition != -1) {
                        notifyItemChanged(oldSelectPosition);
                    }

                    // 更新新的item
                    mSelectData = newValue;
                    notifyItemChanged(newPosition);

                    if (null != mOnRecyclerItemClickListener) {
                        mOnRecyclerItemClickListener.onItemClick(viewHolder, newValue, newPosition);
                    }
                }
            });
        }

        public void setDataList(List<String> list, String selectData) {
            this.mSelectData = selectData;
            this.setDataList(list, true);
        }

        public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<String> listener) {
            this.mOnRecyclerItemClickListener = listener;
        }
    }
}
