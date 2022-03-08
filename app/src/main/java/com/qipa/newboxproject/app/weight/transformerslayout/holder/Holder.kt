package com.qipa.newboxproject.app.weight.transformerslayout.holder

import android.content.Context
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView


abstract class Holder<T>(@NonNull itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
    protected abstract fun initView(itemView: View?)
    abstract fun onBind(context: Context?, list: List<T>?, @Nullable data: T, position: Int)

    init {
        initView(itemView)
    }
}
