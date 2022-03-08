package com.qipa.newboxproject.ui.adapter

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil


class PlatformCurrencyListAdapter(data: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_recharge_num_value, data) {

    private var index = -1
    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.getView<TextView>(R.id.tv_recharge_value).setText(""+item)
        holder.getView<TextView>(R.id.tv_recharge_value_money).setText("/¥"+item.toInt()/10)
        //获取布局中item的位置
        var  position : Int = holder.getLayoutPosition()
        //如果前面点击事件传递进来的点击的位置index和布局item的相同，则设置checked为true，判断进行变色
        var checked : Boolean  = position == index
        if(checked){
            holder.getView<RelativeLayout>(R.id.rel_show_select).visibility = View.VISIBLE
            holder.getView<RelativeLayout>(R.id.lin_recharge).setBackgroundResource(R.drawable.shape_btn_grey_miancolor_bg_4)
            holder.getView<TextView>(R.id.tv_recharge_value).setTextColor(context.resources.getColor(R.color.mainColor))
            holder.getView<TextView>(R.id.tv_recharge_value_money).setTextColor(context.resources.getColor(R.color.mainColor))
        }else{
            holder.getView<RelativeLayout>(R.id.rel_show_select).visibility = View.GONE
            holder.getView<RelativeLayout>(R.id.lin_recharge).setBackgroundResource(R.drawable.shape_btn_grey_backgroud_bg_4)
            holder.getView<TextView>(R.id.tv_recharge_value).setTextColor(context.resources.getColor(R.color.black))
            holder.getView<TextView>(R.id.tv_recharge_value_money).setTextColor(context.resources.getColor(R.color.color88))
        }

    }

    fun setSelection(pos : Int){
        index = pos;
        //一定要刷新数据
        notifyDataSetChanged();
    }


}