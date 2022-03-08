package com.qipa.newboxproject.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.R.drawable.*
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.data.model.bean.TagBean

class FlowAdapter (data : MutableList<TagBean>) :  BaseQuickAdapter<TagBean, BaseViewHolder>(R.layout.adapter_game_list_label, data) {
    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }
    override fun convert(holder: BaseViewHolder, item: TagBean) {
        holder.setText(R.id.text_label,item.name)
        item.let {
            when(it.type){
                1 ->{
                    holder.setBackgroundResource(R.id.text_label,bg_button_dd9341_round_2dp)
                }
                2 ->{
                    holder.setBackgroundResource(R.id.text_label, bg_button_2_round_2dp)
                    holder.setTextColor(R.id.text_label,context.resources.getColor(R.color.teal_200))
                }
                else -> {
                    holder.setBackgroundResource(R.id.text_label, bg_button_3_round_2dp)
                    holder.setTextColor(R.id.text_label,context.resources.getColor(R.color.light_blue_900))
                }
            }
        }
    }
}