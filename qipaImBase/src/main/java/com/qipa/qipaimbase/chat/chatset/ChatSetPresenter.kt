package com.qipa.qipaimbase.chat.chatset


import com.qipa.qipaimbase.chat.chatset.ichatset.IChatSetModel
import com.qipa.qipaimbase.chat.chatset.ichatset.IChatSetPresenter
import com.qipa.qipaimbase.chat.chatset.ichatset.IChatSetView
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.http.jsons.JsonSaveIgnoreInfo

class ChatSetPresenter(iView: IChatSetView?) :
    IChatSetPresenter<IChatSetView?, IChatSetModel?>(iView), IChatSetModel.OnChangeStatusListener {
    override fun generateIModel(): IChatSetModel {
        return ChatSetModel()
    }

    override fun changeTopStatus() {
        getiModel()?.changeTopStatus(this)
    }

    override fun changeIgnoreStatus(remoteId: String?, open: Boolean) {
        getiModel()?.changeIgnoreStatus(PhotonIMMessage.SINGLE, remoteId, open, this)
    }

    override fun getIgnoreStatus(remoteId: String?) {
        getiModel()?.getIgnoreStatus(remoteId,object : IChatSetModel.OnGetIgnoreStatusListener {

            override fun onGetIgnoreStatus(result: JsonResult<JsonRequestResult>) {
                getIView()?.onGetIgnoreStatus(result as JsonResult<JsonRequestResult>)
            }

        })
    }

    override fun onChangeTopStatus(result: JsonSaveIgnoreInfo?) {
        getIView()?.onTopChangeStatusResult(result != null && result.success())
    }

    override fun onChangeIgnoreStatus(success: JsonResult<JsonRequestResult>) {
        getIView()?.onIgnoreChangeStatusResult(success.success())
    }




}
