package com.qipa.newboxproject.app.ext

import androidx.lifecycle.MutableLiveData

class LiveDataBus private constructor() : MutableLiveData<Event<FlashEvent>>() {

    companion object {
        val instance by lazy { LiveDataBus() }
    }
}
// event: 事件类型； data: 要传递的数据
data class FlashEvent(val event: Int, val data: Any) {
    companion object {
        const val EVENT_TEST = 1 // 测试事件
    }
}
