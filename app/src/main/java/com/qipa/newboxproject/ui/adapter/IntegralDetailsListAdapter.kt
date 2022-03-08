package com.qipa.newboxproject.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.data.model.bean.IntegralDetailsBean


class IntegralDetailsListAdapter(data: MutableList<IntegralDetailsBean>) :
    BaseQuickAdapter<IntegralDetailsBean, BaseViewHolder>(R.layout.item_integral_details, data) {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: IntegralDetailsBean) {


    }

}