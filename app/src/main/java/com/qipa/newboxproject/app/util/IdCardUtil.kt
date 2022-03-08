package com.qipa.newboxproject.app.util

import android.text.TextUtils


/**
 * 身份证的工具类
 */
class IdCardUtil(idCardNum: String) {
    private var idCardNum: String? = null

    private val IS_EMPTY = 1
    private val LEN_ERROR = 2
    private val CHAR_ERROR = 3
    private val DATE_ERROR = 4
    private val CHECK_BIT_ERROR = 5

    private val errMsg = arrayOf(
        "身份证完全正确！",
        "身份证为空！",
        "身份证长度不正确！",
        "身份证有非法字符！",
        "身份证中出生日期不合法！",
        "身份证校验位错误！"
    )

    private var error = 0

    fun getIdCardNum(): String? {
        return idCardNum
    }

    fun setIdCardNum(idCardNum: String?) {
        this.idCardNum = idCardNum
        if (!TextUtils.isEmpty(this.idCardNum)) {
            this.idCardNum = this.idCardNum!!.replace("x", "X")
        }
    }

    /**
     * 得到身份证详细错误信息。
     *
     * @return 错误信息。
     */
    fun getErrMsg(): String? {
        return errMsg[error]
    }

    /**
     * 是否为空。
     *
     * @return true: null  false: not null;
     */
    fun isEmpty(): Boolean {
        return if (idCardNum == null) true else if (idCardNum!!.trim { it <= ' ' }.length > 0) false else true
    }

    /**
     * 身份证长度。
     *
     * @return
     */
    fun getLength(): Int {
        return if (this.isEmpty()) 0 else idCardNum!!.length
    }

    /**
     * 身份证长度。
     *
     * @return
     */
    fun getLength(str: String): Int {
        return if (this.isEmpty()) 0 else str.length
    }

    /**
     * 是否是15位身份证。
     *
     * @return true: 15位  false：其他。
     */
    fun is15(): Boolean {
        return this.getLength() == 15
    }

    /**
     * 是否是18位身份证。
     *
     * @return true: 18位  false：其他。
     */
    fun is18(): Boolean {
        return this.getLength() == 18
    }

    /**
     * 得到身份证的省份代码。
     *
     * @return 省份代码。
     */
    fun getProvince(): String? {
        return if (isCorrect() == 0) idCardNum!!.substring(0, 2) else ""
    }

    /**
     * 得到身份证的城市代码。
     *
     * @return 城市代码。
     */
    fun getCity(): String? {
        return if (isCorrect() == 0) idCardNum!!.substring(2, 4) else ""
    }

    /**
     * 得到身份证的区县代码。
     *
     * @return 区县代码。
     */
    fun getCountry(): String? {
        return if (isCorrect() == 0) idCardNum!!.substring(4, 6) else ""
    }

    /**
     * 得到身份证的出生年份。
     *
     * @return 出生年份。
     */
    fun getYear(): String? {
        if (isCorrect() != 0) return ""
        return if (this.getLength() == 15) {
            "19" + idCardNum!!.substring(6, 8)
        } else {
            idCardNum!!.substring(6, 10)
        }
    }

    /**
     * 得到身份证的出生月份。
     *
     * @return 出生月份。
     */
    fun getMonth(): String? {
        if (isCorrect() != 0) return ""
        return if (this.getLength() == 15) {
            idCardNum!!.substring(8, 10)
        } else {
            idCardNum!!.substring(10, 12)
        }
    }

    /**
     * 得到身份证的出生日子。
     *
     * @return 出生日期。
     */
    fun getDay(): String? {
        if (isCorrect() != 0) return ""
        return if (this.getLength() == 15) {
            idCardNum!!.substring(10, 12)
        } else {
            idCardNum!!.substring(12, 14)
        }
    }

    /**
     * 得到身份证的出生日期。
     *
     * @return 出生日期。
     */
    fun getBirthday(): String {
        if (isCorrect() != 0) return ""
        return if (this.getLength() == 15) {
            "19" + idCardNum!!.substring(6, 12)
        } else {
            idCardNum!!.substring(6, 14)
        }
    }

    /**
     * 得到身份证的出生年月。
     *
     * @return 出生年月。
     */
    fun getBirthMonth(): String? {
        return getBirthday().substring(0, 6)
    }

    /**
     * 得到身份证的顺序号。
     *
     * @return 顺序号。
     */
    fun getOrder(): String {
        if (isCorrect() != 0) return ""
        return if (this.getLength() == 15) {
            idCardNum!!.substring(12, 15)
        } else {
            idCardNum!!.substring(14, 17)
        }
    }

    /**
     * 得到性别。
     *
     * @return 性别：1－男  2－女
     */
    fun getSex(): String? {
        if (isCorrect() != 0) return ""
        val p = getOrder().toInt()
        return if (p % 2 == 1) {
            "男"
        } else {
            "女"
        }
    }

    /**
     * 得到性别值。
     *
     * @return 性别：1－男  2－女
     */
    fun getSexValue(): String? {
        if (isCorrect() != 0) return ""
        val p = getOrder().toInt()
        return if (p % 2 == 1) {
            "1"
        } else {
            "2"
        }
    }

