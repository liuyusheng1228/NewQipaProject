package com.ysj.bt.qipa

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil(context: Context) {
    // 存入数据
    fun putSP(key: String?, value: String?) {
        mEditor?.putString(key, value)
        mEditor?.commit()
    }

    // 获取数据
    fun getSP(key: String?): String? {
        return mPreferences?.getString(key, "")
    }

    // 移除数据
    fun removeSP(key: String?) {
        mEditor?.remove(key)
        mEditor?.commit()
    }

    companion object {
        const val mTAG = "test"

        // 创建一个写入器
        private var mPreferences: SharedPreferences? = null
        private var mEditor: SharedPreferences.Editor? = null
        private var mSharedPreferencesUtil: SharedPreferencesUtil? = null

        // 单例模式
        fun getInstance(context: Context): SharedPreferencesUtil? {
            if (mSharedPreferencesUtil == null) {
                mSharedPreferencesUtil = SharedPreferencesUtil(context)
            }
            return mSharedPreferencesUtil
        }
    }

    // 构造方法
    init {
        mPreferences = context.getSharedPreferences(mTAG, Context.MODE_PRIVATE)
        mEditor = mPreferences?.edit()
    }
}
