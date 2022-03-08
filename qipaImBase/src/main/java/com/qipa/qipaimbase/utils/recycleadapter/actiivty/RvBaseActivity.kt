package com.qipa.qipaimbase.utils.recycleadapter.actiivty

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qipa.qipaimbase.base.BaseActivity
import com.qipa.qipaimbase.utils.recycleadapter.CreateRvHelper
import com.qipa.qipaimbase.utils.recycleadapter.ICreateRv

abstract class RvBaseActivity : BaseActivity(), ICreateRv {
    private var createRvHelper: CreateRvHelper? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        createRvHelper = CreateRvHelper.Builder(this).build()
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager? {
        return LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
    }
}
