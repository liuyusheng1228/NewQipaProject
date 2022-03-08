package com.qipa.qipaimbase.chat.emoji

import android.widget.ImageView
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.utils.Constants
import com.qipa.qipaimbase.utils.image.ImageLoaderUtils
import com.qipa.qipaimbase.utils.recycleadapter.ItemData
import com.qipa.qipaimbase.utils.recycleadapter.ItemTypeAbstract
import com.qipa.qipaimbase.utils.recycleadapter.RvViewHolder

class EmojiReycycleItem : ItemTypeAbstract() {
    override fun openClick(): Boolean {
        return true
    }

    override val type: Int
        get() = Constants.ITEM_TYPE_EMOJI

    override val layout: Int
        get() = R.layout.item_emoji

    override fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?) {
        val bean = data as EmojiBean
        val view = rvViewHolder?.getView(R.id.ivEmoji) as ImageView
        ImageLoaderUtils.getInstance().loadResImage(view.context, bean.emojiId, view)
    }



    override val onClickViews: IntArray
        get() = intArrayOf(R.id.ivEmoji)
}
