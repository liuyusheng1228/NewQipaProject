package com.qipa.qipaimbase.view

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.utils.LogUtils
import com.qipa.qipaimbase.utils.Utils

class ChatPopupWindow(
    showCopy: Boolean,
    showRevert: Boolean,
    context: Context?,
    private val onMenuClick: OnMenuClick?
) :
    PopupWindow(), View.OnClickListener {
    override fun onClick(v: View) {
        if (onMenuClick != null) {
            val i = v.id
            if (i == R.id.tvCopy) {
                onMenuClick.onCopyClick()
            } else if (i == R.id.tvRelay) {
                onMenuClick.onRelayClick()
            } else if (i == R.id.tvRevert) {
                onMenuClick.onRevertClick()
            }
        }
        dismiss()
    }

    fun show(lastPoint: FloatArray, parent: View) {
        if (screenHeight == 0) {
            screenHeight = Utils.getScreenSize(parent.context).get(1)
        }
        //        int[] location = new int[2];
//        parent.getLocationInWindow(location);
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        contentView.measure(w, h)
        val height = contentView.measuredHeight
        val width = contentView.measuredWidth
        val yOff: Int
        val up = if (lastPoint[1] <= screenHeight / 2) true else false
        yOff = if (up) {
            contentView.setBackgroundResource(R.drawable.popup_chat_up)
            0
        } else {
            contentView.setBackgroundResource(R.drawable.popup_chat)
            -height
        }
        //        int yOff = up ? (location[1] + parent.getHeight() / 2) : (location[1] - parent.getHeight() / 2);
//        int xOff = location[0] + parent.getWidth() / 2 - width / 2;
        LogUtils.log(
            TAG, String.format(
                "lastx:%f;lasty:%f",
                lastPoint[0], lastPoint[1]
            )
        )
        showAtLocation(
            parent.rootView, Gravity.NO_GRAVITY, lastPoint[0].toInt() - width / 2, lastPoint[1]
                .toInt() + yOff
        )

        // TODO: 2019-08-21 显示在点击的坐标位置
    }

    interface OnMenuClick {
        fun onCopyClick()
        fun onRelayClick()
        fun onRevertClick()
    }

    companion object {
        private const val TAG = "ChatPopupWindow"
        private var screenHeight = 0
    }

    init {
        val view = View.inflate(context, R.layout.popup_chat_men, null)
        contentView = view
        isOutsideTouchable = true
        setBackgroundDrawable(BitmapDrawable())
        this.width = WindowManager.LayoutParams.WRAP_CONTENT
        // 设置SelectPicPopupWindow弹出窗体的高
        this.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvCopy = view.findViewById<TextView>(R.id.tvCopy)
        val tvRelay = view.findViewById<TextView>(R.id.tvRelay)
        val tvRevert = view.findViewById<TextView>(R.id.tvRevert)
        if (!showRevert) {
            view.findViewById<View>(R.id.divider_revert).visibility = View.GONE
            tvRevert.visibility = View.GONE
        }
        if (!showCopy) {
            view.findViewById<View>(R.id.divider_copy).visibility = View.GONE
            tvCopy.visibility = View.GONE
        }
        tvCopy.setOnClickListener(this)
        tvRelay.setOnClickListener(this)
        tvRevert.setOnClickListener(this)
    }
}
