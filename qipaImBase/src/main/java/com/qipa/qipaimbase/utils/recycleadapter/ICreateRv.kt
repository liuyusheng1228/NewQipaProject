package com.qipa.qipaimbase.utils.recycleadapter

import androidx.recyclerview.widget.RecyclerView

interface ICreateRv {
     fun getRecycleView(): RecyclerView?

     fun getAdapter(): RvBaseAdapter<ItemData>?

     fun getLayoutManager(): RecyclerView.LayoutManager?

     fun getItemDecoration(): RecyclerView.ItemDecoration?
}
