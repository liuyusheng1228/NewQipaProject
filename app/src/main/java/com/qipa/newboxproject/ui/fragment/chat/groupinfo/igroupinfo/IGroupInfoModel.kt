package com.qipa.newboxproject.ui.fragment.chat.groupinfo.igroupinfo

import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel

abstract class IGroupInfoModel : IModel {
    abstract fun getGroupInfo(
        sessionId: String?,
        userId: String?,
        gid: String?,
        onGetGroupInfoListener: OnGetGroupInfoListener?
    )

    abstract fun getGroupIgnoreStatus(
        sessionId: String?,
        userId: String?,
        gid: String?,
        onGetGroupIgnoreStatusListener: OnGetGroupIgnoreStatusListener?
    )

    abstract fun changeGroupIgnoreStatus(
        sessionId: String?,
        userId: String?,
        gid: String?,
        switchX: Int,
        onChangeGroupIgnoreStatusListener: OnChangeGroupIgnoreStatusListener?
    )

    interface OnGetGroupInfoListener {
        fun onGetGroupInfoResult(jsonResult: JsonResult<JsonRequestResult>)
    }

    interface OnGetGroupIgnoreStatusListener {
        fun onGetGroupIgnoreStatus(jsonResult: JsonResult<JsonRequestResult>)
    }

    interface OnChangeGroupIgnoreStatusListener {
        fun onChangeGroupIgnoreStatus(jsonResult: JsonResult<JsonRequestResult>)
    }
}
