package com.qipa.qipaimbase.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import com.qipa.qipaimbase.utils.LogUtils
import com.qipa.qipaimbase.utils.ToastUtils
import com.qipa.qipaimbase.utils.looperexecute.CustomRunnable
import com.qipa.qipaimbase.utils.looperexecute.MainLooperExecuteUtil

class VoiceTextView : AppCompatTextView {
    private var outLocation: IntArray? = null
    private var onEventUpListener: OnEventUpListener? = null
    private val timeOut = 0
    private var customRunnable: CustomRunnable? = null
    private var lastDownTime: Long = 0
    private var handlers: Handler? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        handlers = VoiceHandler()
        customRunnable = CustomRunnable()
        customRunnable?.setRunnable(Runnable {
            setText("按住说话")
            if (onEventUpListener != null) {
                onEventUpListener?.onTimeout()
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rawX: Float
        val rawY: Float
        LogUtils.log(TAG, "onTouchEvent start")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (System.currentTimeMillis() - lastDownTime < FRQUENT_DURATION) {
                    ToastUtils.showText(getContext(), "操作太频繁")
                    return false
                }
                lastDownTime = System.currentTimeMillis()
                if (onEventUpListener != null) {
                    if (!onEventUpListener?.canHandle()!!) {
                        return super.onTouchEvent(event)
                    }
                }
                setText("松开发送")
                if (customRunnable?.delayTime!! > 0) {
                    MainLooperExecuteUtil.instance?.post(customRunnable)
                }
                val message = Message.obtain()
                message.obj = onEventUpListener
                message.what = WAHAT_EVENT_DOWN
                handlers?.sendMessage(message)
            }
            MotionEvent.ACTION_MOVE -> {
                rawX = event.rawX
                rawY = event.rawY
                outLocation = IntArray(2)
                getLocationInWindow(outLocation)
                if (rawX > outLocation!![0] + getWidth() || rawX < outLocation!![0] || rawY < outLocation!![1] || rawY > outLocation!![1] + getHeight()) {
                    setText("松开取消")
                } else {
                    setText("松开发送")
                }
                LogUtils.log(TAG, "ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                MainLooperExecuteUtil.instance?.cancelRunnable(customRunnable)
                rawX = event.rawX
                rawY = event.rawY
                if (outLocation != null && (rawX > outLocation!![0] + getWidth() || rawX < outLocation!![0] || rawY < outLocation!![1] || rawY > outLocation!![1] + getHeight())) {
                    if (onEventUpListener != null) {
                        onEventUpListener?.onEventCancel()
                    }
                } else {
                    if (onEventUpListener != null) {
                        onEventUpListener?.onEventUp()
                    }
                }
                setText("按住说话")
                LogUtils.log(TAG, "ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                MainLooperExecuteUtil.instance?.cancelRunnable(customRunnable)
                if (onEventUpListener != null) {
                    onEventUpListener!!.onEventCancel()
                }
                setText("按住说话")
                LogUtils.log(TAG, "ACTION_CANCEL")
            }
        }
        LogUtils.log(TAG, "onTouchEvent end")
        return true
    }

    fun setTimeOut(timeOut: Int) {
        customRunnable?.setDelayTime(timeOut)
    }

    interface OnEventUpListener {
        fun canHandle(): Boolean
        fun onEventDown()
        fun onEventCancel()
        fun onEventUp()
        fun onTimeout()
    }
    class VoiceHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WAHAT_EVENT_DOWN -> if (msg.obj != null) {
                    (msg.obj as OnEventUpListener).onEventDown()
                }
            }
            super.handleMessage(msg)
        }
    }


    fun setOnEventUpListener(onEventUpListener: OnEventUpListener?) {
        this.onEventUpListener = onEventUpListener
    }

    companion object {
        private const val WAHAT_EVENT_DOWN = 925
        private const val TAG = "VoiceTextView"
        private const val FRQUENT_DURATION = 1000
    }
}
