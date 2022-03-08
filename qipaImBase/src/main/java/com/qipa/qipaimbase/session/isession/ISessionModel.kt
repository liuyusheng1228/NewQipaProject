package com.qipa.qipaimbase.session.isession

import com.qipa.qipaimbase.session.SessionData
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel

abstract class ISessionModel : IModel {
    abstract fun loadLocalHostoryMsg(onLoadHistoryListener: OnLoadHistoryListener?)

    abstract fun getOtherInfo(
        sessionData: SessionData?,
        onGetOtherInfoListener: OnGetOtherInfoListener?
    )

    abstract fun saveSession(sessionData: SessionData?)

    abstract fun deleteSession(
        data: SessionData?,
        onDeleteSessionListener: OnDeleteSessionListener?
    )

    abstract fun clearSession(data: SessionData?, onClearSessionListener: OnClearSessionListener?)

    abstract fun getNewSession(
        chatType: Int,
        chatWith: String?,
        onGetSessionListener: OnGetSessionListener?
    )

    abstract fun getAllUnReadCount(onGetAllUnReadCount: OnGetAllUnReadCount?)

    abstract fun updateSessionUnreadCount(chatType: Int, chatWith: String?, unReadCount: Int)

    abstract fun loadHistoryFromRemote(onLoadHistoryFromRemoteListener: OnLoadHistoryFromRemoteListener?)

    abstract fun resendSendingStatusMsgs()

    abstract fun updateSessionAtType(sessionData: SessionData?)


    interface OnLoadHistoryListener {
        fun onLoadHistory(sessionData: List<SessionData?>?)
    }

    interface OnLoadHistoryFromRemoteListener {
        fun onLoadHistoryFromRemote(sessionData: List<SessionData?>?)
    }

    interface OnGetOtherInfoListener {
        fun onGetOtherInfo(result: JsonResult<JsonRequestResult>)
    }

    interface OnDeleteSessionListener {
        fun onDeleteSession()
    }

    interface OnClearSessionListener {
        fun onClearSession()
    }


    interface OnGetSessionListener {
        fun onGetSession(sessionData: SessionData?)
    }

    interface OnGetAllUnReadCount {
        fun onGetAllUnReadCount(result: Int)
    }
}
