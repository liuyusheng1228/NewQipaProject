package com.qipa.qipaimbase.utils.event

import com.qipa.qipaimbase.chat.ChatData

class ChatDataWrapper(chatData: ChatData, code: Int, msg: String?) {
    var chatData: ChatData
    var code: Int
    var msg: String?

    init {
        this.chatData = chatData
        this.code = code
        this.msg = msg
    }
}
