package com.qipa.newboxproject.app.weight.transformerslayout.listener

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView


interface OnTransformersScrollListener {
    fun onScrollStateChanged(@NonNull recyclerView: RecyclerView?, newState: Int)
    fun onScrolled(@NonNull recyclerView: RecyclerView?, dx: Int, dy: Int)
}
