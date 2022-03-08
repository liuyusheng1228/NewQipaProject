package com.qipa.newboxproject.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.comment.VerticalCommentLayout
import com.qipa.newboxproject.data.model.bean.FirstLevelBean





class CommentDialogSingleAdapter(mItemClickListener: VerticalCommentLayout.CommentItemClickListener?) :
    BaseQuickAdapter<FirstLevelBean, BaseViewHolder>(R.layout.item_comment_new) {
    private var mItemClickListener: VerticalCommentLayout.CommentItemClickListener?
    private var mDataPicUrl : ArrayList<String> = arrayListOf()
    private var rvImages : RecyclerView? = null
    private val commentShowPicAdapter : CommentShowPicAdapter by lazy { CommentShowPicAdapter(context,mDataPicUrl,rvImages) }

    init {
        this.mItemClickListener = mItemClickListener
    }


    override fun convert(helper: BaseViewHolder, content: FirstLevelBean) {
        val iv_header: RoundedImageView = helper.getView(R.id.iv_header)
        val iv_like: ImageView = helper.getView(R.id.iv_like)
        val tv_like_count: TextView = helper.getView(R.id.tv_like_count)
        val tv_user_name: TextView = helper.getView(R.id.tv_user_name)
        val tv_content: TextView = helper.getView(R.id.tv_content)
        val rl_group = helper.getView<RelativeLayout>(R.id.rl_group)
        val ll_like = helper.getView<LinearLayout>(R.id.ll_like)
        ll_like.setOnClickListener {
            mItemClickListener?.onLikeClick(ll_like,content.getSecondLevelBeans()?.get(getItemPosition(content)),getItemPosition(content))
        }
        rl_group.setOnClickListener {
            mItemClickListener?.onItemClick(rl_group,content.getSecondLevelBeans()?.get(getItemPosition(content)),getItemPosition(content))
        }
        rvImages = helper.getView<RecyclerView>(R.id.recycler_view_pics)
        rvImages?.init(GridLayoutManager(context, 3),commentShowPicAdapter)
        mDataPicUrl.clear()
        for (index in 0..3){
            mDataPicUrl.add("https://static.runoob.com/images/demo/demo2.jpg")
        }
        commentShowPicAdapter.notifyDataSetChanged()
        iv_like.setImageResource(if (content.isLike === 0) R.mipmap.icon_topic_post_item_like else R.mipmap.icon_topic_post_item_like_blue)
        tv_like_count.setText(content.likeCount.toString() + "")
        tv_like_count.visibility = if (content.likeCount <= 0) View.GONE else View.VISIBLE

        tv_content.setText(content.content)
        tv_user_name.setText(content.userName)

        Glide.with(context).load(content.headImg).into(iv_header)

        if (content.getSecondLevelBeans() != null) {
            val commentWidget: VerticalCommentLayout = helper.getView(R.id.verticalCommentLayout)
            commentWidget.visibility = View.VISIBLE
            val size: Int = content.getSecondLevelBeans()!!.size
            commentWidget.totalCount = size + 10
            commentWidget.position = helper.getAdapterPosition()
            commentWidget.setOnCommentItemClickListener(mItemClickListener)
            val limit: Int = helper.getAdapterPosition() + 1
            commentWidget.addCommentsWithLimit(content.getSecondLevelBeans(), size, false)
        }
    }
}
