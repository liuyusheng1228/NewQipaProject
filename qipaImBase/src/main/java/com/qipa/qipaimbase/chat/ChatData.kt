package com.qipa.qipaimbase.chat

import android.os.Parcel
import android.os.Parcelable
import android.text.SpannableString
import android.text.TextUtils
import com.qipa.qipaimbase.chat.emoji.EmojiUtils
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.session.PhotonIMSession
import com.qipa.qipaimbase.utils.AtSpan
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import java.io.Serializable


class ChatData : ItemData, Parcelable {
    var msgId: String?
    var icon: String? = null
    private var content: String?
    var contentShow: SpannableString? = null
        private set
    var mediaTime: Long
        private set
    var time: Long
    var msgStatus = 0

    //    private boolean illegal;
    var chatWith: String? = null
    var from: String? = null
    var fromName: String? = null
    var to: String? = null
    var fileUrl: String? = null
    var localFile: String? = null
    var chatType // single group
            = 0

    //    public boolean isIllegal() {
    //        return illegal;
    //    }
    var msgType // Text image
            : Int
    var listPostion //list中的位置
            = 0
    override var itemType: Int = Constants.ITEM_TYPE_CHAT_NORMAL_LEFT
        private set
    var timeContent: String? = null
    var notic: String? = null
    private var atType = 0
    private var msgAtList: List<String>? = null

    //    public void setIllegal(boolean illegal) {
    //        this.illegal = illegal;
    //    }
    var isRemainHistory = false
        private set

    //    private MsgExtra extra;
    private constructor(builder: Builder) {
        msgId = builder.msgId
        //        icon = builder.icon;
        content = builder.content
        contentShow = EmojiUtils.generateEmojiSpan(content)
        mediaTime = builder.voiceDuration
        time = builder.time
        //        extra = builder.extra;
        msgStatus = builder.msgStatus
        //        setIllegal(builder.illegal);
        chatWith = builder.chatWith
        from = builder.from
        fromName = builder.fromName
        to = builder.to
        icon = builder.icon
        fileUrl = builder.fileUrl
        localFile = builder.localFile
        chatType = builder.chatType
        notic = builder.notic
        msgType = builder.msgType
        //        setListPostion(builder.listPostion);
        itemType = builder.itemType
        timeContent = builder.timeContent
        atType = builder.atType
        msgAtList = builder.msgAtList
        isRemainHistory = builder.remainHistory
    }

    //    public String getInfo() {
    //        if (extra == null) {
    //            return icon;
    //        }
    //        return extra.getInfo();
    //    }
    fun setContent(content: String?) {
        this.content = content
        contentShow = EmojiUtils.generateEmojiSpan(content)
    }

    //    public void icon(String icon) {
    //        if (extra == null) {
    //            extra = new MsgExtra();
    //        }
    //        extra.icon = icon;
    //    }
    fun getContent(): String? {
        return content
    }

    //    public String getExtra() {
    //        if (extra == null) {
    //            return null;
    //        }
    //        return new Gson().toJson(extra);
    //    }
    fun convertToIMMessage(): PhotonIMMessage {
        val photonIMMessage = PhotonIMMessage()
        photonIMMessage.id = msgId.toString()
        photonIMMessage.chatWith = chatWith.toString()
        photonIMMessage.from = from.toString()
        photonIMMessage.to = to.toString()
        photonIMMessage.time = time
        photonIMMessage.messageType = msgType
        photonIMMessage.status = msgStatus
        photonIMMessage.chatType = chatType
        photonIMMessage.content = content.toString()
        photonIMMessage.mediaTime = mediaTime
        photonIMMessage.fileUrl = fileUrl ?: ""
        photonIMMessage.thumbUrl = fileUrl ?: ""
        photonIMMessage.localFile = localFile.toString()
        photonIMMessage.whRatio = 0.0
        photonIMMessage.msgAtList = msgAtList
        photonIMMessage.atType = atType
        //        getMsgAtStatus(photonIMMessage);
//        photonIMMessage.extra = getExtra();
        return photonIMMessage
    }

    private fun getMsgAtStatus(photonIMMessage: PhotonIMMessage) {
        if (TextUtils.isEmpty(photonIMMessage.content)) {
            return
        }
        if (!photonIMMessage.content.contains("@")) {
            return
        }
        if (photonIMMessage.content.contains("@ 所有人")) {
            photonIMMessage.atType = PhotonIMMessage.MSG_AT_ALL
            //            photonIMMessage.msgAtList;
        } else {
            val spannableString = SpannableString(photonIMMessage.content)
            val spans: Array<AtSpan> = spannableString.getSpans(
                0, photonIMMessage.content.length,
                AtSpan::class.java
            )
            if (spans.size != 0) {
                for (span in spans) {
                }
            }
        }
        //        photonIMMessage.atType;
//        photonIMMessage.msgAtList;
    }

    fun convertToImSession(): PhotonIMSession {
        val photonIMSession = PhotonIMSession()
        photonIMSession.chatType = chatType
        photonIMSession.chatWith = chatWith.toString()
        photonIMSession.lastMsgContent = content.toString()
        photonIMSession.lastMsgFr = from.toString()
        photonIMSession.lastMsgId = msgId.toString()
        photonIMSession.lastMsgStatus = msgStatus
        photonIMSession.lastMsgTime = time
        photonIMSession.lastMsgTo = to.toString()
        photonIMSession.lastMsgType = msgType
        photonIMSession.orderId = System.currentTimeMillis()
        return photonIMSession
    }

