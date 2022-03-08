package com.qipa.qipaimbase.utils.recycleadapter

abstract class ItemTypeAbstract : ItemType<ItemData> {
    override fun openLongClick(): Boolean {
        return false
    }

    override fun isCurrentType(data: ItemData?, position: Int): Boolean {
        return data?.itemType === type
    }

    override val onLongClickViews: IntArray
        get() = IntArray(0)
}
