package com.qipa.qipaimbase.utils.task

import android.os.Process
import java.util.concurrent.Callable

class TaskExecutor private constructor() {
    fun createAsycTask(runnable: Callable<*>?) {
        createAsycTask(runnable, null)
    }

    fun createAsycTask(runnable: Callable<*>?, listener: AsycTaskUtil.OnTaskListener?) {
        AsycTaskUtil.instance?.createAsycTask(
            runnable,
            listener,
            ExecutorUtil.defaultExecutor,
            Process.THREAD_PRIORITY_FOREGROUND
        )
    }

    fun createAsycTaskChat(runnable: Callable<*>?) {
        AsycTaskUtil.instance?.createAsycTask(
            runnable,
            null,
            ExecutorUtil.chatExecutor,
            Process.THREAD_PRIORITY_BACKGROUND
        )
    }

    companion object {
        val instance = TaskExecutor()
    }
}
