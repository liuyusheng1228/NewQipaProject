package com.qipa.newboxproject.app.weight.dialog

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity

import com.github.gzuliyujiang.dialog.DialogConfig

import com.github.gzuliyujiang.dialog.DialogStyle

import com.github.gzuliyujiang.wheelpicker.LinkagePicker
import com.qipa.newboxproject.R


/**
 * @since 2021/6/7 12:20
 */
class AntFortuneLikePicker(activity: Activity) :
    LinkagePicker(activity, R.style.DialogTheme_Sheet) {
    private var lastDialogStyle = 0
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        lastDialogStyle = DialogConfig.getDialogStyle()
        DialogConfig.setDialogStyle(DialogStyle.Default)
        setWidth(activity.resources.displayMetrics.widthPixels)
        setGravity(Gravity.BOTTOM)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        DialogConfig.setDialogStyle(lastDialogStyle)
    }

    override fun initData() {
        super.initData()
        setBackgroundColor(-0x1)
        cancelView.text = "取消"
        cancelView.textSize = 16f
        cancelView.setTextColor(-0xff7e01)
        okView.setTextColor(-0xff7e01)
        okView.text = "确定"
        okView.textSize = 16f
        titleView.setTextColor(-0xcccccd)
        titleView.text = "定投周期"
        titleView.textSize = 16f
        wheelLayout.setData(AntFortuneLikeProvider())
        wheelLayout.setAtmosphericEnabled(true)
        wheelLayout.setVisibleItemCount(7)
        wheelLayout.setCyclicEnabled(false)
        wheelLayout.setIndicatorEnabled(true)
        wheelLayout.setIndicatorColor(-0x222223)
        wheelLayout.setIndicatorSize(
            ((contentView.resources.displayMetrics.density * 1) as Int).toFloat()

        )
        wheelLayout.setTextColor(-0x666667)
        wheelLayout.setSelectedTextColor(-0xcccccd)
        wheelLayout.setCurtainEnabled(false)
        wheelLayout.setCurvedEnabled(false)
    }
}