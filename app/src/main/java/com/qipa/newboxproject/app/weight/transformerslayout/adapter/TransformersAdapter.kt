package com.qipa.newboxproject.app.weight.transformerslayout.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull

import androidx.recyclerview.widget.RecyclerView
import com.qipa.newboxproject.app.weight.transformerslayout.holder.Holder
import com.qipa.newboxproject.app.weight.transformerslayout.holder.TransformersHolderCreator
import com.qipa.newboxproject.app.weight.transformerslayout.listener.OnTransformersItemClickListener


class TransformersAdapter<T>(context: Context, recyclerView: RecyclerView) :
    RecyclerView.Adapter<Holder<T>?>() {
    private val mContext: Context
    private var mData: List<T?>? = null
    private var holderCreator: TransformersHolderCreator<T>? = null
    private val mRecyclerView: RecyclerView
    private var mWidth = 0
    private var spanCount = 0
    private var onTransformersItemClickListener: OnTransformersItemClickListener? = null
    fun setOnTransformersItemClickListener(listener: com.qipa.newboxproject.app.weight.transformerslayout.listener.OnTransformersItemClickListener?) {
        onTransformersItemClickListener = listener
    }

    fun setData(data: List<T?>?) {
        mData = data
        notifyDataSetChanged()
    }

    fun onWidthChanged(width: Int) {
        mWidth = width
        notifyDataSetChanged()
    }

    fun setSpanCount(spanCount: Int) {
        this.spanCount = spanCount
    }

    fun setHolderCreator(creator: com.qipa.newboxproject.app.weight.transformerslayout.holder.TransformersHolderCreator<T>?) {
        holderCreator = creator
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): Holder<T> {
        val layoutId = holderCreator?.layoutId
        val itemView: View? =
            layoutId?.let { LayoutInflater.from(parent.getContext()).inflate(it, parent, false) }
        //每个item平分整个屏幕的宽度
        val params = itemView?.getLayoutParams() as RecyclerView.LayoutParams
        mWidth = if (mWidth == 0) mRecyclerView.measuredWidth else mWidth
        params.width = mWidth / spanCount
        return holderCreator?.createHolder(itemView)!!
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    init {
        mContext = context
        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: Holder<T>, @SuppressLint("RecyclerView") position: Int) {
        if (mData!![position] == null) {
            holder.itemView.visibility = View.INVISIBLE
        } else {
            holder.itemView.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (mData!![position] != null) {
                    if (onTransformersItemClickListener != null) {
                        onTransformersItemClickListener!!.onItemClick(position)
                    }
                }
            }
        })
        mData!![position]?.let { holder.onBind(mContext, mData as List<T>?, it, position) }
    }
}
