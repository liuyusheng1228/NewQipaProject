package com.qipa.newboxproject.app.weight.comment

import android.text.Layout
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.view.MotionEvent
import android.widget.TextView

class TextMovementMethods : LinkMovementMethod() {
    private var mTextClickSpan: TextClickSpans? = null
    var isSpanClick = false

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.getAction() === MotionEvent.ACTION_DOWN) {
            mTextClickSpan = getTextSpan(widget, buffer, event)
            if (mTextClickSpan != null) {
                isSpanClick = true
                mTextClickSpan?.setPressed(true)
                Selection.setSelection(
                    buffer,
                    buffer.getSpanStart(mTextClickSpan),
                    buffer.getSpanEnd(mTextClickSpan)
                )
            } else {
                isSpanClick = false
            }
        } else if (event.getAction() === MotionEvent.ACTION_MOVE) {
            //不需要处理
//            TextClickSpans touchTextClickSpan = getTextSpan(widget, buffer, event);
//            if (mTextClickSpan != null && touchTextClickSpan != mTextClickSpan) {
//                mTextClickSpan.setPressed(false);
//                mTextClickSpan = null;
//                Selection.removeSelection(buffer);
//            }
        } else if (event.getAction() === MotionEvent.ACTION_UP) {
            if (mTextClickSpan != null) {
                mTextClickSpan?.onClick(widget)
                mTextClickSpan?.setPressed(false)
                mTextClickSpan = null
            }
            Selection.removeSelection(buffer)
        } else {
            if (mTextClickSpan != null) {
                mTextClickSpan?.onClick(widget)
                mTextClickSpan?.setPressed(false)
                mTextClickSpan = null
            }
            Selection.removeSelection(buffer)
        }
        return Touch.onTouchEvent(widget, buffer, event)
    }

    /**
     * 得到匹配的span
     *
     * @param widget
     * @param spannable
     * @param event
     * @return
     */
    private fun getTextSpan(
        widget: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): TextClickSpans? {
        var x = event.getX()
        var y = event.getY()
        x -= widget.getTotalPaddingLeft()
        y -= widget.getTotalPaddingTop()
        x += widget.getScrollX()
        y += widget.getScrollY()
        val layout: Layout = widget.getLayout()
        val line: Int = layout.getLineForVertical(y.toInt())
        val off: Int = layout.getOffsetForHorizontal(line, x.toFloat())
        val link: Array<TextClickSpans> = spannable.getSpans(off, off, TextClickSpans::class.java)
        var touchedSpan: TextClickSpans? = null
        if (link.size > 0) {
            touchedSpan = link[0]
        }
        return touchedSpan
    }
}
