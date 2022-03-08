package com.qipa.newboxproject.viewmodel.request

import androidx.lifecycle.MutableLiveData
import com.qipa.newboxproject.app.network.stateCallback.ListDataUiState
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.data.repository.request.HttpRequestCoroutine
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.ext.request

/**
 * 描述　:
 */
class RequestGameDetailViewModel : BaseViewModel() {

    //页码
    var pageNo = 1

//    var titleData: MutableLiveData<ResultState<ArrayList<ClassifyResponse>>> = MutableLiveData()

    var projectDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    fun getProjectTitleData() {
//        request({ apiService.getProjecTitle() }, titleData)
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