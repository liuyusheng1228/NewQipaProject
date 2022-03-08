package com.qipa.newboxproject.ui.fragment.detail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.interfaces.OnAddPicturesListener
import com.qipa.newboxproject.app.weight.RatingBar
import com.qipa.newboxproject.databinding.FragmentUserEvaluateBinding
import com.qipa.newboxproject.viewmodel.state.UserEvaluateModel
import kotlinx.android.synthetic.main.fragment_user_evaluate.*
import com.qipa.newboxproject.ui.adapter.NineGridAdapter
import com.giftedcat.picture.lib.selector.MultiImageSelector
import com.qipa.newboxproject.app.ext.init
import java.util.ArrayList
import androidx.recyclerview.widget.GridLayoutManager
import com.qipa.jetpackmvvm.ext.nav
import kotlinx.android.synthetic.main.include_back.*


class UserEvaluateFragment : BaseFragment<UserEvaluateModel,FragmentUserEvaluateBinding>() {
    val adapter: NineGridAdapter by lazy {NineGridAdapter(mActivity, mSelectList, rv_images)  }
    private val maxNum: Int = 9
    private val REQUEST_IMAGE = 2
    var mSelectList: MutableList<String>? = arrayListOf()
    override fun layoutId() = R.layout.fragment_user_evaluate
    companion object {
        fun newInstance( isNew: Boolean): UserEvaluateFragment {
            val args = Bundle()
            args.putBoolean("isNew", isNew)
            val fragment = UserEvaluateFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewModle = mViewModel
        toolbar_titletv.text = "游戏名称"
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_user_txt.text = "发表"
        toolbar_user_txt.setTextColor(resources.getColor(R.color.white))
        toolbar_user_txt.setBackgroundResource(R.drawable.bg_round_theme_bg)
        adapter.setMaxSize(maxNum)
        rv_images.init(GridLayoutManager(mActivity, 3),adapter)
        ratingBar.setOnStarChangeListener(object : RatingBar.OnStarChangeListener{
            override fun OnStarChanged(selectedNumber: Float, position: Int) {
            }

        })
        adapter.setOnAddPicturesListener(object : OnAddPicturesListener{
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
            if (resultCode == RESULT_OK) {
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