package com.qipa.newboxproject.app.weight.transformerslayout.holder

import android.view.View

interface TransformersHolderCreator<T> {
    fun createHolder(itemView: View) : Holder<T>
    val layoutId: Int
}
