package com.qipa.jetpackmvvm.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class ObservableScrollView : ScrollView {
    /**
     * 回调接口监听事件
     */
    private var mOnObservableScrollViewListener: OnObservableScrollViewListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    /**
     * 添加回调接口，便于把滑动事件的数据向外抛
     */
    interface OnObservableScrollViewListener {
        fun onObservableScrollViewListener(l: Int, t: Int, oldl: Int, oldt: Int)
    }

    /**
     * 注册回调接口监听事件
     *
     * @param onObservableScrollViewListener
     */
    fun setOnObservableScrollViewListener(onObservableScrollViewListener: OnObservableScrollViewListener?) {
        mOnObservableScrollViewListener = onObservableScrollViewListener
    }

    /**
     * 滑动监听
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of
     * [.scrollBy] or [.scrollTo] having been
     * called.
     *
     * @param l Current horizontal scroll origin. 当前滑动的x轴距离
     * @param t Current vertical scroll origin. 当前滑动的y轴距离
     * @param oldl Previous horizontal scroll origin. 上一次滑动的x轴距离
     * @param oldt Previous vertical scroll origin. 上一次滑动的y轴距离
     */
     override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mOnObservableScrollViewListener != null) {
            //将监听到的数据向外抛
            mOnObservableScrollViewListener!!.onObservableScrollViewListener(l, t, oldl, oldt)
        }
    }
}