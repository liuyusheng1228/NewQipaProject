package com.qipa.qipaimbase.view

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.R

object ChatToastUtils {
    private var toast: Toast? = null
    fun showChatVoice() {
        ImBaseBridge.instance?.application?.getResources()?.getString(R.string.chat_toast_voice)
            ?.let {
                showChatToast(
                R.drawable.chat_toast_voice,
                    it
            )
            }
    }

    fun showChatTimeWarn() {
        ImBaseBridge.instance?.application?.getResources()?.getString(R.string.chat_toast_time_short)
            ?.let {
                showChatToast(R.drawable.chat_toast_warn,
                    it
            )
            }
    }

    fun showChatExpireWarn() {
        ImBaseBridge.instance?.application?.getResources()?.getString(R.string.chat_toast_expire)
            ?.let {
                showChatToast(R.drawable.chat_toast_warn,
                    it
                )
            }
    }

    fun showChatSendFailedWarn() {
        ImBaseBridge.instance?.application?.getResources()?.getString(R.string.chat_toast_send_failed)
            ?.let {
                showChatToast(R.drawable.chat_toast_warn,
                    it
            )
            }
    }

    private fun showChatToast(resId: Int, content: String) {
        if (toast != null) {
            toast!!.cancel()
        }
        toast = Toast(ImBaseBridge.instance?.application)
        val view =
            View.inflate(ImBaseBridge.instance?.application, R.layout.chat_toast, null)
        (view.findViewById<View>(R.id.ivTip) as ImageView).setImageResource(resId)
        (view.findViewById<View>(R.id.tvTip) as TextView).text = content
        toast?.setView(view)
        toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.show()
    }
}
