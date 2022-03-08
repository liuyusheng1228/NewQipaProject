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
import com.qipa.jetpackmvvm.state.ResultState
import com.qipa.newboxproject.data.model.UserDelete
import com.qipa.newboxproject.data.model.WxLoginToken
import com.qipa.newboxproject.data.model.WxUserInfoResponse
import com.qipa.newboxproject.data.model.bean.UserListResponse

/**
 * 描述　:
 */
class RequestMessageViewModel : BaseViewModel() {

    //页码
    var pageNo = 1

    var titleData: MutableLiveData<ResultState<String>> = MutableLiveData()

    var wxUserInfoBean : MutableLiveData<WxUserInfoResponse> = MutableLiveData()

    var listResponses : ArrayList<UserListResponse> = arrayListOf()

    var userListResponse: MutableLiveData<ListDataUiState<UserListResponse>> = MutableLiveData()

    var projectDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    fun deleteUser(params: String) {
        var msg : String = RSAUtils.encrypt(Gson().toJson(UserDelete(params)))
        request({ HttpRequestCoroutine.deleteUser(msg) },titleData )
    }


    fun getList() {
        request({ HttpRequestCoroutine.getList() }, {it ->
            listResponses =  Gson().fromJson(""+RSAUtils.decrypt(it),
                object : TypeToken<ArrayList<UserListResponse>>() {}.getType())
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isRefresh = false,
                    isEmpty = false,
                    hasMore = false,
                    isFirstEmpty = false && false,
                    listData = listResponses
                )

            Log.i("Api",":"+Gson().fromJson(""+RSAUtils.decrypt(it),
                object : TypeToken<MutableList<UserListResponse>>() {}.getType()))
            userListResponse.value =  listDataUiState

        }
        )
    }



    fun getProjectData(isRefresh: Boolean, cid: Int, isNew: Boolean = false) {
        if (isRefresh) {
            pageNo = if (isNew) 0 else 1
        }
        request({ HttpRequestCoroutine.getProjectData(pageNo, cid, isNew) }, {
            //请求成功
            pageNo++
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it.isEmpty(),
                    hasMore = it.hasMore(),
                    isFirstEmpty = isRefresh && it.isEmpty(),
                    listData = it.datas
                )
            projectDataState.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = isRefresh,
                    listData = arrayListOf<AriticleResponse>()
                )
            projectDataState.value = listDataUiState
        })
    }
}