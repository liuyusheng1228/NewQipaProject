package com.qipa.newboxproject.ui.adapter

import android.graphics.Paint
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.data.model.bean.RechargeInterfaceBean


class RechargeInterfaceListAdapter(data: MutableList<RechargeInterfaceBean>) :
    BaseQuickAdapter<RechargeInterfaceBean, BaseViewHolder>(R.layout.item_recharge_interface, data) {

    private var index = -1
    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: RechargeInterfaceBean) {
        holder.getView<TextView>(R.id.tv_money_not_value).getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG )

        //获取布局中item的位置
        var  position : Int = holder.getLayoutPosition()
        //如果前面点击事件传递进来的点击的位置index和布局item的相同，则设置checked为true，判断进行变色
        var checked : Boolean  = position == index
        if(checked){
            holder.getView<RelativeLayout>(R.id.item_recharge_interface).setBackgroundResource(R.drawable.bg_round_green_4dp)
        }else{
            holder.getView<RelativeLayout>(R.id.item_recharge_interface).setBackgroundResource(R.drawable.bg_round_grey_white_4dp)
        }

    }

    fun setSelection(pos : Int){
        index = pos;
        //一定要刷新数据
        notifyDataSetChanged();
    }


}