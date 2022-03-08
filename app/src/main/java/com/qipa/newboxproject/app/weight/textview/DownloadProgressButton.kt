package com.qipa.newboxproject.app.weight.textview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.TextView
import com.qipa.newboxproject.R
import java.text.DecimalFormat
import java.util.ArrayList
import android.graphics.LinearGradient


@SuppressLint("AppCompatCustomView")
class DownloadProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    TextView(context, attrs, defStyleAttr) {
    //背景画笔
    private var mBackgroundPaint: Paint? = null

    //按钮文字画笔
    @Volatile
    private var mTextPaint: Paint? = null

    //背景颜色
    private var mBackgroundColor = 0

    //下载中后半部分后面背景颜色
    private var mBackgroundSecondColor = 0

    //文字颜色
    private var mTextColor = 0

    //覆盖后颜色
    var textCoverColor = 0
    var buttonRadius = 0f

    //边框宽度
    var borderWidth = 0f
        private set

    //点动画样式
    private var mBallStyle = STYLE_BALL_JUMP
    var progress = -1f
    private var mToProgress = 0f
    var maxProgress = 0
    var minProgress = 0
    private var mProgressPercent = 0f
    private var mTextRightBorder = 0f
    private var mTextBottomBorder = 0f

    //点的间隙
    private val mBallSpacing = 4f

    //点的半径
    private val mBallRadius = 6f

    //是否显示边框，默认是true
    var isShowBorder = false
    private var mBackgroundBounds: RectF? = null
    private var mProgressTextGradient: LinearGradient? = null

    //下载平滑动画
    private var mProgressAnimation: ValueAnimator? = null

    //记录当前文字
    private var mCurrentText: CharSequence? = null
    private var mState = 0

