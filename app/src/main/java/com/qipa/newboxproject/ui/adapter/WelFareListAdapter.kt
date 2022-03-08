package com.qipa.newboxproject.ui.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.data.model.bean.WelFareListBean


class WelFareListAdapter(data: ArrayList<WelFareListBean>) :
    BaseQuickAdapter<WelFareListBean, BaseViewHolder>(R.layout.item_welfare_card, data) {

    private val multiWelFareListAdapter : MultiWelFareListAdapter by lazy { MultiWelFareListAdapter(
        arrayListOf()) }

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: WelFareListBean) {
        val welfare_card_recylerview = holder.getView<RecyclerView>(R.id.welfare_card_recylerview)
        welfare_card_recylerview.init(LinearLayoutManager(context),multiWelFareListAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f), false))
        }
        multiWelFareListAdapter.setList(item.childList)
        multiWelFareListAdapter.notifyDataSetChanged()

    }

}