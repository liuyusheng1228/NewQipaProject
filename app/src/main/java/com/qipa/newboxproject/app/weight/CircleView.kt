package com.qipa.newboxproject.app.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class CircleView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var widths = 0
    private var heights = 0
    private val paint: Paint
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        widths = getWidth()
        heights = getHeight()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(widths * 1f / 2, heights * 1f / 2, widths * 1f / 2, paint)
    }

    init {
        paint = Paint()
        paint.color = Color.RED
        paint.isAntiAlias = true
    }
}
