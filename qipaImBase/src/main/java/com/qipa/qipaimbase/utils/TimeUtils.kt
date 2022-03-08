package com.qipa.qipaimbase.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private const val TIME_FORMAT_DAY = "yyyy/MM/dd"
    private const val TIME_FORMAT_HOUR = "HH:mm"
    private const val YESTERDY = -1
    private val dateLocal = ThreadLocal<SimpleDateFormat?>()
    private val hourLocal = ThreadLocal<SimpleDateFormat?>()

    //获取聊天中的时间
    fun getTimeContent(curData: Long, preData: Long): String? {
        if (preData == 0L) {
            return getTimeFormat(curData)
        }
        return if (curData - preData >= 1000 * 60 * 5) {
            getTimeFormat(curData)
        } else null
    }

    fun getTimeFormat(time: Long): String {
        return if (isToday(time)) {
            val d = Date(time)
            if (d.hours < 12) {
                String.format("上午%s", hourFormat!!.format(d))
            } else {
                String.format("下午%s", hourFormat!!.format(d))
            }
        } else if (isYesterday(time)) {
            "昨天"
        } else {
            dateFormat!!.format(Date(time))
        }
    }

    //判断选择的日期是否是今天
    fun isToday(time: Long): Boolean {
        return isThisTime(time, TIME_FORMAT_DAY)
    }

    private fun isThisTime(time: Long, pattern: String): Boolean {
        val date = Date(time)
        val param = dateFormat!!.format(date) //参数时间
        val now = dateFormat!!.format(Date()) //当前时间
        return if (param == now) {
            true
        } else false
    }

    /**
     * 读取日期的格式
     */
    val dateFormat: SimpleDateFormat?
        get() {
            if (null == dateLocal.get()) {
                dateLocal.set(SimpleDateFormat(TIME_FORMAT_DAY, Locale.CHINA))
            }
            return dateLocal.get()
        }
    val hourFormat: SimpleDateFormat?
        get() {
            if (null == hourLocal.get()) {
                hourLocal.set(SimpleDateFormat(TIME_FORMAT_HOUR, Locale.CHINA))
            }
            return hourLocal.get()
        }

    fun isYesterday(time: Long): Boolean {
        val format = dateFormat!!.format(Date(time))
        val pre = Calendar.getInstance()
        val predate = Date(System.currentTimeMillis())
        pre.time = predate
        val cal = Calendar.getInstance()
        var date: Date? = null
        date = try {
            dateFormat!!.parse(format)
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
        cal.time = date
        if (cal[Calendar.YEAR] == pre[Calendar.YEAR]) {
            val diffDay = (cal[Calendar.DAY_OF_YEAR]
                    - pre[Calendar.DAY_OF_YEAR])
            if (diffDay == YESTERDY) {
                return true
            }
        }
        return false
    }
}
