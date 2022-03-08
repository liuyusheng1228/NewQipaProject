package com.qipa.newboxproject.app.weight.comment

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.bumptech.glide.Glide

import com.makeramen.roundedimageview.RoundedImageView

import com.blankj.utilcode.util.SizeUtils
import com.qipa.newboxproject.R
import com.qipa.newboxproject.data.model.bean.SecondLevelBean

import com.qipa.newboxproject.app.App


/**
 * 自动添加多个view的LinearLayout
 */
class VerticalCommentLayout : LinearLayout, ViewGroup.OnHierarchyChangeListener {
    private var mCommentBeans: List<SecondLevelBean>? = null
    private var mLayoutParams: LayoutParams? = null
    private var COMMENT_TEXT_POOL: SimpleWeakObjectPool<View>? = null
    private var mCommentVerticalSpace = 0
    private var onCommentItemClickListener: CommentItemClickListener? = null
    var totalCount = 0
    var position = 0

    fun setOnCommentItemClickListener(listener: CommentItemClickListener?) {
        onCommentItemClickListener = listener
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        orientation = VERTICAL
        mCommentVerticalSpace = SizeUtils.dp2px(2f)
        COMMENT_TEXT_POOL = SimpleWeakObjectPool()
        setOnHierarchyChangeListener(this)
    }

    fun addCommentsWithLimit(commentBeans: List<SecondLevelBean>?, limit: Int, more: Boolean) {
        if (commentBeans == null) return
        mCommentBeans = commentBeans
        val oldCount = childCount
        if (!more && oldCount > 0) {
            removeViewsInLayout(0, oldCount)
        }
        val showCount = if (limit > commentBeans.size) commentBeans.size else limit
        for (i in 0 until showCount) {
            val hasChild = i < oldCount
            var childView = if (hasChild) getChildAt(i) else null
            val commentBean = commentBeans[i]
            if (childView == null) {
                childView = COMMENT_TEXT_POOL!!.get()
                childView?.let { addCommentItemView(it, commentBean, i) }
                    ?: addViewInLayout(
                        makeCommentItemView(commentBean, i),
                        i, generateMarginLayoutParams(i), true
                    )
            } else {
                updateCommentData(childView, commentBean, i)
            }
        }
        if (commentBeans.size > 0) {
            addViewInLayout(
                makeMoreView(totalCount > showCount),
                showCount, generateMarginLayoutParams(showCount), true
            )
        }
        requestLayout()
    }

    fun addComments(commentBeans: List<SecondLevelBean>?) {
        if (commentBeans == null) return
        mCommentBeans = commentBeans
        val oldCount = childCount
        if (oldCount <= 0) {
            return
        }
        if (oldCount > 0) {
            removeViewsInLayout(oldCount - 1, 1)
        }
        addCommentsWithLimit(commentBeans, commentBeans.size, true)
    }

    /**
     * 更新指定的position的comment
     */
    fun updateTargetComment(position: Int, commentBeans: List<SecondLevelBean?>) {
        val oldCount = childCount
        for (i in 0 until oldCount) {
            if (i == position) {
                val childView = getChildAt(i)
                val bean = commentBeans[i]
                if (bean == null || childView == null) continue
                updateCommentData(childView, bean, i)
                break
            }
        }
        requestLayout()
    }

    /**
     * 創建Comment item view
     */
    private fun makeCommentItemView(bean: SecondLevelBean, index: Int): View {
        return makeContentView(bean, index)
    }

    /**
     * 添加需要的Comment View
     */
    private fun addCommentItemView(view: View, bean: SecondLevelBean, index: Int) {
        val commentView = makeCommentItemView(bean, index)
        addViewInLayout(commentView, index, generateMarginLayoutParams(index), true)
    }

    /**
     * 更新comment list content
     */
    private fun updateCommentData(view: View, bean: SecondLevelBean, index: Int) {
        bindViewData(view, bean)
    }

    private fun makeContentView(content: SecondLevelBean, index: Int): View {
        val view = inflate(context, R.layout.item_comment_single_child_new, null)
        bindViewData(view, content)
        return view
    }

