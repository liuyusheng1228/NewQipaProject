package com.qipa.qipaimbase.session.isession

import androidx.recyclerview.widget.RecyclerView
import com.qipa.qipaimbase.session.SessionData
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter

abstract class ISessionPresenter<V : ISessionView?, M : ISessionModel?>(iView: V) :
    IPresenter<V, M>(iView) {
    abstract fun loadHistoryData()
    abstract fun loadHistoryFromRemote()
    abstract fun getOthersInfo(sessionData: SessionData?)
    abstract fun saveSession(sessionData: SessionData?)
    abstract fun upDateSessions()
    abstract fun deleteSession(data: SessionData?)
    abstract fun clearSession(data: SessionData?)
    abstract fun updateUnRead(chatWith: String?)
    abstract fun getNewSession(chatType: Int, chatWith: String?)
    abstract fun getSessionUnRead(chatType: Int, chatWith: String?)
    abstract val allUnReadCount: Unit

    abstract fun clearSesionUnReadCount(chatType: Int, chatWith: String?)
    override fun getEmptyView(): V {
        return object : ISessionView() {
            override fun onLoadHistory(sessionData: List<SessionData>?) {}
            override fun onGetOtherInfoResult(result: JsonResult<JsonRequestResult>, sessionData: SessionData?) {}
            override fun onDeleteSession(data: SessionData?) {}
            override fun onClearSession(data: SessionData?) {}
            override fun onNewSession(sessionData: SessionData?) {}
            override fun onGetAllUnReadCount(result: Int) {}
            override fun onLoadHistoryFromRemote(sessionData: List<SessionData>?) {}
            override fun getIPresenter(): IPresenter<in IView, in IModel>? {
                return null
            }

            override fun getRecycleView(): RecyclerView? {
                return null
            }

            override fun getAdapter(): RvBaseAdapter<ItemData>? {
                return null
            }
        } as V
    }

    abstract fun resendSendingStatusMsgs()
    abstract fun updateSessionAtType(sessionData: SessionData?)
}
