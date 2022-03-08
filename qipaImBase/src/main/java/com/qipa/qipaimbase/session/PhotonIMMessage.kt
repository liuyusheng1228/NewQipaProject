package com.qipa.qipaimbase.session

import java.util.ArrayList

class PhotonIMMessage {
    var id = ""
    var chatWith = ""
    var from = ""
    var to = ""
    var time: Long = 0
    var messageType = 0
    var status = 0
    var chatType = 0
    var content = ""
    var notic = ""
    var mediaTime: Long = 0
    var whRatio = 0.0
    var fileUrl = ""
    var thumbUrl = ""
    var localFile = ""
    var localMediaPlayed = false
    var extra: Map<String, String>? = null
    var customArg1 = 0
    var customArg2 = 0
    lateinit var customData: ByteArray
    var msgId: List<String?> = ArrayList<String?>()
    var saveMessageOnServerValue = true
        private set
    var sendPushValue = true
        private set
    var remainHistory = false
    var msgAtList: Any? = ArrayList<Any?>()
    var atType = 0
    fun unSaveMessageOnServer() {
        saveMessageOnServerValue = false
    }

    fun unSendPush() {
        sendPushValue = false
    }

    companion object {
        const val UNKNOW = 0
        const val RAW = 1
        const val TEXT = 2
        const val IMAGE = 3
        const val AUDIO = 4
        const val VIDEO = 5
        const val SINGLE = 1
        const val GROUP = 2
        const val CUSTOMMSG = 3
        const val DEFAULT = 0
        const val RECALL = 1
        const val SENDING = 2
        const val SEND_FAILED = 3
        const val SENT = 4
        const val SENT_READ = 5
        const val RECV_READ = 6
        const val MSG_NO_AT = 0
        const val MSG_NO_AT_ALL = 1
        const val MSG_AT_ALL = 2
        const val SESSION_NO_AT = 0
        const val SESSION_AT_ME = 1
        const val SESSION_AT_ALL = 2
    }
}