    private fun bindViewData(view: View, content: SecondLevelBean) {
        val rl_group = view.findViewById<RelativeLayout>(R.id.rl_group)
        val ll_like = view.findViewById<LinearLayout>(R.id.ll_like)
        val iv_header: RoundedImageView = view.findViewById(R.id.iv_header)
        val iv_like = view.findViewById<ImageView>(R.id.iv_like)
        val tv_like_count = view.findViewById<TextView>(R.id.tv_like_count)
        val tv_user_name = view.findViewById<TextView>(R.id.tv_user_name)
        val tv_content = view.findViewById<TextView>(R.id.tv_content)
        Glide.with(iv_header.context).load(content.headImg).into(iv_header)
        iv_like.setImageResource(if (content.isLike === 0) R.mipmap.icon_topic_post_item_like else R.mipmap.icon_topic_post_item_like_blue)
        tv_like_count.text = content.likeCount.toString() + ""
        tv_like_count.visibility = if (content.likeCount <= 0) GONE else VISIBLE
        val movementMethods = TextMovementMethods()
        if (content.isReply === 0) {
            tv_content.text = content.content
            tv_content.movementMethod = null
        } else {
            val stringBuilder =
                makeReplyCommentSpan(content.replyUserName, content.replyUserId, content.content)
            tv_content.text = stringBuilder
            tv_content.movementMethod = movementMethods
        }
        tv_content.setOnClickListener(OnClickListener {
            if (movementMethods.isSpanClick) return@OnClickListener
            rl_group.performClick()
        })
        tv_user_name.text = content.userName
        rl_group.setOnClickListener { v ->
            if (onCommentItemClickListener != null) onCommentItemClickListener?.onItemClick(
                v,
                content,
                position
            )
        }
        ll_like.setOnClickListener { v ->
            if (onCommentItemClickListener != null) onCommentItemClickListener?.onLikeClick(
                v,
                content,
                position
            )
        }
    }

    private fun makeMoreView(isMore: Boolean): View {
        val view = inflate(context, R.layout.item_comment_new_more, null)
        val ll_group = view.findViewById<LinearLayout>(R.id.ll_group)
        if (isMore) {
            ll_group.setOnClickListener { v ->
                if (onCommentItemClickListener != null) onCommentItemClickListener!!.onMoreClick(
                    v,
                    position
                )
            }
        }
        view.findViewById<View>(R.id.iv_more).visibility = if (isMore) VISIBLE else GONE
        val tvMore = view.findViewById<TextView>(R.id.tv_more)
        tvMore.text = if (isMore) "展开更多回复" else "没有更多回复了"
        return view
    }

    private fun generateMarginLayoutParams(index: Int): LayoutParams {
        if (mLayoutParams == null) {
            mLayoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        if (mCommentBeans != null && index > 0) {
            mLayoutParams!!.topMargin = (mCommentVerticalSpace * 1.2f).toInt()
        }
        return mLayoutParams!!
    }

    override fun onChildViewAdded(parent: View, child: View) {}
    override fun onChildViewRemoved(parent: View, child: View) {
        COMMENT_TEXT_POOL!!.put(child)
    }

    fun makeReplyCommentSpan(
        atSomeone: String?,
        id: String?,
        commentContent: String?
    ): SpannableString {
        val richText = String.format("回复 %s : %s", atSomeone, commentContent)
        val builder = SpannableString(richText)
        if (!TextUtils.isEmpty(atSomeone)) {
            val childStart = 2
            val childEnd = childStart + atSomeone!!.length + 1
            builder.setSpan(object : TextClickSpans() {
                override fun onClick(widget: View) {
                    Toast.makeText(
                        App.instance.applicationContext,
                        "$atSomeone id: $id",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }, childStart, childEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return builder
    }

    interface CommentItemClickListener {
        fun onMoreClick(layout: View?, position: Int)
        fun onItemClick(view: View?, bean: SecondLevelBean?, position: Int)
        fun onLikeClick(layout: View?, bean: SecondLevelBean?, position: Int)
    }
}
