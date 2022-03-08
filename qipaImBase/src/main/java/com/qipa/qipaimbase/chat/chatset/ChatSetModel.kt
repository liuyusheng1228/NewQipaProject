package com.qipa.qipaimbase.chat.chatset

import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.chat.chatset.ichatset.IChatSetModel
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.task.AsycTaskUtil
import com.qipa.qipaimbase.utils.task.TaskExecutor

class ChatSetModel : IChatSetModel() {
    override fun changeTopStatus(listener: OnChangeStatusListener?) {}
    override fun changeIgnoreStatus(
        chatType: Int,
        remoteId: String?,
        open: Boolean,
        listener: OnChangeStatusListener?
    ) {
        TaskExecutor.instance.createAsycTask({
            remoteId?.let {
                changeIgnoreStatusInner(
                    chatType,
                    it,
                    open,
                    listener
                )
            }
        },object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                if (listener != null) {
                    listener.onChangeIgnoreStatus(result as JsonResult<JsonRequestResult>)
                }
            }

        })
    }

    override fun getIgnoreStatus(
        userId: String?,
        onGetIgnoreStatusListener: OnGetIgnoreStatusListener?
    ) {
        val businessListener: ImBaseBridge.BusinessListener = ImBaseBridge.instance?.businessListener
            ?: return
        TaskExecutor.instance
            .createAsycTask({ businessListener.getIgnoreStatus(userId) },object : AsycTaskUtil.OnTaskListener{
                override fun onTaskFinished(result: Any?) {
                    if (onGetIgnoreStatusListener != null) {
                        onGetIgnoreStatusListener.onGetIgnoreStatus(result as JsonResult<JsonRequestResult>)
                    }
                }

            })
    }

    private fun changeIgnoreStatusInner(
        chatType: Int,
        remoteId: String,
        igoreAlert: Boolean,
        listener: OnChangeStatusListener?
    ): Any? {
        val businessListener: ImBaseBridge.BusinessListener = ImBaseBridge.instance?.businessListener
            ?: return null
        val jsonResult: JsonResult<*> = businessListener.setIgnoreStatus(remoteId, igoreAlert)
        if (jsonResult.success()) {
            //待修改
//            PhotonIMDatabase.getInstance().updateSessionIgnoreAlert(chatType, remoteId, igoreAlert)
        }
        return jsonResult
    }
}
