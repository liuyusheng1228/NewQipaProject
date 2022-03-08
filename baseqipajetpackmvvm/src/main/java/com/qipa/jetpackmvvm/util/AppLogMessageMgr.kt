package com.qipa.jetpackmvvm.util

import android.annotation.SuppressLint
import android.util.Log


/**
 * 主要功能： 系统日志输出工具类
 * @Prject: CommonUtilLibrary
 * @Package: com.jingewenku.abrahamcaijin.commonutil
 * @author: AbrahamCaiJin
 * @date: 2017年05月04日 14:13
 * @Copyright: 个人版权所有
 * @Company:
 * @version: 1.0.0
 */
object AppLogMessageMgr {
    //是否输出
    private var isDebug = true

    /*
     * 设置debug模式(true:打印日志  false：不打印)
     */
    fun isEnableDebug(isDebug: Boolean) {
        AppLogMessageMgr.isDebug = isDebug
    }

    /**
     *
     * @param tag
     * @param msg
     */
    fun i(tag: String, msg: String) {
        if (isDebug) {
            Log.i(tag, msg ?: "")
        }
    }

    fun i(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.i(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun i(msg: String?) {
        if (isDebug) {
            Log.i(" [INFO] --- ", msg ?: "")
        }
    }

    /**
     *
     * @param tag
     * @param msg
     */
    fun d(tag: String?, msg: String?) {
        if (isDebug) {
            Log.d(tag, msg ?: "")
        }
    }

    fun d(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.d(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    @SuppressLint("LongLogTag")
    fun d(msg: String?) {
        if (isDebug) {
            Log.d(" [isNetRequestInterceptor] --- ", msg ?: "")
        }
    }

    /**
     *
     * @param tag
     * @param msg
     */
    fun w(tag: String?, msg: String?) {
        if (isDebug) {
            Log.w(tag, msg ?: "")
        }
    }

    fun w(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.w(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun w(msg: String?) {
        if (isDebug) {
            Log.w(" [WARN] --- ", msg ?: "")
        }
    }

    /**
     *
     * @param tag
     * @param msg
     */
    fun e(tag: String, msg: String) {
        if (isDebug) {
            Log.e(tag, msg ?: "")
        }
    }

    fun e(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.e(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun e(msg: String?) {
        if (isDebug) {
            Log.e(" [ERROR] --- ", msg ?: "")
        }
    }

    /**
     *
     * @param tag
     * @param msg
     */
    fun v(tag: String?, msg: String?) {
        if (isDebug) {
            Log.v(tag, msg ?: "")
        }
    }

    fun v(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.v(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun v(msg: String?) {
        if (isDebug) {
            Log.v(" [VERBOSE] --- ", msg ?: "")
        }
    }
}