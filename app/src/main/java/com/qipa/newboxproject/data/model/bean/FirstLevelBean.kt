package com.qipa.newboxproject.data.model.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.qipa.newboxproject.data.model.bean.CommentEntity.Companion.TYPE_COMMENT_PARENT
import java.lang.StringBuilder


class FirstLevelBean : MultiItemEntity {
    private var secondLevelBeans: List<SecondLevelBean>? = null

    //一级评论id
    var id: String? = null

    //一级评论头像
    var headImg: String? = null

    //一级评论的用户名
    var userName: String? = null

    //一级评论的用户id
    var userId: String? = null

    //评论内容
    var content: String? = null

    //创建时间
    var createTime: Long = 0

    //点赞数量
    var likeCount: Long = 0

    //是否点赞了  0没有 1点赞
    var isLike = 0

    //当前评论的总条数（包括这条一级评论）ps:处于未使用状态
    var totalCount: Long = 0

    //当前一级评论的位置（下标）
    var position = 0

    //当前一级评论所在的位置(下标)
    var positionCount = 0

    fun getSecondLevelBeans(): List<SecondLevelBean>? {
        return secondLevelBeans
    }

    fun setSecondLevelBeans(secondLevelBeans: List<SecondLevelBean>?) {
        this.secondLevelBeans = secondLevelBeans
    }

    override fun toString(): String {
        val sb = StringBuilder("{")
        sb.append("\"secondLevelBeans\":")
            .append(secondLevelBeans)
        sb.append(",\"id\":\"")
            .append(id).append('\"')
        sb.append(",\"headImg\":\"")
            .append(headImg).append('\"')
        sb.append(",\"userName\":\"")
            .append(userName).append('\"')
        sb.append(",\"userId\":\"")
            .append(userId).append('\"')
        sb.append(",\"content\":\"")
            .append(content).append('\"')
        sb.append(",\"createTime\":")
            .append(createTime)
        sb.append(",\"likeCount\":")
            .append(likeCount)
        sb.append(",\"isLike\":")
            .append(isLike)
        sb.append(",\"totalCount\":")
            .append(totalCount)
        sb.append(",\"position\":")
            .append(position)
        sb.append(",\"positionCount\":")
            .append(positionCount)
        sb.append('}')
        return sb.toString()
    }

    override val itemType: Int
        get() = TYPE_COMMENT_PARENT
}
