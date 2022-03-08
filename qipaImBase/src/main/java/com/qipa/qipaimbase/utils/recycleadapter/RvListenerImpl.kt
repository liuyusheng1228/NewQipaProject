package com.qipa.qipaimbase.utils.recycleadapter

import android.view.View

open class RvListenerImpl : RvListener<Object> {
    override fun onClick(view: View?, data: ItemData, position: Int) {}

    override fun onLongClick(view: View?, data: ItemData, position: Int) {}
}
