package com.qipa.newboxproject.ui.adapter

import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil


class MultiWelFareListAdapter(data: ArrayList<String>) :
    BaseDelegateMultiAdapter<String, BaseViewHolder>(data) {
    private val TodayType = 1//文章类型
    private val NewProject = 2
    init {
        setAdapterAnimation(SettingUtil.getListMode())

        setMultiTypeDelegate(object : BaseMultiTypeDelegate<String>() {
            override fun getItemType(data: List<String>, position: Int): Int {
                //根据是否有图片 判断为文章还是项目，好像有点low的感觉。。。我看实体类好像没有相关的字段，就用了这个，也有可能是我没发现
                return  TodayType
            }
        })
        // 第二步，绑定 item 类型
        getMultiTypeDelegate()?.let {
            it.addItemType(TodayType, R.layout.item_welfare_today)
//            it.addItemType(Project, R.layout.item_project)
        }
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        when (holder.itemViewType) {
            TodayType -> {

            }
        }
    }

}