    class Builder  //        private MsgExtra extra;
        : Serializable {
        var msgId: String? = null

        //        private String icon;
        var content: String? = null
        var time: Long = 0
        var msgStatus = 0
        var illegal = true
        var msgType = 0
            private set
        var chatWith: String? = null
        var from: String? = null
        var fromName: String? = null
        var to: String? = null
        var fileUrl: String? = null
        var localFile: String? = null
        var chatType = 0
        var listPostion = 0
        var itemType = 0
        var timeContent: String? = null
        var voiceDuration: Long = 0
        var icon: String? = null
        var notic: String? = null
        var atType = 0
        var msgAtList: List<String>? = null
        var remainHistory = false
        fun msgId(`val`: String?): Builder {
            msgId = `val`
            return this
        }

        //        public Builder icon(String val) {
        //            icon = val;
        //            return this;
        //        }
        fun content(`val`: String?): Builder {
            content = `val`
            return this
        }

        fun time(`val`: Long): Builder {
            time = `val`
            return this
        }

        fun msgStatus(`val`: Int): Builder {
            msgStatus = `val`
            return this
        }

        fun illegal(`val`: Boolean): Builder {
            illegal = `val`
            return this
        }

        fun msgType(`val`: Int): Builder {
            msgType = `val`
            return this
        }

        fun chatWith(`val`: String?): Builder {
            chatWith = `val`
            return this
        }

        fun from(`val`: String?): Builder {
            from = `val`
            return this
        }

        fun fromName(`val`: String?): Builder {
            fromName = `val`
            return this
        }

        fun to(`val`: String?): Builder {
            to = `val`
            return this
        }

        fun fileUrl(`val`: String?): Builder {
            fileUrl = `val`
            return this
        }

        fun localFile(`val`: String?): Builder {
            localFile = `val`
            return this
        }

        fun chatType(`val`: Int): Builder {
            chatType = `val`
            return this
        }

        fun listPostion(`val`: Int): Builder {
            listPostion = `val`
            return this
        }

        fun itemType(`val`: Int): Builder {
            itemType = `val`
            return this
        }

        fun timeContent(`val`: String?): Builder {
            timeContent = `val`
            return this
        }

        fun build(): ChatData {
            return ChatData(this)
        }

        fun voiceDuration(`val`: Long): Builder {
            voiceDuration = `val`
            return this
        }

        fun icon(icon: String?): Builder {
            this.icon = icon
            return this
        }

        fun notic(notic: String?): Builder {
            this.notic = notic
            return this
        }

        fun remainHistory(remainHistory: Boolean): Builder {
            this.remainHistory = remainHistory
            return this
        }

        fun atType(atType: Int): Builder {
            this.atType = atType
            return this
        }

        fun msgAtList(msgAtList: List<String>?): Builder {
            this.msgAtList = msgAtList
            return this
        } //        public Builder extra(String extra) {
        //            this.extra = new Gson().fromJson(extra, MsgExtra.class);
        //            return this;
        //        }
        //
        //
        //        public Builder icon(String icon) {
        //            if (extra == null) {
        //                extra = new MsgExtra();
        //            }
        //            extra.icon = icon;
        //            return this;
        //        }
    }

    class MsgExtra : Serializable {
        var icon: String? = null
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(msgId)
        dest.writeString(icon)
        dest.writeString(content)
        //        dest.writeParcelable(this.contentShow, flags);
        dest.writeLong(mediaTime)
        dest.writeLong(time)
        dest.writeInt(msgStatus)
        //        dest.writeByte(this.illegal ? (byte) 1 : (byte) 0);
        dest.writeString(notic)
        dest.writeString(chatWith)
        dest.writeString(from)
        dest.writeString(fromName)
        dest.writeString(to)
        dest.writeString(fileUrl)
        dest.writeString(localFile)
        dest.writeInt(chatType)
        dest.writeInt(msgType)
        dest.writeInt(listPostion)
        dest.writeInt(itemType)
        dest.writeString(timeContent)
    }

    protected constructor(`in`: Parcel) {
        msgId = `in`.readString()
        icon = `in`.readString()
        content = `in`.readString()
        //        this.contentShow = in.readParcelable(SpannableString.class.getClassLoader());
        mediaTime = `in`.readLong()
        time = `in`.readLong()
        msgStatus = `in`.readInt()
        //        this.illegal = in.readByte() != 0;
        notic = `in`.readString()
        chatWith = `in`.readString()
        from = `in`.readString()
        fromName = `in`.readString()
        to = `in`.readString()
        fileUrl = `in`.readString()
        localFile = `in`.readString()
        chatType = `in`.readInt()
        msgType = `in`.readInt()
        listPostion = `in`.readInt()
        itemType = `in`.readInt()
        timeContent = `in`.readString()
    }

    companion object {
        const val CHAT_STATUS_SENTED = 1
        const val CHAT_STATUS_READ = 2
        const val CHAT_STATUS_FAILED = 3
        @JvmField
        val CREATOR: Parcelable.Creator<ChatData?> = object : Parcelable.Creator<ChatData?> {
            override fun createFromParcel(source: Parcel): ChatData? {
                return ChatData(source)
            }

            override fun newArray(size: Int): Array<ChatData?> {
                return arrayOfNulls(size)
            }
        }
    }
}