package com.qipa.newboxproject.app.weight

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Parcel
import android.os.Parcelable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.util.ColorUtils

class SwitchButton : CompoundButton {
    private var mThumbDrawable: Drawable? = null
    private var mBackDrawable: Drawable? = null
    private var mBackColor: ColorStateList? = null
    private var mThumbColor: ColorStateList? = null
    private var mThumbRadius = 0f
    private var mBackRadius = 0f
    private var mThumbMargin: RectF? = null
    private var mBackMeasureRatio = 0f
    var animationDuration: Long = 0

    // fade back drawable or color when dragging or animating
    var isFadeBack = false
    private var mTintColor = 0
    var thumbSizeF: PointF? = null
        private set
    private var mCurrThumbColor = 0
    private var mCurrBackColor = 0
    private var mNextBackColor = 0
    private var mOnTextColor = 0
    private var mOffTextColor = 0
    private var mCurrentBackDrawable: Drawable? = null
    private var mNextBackDrawable: Drawable? = null
    private var mThumbRectF: RectF? = null
    private var mBackRectF: RectF? = null
    private var mSafeRectF: RectF? = null
    private var mTextOnRectF: RectF? = null
    private var mTextOffRectF: RectF? = null
    private var mPaint: Paint? = null

    // whether using Drawable for thumb or back
    private var mIsThumbUseDrawable = false
    private var mIsBackUseDrawable = false
    private var mDrawDebugRect = false
    private var mProcessAnimator: ObjectAnimator? = null

    // animation control
    private var mProcess = 0f

