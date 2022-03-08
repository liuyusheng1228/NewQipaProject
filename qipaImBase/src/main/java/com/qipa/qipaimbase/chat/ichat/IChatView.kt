package com.qipa.qipaimbase.chat.ichat

import android.os.Bundle
import androidx.annotation.Nullable
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.utils.mvpbase.IView
import com.qipa.qipaimbase.utils.recycleadapter.actiivty.RvBaseActivity

abstract class IChatView : RvBaseActivity(), IView {
    protected var chatPresenter: IChatPresenter<*, *>? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatPresenter = (getIPresenter() as IChatPresenter?)!!
        checkNotNull(chatPresenter) { "chatPresenter is null" }
    }

    abstract fun onloadHistoryResult(
        chatData: List<ChatData>?,
        chatDataMap: Map<String?, ChatData>?
    )

    abstract fun onRevertResult(data1: ChatData?, error: Int, msg: String?)
    abstract fun onGetChatVoiceFileResult(data: ChatData?, path: String?)
    abstract fun onRecordFinish(duration: Long)
    abstract fun updateUnreadStatus(data: ChatData?)
    abstract fun onGetIcon(chatData: ChatData?, iconUrl: String?, name: String?)
    abstract fun onRecordFailed()
}
