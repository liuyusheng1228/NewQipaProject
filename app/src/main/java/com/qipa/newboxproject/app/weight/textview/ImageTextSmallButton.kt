package com.qipa.newboxproject.app.weight.textview

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.qipa.newboxproject.R


class ImageTextSmallButton(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    private var mImgView: ImageView? = null
    private var mTextView: TextView? = null
    private val mContext: Context

    /*设置图片接口*/
    fun setImageResource(resId: Int) {
        mImgView!!.setImageResource(resId)
    }

    /*设置文字接口*/
    fun setText(str: String?) {
        mTextView!!.text = str
    }

    /*设置文字大小*/
    fun setTextSize(size: Float) {
        mTextView!!.textSize = size
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.image_text_small_button, this, true)
        mContext = context
        val ta = context.obtainStyledAttributes(attrs, R.styleable.InfoButton)
        mImgView = findViewById<View>(R.id.img) as ImageView
        mTextView = findViewById<View>(R.id.text) as TextView
        val icon = ta.getDrawable(R.styleable.InfoButton_icon)
        mImgView!!.setImageDrawable(icon)
        val labelText = ta.getString(R.styleable.InfoButton_label)
        if (!TextUtils.isEmpty(labelText)) {
            mTextView!!.text = labelText
        }
    }
}