    //点运动动画
    private var mAnimators: ArrayList<ValueAnimator>? = null
    private val scaleFloats = floatArrayOf(
        SCALE,
        SCALE,
        SCALE
    )
    private val translateYFloats = FloatArray(3)
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressButton)
        try {
            mBackgroundColor = a.getColor(
                R.styleable.DownloadProgressButton_progress_btn_background_color,
                Color.parseColor("#3385FF")
            )
            mBackgroundSecondColor = a.getColor(
                R.styleable.DownloadProgressButton_progress_btn_background_second_color,
                Color.parseColor("#E8E8E8")
            )
            buttonRadius =
                a.getDimension(R.styleable.DownloadProgressButton_progress_btn_radius, 0f)
            mTextColor = a.getColor(
                R.styleable.DownloadProgressButton_progress_btn_text_color,
                mBackgroundColor
            )
            textCoverColor = a.getColor(
                R.styleable.DownloadProgressButton_progress_btn_text_cover_color,
                Color.WHITE
            )
            borderWidth = a.getDimension(
                R.styleable.DownloadProgressButton_progress_btn_border_width,
                dp2px(2).toFloat()
            )
            mBallStyle = a.getInt(
                R.styleable.DownloadProgressButton_progress_btn_ball_style,
                STYLE_BALL_JUMP
            )
        } finally {
            a.recycle()
        }
    }

    private fun init() {
        maxProgress = 100
        minProgress = 0
        progress = 0f
        isShowBorder = true

        //设置背景画笔
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.isAntiAlias = true
        mBackgroundPaint!!.style = Paint.Style.FILL

        //设置文字画笔
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize = 50f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //解决文字有时候画不出问题
            setLayerType(LAYER_TYPE_SOFTWARE, mTextPaint)
        }

        //初始化状态设为NORMAL
        mState = STATE_NORMAL
        invalidate()
    }

    private fun setupAnimations() {
        //ProgressBar的动画
        mProgressAnimation = ValueAnimator.ofFloat(0f, 1f).setDuration(500)
        mProgressAnimation?.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val timePercent = animation.animatedValue as Float
                progress = (mToProgress - progress) * timePercent + progress
                invalidate()
            }
        })
        setBallStyle(mBallStyle)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInEditMode) {
            drawing(canvas)
        }
    }

    private fun drawing(canvas: Canvas) {
        drawBackground(canvas)
        drawTextAbove(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        mBackgroundBounds = RectF()
        //根据Border宽度得到Button的显示区域
        mBackgroundBounds?.left = if (isShowBorder) borderWidth else 0F
        mBackgroundBounds?.top = if (isShowBorder) borderWidth else 0F
        if (isShowBorder){
            mBackgroundBounds?.right = measuredWidth -  borderWidth
        }else{
            mBackgroundBounds?.right = measuredWidth.toFloat()
        }
        if (isShowBorder){
            mBackgroundBounds?.bottom = measuredHeight -  borderWidth
        }else{
            mBackgroundBounds?.bottom = measuredHeight.toFloat()
        }
        if (isShowBorder) {
            mBackgroundPaint!!.style = Paint.Style.STROKE
            mBackgroundPaint!!.color = mBackgroundColor
            mBackgroundPaint!!.strokeWidth = borderWidth
            canvas.drawRoundRect(
                mBackgroundBounds!!,
                buttonRadius,
                buttonRadius, mBackgroundPaint!!
            )
        }
        mBackgroundPaint!!.style = Paint.Style.FILL
        when (mState) {
            STATE_NORMAL -> {
                mBackgroundPaint!!.color = mBackgroundColor
                canvas.drawRoundRect(
                    mBackgroundBounds!!,
                    buttonRadius,
                    buttonRadius, mBackgroundPaint!!
                )
            }
            STATE_PAUSE, STATE_DOWNLOADING -> {
                //计算当前的进度
                mProgressPercent = progress / (maxProgress + 0f)
                mBackgroundPaint!!.color = mBackgroundSecondColor
                canvas.save()
                //画出dst图层
                canvas.drawRoundRect(
                    mBackgroundBounds!!,
                    buttonRadius,
                    buttonRadius, mBackgroundPaint!!
                )
                //设置图层显示模式为 SRC_ATOP
                val porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
                mBackgroundPaint!!.color = mBackgroundColor
                mBackgroundPaint!!.xfermode = porterDuffXfermode
                //计算 src 矩形的右边界
                val right = mBackgroundBounds!!.right * mProgressPercent
                //在dst画出src矩形
                canvas.drawRect(
                    mBackgroundBounds!!.left,
                    mBackgroundBounds!!.top,
                    right,
                    mBackgroundBounds!!.bottom,
                    mBackgroundPaint!!
                )
                canvas.restore()
                mBackgroundPaint!!.xfermode = null
            }
            STATE_FINISH -> {
                mBackgroundPaint!!.color = mBackgroundColor
                canvas.drawRoundRect(
                    mBackgroundBounds!!,
                    buttonRadius,
                    buttonRadius, mBackgroundPaint!!
                )
            }
        }
    }

    private fun drawTextAbove(canvas: Canvas) {
        //计算Baseline绘制的Y坐标
        val y = canvas.height / 2 - (mTextPaint!!.descent() / 2 + mTextPaint!!.ascent() / 2)
        if (mCurrentText == null) {
            mCurrentText = ""
        }
        val textWidth = mTextPaint!!.measureText(mCurrentText.toString())
        mTextBottomBorder = y
        mTextRightBorder = (measuredWidth + textWidth) / 2
        when (mState) {
            STATE_NORMAL -> {
                mTextPaint!!.shader = null
                mTextPaint!!.color = textCoverColor
                canvas.drawText(
                    mCurrentText.toString(), (measuredWidth - textWidth) / 2, y,
                    mTextPaint!!
                )
            }
            STATE_PAUSE, STATE_DOWNLOADING -> {

                //进度条压过距离
                val coverLength = measuredWidth * mProgressPercent
                //开始渐变指示器
                val indicator1 = measuredWidth / 2 - textWidth / 2
                //结束渐变指示器
                val indicator2 = measuredWidth / 2 + textWidth / 2
                //文字变色部分的距离
                val coverTextLength = textWidth / 2 - measuredWidth / 2 + coverLength
                val textProgress = coverTextLength / textWidth
                if (coverLength <= indicator1) {
                    mTextPaint!!.shader = null
                    mTextPaint!!.color = mTextColor
                } else if (indicator1 < coverLength && coverLength <= indicator2) {
                    //设置变色效果
                    mProgressTextGradient = LinearGradient(
                        (measuredWidth - textWidth) / 2,
                        0f,
                        (measuredWidth + textWidth) / 2,
                        0f,
                        intArrayOf(
                            textCoverColor, mTextColor
                        ),
                        floatArrayOf(textProgress, textProgress + 0.001f),
                        Shader.TileMode.CLAMP
                    )
                    mTextPaint?.color = mTextColor
                    mTextPaint?.shader = mProgressTextGradient
                } else {
                    mTextPaint?.shader = null
                    mTextPaint?.color = textCoverColor
                }
                canvas.drawText(
                    mCurrentText.toString(), (measuredWidth - textWidth) / 2, y,
                    mTextPaint!!
                )
            }
            STATE_FINISH -> {
                mTextPaint?.color = textCoverColor
                canvas.drawText(
                    mCurrentText.toString(), (measuredWidth - textWidth) / 2, y,
                    mTextPaint!!
                )
                drawLoadingBall(canvas)
            }
        }
    }

    fun drawLoadingBall(canvas: Canvas) {
        for (i in 0..2) {
            canvas.save()
            val translateX = mTextRightBorder + 10 + mBallRadius * 2 * i + mBallSpacing * i
            canvas.translate(translateX, mTextBottomBorder)
            canvas.drawCircle(
                0f, translateYFloats[i], mBallRadius * scaleFloats[i],
                mTextPaint!!
            )
            canvas.restore()
        }
    }

    private fun startAnimators() {
        for (i in mAnimators!!.indices) {
            val animator = mAnimators!![i]
            animator.start()
        }
    }

    private fun stopAnimators() {
        if (mAnimators != null) {
            for (animator in mAnimators!!) {
                if (animator != null && animator.isStarted) {
                    animator.end()
                }
            }
        }
    }

    fun createBallPulseAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = intArrayOf(120, 240, 360)
        for (i in 0..2) {
            val scaleAnim = ValueAnimator.ofFloat(1f, 0.3f, 1f)
            scaleAnim.duration = 750
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()
            scaleAnim.addUpdateListener { animation ->
                scaleFloats[i] = animation.animatedValue as Float
                postInvalidate()
            }
            animators.add(scaleAnim)
        }
        return animators
    }

    fun createBallJumpAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = intArrayOf(70, 140, 210)
        for (i in 0..2) {
            val scaleAnim = ValueAnimator.ofFloat(
                mTextBottomBorder,
                mTextBottomBorder - mBallRadius * 2,
                mTextBottomBorder
            )
            scaleAnim.duration = 600
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()
            scaleAnim.addUpdateListener { animation ->
                translateYFloats[i] = animation.animatedValue as Float
                postInvalidate()
            }
            animators.add(scaleAnim)
        }
        return animators
    }//开启点动画

    //状态确实有改变
    var state: Int
        get() = mState
        set(state) {
            if (mState != state) { //状态确实有改变
                mState = state
                invalidate()
                if (state == STATE_FINISH) {
                    //开启点动画
                    startAnimators()
                } else {
                    stopAnimators()
                }
            }
        }

    /**
     * 设置当前按钮文字
     */
    fun setCurrentText(charSequence: CharSequence?) {
        mCurrentText = charSequence
        invalidate()
    }

    /**
     * 设置带下载进度的文字
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun setProgressText(text: String, progress: Float) {
        if (progress >= minProgress && progress <= maxProgress) {
            val format = DecimalFormat("##0.0")
            mCurrentText = text + format.format(progress.toDouble()) + "%"
            mToProgress = progress
            if (mProgressAnimation!!.isRunning) {
                mProgressAnimation!!.resume()
                mProgressAnimation!!.start()
            } else {
                mProgressAnimation!!.start()
            }
        } else if (progress < minProgress) {
            this.progress = 0f
        } else if (progress > maxProgress) {
            this.progress = 100f
            mCurrentText = "$text$progress%"
            invalidate()
        }
    }

    //设置点动画样式
    private fun setBallStyle(mBallStyle: Int) {
        this.mBallStyle = mBallStyle
        mAnimators = if (mBallStyle == STYLE_BALL_PULSE) {
            createBallPulseAnimators()
        } else {
            createBallJumpAnimators()
        }
    }

    fun setBorderWidth(width: Int) {
        borderWidth = dp2px(width).toFloat()
    }

    fun getTextColor(): Int {
        return mTextColor
    }

    override fun setTextColor(textColor: Int) {
        mTextColor = textColor
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        mState = ss.state
        progress = ss.progress.toFloat()
        mCurrentText = ss.currentText
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return SavedState(
            superState,
            progress.toInt(), mState, mCurrentText.toString()
        )
    }

    class SavedState : BaseSavedState {
        var progress: Int
        var state: Int
        var currentText: String?

        constructor(parcel: Parcelable?, progress: Int, state: Int, currentText: String?) : super(
            parcel
        ) {
            this.progress = progress
            this.state = state
            this.currentText = currentText
        }

        private constructor(`in`: Parcel) : super(`in`) {
            progress = `in`.readInt()
            state = `in`.readInt()
            currentText = `in`.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(progress)
            out.writeInt(state)
            out.writeString(currentText)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    private fun dp2px(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    companion object {
        const val STATE_NORMAL = 0 //开始下载
        const val STATE_DOWNLOADING = 1 //下载之中
        const val STATE_PAUSE = 2 //暂停下载
        const val STATE_FINISH = 3 //下载完成
        const val STATE_OPEEN = 4 //打开
        const val STYLE_BALL_PULSE = 1
        const val STYLE_BALL_JUMP = 2
        const val SCALE = 1.0f
    }

    init {
        if (!isInEditMode) {
            initAttrs(context, attrs)
            init()
            setupAnimations()
        }
    }
}