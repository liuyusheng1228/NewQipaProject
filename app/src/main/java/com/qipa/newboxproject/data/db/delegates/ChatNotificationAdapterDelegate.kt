package com.qipa.newboxproject.data.db.delegates

import android.view.View
import android.view.ViewGroup
import com.hyphenate.easeui.interfaces.MessageListItemClickListener

import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder

import com.hyphenate.easeui.widget.chatrow.EaseChatRow

import com.hyphenate.chat.EMMessage

import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate

import com.hyphenate.chat.EMMessage.Type.TXT
import com.qipa.newboxproject.data.db.view.ChatRowNotification
import com.qipa.newboxproject.data.model.ChatNotificationViewHolder

class ChatNotificationAdapterDelegate :
    EaseMessageAdapterDelegate<EMMessage?, EaseChatRowViewHolder?>() {
    override fun isForViewType(item: EMMessage?, position: Int): Boolean {
        return item?.type == TXT && item.getBooleanAttribute(
            DemoConstant.EM_NOTIFICATION_TYPE,
            false
        )
    }

    override fun getEaseChatRow(parent: ViewGroup, isSender: Boolean): EaseChatRow {
        return ChatRowNotification(parent.getContext(), isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): ChatNotificationViewHolder? {
        return view?.let { ChatNotificationViewHolder(it, itemClickListener) }
    }
}

