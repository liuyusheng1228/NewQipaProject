package com.qipa.newboxproject.app.weight.transformerslayout.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.qipa.newboxproject.app.weight.transformerslayout.listener.OnTransformersScrollListener


class RecyclerViewScrollBar : View {
    private var mRecyclerView: RecyclerView? = null
    private var onTransformersScrollListener: OnTransformersScrollListener? = null
    private var mWidth = 0f
    private var mHeight = 0f
    private val mPaint: Paint = Paint()
    private val mTrackRectF: RectF = RectF()
    private val mThumbRectF: RectF = RectF()
    private var radius = 0f
    private var mTrackColor = 0
    private var mThumbColor = 0
    private var fixedMode //滑块宽度固定模式
            = false
    private var mThumbWidth //滑块宽度
            = 0
    private var mThumbScale = 0f
    private var mScrollScale = 0f
    private var canScrollDistance = 0f
    private var mScrollOffset = 0f

    //当前滚动条位置：起点、滚动中、终点
    private var mScrollLocation = SCROLL_LOCATION_START
    private var scrollBySelf //是否是调用scrollToPosition或smoothScrollToPosition方法来滚动的
            = false

    fun setScrollBySelf(bySelf: Boolean) {
        scrollBySelf = bySelf
    }

    fun setOnTransformersScrollListener(listener: OnTransformersScrollListener?) {
        onTransformersScrollListener = listener
    }

    private val mScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
//            Log.d(TAG, "---------onScrollStateChanged()");
                if (onTransformersScrollListener != null) {
                    onTransformersScrollListener?.onScrollStateChanged(recyclerView, newState)
                }
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
//            Log.d(TAG, "---------onScrolled()");
                computeScrollScale()
                //如果调用scrollToPosition或smoothScrollToPosition来滚动，不会触发onScrollStateChanged，所以这里再次判断下然后手动回调
                if (scrollBySelf && mRecyclerView!!.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE)
                    scrollBySelf = false
                }
                if (onTransformersScrollListener != null) {
                    onTransformersScrollListener?.onScrolled(recyclerView, dx, dy)
                }
            }
        }

    constructor(context: Context?) : this(context, null) {}
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {}
    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        initPaint()
    }

    private fun initPaint() {
        mPaint.setAntiAlias(true)
        mPaint.setDither(true)
        mPaint.setStyle(Paint.Style.FILL)
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        if (mRecyclerView === recyclerView) {
            return
        }
        mRecyclerView = recyclerView
        if (mRecyclerView != null) {
            mRecyclerView!!.removeOnScrollListener(mScrollListener)
            mRecyclerView!!.addOnScrollListener(mScrollListener)
            //监听View的视图树变化,防止初始化未获取到滚动条比例
            mRecyclerView!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    mRecyclerView!!.viewTreeObserver.removeOnPreDrawListener(this)
                    computeScrollScale()
                    return true
                }
            })
        }
    }

    fun setRadius(radius: Float): RecyclerViewScrollBar {
        this.radius = radius
        return this
    }

    fun setTrackColor(@ColorInt color: Int): RecyclerViewScrollBar {
        mTrackColor = color
        return this
    }

    fun setThumbColor(@ColorInt color: Int): RecyclerViewScrollBar {
        mThumbColor = color
        return this
    }

    fun setThumbWidth(width: Int): RecyclerViewScrollBar {
        mThumbWidth = width
        return this
    }

    fun setThumbFixedMode(fixed: Boolean): RecyclerViewScrollBar {
        fixedMode = fixed
        return this
    }

    fun applyChange() {
        postInvalidate()
    }

    fun computeScrollScale() {
        if (mRecyclerView == null) return
        //RecyclerView已显示宽度
        val mScrollExtent = mRecyclerView!!.computeHorizontalScrollExtent().toFloat()
        //RecyclerView实际宽度
        val mScrollRange = mRecyclerView!!.computeHorizontalScrollRange().toFloat()
        if (mScrollRange != 0f) {
            mThumbScale = mScrollExtent / mScrollRange
        }

        //RecyclerView可以滚动的距离
        canScrollDistance = mScrollRange - mScrollExtent

        //RecyclerView已经滚动的距离
        mScrollOffset = mRecyclerView!!.computeHorizontalScrollOffset().toFloat()
        if (mScrollRange != 0f) {
            mScrollScale = mScrollOffset / mScrollRange
        }
        //        Log.e(TAG, "---------mScrollExtent = " + mScrollExtent);
//        Log.e(TAG, "---------mScrollRange = " + mScrollRange);
//        Log.d(TAG, "---------mScrollOffset = " + mScrollOffset);
//        Log.d(TAG, "---------canScrollDistance = " + canScrollDistance);
        Log.d(TAG, "---------mThumbScale = $mThumbScale")
        Log.d(TAG, "---------mScrollScale = $mScrollScale")
        //        Log.d(TAG, "*****************************************");
        mScrollLocation = if (mScrollOffset == 0f) {
            SCROLL_LOCATION_START
        } else if (canScrollDistance == mScrollOffset) {
            SCROLL_LOCATION_END
        } else {
            SCROLL_LOCATION_MIDDLE
        }
        postInvalidate()
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
    }

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTrack(canvas)
        drawThumb(canvas)
    }

    private fun drawThumb(canvas: Canvas) {
        initPaint()
        mPaint.setColor(mThumbColor)
        if (!fixedMode) {
            val left = mScrollScale * mWidth
            val right = left + mWidth * mThumbScale
            when (mScrollLocation) {
                SCROLL_LOCATION_START -> mThumbRectF.set(0f, 0f, right, mHeight)
                SCROLL_LOCATION_MIDDLE -> mThumbRectF.set(left, 0f, right, mHeight)
                SCROLL_LOCATION_END -> mThumbRectF.set(left, 0f, mWidth, mHeight)
            }
        } else {
            val left = (mWidth - mThumbWidth) / canScrollDistance * mScrollOffset
            mThumbRectF.set(left, 0f, left + mThumbWidth, mHeight)
        }
        canvas.drawRoundRect(mThumbRectF, radius, radius, mPaint)
    }

    private fun drawTrack(canvas: Canvas) {
        initPaint()
        mPaint.setColor(mTrackColor)
        mTrackRectF.set(0f, 0f, mWidth, mHeight)
        canvas.drawRoundRect(mTrackRectF, radius, radius, mPaint)
    }

    companion object {
        private val TAG = RecyclerViewScrollBar::class.java.simpleName
        private const val SCROLL_LOCATION_START = 1
        private const val SCROLL_LOCATION_MIDDLE = 2
        private const val SCROLL_LOCATION_END = 3
    }
}