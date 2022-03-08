package com.qipa.newboxproject.ui.adapter

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil

class MenuLeftAdapter (data : MutableList<String>) :  BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_menu_type, data) {
    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.txt_menu_name,item)
        val view_select = holder.getView<View>(R.id.view_select)
        val txt_menu_name = holder.getView<TextView>(R.id.txt_menu_name)
        val li = holder.getView<RelativeLayout>(R.id.li)
           if (clickId.equals(getItemPosition(item))) {
                view_select.visibility = View.VISIBLE
                txt_menu_name.setTextColor(context.resources.getColor(R.color.black))
                li.setBackgroundResource(R.color.white)
            }else{
                view_select.visibility = View.GONE
                txt_menu_name.setTextColor(context.resources.getColor(R.color.backgrounGrey))
                li.setBackgroundResource(R.color.windowBackground)
            }

    }

    private var clickId : Int = 0

    fun setClickPosition(clickIds : Int){
        this.clickId = clickIds
    }



}