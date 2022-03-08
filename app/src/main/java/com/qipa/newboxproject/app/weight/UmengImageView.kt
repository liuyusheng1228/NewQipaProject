package com.qipa.newboxproject.app.weight

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.qipa.newboxproject.R

class UmengImageView : AppCompatImageView {
    private var paint: Paint? = null
    private var isCenterImgShow = false
    private var bitmap: Bitmap? = null
    fun setCenterImgShow(centerImgShow: Boolean) {
        isCenterImgShow = centerImgShow
        if (isCenterImgShow) {
            bitmap = BitmapFactory.decodeResource(resources, R.mipmap.gm_detail_upmengban_bg)
            invalidate()
        }
    }

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
        paint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isCenterImgShow && bitmap != null) {
            canvas.drawBitmap(
                bitmap!!,
                (measuredWidth / 2 - bitmap!!.width / 2).toFloat(),
                (measuredHeight / 2 - bitmap!!.height / 2).toFloat(),
                paint
            )
        }
    }
}