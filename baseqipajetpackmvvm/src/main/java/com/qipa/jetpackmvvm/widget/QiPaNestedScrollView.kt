package com.qipa.jetpackmvvm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.widget.NestedScrollView


class QiPaNestedScrollView : NestedScrollView {
    private var downX = 0
    private var downY = 0
    private var moveX = 0
    private var moveY = 0
    private var mTouchSlop: Int

    constructor(context: Context?) : super(context!!) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.getRawX().toInt()
                downY = e.getRawY().toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                moveY = e.getRawY().toInt()
                moveX = e.getRawX().toInt()
                if (Math.abs(moveX - downX) > mTouchSlop) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}
