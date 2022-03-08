package com.qipa.newboxproject.app.weight


import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import android.view.View
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler


class FlowLayoutManager : RecyclerView.LayoutManager() {
    val self = this
    protected var width = 0f
    protected var height = 0f
    private var left = 0
    private var top = 0
    private var right = 0
    private var usedMaxWidth = 0
    private var verticalScrollOffset = 0
    var totalHeight = 0
        protected set
    private var row: Row = Row()
    private val lineRows: MutableList<Row?> = ArrayList<Row?>()
    private val allItemFrames: SparseArray<Rect?> = SparseArray<Rect?>()
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(-2, -2)
    }

    @SuppressLint("WrongConstant")
    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        if (this.itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            verticalScrollOffset = 0
        } else if (this.childCount != 0 || !state.isPreLayout) {
            detachAndScrapAttachedViews(recycler)
            if (this.childCount == 0) {
                width = getWidth().toFloat()
                height = getHeight().toFloat()
                left = this.paddingLeft
                right = this.paddingRight
                top = this.paddingTop
                usedMaxWidth = (width - left - right).toInt()
            }
            totalHeight = 0
            var cuLineTop = top
            var cuLineWidth = 0
            var maxHeightItem = 0
            row = Row()
            lineRows.clear()
            allItemFrames.clear()
            removeAllViews()
            for (i in 0 until this.itemCount) {
                Log.d(TAG, "index:$i")
                val childAt = recycler.getViewForPosition(i)
                if (8 != childAt.visibility) {
                    measureChildWithMargins(childAt, 0, 0)
                    val childWidth = getDecoratedMeasuredWidth(childAt)
                    val childHeight = getDecoratedMeasuredHeight(childAt)
                    var itemLeft: Int
                    var frame: Rect?
                    if (cuLineWidth + childWidth <= usedMaxWidth) {
                        itemLeft = left + cuLineWidth
                        frame = allItemFrames[i]
                        if (frame == null) {
                            frame = Rect()
                        }
                        frame[itemLeft, cuLineTop, itemLeft + childWidth] = cuLineTop + childHeight
                        allItemFrames.put(i, frame)
                        cuLineWidth += childWidth
                        maxHeightItem = Math.max(maxHeightItem, childHeight)
                        row.addViews(Item(childHeight, childAt, frame))
                        row.setCuTop(cuLineTop.toFloat())
                        row.setMaxHeight(maxHeightItem.toFloat())
                    } else {
                        formatAboveRow()
                        cuLineTop += maxHeightItem
                        totalHeight += maxHeightItem
                        itemLeft = left
                        frame = allItemFrames[i]
                        if (frame == null) {
                            frame = Rect()
                        }
                        frame[itemLeft, cuLineTop, itemLeft + childWidth] = cuLineTop + childHeight
                        allItemFrames.put(i, frame)
                        cuLineWidth = childWidth
                        maxHeightItem = childHeight
                        row.addViews(Item(childHeight, childAt, frame))
                        row.setCuTop(cuLineTop.toFloat())
                        row.setMaxHeight(childHeight.toFloat())
                    }
                    if (i == this.itemCount - 1) {
                        formatAboveRow()
                        totalHeight += maxHeightItem
                    }
                }
            }
            totalHeight = Math.max(totalHeight, verticalSpace)
            fillLayout(recycler, state)
        }
    }

    private fun fillLayout(recycler: Recycler, state: RecyclerView.State) {
        if (!state.isPreLayout) {
            val displayFrame = Rect(
                this.paddingLeft,
                this.paddingTop + verticalScrollOffset,
                getWidth() - this.paddingRight,
                verticalScrollOffset + (getHeight() - this.paddingBottom)
            )
            for (j in lineRows.indices) {
                val row = lineRows[j] as Row
                val lineTop = row.cuTop
                val lineBottom = lineTop + row.maxHeight
                var views: List<*>
                var i: Int
                var scrap: View
                if (lineTop < displayFrame.bottom.toFloat() && displayFrame.top < lineBottom) {
                    views = row.views
                    i = 0
                    while (i < views.size) {
                        scrap = views[i].view
                        measureChildWithMargins(scrap, 0, 0)
                        this.addView(scrap)
                        val frame = views[i].rect
                        layoutDecoratedWithMargins(
                            scrap,
                            frame.left,
                            frame.top - verticalScrollOffset,
                            frame.right,
                            frame.bottom - verticalScrollOffset
                        )
                        ++i
                    }
                } else {
                    views = row.views
                    i = 0
                    while (i < views.size) {
                        scrap = views[i].view
                        removeAndRecycleView(scrap, recycler)
                        ++i
                    }
                }
            }
        }
    }


    private fun formatAboveRow() {
        val views = row.views
        for (i in views.indices) {
            val item = views[i]
            val view = item!!.view
            val position = getPosition(view)
            if (allItemFrames[position]!!.top <this.row.cuTop + (row.maxHeight - views[i]!!.useHeight.toFloat()) / 2.0f) {
                var frame = allItemFrames[position]
                if (frame == null) {
                    frame = Rect()
                }
                frame[allItemFrames[position]!!.left, (row.cuTop + (row.maxHeight - views[i]!!.useHeight.toFloat()) / 2.0f).toInt(), allItemFrames[position]!!.right] =
                    (row.cuTop + (row.maxHeight - views[i]!!.useHeight.toFloat()) / 2.0f + getDecoratedMeasuredHeight(
                        view
                    )
                        .toFloat()).toInt()
                allItemFrames.put(position, frame)
                item.setRect(frame)
                views[i] = item
            }
        }
        row.views = views
        lineRows.add(row)
        row = Row()
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        Log.d("TAG", "totalHeight:" + totalHeight)
        var travel = dy
        if (verticalScrollOffset + dy < 0) {
            travel = -verticalScrollOffset
        } else if (verticalScrollOffset + dy > totalHeight - verticalSpace) {
            travel = totalHeight - verticalSpace - verticalScrollOffset
        }
        verticalScrollOffset += travel
        offsetChildrenVertical(-travel)
        fillLayout(recycler, state)
        return travel
    }

    private val verticalSpace: Int
        private get() = self.getHeight() - self.paddingBottom - self.paddingTop
    val horizontalSpace: Int
        get() = self.getWidth() - self.paddingLeft - self.paddingRight

    inner class Row {
        var cuTop = 0f
        var maxHeight = 0f
        var views: MutableList<Item> = ArrayList<Item>()
        @JvmName("setCuTop1")
        fun setCuTop(cuTop: Float) {
            this.cuTop = cuTop
        }

        @JvmName("setMaxHeight1")
        fun setMaxHeight(maxHeight: Float) {
            this.maxHeight = maxHeight
        }

        fun addViews(view: Item?) {
            view?.let { views.add(it) }
        }
    }

    inner class Item(var useHeight: Int, var view: View, var rect: Rect) {
        @JvmName("setRect1")
        fun setRect(rect: Rect) {
            this.rect = rect
        }
    }

    companion object {
        private val TAG = FlowLayoutManager::class.java.simpleName
    }

    init {
        this.isAutoMeasureEnabled = true
    }
}