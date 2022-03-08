package com.qipa.qipaimbase.chat.chatset.ichatset

import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView

abstract class IChatSetPresenter<V : IChatSetView?, M : IChatSetModel?>(iView: V) :
    IPresenter<V, M>(iView) {
    abstract fun changeTopStatus()
    abstract fun changeIgnoreStatus(remoteId: String?, open: Boolean)
    abstract fun getIgnoreStatus(remoteId: String?)
    override fun getEmptyView(): V {
        return object : IChatSetView() {
            override fun onTopChangeStatusResult(success: Boolean) {}
            override fun onIgnoreChangeStatusResult(success: Boolean) {
            }

            override fun onGetIgnoreStatus(result: JsonResult<JsonRequestResult>) {}

            override fun getIPresenter(): IPresenter<in IView, in IModel>? {
                return null
            }




        } as V
    }
}
