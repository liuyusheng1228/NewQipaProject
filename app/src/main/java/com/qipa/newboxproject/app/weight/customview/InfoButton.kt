package com.qipa.newboxproject.app.weight.customview

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.qipa.newboxproject.R

/**
 * 信息按钮
 */
class InfoButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private val mLabelView: TextView
    private val mInfoView: TextView
    private var mIconSize = 0

    /**
     * 设置 icon
     *
     * @param icon icon
     */
    fun setIcon(icon: Drawable?) {
        if (icon != null) {
            if (mIconSize >= 0) {
                icon.setBounds(0, 0, mIconSize, mIconSize)
            } else {
                icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
            }
        }
        mLabelView.setCompoundDrawables(icon, null, null, null)
    }

    /**
     * 设置 icon 大小
     *
     * @param size icon 大小
     */
    fun setIconSize(size: Int) {
        mIconSize = size
        val icon = mLabelView.compoundDrawables[0]
        icon?.let { setIcon(it) }
    }

    /**
     * 设置 icon 内边距
     *
     * @param padding icon 内边距
     */
    fun setIconPadding(padding: Int) {
        mLabelView.compoundDrawablePadding = padding
    }
    /**
     * 获取标签文本
     *
     * @return 标签文本
     */
    /**
     * 设置标签
     *
     * @param label 标签文本
     */
    var label: CharSequence?
        get() = mLabelView.text
        set(label) {
            mLabelView.text = label
        }

    /**
     * 设置标签文字颜色
     *
     * @param color 文字颜色
     */
    fun setLabelTextColor(@ColorInt color: Int) {
        mLabelView.setTextColor(color)
    }

    /**
     * 设置标签字体大小
     *
     * @param size 字体大小
     */
    fun setLabelTextSize(size: Float) {
        setLabelTextRawSize(sp2px(size).toFloat())
    }

    /**
     * 设置标签字体真实大小
     *
     * @param size 字体真实大小
     */
    private fun setLabelTextRawSize(size: Float) {
        mLabelView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }
    /**
     * 获取信息
     *
     * @return 信息文本
     */
    /**
     * 设置信息
     *
     * @param info 信息文本
     */
    var info: CharSequence?
        get() = mInfoView.text.toString()
        set(info) {
            mInfoView.text = info
        }

    /**
     * 设置信息文字颜色
     *
     * @param color 文字颜色
     */
    fun setInfoTextColor(@ColorInt color: Int) {
        mInfoView.setTextColor(color)
    }

    /**
     * 设置信息字体大小
     *
     * @param size 字体大小
     */
    fun setInfoTextSize(size: Float) {
        setInfoTextRawSize(sp2px(size).toFloat())
    }

    /**
     * 设置信息多行
     *
     * @param multiLine true 表示信息多行
     */
    private fun setInfoMultiLine(multiLine: Boolean) {
        mInfoView.isSingleLine = !multiLine
    }

    /**
     * 设置提示
     *
     * @param hint 提示文本
     */
    fun setHint(hint: CharSequence?) {
        mInfoView.hint = hint
    }

    /**
     * 设置提示文字颜色
     *
     * @param color 文字颜色
     */
    fun setHintTextColor(@ColorInt color: Int) {
        mInfoView.setHintTextColor(color)
    }

    /**
     * 设置内容内边距
     *
     * @param padding 内容内边距
     */
    fun setContentPadding(padding: Int) {
        val lp = mLabelView.layoutParams as LayoutParams
        lp.rightMargin = padding
        mLabelView.layoutParams = lp
    }

    /**
     * 设置信息字体真实大小
     *
     * @param size 字体真实大小
     */
    private fun setInfoTextRawSize(size: Float) {
        mInfoView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    /**
     * 设置 forward 图标
     *
     * @param forwardIcon forward 图标
     */
    fun setForwardIcon(forwardIcon: Drawable?) {
        mInfoView.setCompoundDrawablesWithIntrinsicBounds(null, null, forwardIcon, null)
    }

    /**
     * 设置光标是否在最后
     *
     * @param end true 表示光标在最后
     */
    fun setGravityAtEnd(end: Boolean) {
        if (end) {
            mInfoView.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        } else {
            mInfoView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getParcelable("base_state"))
        mInfoView.onRestoreInstanceState(bundle.getParcelable("info_view_state"))
        mInfoView.text = bundle.getString("info")
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        val baseState = super.onSaveInstanceState()
        val editViewState = mInfoView.onSaveInstanceState()
        bundle.putParcelable("base_state", baseState)
        bundle.putParcelable("info_view_state", editViewState)
        bundle.putString("info", mInfoView.text.toString())
        return bundle
    }
    fun dp2px(dp: Float): Int {
        return _2px(TypedValue.COMPLEX_UNIT_DIP, dp)
    }

    fun px2dp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }
    fun sp2px(sp: Float): Int {
        return _2px(TypedValue.COMPLEX_UNIT_SP, sp)
    }

    private fun _2px(unit: Int, value: Float): Int {
        return TypedValue.applyDimension(unit, value, Resources.getSystem().displayMetrics)
            .toInt()
    }

    init {
        gravity = Gravity.CENTER_VERTICAL
        val paddingLeftRight: Int = dp2px(12f)
        setPadding(paddingLeftRight, 0, paddingLeftRight, 0)
        mLabelView = TextView(context)
        val lvlp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lvlp.rightMargin = dp2px(12f)
        mLabelView.layoutParams = lvlp
        mLabelView.gravity = Gravity.CENTER_VERTICAL
        addView(mLabelView)
        mInfoView = TextView(context)
        val evlp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mInfoView.layoutParams = evlp
        mInfoView.compoundDrawablePadding = dp2px(8f)
        mInfoView.ellipsize = TextUtils.TruncateAt.END
        addView(mInfoView)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.InfoButton)
        val iconSize = ta.getDimensionPixelSize(R.styleable.InfoButton_iconSize, -1)
        setIconSize(iconSize)
        val icon = ta.getDrawable(R.styleable.InfoButton_icon)
        icon?.let { setIcon(it) }
        val iconPadding =
            ta.getDimensionPixelSize(R.styleable.InfoButton_iconPadding, sp2px(8f))
        setIconPadding(iconPadding)
        val labelText = ta.getString(R.styleable.InfoButton_label)
        if (!TextUtils.isEmpty(labelText)) {
            label = labelText
        }
        val labelTextColor = ta.getColor(R.styleable.InfoButton_labelTextColor, 0)
        if (labelTextColor != 0) {
            setLabelTextColor(labelTextColor)
        }
        val labelTextSize =
            ta.getDimensionPixelSize(R.styleable.InfoButton_labelTextSize, 0).toFloat()
        if (labelTextSize > 0) {
            setLabelTextRawSize(labelTextSize)
        }
        var info = ta.getString(R.styleable.InfoButton_info)
        if (!TextUtils.isEmpty(info)) {
            info = info
        }
        val infoTextColor = ta.getColor(R.styleable.InfoButton_infoTextColor, 0)
        if (infoTextColor != 0) {
            setInfoTextColor(infoTextColor)
        }
        val contentPadding =
            ta.getDimensionPixelSize(R.styleable.InfoButton_contentPadding, sp2px(8f))
        setContentPadding(contentPadding)
        val infoTextSize =
            ta.getDimensionPixelSize(R.styleable.InfoButton_infoTextSize, sp2px(14f))
                .toFloat()
        setInfoTextRawSize(infoTextSize)
        val infoMultiLine = ta.getBoolean(R.styleable.InfoButton_infoMultiLine, true)
        setInfoMultiLine(infoMultiLine)
        val hint = ta.getString(R.styleable.InfoButton_hint)
        if (!TextUtils.isEmpty(hint)) {
            setHint(hint)
        }
        val hintTextColor = ta.getColor(R.styleable.InfoButton_hintTextColor, 0)
        if (hintTextColor != 0) {
            setHintTextColor(hintTextColor)
        }
        val gravityAtEnd = ta.getBoolean(R.styleable.InfoButton_gravityAtEnd, true)
        setGravityAtEnd(gravityAtEnd)
        val forwardIcon = ta.getDrawable(R.styleable.InfoButton_forwardIcon)
        setForwardIcon(forwardIcon)
        ta.recycle()
    }
}
