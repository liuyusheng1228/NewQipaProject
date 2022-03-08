package com.qipa.newboxproject.app.weight.banner

/**
 * 描述　:
 */

import android.view.View
import com.qipa.newboxproject.R
import com.qipa.newboxproject.data.model.bean.BannerResponse
import com.zhpan.bannerview.BaseBannerAdapter

class MemberBannerAdapter : BaseBannerAdapter<BannerResponse, MemberBannerViewHolder>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.banner_itemhome
    }

    override fun createViewHolder(itemView: View, viewType: Int): MemberBannerViewHolder {
        return MemberBannerViewHolder(itemView);
    }

    override fun onBind(
        holder: MemberBannerViewHolder?,
        data: BannerResponse?,
        position: Int,
        pageSize: Int
    ) {
        holder?.bindData(data, position, pageSize);
    }


}
