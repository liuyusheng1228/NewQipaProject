package com.qipa.jetpackmvvm.util

import java.lang.Exception
import java.net.URL
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * TimeUtils
 *
 * @author [Trinea](http://www.trinea.cn) 2013-8-24
 */
class DateUtils() {
    // java中怎样计算两个时间如：“21:57”和“08:20”相差的分钟数、小时数 java计算两个时间差小时 分钟 秒 .
    fun timeSubtract() {
        val dfs = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var begin: Date? = null
        var end: Date? = null
        try {
            begin = dfs.parse("2004-01-02 11:30:24")
            end = dfs.parse("2004-03-26 13:31:40")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val between = (end!!.time - begin!!.time) / 1000 // 除以1000是为了转换成秒
        val day1 = between / (24 * 3600)
        val hour1 = between % (24 * 3600) / 3600
        val minute1 = between % 3600 / 60
        val second1 = between % 60
        println(
            "" + day1 + "天" + hour1 + "小时" + minute1 + "分"
                    + second1 + "秒"
        )
    }

    companion object {
        private val calendarLong = 1533081600000L
        private val calendar = "CalendarDay{2018-7-6}"
        @JvmStatic
        fun main(args: Array<String>) {
            println(currentDateBefore30Day)
        }

        fun formatCanlendar(calendar: String): String {
            return calendar.substring(calendar.indexOf("{") + 1, calendar.indexOf("}"))
        }

        val DEFAULT_DATE_FORMAT = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss"
        )
        val DATE_FORMAT_DATE = SimpleDateFormat(
            "yyyy-MM-dd"
        )

        /** 定义常量  */
        val DATE_JFP_STR = "yyyyMM"
        val DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss"
        val DATE_SMALL_STR = "yyyy-MM-dd"
        val DATE_KEY_STR = "yyMMddHHmmss"
        val formatPattern = "yyyy-MM-dd"
        val formatPattern_Short = "yyyyMMdd"
        private val serialVersionUID = 1L

        /**
         * 获取系统时间(格式：yyyyMMddHHmmss)
         * @return String 返回时间
         */
        val nowTime: String
            get() {
                val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                return sdf.format(Calendar.getInstance().time)
            }

        /**
         * 获取系统时间(格式：yyyyMMddHHmmss)
         * @return String 返回时间
         */
        val stringTime: String
            get() {
                val df = SimpleDateFormat("yyyyMMddHHmmss")
                return df.format(Date())
            }

        /**
         * 获取系统时间(格式：yyyyMMddHHmmssSSS)
         * @return String 返回时间
         */
        val stringTimeFull: String
            get() {
                val df = SimpleDateFormat("yyyyMMddHHmmssSSS")
                return df.format(Date())
            }

        /**
         * 判断日期是否属于今天日期(精确到天)
         * @param sDate 日期值
         * @return boolean 返回true表示是，false表示不是
         */
        fun getSysIsToday(sDate: String?): Boolean {
            var falg = false
            try {
                var date: Date? = null
                date = dateFormaterFull.get()?.parse(sDate)
                val today = Date()
                if (date != null) {
                    val nowDate = dateFormater.get()?.format(today)
                    val timeDate = dateFormater.get()?.format(date)
                    if ((nowDate == timeDate)) {
                        falg = true
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                AppLogMessageMgr.e("AppSysDateMgr-->>getSysIsToday", e.message.toString())
            }
            return falg
        }

        private val dateFormater: ThreadLocal<SimpleDateFormat?> =
            object : ThreadLocal<SimpleDateFormat?>() {
                override fun initialValue(): SimpleDateFormat {
                    return SimpleDateFormat("yyyy-MM-dd")
                }
            }
        private val dateFormaterFull: ThreadLocal<SimpleDateFormat?> =
            object : ThreadLocal<SimpleDateFormat?>() {
                override fun initialValue(): SimpleDateFormat {
                    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                }
            }

        /**
         * 检查日期是否有效
         * @param year 年
         * @param month 月
         * @param day 日
         * @return boolean
         */
        fun getDateIsTrue(year: String, month: String, day: String): Boolean {
            try {
                val data = year + month + day
                val simpledateformat = SimpleDateFormat("yyyyMMdd")
                simpledateformat.isLenient = false
                simpledateformat.parse(data)
            } catch (e: ParseException) {
                e.printStackTrace()
                AppLogMessageMgr.e("AppSysDateMgr-->>getDateIsTrue", e.message.toString())
                return false
            }
            return true
        }

        /**
         * 判断两个字符串日期的前后
         * @param strdate1  字符串时间1
         * @param strdate2  字符串时间2
         * @return boolean
         * 日期与时间
         */
        fun getDateIsBefore(strdate1: String?, strdate2: String?): Boolean {
            try {
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                AppLogMessageMgr.i("AppSysDateMgr-->>getDateIsBefore-->>strdate1: ", strdate1)
                AppLogMessageMgr.i("AppSysDateMgr-->>getDateIsBefore-->>strdate2: ", strdate2)
                return df.parse(strdate1).before(df.parse(strdate2))
            } catch (e: ParseException) {
                e.printStackTrace()
                AppLogMessageMgr.e("AppSysDateMgr-->>getDateIsBefore", e.message.toString())
                return false
            }
        }

        /**
         * 判断两个字符串日期的前后
         * @param strdate1  字符串时间1
         * @param strdate2  字符串时间2
         * @return boolean
         */
        fun getDateIsEqual(strdate1: String?, strdate2: String?): Boolean {
            try {
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                return (df.parse(strdate1) == df.parse(strdate2))
            } catch (e: ParseException) {
                e.printStackTrace()
                AppLogMessageMgr.e("AppSysDateMgr-->>getDateIsBefore", e.message.toString())
                return false
            }
        }

        /**
         * 判断两个字符串日期的前后
         * @param Longdate1  字符串时间1
         * @param Longdate2  字符串时间2
         * @return boolean
         */
        fun getDateIsBefore(Longdate1: Long?, Longdate2: Long?): Boolean {
            var Longdate1 = Longdate1
            var Longdate2 = Longdate2
            try {
                AppLogMessageMgr.i(
                    "AppSysDateMgr-->>getDateIsBefore-->>strdate1: ",
                    Longdate1.toString() + ""
                )
                AppLogMessageMgr.i(
                    "AppSysDateMgr-->>getDateIsBefore-->>strdate2: ",
                    Longdate2.toString() + ""
                )
                Longdate1 = Longdate1 ?: 0
                Longdate2 = Longdate2 ?: 0
                return if (Longdate1 > Longdate2) true else false
            } catch (e: Exception) {
                e.printStackTrace()
                AppLogMessageMgr.e("AppSysDateMgr-->>getDateIsBefore", e.message.toString())
                return false
            }
        }

        /**
         * 判断两个时间日期的前后
         * @param date1  日期1
         * @param date2  日期2
         * @return boolean
         */
        fun getDateIsBefore(date1: Date?, date2: Date?): Boolean {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return getDateIsBefore(df.format(date1), df.format(date2))
        }

        /**
         * 得到当前年
         *
         * @return
         */
        val currYear: Int
            get() {
                val cal = Calendar.getInstance()
                return cal[Calendar.YEAR]
            }

        /**
         * 得到当前月份 注意，这里的月份依然是从0开始的
         *
         * @return
         */
        val currMonth: Int
            get() {
                val cal = Calendar.getInstance()
                return cal[Calendar.MONTH]
            }

        /**
         * 得到当前日
         *
         * @return
         */
        val currDay: Int
            get() {
                val cal = Calendar.getInstance()
                return cal[Calendar.DAY_OF_MONTH]
            }

        /**
         * 得到当前星期
         *
         * @return
         */
        val currWeek: Int
            get() {
                val cal = Calendar.getInstance()
                return cal[Calendar.DAY_OF_WEEK]
            }

        /**
         * 得到当前小时
         *
         * @return
         */
        val currHour: Int
            get() {
                val cal = Calendar.getInstance()
                return cal[Calendar.HOUR]
            }

        /**
         * 得到当前分钟
         *
         * @return
         */
        val currMinute: Int
            get() {
                val cal = Calendar.getInstance()
                return cal[Calendar.MINUTE]
            }

        /**
         * 得到当前秒
         *
         * @return
         */
        val currSecond: Int
            get() {
                val cal = Calendar.getInstance()
                return cal[Calendar.SECOND]
            }

        /**
         * Date类型转换到Calendar类型
         *
         * @param date
         * @return
         */
        fun Date2Calendar(date: Date?): Calendar {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal
        }

        /**
         * Calendar类型转换到Date类型
         *
         * @param cal
         * @return
         */
        fun calendar2Date(cal: Calendar): Date {
            return cal.time
        }

        /**
         * Date类型转换到Timestamp类型
         *
         * @param date
         * @return
         */
        fun date2Timestamp(date: Date): Timestamp {
            return Timestamp(date.time)
        }

        /**
         * Calendar类型转换到Timestamp类型
         *
         * @return
         */
        fun calendar2Timestamp(cal: Calendar): Timestamp {
            return Timestamp(cal.timeInMillis)
        }

        /**
         * Timestamp类型转换到Calendar类型
         *
         * @param timestamp
         * @return
         */
        fun timestamp2Calendar(timestamp: Timestamp?): Calendar {
            val cal = Calendar.getInstance()
            cal.time = timestamp
            return cal
        }

        /**
         * 得到当前时间的毫秒数
         *
         * @return
         */
        val currentTimeMillis: Long
            get() = System.currentTimeMillis()

        /**
         * 获取任意时间后num天的时间
         *
         * @param date
         * java.util.Date
         * @param num
         * @return
         */
        fun nextDate(date: Date?, num: Int): Date {
            val cla = Calendar.getInstance()
            cla.time = date
            cla.add(Calendar.DAY_OF_YEAR, num)
            return cla.time
        }

        /**
         * 获取任意时间后num天的时间
         *
         * @param date
         * String; <br></br>
         * 格式支持�?<br></br>
         * yyyy-MM-dd HH:mm:ss <br></br>
         * yyyy年MM月dd日HH时mm分ss�?<br></br>
         * yyyy/MM/dd HH:mm:ss <br></br>
         * 默认时间格式
         * @param num
         * int
         * @return java.util.Date
         * @throws ParseException
         */
        @Throws(ParseException::class)
        fun nextDate(date: String?, num: Int): Date? {
            if (date == null) return null
            var sdf: SimpleDateFormat? = null
            if (date.indexOf("-") != -1) sdf =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss") else if (date.indexOf("-") != -1) sdf =
                SimpleDateFormat("yyyy年MM月dd日HH时mm分ss") else if (date.indexOf("/") != -1) sdf =
                SimpleDateFormat("yyyy/MM/dd HH:mm:ss") else if (date.indexOf("CST") != -1) sdf =
                SimpleDateFormat() else println("no match format:")
            return nextDate(sdf!!.parse(date), num)
        }

        /**
         * 获取当天时间num天后的时间<br></br>
         * 如果num小于0则返回当前时间的前num天的时间<br></br>
         * ，否则返回当天时间后num天的时间
         *
         * @param num
         * int;
         * @return java.util.Date
         */
        fun nextDate(num: Int): Date {
            return nextDate(Date(), num)
        }

        /**
         * 取得当前日期是多少周
         *
         * @param date
         * @return
         */
        fun getWeekOfYear(date: Date?): Int {
            val c = Calendar.getInstance()
            c.firstDayOfWeek = Calendar.MONDAY
            /**
             * 设置一年中第一个星期所需的最少天数，例如，如果定义第一个星期包含一年第一个月的第一天，则使用值 1 调用此方法。
             * 如果最少天数必须是一整个星期，则使用值 7 调用此方法。
             */
            c.minimalDaysInFirstWeek = 1
            c.time = date
            return c[Calendar.WEEK_OF_YEAR]
        }

        /**
         * 获取当前日期
         *
         * @return
         */
        val currentDate: String
            get() {
                val format = SimpleDateFormat(formatPattern)
                return format.format(Date())
            }

        /**
         * 获取当前日期前30天
         *
         * @return
         */
        val currentDateBefore30Day: String
            get() {
                val now = Calendar.getInstance()
                now.add(Calendar.DAY_OF_MONTH, -30)
                return SimpleDateFormat(formatPattern).format(now.time)
            }

        /**
         * 获取制定毫秒数之前的日期
         *
         * @param timeDiff
         * @return
         */
        fun getDesignatedDate(timeDiff: Long): String {
            val format = SimpleDateFormat(formatPattern)
            val nowTime = System.currentTimeMillis()
            val designTime = nowTime - timeDiff
            return format.format(designTime)
        }

        /**
         *
         * 获取前几天的日期
         */
        fun getPrefixDate(count: String): String {
            val cal = Calendar.getInstance()
            val day = 0 - count.toInt()
            cal.add(Calendar.DATE, day) // int amount 代表天数
            val datNew = cal.time
            val format = SimpleDateFormat(formatPattern)
            return format.format(datNew)
        }

        /**
         * 日期转换成字符串
         *
         * @param date
         * @return
         */
        fun dateToString(date: Date?): String {
            val format = SimpleDateFormat(formatPattern)
            return format.format(date)
        }

        /**
         * 字符串转换日期
         *
         * @param str
         * @return
         */
        fun stringToDate(str: String?): Date? {
            // str = " 2008-07-10 19:20:00 " 格式
            val format = SimpleDateFormat(formatPattern)
            if (str != "" && str != null) {
                try {
                    return format.parse(str)
                } catch (e: ParseException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
            return null
        }
        /**
         * 使用用户格式提取字符串日期
         *
         * @param strDate
         * 日期字符串
         * @param pattern
         * 日期格式
         * @return
         */
        /**
         * 使用预设格式提取字符串日期
         *
         * @param strDate
         * 日期字符串
         * @return
         */
        @JvmOverloads
        fun parse(strDate: String?, pattern: String? = DATE_FULL_STR): Date? {
            val df = SimpleDateFormat(pattern)
            try {
                return df.parse(strDate)
            } catch (e: ParseException) {
                e.printStackTrace()
                return null
            }
        }

        /**
         * 两个时间比较
         *
         * @param date
         * @return
         */
        fun compareDateWithNow(date: Date): Int {
            val date2 = Date()
            return date.compareTo(date2)
        }

        /**
         * 两个时间比较(时间戳比较)
         *
         * @param date
         * @return
         */
        fun compareDateWithNow(date: Long): Int {
            val date2 = dateToUnixTimestamp()
            if (date > date2) {
                return 1
            } else return if (date < date2) {
                -1
            } else {
                0
            }
        }

        /**
         * 获取系统当前时间
         *
         * @return
         */
        fun getNowTime(type: String?): String {
            val df = SimpleDateFormat(type)
            return df.format(Date())
        }

        /**
         * 获取系统当前计费期
         *
         * @return
         */
        val jFPTime: String
            get() {
                val df = SimpleDateFormat(DATE_JFP_STR)
                return df.format(Date())
            }

        /**
         * 将指定的日期转换成Unix时间戳
         *
         * @param date
         * date 需要转换的日期 yyyy-MM-dd HH:mm:ss
         * @return long 时间戳
         */
        fun dateToUnixTimestamp(date: String?): Long {
            var timestamp: Long = 0
            try {
                timestamp = SimpleDateFormat(DATE_FULL_STR).parse(date)
                    .time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return timestamp
        }

        /**
         * 将指定的日期转换成Unix时间戳
         *
         * date 需要转换的日期 yyyy-MM-dd
         * @return long 时间戳
         */
        fun dateToUnixTimestamp(date: String?, dateFormat: String?): Long {
            var timestamp: Long = 0
            try {
                timestamp = SimpleDateFormat(dateFormat).parse(date).time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return timestamp
        }

        /**
         * 将当前日期转换成Unix时间戳
         *
         * @return long 时间戳
         */
        fun dateToUnixTimestamp(): Long {
            return Date().time
        }

        /**
         * 将Unix时间戳转换成日期
         *
         * @param timestamp timestamp 时间戳
         * @return String 日期字符串
         */
        fun unixTimestampToDate(timestamp: Long): String {
            val sd = SimpleDateFormat(DATE_FULL_STR)
            sd.timeZone = TimeZone.getTimeZone("GMT+8")
            return sd.format(Date(timestamp))
        }

        /**
         * 格式化时间为
         * @param timeMillis
         * @return
         */
        fun formatTimeInMillis(timeMillis: Long): String {
            var time = "2018-01-01"
            try {
                val curDate = Date(timeMillis) // 获取当前时间
                val format = SimpleDateFormat(DATE_SMALL_STR)
                time = format.format(curDate)
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return time
        }// TODO Auto-generated catch block

        // 获取当前时间
        val systemTime: Int
            get() {
                var result = -1
                try {
                    val curDate = Date(System.currentTimeMillis()) // 获取当前时间
                    val format = SimpleDateFormat("yyyyMMdd")
                    val time = format.format(curDate)
                    result = time.toInt()
                } catch (e: Exception) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                    return 20130918
                }
                return result
            }// TODO Auto-generated catch block// 转换为标准时间对象// 取得网站日期时间

        // 生成连接对象
        // 发出连接
        val time: Int
            get() {
                var result = -1
                try {
                    val url = URL("http://www.bjtime.cn")
                    val uc = url.openConnection() // 生成连接对象
                    uc.connect() // 发出连接
                    val ld = uc.date // 取得网站日期时间
                    val date = Date(ld) // 转换为标准时间对象
                    val format = SimpleDateFormat("yyyyMMdd")
                    val time = format.format(date)
                    result = time.toInt()
                } catch (e: Exception) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                    return systemTime
                }
                return result
            }

        /**
         * long time to string
         *
         * @param timeInMillis
         * @param dateFormat
         * @return
         */
        fun getTime(timeInMillis: Long, dateFormat: SimpleDateFormat): String {
            return dateFormat.format(Date(timeInMillis))
        }

        /**
         * long time to string, format is [.DEFAULT_DATE_FORMAT]
         *
         * @param timeInMillis
         * @return
         */
        fun getTime(timeInMillis: Long): String {
            return getTime(timeInMillis, DEFAULT_DATE_FORMAT)
        }

        /**
         * get current time in milliseconds
         *
         * @return
         */
        val currentTimeInLong: Long
            get() = System.currentTimeMillis()

        /**
         * get current time in milliseconds, format is [.DEFAULT_DATE_FORMAT]
         *
         * @return
         */
        val currentTimeInString: String
            get() = getTime(currentTimeInLong)

        /**
         * get current time in milliseconds
         *
         * @return
         */
        fun getCurrentTimeInString(dateFormat: SimpleDateFormat): String {
            return getTime(currentTimeInLong, dateFormat)
        }
    }
}
