package com.qipa.jetpackmvvm.util.cache

import java.lang.Exception
import java.lang.NullPointerException
import java.util.ArrayList
import java.util.HashMap


object CacheManager {
    private val cacheMap = HashMap<String, Any?>()

    // 获取布尔值的缓存
    fun getSimpleFlag(key: String): Boolean {
        return try {
            cacheMap[key] as Boolean
        } catch (e: NullPointerException) {
            false
        }
    }

    fun getServerStartdt(key: String): Long {
        return try {
            cacheMap[key] as Long
        } catch (ex: Exception) {
            0
        }
    }

    // 设置布尔值的缓存
    @Synchronized
    fun setSimpleFlag(key: String, flag: Boolean): Boolean {
        return if (flag && getSimpleFlag(key)) { // 假如为真不允许被覆盖
            false
        } else {
            cacheMap[key] = flag
            true
        }
    }

    @Synchronized
    fun setSimpleFlag(
        key: String,
        serverbegrundt: Long
    ): Boolean {
        return if (cacheMap[key] == null) {
            cacheMap[key] = serverbegrundt
            true
        } else {
            false
        }
    }

    // 得到缓存。同步静态方法
    @Synchronized
    private fun getCache(key: String): Cache? {
        return cacheMap[key] as Cache?
    }

    // 判断是否存在一个缓存
    @Synchronized
    private fun hasCache(key: String): Boolean {
        return cacheMap.containsKey(key)
    }

    // 清除所有缓存
    @Synchronized
    fun clearAll() {
        cacheMap.clear()
    }

    // 清除某一类特定缓存,通过遍历HASHMAP下的所有对象，来判断它的KEY与传入的TYPE是否匹配
    @Synchronized
    fun clearAll(type: String?) {
        val i: Iterator<*> = cacheMap.entries.iterator()
        var key: String
        val arr: ArrayList<*> = ArrayList<Any?>()
        try {
            while (i.hasNext()) {
                val entry = i.next() as Map.Entry<*, *>
                key = entry.key as String
                if (key.startsWith(type!!)) { // 如果匹配则删除掉
                    arr.add(key as Nothing)
                }
            }
            for (k in arr.indices) {
                clearOnly(arr[k] as String)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // 清除指定的缓存
    @Synchronized
    fun clearOnly(key: String) {
        cacheMap.remove(key)
    }

    // 载入缓存
    @Synchronized
    fun putCache(key: String, obj: Cache?) {
        cacheMap[key] = obj
    }

    // 获取缓存信息
    fun getCacheInfo(key: String): Cache? {
        return if (hasCache(key)) {
            val cache = getCache(key)
            if (cacheExpired(cache)) { // 调用判断是否终止方法
                cache!!.setExpired(true)
            }
            cache
        } else null
    }

    // 载入缓存信息
    fun putCacheInfo(
        key: String, obj: Cache?, dt: Long,
        expired: Boolean
    ) {
        val cache = Cache()
        cache.setKey(key)
        cache.setTimeOut(dt + System.currentTimeMillis()) // 设置多久后更新缓存
        cache.setValue(obj)
        cache.setExpired(expired) // 缓存默认载入时，终止状态为FALSE
        cacheMap[key] = cache
    }

    // 重写载入缓存信息方法
    fun putCacheInfo(key: String, obj: Cache?, dt: Long) {
        val cache = Cache()
        cache.setKey(key)
        cache.setTimeOut(dt + System.currentTimeMillis())
        cache.setValue(obj)
        cache.setExpired(false)
        cacheMap[key] = cache
    }

    // 判断缓存是否终止
    fun cacheExpired(cache: Cache?): Boolean {
        if (null == cache) { // 传入的缓存不存在
            return false
        }
        val nowDt = System.currentTimeMillis() // 系统当前的毫秒数
        val cacheDt = cache.getTimeOut() // 缓存内的过期毫秒数
        return if (cacheDt <= 0 || cacheDt > nowDt) { // 过期时间小于等于零时,或者过期时间大于当前时间时，则为FALSE
            false
        } else { // 大于过期时间 即过期
            true
        }
    }

    // 获取缓存中的大小
    val cacheSize: Int
        get() = cacheMap.size

    // 获取指定的类型的大小
    fun getCacheSize(type: String?): Int {
        var k = 0
        val i: Iterator<*> = cacheMap.entries.iterator()
        var key: String
        try {
            while (i.hasNext()) {
                val entry = i.next() as Map.Entry<*, *>
                key = entry.key as String
                if (key.indexOf(type!!) != -1) { // 如果匹配则删除掉
                    k++
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return k
    }

    // 获取缓存对象中的所有键值名称
    val cacheAllkey: ArrayList<*>
        get() {
            val a: ArrayList<*> = ArrayList<Any?>()
            try {
                val i: Iterator<*> = cacheMap.entries.iterator()
                while (i.hasNext()) {
                    val entry = i.next() as Map.Entry<*, *>
                    a.add(entry.key as Nothing)
                }
            } catch (ex: Exception) {
            } finally {
                return a
            }
        }

    // 获取缓存对象中指定类型 的键值名称
    fun getCacheListkey(type: String?): ArrayList<*> {
        val a: ArrayList<*> = ArrayList<Any?>()
        var key: String
        try {
            val i: Iterator<*> = cacheMap.entries.iterator()
            while (i.hasNext()) {
                val entry = i.next() as Map.Entry<*, *>
                key = entry.key as String
                if (key.indexOf(type!!) != -1) {
                    a.add(key as Nothing)
                }
            }
        } catch (ex: Exception) {
        } finally {
            return a
        }
    }
}
