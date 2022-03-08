package com.qipa.newboxproject.data.db

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.Exception
import java.lang.NullPointerException
import java.util.HashMap
import androidx.annotation.Nullable


/**
 * 替代EventBus及RxBus
 * 代码源自：Android消息总线的演进之路：用LiveDataBus替代RxBus、EventBus
 * https://mp.weixin.qq.com/s?__biz=MjM5NjQ5MTI5OA==&mid=2651748475&idx=4&sn=8feb14dd49ce79726ecf12eb6c243740&chksm=bd12a1368a652820df7c556182d3494d84ae38d4aee4e84c48c227aa5083ebf2b1a0150cf1b5&mpshare=1&scene=1&srcid=1010fzmNILeVVxi5HsAG8R17#rd
 *
 * 基本使用：
 * 注册订阅：
 * LiveDataBus.get().getChannel("key_test", Boolean.class)
 * .observe(this, new Observer<Boolean>() {
 * @Override
 * public void onChanged(@Nullable Boolean aBoolean) {
 * }
 * });
 * 发送消息：
 * LiveDataBus.get().getChannel("key_test").setValue(true);
</Boolean> */
class LiveDataBus private constructor() {
    private val bus: MutableMap<String, BusMutableLiveData<Any>>

    private object SingletonHolder {
        val DEFAULT_BUS = LiveDataBus()
    }

    fun <T> with(key: String, type: Class<T>?): MutableLiveData<T> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as MutableLiveData<T>
    }

    fun with(key: String): MutableLiveData<Any> {
        return with(key, Any::class.java)
    }

    private class ObserverWrapper<T>(private val observer: Observer<T>?) :
        Observer<T> {
        override fun onChanged(@Nullable t: T) {
            if (observer != null) {
                if (isCallOnObserve) {
                    return
                }
                observer.onChanged(t)
            }
        }

        private val isCallOnObserve: Boolean
            private get() {
                val stackTrace = Thread.currentThread().stackTrace
                if (stackTrace != null && stackTrace.size > 0) {
                    for (element in stackTrace) {
                        if ("android.arch.lifecycle.LiveData" == element.className && "observeForever" == element.methodName) {
                            return true
                        }
                    }
                }
                return false
            }

    }

    private class BusMutableLiveData<T> : MutableLiveData<T>() {
        private val observerMap: MutableMap<Observer<*>, Observer<*>> = HashMap()
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            try {
                hook(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun observeForever(observer: Observer<in T?>) {
            if (!observerMap.containsKey(observer)) {
                observerMap[observer] = LiveDataBus.ObserverWrapper(observer)
            }
            super.observeForever(observerMap[observer] as Observer<in T>)
        }

        override fun removeObserver(observer: Observer<in T?>) {
            var realObserver: Observer<*>? = null
            realObserver = if (observerMap.containsKey(observer)) {
                observerMap.remove(observer)
            } else {
                observer
            }
            super.removeObserver(realObserver as Observer<in T>)
        }

        @Throws(Exception::class)
        private fun hook(observer: Observer<in T>) {
            //get wrapper's version
            val classLiveData = LiveData::class.java
            val fieldObservers = classLiveData.getDeclaredField("mObservers")
            fieldObservers.isAccessible = true
            val objectObservers = fieldObservers[this]
            val classObservers: Class<*> = objectObservers.javaClass
            val methodGet = classObservers.getDeclaredMethod(
                "get",
                Any::class.java
            )
            methodGet.isAccessible = true
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            if (objectWrapper == null) {
                throw NullPointerException("Wrapper can not be bull!")
            }
            val classObserverWrapper: Class<*> = objectWrapper.javaClass.superclass
            val fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion")
            fieldLastVersion.isAccessible = true
            //get livedata's version
            val fieldVersion = classLiveData.getDeclaredField("mVersion")
            fieldVersion.isAccessible = true
            val objectVersion = fieldVersion[this]
            //set wrapper's version
            fieldLastVersion[objectWrapper] = objectVersion
        }
    }

    companion object {
        fun get(): LiveDataBus {
            return SingletonHolder.DEFAULT_BUS
        }
    }

    init {
        bus = HashMap()
    }
}