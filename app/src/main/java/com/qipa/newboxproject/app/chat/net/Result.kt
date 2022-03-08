package com.qipa.newboxproject.app.chat.net

/**
 * 结果基础类
 * @param <T> 请求结果的实体类
</T> */
class Result<T> {
    var code = 0
    var result: T? = null

    constructor() {}
    constructor(code: Int, result: T) {
        this.code = code
        this.result = result
    }

    constructor(code: Int) {
        this.code = code
    }

    @JvmName("setResult1")
    fun setResult(result: T) {
        this.result = result
    }

    val isSuccess: Boolean
        get() = code == ErrorCode.EM_NO_ERROR
}
