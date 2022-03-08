package com.qipa.newboxproject.app.util

object RegexUtil {
    /**
     * 比较真实完整的判断身份证号码的工具
     *
     * @param IdCard 用户输入的身份证号码
     * @return [true符合规范, false不符合规范]
     */
    fun isRealIDCard(IdCard: String?): Boolean {
        if (IdCard != null) {
            val correct = IdCardUtil(IdCard).isCorrect()
            if (0 == correct) { // 符合规范
                return true
            }
        }
        return false
    }
}