package com.qipa.qipaimbase.utils

import android.util.Log

object LogUtils {
    private const val DEFAULT_TAG = "DEFAULT_TAG"
    fun log(content: String?) {
        Log.v(DEFAULT_TAG, content!!)
    }

    fun log(tag: String?, content: String?) {
        Log.v(tag, content!!)
    }
}