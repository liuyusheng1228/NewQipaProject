package com.qipa.qipaimbase.session

import android.text.SpannableString
import com.qipa.qipaimbase.chat.emoji.EmojiUtils
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.TimeUtils
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import java.util.HashMap

class SessionData : ItemData {
    var chatWith: String? = null
    var chatType = 0
    var isIgnoreAlert = false
    var isSticky = false
    var unreadCount = 0
    var lastMsgType = 0
    var lastMsgId: String? = null
    var lastMsgFr: String? = null
    var lastMsgFrName: String? = null
    var isUpdateFromInfo = false
    var lastMsgTo: String? = null
    var lastMsgContent: String? = null
    private var lastMsgContentShow: SpannableString?
    var lastMsgTime: Long = 0
    var timeContent: String?
        private set
    var lastMsgStatus = 0
    var extra: Map<String, String>? = null

    //    private Map<String, String> sessionExtra;//保存exta里的json信息
    var orderId: Long = 0
    var draft: String? = null
    override var itemType = 0
    var itemPosition = 0
    var isShowAtTip = false
        private set
    var atMsg: SpannableString? = null
        private set

    //    public SessionData(PhotonIMMessage msg) {
    //        chatWith = msg.chatWith;
    //        chatType = msg.chatType;
    //        lastMsgFr = msg.from;
    //        lastMsgId = msg.id;
    //        lastMsgTime = msg.time;
    //        lastMsgTo = msg.to;
    //        lastMsgType = msg.messageType;
    //        itemType = Constants.ITEM_TYPE_MSG;
    //        unreadCount = 1;
    //    }
    constructor(session: PhotonIMSession) {
        chatWith = session.chatWith
        chatType = session.chatType
        isIgnoreAlert = session.ignoreAlert
        isSticky = session.sticky
        unreadCount = session.unreadCount
        lastMsgType = session.lastMsgType
        lastMsgFr = session.lastMsgFr
        lastMsgTo = session.lastMsgTo
        lastMsgContent = session.lastMsgContent
        lastMsgContentShow = EmojiUtils.generateEmojiSpan(lastMsgContent)
        lastMsgTime = session.lastMsgTime
        timeContent = if (lastMsgTime == 0L) null else TimeUtils.getTimeFormat(lastMsgTime)
        lastMsgStatus = session.lastMsgStatus
        extra = session.extra
        orderId = session.orderId
        draft = session.draft
    }

    private constructor(builder: Builder) {
        chatWith = builder.chatWith
        chatType = builder.chatType
        isIgnoreAlert = builder.ignoreAlert
        isSticky = builder.sticky
        unreadCount = builder.unreadCount
        lastMsgType = builder.lastMsgType
        lastMsgId = builder.lastMsgId
        lastMsgFr = builder.lastMsgFr
        lastMsgFrName = builder.lastMsgFrName
        isUpdateFromInfo = builder.updateFromInfo
        lastMsgTo = builder.lastMsgTo
        lastMsgContent = builder.lastMsgContent
        lastMsgContentShow = EmojiUtils.generateEmojiSpan(lastMsgContent)
        lastMsgTime = builder.lastMsgTime
        timeContent = if (lastMsgTime == 0L) null else TimeUtils.getTimeFormat(lastMsgTime)
        lastMsgStatus = builder.lastMsgStatus
        extra = builder.extra
        //        sessionExtra = builder.sessionExtra;
        orderId = builder.orderId
        draft = builder.draft
        itemType = builder.itemType
        itemPosition = builder.position
        isShowAtTip = builder.showAtTip
        atMsg = builder.generateAtMsg
    }

    //        return sessionExtra.icon;
    var icon: String?
        get() = if (extra == null) {
            null
        } else extra!!["icon"]
        //        return sessionExtra.icon;
        set(avatar) {
            if (extra == null) {
                extra = HashMap()
                avatar?.let { (extra as HashMap<String, String>).put("icon", it) }
            }

        }

    var nickName: String?
        get() = if (extra == null) {
            null
        } else extra!!["nickname"]
        set(nickname) {
            if (extra == null) {
                extra = HashMap()
                nickname?.let { (extra as HashMap<String, String>).put("nickname", it) }
            }

        }

    fun convertToPhotonIMSession(): PhotonIMSession {
        val photonIMSession = PhotonIMSession()
        photonIMSession.chatType = chatType
        photonIMSession.chatWith = chatWith!!
        photonIMSession.draft = draft!!
        //        photonIMSession.extra = extra == null ? new Gson().toJson(sessionExtra) : extra;
        photonIMSession.extra = if (extra == null) HashMap<String, String>() else extra
        photonIMSession.ignoreAlert = isIgnoreAlert
        photonIMSession.lastMsgContent = lastMsgContent!!
        photonIMSession.lastMsgFr = lastMsgFr!!
        photonIMSession.lastMsgId = lastMsgId!!
        photonIMSession.lastMsgStatus = lastMsgStatus
        photonIMSession.lastMsgTime = lastMsgTime
        photonIMSession.lastMsgTo = lastMsgTo!!
        photonIMSession.lastMsgType = lastMsgType
        photonIMSession.orderId = orderId
        photonIMSession.sticky = isSticky
        photonIMSession.unreadCount = unreadCount
        return photonIMSession
    }

