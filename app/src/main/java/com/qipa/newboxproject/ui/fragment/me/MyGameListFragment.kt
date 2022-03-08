package com.qipa.newboxproject.ui.fragment.me

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SizeUtils
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.ext.bindViewPager2
import com.qipa.newboxproject.app.ext.init
import com.qipa.newboxproject.databinding.FragmentMyGameListBinding
import com.qipa.newboxproject.ui.fragment.me.mygame.MyGameShowDetailFragment
import com.qipa.newboxproject.viewmodel.state.MyGameListModel
import kotlinx.android.synthetic.main.fragment_game_detail.*
import kotlinx.android.synthetic.main.include_back.*
import kotlinx.android.synthetic.main.include_viewpager.*
import kotlinx.android.synthetic.main.include_viewpager.viewpager_linear
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class  MyGameListFragment : BaseFragment<MyGameListModel,FragmentMyGameListBinding> (){
    override fun layoutId() = R.layout.fragment_my_game_list
    private val mDataGameListTitle : MutableList<String> = arrayListOf()
    private val mDataGameListFragment : ArrayList<Fragment> = arrayListOf()

    @SuppressLint("ResourceAsColor")
    override fun initView(savedInstanceState: Bundle?) {
        view_pager.init(this,mDataGameListFragment)
//        StatusNavigationUtils.setStatusBarColor(mActivity,R.color.black)
        magic_indicator.bindViewPager2(view_pager,mDataGameListTitle,false)
        toolbar_titletv.text = resources.getString(R.string.my_games)
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        loadData()
        viewpager_linear.setBackgroundColor(resources.getColor(R.color.white))

    }

    fun loadData(){
        mDataGameListTitle.add(resources.getString(R.string.game_playing))
        mDataGameListTitle.add(resources.getString(R.string.concerned))
        mDataGameListTitle.add(resources.getString(R.string.reserved))
        mDataGameListFragment.add(MyGameShowDetailFragment.newInstance(1))
        mDataGameListFragment.add(MyGameShowDetailFragment.newInstance(2))
        mDataGameListFragment.add(MyGameShowDetailFragment.newInstance(3))
        initIndicator()
    }

    fun initIndicator(){
        val commonNavigator = CommonNavigator(mActivity)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return if (mDataGameListTitle == null) 0 else mDataGameListTitle.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView? {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.GRAY
                colorTransitionPagerTitleView.selectedColor = Color.BLACK
                colorTransitionPagerTitleView.width = SizeUtils.dp2px(150f)
                colorTransitionPagerTitleView.setText(mDataGameListTitle.get(index))
                colorTransitionPagerTitleView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        view_pager.setCurrentItem(index)
                    }
                })
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator? {
                val indicator = LinePagerIndicator(context)
                //设置宽度
                indicator.lineWidth = SizeUtils.dp2px(30f).toFloat()
                //设置高度
                indicator.lineHeight = SizeUtils.dp2px(5f).toFloat()
                //设置颜色
                indicator.setColors(resources.getColor(R.color.teal_200))
                //设置圆角
                indicator.roundRadius = 5f
                //设置模式
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        magic_indicator.navigator = commonNavigator
        magic_indicator.navigator.notifyDataSetChanged()
        view_pager.adapter?.notifyDataSetChanged()
        view_pager.offscreenPageLimit = mDataGameListFragment.size
    }

    override fun onPause() {
        super.onPause()
//        StatusNavigationUtils.setNavigationBarTransparent(mActivity)
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}