package com.qipa.newboxproject.ui.fragment.me.mygame

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.app.weight.recyclerview.SpaceItemDecoration
import com.qipa.newboxproject.data.model.bean.AriticleResponse
import com.qipa.newboxproject.databinding.FragmentMyGameShowDetailBinding
import com.qipa.newboxproject.ui.adapter.MyGameShowDetailAdapter
import com.qipa.newboxproject.viewmodel.state.MyGameShowDetailModel
import kotlinx.android.synthetic.main.include_recyclerview.*

class MyGameShowDetailFragment : BaseFragment<MyGameShowDetailModel,FragmentMyGameShowDetailBinding>() {
    private val myGameShowDetailAdapter : MyGameShowDetailAdapter by lazy { MyGameShowDetailAdapter(arrayListOf()) }
    private val mDataGameList : MutableList<AriticleResponse> = arrayListOf()

    override fun layoutId() = R.layout.fragment_my_game_show_detail
    //改项目对应的id
    private var cid = 0
    companion object {
        fun newInstance( cid: Int): MyGameShowDetailFragment {
            val args = Bundle()
            args.putInt("cid", cid)
            val fragment = MyGameShowDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            cid = it.getInt("cid")
        }
        recyclerView.init(LinearLayoutManager(mActivity),myGameShowDetailAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f), false))
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}