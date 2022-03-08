package com.qipa.newboxproject.app.weight.transformerslayout

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.annotation.RequiresApi

import androidx.recyclerview.widget.GridLayoutManager

import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.weight.recyclerview.HorizontalRecyclerView
import com.qipa.newboxproject.app.weight.transformerslayout.adapter.TransformersAdapter
import com.qipa.newboxproject.app.weight.transformerslayout.holder.TransformersHolderCreator
import com.qipa.newboxproject.app.weight.transformerslayout.listener.OnTransformersItemClickListener
import com.qipa.newboxproject.app.weight.transformerslayout.listener.OnTransformersScrollListener
import com.qipa.newboxproject.app.weight.transformerslayout.view.RecyclerViewScrollBar


class TransformersLayout<T> : LinearLayout {
    private var spanCount = 0
    private var lines = 0
    private var scrollBarRadius = 0f
    private var scrollBarTrackColor = 0
    private var scrollBarThumbColor = 0
    private var scrollBarTopMargin = 0
    private var scrollBarBottomMargin = 0
    private var scrollBarWidth = 0
    private var scrollBarHeight = 0
    private var scrollBarThumbWidth = 0
    private var scrollBarThumbFixedMode = false
    private var pagingMode = false
    private var onTransformersItemClickListener: OnTransformersItemClickListener? = null
    private var recyclerView: HorizontalRecyclerView? = null
    private var scrollBar: RecyclerViewScrollBar? = null
    private var onScrollListener: OnTransformersScrollListener? = null
    private var mDataList: MutableList<T?>? = null
    private var transformersAdapter: TransformersAdapter<T>? = null
    private var layoutManager: GridLayoutManager? = null
    private var savedState //保存的滚动状态
            : Parcelable? = null
    var options: TransformersOptions? = null
        private set

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        parseAttrs(context, attrs)
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        parseAttrs(context, attrs)
        init(context)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.TransformersLayout)
        spanCount =
            array.getInteger(R.styleable.TransformersLayout_tl_spanCount, DEFAULT_SPAN_COUNT)
        lines = array.getInteger(R.styleable.TransformersLayout_tl_lines, DEFAULT_LINES)
        pagingMode = array.getBoolean(R.styleable.TransformersLayout_tl_pagingMode, false)
        scrollBarRadius =
            array.getDimensionPixelSize(R.styleable.TransformersLayout_tl_scrollbarRadius, -1)
                .toFloat()
        scrollBarTrackColor = array.getColor(
            R.styleable.TransformersLayout_tl_scrollbarTrackColor,
            DEFAULT_TRACK_COLOR
        )
        scrollBarThumbColor = array.getColor(
            R.styleable.TransformersLayout_tl_scrollbarThumbColor,
            DEFAULT_THUMB_COLOR
        )
        scrollBarTopMargin =
            array.getDimensionPixelSize(R.styleable.TransformersLayout_tl_scrollbarMarginTop, 0)
        scrollBarBottomMargin =
            array.getDimensionPixelSize(R.styleable.TransformersLayout_tl_scrollBarMarginBottom, 0)
        scrollBarWidth = array.getDimensionPixelSize(
            R.styleable.TransformersLayout_tl_scrollbarWidth, dp2px(
                DEFAULT_SCROLL_BAR_WIDTH.toFloat()
            )
        )
        scrollBarHeight = array.getDimensionPixelSize(
            R.styleable.TransformersLayout_tl_scrollbarHeight, dp2px(
                DEFAULT_SCROLL_BAR_HEIGHT.toFloat()
            )
        )
        scrollBarThumbFixedMode =
            array.getBoolean(R.styleable.TransformersLayout_tl_scrollbarThumbFixedMode, false)
        scrollBarThumbWidth = array.getDimensionPixelSize(
            R.styleable.TransformersLayout_tl_scrollbarThumbFixedWidth, dp2px(
                DEFAULT_SCROLL_BAR_THUMB_WIDTH.toFloat()
            )
        )
        array.recycle()
        if (scrollBarRadius < 0) {
            scrollBarRadius = dp2px(DEFAULT_SCROLL_BAR_HEIGHT.toFloat()) / 2f
        }
        if (spanCount <= 0) {
            spanCount = DEFAULT_SPAN_COUNT
        }
        if (lines <= 0) {
            lines = DEFAULT_LINES
        }
    }

    private fun init(context: Context) {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        layoutParams = LayoutParams(-1, -2)
        recyclerView = HorizontalRecyclerView(context)
        recyclerView?.layoutParams = LayoutParams(-1, -2)
        recyclerView?.overScrollMode = OVER_SCROLL_NEVER
        recyclerView?.isNestedScrollingEnabled = false
        recyclerView?.setHasFixedSize(true)
        val itemAnimator = recyclerView!!.itemAnimator
        if (itemAnimator != null) {
            itemAnimator.changeDuration = 0
        }
        layoutManager = object : GridLayoutManager(getContext(), lines, HORIZONTAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        recyclerView?.layoutManager = layoutManager
        transformersAdapter = TransformersAdapter(context, recyclerView!!)
        recyclerView?.adapter = transformersAdapter
        recyclerView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recyclerView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                //                Log.e(TAG, "rv宽度=" + recyclerView.getWidth());
                recyclerView?.width?.let { transformersAdapter?.onWidthChanged(it) }
            }
        })
        scrollBar = RecyclerViewScrollBar(context)
        setupScrollBar()
        addView(recyclerView)
        addView(scrollBar)
    }

    private fun setupScrollBar() {
        val params = LayoutParams(scrollBarWidth, scrollBarHeight)
        params.topMargin = scrollBarTopMargin
        params.bottomMargin = scrollBarBottomMargin
        scrollBar?.layoutParams = params
        scrollBar!!.setTrackColor(scrollBarTrackColor)
            .setThumbColor(scrollBarThumbColor)
            .setRadius(scrollBarRadius)
            .setThumbFixedMode(scrollBarThumbFixedMode)
            .setThumbWidth(scrollBarThumbWidth)
            .applyChange()
    }

    fun addOnTransformersItemClickListener(listener: OnTransformersItemClickListener?): TransformersLayout<T> {
        onTransformersItemClickListener = listener
        return this
    }

    fun load(data: MutableList<T?>, creator: TransformersHolderCreator<T>?) {
        //如果数据少于一页的列数
        mDataList = data
        fixLineCount()
        fixData(mDataList!!)
        transformersAdapter?.setOnTransformersItemClickListener(onTransformersItemClickListener)
        transformersAdapter?.setHolderCreator(creator)
        transformersAdapter?.setSpanCount(spanCount)
        transformersAdapter?.setData(mDataList)
        toggleScrollBar(data)
        if (scrollBar!!.visibility == VISIBLE) {
            scrollBar!!.attachRecyclerView(recyclerView!!)
        }
    }

    /**
     * 重新排列数据，使数据转换成分页模式
     * 原始数据：
     * 1 3 5 7 9   11 13 15
     * 2 4 6 8 10  12 14 16
     * ==============================
     * 转换之后：（数据会增加null值）
     * 1 2 3 4 5   11 12 13 14 15
     * 6 7 8 9 10  16 null...
     */
    private fun rearrange(data: MutableList<T?>?): MutableList<T?>? {
        if (lines <= 1) return data
        if (data == null || data.isEmpty()) return data
        val pageSize = lines * spanCount
        val size = data.size
        //如果数据少于一行
        if (size <= spanCount) {
            return ArrayList(data)
        }
        val destList: MutableList<T?> = ArrayList()
        //转换后的总数量，包括空数据
        val sizeAfterTransform: Int
        sizeAfterTransform = if (size < pageSize) {
            //            sizeAfterTransform = pageSize;
            if (size < spanCount) size * lines else pageSize
        } else if (size % pageSize == 0) {
            size
        } else {
            //            sizeAfterTransform = (size / pageSize + 1) * pageSize;
            if (size % pageSize < spanCount) size / pageSize * pageSize + size % pageSize * lines else (size / pageSize + 1) * pageSize
        }
        //类似置换矩阵
        for (i in 0 until sizeAfterTransform) {
            val pageIndex = i / pageSize
            val columnIndex = (i - pageSize * pageIndex) / lines
            val rowIndex = (i - pageSize * pageIndex) % lines
            val destIndex = rowIndex * spanCount + columnIndex + pageIndex * pageSize
            if (destIndex >= 0 && destIndex < size) {
                destList.add(data[destIndex])
            } else {
                destList.add(null)
            }
        }
        return destList
    }

    /**
     * 不排序时如果数据大于一页，使用空数据填满最后一列，用于修复滚动条滑动时变长变短的问题
     * @param data
     */
    private fun fillData(data: List<T?>) {
        if (lines <= 1) return
        mDataList = ArrayList(data)
        if ((mDataList as ArrayList<T?>).size > lines * spanCount && (mDataList as ArrayList<T?>).size % lines > 0) {
            val rest = lines - (mDataList as ArrayList<T?>).size % lines
            for (i in 0 until rest) {
                (mDataList as ArrayList<T?>).add(null)
            }
        }
    }

    /**
     * 获取列表数据
     * @return
     */
    val dataList: List<T?>?
        get() = mDataList

    fun apply(options: TransformersOptions?): TransformersLayout<T> {
        if (options != null) {
            this.options = options
            spanCount = if (options.spanCount <= 0) spanCount else options.spanCount
            val newLines = if (options.lines <= 0) lines else options.lines
            scrollBarWidth =
                if (options.scrollBarWidth <= 0) scrollBarWidth else options.scrollBarWidth
            scrollBarHeight =
                if (options.scrollBarHeight <= 0) scrollBarHeight else options.scrollBarHeight
            scrollBarRadius =
                if (options.scrollBarRadius < 0) scrollBarHeight / 2f else options.scrollBarRadius
            scrollBarTopMargin =
                if (options.scrollBarTopMargin <= 0) scrollBarTopMargin else options.scrollBarTopMargin
            pagingMode = options.pagingMode

//            Log.e(TAG, "trackColor = " + options.scrollBarTrackColor);
//            Log.e(TAG, "thumbColor = " + options.scrollBarThumbColor);
//            Log.e(TAG, "radius = " + options.scrollBarRadius);
            scrollBarTrackColor =
                if (options.scrollBarTrackColor == 0) scrollBarTrackColor else options.scrollBarTrackColor
            scrollBarThumbColor =
                if (options.scrollBarThumbColor == 0) scrollBarThumbColor else options.scrollBarThumbColor
            scrollBarThumbFixedMode = options.scrollBarThumbFixedMode
            scrollBarThumbWidth =
                if (options.scrollBarThumbWidth == 0) scrollBarThumbWidth else options.scrollBarThumbWidth
            if (newLines != lines) {
                lines = newLines
                layoutManager!!.spanCount = newLines
            }
            setupScrollBar()
        }
        return this
    }

    /**
     * 恢复滚动状态
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (savedState != null) {
            layoutManager!!.onRestoreInstanceState(savedState)
        }
        savedState = null
    }

    /**
     * 保存滚动状态
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        savedState = layoutManager!!.onSaveInstanceState()
    }

    fun notifyDataChanged(data: MutableList<T?>) {
        mDataList = data
        fixLineCount()
        fixData(data)
        if (transformersAdapter != null) {
            transformersAdapter!!.setData(mDataList)
            scrollToStart()
        }
        toggleScrollBar(data)
        //数据发生改变时重新计算滚动比例
        if (scrollBar!!.visibility == VISIBLE) {
            scrollBar!!.computeScrollScale()
        }
    }

    private fun fixLineCount() {
        //如果总数据少于一页，动态调整行数
        val size = mDataList!!.size
        if (size <= spanCount * lines) {
            lines = if (size % spanCount == 0) size / spanCount else size / spanCount + 1
            Log.e(TAG, "lines = $lines")
            lines = if (lines > 0) lines else 1
            layoutManager!!.spanCount = lines
        }
    }

    private fun fixData(data: MutableList<T?>) {
        if (pagingMode) {
            mDataList = rearrange(data)
        } else {
            fillData(data)
        }
    }

    @JvmOverloads
    fun scrollToStart(smooth: Boolean = true) {
        scrollBar?.setScrollBySelf(true)
        if (recyclerView != null) {
            if (recyclerView!!.computeHorizontalScrollOffset() > 0) {
                if (smooth) {
                    recyclerView?.smoothScrollToPosition(0)
                } else {
                    recyclerView?.scrollToPosition(0)
                }
            }
        }
    }

    fun addOnTransformersScrollListener(listener: OnTransformersScrollListener?): TransformersLayout<T> {
        onScrollListener = listener
        if (scrollBar != null) {
            scrollBar!!.setOnTransformersScrollListener(onScrollListener)
        }
        return this
    }

    /**
     * 不足一页时隐藏滚动条
     */
    private fun toggleScrollBar(data: List<T?>) {
        if (spanCount * lines >= data.size) {
            scrollBar!!.visibility = GONE
        } else {
            scrollBar!!.visibility = VISIBLE
        }
    }

    private fun dp2px(dp: Float): Int {
        return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    companion object {
        private val TAG = TransformersLayout::class.java.simpleName

        /** 默认每页5列  */
        private const val DEFAULT_SPAN_COUNT = 5

        /** 默认每页2行  */
        private const val DEFAULT_LINES = 2

        /** 滚动条默认宽度  */
        private const val DEFAULT_SCROLL_BAR_WIDTH = 48 //dp

        /** 滚动条滑块宽度  */
        private const val DEFAULT_SCROLL_BAR_THUMB_WIDTH = 20 //dp

        /** 滚动条默认高度  */
        private const val DEFAULT_SCROLL_BAR_HEIGHT = 3 //dp
        private val DEFAULT_TRACK_COLOR = Color.parseColor("#f0f0f0")
        private val DEFAULT_THUMB_COLOR = Color.parseColor("#ffc107")
    }
}
