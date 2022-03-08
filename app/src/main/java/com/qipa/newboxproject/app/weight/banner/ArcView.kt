package com.qipa.newboxproject.app.weight.banner

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable
import com.qipa.newboxproject.R


/**
 * 弧形的view
 */
class ArcView(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    private var mWidth = 0
    private var mHeight = 0
    private val mArcHeight //圆弧的高度
            : Int
    private val mBgColor //背景颜色
            : Int
    private val lgColor //变化的最终颜色
            : Int
    private val mPaint //画笔
            : Paint
    private var linearGradient: LinearGradient? = null
    private val rect: Rect = Rect(0, 0, 0, 0) //普通的矩形
    private val path: Path = Path() //用来绘制曲面

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {}

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //        Log.d("----","onSizeChanged");
        linearGradient = LinearGradient(
            0f, 0f, measuredWidth.toFloat(), 0f,
            mBgColor, lgColor, Shader.TileMode.CLAMP
        )
        mPaint.setShader(linearGradient)
    }

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //设置成填充
        mPaint.setStyle(Paint.Style.FILL)
        mPaint.setColor(mBgColor)

        //绘制矩形
        rect.set(0, 0, mWidth, mHeight - mArcHeight)
        canvas.drawRect(rect, mPaint)

        //绘制路径
        path.moveTo(0f, (mHeight - mArcHeight).toFloat())
        path.quadTo((mWidth shr 1).toFloat(), mHeight.toFloat(), mWidth.toFloat(),
            (mHeight - mArcHeight).toFloat()
        )
        canvas.drawPath(path, mPaint)
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize: Int = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcView)
        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcView_arcHeight, 0)
        mBgColor = typedArray.getColor(R.styleable.ArcView_bgColor, Color.parseColor("#303F9F"))
        lgColor = typedArray.getColor(R.styleable.ArcView_lgColor, mBgColor)
        mPaint = Paint()
        mPaint.setAntiAlias(true)
        typedArray.recycle()
    }
}