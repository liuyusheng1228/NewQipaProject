package com.qipa.newboxproject.viewmodel.request

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qipa.newboxproject.app.network.stateCallback.ListDataUiState
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.data.repository.request.HttpRequestCoroutine
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.ext.request
import com.qipa.jetpackmvvm.network.utils.rsa.RSAUtils
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.data.model.bean.BindPhoneBean
import com.qipa.qipaimbase.utils.ToastUtils

/**
 * 描述　:
 */
class RequestBindPhoneViewModel : BaseViewModel() {

    //页码
    var pageNo = 1

    var bindResult : MutableLiveData<String> = MutableLiveData()

//    var titleData: MutableLiveData<ResultState<ArrayList<ClassifyResponse>>> = MutableLiveData()

    var projectDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    fun getProjectTitleData() {
//        request({ apiService.getProjecTitle() }, titleData)
    }

    fun bindPhone(bindPhoneBean: BindPhoneBean) {
        var msg : String = RSAUtils.encrypt(Gson().toJson(bindPhoneBean))
        request({ HttpRequestCoroutine.bindPhone(msg) },{
            bindResult.value = it
            CacheUtil.setIsBindPhone(true)
        },{
            ToastUtils.showText("绑定失败")
        },true,
            "正在绑定中..." )
    }

    fun unBindPhone(bindPhoneBean: BindPhoneBean) {
        var msg : String = RSAUtils.encrypt(Gson().toJson(bindPhoneBean))
        request({ HttpRequestCoroutine.unBindPhone(msg) },{
            bindResult.value = it
            CacheUtil.setIsBindPhone(false)
            ToastUtils.showText("解绑成功")
        },{
            ToastUtils.showText("解绑失败")
        },true,
            "正在解绑中..." )
    }

}