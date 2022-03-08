package com.qipa.newboxproject.app.chat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 设置并监听单一数据源时使用 LiveData
 * 方便于当需要切换数据源时自动取消掉前一个数据源的监听
 *
 * @param <T> 监听的数据源类型
</T> */
class SingleSourceLiveData<T> : MutableLiveData<T>() {
    private var lastSource: LiveData<T>? = null
    private var lastData: T? = null
    private val observer: Observer<T> = Observer { t ->
        if (t != null && t === lastData) {
            return@Observer
        }
        lastData = t
        setValue(t)
    }

    /**
     * 设置数据源，当有已设置过的数据源时会取消该数据源的监听
     *
     * @param source
     */
    fun setSource(source: LiveData<T>) {
        if (lastSource === source) {
            return
        }
        if (lastSource != null) {
            lastSource!!.removeObserver(observer)
        }
        lastSource = source
        if (hasActiveObservers()) {
            lastSource!!.observeForever(observer)
        }
    }
    override fun onActive() {
        super.onActive()
        if (lastSource != null) {
            lastSource!!.observeForever(observer)
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (lastSource != null) {
            lastSource!!.removeObserver(observer)
        }
    }

}
