package com.qipa.qipaimbase.utils.recycleadapter

interface ItemType<T : ItemData?> {
   fun openClick(): Boolean
   fun openLongClick(): Boolean
   val type: Int
   val layout: Int

   fun fillContent(rvViewHolder: RvViewHolder?, position: Int, data: ItemData?)
   fun isCurrentType(data: ItemData?, position: Int): Boolean
   val onClickViews: IntArray?
   val onLongClickViews: IntArray?
}
