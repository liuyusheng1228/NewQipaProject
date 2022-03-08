package com.qipa.newboxproject.ui.fragment.home

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.bindViewPager2
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.databinding.FragmentHomeBinding
import com.qipa.newboxproject.viewmodel.state.HomeModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_viewpager.*
import kotlinx.android.synthetic.main.include_viewpager.view.*

class HomeFragment : BaseFragment<HomeModel, FragmentHomeBinding>() {
    override fun layoutId() = R.layout.fragment_home

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewpager2
        view_pager.init(this, fragments)
        //初始化 magic_indicator

        val layoutparams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        layoutparams.gravity = Gravity.CENTER_VERTICAL
        layoutparams.leftMargin = 15
        magic_indicator.setLayoutParams(layoutparams)
        magic_indicator.bindViewPager2(view_pager, mDataList,false)
        //初始化数据
        mDataList.add(resources.getString(R.string.popular_recommendation))
        mDataList.add(resources.getString(R.string.ranking_list))
        fragments.add(HotFragment.newInstance(true))
        fragments.add(RankingListFragment.newInstance( true))
        magic_indicator.navigator.notifyDataSetChanged()
        view_pager.adapter?.notifyDataSetChanged()
        view_pager.offscreenPageLimit = fragments.size

        home_search.setOnClickListener {
            nav().navigateAction(R.id.action_mainfragment_to_searchFragment)
        }
        mDatabind.click = ProClick()
    }

    inner class ProClick{
        fun gotodownView(){
            nav().navigateAction(R.id.action_mainfragment_action_to_downLoadFragment)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}