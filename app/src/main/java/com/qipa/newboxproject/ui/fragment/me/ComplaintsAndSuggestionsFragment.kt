package com.qipa.newboxproject.ui.fragment.me

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.giftedcat.picture.lib.selector.MultiImageSelector
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.interfaces.OnAddPicturesListener
import com.qipa.newboxproject.databinding.FragmentComplaintsAndSuggestionsBinding
import com.qipa.newboxproject.ui.adapter.NineGridAdapter
import com.qipa.newboxproject.ui.adapter.QuesitionListAdapter
import com.qipa.newboxproject.viewmodel.state.ComPlainsAndSuggestionModel
import kotlinx.android.synthetic.main.fragment_complaints_and_suggestions.*
import kotlinx.android.synthetic.main.fragment_complaints_and_suggestions.rv_images
import kotlinx.android.synthetic.main.fragment_user_evaluate.*
import kotlinx.android.synthetic.main.include_back.*
import java.util.ArrayList

class ComplaintsAndSuggestionsFragment : BaseFragment<ComPlainsAndSuggestionModel,FragmentComplaintsAndSuggestionsBinding>() {
    override fun layoutId() = R.layout.fragment_complaints_and_suggestions
    private val quesitionListAdapter : QuesitionListAdapter by lazy { QuesitionListAdapter(mDataListQuesValue) }
    private var mDataListQuesValue : MutableList<String> = arrayListOf()
    val adapter: NineGridAdapter by lazy { NineGridAdapter(mActivity, mSelectList, rv_images)  }
    var mSelectList: MutableList<String>? = arrayListOf()
    private val maxNum: Int = 9
    private val REQUEST_IMAGE = 2

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDataListQuesValue.add("投诉客服服务")
        mDataListQuesValue.add("投诉处理结果")
        mDataListQuesValue.add("游戏建议")
        mDataListQuesValue.add("其它建议")
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = resources.getText(R.string.problem_handling)
        recy_list_question.init(GridLayoutManager(mActivity, 3),quesitionListAdapter)
        quesitionListAdapter.setList(mDataListQuesValue)
        quesitionListAdapter.notifyDataSetChanged()
        quesitionListAdapter.setOnItemClickListener { adapter, view, position ->
            quesitionListAdapter.setSelection(position)
        }
        adapter.setMaxSize(maxNum)
        rv_images.init(GridLayoutManager(mActivity, 3),adapter)

        adapter.setOnAddPicturesListener(object : OnAddPicturesListener {
            override fun onAdd() {
                pickImage()
            }

        })


    }

    /**
     * 选择需添加的图片
     */
    private fun pickImage() {
        val selector = MultiImageSelector.create(context)
        selector.showCamera(true)
        selector.count(maxNum)
        selector.multi()
        selector.origin(mSelectList)
        selector.start(this, REQUEST_IMAGE)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val select: ArrayList<String>? =
                    data?.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT)
                mSelectList?.clear()
                if (select != null) {
                    mSelectList?.addAll(select)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}