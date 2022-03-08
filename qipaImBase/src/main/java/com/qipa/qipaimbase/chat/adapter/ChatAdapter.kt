package com.qipa.qipaimbase.chat.adapter

import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter

class ChatAdapter(
    baseDataList: List<*>?, onGetVoiceFileListener: ChatNormalLeftItem.OnGetVoiceFileListener?,
    onReceiveReadListener: ChatNormalLeftItem.OnReceiveReadListener?,
    onGetIconListener: ChatNormalLeftItem.OnGetIconListener?, group: Boolean
) :
    RvBaseAdapter<ItemData>(baseDataList as List<ItemData>?) {
    init {
        checkNotNull(baseDataList) { "chat list is null" }
        addItemType(
            ChatNormalLeftItem(
                onGetVoiceFileListener,
                onReceiveReadListener,
                onGetIconListener
            )
        )
        addItemType(ChatNormalRightItem(group))
        addItemType(ChatSysInfoItem())
    }
}

