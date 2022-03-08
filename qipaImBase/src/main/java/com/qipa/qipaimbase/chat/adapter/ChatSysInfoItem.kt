package com.qipa.qipaimbase.chat.adapter

import android.widget.TextView
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

class ChatSysInfoItem : ChatItemTypeAbstract() {
    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        super.fillContent(rvViewHolder, position, data)
        (rvViewHolder?.getView(R.id.tvSysInfo) as TextView).setText((data as ChatData).contentShow)
    }

    override fun openClick(): Boolean {
        return false
    }

    override val type: Int
        get() =  Constants.ITEM_TYPE_CHAT_SYSINFO

    override val layout: Int
        get() = R.layout.layout_chat_item_sysinfo



    override val onClickViews: IntArray
        get() = IntArray(0)
}
