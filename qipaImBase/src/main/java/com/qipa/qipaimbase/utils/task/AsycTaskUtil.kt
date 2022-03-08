package com.qipa.qipaimbase.utils.task

import android.os.AsyncTask
import android.os.Process
import com.qipa.qipaimbase.utils.LogUtils
import java.lang.Exception
import java.util.HashMap
import java.util.concurrent.Callable
import java.util.concurrent.Executor

class AsycTaskUtil private constructor() {
    private var runningTaskMap: HashMap<Callable<Object>?, AsyncTaskCustom>? = null
    @JvmOverloads
    fun createAsycTask(
        runnable: Callable<*>?,
        listener: OnTaskListener?,
        executor: Executor?,
        priority: Int = Process.THREAD_PRIORITY_BACKGROUND
    ) {
        val asycTask = AsyncTaskCustom()
        asycTask.setCallable(runnable as Callable<Object>?)
        asycTask.setListener(listener)
        asycTask.setPriority(priority)
        asycTask.executeOnExecutor(executor)
        if (runningTaskMap == null) {
            runningTaskMap = HashMap()
        }
        runningTaskMap!![runnable as Callable<Object>?] = asycTask
    }

    fun cancelTask(runnable: Callable<*>?) {
        if (runningTaskMap == null) return
        val taskCustom = runningTaskMap!![runnable] ?: return
        taskCustom.cancel(true)
        taskCustom.setCallable(null)
        taskCustom.setListener(null)
        runningTaskMap!!.remove(runnable)
    }

    private class AsyncTaskCustom :
        AsyncTask<Object?, Object?, Object?>() {
        private var callable: Callable<Object>? = null
        private var listener: OnTaskListener? = null
        private var priority = Process.THREAD_PRIORITY_BACKGROUND
        override fun doInBackground(objects: Array<Object?>): Object? {
            Process.setThreadPriority(priority)
            if (callable != null) {
                if (!isCancelled) {
                    try {
                        LogUtils.log(TAG, callable.toString())
                        return callable!!.call()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return null
        }

        override fun onPostExecute(o: Object?) {
            if (listener != null) {
                listener!!.onTaskFinished(o)
            }
        }

        fun setCallable(callable: Callable<Object>?) {
            this.callable = callable
        }

        fun setListener(listener: OnTaskListener?) {
            this.listener = listener
        }

        fun setPriority(priority: Int) {
            var priority = priority
            if (priority > Process.THREAD_PRIORITY_LOWEST) {
                priority = Process.THREAD_PRIORITY_LOWEST
            }
            if (priority < THREAD_PRIORITY_HIGHTEST) {
                priority = THREAD_PRIORITY_HIGHTEST
            }
            this.priority = priority
        }
    }

    interface OnTaskListener {
        fun onTaskFinished(result: Any?)
    }

    companion object {
        private const val TAG = "AsycTaskUtil"
        private const val THREAD_PRIORITY_HIGHTEST = -20
        var instance: AsycTaskUtil? = null
            get() {
                if (field == null) {
                    field = AsycTaskUtil()
                }
                return field
            }
            private set
    }
}

