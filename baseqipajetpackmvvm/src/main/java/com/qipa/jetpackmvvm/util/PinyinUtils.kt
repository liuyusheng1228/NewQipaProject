package com.qipa.jetpackmvvm.util

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

/**
 * 拼音帮助类
 */
object PinyinUtils {
    @JvmStatic
    fun main(args: Array<String>) {
        // TODO Auto-generated method stub
        println("main:" + getFirstSpell("我们的pinyinTest"))
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     * 花花大神->huahuadashen
     * @param inputString
     * @return
     */
    fun getPingYin(inputString: String): String {
        val format = HanyuPinyinOutputFormat()
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE)
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
        format.setVCharType(HanyuPinyinVCharType.WITH_V)
        val input = inputString.trim { it <= ' ' }.toCharArray()
        var output = ""
        try {
            for (curchar in input) {
                output += if (Character.toString(curchar).matches(
                        "[\\u4E00-\\u9FA5]+".toRegex()
                    )
                ) {
                    val temp: Array<String> = PinyinHelper.toHanyuPinyinStringArray(
                        curchar, format
                    )
                    temp[0]
                } else Character.toString(curchar)
            }
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
        }
        return output
    }

    /**
     * 汉字转换为汉语拼音首字母，英文字符不变
     * 花花大神->hhds
     * 汉字
     * @return 拼音
     */
    fun getFirstSpell(chinese: String): String {
        val pybf = StringBuffer()
        val arr = chinese.toCharArray()
        val defaultFormat = HanyuPinyinOutputFormat()
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE)
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
        for (curchar in arr) {
            if (curchar.toInt() > 128) {
                try {
                    val temp: Array<String> =
                        PinyinHelper.toHanyuPinyinStringArray(curchar, defaultFormat)
                    if (temp != null) {
                        pybf.append(temp[0][0])
                    }
                } catch (e: BadHanyuPinyinOutputFormatCombination) {
                    e.printStackTrace()
                }
            } else {
                pybf.append(curchar)
            }
        }
        return pybf.toString().replace("\\W".toRegex(), "").trim { it <= ' ' }
    }
}