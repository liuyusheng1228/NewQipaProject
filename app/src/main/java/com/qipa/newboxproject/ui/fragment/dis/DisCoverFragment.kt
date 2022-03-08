package com.qipa.newboxproject.ui.fragment.dis

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kingja.loadsir.core.LoadService
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.*
import com.qipa.newboxproject.databinding.FragmentDiscoverBinding
import com.qipa.newboxproject.viewmodel.request.RequestDisCoverViewModel
import com.qipa.newboxproject.viewmodel.state.DisCoverModel
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_viewpager.*

class DisCoverFragment : BaseFragment<DisCoverModel, FragmentDiscoverBinding>() {
    private val requestDisCoverViewModel: RequestDisCoverViewModel by viewModels()
    private var fragments : ArrayList<Fragment> = arrayListOf()
    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    override fun layoutId() =  R.layout.fragment_discover

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel

        mDatabind.click = ProxyClick()
        view_pager.init(this,fragments)

        magic_indicator.bindViewPager3(view_pager, mDataList,false)

        mDataList.add("分类")
        mDataList.add("开服")
        mDataList.add("开测")

        fragments.add(ConcenTrationFragment.newInstance(true))
        fragments.add(OpenServiceFragment.newInstance(true))
        fragments.add(OpenTestFragment.newInstance(true))
        magic_indicator.navigator.notifyDataSetChanged()
        view_pager.adapter?.notifyDataSetChanged()
        view_pager.offscreenPageLimit = fragments.size

    }
    inner class ProxyClick {

    }

    override fun onBackPressed(): Boolean {
        return false
    }
}