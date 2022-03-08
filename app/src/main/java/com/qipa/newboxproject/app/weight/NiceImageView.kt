package com.qipa.newboxproject.app.weight

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.util.ConvertUtils
import com.qipa.newboxproject.R

class NiceImageView(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    private val contexts: Context = context
    private var isCircle // 是否显示为圆形，如果为圆形则设置的corner无效
            = false
    private var isCoverSrc // border、inner_border是否覆盖图片
            = false
    private var borderWidth // 边框宽度
            = 0
    private var borderColor: Int = Color.WHITE // 边框颜色
    private var innerBorderWidth // 内层边框宽度
            = 0
    private var innerBorderColor: Int = Color.WHITE // 内层边框充色
    private var cornerRadius // 统一设置圆角半径，优先级高于单独设置每个角的半径
            = 0
    private var cornerTopLeftRadius // 左上角圆角半径
            = 0
    private var cornerTopRightRadius // 右上角圆角半径
            = 0
    private var cornerBottomLeftRadius // 左下角圆角半径
            = 0
    private var cornerBottomRightRadius // 右下角圆角半径
            = 0
    private var maskColor // 遮罩颜色
            = 0
    private var xfermode: Xfermode? = null
    private var width = 0f
    private var height = 0f
    private var radius = 0f
    private val borderRadii: FloatArray
    private val srcRadii: FloatArray
    private var srcRectF // 图片占的矩形区域
            : RectF
    private val borderRectF // 边框的矩形区域
            : RectF
    private val paint: Paint
    private val path // 用来裁剪图片的ptah
            : Path
    private var srcPath // 图片区域大小的path
            : Path? = null

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {}

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w.toFloat()
        height = h.toFloat()
        initBorderRectF()
        initSrcRectF()
    }

    protected override fun onDraw(canvas: Canvas) {
        // 使用图形混合模式来显示指定区域的图片
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG)
        if (!isCoverSrc) {
            val sx = 1.0f * (width - 2 * borderWidth - 2 * innerBorderWidth) / width
            val sy = 1.0f * (height - 2 * borderWidth - 2 * innerBorderWidth) / height
            // 缩小画布，使图片内容不被borders覆盖
            canvas.scale(sx, sy, width / 2.0f, height / 2.0f)
        }
        super.onDraw(canvas)
        paint.reset()
        path.reset()
        if (isCircle) {
            path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW)
        } else {
            path.addRoundRect(srcRectF, srcRadii, Path.Direction.CCW)
        }
        paint.setAntiAlias(true)
        paint.setStyle(Paint.Style.FILL)
        paint.setXfermode(xfermode)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(path, paint)
        } else {
            srcPath?.addRect(srcRectF, Path.Direction.CCW)
            // 计算tempPath和path的差集
            srcPath?.op(path, Path.Op.DIFFERENCE)
            srcPath?.let { canvas.drawPath(it, paint) }
        }
        paint.setXfermode(null)

        // 绘制遮罩
        if (maskColor != 0) {
            paint.setColor(maskColor)
            canvas.drawPath(path, paint)
        }
        // 恢复画布
        canvas.restore()
        // 绘制边框
        drawBorders(canvas)
    }

    private fun drawBorders(canvas: Canvas) {
        if (isCircle) {
            if (borderWidth > 0) {
                drawCircleBorder(canvas, borderWidth, borderColor, radius - borderWidth / 2.0f)
            }
            if (innerBorderWidth > 0) {
                drawCircleBorder(
                    canvas,
                    innerBorderWidth,
                    innerBorderColor,
                    radius - borderWidth - innerBorderWidth / 2.0f
                )
            }
        } else {
            if (borderWidth > 0) {
                drawRectFBorder(canvas, borderWidth, borderColor, borderRectF, borderRadii)
            }
        }
    }

    private fun drawCircleBorder(
        canvas: Canvas,
        borderWidth: Int,
        borderColor: Int,
        radius: Float
    ) {
        initBorderPaint(borderWidth, borderColor)
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW)
        canvas.drawPath(path, paint)
    }

    private fun drawRectFBorder(
        canvas: Canvas,
        borderWidth: Int,
        borderColor: Int,
        rectF: RectF,
        radii: FloatArray
    ) {
        initBorderPaint(borderWidth, borderColor)
        path.addRoundRect(rectF, radii, Path.Direction.CCW)
        canvas.drawPath(path, paint)
    }

    private fun initBorderPaint(borderWidth: Int, borderColor: Int) {
        path.reset()
        paint.setStrokeWidth(borderWidth.toFloat())
        paint.setColor(borderColor)
        paint.setStyle(Paint.Style.STROKE)
    }

    /**
     * 计算外边框的RectF
     */
    private fun initBorderRectF() {
        if (!isCircle) {
            borderRectF.set(
                borderWidth / 2.0f,
                borderWidth / 2.0f,
                width - borderWidth / 2.0f,
                height - borderWidth / 2.0f
            )
        }
    }

    /**
     * 计算图片原始区域的RectF
     */
    private fun initSrcRectF() {
        if (isCircle) {
            radius = Math.min(width, height) / 2.0f
            srcRectF.set(
                width / 2.0f - radius,
                height / 2.0f - radius,
                width / 2.0f + radius,
                height / 2.0f + radius
            )
        } else {
            srcRectF.set(0f, 0f, width, height)
            if (isCoverSrc) {
                srcRectF = borderRectF
            }
        }
    }

    /**
     * 计算RectF的圆角半径
     */
    private fun calculateRadii() {
        if (isCircle) {
            return
        }
        if (cornerRadius > 0) {
            for (i in borderRadii.indices) {
                borderRadii[i] = cornerRadius.toFloat()
                srcRadii[i] = cornerRadius - borderWidth / 2.0f
            }
        } else {
            borderRadii[1] = cornerTopLeftRadius.toFloat()
            borderRadii[0] = borderRadii[1]
            borderRadii[3] = cornerTopRightRadius.toFloat()
            borderRadii[2] = borderRadii[3]
            borderRadii[5] = cornerBottomRightRadius.toFloat()
            borderRadii[4] = borderRadii[5]
            borderRadii[7] = cornerBottomLeftRadius.toFloat()
            borderRadii[6] = borderRadii[7]
            srcRadii[1] = cornerTopLeftRadius - borderWidth / 2.0f
            srcRadii[0] = srcRadii[1]
            srcRadii[3] = cornerTopRightRadius - borderWidth / 2.0f
            srcRadii[2] = srcRadii[3]
            srcRadii[5] = cornerBottomRightRadius - borderWidth / 2.0f
            srcRadii[4] = srcRadii[5]
            srcRadii[7] = cornerBottomLeftRadius - borderWidth / 2.0f
            srcRadii[6] = srcRadii[7]
        }
    }

    private fun calculateRadiiAndRectF(reset: Boolean) {
        if (reset) {
            cornerRadius = 0
        }
        calculateRadii()
        initBorderRectF()
        invalidate()
    }

    /**
     * 目前圆角矩形情况下不支持inner_border，需要将其置0
     */
    private fun clearInnerBorderWidth() {
        if (!isCircle) {
            innerBorderWidth = 0
        }
    }

    fun isCoverSrc(isCoverSrc: Boolean) {
        this.isCoverSrc = isCoverSrc
        initSrcRectF()
        invalidate()
    }

    fun isCircle(isCircle: Boolean) {
        this.isCircle = isCircle
        clearInnerBorderWidth()
        initSrcRectF()
        invalidate()
    }

    fun setBorderWidth(borderWidth: Int) {
        this.borderWidth = ConvertUtils.dp2px(borderWidth.toFloat())
        calculateRadiiAndRectF(false)
    }

    fun setBorderColor(@ColorInt borderColor: Int) {
        this.borderColor = borderColor
        invalidate()
    }

    fun setInnerBorderWidth(innerBorderWidth: Int) {
        this.innerBorderWidth = ConvertUtils.dp2px(innerBorderWidth.toFloat())
        clearInnerBorderWidth()
        invalidate()
    }

    fun setInnerBorderColor(@ColorInt innerBorderColor: Int) {
        this.innerBorderColor = innerBorderColor
        invalidate()
    }

    fun setCornerRadius(cornerRadius: Int) {
        this.cornerRadius = ConvertUtils.dp2px(cornerRadius.toFloat())
        calculateRadiiAndRectF(false)
    }

    fun setCornerTopLeftRadius(cornerTopLeftRadius: Int) {
        this.cornerTopLeftRadius = ConvertUtils.dp2px(cornerTopLeftRadius.toFloat())
        calculateRadiiAndRectF(true)
    }

    fun setCornerTopRightRadius(cornerTopRightRadius: Int) {
        this.cornerTopRightRadius = ConvertUtils.dp2px(cornerTopRightRadius.toFloat())
        calculateRadiiAndRectF(true)
    }

    fun setCornerBottomLeftRadius(cornerBottomLeftRadius: Int) {
        this.cornerBottomLeftRadius = ConvertUtils.dp2px(cornerBottomLeftRadius.toFloat())
        calculateRadiiAndRectF(true)
    }

    fun setCornerBottomRightRadius(cornerBottomRightRadius: Int) {
        this.cornerBottomRightRadius = ConvertUtils.dp2px(cornerBottomRightRadius.toFloat())
        calculateRadiiAndRectF(true)
    }

    fun setMaskColor(@ColorInt maskColor: Int) {
        this.maskColor = maskColor
        invalidate()
    }

    init {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.NiceImageView, 0, 0)
        for (i in 0 until ta.getIndexCount()) {
            val attr: Int = ta.getIndex(i)
            if (attr == R.styleable.NiceImageView_is_cover_src) {
                isCoverSrc = ta.getBoolean(attr, isCoverSrc)
            } else if (attr == R.styleable.NiceImageView_is_circle) {
                isCircle = ta.getBoolean(attr, isCircle)
            } else if (attr == R.styleable.NiceImageView_border_width) {
                borderWidth = ta.getDimensionPixelSize(attr, borderWidth)
            } else if (attr == R.styleable.NiceImageView_border_color) {
                borderColor = ta.getColor(attr, borderColor)
            } else if (attr == R.styleable.NiceImageView_inner_border_width) {
                innerBorderWidth = ta.getDimensionPixelSize(attr, innerBorderWidth)
            } else if (attr == R.styleable.NiceImageView_inner_border_color) {
                innerBorderColor = ta.getColor(attr, innerBorderColor)
            } else if (attr == R.styleable.NiceImageView_corner_radius) {
                cornerRadius = ta.getDimensionPixelSize(attr, cornerRadius)
            } else if (attr == R.styleable.NiceImageView_corner_top_left_radius) {
                cornerTopLeftRadius = ta.getDimensionPixelSize(attr, cornerTopLeftRadius)
            } else if (attr == R.styleable.NiceImageView_corner_top_right_radius) {
                cornerTopRightRadius = ta.getDimensionPixelSize(attr, cornerTopRightRadius)
            } else if (attr == R.styleable.NiceImageView_corner_bottom_left_radius) {
                cornerBottomLeftRadius = ta.getDimensionPixelSize(attr, cornerBottomLeftRadius)
            } else if (attr == R.styleable.NiceImageView_corner_bottom_right_radius) {
                cornerBottomRightRadius = ta.getDimensionPixelSize(attr, cornerBottomRightRadius)
            } else if (attr == R.styleable.NiceImageView_mask_color) {
                maskColor = ta.getColor(attr, maskColor)
            }
        }
        ta.recycle()
        borderRadii = FloatArray(8)
        srcRadii = FloatArray(8)
        borderRectF = RectF()
        srcRectF = RectF()
        paint = Paint()
        path = Path()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        } else {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            srcPath = Path()
        }
        calculateRadii()
        clearInnerBorderWidth()
    }
}