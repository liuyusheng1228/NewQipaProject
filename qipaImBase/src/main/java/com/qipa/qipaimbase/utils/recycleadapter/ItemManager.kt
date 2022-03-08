package com.qipa.qipaimbase.utils.recycleadapter

import android.util.SparseArray
import java.lang.IllegalArgumentException

class ItemManager private constructor() {
    private val itemTypeSparseArray: SparseArray<ItemType<in ItemData>>
    fun addItems(itemType: ItemType<in ItemData>) {
        if (itemType == null) {
            return
        }
        itemTypeSparseArray.put(itemType.type, itemType)
    }

    fun getLayout(viewType: Int): Int {
        return itemTypeSparseArray[viewType].layout
    }

    fun getItemType(viewType: Int): ItemType<in ItemData> {
        return itemTypeSparseArray[viewType]
    }

    fun <T : ItemData?> getType(t: T, position: Int): Int {
        val size = itemTypeSparseArray.size()
        var itemType: ItemType<in ItemData>
        for (i in 0 until size) {
            itemType = itemTypeSparseArray.valueAt(i)
            if (itemType.isCurrentType(t, position)) { // NOTE: 比较骚的是这个判断需要具体实现完成
                return itemType.type
            }
        }
        throw IllegalArgumentException("unknow msgType")
    }

    companion object {
        val instance = ItemManager()
    }

    init {
        itemTypeSparseArray = SparseArray<ItemType<in ItemData>>()
    }
}
