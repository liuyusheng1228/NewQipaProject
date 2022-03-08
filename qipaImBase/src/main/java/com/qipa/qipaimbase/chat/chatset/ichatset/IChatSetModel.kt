package com.qipa.qipaimbase.chat.chatset.ichatset

import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.http.jsons.JsonSaveIgnoreInfo
import com.qipa.qipaimbase.utils.mvpbase.IModel

abstract class IChatSetModel : IModel {
    abstract fun changeTopStatus(listener: OnChangeStatusListener?)
    abstract fun changeIgnoreStatus(
        chatType: Int,
        remoteId: String?,
        open: Boolean,
        listener: OnChangeStatusListener?
    )

    abstract fun getIgnoreStatus(
        userId: String?,
        onGetIgnoreStatusListener: OnGetIgnoreStatusListener?
    )

    interface OnChangeStatusListener {
        fun onChangeTopStatus(result: JsonSaveIgnoreInfo?)
        fun onChangeIgnoreStatus(success: JsonResult<JsonRequestResult>)
    }

    interface OnGetIgnoreStatusListener {
        fun onGetIgnoreStatus(result: JsonResult<JsonRequestResult>)
    }
}