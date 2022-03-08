package com.qipa.newboxproject.app.weight

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import com.qipa.newboxproject.R

class RatingBar(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    // 正常、半个和选中的星星
    private var mStarNormal: Bitmap
    private var mStarHalf: Bitmap? = null
    private var mStarSelected: Bitmap

    //星星的总数
    private var mStartTotalNumber = 5

    //选中的星星个数
    private var mSelectedNumber: Float = 0f

    // 星星之间的间距
    private val mStartDistance: Int

    // 是否画满
    private var mStatus = Status.FULL

    // 星星的宽高
    private val mStarWidth: Float
    private val mStarHeight: Float

    // 星星选择变化的回调
    private var mOnStarChangeListener: OnStarChangeListener? = null

    // 是不是要画满,默认不画半个的
    private val isFull: Boolean

    // 画笔
    private val mPaint: Paint = Paint()

    // 用于判断是绘制半个，还是全部
    private enum class Status {
        FULL, HALF
    }

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {}

    /**
     * 如果用户设置了图片的宽高，就重新设置图片
     */
    fun resetBitmap(bitMap: Bitmap?, startWidth: Int): Bitmap {
        // 得到新的图片
        return bitMap?.let { Bitmap.createScaledBitmap(it, startWidth, startWidth, true) }!!
    }

    /**
     * 设置选中星星的数量
     */
    fun setSelectedNumber(selectedNumber: Int) {
        if (selectedNumber >= 0 && selectedNumber <= mStartTotalNumber) {
            mSelectedNumber = selectedNumber.toFloat()
            invalidate()
        }
    }

    /**
     * 设置星星的总数量
     */
    fun setStartTotalNumber(startTotalNumber: Int) {
        if (startTotalNumber > 0) {
            mStartTotalNumber = startTotalNumber
            invalidate()
        }
    }

    init {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar)

        // 未选中的图片资源
        val starNormalId: Int = array.getResourceId(R.styleable.RatingBar_starEmptyRes, 0)
        require(starNormalId != 0) { "请设置属性 starNormal" }
        mStarNormal = BitmapFactory.decodeResource(getResources(), starNormalId)
        // 选中一半的图片资源
        val starHalfId: Int = array.getResourceId(R.styleable.RatingBar_starHalfRes, 0)
        if (starHalfId != 0) {
            mStarHalf = BitmapFactory.decodeResource(getResources(), starHalfId)
        }
        // 选中全部的图片资源
        val starSelectedId: Int = array.getResourceId(R.styleable.RatingBar_starSelectedRes, 0)
        require(starSelectedId != 0) { "请设置属性 starSelected" }
        mStarSelected = BitmapFactory.decodeResource(getResources(), starSelectedId)
        // 如果没设置一半的图片资源，就用全部的代替
        if (starHalfId == 0) {
            mStarHalf = mStarSelected
        }
        mStartTotalNumber = array.getInt(R.styleable.RatingBar_startTotalNumber, mStartTotalNumber)
        mSelectedNumber = array.getFloat(R.styleable.RatingBar_selectedNumber, mSelectedNumber)
        mStartDistance = array.getDimension(R.styleable.RatingBar_starDistance, 0f).toInt()
        mStarWidth = array.getDimension(R.styleable.RatingBar_starWidth, 0f)
        mStarHeight = array.getDimension(R.styleable.RatingBar_starHeight, 0f)
        isFull = array.getBoolean(R.styleable.RatingBar_starIsFull, true)
        array.recycle()

        // 如有指定宽高，获取最大值 去改变星星的大小（星星是正方形）
        val starWidth = Math.max(mStarWidth, mStarHeight).toInt()
        if (starWidth > 0) {
            mStarNormal = resetBitmap(mStarNormal, starWidth)
            mStarSelected = resetBitmap(mStarSelected, starWidth)
            mStarHalf = resetBitmap(mStarHalf, starWidth)
        }

        // 计算一半还是全部（小数部分小于等于0.5就只是显示一半）
        if (!isFull) {
            val num = mSelectedNumber.toInt()
            if (mSelectedNumber <= num + 0.5f) {
                mStatus = Status.HALF
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 用正常的一个星星图片去测量高
        val height = paddingTop + paddingBottom + mStarNormal.height
        // 宽 = 星星的宽度*总数 + 星星的间距*（总数-1） +padding
        val width =
            paddingLeft + paddingRight + mStarNormal.width * mStartTotalNumber + mStartDistance * (mStartTotalNumber - 1)
        setMeasuredDimension(width, height)
    }
    override fun onDraw(canvas: Canvas) {
        // 循环绘制
        for (i in 0 until mStartTotalNumber) {
            var left = paddingLeft.toFloat()
            // 从第二个星星开始，给它设置星星的间距
            if (i > 0) {
                left = (paddingLeft + i * (mStarNormal.width + mStartDistance)).toFloat()
            }
            val top = paddingTop.toFloat()
            // 绘制选中的星星
            if (i < mSelectedNumber) {
                // 比当前选中的数量小
                if (i < mSelectedNumber - 1) {
                    canvas.drawBitmap(mStarSelected, left, top, mPaint)
                } else {
                    // 在这里判断是不是要绘制满的
                    if (mStatus === Status.FULL) {
                        canvas.drawBitmap(mStarSelected, left, top, mPaint)
                    } else {
                        mStarHalf?.let { canvas.drawBitmap(it, left, top, mPaint) }
                    }
                }
            } else {
                // 绘制正常的星星
                canvas.drawBitmap(mStarNormal, left, top, mPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN ->{
                // 获取用户触摸的x位置
                val x: Float = event.getX()
                // 一个星星占的宽度
                val startWidth = width / mStartTotalNumber
                // 计算用户触摸星星的位置
                var position = (x / startWidth + 1).toInt()
                if (position < 0) {
                    position = 0
                }
                if (position > mStartTotalNumber) {
                    position = mStartTotalNumber
                }
                // 计算绘制的星星是不是满的
                val result = x - startWidth * (position - 1)
                var status: Status
                // 结果大于一半就是满的
                status = if (result > startWidth * 0.5f) {
                    // 满的
                    Status.FULL
                } else {
                    // 一半的
                    Status.HALF
                }
                if (isFull) {
                    status = Status.FULL
                }
                //减少绘制
                if (mSelectedNumber != position.toFloat() || status !== mStatus) {
                    mSelectedNumber = position.toFloat()
                    mStatus = status
                    invalidate()
                    if (mOnStarChangeListener != null) {
                        position = (mSelectedNumber - 1).toInt()
                        // 选中的数量：满的就回调（1.0这种），一半就（0.5这种）
                        val selectedNumber =
                            if (status === Status.FULL) mSelectedNumber else mSelectedNumber - 0.5f
                        mOnStarChangeListener?.OnStarChanged(
                            selectedNumber,
                            if (position < 0) 0 else position
                        )
                    }
                }

            }
            MotionEvent.ACTION_MOVE -> {
                // 获取用户触摸的x位置
                val x: Float = event.getX()
                // 一个星星占的宽度
                val startWidth = width / mStartTotalNumber
                // 计算用户触摸星星的位置
                var position = (x / startWidth + 1).toInt()
                if (position < 0) {
                    position = 0
                }
                if (position > mStartTotalNumber) {
                    position = mStartTotalNumber
                }
                // 计算绘制的星星是不是满的
                val result = x - startWidth * (position - 1)
                var status: Status
                // 结果大于一半就是满的
                status = if (result > startWidth * 0.5f) {
                    // 满的
                    Status.FULL
                } else {
                    // 一半的
                    Status.HALF
                }
                if (isFull) {
                    status = Status.FULL
                }
                //减少绘制
                if (mSelectedNumber != position.toFloat() || status !== mStatus) {
                    mSelectedNumber = position.toFloat()
                    mStatus = status
                    invalidate()
                    if (mOnStarChangeListener != null) {
                        position = (mSelectedNumber - 1).toInt()
                        // 选中的数量：满的就回调（1.0这种），一半就（0.5这种）
                        val selectedNumber =
                            if (status === Status.FULL) mSelectedNumber else mSelectedNumber - 0.5f
                        mOnStarChangeListener?.OnStarChanged(
                            selectedNumber,
                            if (position < 0) 0 else position
                        )
                    }
                }
            }
        }
        return true
    }

    fun setOnStarChangeListener(onStarChangeListener : OnStarChangeListener){
        mOnStarChangeListener = onStarChangeListener
    }

    //  回调监听（选中的数量，位置）
    interface OnStarChangeListener {
        fun OnStarChanged(selectedNumber: Float, position: Int)
    }


}
