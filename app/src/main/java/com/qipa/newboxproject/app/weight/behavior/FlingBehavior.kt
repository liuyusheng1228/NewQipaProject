package com.qipa.newboxproject.app.weight.behavior

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.weight.UmengImageView

class FlingBehavior @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?=null
) : AppBarLayout.Behavior(context, attrs) {

    private val TOP_CHILD_FLING_THRESHOLD = 3
    private var isPositive = false


    private var mImageView: UmengImageView? = null
    private var mAppbarHeight //记录AppbarLayout原始高度
            = 0
    private var mImageViewHeight //记录ImageView原始高度
            = 0

    private val MAX_ZOOM_HEIGHT = 500f //放大最大高度

    private var mTotalDy //手指在Y轴滑动的总距离
            = 0f
    private var mScaleValue //图片缩放比例
            = 0f
    private var mLastBottom //Appbar的变化高度
            = 0

    private var isAnimate //是否做动画标志
            = false


    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {

        Log.d("JACK8",
            "onNestedFling() called with: coordinatorLayout = $coordinatorLayout, child = $child, target = $target, velocityX = $velocityX, velocityY = $velocityY, consumed = $consumed"
        )
        var velocityY=velocityY
        var consumed=consumed

        if (velocityY > 0.0f && !isPositive || velocityY < 0.0f && isPositive) {
            velocityY *= -1.0f
        }
        if (target is RecyclerView && velocityY < 0.0f) {
            consumed = target.getChildAdapterPosition(target.getChildAt(0)) > TOP_CHILD_FLING_THRESHOLD
        }
        if (velocityY > 100) {
            isAnimate = false;
        }
        return super.onNestedFling(
            coordinatorLayout,
            child,
            target,
            velocityX,
            velocityY,
            consumed
        )
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray,type : Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed,type)
        Log.d(
            "JACK8",
            "onNestedPreScroll() called with: coordinatorLayout = $coordinatorLayout, child = $child, target = $target, dx = $dx, dy = $dy, consumed = $consumed"
        )
        isPositive = dy > 0

        if (mImageView != null && child.getBottom() >= mAppbarHeight && dy < 0 && type == ViewCompat.TYPE_TOUCH) {
            zoomHeaderImageView(child, dy);
        } else {
            if (mImageView != null && child.getBottom() > mAppbarHeight && dy > 0 && type == ViewCompat.TYPE_TOUCH) {
                consumed[1] = dy;
                zoomHeaderImageView(child, dy);
            } else {
                if (valueAnimator == null || !valueAnimator?.isRunning()!!) {
                    super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
                }

            }
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        abl: AppBarLayout,
        layoutDirection: Int
    ): Boolean {
        val handled = super.onLayoutChild(parent!!, abl, layoutDirection)
        init(abl)
        return handled
    }

    /**
     * 进行初始化操作，在这里获取到ImageView的引用，和Appbar的原始高度
     *
     * @param abl
     */
    private fun init(abl: AppBarLayout) {
        abl.clipChildren = false
        mAppbarHeight = abl.height
        mImageView = abl.findViewById<View>(R.id.gm_up_um_bg) as UmengImageView
        if (mImageView != null) {
            mImageViewHeight = mImageView!!.getHeight()
        }
    }

    /**
     * 是否处理嵌套滑动
     *
     * @param parent
     * @param child
     * @param directTargetChild
     * @param target
     * @param nestedScrollAxes
     * @param type
     * @return
     */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int
    ): Boolean {
        isAnimate = true
        return true
    }


    /**
     * 对ImageView进行缩放处理，对AppbarLayout进行高度的设置
     *
     * @param abl
     * @param dy
     */
    private fun zoomHeaderImageView(abl: AppBarLayout, dy: Int) {
        mTotalDy += -dy.toFloat()
        mTotalDy = Math.min(mTotalDy, MAX_ZOOM_HEIGHT)
        mScaleValue = Math.max(1f, 1f + mTotalDy / MAX_ZOOM_HEIGHT)
        ViewCompat.setScaleX(mImageView, mScaleValue)
        ViewCompat.setScaleY(mImageView, mScaleValue)
        mLastBottom = mAppbarHeight + (mImageViewHeight / 2 * (mScaleValue - 1)).toInt()
        abl.bottom = mLastBottom
    }

    /**
     * 滑动停止的时候，恢复AppbarLayout、ImageView的原始状态
     *
     * @param coordinatorLayout
     * @param abl
     * @param target
     * @param type
     */
    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        abl: AppBarLayout,
        target: View,
        type: Int
    ) {
        recovery(abl)
        super.onStopNestedScroll(coordinatorLayout!!, abl, target!!, type)
    }

    var valueAnimator: ValueAnimator? = null

    /**
     * 通过属性动画的形式，恢复AppbarLayout、ImageView的原始状态
     *
     * @param abl
     */
    private fun recovery(abl: AppBarLayout) {
        if (mTotalDy > 0) {
            mTotalDy = 0f
            if (isAnimate) {
                valueAnimator = ValueAnimator.ofFloat(mScaleValue, 1f).setDuration(220)
                valueAnimator?.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                    override fun onAnimationUpdate(animation: ValueAnimator) {
                        val value = animation.getAnimatedValue() as Float
                        ViewCompat.setScaleX(mImageView, value)
                        ViewCompat.setScaleY(mImageView, value)
                        abl.bottom =
                            (mLastBottom - (mLastBottom - mAppbarHeight) * animation.getAnimatedFraction()) as Int
                    }
                })
                valueAnimator?.start()
            } else {
                ViewCompat.setScaleX(mImageView, 1f)
                ViewCompat.setScaleY(mImageView, 1f)
                abl.bottom = mAppbarHeight
            }
        }
    }

}