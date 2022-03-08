package com.qipa.qipaimbase.base

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qipa.qipaimbase.utils.recycleadapter.CreateRvHelper
import com.qipa.qipaimbase.utils.recycleadapter.ICreateRv

abstract class RvBaseFragment : BaseFragment(), ICreateRv {
    private var createRvHelper: CreateRvHelper? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected open fun initRv() {
        createRvHelper = CreateRvHelper.Builder(this).build()
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager? {
        return LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
    }
}