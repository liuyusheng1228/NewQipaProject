package com.qipa.qipaimbase.utils.looperexecute

import android.os.Handler
import android.os.Looper
import android.os.Message

class MainLooperExecuteUtil private constructor() {
    fun post(runnable: CustomRunnable?): Int {
        if (runnable == null) {
            return CustomRunnable.ID_ILLEGAL
        }
        val msg = Message.obtain()
        msg.obj = runnable
        msg.what = runnable.id
        if (runnable.delayTime !== CustomRunnable.NO_DELAY) {
            handler.sendMessageDelayed(msg, runnable.delayTime)
        } else {
            handler.sendMessage(msg)
        }
        return runnable.id
    }

    fun cancelRunnable(runnable: CustomRunnable?) {
        if (runnable == null) {
            return
        }
        runnable.setCanceled(true)
        handler.removeMessages(runnable.id)
    }

    companion object {
        private var mainLooperExecuteUtil: MainLooperExecuteUtil? = null
        val instance: MainLooperExecuteUtil?
            get() {
                if (mainLooperExecuteUtil == null) {
                    mainLooperExecuteUtil = MainLooperExecuteUtil()
                }
                return mainLooperExecuteUtil
            }
        var handler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.obj is CustomRunnable) {
                    val runnable = msg.obj as CustomRunnable
                    runnable.runnable?.run()
                    if (!runnable.isCanceled && runnable.isRepeated) {
                        instance!!.post(runnable)
                    }
                }
            }
        }
    }
}