    /**
     * 得到校验位。
     *
     * @return 校验位。
     */
    fun getCheck(): String {
        if (!isLenCorrect()) return ""
        var lastStr = idCardNum!!.substring(idCardNum!!.length - 1)
        if ("x" == lastStr) {
            lastStr = "X"
        }
        return lastStr
    }

    /**
     * 得到15位身份证。
     *
     * @return 15位身份证。
     */
    fun to15(): String? {
        if (isCorrect() != 0) return ""
        return if (is15()) idCardNum else idCardNum!!.substring(0, 6) + idCardNum!!.substring(8, 17)
    }

    /**
     * 得到18位身份证。
     *
     * @return 18位身份证。
     */
    fun to18(): String? {
        if (isCorrect() != 0) return ""
        return if (is18()) idCardNum else idCardNum!!.substring(
            0,
            6
        ) + "19" + idCardNum!!.substring(6) + this.getCheckBit()
    }

    /**
     * 得到18位身份证。
     *
     * @return 18位身份证。
     */
    fun toNewIdCard(tempStr: String): String? {
        return if (tempStr.length == 18) tempStr.substring(0, 6) + tempStr.substring(
            8,
            17
        ) else tempStr.substring(0, 6) + "19" + tempStr.substring(6) + getCheckBit(tempStr)
    }

    /**
     * 校验身份证是否正确
     *
     * @return 0：正确
     */
    fun isCorrect(): Int {
        if (this.isEmpty()) {
            error = IS_EMPTY
            return error
        }
        if (!isLenCorrect()) {
            error = LEN_ERROR
            return error
        }
        if (!isCharCorrect()) {
            error = CHAR_ERROR
            return error
        }
        if (!isDateCorrect()) {
            error = DATE_ERROR
            return error
        }
        if (is18()) {
            if (getCheck() != this.getCheckBit()) {
                error = CHECK_BIT_ERROR
                return error
            }
        }
        return 0
    }


    private fun isLenCorrect(): Boolean {
        return is15() || is18()
    }

    /**
     * 判断身份证中出生日期是否正确。
     *
     * @return
     */
    private fun isDateCorrect(): Boolean {

        /*非闰年天数*/
        val monthDayN = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        /*闰年天数*/
        val monthDayL = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val month: Int
        month = if (is15()) {
            idCardNum!!.substring(8, 10).toInt()
        } else {
            idCardNum!!.substring(10, 12).toInt()
        }
        val day: Int
        day = if (is15()) {
            idCardNum!!.substring(10, 12).toInt()
        } else {
            idCardNum!!.substring(12, 14).toInt()
        }
        if (month > 12 || month <= 0) {
            return false
        }
        if (isLeapyear()) {
            if (day > monthDayL[month - 1] || day <= 0) return false
        } else {
            if (day > monthDayN[month - 1] || day <= 0) return false
        }
        return true
    }

    /**
     * 得到校验位。
     *
     * @return
     */
    private fun getCheckBit(): String {
        if (!isLenCorrect()) return ""
        var temp: String? = null
        temp =
            if (is18()) idCardNum else idCardNum!!.substring(0, 6) + "19" + idCardNum!!.substring(6)
        val checkTable = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")
        val wi = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1)
        var sum = 0
        for (i in 0..16) {
            val ch = temp!!.substring(i, i + 1)
            sum = sum + ch.toInt() * wi[i]
        }
        val y = sum % 11
        return checkTable[y]
    }


    /**
     * 得到校验位。
     *
     * @return
     */
    private fun getCheckBit(str: String): String {
        var temp: String? = null
        temp = if (str.length == 18) str else str.substring(0, 6) + "19" + str.substring(6)
        val checkTable = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")
        val wi = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1)
        var sum = 0
        for (i in 0..16) {
            val ch = temp.substring(i, i + 1)
            sum = sum + ch.toInt() * wi[i]
        }
        val y = sum % 11
        return checkTable[y]
    }


    /**
     * 身份证号码中是否存在非法字符。
     *
     * @return true: 正确  false：存在非法字符。
     */
    private fun isCharCorrect(): Boolean {
        var iRet = true
        if (isLenCorrect()) {
            val temp = idCardNum!!.toByteArray()
            if (is15()) {
                for (i in temp.indices) {
                    if (temp[i] < 48 || temp[i] > 57) {
                        iRet = false
                        break
                    }
                }
            }
            if (is18()) {
                for (i in temp.indices) {
                    if (temp[i] < 48 || temp[i] > 57) {
                        if (i == 17 && temp[i].toInt() != 88) {
                            iRet = false
                            break
                        }
                    }
                }
            }
        } else {
            iRet = false
        }
        return iRet
    }

    /**
     * 判断身份证的出生年份是否未闰年。
     *
     * @return true ：闰年  false 平年
     */
    private fun isLeapyear(): Boolean {
        val temp: String
        temp = if (is15()) {
            "19" + idCardNum!!.substring(6, 8)
        } else {
            idCardNum!!.substring(6, 10)
        }
        val year = temp.toInt()
        return if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) true else false
    }
    /**
     * 构造方法。
     *
     * @param idCardNum
     */
    init {
        // super();
        this.idCardNum = idCardNum.trim { it <= ' ' }
        if (!TextUtils.isEmpty(this.idCardNum)) {
            this.idCardNum = this.idCardNum!!.replace("x", "X")
        }
    }
}
