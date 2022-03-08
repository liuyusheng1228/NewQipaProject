package com.qipa.qipaimbase.utils

object CollectionUtils {
    fun <E> isEmpty(collection: Collection<E>?): Boolean {
        return collection == null || collection.size == 0
    }

    fun isEmpty(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }
}
