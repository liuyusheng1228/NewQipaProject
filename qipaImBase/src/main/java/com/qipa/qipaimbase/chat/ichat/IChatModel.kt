package com.qipa.qipaimbase.chat.ichat

import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import com.qipa.qipaimbase.utils.mvpbase.IModel

abstract class IChatModel : IModel {
    abstract fun loadLocalHistory(
        chatType: Int, chatWith: String?, anchorMsgId: String?,
        beforeAuthor: Boolean, asc: Boolean,
        size: Int, myId: String?, listener: OnLoadHistoryListener?
    )

    abstract fun loadAllHistory(
        chatType: Int,
        chatWith: String?,
        size: Int,
        beginTimeStamp: Long,
        listener: OnLoadHistoryListener?
    )

    abstract fun loadAllHistory(
        chatType: Int,
        chatWith: String?,
        size: Int,
        beginTimeStamp: Long,
        endTimeStamp: Long,
        listener: OnLoadHistoryListener?
    )

    //    public abstract void loadRemoteHistorey(OnLoadHistoryListener listener);
    //    public abstract void sendTextMsg(int chatType, String messagId, String content, String chatWith, String fromId, String toId,
    //                                     String icon, OnMsgSendListener onMsgSendListener);
    //
    //    public abstract void sendTextMsg(int chatType, String content, String chatWith, String fromId, String toId,
    //                                     String icon, OnMsgSendListener onMsgSendListener);
    //
    //    public abstract void uploadPic(int chatType, String absolutePath, String icon, String chatWith, String from,
    //                                   String to, OnMsgSendListener onMsgSendListener, OnPicUploadListener onPicUploadListener);
    //
    //    public abstract void uploadPic(int chatType, String messageId, String absolutePath, String icon, String chatWith, String from,
    //                                   String to, OnMsgSendListener onMsgSendListener, OnPicUploadListener onPicUploadListener);
    abstract fun uploadPic(chatData: ChatData?, onPicUploadListener: OnPicUploadListener?)

    //    public abstract void uploadVoiceFile(int chatType, String absolutePath, long time, String icon, String chatWith,
    //                                         String from, String to, OnMsgSendListener onMsgSendListener, OnVoiceUploadListener onVoiceUploadListener);
    //
    //    public abstract void uploadVoiceFile(int chatType, String msgId, String absolutePath, long time, String icon, String chatWith,
    //                                         String from, String to, OnMsgSendListener onMsgSendListener, OnVoiceUploadListener onVoiceUploadListener);
    abstract fun uploadVoiceFile(chatData: ChatData?, onVoiceUploadListener: OnVoiceUploadListener?)
    abstract fun sendTextMsg(chatData: ChatData?)
    abstract fun sendPicMsg(chatData: ChatData?, onMsgSendListener: OnMsgSendListener?)
    abstract fun sendMsgMulti(chatDatas: List<ChatData?>?, onMsgSendListener: OnMsgSendListener?)
    abstract fun sendVoiceFileMsg(chatData: ChatData?, onMsgSendListener: OnMsgSendListener?)
    abstract fun revertMsg(data: ChatData?, onRevertListener: OnRevertListener?)
    abstract fun sendReadMsg(messageData: ChatData?, onSendReadListener: OnSendReadListener?)
    abstract fun getVoiceFile(
        chatData: ChatData?,
        savePath: String?,
        onGetFileListener: OnGetFileListener?
    )

    abstract fun updateExtra(messageData: ChatData?)
    abstract fun removeChat(chatType: Int, chatWith: String?, id: String?)
    abstract fun updateStatus(chatType: Int, chatWith: String?, id: String?, status: Int)
    interface OnLoadHistoryListener {
        fun onLoadHistory(chatData: List<ChatData?>?, chatDataMap: Map<String?, ChatData?>?)
    }

    interface OnMsgSendListener {
        fun onMsgSend(code: Int, codeMsg: String?, chatData: ChatData?)
    }

    interface OnPicUploadListener {
        fun onPicUpload(chatData: ChatData?, result: JsonResult<JsonRequestResult>)
    }

    interface OnVoiceUploadListener {
        fun onVoiceFileUpload(chatData: ChatData?, result: JsonResult<JsonRequestResult>)
    }

    interface OnRevertListener {
        fun onRevert(data: ChatData?, error: Int, msg: String?)
    }

    interface OnSendReadListener {
        fun onSendRead(data: ChatData?, error: Int, msg: String?)
    }

    interface OnGetFileListener {
        fun onGetFile(path: String?)
    }
}
