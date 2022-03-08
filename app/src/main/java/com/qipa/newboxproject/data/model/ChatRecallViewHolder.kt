package com.qipa.newboxproject.data.model

import android.view.View
import android.view.ViewGroup
import com.qipa.newboxproject.data.db.view.ChatRowRecall

import com.hyphenate.easeui.interfaces.MessageListItemClickListener

import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder


class ChatRecallViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener) {
    companion object {
        fun create(
            parent: ViewGroup, isSender: Boolean,
            listener: MessageListItemClickListener?
        ): ChatRecallViewHolder {
            return ChatRecallViewHolder(ChatRowRecall(parent.getContext(), isSender), listener)
        }
    }
}
