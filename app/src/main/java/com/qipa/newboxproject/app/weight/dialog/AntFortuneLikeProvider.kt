package com.qipa.newboxproject.app.weight.dialog

import com.github.gzuliyujiang.wheelpicker.contract.LinkageProvider
import java.util.*


/**
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/6/7 12:22
 */
class AntFortuneLikeProvider : LinkageProvider {
    override fun firstLevelVisible(): Boolean {
        return true
    }

    override fun thirdLevelVisible(): Boolean {
        return false
    }

    override fun provideFirstData(): List<String?> {
        return Arrays.asList("每周", "每两周", "每月", "每日")
    }

    override fun linkageSecondData(firstIndex: Int): List<String?> {
        when (firstIndex) {
            0, 1 -> return Arrays.asList("周一", "周二", "周三", "周四", "周五")
            2 -> {
                val data: MutableList<String?> = ArrayList()
                var i = 1
                while (i <= 28) {
                    data.add(i.toString() + "日")
                    i++
                }
                return data
            }
        }
        return ArrayList()
    }

    override fun linkageThirdData(firstIndex: Int, secondIndex: Int): List<String?> {
        return ArrayList()
    }

    override fun findFirstIndex(firstValue: Any): Int {
        return 0
    }

    override fun findSecondIndex(firstIndex: Int, secondValue: Any): Int {
        return 0
    }

    override fun findThirdIndex(firstIndex: Int, secondIndex: Int, thirdValue: Any): Int {
        return 0
    }
}