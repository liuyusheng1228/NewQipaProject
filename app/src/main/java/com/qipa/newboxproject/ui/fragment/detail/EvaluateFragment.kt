package com.qipa.newboxproject.ui.fragment.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.comment.VerticalCommentLayout
import com.qipa.newboxproject.databinding.FragmentEvaluateBinding
import com.qipa.newboxproject.viewmodel.state.EvaluateModel
import com.qipa.newboxproject.data.model.bean.SecondLevelBean
import kotlinx.android.synthetic.main.update_app_alert.*
import com.qipa.newboxproject.data.model.bean.FirstLevelBean
import com.qipa.newboxproject.ui.adapter.CommentDialogSingleAdapter
import kotlinx.android.synthetic.main.fragment_evaluate.*


class EvaluateFragment : BaseFragment<EvaluateModel,FragmentEvaluateBinding>() ,
    VerticalCommentLayout.CommentItemClickListener{
    override fun layoutId() = R.layout.fragment_evaluate
    private var data: ArrayList<FirstLevelBean> = arrayListOf()
    private val commentDialogSingleAdapter: CommentDialogSingleAdapter by lazy { CommentDialogSingleAdapter(this) }
    companion object {
        fun newInstance( isNew: Boolean): EvaluateFragment {
            val args = Bundle()
            args.putBoolean("isNew", isNew)
            val fragment = EvaluateFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewModle = mViewModel
        dialog_bottomsheet_rv_lists.init(LinearLayoutManager(mActivity),commentDialogSingleAdapter).let {
            it.setHasFixedSize(true)
        }
        initDatas()
        commentDialogSingleAdapter.setList(data)
        commentDialogSingleAdapter.notifyDataSetChanged()
    }

    //初始化数据 在项目中是从服务器获取数据
      fun initDatas() {
        for (i in 0..9) {
            val firstLevelBean = FirstLevelBean()
            firstLevelBean.content =
                "第" + (i + 1).toString() + "人评论内容" + if (i % 3 === 0) firstLevelBean.content + (i + 1).toString() + "次" else ""
            firstLevelBean.createTime = System.currentTimeMillis()
            firstLevelBean.headImg =
                "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3370302115,85956606&fm=26&gp=0.jpg"
            firstLevelBean.id = i.toString() + ""
            firstLevelBean.userId = "UserId$i"
            firstLevelBean.isLike =0
            firstLevelBean.position = i
            firstLevelBean.likeCount = i.toLong()
            firstLevelBean.userName = "星梦缘" + (i + 1)
            val beanList: MutableList<SecondLevelBean> = ArrayList()
            for (j in 0..2) {
                val secondLevelBean = SecondLevelBean()
                secondLevelBean.content =
                    "一级第" + (i + 1) + "人 二级第" + (j + 1) + "人评论内容" + if (j % 3 == 0) secondLevelBean.content + (j + 1) + "次" else ""
                secondLevelBean.createTime = System.currentTimeMillis()
                secondLevelBean.headImg =
                    "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1918451189,3095768332&fm=26&gp=0.jpg"
                secondLevelBean.id = j.toString() + ""
                firstLevelBean.userId = "ChildUserId$i"
                secondLevelBean.isLike = 0
                secondLevelBean.likeCount = j.toLong()
                secondLevelBean.userName = "星梦缘" + (i + 1) + "  " + (j + 1)
                secondLevelBean.isReply = (if (j % 5 == 0) 1 else 0)
                secondLevelBean.replyUserName = if (j % 5 == 0) "闭嘴家族$j" else ""
                secondLevelBean.position = i
                secondLevelBean.childPosition = j
                beanList.add(secondLevelBean)
            }
            firstLevelBean.setSecondLevelBeans(beanList)
            data.add(firstLevelBean)
        }
    }

    override fun onMoreClick(layout: View?, position: Int) {
        nav().navigateAction(R.id.action_gameDetailFragment_to_inputCommentFragment)
    }

    override fun onItemClick(view: View?, bean: SecondLevelBean?, position: Int) {
        nav().navigateAction(R.id.action_gameDetailFragment_to_inputCommentFragment)
    }

    override fun onLikeClick(layout: View?, bean: SecondLevelBean?, position: Int) {
        Toast.makeText(mActivity,"点击",Toast.LENGTH_SHORT).show()
        nav().navigateAction(R.id.action_gameDetailFragment_to_inputCommentFragment)
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}