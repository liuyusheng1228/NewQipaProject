package com.qipa.newboxproject.data.model.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.qipa.newboxproject.data.model.bean.CommentEntity.Companion.TYPE_COMMENT_CHILD
import java.lang.StringBuilder


class SecondLevelBean : MultiItemEntity {
    //二级评论id
    var id: String? = null

    //二级评论头像
    var headImg: String? = null

    //二级评论的用户名
    var userName: String? = null

    //二级评论的用户id
    var userId: String? = null

    //回复评论人的用户名
    var replyUserName: String? = null

    //回复评论人的用户id
    var replyUserId: String? = null

    //评论内容（回复内容）
    var content: String? = null

    //创建时间
    var createTime: Long = 0

    //点赞数量
    var likeCount: Long = 0

    //是否点赞了  0没有 1点赞
    var isLike = 0

    //本条评论是否为回复
    var isReply = 0

    //当前评论的总条数（包括这条一级评论）ps:处于未使用状态
    var totalCount: Long = 0

    //当前一级评论的位置（下标）
    var position = 0

    //当前二级评论所在的位置(下标)
    var positionCount = 0

    //当前二级评论所在一级评论条数的位置（下标）
    var childPosition = 0

    override fun toString(): String {
        val sb = StringBuilder("{")
        sb.append("\"id\":\"")
            .append(id).append('\"')
        sb.append(",\"headImg\":\"")
            .append(headImg).append('\"')
        sb.append(",\"userName\":\"")
            .append(userName).append('\"')
        sb.append(",\"userId\":\"")
            .append(userId).append('\"')
        sb.append(",\"replyUserName\":\"")
            .append(replyUserName).append('\"')
        sb.append(",\"replyUserId\":\"")
            .append(replyUserId).append('\"')
        sb.append(",\"content\":\"")
            .append(content).append('\"')
        sb.append(",\"createTime\":")
            .append(createTime)
        sb.append(",\"likeCount\":")
            .append(likeCount)
        sb.append(",\"isLike\":")
            .append(isLike)
        sb.append(",\"isReply\":")
            .append(isReply)
        sb.append(",\"totalCount\":")
            .append(totalCount)
        sb.append(",\"position\":")
            .append(position)
        sb.append(",\"positionCount\":")
            .append(positionCount)
        sb.append(",\"childPosition\":")
            .append(childPosition)
        sb.append('}')
        return sb.toString()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj !is SecondLevelBean) return false
        return obj.id == id
    }

    override val itemType: Int
        get() = TYPE_COMMENT_CHILD
}
