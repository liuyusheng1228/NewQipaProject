package com.qipa.newboxproject.ui.fragment.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ktx.immersionBar
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.*
import com.qipa.newboxproject.databinding.FragmentHomeBinding
import com.qipa.newboxproject.viewmodel.state.HomeModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_viewpager.*
import kotlinx.android.synthetic.main.include_viewpager.view.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator




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
        magic_indicator.bindViewPager21("#FFFFFFFF",view_pager, mDataList,false)
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

        LiveDataBus.instance.observe(viewLifecycleOwner,EventObserver{
            if (it.event == FlashEvent.EVENT_TEST) {
                if (it.data == 0){
//                    immersionBar {
//                        fullScreen(true)
//                        statusBarColor(R.color.transparent)
//                        navigationBarColor(R.color.transparent)
////                        fitsSystemWindows(true)
//                    }
                    mian_lin.setBackgroundColor(resources.getColor(R.color.tranaction))
                    viewpager_linear.setBackgroundColor(resources.getColor(R.color.tranaction))

                }else if(it.data == 1){
                    start_bar_view.setBackgroundColor(resources.getColor(R.color.tranaction))
                    immersionBar {
                        fullScreen(true)
                        statusBarColor(R.color.transparent)
                        navigationBarColor(R.color.transparent)
//                        fitsSystemWindows(true)
                        flymeOSStatusBarFontColor(R.color.white)
                    }
                    magic_indicator.bindViewPager21("#FFFFFFFF",view_pager, mDataList,false)
                    mian_lin.setBackgroundColor(resources.getColor(R.color.tranaction))
                    viewpager_linear.setBackgroundColor(resources.getColor(R.color.tranaction))

                }else if(it.data == 2){
                    mian_lin.setBackgroundColor(resources.getColor(R.color.white))
                    viewpager_linear.setBackgroundColor(resources.getColor(R.color.white))

                    immersionBar {
                        start_bar_view.setBackgroundColor(resources.getColor(R.color.white))
                        fullScreen(true)
                        statusBarColor(R.color.white)
                        navigationBarColor(R.color.white)
                        flymeOSStatusBarFontColor(R.color.black)
                    }
                    magic_indicator.bindViewPager21("#FF000000",view_pager, mDataList,false)
                }
                // do sth.
            }
        })
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