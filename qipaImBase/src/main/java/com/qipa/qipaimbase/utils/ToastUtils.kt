package com.qipa.qipaimbase.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.qipa.qipaimbase.ImBaseBridge

object ToastUtils {
    private var toast: Toast? = null
    private var handler: Handler? = null
    private var reuseRunnable: ReuseRunnable? = null
    fun showText(content: String?) {
        showText(null, content)
    }

    fun showText(context: Context?, content: String?) {
        //TODO 是否可以公用一个toast
        if (Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
            showTextInner(ImBaseBridge.instance?.application, content)
        } else {
            if (handler == null) {
                handler = Handler(Looper.getMainLooper())
            }
            if (reuseRunnable == null) {
                reuseRunnable = ReuseRunnable()
            }
            reuseRunnable!!.setContext(ImBaseBridge.instance?.application)
            reuseRunnable!!.setContent(content)
            handler!!.post(reuseRunnable!!)
        }
    }

    private fun showTextInner(context: Context?, content: String?) {
        if (toast != null) {
            toast!!.cancel()
        }
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
        toast?.setText(content)
        toast?.show()
    }

    fun cancel() {
        if (toast != null) {
            toast!!.cancel()
        }
    }

    private class ReuseRunnable : Runnable {
        fun setContent(content: String?) {
            this.content = content
        }

        fun setContext(context: Context?) {
            this.context = context
        }

        private var context: Context? = null
        private var content: String? = null
        override fun run() {
            showTextInner(context, content)
        }
    }
}
