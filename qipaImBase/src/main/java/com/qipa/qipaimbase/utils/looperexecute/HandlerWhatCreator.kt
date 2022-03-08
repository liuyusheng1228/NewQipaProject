package com.qipa.qipaimbase.utils.looperexecute

class HandlerWhatCreator private constructor() {
    private var generateId = 0
    val nextId: Int
        get() {
            ++generateId
            if (generateId == Int.MAX_VALUE) {
                generateId = 1
            }
            return generateId
        }

    companion object {
        private var ourInstance: HandlerWhatCreator? = null
        val instance: HandlerWhatCreator?
            get() {
                if (ourInstance == null) {
                    ourInstance = HandlerWhatCreator()
                }
                return ourInstance
            }
    }
}
