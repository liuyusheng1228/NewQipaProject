package com.qipa.qipaimbase.utils.recycleadapter

import android.util.SparseArray
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class RvViewHolder(@NonNull itemViews: View) : RecyclerView.ViewHolder(itemViews) {
    private val sparseArray: SparseArray<View>
    val itemViews: View

    fun getView(viewId: Int): View {
        val view = sparseArray[viewId]
        if (view == null) {
            val viewById = itemView.findViewById<View>(viewId)
            sparseArray.put(viewId, viewById)
            return viewById
        }
        return view
    }

    init {
        this.itemViews = itemViews
        sparseArray = SparseArray()
    }
}

