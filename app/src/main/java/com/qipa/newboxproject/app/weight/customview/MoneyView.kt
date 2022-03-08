package com.qipa.newboxproject.app.weight.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

import com.qipa.newboxproject.R
import java.text.NumberFormat


/**
 * Created by cchao on 2016/8/31.
 * E-mail:   cchao1024@163.com
 * Description:自定义金额View
 */
class MoneyView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    View(context, attrs, defStyle) {
    private var mIsGroupingUsed //是否开启 千分符
            = false
    private var mTextWidth //文本显示占用的宽高
            = 0
    private var mTextHeight = 0

    //钱文本 e.g.    789.15
    private var mMoneyText = "0.00"
    private val POINT = "." //小数点
    private var mMoneyColor: Int = Color.parseColor("#F02828")
    private var mPrefixColor: Int = Color.parseColor("#F02828")
    private var mPrefix //前缀
            : String? = null
    private var mYuan //多少元
            : String? = null
    private var mCent //多少分
            : String? = null

    /**
     * 文本 元的大小
     */
    private var mYuanSize = sp2px(18)
    private var mCentSize = sp2px(14)
    private var mPrefixSize = sp2px(12)
    private var mPrefixPadding = dp2px(4f) //小数点与分的间隔
    private var mPointPaddingLeft = dp2px(3f) //小数点与分的间隔
    private var mPointPaddingRight = dp2px(4f) //小数点与分的间隔

    /**
     * 绘制时控制文本绘制的范围
     */
    private var mYuanBound: Rect? = null
    private var mPrefixBound: Rect? = null
    private var mCentBound: Rect? = null
    private var mPointBound: Rect? = null
    private var mPaint: Paint? = null

