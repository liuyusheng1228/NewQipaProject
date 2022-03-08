package com.qipa.qipaimbase.utils.recycleadapter

import android.view.View

interface RvListener<T> {
     fun onClick(view: View?, data: ItemData, position: Int)
     fun onLongClick(view: View?, data: ItemData, position: Int)
}
