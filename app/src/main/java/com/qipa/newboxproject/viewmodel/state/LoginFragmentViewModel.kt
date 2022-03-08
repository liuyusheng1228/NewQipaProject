package com.qipa.newboxproject.viewmodel.state

import android.app.Application
import com.hyphenate.easeui.domain.EaseUser

import androidx.lifecycle.MediatorLiveData

import androidx.lifecycle.AndroidViewModel
import com.qipa.newboxproject.app.chat.net.Resource
import com.qipa.newboxproject.app.chat.repository.EMClientRepository


class LoginFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: EMClientRepository
    private var loginObservable: MediatorLiveData<Resource<EaseUser>>? = null

    /**
     * 登录环信
     * @param userName
     * @param pwd
     * @param isTokenFlag
     */
    fun login(userName: String?, pwd: String?, isTokenFlag: Boolean) {
        loginObservable?.addSource(
            mRepository.loginToServer(userName, pwd, isTokenFlag)
        ) { response: Resource<EaseUser>? ->
            loginObservable?.setValue(response)
        }
    }
    fun getLoginObservable(): MediatorLiveData<Resource<EaseUser>>? {
        return loginObservable
    }

    init {
        mRepository = EMClientRepository()
        loginObservable = MediatorLiveData<Resource<EaseUser>>()
    }
}
