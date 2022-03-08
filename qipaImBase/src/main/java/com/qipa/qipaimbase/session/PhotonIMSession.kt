package com.qipa.qipaimbase.session

class PhotonIMSession {
    var chatWith = ""
    var chatType = 0
    var ignoreAlert = false
    var sticky = false
    var unreadCount = 0
    var lastMsgType = 0
    var lastMsgId = ""
    var lastMsgFr = ""
    var lastMsgTo = ""
    var lastMsgContent = ""
    var lastMsgTime: Long = 0
    var lastMsgStatus = 0
    var extra: Map<String, String>? = null
    var orderId: Long = 0
    var draft = ""
    var lastMsgArg1 = 0
    var lastMsgArg2 = 0
    var atType = 0
}
