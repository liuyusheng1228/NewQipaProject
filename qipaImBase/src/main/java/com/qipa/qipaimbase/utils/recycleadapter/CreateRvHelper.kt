package com.qipa.qipaimbase.utils.recycleadapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

class CreateRvHelper private constructor(private val builder: Builder) {
    private val recyclerView: RecyclerView?
    private val adapter: RvBaseAdapter<in ItemData>
    private fun initRecycleView() {
        val itemDecoration: RecyclerView.ItemDecoration? = builder.iCreateRv?.getItemDecoration()
        val layoutManager: RecyclerView.LayoutManager? = builder.iCreateRv?.getLayoutManager()
        Log.i("Api",""+recyclerView+adapter+layoutManager)
        require(!(recyclerView == null || adapter == null || layoutManager == null)) { "初始化失败！" }
        recyclerView.setLayoutManager(layoutManager)
        if (itemDecoration != null) {
            recyclerView.addItemDecoration(itemDecoration)
        }
        recyclerView.setAdapter(adapter)
    }

    class Builder(val iCreateRv: ICreateRv?) {
        fun build(): CreateRvHelper {
            requireNotNull(iCreateRv) { "iCreateRv is null" }
            return CreateRvHelper(this)
        }
    }

    init {
        recyclerView = builder.iCreateRv?.getRecycleView()
        adapter = builder.iCreateRv?.getAdapter()!!
        initRecycleView()
    }
}
