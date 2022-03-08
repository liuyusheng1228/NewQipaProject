package com.qipa.newboxproject.app.weight

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.qipa.newboxproject.R

class SettingItemView : LinearLayout {
    private var ivTagImage: ImageView? = null
    private var sbSwitch: SwitchButton? = null

    /**
     * 设置 value 颜色
     */
    var valueView: TextView? = null
        private set
    private var tvContent: TextView? = null
    private var ivImage: ImageView? = null
    private var ivSelectImage: ImageView? = null
    private var isShowSelected = false
    var rightImageView: ImageView? = null
        private set
    private var checkedListener: CompoundButton.OnCheckedChangeListener? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val view = inflate(context, R.layout.item_setting, this)
        ivImage = findViewById(R.id.iv_image)
        tvContent = findViewById(R.id.tv_content)
        valueView = findViewById(R.id.tv_value)
        val vDivider = findViewById<View>(R.id.v_divider)
        ivTagImage = findViewById(R.id.iv_tag_image)
        sbSwitch = findViewById(R.id.sb_switch)
        ivSelectImage = findViewById(R.id.iv_select_image)
        rightImageView = findViewById(R.id.iv_right_image)
        ivImage?.setVisibility(GONE)
        valueView?.setVisibility(GONE)
        ivTagImage?.setVisibility(GONE)
        sbSwitch?.setVisibility(GONE)
        vDivider.visibility = GONE
        ivSelectImage?.setVisibility(GONE)
        rightImageView?.setVisibility(GONE)
        view.setBackgroundResource(R.drawable.seal_mine_setting_item_selector)
        val ta = if (attrs == null) null else context.obtainStyledAttributes(
            attrs,
            R.styleable.SettingItemView
        )
        if (ta != null) {
            val N = ta.indexCount
            for (i in 0 until N) {
                val attr = ta.getIndex(i)
                when (attr) {
                    R.styleable.SettingItemView_item_image -> {
                        val drawable = ta.getDrawable(attr)
                        ivImage?.setVisibility(VISIBLE)
                        ivImage?.setImageDrawable(drawable)
                    }
                    R.styleable.SettingItemView_item_image_height -> {
                        val imageHeight = ta.getDimension(attr, 0f)
                        if (imageHeight > 0) {
                            val layoutParamsHeight = ivImage?.getLayoutParams()
                            layoutParamsHeight?.height = Math.round(imageHeight)
                            ivImage?.setLayoutParams(layoutParamsHeight)
                        }
                    }
                    R.styleable.SettingItemView_item_image_width -> {
                        val imageWidth = ta.getDimension(attr, 0f)
                        if (imageWidth > 0) {
                            val layoutParamsWidth = ivImage?.getLayoutParams()
                            layoutParamsWidth?.width = Math.round(imageWidth)
                            ivImage?.setLayoutParams(layoutParamsWidth)
                        }
                    }
                    R.styleable.SettingItemView_item_content -> {
                        val content = ta.getString(attr)
                        tvContent?.setText(content ?: "")
                    }
                    R.styleable.SettingItemView_item_content_text_size -> {
                        val contentSize = ta.getDimension(attr, 0f)
                        if (contentSize > 0) {
                            tvContent?.setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                Math.round(contentSize).toFloat()
                            )
                        }
                    }
                    R.styleable.SettingItemView_item_content_text_color -> {
                        val color = ta.getColor(attr, -1)
                        if (color > 0) {
                            tvContent?.setTextColor(color)
                        }
                    }
                    R.styleable.SettingItemView_item_value -> {
                        val value = ta.getString(attr)
                        valueView?.setVisibility(VISIBLE)
                        valueView?.setText(value)
                    }
                    R.styleable.SettingItemView_item_value_text_size -> {
                        val valueSize = ta.getDimension(attr, 0f)
                        if (valueSize > 0) {
                            valueView?.setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                Math.round(valueSize).toFloat()
                            )
                        }
                    }
                    R.styleable.SettingItemView_item_value_text_color -> {
                        val valueColor = ta.getColor(attr, -1)
                        if (valueColor > 0) {
                            valueView?.setTextColor(valueColor)
                        }
                    }
                    R.styleable.SettingItemView_item_tag_image -> {
                        val tagImage = ta.getDrawable(R.styleable.SettingItemView_item_tag_image)
                        if (tagImage != null) {
                            ivTagImage?.setImageDrawable(tagImage)
                        }
                    }
                    R.styleable.SettingItemView_item_tag_image_height -> {
                        val tagImageHeight =
                            ta.getDimension(R.styleable.SettingItemView_item_tag_image_height, 0f)
                        if (tagImageHeight > 0) {
                            val layoutParamsTagHeight = ivTagImage?.getLayoutParams()
                            layoutParamsTagHeight?.height = Math.round(tagImageHeight)
                            ivTagImage?.setLayoutParams(layoutParamsTagHeight)
                        }
                    }
                    R.styleable.SettingItemView_item_tag_image_width -> {
                        val tagImageWidth =
                            ta.getDimension(R.styleable.SettingItemView_item_tag_image_width, 0f)
                        if (tagImageWidth > 0) {
                            val layoutParamsTagWidth = ivTagImage?.getLayoutParams()
                            layoutParamsTagWidth?.width = Math.round(tagImageWidth)
                            ivTagImage?.setLayoutParams(layoutParamsTagWidth)
                        }
                    }
                    R.styleable.SettingItemView_item_divider -> {
                        val divider = ta.getBoolean(attr, false)
                        vDivider.visibility = if (divider) VISIBLE else GONE
                    }
                    R.styleable.SettingItemView_item_switch -> {
                        val switchCheck = ta.getBoolean(attr, false)
                        if (switchCheck) {
                            sbSwitch?.setVisibility(VISIBLE)
                        } else {
                            sbSwitch?.setVisibility(GONE)
                        }
                    }
                    R.styleable.SettingItemView_item_null_background -> {
                        val bgNull = ta.getBoolean(attr, false)
                        if (bgNull) {
                            background = null
                        }
                    }
                    R.styleable.SettingItemView_item_background -> {
                        val bg = ta.getDrawable(attr)
                        background = bg
                    }
                    R.styleable.SettingItemView_item_show_selected -> isShowSelected =
                        ta.getBoolean(attr, false)
                    R.styleable.SettingItemView_item_selected_image -> {
                        val selectedImage = ta.getDrawable(attr)
                        ivSelectImage?.setImageDrawable(selectedImage)
                    }
                    R.styleable.SettingItemView_item_right_image -> {
                        val rightImage = ta.getDrawable(attr)
                        rightImageView?.setImageDrawable(rightImage)
                        rightImageView?.setVisibility(VISIBLE)
                    }
                    R.styleable.SettingItemView_item_right_image_height -> {
                        val rightImageHeight = ta.getDimension(attr, 0f)
                        if (rightImageHeight > 0) {
                            val layoutParamsHeight = rightImageView?.getLayoutParams()
                            layoutParamsHeight?.height = Math.round(rightImageHeight)
                            rightImageView?.setLayoutParams(layoutParamsHeight)
                        }
                    }
                    R.styleable.SettingItemView_item_right_image_width -> {
                        val rightImageWidth = ta.getDimension(attr, 0f)
                        if (rightImageWidth > 0) {
                            val layoutParamsWidth = rightImageView?.getLayoutParams()
                            layoutParamsWidth?.width = Math.round(rightImageWidth)
                            rightImageView?.setLayoutParams(layoutParamsWidth)
                        }
                    }
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val defHeight = resources.getDimension(R.dimen.dp_15)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val specMode = MeasureSpec.getMode(heightMeasureSpec)
        heightMeasureSpec = if (specMode != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(defHeight.toInt(), MeasureSpec.EXACTLY)
        } else {
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 设置右侧图片的隐藏
     *
     * @param visibility
     */
    fun setRightImageVisibility(visibility: Int) {
        rightImageView!!.visibility = visibility
    }

    /**
     * 设置标示图片显示隐藏
     *
     * @param visibility
     */
    fun setTagImageVisibility(visibility: Int) {
        ivTagImage!!.visibility = visibility
    }

    /**
     * 设置标示图片
     *
     * @param resId
     */
    fun setTagImage(resId: Int) {
        ivTagImage!!.setImageResource(resId)
    }

    /**
     * 设置switch 按钮显示隐藏
     *
     * @param visibility
     */
    fun setSwitchButtonVisibility(visibility: Int) {
        sbSwitch?.setVisibility(visibility)
    }

    /**
     * 设置 switch 按钮选择监听
     *
     * @param listener
     */
    fun setSwitchCheckListener(listener: CompoundButton.OnCheckedChangeListener?) {
        checkedListener = listener
        sbSwitch?.setOnCheckedChangeListener(checkedListener)
    }

    fun setSwitchTouchListener(listener: OnTouchListener?) {
        sbSwitch?.setOnTouchListener(listener)
    }

    /**
     * 设置 value 值 显示隐藏
     *
     * @param visibility
     */
    fun setValueVisibility(visibility: Int) {
        valueView!!.visibility = visibility
    }

    /**
     * 设置 value 值
     *
     * @param resId
     */
    fun setValue(resId: Int) {
        valueView!!.setText(resId)
        valueView!!.visibility = VISIBLE
    }

    /**
     * 设置 value 值
     *
     * @param value
     */
    fun setValue(value: String?) {
        valueView!!.text = value
        valueView!!.visibility = VISIBLE
    }

    /**
     * 获取value
     *
     * @return
     */
    val value: String
        get() = if (valueView!!.text == null) {
            ""
        } else valueView!!.text.toString()

    /**
     * 设置内容
     *
     * @param resId
     */
    fun setContent(resId: Int) {
        tvContent!!.setText(resId)
    }

    /**
     * 设置内容
     *
     * @param content
     */
    fun setContent(content: String?) {
        tvContent?.text = content
    }

    /**
     * 设置左侧图片显示隐藏
     *
     * @param visibility
     */
    fun setImageVisibility(visibility: Int) {
        ivImage?.visibility = visibility
    }

    /**
     * 设置左侧图片
     *
     * @param resId
     */
    fun setImage(resId: Int) {
        ivImage?.setImageResource(resId)
    }

    /**
     * 设置 switch 按钮选择状态,不触发选中事件
     *
     * @param isChecked
     */
    fun setCheckedWithOutEvent(isChecked: Boolean) {
        sbSwitch?.setOnCheckedChangeListener(null)
        sbSwitch?.setChecked(isChecked)
        sbSwitch?.setOnCheckedChangeListener(checkedListener)
    }

    /**
     * 立即设置 switch 按钮选择状态，没有动画
     *
     * @param isChecked
     */
    fun setCheckedImmediately(isChecked: Boolean) {
        sbSwitch?.setCheckedImmediately(isChecked)
    }

    /**
     * 立即设置 switch 按钮选择状态，没有动画,不触发选中事件
     *
     * @param isChecked
     */
    fun setCheckedImmediatelyWithOutEvent(isChecked: Boolean) {
        sbSwitch?.setOnCheckedChangeListener(null)
        sbSwitch?.setCheckedImmediately(isChecked)
        sbSwitch?.setOnCheckedChangeListener(checkedListener)
    }
    /**
     * 获取当前 switch 状态
     */
    /**
     * 设置 switch 按钮选择状态
     *
     * @param isChecked
     */
    var isChecked: Boolean
        get() = sbSwitch?.isChecked()!!
        set(isChecked) {
            sbSwitch?.setChecked(isChecked)
        }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected && isShowSelected) {
            ivSelectImage!!.visibility = VISIBLE
        } else {
            ivSelectImage!!.visibility = GONE
        }
    }
}
