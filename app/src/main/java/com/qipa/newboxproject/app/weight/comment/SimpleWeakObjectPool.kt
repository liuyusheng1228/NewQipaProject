package com.qipa.newboxproject.app.weight.comment

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;

class SimpleWeakObjectPool<T> @JvmOverloads constructor(private val size: Int = 5) {
    private val objsPool: kotlin.Array<WeakReference<T>?>?
    private var curPointer = -1
    @Synchronized
    fun get(): T? {
        if (curPointer == -1 || curPointer > objsPool!!.size) return null
        val obj = objsPool[curPointer]!!.get()
        objsPool[curPointer] = null
        curPointer--
        return obj
    }

    @Synchronized
    fun put(t: T): Boolean {
        if (curPointer == -1 || curPointer < objsPool!!.size - 1) {
            curPointer++
            objsPool!![curPointer] = WeakReference(t)
            return true
        }
        return false
    }

    fun clearPool() {
        for (i in objsPool!!.indices) {
            objsPool[i]!!.clear()
            objsPool[i] = null
        }
        curPointer = -1
    }

    fun size(): Int {
        return objsPool?.size ?: 0
    }

    init {
        objsPool = Array.newInstance(
            WeakReference::class.java,
            size
        ) as kotlin.Array<WeakReference<T>?>
    }
}
