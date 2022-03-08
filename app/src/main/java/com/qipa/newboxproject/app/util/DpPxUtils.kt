package com.qipa.newboxproject.app.util

import android.content.Context


object DpPxUtils {
    fun dp2Px(context: Context, dp: Int): Int {
        val displayMetrics = context.resources
            .displayMetrics
        val density = displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    fun px2Dp(context: Context, px: Int): Int {
        val displayMetrics = context.resources
            .displayMetrics
        val density = displayMetrics.density
        return (px / density + 0.5f).toInt()
    }
}