    // temp position of thumb when dragging or animating
    private var mPresentThumbRectF: RectF? = null
    private var mStartX = 0f
    private var mStartY = 0f
    private var mLastX = 0f
    private var mTouchSlop = 0
    private var mClickTimeout = 0
    private var mRectPaint: Paint? = null
    private var mTextOn: CharSequence? = null
    private var mTextOff: CharSequence? = null
    private var mTextPaint: TextPaint? = null
    private var mOnLayout: Layout? = null
    private var mOffLayout: Layout? = null
    private var mTextWidth = 0f
    private var mTextHeight = 0f
    private var mTextMarginH = 0f

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    private fun init(attrs: AttributeSet?) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mClickTimeout =
            ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectPaint!!.style = Paint.Style.STROKE
        mRectPaint!!.strokeWidth = resources.displayMetrics.density
        mTextPaint = paint
        mThumbRectF = RectF()
        mBackRectF = RectF()
        mSafeRectF = RectF()
        thumbSizeF = PointF()
        mThumbMargin = RectF()
        mTextOnRectF = RectF()
        mTextOffRectF = RectF()
        mProcessAnimator = ObjectAnimator.ofFloat(this, "process", 0f, 0f).setDuration(
            DEFAULT_ANIMATION_DURATION.toLong()
        )
        mProcessAnimator!!.interpolator = AccelerateDecelerateInterpolator()
        mPresentThumbRectF = RectF()
        val res = resources
        val density = res.displayMetrics.density
        var thumbDrawable: Drawable? = null
        var thumbColor: ColorStateList? = null
        var margin = density * DEFAULT_THUMB_MARGIN_DP
        var marginLeft = 0f
        var marginRight = 0f
        var marginTop = 0f
        var marginBottom = 0f
        var thumbWidth = density * DEFAULT_THUMB_SIZE_DP
        var thumbHeight = density * DEFAULT_THUMB_SIZE_DP
        var thumbRadius = density * DEFAULT_THUMB_SIZE_DP / 2
        var backRadius = thumbRadius
        var backDrawable: Drawable? = null
        var backColor: ColorStateList? = null
        var backMeasureRatio = DEFAULT_BACK_MEASURE_RATIO
        var animationDuration = DEFAULT_ANIMATION_DURATION
        var fadeBack = true
        var tintColor = Int.MIN_VALUE
        var textOn: String? = null
        var textOff: String? = null
        var textMarginH = density * DEFAULT_TEXT_MARGIN_DP
        val ta = if (attrs == null) null else context.obtainStyledAttributes(
            attrs,
            R.styleable.SwitchButton
        )
        if (ta != null) {
            thumbDrawable = ta.getDrawable(R.styleable.SwitchButton_kswThumbDrawable)
            thumbColor = ta.getColorStateList(R.styleable.SwitchButton_kswThumbColor)
            margin = ta.getDimension(R.styleable.SwitchButton_kswThumbMargin, margin)
            marginLeft = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginLeft, margin)
            marginRight = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginRight, margin)
            marginTop = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginTop, margin)
            marginBottom = ta.getDimension(R.styleable.SwitchButton_kswThumbMarginBottom, margin)
            thumbWidth = ta.getDimension(R.styleable.SwitchButton_kswThumbWidth, thumbWidth)
            thumbHeight = ta.getDimension(R.styleable.SwitchButton_kswThumbHeight, thumbHeight)
            thumbRadius = ta.getDimension(
                R.styleable.SwitchButton_kswThumbRadius,
                Math.min(thumbWidth, thumbHeight) / 2f
            )
            backRadius =
                ta.getDimension(R.styleable.SwitchButton_kswBackRadius, thumbRadius + density * 2f)
            backDrawable = ta.getDrawable(R.styleable.SwitchButton_kswBackDrawable)
            backColor = ta.getColorStateList(R.styleable.SwitchButton_kswBackColor)
            backMeasureRatio =
                ta.getFloat(R.styleable.SwitchButton_kswBackMeasureRatio, backMeasureRatio)
            animationDuration =
                ta.getInteger(R.styleable.SwitchButton_kswAnimationDuration, animationDuration)
            fadeBack = ta.getBoolean(R.styleable.SwitchButton_kswFadeBack, true)
            tintColor = ta.getColor(R.styleable.SwitchButton_kswTintColor, tintColor)
            textOn = ta.getString(R.styleable.SwitchButton_kswTextOn)
            textOff = ta.getString(R.styleable.SwitchButton_kswTextOff)
            textMarginH = ta.getDimension(R.styleable.SwitchButton_kswTextMarginH, textMarginH)
            ta.recycle()
        }

        // text
        mTextOn = textOn
        mTextOff = textOff
        mTextMarginH = textMarginH

        // thumb drawable and color
        mThumbDrawable = thumbDrawable
        mThumbColor = thumbColor
        mIsThumbUseDrawable = mThumbDrawable != null
        mTintColor = tintColor
        if (mTintColor == Int.MIN_VALUE) {
            mTintColor = DEFAULT_TINT_COLOR
        }
        if (!mIsThumbUseDrawable && mThumbColor == null) {
            mThumbColor = ColorUtils.generateThumbColorWithTintColor(mTintColor)
            mCurrThumbColor = mThumbColor!!.defaultColor
        }
        if (mIsThumbUseDrawable) {
            thumbWidth = Math.max(thumbWidth, mThumbDrawable!!.minimumWidth.toFloat())
            thumbHeight = Math.max(thumbHeight, mThumbDrawable!!.minimumHeight.toFloat())
        }
        thumbSizeF!![thumbWidth] = thumbHeight

        // back drawable and color
        mBackDrawable = backDrawable
        mBackColor = backColor
        mIsBackUseDrawable = mBackDrawable != null
        if (!mIsBackUseDrawable && mBackColor == null) {
            mBackColor = ColorUtils.generateBackColorWithTintColor(mTintColor)
            mCurrBackColor = mBackColor!!.defaultColor
            mNextBackColor = mBackColor!!.getColorForState(CHECKED_PRESSED_STATE, mCurrBackColor)
        }

        // margin
        mThumbMargin!![marginLeft, marginTop, marginRight] = marginBottom

        // size & measure params must larger than 1
        mBackMeasureRatio =
            if (mThumbMargin!!.width() >= 0) Math.max(backMeasureRatio, 1f) else backMeasureRatio
        mThumbRadius = thumbRadius
        mBackRadius = backRadius
        this.animationDuration = animationDuration.toLong()
        isFadeBack = fadeBack
        mProcessAnimator!!.duration = this.animationDuration
        isFocusable = true
        isClickable = true

        // sync checked status
        if (isChecked) {
            process = 1f
        }
    }

    private fun makeLayout(text: CharSequence): Layout {
        return StaticLayout(
            text,
            mTextPaint,
            Math.ceil(Layout.getDesiredWidth(text, mTextPaint).toDouble()).toInt(),
            Layout.Alignment.ALIGN_CENTER,
            1f,
            0F,
            false
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mOnLayout == null && mTextOn != null) {
            mOnLayout = makeLayout(mTextOn!!)
        }
        if (mOffLayout == null && mTextOff != null) {
            mOffLayout = makeLayout(mTextOff!!)
        }
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var measuredWidth: Int
        var minWidth = (thumbSizeF!!.x * mBackMeasureRatio).toInt()
        if (mIsBackUseDrawable) {
            minWidth = Math.max(minWidth, mBackDrawable!!.minimumWidth)
        }
        val onWidth = if (mOnLayout != null) mOnLayout!!.width.toFloat() else 0.toFloat()
        val offWidth = if (mOffLayout != null) mOffLayout!!.width.toFloat() else 0.toFloat()
        if (onWidth != 0f || offWidth != 0f) {
            mTextWidth = Math.max(onWidth, offWidth) + mTextMarginH * 2
            val left = minWidth - thumbSizeF!!.x
            if (left < mTextWidth) {
                minWidth += (mTextWidth - left).toInt()
            }
        }
        minWidth = Math.max(
            minWidth,
            (minWidth + mThumbMargin!!.left + mThumbMargin!!.right).toInt()
        )
        minWidth = Math.max(minWidth, minWidth + paddingLeft + paddingRight)
        minWidth = Math.max(minWidth, suggestedMinimumWidth)
        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = Math.max(minWidth, widthSize)
        } else {
            measuredWidth = minWidth
            if (widthMode == MeasureSpec.AT_MOST) {
                measuredWidth = Math.min(measuredWidth, widthSize)
            }
        }
        return measuredWidth
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var measuredHeight: Int
        var minHeight =
            Math.max(thumbSizeF!!.y, thumbSizeF!!.y + mThumbMargin!!.top + mThumbMargin!!.right)
                .toInt()
        val onHeight = if (mOnLayout != null) mOnLayout!!.height.toFloat() else 0.toFloat()
        val offHeight = if (mOffLayout != null) mOffLayout!!.height.toFloat() else 0.toFloat()
        if (onHeight != 0f || offHeight != 0f) {
            mTextHeight = Math.max(onHeight, offHeight)
            minHeight = Math.max(minHeight.toFloat(), mTextHeight).toInt()
        }
        minHeight = Math.max(minHeight, suggestedMinimumHeight)
        minHeight = Math.max(minHeight, minHeight + paddingTop + paddingBottom)
        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = Math.max(minHeight, heightSize)
        } else {
            measuredHeight = minHeight
            if (heightMode == MeasureSpec.AT_MOST) {
                measuredHeight = Math.min(measuredHeight, heightSize)
            }
        }
        return measuredHeight
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            setup()
        }
    }

    /**
     * set up the rect of back and thumb
     */
    private fun setup() {
        var thumbTop = paddingTop + Math.max(0f, mThumbMargin!!.top)
        val thumbLeft = paddingLeft + Math.max(0f, mThumbMargin!!.left)
        if (mOnLayout != null && mOffLayout != null) {
            if (mThumbMargin!!.top + mThumbMargin!!.bottom > 0) {
                // back is higher than thumb
                val addition =
                    (measuredHeight - paddingBottom - paddingTop - thumbSizeF!!.y - mThumbMargin!!.top - mThumbMargin!!.bottom) / 2
                thumbTop += addition
            }
        }
        if (mIsThumbUseDrawable) {
            thumbSizeF!!.x = Math.max(thumbSizeF!!.x, mThumbDrawable!!.minimumWidth.toFloat())
            thumbSizeF!!.y = Math.max(thumbSizeF!!.y, mThumbDrawable!!.minimumHeight.toFloat())
        }
        mThumbRectF!![thumbLeft, thumbTop, thumbLeft + thumbSizeF!!.x] = thumbTop + thumbSizeF!!.y
        val backLeft = mThumbRectF!!.left - mThumbMargin!!.left
        val textDiffWidth = Math.min(
            0f,
            (Math.max(
                thumbSizeF!!.x * mBackMeasureRatio,
                thumbSizeF!!.x + mTextWidth
            ) - mThumbRectF!!.width() - mTextWidth) / 2
        )
        val textDiffHeight = Math.min(
            0f,
            (mThumbRectF!!.height() + mThumbMargin!!.top + mThumbMargin!!.bottom - mTextHeight) / 2
        )
        mBackRectF!![backLeft + textDiffWidth, mThumbRectF!!.top - mThumbMargin!!.top + textDiffHeight, backLeft + mThumbMargin!!.left + Math.max(
            thumbSizeF!!.x * mBackMeasureRatio,
            thumbSizeF!!.x + mTextWidth
        ) + mThumbMargin!!.right - textDiffWidth] =
            mThumbRectF!!.bottom + mThumbMargin!!.bottom - textDiffHeight
        mSafeRectF!![mThumbRectF!!.left, 0f, mBackRectF!!.right - mThumbMargin!!.right - mThumbRectF!!.width()] =
            0f
        val minBackRadius = Math.min(mBackRectF!!.width(), mBackRectF!!.height()) / 2f
        mBackRadius = Math.min(minBackRadius, mBackRadius)
        if (mBackDrawable != null) {
            mBackDrawable!!.setBounds(
                mBackRectF!!.left.toInt(),
                mBackRectF!!.top.toInt(), mBackRectF!!.right.toInt(), mBackRectF!!.bottom.toInt()
            )
        }
        if (mOnLayout != null) {
            val marginOnX =
                mBackRectF!!.left + (mBackRectF!!.width() - mThumbRectF!!.width() - mOnLayout!!.width) / 2 - mThumbMargin!!.left + mTextMarginH * if (mThumbMargin!!.left > 0) 1 else -1
            val marginOnY = mBackRectF!!.top + (mBackRectF!!.height() - mOnLayout!!.height) / 2
            mTextOnRectF!![marginOnX, marginOnY, marginOnX + mOnLayout!!.width] =
                marginOnY + mOnLayout!!.height
        }
        if (mOffLayout != null) {
            val marginOffX =
                mBackRectF!!.right - (mBackRectF!!.width() - mThumbRectF!!.width() - mOffLayout!!.width) / 2 + mThumbMargin!!.right - mOffLayout!!.width - mTextMarginH * if (mThumbMargin!!.right > 0) 1 else -1
            val marginOffY = mBackRectF!!.top + (mBackRectF!!.height() - mOffLayout!!.height) / 2
            mTextOffRectF!![marginOffX, marginOffY, marginOffX + mOffLayout!!.width] =
                marginOffY + mOffLayout!!.height
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // fade back
        if (mIsBackUseDrawable) {
            if (isFadeBack && mCurrentBackDrawable != null && mNextBackDrawable != null) {
                var alpha = (255 * if (isChecked) process else 1 - process).toInt()
                mCurrentBackDrawable!!.alpha = alpha
                mCurrentBackDrawable!!.draw(canvas)
                alpha = 255 - alpha
                mNextBackDrawable!!.alpha = alpha
                mNextBackDrawable!!.draw(canvas)
            } else {
                mBackDrawable!!.alpha = 255
                mBackDrawable!!.draw(canvas)
            }
        } else {
            if (isFadeBack) {
                var alpha: Int
                var colorAlpha: Int

                // curr back
                alpha = (255 * if (isChecked) process else 1 - process).toInt()
                colorAlpha = Color.alpha(mCurrBackColor)
                colorAlpha = colorAlpha * alpha / 255
                mPaint!!.setARGB(
                    colorAlpha,
                    Color.red(mCurrBackColor),
                    Color.green(mCurrBackColor),
                    Color.blue(mCurrBackColor)
                )
                canvas.drawRoundRect(mBackRectF!!, mBackRadius, mBackRadius, mPaint!!)

                // next back
                alpha = 255 - alpha
                colorAlpha = Color.alpha(mNextBackColor)
                colorAlpha = colorAlpha * alpha / 255
                mPaint!!.setARGB(
                    colorAlpha,
                    Color.red(mNextBackColor),
                    Color.green(mNextBackColor),
                    Color.blue(mNextBackColor)
                )
                canvas.drawRoundRect(mBackRectF!!, mBackRadius, mBackRadius, mPaint!!)
                mPaint!!.alpha = 255
            } else {
                mPaint!!.color = mCurrBackColor
                canvas.drawRoundRect(mBackRectF!!, mBackRadius, mBackRadius, mPaint!!)
            }
        }

        // text
        val switchText = if (process > 0.5) mOnLayout else mOffLayout
        val textRectF = if (process > 0.5) mTextOnRectF else mTextOffRectF
        if (switchText != null && textRectF != null) {
            val alpha =
                (if (255 * process >= 0.75) process * 4 - 3 else if (process < 0.25) 1 - process * 4 else 0) as Int
            val textColor = if (process > 0.5) mOnTextColor else mOffTextColor
            var colorAlpha = Color.alpha(textColor)
            colorAlpha = colorAlpha * alpha / 255
            switchText.paint.setARGB(
                colorAlpha,
                Color.red(textColor),
                Color.green(textColor),
                Color.blue(textColor)
            )
            canvas.save()
            canvas.translate(textRectF.left, textRectF.top)
            switchText.draw(canvas)
            canvas.restore()
        }

        // thumb
        mPresentThumbRectF!!.set(mThumbRectF!!)
        mPresentThumbRectF!!.offset(mProcess * mSafeRectF!!.width(), 0f)
        if (mIsThumbUseDrawable) {
            mThumbDrawable!!.setBounds(
                mPresentThumbRectF!!.left.toInt(),
                mPresentThumbRectF!!.top.toInt(),
                mPresentThumbRectF!!.right.toInt(), mPresentThumbRectF!!.bottom.toInt()
            )
            mThumbDrawable!!.draw(canvas)
        } else {
            mPaint!!.color = mCurrThumbColor
            canvas.drawRoundRect(mPresentThumbRectF!!, mThumbRadius, mThumbRadius, mPaint!!)
        }
        if (mDrawDebugRect) {
            mRectPaint!!.color = Color.parseColor("#AA0000")
            canvas.drawRect(mBackRectF!!, mRectPaint!!)
            mRectPaint!!.color = Color.parseColor("#0000FF")
            canvas.drawRect(mPresentThumbRectF!!, mRectPaint!!)
            mRectPaint!!.color = Color.parseColor("#00CC00")
            canvas.drawRect((if (process > 0.5) mTextOnRectF else mTextOffRectF)!!, mRectPaint!!)
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (!mIsThumbUseDrawable && mThumbColor != null) {
            mCurrThumbColor = mThumbColor!!.getColorForState(drawableState, mCurrThumbColor)
        } else {
            setDrawableState(mThumbDrawable)
        }
        val nextState = if (isChecked) UNCHECKED_PRESSED_STATE else CHECKED_PRESSED_STATE
        val textColors = textColors
        if (textColors != null) {
            val defaultTextColor = textColors.defaultColor
            mOnTextColor = textColors.getColorForState(CHECKED_PRESSED_STATE, defaultTextColor)
            mOffTextColor = textColors.getColorForState(UNCHECKED_PRESSED_STATE, defaultTextColor)
        }
        if (!mIsBackUseDrawable && mBackColor != null) {
            mCurrBackColor = mBackColor!!.getColorForState(drawableState, mCurrBackColor)
            mNextBackColor = mBackColor!!.getColorForState(nextState, mCurrBackColor)
        } else {
            mNextBackDrawable = if (mBackDrawable is StateListDrawable && isFadeBack) {
                mBackDrawable?.setState(nextState)
                mBackDrawable?.getCurrent()?.mutate()
            } else {
                null
            }
            setDrawableState(mBackDrawable)
            if (mBackDrawable != null) {
                mCurrentBackDrawable = mBackDrawable!!.current.mutate()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || !isClickable) {
            return false
        }
        val action = event.action
        val deltaX = event.x - mStartX
        val deltaY = event.y - mStartY

        // status the view going to change to when finger released
        val nextStatus: Boolean
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                catchView()
                mStartX = event.x
                mStartY = event.y
                mLastX = mStartX
                isPressed = true
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                process = process + (x - mLastX) / mSafeRectF!!.width()
                mLastX = x
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isPressed = false
                nextStatus = statusBasedOnPos
                val time = (event.eventTime - event.downTime).toFloat()
                if (deltaX < mTouchSlop && deltaY < mTouchSlop && time < mClickTimeout) {
                    performClick()
                } else {
                    if (nextStatus != isChecked) {
                        playSoundEffect(SoundEffectConstants.CLICK)
                        isChecked = nextStatus
                    } else {
                        animateToState(nextStatus)
                    }
                }
            }
            else -> {
            }
        }
        return true
    }

    /**
     * return the status based on position of thumb
     *
     * @return
     */
    private val statusBasedOnPos: Boolean
        private get() = process > 0.5f
    var process: Float
        get() = mProcess
        set(process) {
            var tp = process
            if (tp > 1) {
                tp = 1f
            } else if (tp < 0) {
                tp = 0f
            }
            mProcess = tp
            invalidate()
        }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * processing animation
     *
     * @param checked checked or unChecked
     */
    protected fun animateToState(checked: Boolean) {
        if (mProcessAnimator == null) {
            return
        }
        if (mProcessAnimator!!.isRunning) {
            mProcessAnimator!!.cancel()
        }
        mProcessAnimator!!.duration = animationDuration
        if (checked) {
            mProcessAnimator!!.setFloatValues(mProcess, 1f)
        } else {
            mProcessAnimator!!.setFloatValues(mProcess, 0f)
        }
        mProcessAnimator!!.start()
    }

    private fun catchView() {
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    override fun setChecked(checked: Boolean) {
        // animate before super.setChecked() become user may call setChecked again in OnCheckedChangedListener
        if (isChecked != checked) {
            animateToState(checked)
        }
        super.setChecked(checked)
    }

    fun setCheckedImmediately(checked: Boolean) {
        super.setChecked(checked)
        if (mProcessAnimator != null && mProcessAnimator!!.isRunning) {
            mProcessAnimator!!.cancel()
        }
        process = if (checked) 1F else 0.toFloat()
        invalidate()
    }

    fun toggleImmediately() {
        setCheckedImmediately(!isChecked)
    }

    private fun setDrawableState(drawable: Drawable?) {
        if (drawable != null) {
            val myDrawableState = drawableState
            drawable.state = myDrawableState
            invalidate()
        }
    }

    var isDrawDebugRect: Boolean
        get() = mDrawDebugRect
        set(drawDebugRect) {
            mDrawDebugRect = drawDebugRect
            invalidate()
        }
    var thumbDrawable: Drawable?
        get() = mThumbDrawable
        set(thumbDrawable) {
            mThumbDrawable = thumbDrawable
            mIsThumbUseDrawable = mThumbDrawable != null
            setup()
            refreshDrawableState()
            requestLayout()
            invalidate()
        }

    fun setThumbDrawableRes(thumbDrawableRes: Int) {
        thumbDrawable =
            ContextCompat.getDrawable(context, thumbDrawableRes)
    }

    var backDrawable: Drawable?
        get() = mBackDrawable
        set(backDrawable) {
            mBackDrawable = backDrawable
            mIsBackUseDrawable = mBackDrawable != null
            setup()
            refreshDrawableState()
            requestLayout()
            invalidate()
        }

    fun setBackDrawableRes(backDrawableRes: Int) {
        backDrawable =
            ContextCompat.getDrawable(context, backDrawableRes)
    }

    var backColor: ColorStateList?
        get() = mBackColor
        set(backColor) {
            mBackColor = backColor
            if (mBackColor != null) {
                backDrawable = null
            }
            invalidate()
        }

    fun setBackColorRes(backColorRes: Int) {
//		setBackColor(ContextCompat.getColorStateList(getContext(), backColorRes));
    }

    var thumbColor: ColorStateList?
        get() = mThumbColor
        set(thumbColor) {
            mThumbColor = thumbColor
            if (mThumbColor != null) {
                thumbDrawable = null
            }
        }

    fun setThumbColorRes(thumbColorRes: Int) {
//		setThumbColor(ContextCompat.getColorStateList(getContext(), thumbColorRes));
    }

    var backMeasureRatio: Float
        get() = mBackMeasureRatio
        set(backMeasureRatio) {
            mBackMeasureRatio = backMeasureRatio
            requestLayout()
        }
    var thumbMargin: RectF?
        get() = mThumbMargin
        set(thumbMargin) {
            if (thumbMargin == null) {
                setThumbMargin(0f, 0f, 0f, 0f)
            } else {
                setThumbMargin(
                    thumbMargin.left,
                    thumbMargin.top,
                    thumbMargin.right,
                    thumbMargin.bottom
                )
            }
        }

    fun setThumbMargin(left: Float, top: Float, right: Float, bottom: Float) {
        mThumbMargin!![left, top, right] = bottom
        requestLayout()
    }

    fun setThumbSize(width: Float, height: Float) {
        thumbSizeF!![width] = height
        setup()
        requestLayout()
    }

    val thumbWidth: Float
        get() = thumbSizeF!!.x
    val thumbHeight: Float
        get() = thumbSizeF!!.y

    fun setThumbSize(size: PointF?) {
        if (size == null) {
            val defaultSize = resources.displayMetrics.density * DEFAULT_THUMB_SIZE_DP
            setThumbSize(defaultSize, defaultSize)
        } else {
            setThumbSize(size.x, size.y)
        }
    }

    var thumbRadius: Float
        get() = mThumbRadius
        set(thumbRadius) {
            mThumbRadius = thumbRadius
            if (!mIsThumbUseDrawable) {
                invalidate()
            }
        }
    val backSizeF: PointF
        get() = PointF(mBackRectF!!.width(), mBackRectF!!.height())
    var backRadius: Float
        get() = mBackRadius
        set(backRadius) {
            mBackRadius = backRadius
            if (!mIsBackUseDrawable) {
                invalidate()
            }
        }

    // call this method to refresh color states
    var tintColor: Int
        get() = mTintColor
        set(tintColor) {
            mTintColor = tintColor
            mThumbColor = ColorUtils.generateThumbColorWithTintColor(mTintColor)
            mBackColor = ColorUtils.generateBackColorWithTintColor(mTintColor)
            mIsBackUseDrawable = false
            mIsThumbUseDrawable = false
            // call this method to refresh color states
            refreshDrawableState()
            invalidate()
        }

    fun setText(onText: CharSequence?, offText: CharSequence?) {
        mTextOn = onText
        mTextOff = offText
        mOnLayout = null
        mOffLayout = null
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.onText = mTextOn
        ss.offText = mTextOff
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        setText(ss.onText, ss.offText)
        super.onRestoreInstanceState(ss.superState)
    }

    internal class SavedState : BaseSavedState {
        var onText: CharSequence? = null
        var offText: CharSequence? = null

        constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            onText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
            offText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(onText, out, flags)
            TextUtils.writeToParcel(offText, out, flags)
        }


    }

    companion object {
        const val DEFAULT_BACK_MEASURE_RATIO = 1.8f
        const val DEFAULT_THUMB_SIZE_DP = 20
        const val DEFAULT_THUMB_MARGIN_DP = 2
        const val DEFAULT_TEXT_MARGIN_DP = 2
        const val DEFAULT_ANIMATION_DURATION = 250
        const val DEFAULT_TINT_COLOR = 0x327FC2
        private val CHECKED_PRESSED_STATE = intArrayOf(
            android.R.attr.state_checked,
            android.R.attr.state_enabled,
            android.R.attr.state_pressed
        )
        private val UNCHECKED_PRESSED_STATE = intArrayOf(
            -android.R.attr.state_checked,
            android.R.attr.state_enabled,
            android.R.attr.state_pressed
        )
    }
}