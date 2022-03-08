package com.qipa.newboxproject.ui.adapter

import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.setAdapterAnimation
import com.qipa.newboxproject.app.util.SettingUtil
import com.qipa.newboxproject.data.model.bean.SecondLevelBean

class CommentAllShowCommentAdapter  (datas : MutableList<SecondLevelBean>) :  BaseQuickAdapter<SecondLevelBean, BaseViewHolder>(
    R.layout.item_reply_user, datas) {

    private var monItemClickLikesAndComment : onItemClickLikesAndComment? = null

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }



    override fun convert(holder: BaseViewHolder, item: SecondLevelBean) {

        holder.getView<LinearLayout>(R.id.ll_comment).setOnClickListener {
            if (monItemClickLikesAndComment != null){
                monItemClickLikesAndComment?.onClickItemComment()
            }
        }
        holder.getView<LinearLayout>(R.id.ll_like).setOnClickListener {
            if (monItemClickLikesAndComment != null){
                monItemClickLikesAndComment?.OnClickItemLikes()
            }
        }

    }

    fun setonItemClickLikesAndComment(onItemClickLikesAndComment : onItemClickLikesAndComment){
        monItemClickLikesAndComment = onItemClickLikesAndComment
    }

    interface onItemClickLikesAndComment{
        fun onClickItemComment()
        fun OnClickItemLikes()

    }
}