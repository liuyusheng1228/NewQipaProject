package com.qipa.newboxproject.data.model.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.qipa.newboxproject.data.model.bean.CommentEntity.Companion.TYPE_COMMENT_MORE


class CommentMoreBean : MultiItemEntity {
    var totalCount: Long = 0
    var position: Long = 0
    var positionCount: Long = 0
    override val itemType: Int
        get() = TYPE_COMMENT_MORE
}
