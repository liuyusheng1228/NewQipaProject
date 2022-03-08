package com.qipa.qipaimbase.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView

class TouchRecycleView : RecyclerView {
    private var downX = 0f
    private var downY = 0f
    private var move = 0
    private var onRecycleViewClickListener: OnRecycleViewClickListener? = null
    private var lastPointX = 0f
    private var lastPointY = 0f

    constructor(@NonNull context: Context?) : super(context!!) {
        init()
    }

    constructor(@NonNull context: Context?, @Nullable attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        move = ViewConfiguration.get(context).scaledTouchSlop
        itemAnimator = null
        setHasFixedSize(false)
        //        getItemAnimator().setAddDuration(0);
//        getItemAnimator().setChangeDuration(0);
//        getItemAnimator().setMoveDuration(0);
//        getItemAnimator().setRemoveDuration(0);
//        ((DefaultItemAnimator)getItemAnimator()).setSupportsChangeAnimations(false);
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        lastPointX = e.rawX
        lastPointY = e.rawY
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.x
                downY = e.y
            }
            MotionEvent.ACTION_UP -> if (Math.abs(downX - e.x) < move
                && Math.abs(downY - e.y) < move
            ) {
                if (onRecycleViewClickListener != null) {
                    onRecycleViewClickListener!!.onRecycleViewClick()
                }
            }
        }
        return super.onTouchEvent(e)
    }

    fun setOnRecycleViewClickListener(onRecycleViewClickListener: OnRecycleViewClickListener?) {
        this.onRecycleViewClickListener = onRecycleViewClickListener
    }

    interface OnRecycleViewClickListener {
        fun onRecycleViewClick()
    }

    val lastPoint: FloatArray
        get() = floatArrayOf(lastPointX, lastPointY)

    companion object {
        private const val TAG = "TouchRecycleView"
    }
}