    //基线高度
    var maxDescent = 0f

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val typedArray: TypedArray =
            context.getTheme().obtainStyledAttributes(attrs, R.styleable.MoneyView, defStyle, 0)
        mMoneyText = typedArray.getString(R.styleable.MoneyView_money_text).toString()
        mMoneyColor = typedArray.getColor(R.styleable.MoneyView_money_color, mMoneyColor)
        mYuanSize = typedArray.getDimensionPixelSize(R.styleable.MoneyView_yuan_size,
            mYuanSize
        )
        mCentSize = typedArray.getDimensionPixelSize(R.styleable.MoneyView_cent_size,
            mCentSize
        )
        mPrefix = typedArray.getString(R.styleable.MoneyView_prefix_text)
        mPrefixSize =
            typedArray.getDimensionPixelSize(R.styleable.MoneyView_prefix_size, mPrefixSize)
        mPrefixColor = typedArray.getColor(R.styleable.MoneyView_prefix_color, mPrefixColor)
        mPrefixPadding =
            typedArray.getDimensionPixelSize(R.styleable.MoneyView_prefix_padding, mPrefixPadding)
        mPointPaddingLeft = typedArray.getDimensionPixelSize(
            R.styleable.MoneyView_point_padding_left,
            mPointPaddingLeft
        )
        mPointPaddingRight = typedArray.getDimensionPixelSize(
            R.styleable.MoneyView_point_padding_right,
            mPointPaddingRight
        )
        mIsGroupingUsed = typedArray.getBoolean(R.styleable.MoneyView_grouping, false)
        typedArray.recycle()
        /**
         * 获得绘制文本的宽和高
         */
        mPaint = Paint()
        mPaint?.setAntiAlias(true) // 消除锯齿
        mPaint?.setFlags(Paint.ANTI_ALIAS_FLAG) // 消除锯齿
        mYuanBound = Rect()
        mCentBound = Rect()
        mPointBound = Rect()
        mPrefixBound = Rect()
        if (TextUtils.isEmpty(mPrefix)) {
            mPrefix = "¥"
        }
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int
        val height: Int
        var pointPosition = mMoneyText.indexOf(POINT)
        if (!mMoneyText.contains(POINT)) {
            pointPosition = mMoneyText.length
        }
        //获取元的文本
        mYuan = mMoneyText.substring(0, pointPosition)
        //如果使用千分符
        if (mIsGroupingUsed) {
            mYuan = NumberFormat.getInstance().format(java.lang.Long.valueOf(mYuan))
        }
        //获取分的文本
        mCent = mMoneyText.substring(pointPosition + 1, mMoneyText.length)
        //获取元小数点、的占据宽高
        mPaint?.setTextSize(mYuanSize.toFloat())
        mPaint?.getTextBounds(mYuan, 0, mYuan!!.length, mYuanBound)
        mPaint?.getTextBounds(POINT, 0, POINT.length, mPointBound)
        //获取分占据宽高
        mPaint?.setTextSize(mCentSize.toFloat())
        mPaint?.getTextBounds(mCent, 0, mCent!!.length, mCentBound)
        //获取前缀占据宽高
        mPaint?.setTextSize(mPrefixSize.toFloat())
        mPaint?.getTextBounds(mPrefix, 0, mPrefix!!.length, mPrefixBound)
        //文本占据的宽度
        mTextWidth =
            (mYuanBound!!.width() + mCentBound!!.width() + mPrefixBound!!.width() + mPointBound!!.width()
                    + mPointPaddingLeft + mPointPaddingRight + mPrefixPadding)
        /**
         * 设置宽度
         */
        var specMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        var specSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        width = if (specMode == MeasureSpec.EXACTLY) {
            specSize + getPaddingLeft() + getPaddingRight()
        } else {
            mTextWidth + getPaddingLeft() + getPaddingRight()
        }
        /**
         * 设置高度
         */
        //获取最大字号
        var maxSize = Math.max(mYuanSize, mCentSize).toInt()
        maxSize = Math.max(maxSize, mPrefixSize).toInt()
        mPaint?.setTextSize(maxSize.toFloat())
        //获取基线距离底部
        maxDescent = mPaint!!.fontMetrics.descent
        var maxHeight: Int = Math.max(mYuanBound!!.height(), mCentBound!!.height())
        maxHeight = Math.max(maxHeight, mPrefixBound!!.height())
        //文本占据的高度
        mTextHeight = maxHeight + (maxDescent + 0.5f).toInt()
        specMode = MeasureSpec.getMode(heightMeasureSpec)
        specSize = MeasureSpec.getSize(heightMeasureSpec)
        height = if (specMode == MeasureSpec.EXACTLY) {
            specSize
        } else {
            mTextHeight
        }
        setMeasuredDimension(width, height)
    }

    protected override fun onDraw(canvas: Canvas) {

        //绘制X坐标
        var drawX = (getMeasuredWidth() - mTextWidth) / 2
        val drawY = (getMeasuredHeight() + mTextHeight) / 2 - maxDescent

        //绘制前缀
        mPaint?.setColor(mPrefixColor)
        mPaint?.setTextSize(mPrefixSize.toFloat())
        mPrefix?.let { mPaint?.let { it1 -> canvas.drawText(it, drawX.toFloat(), drawY, it1) } }
        //绘制元
        drawX += mPrefixBound!!.width() + mPrefixPadding
        mPaint?.setColor(mMoneyColor)
        mPaint?.setTextSize(mYuanSize.toFloat())
        mPaint?.let { canvas.drawText(mYuan.toString(), drawX.toFloat(), drawY, it) }
        //绘制小数点
        drawX += mYuanBound!!.width() + mPointPaddingLeft
        mPaint?.let { canvas.drawText(POINT, drawX.toFloat(), drawY, it) }
        //绘制分
        drawX += mPointPaddingRight
        mPaint?.setTextSize(mCentSize.toFloat())
        mPaint?.let { canvas.drawText(mCent.toString(), drawX.toFloat(), drawY, it) }
    }

    var moneyText: String?
        get() = mMoneyText
        set(string) {
            var string = string
            if (string == null) {
                string = ""
            }
            mMoneyText = string
            requestLayout()
            postInvalidate()
        }

    /**
     * 开启千分符号
     *
     * @param used yes or no
     */
    fun setGroupingUsed(used: Boolean) {
        mIsGroupingUsed = used
    }

    val paint: Paint?
        get() = mPaint

    private fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), getResources().getDisplayMetrics()
        ).toInt()
    }

    private fun dp2px(dipValue: Float): Int {
        val scale: Float = getContext().getResources().getDisplayMetrics().density
        return (dipValue * scale + 0.5f).toInt()
    }

    init {
        init(context, attrs, defStyle)
    }
}