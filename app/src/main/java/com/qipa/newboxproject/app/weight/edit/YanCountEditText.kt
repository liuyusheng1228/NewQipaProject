package com.qipa.newboxproject.app.weight.edit

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.qipa.newboxproject.R

/**
 * * created by zhangyan
 * 自定义可计数输入框
 */
class YanCountEditText : RelativeLayout {
    private var etContent //文本框
            : EditText? = null
    private var vLineUp //底部横线
            : View? = null
    private var vLineDn //底部横线
            : View? = null
    private var TYPES = SINGULAR //类型，需要根据字段判断
    private var mText = "" //默认文字
    private var mHint = "请输入内容" //提示文字
    private var mMaxNum = 100 //最大字符
    private var mLineColor: Int = Color.BLACK //横线颜色
    private var mTextColor: Int = Color.BLACK //输入文字颜色
    private var mTvNum //字数显示TextView
            : TextView? = null
    private var mTextLeftColor //设置/左边的默认颜色
            = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    /**
     * 构造函数初始化
     *
     * @param context
     * @param attrs
     */
    private fun init(context: Context, attrs: AttributeSet) {
        LayoutInflater.from(context).inflate(R.layout.layout_edittext, this, true)
        etContent = findViewById(R.id.etContent)
        mTvNum = findViewById(R.id.tvNum)
        vLineUp = findViewById(R.id.vLineUp)
        vLineDn = findViewById(R.id.vLineDn)

        // 自定义属性取值
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.EhireCountEditText)
        if (typedArray != null) {
            //默认文字
            mText = typedArray.getString(R.styleable.EhireCountEditText_etText).toString()
//            etContent?.setText(mText)
//            etContent?.setSelection(etContent!!.text.length)
            //提示文字
            mHint = typedArray.getString(R.styleable.EhireCountEditText_etHint).toString()
            etContent?.setHint(mHint)
            //提示文字颜色
            etContent?.setHintTextColor(
                typedArray.getColor(
                    R.styleable.EhireCountEditText_etHintColor,
                    Color.rgb(155, 155, 155)
                )
            )
            //最小高度
            etContent?.setMinHeight(
                px2dip(
                    context,
                    typedArray.getDimensionPixelOffset(
                        R.styleable.EhireCountEditText_etMinHeight,
                        200
                    )
                )
            )
            //最大字符
            mMaxNum = typedArray.getInt(R.styleable.EhireCountEditText_etMaxLength, 500)
            //横线颜色
            mLineColor =
                typedArray.getColor(R.styleable.EhireCountEditText_etLineColor, Color.BLACK)
            vLineDn?.setBackgroundColor(mLineColor)
            vLineUp?.setBackgroundColor(mLineColor)
            //输入文字大小
            etContent?.setTextSize(
                px2sp(
                    context,
                    typedArray.getDimensionPixelOffset(
                        R.styleable.EhireCountEditText_etTextSize,
                        16
                    )
                )
            )
            //输入文字颜色
            mTextColor =
                typedArray.getColor(R.styleable.EhireCountEditText_etTextColor, Color.BLACK)
            etContent?.setTextColor(mTextColor)
            //设置提示统计文字大小
            mTvNum?.setTextSize(
                px2sp(
                    context,
                    typedArray.getDimensionPixelSize(
                        R.styleable.EhireCountEditText_etPromptTextSize,
                        13
                    )
                )
            )
            //设置提示统计文字颜色
            mTvNum?.setTextColor(
                typedArray.getColor(
                    R.styleable.EhireCountEditText_etPromptTextColor,
                    Color.BLACK
                )
            )
            //初始化/左边的文字颜色
            mTextLeftColor = typedArray.getColor(
                R.styleable.EhireCountEditText_etPromptTextLeftColor,
                Color.BLACK
            )
            //设置提示统计显示类型
            val t: Int = typedArray.getInt(R.styleable.EhireCountEditText_etType, 0)
            TYPES = if (t == 0) {
                SINGULAR
            } else {
                PERCENTAGE
            }
            if (TYPES == SINGULAR) {         //类型1
                mTvNum?.setText(mMaxNum.toString())
            } else {                              //类型2
                mTvNum?.setText("0/$mMaxNum")
            }
            //设置提示位置
            val promptPosition: Int =
                typedArray.getInt(R.styleable.EhireCountEditText_etPromptPosition, 0)
            if (promptPosition == 0) { //上方
                vLineDn?.setVisibility(View.VISIBLE)
                vLineUp?.setVisibility(View.GONE)
            } else { //下方
                vLineUp?.setVisibility(View.VISIBLE)
                vLineDn?.setVisibility(View.GONE)
            }
            typedArray.recycle()
        }
        //监听输入
        etContent?.addTextChangedListener(mTextWatcher)
    }

    private fun px2sp(context: Context, pxValue: Int): Float {
        val fontScale: Float = context.getResources().getDisplayMetrics().scaledDensity
        return (pxValue / fontScale + 0.5f).toFloat()
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            var editStart: Int? = etContent?.selectionStart
            var editEnd: Int? = etContent?.selectionEnd
            // 先去掉监听器，否则会出现栈溢出
            etContent?.removeTextChangedListener(this)
            // 去除首位空格
            if (s.toString().startsWith(" ")) {
                s.replace(0, 1, "")
            }
            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，这里计算的规则是一个汉字对应两个字节，一个数字或字母对应一个字节
            // 最后在UI上的变化是，一个汉字计数+1，一个字母或数字+1，如果继续输入一个字母或数字则计数不变
            while (inputCount > mMaxNum * 2) { // 当输入字符个数超过限制的大小时，进行截断操作
                if (editStart != null) {
                    editEnd?.let { s.delete(editStart - 1, it)
                        editStart--
                        editEnd--
                    }
                }


            }
            etContent?.setSelection(s.length)
            // 恢复监听器
            etContent?.addTextChangedListener(this)
            setLeftCount()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * 刷新剩余输入字数
     */
    private fun setLeftCount() {
        if (TYPES == SINGULAR) { //类型1
            mTvNum?.setText((mMaxNum - inputCount).toString())
        } else if (TYPES == PERCENTAGE) { //类型2, 这里需要计算颜色，以"/"区分不同的颜色
            val content = ((inputCount + 1) / 2).toString() + "/" + mMaxNum
            val end = content.indexOf("/")
            val start = 0
            val builder = SpannableStringBuilder(content)
            val span = ForegroundColorSpan(mTextLeftColor)
            builder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            // 如果输入框内容重新变为空，则计数值为0
            if (inputCount == 0L) {
                mTvNum?.setText("0/$mMaxNum")
            } else {     // 如果不为空，则为其填充计数和颜色
                mTvNum?.setText(builder)
            }
        }
    }

    /**
     * 获取用户输入内容字数
     */
    private val inputCount: Long
        private get() = calculateLength(etContent?.getText().toString().trim()).toLong()

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点
     * 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param s
     * @return
     */
    private fun calculateLength(s: String): Int {
        var len = 0
        for (i in 0 until s.length) {
            val charAt = s[i]
            if (charAt.toInt() < 255) {
                len++
            } else {
                len += 2
            }
        }
        return Math.round(len.toFloat())
    }
    /**
     * 获取输入内容，这是一个java方法
     *
     * @return 内容
     */
    /**
     * 设置默认内容
     *
     * @param str --内容
     */
    var text: String?
        get() = etContent?.getText().toString()
        set(str) {
            etContent?.setText(str)
            etContent?.getText()?.let { etContent?.setSelection(it.length) }
        }

    companion object {
        //类型1(单数类型)：TextView显示总字数，然后根据输入递减.例：100，99，98
        //类型2(百分比类型)：TextView显示总字数和当前输入的字数，例：0/100，1/100，2/100
        const val SINGULAR = "Singular" //类型1(单数类型)
        const val PERCENTAGE = "Percentage" //类型2(百分比类型)
        private fun px2dip(context: Context, pxValue: Int): Int {
            val scale: Float = context.getResources().getDisplayMetrics().density
            return (pxValue / scale + 0.5f).toInt()
        }
    }
}