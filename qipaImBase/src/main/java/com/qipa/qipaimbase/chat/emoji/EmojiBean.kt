package com.qipa.qipaimbase.chat.emoji

import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.recycleadapter.ItemData

class EmojiBean : ItemData {
    var emojiId = 0
    var emojiContent: String? = null
    override val itemType: Int
        get() = Constants.ITEM_TYPE_EMOJI
}
