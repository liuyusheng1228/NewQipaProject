package com.qipa.newboxproject.app.chat.interfaceOrImplement

import com.hyphenate.EMValueCallBack


abstract class ResultCallBack<T> : EMValueCallBack<T> {
    /**
     * 针对只返回error code的情况
     * @param error
     */
    fun onError(error: Int) {
        onError(error, null)
    }
}
