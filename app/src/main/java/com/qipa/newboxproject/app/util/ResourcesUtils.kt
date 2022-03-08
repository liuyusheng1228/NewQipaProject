package com.qipa.newboxproject.app.util

import android.content.Context
import com.mob.tools.utils.ResHelper

object ResourcesUtils {
    fun getBitmapRes(context: Context?, resName: String?): Int {
        return ResHelper.getBitmapRes(context, resName)
    }
}