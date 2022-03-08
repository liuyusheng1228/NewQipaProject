package com.qipa.newboxproject.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil


class QuesitionListAdapter(data: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_question_value, data) {

    private var index = -1
    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.getView<TextView>(R.id.tv_ques_value).setText(""+item)

        //获取布局中item的位置
        var  position : Int = holder.getLayoutPosition()
        //如果前面点击事件传递进来的点击的位置index和布局item的相同，则设置checked为true，判断进行变色
        var checked : Boolean  = position == index
        if(checked){
            holder.getView<TextView>(R.id.tv_ques_value).setBackgroundResource(R.drawable.bg_main_button_green_round_2dp)
            holder.getView<TextView>(R.id.tv_ques_value).setTextColor(context.resources.getColor(R.color.white))
        }else{
            holder.getView<TextView>(R.id.tv_ques_value).setBackgroundResource(R.drawable.bg_button_2_round_2dp)
            holder.getView<TextView>(R.id.tv_ques_value).setTextColor(context.resources.getColor(R.color.black))
        }

    }

    fun setSelection(pos : Int){
        index = pos;
        //一定要刷新数据
        notifyDataSetChanged();
    }


}