    fun getExtra(nickName: String, icon: String): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map["nickname"] = nickName
        map["icon"] = icon
        return map
    }

    fun setExtra(nickName: String?, icon: String?) {
        extra = HashMap()
        if (nickName != null) {
            (extra as HashMap<String, String>)?.put("nickname", nickName)
        }
        if (icon != null) {
            (extra as HashMap<String, String>).put("icon", icon)
        }
        extra = extra
    }

    class Builder {
        var chatWith: String? = null
        var chatType = 0
        var ignoreAlert = false
        var sticky = false
        var unreadCount = 0
        var lastMsgType = 0
        var lastMsgId: String? = null
        var lastMsgFr: String? = null
        var lastMsgFrName: String? = null
        var updateFromInfo = false
        var lastMsgTo: String? = null
        var lastMsgContent: String? = null
        private var lastMsgContentShow: SpannableString? = null
        var lastMsgTime: Long = 0
        private var timeContent: String? = null
        var lastMsgStatus = 0
        var extra: Map<String, String>? = null

        //        private Map<String, String> sessionExtra;
        private var icon: String? = null
        private val nickName: String? = null
        var orderId: Long = 0
        var draft: String? = null
        var itemType = Constants.ITEM_TYPE_MSG
        var position = 0
        var showAtTip = false
        private var atMsg: SpannableString? = null
        var generateAtMsg: SpannableString? = null
        fun chatWith(`val`: String?): Builder {
            chatWith = `val`
            return this
        }

        fun chatType(`val`: Int): Builder {
            chatType = `val`
            return this
        }

        fun ignoreAlert(`val`: Boolean): Builder {
            ignoreAlert = `val`
            return this
        }

        fun sticky(`val`: Boolean): Builder {
            sticky = `val`
            return this
        }

        fun unreadCount(`val`: Int): Builder {
            unreadCount = `val`
            return this
        }

        fun lastMsgType(`val`: Int): Builder {
            lastMsgType = `val`
            return this
        }

        fun lastMsgId(`val`: String?): Builder {
            lastMsgId = `val`
            return this
        }

        fun lastMsgFr(`val`: String?): Builder {
            lastMsgFr = `val`
            return this
        }

        fun lastMsgFrName(`val`: String?): Builder {
            lastMsgFrName = `val`
            return this
        }

        fun updateFromInfo(`val`: Boolean): Builder {
            updateFromInfo = `val`
            return this
        }

        fun lastMsgTo(`val`: String?): Builder {
            lastMsgTo = `val`
            return this
        }

        fun lastMsgContent(`val`: String?): Builder {
            lastMsgContent = `val`
            return this
        }

        fun lastMsgContentShow(`val`: SpannableString?): Builder {
            lastMsgContentShow = `val`
            return this
        }

        fun lastMsgTime(`val`: Long): Builder {
            lastMsgTime = `val`
            return this
        }

        fun timeContent(`val`: String?): Builder {
            timeContent = `val`
            return this
        }

        fun lastMsgStatus(`val`: Int): Builder {
            lastMsgStatus = `val`
            return this
        }

        fun extra(`val`: Map<String, String>?): Builder {
            extra = `val`
            return this
        }

        //        public Builder sessionExtra(Map<String, String> val) {
        //            sessionExtra = val;
        //            return this;
        //        }
        fun orderId(`val`: Long): Builder {
            orderId = `val`
            return this
        }

        fun draft(`val`: String?): Builder {
            draft = `val`
            return this
        }

        fun icon(`val`: String?): Builder {
            icon = `val`
            return this
        }

        fun itemType(`val`: Int): Builder {
            itemType = `val`
            return this
        }

        fun position(`val`: Int): Builder {
            position = `val`
            return this
        }

        fun showAtTip(`val`: Boolean): Builder {
            showAtTip = `val`
            return this
        }

        fun atMsg(`val`: SpannableString?): Builder {
            atMsg = `val`
            return this
        }

        fun generateAtMsg(`val`: SpannableString?): Builder {
            generateAtMsg = `val`
            return this
        }

        fun build(): SessionData {
            return SessionData(this)
        }
    }

    inner class SessionExtra {
        /**
         * icon :
         * nickname :
         */
        var icon: String? = null
        var nickname: String? = null
    }
}
