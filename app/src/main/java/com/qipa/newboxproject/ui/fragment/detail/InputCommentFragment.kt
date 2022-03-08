package com.qipa.newboxproject.ui.fragment.detail

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.dialog.InputTextMsgDialog
import com.qipa.newboxproject.data.model.bean.SecondLevelBean
import com.qipa.newboxproject.databinding.FragmentInputCommentBinding
import com.qipa.newboxproject.ui.adapter.CommentAllShowCommentAdapter
import com.qipa.newboxproject.ui.adapter.CommentShowPicAdapter
import com.qipa.newboxproject.viewmodel.state.InputCommentModel
import kotlinx.android.synthetic.main.fragment_input_comment.*
import kotlinx.android.synthetic.main.include_back.*
import java.lang.Exception

class InputCommentFragment : BaseFragment<InputCommentModel,FragmentInputCommentBinding> () , CommentAllShowCommentAdapter.onItemClickLikesAndComment{
    private val commentShowPicAdapter : CommentShowPicAdapter by lazy { CommentShowPicAdapter(mActivity,mDataPicUrl,recycler_view_pics) }
    private val commentAllShowCommentAdapter : CommentAllShowCommentAdapter by lazy { CommentAllShowCommentAdapter(mDataSecondBean) }
    private var mDataPicUrl : MutableList<String> = arrayListOf()
    private var mDataSecondBean : MutableList<SecondLevelBean> = arrayListOf()
    private var inputTextMsgDialog : InputTextMsgDialog? = null
    private var offsetY : Int = 0
    override fun layoutId() = R.layout.fragment_input_comment
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        initCommentMsgData()
    }

    fun initCommentMsgData(){
        recycler_view_pics.init(GridLayoutManager(context, 3),commentShowPicAdapter)
        mDataPicUrl.clear()
        for (index in 0..3){
            mDataPicUrl.add("https://static.runoob.com/images/demo/demo2.jpg")
            mDataPicUrl.add("http://mmbiz.qpic.cn/mmbiz/PwIlO51l7wuFyoFwAXfqPNETWCibjNACIt6ydN7vw8LeIwT7IjyG3eeribmK4rhibecvNKiaT2qeJRIWXLuKYPiaqtQ/0")

        }
        commentShowPicAdapter.notifyDataSetChanged()
        recycler_all_comment.init(LinearLayoutManager(mActivity),commentAllShowCommentAdapter)
        for(index in 1..20){
            var secondLevelBean : SecondLevelBean = SecondLevelBean()
            secondLevelBean.isReply = 0
            secondLevelBean.content = "法布施新时代发送到"
            secondLevelBean.likeCount = 30
            mDataSecondBean.add(secondLevelBean)
        }
        commentAllShowCommentAdapter.setonItemClickLikesAndComment(this)
        commentAllShowCommentAdapter.setList(mDataSecondBean)
        commentAllShowCommentAdapter.notifyDataSetChanged()

        inputTextMsgDialog = InputTextMsgDialog(mActivity, R.style.dialog)
    }
    private fun initInputTextMsgDialog(
        view: View?,
        isReply: Boolean,
        headImg: String?,
        position: Int
    ) {
        dismissInputDialog()
        if (view != null) {
            offsetY = view.getTop()
            scrollLocation(offsetY)
        }
        if (inputTextMsgDialog == null) {
            inputTextMsgDialog?.setmOnTextSendListener(object :
                InputTextMsgDialog.OnTextSendListener {
                override fun onTextSend(msg: String?) {
//                    addComment(isReply, headImg, position, msg)
                }

                override fun dismiss() {
                    scrollLocation(-offsetY)
                }
            })
        }
        showInputTextMsgDialog()
    }

    fun dismissInputDialog(){
        inputTextMsgDialog?.dismiss()
    }

    // item滑动到原位
    fun scrollLocation(offsetY: Int) {
        try {
            recycler_all_comment.smoothScrollBy(0, offsetY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun showInputTextMsgDialog() {
        inputTextMsgDialog?.show()
    }

    inner class ProxyClick{

        fun clickeditcomment(){
            initInputTextMsgDialog(null, false, null, -1)

        }

    }

    override fun onClickItemComment() {
        initInputTextMsgDialog(null, false, null, -1)
    }

    override fun OnClickItemLikes() {

    }

    override fun onBackPressed(): Boolean {
        return false
    }


}