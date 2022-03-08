package com.qipa.newboxproject.data.model.bean

import java.lang.StringBuilder

class CommentEntity {
    var firstLevelBeans: List<FirstLevelBean>? = null
    var totalCount: Long = 0

    override fun toString(): String {
        val sb = StringBuilder("{")
        sb.append("\"firstLevelBeans\":")
            .append(firstLevelBeans)
        sb.append(",\"totalCount\":")
            .append(totalCount)
        sb.append('}')
        return sb.toString()
    }

    companion object {
        const val TYPE_COMMENT_PARENT = 1
        const val TYPE_COMMENT_CHILD = 2
        const val TYPE_COMMENT_MORE = 3
        const val TYPE_COMMENT_EMPTY = 4
        const val TYPE_COMMENT_OTHER = 5
    }
}
