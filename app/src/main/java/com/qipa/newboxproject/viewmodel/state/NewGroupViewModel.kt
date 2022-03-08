package com.qipa.newboxproject.viewmodel.state

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.hyphenate.chat.EMGroup
import com.hyphenate.chat.EMGroupOptions
import com.qipa.newboxproject.app.chat.net.Resource
import com.qipa.newboxproject.app.chat.repository.EMGroupManagerRepository
import com.qipa.newboxproject.app.chat.repository.SingleSourceLiveData

class NewGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EMGroupManagerRepository
    private var groupObservable: SingleSourceLiveData<Resource<EMGroup>>? = null
    fun groupObservable(): LiveData<Resource<EMGroup>>? {
        return groupObservable
    }

    fun createGroup(
        groupName: String?,
        desc: String?,
        allMembers: Array<String?>?,
        reason: String?,
        option: EMGroupOptions?
    ) {
        Log.i("Api","创建群聊")
        groupObservable?.setSource(
            repository.createGroup(
                groupName,
                desc,
                allMembers,
                reason,
                option
            )
        )
    }


    init {
        repository = EMGroupManagerRepository()
        groupObservable = SingleSourceLiveData()
    }
}
