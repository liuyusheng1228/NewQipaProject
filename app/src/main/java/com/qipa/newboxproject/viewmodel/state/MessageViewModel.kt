package com.qipa.newboxproject.viewmodel.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hyphenate.easeui.model.EaseEvent
import com.qipa.newboxproject.data.db.LiveDataBus

class MessageViewModel(application: Application) :
    AndroidViewModel(application) {
    val messageChange: LiveDataBus

    fun setMessageChange(change: EaseEvent) {
        messageChange.with(change.event).postValue(change)
    }

    init {
        messageChange = LiveDataBus.get()
    }
}
