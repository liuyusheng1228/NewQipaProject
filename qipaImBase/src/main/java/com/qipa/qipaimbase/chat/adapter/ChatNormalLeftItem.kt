package com.qipa.qipaimbase.chat.adapter

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

class ChatNormalLeftItem(
    private val onGetVoiceFileListener: OnGetVoiceFileListener?,
    private val onReceiveReadListener: OnReceiveReadListener?,
    private val onGetInfoListener: OnGetIconListener?
) :
    ChatItemTypeAbstract() {
    override fun openClick(): Boolean {
        return true
    }

    override fun openLongClick(): Boolean {
        return true
    }

    override val type: Int
        get() = Constants.ITEM_TYPE_CHAT_NORMAL_LEFT
    override val layout: Int
        get() = R.layout.layout_chat_item_normal_left

    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        super.fillContent(rvViewHolder, position, data)
        rvViewHolder?.let { fillMsgContent(it) }
        if (chatData?.msgType === PhotonIMMessage.AUDIO && TextUtils.isEmpty((data as ChatData).localFile)) {
            onGetVoiceFileListener?.onGetVoice(data as ChatData)
        }
        when (chatData?.msgStatus) {
            PhotonIMMessage.RECALL -> {
                rvViewHolder?.getView(R.id.llMsgRoot)?.setVisibility(View.GONE)
                rvViewHolder?.getView(R.id.tvSysInfo)?.setVisibility(View.VISIBLE)
                (rvViewHolder?.getView(R.id.tvSysInfo) as TextView).setText(chatData?.contentShow)
            }
        }
        if (chatData?.chatType === PhotonIMMessage.SINGLE && chatData?.msgStatus !== PhotonIMMessage.RECV_READ && chatData?.msgStatus !== PhotonIMMessage.RECALL) {
            onReceiveReadListener?.onReceiveRead(data as ChatData)
        }
        Log.i("Api", "type:" + chatData?.chatType)
        if (chatData?.chatType === PhotonIMMessage.GROUP) {
            val view = rvViewHolder?.getView(R.id.tvSenderName) as TextView
            view.visibility = View.VISIBLE
            view.text = (if (TextUtils.isEmpty(chatData?.fromName)) chatData?.from else chatData?.fromName)
        } else {
            rvViewHolder?.getView(R.id.tvSenderName)?.setVisibility(View.GONE)
        }
        if (chatData?.icon == null ||
            chatData?.chatType === PhotonIMMessage.GROUP && TextUtils.isEmpty(chatData?.fromName)
        ) {
            onGetInfoListener?.onGetUserInfo(data as ChatData?)
        }
    }

    override val onClickViews: IntArray
        get() = intArrayOf(R.id.tvContent, R.id.llVoice, R.id.ivPic)
    override val onLongClickViews: IntArray
        get() = intArrayOf(R.id.tvContent, R.id.llVoice, R.id.ivPic)

    interface OnGetVoiceFileListener {
        fun onGetVoice(data: ChatData?)
    }

    interface OnReceiveReadListener {
        fun onReceiveRead(data: ChatData?)
    }

    interface OnGetIconListener {
        fun onGetUserInfo(chatData: ChatData?)
    }
}
