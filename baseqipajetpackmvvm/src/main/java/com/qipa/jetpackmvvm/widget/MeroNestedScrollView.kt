package com.qipa.jetpackmvvm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView


class MeroNestedScrollView : NestedScrollView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
    }

    private var xDistance = 0f
    private var yDistance = 0f
    private var lastX = 0f
    private var lastY = 0f
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val x: Float = ev.getX()
        val y: Float = ev.getY()
        when (ev.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                run {
                    yDistance = 0f
                    xDistance = yDistance
                }
                lastX = ev.getX()
                lastY = ev.getY()

                // This is very important line that fixes
                computeScroll()
            }
            MotionEvent.ACTION_MOVE -> {
                val curX: Float = ev.getX()
                val curY: Float = ev.getY()
                xDistance += Math.abs(curX - lastX)
                yDistance += Math.abs(curY - lastY)
                lastX = curX
                lastY = curY
                if (xDistance > yDistance) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}