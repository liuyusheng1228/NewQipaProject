package com.qipa.newboxproject.data.db.delegates

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate
import com.hyphenate.easeui.interfaces.MessageListItemClickListener
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder
import com.hyphenate.easeui.widget.chatrow.EaseChatRow
import com.qipa.newboxproject.data.db.view.ChatRowRecall
import com.qipa.newboxproject.data.model.ChatRecallViewHolder


class ChatRecallAdapterDelegate :
    EaseMessageAdapterDelegate<EMMessage?, EaseChatRowViewHolder?>() {
    override fun isForViewType(item: EMMessage?, position: Int): Boolean {
        return item?.type == EMMessage.Type.TXT && item.getBooleanAttribute(
            DemoConstant.MESSAGE_TYPE_RECALL,
            false
        )
    }

    override fun getEaseChatRow(parent: ViewGroup, isSender: Boolean): EaseChatRow {
        return ChatRowRecall(parent.getContext(), isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): ChatRecallViewHolder? {
        return view?.let { ChatRecallViewHolder(it, itemClickListener) }
    }
}