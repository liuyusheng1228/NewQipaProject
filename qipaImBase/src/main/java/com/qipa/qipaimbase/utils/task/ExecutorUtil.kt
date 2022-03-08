package com.qipa.qipaimbase.utils.task

import android.util.Log
import java.util.concurrent.*

object ExecutorUtil {
    private const val TAG = "ExecutorUtil"
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4))
    private val maximumPoolSize = CPU_COUNT * 2 + 1
    private const val keepAliveTime = 30
    var defaultExecutor: Executor? = null
        get() {
            if (field == null) {
                field = ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime.toLong(),
                    TimeUnit.SECONDS,
                    blockingQueue as BlockingQueue<Runnable>?,
                    threadFactory,
                    rejectedExecutionHandler
                )
            }
            return field
        }
        private set
    var chatExecutor: Executor? = null
        get() {
            if (field == null) {
                field = ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime.toLong(),
                    TimeUnit.SECONDS,
                    chatBlockingQueue as BlockingQueue<Runnable>?,
                    threadFactory,
                    rejectedExecutionHandler
                )
            }
            return field
        }
        private set
    private val blockingQueue: Any? = LinkedBlockingDeque<Any?>()
    private val chatBlockingQueue: Any? = LinkedBlockingDeque<Any?>()
    private val threadFactory =
        ThreadFactory { r -> //            thread.setPriority();
            Thread(r)
        }
    private val rejectedExecutionHandler: RejectedExecutionHandler =
        object : ThreadPoolExecutor.DiscardOldestPolicy() {
            override fun rejectedExecution(r: Runnable, e: ThreadPoolExecutor) {
                Log.i(TAG, "rejectedExecution runnable:" + r.hashCode())
                super.rejectedExecution(r, e)
            }
        }
}
