package com.qipa.qipaimbase.chat.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

class ChatNormalRightItem(private val group: Boolean) : ChatItemTypeAbstract() {
    override fun openClick(): Boolean {
        return true
    }

    override fun openLongClick(): Boolean {
        return true
    }

    override val type: Int
        get() = Constants.ITEM_TYPE_CHAT_NORMAL_RIGHT
    override val layout: Int
        get() = R.layout.layout_chat_item_normal_right

    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        super.fillContent(rvViewHolder, position, data)
        if (rvViewHolder != null) {
            fillMsgContent(rvViewHolder)
        }
        val illegal = rvViewHolder?.getView(R.id.tvStatus) as TextView
        if (!TextUtils.isEmpty(chatData?.notic)) {
            illegal.setText(chatData?.notic)
            rvViewHolder.getView(R.id.ivWarn).setVisibility(View.VISIBLE)
        } else {
            rvViewHolder.getView(R.id.ivWarn).setVisibility(View.GONE)
            when (chatData?.msgStatus) {
                PhotonIMMessage.SENDING -> {
                    illegal.visibility = View.VISIBLE
                    illegal.text = "发送中"
                }
                PhotonIMMessage.SENT_READ -> {
                    illegal.visibility = View.VISIBLE
                    illegal.text = "已读"
                }
                PhotonIMMessage.SENT -> if (!group) {
                    illegal.visibility = View.VISIBLE
                    illegal.text = "已送达"
                } else {
                    illegal.visibility = View.GONE
                }
                PhotonIMMessage.SEND_FAILED -> {
                    illegal.visibility = View.VISIBLE
                    illegal.text = "发送失败"
                    rvViewHolder.getView(R.id.ivWarn).setVisibility(View.VISIBLE)
                }
                PhotonIMMessage.RECALL -> {
                    rvViewHolder.getView(R.id.llMsgRoot).setVisibility(View.GONE)
                    rvViewHolder.getView(R.id.tvSysInfo).setVisibility(View.VISIBLE)
                    rvViewHolder.getView(R.id.tvStatus).setVisibility(View.GONE)
                    (rvViewHolder.getView(R.id.tvSysInfo) as TextView).setText(chatData?.contentShow)
                }
                else -> illegal.text = ""
            }
        }
    }

    override val onClickViews: IntArray
        get() = intArrayOf(R.id.tvContent, R.id.llVoice, R.id.ivWarn, R.id.ivPic)
    override val onLongClickViews: IntArray
        get() = intArrayOf(R.id.tvContent, R.id.llVoice, R.id.ivPic)

}
