package com.qipa.newboxproject.app.weight.banner

/**
 * 描述　:
 */

import android.view.View
import com.qipa.newboxproject.R
import com.qipa.newboxproject.data.model.bean.BannerResponse
import com.zhpan.bannerview.BaseBannerAdapter

class HotBannerAdapter : BaseBannerAdapter<BannerResponse, HotBannerViewHolder>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.banner_itemhome
    }

    override fun createViewHolder(itemView: View, viewType: Int): HotBannerViewHolder {
        return HotBannerViewHolder(itemView);
    }

    override fun onBind(
        holder: HotBannerViewHolder?,
        data: BannerResponse?,
        position: Int,
        pageSize: Int
    ) {
        holder?.bindData(data, position, pageSize);
    }


}
