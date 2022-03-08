package com.qipa.jetpackmvvm.util.cache

class Cache {
    private var key // 缓存ID
            : String? = null
    private var value // 缓存数据
            : Any? = null
    private var timeOut // 更新时间
            : Long = 0
    private var expired // 是否终止
            = false

    fun Cache() {
    }

    fun Cache(key: String?, value: Any?, timeOut: Long, expired: Boolean) {
        this.key = key
        this.value = value
        this.timeOut = timeOut
        this.expired = expired
    }

    fun getKey(): String? {
        return key
    }

    fun getTimeOut(): Long {
        return timeOut
    }

    fun getValue(): Any? {
        return value
    }

    fun setKey(string: String?) {
        key = string
    }

    fun setTimeOut(l: Long) {
        timeOut = l
    }

    fun setValue(`object`: Any?) {
        value = `object`
    }

    fun isExpired(): Boolean {
        return expired
    }

    fun setExpired(b: Boolean) {
        expired = b
    }
}