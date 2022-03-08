package com.qipa.newboxproject.app.weight.comment

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

abstract class TextClickSpans : ClickableSpan(), View.OnClickListener {
    private var mPressed = false
    fun setPressed(isPressed: Boolean) {
        mPressed = isPressed
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        if (mPressed) {
            ds.bgColor = Color.parseColor("#B5B5B5")
        } else {
            ds.bgColor = Color.TRANSPARENT
        }
        ds.setColor(Color.parseColor("#697A9F"))
        ds.setUnderlineText(false)
    }
}
