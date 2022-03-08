package com.qipa.newboxproject.app.util

import android.os.Debug
import android.util.Log

object MemoryUtil {
    /**
     * 对比性能参数的时间以及内存
     */
    fun printMemoryMsg(type: String) {
        Log.e("MemoryUtil", type + "_currentTimeMillis:" + System.currentTimeMillis())
        val memoryInfo: Debug.MemoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        val totalPss: Int = memoryInfo.getTotalPss() //返回的是KB
        Log.e("MemoryUtil", type + "_totalPss:" + totalPss)
    }
}