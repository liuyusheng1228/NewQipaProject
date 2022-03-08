package com.qipa.qipaimbase.chat

import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.chat.chatset.ChatSetActivity
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.mvpbase.IModel
import com.qipa.qipaimbase.utils.mvpbase.IPresenter
import com.qipa.qipaimbase.utils.mvpbase.IView

class ChatSingleActivity : ChatBaseActivity() {
    override fun onInfoClick() {
        ChatSetActivity.startActivity(this, chatWith, singleChatUserIcon)
    }

    protected override fun getChatIcon(msg: PhotonIMMessage?): String? {
        val businessListener: ImBaseBridge.BusinessListener? = ImBaseBridge.instance?.businessListener
        var loginUserId = ""
        if (businessListener != null) {
            loginUserId = businessListener.userId.toString()
        }
        return if (msg?.from.equals(loginUserId)) myIcon else singleChatUserIcon
    }

    override val isGroup: Boolean
        get() = false

    override fun getIPresenter(): IPresenter<in IView, in IModel>? {
        TODO("Not yet implemented")
    }

}
