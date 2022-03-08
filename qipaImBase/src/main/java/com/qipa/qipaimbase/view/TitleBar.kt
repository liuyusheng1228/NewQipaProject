package com.qipa.qipaimbase.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.qipa.qipaimbase.R

class TitleBar : FrameLayout {
    var ivLeft: ImageView? = null

    var ivRight: ImageView? = null

    var tvTitle: TextView? = null

    var tvLeft: TextView? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.view_titlevar, this)
        ivLeft = findViewById(R.id.ivLeft)
        ivRight = findViewById(R.id.ivRight)
        tvTitle = findViewById(R.id.tvTitle)
        tvLeft = findViewById(R.id.tvLeft)
    }

    fun setTitle(title: String?): TitleBar {
        tvTitle?.text = title
        return this
    }

    fun setTitle(title: String?, onClickListener: OnClickListener?): TitleBar {
        tvTitle?.text = title
        tvTitle?.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftImageEvent(@DrawableRes resId: Int, onClickListener: OnClickListener?): TitleBar {
        ivLeft?.visibility = VISIBLE
        ivLeft?.setImageResource(resId)
        ivLeft?.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftTextEvent(leftContent: String?, onClickListener: OnClickListener?): TitleBar {
        tvLeft?.visibility = VISIBLE
        tvLeft?.text = leftContent
        tvLeft?.setOnClickListener(onClickListener)
        return this
    }

    fun setRightImageEvent(@DrawableRes resId: Int, onClickListener: OnClickListener?): TitleBar {
        ivRight?.visibility = VISIBLE
        ivRight?.setImageResource(resId)
        ivRight?.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftVisible(visible: Int): TitleBar {
        ivLeft?.visibility = visible
        return this
    }

    fun setRightVisible(visible: Int): TitleBar {
        ivRight?.visibility = visible
        return this
    }
}
