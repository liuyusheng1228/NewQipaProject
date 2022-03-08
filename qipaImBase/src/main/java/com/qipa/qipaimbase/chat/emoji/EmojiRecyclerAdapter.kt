package com.qipa.qipaimbase.chat.emoji

import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.RvBaseAdapter


class EmojiRecyclerAdapter(baseDataList: List<*>?) :
    RvBaseAdapter<ItemData>(baseDataList as List<ItemData>?) {
    init {
        addItemType(EmojiReycycleItem())
    }
}
