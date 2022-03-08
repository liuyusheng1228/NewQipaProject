package com.qipa.newboxproject.ui.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.data.model.bean.DailyTaskBean
import com.qipa.newboxproject.app.weight.dialog.CommonDialog
import com.qipa.newboxproject.app.weight.dialog.CommonDialog.OnClickBottomListener



class DailyTaskListAdapter(data: ArrayList<DailyTaskBean>) :
    BaseQuickAdapter<DailyTaskBean, BaseViewHolder>(R.layout.item_dail_task, data) {


    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: DailyTaskBean) {
        holder.setText(R.id.tv_show_daiy_title,""+item.daily_title)
        holder.setText(R.id.txt_day_rise_text,""+item.daily_value)
        holder.setText(R.id.btn_dail_take,""+item.daily_btn_value)
        holder.getView<ImageView>(R.id.iv_icon_yiwen).setOnClickListener {
            val dialog = CommonDialog(context)
            dialog.setMessage("这是一个自定义Dialog。")
                .setImageResId(R.mipmap.ic_launcher) //                .setTitle("系统提示")
                .setSingle(true).
                setOnClickBottomListener(object : OnClickBottomListener {


                    override fun onPositiveClick(inputmsg: String) {
                        dialog.dismiss()
                    }

                    override fun onNegtiveClick() {
                        dialog.dismiss()
                    }
                }).show()
        }
    }

}