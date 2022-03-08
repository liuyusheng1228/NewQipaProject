package com.qipa.qipaimbase.utils.looperexecute

class CustomRunnable {
    var isRepeated = false
        private set
    var runnable: Runnable? = null
        private set
    var id = 0
        private set
    var delayTime = NO_DELAY
        private set
    var isCanceled = false
        private set

    private fun generateId() {
        id = HandlerWhatCreator.instance?.nextId!!
    }

    fun setRepeated(repeated: Boolean): CustomRunnable {
        isRepeated = repeated
        return this
    }

    fun setRunnable(runnable: Runnable?): CustomRunnable {
        this.runnable = runnable
        return this
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is CustomRunnable) {
            if (obj.id == id) {
                true
            } else {
                false
            }
        } else super.equals(obj)
    }

    fun setDelayTime(delayTime: Int): CustomRunnable {
        this.delayTime = delayTime.toLong()
        return this
    }

    fun setCanceled(canceled: Boolean): CustomRunnable {
        isCanceled = canceled
        return this
    }

    companion object {
        const val NO_DELAY = -1L
        const val ID_ILLEGAL = -1
    }

    init {
        generateId()
    }
}