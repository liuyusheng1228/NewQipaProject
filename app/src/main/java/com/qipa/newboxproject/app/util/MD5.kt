package com.qipa.newboxproject.app.util

import android.text.TextUtils
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object MD5 {
    /**
     * 进行MD5加密
     * @param str
     * @return
     */
    fun encrypt2MD5(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return ""
        }
        var hexStr = ""
        try {
            val hash: ByteArray =
                MessageDigest.getInstance("MD5").digest(str.toByteArray(charset("utf-8")))
            //对生成的16字节数组进行补零操作
            val hex = StringBuilder(hash.size * 2)
            for (b in hash) {
                if (b and 0xFF.toByte() < 0x10) {
                    hex.append("0")
                }
                hex.append(Integer.toHexString((b and 0xFF.toByte()).toInt()))
            }
            hexStr = hex.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return hexStr
    }
}
